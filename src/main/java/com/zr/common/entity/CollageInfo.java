package com.zr.common.entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@ApiModel(value = "CollageInfo" ,description = "CollageInfo")
@Document(indexName = "collageinfo",type ="docs",shards = 1,replicas = 0)
 public class CollageInfo {
 @Id
 @ApiModelProperty(value = "collageinfo id")
 private Integer id;

 @ApiModelProperty(value = "collageinfo collageid")
 private String collageid;

 @ApiModelProperty(value = "collage manager")
 private String collagemanager;

 @ApiModelProperty(value = "university number")

 private Integer universityno;

 @ApiModelProperty(value = "collagestudent")
 private Integer collagestudent;

}
