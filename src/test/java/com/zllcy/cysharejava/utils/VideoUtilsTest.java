package com.zllcy.cysharejava.utils;

import org.junit.Test;

import java.io.File;

/**
 * @author zllcy
 * @date 2022/4/27 16:50
 */

public class VideoUtilsTest {
    @Test
    public void getCover() {
        File file = new File("D:/DownLoad/阿里云盘/2.mp4");
        try {
            VideoUtils.fetchPic(file,"D:/DownLoad/阿里云盘/2.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
