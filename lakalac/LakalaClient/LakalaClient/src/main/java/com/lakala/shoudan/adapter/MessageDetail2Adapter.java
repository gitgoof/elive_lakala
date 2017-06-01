package com.lakala.shoudan.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.DateUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.messagecenter.MessageDetail2Activity;
import com.lakala.shoudan.activity.messagecenter.messageBean.BusinessMessage;
import com.lakala.shoudan.activity.messagecenter.messageBean.BusinessType;
import com.lakala.shoudan.activity.messagecenter.messageBean.MessageIconType;
import com.lakala.shoudan.activity.messagecenter.messageBean.PublishMessage;
import com.lakala.shoudan.activity.messagecenter.messageBean.TradeMessage;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.datadefine.Message;
import com.lakala.ui.component.CornerImageView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;

import java.util.List;

import static com.lakala.shoudan.R.drawable.pic_gmsjskb;
import static com.lakala.shoudan.R.drawable.pic_hyfl;
import static com.lakala.shoudan.R.drawable.pic_lc;
import static com.lakala.shoudan.R.drawable.pic_news;
import static com.lakala.shoudan.R.drawable.pic_shkt;
import static com.lakala.shoudan.R.drawable.pic_ywtg;


/**
 * Created by huwei on 16/8/16.
 */
public class MessageDetail2Adapter extends BaseAdapter {
    private List<TradeMessage> mTradelist_All;
    private List<PublishMessage> mPublishlist_All;
    private List<BusinessMessage> mBusinesslist_All;
    private LayoutInflater inflater;
    Message.MSG_TYPE MSG_TYPE = Message.MSG_TYPE.Publish;
    protected OnFunctionBtnClickListener functionclick;
    private Context context;
    private BitmapUtils bitmapUtils;

