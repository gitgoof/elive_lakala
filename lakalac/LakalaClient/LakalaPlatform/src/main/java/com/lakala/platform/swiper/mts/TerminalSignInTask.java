package com.lakala.platform.swiper.mts;

import android.support.v4.app.FragmentActivity;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.IHttpRequestEvents;
import com.lakala.library.exception.BaseException;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.swiper.TerminalKey;

import org.json.JSONObject;

/**
 * Created by wangchao on 14-2-13.
 */
public class TerminalSignInTask {
    /**
     * 实例
     */
    private static TerminalSignInTask instance;
    /**
     * 监听
     */
    private TerminalSignInListener listener;
    /**
     * activity
     */
    private FragmentActivity activity;
    /**
     * 请求实例
     */
    private BusinessRequest request;

    public TerminalSignInTask(FragmentActivity activity) {
        this.activity = activity;
    }

    /**
     * 设置监听
     */
    public void setListener(TerminalSignInListener listener) {
        this.listener = listener;
    }

    /**
     * 终端签到
     *
     * @param terminalId
     */
    public void execute(final String terminalId) {
        request = SwipeRequest.queryTerminalState(activity, terminalId);
        request.setIHttpRequestEvents(new IHttpRequestEvents(){
            @Override
            public void onSuccess(HttpRequest request) {
                super.onSuccess(request);
                JSONObject response = (JSONObject) request.getResponseHandler().getResultData();
                Status status = Status.Bind;
                String IsBind = response.optString("IsBind");
                if (IsBind.equals("0")) {
                    //未绑定
                    status = Status.UnBind;
                } else if (IsBind.equals("1")) {
                    //已绑定
                    status = Status.Bind;
                } else if (IsBind.equals("2")) {
                    //不可用
                    status = Status.Disabled;
                } else if (IsBind.equals("3")) {
                    //用户冲突
                    status = Status.Conflict;
                    status.setUserId(response.optString("BindMobile"));
                }
                if (status == Status.Bind) {
//                    ApplicationEx.getInstance().getUser().setSwiperId(terminalId);
                    TerminalKey.setMac(terminalId, response.optString("MacKey"));//保存mac密钥
                    TerminalKey.setPik(terminalId, response.optString("PinKey"));//保存工作密钥
                    TerminalKey.setTpk(terminalId, response.optString("WorkKey"));//保存主密钥
                }
                if(listener != null) {
                    listener.signInSuccess(status);
                    LogUtil.print("queryTerminalState", response.toString());
                }
            }

            @Override
            public void onFailure(HttpRequest request, BaseException exception) {
                super.onFailure(request, exception);
                if(listener != null) {
                    listener.signInFailure();
                }
            }
        });
        request.execute();
    }

    /**
     * 签到回调
     */
    public interface TerminalSignInListener {
        void signInSuccess(Status status);

        void signInFailure();
    }

    /**
     * 刷卡器状态
     */
    public enum Status {
        UnBind, Bind, Disabled, Conflict;

        private String userId = "其他";

        public String getUserId() {
            return userId;
        }

        public Status setUserId(String userId) {
            this.userId = userId;
            return this;
        }
    }

}
