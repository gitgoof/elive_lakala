package com.lakala.library.jni;

public class LakalaNative {

    /**
     * 获取登陆公钥
     * @return
     */
	public static native String getLoginPublicKey();

    /**
     * 获取密码公钥
     * @param isDebug 是否debug模式
     * @return
     */
    public static native String getPasswordPublicKey(boolean isDebug);

    /**
     * 获取密码键盘使用的私钥
     * @return
     */
    public static native String getKeyBoardPrivateKey();
	
	/**
	 * 对密码加密
	 * @param personalKeyCipher	个人密钥密文
	 * @param workKeyCipher		工作密钥密文
	 * @param password			密码明文
	 * @return
	 */
	public static native String encryptPwd(
            String personalKeyCipher,
            String workKeyCipher,
            String password,
            boolean isDebug);
	
	/**
	 * 生成mac域
	 * @param personalKeyCipher 个人密钥密文
	 * @param macKeyCipher 	    mac密钥密文
	 * @param source			参与运算的mac域
	 * @param isDebug			是否debug状态，debug状态会打印log
	 * @return
	 */
	public static native String generateMac(
            String personalKeyCipher,
            String macKeyCipher,
            String source,
            boolean isDebug);
	
	static{
		System.loadLibrary("LakalaNative");
	}
}
