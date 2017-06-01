package com.lakala.platform.swiper.devicemanager;
import android.util.Log;

import com.lakala.library.util.LogUtil;
import com.newland.mtype.common.Const.EmvSelfDefinedReference;
import com.newland.mtype.common.Const.EmvStandardReference;
import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.tlv.TLVPackage;
import com.newland.mtype.util.ISOUtils;

/**
 * IC卡 55域构造者
 *<br/>使用静态方法 TLVPackage createIC55TLVPackage(EmvTransInfo info) 
 *<br/>从pos机返回的IC卡数据中提取55域所需的数据封装成一个TLV包
 */
public  class ICFieldConstructor {

    /**
     * 构建IC卡 55域TLV
     * @param info	EmvTransInfo pos机发起联机交易请求时 监听返回的相关数据
     * @return 封装好的55域TLV包
     */
    public static TLVPackage createIC55TLVPackage(EmvTransInfo info){
        TLVPackage tlvPackage= ISOUtils.newTlvPackage();
        //必选
        tlvPackage.append(EmvStandardReference.APP_CRYPTOGRAM, info.getAppCryptogram());					//0x9F26,应用密文
        String crypt = String.valueOf(info.getCryptogramInformationData());
        tlvPackage.append(EmvStandardReference.CRYPTOGRAM_INFORMATION_DATA,new byte[]{info.getCryptogramInformationData()});	//0x9F27,应用信息数据
//        tlvPackage.append(EmvStandardReference.CRYPTOGRAM_INFORMATION_DATA,String.valueOf(info.getCryptogramInformationData()));	///0x9F27,应用信息数据
        tlvPackage.append(EmvStandardReference.ISSUER_APPLICATION_DATA,info.getIssuerApplicationData());			//0x9F10,发卡行应用数据
        tlvPackage.append(EmvStandardReference.UNPREDICTABLE_NUMBER,info.getUnpredictableNumber());			// 0x9F37 ,不可预知数
        tlvPackage.append(EmvStandardReference.APP_TRANSACTION_COUNTER,info.getAppTransactionCounter());		//0x9F36,应用交易计数器
        tlvPackage.append(EmvStandardReference.TERMINAL_VERIFICATION_RESULTS,info.getTerminalVerificationResults());		//0x95,终端验证结果
        tlvPackage.append(EmvStandardReference.TRANSACTION_DATE,info.getTransactionDate());						//0x9A,交易日期
        tlvPackage.append(EmvStandardReference.TRANSACTION_TYPE,String.valueOf(info.getTransactionType()));		//0x9C,交易类型

        if(null != info.getAmountAuthorisedNumeric()){
            tlvPackage.append(EmvStandardReference.AMOUNT_AUTHORISED_NUMERIC, ISOUtils.intToBCD(Integer.parseInt(info.getAmountAuthorisedNumeric()), 12, true));			//0x9F02,交易金额
        }

        if(null != info.getTransactionCurrencyCode()){
            tlvPackage.append(EmvStandardReference.TRANSACTION_CURRENCY_CODE,info.getTransactionCurrencyCode());				//0x5F2A,交易货币代码
        }

        if(null != info.getApplicationInterchangeProfile()){
            tlvPackage.append(EmvStandardReference.APPLICATION_INTERCHANGE_PROFILE,info.getApplicationInterchangeProfile());			//0x82,应用交互特征
        }

        if(null != info.getTerminalCountryCode()){
            tlvPackage.append(EmvStandardReference.TERMINAL_COUNTRY_CODE,info.getTerminalCountryCode());					//0x9F1A,终端国家代码
        }

        if(null != info.getAmountOtherNumeric()){
            tlvPackage.append(EmvStandardReference.AMOUNT_OTHER_NUMERIC, ISOUtils.intToBCD(Integer.parseInt(info.getAmountOtherNumeric()), 12, true));					//0x9F03,其他金额
        }

        tlvPackage.append(EmvStandardReference.TERMINAL_CAPABILITIES,info.getTerminal_capabilities());					//0x9F33,终端性能


        tlvPackage.append(EmvStandardReference.CVM_RESULTS,info.getCvmRslt());										//0x9F34,持卡人验证结果

        if(info.getTerminalType() != null){
            tlvPackage.append(EmvStandardReference.TERMINAL_TYPE,info.getTerminalType());								//0x9F35,终端类型
        }
        if(info.getInterface_device_serial_number() != null){
            tlvPackage.append(EmvStandardReference.INTERFACE_DEVICE_SERIAL_NUMBER,info.getInterface_device_serial_number().getBytes());		//0x9F1E,接口设备序列号
        }
        tlvPackage.append(EmvStandardReference.DEDICATED_FILE_NAME,info.getDedicatedFileName());							//0x84,专用文件名称
        if(info.getAppVersionNumberTerminal()!= null){
            tlvPackage.append(EmvStandardReference.APP_VERSION_NUMBER_TERMINAL, ISOUtils.intToBCD(Integer.parseInt(info.getAppVersionNumberTerminal()), 4, true));			//0x9F09,应用版本号
        }

        tlvPackage.append(EmvStandardReference.TRANSACTION_SEQUENCE_COUNTER,info.getTransactionSequenceCounter());		//0x9F41,交易序列计数器
//		tlvPackage.append(EmvStandardReference.ISSUER_AUTHENTICATION_DATA,info.getIssuerAuthenticationData());					//0x91，发卡行认证数据
//		tlvPackage.append(EmvStandardReference.ISSUER_SCRIPT_TEMPLATE_1,info.getIssuerScriptTemplate1());						//0x71，发卡行脚本1
//		tlvPackage.append(EmvStandardReference.ISSUER_SCRIPT_TEMPLATE_2,info.getIssuerScriptTemplate2());						//0x72，发卡行脚本2

        return tlvPackage;
    }

