package com.example.paperssearchengine.tools;

import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileTools {

    public static File cleanFile(File file) throws Exception {
        if(file.exists()) {
            if(file.delete() && file.createNewFile()) // clean
                return file;
            else
                return null;
        } else
            return file;
    }

    public static int copyFile(String src, String dest) {
        return copyFile(new File(src), new File(dest));
    }

    public static int copyFile(File src, File dest) {
        try {
            cleanFile(dest);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        int byteSum = 0,
                byteRead = 0;
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);
            byte[] buf = new byte[4096];
            while ((byteRead = in.read(buf)) != -1) {
                byteSum += byteRead;
                out.write(buf, 0, byteRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if(in != null) {
                    in.close();
                    in = null;
                }

                if(out != null) {
                    out.close();
                    out = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            } finally {
                return byteSum;
            }
        }
    }
    public static String md5(FileInputStream in) {
        String res = null;
        try {
            res = DigestUtils.md5DigestAsHex(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String md5(File in) {
        try {
            return md5(new FileInputStream(in));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String md5(String str) {
        String res = null;
        try {
            res = DigestUtils.md5DigestAsHex(str.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
