package com.zr.common.service;
import com.zr.common.entity.Collage;
import com.zr.common.mapper.CollageMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
 public class CollageService {
 @Autowired
 private CollageMapper collageMapper;

  public List<Collage> selectAllCollage(){
   return  collageMapper.sleectAllCollage();
  }
  public Collage selectCollageByCollageNum(String collagenum){
   return collageMapper.selectCollageByCollageNum(collagenum);
  }
  public List<Collage> selectCollageByName(String collagename){
   return collageMapper.selectCollageByLikeName(collagename);
  }

}
