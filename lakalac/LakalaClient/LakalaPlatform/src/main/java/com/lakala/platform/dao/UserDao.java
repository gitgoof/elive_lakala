package com.lakala.platform.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.lakala.core.dao.BaseDao;
import com.lakala.library.DebugConfig;
import com.lakala.library.encryption.AESUtils;
import com.lakala.library.util.DeviceUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.bean.AppConfig;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;

/**
 * 用户信息表
 *
 * Created by jerry on 14-2-28.
 */
public class UserDao extends BaseDao {

    private static final String TABLE_NAME = "t_user";
    private static final String TABLE_NAME_T = "t_user_t";
    private static final String TABLE_NAME_MERCHANT_INFO = "merchant_info_table";

    private static final String KEY_LOGIN_NAME = "loginName";
    private static final String KEY_IS_LOGIN = "isLogin";

    private static final String MER_NO = "merNo";//商户号
    private static final String ACCOUNT_NO = "accountNo";
    private static final String ACCOUNT_NAME = "accountName";
    private static final String REAL_NAME = "realName";
    private static final String STATUS = "status";
    private static final String MERCHANT_STATUS = "merchantStatus";
    private static final String ID_CARD_ID = "idCardId";
    private static final String MERCHANT_NAME = "merchantName";
    private static final String MERCHANT_TYPE = "merchantType";
    private static final String BANK_NAME = "bankName";
    private static final String ACCOUNT_TYPE = "accountType";
    private static final String EXPIREIN = "expirein";
    private static final String MERCHANT_APPLIED  = "merchant_applied";
    private static final String MSG_COUNT = "msg_count";
    private static final String APP_CONFIG = "app_config";
    private static final String MERCHANT_ADDR = "merchant_addr";
    private static final String MERCHANT_AREA = "merchant_area";
    private static final String MERCHANT_AREA_CODE = "merchant_area_code";

    private static final String MTS_TRS_PASSWORD_FLAG = "trsPasswordFlag";
    private static final String MTS_QUESTION_FLAG = "questionFlag";
    private static final String MTS_TERMINAL_ID = "mtsTerminalID";
    private static final String MTS_PIN_KEY = "mtsPinKey";
    private static final String MTS_WORK_KEY = "mtsWorkKey";
    private static final String MTS_MAC_KEY = "mtsMacKey";
    private static final String MTS_AUTH_FLAG = "mtsAuthFlag";
    private static final String MTS_CUSTOMER_NAME = "mtsCustomerName";
    private static final String MTS_IDENTIFIER = "mtsIdentifier";
    private static final String MERCHANT_INFO = "merchant_info";

    //创建用户表sql语句
    private final String sql_t_user = "create table if not exists t_user(" +
            "mobile text primary key," +    //手机号，登录用户名
            "userId text," +                //用户id
            "accessToken text," +           //访问token
            "refreshToken text," +          //刷新token
            "terminalId text," +            //虚拟终端号
            "gesturePassword text," +       //手势密码
            formatKey(MER_NO) +
            formatKey(ACCOUNT_NO) +
            formatKey(ACCOUNT_NAME) +
            formatKey(REAL_NAME) +
            formatKey(STATUS) +
            formatKey(MERCHANT_STATUS) +
            formatKey(ID_CARD_ID) +
            formatKey(MERCHANT_NAME) +
            formatKey(MERCHANT_TYPE) +
            formatKey(BANK_NAME) +
            formatKey(ACCOUNT_TYPE) +
            formatKey(EXPIREIN) +
            formatKey(MERCHANT_APPLIED) +
            MSG_COUNT + " INTEGER," +
            formatKey(APP_CONFIG) +
            formatKey(MERCHANT_ADDR) +
            formatKey(MERCHANT_AREA) +
            formatKey(MERCHANT_AREA_CODE) +
            "isLogin text)";                //是否当前登录用户

