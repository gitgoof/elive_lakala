package com.lakala.shoudan.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.DateUtil;
import com.lakala.platform.common.UILUtils;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.messagecenter.messageBean.MessageIconType;
import com.lakala.shoudan.component.SpannableBuilder;
import com.lakala.shoudan.datadefine.AdBottomMessage;
import com.lakala.ui.component.CornerImageView;

import java.util.List;

import static com.lakala.shoudan.R.drawable.pic_gmsjskb;
import static com.lakala.shoudan.R.drawable.pic_hyfl;
import static com.lakala.shoudan.R.drawable.pic_lc;
import static com.lakala.shoudan.R.drawable.pic_news;
import static com.lakala.shoudan.R.drawable.pic_shkt;
import static com.lakala.shoudan.R.drawable.pic_ywtg;

/**
 * Created by huwei on 2017/3/1.
 */

public class AdBottomAdapter extends BaseAdapter {
    private Context context;
    private List<AdBottomMessage> mList;
    private LayoutInflater inflater;
    private AdBottomFunctionClickListener functionclick;

    public AdBottomAdapter(Context context, List<AdBottomMessage> list) {
        this.context = context;
        this.mList = list;
        this.inflater = LayoutInflater.from(context);
        UILUtils.init(context);
    }

    public void setFunctionClickListener(AdBottomFunctionClickListener fc) {
        this.functionclick = fc;
    }

