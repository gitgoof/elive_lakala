package com.lakala.shoudan.common.util;

import java.math.BigDecimal;

import android.text.TextUtils;

/**
 * 数据计算(加减乘除)
 * @author jack
 */
public class MathUtil {
	
	/**
	 * 精度加法 （保留两位小数点）
	 * @param addend  加数
	 * @param augend  被加数
	 * @return
	 */
	public static String plus(String addend,String augend) {
		
		if(TextUtils.isEmpty(addend) || addend.equals("")){
			addend = "0" ;
		}
		if (TextUtils.isEmpty(addend) || augend.equals("")) {
			augend = "0";
		}
		String result = new BigDecimal(addend).add(new BigDecimal(augend)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		return result;
	}
	
	/**
	 * 精度减法 （保留两位小数点）
	 * @param subtrahend   减数
	 * @param minuend      被减数
	 * @return
	 */
	public static String minus(String subtrahend,String minuend) {
		if(TextUtils.isEmpty(subtrahend) || subtrahend.equals("")){
			subtrahend = "0" ;
		}
		if (TextUtils.isEmpty(minuend) || minuend.equals("")) {
			minuend = "0";
		}
		String result = new BigDecimal(subtrahend).subtract(new BigDecimal(minuend)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		return result;
	}
	
	/**
	 * 精度乘法（保留两位小数点）
	 * @param multiplier 乘数
	 * @param multiplicand 被乘数
	 * @return 
	 */
	public static String mutiply(String multiplier,String multiplicand) {
		if(TextUtils.isEmpty(multiplier) || multiplier.equals("")){
			multiplier = "0" ;
		}
		if (TextUtils.isEmpty(multiplicand) || multiplicand.equals("")) {
			multiplicand = "0";
		}
		String result = new BigDecimal(multiplier).multiply(new BigDecimal(multiplicand)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		return result;
	}
	
	/**
	* 提供精确的减法运算。 
	* @param subtrahend 减数
	* @param minuend  被减数 
	* @return 两个参数的差 
	*/  
	public static double sub(double subtrahend,double minuend){  
		BigDecimal subtrahendDec = new BigDecimal(Double.toString(subtrahend));  
		BigDecimal minuendDec = new BigDecimal(Double.toString(minuend));  
		return subtrahendDec.subtract(minuendDec).doubleValue();  
	}
	
	/**
	 * double加法
	 * @param addend
	 * @param augend
	 * @return
	 */
	public static double add(double addend, double augend){
		BigDecimal addendDec = new BigDecimal(Double.toString(addend));  
		BigDecimal augendDec = new BigDecimal(Double.toString(augend));  
		return addendDec.add(augendDec).doubleValue();  
	}

	
}
