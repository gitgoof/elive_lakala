package com.lakala.elive.preenterpiece;

import android.content.Context;
import android.text.TextUtils;

import com.lakala.elive.beans.MerAttachFile;
import com.lakala.elive.common.utils.PhotoUtils;

import java.io.File;

/**
 */
public class PrePhotoFileFactory {

    private final String ID_CARD_FRONT = "ID_CARD_FRONT";//身份证正面
    private final String ID_CARD_FRONT_OCR = "2";//身份证正面ocr

    private final String ID_CARD_BEHIND = "ID_CARD_BEHIND";//身份证反面
    private final String ID_CARD_BEHIND_OCR = "6";//身份证反面ocr

    private final String BANK_CARD = "BANK_CARD";//银行卡
    private final String BANK_CARD_OCR = "1";//银行卡ocr

    private final String BUSINESS_LICENCE = "BUSINESS_LICENCE";//营业执照照片
    private final String BUSINESS_LICENCE_OCR = "3";//营业执照照片ocr

    private final String SHOPINNER = "SHOPINNER";//营业场所照片

    private final String XY = "XY";//商户协议
    private final String KH = "KH";//开户许可
    private final String SUP_PROOF = "SUP_PROOF";//附件 补充
    private final String MT = "MT";//门头照
    private final String SYT = "SYT";//机器照
    private final String OTHERS = "OTHERS";//其他

    private Context mContext;
    private String photoType;//区分照片类型
    private String comments;//协议与附件有多张,用于标识第几张
    private int photoUploadType;//区分图片上传请求
    private String segment;//   APPLY_ACCOUNT:结算账户验证附件 APPLY_LICENSE:营业执照 APPLY_SUP_PROOF：补充证明

    public PrePhotoFileFactory(Context context) {
        this.mContext = context;
    }

    /**
     * 设置照片类型
     *
     * @param type            区分照片类型
     * @param segment         APPLY_ACCOUNT:结算账户验证附件 APPLY_LICENSE:营业执照 APPLY_SUP_PROOF：补充证明
     * @param comment         协议与附件有多张,用于标识第几张   其他类型客串空
     * @param photoUploadType 区分图片上传请求 在onSuccess中区分做对应的处理
     */
    public void setPhotoType(String type, String segment, String comment, int photoUploadType) {
        this.photoType = type;
        this.segment = segment;
        this.comments = comment;
        this.photoUploadType = photoUploadType;
    }

    /**
     * 添加需要上传的图片
     *
     * @param path 图片路径
     */
    public MerAttachFile photoFileCreate(String path) {
        if (TextUtils.isEmpty(photoType) || TextUtils.isEmpty(path)) {
            return null;
        }

        File file = PhotoUtils.compressImage(new File(path), mContext);//压缩图片
        MerAttachFile merAttachFile = new MerAttachFile();
        merAttachFile.photoUploadType = this.photoUploadType;//这个字段不需要上传服务器,只用于判断
        merAttachFile.setFileType(photoType);
        merAttachFile.setFileName(file.getName());
        merAttachFile.setFileContent(PhotoUtils.fileBase64String(file.getAbsolutePath()));
        merAttachFile.setSegment(this.segment);

        if (XY.equals(photoType) || KH.equals(photoType) || SUP_PROOF.equals(photoType)//商户协议,开户许可证,附件,营业场所,门头照,机器照都不要需要ocr,其他不需要ocr
                || SHOPINNER.equals(photoType) || MT.equals(photoType) || SYT.equals(photoType) || OTHERS.equals(photoType)) {
            merAttachFile.setIsOcr("0");
        } else {
            merAttachFile.setIsOcr("1");
            switch (photoType) {
                case ID_CARD_FRONT://身份证识别
                    merAttachFile.setOcrType(ID_CARD_FRONT_OCR);
                    break;
                case ID_CARD_BEHIND://身份证背面
                    merAttachFile.setOcrType(ID_CARD_BEHIND_OCR);
                    break;
                case BANK_CARD://银行卡
                    merAttachFile.setOcrType(BANK_CARD_OCR);
                    break;
                case BUSINESS_LICENCE://银行卡
                    merAttachFile.setOcrType(BUSINESS_LICENCE_OCR);
                    break;
            }
        }
        //商户协议,附件,门头照都都有多张,需要添加comments区分排序
        if (XY.equals(this.photoType) || SUP_PROOF.equals(photoType) || MT.equals(photoType)) {
            merAttachFile.setComments(this.comments);
        }
        return merAttachFile;
    }
}
