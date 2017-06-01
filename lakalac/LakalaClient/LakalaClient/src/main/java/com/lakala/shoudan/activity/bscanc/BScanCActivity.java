package com.lakala.shoudan.activity.bscanc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.encryption.Base64;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.ScancodeAccess;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ScanCodeCollectionEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.barcodecollection.apply.BarcodeInstructionActivity;
import com.lakala.shoudan.util.UIUtils;
import com.lakala.ui.component.NavigationBar;
import com.sensetime.sample.core.LivenessActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by huwei on 16/7/11.
 */
public class BScanCActivity extends AppBaseActivity {
    @Bind(R.id.iv_barcode)
    ImageView iv_barcode;
    @Bind(R.id.iv_barcode_center)
    ImageView iv_barcode_center;
    @Bind(R.id.tv_savebarcode)
    TextView tv_savebarcode;
    @Bind(R.id.tv_help)
    TextView tv_help;
    @Bind(R.id.merchant_shopname)
    TextView tv_merchant_shopname;
    @Bind(R.id.ll_storeCode)
    LinearLayout ll_storeCode;
    @Bind(R.id.iv_nobarcode)
    ImageView iv_nobarcode;
    @Bind(R.id.tv_barcode_status)
    TextView tv_barcode_status;
    @Bind(R.id.rl_code_active_success)
    RelativeLayout rl_code_active_success;
    @Bind(R.id.rl_code_active_fail)
    RelativeLayout rl_code_active_fail;
    @Bind(R.id.rl_barcode_merchant)
    RelativeLayout rl_barcode_merchant;
    @Bind(R.id.rl_pay)
    RelativeLayout rl_pay;

    private ImageView pay_weixin;
    private TextView tv_weixin;
    private Bitmap type_bitmap = null;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    File file = (File) msg.obj;
                    Uri uri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(uri);
                    sendBroadcast(intent);
                    hideProgressDialog();
                    ToastUtil.toast(BScanCActivity.this, getString(R.string.savepic_success));
                    break;
                case 0:
                    hideProgressDialog();
                    ToastUtil.toast(BScanCActivity.this, getString(R.string.savepic_fail));
                    break;
                default:
                    hideProgressDialog();
                    ToastUtil.toast(BScanCActivity.this, getString(R.string.savepic_fail));
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bscanc);
        ButterKnife.bind(this);
        initUI();
    }

    public static void start(Context context, String status) {
        Intent intent = new Intent(context, BScanCActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BSCANC_STATUS, status);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void start(Context context, String status, String merCode, Bitmap bitmap) {
        Intent intent = new Intent(context, BScanCActivity.class);
//        intent.putExtra(Constants.BSCANC_STATUS, status);
//        intent.putExtra(Constants.BARCODE_JSON, merCode);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BSCANC_STATUS, status);
        bundle.putString(Constants.BARCODE_JSON, merCode);
//        ByteArrayOutputStream stream=new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
//        byte[] bytes=stream.toByteArray();
//        bundle.putByteArray("bitmap",bytes);
//        intent.putExtra("bitmap",bytes);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    @Override
    protected void initUI() {
        super.initUI();
        pay_weixin = (ImageView) findViewById(R.id.pay_weixin);
        tv_weixin = (TextView) findViewById(R.id.tv_weixin);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) pay_weixin.getLayoutParams();
        layoutParams.height = ((UIUtils.getScreenWidth(this) - UIUtils.dip2px(30)) * 478 / 1958);
        pay_weixin.setLayoutParams(layoutParams);
        initListener();
        initBar();
        initData();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        pay_weixin.setLayoutParams(new RelativeLayout.LayoutParams(
//                pay_weixin.getWidth(),
//                pay_weixin.getWidth()*478/1958));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        context.hideProgressDialog();
    }

    private void initListener() {
        tv_help.setOnClickListener(this);
        tv_barcode_status.setOnClickListener(this);
        tv_savebarcode.setOnClickListener(this);
        iv_barcode.setOnClickListener(this);
        iv_nobarcode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_help:
                ProtocalActivity.open(context, ProtocalType.ERWEIMA_RULES);
                ShoudanStatisticManager.getInstance()
                        .onEvent(getEvent(true), context);
                break;
            case R.id.tv_savebarcode:
                showProgressWithNoMsg();
                ShoudanStatisticManager.getInstance().onEvent(getEvent(false), this);
                saveImageToGallery(BScanCActivity.this, getViewBitmap(ll_storeCode));
                break;
            default:
                break;
        }
    }

    /**
     * 埋点统计
     *
     * @return
     */
    private String getEvent(boolean isHelp) {
        boolean isHomepage = ScanCodeCollectionEnum.ScanCodeCollection.isHomePage();
        if (isHelp) {
            if (isHomepage)
                return ShoudanStatisticManager.Scan_Code_Collection_Help;
            return ShoudanStatisticManager.Scan_Code_Collection_Help_Public;
        } else {
            if (isHomepage)
                return ShoudanStatisticManager.Scan_Code_Collection_SaveBarCode;
            return ShoudanStatisticManager.Scan_Code_Collection_SaveBarCode_Public;
        }
    }

    private Bitmap getViewBitmap(View v) {
        iv_barcode_center.setVisibility(View.VISIBLE);
        iv_barcode.setVisibility(View.GONE);
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        tv_savebarcode.setVisibility(View.GONE);
        tv_help.setVisibility(View.GONE);
        v.setDrawingCacheBackgroundColor(getResources().getColor(R.color.white));
//        if (color != 0) {
//            v.destroyDrawingCache();
//        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            LogUtil.e("Folder", "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        tv_help.setVisibility(View.VISIBLE);
        tv_savebarcode.setVisibility(View.VISIBLE);
        iv_barcode_center.setVisibility(View.GONE);
        iv_barcode.setVisibility(View.VISIBLE);
//        bitmap = zoomBitmap(bitmap);
        return bitmap;
    }

    public void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "lklcm");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "barcode" + ".jpg";
        android.os.Message message = new android.os.Message();
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
            message.what = 1;
            message.obj = file;
            showProgressDialog("正在保存");
            handler.sendMessageDelayed(message, 800);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            message.obj = 0;
            showProgressDialog("正在保存");
            handler.sendMessageDelayed(message, 800);
        }
        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
    }


    /**
     * 图片缩放至固定大小
     */
