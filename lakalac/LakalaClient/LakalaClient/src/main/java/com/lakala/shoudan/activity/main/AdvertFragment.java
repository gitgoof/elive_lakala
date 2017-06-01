package com.lakala.shoudan.activity.main;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.AppUpgradeController;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.UILUtils;
import com.lakala.platform.db.DBManager;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.BaseFragment;
import com.lakala.shoudan.activity.bscanc.BScanCActivity;
import com.lakala.shoudan.activity.messagecenter.MessageDao;
import com.lakala.shoudan.activity.messagecenter.messageBean.PublishMessage;
import com.lakala.shoudan.activity.messagecenter.messageBean.ServiceType;
import com.lakala.shoudan.activity.shoudan.AdShareActivity;
import com.lakala.shoudan.activity.shoudan.AdViewpageAdapter;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.adapter.AdBottomAdapter;
import com.lakala.shoudan.bll.AdDownloadManager;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.AdViewPager;
import com.lakala.shoudan.component.DrawButtonClickListener2;
import com.lakala.shoudan.datadefine.AdBottomMessage;
import com.lakala.shoudan.datadefine.Message;
import com.lakala.shoudan.util.CommonUtil;
import com.lakala.shoudan.util.ScreenUtil;
import com.lidroid.xutils.db.sqlite.Selector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.lakala.platform.statistic.ShoudanStatisticManager.wechatFriend;

//import com.lakala.shoudan.adapter.AdBottomAdapter1;

/**
 * Created by LMQ on 2015/12/14.
 */
public class AdvertFragment extends BaseFragment implements AdBottomAdapter.AdBottomFunctionClickListener {

