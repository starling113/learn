package org.lingg.download.bing;

import org.apache.commons.fileupload.util.mime.MimeUtility;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownBingPic {
    private static Logger log = LoggerFactory.getLogger(DownBingPic.class);

    private static final int DEFAULT_TIMEOUT = 5000;
    private static final String UTF_8 = "UTF-8";
    private static final String DOWN_PIC_PATH = "F:\\BaiduYunDownload\\Bing壁纸\\";

    public static void main(String[] args){
        log.info("start.....");

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy_MM_dd");
        for (int i = 0; i < 100; i++) {

            String filename = fmt.format(new Date(System.currentTimeMillis() - i * 24 * 60 * 60 * 1000L)) + ".jpg";
            File f = new File(DOWN_PIC_PATH + filename);
            if (!f.exists()) {
                log.info("{}", i);
                download("https://bing.ioliu.cn/v1/?w=1920&h=1080&d=" + i, filename);
            }else {
                log.info("{} already existing", f.getName());
            }

        }

        log.info("done!!!!");

//		for(int i=0; i<500; i++){
//			Thread.sleep(1000+i);
//			String durl = "https://bing.ioliu.cn/v1/?w=1920&h=1080&d=" + i;
//			
//			HttpsClient  c = new HttpsClient();
//			String post = c.get(durl);
//			log.info("get json ====  "+post);
////			JSONObject obj = JSONObject.parseObject(post);
////			JSONObject obj2 = obj.getJSONObject("data");
////			String url = obj2.getString("url");
////			
////			if(url.startsWith("http://s.cn.bing.net/az/hprichbg/rb/")){
////				url = url.replaceAll("http://s.cn.bing.net/az/hprichbg/rb/", "https://bing.ioliu.cn/photo/");
////				url = url.replaceAll("_1920x1080.jpg", "?force=download");
////			}
////				
////			System.out.println(url);
////			download(url, null, obj2.getString("enddate"));
//		}

//		HttpsClient  c = new HttpsClient();
//		String post = c.post("https://bing.ioliu.cn/v1/?type=json&d=5");
//		System.out.println(post);
//		JSONObject obj = JSONObject.parseObject(post);
//		JSONObject obj2 = obj.getJSONObject("data");
//		String url = obj2.getString("url");
//		System.out.println(url);
//		download(url, null, obj2.getString("enddate"));
    }

//    public static void downFile(String url, String fileName){
//        DownBingPic downBingPic = new DownBingPic();
//        downBingPic.download(url, fileName);
//    }

    /**
     * 下载文件
     *
     * @param url      下载url
     * @param fileName 保存的文件名（可以为null）
     */
    private static void download(String url, String fileName) {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            httpclient = buildHttpClient();
            HttpGet httpget = new HttpGet(url);
            response = httpclient.execute(httpget);
            entity = response.getEntity();
            // 下载
            if (entity.isStreaming()) {
                String destFileName = "data";
                if (!isBlank(fileName)) {
                    destFileName = fileName;
                } else if (response.containsHeader("Content-Disposition")) {
                    String dstStr = response.getLastHeader(
                            "Content-Disposition").getValue();
                    dstStr = decodeHeader(dstStr);
                    //使用正则截取
                    Pattern p = Pattern.compile("filename=\"?(.+?)\"?$");
                    Matcher m = p.matcher(dstStr);
                    if (m.find()) {
                        destFileName = m.group(1);
                    }
                } else {
                    destFileName = url.substring(url.lastIndexOf("/") + 1);
                }

//	            destFileName = filedatestr + "_" + destFileName;

                log.info("downloading file: " + destFileName);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(DOWN_PIC_PATH + destFileName);
                    entity.writeTo(fos);
                } finally {
                    try {
                        //if(null != fos)
                            fos.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                log.info("download complete");
            } else {
                log.error("Not Found");
                log.info(EntityUtils.toString(entity));
            }
        } catch (Exception e) {
            log.error("downloading file from " + url + " occursing some error.");
            log.error(e.toString());
        } finally {
            try {
                EntityUtils.consume(entity);
                response.close();
                httpclient.close();
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
    }

    /**
     * 构建可信任的https的HttpClient
     *
     * @return
     * @throws Exception
     */
    public static CloseableHttpClient buildHttpClient() throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
                new TrustStrategy() {
                    public boolean isTrusted(X509Certificate[] arg0, String arg1)
                            throws CertificateException {
                        return true;
                    }
                }).build();
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext, new NoopHostnameVerifier());
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("http",
                        PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        // set longer timeout value
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(DEFAULT_TIMEOUT)
                .setConnectTimeout(DEFAULT_TIMEOUT)
                .setConnectionRequestTimeout(DEFAULT_TIMEOUT).build();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSslcontext(sslContext)
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig).build();
        return httpclient;
    }

    /**
     * 上传文件
     *
     * @param url        上传路径
     * @param file       文件路径
     * @param stringBody 附带的文本信息
     * @return 响应结果
     */
    public static String upload(String url, String file, String stringBody) {
        String result = "";
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        HttpEntity resEntity = null;
        try {
            httpclient = buildHttpClient();
            HttpPost httppost = new HttpPost(url);
            // 把文件转换成流对象FileBody
            FileBody bin = new FileBody(new File(file));
            StringBody comment = new StringBody(stringBody, ContentType.create(
                    "text/plain", Consts.UTF_8));
            // 以浏览器兼容模式运行，防止文件名乱码。
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addPart("bin", bin).addPart("comment", comment)
                    .setCharset(Consts.UTF_8).build();
            httppost.setEntity(reqEntity);
            log.info("executing request " + httppost.getRequestLine());
            response = httpclient.execute(httppost);
            log.info(response.getStatusLine() + "");
            resEntity = response.getEntity();
            if (resEntity != null) {
                log.info("Response content length: "
                        + resEntity.getContentLength());
                result = EntityUtils.toString(resEntity, Consts.UTF_8);
            }
        } catch (Exception e) {
            log.error("executing request " + url + " occursing some error.");
            log.error(e.getMessage());
        } finally {
            try {
                EntityUtils.consume(resEntity);
                response.close();
                httpclient.close();
            } catch (IOException e) {
	            //log.error(e);
            }
        }
        return result;
    }

    // 判断字符串是否为空
    private static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    // 将header信息按照
    // 1,iso-8859-1转utf-8;2,URLDecoder.decode;3,MimeUtility.decodeText;
    // 做处理，处理后的string就为编码正确的header信息（包括中文等）
    private static String decodeHeader(String header)
            throws UnsupportedEncodingException {
        return MimeUtility.decodeText(URLDecoder.decode(
                new String(header.getBytes(Consts.ISO_8859_1), Consts.UTF_8),
                UTF_8));
    }


}