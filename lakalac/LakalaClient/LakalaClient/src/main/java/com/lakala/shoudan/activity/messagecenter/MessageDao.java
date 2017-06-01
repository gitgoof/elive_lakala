package com.lakala.shoudan.activity.messagecenter;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.TextUtils;

import com.lakala.core.dao.BaseDao;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.activity.messagecenter.messageBean.MessageCount;
import com.lakala.shoudan.activity.messagecenter.messageBean.TradeMessage;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.datadefine.Message;

import net.sqlcipher.database.SQLiteConstraintException;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LMQ on 2015/11/25.
 */
public class MessageDao extends BaseDao {
    private static final String MSG_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " +
            MessageTableEntity.TABLE_NAME + " (" +
            MessageTableEntity.ID + " TEXT PRIMARY KEY," +
            MessageTableEntity.TITLE + " TEXT," +
            MessageTableEntity.TOP + " TEXT," +
            MessageTableEntity.READED + " TEXT," +
            MessageTableEntity.CONTENT + " TEXT," +
            MessageTableEntity.MSG_TIME + " Long," +
            MessageTableEntity.IDX + " Int," +
            MessageTableEntity.EXT_INFO + " TEXT," +
            MessageTableEntity.MSG_TYPE + " TEXT," +
            MessageTableEntity.CONTENT_TYPE + " TEXT)";
    private static final String SQL_DELET_MSG_TABLE =
            "DROP TABLE IF EXISTS " + MessageTableEntity.TABLE_NAME;
    private static MessageDao instance = null;
    private String user = "";

    public static MessageDao getInstance(String username) {
        if (instance == null || !TextUtils.equals(username, instance.user)) {
            instance = new MessageDao();
            instance.user = username;
        }
        return instance;
    }

