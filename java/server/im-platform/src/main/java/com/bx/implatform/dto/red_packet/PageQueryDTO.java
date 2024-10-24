package com.bx.implatform.dto.red_packet;

import java.io.Serializable ;
import java.util.List ;

import io.swagger.annotations.ApiModel ;
import io.swagger.annotations.ApiModelProperty ;
import lombok.Data;
@Data
@ApiModel
public class PageQueryDTO implements Serializable{

	private static final long serialVersionUID = 1L ;
	@ApiModelProperty(value = "当前页",example = "1")
	public Integer page=1;//当前页
	@ApiModelProperty(value = "分页单位",example = "10")
	public Integer limit=10;//分页单位
	@ApiModelProperty(value = "起始时间")
	public String start_date;
	@ApiModelProperty(value = "结束时间")
	public String end_date;
	@ApiModelProperty(value = "字段增序排序")
	public List<String> ordery_by_asc;
	@ApiModelProperty(value = "字段降序排序")
	public List<String> ordery_by_desc;
	
	public void setPage(Integer page){
		if(page!=null)
			this.page=page;
	}
	public void setLimit(Integer limit){
		if(limit!=null)
			this.limit=limit;
	}


}