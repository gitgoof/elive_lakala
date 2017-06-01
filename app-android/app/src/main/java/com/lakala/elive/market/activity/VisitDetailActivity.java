package com.lakala.elive.market.activity;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.ImageItemInfo;
import com.lakala.elive.beans.ShopVisitInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerShopReqInfo;
import com.lakala.elive.common.utils.FileUtils;
import com.lakala.elive.common.utils.ImageTools;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.market.adapter.ShopPicGridViewAdapter;
import com.lakala.elive.user.base.BaseActivity;

import java.util.ArrayList;


/**
 *
 *
 * Step3 网点终端列表
 *
 * @author hongzhiliang
 */
public class VisitDetailActivity extends BaseActivity {

    private MerShopReqInfo merShopReqInfo = new MerShopReqInfo(); //请求

    //拜访详情
    ShopVisitInfo mShopVisitInfo = new ShopVisitInfo();


    //***********拍摄拜访图片*******************************
    private GridView noScrollgridview;

    //图片展示适配器
    private ShopPicGridViewAdapter mShopPicGridViewAdapter;

    //选择的图片的临时列表
    private ArrayList<ImageItemInfo> tempSelectBitmapList = new ArrayList<ImageItemInfo>();

    //维护一个图片的索引值
    private int itemIndex = 1;

    //定位地址显示
    private TextView tvVisitAddr;
    private TextView tvVisitType;
    //拜访备注
    private TextView tvVisitComment;

    private double latitude; // 纬度

    private double longitude; // 经度


    @Override
    protected void setContentViewId() {
        setContentView(R.layout.fragment_mer_visit_detail);
    }

    @Override
    protected void bindView() {
        tvVisitAddr = (TextView) findViewById(R.id.tv_visit_addr);
        tvVisitComment = (TextView) findViewById(R.id.et_visit_comment);
        tvVisitType = (TextView)findViewById(R.id.tv_visit_type);
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
    }


    private void initImageGridView() {
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));

        if(mShopVisitInfo.getPhotos()!=null && mShopVisitInfo.getPhotos().length > 0){
            for(int i =0 ; i< mShopVisitInfo.getPhotos().length; i++ ){
                ImageItemInfo addImageBtn = new ImageItemInfo();
                addImageBtn.isSelected = true;
                try {
                    addImageBtn.setBitmap(ImageTools.base64ToBitmap( mShopVisitInfo.getPhotos()[i]));
                    tempSelectBitmapList.add(addImageBtn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //初始化适配器
        mShopPicGridViewAdapter = new ShopPicGridViewAdapter(VisitDetailActivity.this, tempSelectBitmapList);
        noScrollgridview.setAdapter(mShopPicGridViewAdapter);

        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //写一下,给图片路径
                    Intent screenShotIntent = new Intent(VisitDetailActivity.this, ScreenshotActivity.class);
                    String base64Img = mShopVisitInfo.getPhotos()[position];

                    String imgPath = ImageTools.saveBitmapPhoto(ImageTools.base64ToBitmap(base64Img),
                            FileUtils.ELIVE_SD_SAVE_PATH,
                            String.valueOf(System.currentTimeMillis())+ ".jpeg");

                    screenShotIntent.putExtra(Constants.EXTRAS_MER_SCREENSHOT_INFO,imgPath);
                    startActivity(screenShotIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        noScrollgridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //长按事件处理
                return false;
            }
        });
    }


    @Override
    protected void bindEvent() {
        iBtnBack = (ImageView) findViewById(R.id.btn_iv_back);
        iBtnBack.setVisibility(View.VISIBLE);
        iBtnBack.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        tvTitleName.setText("网点签到详情");
        mShopVisitInfo = (ShopVisitInfo) getIntent().getExtras().get(Constants.EXTRAS_MER_VISIT_INFO); //获取页面传递的对象
        handleVisitInfo();

        queryMerVisitDetail();


    }

    private void handleVisitInfo() {
       tvVisitAddr.setText(mShopVisitInfo.getAddress());

       tvVisitComment.setText(mShopVisitInfo.getComments());
        if(mShopVisitInfo.getVisitType()!=null
                && !"".equals(mShopVisitInfo.getVisitType()) && !"-1".equals(mShopVisitInfo.getVisitType()) ){
            tvVisitType.setText(mSession.getSysDictMap().get(Constants.VISIT_TYPE).get(mShopVisitInfo.getVisitType()));
        }else {
            tvVisitType.setText("");
        }

    }


    private void queryMerVisitDetail() {
        showProgressDialog("正在加载签到详情...");
        merShopReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merShopReqInfo.setVisitNo(mShopVisitInfo.getVisitNo());

        NetAPI.queryVisitDetail(this, this, merShopReqInfo);

    }

    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_SHOP_VISIT_DETAIL:
                closeProgressDialog();
                handleVisitInfo((ShopVisitInfo) obj);
                break;
        }
    }


    private void handleVisitInfo(ShopVisitInfo shopVisitInfo) {
        this.mShopVisitInfo = shopVisitInfo;

        tvVisitAddr.setText(mShopVisitInfo.getAddress());

        tvVisitComment.setText(mShopVisitInfo.getComments());

        if(mShopVisitInfo.getVisitType()!=null
                && !"".equals(mShopVisitInfo.getVisitType()) && !"-1".equals(mShopVisitInfo.getVisitType()) ){
            tvVisitType.setText(mSession.getSysDictMap().get(Constants.VISIT_TYPE).get(mShopVisitInfo.getVisitType()));
        }else {
            tvVisitType.setText("");
        }


        initImageGridView();

    }

    @Override
    public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_SHOP_VISIT_DETAIL:
                closeProgressDialog();
                Utils.showToast(this, "加载失败:" + statusCode + "!");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            default:
                break;
        }
    }


}
