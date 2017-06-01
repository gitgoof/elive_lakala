package com.lakala.shoudan.activity.payment.signature;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.ImageUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.loopj.lakala.http.RequestParams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by More on 14-1-26.
 */
public class SignatureManager {

    /**
     * sp key
     */
    public static class UploadKey {
        public static final String MOBILE_NO = "mobile_no";//转出卡号
        public static final String PAN = "pan";//转出卡号
        public static final String SID = "sid";
        public static final String CHRTT_CODE = "chrtt_code";
        public static final String UPLOAD_COUNT = "upload_count";
        public static final String FILE_NAME = "signature_file_name";
        public static final String SIGNATRUE_UPLOAD_ACTION = "com.lakala.shoudan.ui.business.shoudan.signature.SignatruePollingService";
    }

    private SharedPreferences sp;
    private SharedPreferences.Editor et;
    private Context context;
    private static SignatureManager instance = new SignatureManager();

    public Bitmap showPic;
    public Bitmap uploadPic;

    /**
     * 签名文件保存名
     **/
    public static final String fileName = "temp_signature.jpg";

    private SignatureManager() {
        context = ApplicationEx.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        et = sp.edit();
        initPaint();
    }

    public static SignatureManager getInstance() {

        if (instance == null) {
            instance = new SignatureManager();
        }
        return instance;

    }

