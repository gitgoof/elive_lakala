package com.lakala.platform.cordovaplugin;

import android.support.v4.app.FragmentActivity;

import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaPlugin;
import com.lakala.core.cordova.cordova.PluginResult;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.activity.BaseCordovaWebActivity;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.swiper.mts.CardInfo;
import com.lakala.platform.swiper.mts.SetSwipeTypeActivity;
import com.lakala.platform.swiper.mts.SwipeDefine;
import com.lakala.platform.swiper.mts.SwipeLauncher;
import com.lakala.platform.swiper.mts.SwipeListener;
import com.lakala.platform.swiper.mts.TcDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vinchaos api on 14-1-20.
 * 调用刷卡插件
 * 返回object
 * {
 * encTracks:磁道,
 * randomNumber:随机数,
 * maskedPANString:卡,
 * ksn:ksn
 * }
 */
public class SwipePlugin extends CordovaPlugin implements SwipePluginListener {

    //启动刷卡
    private static final String ACTION_SWIPE            = "swipe";
    //停止刷卡
    private static final String ACTION_STOP             = "stop";
    //加密
    private static final String ACTION_ENCRYPT          = "encrypt";
    //发送pin
    private static final String ACTION_SEND_PIN         = "sendPin";
    //保存TC
    private static final String ACTION_STORE_TC         = "storeTC";
    //二次授权
    private static final String ACTION_SECOND_ISSUANCE  = "doSecondInssuance";
    //EMV FINISH
    private static final String ACTION_EMV_FINISH       = "emvFinish";

