package com.zr.common.service;

import com.zr.common.entity.CollageInfo;
import com.zr.common.mapper.CollageInfoMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
 public class CollageInfoService {

 @Autowired
 private CollageInfoMapper collageInfoMapper;

 public CollageInfo selectByCollageId(String collageid){
  return  collageInfoMapper.selectByCollageId(collageid);
 }
}
