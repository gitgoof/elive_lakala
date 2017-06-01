package com.lakala.elive.market.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.DictDetailInfo;
import com.lakala.elive.beans.ImageItemInfo;
import com.lakala.elive.beans.MessageEvent;
import com.lakala.elive.beans.ShopVisitInfo;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.common.utils.BaiduMapUtils;
import com.lakala.elive.common.utils.DialogUtil;
import com.lakala.elive.common.utils.FileUtils;
import com.lakala.elive.common.utils.ImgCompressor;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.map.activity.LocationActivity;
import com.lakala.elive.market.adapter.ShopPicGridViewAdapter;
import com.lakala.elive.user.adapter.DictDetailListAdpter;
import com.lakala.elive.user.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


/**
 *
 * 工单签到处理
 *
 * @author hongzhiliang
 */
public class TaskVisitActivity extends BaseActivity implements BaiduMapUtils.LocationGpsListener,ImgCompressor.CompressListener{

    //拜访详情
    ShopVisitInfo mShopVisitInfo = new ShopVisitInfo();

    private TaskInfo taskInfo;

    //***********拍摄拜访图片*******************************
    private GridView noScrollgridview;

    //图片展示适配器
    private ShopPicGridViewAdapter mShopPicGridViewAdapter;

    //选择的图片的临时列表
    private ArrayList<ImageItemInfo> tempSelectBitmapList = new ArrayList<ImageItemInfo>();
    //维护一个图片的索引值
    private int itemIndex = 1;

    //***********拍摄拜访图片*******************************

    //***********下拉选择框*******************************
    private TextView tvVisitType;

    private DictDetailListAdpter mTaskResultAdapter;
    //***********下拉选择框*******************************

    //拜访地址地图显示按钮
    private ImageButton iBtnVisitLocation;
    //百度地图工具类
    private BaiduMapUtils mBaiduMapUtils;
    //定位地址显示
    private TextView tvVisitAddr;
    private Spinner  spinnerDealResult;

    private double latitude; // 纬度
    private double longitude; // 经度

    public String province = ""; // 省份

    public String city = ""; // 城市

    public String  district = ""; //区域

    //拜访备注
    private EditText etVisitComment;
    //拜访提交按钮
    private Button btnSubmitVisit;

    private Button btnPreEdit; //上一步提交信息
    private Button  btnOpenNormal;
    private Button  btnOpenClear;
    /**
     * 输入法管理器
     */
    private InputMethodManager mInputMethodManager;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_task_visit);
    }

    @Override
    protected void bindView() {
        tvVisitAddr = (TextView) findViewById(R.id.tv_visit_addr);
        etVisitComment = (EditText) findViewById(R.id.et_visit_comment);
        tvVisitType = (TextView) findViewById(R.id.tv_visit_type);
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        iBtnVisitLocation = (ImageButton) findViewById(R.id.btn_visit_location);
        btnSubmitVisit = (Button) findViewById(R.id.btn_submit_visit);//提交按钮
        btnOpenNormal = (Button) findViewById(R.id.btn_camera_open_normal);//普通拍摄
        btnOpenClear = (Button) findViewById(R.id.btn_camera_open_clear);//高清拍摄
        spinnerDealResult = (Spinner) findViewById(R.id.spinner_deal_result);//高清拍摄
    }

    @Override
    protected void bindEvent() {
        //返回按钮处理
        iBtnBack.setVisibility(View.VISIBLE);
        iBtnBack.setOnClickListener(this);

        etVisitComment.setOnClickListener(this);
        //初始化输入法
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        iBtnVisitLocation.setOnClickListener(this);
        btnSubmitVisit.setOnClickListener(this);

        //图片拍摄初始化
        initImageGridView();

        //BaiduMapUtils
        mBaiduMapUtils = new BaiduMapUtils();
        mBaiduMapUtils.setLocationListener(this);
        mBaiduMapUtils.startGps(this);


        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setOnClickListener(this);

        btnOpenNormal.setOnClickListener(this);
        btnOpenClear.setOnClickListener(this);
    }



    @Override
    protected void bindData() {
        //获取页面传递的对象
        taskInfo = (TaskInfo) getIntent().getExtras().get(Constants.EXTRAS_TASK_DEAL_INFO);

        tvTitleName.setText("工单处理签到");

        //工单类型
        tvVisitType.setText(mSession.getSysDictMap().get(Constants.TASK_TYPE).get(taskInfo.getTaskType()));

        //getTaskDealDataDict
        mSession =  Session.get(this);
        UserReqInfo reqInfo = new UserReqInfo();

        reqInfo.setTypeCode("TASK_RESULT");
        if("7".equals(taskInfo.getTaskType())||"6".equals(taskInfo.getTaskType())||"8".equals(taskInfo.getTaskType())){
            reqInfo.setBigType(taskInfo.getTaskType());
        }else{
            reqInfo.setBigType("1");
        }

        reqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        NetAPI.getDictDataListByBig(this, this,reqInfo);

        mTaskResultAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.TASK_RESULT));
        spinnerDealResult.setAdapter(mTaskResultAdapter);

        spinnerDealResult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mTaskResultAdapter.getItem(position);
                mShopVisitInfo.setExecuteResult(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


    }


    private void initImageGridView() {
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));

        //添加图片新增按钮
