package com.zr.common.entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@ApiModel(value = "Collage" ,description = "Collage")
@Document(indexName = "collage",type ="docs",shards = 1,replicas = 0)
 public class Collage {
 @Id
 @ApiModelProperty(value = "collage id")
 private Integer id;

 @ApiModelProperty(value = "collage number")
 private String collagenum;

 @ApiModelProperty(value = "collage name")

 private String collagename;

 @ApiModelProperty(value = "collagestyle")
 private String collagestyle;

 @ApiModelProperty(value = "collagecity")
 private String collagecity;
}