//    private Bitmap zoomBitmap(Bitmap bmp) {
//        int width = bmp.getWidth();
//        int height = bmp.getHeight();
//        int newWidth = (int) ((UIUtils.getScreenWidth(this)) * 0.8);
//        int newHeight = (int) (height * 0.8);
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;
//        Matrix matrix = new Matrix();
//        matrix.postScale(scaleWidth, scaleHeight);
//        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0,
//                width, height, matrix, true);
//        return resizedBitmap;
//    }


    private String merCode;
    private String status;

    private void initData() {
        MerchantInfo info = ApplicationEx.getInstance().getUser().getMerchantInfo();
        if (!TextUtils.isEmpty(info.getBusinessName()))
            tv_merchant_shopname.setText(info.getBusinessName());
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
//            status = getIntent().getStringExtra(Constants.BSCANC_STATUS);
//            merCode = getIntent().getStringExtra(Constants.BARCODE_JSON);
            status = bundle.getString(Constants.BSCANC_STATUS);
            merCode = bundle.getString(Constants.BARCODE_JSON);
//            byte[] bytes=bundle.getByteArray("bitmap");
            type_bitmap = ApplicationEx.getInstance().getBitmap();
        }
        handleAccessState();
        if (type_bitmap == null) {
            tv_weixin.setVisibility(View.VISIBLE);
            pay_weixin.setVisibility(View.GONE);
        } else {
            tv_weixin.setVisibility(View.GONE);
            pay_weixin.setImageBitmap(type_bitmap);
        }
    }

    /**
     * 处理二维码
     */
    private Bitmap bitmap;

    private void handleQrCode() {
        setVisibilityWithStatus(true);
        if (!TextUtils.isEmpty(merCode)) {
            bitmap = decodeStringToBitmap(merCode);
            if (bitmap != null) {
                iv_barcode.setImageBitmap(bitmap);
                iv_barcode_center.setImageBitmap(bitmap);
            }
        }
    }


    private void handleAccessState() {
        if (TextUtils.isEmpty(status)) {
            return;
        }
        ScancodeAccess scancodeAccess = ScancodeAccess.valueOf(status);
        switch (scancodeAccess) {
            case NONE:
            case SUPPORT:
                hideProgressDialog();
                handleNonActive();
                break;
            case UNKNOWN:
            case NOTSUPPORT:
            case FAILURE:
            case CLOSED:
                hideProgressDialog();
                handleFailure();
                break;
            case PROCESSING:
                hideProgressDialog();
                handleProcessing();
                break;
            case COMPLETED:
                hideProgressDialog();
                handleQrCode();
                break;
        }
    }