//        ImageItemInfo addImageBtn = new ImageItemInfo();
//        addImageBtn.isSelected = false;
//        addImageBtn.sortId = itemIndex++;
//        tempSelectBitmapList.add(addImageBtn);

        //初始化适配器
        mShopPicGridViewAdapter = new ShopPicGridViewAdapter(TaskVisitActivity.this, tempSelectBitmapList);
        noScrollgridview.setAdapter(mShopPicGridViewAdapter);

        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //图片点击事件处理
                ImageItemInfo imageItem = tempSelectBitmapList.get(position);
                if (!imageItem.isSelected) {

                } else {
                    try {
//                        byte[] buffer = FileUtils.getImageFileBytes(tempSelectBitmapList.get(position).getImagePath());
//                        String photo = Base64.encodeToString(buffer, 0, buffer.length,Base64.DEFAULT);
                        //删除图片
                        Intent screenShotIntent = new Intent(getApplicationContext(), ScreenshotActivity.class);
                        screenShotIntent.putExtra(Constants.EXTRAS_MER_SCREENSHOT_INFO, tempSelectBitmapList.get(position).getImagePath());
                        startActivity(screenShotIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        noScrollgridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                DialogUtil.createAlertDialog(TaskVisitActivity.this,
                        "提示", "是否删除该图片？", "取消", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case AlertDialog.BUTTON_POSITIVE:
                                        dialog.dismiss();
                                        tempSelectBitmapList.remove(position);
                                        mShopPicGridViewAdapter.setTempSelectBitmapList(tempSelectBitmapList);
                                        mShopPicGridViewAdapter.notifyDataSetChanged();
                                        break;
                                    case AlertDialog.BUTTON_NEGATIVE:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        }).show();

                //长按事件处理
//                Utils.showToast(TaskVisitActivity.this, "图片已删除!");
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.btn_visit_location:
                Intent intent = new Intent(TaskVisitActivity.this,LocationActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_submit_visit:
                submitVisit();
                break;
            case R.id.et_visit_comment:
//              etVisitComment = (EditText) findViewById(R.id.et_visit_comment);
                etVisitComment.setFocusable(true);//设置输入框可聚集
                etVisitComment.setFocusableInTouchMode(true);//设置触摸聚焦
                etVisitComment.requestFocus();//请求焦点
                etVisitComment.findFocus();//获取焦点
                mInputMethodManager.showSoftInput(etVisitComment, InputMethodManager.SHOW_FORCED);// 显示输入法
                break;
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.btn_action:
                doCancel();
                break;
            case R.id.btn_camera_open_normal: //普通打开摄像头
                if(tempSelectBitmapList.size() < Constants.MER_IMG_SIZE ){
                    UiUtils.startTakePhotoIntent(TaskVisitActivity.this,Constants.TAKE_PICTURE_NORMAL);
                }else{
                    Utils.showToast(TaskVisitActivity.this, "只能上传" + Constants.MER_IMG_SIZE  +"张图片!");
                }
                break;
            case R.id.btn_camera_open_clear: //高清打开摄像头
                if(tempSelectBitmapList.size() < Constants.MER_IMG_SIZE ){
                    UiUtils.startTakePhotoIntent(TaskVisitActivity.this,Constants.TAKE_PICTURE_CLEAR);
                }else{
                    Utils.showToast(TaskVisitActivity.this, "只能上传" + Constants.MER_IMG_SIZE  +"张图片!");
                }
                break;
            default:
        }
    }

    private void doCancel() {
        DialogUtil.createAlertDialog(
                this,
                "用户确认提示！",
                "取消工单处理？",
                "取消",
                "确定",
                mCancleListener
        ).show();
    }


    public void submitVisit(){
        try {
            mShopVisitInfo.setVisitType(taskInfo.getTaskType());
            mShopVisitInfo.setTaskId(taskInfo.getTaskId());
            mShopVisitInfo.setShopName(taskInfo.getShopName());
            mShopVisitInfo.setLatitude(latitude);
            mShopVisitInfo.setLongitude(longitude);
            mShopVisitInfo.setAddress(tvVisitAddr.getText().toString());

            if(mShopVisitInfo.getShopNo()!=null){
                mShopVisitInfo.setShopNo(taskInfo.getShopNo());
            }

            if(mShopVisitInfo.getMerchantCode()!=null){
                mShopVisitInfo.setMerchantCode(taskInfo.getMerchantCode());
            }

            if(mShopVisitInfo.getExecuteResult() == null || "".equals(mShopVisitInfo.getExecuteResult()) || "-1".equals(mShopVisitInfo.getExecuteResult())){
                Utils.showToast(this,"处理结果不能为空！");
                return;
            }else{
                DictDetailInfo dictDetailInfo = (DictDetailInfo) spinnerDealResult.getSelectedItem();
                mShopVisitInfo.setExecuteResult(dictDetailInfo.getDictKey());
            }

            //图片处理
            if(tempSelectBitmapList.size() >= 1){
                String[] photos = new String[tempSelectBitmapList.size()];
                for(int i=0; i < tempSelectBitmapList.size();i++){
                    byte[] buffer = FileUtils.getImageFileBytes(tempSelectBitmapList.get(i).getImagePath());
                    String photo = Base64.encodeToString(buffer, 0, buffer.length,Base64.DEFAULT);
                    photos[i] = photo;
                }
                mShopVisitInfo.setPhotos(photos);
            }else{
                //未添加说明
                Utils.showToast(this,"签到图片不能为空！");
                return;
            }

            String visitComment = etVisitComment.getText().toString();
            if(!TextUtils.isEmpty(visitComment)){
                if(visitComment.length() > Constants.VISIT_COMMENT_LEN){
                    //说明过长
                    Utils.showToast(this,"处理内容输入过长！");
                    return;
                }else{
                    mShopVisitInfo.setComments(visitComment);
                }

            }else{//未添加说明
                Utils.showToast(this,"处理说明不能为空！");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        mShopVisitInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());

        //信息编辑验证 这里没有必选条件 提示用户是否确认信息提交
        DialogUtil.createAlertDialog(
                this,
                "用户确认提示！",
                "提交工单信息？",
                "取消",
                "确定",
                mListener
        ).show();

    }

    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_TASK_VISIT:
                closeProgressDialog();
                Utils.showToast(this, "工单处理成功");
                // 4 发送消息
                EventBus.getDefault().post(new MessageEvent(Constants.MessageType.TASK_DEAL,null));
                MessageEvent messageEvent = new MessageEvent("path_task_for_plan",null);
                messageEvent.result = taskInfo.getTaskId();
                EventBus.getDefault().post(messageEvent);

                finish();
                break;
            case NetAPI.ACTION_GET_DICT_DATA_BIG:
                List<DictDetailInfo> tmpDictList = (List<DictDetailInfo>)obj;
                tmpDictList.add(0,new DictDetailInfo("-1","---请选择---"));
                mTaskResultAdapter = new DictDetailListAdpter(this,tmpDictList );
                spinnerDealResult.setAdapter(mTaskResultAdapter);
                spinnerDealResult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        DictDetailInfo dictDetailInfo = (DictDetailInfo) mTaskResultAdapter.getItem(position);
                        mShopVisitInfo.setExecuteResult(dictDetailInfo.getDictKey());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
        }
    }

    @Override
    public void onError(int method, String message) {
        switch (method) {
            case NetAPI.ACTION_TASK_VISIT:
                closeProgressDialog();
                Utils.showToast(this, "工单提交失败:" + message + "!" );
                break;
            case NetAPI.ACTION_GET_DICT_DATA_BIG:
                Utils.showToast(this, "数据字典加载失败:" + message + "!" );
                break;
        }
    }


    @Override
    public void locationErr() {
        tvVisitAddr.setText("定位失败!");
    }

    @Override
    public void locationSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvVisitAddr.setText(mBaiduMapUtils.locationAddr);
                latitude = mBaiduMapUtils.latitude;
                longitude = mBaiduMapUtils.longitude;
                province = mBaiduMapUtils.province;
                city = mBaiduMapUtils.city;
                district = mBaiduMapUtils.district;
                mBaiduMapUtils.stopGps();
            }
        });
    }


    @Override
    public void onCompressStart() {

    }

    @Override
    public void onCompressEnd(ImgCompressor.CompressResult mCompressResult) {
        if ( mCompressResult.getStatus() == ImgCompressor.CompressResult.RESULT_OK){
            //维护对应图片的数据结构
            ImageItemInfo takePhotoItem = new ImageItemInfo();
            takePhotoItem.setImagePath(mCompressResult.getOutPath());
            takePhotoItem.isSelected = true;
            takePhotoItem.sortId = itemIndex++;
            tempSelectBitmapList.add(takePhotoItem);
            Comparator comp = new ListSortComparator();//数据排序一下
            Collections.sort(tempSelectBitmapList,comp);
            //刷新图片列表
            mShopPicGridViewAdapter.setTempSelectBitmapList(tempSelectBitmapList);
            mShopPicGridViewAdapter.notifyDataSetChanged();
        }else{
            Utils.showToast(this, "图片获取失败:" + mCompressResult.getStatus() + "!" );
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.TAKE_PICTURE_NORMAL:
                if (resultCode == RESULT_OK) { //图片异步压缩转存
                    ImgCompressor.getInstance(this).setListener(this).starCompressWithDefault(
                            Constants.MER_IMG_TMP_FILE,
                            FileUtils.ELIVE_SD_SAVE_PATH,
                            String.valueOf(System.currentTimeMillis()) + ".jpeg",
                            "60");
                }
                break;
            case Constants.TAKE_PICTURE_CLEAR:
                if (resultCode == RESULT_OK) { //图片异步压缩转存
                    ImgCompressor.getInstance(this).setListener(this).starCompressWithDefault(
                            Constants.MER_IMG_TMP_FILE,
                            FileUtils.ELIVE_SD_SAVE_PATH,
                            String.valueOf(System.currentTimeMillis()) + ".jpeg",
                            "100");
                }
                break;
        }
    }


    class ListSortComparator implements Comparator {
        @Override
        public int compare(Object lhs, Object rhs) {
            ImageItemInfo a = (ImageItemInfo) lhs;
            ImageItemInfo b = (ImageItemInfo) rhs;
            return  b.sortId - a.sortId;
        }
    }


    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener mCancleListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which){
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    dialog.dismiss();
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which){
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    dialog.dismiss();
                    NetAPI.addTaskVisit(TaskVisitActivity.this, TaskVisitActivity.this, mShopVisitInfo);
                    showProgressDialog("签到提交中......");
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

}