    @Override
    public int getCount() {
        return mList.size() > 0 ? mList.size() : 3;
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MyComplexViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_ad_bottom, viewGroup, false);
            holder = new MyComplexViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MyComplexViewHolder) convertView.getTag();
        }
        if (i == 0) {
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            params.setMargins(0, 20, 0, 20);
            holder.tv_content_publish.setLayoutParams(params);
            Spannable ss = SpannableBuilder.create(context).append("恭喜您,您的考拉信用已达\n", R.dimen.font_size_15, R.color.gray_666666)
                    .append("         ", R.dimen.font_size_15, R.color.gray_666666).append("888", R.dimen.font_size_22, R.color.gray_666666)
                    .append("分", R.dimen.font_size_15, R.color.gray_666666).build();
            holder.tv_content_publish.setText(ss);
        }
        AdBottomMessage message = (AdBottomMessage) getItem(i);
        fillPublishData(message, holder);
        return convertView;
    }

    private void fillPublishData(AdBottomMessage message, MyComplexViewHolder holder) {
        checkTitleType(message, holder);
        checkContentType(message, holder);
        checkFunctionType(message, holder);
    }


    /**
     * @param am     系统消息
     * @param holder 系统消息uiHolder
     */
    private void checkFunctionType(final AdBottomMessage am, MyComplexViewHolder holder) {
        holder.fl_function.setVisibility(View.VISIBLE);
        holder.tv_function_middle.setVisibility(View.VISIBLE);
        holder.tv_function_middle.setText("版本升级");
        holder.tv_function_right.setText("立即体验");
        String versionUpdateFlag = am.getVersionUpdateFlag();
        String serviceActionURL = am.getServiceActionURL();
        String detailsClickURL = am.getDetailsClickURL();
        String detailsLabVal = am.getDetailsLabVal();
        boolean versionUpdate = Boolean.parseBoolean(versionUpdateFlag);
        boolean isServiceNull = TextUtils.isEmpty(serviceActionURL);
        boolean isDetailNull = TextUtils.isEmpty(detailsClickURL);
        detailsLabVal = TextUtils.isEmpty(detailsLabVal) ? "查看详情" : detailsLabVal;
        holder.tv_function_left.setText(detailsLabVal);
        if (versionUpdate) {//版本升级不为空
            holder.tv_function_left.setText(detailsLabVal);
            holder.tv_function_middle.setText("版本升级");
            holder.tv_function_left.setVisibility(!isDetailNull ? View.VISIBLE : View.GONE);
            holder.tv_function_right.setVisibility(!isServiceNull ? View.VISIBLE : View.GONE);
            if (!isDetailNull) {//处理版本升级为右侧
                holder.tv_function_middle.setVisibility(View.GONE);
                holder.tv_function_right.setVisibility(View.VISIBLE);
                holder.tv_function_right.setText("版本升级");
                holder.tv_function_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        functionclick.OnAdFuntionClick3(view.getId());
                    }
                });
            } else {
                holder.tv_function_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        functionclick.OnAdFuntionClick(view.getId(), am);
                    }
                });
            }
            holder.tv_function_middle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    functionclick.OnAdFuntionClick(view.getId(), am);
                }
            });
        } else {//无版本升级选项
            if (!isDetailNull && !isServiceNull) {
                holder.tv_function_middle.setVisibility(View.GONE);
                holder.tv_function_left.setVisibility(View.VISIBLE);
                holder.tv_function_right.setVisibility(View.VISIBLE);
            } else if (!isDetailNull && isServiceNull) {//左侧功能键居中
                holder.tv_function_left.setVisibility(View.GONE);
                holder.tv_function_right.setVisibility(View.GONE);
                holder.tv_function_middle.setText(detailsLabVal);
                holder.tv_function_middle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        functionclick.OnAdFuntionClick2(view.getId(), am, am.getDetailsClickURL());
                    }
                });
            } else if (isDetailNull && !isServiceNull) {//右侧功能键居中
                holder.tv_function_left.setVisibility(View.GONE);
                holder.tv_function_right.setVisibility(View.GONE);
                holder.tv_function_middle.setText("立即体验");
                holder.tv_function_middle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        functionclick.OnAdFuntionClick1(view.getId(), am, am.getServiceActionURL());
                    }
                });
            } else {
                holder.fl_function.setVisibility(View.GONE);
            }
            holder.tv_function_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    functionclick.OnAdFuntionClick(view.getId(), am);
                }
            });
        }
        holder.tv_function_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionclick.OnAdFuntionClick(view.getId(), am);
            }
        });
    }


    /**
     * @param am     系统消息
     * @param holder 系统消息uiHolder
     */
    private void checkContentType(final AdBottomMessage am, MyComplexViewHolder holder) {
        holder.fl_content.setVisibility(View.VISIBLE);
        String contentClickURL = am.getContentClickURL(); //url_html
        String contentImageURL = am.getContentImageURL(); //url_picture
        String detailsClickURL = am.getDetailsClickURL(); //查看详情url，与图片点击url为统一入口
        String contentText = am.getContentText();
//        bitmapUtils.configDefaultLoadingImage(R.drawable.banner_default_big);
//        bitmapUtils.configDefaultLoadFailedImage(R.drawable.banner_default_big);
        holder.iv_content_publish.setClickable(true);
        if (!TextUtils.isEmpty(contentText)) {//文字
            if (!TextUtils.isEmpty(contentImageURL)) {//+图片
                holder.tv_content_publish.setVisibility(View.GONE);
                holder.vdivider_content.setVisibility(View.VISIBLE);
                holder.tv_content_bottom.setVisibility(View.VISIBLE);
                holder.tv_content_bottom.setText(contentText);
                holder.iv_content_publish.setVisibility(View.VISIBLE);
                UILUtils.display(contentImageURL, holder.iv_content_publish);
                holder.iv_content_publish.setLongClickable(true);
                if (!TextUtils.isEmpty(detailsClickURL)) {
                    holder.iv_content_publish.setClickable(true);
                    holder.iv_content_publish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            functionclick.OnAdFuntionClick(view.getId(), am);
                        }
                    });
                } else {
                    holder.iv_content_publish.setClickable(false);
                }
            } else {
                holder.tv_content_publish.setVisibility(View.VISIBLE);
                holder.tv_content_publish.setText(contentText);
                holder.vdivider_content.setVisibility(View.GONE);
                holder.tv_content_bottom.setVisibility(View.GONE);
                holder.iv_content_publish.setVisibility(View.GONE);
            }
        } else {//没有文字
            holder.tv_content_bottom.setVisibility(View.GONE);
            holder.vdivider_content.setVisibility(View.GONE);
            holder.tv_content_publish.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(contentImageURL)) {//有图片
                holder.iv_content_publish.setVisibility(View.VISIBLE);
                UILUtils.display(contentImageURL, holder.iv_content_publish);
                holder.iv_content_publish.setLongClickable(true);
                if (!TextUtils.isEmpty(detailsClickURL)) {
                    holder.iv_content_publish.setClickable(true);
                    holder.iv_content_publish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            functionclick.OnAdFuntionClick(view.getId(), am);
                        }
                    });
                } else {
                    holder.iv_content_publish.setClickable(false);
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
     * @param am     系统消息
     * @param holder 系统消息uiHolder
     */
    private void checkTitleType(final AdBottomMessage am, MyComplexViewHolder holder) {
        String titleText = am.getTitleText();
        String titleImagURL = am.getTitleImagURL();
        holder.fl_title.setVisibility(View.VISIBLE);
//        bitmapUtils.configDefaultLoadingImage(R.drawable.banner_default_small);
//        bitmapUtils.configDefaultLoadFailedImage(R.drawable.banner_default_small);
        if (!TextUtils.isEmpty(titleText)) {//图片+文字
            holder.rl_title_text.setVisibility(View.VISIBLE);
            holder.iv_publish_title.setVisibility(View.GONE);
            holder.tv_publish_title.setText(titleText);
            if (TextUtils.isEmpty(titleImagURL)) {
                holder.iv_publish_icon.setImageResource(pic_news);
                return;
            }
            try {
                switch (MessageIconType.valueOf(titleImagURL)) {
                    case largeLimitPayment:
                        holder.iv_publish_icon.setImageResource(R.drawable.pic_desk);
                        break;
                    case loan:
                        holder.iv_publish_icon.setImageResource(R.drawable.pic_dk);
                        break;
                    case buyMobilePhonesOfTreasure:
                        holder.iv_publish_icon.setImageResource(pic_gmsjskb);
                        break;
                    case activityArea:
                        holder.iv_publish_icon.setImageResource(pic_hyfl);
                        break;
                    case tradePrinciple:
                        holder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case manangeMoneyMaster:
                        holder.iv_publish_icon.setImageResource(pic_lc);
                        break;
                    case D0:
                        holder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case scancodePayment:
                        holder.iv_publish_icon.setImageResource(R.drawable.pic_sm);
                        break;
                    case deviceManager:
                        holder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case usingHelp:
                        holder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case mobileRecharge:
                        holder.iv_publish_icon.setImageResource(R.drawable.pic_sjcz);
                        break;
                    case swipingCardPayment:
                        holder.iv_publish_icon.setImageResource(R.drawable.pic_sk);
                        break;
                    case creditCardPayment:
                        holder.iv_publish_icon.setImageResource(R.drawable.pic_xyk);
                        break;
                    case aPieceOfTreasure:
                        holder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case userInformation:
                        holder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case balanceQuery:
                        holder.iv_publish_icon.setImageResource(pic_news);
                        break;
                    case remittance:
                        holder.iv_publish_icon.setImageResource(R.drawable.pic_zz);
                        break;
                    case D0withdraw:
                        holder.iv_publish_icon.setImageResource(R.drawable.pic_dzero);
                        break;
                    case merchantOpen:
                        holder.iv_publish_icon.setImageResource(pic_shkt);
                        break;
                    case UpgradeBusinessPromotion:
                        holder.iv_publish_icon.setImageResource(R.drawable.pic_sjsjsj);
                        break;
                    case businessPromotion:
                        holder.iv_publish_icon.setImageResource(pic_ywtg);
                        break;
                    default:
                        holder.iv_publish_icon.setImageResource(pic_news);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
                //防止空枚举出现的崩溃现象
                holder.iv_publish_icon.setImageResource(pic_news);
            }
        } else {//图片
            holder.rl_title_text.setVisibility(View.GONE);
            holder.iv_publish_title.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(titleImagURL)) {
                UILUtils.display(titleImagURL, holder.iv_publish_title);
                holder.iv_publish_title.setClickable(true);
                holder.iv_publish_title.setLongClickable(true);
            } else {
                holder.fl_title.setVisibility(View.GONE);
            }
        }
    }

    private class MyComplexViewHolder {
        TextView tv_publish_title, tv_content_publish, tv_function_left, tv_function_right, tv_function_middle, tv_content_bottom;
        ImageView iv_publish_title, iv_content_publish;
        CornerImageView iv_publish_icon;
        RelativeLayout rl_title_text, rl_container;
        FrameLayout fl_title, fl_content, fl_function;
        View vdivider_content;

        public MyComplexViewHolder(View convertView) {
            vdivider_content = convertView.findViewById(R.id.view_seprator_content);
            rl_title_text = (RelativeLayout) convertView.findViewById(R.id.rl_title_text);
            rl_container = (RelativeLayout) convertView.findViewById(R.id.rl_container);
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

    public interface AdBottomFunctionClickListener {
        void OnAdFuntionClick(int ids, AdBottomMessage message);

        void OnAdFuntionClick1(int ids, AdBottomMessage message, String url);

        void OnAdFuntionClick2(int ids, AdBottomMessage message, String url);

        void OnAdFuntionClick3(int ids);
    }

    public void addAll(List<? extends Object> list) {
        mList = (List<AdBottomMessage>) list;
        notifyDataSetChanged();
    }
}
