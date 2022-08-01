package com.zllcy.cysharejava.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zllcy.cysharejava.entity.Label;
import com.zllcy.cysharejava.entity.Video;
import com.zllcy.cysharejava.mapper.LabelMapper;
import com.zllcy.cysharejava.mapper.VideoMapper;
import com.zllcy.cysharejava.service.VideoService;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zllcy
 * @date 2022/3/28 21:51
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Value("${index.video}")
    private String indexLocation;

    @Value("${upload.location}")
    private String uploadLocation;

    @Value("${upload.cover}")
    private String coverLocation;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private LabelMapper labelMapper;

    @Override
    public int insertVideo(Video video) throws Exception {
        // 添加标签信息
        String labelName = video.getLabel();
        Label label = labelMapper.searchLabel(labelName);
        if (label == null) {
            label = new Label(labelName);
            labelMapper.insertLabel(label);
        }
        Integer labelId = label.getId();
        videoMapper.insertVideo(video, labelId);
        // 添加索引
        insertIndex(video);
        return video.getId();
    }

    @Override
    public int deleteVideo(Integer id) throws Exception {
        // 删除索引、视频和封面
        deleteIndex(id);
        Video video = videoMapper.searchVideo(id);
        File videoFile = new File(uploadLocation, video.getFilename());
        File coverFile = new File(coverLocation, video.getCover());
        videoFile.delete();
        coverFile.delete();
        return videoMapper.deleteVideo(id);
    }

    @Override
    public int updateVideo(Video video) throws Exception {
        updateIndex(video);
        // 添加标签信息
        String labelName = video.getLabel();
        Label label = labelMapper.searchLabel(labelName);
        if (label == null) {
            label = new Label(labelName);
            labelMapper.insertLabel(label);
        }
        Integer labelId = label.getId();
        return videoMapper.updateVideo(video, labelId);
    }

    @Override
    public PageInfo<Video> listVideoForPage(Integer pageNum) {
        PageHelper.startPage(pageNum, PAGE_SIZE);
        List<Video> videoList = videoMapper.listVideo();
        PageInfo<Video> videoPageInfo = new PageInfo<>(videoList);
        return videoPageInfo;
    }

    @Override
    public PageInfo<Video> searchVideoForPageByKeyWords(Integer pageNum, String keyWords) throws Exception {
        List<Video> videoList = new ArrayList<>();
        Analyzer analyzer = new IKAnalyzer();
        // 创建搜索解析器，默认查询title
        QueryParser queryParser = new QueryParser("title", analyzer);
        // 创建搜索对象
        Query query = queryParser.parse(keyWords);
        // 声明索引库位置
        Directory directory = FSDirectory.open(Paths.get(indexLocation));
        // 创建索引读取对象
        IndexReader reader = DirectoryReader.open(directory);
        // 创建索引搜索对象
        IndexSearcher searcher = new IndexSearcher(reader);
        // 使用索引搜索对象，执行搜索，返回结果集TopDocs
        TopDocs topDocs = searcher.search(query, 100);
        // 获取查询结果集
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        // 解析结果集
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = searcher.doc(docId);
            // 根据获取到的视频id获取视频，添加到集合中
//            Video video = videoMapper.searchVideo(Integer.valueOf(document.get("id")));
            Video video = new Video(Integer.valueOf(document.get("id")), document.get("username"),
                    document.get("filename"), document.get("title"), document.get("category"),
                    document.get("label"), document.get("language"), document.get("year"),
                    document.get("description"), document.get("cover"));
            videoList.add(video);
        }
        reader.close();
        Page page = new Page(pageNum, PAGE_SIZE);
        int total = videoList.size();
        page.setTotal(total);
        int startIndex = (pageNum - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, total);
        page.addAll(videoList.subList(startIndex, endIndex));
        PageInfo videoPageInfo = new PageInfo(page);
        return videoPageInfo;
    }

    @Override
    public void insertIndex(Video video) throws Exception {
        // 创建分词器
        Analyzer analyzer = new IKAnalyzer();
        // 声明索引库位置
        Directory directory = FSDirectory.open(Paths.get(indexLocation));
        // 写入索引需要的配置
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        // 创建IndexWriter写入对象，将分词后的文档写入索引库
        IndexWriter indexWriter = new IndexWriter(directory, config);
        // 创建Document
        Document document = new Document();
        document.add(new StringField("id", String.valueOf(video.getId()), Field.Store.YES));
        document.add(new StoredField("username", video.getUsername()));
        document.add(new StoredField("filename", video.getFilename()));
        document.add(new TextField("title", video.getTitle(),Field.Store.YES));
        document.add(new TextField("category", video.getCategory(), Field.Store.YES));
        document.add(new TextField("label", video.getLabel(), Field.Store.YES));
        document.add(new TextField("language", video.getLanguage(), Field.Store.YES));
        document.add(new TextField("year", video.getYear(), Field.Store.YES));
        document.add(new TextField("description", video.getDescription(), Field.Store.YES));
        document.add(new StoredField("cover", video.getCover()));
        // 写入索引库
        indexWriter.addDocument(document);
        // 释放资源
        indexWriter.close();
    }

    @Override
    public void updateIndex(Video video) throws Exception {
        // 创建分词器
        Analyzer analyzer = new IKAnalyzer();
        // 声明索引库位置
        Directory directory = FSDirectory.open(Paths.get(indexLocation));
        // 写入索引需要的配置
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        // 创建IndexWriter写入对象
        IndexWriter indexWriter = new IndexWriter(directory, config);
        // 创建Document
        Document document = new Document();
        document.add(new StringField("id", String.valueOf(video.getId()), Field.Store.YES));
        document.add(new StoredField("username", video.getUsername()));
        document.add(new StoredField("filename", video.getFilename()));
        document.add(new TextField("title", video.getTitle(),Field.Store.YES));
        document.add(new TextField("category", video.getCategory(), Field.Store.YES));
        document.add(new TextField("label", video.getLabel(), Field.Store.YES));
        document.add(new TextField("language", video.getLanguage(), Field.Store.YES));
        document.add(new TextField("year", video.getYear(), Field.Store.YES));
        document.add(new TextField("description", video.getDescription(), Field.Store.YES));
        document.add(new StoredField("cover", video.getCover()));
        // 执行更新，会把所有符合条件的Document删除，再新增
//        deleteIndex(video.getId());
        indexWriter.updateDocument(new Term("id", String.valueOf(video.getId())), document);
        indexWriter.close();
    }

    @Override
    public void deleteIndex(Integer id) throws Exception{
        // 创建分词器
        Analyzer analyzer = new IKAnalyzer();
        // 声明索引库位置
        Directory directory = FSDirectory.open(Paths.get(indexLocation));
        // 写入索引需要的配置
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        // 创建IndexWriter写入对象
        IndexWriter indexWriter = new IndexWriter(directory, config);
        // 删除索引
        indexWriter.deleteDocuments(new Term("id", String.valueOf(id)));
        indexWriter.close();
    }

    @Override
    public void deleteAllIndex() throws Exception {
        // 创建分词器
        Analyzer analyzer = new IKAnalyzer();
        // 声明索引库位置
        Directory directory = FSDirectory.open(Paths.get(indexLocation));
        // 写入索引需要的配置
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        // 创建IndexWriter写入对象
        IndexWriter indexWriter = new IndexWriter(directory, config);
        // 删除索引
        indexWriter.deleteAll();
        indexWriter.close();
    }

    @Override
    public PageInfo<Video> listVideoForPageByUsername(String username, Integer pageNum) {
        PageHelper.startPage(pageNum, PAGE_SIZE);
        List<Video> videoList = videoMapper.listVideoByUsername(username);
        PageInfo<Video> videoPageInfo = new PageInfo<>(videoList);
        return videoPageInfo;
    }

}
