package com.lakala.platform.cordovaplugin;

import android.app.DatePickerDialog;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.DatePicker;

import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaPlugin;
import com.lakala.library.util.DateUtil;
import com.lakala.library.util.ImageUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.common.PackageFileManager;
import com.lakala.platform.swiper.mts.DialogController;
import com.lakala.platform.swiper.mts.payment.PaymentTypeSelectListener;
import com.lakala.platform.swiper.mts.payment.PaymentTypeSelector;
import com.lakala.platform.swiper.mts.payment.QuickCard;
import com.lakala.platform.swiper.mts.paypwd.PayPwdProcess;
import com.lakala.ui.common.CommmonSelectData;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * 对话框插件类
 *
 */
public class DialogPlugin extends CordovaPlugin {

    private static final String ALERT               = "alert";
    private static final String CONFIRM             = "confirm";
    private static final String SHOW_PROGRESS       = "showProgress";
    private static final String HIDE_PROGRESS       = "hideProgress";
    private static final String TOAST               = "toast";
    private static final String COMMON_INPUT        = "commonInput";
    private static final String IMAGE_INPUT         = "imageInput";
    private static final String SHOW_SELECT         = "showSelect";
    private static final String PAY_PASSWORD        = "payPassword";
    private static final String SELECT_PAYMENT      = "selectPayment";
    private static final String CUSTOM_IMG_DIALOG   = "customImgDialog";
    private static final String WEB_DIALOG          = "webDialog";
    private static final String DATE_DIALOG         = "dateDialog";
    //从手机系统中选取一张图片
    private static final String GET_PICTURE         = "getPicture";

    private final int DELAY_TIME    = 500;
    private boolean isDialogShowing = false;

    private FragmentManager fragmentManager;
    private FragmentActivity context;
    private ProgressDialog progressDialog;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        context         = ((FragmentActivity) this.cordova.getActivity());
        fragmentManager = context.getSupportFragmentManager();

        if (action.equals(ALERT)) {
            if (!isDialogShowing) {
                isDialogShowing = true;
                return showAlert(args, callbackContext);
            }
        }

        if (action.equals(CONFIRM)) {
            if (!isDialogShowing) {
                isDialogShowing = true;
                return showConfirm(args, callbackContext);
            }
        }

        if (action.equals(SHOW_PROGRESS)) {
            return showProgress(args);
        }

        if (action.equals(HIDE_PROGRESS)) {
            return hideProgress(callbackContext);
        }

        if (action.equals(TOAST)) {
            return showToast(args);
        }
        if (action.equals(COMMON_INPUT)) {
            if (!isDialogShowing) {
                isDialogShowing = true;
                return commonInput(args, callbackContext);
            }
        }
        if (action.equals(IMAGE_INPUT)) {
            if (!isDialogShowing) {
                isDialogShowing = true;
                return imageInput(args, callbackContext);
            }
        }
        if (action.equals(SHOW_SELECT)) {
            if (!isDialogShowing) {
                isDialogShowing = true;
                return showSelect(args, callbackContext);
            }
        }
        if (action.equals(PAY_PASSWORD)) {
            if (!isDialogShowing) {
                isDialogShowing = true;
                return payPassword(args, callbackContext);
            }
        }
        if (action.equals(SELECT_PAYMENT)) {
            if (!isDialogShowing) {
                isDialogShowing = true;
                return showPaymentSelectListDialog(args, callbackContext);
            }
        }
        if (action.equals(CUSTOM_IMG_DIALOG)) {
            if (!isDialogShowing) {
                isDialogShowing = true;
                return showCustomImgDialog(args, callbackContext);
            }
        }
        if (action.equals(WEB_DIALOG)) {
            if (!isDialogShowing) {
                isDialogShowing = true;
                return showWebDialog(args, callbackContext);
            }
        }