    private CallbackContext callbackContext;
    private PluginResult cordovaResult;
    private FragmentActivity activity;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        activity = (FragmentActivity) this.cordova.getActivity();
        if (action.equalsIgnoreCase(ACTION_SWIPE))
            return onSwipe(args, callbackContext);
        if (action.equalsIgnoreCase(ACTION_STOP))
            return stopSwipe();
        if (action.equalsIgnoreCase(ACTION_ENCRYPT))
            return encrypt(callbackContext, args);
        if (action.equalsIgnoreCase(ACTION_SEND_PIN))
            return sendPin(args, callbackContext);
        if (action.equalsIgnoreCase(ACTION_STORE_TC))
            return storeTC(args, callbackContext);
        if (action.equalsIgnoreCase(ACTION_SECOND_ISSUANCE))
            return doSecondIssuance(args, callbackContext);
        if (action.equalsIgnoreCase(ACTION_EMV_FINISH))
            return emvFinish(args, callbackContext);
        return false;
    }

    /**
     * 调用刷卡
     */
    private boolean onSwipe(JSONArray args, CallbackContext callbackContext) {

        if (activity instanceof BaseCordovaWebActivity) {
            ((BaseCordovaWebActivity) activity).setSwipePluginListener(this);
        }

        this.callbackContext = callbackContext;
        String tempTitle     = args.optString(0);
        if(tempTitle.length() > 10){
            tempTitle = tempTitle.substring(0,10);
        }
        final String title     = tempTitle;
        final String amount    = args.optString(1);
        final JSONObject business  = args.optJSONObject(2);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SwipeLauncher.getInstance().setActivity(activity);
                SwipeLauncher.getInstance().setSwipeListener((SwipeListener) activity);
                SwipeLauncher.getInstance().setStartParameters(title, amount, SwipeDefine.SwipeCollectionType.CONSUMPTION);
                SwipeLauncher.getInstance().launch(business);
                SwipeLauncher.getInstance().setShowDialog(true);
            }
        });
        return true;
    }

    /**
     * 。数据加密
     */
    private boolean encrypt(final CallbackContext callbackContext, JSONArray args) {
        final String originalString = args.optString(0);
        final String termId = args.optString(1);
        callbackContext.success(CommonEncrypt.pinKeyDesRsaEncrypt(termId, originalString));
        return true;
    }

    /**
     * 停止刷卡
     */
    private boolean stopSwipe() {
        SwipeLauncher.getInstance().stopSwipe();
        return true;
    }


    /**
     * 当用手机输完 pin 后，需要调用此插件方法将 pin 送回刷卡模块。
     */
    private boolean sendPin(JSONArray args, CallbackContext callbackContext) {
        String pin = args.optString(0);
        SwipeLauncher.getInstance().sendPin(pin);
        return true;
    }

    /**
     * IC 卡交易二次授权
     */
    private boolean doSecondIssuance(JSONArray args, CallbackContext callbackContext) {

        //联机交易结果,所有交易实体
        JSONObject onlineResult = args.optJSONObject(0);
        //授权应答码 AuthorisationResponseCode，'00' 表示成功，'01' 表示失败..
        String authCode     = args.optString(1);

        SwipeLauncher.getInstance().doSecondIssuance(authCode, onlineResult.optString("Icc55"));
        return true;
    }

    /**
     * 上送 TC 失败后需要调用此方法保存 TC 值到终端中。
     */
    private boolean storeTC(JSONArray args, CallbackContext callbackContext) {
        JSONObject parameter = args.optJSONObject(0);
        //保存TC,内部异步上传TC
        TcDao.getInstance().storeTc(parameter);
        return true;
    }

    /**
     * EMV FINISH
     *
     * @param args
     * @param callbackContext
     * @return
     */
    private boolean emvFinish(JSONArray args, CallbackContext callbackContext){
//        SwipeLauncher.getInstance().cancelCardRead();
        SwipeLauncher.getInstance().cancelEmv(false);
        return true;
    }

    /**
     * 获取onReadCardPin返回实体
     *
     * @param cardInfo
     * @return
     */
    private JSONObject getReadCardPinObj(CardInfo cardInfo) {

        JSONObject result  = new JSONObject();
        JSONObject cardPin = new JSONObject();
        try {
            cardPin.put("Otrack", cardInfo.getOtrack());
            cardPin.put("Pinkey", cardInfo.getPinkey());
            cardPin.put("PinLength", cardInfo.getPinLength());
            cardPin.put("Random", cardInfo.getRandom());
            cardPin.put("CardType", cardInfo.getCardType());
            cardPin.put("Track2", cardInfo.getTrack2());
            cardPin.put("Icc55", cardInfo.getIcc55());
            cardPin.put("Posemc", cardInfo.getPosemc());
            cardPin.put("CardSn", cardInfo.getCardSn());
            cardPin.put("Pan", cardInfo.getPan());
            cardPin.put("MaskPan", cardInfo.getMaskPan());
            cardPin.put("Ksn", SwipeLauncher.getInstance().getKsn());
            cardPin.put("ChnType", cardInfo.getChnType());

            result.put("cardpin", cardPin);
            result.put("event", "onReadCardPin");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onCancel(String data) {
        JSONObject result = null;
        try {
            result = new JSONObject(data);
        } catch (JSONException e) {
            LogUtil.print(e);
        }

        cordovaResult = new PluginResult(PluginResult.Status.OK, result);
        cordovaResult.setKeepCallback(false);
        callbackContext.sendPluginResult(cordovaResult);
    }

    @Override
    public void onRequestPin(String maskPan) {

        JSONObject result = new JSONObject();
        try {
            result.put("event", "onRequestPin");
            result.put("maskPan", maskPan);
        } catch (JSONException e) {
            LogUtil.print(e);
        }

        cordovaResult = new PluginResult(PluginResult.Status.OK, result);
        cordovaResult.setKeepCallback(true);
        callbackContext.sendPluginResult(cordovaResult);
    }

    @Override
    public void onReadCardPin(CardInfo cardInfo, SwipeDefine.SwipeCardType swipeCardType) {
        //刷卡成功后，不显示对话框
        SwipeLauncher.getInstance().setShowDialog(false);

        switch (swipeCardType) {
            //不带键盘磁条卡
            case MAGNETIC_NORMAL:
                cardInfo.setMagneticCard(true);
                break;
            //不带键盘IC卡
            case IC_NORMAL:
                cardInfo.setMagneticCard(false);
                break;
            //带键盘磁条卡
            case MAGNETIC_KEYBOARD:
                cardInfo.setMagneticCard(true);
                break;
            //带键盘IC卡
            case IC_KEYBOARD:
                cardInfo.setMagneticCard(false);
                break;
        }

        cordovaResult = new PluginResult(PluginResult.Status.OK, getReadCardPinObj(cardInfo));
        //如果不是磁条卡则保持Callback
        cordovaResult.setKeepCallback(!cardInfo.isMagneticCard());
        callbackContext.sendPluginResult(cordovaResult);

    }

    /**
     * 当完成EMV交易流程时触发该事件。
     */
    @Override
    public void onFinish(boolean isSuccess, JSONObject tc) {

        JSONObject result = new JSONObject();
        try {
            result.put("event", "onFinish");
            result.put("tc", tc);
            result.put("isSuccess", isSuccess);
        } catch (JSONException e) {
            LogUtil.print(e);
        }

        cordovaResult = new PluginResult(PluginResult.Status.OK, result);
        cordovaResult.setKeepCallback(false);
        callbackContext.sendPluginResult(cordovaResult);
    }

    @Override
    public void onSecondIssuanceFail() {

        JSONObject result = new JSONObject();
        try {
            result.put("event", "onSecondIssuanceFail");
        } catch (JSONException e) {
            LogUtil.print(e);
        }

        cordovaResult = new PluginResult(PluginResult.Status.OK, result);
        cordovaResult.setKeepCallback(false);
        callbackContext.sendPluginResult(cordovaResult);
    }

    @Override
    public void onStartSwiper() {


        JSONObject result = new JSONObject();
        try {
            result.put("event", "onStartSwiper");
        } catch (JSONException e) {
            LogUtil.print(e);
        }

        cordovaResult = new PluginResult(PluginResult.Status.OK, result);
        cordovaResult.setKeepCallback(true);
        callbackContext.sendPluginResult(cordovaResult);
    }

    @Override
    public void onWaitInputPinTimeout() {

        JSONObject result = new JSONObject();
        try {
            result.put("event", "onWaitInputPinTimeout");
        } catch (JSONException e) {
            LogUtil.print(e);
        }

        cordovaResult = new PluginResult(PluginResult.Status.OK, result);
        cordovaResult.setKeepCallback(true);
        callbackContext.sendPluginResult(cordovaResult);
    }

    @Override
    public void handleAction(int action) {
        if (action == SetSwipeTypeActivity.CREDIT_RESULT_CODE){
            JSONObject result = new JSONObject();
            try {
                result.put("event", "showSelectPayment");
            } catch (JSONException e) {
                LogUtil.print(e);
            }
            cordovaResult = new PluginResult(PluginResult.Status.OK, result);
            cordovaResult.setKeepCallback(false);
            callbackContext.sendPluginResult(cordovaResult);
        }
    }

}
