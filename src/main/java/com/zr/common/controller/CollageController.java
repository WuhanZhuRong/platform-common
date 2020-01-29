package com.zr.common.controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zr.common.service.CollageService;

@Api(tags = "Promote Collage Manager")
@RequestMapping("/api/v1/study/Collage")
@RestController
public class CollageController {
    @Autowired
    private CollageService collageService;
    @Autowired
    private TransportClient client;
    @ApiOperation("Collage select by id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "成功返回 JSON 格式的公共返回类", responseContainer = "int")})
    @GetMapping("getCollage")
    public ResponseEntity selectById(@RequestParam(name = "id", defaultValue = "") String id) {

            if (id.isEmpty()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            // 通过索引、类型、id向es进行查询数据
            GetResponse response = this.client.prepareGet("University", "collage", id).get();
            if (response.isExists()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            //返回查询到的数据
            return new ResponseEntity(response.getSource(), HttpStatus.OK);

    }
    @ApiOperation("Collage select")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "成功返回 JSON 格式的公共返回类")})
    @GetMapping("getAllCollage")
    public Object getAllCollage(){
        return collageService.selectAllCollage();
    }

    @ApiOperation("Collage select by collagenum")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "成功返回 JSON 格式的公共返回类", responseContainer = "String")})
    @GetMapping("getCollageByCollageNum")
    public Object getCollage(String collagenum){
        return  collageService.selectCollageByCollageNum(collagenum);
    }

    @ApiOperation("Collage select by collagename")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "成功返回 JSON 格式的公共返回类", responseContainer = "String")})
    @GetMapping("getCollageByCollageName")
    public Object getCollageByName(String collagename){
        return  collageService.selectCollageByName(collagename);
    }

}

