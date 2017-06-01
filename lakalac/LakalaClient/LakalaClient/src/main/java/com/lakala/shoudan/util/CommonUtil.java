package com.lakala.shoudan.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.MerchantStatus;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.myaccount.AccountInfoActivity;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.dialog.BaseDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by HUASHO on 2015/1/27.
 */
public class CommonUtil {
    public static void loanMoneyTextFocus(EditText editText) {
        String result = editText.getText().toString().trim();
        if (TextUtils.isEmpty(result)) {
            return;
        }
        int indexOfDot = result.indexOf(".");
        if (indexOfDot > 0) {
            if (result.length() - indexOfDot - 1 == 1) {
                //小数点后只有1位
                String lastEnd = result.substring(indexOfDot + 1, indexOfDot + 2);
                if ("0".equals(lastEnd)) {//只有一位且为0，则去掉小数点后的值
                    result = result.substring(0, indexOfDot);
                }
            } else if (result.length() - indexOfDot - 1 == 2) {
                String lastEnd = result.substring(indexOfDot + 1, indexOfDot + 3);
                if ("00".equals(lastEnd)) {//小数后有两位且为0，则去掉小数点后的值
                    result = result.substring(0, indexOfDot);
                }
            } else if (result.length() - indexOfDot - 1 == 0) {
                result = result.substring(0, indexOfDot);
            }
            editText.setText(result);
        }
    }

