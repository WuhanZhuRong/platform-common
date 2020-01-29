package com.zr.common.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zr.common.service.CollageInfoService;


@Api(tags = "Promote CollageInfo Manager")
@RequestMapping("/api/v1/study/CollageInfo")
@RestController
 public class CollageInfoController {
 @Autowired
 private CollageInfoService collageInfoService;
 @ApiOperation("Collage select by id")
 @ApiResponses(value = {@ApiResponse(code = 200, message = "成功返回 JSON 格式的公共返回类", responseContainer = "String")})
 @GetMapping("getCollageInfo")
 public Object getCollageInfo(String collageid){
  return collageInfoService.selectByCollageId(collageid);
 }
}
