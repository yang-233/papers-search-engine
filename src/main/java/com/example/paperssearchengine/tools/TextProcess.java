package com.example.paperssearchengine.tools;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class TextProcess {
    public static final String pdfPath = "/run/media/yang/Data/papers";

    public static void test() {
        File file = new File(pdfPath);
        File[] papers = file.listFiles();
/*        for(int i = 0; i < papers.length; ++i) {
            if(!papers[i].isDirectory())
                System.out.println(papers[i].getName());
        }
        System.out.println(papers.length);*/
        System.out.println(getStringFromPdf(papers[0]));
    }

    public static String getStringFromPdf(File pdf) {
        try {
            PDDocument doc = PDDocument.load(pdf);
            return new PDFTextStripper().getText(doc);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String args[]) {
        test();
    }
}
