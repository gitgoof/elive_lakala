package com.lakala.shoudan.activity.shoudan.finance.bean;

import com.alibaba.fastjson.JSON;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.ProfitList;

import java.util.List;

/**
 * Created by HJP on 2015/10/20.
 */
public class ProfitBeans {
    private List<ProfitList> List;
    private String _Guid;
    private String PageNum;

    public static ProfitBeans parserStringToProfitBeans(String str){
        return JSON.parseObject(str, ProfitBeans.class);
    }
    public List<ProfitList> getLists() {
        return List;
    }

    public void setLists(List<ProfitList> lists) {
        this.List = lists;
    }

    public String get_Guid() {
        return _Guid;
    }

    public void set_Guid(String _Guid) {
        this._Guid = _Guid;
    }

    public String getPageNum() {
        return PageNum;
    }

    public void setPageNum(String pageNum) {
        PageNum = pageNum;
    }
}