        if (action.equals(DATE_DIALOG)) {
            if (!isDialogShowing) {
                isDialogShowing = true;
                return showDateDialog(args, callbackContext);
            }
        }

//        //从手机系统中选取一张图片
//        if (action.equals(GET_PICTURE)){
//            this.callbackContext = callbackContext;
//            return getPicture(args, callbackContext);
//        }
        return false;
    }


    /**
     * Toast提示
     */
    private boolean showToast(JSONArray args) {
        final String message = args.optString(0);
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.toast(context, message);
            }
        });
        return true;
    }

    /**
     * Alert 对话框
     */
    private boolean showAlert(JSONArray args, final CallbackContext callbackContext) {
        final String title      = args.optString(0);
        final String message    = args.optString(1);
        final String btnString  = context.getString(R.string.com_confirm);

        this.cordova.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog();
                alertDialog.setTitle(title);
                alertDialog.setMessage(message);
                alertDialog.setButtons(new String[]{btnString});
                alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate(){
                    @Override
                    public void onButtonClick(AlertDialog dialog, View view, int index) {
                        super.onButtonClick(dialog, view, index);
                        if (index ==0){
                            callbackContext.success("");
                            dialog.dismiss();
                        }
                    }
                });
                if(cordova.getActivity().isFinishing()){
                    return;
                }
                alertDialog.show(fragmentManager);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDialogShowing = false;
            }
        }, DELAY_TIME);

        return true;
    }

    /**
     * Confirm 对话框
     */
    private boolean showConfirm(JSONArray args, final CallbackContext callbackContext) {
        final String title      = args.optString(0);
        final String message    = args.optString(1);
        final String leftBtn    = args.optString(2).equals("null") ? "取消" : args.optString(2);
        final String rightBtn   = args.optString(3).equals("null") ? "确定" : args.optString(3);

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog alertDialog = new AlertDialog();
                alertDialog.setTitle(title);
                alertDialog.setMessage(message);
                alertDialog.setButtons(new String[]{leftBtn,rightBtn});
                alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate(){
                    @Override
                    public void onButtonClick(AlertDialog dialog, View view, int index) {
                        super.onButtonClick(dialog, view, index);
                        switch (index){
                            case 0:
                                callbackContext.success(0);
                                break;
                            case 1:
                                callbackContext.success(1);
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                alertDialog.show(fragmentManager);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDialogShowing = false;
            }
        }, DELAY_TIME);

        return true;
    }


    /**
     * 显示加载对话框
     */
    private boolean showProgress(JSONArray args) {
        String msg  = args.optString(0);
        msg         = StringUtil.isEmpty(msg) ? "" : msg;
        final String finalMsg = msg;

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progressDialog = new ProgressDialog(context);
                progressDialog.setProgressMessage(finalMsg);
                progressDialog.show();
            }
        });
        return true;
    }

    /**
     * 隐藏加载对话框
     */
    private boolean hideProgress(final CallbackContext callbackContext) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (progressDialog != null) progressDialog.dismiss();

            }
        });
        return true;
    }
    /**
     * 显示图片的Dialog
     */
    private boolean showCustomImgDialog(JSONArray args, final CallbackContext callbackContext) {
        final String title      = args.optString(0);
        final String img        = args.optString(1);
        final String msg        = args.optString(2);
        final String showInput  = args.optString(3);

        if(StringUtil.isNotEmpty(img) && img.contains("/")){
            String path = "";
            if(img.startsWith("resources")){
                path = PackageFileManager.getInstance().getPmobilePath()+"/webapp/"+img;
            }

            if(img.startsWith("images")){
                path = PackageFileManager.getInstance().getPmobilePath()+"/"+img;
            }

            final String localPath = path;
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogController.getInstance().showImageDialog(context,
                            title,
                            msg,
                            localPath,
                            context.getString(R.string.complete), showInput,
                            new DialogController.DialogConfirmClick() {

                                @Override
                                public void onDialogConfirmClick(Object data) {
                                    if (showInput.equals("1")) {
                                        //String inputString = "{data:"+ (String) data +"}";
                                        callbackContext.success((String) data);
                                    }
                                }

                                @Override
                                public void onDialogCancelClick() {
                                }
                            }
                    );
                }
            });
        }else{
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogController.getInstance().showImageDialog(context,
                            title,
                            msg,
                            ImageUtil.getDrawableIdentifier(context.getApplicationContext(), img),
                            context.getString(R.string.complete), showInput,
                            new DialogController.DialogConfirmClick() {

                                @Override
                                public void onDialogConfirmClick(Object data) {
                                    if (showInput.equals("1")) {
                                        //String inputString = "{data:"+ (String) data +"}";
                                        callbackContext.success((String) data);
                                    }
                                }

                                @Override
                                public void onDialogCancelClick() {
                                }
                            }
                    );
                }
            });
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDialogShowing = false;
            }
        }, DELAY_TIME);

        return true;
    }

    /**
     * 显示webview对话框
     *
     * @param args
     * @param callbackContext
     * @return
     */
    private boolean showWebDialog(JSONArray args, CallbackContext callbackContext) {
        final String content = args.optString(0);
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String head = PackageFileManager.getInstance().getAgreementsDirPath();
                DialogController.getInstance().showWebViewDialog(context, "file://" + head+ content);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDialogShowing = false;
            }
        }, DELAY_TIME);

        return true;
    }

    /**
     * 日期选择对话框
     *
     * @param callbackContext
     * @return
     */
    private boolean showDateDialog(JSONArray args, final CallbackContext callbackContext) {
        JSONObject jsonObject   = args.optJSONObject(0);
        String formatString            = jsonObject.optString("formate");
        final String date       = jsonObject.optString("date");
        final String minDate       = jsonObject.optString("minDate");
        final String maxDate       = jsonObject.optString("maxDate");
        if (StringUtil.isEmpty(formatString)){
            formatString = "yyyy-MM-dd";
        }
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        Date initDate =null;
        Calendar calendar     = Calendar.getInstance(Locale.CHINA);
        int initYear          = calendar.get(Calendar.YEAR);
        int initMonth         = calendar.get(Calendar.MONTH);
        int initDay           = calendar.get(Calendar.DAY_OF_MONTH);
        try{
            initDate = (Date)format.parseObject(date);
        }catch (Exception e){
            LogUtil.print(e.getMessage());
        }
        if (initDate!=null){
            Calendar calendar0 = Calendar.getInstance(Locale.CHINA);
            calendar.setTime(initDate);
            initYear          = calendar.get(Calendar.YEAR);
            initMonth         = calendar.get(Calendar.MONTH);
            initDay           = calendar.get(Calendar.DAY_OF_MONTH);
        }
        final int year  = initYear;
        final int month = initMonth;
        final int day   = initDay;
        final SimpleDateFormat sdf = format;
        final String inFormat = formatString;
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        try {
                            Date setDate = new Date(year - 1900, month, day);
                            String setDateString = DateUtil.formatDate(setDate, inFormat);

                            if (StringUtil.isNotEmpty(minDate)) {
                                Date miDate = sdf.parse(minDate);

                                if (setDate.compareTo(miDate) == -1) {
                                    ToastUtil.toast(context, "不能早于" + date);
                                } else {
                                    callbackContext.success(setDateString);
                                }
                            }
                            if (StringUtil.isNotEmpty(maxDate)) {
                                Date maDate = sdf.parse(maxDate);
                                if (setDate.compareTo(maDate) == 1) {
                                    ToastUtil.toast(context, "不能晚于" + date);
                                } else {
                                    callbackContext.success(setDateString);
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDialogShowing = false;
            }
        }, DELAY_TIME);

        return true;
    }


    /**
     * 支付密码对话框
     */
    private boolean payPassword(JSONArray args, final CallbackContext callbackContext) {
        JSONObject jsonObject   = args.optJSONObject(0);
        final int type          = jsonObject.optInt("type");
        final String title      = jsonObject.optString("title");
        final String message    = jsonObject.optString("message");
        final boolean encrypt   = jsonObject.optBoolean("encrypt", true);

        context.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                PayPwdProcess.showInputPasswordDialog(context, type, title, message, encrypt, new DialogController.DialogConfirmClick() {
                    @Override
                    public void onDialogConfirmClick(Object data) {
                        callbackContext.success((String) data);
                    }

                    @Override
                    public void onDialogCancelClick() {
                        callbackContext.error("");
                    }
                });
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDialogShowing = false;
            }
        }, DELAY_TIME);

        return true;
    }

    /**
     * 输入验证码对话框
     */
    private boolean commonInput(JSONArray args, final CallbackContext callbackContext) {
        JSONObject jsonObject   = args.optJSONObject(0);
        final String title      = jsonObject.optString("title");
        final int minLength     = jsonObject.optInt("minLength");
        final int maxLength     = jsonObject.optInt("maxLength");
        final boolean encrypt   = jsonObject.optBoolean("encrypt");

        this.cordova.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                DialogController.getInstance().showPwVercodeDialog(context, title, "", minLength, maxLength, false, null, encrypt, false,

                        new DialogController.DialogConfirmClick() {

                            @Override
                            public void onDialogConfirmClick(Object vercode) {
                                callbackContext.success((String) vercode);
                            }

                            @Override
                            public void onDialogCancelClick() {
                                callbackContext.error("");
                            }
                        }
                );
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDialogShowing = false;
            }
        }, DELAY_TIME);

        return true;
    }

    /**
     * 输入图片验证码对话框
     */
    private boolean imageInput(JSONArray args, final CallbackContext callbackContext) {
        JSONObject jsonObject       = args.optJSONObject(0);
        final String title          = jsonObject.optString("title");
        final int minLength         = jsonObject.optInt("minLength");
        final int maxLength         = jsonObject.optInt("maxLength");
        final boolean encrypt       = jsonObject.optBoolean("encrypt");
        final String mBase64Vercode = args.optString(1);

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogController.getInstance().showPwVercodeDialog(context,
                        title, "", minLength, maxLength, true, mBase64Vercode, encrypt, false,

                        new DialogController.DialogConfirmClick() {
                            @Override
                            public void onDialogConfirmClick(Object vercode) {
                                callbackContext.success((String) vercode);
                            }

                            @Override
                            public void onDialogCancelClick() {
                                callbackContext.error("");
                            }
                        }
                );
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDialogShowing = false;
            }
        }, DELAY_TIME);

        return true;
    }

    /**
     * 选择选择列表对话框
     */
    private boolean showSelect(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        ArrayList<CommmonSelectData> datas  = new ArrayList<CommmonSelectData>();
        final String title                  = args.getString(0);
        final boolean mulitselect           = args.getBoolean(1);
        JSONArray jsonArray                 = (JSONArray) args.get(2);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject   = jsonArray.getJSONObject(i);
            CommmonSelectData data  = new CommmonSelectData();
            data.setLeftTopText(jsonObject.optString("Maintitle"));
            data.setLeftBottomText(jsonObject.optString("Subtitle"));
            data.setMergeFlag(jsonObject.optString("MergeFlag"));
            if (jsonObject.optInt("IsSelected") == 0) {
                data.setSelected(false);
            } else {
                data.setSelected(true);
            }
            data.setItemId(jsonObject.optString("Index"));
            datas.add(data);
        }
        final ArrayList<CommmonSelectData> datas0 = datas;

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mulitselect) {
                    DialogController.getInstance().showMutiSelectDialog(context, title, datas0, new DialogController.DialogConfirmClick() {
                        @Override
                        public void onDialogConfirmClick(Object dataLists) {
                            ArrayList<Integer> selectIndexs = (ArrayList<Integer>) dataLists;
                            JSONArray jsonArray = new JSONArray();
                            for (int i = 0; i < selectIndexs.size(); i++) {
                                int index = Integer.parseInt(datas0.get(selectIndexs.get(i)).getItemId());
                                jsonArray.put(index);
                            }
                            callbackContext.success(jsonArray);
                        }

                        @Override
                        public void onDialogCancelClick() {
                            callbackContext.error("");
                        }
                    });
                } else {
                    DialogController.getInstance().showSingleSelectDialog(context, title, datas0, new DialogController.DialogConfirmClick() {
                        @Override
                        public void onDialogConfirmClick(Object dataLists) {
                            ArrayList<Integer> selectIndexs = (ArrayList<Integer>) dataLists;
                            JSONArray jsonArray = new JSONArray();
                            for (int i = 0; i < selectIndexs.size(); i++) {
                                if (selectIndexs.get(i) != -1) {
                                    int index = Integer.parseInt(datas0.get(selectIndexs.get(i)).getItemId());
                                    jsonArray.put(index);
                                }
                            }
                            if (jsonArray.length() > 0) {
                                callbackContext.success(jsonArray);
                            } else {
                                callbackContext.error("");
                            }
                        }

                        @Override
                        public void onDialogCancelClick() {
                            callbackContext.error("");
                        }
                    });
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDialogShowing = false;
            }
        }, DELAY_TIME);

        return true;
    }

    /**
     * 选择支付方式对话框
     *
     * @param args
     * @param callbackContext
     * @return
     * @throws org.json.JSONException
     */
    private boolean showPaymentSelectListDialog(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        final String title          = args.optString(0);
        final JSONArray jsonArray   = (JSONArray) args.get(1);
        ArrayList<PaymentTypeSelector.PaymentType> paymentTypes = new ArrayList<PaymentTypeSelector.PaymentType>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            PaymentTypeSelector.PaymentType paymentType = new PaymentTypeSelector.PaymentType();
            paymentType.setDescription(jsonObject.getString("Title"));
            paymentTypes.add(paymentType);
        }
        final ArrayList<PaymentTypeSelector.PaymentType> paymentTypes0 = paymentTypes;
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PaymentTypeSelector paymentTypeSelector = new PaymentTypeSelector(context, title, paymentTypes0, new PaymentTypeSelectListener() {
                    @Override
                    public void onBalancePaySelected() {
                    }

                    @Override
                    public void onQuickPaySelected(QuickCard quickCard) {
                    }

                    @Override
                    public void onSwipePaySelected() {
                    }

                    @Override
                    public void onAddQuickCardSelected() {
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onItemSelected(Object object) {
                        int position = (Integer) object;
                        JSONObject jsonObject = jsonArray.optJSONObject(position).optJSONObject("Data");
                        callbackContext.success(jsonObject);
                    }
                });
                paymentTypeSelector.show();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDialogShowing = false;
            }
        }, DELAY_TIME);

        return true;
    }
}
