package com.example.paperssearchengine.tools;

import com.example.paperssearchengine.model.Paper;
import com.hankcs.lucene.HanLPAnalyzer;
import com.hankcs.lucene.HanLPIndexAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class TextProcess {
    public static final String srcPath = "/home/yang/Documents/代谢病_E504EBD3F41743268601C2BBC617A832/";
    public static final String destPath = "/home/yang/Documents/papers/pdf/";
    public static final String summaryPath = "/home/yang/Documents/papers/summary/";
    public static final String contentPath = "/home/yang/Documents/papers/content/";
    public static final String indexPath = "/home/yang/Documents/papers/index/";

    public static final HanLPIndexAnalyzer indexAnalyzer = new HanLPIndexAnalyzer();
    public static final HanLPAnalyzer queryAnalyzer = new HanLPAnalyzer();
    public static final SimpleAnalyzer simpleAnalyzer = new SimpleAnalyzer();


    public static String getStringFromPdf(File pdf) {
        try {
            PDDocument doc = PDDocument.load(pdf);//加载pdf文档
            PDFTextStripper stripper = new PDFTextStripper();//新建一个pdf提取类
            String res = stripper.getText(doc);//
            doc.close();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileName(String filePath) {
        int idx = filePath.length() - 1;
        while(idx >= 0 && filePath.charAt(idx) != '\\')
            --idx;
        return filePath.substring(idx + 1);
    }
    public static void buildIndexFiles() {
        try {
            //读入导出题录
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("/home/yang/Documents/papers/导出题录.eln"));
            NodeList nl = doc.getElementsByTagName("DATA");

            //对其余字段使用hanlp检索分词器
            Analyzer analyzer = new HanLPIndexAnalyzer();//使用hanlp分词器
            //对作者和关键词字段使用简单分词器
            Map<String, Analyzer> fieldAnalyzerMap = new HashMap<>();
            fieldAnalyzerMap.put("author", new SimpleAnalyzer());
            fieldAnalyzerMap.put("keyword", new SimpleAnalyzer());
            fieldAnalyzerMap.put("source", new SimpleAnalyzer());
            PerFieldAnalyzerWrapper analyzerWrapper = new PerFieldAnalyzerWrapper(new HanLPIndexAnalyzer(), fieldAnalyzerMap);//默认使用hanlp分词器

            FSDirectory fsDirectory = FSDirectory.open(Paths.get(indexPath));
            IndexWriterConfig config = new IndexWriterConfig(analyzerWrapper);
            IndexWriter writer = new IndexWriter(fsDirectory, config);//索引构造器

            int total = 0;//记录索引文件数目

            for(int i = 0; i < nl.getLength(); ++i) {
                Paper paper = new Paper();
                for(Node j = nl.item(i).getFirstChild(); j != null; j = j.getNextSibling()) {
                    String text = j.getTextContent();
                    switch (j.getNodeName()) {
                        case "Title":
                            paper.setTitle(text);
                            break;
                        case "Author":
                            paper.setAuthor(text);
                            break;
                        case "PubTime":
                            paper.setPubTime(getDateInt(text));
                            paper.setDate(text.substring(0, 10));
                            break;
                        case "Source":
                            paper.setSource(text);
                            break;
                        case "Keyword":
                            paper.setKeyword(text);
                            break;
                        case "Summary":
                            paper.setSummary(text);
                            break;
                        case "PageCount":
                            paper.setPageCount(Integer.parseInt(text));
                            break;
                        case "FilePath":
                            paper.setFileName(getFileName(text));
                    }
                }
                try {
                    File src = new File(srcPath + paper.getFileName());
                    File dest = new File(destPath + paper.getFileName());
                    paper.setContent(getStringFromPdf(src));
                    paper.setMd5key(FileTools.md5(src));
                    try {
                        writer.addDocument(paper.toDocument());
                        FileTools.copyFile(src, dest);
                        ++total;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {}
            }

            writer.flush();
            writer.commit();
            writer.close();
            fsDirectory.close();
            System.out.println("共索引" + total + "篇论文。");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static List<Paper> query(String key, String author, String source, String lowerTime, String upperTime) throws IOException, InvalidTokenOffsetsException {
        Directory fsDirectory = FSDirectory.open(Paths.get(indexPath));
        IndexReader reader = DirectoryReader.open(fsDirectory);//选定索引所在路径
        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = queryPaper(key, author, source, lowerTime, upperTime);
        TopDocs topDocs = searcher.search(query, 60);


        ArrayList<Paper> res = new ArrayList<>();


        Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter("<span style=\"color: red\">", "</span>"),
                new QueryScorer(query));

        for(ScoreDoc sdoc : topDocs.scoreDocs) {
            Paper paper = new Paper();
            org.apache.lucene.document.Document hitDoc = searcher.doc(sdoc.doc);

            paper.setAuthor(hitDoc.get("author"));
            paper.setDate(hitDoc.get("date"));
            paper.setSource(hitDoc.get("source"));
            paper.setFileName(hitDoc.get("fileName"));
            paper.setPageCount(Integer.parseInt(hitDoc.get("pageCount")));
//            paper.setContent(hitDoc.get("content"));
            paper.setMd5key(hitDoc.get("md5key"));

            String title = hitDoc.get("title");
            String keyword = hitDoc.get("keyword");
            String summary = hitDoc.get("summary");
            String temp = null;
            paper.setTitle(title);
            paper.setKeyword(keyword);
            paper.setSummary(summary);

            highlighter.setTextFragmenter(new SimpleFragmenter(title.length()));
            TokenStream titleTokenStream = queryAnalyzer.tokenStream("title", new StringReader(title));
            temp = highlighter.getBestFragment(titleTokenStream, title);
            if(temp != null) {
                paper.setTitle(temp);
                temp = null;
            }
            highlighter.setTextFragmenter(new SimpleFragmenter(keyword.length()));
            TokenStream keywordTokenStream = simpleAnalyzer.tokenStream("keyword", new StringReader(keyword));
            temp = highlighter.getBestFragment(keywordTokenStream, keyword);
            if(temp != null) {
                paper.setKeyword(temp);
                temp = null;
            }
            highlighter.setTextFragmenter(new SimpleFragmenter(summary.length()));
            TokenStream summaryTokenStream = queryAnalyzer.tokenStream("summay", new StringReader(summary));
            temp = highlighter.getBestFragment(summaryTokenStream, summary);
            if(temp !=null) {
                paper.setSummary(temp);
                temp = null;
            }
            System.out.println(paper);
            res.add(paper);
        }
        reader.close();
        fsDirectory.close();
        return res;
    }

    public static int getDateInt(String date) {
        //format yyyy-mm-dd hh:mm:ss

        int year = Integer.parseInt(date.substring(0, 4));

        int month = Integer.parseInt(date.substring(5, 7));

        int day = Integer.parseInt(date.substring(8, 10));

        return year * 10000 + month * 100 + day;
    }
    public static Query queryPaper(String key, String author, String source, String lowerTime, String upperTime) {
        try {
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            if(key != null && !key.isEmpty()) {//查询关键字，关键字会在好几个Field里面出现
                MultiFieldQueryParser contentParser = new MultiFieldQueryParser(new String[]{"title", "summary", "content", "keyword"}, queryAnalyzer);
                builder.add(contentParser.parse(key), BooleanClause.Occur.MUST);
            }
            if (author != null && !author.isEmpty()) {//指定了作者
                QueryParser authorQuery = new QueryParser("author", simpleAnalyzer);
                builder.add(authorQuery.parse(author), BooleanClause.Occur.MUST);
            }
            if(source != null && !source.isEmpty()) {//指定了期刊来源
                QueryParser sourceQuery = new QueryParser("source", simpleAnalyzer);
                builder.add(sourceQuery.parse(source), BooleanClause.Occur.MUST);
            }
            if(lowerTime != null && upperTime != null && !lowerTime.isEmpty() && !upperTime.isEmpty()) {//指定了发布时间
                builder.add(IntPoint.newRangeQuery("pubTime", getDateInt(lowerTime), getDateInt(upperTime)), BooleanClause.Occur.MUST);
            }
            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String args[]) throws Exception {
        System.out.println(query("" , "", "", "", "").size());
        //buildIndexFiles();
//        System.out.println(getDateInt("2019-01-01"));
    }

}