    private void initBitmapUtils() {
        bitmapUtils = new BitmapUtils(context);
//        bitmapUtils.configDefaultLoadFailedImage(R.drawable.banner_default_big);
        bitmapUtils.configDefaultShowOriginal(false);// 显示原始图片,不压缩,
        // 尽量不要使用,图片太大时容易OOM。
        bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
        bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils
                .getScreenSize(context));
//        bitmapUtils.configDefaultCacheExpiry(2 * 60 * 1000);
        bitmapUtils.configThreadPoolSize(4);
        bitmapUtils.configDiskCacheEnabled(true);
        bitmapUtils.configMemoryCacheEnabled(false);
    }

    public MessageDetail2Adapter(Context context, Message.MSG_TYPE MSG_TYPE, List<BusinessMessage> mBusinesslist) {
        this.context = context;
        this.mBusinesslist_All = mBusinesslist;
        this.MSG_TYPE = MSG_TYPE;
        this.inflater = LayoutInflater.from(context);
        initBitmapUtils();
    }

    public MessageDetail2Adapter(Message.MSG_TYPE MSG_TYPE, List<TradeMessage> mTradeList, Context context) {
        this.context = context;
        this.mTradelist_All = mTradeList;
        this.MSG_TYPE = MSG_TYPE;
        this.inflater = LayoutInflater.from(context);
        initBitmapUtils();
    }

    public MessageDetail2Adapter(List<PublishMessage> mPublishlist, Context context, Message.MSG_TYPE MSG_TYPE) {
        this.context = context;
        this.mPublishlist_All = mPublishlist;
        this.MSG_TYPE = MSG_TYPE;
        this.inflater = LayoutInflater.from(context);
        initBitmapUtils();
    }


    public void setOnFunctionBtnClick(OnFunctionBtnClickListener listener) {
        this.functionclick = listener;
    }


    @Override
    public int getCount() {
        int size = 0;
        switch (MSG_TYPE) {
            case Publish:
                size = mPublishlist_All.size();
                break;
            case Trade:
                size = mTradelist_All.size();
                break;
            case Business:
                size = mBusinesslist_All.size();
                break;
        }
        return size;
    }

    /**
     * 刷新数据
     *
     * @param list
     */
    public void addAll(List<? extends Object> list) {
        switch (MSG_TYPE) {
            case Publish:
                mPublishlist_All = (List<PublishMessage>) list;
                break;
            case Trade:
                mTradelist_All = (List<TradeMessage>) list;
                break;
            case Business:
                mBusinesslist_All = (List<BusinessMessage>) list;
                break;
        }
        notifyDataSetChanged();
    }

    public void addTradeList(List<TradeMessage> mTradeList) {
        mTradelist_All = mTradeList;
        notifyDataSetChanged();
    }

    public void addBusinessList(List<BusinessMessage> mBusinessList) {
        this.mBusinesslist_All = mBusinessList;
        notifyDataSetChanged();
    }

    public void addPublishList(List<PublishMessage> mPublishList) {
        this.mPublishlist_All = mPublishList;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        Object obj = null;
        switch (MSG_TYPE) {
            case Publish:
                obj = mPublishlist_All.get(position);
                break;
            case Trade:
                obj = mTradelist_All.get(position);
                break;
            case Business:
                obj = mBusinesslist_All.get(position);
                break;
        }
        return obj;
    }

    //删除列表中的元素
    public void removeItem(Object obj) {
        switch (MSG_TYPE) {
            case Publish:
                mPublishlist_All.remove((PublishMessage) obj);
                if (mPublishlist_All.size() == 0) {
                    ((MessageDetail2Activity) context).handleFailure();
                }
                break;
            case Trade:
                mTradelist_All.remove((TradeMessage) obj);
                if (mTradelist_All.size() == 0) {
                    ((MessageDetail2Activity) context).handleFailure();
                }
                break;
            case Business:
                mBusinesslist_All.remove((BusinessMessage) obj);
                if (mBusinesslist_All.size() == 0) {
                    ((MessageDetail2Activity) context).handleFailure();
                }
                break;
        }
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyEasyViewHolder eHolder = null;
        MyComplexViewHolder cHolder = null;
        if (convertView == null) {
            switch (MSG_TYPE) {
                case Trade:
                case Business:
                    convertView = inflater.inflate(R.layout.item_message_business, null);
                    eHolder = new MyEasyViewHolder(convertView);
                    convertView.setTag(eHolder);
                    break;
                case Publish:
                    convertView = inflater.inflate(R.layout.item_message_publish_ptf, null);
                    cHolder = new MyComplexViewHolder(convertView);
                    convertView.setTag(cHolder);
            }
        } else {
            if (MSG_TYPE.getIndex() == 0)//publish
                cHolder = (MyComplexViewHolder) convertView.getTag();
            else
                eHolder = (MyEasyViewHolder) convertView.getTag();//business,trade
        }
        if (MSG_TYPE.getIndex() == 0) {
            final PublishMessage pm = (PublishMessage) getItem(position);
            if (pm != null)
                fillPublishData(pm, cHolder);
        } else if (MSG_TYPE.getIndex() == 1) {
            eHolder.ll_trade.setVisibility(View.VISIBLE);
            eHolder.tv_content.setVisibility(View.GONE);
            eHolder.tv_amount.setVisibility(View.VISIBLE);
            final TradeMessage tm = (TradeMessage) getItem(position);
            if (tm != null)
                fillTradeData(tm, eHolder);
        } else {
            eHolder.ll_trade.setVisibility(View.GONE);
            eHolder.tv_content.setVisibility(View.VISIBLE);
            eHolder.tv_amount.setVisibility(View.GONE);
            final BusinessMessage bm = (BusinessMessage) getItem(position);
            if (bm != null) {
                String typeName = bm.getTypeName();
                if (TextUtils.equals(typeName, Constants.BUSINESS_PUBLISH)) {
                    eHolder.rl_container.setVisibility(View.GONE);
                    eHolder.rl_container_bp.setVisibility(View.VISIBLE);
                    fillBusinessPublishData(bm, eHolder);
                } else {
                    eHolder.rl_container.setVisibility(View.VISIBLE);
                    eHolder.rl_container_bp.setVisibility(View.GONE);
                    fillBusinessData(bm, eHolder);
                }

            }
        }
        return convertView;
    }

    /**
     * 填充业务中系统内容
     */

    private void fillBusinessPublishData(BusinessMessage bm, MyEasyViewHolder eHolder) {
        checkTitleType_bp(bm, eHolder);
        checkContentType_bp(bm, eHolder);
        checkFunctionType_bp(bm, eHolder);
    }

    /**
     * @param bm      系统消息
     * @param eHolder 业务内系统消息uiHolder
     */
    private void checkFunctionType_bp(final BusinessMessage bm, MyEasyViewHolder eHolder) {
        eHolder.fl_function.setVisibility(View.VISIBLE);
        eHolder.tv_function_middle.setVisibility(View.VISIBLE);
        eHolder.tv_function_middle.setText("版本升级");
        eHolder.tv_function_right.setText("立即体验");
        String versionUpdateFlag = bm.getVersionUpdateFlag();
        String serviceActionURL = bm.getServiceActionURL();
        String detailsClickURL = bm.getDetailsClickURL();
        String detailsLabVal = bm.getDetailsLabVal();
        boolean versionUpdate = Boolean.parseBoolean(versionUpdateFlag);
        boolean isServiceNull = TextUtils.isEmpty(serviceActionURL);
        boolean isDetailNull = TextUtils.isEmpty(detailsClickURL);
        detailsLabVal = TextUtils.isEmpty(detailsLabVal) ? "查看详情" : detailsLabVal;
        eHolder.tv_function_left.setText(detailsLabVal);
        if (versionUpdate) {//版本升级不为空
            eHolder.tv_function_middle.setText("版本升级");
            eHolder.tv_function_left.setVisibility(!isDetailNull ? View.VISIBLE : View.GONE);
            eHolder.tv_function_right.setVisibility(!isServiceNull ? View.VISIBLE : View.GONE);
            if (!isDetailNull) {//处理版本升级为右侧
                eHolder.tv_function_middle.setVisibility(View.GONE);
                eHolder.tv_function_right.setVisibility(View.VISIBLE);
                eHolder.tv_function_right.setText("版本升级");
                eHolder.tv_function_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        functionclick.OnPublishBusinessClickEvent3(view.getId());
                    }
                });
            } else {
                eHolder.tv_function_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        functionclick.OnPublishBusinessClickEvent(view.getId(), bm);
                    }
                });
            }
            eHolder.tv_function_middle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    functionclick.OnPublishBusinessClickEvent(view.getId(), bm);
                }
            });
        } else {//无版本升级选项
            if (!isDetailNull && !isServiceNull) {
                eHolder.tv_function_middle.setVisibility(View.GONE);
                eHolder.tv_function_left.setVisibility(View.VISIBLE);
                eHolder.tv_function_right.setVisibility(View.VISIBLE);
            } else if (!isDetailNull && isServiceNull) {//左侧功能键居中
                eHolder.tv_function_left.setVisibility(View.GONE);
                eHolder.tv_function_right.setVisibility(View.GONE);
                eHolder.tv_function_middle.setText(detailsLabVal);
                eHolder.tv_function_middle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        functionclick.OnPublishBusinessClickEvent2(view.getId(), bm, bm.getDetailsClickURL());
                    }
                });
            } else if (isDetailNull && !isServiceNull) {//右侧功能键居中
                eHolder.tv_function_left.setVisibility(View.GONE);
                eHolder.tv_function_right.setVisibility(View.GONE);
                eHolder.tv_function_middle.setText("立即体验");
                eHolder.tv_function_middle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        functionclick.OnPublishBusinessClickEvent1(view.getId(), bm, bm.getServiceActionURL());
                    }
                });
            } else {
                eHolder.fl_function.setVisibility(View.GONE);
            }
            eHolder.tv_function_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    functionclick.OnPublishBusinessClickEvent(view.getId(), bm);
                }
            });
        }
        eHolder.tv_function_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionclick.OnPublishBusinessClickEvent(view.getId(), bm);
            }
        });
    }


    /**
     * @param bm      系统消息
     * @param eHolder 系统消息uiHolder
     */
    private void checkContentType_bp(final BusinessMessage bm, MyEasyViewHolder eHolder) {
        eHolder.fl_content_bp.setVisibility(View.VISIBLE);
        String contentClickURL = bm.getContentClickURL(); //url_html
        String contentImageURL = bm.getContentImageURL(); //url_picture
        String detailsClickURL = bm.getDetailsClickURL(); //查看详情url，与图片点击url为统一入口
        String contentText = bm.getContentText();
//        bitmapUtils.configDefaultLoadingImage(R.drawable.banner_default_big);
        bitmapUtils.configDefaultLoadFailedImage(R.drawable.banner_default_big);
        eHolder.iv_content_publish.setClickable(true);
        if (!TextUtils.isEmpty(contentText)) {//文字
            if (!TextUtils.isEmpty(contentImageURL)) {//+图片
                eHolder.tv_content_publish.setVisibility(View.GONE);
                eHolder.vdivider_content.setVisibility(View.VISIBLE);
                eHolder.tv_content_bottom.setVisibility(View.VISIBLE);
                eHolder.tv_content_bottom.setText(contentText);
                eHolder.iv_content_publish.setVisibility(View.VISIBLE);
                bitmapUtils.display(eHolder.iv_content_publish, contentImageURL);
                eHolder.iv_content_publish.setLongClickable(true);
                if (!TextUtils.isEmpty(detailsClickURL)) {
                    eHolder.iv_content_publish.setClickable(true);
                    eHolder.iv_content_publish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            functionclick.OnPublishBusinessClickEvent(view.getId(), bm);
                        }
                    });
                } else {
                    eHolder.iv_content_publish.setClickable(false);
                }
            } else {
                eHolder.tv_content_publish.setVisibility(View.VISIBLE);
                eHolder.tv_content_publish.setText(contentText);
                eHolder.vdivider_content.setVisibility(View.GONE);
                eHolder.tv_content_bottom.setVisibility(View.GONE);
                eHolder.iv_content_publish.setVisibility(View.GONE);
            }
        } else {//没有文字
            eHolder.tv_content_bottom.setVisibility(View.GONE);
            eHolder.vdivider_content.setVisibility(View.GONE);
            eHolder.tv_content_publish.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(contentImageURL)) {//有图片
                eHolder.iv_content_publish.setVisibility(View.VISIBLE);
                bitmapUtils.display(eHolder.iv_content_publish, contentImageURL);
                eHolder.iv_content_publish.setLongClickable(true);
                if (!TextUtils.isEmpty(detailsClickURL)) {
                    eHolder.iv_content_publish.setClickable(true);
                    eHolder.iv_content_publish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            functionclick.OnPublishBusinessClickEvent(view.getId(), bm);
                        }
                    });
                } else {
                    eHolder.iv_content_publish.setClickable(false);
                }
            }

        }
    }

    private void checkTitleType_bp(final BusinessMessage bm, MyEasyViewHolder eHolder) {
        eHolder.tv_time.setText(DateUtil.formatDateStr_msgTime(bm.getMsgTime()));
        String titleText = bm.getTitleText();
        String titleImagURL = bm.getTitleImagURL();
        eHolder.fl_title.setVisibility(View.VISIBLE);
//        bitmapUtils.configDefaultLoadingImage(R.drawable.banner_default_small);
        bitmapUtils.configDefaultLoadFailedImage(R.drawable.banner_default_small);
        if (!TextUtils.isEmpty(titleText)) {//图片+文字
            eHolder.rl_title_text.setVisibility(View.VISIBLE);
            eHolder.iv_publish_title.setVisibility(View.GONE);
            eHolder.tv_publish_title.setText(titleText);
            if (TextUtils.isEmpty(titleImagURL)) {
                eHolder.iv_publish_icon.setImageResource(pic_news);
                return;
            }
            try {
                switch (MessageIconType.valueOf(titleImagURL)) {
                    case largeLimitPayment:
                        eHolder.iv_publish_icon.setImageResource(R.drawable.pic_desk);
                        break;
                    case loan:
                        eHolder.iv_publish_icon.setImageResource(R.drawable.pic_dk);
                        break;
                    case buyMobilePhonesOfTreasure:
                        eHolder.iv_publish_icon.setImageResource(pic_gmsjskb);
                        break;
                    case activityArea:
                        eHolder.iv_publish_icon.setImageResource(pic_hyfl);
                        break;
                    case tradePrinciple:
                        eHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case manangeMoneyMaster:
                        eHolder.iv_publish_icon.setImageResource(pic_lc);
                        break;
                    case D0:
                        eHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case scancodePayment:
                        eHolder.iv_publish_icon.setImageResource(R.drawable.pic_sm);
                        break;
                    case deviceManager:
                        eHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case usingHelp:
                        eHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case mobileRecharge:
                        eHolder.iv_publish_icon.setImageResource(R.drawable.pic_sjcz);
                        break;
                    case swipingCardPayment:
                        eHolder.iv_publish_icon.setImageResource(R.drawable.pic_sk);
                        break;
                    case creditCardPayment:
                        eHolder.iv_publish_icon.setImageResource(R.drawable.pic_xyk);
                        break;
                    case aPieceOfTreasure:
                        eHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case userInformation:
                        eHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case balanceQuery:
                        eHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case remittance:
                        eHolder.iv_publish_icon.setImageResource(R.drawable.pic_zz);
                        break;
                    case D0withdraw:
                        eHolder.iv_publish_icon.setImageResource(R.drawable.pic_dzero);
                        break;
                    case merchantOpen:
                        eHolder.iv_publish_icon.setImageResource(pic_shkt);
                        break;
                    case UpgradeBusinessPromotion:
                        eHolder.iv_publish_icon.setImageResource(R.drawable.pic_sjsjsj);
                        break;
                    case businessPromotion:
                        eHolder.iv_publish_icon.setImageResource(pic_ywtg);
                        break;
                    default:
                        eHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                eHolder.iv_publish_icon.setImageResource(pic_news);
            }
        } else {//图片
            eHolder.rl_title_text.setVisibility(View.GONE);
            eHolder.iv_publish_title.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(titleImagURL)) {
                bitmapUtils.display(eHolder.iv_publish_title, titleImagURL);
                eHolder.iv_publish_title.setClickable(true);
                eHolder.iv_publish_title.setLongClickable(true);
            } else {
                eHolder.fl_title.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 填充系统公告
     *
     * @param pm
     * @param cHolder
     */
    private void fillPublishData(PublishMessage pm, MyComplexViewHolder cHolder) {
        checkTitleType(pm, cHolder);
        checkContentType(pm, cHolder);
        checkFunctionType(pm, cHolder);
    }

    /**
     * @param pm      系统消息
     * @param cHolder 系统消息uiHolder
     */
    private void checkFunctionType(final PublishMessage pm, MyComplexViewHolder cHolder) {
        cHolder.fl_function.setVisibility(View.VISIBLE);
        cHolder.tv_function_middle.setVisibility(View.VISIBLE);
        cHolder.tv_function_middle.setText("版本升级");
        cHolder.tv_function_right.setText("立即体验");
        String versionUpdateFlag = pm.getVersionUpdateFlag();
        String serviceActionURL = pm.getServiceActionURL();
        String detailsClickURL = pm.getDetailsClickURL();
        String detailsLabVal = pm.getDetailsLabVal();
        boolean versionUpdate = Boolean.parseBoolean(versionUpdateFlag);
        boolean isServiceNull = TextUtils.isEmpty(serviceActionURL);
        boolean isDetailNull = TextUtils.isEmpty(detailsClickURL);
        detailsLabVal = TextUtils.isEmpty(detailsLabVal) ? "查看详情" : detailsLabVal;
        cHolder.tv_function_left.setText(detailsLabVal);
        if (versionUpdate) {//版本升级不为空
            cHolder.tv_function_left.setText(detailsLabVal);
            cHolder.tv_function_middle.setText("版本升级");
            cHolder.tv_function_left.setVisibility(!isDetailNull ? View.VISIBLE : View.GONE);
            cHolder.tv_function_right.setVisibility(!isServiceNull ? View.VISIBLE : View.GONE);
            if (!isDetailNull) {//处理版本升级为右侧
                cHolder.tv_function_middle.setVisibility(View.GONE);
                cHolder.tv_function_right.setVisibility(View.VISIBLE);
                cHolder.tv_function_right.setText("版本升级");
                cHolder.tv_function_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        functionclick.OnPublishClickEvent3(view.getId());
                    }
                });
            } else {
                cHolder.tv_function_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        functionclick.OnPublishClickEvent(view.getId(), pm);
                    }
                });
            }
            cHolder.tv_function_middle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    functionclick.OnPublishClickEvent(view.getId(), pm);
                }
            });
        } else {//无版本升级选项
            if (!isDetailNull && !isServiceNull) {
                cHolder.tv_function_middle.setVisibility(View.GONE);
                cHolder.tv_function_left.setVisibility(View.VISIBLE);
                cHolder.tv_function_right.setVisibility(View.VISIBLE);
            } else if (!isDetailNull && isServiceNull) {//左侧功能键居中
                cHolder.tv_function_left.setVisibility(View.GONE);
                cHolder.tv_function_right.setVisibility(View.GONE);
                cHolder.tv_function_middle.setText(detailsLabVal);
                cHolder.tv_function_middle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        functionclick.OnPublishClickEvent2(view.getId(), pm, pm.getDetailsClickURL());
                    }
                });
            } else if (isDetailNull && !isServiceNull) {//右侧功能键居中
                cHolder.tv_function_left.setVisibility(View.GONE);
                cHolder.tv_function_right.setVisibility(View.GONE);
                cHolder.tv_function_middle.setText("立即体验");
                cHolder.tv_function_middle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        functionclick.OnPublishClickEvent1(view.getId(), pm, pm.getServiceActionURL());
                    }
                });
            } else {
                cHolder.fl_function.setVisibility(View.GONE);
            }
            cHolder.tv_function_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    functionclick.OnPublishClickEvent(view.getId(), pm);
                }
            });
        }
        cHolder.tv_function_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionclick.OnPublishClickEvent(view.getId(), pm);
            }
        });
    }


    /**
     * @param pm      系统消息
     * @param cHolder 系统消息uiHolder
     */
    private void checkContentType(final PublishMessage pm, MyComplexViewHolder cHolder) {
        cHolder.fl_content.setVisibility(View.VISIBLE);
        String contentClickURL = pm.getContentClickURL(); //url_html
        String contentImageURL = pm.getContentImageURL(); //url_picture
        String detailsClickURL = pm.getDetailsClickURL(); //查看详情url，与图片点击url为统一入口
        String contentText = pm.getContentText();
//        bitmapUtils.configDefaultLoadingImage(R.drawable.banner_default_big);
        bitmapUtils.configDefaultLoadFailedImage(R.drawable.banner_default_big);
        cHolder.iv_content_publish.setClickable(true);
        if (!TextUtils.isEmpty(contentText)) {//文字
            if (!TextUtils.isEmpty(contentImageURL)) {//+图片
                cHolder.tv_content_publish.setVisibility(View.GONE);
                cHolder.vdivider_content.setVisibility(View.VISIBLE);
                cHolder.tv_content_bottom.setVisibility(View.VISIBLE);
                cHolder.tv_content_bottom.setText(contentText);
                cHolder.iv_content_publish.setVisibility(View.VISIBLE);
                bitmapUtils.display(cHolder.iv_content_publish, contentImageURL);
                cHolder.iv_content_publish.setLongClickable(true);
                if (!TextUtils.isEmpty(detailsClickURL)) {
                    cHolder.iv_content_publish.setClickable(true);
                    cHolder.iv_content_publish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            functionclick.OnPublishClickEvent(view.getId(), pm);
                        }
                    });
                } else {
                    cHolder.iv_content_publish.setClickable(false);
                }
            } else {
                cHolder.tv_content_publish.setVisibility(View.VISIBLE);
                cHolder.tv_content_publish.setText(contentText);
                cHolder.vdivider_content.setVisibility(View.GONE);
                cHolder.tv_content_bottom.setVisibility(View.GONE);
                cHolder.iv_content_publish.setVisibility(View.GONE);
            }
        } else {//没有文字
            cHolder.tv_content_bottom.setVisibility(View.GONE);
            cHolder.vdivider_content.setVisibility(View.GONE);
            cHolder.tv_content_publish.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(contentImageURL)) {//有图片
                cHolder.iv_content_publish.setVisibility(View.VISIBLE);
                bitmapUtils.display(cHolder.iv_content_publish, contentImageURL);
                cHolder.iv_content_publish.setLongClickable(true);
                if (!TextUtils.isEmpty(detailsClickURL)) {
                    cHolder.iv_content_publish.setClickable(true);
                    cHolder.iv_content_publish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            functionclick.OnPublishClickEvent(view.getId(), pm);
                        }
                    });
                } else {
                    cHolder.iv_content_publish.setClickable(false);
                }
            }

        }
    }

