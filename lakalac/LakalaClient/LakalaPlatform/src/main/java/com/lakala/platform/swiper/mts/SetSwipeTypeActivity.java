package com.lakala.platform.swiper.mts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.IHttpRequestEvents;
import com.lakala.library.exception.BaseException;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.activity.BaseActionBarActivity;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.statistic.StatisticManager;
import com.lakala.platform.swiper.mts.protocal.ProtocalActivity;
import com.lakala.ui.common.GifMovieView;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.dialog.mts.AlertDialog;
import com.lakala.ui.module.CustomNestListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wangchao on 14-2-6.
 * 刷卡器类型选择
 */
public class SetSwipeTypeActivity extends BaseActionBarActivity implements AdapterView.OnItemClickListener, GetSwipeKsnTask.GetKsnListener {

    //如果从信用卡还款业务进入到改页面，那么设置
    final static public int CREDIT_RESULT_CODE = 50;

    final static public String SWIPE_PULL_UP   = "swipe_pull_up";
    final static public String SWIPE_PULL_DOWN = "swipe_pull_down";

    private SwipeStateReceive receiver;

    /**
     * 保存
     */
    private Button id_common_guide_button;
    /**
     * 其他设备
     */
    private RelativeLayout other_type_rel;
    /**
     * 其他设备,箭头
     */
    private ImageView other_type_select_img;

    private CustomNestListView default_type_list;
    private CustomNestListView other_type_list;

    private List<SwipeItem> defaultSwipeList;
    private List<SwipeItem> otherSwipeList;

    private SwipeTypeAdapter defaultAdapter;
    private SwipeTypeAdapter otherAdapter;
    /**
     * 选择类型
     */
    private String selectType;
    /**
     * 旋转动画
     */
    private Animation animation;
    /**
     * 其它刷卡器类型是否显示
     */
    private boolean isShow;
    /**
     * gif
     */
    private GifMovieView gifMovieView;
    /**
     * 刷卡器类型
     */
    private LinearLayout swipe_types_ll;
    /**
     * 提示文字
     */
    private TextView hint_txt;

