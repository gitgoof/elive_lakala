package com.lakala.elive.merapply.gallery.bean;

/**
 * Created by guwh on 2016/7/8.
 */
public class FolderBean {
    private String dir;
    private String firstImgPath;
    private String name;
    private int count;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;

        int lastIndexOf=this.dir.lastIndexOf("/")+1;
        this.name=this.dir.substring(lastIndexOf);

    }

    public String getFirstImgPath() {
        return firstImgPath;
    }

    public void setFirstImgPath(String firstImgPath) {
        this.firstImgPath = firstImgPath;
    }

    public String getName() {
        return name;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
