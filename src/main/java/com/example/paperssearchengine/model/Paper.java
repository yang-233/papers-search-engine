package com.example.paperssearchengine.model;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;

public class Paper {
    private String
        title,
        content,
        author,
        source,
        keyword,
        summary,
        md5key,
        date,
        fileName;
    private int
            pubTime,
        pageCount;

    public Document toDocument() {
        Document document = new Document();
        FieldType contentType = new FieldType();
        contentType.setStored(true);//存储
        contentType.setTokenized(true);//进行分词
        contentType.setStoreTermVectors(true);
        contentType.setStoreTermVectorOffsets(true);
        contentType.setStoreTermVectorPayloads(true);
        contentType.setStoreTermVectorPositions(true);//记录索引位置
        contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);

        contentType.freeze();
        //用于检索的部分
        document.add(new Field("title", title, contentType));//标题
        document.add(new Field("summary", summary, contentType));//摘要
        document.add(new Field("author", author, contentType));//作者
        document.add(new Field("keyword", keyword, contentType));//关键词
//        document.add(new Field("pubTime", pubTime, contentType));//日期便于范围查询
        document.add(new Field("source", source, contentType));//仅适用于完全匹配
        document.add(new Field("content", content, contentType));//内容不存储


        document.add(new IntPoint("pubTime", pubTime));
        //仅作为字段存储
        document.add(new StoredField("date", date));//存储时间
        document.add(new StoredField("fileName", fileName));//记录文件名
        document.add(new StoredField("pageCount", String.valueOf(pageCount)));//记录页数

        FieldType doNotTokenType = new FieldType();
        doNotTokenType.setStored(true);
        doNotTokenType.setTokenized(false);//不进行分词
        doNotTokenType.setIndexOptions(IndexOptions.DOCS);//但是进行索引
        doNotTokenType.freeze();
        //适用于完全匹配
        document.add(new Field("md5key", md5key, doNotTokenType));//检查文档是否已存在
        return document;
    }
    public Paper() {}


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getMd5key() {
        return md5key;
    }

    public void setMd5key(String md5key) {
        this.md5key = md5key;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getPubTime() {
        return pubTime;
    }

    public void setPubTime(int pubTime) {
        this.pubTime = pubTime;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Paper{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", source='" + source + '\'' +
                ", keyword='" + keyword + '\'' +
                ", summary='" + summary + '\'' +
                ", md5key='" + md5key + '\'' +
                ", date='" + date + '\'' +
                ", fileName='" + fileName + '\'' +
                ", pubTime=" + pubTime +
                ", pageCount=" + pageCount +
                '}';
    }
}
