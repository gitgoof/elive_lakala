package com.lakala.shoudan.activity.messagecenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.datadefine.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by LMQ on 2015/4/22.
 */
public class MessageCenterActivity extends AppBaseActivity {
    private PullToRefreshListView msgListView;
    private List<Message> mData;
    private MsgAdapter mAdapter;
    private RadioGroup radioTabs;
    private static final int DEFAULT_CHECK_TAB = R.id.tab2;
    private static final int[] TABS_ID = {R.id.tab1, R.id.tab2, R.id.tab3};
    private static final Message.MSG_TYPE[] TYPES = {Message.MSG_TYPE.Publish,
            Message.MSG_TYPE.Trade,
            Message.MSG_TYPE.Business};
    private MessageDao messageDao;
    private SparseArray<Integer> tabIds = new SparseArray<Integer>();
    private View emptyView;
    private Message.MSG_TYPE oldType = Message.MSG_TYPE.NULL;
    private static final int PAGE_SIZE = 20;
    private int mPage = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        for (int i = 0; i < TABS_ID.length; i++) {
            tabIds.put(TABS_ID[i], TYPES[i].ordinal());
        }
        messageDao = MessageDao.getInstance(ApplicationEx.getInstance().getUser().getLoginName());
        initUI();
        initData();
        initListener();
        getAllTypeServiceData();
    }

    @Override
    protected void initUI() {
        super.initUI();
        msgListView = (PullToRefreshListView) findViewById(R.id.listview_msg);
        emptyView = findViewById(R.id.emptyView);
        radioTabs = (RadioGroup) findViewById(R.id.radioTabs);
    }

    private void initListener() {
        msgListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           long id) {
                final int p;
                if (position != 0) {
                    p = position - 1;
                } else {
                    p = 0;
                }
                DialogCreator.createFullContentDialog(context, "确定", "取消", "是否删除该通知？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int delCount = messageDao.deleteMsg(mAdapter.getItem(p));
                        if (delCount != 0) {
                            updateData(true);
                        }
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                return true;
            }
        });
        msgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    position--;
                }
                Message msg = mAdapter.getItem(position);
                msg.setReaded(true);
                messageDao.updateMsg(msg);
                mAdapter.notifyDataSetChanged();
                switch (Message.CONTENT_TYPE.valueOf(msg.getContentType())) {
                    case URL:
                    case HTML: {
                        String title = msg.getMsgTypeChinese();
                        String url = msg.getContent();
                        ProtocalActivity.open(context, title, url);
                        break;
                    }
                    case TEXT: {
                        Intent intent = new Intent(MessageCenterActivity.this, MessageDetailActivity.class);
                        intent.putExtra(MessageDetailActivity.MSG_EXTRA_NAME, msg);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });
        msgListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getServiceData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                updateData();
                emptyView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        msgListView.onRefreshComplete();
                    }
                }, 500);
            }
        });

        radioTabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Message.MSG_TYPE checkType = Message.MSG_TYPE.valueOf(tabIds.get(checkedId));
                if (oldType == Message.MSG_TYPE.NULL) {
                    oldType = checkType;
                }
                updateData(checkType);
            }
        });
    }

    private void initData() {
        navigationBar.setTitle("消息中心");
        mData = new ArrayList<Message>();
        mAdapter = new MsgAdapter(this, mData);
        msgListView.setAdapter(mAdapter);
        msgListView.setMode(PullToRefreshBase.Mode.BOTH);
        for (int id : TABS_ID) {//设置各个RadioButton的text为TYPES顺序
            RadioButton radioButton = (RadioButton) radioTabs.findViewById(id);
            radioButton.setText(TYPES[tabIds.get(id)].getChineseValue());
        }
    }

    private void setCheck(int id) {
        ((RadioButton) radioTabs.findViewById(id)).setChecked(true);
    }

    private void getServiceData() {
        getServiceData(getCheckedMSG_TYPE(), true);
    }

    int count = 0;
    volatile boolean isAll = true;

    private void getAllTypeServiceData() {
        isAll = true;
        //自动显示正在载入
        msgListView.setShowViewWhileRefreshing(true);
        msgListView.setRefreshing(true, true);

        getServiceData(Message.MSG_TYPE.Publish, false);
        getServiceData(Message.MSG_TYPE.Trade, false);
        getServiceData(Message.MSG_TYPE.Business, false);
    }

    private Message getDebugMessage() {
        Message msg = new Message();
        msg.setMsgType(Message.MSG_TYPE.Trade.name());
        msg.setContentType(Message.CONTENT_TYPE.TEXT.name());
        msg.setContent("测试");
        msg.setId(String.valueOf(new Random().nextInt(100)));
        return msg;
    }

    private void getServiceData(final Message.MSG_TYPE msgType, final boolean isUpdated) {
        CommonServiceManager manager = CommonServiceManager.getInstance();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    JSONArray jsonArray = null;
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        jsonArray = jsonObject.getJSONArray("messages");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int length = 0;
                    if (jsonArray != null) {
                        length = jsonArray.length();
                    }
                    for (int i = 0; i < length; i++) {
                        try {
                            JSONObject json = jsonArray.getJSONObject(i);
                            Message msg = Message.obtain(json);
                            //插一条消息到数据库
                            messageDao.insertMsg(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    count++;
                    boolean toUpdate = isUpdated;
                    if (count >= 3 && isAll) {
                        if (messageDao.getUnreadMsgCount() == 0) {
                            setCheck(DEFAULT_CHECK_TAB);
                        } else {
                            List<Message.MSG_TYPE> typeList = new ArrayList<Message.MSG_TYPE>();
                            typeList.addAll(Arrays.asList(Message.MSG_TYPE.values()));
                            typeList.remove(Message.MSG_TYPE.INDEX);
                            typeList.remove(Message.MSG_TYPE.NULL);
                            for (Message.MSG_TYPE type : typeList) {
                                int num = messageDao.getUnreadMsgCount(type);
                                if (num != 0) {
                                    setCheck(TABS_ID[type.ordinal()]);
                                    break;
                                }
                            }
                        }
                        isAll = false;
                        toUpdate = true;
                    }
                    if (toUpdate) {
                        msgListView.setEmptyView(emptyView);
                        msgListView.onRefreshComplete();
                        updateData();
                    }
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        manager.getMessageList(ApplicationEx.getInstance().getUser().getLoginName(), msgType, callback);
    }

    private void updateData() {
        updateData(getCheckedMSG_TYPE());
    }

    private Message.MSG_TYPE getCheckedMSG_TYPE() {
        Integer index = tabIds.get(radioTabs.getCheckedRadioButtonId());
        return index == null ? null : Message.MSG_TYPE.valueOf(index);
    }

    private void updateData(Message.MSG_TYPE type) {
        if (oldType == type) {
            mPage++;
        } else {
            mPage = 0;
            mData.clear();
        }
        oldType = type;
        List<Message> msgList = messageDao.queryMsg(type, PAGE_SIZE, mPage);
        mData.addAll(msgList);
        mAdapter.notifyDataSetChanged();
    }

    private void updateData(boolean isDeleted) {
        if (isDeleted) {
            int newSize = mData.size() - 1;
            List<Message> msgList = messageDao.queryMsg(getCheckedMSG_TYPE(), newSize, 0);
            mData.clear();
            mData.addAll(msgList);
            mAdapter.notifyDataSetChanged();
        } else {
            updateData();
        }
    }

    private class MsgAdapter extends BaseAdapter {
        private Context mContext;
        private List<Message> mData;
        private LayoutInflater mInflater;

        private MsgAdapter(Context context, List<Message> mData) {
            this.mContext = context;
            this.mData = mData;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Message getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.adapter_msg_item, null);
                convertView.setTag(holder);
                holder.msgContent = (TextView) convertView.findViewById(R.id.msg_content);
                holder.msgTime = (TextView) convertView.findViewById(R.id.msg_time);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Message msg = getItem(position);
            int isReaded = msg.getReaded();

            holder.msgContent.setText(msg.getTitle());
            holder.msgTime.setText(msg.getMsgFormatTime());
            setMsgReaded(holder.msgContent, isReaded);
            return convertView;
        }

        private void setMsgReaded(TextView tv, int isReaded) {//未读：0 ， 已读：1
            if (isReaded == 0) {
                tv.setTextColor(getResources().getColor(R.color.msg_unread));
            } else {
                tv.setTextColor(getResources().getColor(R.color.combination_title_text_color));
            }
        }

        class ViewHolder {
            TextView msgTime;
            TextView msgContent;
        }
    }
}
