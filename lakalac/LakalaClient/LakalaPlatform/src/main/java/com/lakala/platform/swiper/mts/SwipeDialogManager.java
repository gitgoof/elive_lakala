package com.lakala.platform.swiper.mts;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.library.util.StringUtil;
import com.lakala.platform.R;
import com.lakala.ui.common.GifMovieView;
import com.lakala.ui.dialog.mts.AlertDialog;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangchao on 14-2-12.
 * 刷卡器显示GIF动画dialog管理
 */
public class SwipeDialogManager implements View.OnClickListener {
    /**
     * 点击类型
     */
    public enum ClickMode {
        LEFT,       //left button,当对话框为一个按钮时候，也用这个mode
        RIGHT,      //right button
        LEFT_TEXT,  //内容左文本
        RIGHT_TEXT, //内容右文本
        KEYBOARD
    }

    /**
     * 当前模式
     */
    public enum CurrentMode {
        INSERT,         //提示连接刷卡器
        SWIPE,          //刷卡状态
        INSERT_IC_CARD, //插入IC卡状态
        STOP,           //停止刷卡状态
        INPUT_PIN,      //输入pin
        INSERT_CREDIT   //信用卡提示连接刷卡器模式
    }

    //插入刷卡器动画
    private static final int INSERT_SWIPE = R.drawable.flash_insert_card_reader;
    //刷卡动画
    private static final int PLEASE_SWIPE = R.drawable.flash_swiping_card;
    //插入ic卡动画
    private static final int INSERT_IC_CARD = R.drawable.flash_insert_ic_card;
    //刷卡消息
    private static final int MSG_SWIPE = 1;
    //插卡消息
    private static final int MSG_INSERT_IC_CARD = 2;


    //gif
    private GifMovieView gifView;
    //click监听
    private SwipeDialogClickListener listener;
    //当前GifMode
    private CurrentMode currentMode;
    //dialog控制器
    private DialogController dialogController;
    //显示的View
    private View view;
    //activity
    private FragmentActivity activity;
    //Gif动画容器
    private LinearLayout contentView;
    //右下角text
    private TextView leftText, rightText;
    //停止刷卡动画图片
    private ImageView stop_img;
    //top 显示的文字
    private TextView topText;
    //
    private Timer timer;

