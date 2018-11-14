package org.lingg.jdk.base64;

import org.junit.jupiter.api.Test;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.util.Base64;

public class TestBase64EncodeDecode {
    @Test
    public void testSunBase64() throws Exception {
        //早期在Java上做Base64的编码与解码，会使用到JDK里sun.misc套件下的BASE64Encoder和BASE64Decoder这两个类别，用法如下
        final BASE64Encoder encoder = new BASE64Encoder();
        final BASE64Decoder decoder = new BASE64Decoder();
        final String text = "字串文字";
        final byte[] textByte = text.getBytes("UTF-8");

        final String encodedText = encoder.encode(textByte);//编码
        System.out.println(encodedText);

        System.out.println(new String(decoder.decodeBuffer(encodedText), "UTF-8"));//解码
    }

    @Test
    public void testApacheCommonsCodecBase64() throws Exception {
        //Apache Commons Codec有提供Base64的编码与解码功能，会使用到org.apache.commons.codec.binary套件下的Base64类别，用法如下

        final org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64();
        final String text = "字串文字";
        final byte[] textByte = text.getBytes("UTF-8");

        final String encodedText = base64.encodeToString(textByte);//编码
        System.out.println(encodedText);

        System.out.println(new String(base64.decode(encodedText), "UTF-8"));//解码
    }

    @Test
    public void testJava8Base64() throws Exception {
        //Java 8的java.util套件中，新增了Base64的类别，可以用来处理Base64的编码与解码，用法如下

        final Base64.Decoder decoder = Base64.getDecoder();
        final Base64.Encoder encoder = Base64.getEncoder();
        final String text = "字串文字";
        final byte[] textByte = text.getBytes("UTF-8");

        final String encodedText = encoder.encodeToString(textByte);//编码
        System.out.println(encodedText);

        System.out.println(new String(decoder.decode(encodedText), "UTF-8"));//解码
    }
}
