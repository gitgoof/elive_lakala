package com.lakala.shoudan.bll;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.lakala.platform.common.LklPreferences;


import com.lakala.platform.common.UILUtils;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.Parameters;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by More on 15/6/29.
 */
public class AdDownloadManager {

    public enum Type {
        EVENT("EVENT", "活动专区"), INDEX("INDEX", "首页"), INDEX_T("INDEX_T", "首页顶部"),
        INDEX_B("INDEX_B", "首页底部"), SPLASH("SPLASH", "启动页"),
        FIX_3_1("FIX_3.1", "3.1首页"), LICAI("LICAI", "理财"),
        DAIKUAN("DAIKUAN", "贷款首页");
        private String value;

        Type(String value, String description) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public final static String LOCAL_ADS = "local_ads";

    private static AdDownloadManager ourInstance = new AdDownloadManager();

    public static AdDownloadManager getInstance() {
        return ourInstance;
    }


    private AdDownloadManager() {
    }

    private void loadAdvertiseImg(final List<Advertise> advertises,
                                  final CountDownLatch latch) {
        for (final Advertise advertise : advertises) {
            UILUtils.load(
                    advertise.getContent(), new SimpleImageLoadingListener() {
                        HashMap<String, Boolean> uriMap = new HashMap<String, Boolean>();

                        @Override
                        public void onLoadingComplete(String imageUri, View view,
                                                      Bitmap loadedImage) {
                            uriMap.put(imageUri, true);
                            advertise.setLoadComplete(true);
                            latch.countDown();
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {
                            latch.countDown();
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                            super.onLoadingCancelled(imageUri, view);
                            if (uriMap.containsKey(imageUri) && uriMap.get(imageUri)) {
                                advertise.setLoadComplete(true);
                            } else {
                                advertise.setLoadComplete(false);
                            }
                            latch.countDown();
                        }
                    }
            );
        }
    }

    /**
     * @param adDownloadListener
     * @param type
     * @param justSuccess        是否只返回图片下载成功的广告
     */
    public void check(final AdDownloadListener adDownloadListener, final Type type,
                      final boolean justSuccess) {

        ShoudanService.getInstance().getAds(
                type.getValue(),
                "L",
                new ServiceResultCallback() {
                    @Override
                    public void onSuccess(ResultServices resultForService) {
                        boolean isSuccess = false;
                        List<Advertise> advertises = null;

                        if (resultForService.isRetCodeSuccess()) {
                            try {
                                final JSONArray ads = new JSONArray(resultForService.retData);
                                advertises = parseJSONArray2List(ads);
                                isSuccess = true;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            isSuccess = false;
                        }
                        handle2LoadImg(isSuccess, advertises, justSuccess, adDownloadListener);
                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {

                        boolean isSuccess = false;
                        List<Advertise> advertises = null;

                        String ads = LklPreferences.getInstance()
                                .getString(LOCAL_ADS + type.getValue());
                        try {
                            advertises = parseJSONArray2List(new JSONArray(ads));
                            isSuccess = true;
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            isSuccess = false;
                        }
                        handle2LoadImg(isSuccess, advertises, justSuccess, adDownloadListener);
                    }
                });


    }
    /**
     * @param
     * @param type
     * @param
     */
    public void check1(final Intent intent, final Type type,
                       final AppBaseActivity context) {

        ShoudanService.getInstance().getAds(
                type.getValue(),
                "L",
                new ServiceResultCallback() {
                    @Override
                    public void onSuccess(ResultServices resultForService) {
                        boolean isSuccess = false;
                        List<Advertise> advertises = null;

                        if (resultForService.isRetCodeSuccess()) {
                            try {
                                final JSONArray ads = new JSONArray(resultForService.retData);
                                advertises = parseJSONArray2List(ads);
                                isSuccess = true;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            isSuccess = false;
                        }
                        context.hideProgressDialog();
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("adverList", (ArrayList<? extends Parcelable>) advertises);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                        PublicToEvent.normalEvent(ShoudanStatisticManager.Loan_List_Homepage, context);

                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {
                    }
                });


    }

    private void handle2LoadImg(boolean isSuccess, List<Advertise> advertises,
                                final boolean justSuccess,
                                final AdDownloadListener adDownloadListener) {
        final boolean success = isSuccess;
        final List<Advertise> advs = advertises;
        new Thread(new Runnable() {
            @Override
            public void run() {
                handleRequestCallBack(success, justSuccess, advs, adDownloadListener);
            }
        }).start();
    }

    private void handleRequestCallBack(boolean isSuccess, final boolean justSuccess, List<Advertise> advertises, final AdDownloadListener adDownloadListener) {
        Looper looper = Looper.getMainLooper();
        android.os.Handler handler = new android.os.Handler(looper);
        if (isSuccess) {
            try {
                if (justSuccess) {
                    int size = advertises == null ? 0 : advertises.size();
                    CountDownLatch latch = new CountDownLatch(size);
                    if (latch.getCount() > 0) {
                        loadAdvertiseImg(advertises, latch);
                    }
                    latch.await(30, TimeUnit.SECONDS);
                }
                final List<Advertise> advertiseList = new ArrayList<Advertise>();
                for (Advertise advertise : advertises) {
                    if (!justSuccess || advertise.isLoadComplete()) {
                        advertiseList.add(advertise);
                    }
                }
                if (adDownloadListener != null) {
                    handler.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    adDownloadListener.onSuccess(advertiseList);
                                }
                            }
                    );
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (adDownloadListener != null) {
                    handler.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    adDownloadListener.onFailed();
                                }
                            }
                    );
                }
            }
        } else {
            if (adDownloadListener != null) {
                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                adDownloadListener.onFailed();
                            }
                        }
                );
            }
        }
    }

    public void check(final AdDownloadListener adDownloadListener, final Type type) {
        check(adDownloadListener, type, false);
    }

    private int interval = 5;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {

        if (interval <= 0) {
            return;
        }

        this.interval = interval;
    }

    private List<Advertise> parseJSONArray2List(JSONArray ads) throws JSONException {
        List<Advertise> advertises = new ArrayList<Advertise>();

        for (int i = 0; i < ads.length(); i++) {

            JSONObject ad = ads.getJSONObject(i);
            String ver = ad.optString("ver");
            setInterval(ad.optInt("runTime"));
            JSONArray advDatas = ad.getJSONArray("advData");
            for (int j = 0; j < advDatas.length(); j++) {
                JSONObject tempAdvData = advDatas.optJSONObject(j);
                String content = tempAdvData.optString("content");
                String id = tempAdvData.optString("id");
                String clickUrl = tempAdvData.optString("clickUrl");
                String title = tempAdvData.optString("webviewTitle");
                String remark = tempAdvData.optString("remark");
                String type = tempAdvData.optString("type", "PIC");
                int idx = tempAdvData.optInt("idx");
                advertises.add(new Advertise(content, id, clickUrl, ver, title, remark, type, idx));
            }
        }

        return advertises;

    }

    public static class Advertise implements Parcelable {

        int idx;
        String content = "";
        String id = "";
        String clickUrl = "";
        String ver = "";
        String title = "";
        String remark = "";
        String type = "";
        boolean loadComplete = false;

        public Advertise() {
            this.remark = "卡来我刷，码来我扫！\\n拉卡拉全能收单时代来临";
            this.clickUrl = Parameters.DEFAULT_AD_URL;
            this.type = "PIC";
            this.content = "drawable://" + R.drawable.pic_advertising;

        }

        public Advertise(String content, String id, String clickUrl, String ver, String title, String type) {
            this.content = content;
            this.id = id;
            this.clickUrl = clickUrl;
            this.ver = ver;
            this.title = title;
            this.type = type;
        }

        public Advertise(String content, String id, String clickUrl, String ver, String title,
                         String remark, String type, int idx) {
            this.content = content;
            this.id = id;
            this.clickUrl = clickUrl;
            this.ver = ver;
            this.title = title;
            this.remark = remark;
            this.type = type;
            this.idx = idx;
        }

        public int getIdx() {
            return idx;
        }

        public boolean isLoadComplete() {
            return loadComplete;
        }

        public Advertise setLoadComplete(boolean loadComplete) {
            this.loadComplete = loadComplete;
            return this;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getClickUrl() {
            return clickUrl;
        }

        public void setClickUrl(String clickUrl) {
            this.clickUrl = clickUrl;
        }

        public String getVer() {
            return ver;
        }

        public void setVer(String ver) {
            this.ver = ver;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(content);
            parcel.writeString(id);
            parcel.writeString(clickUrl);
            parcel.writeString(ver);
            parcel.writeString(title);
            parcel.writeString(remark);
        }

        public static final Creator<Advertise> CREATOR = new Creator<Advertise>() {

            @Override
            public Advertise createFromParcel(Parcel parcel) {
                Advertise advertise = new Advertise();
                advertise.content = parcel.readString();
                advertise.id = parcel.readString();
                advertise.clickUrl = parcel.readString();
                advertise.ver = parcel.readString();
                advertise.title = parcel.readString();
                advertise.remark = parcel.readString();
                return advertise;
            }

            @Override
            public Advertise[] newArray(int i) {
                return new Advertise[0];
            }
        };
    }

    public interface AdDownloadListener {
        void onSuccess(List<Advertise> advertises);

        void onFailed();
    }

}
