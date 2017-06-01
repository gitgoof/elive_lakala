package com.lakala.shoudan.activity.messagecenter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.datadefine.Message;

/**
 * Created by LMQ on 2015/4/27.
 */
public class MessageDetailActivity extends AppBaseActivity {
    public static final String MSG_EXTRA_NAME = "msg";
    private Message mShowMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        initData();
        initUI();
    }

    private void initData() {
        Intent i = getIntent();
        if(i != null){
            mShowMessage = i.getParcelableExtra(MSG_EXTRA_NAME);
        }
        if(mShowMessage == null){
            mShowMessage = new Message();
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(mShowMessage.getMsgTypeChinese());
        TextView msgTime = (TextView) findViewById(R.id.msg_time);
        TextView msgTitle = (TextView) findViewById(R.id.msg_title);
        TextView msgContent = (TextView) findViewById(R.id.msg_content);
        msgTime.setText(mShowMessage.getMsgFormatTime());
        msgTitle.setText(mShowMessage.getTitle());
        switch(Message.CONTENT_TYPE.valueOf(mShowMessage.getContentType())){
            case TEXT:{
                msgContent.setText(mShowMessage.getContent());
                break;
            }
            case HTML:{
                msgContent.setText(Html.fromHtml(mShowMessage.getContent()));
                break;
            }
        }
    }
}