//    private void handleClosed() {
//        setVisibilityWithStatus(false);
//        tv_barcode_status.setText("您暂时不能使用扫码收款业务，请稍后再试");
//    }

    /**
     * 业务开通申请失败
     */

    public static final String phoneNumber = "";

    private void handleFailure() {
        setVisibilityWithStatus(false);
//        rl_barcode_merchant.setClickable(true);
//        tv_barcode_status.setClickable(true);
//        iv_nobarcode.setClickable(true);
//        tv_merchant_shopname.setClickable(true);
//        rl_barcode_merchant.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent();
//                i.setAction(Intent.ACTION_DIAL);
//                i.setData(Uri.parse("tel" + phoneNumber));
//                if (i.resolveActivity(getPackageManager()) != null) {
//                    startActivity(i);
//                }
//            }
//        });
        tv_barcode_status.setText(R.string.barCodeCommitFailed);
    }

    private void handleProcessing() {
        setVisibilityWithStatus(false);
        tv_barcode_status.setText(R.string.barCodeApplicationCommit);
    }

    /**
     * 处理失败状态
     */
    private void handleNonActive() {
        setVisibilityWithStatus(false);
        tv_barcode_status.setText(R.string.barCodeInactive);
        rl_barcode_merchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startApply();
            }
        });
        tv_barcode_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startApply();
            }
        });
        iv_nobarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startApply();
            }
        });
    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap decodeStringToBitmap(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 进入协议页面,C扫B
     */
    private void startApply() {

        Intent intent = new Intent(this, BarcodeInstructionActivity.class);
        intent.putExtra(Constants.BARCODE_STATUS, true);
        startActivity(intent);
        PublicToEvent.MerchantlEvent(ShoudanStatisticManager.Merchant_smType_Open, context);
    }

    private void setVisibilityWithStatus(boolean complete) {
        if (complete) {
            rl_code_active_fail.setVisibility(View.GONE);
            rl_code_active_success.setVisibility(View.VISIBLE);
            rl_pay.setVisibility(View.VISIBLE);

        } else {
            rl_code_active_fail.setVisibility(View.VISIBLE);
            iv_nobarcode.setClickable(TextUtils.equals(status, "NONE") ? true : false);
            tv_barcode_status.setClickable(TextUtils.equals(status, "NONE") ? true : false);
            rl_barcode_merchant.setClickable(TextUtils.equals(status, "NONE") ? true : false);
            tv_merchant_shopname.setClickable(TextUtils.equals(status, "NONE") ? true : false);
            rl_code_active_success.setVisibility(View.GONE);
            rl_pay.setVisibility(View.GONE);
        }

    }

    private void initBar() {
        navigationBar.setTitle("扫码收款");
        navigationBar.setActionBtnText("");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem) {
                    case action:
                        //扫码收款
//                        if (CommonUtil.isMerchantCompleted(context)) {
////                            BarcodeAccessManager barcodeManager = new BarcodeAccessManager((AppBaseActivity) context);
////                            barcodeManager.setStatistic(ShoudanStatisticManager.Scan_Code_Collection_Homepage);
////                            barcodeManager.check(true);
////                            ScanCodeCollectionEnum.ScanCodeCollection.setData(null, false);
////                            drawerFragment.setUpdateOnResume(true);
//                            ErweimaCollectionEnum.ERWEIMA_COLLECTION.setData(null, false);
//                            BarcodeAccessManager barcodeAccessManager = new BarcodeAccessManager((AppBaseActivity) context);
//                            barcodeAccessManager.setStatistic(ShoudanStatisticManager.ErWeiMa_Collection_SecondPage);
//                            barcodeAccessManager.check(true);
//                        }
                        break;
                    case back:
                        onBackPressed();
                        break;
                }
            }
        });
    }
}
