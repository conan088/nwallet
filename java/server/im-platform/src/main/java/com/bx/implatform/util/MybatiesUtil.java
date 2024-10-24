package com.bx.implatform.util ;

import java.lang.reflect.Field ;
import java.util.ArrayList ;
import java.util.List ;
import java.util.Map ;

import org.apache.commons.lang3.StringUtils ;

import com.baomidou.mybatisplus.annotation.TableId ;
import com.baomidou.mybatisplus.annotation.TableName ;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper ;
import com.baomidou.mybatisplus.core.metadata.OrderItem ;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils ;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction ;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda ;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page ;
import com.bx.implatform.dto.red_packet.PageQueryDTO ;


public class MybatiesUtil{
	/**
	 * 页面列表查询公共字段拼接
	 */
	@Deprecated
	public static void append(QueryWrapper<?> qw, Object param, String param_name) {
		if (param == null)
			return ;
		if ("time_start".equals(param_name))
			qw.ge("create_date", param) ;
		else if ("time_end".equals(param_name))
			qw.le("create_date", param) ;
		else if (param instanceof String) {
			param = String.valueOf(param).trim() ;
			qw.like(param_name, param) ;
		} else
			qw.eq(param_name, param) ;
	}

	/*public static <T> Page<T> getPage(int page,int limit){
		Page<T> p = new Page<>();
		p.setCurrent(page);
		p.setSize(limit);
		return p;
	}*/
	//	@Deprecated
	public static <T> Page<T> getPage(PageQueryDTO page) {
		Page<T> ipage = new Page<>() ;
		if(page==null)
			page = new PageQueryDTO();
		Integer limit = page.getLimit() ;
		ipage.setSize(limit) ;
		ipage.setCurrent(page.getPage()) ;
		//排序
		List<OrderItem> items = new ArrayList<>() ;
		if (page.getOrdery_by_asc() != null) {
			page.ordery_by_asc.forEach(x -> items.add(OrderItem.asc(x))) ;
		}
		if (page.getOrdery_by_desc() != null) {
			page.ordery_by_desc.forEach(x -> items.add(OrderItem.desc(x))) ;
		}
		if(page.getOrdery_by_desc() == null&&page.getOrdery_by_asc()==null) {
			items.add(OrderItem.desc("create_date"));
		}
		ipage.addOrder(items);
		return ipage ;
	}

	public static <T> Page<T> getPage(Map<String, Object> params) {
		Page<T> ipage = new Page<>() ;
		Long page = 1L ;
		Long limit = 30L ;
		if (params != null) {
			String p = (String) params.get("page") ;
			if (StringUtils.isNotEmpty(p)) {
				page = Long.valueOf(p) ;
			}
			String l = (String) params.get("limit") ;
			if (StringUtils.isNotEmpty(l)) {
				limit = Long.valueOf(l) ;
			}
			String date = (String) params.get("date") ;
			if (StringUtils.isNotEmpty(date)) {
				String[] split = date.split(" - ") ;
				params.put("date_start", split[0]) ;
				params.put("date_end", split[1]) ;
			}
			params.remove("page") ;
			params.remove("limit") ;
			params.remove("date") ;
		}
		ipage.setSize(limit) ;
		ipage.setCurrent(page) ;
		ipage.setDesc("create_date") ;
		return ipage ;
	}

	public static <T> QueryWrapper<T> getQW(Object dto, PageQueryDTO pageQuery) {
		QueryWrapper<T> qw = new QueryWrapper<>() ;
		if(dto==null)return qw;
		Map<String, Object> params =null;
		if(dto instanceof Map) {
			params = (Map)dto;
		}else {
			params = MapUtil.bean2Map(dto) ;
		}
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (entry.getValue() != null && !"".equals(entry.getValue())) {
				Object value = entry.getValue() ;
				qw.eq(value != null, entry.getKey(), value) ;
			}
		}
		qw.ge(StringUtils.isNotEmpty(pageQuery.getStart_date()), "DATE_FORMAT(create_date, '%Y-%m-%d')", pageQuery.getStart_date()) ;
		qw.le(StringUtils.isNotEmpty(pageQuery.getEnd_date()), "DATE_FORMAT(create_date, '%Y-%m-%d')", pageQuery.getEnd_date()) ;
		return qw ;
	}

	public static <T> QueryWrapper<T> getQWByMap(Map<String, Object> params){
		QueryWrapper<T> qw = new QueryWrapper<>();
		for(Map.Entry<String,Object> entry:params.entrySet()){
	        if(entry.getValue()!=null&&!"".equals(entry.getValue())){
	        	String key =entry.getKey();
	        	Object value =entry.getValue();
	        	switch(key){
					case "ordery_by":
						qw.orderBy(true, false, value.toString());
						break;
					case "start_date":
						qw.ge("create_date",value);
						break;
					case "end_date":
						qw.le("create_date",value);
						break;
					default:
	//    					if(entry.getKey().toLowerCase().contains("id")){
							qw.eq(entry.getKey(), value);
	//    	            	}else{
	//    	            		qw.like(entry.getKey(), value);
	//    	            	}
						break;
					}
	        	}
	    }
		return qw;
	}

	public static String getTableName(Object object) {
		return object.getClass().getAnnotation(TableName.class).value() ;
	}

	public static String getTableId(Object object) {
		Field[] fields = object.getClass().getDeclaredFields() ;
		for (Field field : fields) {
			boolean isIdField = field.isAnnotationPresent(TableId.class) ;
			if (isIdField) {
				return field.getName() ;
			}
		}
		return null ;
	}
	/**
	 * 获取属性名称
	 */
	public static <T> String getAttributesName(SFunction<T, ?> func) {
        SerializedLambda resolve = LambdaUtils.resolve(func);
        String get = resolve.getImplMethodName().replace("get", "");
        return get.toLowerCase();
    }
}