package com.lakala.shoudan.component;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lakala.platform.activity.WeiBoFilterRespActivity;
import com.lakala.shoudan.R;
import com.lakala.platform.statistic.ShoudanStatisticManager;

/**
 * Created by HJP on 2015/8/26.
 * 自定义分享弹出框
 */
public class SharePopupWindow extends PopupWindow implements View.OnClickListener {
    public  static final int SHARE_CODE = 0x909;
    private String url;
    private String title;
    private Activity context;
    private TextView tvCancel;
    private ShareBtnItem item = null;
    private View contentView;
    private View view;
    private ImageButton ibWechatFriend;
    private ImageButton ibWechatMoment;
    private ImageButton ibWeibo;
    private ImageButton ibQQ;
    private String shareType = "";
    private int from=0;//从哪点击分享

    /**
     *
     * @param shareType SHARE_TYPE_ADV,SHARE_TYPE_PROMOTION
     * @return
     */
    public SharePopupWindow setShareType(String shareType) {
        this.shareType = shareType;
        return this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.ib_wechat_friend:
                item = ShareBtnItem.wechatfriend;
                this.dismiss();
                Intent intent = new Intent(context, WeiBoFilterRespActivity.class);
                intent.putExtra("url", url).putExtra("title", title);
                intent.putExtra("type", "wechat_friend");
                intent.putExtra("from",from);
                context.startActivityForResult(intent, SHARE_CODE);
                break;

            case R.id.ib_wechat_moment:
                item = ShareBtnItem.wechatmoment;
                this.dismiss();

                Intent intent1 = new Intent(context, WeiBoFilterRespActivity.class);
                intent1.putExtra("url", url).putExtra("title", title);
                intent1.putExtra("type", "wechat_moment");
                intent1.putExtra("from",from);
                context.startActivityForResult(intent1, SHARE_CODE);
                break;

            case R.id.ib_weibo:
                item = ShareBtnItem.weibo;
                this.dismiss();
                Intent intent2 = new Intent(context, WeiBoFilterRespActivity.class);
                intent2.putExtra("url", url).putExtra("title", title);
                intent2.putExtra("type", "weibo");
                intent2.putExtra("from",from);
                context.startActivityForResult(intent2, SHARE_CODE);
                break;
            case R.id.ib_qq:
                item = ShareBtnItem.qq;
                this.dismiss();
                Intent intent3 = new Intent(context, WeiBoFilterRespActivity.class);
                intent3.putExtra("url", url).putExtra("title", title);
                intent3.putExtra("type", "qq");
                intent3.putExtra("from",from);
                context.startActivityForResult(intent3, SHARE_CODE);
                break;
            case R.id.tv_cancel:
                this.dismiss();
                break;
        }
    }

    public enum ShareBtnItem {
        wechatfriend,//微信好友
        wechatmoment,//朋友圈
        weibo,//微博
        qq;//qq

    }

    public SharePopupWindow(Activity context, View view, String url, String title) {
        this.context=context;
        this.url=url;
        this.title=title;
        this.view = view;
        contentView = LayoutInflater.from(context).inflate(R.layout.share_popupwindow, null);
        this.setContentView(contentView);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setFocusable(true);
        this.setAnimationStyle(R.style.more_popup_anim_style);
        contentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                }
                return false;
            }
        });


        ibWechatFriend = (ImageButton) contentView.findViewById(R.id.ib_wechat_friend);
        ibWechatMoment = (ImageButton) contentView.findViewById(R.id.ib_wechat_moment);
        ibWeibo = (ImageButton) contentView.findViewById(R.id.ib_weibo);
        ibQQ = (ImageButton) contentView.findViewById(R.id.ib_qq);
        tvCancel = (TextView) contentView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(this);
        ibWechatFriend.setOnClickListener(this);
        ibWechatMoment.setOnClickListener(this);
        ibWeibo.setOnClickListener(this);
        ibQQ.setOnClickListener(this);

    }
    //新增from，表明来源。默认为0：其他  1：考拉分享
    public SharePopupWindow(Activity context, View view, String url, String title,int from) {
        this.from=from;
        this.context=context;
        this.url=url;
        this.title=title;
        this.view = view;
        contentView = LayoutInflater.from(context).inflate(R.layout.share_popupwindow, null);
        this.setContentView(contentView);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setFocusable(true);
        this.setAnimationStyle(R.style.more_popup_anim_style);
        contentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                }
                return false;
            }
        });


        ibWechatFriend = (ImageButton) contentView.findViewById(R.id.ib_wechat_friend);
        ibWechatMoment = (ImageButton) contentView.findViewById(R.id.ib_wechat_moment);
        ibWeibo = (ImageButton) contentView.findViewById(R.id.ib_weibo);
        ibQQ = (ImageButton) contentView.findViewById(R.id.ib_qq);
        tvCancel = (TextView) contentView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(this);
        ibWechatFriend.setOnClickListener(this);
        ibWechatMoment.setOnClickListener(this);
        ibWeibo.setOnClickListener(this);
        ibQQ.setOnClickListener(this);

    }
    public ShareBtnItem getItem() {
        return item;
    }

    public void showSharePopupWindow() {
        if (!this.isShowing()) {
            this.showAtLocation(view, Gravity.BOTTOM | Gravity
                    .CENTER_HORIZONTAL, 0, 0);
        } else {
            this.dismiss();
        }
    }
}
