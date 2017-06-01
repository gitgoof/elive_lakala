package com.lakala.elive.preenterpiece.response;

import java.io.Serializable;

/**
 * Created by ousachisan on 2017/3/23.
 * <p>
 * 合作方预进件的列表也查询接口的Response(ELIVE_PARTNER_APPLY_001)
 */

public class PreEnPieceSubmitInfoResponse implements Serializable {

    private String message;
    private String resultCode;
    private PartnerApplyInfo content;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public PartnerApplyInfo getContent() {
        return content;
    }

    public void setContent(PartnerApplyInfo content) {
        this.content = content;
    }

    public static class PartnerApplyInfo implements Serializable {
        //  申请ID
        public String applyId;//编辑修改工单情况下，该值必填 （后台根据此值判断新增和修改）

        public String getApplyId() {
            return applyId;
        }

        public void setApplyId(String applyId) {
            this.applyId = applyId;
        }
    }
}
