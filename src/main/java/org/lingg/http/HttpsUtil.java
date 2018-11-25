package org.lingg.http;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
 
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
 
/**
 * @author ks
 * @date 2018-04-25
 * https工具类
 */
public class HttpsUtil{
 
private static class TrustAnyTrustManager implements X509TrustManager {
 
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
 
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
 
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
}
 
private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
}
 
 
/**
* post方式请求服务器(https协议)
* @param url
*            请求地址
* @param content
*            参数
* @param charset
*            编码
* @return
* @throws NoSuchAlgorithmException
* @throws KeyManagementException
* @throws IOException
*/
public static String post(String url, String content)
            throws NoSuchAlgorithmException, KeyManagementException,
            IOException {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
                new java.security.SecureRandom());
 
        URL console = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
        conn.setDoOutput(true);
        conn.addRequestProperty("connection", "Keep-Alive");
//        conn.setSSLSocketFactory(sc.getSocketFactory());
        conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
        conn.connect();
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.write(content.getBytes("utf-8"));
        // 刷新、关闭
        out.flush();
        out.close();
        InputStream is = conn.getInputStream();
        if (is != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            is.close();
            if(outStream!=null){
            	return new String(outStream.toByteArray(),"utf-8");
            }
        }
        return null;
}
 
 public static void main(String[] args) {
	try {
		String str=HttpsUtil.post("https://api.mch.weixin.qq.com/pay/unifiedorder", "");
		System.out.println(str);
	} catch (KeyManagementException e) {
		e.printStackTrace();
	} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
}

@Test
    public void test1() throws Exception{
    String url  = "https://test-fls-aflm.pingan.com.cn/awf/vservice/UpdateDetail/Invoke.service?detail=%7B%22parameters.common%22%3A%7B%7D%2C%22data.app.carFinance%22%3A%7B%22content%22%3A%22VMEpV8Dqqn3xZpAKvAnd2kVM%2Fg8%2BQd2bv3pmfWPK%2FgnnwZXS9qvNlxoo69WnEWz1L%2BWjtV3VlB8rEfTLpU5hdLZlTN4YYH3aH1RReEIrKfv4sb8%2BTt2AYTnZUOkrJcgLeCkFqpPyMV2qGEiaQtziQ%2BoLYMcEgbSte3MBsVtTWxXEx7cvdKctUnd77MAfmbtOh5U2YyIzm%2BcHrgIohDBT1ypa%2FKdOIgesR5%2BJD2ti%2FuOGqbwxLNFaYLKJ5PaYfafrYCyvqJa6cIeTN7gdgMoe0eHk6rDbo2nmNEyCvlFIo1Nid4dLeS6rqPiTccHys5YpeuE7X0%2Fs8xBEM%2FwNAmHvsCTGJphgQ3ZtPR9V9XmEvuj57lqmwdX%2FcNhigLQsmkIsPHReXYrJ3bFb5w8C4WyoNpRO%2BWLEpsCkActCzGmBxUjDBl6sJ3fdP9%2F7JPn7459sr4qvflGY1q3ZWxuzTZJZJubWVF%2BrHGTnDMYMr8sxNDcwsKUwsKV4%2FaszDQyv8Ctc0EiB%2BakphS5%2FbOVw8C%2BKdG5589yi6X7x51qWQSSW4DOurOgNoNovN5p0Afcv8i%2B9YA0MIVKS%2FMjRxLGPoJ1zp3%2Ffn5Pm0aRoAxQKvDDzRW35gM75M0XLLZBbD5nSbqc21eWVcUT4TLQOCedTQdxviOlfTh6O8Yh5iMggTVd7UKU%3D%22%7D%7D";

    System.out.println(HttpsUtil.post(url, ""));
}
} 