    private FragmentActivity _this;
    /**
     * 获取list显示数据
     */
    private void getData() {
        defaultSwipeList = SwipeItem.getDefaultList();
        otherSwipeList = SwipeItem.getOtherList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plat_activity_set_swipe_type);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SWIPE_PULL_UP);
        filter.addAction(SWIPE_PULL_DOWN);
        this.registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        try {
            this.unregisterReceiver(receiver);
        }catch (Exception e){
        }
        super.onStop();
    }

    private void init() {
        this._this = this;
        queryUnitInfoList();
        navigationBar.setTitle("选择型号");
        id_common_guide_button = (Button) findViewById(R.id.id_common_guide_button);
        id_common_guide_button.setText("保存");
        other_type_rel = (RelativeLayout) findViewById(R.id.other_type_rel);
        default_type_list = (CustomNestListView) findViewById(R.id.default_type_list);
        other_type_list = (CustomNestListView) findViewById(R.id.other_type_list);
        other_type_select_img = (ImageView) findViewById(R.id.other_type_select_img);
        swipe_types_ll = (LinearLayout) findViewById(R.id.swipe_types_ll);
        hint_txt = (TextView) findViewById(R.id.hint_txt);
        gifMovieView = (GifMovieView) findViewById(R.id.gif_movie_view);
        gifMovieView.setMovieResource(R.drawable.flash_insert_card_reader);

        id_common_guide_button.setOnClickListener(this);
        other_type_rel.setOnClickListener(this);

        receiver = new SwipeStateReceive();
    }

    private void setAdapter(){
        DialogController.getInstance().dismiss();
        defaultAdapter = new SwipeTypeAdapter(this, defaultSwipeList);
        otherAdapter = new SwipeTypeAdapter(this, otherSwipeList);
        default_type_list.setAdapter(defaultAdapter);
        other_type_list.setAdapter(otherAdapter);
        default_type_list.setOnItemClickListener(this);
        other_type_list.setOnItemClickListener(this);
    }

    @Override
    protected void onViewClick(View view) {
        super.onViewClick(view);
        if (view.equals(id_common_guide_button)) {
            if (!checkSelect()) return;
            SwipeLauncher.getInstance().setSwipeType(selectType);
            SwipeLauncher.getInstance().getKsn(this);
        } else if (view.equals(other_type_rel)) {
            toggleOtherType();
        }

    }

    /**
     * 显示隐藏其他刷卡器类型
     */
    private void toggleOtherType() {
        isShow = other_type_list.getVisibility() == View.VISIBLE;

        Animation alpha = new AlphaAnimation(isShow ? 0 : 0.2f, isShow ? 0 : 1);
        alpha.setDuration(400);
        other_type_list.startAnimation(alpha);
        other_type_list.setVisibility(isShow ? View.GONE : View.VISIBLE);

        animation = new RotateAnimation(isShow ? 180 : 0, isShow ? 0 : 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setDuration(400);
        other_type_select_img.startAnimation(animation);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.equals(default_type_list)) {
            selectType = defaultSwipeList.get(position).getType();
            otherAdapter.clearSelect();
            defaultAdapter.setSelect(position);
        } else if (parent.equals(other_type_list)) {
            selectType = otherSwipeList.get(position).getType();
            defaultAdapter.clearSelect();
            otherAdapter.setSelect(position);
        }
    }

    @Override
    public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
        if (navBarItem == NavigationBar.NavigationBarItem.back) {
            SwipeLauncher.getInstance().setCurrentConnectInvalid(false);
            SwipeLauncher.getInstance().cancel(SwipeLauncher.CancelMode.SET_SWIPE);
            finish();
        }
        super.onNavItemClick(navBarItem);
    }

    @Override
    public void onBackPressed() {
        SwipeLauncher.getInstance().setCurrentConnectInvalid(false);
        SwipeLauncher.getInstance().cancel(SwipeLauncher.CancelMode.SET_SWIPE);
        super.onBackPressed();
    }

    /**
     * 验证是否选择刷卡器类型
     */
    private boolean checkSelect() {
        if (StringUtil.isEmpty(selectType)) {
            ToastUtil.toast(this, "请选择刷卡器类型！");
            return false;
        }
        return true;
    }

    /**
     * GetKsnListener
     */
    @Override
    public void GetKsnSuccess(String ksn) {

        SwipeLauncher.getInstance().statisticGetKSNStatus(StatisticManager.getKsnSuccess, this);

        SwipeLauncher.getInstance().setCurrentConnectInvalid(false);
        SwipeLauncher.getInstance().setKsn(ksn);
        SwipeLauncher.getInstance().launch(SwipeLauncher.getInstance().getBusiness());
        finish();
    }

    @Override
    public void GetKsnFailure() {
        showGetKSNFailuerDialog();

        SwipeLauncher.getInstance().statisticGetKSNStatus(StatisticManager.getKSNFailure, this);
        SwipeLauncher.getInstance().statisticGetKSNStatus(StatisticManager.getKSNFailureSelect, this);
    }

    @Override
    public FragmentActivity ksnActivity() {
        return this;
    }


    private boolean isCredit(){
        final String businessKey = SwipeLauncher.getInstance().getCurrentBusinessKey();
        return businessKey.equals("creditguide");
    }

    /**
     * 判断当前刷卡业务
     */
    private void showGetKSNFailuerDialog(){
        DialogController.getInstance().showAlertDialog(this,
                getResources().getString(R.string.plat_select_swipe_getksn_title),
                getResources().getString(R.string.plat_select_swipe2),
                getString(R.string.plat_swipe_error_credit_left_button),
                getString(R.string.plat_set_swipe_close),
                new AlertDialog.Builder.AlertDialogClickListener() {
                    @Override
                    public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                        alertDialog.dismiss();
                        switch (typeEnum) {
                            case LEFT_BUTTON:
                                goHowToSwipePage();
                                break;
                        }
                    }
                });
    }

