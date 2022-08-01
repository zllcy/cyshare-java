package com.zllcy.cysharejava.service;

import com.github.pagehelper.PageInfo;
import com.zllcy.cysharejava.entity.Audio;


/**
 * @author zllcy
 * @date 2022/4/20 16:34
 */
public interface AudioService {
    int PAGE_SIZE = 4;

    int insertAudio(Audio audio) throws Exception;

    int deleteAudio(Integer id) throws Exception;

    int updateAudio(Audio audio) throws Exception;

    PageInfo<Audio> listAudioForPage(Integer pageNum);

    PageInfo<Audio> searchAudioForPageByKeyWords(Integer pageNum, String keyWords) throws Exception;

    void insertIndex(Audio audio) throws Exception;

    void updateIndex(Audio audio) throws Exception;

    void deleteIndex(Integer id) throws Exception;

    void deleteAllIndex() throws Exception;

    PageInfo<Audio> listAudioForPageByUsername(String username, Integer pageNum);
}
