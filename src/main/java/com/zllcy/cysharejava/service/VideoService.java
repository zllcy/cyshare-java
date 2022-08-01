package com.zllcy.cysharejava.service;

import com.github.pagehelper.PageInfo;
import com.zllcy.cysharejava.entity.Video;

/**
 * @author zllcy
 * @date 2022/4/12 9:33
 */
public interface VideoService {
    int PAGE_SIZE = 5;

    int insertVideo(Video video) throws Exception;

    int deleteVideo(Integer id) throws Exception;

    int updateVideo(Video video) throws Exception;

    PageInfo<Video> listVideoForPage(Integer pageNum);

    PageInfo<Video> searchVideoForPageByKeyWords(Integer pageNum, String keyWords) throws Exception;

    void insertIndex(Video video) throws Exception;

    void updateIndex(Video video) throws Exception;

    void deleteIndex(Integer id) throws Exception;

    void deleteAllIndex() throws Exception;

    PageInfo<Video> listVideoForPageByUsername(String username, Integer pageNum);
}
