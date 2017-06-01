package com.lakala.elive.beans;

/**
 * @author hongzhiliang
 * @version $Rev$
 * @time 2016/9/26 12:08
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class UserLoginInfo {

    //用户编码
    private String userId;

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getUserSource() {
        return userSource;
    }

    public void setUserSource(String userSource) {
        this.userSource = userSource;
    }

    //系统来源  //1.ELIVE 2.BMCP
    private String userSource;


    //机构名称
    private String organName;
    //机构ID
    private String organId;

    //用户名称
    private String userName;

    //登录名
    private String loginName;

    //用户手机号
    private String userMobile;

    //用户邮箱
    private String userMail;

    //用户性别
    private String sexType;

    //登录动态授权令牌
    private String authToken;

    //物理终端验证状态
    private String devChkStatus;

    //密码强制修改状态
    private String isNeedUpdatePwd;

    public String getDevChkStatus() {
        return devChkStatus;
    }

    public void setDevChkStatus(String devChkStatus) {
        this.devChkStatus = devChkStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getSexType() {
        return sexType;
    }

    public void setSexType(String sexType) {
        this.sexType = sexType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    public String getIsNeedUpdatePwd() {
        return isNeedUpdatePwd;
    }

    public void setIsNeedUpdatePwd(String isNeedUpdatePwd) {
        this.isNeedUpdatePwd = isNeedUpdatePwd;
    }


    @Override
    public String toString() {
        return "UserLoginInfo{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", loginName='" + loginName + '\'' +
                ", userMobile='" + userMobile + '\'' +
                ", userMail='" + userMail + '\'' +
                ", sexType='" + sexType + '\'' +
                ", authToken='" + authToken + '\'' +
                ", devChkStatus='" + devChkStatus + '\'' +
                ", isNeedUpdatePwd='" + isNeedUpdatePwd + '\'' +
                '}';
    }
}
