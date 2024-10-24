package com.bx.implatform.util;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils ;
import org.apache.commons.lang3.StringUtils;
/**
 * 获取随机字符：RandomStringUtils.randomAlphanumeric(20)
 */
public class StringUtil extends StringUtils {
	
	public static boolean isBlank(final String cs) {
		if(cs==null||cs.trim().length()==0||"null".equals(cs.trim()))
			return true;
        return false;
    }
	public static boolean isNotBlank(Object obj) {
		if(obj==null)return false;
		return !isBlank(obj.toString());
	}
	public static String lowerFirst(String str){
		if(isBlank(str))return "";
			return str.substring(0,1).toLowerCase() + str.substring(1);
	}
	
	public static String upperFirst(String str){
		if(isBlank(str))return "";
			return str.substring(0,1).toUpperCase() + str.substring(1);
	}
	/**
	 * 替换掉HTML标签方法
	 */
	public static String replaceHtml(String html) {
		if (isBlank(html))return "";
		String regEx = "<.+?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}
	/**
	 * 替换掉空格
	 */
	public static String replaceSpace(String str) {
		return str.replaceAll("[\\t\\n\\r]", "");
	}

	/**
	 * 缩略字符串（不区分中英文字符）
	 * @param str 目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String abbr(String str, int length) {
		if (str == null)return "";
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
				currentLength += String.valueOf(c).getBytes("GBK").length;
				if (currentLength <= length - 3) {
					sb.append(c);
				} else {
					sb.append("...");
					break;
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			System.err.println(e);
		}
		return "";
	}

	/**
	 * 缩略字符串（替换html）
	 * @param str 目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String rabbr(String str, int length) {
        return abbr(replaceHtml(str), length);
	}
		
	
	/**
	 * 转换为Double类型
	 */
	public static Double toDouble(Object val){
		if (val == null){
			return 0D;
		}
		try {
			return Double.valueOf(trim(val.toString()));
		} catch (Exception e) {
			return 0D;
		}
	}

	/**
	 * 转换为Float类型
	 */
	public static Float toFloat(Object val){
		return toDouble(val).floatValue();
	}

	/**
	 * 转换为Long类型
	 */
	public static Long toLong(Object val){
		return toDouble(val).longValue();
	}

	/**
	 * 转换为Integer类型
	 */
	public static Integer toInteger(Object val){
		return toLong(val).intValue();
	}
	/**
	 * 截取2个指定字符之间的字符串
	 */
	public static String subString(String str, String strStart, String strEnd) {
        /* 找出指定的2个字符在 该字符串里面的 位置 */
        int strStartIndex = str.indexOf(strStart);
        int strEndIndex = str.indexOf(strEnd);
 
        /* index 为负数 即表示该字符串中 没有该字符 */
        if (strStartIndex < 0) {
            throw new CustomException( "字符串 :---->" + str + "<---- 中不存在 " + strStart + ", 无法截取目标字符串");
        }
        if (strEndIndex < 0) {
        	throw new CustomException( "字符串 :---->" + str + "<---- 中不存在 " + strEnd + ", 无法截取目标字符串");
        }
        return str.substring(strStartIndex, strEndIndex).substring(strStart.length());
    }
	/**
	 * Json转map
	 *Gson gson = new Gson();
      Map<String, String> reqmap = gson.fromJson(contents, Map.class);
      Map<String,Object> contentMap=gson.fromJson(message.getMessageBodyAsString(), HashMap.class);
	 */
	/**
	 * Unicode转 汉字字符串
	 */
	public static String unicodeToString(String str) {
		 
	    Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");    
	    Matcher matcher = pattern.matcher(str);
	    char ch;
	    while (matcher.find()) {
	        ch = (char) Integer.parseInt(matcher.group(2), 16);
	        str = str.replace(matcher.group(1), ch + "");    
	    }
	    return str;
	}
	/**
	 * 将 null 替换成空
	 * @return
	 */
	public static String replaceNull(String str){
		if(str==null||"null".equals(str)){
			str = "";
		}
		return str;
	}
	/**
	 * 如去除字符串 “abcdefgh” b和f之间的字符（包含b和f）
	 */
	public static String subRangeString(String body,String str1,String str2) {
        while (true) {
            int index1 = body.indexOf(str1);
            if (index1 != -1) {
                int index2 = body.indexOf(str2, index1);
                if (index2 != -1) {
                    String str3 = body.substring(0, index1) + body.substring(index2 +    str2.length(), body.length());       
                    body = str3;
                }else {
                    return body;
                }
            }else {
                return body;
            }
        }
    }
	/**
	 * 如去除字符串 “abcdefgh” b和f之间的字符（包含b和f） 
	 * *【符号相同】
	 */
	public static String subRangeString(String body,String str1) {
        while (true) {
            int index1 = body.indexOf(str1);
            if (index1 != -1) {
                int index2 = body.indexOf(str1, index1+1);//+1是为了防止相同的符号
                if (index2 != -1) {
                    String str3 = body.substring(0, index1) + body.substring(index2 +    str1.length(), body.length());       
                    body = str3;
                }else {
                    return body;
                }
            }else {
                return body;
            }
        }
    }
	/**
	 * 判断字符串中包含中文
	 */
	public static boolean isContainChinese(Object obj) {
		if(obj==null)return false;
		String str = obj+"";
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}
	/**
	 * Unicode转UTF-8
	 */
	public static String decodeUnicode(String theString) {
	    char aChar;
	    int len = theString.length();
	    StringBuffer outBuffer = new StringBuffer(len);
	    for (int x = 0; x < len;) {
	        aChar = theString.charAt(x++);
	        if (aChar == '\\') {
	            aChar = theString.charAt(x++);
	            if (aChar == 'u') {
	                // Read the xxxx
	                int value = 0;
	                for (int i = 0; i < 4; i++) {
	                    aChar = theString.charAt(x++);
	                    switch (aChar) {
	                    case '0':
	                    case '1':
	                    case '2':
	                    case '3':
	                    case '4':
	                    case '5':
	                    case '6':
	                    case '7':
	                    case '8':
	                    case '9':
	                        value = (value << 4) + aChar - '0';
	                        break;
	                    case 'a':
	                    case 'b':
	                    case 'c':
	                    case 'd':
	                    case 'e':
	                    case 'f':
	                        value = (value << 4) + 10 + aChar - 'a';
	                        break;
	                    case 'A':
	                    case 'B':
	                    case 'C':
	                    case 'D':
	                    case 'E':
	                    case 'F':
	                        value = (value << 4) + 10 + aChar - 'A';
	                        break;
	                    default:
	                        throw new IllegalArgumentException(
	                                "Malformed   \\uxxxx   encoding.");
	                    }

	                }
	                outBuffer.append((char) value);
	            } else {
	                if (aChar == 't')
	                    aChar = '\t';
	                else if (aChar == 'r')
	                    aChar = '\r';
	                else if (aChar == 'n')
	                    aChar = '\n';
	                else if (aChar == 'f')
	                    aChar = '\f';
	                outBuffer.append(aChar);
	            }
	        } else
	            outBuffer.append(aChar);
	    }
	    return outBuffer.toString();
	}
}
