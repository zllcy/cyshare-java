package com.zllcy.cysharejava.mapper;

import com.zllcy.cysharejava.entity.Audio;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zllcy
 * @date 2022/4/20 16:33
 */
@Mapper
public interface AudioMapper {
    int insertAudio(@Param("audio") Audio audio, @Param("singerId") Integer singerId);

    int deleteAudio(Integer id);

    int updateAudio(@Param("audio") Audio audio, @Param("singerId") Integer singerId);

    List<Audio> listAudio();

    Audio searchAudio(Integer id);

    List<Audio> listAudioByUsername(String username);
}
