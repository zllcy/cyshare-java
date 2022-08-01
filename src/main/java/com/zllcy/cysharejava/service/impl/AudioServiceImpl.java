package com.zllcy.cysharejava.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zllcy.cysharejava.entity.Audio;
import com.zllcy.cysharejava.entity.Singer;
import com.zllcy.cysharejava.mapper.AudioMapper;
import com.zllcy.cysharejava.mapper.SingerMapper;
import com.zllcy.cysharejava.service.AudioService;
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
 * @date 2022/4/20 16:34
 */
@Service
public class AudioServiceImpl implements AudioService {
    @Value("${index.audio}")
    private String indexLocation;

    @Value("${upload.location}")
    private String uploadLocation;

    @Autowired
    private AudioMapper audioMapper;

    @Autowired
    private SingerMapper singerMapper;

    @Override
    public int insertAudio(Audio audio) throws Exception {
        // 添加歌手
        String singerName = audio.getSinger();
        Singer singer = singerMapper.searchSinger(singerName);
        if (singer == null) {
            singer = new Singer(singerName);
            singerMapper.insertSinger(singer);
        }
        Integer singerId = singer.getId();
        audioMapper.insertAudio(audio, singerId);
        insertIndex(audio);
        return audio.getId();
    }

    @Override
    public int deleteAudio(Integer id) throws Exception {
        deleteIndex(id);
        Audio audio = audioMapper.searchAudio(id);
        File file = new File(uploadLocation, audio.getFilename());
        file.delete();
        return audioMapper.deleteAudio(id);
    }

    @Override
    public int updateAudio(Audio audio) throws Exception {
        updateIndex(audio);
        // 添加歌手
        String singerName = audio.getSinger();
        Singer singer = singerMapper.searchSinger(singerName);
        if (singer == null) {
            singer = new Singer(singerName);
            singerMapper.insertSinger(singer);
        }
        Integer singerId = singer.getId();
        return audioMapper.updateAudio(audio, singerId);
    }

    @Override
    public PageInfo<Audio> listAudioForPage(Integer pageNum) {
        PageHelper.startPage(pageNum, PAGE_SIZE);
        List<Audio> audioList = audioMapper.listAudio();
        PageInfo<Audio> audioPageInfo = new PageInfo<>(audioList);
        return audioPageInfo;
    }

    @Override
    public PageInfo<Audio> searchAudioForPageByKeyWords(Integer pageNum, String keyWords) throws Exception {
        List<Audio> audioList = new ArrayList<>();
        Analyzer analyzer = new IKAnalyzer();
        // 创建搜索解析器，默认查询title
        QueryParser queryParser = new QueryParser("song", analyzer);
        // 创建搜索对象
        Query query = queryParser.parse(keyWords);
        // 声明索引库位置
        Directory directory = FSDirectory.open(Paths.get(indexLocation));
        // 创建索引读取对象
        IndexReader reader = DirectoryReader.open(directory);
        // 创建索引搜索对象
        IndexSearcher searcher = new IndexSearcher(reader);
        // 使用索引搜索对象，执行搜索，返回结果集TopDocs
        TopDocs topDocs = searcher.search(query, 10);
        // 获取查询结果集
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        // 解析结果集
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = searcher.doc(docId);
            Audio audio = new Audio(Integer.valueOf(document.get("id")), document.get("username"),
                    document.get("filename"), document.get("song"), document.get("singer"),
                    document.get("style"), document.get("language"), document.get("year"),
                    document.get("description"));
            audioList.add(audio);
        }
        reader.close();
        Page page = new Page(pageNum, PAGE_SIZE);
        int total = audioList.size();
        page.setTotal(total);
        int startIndex = (pageNum - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, total);
        page.addAll(audioList.subList(startIndex, endIndex));
        PageInfo audioPageInfo = new PageInfo(page);
        return audioPageInfo;
    }

    @Override
    public void insertIndex(Audio audio) throws Exception {
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
        document.add(new StringField("id", String.valueOf(audio.getId()), Field.Store.YES));
        document.add(new StoredField("username", audio.getUsername()));
        document.add(new StoredField("filename", audio.getFilename()));
        document.add(new TextField("song", audio.getSong(), Field.Store.YES));
        document.add(new TextField("singer", audio.getSinger(), Field.Store.YES));
        document.add(new TextField("style", audio.getStyle(), Field.Store.YES));
        document.add(new TextField("language", audio.getLanguage(), Field.Store.YES));
        document.add(new TextField("year", audio.getYear(), Field.Store.YES));
        document.add(new TextField("description", audio.getDescription(), Field.Store.YES));
        // 写入索引库
        indexWriter.addDocument(document);
        // 释放资源
        indexWriter.close();
    }

    @Override
    public void updateIndex(Audio audio) throws Exception {
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
        document.add(new StringField("id", String.valueOf(audio.getId()), Field.Store.YES));
        document.add(new StoredField("username", audio.getUsername()));
        document.add(new StoredField("filename", audio.getFilename()));
        document.add(new TextField("song", audio.getSong(), Field.Store.YES));
        document.add(new TextField("singer", audio.getSinger(), Field.Store.YES));
        document.add(new TextField("style", audio.getStyle(), Field.Store.YES));
        document.add(new TextField("language", audio.getLanguage(), Field.Store.YES));
        document.add(new TextField("year", audio.getYear(), Field.Store.YES));
        document.add(new TextField("description", audio.getDescription(), Field.Store.YES));
        // 执行更新，会把所有符合条件的Document删除，再新增
        indexWriter.updateDocument(new Term("id", String.valueOf(audio.getId())), document);
        indexWriter.close();
    }

    @Override
    public void deleteIndex(Integer id) throws Exception {
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
    public PageInfo<Audio> listAudioForPageByUsername(String username, Integer pageNum) {
        PageHelper.startPage(pageNum, PAGE_SIZE);
        List<Audio> audioList = audioMapper.listAudioByUsername(username);
        PageInfo<Audio> audioPageInfo = new PageInfo<>(audioList);
        return audioPageInfo;
    }
}
