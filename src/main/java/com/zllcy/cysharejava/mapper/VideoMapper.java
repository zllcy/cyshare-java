package com.zllcy.cysharejava.mapper;

import com.zllcy.cysharejava.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zllcy
 * @date 2022/4/18 11:34
 */
@Mapper
public interface VideoMapper {
    int insertVideo(@Param("video") Video video, @Param("labelId") Integer labelId);

    int deleteVideo(Integer id);

    int updateVideo(@Param("video") Video video, @Param("labelId") Integer labelId);

    List<Video> listVideo();

    Video searchVideo(Integer id);


    List<Video> listVideoByUsername(String username);
}
