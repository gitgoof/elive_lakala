package com.lakala.elive.message.activity;

import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.NoticeInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.user.base.BaseActivity;


/**
 * 
 * 通知详情页面
 * 
 */
public class NoticeDetailActivity extends BaseActivity {
	
	private NoticeInfo noticeInfo;
	
	private TextView tvNoticeTitle;
	private TextView tvNoticeContent;



    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_notice_detail);
    }

    @Override
    protected void bindView() {
        tvTitleName = (TextView)findViewById(R.id.tv_title_name);
        tvNoticeTitle = (TextView)findViewById(R.id.tv_notice_title);
        tvNoticeContent = (TextView)findViewById(R.id.tv_notice_content);
    }

    @Override
    protected void bindEvent() {
        tvTitleName.setText("通知详情");
        iBtnBack.setVisibility(View.VISIBLE);
        iBtnBack.setOnClickListener(this);

        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        //获取页面传递的对象
        noticeInfo = (NoticeInfo)getIntent().getExtras().get(Constants.EXTRAS_NOTICE_INFO);

        tvNoticeTitle.setText(noticeInfo.getNoticeSubject());
        tvNoticeContent.setText(Html.fromHtml(noticeInfo.getContent()));

        if("1".equals(noticeInfo.getReadConfirm())){
            if("1".equals(noticeInfo.getIsRead())){
                btnCancel.setEnabled(false);
                btnCancel.setText("已阅");
            }else{
                btnCancel.setText("确认");
            }
        }else{
            btnCancel.setVisibility(View.GONE);
        }


    }


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_iv_back:
				this.finish();
				break;
			case R.id.btn_action:
                UserReqInfo reqInfo = new UserReqInfo();
                reqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
                reqInfo.setNoticeNo(noticeInfo.getNoticeNo());
				NetAPI.updateNoticeRead(this, this, reqInfo);
				break;
			default:
				break;
		}
	}

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
			case NetAPI.ACTION_CHECK_MSG:
				noticeInfo.setIsRead("1");
                btnCancel.setEnabled(false);
                btnCancel.setText("已阅");
				Toast.makeText(this, "通知确认成功！", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
		}
		
	}

	@Override
	public void onError(int method, String statusCode) {
		switch (method) {
			case NetAPI.ACTION_CHECK_MSG:
				Toast.makeText(this, "通知确认失败:"+ statusCode +"！", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
		}
	}

}
