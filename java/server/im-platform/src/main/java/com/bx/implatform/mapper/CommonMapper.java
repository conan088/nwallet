package com.bx.implatform.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
@Repository
public interface CommonMapper {
	@Select("<script>"
			+"${sql}"
			+ "</script>")
	Object select(@Param("sql") String sql);
	@Select("<script>"
			+"select * from ${table}"
			+"<where> ${ew.sqlSegment} </where>"
			+ "</script>")
	Map<String,Object> selectByWrapper(@Param("table") String table,@Param("ew")QueryWrapper ew);
	@Select("<script>"
			+"select count(1) from ${table}"
			+"<where> ${ew.sqlSegment} </where>"
			+ "</script>")
	int selectCount(@Param("table") String table,@Param("ew")QueryWrapper ew);
	@Select("<script>"
			+"${sql}"
			+ "</script>")
	Map<String,Object> selectMap(@Param("sql") String sql);
	@Select("<script>"
			+"${sql}"
			+ "</script>")
	List<String> selects(@Param("sql") String sql);
	@Select("<script>"
			+"${sql}"
			+ "</script>")
	List<Map<String,Object>> selectMaps(@Param("sql") String sql);
	@Select("<script>"
			+"${sql}"
			+ "</script>")
	List<String> selectStrings(@Param("sql") String sql);
	@Insert("<script>"
			+"${sql}"
			+ "</script>")
	Integer insert(@Param("sql") String sql);
	
	@Update("<script>"
			+"${sql}"
			+ "</script>")
	Integer update(@Param("sql") String sql);
	@Update("<script>"
			+"update ${table} set ${ew.sqlSet}"
			+"<where> ${ew.sqlSegment} </where>"
			+ "</script>")
	Integer updateByWrapper(@Param("table") String table,@Param("ew")UpdateWrapper ew);
}