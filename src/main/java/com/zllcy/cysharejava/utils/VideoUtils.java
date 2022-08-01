package com.zllcy.cysharejava.utils;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
/**
 * @author zllcy
 * @date 2022/4/27 16:47
 */
public class VideoUtils {
    /**
     * 获取指定视频的帧并保存为图片
     * @param file  源视频文件地址
     * @param framefile  截取帧的图片存放地址
     * @throws Exception
     */
    public static void fetchPic(File file, String framefile) throws Exception{
        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(file);
        ff.start();
        int lenght = ff.getLengthInFrames();

        File targetFile = new File(framefile);
        int i = 0;
        Frame frame = null;
        while (i < lenght) {
            frame = ff.grabFrame();
            if ((i > lenght/100) && (frame.image != null)) {
                break;
            }
            i++;
        }

        String imgSuffix = "jpg";
        if(framefile.indexOf('.') != -1){
            String[] arr = framefile.split("\\.");
            if(arr.length>=2){
                imgSuffix = arr[1];
            }
        }

        Java2DFrameConverter converter =new Java2DFrameConverter();
        BufferedImage srcBi =converter.getBufferedImage(frame);
        try {
            ImageIO.write(srcBi, imgSuffix, targetFile);
        }catch (Exception e) {
            e.printStackTrace();
        }
        ff.stop();
    }

    /**
     * 获取视频时长，单位为秒
     * @param file 即为视频地址
     * @return 时长（秒）
     */
    public static Long getVideoTime(File file){
        Long times = 0L;
        try {
            FFmpegFrameGrabber ff = new FFmpegFrameGrabber(file);
            ff.start();
            times = ff.getLengthInTime()/(1000*1000);
            ff.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

}