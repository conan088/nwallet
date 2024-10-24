package com.bx.implatform.config;

import java.lang.reflect.Method;
import java.util.Map ;
import java.util.concurrent.ConcurrentHashMap ;

import org.springframework.stereotype.Service ;

import com.bx.implatform.util.CustomException ;

import lombok.extern.slf4j.Slf4j ;
@Slf4j
@Service
public class BeanServer{
	
	private static final Map<String, Class<?>[]> METHOD_MAP = new ConcurrentHashMap<>();
	public Object exeMethod(String className,String methodName,Object... data) {
//		log.info("className  {}  methodName {}",className,methodName);
		try {
			Object bean = ApplicatCfg.getBean(className) ;
			Class<?>[] classes = METHOD_MAP.get(className+methodName) ;
			if(classes==null) {
				Method[] methods = bean.getClass().getDeclaredMethods();
				for(Method m : methods) {
					String name = m.getName();
					if (!METHOD_MAP.containsKey(name)) {
						METHOD_MAP.put(className+name,m.getParameterTypes());
					}
				}
				classes = METHOD_MAP.get(className+methodName) ;
			}
			return bean.getClass().getDeclaredMethod(methodName, classes).invoke(bean, data);
		} catch (Exception e) {
			log.info("BeanServer服务调用失败",e.getCause());
			throw new CustomException(e.getCause().getMessage());
		}
	}
}
