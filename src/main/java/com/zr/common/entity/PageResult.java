/**
 * @date:2019-09-15
 * @describe:界面调用接口结果返回类
 * @version:1.0
 */
package com.zr.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Map;

@Data
@ApiModel(value = "PageResult", description = "接口返回公共类")
public class PageResult {

    @ApiModelProperty(notes = "返回数据信息")
    private Map<String, Object> body;

    @ApiModelProperty(notes = "记录接口调用所发时间(单位：毫秒)")
    private Long time;

    @ApiModelProperty(notes = "状态码：1成功，2失败，0session失效，9未登录")
    private int status;

    @ApiModelProperty(notes = "返回信息")
    private String msg;

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static PageResult success(String msg){
        return success(1, msg, null);
    }

    public static PageResult success(int status, String msg){
        return success(status, msg, null);
    }

    public static PageResult success(int status, String msg, Map<String, Object> body){
        PageResult pageResult = new PageResult();
        pageResult.setStatus(status);
        pageResult.setMsg(msg);
        pageResult.setBody(body);
        return pageResult;
    }

    public static PageResult failed(String msg){
        return failed(2, msg);
    }
    public static PageResult failed(int status, String msg){
        PageResult pageResult = new PageResult();
        pageResult.setStatus(status);
        pageResult.setMsg(msg);
        return pageResult;
    }
}
