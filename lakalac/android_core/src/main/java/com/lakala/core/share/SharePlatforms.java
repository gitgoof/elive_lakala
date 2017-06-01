package com.lakala.core.share;

/**
 * Created by xu on 14-1-1.
 *
 * 分享（share）类
 * 继承了shareSDK 的分享功能
 * 由此类得到各个平台并进行分享
 *
 */

public class SharePlatforms {

//    private PopupWindow popupWindow;
//    private Context mContext;
//    private ShareCallback callback;
//
//    public SharePlatforms(Context context,ShareCallback callback) {
//        this.mContext = context;
//        this.callback = callback;
//
//
//        //初始化数据
//        View contentView = LayoutInflater.from(context).inflate(
//                R.layout.core_dialog_share_platform, null);
//        popupWindow = new PopupWindow(LayoutParams.MATCH_PARENT,
//                LayoutParams.MATCH_PARENT);
//        popupWindow.setContentView(contentView);
//
//
//        //通过按钮选择各种平台
//        contentView.findViewById(R.id.iv_qq).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getQQPlatform();
//            }
//        });
//        contentView.findViewById(R.id.iv_qzone).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getQZonePlatform();
//            }
//        });
//        contentView.findViewById(R.id.iv_weibo_sina).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getSinaWeiboPlatform();
//            }
//        });
////        contentView.findViewById(R.id.iv_wechat).setOnClickListener(new OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                getWechatPlatform();
////
////            }
////        });
////        contentView.findViewById(R.id.iv_wechatmoents).setOnClickListener(new OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                getWechatMoentsPlatform();
////
////            }
////        });
//
//        contentView.findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//
//            }
//        });
//
//        //初始化ShareSDK，此后对ShareSDK的操作都依次为基础。
//        //如果不在所有ShareSDK的操作之前调用这行代码，会抛出空指针异常。
//        ShareSDK.initSDK(mContext);
//
//    }
//
//    /**
//     * 显示分享平台列表
//     *
//     * @param v
//     * 显示位置，v的下面
//     */
//    public void show(View v) {
//        if(popupWindow != null){
//            popupWindow.showAsDropDown(v);
//        }
//    }
//
//    /**
//     * 隐藏分享界面
//     */
//    public void dismiss(){
//        if(popupWindow != null && popupWindow.isShowing()){
//            popupWindow.dismiss();
//        }
//    }
//
//    public boolean isShowing(){
//        return popupWindow != null && popupWindow.isShowing();
//    }
//
//
//    /**
//     * 得到 QQ 的平台实例，可在得到实例后进行具体的分享行为设置
//     * */
//    public void getQQPlatform(){
//        Platform qq = ShareSDK.getPlatform(mContext, QQ.NAME);
//        QQ.ShareParams params = new QQ.ShareParams();
//        params.text = "QQ分享内容";
//        qq.share(params);
//    }
//
//    /**
//     * 得到 QQ空间 的平台实例，可在得到实例后进行具体的分享行为设置
//     * */
//    public void getQZonePlatform(){
//        Platform qzone = ShareSDK.getPlatform(mContext, QZone.NAME);
//        QZone.ShareParams params2 = new QZone.ShareParams();
//        params2.text = "QQ空间分享内容";
//        qzone.setPlatformActionListener(listener);
//        qzone.share(params2);
//    }
//
//    /**
//     * 得到 新浪微博 的平台实例，可在得到实例后进行具体的分享行为设置
//     * */
//    public void getSinaWeiboPlatform(){
//        Platform weiboSina = ShareSDK.getPlatform(mContext, SinaWeibo.NAME);
//        SinaWeibo.ShareParams params3 = new SinaWeibo.ShareParams();
//        params3.text = "新浪微博分享内容";
//        weiboSina.setPlatformActionListener(listener);
//        weiboSina.share(params3);
//    }
//
////    /**
////     * 得到 腾讯微博 的平台实例，可在得到实例后进行具体的分享行为设置
////     * */
////    public void getTencentWeiboPlatform(){
////        Platform weiboTencent = ShareSDK.getPlatform(mContext,
////                TencentWeibo.NAME);
////        TencentWeibo.ShareParams params4 = new TencentWeibo.ShareParams();
////        params4.text = "腾讯微博分享内容";
////        params4.imageUrl = "http://f.hiphotos.baidu.com/image/w%3D2048/sign=c12c66ada6c27d1ea5263cc42fedad6e/024f78f0f736afc36c22eb6cb119ebc4b7451288.jpg";
////        weiboTencent.setPlatformActionListener(listener);
////        weiboTencent.share(params4);
////    }
//
////    /**
////     * 得到 微信 的平台实例，可在得到实例后进行具体的分享行为设置
////     * */
////    public void getWechatPlatform(){
////        Platform wechat = ShareSDK.getPlatform(mContext,
////                Wechat.NAME);
////        Wechat.ShareParams params5 = new Wechat.ShareParams();
////        params5.text = "微信分享内容";
////        wechat.setPlatformActionListener(listener);
////        wechat.share(params5);
////    }
////
////    /**
////     * 得到 微信朋友圈 的平台实例，可在得到实例后进行具体的分享行为设置
////     * */
////    public void getWechatMoentsPlatform(){
////        Platform wechatmoent = ShareSDK.getPlatform(mContext,
////                WechatMoments.NAME);
////        WechatMoments.ShareParams params6 = new WechatMoments.ShareParams();
////        params6.text = "微聊分享内容";
////        wechatmoent.setPlatformActionListener(listener);
////        wechatmoent.share(params6);
////    }
//
//    // ShareSdk 分享回调监听
//    private PlatformActionListener listener = new PlatformActionListener() {
//
//        @Override
//        public void onError(Platform arg0, int arg1, Throwable arg2) {
//            if(callback != null){
//                callback.onError();
//            }
//        }
//
//        @Override
//        public void onComplete(Platform arg0, int arg1,
//                               HashMap<String, Object> arg2) {
//            if(callback != null){
//                callback.onComplete();
//            }
//        }
//
//        @Override
//        public void onCancel(Platform arg0, int arg1) {
//            if(callback != null){
//                callback.onCancle();
//            }
//        }
//    };
//
//
//    /**
//     * 结束ShareSDK的统计功能并释放资源。
//     * 如果这行代码没有被调用，那么“应用启动次数”的统计将不会准确，因为应用可能从来没有被关闭。
//     * initSDK是可以重复调用的，其实ShareSDK建议在您不确定的时候调用这个方法，来保证ShareSDK被正确初始化。
//     * 而stopSDK一旦调用了，就必须重新调用initSDK才能使用ShareSDK的功能，否则会出现空指针异常。
//     */
//    public void destory(){
//        ShareSDK.stopSDK(mContext);
//    }
//
//
//    /**
//     * 自定义分享回调监听，由ShareSDK的回调处理，所以实现时注意，可能当前线程是子线程。
//     * 包含分享成功、失败、取消的处理
//     */
//    public interface ShareCallback {
//
//        //分享错误的处理
//        void onError();
//
//        //分享成功的处理
//        void onComplete();
//
//        //取消分享的处理
//        void onCancle();
//    }


}