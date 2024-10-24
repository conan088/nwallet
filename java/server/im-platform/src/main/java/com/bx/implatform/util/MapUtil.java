package com.bx.implatform.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier ;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.thread.ThreadException ;



public class MapUtil {
	
	public static final Map<String,Object> getMap(){
		return new HashMap<String,Object>();
	}
	public static Map<String, Object> bean2Map(Object obj){
        try{
        	Map<String, Object> map = new HashMap<>();
        	Class<?> clazz = obj.getClass();
        	for (Field field : clazz.getDeclaredFields()) {
        		field.setAccessible(true);
        		String fieldName = field.getName();
        		Object value = field.get(obj);
        		map.put(fieldName, value);
        	}
        	return map;
		}catch(Exception e){
			e.printStackTrace();
			throw new ThreadException("系统运行异常："+e.getMessage());
		}  
	}
	public static Map<String, String> bean2StringMap(Object obj){
		try{
			Map<String, String> map = new HashMap<>();
			Class<?> clazz = obj.getClass();
			for (Field field : clazz.getDeclaredFields()) {
				field.setAccessible(true);
				String fieldName = field.getName();
				Object value = field.get(obj);
				if(value!=null) {
					map.put(fieldName, value.toString());
				}
			}
			return map;
		}catch(Exception e){
			e.printStackTrace();
			throw new ThreadException("系统运行异常："+e.getMessage());
		}  
	}
	/**
	 * 反射map-->bean
	 * MapUtil.map2bean((Map)baseController.redis.get(token),User.class);
	 * 数据类型不能出错
	 */
	public static <T> T map2bean(Map<String,Object> map,Class<T> clz){
		if(map==null)
			return null;
		try{
			T obj = clz.newInstance();
			BeanInfo beanInfo = Introspector.getBeanInfo(clz, Object.class);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			for(PropertyDescriptor pd:pds){
				Object value = map.get(pd.getName());
				Method setter = pd.getWriteMethod();
				if(value==null)continue;
				setter.invoke(obj, value);
			}
			return  obj;
		}catch(Exception e){
			e.printStackTrace();
			throw new ThreadException("Map转化对象错误："+map);
		}
	}
	public static <T> T mapToObject(Map<String, Object> map, Class<T> beanClass){
		try {
			if (map == null) {
				return null ;
			}
			T obj = beanClass.newInstance() ;
			Field[] fields = obj.getClass().getDeclaredFields() ;
			for (Field field : fields) {
				int mod = field.getModifiers() ;
				if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
					continue ;
				}
				field.setAccessible(true) ;
				field.set(obj, map.get(field.getName())) ;
			}
			return obj ;
		} catch (Exception e) {
			throw new ThreadException("对象转换异常："+e.getMessage());
		}
	}
	/**
	 * 
	 创建Map
	 Map<String,String> map = new HashMap<>();
		map.put("", "");
		map.put("", "");
		map.put("", "");
	 */
	public static  Map<String, Object> mapMerge(Map<String,Object> ...maps) {
		 Map<String, Object> combineResultMap = new HashMap<>();
		 for(Map<String,Object> map:maps) {
			 combineResultMap.putAll(map);
		 }
		 return combineResultMap;
	}
}
