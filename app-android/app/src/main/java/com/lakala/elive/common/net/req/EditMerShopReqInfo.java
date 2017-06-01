package com.lakala.elive.common.net.req;

import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.TermInfo;
import com.lakala.elive.common.net.req.base.RequestInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @author hongzhiliang
 * @version $Rev$
 * @time 2016/9/26 10:44
 * @des
 *
 * 修改网点信息
 *
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class EditMerShopReqInfo extends RequestInfo implements Serializable{

    private MerShopInfo     shopChangeRecordVO;

    private List<TermInfo>  terminalList;

    public MerShopInfo getShopChangeRecordVO() {
        return shopChangeRecordVO;
    }

    public void setShopChangeRecordVO(MerShopInfo shopChangeRecordVO) {
        this.shopChangeRecordVO = shopChangeRecordVO;
    }

    public List<TermInfo> getTerminalList() {
        return terminalList;
    }

    public void setTerminalList(List<TermInfo> terminalList) {
        this.terminalList = terminalList;
    }
}
