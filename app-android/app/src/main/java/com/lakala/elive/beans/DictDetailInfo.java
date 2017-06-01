package com.lakala.elive.beans;


public class DictDetailInfo {

    private String  dictKey; //字典key
    private String  dictName; //字典name

    public DictDetailInfo(String dictKey, String dictName) {
        this.dictKey = dictKey;
        this.dictName = dictName;
    }



    public String getDictKey() {
        return dictKey;
    }

    public void setDictKey(String dictKey) {
        this.dictKey = dictKey;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    @Override
    public String toString() {
        return "DictDetailInfo{" +
                "dictKey='" + dictKey + '\'' +
                ", dictName='" + dictName + '\'' +
                '}';
    }
}