//    /**
//     * 根据业务不同显示不同的对话框
//     *
//     */
//    private void showCustomBusinessDialog(){
//        final JSONObject business = SwipeLauncher.getInstance().getBusiness();
//        final boolean rightEnable = business != null && business.optBoolean("wuka", false);
//        final View view = View.inflate(this, R.layout.view_custom_business_dialog, null);
//        final TextView title   = (TextView) view.findViewById(R.id.title);
//        final TextView close    = (TextView) view.findViewById(R.id.close);
//        final TextView content = (TextView) view.findViewById(R.id.content);
//        title.setText(getString(R.string.plat_select_swipe_getksn_title));
//        content.setText(getString(R.string.plat_swipe_error_credit_content));
//
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogController.getInstance().dismiss();
//            }
//        });
//
//        DialogController.getInstance().setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, rightEnable);
//
//        final Activity me = this;
//        DialogController.getInstance().showAlertDialog(
//                this,
//                0,
//                "",
//                view,
//                getString(R.string.plat_swipe_error_credit_left_button),
//                getString(R.string.plat_swipe_error_credit_right_button),
//                "", new AlertDialog.Builder.AlertDialogClickListener() {
//                    @Override
//                    public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
//                        alertDialog.dismiss();
//                        switch (typeEnum) {
//                            case LEFT_BUTTON:
//                                goHowToSwipePage();
//                                break;
//                            case RIGHT_BUTTON:
//                                me.setResult(CREDIT_RESULT_CODE);
//                                me.finish();
//                                break;
//                        }
//                    }
//                }, false);
//    }

    /**
     * 跳转到刷卡失败如何解决的页面
     */
    private void goHowToSwipePage(){

        String html = "";
        String model = Build.MODEL;
        if(model.contains("MI 2")){
            html = "swiper_help/xiaomi2/index.html";
        }else if(model.contains("MI 3")){
            html = "swiper_help/xiaomi3/index.html";
        }else if(model.contains("MI 4")){
            html = "swiper_help/xiaomi4/index.html";
        }else if(model.contains("HUAWEI") || model.contains("PE") || model.contains("HONOR")
               || model.contains("G62") || model.contains("H60") || model.contains("H30")
               || model.contains("C88") || model.contains("G730") || model.contains("CHE")
               ){
            html = "swiper_help/huawei/index.html";
        }else if(model.contains("SM")){
            html = "swiper_help/samsung_note/index.html";
        }else{
            html = "swiper_help/general/index.html";
        }
        try{

            Intent intent =new Intent(_this, ProtocalActivity.class);
            Bundle bundle =new Bundle();
            JSONObject data =new JSONObject();
            data.put(ProtocalActivity.PROTOCAL_TITLE,"");
            data.put(ProtocalActivity.PROTOCAL_URL,html);
            bundle.putString(ProtocalActivity.DATA, data.toString());
            intent.putExtra(ProtocalActivity.BUSINESS_BUNDLE_KEY,bundle);
            _this.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Adapter
     */
    private class SwipeTypeAdapter extends BaseAdapter {

        private List<SwipeItem> list;
        private Context context;

        public SwipeTypeAdapter(Context context, List<SwipeItem> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 设置选中
         */
        public void setSelect(int position) {
            for (int i = 0; i < list.size(); i++) {
                if (position == i) list.get(i).setSelect(true);
                else list.get(i).setSelect(false);
            }
            notifyDataSetChanged();
        }

        /**
         * 清空选中
         */
        public void clearSelect() {
            for (SwipeItem item : list) {
                item.setSelect(false);
            }
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.plat_adapter_swipe_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            SwipeItem item = list.get(position);
            holder.swipe_name_txt.setText(item.getName());
            holder.swipe_select_img.setBackgroundResource(item.isSelect() ? R.drawable.ui_check_on : R.drawable.ui_check_off);
            return convertView;
        }

        private class ViewHolder {
            TextView swipe_name_txt;
            ImageView swipe_select_img;

            public ViewHolder(View parent) {
                swipe_name_txt = (TextView) parent.findViewById(R.id.swipe_name_txt);
                swipe_select_img = (ImageView) parent.findViewById(R.id.swipe_select_img);
            }
        }
    }

    public class SwipeStateReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(SWIPE_PULL_UP)) {
                setLayout(true);
            }
            if (action.equals(SWIPE_PULL_DOWN)){
                setLayout(false);
            }
        }

        private void setLayout(boolean isPullUp){
            swipe_types_ll.setVisibility(isPullUp ? View.GONE : View.VISIBLE);
            gifMovieView.setVisibility(isPullUp ? View.VISIBLE : View.GONE);
            id_common_guide_button.setEnabled(!isPullUp);
            hint_txt.setText(isPullUp ? R.string.plat_select_swipe0 : R.string.plat_select_swipe);
        }
    }

    /**
     * 比较合并服务端返回的刷卡器类型
     *
     * @param response
     */
    private void compareObject(JSONObject response) {
        List<SwipeItem> resultList = new ArrayList<SwipeItem>();
        defaultSwipeList = new ArrayList<SwipeItem>();
        otherSwipeList = new ArrayList<SwipeItem>();
        JSONArray array = response.optJSONArray("List");
        if(array == null)return;
        for (int i = 0; i < array.length(); i++) {
            resultList.add(new SwipeItem(array.optJSONObject(i).toString()));
        }
        List<SwipeItem> _defaultList = SwipeItem.getDefaultList();
        List<SwipeItem> _otherList = SwipeItem.getOtherList();
        for (SwipeItem swipeItem : _defaultList) {
            for (int i = 0; i < resultList.size(); i++) {
                if (swipeItem.getType().equals(resultList.get(i).getType())) {
                    defaultSwipeList.add(swipeItem);
                }
            }
        }
        for (SwipeItem swipeItem : _otherList) {
            for (int i = 0; i < resultList.size(); i++) {
                if (swipeItem.getType().equals(resultList.get(i).getType())) {
                    otherSwipeList.add(swipeItem);
                }
            }
        }

        //如果列表为空则设置默认
        if(defaultSwipeList.size() == 0) defaultSwipeList = SwipeItem.getDefaultList();
        if(otherSwipeList.size() == 0) otherSwipeList = SwipeItem.getOtherList();

        //当前设备非laphone手机时，从刷卡器列表中移除Laphone一项
        if(!SwipeLauncher.getInstance().isLaPhone()){
            Iterator<SwipeItem> iterator = otherSwipeList.iterator();
            while (iterator.hasNext()){
                SwipeItem swipeItem = iterator.next();
                if(swipeItem.getName().equals("lklphone")){
                    iterator.remove();
                    break;
                }
            }
        }
    }

    /**
     * 刷卡器列表查询
     */
    private void queryUnitInfoList() {
        BusinessRequest request = SwipeRequest.queryUnitInfoList(this);
        request.setIHttpRequestEvents(new IHttpRequestEvents(){
            @Override
            public void onSuccess(HttpRequest request) {
                super.onSuccess(request);
                JSONObject response = (JSONObject) request.getResponseHandler().getResultData();
                compareObject(response);
                setAdapter();
            }

            @Override
            public void onFailure(HttpRequest request, BaseException exception) {
                super.onFailure(request, exception);
                DialogController.getInstance().showAlertDialog(_this,
                        getResources().getString(R.string.plat_prompt),
                        getResources().getString(R.string.plat_select_swipe_querylist_hint),
                        getResources().getString(R.string.plat_swipe_cancel),
                        getResources().getString(R.string.plat_swipe_retry), new AlertDialog.Builder.AlertDialogClickListener() {
                            @Override
                            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                                switch (typeEnum) {
                                    case LEFT_BUTTON:
                                        SwipeLauncher.getInstance().cancel(SwipeLauncher.CancelMode.SET_SWIPE);
                                        finish();
                                        break;
                                    case RIGHT_BUTTON:
                                        queryUnitInfoList();
                                        break;
                                }
                            }
                        }
                );
            }
        });
        request.execute();
    }
}
