package com.lakala.elive.user.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ScrollView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.FunctionMenuInfo;
import com.lakala.elive.beans.NoticeReq;
import com.lakala.elive.beans.NoticeResp;
import com.lakala.elive.beans.PageModel;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.PageReqInfo;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.user.adapter.MenuGridViewAdapter;
import com.lakala.elive.user.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 主菜单页面
 *
 * @author hongzhiliang
 */
public class HomeFragment extends BaseFragment {

    private MenuGridViewAdapter menuGridAdapter;
    private GridView homeMenuGrid;
    private List<FunctionMenuInfo> menuInfoList = new ArrayList<FunctionMenuInfo>();

    private ScrollView mScrollView;

    @Override
    protected void setViewLayoutId() {
        viewLayoutId = R.layout.fragment_user_home;
    }

    @Override
    protected void bindViewIds() {
        initMenuGrid();
        mScrollView = (ScrollView) mView.findViewById(R.id.lv_menu_empty);
    }

    Session mSession;

    @Override
    protected void initData() {
        mSession = Session.get(getActivity());
        UserReqInfo reqInfo = new UserReqInfo();
        reqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        NetAPI.getFunctionMenu(getActivity(), this, reqInfo);
    }

    /**
     *  初始化功能菜单
     */
    private void initMenuGrid() {
        homeMenuGrid = (GridView) mView.findViewById(R.id.gv_main_menu);
        menuGridAdapter = new MenuGridViewAdapter(getActivity(), menuInfoList);
        homeMenuGrid.setAdapter(menuGridAdapter);
        homeMenuGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int postion, long id) {
                        String userId = Session.get(getActivity()).getUserLoginInfo().getUserId();
                        if(TextUtils.isEmpty(userId)){
                            Utils.showToast(getActivity(), "请重新登录!");
                            return;
                        }
                        FunctionMenuInfo menuInfo = menuInfoList.get(postion);//菜单信息
                        Class<?> mIntentClazz = Constants.menuListMap.get(menuInfo.getMenuId());
                        if (menuInfo.getMenuId().equals("11") && !TextUtils.isEmpty(mSession.getUserLoginInfo().getUserSource()) && mSession.getUserLoginInfo().getUserSource().equals("1")) {
                            Utils.showToast(getActivity(), "非BMCP账号登录该功能不能使用！");
                        } else {
                            if (mIntentClazz != null) {
                                    Intent intent = new Intent(getActivity(), mIntentClazz);
                                    startActivity(intent);
                            } else {
                                Utils.showToast(getActivity(), "该功能未开发!");
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        initCornerMark();
    }

    private void initCornerMark(){
//        PageReqInfo pageReqInfo = new PageReqInfo();
//        pageReqInfo.setTaskStatus("2");
//        pageReqInfo.setSearchCode(""); //不是查询模式综合查询条件消除
//        if(mSession==null || mSession.getUserLoginInfo() == null)return;
//        pageReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
//        pageReqInfo.setPageNo(1);
//        pageReqInfo.setPageSize(1);
//        NetAPI.queryTaskDealList(getActivity(),this,pageReqInfo);
        NoticeReq noticeReq = new NoticeReq();
        noticeReq.setOrganId(mSession.getUserLoginInfo().getOrganId());
        noticeReq.setUserId(mSession.getUserLoginInfo().getUserId());
        NetAPI.getNotify(getActivity(), this, noticeReq);
    }

    @Override
    public void onSuccess(int method, Object obj) {
        if(this.isDetached()||this.getActivity()==null || this.getActivity().isFinishing()){
            return;
        }
        switch (method) {
            case NetAPI.ACTION_GET_FUNCTON_MENU:
                menuInfoList = (List<FunctionMenuInfo>) obj;
                if (menuInfoList.size() <= 0) {
                    homeMenuGrid.setVisibility(View.GONE);
                    mScrollView.setVisibility(View.VISIBLE);
                } else {
                    homeMenuGrid.setVisibility(View.VISIBLE);
                    mScrollView.setVisibility(View.GONE);
                    menuGridAdapter.setFunctionInfoList(menuInfoList);
                    if(mPageModel  != null){
                        NoticeResp.ContentBean content = mPageModel.getContent();
                        if(menuInfoList!=null){
                            for(FunctionMenuInfo menuInfo : menuInfoList){
                                String id = menuInfo.getMenuId();
                                if(!TextUtils.isEmpty(id)&&id.equals("09")){//工单处理
                                    if(content.getTaskCnt()>0){
                                        menuInfo.setCornerMarkNum(content.getTaskCnt());
                                        menuInfo.setShowCornerMark(true);
                                    }else{
                                        menuInfo.setShowCornerMark(false);
                                    }
                                } else {
                                    menuInfo.setShowCornerMark(false);
                                }
                            }
                        }
                    }
                    menuGridAdapter.notifyDataSetChanged();
                }
                break;
            case NetAPI.ACTION_NOTICE:
                mPageModel = (NoticeResp) obj;
                NoticeResp.ContentBean content = mPageModel.getContent();
                if(menuInfoList==null) break;
                for(FunctionMenuInfo menuInfo : menuInfoList){
                    String id = menuInfo.getMenuId();
                    if(!TextUtils.isEmpty(id)&&id.equals("09")){//工单处理
                        if(content.getTaskCnt()>0){
                            menuInfo.setCornerMarkNum(content.getTaskCnt());
                            menuInfo.setShowCornerMark(true);
                        }else{
                            menuInfo.setShowCornerMark(false);
                        }
                    } else {
                        menuInfo.setShowCornerMark(false);
                    }
                }
                menuGridAdapter.notifyDataSetChanged();
                break;
//            case NetAPI.ACTION_TASK_DEAL_LIST:
//                mPageModel = (PageModel<TaskInfo>) obj;
//                final int count = mPageModel.getRecordCount();
//                if(menuInfoList==null)break;
//                for(FunctionMenuInfo menuInfo:menuInfoList){
//                    String name = menuInfo.getMenuName();
//                    if(!TextUtils.isEmpty(name)&&name.equals("工单处理")){
//                        menuInfo.setCornerMarkNum(count);
//                        menuInfo.setShowCornerMark(true);
//                    } else {
//                        menuInfo.setShowCornerMark(false);
//                    }
//                }
//                menuGridAdapter.notifyDataSetChanged();
//                break;
        }
    }

    private NoticeResp mPageModel;

    @Override
    public void onError(int method, String statusCode) {
        if(this.isDetached()||this.getActivity()==null || this.getActivity().isFinishing()){
            return;
        }
        switch (method) {
            case NetAPI.ACTION_GET_FUNCTON_MENU:
                Utils.showToast(getActivity(), "菜单获取失败:" + statusCode + "!");
                break;
            case NetAPI.ACTION_NOTICE:
                Utils.showToast(getActivity(), "消息获取失败:" + statusCode + "!");
                break;
        }
    }
}