//    扫码收款——pic_sm.png
//    刷卡收款——pic_sk.png
//    大额收款——pic_desk.png
//    信用卡还款——pic_xyk.png
//    转账汇款——pic_zz.png
//    贷款——pic_dk.png
//    理财——pic_lc.png
//    手机充值——pic_sjcz.png
//    活动专区——pic_hyfl.png
//    用户信息——默认标识--pic
//    购买手机收款宝——pic_gmsjskb.png_news.png
//    设备管理——默认标识--pic_news.png
//    交易规则——默认标识--pic_news.png
//    使用帮助——默认标识--pic_news.png
//    立即提款——默认标识--pic_news.png
//    余额查询——默认标识--pic_news.png
//    一块夺宝——默认标识--pic_news.png
//    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//    新增通知类型
//    D0提款——pic_D0.png
//    商户开通——pic_shkt.png
//    升级——pic_sj.png
//    业务推广——pic_ywtg.png


    /**
     * 系统公告主题
     *
     * @param pm      系统消息
     * @param cHolder 系统消息uiHolder
     */
    private void checkTitleType(final PublishMessage pm, MyComplexViewHolder cHolder) {
        cHolder.tv_time_message.setText(DateUtil.formatDateStr_msgTime(pm.getMsgTime()));
        String titleText = pm.getTitleText();
        String titleImagURL = pm.getTitleImagURL();
        cHolder.fl_title.setVisibility(View.VISIBLE);
//        bitmapUtils.configDefaultLoadingImage(R.drawable.banner_default_small);
        bitmapUtils.configDefaultLoadFailedImage(R.drawable.banner_default_small);
        if (!TextUtils.isEmpty(titleText)) {//图片+文字
            cHolder.rl_title_text.setVisibility(View.VISIBLE);
            cHolder.iv_publish_title.setVisibility(View.GONE);
            cHolder.tv_publish_title.setText(titleText);
            if (TextUtils.isEmpty(titleImagURL)) {
                cHolder.iv_publish_icon.setImageResource(pic_news);
                return;
            }
            try {
                switch (MessageIconType.valueOf(titleImagURL)) {
                    case largeLimitPayment:
                        cHolder.iv_publish_icon.setImageResource(R.drawable.pic_desk);
                        break;
                    case loan:
                        cHolder.iv_publish_icon.setImageResource(R.drawable.pic_dk);
                        break;
                    case buyMobilePhonesOfTreasure:
                        cHolder.iv_publish_icon.setImageResource(pic_gmsjskb);
                        break;
                    case activityArea:
                        cHolder.iv_publish_icon.setImageResource(pic_hyfl);
                        break;
                    case tradePrinciple:
                        cHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case manangeMoneyMaster:
                        cHolder.iv_publish_icon.setImageResource(pic_lc);
                        break;
                    case D0:
                        cHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case scancodePayment:
                        cHolder.iv_publish_icon.setImageResource(R.drawable.pic_sm);
                        break;
                    case deviceManager:
                        cHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case usingHelp:
                        cHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case mobileRecharge:
                        cHolder.iv_publish_icon.setImageResource(R.drawable.pic_sjcz);
                        break;
                    case swipingCardPayment:
                        cHolder.iv_publish_icon.setImageResource(R.drawable.pic_sk);
                        break;
                    case creditCardPayment:
                        cHolder.iv_publish_icon.setImageResource(R.drawable.pic_xyk);
                        break;
                    case aPieceOfTreasure:
                        cHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case userInformation:
                        cHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case balanceQuery:
                        cHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case remittance:
                        cHolder.iv_publish_icon.setImageResource(R.drawable.pic_zz);
                        break;
                    case D0withdraw:
                        cHolder.iv_publish_icon.setImageResource(R.drawable.pic_dzero);
                        break;
                    case merchantOpen:
                        cHolder.iv_publish_icon.setImageResource(pic_shkt);
                        break;
                    case UpgradeBusinessPromotion:
                        cHolder.iv_publish_icon.setImageResource(R.drawable.pic_sjsjsj);
                        break;
                    case businessPromotion:
                        cHolder.iv_publish_icon.setImageResource(pic_ywtg);
                        break;
                    default:
                        cHolder.iv_publish_icon.setImageResource(pic_news);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
                //防止空枚举出现的崩溃现象
                cHolder.iv_publish_icon.setImageResource(pic_news);
            }
        } else {//图片
            cHolder.rl_title_text.setVisibility(View.GONE);
            cHolder.iv_publish_title.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(titleImagURL)) {
                bitmapUtils.display(cHolder.iv_publish_title, titleImagURL);
                cHolder.iv_publish_title.setClickable(true);
                cHolder.iv_publish_title.setLongClickable(true);
            } else {
                cHolder.fl_title.setVisibility(View.GONE);
            }
        }
    }


    /**
     * @param bm      业务消息对象
     * @param eHolder 业务消息uiHolder
     */
    private void fillBusinessData(final BusinessMessage bm, MyEasyViewHolder eHolder) {
        String typeName = bm.getTypeName();
        String status = bm.getStatus();
        eHolder.tv_time.setText(DateUtil.formatDateStr_msgTime(bm.getMsgTime()));
        eHolder.tv_title.setText(bm.getTitleText());
        eHolder.tv_content.setText(bm.getContentText());
        eHolder.tv_function.setText(TextUtils.equals(status, "SUCCESS") ? context.getString(R.string.openbusiness_success) : context.getString(R.string.openbusiness_failure));
        if (!TextUtils.isEmpty(typeName)) {
            try {
                switch (BusinessType.valueOf(typeName)) {
                    case ONE_DAY_LOAN:
                        eHolder.tv_function.setVisibility(View.VISIBLE);
                        eHolder.iv_title.setImageResource(R.drawable.pic_dk);
                        break;
                    case UP_MERCHANT_LEVEL_FIRST:
                    case UP_MERCHANT_LEVEL_SECOND:
                        switch (BusinessType.TypeStatus.valueOf(status)) {
                            case SUCCESS:
                                eHolder.tv_function.setVisibility(View.GONE);
                        }
                        eHolder.iv_title.setImageResource(R.drawable.pic_shsj);
                        break;
                    case D0_APPLAY:
                        eHolder.tv_function.setVisibility(View.VISIBLE);
                        eHolder.iv_title.setImageResource(R.drawable.pic_dzero);
                        break;
                    case Wechat_APPLAY:
                        eHolder.tv_function.setVisibility(View.VISIBLE);
                        eHolder.iv_title.setImageResource(R.drawable.pic_sm);
                        break;
                    case BLimit_APPLAY:
                        eHolder.tv_function.setVisibility(View.VISIBLE);
                        eHolder.iv_title.setImageResource(R.drawable.pic_desk);
                        break;
                    case MERCHANT_APPLAY:
                        switch (BusinessType.TypeStatus.valueOf(status)) {
                            case SUCCESS:
                                eHolder.tv_function.setVisibility(View.GONE);
                        }
                        eHolder.iv_title.setImageResource(pic_shkt);
                        break;
                    case UP_MERINFO_ACCOUNTNO:
                        switch (BusinessType.TypeStatus.valueOf(status)) {
                            case SUCCESS:
                                eHolder.tv_function.setVisibility(View.GONE);
                                break;
                        }
                        eHolder.iv_title.setImageResource(pic_news);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
                eHolder.iv_title.setImageResource(pic_news);
            }
        }
        eHolder.tv_function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionclick.OnBusinessClickEvent(view.getId(), bm);
            }
        });
    }

    /**
     * 填充交易模块
     *
     * @param tm
     * @param eHolder
     */
    private void fillTradeData(final TradeMessage tm, MyEasyViewHolder eHolder) {
        eHolder.rl_trade_status.setVisibility(View.VISIBLE);
        eHolder.fl_trade_function.setVisibility(View.VISIBLE);
        eHolder.rl_special.setVisibility(View.GONE);
        eHolder.tv_payNum.setText("付款卡号");
        eHolder.tv_tradeDep.setText("交易时间");
        eHolder.tv_title.setText(tm.getTitleText());
        eHolder.tv_amount.setText(Util.formatAmount(tm.getAmount()) + "元");
        eHolder.tv_amount.setTextColor(getColor(R.color.amount_green));
        eHolder.tv_trade_card.setText(tm.getPayCardNum());
        eHolder.tv_trade_time.setText(tm.getTradeTime());
        eHolder.tv_time.setText(DateUtil.formatDateStr_msgTime(tm.getMsgTime()));
        eHolder.tv_trade_status.setText(TextUtils.equals(tm.getStatus(), "SUCCESS") ? "交易成功" : "交易成功");
        eHolder.tv_trade_status.setTextColor(getColor(R.color.amount_green));
        eHolder.tv_function.setText("查看详情");
        String typeName = tm.getTypeName();
//        eHolder.rl_collectionNum.setVisibility(TextUtils.equals(typeName, "W00008") ? View.GONE : View.VISIBLE);
        if (!TextUtils.isEmpty(typeName)) {
            switch (typeName) {
                case "18X"://刷卡收款
                    eHolder.tv_amount.setText("+" + Util.formatAmount(tm.getAmount()) + "元");
                    eHolder.iv_title.setImageResource(R.drawable.pic_sk);
                    break;
                case "18Y"://刷卡撤销
                    eHolder.tv_amount.setText("-" + Util.formatAmount(tm.getAmount()) + "元");
                    eHolder.tv_amount.setTextColor(getColor(R.color.amount_green));
                    eHolder.iv_title.setImageResource(R.drawable.pic_sk);
                    break;
                case "W00008"://扫码收款
                    eHolder.tv_amount.setText("+" + Util.formatAmount(tm.getAmount()) + "元");
                    eHolder.tv_payNum.setText("支付渠道");
                    eHolder.tv_trade_card.setText("微信支付");
                    eHolder.iv_title.setImageResource(R.drawable.pic_sm);
                    break;
                case "M90000"://信用卡还款
                    eHolder.rl_special.setVisibility(View.VISIBLE);
                    eHolder.tv_special.setText("还款卡号");
                    eHolder.tv_trade_card_special.setText(tm.getReceiveCardNum());
                    eHolder.iv_title.setImageResource(R.drawable.pic_xyk);
                    break;
                case "M80001"://转账汇款
                    eHolder.rl_special.setVisibility(View.VISIBLE);
                    eHolder.tv_special.setText("收款卡号");
                    eHolder.tv_trade_card_special.setText(tm.getReceiveCardNum());
                    eHolder.iv_title.setImageResource(R.drawable.pic_zz);
                    break;
                case "M20020"://手机充值
                    eHolder.rl_special.setVisibility(View.VISIBLE);
                    eHolder.tv_special.setText("充值手机号");
                    eHolder.tv_trade_card_special.setText(StringUtil.formatPhoneN3S4N4(tm.getMobileNum()));
                    eHolder.iv_title.setImageResource(R.drawable.pic_sjcz);
                    break;
                case "T0_CASH":
                    eHolder.rl_trade_status.setVisibility(View.GONE);
                    eHolder.fl_trade_function.setVisibility(View.GONE);
                    eHolder.tv_payNum.setText("结算账号");
                    eHolder.tv_tradeDep.setText("划款时间");
                    eHolder.iv_title.setImageResource(R.drawable.pic_dzero);
                    break;
                default:
                    eHolder.iv_title.setImageResource(R.drawable.pic_news);
                    break;
            }

        }
        eHolder.tv_function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionclick.OnTradeClickEvent(view.getId(), tm);
            }
        });
    }


    private class MyEasyViewHolder {
        TextView tv_time, tv_title, tv_content, tv_function, tv_amount, tv_trade_status, tv_trade_time, tv_trade_card, tv_tradeDep;
        LinearLayout ll_trade;
        ImageView iv_title;
        RelativeLayout rl_collectionNum, rl_container, rl_trade_status;
        FrameLayout fl_trade_function;

        TextView tv_payNum, tv_publish_title, tv_content_publish, tv_trade_card_special, tv_function_left, tv_function_right, tv_function_middle, tv_content_bottom, tv_special;
        ImageView iv_publish_title, iv_content_publish;
        CornerImageView iv_publish_icon;
        RelativeLayout rl_title_text, rl_container_bp, rl_special;
        FrameLayout fl_title, fl_content_bp, fl_function;
        View vdivider_content;

        public MyEasyViewHolder(View convertView) {
            rl_collectionNum = (RelativeLayout) convertView.findViewById(R.id.rl_collectionNum);
            rl_trade_status = (RelativeLayout) convertView.findViewById(R.id.rl_trade_status);
            fl_trade_function = (FrameLayout) convertView.findViewById(R.id.fl_trade_function);
            rl_container = (RelativeLayout) convertView.findViewById(R.id.rl_container);
            rl_special = (RelativeLayout) convertView.findViewById(R.id.rl_special);
            tv_trade_card_special = (TextView) convertView.findViewById(R.id.tv_trade_card_special);
            tv_tradeDep = (TextView) convertView.findViewById(R.id.tv_tradeDep);
            iv_title = (ImageView) convertView.findViewById(R.id.iv_title);
            tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            tv_special = (TextView) convertView.findViewById(R.id.tv_special);
            tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            tv_function = (TextView) convertView.findViewById(R.id.tv_function);
            tv_amount = (TextView) convertView.findViewById(R.id.tv_amount);
            ll_trade = (LinearLayout) convertView.findViewById(R.id.ll_trade);
            tv_trade_status = (TextView) convertView.findViewById(R.id.tv_trade_status);
            tv_trade_time = (TextView) convertView.findViewById(R.id.tv_trade_time);
            tv_trade_card = (TextView) convertView.findViewById(R.id.tv_trade_card);
            tv_payNum = (TextView) convertView.findViewById(R.id.tv_payNum);


            vdivider_content = convertView.findViewById(R.id.view_seprator_content);
            rl_title_text = (RelativeLayout) convertView.findViewById(R.id.rl_title_text);
            rl_container_bp = (RelativeLayout) convertView.findViewById(R.id.rl_container_bp);
            tv_publish_title = (TextView) convertView.findViewById(R.id.tv_publish_title);
            tv_content_publish = (TextView) convertView.findViewById(R.id.tv_content_publish);
            tv_function_left = (TextView) convertView.findViewById(R.id.tv_function_left);
            tv_function_right = (TextView) convertView.findViewById(R.id.tv_function_right);
            tv_function_middle = (TextView) convertView.findViewById(R.id.tv_function_middle);
            iv_publish_title = (ImageView) convertView.findViewById(R.id.iv_publish_title);
            iv_publish_icon = (CornerImageView) convertView.findViewById(R.id.iv_publish_icon);
            iv_content_publish = (ImageView) convertView.findViewById(R.id.iv_content_publish);
            tv_content_bottom = (TextView) convertView.findViewById(R.id.tv_content_bottom);
            fl_title = (FrameLayout) convertView.findViewById(R.id.fl_title);
            fl_content_bp = (FrameLayout) convertView.findViewById(R.id.fl_content_bp);
            fl_function = (FrameLayout) convertView.findViewById(R.id.fl_function);
        }

    }

    public int getColor(int ids) {
        return context.getResources().getColor(ids);
    }

    private class MyComplexViewHolder {
        TextView tv_publish_title, tv_content_publish, tv_function_left, tv_function_right, tv_function_middle, tv_content_bottom, tv_time_message;
        ImageView iv_publish_title, iv_content_publish;
        CornerImageView iv_publish_icon;
        RelativeLayout rl_title_text, rl_container;
        FrameLayout fl_title, fl_content, fl_function;
        View vdivider_content;

        public MyComplexViewHolder(View convertView) {
            vdivider_content = convertView.findViewById(R.id.view_seprator_content);
            rl_title_text = (RelativeLayout) convertView.findViewById(R.id.rl_title_text);
            rl_container = (RelativeLayout) convertView.findViewById(R.id.rl_container);
            tv_time_message = (TextView) convertView.findViewById(R.id.tv_time_message);
            tv_publish_title = (TextView) convertView.findViewById(R.id.tv_publish_title);
            tv_content_publish = (TextView) convertView.findViewById(R.id.tv_content_publish);
            tv_function_left = (TextView) convertView.findViewById(R.id.tv_function_left);
            tv_function_right = (TextView) convertView.findViewById(R.id.tv_function_right);
            tv_function_middle = (TextView) convertView.findViewById(R.id.tv_function_middle);
            iv_publish_title = (ImageView) convertView.findViewById(R.id.iv_publish_title);
            iv_publish_icon = (CornerImageView) convertView.findViewById(R.id.iv_publish_icon);
            iv_content_publish = (ImageView) convertView.findViewById(R.id.iv_content_publish);
            tv_content_bottom = (TextView) convertView.findViewById(R.id.tv_content_bottom);
            fl_title = (FrameLayout) convertView.findViewById(R.id.fl_title);
            fl_content = (FrameLayout) convertView.findViewById(R.id.fl_content);
            fl_function = (FrameLayout) convertView.findViewById(R.id.fl_function);
        }
    }

    public interface OnFunctionBtnClickListener {
        void OnTradeClickEvent(int ids, TradeMessage message);

        void OnBusinessClickEvent(int ids, BusinessMessage message);

        void OnPublishClickEvent(int ids, PublishMessage message);

        void OnPublishClickEvent1(int ids, PublishMessage message, String url);

        void OnPublishClickEvent2(int ids, PublishMessage message, String url);

        void OnPublishClickEvent3(int ids);


        //业务系统回调
        void OnPublishBusinessClickEvent(int ids, BusinessMessage message);

        void OnPublishBusinessClickEvent1(int ids, BusinessMessage message, String url);

        void OnPublishBusinessClickEvent2(int ids, BusinessMessage message, String url);

        void OnPublishBusinessClickEvent3(int ids);


    }
}

