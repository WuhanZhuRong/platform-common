package com.zr.common.entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="User",description="User")
public class User {
	@ApiModelProperty(value="user  name")
	private String name;
	@ApiModelProperty(value="user sex")
	private String sex;
	
	@ApiModelProperty(value="user age")
	private Integer age;

}
