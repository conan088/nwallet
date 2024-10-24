package com.bx.implatform.util;

import java.math.BigDecimal;
import java.util.Objects;

public class MathUtil{

	/*  
	 * 小数精确的位数  
	 */
	private static final int DEF_DIV_SCALE=8;
	public static final String ZERO="0";
	public static final String ADD="+";
	public static final String SUBTRACT="-";
	
	/**
	 * 获取百分比占比 
	 * @param value
	 * @param scale
	 * @return
	 */
	public static String percentage(Object value,int scale) {
		return new BigDecimal(value.toString()).multiply(new BigDecimal(100)).setScale(scale,BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString()+"%";
	}
	public static String retain(Object value,int scale) {
		return new BigDecimal(value.toString()).setScale(scale,BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
	}
	public static BigDecimal retainB(Object value,int scale) {
		return new BigDecimal(value.toString()).setScale(scale,BigDecimal.ROUND_DOWN).stripTrailingZeros();
	}
	public static void main(String[] args) {
		System.out.println(retainB("0.167876", 4)) ;
	}
	public static BigDecimal getRandomRedPacketBetweenMinAndMax(BigDecimal min, BigDecimal max){
		   float minF = min.floatValue();
		   float maxF = max.floatValue();


		   //生成随机数
		   BigDecimal db = new BigDecimal(Math.random() * (maxF - minF) + minF);

		   //返回保留两位小数的随机数。不进行四舍五入
		   return db.setScale(2,BigDecimal.ROUND_DOWN);
		}
	/**  
	 * 提供精确的加法运算。  
	 *  
	 * @param v1  
	 *            被加数  
	 * @param v2  
	 *            加数  
	 * @return 两个参数的和  
	 */
	public static BigDecimal add(Object v1,Object v2){
		BigDecimal b1=new BigDecimal(v1.toString());
		BigDecimal b2=new BigDecimal(v2.toString());
		return b1.add(b2);
	}

	public static double add(double...args){
		double sum=0;
		for(double a:args){
			BigDecimal b1=new BigDecimal(Double.toString(sum));
			BigDecimal b2=new BigDecimal(Double.toString(a));
			sum=b1.add(b2).doubleValue();
		}
		return sum;
	}
	public static BigDecimal add(Object...args){
		BigDecimal sum=new BigDecimal(0);
		for(Object a:args){
			if(a!=null) {
				sum=sum.add(new BigDecimal(a.toString()));
			}
		}
		return sum;
	}

	/**  
	 * 提供精确的加法运算。  
	 *  
	 * @param v1  
	 *            被加数  
	 * @param v2  
	 *            加数  
	 * @return 两个参数的和  
	 */
	public static BigDecimal add(String v1,String v2){
		BigDecimal b1=new BigDecimal(v1);
		BigDecimal b2=new BigDecimal(v2);
		return b1.add(b2);
	}
	/**  
	 * 比较大小 如果v1 大于等于v2 则 返回true 否则false  
	 * @param v1  
	 * @param v2  
	 * @return  
	 */
	public static boolean strcompareTo2(String v1,String v2){
		BigDecimal b1=new BigDecimal(v1);
		BigDecimal b2=new BigDecimal(v2);
		int bj=b1.compareTo(b2);
		boolean res;
		if(bj>=0)
			res=true;
		else
			res=false;
		return res;
	}
	/**  
	 * 比较大小 如果v1 大于v2 则 返回true 否则false  
	 * @param v1  
	 * @param v2  
	 * @return  
	 */
	public static boolean strcompareToGt(String v1,String v2){
		BigDecimal b1=new BigDecimal(v1);
		BigDecimal b2=new BigDecimal(v2);
		int bj=b1.compareTo(b2);
		boolean res;
		if(bj>0)
			res=true;
		else
			res=false;
		return res;
	}

	/**  
	 * 提供精确的加法运算。 String  
	 *  
	 * @param v1  
	 *            被加数  
	 * @param v2  
	 *            加数  
	 * @return 两个参数的和  
	 */
	public static String strAdd(String v1,String v2,int scale){
		if(scale<0){
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1=new BigDecimal(v1);
		BigDecimal b2=new BigDecimal(v2);
		return b1.add(b2).setScale(scale,BigDecimal.ROUND_DOWN).toString();
	}

	/**  
	 * 提供精确的减法运算。  
	 *  
	 * @param v1  
	 *            被减数  
	 * @param v2  
	 *            减数  
	 * @return 两个参数的差  
	 */
	public static double sub(double v1,double v2){
		BigDecimal b1=new BigDecimal(Double.toString(v1));
		BigDecimal b2=new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**  
	 * 提供精确的减法运算。  
	 *  
	 * @param v1  
	 *            被减数  
	 * @param v2  
	 *            减数  
	 * @return 两个参数的差  
	 */
	public static BigDecimal sub(Object v1,Object v2){
		BigDecimal b1=new BigDecimal(v1.toString());
		BigDecimal b2=new BigDecimal(v2.toString());
		return b1.subtract(b2);
	}

	public static BigDecimal sub(Object v1,Object...arg){
		BigDecimal b1=new BigDecimal(v1.toString());
		for(Object x:arg) {
			b1 = b1.subtract(new BigDecimal(x.toString()));
		}
		return b1;
	}

	/**  
	 * 对一个数字取精度  
	 * @param v  
	 * @param scale  
	 * @return  
	 */
	public static BigDecimal round(String v,int scale){
		if(scale<0){
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b=new BigDecimal(v);
		BigDecimal one=new BigDecimal("1");
		return b.divide(one,scale,BigDecimal.ROUND_DOWN);
	}
	/**  
	 * 提供精确的乘法运算。  
	 *  
	 * @param v1  
	 *            被乘数  
	 * @param v2  
	 *            乘数  
	 * @return 两个参数的积  
	 */
	public static BigDecimal mul(Object b1,Object ...arg){
		BigDecimal result = new BigDecimal(b1.toString());
		for(Object x:arg) {
			result = result.multiply(new BigDecimal(x.toString()));
		}
		return result;
	}
	/**  
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。  
	 *  
	 * @param v1  
	 *            被除数  
	 * @param v2  
	 *            除数  
	 * @return 两个参数的商
	 * 去除小数点后的0：.stripTrailingZeros().toPlainString()  
	 */
	public static BigDecimal div(Object v1,Object v2){
		return div(v1,v2,8).stripTrailingZeros();
	}
	/**
	 * 清除末尾的0
	 * @param v1
	 * @return
	 */
	public static String clearZero(String v1){
		return new BigDecimal(v1).stripTrailingZeros().toPlainString();
	}


	/**  
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。  
	 *  
	 * @param v1  
	 *            被除数  
	 * @param v2  
	 *            除数  
	 * @param scale  
	 *            表示需要精确到小数点以后几位。  
	 * @return 两个参数的商  
	 */
	public static BigDecimal div(Object v1,Object v2,int scale){
		if(scale<0){
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1=new BigDecimal(v1.toString());
		BigDecimal b2=new BigDecimal(v2.toString());
		return b1.divide(b2,scale,BigDecimal.ROUND_DOWN);
	}
	public static BigDecimal divs(Object v1,Object v2,Object v3,int scale){
		if(scale<0){
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1=new BigDecimal(v1.toString());
		BigDecimal b2=new BigDecimal(v2.toString());
		BigDecimal b3=new BigDecimal(v3.toString());
		return b1.divide(b2,10,BigDecimal.ROUND_DOWN).divide(b3,scale,BigDecimal.ROUND_DOWN);
	}

	/**  
	 * 取余数  string  
	 * @param v1  
	 * @param v2  
	 * @param scale  
	 * @return  
	 */
	public static BigDecimal strRemainder(String v1,String v2,int scale){
		if(scale<0){
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1=new BigDecimal(v1);
		BigDecimal b2=new BigDecimal(v2);
		return b1.remainder(b2).setScale(scale,BigDecimal.ROUND_DOWN);
	}

	/**  
	 * 取余数  string  
	 * @param v1  
	 * @param v2  
	 * @param scale  
	 * @return  string  
	 */
	public static String strRemainder2Str(String v1,String v2,int scale){
		if(scale<0){
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1=new BigDecimal(v1);
		BigDecimal b2=new BigDecimal(v2);
		return b1.remainder(b2).setScale(scale,BigDecimal.ROUND_DOWN).toString();
	}

	/**  
	 * 比较大小 如果v1 大于v2 则 返回true 否则false  
	 * @param v1  
	 * @param v2  
	 * @return  
	 */
	public static boolean compareTo(Object v1,Object v2){
		BigDecimal b1=new BigDecimal(v1.toString());
		BigDecimal b2=new BigDecimal(v2.toString());
		int bj=b1.compareTo(b2);
		boolean res;
		if(bj>0)
			res=true;
		else
			res=false;
		return res;
	}

	/**  
	 * 比较大小 如果v1 大于等于v2 则 返回true 否则false  
	 * @param v1  
	 * @param v2  
	 * @return  
	 */
	public static boolean compareTo2(Object b1,Object b2){
		int bj=new BigDecimal(b1.toString()).compareTo(new BigDecimal(b2.toString()));
		boolean res;
		if(bj>=0)
			res=true;
		else
			res=false;
		return res;
	}

	/**  
	 * 比较大小 如果v1 等于v2 则 返回true 否则false  
	 * @param v1  
	 * @param v2  
	 * @return  
	 */
	public static boolean compareTo3(String v1,String v2){
		BigDecimal b1=new BigDecimal(v1);
		BigDecimal b2=new BigDecimal(v2);
		int bj=b1.compareTo(b2);
		boolean res;
		if(bj==0)
			res=true;
		else
			res=false;
		return res;
	}
	public static boolean compareTo3(BigDecimal b1,BigDecimal b2){
		int bj=b1.compareTo(b2);
		boolean res;
		if(bj==0)
			res=true;
		else
			res=false;
		return res;
	}
	/**  
	 * 比较大小 如果v1 小于v2 则 返回true 否则false  
	 * @param v1  
	 * @param v2  
	 * @return  
	 */
	public static boolean compareTo4(String v1,String v2){
		BigDecimal b1=new BigDecimal(v1);
		BigDecimal b2=new BigDecimal(v2);
		int bj=b1.compareTo(b2);
		boolean res;
		if(bj<0)
			res=true;
		else
			res=false;
		return res;
	}
	public static boolean compareTo4(BigDecimal b1,BigDecimal b2){
		int bj=b1.compareTo(b2);
		boolean res;
		if(bj<0)
			res=true;
		else
			res=false;
		return res;
	}

	/**  
	 * 取余数  BigDecimal  
	 * @param v1  
	 * @param v2  
	 * @param scale  
	 * @return  
	 */
	public static BigDecimal bigRemainder(BigDecimal v1,BigDecimal v2,int scale){
		if(scale<0){
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		return v1.remainder(v2).setScale(scale,BigDecimal.ROUND_DOWN);
	}

	/**  
	 * 提供精确的小数位四舍五入处理。  
	 *  
	 * @param v  
	 *            需要四舍五入的数字  
	 * @param scale  
	 *            小数点后保留几位  
	 * @return 四舍五入后的结果  
	 */
	public static double round(double v,int scale){
		if(scale<0){
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b=new BigDecimal(Double.toString(v));
		BigDecimal one=new BigDecimal("1");
		return b.divide(one,scale,BigDecimal.ROUND_DOWN).doubleValue();
	}

	/**  
	 * 提供精确的小数位四舍五入处理。string  
	 *  
	 * @param v  
	 *            需要四舍五入的数字  
	 * @param scale  
	 *            小数点后保留几位  
	 * @return 四舍五入后的结果  
	 */
	public static String strRound(String v,int scale){
		if(scale<0){
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b=new BigDecimal(v);
		return b.setScale(scale,BigDecimal.ROUND_DOWN).toString();
	}

	/**
	 * 获取两个数之间的随机数 
	 * @param x
	 * @param y
	 * @return
	 */
	public static int getRandom(int x,int y){
		int num=-1;
		// 说明：两个数在合法范围内，并不限制输入的数哪个更大一些
		if(x<0||y<0){
			return num;
		}else{
			int max=x>y?x:y;
			int min=x<y?x:y;
			int mid=max-min;// 求差
			// 产生随机数
			num=(int)(Math.random()*(mid+1))+min;
		}
		return num;
	}

	/**
	 * double类型如果小数点后为零显示整数否则保留 返回String
	 * @param num
	 * @return
	*/
	public static String doubleTrans(double num){
		String number1=String.format("%.6f",num);// 只保留小数点后6位
		double number2=Double.parseDouble(number1);// 類型轉換
		if(Math.round(number2)-number2==0){
			return String.valueOf((long)number2);
		}
		return String.valueOf(number2);
	}
	/**
	 * 验证金额是不是约等于0  向下取整
	 * @param num
	 * @param scale 精确小数点位数
	 * @return
	 */
	public static boolean validateZero(Object num,int scale) {
		return Objects.equals(new BigDecimal(num.toString()).setScale(scale,BigDecimal.ROUND_DOWN).stripTrailingZeros(),new BigDecimal("0"));
	}
}