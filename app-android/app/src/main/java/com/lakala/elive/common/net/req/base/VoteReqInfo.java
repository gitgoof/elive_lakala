package com.lakala.elive.common.net.req.base;

import com.lakala.elive.beans.VoteItemInfo;
import com.lakala.elive.beans.VoteTaskInfo;

import java.io.Serializable;
import java.util.List;


public class VoteReqInfo extends RequestInfo implements Serializable{

    private VoteTaskInfo voterVo;

    private List<VoteItemInfo> items;


    public VoteTaskInfo getVoterVo() {
        return voterVo;
    }

    public void setVoterVo(VoteTaskInfo voterVo) {
        this.voterVo = voterVo;
    }

    public List<VoteItemInfo> getItems() {
        return items;
    }

    public void setItems(List<VoteItemInfo> items) {
        this.items = items;
    }


}
