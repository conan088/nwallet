package com.bx.implatform.util;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.Iterator ;
import java.util.List ;
import java.util.Map ;

import cn.hutool.core.bean.BeanUtil ;
import cn.hutool.core.collection.CollUtil ;
import cn.hutool.core.convert.ConvertException ;

public class CloneUtil{
	
	/**
     * 单个实体转换
     *
     * @param source 源对象实体
     * @param clazz  target 类型
     * @param <T>    target 类型
     * @return target 类型实例
     */
    public static <T> T clone(Object source, Class<T> clazz) {
        try {
            if (source == null) {
                return null;
            }
            T bean = clazz.getDeclaredConstructor().newInstance();
            BeanUtil.copyProperties(source, bean);
            return bean;
        } catch (Exception e) {
            throw new ConvertException(e);
        }
    }
    /**
     * list类型转换
     *
     * @param source source
     * @param clazz  target 类型
     * @param <T>    target 类型
     * @return target 类型列表
     */
    public static <T> List<T> cloneList(List source, Class<T> clazz) {
        List<T> resultList = new ArrayList<>();
        if (!CollUtil.isEmpty(source)) {
            for (Object ele : source) {
                resultList.add(clone(ele, clazz));
            }
        }
        return resultList;
    }
    /**
     * 复制map对象
     * @explain 将paramsMap中的键值对全部拷贝到resultMap中；
     * paramsMap中的内容不会影响到resultMap（深拷贝）
     * @param paramsMap
     *     被拷贝对象
     * @param resultMap
     *     拷贝后的对象
     */
    public static void mapCopy(Map paramsMap, Map resultMap) {
        if (resultMap == null) resultMap = new HashMap();
        if (paramsMap == null) return;

        Iterator it = paramsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            resultMap.put(key, paramsMap.get(key) != null ? paramsMap.get(key) : "");

        }
    }

}