    /**
     * 开启电子签名补签
     * 保存至本地
     */
    public void startSignatruePollingSerice(String sid, String chrttCode, String pan, String mobileNo) {
        if (!savedSignatureCache(getMarkBitmap(chrttCode))) {
            //保存电子签名
            putUploadCount(-1);
            return;
        }
        saveMobileNo(mobileNo);
        savePan(pan);
        saveSid(sid);
        saveChrttCode(chrttCode);
        putUploadCount(5);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SignatruePollingService.class);
        intent.setAction(UploadKey.SIGNATRUE_UPLOAD_ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long triggerAtTime = SystemClock.elapsedRealtime();
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime, 60 * 1000, pendingIntent);
    }

    /**
     * 结束补签
     */
    public void stopSignatruePollingService() {
        putUploadCount(-1);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //删除文件????
        Intent intent = new Intent(context, SignatruePollingService.class);
        intent.setAction(UploadKey.SIGNATRUE_UPLOAD_ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);
    }


    /**
     * 保存电子签名文件名
     *
     * @param fileName
     */
    public void savePrefarenceFileName(String fileName) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(UploadKey.FILE_NAME, fileName);
        editor.commit();
    }

    /**
     * 保存特征码
     *
     * @param code
     */
    public void saveChrttCode(String code) {
        et.putString(UploadKey.CHRTT_CODE, code);
        et.commit();
    }

    /**
     * 获取特征码
     *
     * @return
     */
    public String getChrttCode() {
        return sp.getString(UploadKey.CHRTT_CODE, "");
    }

    /**
     * 获取上一次保存的签名文件地址
     *
     * @return
     */
    public String getSavedFileName() {
        return sp.getString(UploadKey.FILE_NAME, "");
    }

    /**
     * 保存电子签名
     *
     * @param bitmap
     */
    private boolean savedSignatureCache(final Bitmap bitmap) {
        try {
            return ImageUtil.saveLoacalPhotoImage(ApplicationEx.getInstance(), ApplicationEx.getInstance().getUser().getLoginName(), bitmap);
        } catch (Exception e) {
            LogUtil.print(e);
            return false;
        }
    }

    /**
     * 获取保存的签名
     *
     * @return
     */
    public Bitmap getSignatureBitmap() {

        return ImageUtil.getLoacalPhotoImage(ApplicationEx.getInstance(), ApplicationEx.getInstance().getUser().getLoginName());
    }

    /**
     * 剩余上传次数
     *
     * @return
     */
    public int getUploadCount() {
        return sp.getInt(UploadKey.UPLOAD_COUNT, -1);
    }

    /**
     * 设置剩余上传次数
     *
     * @param count
     */
    public void putUploadCount(int count) {
        et.putInt(UploadKey.UPLOAD_COUNT, count);
        et.commit();
    }

    /**
     * 获取 Sid
     *
     * @return
     */
    public String getSid() {
        return sp.getString(UploadKey.SID, "");
    }

    public void saveSid(String sid) {
        et.putString(UploadKey.SID, sid);
        et.commit();
    }

    public void savePan(String pan) {
        et.putString(UploadKey.PAN, pan);
        et.commit();
    }

    public void saveMobileNo(String mobileNo) {
        if (mobileNo == null) {
            mobileNo = "";
        }
        et.putString(UploadKey.MOBILE_NO, mobileNo);
        et.commit();
    }

    public String getPan() {
        return sp.getString(UploadKey.PAN, "");
    }

    public String getMobileNo() {
        return sp.getString(UploadKey.MOBILE_NO, "");
    }

    /**
     * 补签
     *
     * @param listener
     */
    public void startUpload(final UploadListener listener) {
        int count = getUploadCount();
        if (count <= 0) {
            stopSignatruePollingService();
            listener.onUploadFinish(false);
            return;
        }
        final String sid = getSid();
        final String chrtt = getChrttCode();
        final String pan = getPan();
        final String mobileNo = getMobileNo();

        if (sid.length() == 0 || chrtt.length() == 0) {
            //Log.d("Lakalalkl", "Length == 0");
            stopSignatruePollingService();
            listener.onUploadFinish(false);
            return;
        }

        putUploadCount(--count);
        uploadService(sid, chrtt, pan, mobileNo, listener);
    }

    public void uploadService(final String sid, final String chrtt, final String pan, final String mobileNo, final UploadListener listener) {

        try {
            final Bitmap bitmap = getSignatureBitmap();
            if (bitmap == null) {
                return;
            }
            uploadReqeust(sid, chrtt, pan, mobileNo, bitmap,
                    new ResultDataResponseHandler(new ServiceResultCallback() {
                        @Override
                        public void onSuccess(ResultServices resultServices) {
                            LogUtil.print(resultServices.retMsg);
                            listener.onUploadFinish(true);
                        }

                        @Override
                        public void onEvent(HttpConnectEvent connectEvent) {
                            LogUtil.print(connectEvent.getDescribe());
                            listener.onUploadFinish(false);
                        }
                    }));


        } catch (Exception e) {
            LogUtil.print("tag", "Upload Failed", e);
            listener.onUploadFinish(false);
        }

    }

    private void uploadReqeust(final String signStatus ,final String sendVouch ,final String sid, final String chrtt, final String pan, final String mobileNo, Bitmap bitmap, ResultDataResponseHandler listener) {
        BusinessRequest businessRequest = BusinessRequest.obtainRequest("v1.0/trade/sign/" + sid, HttpRequest.RequestMethod.POST);
        RequestParams requestParams = businessRequest.getRequestParams();
        requestParams.put("sid", sid);
        requestParams.put("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
        requestParams.put("termid", ApplicationEx.getInstance().getSession().getCurrentKSN());
        requestParams.put("characteristicCode", chrtt);
        requestParams.put("custContact", mobileNo);
        requestParams.put("custContactType", "mobile");
        requestParams.put("series", Utils.createSeries());
        requestParams.put("chntype", BusinessRequest.CHN_TYPE);
        requestParams.put("tdtm", Utils.createTdtm());
        requestParams.put("pan", pan);
        requestParams.put("chncode", BusinessRequest.CHN_CODE);
        requestParams.put("busid", "1CK");

        if (bitmap!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
            requestParams.put("eReceiptData", sbs);
        }
        requestParams.put("signStatus", signStatus);

        businessRequest.setResponseHandler(listener);
        businessRequest.execute();
    }
    private void senduploadReqeust(final String pasm ,final String dealStartDateTime ,final String signStatus ,final String sid, final String chrtt, final String pan, final String mobileNo,  ResultDataResponseHandler listener) {
        BusinessRequest businessRequest = BusinessRequest.obtainRequest("v1.0/trade/sign/" + sid+"/send", HttpRequest.RequestMethod.POST);
        RequestParams requestParams = businessRequest.getRequestParams();
        requestParams.put("sid", sid);
        requestParams.put("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
        requestParams.put("termid",pasm);
        requestParams.put("characteristicCode", chrtt);
        requestParams.put("custContact", mobileNo);
        requestParams.put("custContactType", "mobile");
        requestParams.put("series", Utils.createSeries());
        requestParams.put("chntype", BusinessRequest.CHN_TYPE);
        requestParams.put("tdtm", dealStartDateTime.substring(2,dealStartDateTime.length()));
        requestParams.put("pan", pan);
        requestParams.put("chncode", BusinessRequest.CHN_CODE);
        requestParams.put("busid", "1CK");

        requestParams.put("signStatus", signStatus);

        businessRequest.setResponseHandler(listener);
        businessRequest.execute();
    }
    private void uploadReqeust(final String sid, final String chrtt, final String pan, final String mobileNo, Bitmap bitmap, ResultDataResponseHandler listener) {
        BusinessRequest businessRequest = BusinessRequest.obtainRequest("v1.0/trade/sign/" + sid, HttpRequest.RequestMethod.POST);
        RequestParams requestParams = businessRequest.getRequestParams();
        requestParams.put("sid", sid);
        requestParams.put("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
        requestParams.put("termid", ApplicationEx.getInstance().getSession().getCurrentKSN());
        requestParams.put("characteristicCode", chrtt);
        requestParams.put("custContact", mobileNo);
        requestParams.put("custContactType", "mobile");
        requestParams.put("series", Utils.createSeries());
        requestParams.put("chntype", BusinessRequest.CHN_TYPE);
        requestParams.put("tdtm", Utils.createTdtm());
        requestParams.put("pan", pan);
        requestParams.put("chncode", BusinessRequest.CHN_CODE);
        requestParams.put("busid", "1CK");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
        requestParams.put("eReceiptData", sbs);
        requestParams.put("sendVouch", "0");
        requestParams.put("signStatus", "0");

        businessRequest.setResponseHandler(listener);
        businessRequest.execute();
    }

    public void uploadCacheSignatrue(final String signStatus ,final String sendVouch ,final String sid, final String chrtt, final String pan, final String mobileNo, final UploadListener listener) {


        try {
            Bitmap bitmap = getMarkBitmap(chrtt);
            uploadReqeust(signStatus,sendVouch,sid, chrtt, pan, mobileNo, bitmap,
                    new ResultDataResponseHandler(new ServiceResultCallback() {
                        @Override
                        public void onSuccess(ResultServices resultServices) {
                            LogUtil.print(resultServices.retMsg);
                            listener.onUploadFinish(true);
                        }

                        @Override
                        public void onEvent(HttpConnectEvent connectEvent) {
                            listener.onUploadFinish(false);
                        }
                    }));
        } catch (Exception e) {
            LogUtil.print("tag", "upload failed", e);
            listener.onUploadFinish(false);
        }


    }
    public void sendSignatrue(final String pasm ,final String dealStartDateTime ,final String signStatus ,final String sid, final String chrtt, final String pan, final String mobileNo, final UploadListener listener) {


        try {

            senduploadReqeust(pasm,dealStartDateTime,signStatus,sid, chrtt, pan, mobileNo,
                    new ResultDataResponseHandler(new ServiceResultCallback() {
                        @Override
                        public void onSuccess(ResultServices resultServices) {
                            LogUtil.print(resultServices.retMsg);
                            listener.onUploadFinish(true);
                        }

                        @Override
                        public void onEvent(HttpConnectEvent connectEvent) {
                            listener.onUploadFinish(false);
                        }
                    }));
        } catch (Exception e) {
            LogUtil.print("tag", "upload failed", e);
            listener.onUploadFinish(false);
        }


    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "TIMES.TTF");
        paint.setTypeface(tf);
        paint.setTextSize(32);

    }

    /**
     * 上送线程监听器
     */
    public interface UploadListener {
        public void onUploadFinish(boolean ifSuccess);
    }

    private Paint paint;

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Bitmap getMarkBitmap(String str) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        int strh = (int) (fm.descent - fm.ascent);
        int strw = (int) paint.measureText(str);
        int as = (int) fm.ascent;
        Bitmap bmp = showPic;
        int bw = bmp.getWidth();
        int bh = bmp.getHeight();
        Bitmap nbmp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(nbmp);
        c.drawBitmap(bmp, 0, 0, new Paint());
        c.save();
        c.drawText(str, (bw - strw) / 2, (bh - strh) / 2 - as, paint);
        return nbmp;
    }



    // 获取交易特征码

    /**
     * @param fulldate MMdd
     * @param ref
     * @return
     */
    public static String getTransCode(String fulldate, String ref) {
        if (TextUtils.isEmpty(fulldate)) {
            return "";
        }
        String date = fulldate.substring(0, 5).replace("-", "");
        if (date.length() == 4 && ref.length() >= 4) {
            date = date + ref.substring(0, 4);
            ref = ref.substring(4);
            byte[] dateb = date.getBytes();
            byte[] refb = ref.getBytes();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < dateb.length; i++) {
                sb.append(Integer.toHexString(dateb[i] ^ refb[i]));
            }
            return sb.toString();
        } else {

            return "99999";

        }
    }

}