    private static final String TAG = "AdvertFragment";
    private GridView listAdv1;
    private AdViewPager viewPagerAdv2;
    private AdViewpageAdapter adAdapter;
    private List<AdDownloadManager.Advertise> mAdvertises = new ArrayList<>();
    private View layoutAdv3, layoutAdv3_2;
    private MessageDao messageDao;
    private Adv1Adapter adv1Adapter;
    private OnPageChangedListener onAdv2ChangedListener;
    private View view;
    private boolean isOneDays = false;
    private ListView listView;
    private AdBottomAdapter mAdBottomAdapter;
    private RelativeLayout rl_adv4;
    private DBManager mDbManager;
    private Selector selector;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_advert, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String loginName = ApplicationEx.getInstance().getUser().getLoginName();
        messageDao = MessageDao.getInstance(ApplicationEx.getInstance().getUser().getLoginName());
        mDbManager = DBManager.getIntance(context, loginName);
        listAdv1 = (GridView) view.findViewById(R.id.list_adv1);
        rl_adv4 = (RelativeLayout) view.findViewById(R.id.rl_adv4);
        listView = (ListView) view.findViewById(R.id.lv_ad4);
        viewPagerAdv2 = (AdViewPager) view.findViewById(R.id.viewpager_adv2);
        layoutAdv3 = view.findViewById(R.id.layout_adv3);
        layoutAdv3_2 = view.findViewById(R.id.layout_adv4);
        this.view = view;
        initData();
    }


    private void initData() {
        initAdv1();
        initAdv2();
        initAdv3();
        initAdv4();
    }

    List<AdBottomMessage> list = new ArrayList<>();

    /**
     * 底部定向业务
     */
    private void initAdv4() {
        getData();
    }

    private boolean fromLocal;
    private ArrayList<AdBottomMessage> mAdBottomList = new ArrayList<>();
    private ArrayList<AdBottomMessage> mAdBottomTotalList = new ArrayList<>();

    private void getData() {
        CommonServiceManager manager = CommonServiceManager.getInstance();
        manager.getAdBottomMessage(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONArray array = null;
                    try {
                        JSONObject jsonobject = new JSONObject(resultServices.retData);
                        array = jsonobject.getJSONArray("messages");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.toast(context, e.toString());
                    }
                    int arrayLength = 0;
                    if (array != null) {
                        if (array.length() > 0) {
                            fromLocal = false;
                            arrayLength = array.length();
                        } else {
                            fromLocal = true;
                        }
                    }
                    for (int i = 0; i < arrayLength; i++) {
                        try {
                            JSONObject json = array.getJSONObject(i);
                            AdBottomMessage message = AdBottomMessage.parseObject(json);
                            mAdBottomList.add(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideProgressDialog();
                        }
                    }
                    loadData();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context, R.string.socket_error);
                handleFailure();
            }
        });
    }

    private void loadServiceData() {
        if (mAdBottomList != null && mAdBottomList.size() > 0) {
            if (mAdBottomAdapter == null) {
                mAdBottomAdapter = new AdBottomAdapter(context, mAdBottomList);
                mAdBottomAdapter.setFunctionClickListener(this);
                listView.setAdapter(mAdBottomAdapter);
            } else {
                mAdBottomAdapter.addAll(mAdBottomList);
            }
        } else {
            handleFailure();
        }
    }

    private void loadData() {
        if (!fromLocal) {
            mDbManager.saveOrupdateAll(mAdBottomList);
            if (adAdapter == null) {
                loadCacheMessageData();
            } else {
                Collections.reverse(mAdBottomList);
            }
            mAdBottomTotalList.addAll(0, mAdBottomList);
        } else {
            if (mAdBottomAdapter == null) {
                loadCacheMessageData();
                mAdBottomTotalList.addAll(mAdBottomList);
            }
        }
        fillList();
    }

    /**
     * 填充列表
     */
    private void fillList() {
        if (mAdBottomTotalList != null && mAdBottomTotalList.size() > 0) {
            if (mAdBottomAdapter == null) {
                mAdBottomAdapter = new AdBottomAdapter(context, mAdBottomTotalList);
                mAdBottomAdapter.setFunctionClickListener(this);
                listView.setAdapter(mAdBottomAdapter);
            } else {
                mAdBottomAdapter.addAll(mAdBottomTotalList);
            }
        } else {
            handleFailure();
        }
    }

    /**
     * 加载缓存
     */
    public void loadCacheMessageData() {
        Selector selector = Selector.from(PublishMessage.class).orderBy("msgTime", true);
        mAdBottomList = (ArrayList<AdBottomMessage>) mDbManager.getAllObject(PublishMessage.class, selector);
    }


    /**
     *
     */
    private void handleFailure() {
        if (mAdBottomAdapter == null) {
            for (int i = 0; i < 3; i++) {
                AdBottomMessage message = new AdBottomMessage();
                message.setFunctionText("查看详情");
                list.add(message);
            }
            mAdBottomAdapter = new AdBottomAdapter(context, list);
            listView.setAdapter(mAdBottomAdapter);
        } else {
            mAdBottomAdapter.notifyDataSetChanged();
        }
    }

    private void addViewFooter() {
        TextView textView = new TextView(context);
        textView.setText("已显示全部内容");
        textView.setTextSize(16);
        textView.setTextColor(Color.parseColor("#999999"));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(Gravity.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParams.setMargins(8, 8, 8, 8);
        textView.setLayoutParams(layoutParams);
        listView.addFooterView(textView);
    }

    public AdvertFragment setOnAdv2ChangedListener(OnPageChangedListener onAdv2ChangedListener) {
        this.onAdv2ChangedListener = onAdv2ChangedListener;
        return this;
    }


    private void initDefaultAdv1(List<Message> adv1Data) {
        Message msg = new Message();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("titleText", "易分期");
            jsonObject1.put("titleIconImagURL", "drawable://" + R.drawable.icon_yfq);
            jsonObject.put("titleRegionData", jsonObject1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg.setContent("lklmpos://action=native?launch=yfq");
        msg.setExtInfo(jsonObject.toString());
        adv1Data.add(msg);

        if (isOneDays) {
            adv1Data.add(getOneDay());
        } else {
            adv1Data.add(getD0());
        }

        msg = new Message();
        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("titleText", "i贷");
            jsonObject1.put("titleIconImagURL", "drawable://" + R.drawable.icon_id);
            jsonObject.put("titleRegionData", jsonObject1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg.setContent("lklmpos://action=native?launch=paph");
        msg.setExtInfo(jsonObject.toString());
        adv1Data.add(msg);

        msg = new Message();
        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("titleText", "信用卡申办");
            jsonObject1.put("titleIconImagURL", "drawable://" + R.drawable.icon_xyksb);
            jsonObject.put("titleRegionData", jsonObject1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg.setContent("lklmpos://action=native?launch=xyksb");
        msg.setExtInfo(jsonObject.toString());
        adv1Data.add(msg);

        msg = new Message();
        jsonObject = new JSONObject();
        try {
            jsonObject1.put("titleText", "信用卡还款");
            jsonObject1.put("titleIconImagURL", "drawable://" + R.drawable.icon_xykhk);
            jsonObject.put("titleRegionData", jsonObject1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg.setContent("lklmpos://action=native?launch=xykhk");
        msg.setExtInfo(jsonObject.toString());
        adv1Data.add(msg);

        msg = new Message();
        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("titleText", "转账汇款");
            jsonObject1.put("titleIconImagURL", "drawable://" + R.drawable.icon_zz);
            jsonObject.put("titleRegionData", jsonObject1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg.setContent("lklmpos://action=native?launch=zzhk");
        msg.setExtInfo(jsonObject.toString());
        adv1Data.add(msg);

        msg = new Message();
        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("titleText", "余额查询");
            jsonObject1.put("titleIconImagURL", "drawable://" + R.drawable.icon_yecx);
            jsonObject.put("titleRegionData", jsonObject1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg.setContent("lklmpos://action=native?launch=yecx");
        msg.setExtInfo(jsonObject.toString());
        adv1Data.add(msg);

        msg = new Message();
        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("titleText", "申请收款宝");
            jsonObject1.put("titleIconImagURL", "drawable://" + R.drawable.icon_sqskbr);
            jsonObject.put("titleRegionData", jsonObject1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg.setContent("lklmpos://action=native?launch=zzhk");
        msg.setExtInfo(jsonObject.toString());
        adv1Data.add(msg);
    }

    public Message getOneDay() {

        Message msg = new Message();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("titleText", "一日贷");
            jsonObject1.put("titleIconImagURL", "drawable://" + R.drawable.icon_yrd);
            jsonObject.put("titleRegionData", jsonObject1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg.setContent("lklmpos://action=native?launch=yrd");
        msg.setExtInfo(jsonObject.toString());
        return msg;
    }

    public Message getD0() {
        Message msg = new Message();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("titleText", "D0提款");
            jsonObject1.put("titleIconImagURL", "drawable://" + R.drawable.icon_d0);
            jsonObject.put("titleRegionData", jsonObject1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg.setContent("lklmpos://action=native?launch=d0");
        msg.setExtInfo(jsonObject.toString());
        return msg;
    }

    /**
     * @param mostCnt 最多取mostCnt条数据
     */
    private void queryMsg(final int mostCnt) {
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    JSONArray jsonArray = null;
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        jsonArray = jsonObject.getJSONArray("messages");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int length = 0;
                    if (jsonArray != null) {
                        length = jsonArray.length();
                    }
                    length = length < mostCnt ? length : mostCnt;//需求：最多取3条数据
                    for (int i = 0; i < length; i++) {
                        try {
                            JSONObject json = jsonArray.getJSONObject(i);
                            Message msg = Message.obtain(json);
                            int idx = msg.getIdx();
                            if (idx >= 1 && idx <= 3) {
                                adv1Data.set(idx - 1, msg);
                            }
//                            int updateCnt = messageDao.updateMsgByIdx(msg);
//                            if (updateCnt == 0) {
//                                messageDao.insertMsg(msg);
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (adAdapter != null)
                        adv1Adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getMessageList(ApplicationEx.getInstance().getUser()
                .getLoginName(), Message
                .MSG_TYPE.INDEX, callback);
    }

    List<Message> adv1Data;

    public void initAdv1() {

        if (adv1Data != null) {
            return;
        }
        adv1Data = new ArrayList<>();
        initDefaultAdv1(adv1Data);
//        List<Message> data = messageDao.queryMsg(Message.MSG_TYPE.INDEX);
        isOneDay();
        adv1Adapter = new Adv1Adapter(adv1Data);
        listAdv1.setAdapter(adv1Adapter);
        listAdv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message message = adv1Adapter.getItem(position);
                if (!nativeAction(message.getContent())) {
                    httpPage(message.getContent(), message.getTitle(), message.getId());
                }
            }
        });
    }

    private View.OnClickListener adv3Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AdDownloadManager.Advertise adv = (AdDownloadManager.Advertise) v.getTag();
            String event = String.format(ShoudanStatisticManager.Advert_ID, adv.getId());
            ShoudanStatisticManager.getInstance().onEvent(event, context);
            Uri uri = Uri.parse(adv.getClickUrl());
            if (!nativeAction(uri, adv)) {
                PublicToEvent.messeage = false;
                PublicEnum.Business.setPublic(true);
                httpPage(adv.getClickUrl(), adv.getTitle(), adv.getId());
            }
        }
    };

    private void httpPage(String url, String title, String id) {
        if (TextUtils.isEmpty(url) || "null".equals(url)) {
            return;
        }
        context.startActivity(
                new Intent(
                        context, AdShareActivity.class
                ).putExtra("url", url).putExtra("title", title));
        ShoudanStatisticManager.getInstance().onEvent(String.format(ShoudanStatisticManager.Advert_ID, id), context);
        wechatFriend = String.format(ShoudanStatisticManager.wechatFriend, id);
        ShoudanStatisticManager.wechatmoment = String.format(ShoudanStatisticManager.wechatmoment, id);
        ShoudanStatisticManager.weibo = String.format(ShoudanStatisticManager.weibo, id);
    }


    private boolean nativeAction(String url) {
        return nativeAction(Uri.parse(url));
    }

    private boolean nativeAction(Uri uri) {
        return nativeAction(uri, null);
    }

    private boolean nativeAction(Uri uri, AdDownloadManager.Advertise adv) {
        if (TextUtils.equals("lklmpos", uri.getScheme())) {
            String key = uri.getQueryParameter("launch");
            if (!TextUtils.isEmpty(key)) {
                PublicToEvent.messeage = false;
                PublicEnum.Business.setDirection(true);
                ActiveNaviUtils.start((AppBaseActivity) context, key, adv);
            }
            return true;
        }
        return false;
    }

    /**
     * 初始化推荐位广告
     */
    private void initAdv3() {
        final int[] ids = {R.id.iv_adv3_1, R.id.iv_adv3_2, R.id.iv_adv3_3,
                R.id.iv_adv4_1, R.id.iv_adv4_2, R.id.iv_adv4_3};
        initDefaultAdv3(ids);
        showProgressWithNoMsg();
        AdDownloadManager.getInstance().check(new AdDownloadManager.AdDownloadListener() {
            @Override
            public void onSuccess(List<AdDownloadManager.Advertise> advertises) {
                hideProgressDialog();
                int length = advertises == null ? 0 : advertises.size();
                length = length < 3 ? length : 3;
                for (int i = 0; i < length; i++) {
                    AdDownloadManager.Advertise adv = advertises.get(i);
                    int idx = adv.getIdx();
                    if (idx >= 1 && idx <= 6) {
                        ImageView imageView = (ImageView) view.findViewById(ids[idx - 1]);
                        imageView.setTag(adv);
                        imageView.setOnClickListener(adv3Listener);
                        UILUtils.displayBackground(adv.getContent(), imageView);
                    }
                }
            }

            @Override
            public void onFailed() {
                hideProgressDialog();
            }
        }, AdDownloadManager.Type.INDEX_B, true);
    }

    private void initDefaultAdv3(int[] ids) {
        int[] imgs = {R.drawable.pic_zxfk, R.drawable.pic_zbmd, R.drawable.pic_zzsq,
                R.drawable.home_ad_zzwza, R.drawable.home_ad_qthhk, R.drawable.home_ad_desk};
        AdDownloadManager.Advertise[] advertises = new AdDownloadManager.Advertise[6];
        advertises[0] = new AdDownloadManager.Advertise();
        advertises[1] = new AdDownloadManager.Advertise();
        advertises[2] = new AdDownloadManager.Advertise();
        advertises[3] = new AdDownloadManager.Advertise();
        advertises[4] = new AdDownloadManager.Advertise();
        advertises[5] = new AdDownloadManager.Advertise();

        advertises[0].setContent("drawable://" + imgs[0]);
        advertises[0].setClickUrl("lklmpos://action=native?launch=loan_business");
        advertises[1].setContent("drawable://" + imgs[1]);
        advertises[1].setClickUrl("lklmpos://action=native?launch=d0");
        advertises[2].setContent("drawable://" + imgs[2]);
        advertises[2].setClickUrl("lklmpos://action=native?launch=xyksb");

        for (int i = 0; i < 3; i++) {
            AdDownloadManager.Advertise adv = advertises[i];
            ImageView imageView = (ImageView) view.findViewById(ids[i]);
            imageView.setTag(adv);
            imageView.setOnClickListener(adv3Listener);
            UILUtils.displayBackground(adv.getContent(), imageView);
        }
    }

    private void initAdv2() {
        adAdapter = new AdViewpageAdapter(mAdvertises);
        viewPagerAdv2.setAdapter(adAdapter);
        showProgressWithNoMsg();
        ScreenUtil.getScrrenWidthAndHeight(context);
        int hor = (int) context.getResources().getDimension(R.dimen
                .combination_text_edit_margin_left);
        int width = Parameters.screenWidth - hor * 2;
        int height = (int) (156 * width * 1.0 / 864);
        ViewGroup.LayoutParams params = viewPagerAdv2.getLayoutParams();
        params.width = width;
        params.height = height;
        viewPagerAdv2.setLayoutParams(params);
        viewPagerAdv2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (onAdv2ChangedListener != null) {
                    onAdv2ChangedListener.onPageChanged();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        AdDownloadManager.getInstance().check(
                new AdDownloadManager.AdDownloadListener() {
                    @Override
                    public void onSuccess(List<AdDownloadManager.Advertise> advertises) {
                        hideProgressDialog();
                        adAdapter.clear();
                        mAdvertises.clear();
                        mAdvertises.addAll(advertises);
                        adAdapter = new AdViewpageAdapter(mAdvertises);
                        viewPagerAdv2.setAdapter(adAdapter);
                        viewPagerAdv2.startAdvertise(AdDownloadManager.getInstance().getInterval());
                    }

                    @Override
                    public void onFailed() {
                        hideProgressDialog();
                    }
                }, AdDownloadManager.Type.INDEX, true
        );
    }


    private class Adv1Adapter extends BaseAdapter {
        List<Message> data = null;

        public Adv1Adapter(List<Message> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Message getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_item_adv1_1, null);
                holder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.title = (TextView) convertView.findViewById(R.id.tv_title);
//                holder.content = (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Message message = getItem(position);
            String extInfo = message.getExtInfo();
            try {
                JSONObject jsonObject = new JSONObject(extInfo);
                JSONObject jsonObject1 = jsonObject.getJSONObject("titleRegionData");
                String iconURL = jsonObject1.optString("titleIconImagURL", "");
                String mainTitle = jsonObject1.optString("titleText", "");
//                LogUtil.print(TAG,iconURL);
//                String subTitle = jsonObject.optString("subTitle", "");
                UILUtils.displayBackground(iconURL, holder.icon);
                holder.title.setText(mainTitle);
//                holder.content.setText(subTitle);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }

        private class ViewHolder {
            ImageView icon;
            TextView title;
            TextView content;
        }
    }

    public interface OnPageChangedListener {
        void onPageChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
//        getData();
    }

    /**
     * 首页是否一日贷
     */
    private void isOneDay() {
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
//                queryMsg(3);
                getAd1();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String isOneDay = jsonObject.optString("business_idx3");
                        if ("0".equals(isOneDay)) {
                            if (isOneDays) {
                                adv1Data.remove(1);
                                adv1Data.add(getD0());
                                adv1Adapter.notifyDataSetChanged();
                                isOneDays = false;
                            }
                        } else {
                            if (!isOneDays) {
                                adv1Data.remove(1);
                                adv1Data.add(getOneDay());
                                adv1Adapter.notifyDataSetChanged();
                                isOneDays = true;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
//                queryMsg(3);
                getAd1();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getIsOneDay(callback);
    }

    /**
     *
     */
    private void getAd1() {
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    JSONArray jsonArray = null;
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        jsonArray = jsonObject.getJSONArray("messages");
                        for (int j=0;j<20;j++){
                            System.out.println("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {

                        try {
                            JSONObject json = jsonArray.getJSONObject(i);
//                            LogUtil.print(TAG,json.toString());
                            Message msg = Message.obtain2(json);
                            int idx = msg.getIdx();
//                            LogUtil.print(TAG,""+i);
//                            LogUtil.print(TAG,""+idx);
//                            if (idx >= 1 && idx <= 6) {
                                adv1Data.set(idx - 1, msg);
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (adAdapter != null)
//                        LogUtil.print(TAG,"notifyDataSetChanged");
                        adv1Adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getAd1(callback);
    }

    /**
     * 底部功能区点击回调
     *
     * @param ids
     * @param message
     */
    @Override
    public void OnAdFuntionClick(int ids, AdBottomMessage message) {
        switch (ids) {
            case R.id.tv_function_left://查看详情
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Publish_detailsShare, context);
                forwardAdShareActivity(message.getDetailsClickURL(), message.getContentImageTitle());
                break;
            case R.id.tv_function_right://立即体验
                handleServiceAction(message);
                break;
            case R.id.iv_content_publish://点击图片
                forwardAdShareActivity(message.getDetailsClickURL(), message.getContentImageTitle());
                break;
            case R.id.tv_function_middle://版本升级
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Publish_versionUpdate, context);
                checkAppUpdate();
                break;
        }
    }

    /**
     * 功能区单个按钮居中为立即体验
     *
     * @param ids
     * @param message
     * @param url
     */
    @Override
    public void OnAdFuntionClick1(int ids, AdBottomMessage message, String url) {
        switch (ids) {
            case R.id.tv_function_middle:
                handleServiceAction(message);
                break;
        }
    }

    /**
     * 功能区单个按钮居中为查看详情
     *
     * @param ids
     * @param message
     * @param url
     */
    @Override
    public void OnAdFuntionClick2(int ids, AdBottomMessage message, String url) {
        switch (ids) {
            case R.id.tv_function_middle:
                forwardAdShareActivity(url, message.getContentImageTitle());
                break;
        }
    }

    /**
     * 版本升级在右边的回调
     *
     * @param ids
     * @param
     */
    @Override
    public void OnAdFuntionClick3(int ids) {
        switch (ids) {
            case R.id.tv_function_right:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Publish_versionUpdate, context);
                checkAppUpdate();
                break;
        }
    }

    private void handleServiceAction(AdBottomMessage message) {
        int serVersionCode = 0;
        if (!TextUtils.isEmpty(message.getVersion()))
            serVersionCode = Integer.parseInt(message.getVersion());
        int curVersionCode = Integer.parseInt(Util.getVersionCode());
        if (serVersionCode > curVersionCode) {
            checkAppUpdate();
        } else {
            skipToBusinessPage(message);
        }
    }

    /**
     * 系统公告进入各业务
     *
     * @param message
     */
    private void skipToBusinessPage(AdBottomMessage message) {
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Publish_business, context);
        String serviceActionURL = message.getServiceActionURL();
        try {
            switch (ServiceType.valueOf(serviceActionURL)) {
                case businessTypeImmediateWithdrawal://立即提款
                    new DrawButtonClickListener2((AppBaseActivity) context).onClick(null);
                    break;
                case businessTypeCreditCardCollection://刷卡收款
                    if (CommonUtil.isMerchantValid(context)) {
                        BusinessLauncher.getInstance().start("collection_transaction");//收款交易
                    }
                    break;
                case businessTypeSweepCodeCollection://扫码收款
                    if (CommonUtil.isMerchantValid(context)) {
                        getMerQRCode();
                    }
                    break;
                case businessTypeConductFinancialTransactions://理财
                    FinanceRequestManager financeRequestManager = FinanceRequestManager.getInstance();
//                financeRequestManager.setStatistic(ShoudanStatisticManager.Finance_HomePage);
                    financeRequestManager.startFinance((AppBaseActivity) context);
                    break;
                case businessTypeLoan://贷款
                    ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.LOAN_BUSINESS);
                    break;
                case businessTypeAPieceOfLndiana://一块夺宝
                    ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.TREASURE);
                    break;
                case businessTypeCreditCardPayment://信用卡还款
                    BusinessLauncher.getInstance().start("creditcard_payment");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void forwardAdShareActivity(String url, String title) {
        startActivity(new Intent(context, AdShareActivity.class).putExtra("url", url).putExtra("title", title));
    }

    String merQrCode;

    /**
     * 微信开户开通入口
     */
    private void getMerQRCode() {
        showProgressWithNoMsg();
        ShoudanService.getInstance().getMerQRCode(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        merQrCode = jsonObject.optString("merQrcode");
                        hideProgressDialog();
                        BScanCActivity.start(context, "COMPLETED", merQrCode, null);
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                } else {
                    hideProgressDialog();
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);
            }
        });

    }

    private AppUpgradeController appUpgradeController = AppUpgradeController.getInstance();

    /**
     * 检查更新
     */
    private void checkAppUpdate() {
        appUpgradeController.setCtx(context);
        appUpgradeController.check(true, true);
    }

}