    /*                  动画切换                    */
    final private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SWIPE:
                    setContent(CurrentMode.SWIPE, "", "使用说明");
                    break;
                case MSG_INSERT_IC_CARD:
                    setContent(CurrentMode.INSERT_IC_CARD, "", "使用说明");
                    break;
            }
        }
    };

    public SwipeDialogManager(SwipeDialogClickListener listener) {
        initDialog(listener);
    }

    /**
     * 初始化
     */
    private void initDialog(SwipeDialogClickListener listener) {
        dialogController = DialogController.getInstance();
        this.listener    = listener;
        activity = (FragmentActivity) listener.getActivity();
    }

    /**
     * 初始化显示View
     */
    private boolean initView() {
        if(activity == null) return false;

        view = View.inflate(activity, R.layout.plat_view_insertswipe, null);

        contentView = (LinearLayout) view.findViewById(R.id.gif_view_ll);
        rightText   = (TextView)     view.findViewById(R.id.right_swipe_txt);
        leftText    = (TextView)     view.findViewById(R.id.left_swipe_txt);
        stop_img    = (ImageView)    view.findViewById(R.id.stop_img);
        topText     = (TextView)     view.findViewById(R.id.topText);

        leftText.setOnClickListener(this);
        rightText.setOnClickListener(this);

        return true;
    }

    /**
     * 获取字符串
     *
     * @param id
     * @return
     */
    private String getString(int id){
        if(activity == null) return "";
        return activity.getResources().getString(id);
    }

    /**
     * 显示提示插入刷卡器动画dialog
     */
    public void showInsertSwipeDialog(JSONObject business) {
        if(!initView()) return;
        if(dialogController == null)return;
        stopSwitchGif();

        if (business == null){
            setContent(CurrentMode.INSERT, "使用说明", "");
        }else {
            final String busId = business.optString("busId");
            if (busId.equals("creditguide")){
                setContent(CurrentMode.INSERT_CREDIT, "了解无卡还款", "常见问题");
            }
        }

        String right = "使用蓝牙刷卡器";

        dialogController.showAlertDialog(activity,
                0,
                "请将刷卡器插入手机的耳机孔",
                view,
                "取消",
                right,
                "",
                new AlertDialog.Builder.AlertDialogClickListener() {
            @Override
            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                switch (typeEnum) {
                    case LEFT_BUTTON:
                        alertDialog.dismiss();
                        listener.onClick(ClickMode.LEFT, CurrentMode.INSERT);
                        break;
                    case RIGHT_BUTTON:
                        listener.onClick(ClickMode.RIGHT, CurrentMode.INSERT);
                        break;
                }
            }
        }, false);
    }

    /**
     * 显示刷卡和插卡交换提示对话框
     */
    public void showSwipeDialog() {
        if(!initView()) return;
        if(dialogController == null)return;
        stopSwitchGif();

        setContent(CurrentMode.INSERT_IC_CARD, "", "使用说明");

        dialogController.showAlertDialog(activity,
                0,
                "请刷卡或插入IC卡",
                view,
                "取消",
                "重新刷卡",
                "", new AlertDialog.Builder.AlertDialogClickListener() {
            @Override
            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                switch (typeEnum) {
                    case LEFT_BUTTON:
                        alertDialog.dismiss();
                        listener.onClick(ClickMode.LEFT, CurrentMode.SWIPE);
                        break;
                    case RIGHT_BUTTON:
                        listener.onClick(ClickMode.RIGHT, CurrentMode.STOP);
                        break;
                }
            }
        }, false);

        //设置右面的按钮不可点击
        setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, false);

        startSwitchGif();

    }

    /**
     * 仅显示刷卡动画对话框
     */
    public void showOnlySwipeDialog() {
        if(!initView()) return;
        if(dialogController == null)return;

        setContent(CurrentMode.SWIPE, "", "使用说明");

        dialogController.showAlertDialog(activity,
                0,
                "请刷磁条卡",
                view,
                "取消",
                "重新刷卡",
                "", new AlertDialog.Builder.AlertDialogClickListener() {
                    @Override
                    public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                        switch (typeEnum) {
                            case LEFT_BUTTON:
                                alertDialog.dismiss();
                                listener.onClick(ClickMode.LEFT, CurrentMode.SWIPE);
                                break;
                            case RIGHT_BUTTON:
                                listener.onClick(ClickMode.RIGHT, CurrentMode.STOP);
                                break;
                        }
                    }
                }, false);

        //设置右面的按钮不可点击
        setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, false);
    }


    /**
     * 显示使用刷卡器硬键盘提示
     * static
     */
    public void showKeyBoardHintDialog (){
        if(activity == null) return;
        stopSwitchGif();

        DialogController.getInstance().showAlertDialog(activity,
                "请输入密码",
                "请在设备终端上输入银行卡密码，输入完成后按”确认“按钮完成支付",
                "取消",
                new AlertDialog.Builder.AlertDialogClickListener() {
            @Override
            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                listener.onClick(ClickMode.LEFT, CurrentMode.INPUT_PIN);
            }
        });
    }

    /**
     * 显示输入pin超时对话框
     */
    public void showInputPinTimeoutDialog(){
        if(activity == null) return;
        startSwitchGif();

        DialogController.getInstance().showAlertDialog(activity,
                "等待超时",
                "输入密码超时，请重新刷卡并在1分钟内输入密码",
                "取消",
                "重新刷卡",
                new AlertDialog.Builder.AlertDialogClickListener() {
                    @Override
                    public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                        alertDialog.dismiss();
                        switch (typeEnum){
                            case LEFT_BUTTON:
                                listener.onClick(ClickMode.LEFT, CurrentMode.SWIPE);
                                break;
                            case RIGHT_BUTTON:
                                listener.onClick(ClickMode.RIGHT, CurrentMode.STOP);
                                break;
                        }
                    }
                }
                );
    }

    /**
     * 显示非蓝牙异常对话框
     */
    public void showNormalErrorDialog(){
        DialogController.getInstance().showAlertDialog(activity,
                "",
                "刷卡器加载失败",
                "取消",
                "重试",
                new AlertDialog.Builder.AlertDialogClickListener() {
                    @Override
                    public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                        alertDialog.dismiss();
                        switch (typeEnum) {
                            case LEFT_BUTTON:
                                listener.onClick(ClickMode.LEFT, CurrentMode.SWIPE);
                                break;
                            case RIGHT_BUTTON:
                                listener.onClick(ClickMode.RIGHT, CurrentMode.STOP);
                                break;
                        }
                    }
                }
        );
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        if(dialogController == null)return;
        dialogController.setTitle(title);
    }

    /**
     * 设置StopSwipe图片
     */
    public void setStopImage(boolean b) {
        if(contentView == null || stop_img == null) return;
        contentView.setVisibility(b ? View.GONE : View.VISIBLE);
        stop_img.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置button状态
     */
    public void setButtonVisibility(AlertDialog.Builder.ButtonTypeEnum typeEnum
                                    ,int visibility){
        if(dialogController == null)return;
        dialogController.setButtonVisable(typeEnum, visibility);
    }

    /**
     * 设置button text
     *
     * @param typeEnum
     * @param text
     */
    public void setButtonText(AlertDialog.Builder.ButtonTypeEnum typeEnum
            ,String text){
        if(dialogController == null)return;
        dialogController.setButtonText(typeEnum, text);
    }

    /**
     * 设置button是否可用
     *
     * @param typeEnum
     * @param enable
     */
    public void setButtonEnable(AlertDialog.Builder.ButtonTypeEnum typeEnum, boolean enable){
        if(dialogController == null) return;
        dialogController.setButtonEnable(typeEnum, enable);
    }

    /**
     * 设置内容
     *
     * @param mode     gif类型
     * @param leftTxt  左下角文字
     * @param rightTxt 右下角显示文字
     */
    public void setContent(CurrentMode mode, String leftTxt, String rightTxt) {
        currentMode = mode;

        //如果停止刷卡，就取消计时器
        if (mode == CurrentMode.STOP || mode == CurrentMode.INSERT){
            stopSwitchGif();
        }

        if(mode == CurrentMode.STOP) {
            //设置显示对话框右按钮
//            setButtonVisibility(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, View.VISIBLE);

            setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, true);

            //设置对话框右按钮的文字
            setButtonText(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON,
                    "重新刷卡");
        }

        //crash #1499 如果为空，则不进行后续处理，可能是对话框已经消失后，触发了超时的回调
        if (topText == null){
            return;
        }

        if (mode == CurrentMode.INSERT_CREDIT){
            topText.setText("使用无卡支付，不用刷卡也能还款噢！");
            topText.setVisibility(View.VISIBLE);
        }else {
            topText.setVisibility(View.GONE);
        }

        setStopImage(mode == CurrentMode.STOP);

        leftText.setVisibility(StringUtil.isEmpty(leftTxt) ? View.GONE : View.VISIBLE);
        leftText.setText(leftTxt);
        rightText.setVisibility(StringUtil.isEmpty(rightTxt) ? View.GONE : View.VISIBLE);
        rightText.setText(rightTxt);

        gifDestroy();
        gifView = new GifMovieView(activity);

        switch (mode){
            case INSERT_CREDIT:
            case INSERT:
                gifView.setMovieResource(INSERT_SWIPE);
                break;
            case INSERT_IC_CARD:
                gifView.setMovieResource(INSERT_IC_CARD);
                break;
            case SWIPE:
                gifView.setMovieResource(PLEASE_SWIPE);
                break;
        }

        if (contentView.getChildCount() != 0) contentView.removeAllViews();
        contentView.addView(gifView);
    }

    /**
     * 销毁
     */
    public void onDestroy(){
        stopSwitchGif();
    }

    /**
     * 启动切换gif动画
     * 两秒切换一次s
     */
    private void startSwitchGif(){

        //两秒钟切换一次动画,刷卡和插卡动画切换
        TimerTask timerTask = new TimerTask() {

            public boolean toogle = true;

            @Override
            public void run() {
                if(toogle){
                    handler.sendEmptyMessage(MSG_SWIPE);
                    toogle = false;
                }else {
                    handler.sendEmptyMessage(MSG_INSERT_IC_CARD);
                    toogle = true;
                }
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask,2000,2000);
    }

    /**
     * 取消动画切换task
     */
    public void stopSwitchGif(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    private void gifDestroy() {
        if (gifView != null) {
            gifView.setPaused(true);
            gifView = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(rightText)) {
            listener.onClick(ClickMode.RIGHT_TEXT, currentMode);
        }else if(v.equals(leftText)) {
            listener.onClick(ClickMode.LEFT_TEXT, currentMode);
        }
    }

    /**
     * dialog点击回调
     */
    public interface SwipeDialogClickListener {
        void onClick(ClickMode type, CurrentMode gifType);

        Activity getActivity();
    }
}