    //创建用户表sql语句
    private final String sql_t_user_t = "create table if not exists " + TABLE_NAME_T  + "(" +
            "mobile text primary key," +    //手机号，登录用户名
            "trsPasswordFlag text," +
            "questionFlag text," +
            "mtsTerminalID text," +
            "mtsPinKey text," +
            "mtsWorkKey text," +
            "mtsMacKey text," +
            "mtsAuthFlag text," +
            "mtsCustomerName text," +
            "mtsIdentifier text" +
            ")";                //是否当前登录用户

    //创建用户表sql语句
    private final String sql_t_merchant_info = "create table if not exists " + TABLE_NAME_MERCHANT_INFO   + "(" +
            "mobile text primary key," +    //手机号，登录用户名
            (MERCHANT_INFO) + " text" +
            ")";

    private static UserDao mUserDao;

    private static String formatKey(String key){
        return key + " text,";
    }

    public static synchronized UserDao getInstance(){
        if (mUserDao == null){
            mUserDao = new UserDao();
        }
        return mUserDao;
    }

    private UserDao() {
        super();
        createTable();
    }

    private void createTable(){
        db.execSQL(sql_t_user);
        db.execSQL(sql_t_user_t);
        db.execSQL(sql_t_merchant_info);
    }

    /**
     * 是否存在用户信息
     * @param loginName 登录名
     * @return true 存在 false 不存在
     */
    public synchronized boolean isExistUser(String loginName){
        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{"mobile"},
                "mobile = ?",
                new String[]{loginName}, null, null, null);
        boolean flag = false;
        if (cursor.getCount() > 0){
            flag = true;
        }
        cursor.close();
        return flag;
    }

    /**
     * 是否存在用户信息
     * @param loginName 登录名
     * @return true 存在 false 不存在
     */
    public synchronized boolean isExistUser(String loginName, String table){
        Cursor cursor = db.query(
                table,
                new String[]{"mobile"},
                "mobile = ?",
                new String[]{loginName}, null, null, null);
        boolean flag = false;
        if (cursor.getCount() > 0){
            flag = true;
        }
        cursor.close();
        return flag;
    }
    /**
     * 是否存在用户信息
     * @param loginName 登录名
     * @return true 存在 false 不存在
     */
    public synchronized boolean isExistUser_t(String loginName){
        Cursor cursor = db.query(
                TABLE_NAME_T,
                new String[]{"mobile"},
                "mobile = ?",
                new String[]{loginName}, null, null, null);
        boolean flag = false;
        if (cursor.getCount() > 0){
            flag = true;
        }
        cursor.close();
        return flag;
    }


    /**
     * 保存用户信息
     * @param user 用户对象
     */
    public synchronized void saveUser(User user) throws Exception {

        LogUtil.print(user.toString());

        ApplicationEx.getInstance().getSession().setUser(user);

        String mobile = user.getLoginName();
        if(TextUtils.isEmpty(mobile)){
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("mobile",mobile);
        contentValues.put("userId",user.getUserId());
        contentValues.put("accessToken",user.getAccessToken());
        contentValues.put("refreshToken",user.getRefreshToken());
        contentValues.put("terminalId",user.getTerminalId());
        contentValues.put("gesturePassword",user.getGesturePwd());

        contentValues.put(EXPIREIN, user.getExpirein());
        contentValues.put(MSG_COUNT, user.getMsgCount());
        contentValues.put(APP_CONFIG, user.getAppConfig().getConfigStr());

        contentValues.put("isLogin","1");

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("mobile", mobile);
        contentValues1.put(MTS_TRS_PASSWORD_FLAG, user.isTrsPasswordFlag() ? "1" : "0");
        contentValues1.put(MTS_QUESTION_FLAG, user.isQuestionFlag() ? "1" : "0");
        contentValues1.put(MTS_TERMINAL_ID, user.getMtsTerminalId());
        contentValues1.put(MTS_PIN_KEY, user.getMtsPinKey());
        contentValues1.put(MTS_WORK_KEY, user.getMtsWorkKey());
        contentValues1.put(MTS_MAC_KEY, user.getMtsMacKey());
        contentValues1.put(MTS_AUTH_FLAG, user.getAuthFlag());
        contentValues1.put(MTS_CUSTOMER_NAME, user.getMtsCustomerName());
        contentValues1.put(MTS_IDENTIFIER, user.getIdentifier());

        ContentValues contentValuesMerchantInfo = new ContentValues();
        contentValuesMerchantInfo.put("mobile", mobile);
        contentValuesMerchantInfo.put(MERCHANT_INFO, encrypt(user.getMerchantInfo() == null ? "" : user.getMerchantInfo().getJsonMerchantInfo()));

        LogUtil.print("MerchantInfo = " + (user.getMerchantInfo() == null ? "" : user.getMerchantInfo().getJsonMerchantInfo()));

        setAllUserLoginFalse();
        if(isExistUser(mobile,TABLE_NAME)){
            db.update(TABLE_NAME, contentValues, "mobile = ?", new String[]{mobile});
        }else {
            db.insert(TABLE_NAME,null,contentValues);
        }

        if (isExistUser_t(mobile)){
            db.update(TABLE_NAME_T, contentValues1, "mobile = ?", new String[]{mobile});
        }else {
            db.insert(TABLE_NAME_T,null,contentValues1);
        }

        if(isExistUser(mobile, TABLE_NAME_MERCHANT_INFO)){
            db.update(TABLE_NAME_MERCHANT_INFO, contentValuesMerchantInfo, "mobile = ?", new String[]{mobile});
        }else{
            db.insert(TABLE_NAME_MERCHANT_INFO, null, contentValuesMerchantInfo);
        }
    }


    /**
     * 获得登录用户对象
     * @return
     */
    public synchronized User getLoginUser(){
        User user = new User();


        Cursor cursor = db.query(TABLE_NAME, null, "isLogin = ?", new String[]{"1"}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                try{

                    String mobile = cursor.getString(cursor.getColumnIndex("mobile"));
                    String userId = cursor.getString(cursor.getColumnIndex("userId"));
                    String accessToken = cursor.getString(cursor.getColumnIndex("accessToken"));
                    String refreshToken = cursor.getString(cursor.getColumnIndex("refreshToken"));
                    String terminalId = cursor.getString(cursor.getColumnIndex("terminalId"));
                    String gesturePassword = cursor.getString(cursor.getColumnIndex("gesturePassword"));

                    String trsPasswordFlag = "";
                    String questionFlag = "";
                    String mtsTerminalId = "";
                    String mtsMacKey = "";
                    String mtsWorkKey = "";
                    String mtsPinKey = "";
                    String authFlag = "";
                    String mtsCustomerName = "";
                    String mtsIdentifier = "";

                    Cursor cursor1 = db.query(TABLE_NAME_T, null, "mobile = ?", new String[]{mobile}, null, null, null);
                    if (cursor1 != null){
                        if (cursor1.moveToFirst()){
                            trsPasswordFlag = cursor1.getString(cursor1.getColumnIndex(MTS_TRS_PASSWORD_FLAG));
                            questionFlag = cursor1.getString(cursor1.getColumnIndex(MTS_QUESTION_FLAG));
                            mtsTerminalId = cursor1.getString(cursor1.getColumnIndex(MTS_TERMINAL_ID));
                            mtsMacKey = cursor1.getString(cursor1.getColumnIndex(MTS_MAC_KEY));
                            mtsWorkKey = cursor1.getString(cursor1.getColumnIndex(MTS_WORK_KEY));
                            mtsPinKey = cursor1.getString(cursor1.getColumnIndex(MTS_PIN_KEY));
                            authFlag = cursor1.getString(cursor1.getColumnIndex(MTS_AUTH_FLAG));
                            mtsCustomerName = cursor1.getString(cursor1.getColumnIndex(MTS_CUSTOMER_NAME));
                            mtsIdentifier = cursor1.getString(cursor1.getColumnIndex(MTS_IDENTIFIER));
                        }
                        cursor1.close();
                    }

                    user.setTrsPasswordFlag("1".equals(trsPasswordFlag));
                    user.setQuestionFlag("1".equals(questionFlag));
                    user.setMtsTerminalId(mtsTerminalId);
                    user.setMtsMacKey(mtsMacKey);
                    user.setMtsWorkKey(mtsWorkKey);
                    user.setMtsPinKey(mtsPinKey);
                    user.setAuthFlag(authFlag);
                    user.setMtsCustomerName(mtsCustomerName);
                    user.setIdentifier(mtsIdentifier);

                    user.setExpirein(getCursorByKey(cursor, EXPIREIN));

                    user.setMsgCount(cursor.getInt(cursor.getColumnIndex(MSG_COUNT)));

                    user.setAppConfig(new AppConfig().updateConfig(getCursorByKey(cursor, APP_CONFIG)));

                    Cursor merchantCursor = db.query(TABLE_NAME_MERCHANT_INFO, null, "mobile = ?", new String[]{mobile}, null, null, null);

                    if (merchantCursor != null){
                        if (merchantCursor.moveToFirst()){

                            user.setMerchantInfo(new MerchantInfo(decrypt(getCursorByKey(merchantCursor, MERCHANT_INFO))));

                        }
                        merchantCursor.close();
                    }


                    user.setLoginName(mobile);
                    user.setAccessToken(accessToken);
                    user.setRefreshToken(refreshToken);
                    user.setTerminalId(terminalId);
                    user.setUserId(userId);
                    user.setGesturePwd(gesturePassword);

                    if(DebugConfig.DEBUG){
                        LogUtil.print(getClass().getName(), "get login user = " + user.toString());
                    }

                }catch (Exception e){
                    LogUtil.print(e);
                }

            }
            cursor.close();
            cursor = null;
        }
        return  user;
    }

    private String getImeiAesKey(){

        String imei = DeviceUtil.getIMEI(ApplicationEx.getInstance());
        if(TextUtils.isEmpty(imei)){

            return "h3gjui^om(bvft%u";
        }

        if(imei.length()>=16){
            return imei.substring(0,16);
        }else{
            return ("!@#$%^&*" + imei + "asdfghjkl").substring(0, 16);
        }
    }

    private String encrypt(String originalData) throws Exception {

        if(TextUtils.isEmpty(originalData)){
            return "";
        }

        return AESUtils.encrypt( originalData,getImeiAesKey());

    }

    private String decrypt(String encryptData) throws Exception {

        if(TextUtils.isEmpty(encryptData)){
            return "";
        }

        return new String((AESUtils.decrypt(encryptData, getImeiAesKey())));

    }

    /**
     * 获得登录用户对象
     * @return
     */
    public synchronized User getLoginUser(String loginName){
        User user = new User();

        Cursor cursor = db.query(TABLE_NAME,null,"mobile = ?",new String[]{loginName},null,null,null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                try{

                    String mobile = cursor.getString(cursor.getColumnIndex("mobile"));
                    String userId = cursor.getString(cursor.getColumnIndex("userId"));
                    String accessToken = cursor.getString(cursor.getColumnIndex("accessToken"));
                    String refreshToken = cursor.getString(cursor.getColumnIndex("refreshToken"));
                    String terminalId = cursor.getString(cursor.getColumnIndex("terminalId"));
                    String gesturePassword = cursor.getString(cursor.getColumnIndex("gesturePassword"));


                    String trsPasswordFlag = "";
                    String questionFlag = "";
                    String mtsTerminalId = "";
                    String mtsMacKey = "";
                    String mtsWorkKey = "";
                    String mtsPinKey = "";
                    String authFlag = "";
                    String mtsCustomerName = "";
                    String mtsIdentifier = "";

                    Cursor cursor1 = db.query(TABLE_NAME_T, null, "mobile = ?", new String[]{mobile}, null, null, null);
                    if (cursor1 != null){
                        if (cursor1.moveToFirst()){
                            trsPasswordFlag = cursor1.getString(cursor1.getColumnIndex(MTS_TRS_PASSWORD_FLAG));
                            questionFlag = cursor1.getString(cursor1.getColumnIndex(MTS_QUESTION_FLAG));
                            mtsTerminalId = cursor1.getString(cursor1.getColumnIndex(MTS_TERMINAL_ID));
                            mtsMacKey = cursor1.getString(cursor1.getColumnIndex(MTS_MAC_KEY));
                            mtsWorkKey = cursor1.getString(cursor1.getColumnIndex(MTS_WORK_KEY));
                            mtsPinKey = cursor1.getString(cursor1.getColumnIndex(MTS_PIN_KEY));
                            authFlag = cursor1.getString(cursor1.getColumnIndex(MTS_AUTH_FLAG));
                            mtsCustomerName = cursor1.getString(cursor1.getColumnIndex(MTS_CUSTOMER_NAME));
                            mtsIdentifier = cursor1.getString(cursor1.getColumnIndex(MTS_IDENTIFIER));
                        }
                        cursor1.close();
                    }

                    Cursor merchantCursor = db.query(TABLE_NAME_MERCHANT_INFO, null, "mobile = ?", new String[]{mobile}, null, null, null);

                    if (merchantCursor != null){
                        if (merchantCursor.moveToFirst()){

                            user.setMerchantInfo(new MerchantInfo(decrypt(getCursorByKey(merchantCursor, MERCHANT_INFO))));

                        }
                        merchantCursor.close();
                    }

                    user.setTrsPasswordFlag("1".equals(trsPasswordFlag));
                    user.setQuestionFlag("1".equals(questionFlag));
                    user.setMtsTerminalId(mtsTerminalId);
                    user.setMtsMacKey(mtsMacKey);
                    user.setMtsWorkKey(mtsWorkKey);
                    user.setMtsPinKey(mtsPinKey);
                    user.setAuthFlag(authFlag);
                    user.setMtsCustomerName(mtsCustomerName);
                    user.setIdentifier(mtsIdentifier);

                    user.setExpirein(getCursorByKey(cursor, EXPIREIN));

                    user.setMsgCount(cursor.getInt(cursor.getColumnIndex(MSG_COUNT)));

                    user.setAppConfig(new AppConfig().updateConfig(getCursorByKey(cursor, APP_CONFIG)));

                    user.setLoginName(mobile);
                    user.setAccessToken(accessToken);
                    user.setRefreshToken(refreshToken);
                    user.setTerminalId(terminalId);
                    user.setUserId(userId);
                    user.setGesturePwd(gesturePassword);

                    if(DebugConfig.DEBUG){
                        LogUtil.print(getClass().getName(), "get login user = " + user.toString());
                    }

                }catch (Exception e){
                    LogUtil.print(e);
                }

            }
            cursor.close();
            cursor = null;
        }
        return  user;
    }

    private String getCursorByKey(Cursor cursor, String key){
        return cursor.getString(cursor.getColumnIndex(key));
    }

    /**
     * 是否有用户登录信息记录
     * @return
     */
    public boolean  isHasLoginUser(){
        boolean result = false;

        Cursor cursor = db.query(TABLE_NAME,new String[]{"isLogin"},"isLogin = ?",new String[]{"1"},null,null,null);
        if(cursor.getCount() > 0){
            result = true;
        }
        cursor.close();

        return result;
    }

    /**
     * 获取用户设置的支付密码信息
     * @param mobile
     * @return
     */
    public String getUserGesturePwd(String mobile){
        String result = "";

        Cursor cursor = db.query(TABLE_NAME,new String[]{"gesturePassword"},"mobile = ?",new String[]{mobile},
                null,null,null);
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex("gesturePassword"));
            }
            cursor.close();
            cursor = null;
        }
        return result;
    }


    /**
     * 设置所有用户最后一次登录标志为false
     */
    public synchronized void setAllUserLoginFalse(){
        String sql = "UPDATE "+TABLE_NAME+" SET "+KEY_IS_LOGIN
                +" = REPLACE("+KEY_IS_LOGIN+",'1','0')";
        db.execSQL(sql);
    }

    public synchronized void updateUserLoginSate(String loginName) {
        String sql = "UPDATE " + TABLE_NAME + " SET " + KEY_IS_LOGIN
                + " = 1 where mobile="+loginName;
        db.execSQL(sql);
    }

}
