package com.lakala.elive.beans;

public class FunctionMenuInfo {

    private String menuId; //Id

    private String menuName; //名称

    private int iconResId; //本地资源路径

    private String iconUrl; //服务器图片路径

    private int showIndex; //路径
    private String icon;  //icon

    private boolean showCornerMark = false;
    private int cornerMarkNum = 0;

    public String getIcon() {
        return icon;
    }

    public int getShowIndex() {
        return showIndex;
    }

    public void setShowIndex(int showIndex) {
        this.showIndex = showIndex;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getMenuId() {
        return menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public boolean isShowCornerMark() {
        return showCornerMark;
    }

    public void setShowCornerMark(boolean showCornerMark) {
        this.showCornerMark = showCornerMark;
    }

    public int getCornerMarkNum() {
        return cornerMarkNum;
    }

    public void setCornerMarkNum(int cornerMarkNum) {
        this.cornerMarkNum = cornerMarkNum;
    }

    @Override
    public String toString() {
        return "FunctionMenuInfo{" +
                "menuId='" + menuId + '\'' +
                ", menuName='" + menuName + '\'' +
                '}';
    }

}