    public static TLVPackage createTcicc55(EmvTransInfo info){
        TLVPackage tlvPackage= ISOUtils.newTlvPackage();
        tlvPackage.append(EmvStandardReference.CRYPTOGRAM_INFORMATION_DATA,String.valueOf(info.getCryptogramInformationData()));	//0x9F27,应用信息数据
        tlvPackage.append(EmvStandardReference.APP_TRANSACTION_COUNTER,info.getAppTransactionCounter());		//0x9F36,应用交易计数器
        tlvPackage.append(EmvStandardReference.UNPREDICTABLE_NUMBER,info.getUnpredictableNumber());			// 0x9F37 ,不可预知数
        tlvPackage.append(EmvStandardReference.TERMINAL_VERIFICATION_RESULTS,info.getTerminalVerificationResults());		 //0x95,终端验证结果
        tlvPackage.append(EmvStandardReference.TRANSACTION_DATE,info.getTransactionDate());						//0x9A,交易日期
        tlvPackage.append(EmvStandardReference.TRANSACTION_TYPE,String.valueOf(info.getTransactionType()));		//0x9C,交易类型
        //tlvPackage.append(EmvStandardReference.);//sciptExectRslt
        //executeRslt
        //errorcode
        return tlvPackage;
    }
    public static TLVPackage createScpic55(EmvTransInfo emvTransInfo) {

        TLVPackage tlvPackage = ISOUtils.newTlvPackage();

        if (null == emvTransInfo || null == emvTransInfo.getScriptExecuteRslt() || emvTransInfo.getScriptExecuteRslt().length == 0) {
            return null;
        } else {

            try {
                tlvPackage.append(EmvStandardReference.APP_CRYPTOGRAM, emvTransInfo.getAppCryptogram());                    //0x9F26,应用密文
                tlvPackage.append(EmvStandardReference.ISSUER_APPLICATION_DATA, emvTransInfo.getIssuerApplicationData());            //0x9F10,发卡行应用数据
                tlvPackage.append(EmvStandardReference.UNPREDICTABLE_NUMBER, emvTransInfo.getUnpredictableNumber());            // 0x9F37 ,不可预知数
                tlvPackage.append(EmvStandardReference.APP_TRANSACTION_COUNTER, emvTransInfo.getAppTransactionCounter());        //0x9F36,应用交易计数器
                tlvPackage.append(EmvStandardReference.TERMINAL_VERIFICATION_RESULTS, emvTransInfo.getTerminalVerificationResults());         //0x95,终端验证结果
                if (emvTransInfo.getTransactionDate() == null) {
                    tlvPackage.append(EmvStandardReference.TRANSACTION_DATE, new byte[0]);
                } else {
                    tlvPackage.append(EmvStandardReference.TRANSACTION_DATE, emvTransInfo.getTransactionDate());                        //0x9A,交易日期
                }
                tlvPackage.append(EmvStandardReference.APPLICATION_INTERCHANGE_PROFILE, emvTransInfo.getApplicationInterchangeProfile() == null ? new byte[0] : emvTransInfo.getApplicationInterchangeProfile());            //0x82,应用交互特征
                if (emvTransInfo.getTerminalCountryCode() != null) {
                    tlvPackage.append(EmvStandardReference.TERMINAL_COUNTRY_CODE, emvTransInfo.getTerminalCountryCode() == null ? "" : emvTransInfo.getTerminalCountryCode());                    //0x9F1A,终端国家代码
                } else {
                    tlvPackage.append(EmvStandardReference.TERMINAL_COUNTRY_CODE, new byte[0]);                    //0x9F1A,终端国家代码
                }
                tlvPackage.append(EmvStandardReference.TERMINAL_CAPABILITIES, emvTransInfo.getTerminal_capabilities());                    //0x9F33,终端性能

                if (emvTransInfo.getInterface_device_serial_number() != null) {
                    tlvPackage.append(EmvStandardReference.INTERFACE_DEVICE_SERIAL_NUMBER, emvTransInfo.getInterface_device_serial_number().getBytes());        //0x9F1E,接口设备序列号

                } else {
                    tlvPackage.append(EmvStandardReference.INTERFACE_DEVICE_SERIAL_NUMBER, new byte[0]);        //0x9F1E,接口设备序列号

                }
                tlvPackage.append(EmvSelfDefinedReference.SCRIPT_EXECUTE_RSLT, emvTransInfo.getScriptExecuteRslt());

            } catch (Exception e) {
                LogUtil.print("ICfiedConstructor", "scpicc55 error", e);
            }

        }
        return tlvPackage;
    }

    public static TLVPackage createTcvalue(EmvTransInfo emvTransInfo){
        TLVPackage tlvPackage= ISOUtils.newTlvPackage();
        return tlvPackage;
    }
}
