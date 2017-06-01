package com.lakala.elive.task.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.TaskListBaseReq;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.map.activity.MapStoreShowAct;
import com.lakala.elive.map.activity.PathProjectActivity;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.lakala.elive.task.fragment.TaskListFragment;
import com.lakala.elive.task.fragment.TaskMapFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Des: 工单列表显示
 * Created by zhouzx on 2017/3/9.
 */
public class TaskListActivity extends BaseActivity {

    private final int MAX_MAP_LENGTH = 20;
    private TextView back;
    private ImageView titleLeftImage;
    private ImageView titleRightImage;
    /**
     * 标题
     */
    private TextView mTvTitleName;

    private TextView addTask;
    private TextView routePlanning;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_task_list);
    }

    private RelativeLayout mRelativeContent;
    @Override
    protected void bindView() {
        EventBus.getDefault().register(this);

        mTvTitleName = (TextView) findViewById(R.id.title_location);
        mTvTitleName.setText("任务列表");

        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(this);

        titleLeftImage = (ImageView) findViewById(R.id.left_image);
        titleLeftImage.setOnClickListener(this);
        titleRightImage = (ImageView) findViewById(R.id.right_image);
        titleRightImage.setOnClickListener(this);

        addTask = (TextView) findViewById(R.id.add_task);
        addTask.setOnClickListener(this);
        routePlanning = (TextView) findViewById(R.id.route_planning);
        routePlanning.setOnClickListener(this);

        mRelativeContent = (RelativeLayout) findViewById(R.id.relative_act_task_content);
        changeFragment(mShowIndex);
    }
    private TaskListFragment mTaskListFragment;
    private TaskMapFragment mTaskMapFragment;

    private void changeFragment(int index){
        if(index == 0){
            if(mTaskListFragment == null){
                mTaskListFragment = new TaskListFragment();
            }
            showFragment(mTaskListFragment,mTaskMapFragment);
            if(mTaskMapFragment!=null)
            mTaskListFragment.setBeginIndex(mTaskMapFragment.getmBeginIndex());
        } else {
            if(mTaskMapFragment == null){
                mTaskMapFragment = new TaskMapFragment();
            }
            showFragment(mTaskMapFragment,mTaskListFragment);
            // TODO 数据传入到地图中
            mTaskMapFragment.setShowDatas(mTaskListFragment.getPointsList().getContent());

        }
    }
    public void notifiyDataChanged(int index){
        if(index == 0){
            if(mTaskListFragment != null){
                mTaskListFragment.notifyDataChanged();
            }
        } else {
            if(mTaskMapFragment != null){
                mTaskMapFragment.notifyDataChanged();
            }
        }
    }
    private void showFragment(Fragment show, Fragment hide){
        if(show.isAdded()){
            if(!show.isHidden()){
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.show(show).commit();
                return;
            } else {
                if(hide!=null && hide.isAdded() && !hide.isHidden()){
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.hide(hide).show(show).commit();
                } else {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.show(show).commit();
                }
            }
        } else {
            if(hide!=null && hide.isAdded() && !hide.isHidden()){
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                getFragmentManager().beginTransaction();
                transaction.hide(hide).add(R.id.relative_act_task_content,show,"TaskList").commit();
            } else {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.relative_act_task_content,show,"TaskList").commit();
            }
        }
    }
    @Override
    protected void bindEvent() {
    }
    @Override
    protected void bindData() {
    }
    @Override
    public void onClick(View v) {
        if(checkPop())return;
        switch (v.getId()) {
            case R.id.back:
                TaskListActivity.this.finish();
                break;
            case R.id.add_task:
                // TODO 添加任务。需求:添加多项选择.实现：弹出pop让选择。
                popWindowForCall();
//                startActivityForResult(new Intent(TaskListActivity.this, SelectTasksActivity.class),ACT_REQUEST_TASK);
                break;
            case R.id.route_planning:
                // TODO 路线规划,需要传递任务列表
                if(mTaskListFragment==null)break;
//                Toast.makeText(TaskListActivity.this, "路径规划", Toast.LENGTH_SHORT).show();
                if(mTaskListFragment.getPointsList().getContent().size() == 0){
                    Toast.makeText(TaskListActivity.this, "请添加任务!", Toast.LENGTH_SHORT).show();
                    break;
                }
                /*
                TaskListReqResp resp = mTaskListFragment.getPointsList();
                List<TaskListReqResp.ContentBean> list =  resp.getContent();
                if(list.size()>MAX_MAP_LENGTH){
                    List<TaskListReqResp.ContentBean> listP = new ArrayList<TaskListReqResp.ContentBean>();
                    int icount = 0;
                    for(int i = 0;i<list.size();i++){
                        TaskListReqResp.ContentBean contentBean = list.get(i);
                        int status = contentBean.getStatus();
                        if(status != 0&&status!=2&&status!=3)continue;
                        listP.add(list.get(i));
                        icount++;
                        if(icount >= MAX_MAP_LENGTH){
                            break;
                        }
                    }
                    resp.setContent(listP);
                }
                */
                Intent intent = new Intent(TaskListActivity.this, PathProjectActivity.class);
//                intent.putExtra("point_list",resp);
                startActivity(intent);
                break;
            case R.id.left_image:
                // TODO 地图
                if(mShowIndex == 0){
                    mShowIndex = 1;
                    titleLeftImage.setImageResource(R.drawable.task_list_img);
                } else {
                    mShowIndex = 0;
                    titleLeftImage.setImageResource(R.drawable.task_map_icon);
                }
                changeFragment(mShowIndex);
                break;
            case R.id.right_image:
                // TODO 任务历史记录
//                Toast.makeText(TaskListActivity.this, "任务历史记录", Toast.LENGTH_SHORT).show();
                Intent historyList = new Intent(TaskListActivity.this, TaskHistoryListAct.class);
                startActivity(historyList);
                break;
        }
    }
    private int mShowIndex = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ACT_REQUEST_TASK:
            case ACT_REQUEST_STORE:
                if(resultCode == 100){
                    // 更新列表
                    mTaskListFragment.bindData();
                }
                break;
        }
    }
    /**
     * 列表界面更新返回标识
     */
    private final int ACT_REQUEST_TASK = 0x1000;
    /**
     *  网点界面更新返回的标识
     */
    private final int ACT_REQUEST_STORE = 0x1001;

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.loadWfTaskList:
                Utils.showToast(TaskListActivity.this, "载入失败：" + statusCode + "!");
                break;
            default:
                break;
        }
    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.loadWfTaskList:
                // TODO 载入工单。如果成功，就得重新请求
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if(mTaskListFragment != null){
            fragmentTransaction.detach(mTaskListFragment);
            mTaskListFragment = null;
        }
        if(mTaskMapFragment != null){
            fragmentTransaction.detach(mTaskMapFragment);
            mTaskMapFragment.destoryLoca();
            mTaskMapFragment = null;
        }
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void refresh(String instructions){
        if(TextUtils.equals(instructions,"refresh")){
            bindData();
        }
    }
    private long mCurrentTime = 0l;
    private boolean checkPop(){
        long now = System.currentTimeMillis();
        if(now-mCurrentTime<200){
            return true;
        }
        mCurrentTime = now;
        return false;
    }
    private PopupWindow mPopupWindow;
    private void popWindowForCall(){
        if(mPopupWindow == null){
            mPopupWindow = new PopupWindow(TaskListActivity.this);
            mPopupWindow.setContentView(LayoutInflater.from(TaskListActivity.this).inflate(R.layout.pop_task_list_more_bottom_layout,null));
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    checkPop();
                }
            });
        }
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_pop_task_call_bottom_five://搜索所辖网点加入
                    case R.id.tv_pop_task_call_bottom_one:
                        startActivityForResult(new Intent(TaskListActivity.this, SelectStoresActivity.class),ACT_REQUEST_STORE);
                        break;
                    case R.id.tv_pop_task_call_bottom_two:
                        startActivityForResult(new Intent(TaskListActivity.this, SelectTasksActivity.class),ACT_REQUEST_TASK);
                        break;
                    case R.id.tv_pop_task_call_bottom_three:
