package com.zllcy.cysharejava.controller;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.zllcy.cysharejava.common.Result;
import com.zllcy.cysharejava.config.NonStaticResourceHttpRequestHandler;
import com.zllcy.cysharejava.entity.Audio;
import com.zllcy.cysharejava.entity.Singer;
import com.zllcy.cysharejava.service.AudioService;
import com.zllcy.cysharejava.service.SingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author zllcy
 * @date 2022/4/20 16:35
 */
@RestController
@RequestMapping("/audio")
public class AudioController {
    @Value("${upload.location}")
    private String uploadLocation;

    @Value("${upload.cover}")
    private String coverLocation;

    @Value("${download.location}")
    private String downloadLocation;

    @Autowired
    private NonStaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler;

    @Autowired
    private AudioService audioService;

    @Autowired
    private SingerService singerService;

    @GetMapping("/listAudio/{pageNum}")
    public Result listAudioForPage(@PathVariable("pageNum") Integer pageNum) {
        PageInfo<Audio> audioPageInfo = audioService.listAudioForPage(pageNum);
        return Result.success(audioPageInfo);
    }

    @GetMapping("/listAudioByUsername/{username}/{pageNum}")
    public Result listAudioForPage(@PathVariable("username") String username, @PathVariable("pageNum") Integer pageNum) {
        PageInfo<Audio> audioPageInfo = audioService.listAudioForPageByUsername(username, pageNum);
        return Result.success(audioPageInfo);
    }

    @PostMapping("/insertAudio")
    public Result insertVideo (Audio audio, @RequestParam(value = "file") MultipartFile multipartFile) throws Exception {
        // 获取上传文件名
        String audioFilename = multipartFile.getOriginalFilename();

        // 上传视频
        if (StrUtil.isNotEmpty(audioFilename)) {
            int suffixIndex = audioFilename.lastIndexOf('.');
            // 获取后缀
            String suffix = audioFilename.substring(suffixIndex);
            // 当前时间的毫秒值作为前缀
            long prefix = System.currentTimeMillis();
            // 文件名
            String fullFileName = prefix + suffix;
            // 设置视频文件名
            audio.setFilename(fullFileName);
            // 拼接路径
            File parentPath = new File(uploadLocation);
            // 条件成立：服务器路径不存在创建路径
            if (!parentPath.exists()) {
                parentPath.mkdirs();
            }
            // 全路径
            File fullPath = new File(parentPath, fullFileName);
            // 上传
            multipartFile.transferTo(fullPath);
        } else {
            return Result.fail("音频不存在");
        }

        audioService.insertAudio(audio);

        return Result.success(null);
    }

    @PutMapping("/updateAudio")
    public Result updateAudio(@RequestBody Audio audio) throws Exception {
        int i = audioService.updateAudio(audio);
        if (i != 1) {
            return Result.fail("修改失败");
        }
        return Result.success(null);
    }

    @DeleteMapping("/deleteAudio/{id}")
    public Result deleteAudio(@PathVariable("id") Integer id) throws Exception {
        int i = audioService.deleteAudio(id);
        if (i != 1) {
            return Result.fail("删除失败");
        }
        return Result.success(null);
    }

    @GetMapping("/searchAudio/{keyWords}/{pageNum}")
    public Result searchAudio(@PathVariable("keyWords") String keyWords, @PathVariable("pageNum") Integer pageNum) throws Exception {
        PageInfo<Audio> audioPageInfo = audioService.searchAudioForPageByKeyWords(pageNum, keyWords);
        return Result.success(audioPageInfo);
    }

    @RequestMapping("/play/{filename}")
    public void play(HttpServletRequest request, HttpServletResponse response, @PathVariable("filename") String filename) throws Exception{
        // 拼接文件名，生成全路径
        String fullPath =  uploadLocation + filename;
        File file = new File(fullPath);
        if (file.exists()) {
            request.setAttribute(NonStaticResourceHttpRequestHandler.ATTR_FILE, fullPath);
            nonStaticResourceHttpRequestHandler.handleRequest(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        }
    }

    @GetMapping("/listSinger")
    public Result listSinger() {
        List<Singer> singerList = singerService.listSinger();
        return Result.success(singerList);
    }
}
