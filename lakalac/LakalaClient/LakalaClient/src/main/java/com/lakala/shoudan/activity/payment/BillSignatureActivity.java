package com.lakala.shoudan.activity.payment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.activity.BaseActionBarActivity;
import com.lakala.platform.bean.StlmRem2;
import com.lakala.platform.common.TimeCounter;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceCallback;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.collection.CollectionTransInfo;
import com.lakala.shoudan.activity.main.MainActivity;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.shoudan.activity.payment.signature.SignatureManager;
import com.lakala.shoudan.activity.quickArrive.TradeResultActivity;
import com.lakala.shoudan.activity.revocation.RevocationTransinfo;
import com.lakala.shoudan.activity.shoudan.barcodecollection.BarCodeCollectionTransInfo;
import com.lakala.shoudan.adapter.BillItemAdapter;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.component.SignatureView;
import com.lakala.shoudan.component.VerticalListView;
import com.lakala.shoudan.util.CommonUtil;
import com.lakala.ui.component.CustomScrollView;
import com.lakala.ui.component.CustomTableListView;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.AlertInputDialog;
import com.lakala.ui.dialog.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by More on 14-4-29.
 */
public class BillSignatureActivity extends BaseActionBarActivity {

    private boolean isSign = false;
    private SignatureView signaturelayout;
    public static final int START_FOR_SIGN = 456;
    BaseTransInfo transInfo;
    private BillItemAdapter mAdapter;
    private List<BillItemAdapter.ItemData> mData = new ArrayList<BillItemAdapter.ItemData>();
    ;
    private CustomScrollView scrollView;
    private List<BillItemAdapter.ItemData> moreData;
    private List<BillItemAdapter.ItemData> commonData;
    private boolean isSend=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_signature);
        initUI();
        initSignatureInfo();
        initList();
    }

    private View.OnClickListener signatureLayoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener completeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            completeClick();
        }
    };

    private View.OnClickListener goSignatureActivityClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cleanSign();
            Intent i = getIntent();
            i.setClass(BillSignatureActivity.this, SignatrueActivity.class);
            startActivityForResult(i, START_FOR_SIGN);
        }
    };

    private View.OnClickListener reSignClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cleanSign();
        }
    };


    protected void initUI() {
        if (null != SignatureManager.getInstance().showPic) {
            SignatureManager.getInstance().showPic.recycle();
        }
        transInfo = (BaseTransInfo) getIntent().getSerializableExtra(ConstKey.TRANS_INFO);

        //统计交易结果
        if(!TextUtils.isEmpty(transInfo.getStatisticTransResult())){
            ShoudanStatisticManager.getInstance().onEvent(transInfo.getStatisticTransResult(), this);
        }
        //统计交易结果
        if(!TextUtils.isEmpty(transInfo.getStatisticSignPage())){
            ShoudanStatisticManager.getInstance().onEvent(transInfo.getStatisticSignPage(), this);
        }

        try {
            PublicEnum.Business.Close();
        }catch (Exception x){}

//        resultList.addList(this, transInfo.getBillInfo(), false, getResources().getColor(R.color.transparent));
        scrollView = (CustomScrollView) findViewById(R.id.scroll_view);
        signaturelayout = (SignatureView) findViewById(R.id.signature_layout);
        signaturelayout.getPaint2().setStrokeWidth(3);
        scrollView.setIgnoreView(signaturelayout);

        findViewById(R.id.go_signature_activity).setOnClickListener(goSignatureActivityClick);
        ((TextView) findViewById(R.id.go_signature_activity)).setText(Html.fromHtml("<u>横屏</u>"));
        ((TextView) findViewById(R.id.complete)).setText(getString(R.string.complete));
        findViewById(R.id.complete).setOnClickListener(completeClick);
        findViewById(R.id.signature_layout).setOnClickListener(signatureLayoutClick);
        ((TextView) findViewById(R.id.re_sign)).setText(getString(R.string.re_sign));
        findViewById(R.id.re_sign).setOnClickListener(reSignClick);
        findViewById(R.id.context).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                CommonUtil.initBackground(BillSignatureActivity.this);
            }
        });
        navigationBar.setTitle("签购单");
        navigationBar.setBackBtnVisibility(View.INVISIBLE);
    }

    private void initList() {
        CustomTableListView resultList = (CustomTableListView) findViewById(R.id.result_list);
        List<TransferInfoEntity> billInfos = transInfo.getBillInfo();
        int size = billInfos == null ? 0 : billInfos.size();
        commonData = new ArrayList<BillItemAdapter.ItemData>();
        for (int i = 0; i < size; i++) {
            TransferInfoEntity info = billInfos.get(i);
            BillItemAdapter.ItemData item = new BillItemAdapter.ItemData();
            item.setTransInfo(info);
            item.setContent(true);
            item.setAmount(info.isDiffColor());
            VerticalListView.IconType type = info.getType();
            if (type != null) {
                switch (type) {
                    case WECHAT:
                        item.setLogoRes(R.drawable.pic_weixin);
                        break;
                    case ALIPAY:
                        item.setLogoRes(R.drawable.pic_zhifubao);
                        break;
                    case BAIDUPAY:
                        item.setLogoRes(R.drawable.pic_baidu);
                        break;
                    case UNIONPAY:
                        item.setLogoRes(R.drawable.pic_yinlian);
                        break;
                    default:
                        item.setLogoRes(0);
                        break;
                }
            }
            commonData.add(item);
        }
        commonData.add(new BillItemAdapter.ItemData().setContent(false));
        mData.addAll(commonData);
        mAdapter = new BillItemAdapter(mData);
        mAdapter.setOnDetailClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(String.valueOf(v.getTag()), "OPEN")) {
                    closeDetailView(v);
                } else {
                    if (moreData != null && moreData.size() != 0) {
                        openDetailView(v);
                    } else {
                        getDetailTask(v);
                    }
                }
            }
        });
        resultList.setAdapter(mAdapter);
    }

    private void getDetailTask(final View v) {
        showProgressDialog(null);
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        parseMoreData(jsonObject);
                        openDetailView(v);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.toast(BillSignatureActivity.this, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
            }
        };
        CommonServiceManager.getInstance()
                .getSignDetail(sid, callback);
    }

    private void parseMoreData(JSONObject jsonObject) {
        /**
         * terminalNo	终端号
         cardType	卡类别
         expiredDate	有效期
         tradeType	交易类别
         batchNo	批次号
         voucherCode	凭证号
         autcode	授权码
         sysSeq	参考号
         */
        moreData = new ArrayList<BillItemAdapter.ItemData>();
        BillItemAdapter.ItemData item = null;

        item = new BillItemAdapter.ItemData("商户名称", jsonObject.optString("shopName")).setDetail(true).setContent(true);
        moreData.add(item);

        item = new BillItemAdapter.ItemData("商户编号", jsonObject.optString("shopNo")).setDetail(true).setContent(true);
        moreData.add(item);

        item = new BillItemAdapter.ItemData("终端号", jsonObject.optString("terminalNo")).setDetail(true).setContent(true);
        moreData.add(item);

        if (transInfo instanceof BarCodeCollectionTransInfo) {//如果是扫码收款
            String billNo = jsonObject.optString("logNo");
            item = new BillItemAdapter.ItemData("订单号", billNo).setDetail(true).setContent(true);
            moreData.add(item);
        } else {
            item = new BillItemAdapter.ItemData("发卡行", jsonObject.optString("cardType")).setDetail(true).setContent(true);
            moreData.add(item);
            item = new BillItemAdapter.ItemData("有效期", jsonObject.optString("expiredDate")).setDetail(true).setContent(true);
            moreData.add(item);
        }

        item = new BillItemAdapter.ItemData("交易类别", jsonObject.optString("tradeType")).setDetail(true).setContent(true);
        moreData.add(item);
        item = new BillItemAdapter.ItemData("批次号", jsonObject.optString("batchNo")).setDetail(true).setContent(true);
        moreData.add(item);
//        item = new BillItemAdapter.ItemData("凭证号", jsonObject.optString("voucherCode")).setDetail(true).setContent(true);
//        moreData.add(item);
        item = new BillItemAdapter.ItemData("授权码", jsonObject.optString("autcode")).setDetail(true).setContent(true);
        moreData.add(item);
        item = new BillItemAdapter.ItemData("参考号", jsonObject.optString("sysSeq")).setDetail(true).setContent(true);
        moreData.add(item);
    }

    private void openDetailView(View v) {
        v.setTag("OPEN");
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), getResources().getDimensionPixelSize(R.dimen.dimen_30));
        TextView textView = (TextView) v.findViewById(R.id.textView);
        View arrow = v.findViewById(R.id.arrow);
        arrow.setSelected(true);
        textView.setText("收起详情");

        mData.addAll(moreData);
        mAdapter.notifyDataSetChanged();
    }

    private void closeDetailView(View v) {
        v.setTag("CLOSE");
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), 0);
        TextView textView = (TextView) v.findViewById(R.id.textView);
        View arrow = v.findViewById(R.id.arrow);
        arrow.setSelected(false);
        textView.setText("显示详情");

        mData.clear();
        mData.addAll(commonData);
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 清空签名板
     */
    private void cleanSign() {
        signaturelayout.clear();
        signaturelayout.setBackgroundColor(Color.WHITE);
        isSign = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        TimeCounter.getInstance().may2Gesture(this);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        TimeCounter.getInstance().save2BackgroundTime(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (START_FOR_SIGN == requestCode && resultCode == RESULT_OK) {
            try {
                isSign = data.getBooleanExtra(SignatrueActivity.RETURN_CHECK_SIGN, false);
                signaturelayout.setBackgroundDrawable(new BitmapDrawable(SignatureManager.getInstance().showPic));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
    }


    @Override
    public void onBackPressed() {
        return;
    }

    /**
     * 判断是否有签名字迹
     *
     * @return
     */
    private boolean isSign() {
        return signaturelayout.checkSignPath() || isSign;
    }
//
//    Handler handler_bitmap = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    startUpload();
//                    break;
//            }
//
//        }
//    };

    /**
     * 签名点击完成后的处理
     */
    private void completeClick() {
        if (isSign()) {
            if (transInfo instanceof CollectionTransInfo||transInfo instanceof RevocationTransinfo) {
                alertMobileNoInputDialog();
            } else {
                startUpload();
            }
        } else {
            ToastUtil.toast(BillSignatureActivity.this, getString(R.string.card_owner_sign));
        }
    }


    /**
     * 将bitmap中的某种颜色值替换成新的颜色
     *
     * @param oldBitmap
     * @param oldColor
     * @param newColor
     * @return
     */
    public static Bitmap replaceBitmapColor(Bitmap oldBitmap, int oldColor, int newColor) {
        //相关说明可参考 http://xys289187120.blog.51cto.com/3361352/657590/
        Bitmap mBitmap = oldBitmap.copy(Bitmap.Config.RGB_565, true);
        //循环获得bitmap所有像素点
        int mBitmapWidth = mBitmap.getWidth();
        int mBitmapHeight = mBitmap.getHeight();
        int mArrayColorLengh = mBitmapWidth * mBitmapHeight;
        int[] mArrayColor = new int[mArrayColorLengh];
        int count = 0;
        for (int i = 0; i < mBitmapHeight; i++) {
            for (int j = 0; j < mBitmapWidth; j++) {
                //获得Bitmap 图片中每一个点的color颜色值
                //将需要填充的颜色值如果不是
                //在这说明一下 如果color 是全透明 或者全黑 返回值为 0
                //getPixel()不带透明通道 getPixel32()才带透明部分 所以全透明是0x00000000
                //而不透明黑色是0xFF000000 如果不计算透明部分就都是0了
                int color = mBitmap.getPixel(j, i);
                //将颜色值存在一个数组中 方便后面修改
                if (color == oldColor) {
                    mBitmap.setPixel(j, i, newColor);  //将白色替换成透明色
                }

            }
        }
        return mBitmap;
    }

    private ProgressDialog progressDialog;

    protected void showProgressDialog(String str) {
        progressDialog = DialogCreator.createProgressDialog(this, str);
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        progressDialog.dismiss();
    }


    private void startUpload() {
        SignatureManager.getInstance().showPic = signaturelayout.getOriginalBitmap();
        LogUtil.print("Bill", SignatureManager.getInstance().showPic.getConfig() + "");
        showProgressDialog(null);
        SignatureManager.getInstance().uploadCacheSignatrue("1","",sid, chrttCode, pan, mobileNo, new SignatureManager.UploadListener() {
            @Override
            public void onUploadFinish(boolean ifSuccess) {
                hideProgressDialog();
                if (ifSuccess) {
                    LogUtil.print("UPLOAD","UPLOAD_SUCCESS");
                    handler.sendEmptyMessage(UPLOAD_SUCCESS);
                } else {
                    LogUtil.print("UPLOAD","UPLOAD_FAILED");
                    handler.sendEmptyMessage(UPLOAD_FAILED);
                }
            }
        });

    }

    private String sid;
    private String chrttCode;
    private String pan;
    private String mobileNo = "";

    protected void initSignatureInfo() {
        transInfo = (BaseTransInfo) getIntent().getSerializableExtra(ConstKey.TRANS_INFO);
        sid = transInfo.getSid();
        if (transInfo instanceof BarCodeCollectionTransInfo) {
            //系统交易特征码
            chrttCode = SignatureManager.getTransCode(transInfo.getSyTm(), transInfo.getSysRef());
        } else {
            chrttCode = SignatureManager.getTransCode(transInfo.getSyTm(), "000000000000");
        }
        pan = transInfo.getPayCardNo();

    }

    private static final int SHOW_PROGRESS = 10;
    private static final int UPLOAD_FAILED = 11;
    private static final int UPLOAD_SUCCESS = 12;

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_PROGRESS:
                    showProgressDialog(null);
                    break;
                case UPLOAD_FAILED:
                    SignatureManager.getInstance().startSignatruePollingSerice(sid, chrttCode, pan, mobileNo);//补签不送手机号
                    completeEvent();
                    break;
                case UPLOAD_SUCCESS:
                    completeEvent();
                    break;
            }
        }
    };

    private void completeEvent() {

        ToastUtil.toast(this, getString(R.string.sumbit_ok));
        if(transInfo instanceof CollectionTransInfo){
            getInfo();
        }else {
            startActivity(new Intent(this,MainActivity.class));
        }

    }

    /**
     * 获取交易详情
     */
    public void getInfo(){
        String url="v1.0/business/speedArrivalD0/getBusinessStatus/"+transInfo.getSid();
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(this,url, HttpRequest.RequestMethod.GET,true);
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if("000000".equals(resultServices.retCode)||"050506".equals(resultServices.retCode)){
                    try {
                        startActivity(new Intent(BillSignatureActivity.this, TradeResultActivity.class)
                                .putExtra("isSend",isSend)
                                .putExtra("data",resultServices.retData)
                                .putExtra("msg",resultServices.retMsg)
                                .putExtra("code",resultServices.retCode)
                                .putExtra(ConstKey.TRANS_INFO,transInfo)
                                .putExtra("amout",transInfo.getSwipeAmount()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        isSend=false;
                    }
                }else {
                    isSend=false;
                    ToastUtil.toast(BillSignatureActivity.this, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                isSend=false;
                ToastUtil.toast(BillSignatureActivity.this,R.string.socket_fail);
            }
        }));
        businessRequest.execute();
    }


    private void alertMobileNoInputDialog() {
        final AlertInputDialog alertInputDialog = new AlertInputDialog();
        alertInputDialog.setButtons(new String[]{"不发送", "发送"});
        alertInputDialog.setTitle("发送凭证");
        alertInputDialog.setDividerVisibility(View.VISIBLE);
        alertInputDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                String event=transInfo.getStatisticIsSend();
                switch (index) {
                    case 0:
                        isSend=false;
                        mobileNo = "";
                        startUpload();
                        dialog.dismiss();
                        if(!TextUtils.isEmpty(event)){
                            event=String.format(event,"不发");
                        }
                        break;
                    case 1:
                        isSend=true;
                        EditText editInput = alertInputDialog.getEditInput();
                        if (editInput.getText() == null) {
                            mobileNo = "";
                        } else {
                            mobileNo = editInput.getText().toString();
                        }

                        if (!PhoneNumberUtil.checkPhoneNumber(mobileNo)) {
                            ToastUtil.toast(BillSignatureActivity.this, getString(R.string.toast_input_correct_phone_num));
                            return;
                        }

                        startUpload();
                        dialog.dismiss();
                        if(!TextUtils.isEmpty(event)){
                            event=String.format(event,"发");
                        }
                        break;
                }
                if(!TextUtils.isEmpty(event)){
                    ShoudanStatisticManager.getInstance().onEvent(event,BillSignatureActivity.this);
                }
            }
        });

        alertInputDialog.show(getSupportFragmentManager());


    }

}
