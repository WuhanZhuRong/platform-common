package com.zr.common.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.zr.common.entity.Collage;

import java.util.List;

@Mapper
public interface CollageMapper {
    List<Collage> sleectAllCollage();

    Collage selectCollageByCollageNum(@Param("collagenum")String collagenum);
    List<Collage> selectCollageByLikeName(@Param("collagename")String collagename);

}
