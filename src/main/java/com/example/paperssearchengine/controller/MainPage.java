package com.example.paperssearchengine.controller;

import com.example.paperssearchengine.model.Paper;
import com.example.paperssearchengine.tools.TextProcess;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Controller
public class MainPage {

    public static final String filePath = "classpath:static/papers/";
    @RequestMapping(value = "/index")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/query")
    public ModelAndView query(String keyword, String author, String source, String lowerTime, String upperTime, ModelAndView mv) {
        System.out.println(keyword + author + source + lowerTime + upperTime);
        try {
            List<Paper> result = TextProcess.query(keyword, author, source, lowerTime, upperTime);
            mv.addObject("keyword", keyword);
            mv.addObject("author", author);
            mv.addObject("source", source);
            mv.addObject("lowerTime", lowerTime);
            mv.addObject("upperTime", upperTime);
            mv.addObject("result", result);
            mv.setViewName("index");
            if(result.size() == 0){
                mv.addObject("flag", true);
            }
        } catch (IOException | InvalidTokenOffsetsException e) {
            e.printStackTrace();
        }
        return mv;
    }


    @RequestMapping(value = "/download")
    public String download(String fileName, HttpServletResponse response) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println(fileName);
        File file = ResourceUtils.getFile(filePath + fileName);
        byte[] buffer = new byte[4096];
        if(file.exists()){
            response.setHeader("Content-Disposition", "attachment;fileName=" + new String(file.getName().getBytes("GB2312"),"ISO-8859-1"));
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;
            OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while(i != -1){
                    os.write(buffer);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }
}
