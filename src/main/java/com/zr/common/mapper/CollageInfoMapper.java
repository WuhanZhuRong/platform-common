package com.zr.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.zr.common.entity.CollageInfo;

@Mapper
public interface CollageInfoMapper {
     CollageInfo selectByCollageId(@Param("collageid") String collagerid);
}
