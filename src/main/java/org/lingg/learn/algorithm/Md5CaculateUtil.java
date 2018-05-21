package org.lingg.learn.algorithm;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *MD5计算工具 xuxile 2017-09-13
 */
public class Md5CaculateUtil {

    /**
     * 获取一个文件的md5值(可处理大文件)
     * @return md5 value
     */
    public static String getFileMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length); // 不断更新MD5值
            }
            return new String(Hex.encodeHex(md5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null){
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 求一个字符串的md5值
     * @param target 字符串
     * @return md5 value
     */
    public static String MD5(String target) {
        return DigestUtils.md5Hex(target);
    }

    public static void main(String[] args) {
        long beginTime = System.currentTimeMillis();
        File file = new File("F:/BaiduYunDownload/TIB_js-studiocomm_6.4.3.final_windows_x86_64.exe");
        String md5 = getFileMD5(file);
        long endTime = System.currentTimeMillis();
        System.out.println("MD5:" + md5 + "\n 耗时:" + (endTime - beginTime) + "ms");
    }
}