//                        Toast.makeText(TaskListActivity.this,"还未实现该功能！",Toast.LENGTH_SHORT).show();
                        loadWfTaskList();
                        break;
                    case R.id.tv_pop_task_call_bottom_four:
                        startActivity(new Intent(TaskListActivity.this, MapStoreShowAct.class).putExtra("entrance", 0));
                        break;
                    case R.id.tv_pop_task_call_bottom_six://从历史任务载入未完成
                        Intent historyList = new Intent(TaskListActivity.this, TaskHistoryListAct.class);
                        startActivity(historyList);
                        break;
                }
                if(mPopupWindow.isShowing()){
                    mPopupWindow.dismiss();
                }
            }
        };
        mPopupWindow.getContentView().findViewById(R.id.tv_pop_task_call_bottom_one).setOnClickListener(listener);
        mPopupWindow.getContentView().findViewById(R.id.tv_pop_task_call_bottom_two).setOnClickListener(listener);
        mPopupWindow.getContentView().findViewById(R.id.tv_pop_task_call_bottom_three).setOnClickListener(listener);
        mPopupWindow.getContentView().findViewById(R.id.tv_pop_task_call_bottom_four).setOnClickListener(listener);
        mPopupWindow.getContentView().findViewById(R.id.tv_pop_task_call_bottom_five).setOnClickListener(listener);
        mPopupWindow.getContentView().findViewById(R.id.tv_pop_task_call_bottom_six).setOnClickListener(listener);

        View cancelCall = mPopupWindow.getContentView().findViewById(R.id.btn_pop_task_call_bottom_cancel);
        if(cancelCall != null){
            cancelCall.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(mPopupWindow.isShowing()){
                        mPopupWindow.dismiss();
                    }
                }
            });
        }

        int[] location = new int[2];
        addTask.getLocationOnScreen(location);
        mPopupWindow.showAtLocation(addTask, Gravity.NO_GRAVITY,
                location[0],location[1]-dpToXp(5)-mPopupWindow.getContentView().getMeasuredHeight());
    }
    private int dpToXp(int dp){
        float density = getResources().getDisplayMetrics().density;
        return (int)(density*dp);
    }

    private void loadWfTaskList(){
        showProgressDialog("载入中...");
        TaskListBaseReq req = new TaskListBaseReq();
        req.setAuthToken(Session.get(TaskListActivity.this).getUserLoginInfo().getAuthToken());
        req.setUserId(Session.get(TaskListActivity.this).getUserLoginInfo().getUserId());
        NetAPI.loadWfTaskList(TaskListActivity.this,this,req);
    }
}