    /**
     * 隐藏键盘 点击键盘右下角按钮时隐藏键盘
     */
    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) ApplicationEx
                .getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示键盘 点击键盘右下角按钮时显示键盘
     */
    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) ApplicationEx.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 显示不能做交易和查询的对话框
     */
    public static boolean isMerchantValid(FragmentActivity context) {
        boolean isValid = true;
        MerchantInfo merchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();
        if (merchantInfo.getMerchantStatus() == MerchantStatus.FROZEN) {
            String msg = context.getString(R.string.frozen_account);
            DialogCreator.createOneConfirmButtonDialog(context, "确定", msg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
            isValid = false;
        }
        if (merchantInfo.getMerchantStatus() == MerchantStatus.NONE) {
            DialogCreator.showMerchantNotOpenDialog(context);
            isValid = false;
        }
        return isValid;
    }
    public static boolean isMerchantValid1(FragmentActivity context) {
        boolean isValid = true;
        MerchantInfo merchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();
        if (merchantInfo.getMerchantStatus() == MerchantStatus.FROZEN) {
            isValid = false;
        }else if (merchantInfo.getMerchantStatus() == MerchantStatus.PROCESSING) {
            isValid = false;
        }else if (merchantInfo.getMerchantStatus() == MerchantStatus.NONE) {
            isValid = false;
        } else if (merchantInfo.getMerchantStatus() == MerchantStatus.FAILURE) {
            isValid = false;
        }
        return isValid;
    }

    public static boolean isMerchantCompleted(final FragmentActivity context) {
        boolean isCompleted = isMerchantValid(context);
        MerchantInfo merchantInfoData = ApplicationEx.getInstance().getUser().getMerchantInfo();
        MerchantStatus merchantStatus = merchantInfoData.getMerchantStatus();
        if (merchantStatus == MerchantStatus.FAILURE) {
            isCompleted = false;
            DialogCreator.createFullContentDialog(
                    context, "取消", "确定", "您的商户资料审核未通过,快点击\"确定\"重新提交吧!",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            queryUsrInfo(context);
//                            Intent intent = new Intent(context, UserInforActivity.class);
////                            Intent intent = new Intent(context, UpdateMerchantInfoActivity.class);
////                            intent.putExtra(Constants.IntentKey.MODIFY_ACCOUNTINFO, true);
//                            context.startActivity(intent);
                        }
                    }).show();
        } else if (merchantStatus == MerchantStatus.PROCESSING) {
            isCompleted = false;
            DialogCreator.createConfirmDialog(
                    context, "确定", "商户资料正在审核中，请耐心等待").show();
        } else if (merchantStatus == MerchantStatus.COMPLETED) {
            isCompleted = true;
        }
        return isCompleted;
    }

    /**
     * 显示不能做交易和查询的对话框
     */
    public static boolean isMerchantValid2(FragmentActivity context) {
        boolean isValid = true;
        MerchantInfo merchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();
        if (merchantInfo.getMerchantStatus() == MerchantStatus.FROZEN) {
            String msg = context.getString(R.string.frozen_account);
            DialogCreator.createOneConfirmButtonDialog(context, "确定", msg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
            isValid = false;
        }
        if (merchantInfo.getMerchantStatus() == MerchantStatus.NONE) {
            DialogCreator.showMerchantNotOpenDialog2(context);
            isValid = false;
        }
        return isValid;
    }

    public static boolean isMerchantCompleted2(final FragmentActivity context) {
        boolean isCompleted = isMerchantValid2(context);
        MerchantInfo merchantInfoData = ApplicationEx.getInstance().getUser().getMerchantInfo();
        MerchantStatus merchantStatus = merchantInfoData.getMerchantStatus();
        if (merchantStatus == MerchantStatus.FAILURE) {
            isCompleted = false;
            DialogCreator.createFullContentDialog(
                    context, "取消", "确定", "您的商户资料审核未通过,快点击\"确定\"重新提交吧!",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            queryUsrInfo(context);
//                            Intent intent = new Intent(context, UserInforActivity.class);
////                            Intent intent = new Intent(context, UpdateMerchantInfoActivity.class);
////                            intent.putExtra(Constants.IntentKey.MODIFY_ACCOUNTINFO, true);
//                            context.startActivity(intent);
                        }
                    }).show();
        } else if (merchantStatus == MerchantStatus.PROCESSING) {
            isCompleted = false;
            DialogCreator.createConfirmDialog(
                    context, "确定", "商户资料正在审核中，请耐心等待").show();
        } else if (merchantStatus == MerchantStatus.COMPLETED) {
            isCompleted = true;
        }
        return isCompleted;
    }

    private static void queryUsrInfo(final FragmentActivity context) {
        ServiceResultCallback serviceResultCallback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                JSONObject jsonObject = null;
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        String firstUpgradeStatus = jsonObject.optString("firstUpgradeStatus");
                        String secondUpgradeStatus = jsonObject.optString("secondUpgradeStatus");
                        String openBankBranchName = jsonObject.optString("openBankBranchName");
                        String busLicenceCode = jsonObject.optString("busLicenceCode");
                        Intent intent = new Intent(context, AccountInfoActivity.class);
                        intent.putExtra("firstUpgradeStatus", firstUpgradeStatus);
                        intent.putExtra("secondUpgradeStatus", secondUpgradeStatus);
                        intent.putExtra("openBankBranchName", openBankBranchName);
                        intent.putExtra("busLicenceCode", busLicenceCode);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context, context.getString(R.string.socket_fail));
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.UPDATE_STATUS);
        businessRequest.setAutoShowProgress(true);
        businessRequest.setResponseHandler(serviceResultCallback);
        businessRequest.execute();
        ShoudanStatisticManager.getInstance()
                .onEvent(ShoudanStatisticManager.Merchant_Info, context);
    }

    public static boolean isPwdComplicatedEnough(FragmentActivity context, String pwd) {

        if (Util.checkPWLevel(pwd).equals("BAD")) {
            BaseDialog dialog = DialogCreator.createConfirmDialog(context, "确定", "登录密码过于简单，至少包含英文字母、数字和符号中的两种。");
            dialog.show();
            return false;
        }
        return true;

    }

    /**
     * 初始化圆孔纸背景
     */
    public static void initBackground(Activity context) {
        View contentView = context.findViewById(R.id.context);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) contentView.getLayoutParams();
        LinearLayout imageView = (LinearLayout) context.findViewById(R.id.bg_left);
        LinearLayout imageView2 = (LinearLayout) context.findViewById(R.id.bg_right);
        params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, contentView.getHeight());
        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, contentView.getHeight());
        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        imageView.setLayoutParams(params1);
        imageView2.setLayoutParams(params2);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), (R.drawable.lakala_bg_qgd));//修改圆孔

        BitmapDrawable drawable = new BitmapDrawable(bitmap);
        drawable.setTileModeY(Shader.TileMode.REPEAT);
        imageView.getLayoutParams().width = drawable.getBitmap().getWidth();
        imageView.setBackgroundDrawable(drawable);

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lakala_bg_qgd);//修改圆孔

        drawable = new BitmapDrawable(bitmap);
        drawable.setTileModeY(Shader.TileMode.REPEAT);
        drawable.setGravity(Gravity.RIGHT);
        imageView2.setBackgroundDrawable(drawable);

        imageView2.getLayoutParams().width = drawable.getBitmap().getWidth();
    }

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            if (sentBitmap == null) {
                return null;
            }
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    public static Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static Bitmap takeScreenshot(View v1) {
        Bitmap retBitmap = null;
        try {
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int quality = 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 30;
            retBitmap = BitmapFactory.decodeStream(is, null, opts);
            is.close();
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
        return retBitmap;
    }
}
