package com.zr.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.zr.common.entity.CollageInfo;
import com.zr.common.entity.Inter;

import java.util.List;
import java.util.Map;

@Mapper
public interface InterMapper {
    /*Inter selectInterByServiceId(@Param("serviceId")String serviceId);
    List<CollageInfo> selectBycollageId(@Param("id")String collageid);*/
    Inter queryone(@Param("sqlMap")String sqlMap);
    List<Map<String,Object>> query(Map<String,Object> sqlMap);

}