    public MessageDao() {
        super(ApplicationEx.getInstance().getUser().getLoginName());
        db.execSQL(MSG_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        super.onUpgrade(sqLiteDatabase, i, i2);
        sqLiteDatabase.execSQL(SQL_DELET_MSG_TABLE);
    }

    public int deleteMsg(Message msg) {
        int delCount = db.delete(MessageTableEntity.TABLE_NAME, MessageTableEntity.ID + "=?",
                new String[]{msg.getId()});
        return delCount;
    }

    /**
     * 删除type类型的所有消息
     *
     * @param type
     * @return
     */
    public int deleteMsg(Message.MSG_TYPE type) {
        int delCount = db.delete(MessageTableEntity.TABLE_NAME, MessageTableEntity.MSG_TYPE + "=?",
                new String[]{type.name()});
        return delCount;
    }

    /**
     * 更新Message
     *
     * @param msg
     * @return 被更新的消息条数
     */
    public int updateMsg(Message msg) {
        int updateCnt = db
                .update(MessageTableEntity.TABLE_NAME, Msg2CV(msg), MessageTableEntity.ID + "=?",
                        new String[]{msg.getId()});
        return updateCnt;
    }

    public int updateMsgByIdx(Message msg) {
        int updateCnt = db.update(MessageTableEntity.TABLE_NAME, Msg2CV(msg), MessageTableEntity.IDX + "=?",
                new String[]{String.valueOf(msg.getIdx())});
        return updateCnt;
    }

    /**
     * 插入一条消息到数据库
     *
     * @param msg
     */
    public void insertMsg(Message msg) throws SQLiteConstraintException {
        ContentValues values = Msg2CV(msg);
        db.insert(MessageTableEntity.TABLE_NAME, null, values);
    }

    /**
     * 获取本地数据库中未读消息条数
     *
     * @return
     */
    public int getUnreadMsgCount() {
        Cursor cursor = db.query(MessageTableEntity.TABLE_NAME, new String[]{MessageTableEntity.ID},
                MessageTableEntity.READED + "=? and " + MessageTableEntity.MSG_TYPE + "!=?",
                new String[]{"0", Message.MSG_TYPE.INDEX.name()}, null, null,
                null);
        int cnt = 0;
        if (cursor != null) {
            cnt = cursor.getCount();
        }
        closeCursor(cursor);
        return cnt;
    }

    /**
     * 根据消息类型获取本地数据库中未读消息条数
     *
     * @param type
     * @return
     */
    public int getUnreadMsgCount(Message.MSG_TYPE type) {
        Cursor cursor = db.query(MessageTableEntity.TABLE_NAME, new String[]{MessageTableEntity.ID},
                MessageTableEntity.READED + "=? and " + MessageTableEntity
                        .MSG_TYPE + "=?", new
                        String[]{"0", type.name()}, null,
                null,
                null);
        int cnt = 0;
        if (cursor != null) {
            cnt = cursor.getCount();
        }
        closeCursor(cursor);
        return cnt;
    }

    /**
     * 获取消息类型为type的所有消息
     *
     * @param type
     * @return
     */
    public List<Message> queryMsg(Message.MSG_TYPE type) {
        return queryMsg(type, Integer.MAX_VALUE, 0);
    }

    /**
     * @param type     获取的消息类型
     * @param pageSize 消息分页条数
     * @param page     获取第page页的消息(从0开始)
     * @return
     */
    public List<Message> queryMsg(Message.MSG_TYPE type, int pageSize, int page) {
        int m = pageSize * page;
        int n = pageSize;
        Cursor cursor = db.query(MessageTableEntity.TABLE_NAME, null, MessageTableEntity.MSG_TYPE + "=?",
                new String[]{type.name()},
                null, null,
                MessageTableEntity.ID + " desc", m + "," + n);
        Message msg = null;
        List<Message> msgList = new ArrayList<Message>();
        while (cursor.moveToNext()) {
            msg = new Message();
            msgList.add(msg);
            msg.setId(cursor.getString(cursor.getColumnIndex(MessageTableEntity.ID)));
            msg.setTitle(cursor.getString(cursor.getColumnIndex(MessageTableEntity.TITLE)));
            int tempTop = cursor.getInt(cursor.getColumnIndex(MessageTableEntity.TOP));
            msg.setTop(tempTop == 1);
            msg.setReaded(cursor.getInt(cursor.getColumnIndex(MessageTableEntity.READED)) == 1);
            msg.setContent(cursor.getString(cursor.getColumnIndex(MessageTableEntity.CONTENT)));
            msg.setLongMsgTime(cursor.getLong(cursor.getColumnIndex(MessageTableEntity.MSG_TIME)));
            msg.setExtInfo(cursor.getString(cursor.getColumnIndex(MessageTableEntity.EXT_INFO)));
            msg.setMsgType(cursor.getString(cursor.getColumnIndex(MessageTableEntity.MSG_TYPE)));
            msg.setContentType(cursor.getString(cursor.getColumnIndex(MessageTableEntity.CONTENT_TYPE)));
            msg.setIdx(cursor.getInt(cursor.getColumnIndex(MessageTableEntity.IDX)));
        }
        closeCursor(cursor);
        return msgList;
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = null;
    }

    private ContentValues Msg2CV(Message msg) {
        ContentValues values = new ContentValues();
        values.put(MessageTableEntity.ID, msg.getId());
        values.put(MessageTableEntity.TITLE, msg.getTitle());
        values.put(MessageTableEntity.TOP, msg.getTop());
        values.put(MessageTableEntity.READED, msg.getReaded());
        values.put(MessageTableEntity.CONTENT, msg.getContent());
        values.put(MessageTableEntity.MSG_TIME, msg.getMsgTime());
        values.put(MessageTableEntity.EXT_INFO, msg.getExtInfo());
        values.put(MessageTableEntity.MSG_TYPE, msg.getMsgType());
        values.put(MessageTableEntity.CONTENT_TYPE, msg.getContentType());
        values.put(MessageTableEntity.IDX, msg.getIdx());
        return values;
    }

    public static void saveUnread(Context context, MessageCount mc) {
        SharedPreferences shp = context.getSharedPreferences(
                Constants.shp_client, Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor = shp.edit();
        editor.putInt("unread_all", mc.getAllCount());
        editor.putInt("unread_publish", mc.getPublishCount());
        editor.putInt("unread_trade", mc.getTradeCount());
        editor.putInt("unread_business", mc.getBusinessCount());
        editor.commit();
    }

    public static void saveMessageMap(Context context, Map<String, String> messageMap) {
        SharedPreferences shp = context.getSharedPreferences(
                Constants.MAP_MESSAGE, Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor = shp.edit();
        for (String key : messageMap.keySet()) {
            String json = messageMap.get(key);
            editor.putString(key, json);
        }
        editor.commit();
    }

    public static Map<String, String> getMessageMap(Context context, TradeMessage message) {
        Map<String, String> map = new HashMap<>();
        @SuppressWarnings("deprecation")
        SharedPreferences shp = context.getSharedPreferences(
                Constants.MAP_MESSAGE, Context.MODE_WORLD_WRITEABLE);
        map.put(message.getId(), shp.getString(message.getId(), ""));
        return map;
    }

    public static MessageCount readUnread(Context context) {
        MessageCount mc = new MessageCount();
        @SuppressWarnings("deprecation")
        SharedPreferences shp = context.getSharedPreferences(
                Constants.shp_client, Context.MODE_WORLD_WRITEABLE);
        mc.setAllCount(shp.getInt("unread_all", 0));
        mc.setPublishCount(shp.getInt("unread_publish", 0));
        mc.setTradeCount(shp.getInt("unread_trade", 0));
        mc.setBusinessCount(shp.getInt("unread_business", 0));
        return mc;
    }
}
