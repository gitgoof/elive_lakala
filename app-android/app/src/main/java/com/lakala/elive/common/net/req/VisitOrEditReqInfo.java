package com.lakala.elive.common.net.req;

import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.ShopVisitInfo;
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
 * 用户拜访记录 和 商户网点信息变更
 *
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class VisitOrEditReqInfo extends RequestInfo implements Serializable{

    //VISIT : 签到  ;  EDIT:编辑
    private String visitOrEdit;

    private ShopVisitInfo   merchantVisitVO;

    private MerShopInfo     shopChangeRecordVO;

    private List<TermInfo>  terminalList;


    public ShopVisitInfo getMerchantVisitVO() {
        return merchantVisitVO;
    }

    public void setMerchantVisitVO(ShopVisitInfo merchantVisitVO) {
        this.merchantVisitVO = merchantVisitVO;
    }

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

    public String getVisitOrEdit() {
        return visitOrEdit;
    }

    public void setVisitOrEdit(String visitOrEdit) {
        this.visitOrEdit = visitOrEdit;
    }
}
