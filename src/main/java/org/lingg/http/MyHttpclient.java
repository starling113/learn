package org.lingg.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
 
public class MyHttpclient {
 
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MyHttpclient.class);
 
	public static final String METHOD_POST = "POST";
	public static final String METHOD_GET = "GET";
	public static final String CHARACTER_ENCODING = "UTF-8";
 
	/**
	 * 
	 * @param map  数据为map  范型 为Map<String, String>
	 * @param url
	 * @param method
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static String sendRequestMethod(Map<String, String> map, String url, String method, int timeout)
			throws Exception {
 
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout * 1000)
				.setConnectTimeout(timeout * 1000).setConnectionRequestTimeout(timeout * 1000).build();
 
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (map != null) {
				Set<Map.Entry<String, String>> entrySet = map.entrySet();
				for (Map.Entry<String, String> e : entrySet) {
					String name = e.getKey();
					String value = e.getValue();
					NameValuePair pair = new BasicNameValuePair(name, value);
					params.add(pair);
				}
			}
 
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, "UTF-8");
 
			if (logger.isDebugEnabled()) {
				logger.debug("http client url:" + url);
				logger.debug("http client params:" + params.toString());
			}
			System.out.println("http client url:" + url);
			System.out.println("http client params:" + params.toString());
			HttpUriRequest reqMethod = null;
			if (METHOD_POST.equalsIgnoreCase(method)) {
				reqMethod = RequestBuilder.post().setUri(url)
						// .setCharset(java.nio.charset.Charset.forName("UTF-8"))
						// .addParameters(params.toArray(new
						// BasicNameValuePair[params.size()]))
						.setEntity(urlEncodedFormEntity).setConfig(requestConfig).build();
			} else if (METHOD_GET.equalsIgnoreCase(method)) {
				reqMethod = RequestBuilder.get().setUri(url).setEntity(urlEncodedFormEntity)
						// .addParameters(params.toArray(new
						// BasicNameValuePair[params.size()]))
						.setConfig(requestConfig).build();
			} else {
				logger.warn("method unknow, return null.");
				return null;
			}
			CloseableHttpResponse response = null;
			if (httpclient != null)
				response = httpclient.execute(reqMethod);
 
			if (response != null && response.getStatusLine().getStatusCode() == 200)
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			else {
				if (response != null)
					logger.warn("http response status error, status{}, return null"
							+ response.getStatusLine().getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			if (httpclient != null)
				httpclient.close();
		}
	}
 
	/**
	 * 
	 * @param map  数据为map  范型 为Map<String, Object> map
	 * @param url
	 * @param method  大写 POST 或者   GET
	 * @return
	 * @throws Exception
	 */
	public static String sendRequestMethod(Map<String, Object> map, String url, String method) throws Exception {
		int timeout = 60;
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout * 1000)
				.setConnectTimeout(timeout * 1000).setConnectionRequestTimeout(timeout * 1000).build();
 
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (map != null) {
				Set<Entry<String, Object>> entrySet = map.entrySet();
				for (Entry<String, Object> e : entrySet) {
					String name = e.getKey();
					String value = String.valueOf(e.getValue());
					NameValuePair pair = new BasicNameValuePair(name, value);
					params.add(pair);
				}
			}
 
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, "UTF-8");
 
			if (logger.isDebugEnabled()) {
				logger.debug("http client url:" + url);
				logger.debug("http client params:" + params.toString());
			}
			System.out.println("http client url:" + url);
			System.out.println("http client params:" + params.toString());
			HttpUriRequest reqMethod = null;
			if (METHOD_POST.equalsIgnoreCase(method)) {
				reqMethod = RequestBuilder.post().setUri(url)
						// .setCharset(java.nio.charset.Charset.forName("UTF-8"))
						// .addParameters(params.toArray(new
						// BasicNameValuePair[params.size()]))
						.setEntity(urlEncodedFormEntity).setConfig(requestConfig).build();
			} else if (METHOD_GET.equalsIgnoreCase(method)) {
				reqMethod = RequestBuilder.get().setUri(url).setEntity(urlEncodedFormEntity)
						// .addParameters(params.toArray(new
						// BasicNameValuePair[params.size()]))
						.setConfig(requestConfig).build();
			} else {
				logger.warn("method unknow, return null.");
				return null;
			}
			CloseableHttpResponse response = null;
			if (httpclient != null)
				response = httpclient.execute(reqMethod);
 
			if (response != null && response.getStatusLine().getStatusCode() == 200)
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			else {
				if (response != null)
					logger.warn("http response status error, status{}, return null"
							+ response.getStatusLine().getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			if (httpclient != null)
				httpclient.close();
		}
	}
 
	/**
	 * 发送Https 并不校验证书
	 * 
	 * @param url
	 *            地址
	 * @param map   数据 Map<String, Object>
	 * @return
	 * @throws Exception
	 */
	public static String sendRequestNoCheckCerPostMap(String url, Map<String, Object> map) throws Exception {
		int timeout = 60;
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = new SSLClient();
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout * 1000)
				.setConnectTimeout(timeout * 1000).setConnectionRequestTimeout(timeout * 1000).build();
 
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (map != null) {
				Set<Entry<String, Object>> entrySet = map.entrySet();
				for (Entry<String, Object> e : entrySet) {
					String name = e.getKey();
					String value = String.valueOf(e.getValue());
					NameValuePair pair = new BasicNameValuePair(name, value);
					params.add(pair);
				}
			}
 
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, "UTF-8");
 
			if (logger.isDebugEnabled()) {
				logger.debug("http client url:" + url);
				logger.debug("http client params:" + params.toString());
			}
			System.out.println("http client url:" + url);
			System.out.println("http client params:" + params.toString());
			HttpUriRequest reqMethod = null;
			if (METHOD_POST.equalsIgnoreCase("POST")) {
				reqMethod = RequestBuilder.post().setUri(url)
						// .setCharset(java.nio.charset.Charset.forName("UTF-8"))
						// .addParameters(params.toArray(new
						// BasicNameValuePair[params.size()]))
						.setEntity(urlEncodedFormEntity).setConfig(requestConfig).build();
			} /*
				 * else if(METHOD_GET.equalsIgnoreCase(method)) { reqMethod =
				 * RequestBuilder.get().setUri(url)
				 * .setEntity(urlEncodedFormEntity)
				 * //.addParameters(params.toArray(new
				 * BasicNameValuePair[params.size()]))
				 * .setConfig(requestConfig).build(); }
				 */else {
				logger.warn("method unknow, return null.");
				return null;
			}
			CloseableHttpResponse response = null;
			if (httpclient != null)
				response = httpclient.execute(reqMethod);
			String string = EntityUtils.toString(response.getEntity(), "UTF-8");
			System.out.println("statusCode: " + response.getStatusLine().getStatusCode());
			System.out.println("resp: " + string);
			if (response != null && response.getStatusLine().getStatusCode() == 200)
				return string;
			else {
				if (response != null)
					logger.warn("http response status error, status{}, return null"
							+ response.getStatusLine().getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			if (httpclient != null)
				httpclient.close();
		}
	}
 
	/**
	 * 发送Https 并不校验证书
	 * 
	 * @param url
	 *            地址
	 * @param json
	 *            数据
	 * @return
	 * @throws Exception
	 */
	public static String sendRequestNoCheckCerPostJOSNString(String url, String json) throws Exception {
 
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = new SSLClient();
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60 * 1000).setConnectTimeout(60 * 1000)
				.setConnectionRequestTimeout(60 * 1000).build();
		HttpPost httpPost = new HttpPost(url);
		try {
			StringEntity entity = new StringEntity(json, "utf-8");// 解决中文乱码问题
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			httpPost.setEntity(entity);
 
			if (logger.isDebugEnabled())
				logger.debug("executing request :{}" + httpPost.getRequestLine());
 
			HttpUriRequest reqMethod = RequestBuilder.post().setUri(url).setEntity(entity).setConfig(requestConfig)
					.build();
			CloseableHttpResponse response = null;
			if (httpclient != null)
				response = httpclient.execute(reqMethod);
 
			if (response != null && response.getStatusLine().getStatusCode() == 200)
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			else {
				if (response != null)
					logger.warn(" status code {} " + response.getStatusLine().getStatusCode());
				logger.warn(" server error, return null");
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			if (httpclient != null)
				httpclient.close();
		}
	}
 
	/**
	 * post 请求 json 数据
	 * 
	 * @param json
	 *            JSON
	 * @param url
	 *            地址
	 * @param timeout
	 *            设置超时时间
	 * @return
	 * @throws Exception
	 */
	public static String sendJsonRequestMethod(String json, String url, int timeout) throws Exception {
 
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout * 1000)
				.setConnectTimeout(timeout * 1000).setConnectionRequestTimeout(timeout * 1000).build();
		HttpPost httpPost = new HttpPost(url);
		try {
			StringEntity entity = new StringEntity(json, "utf-8");// 解决中文乱码问题
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			httpPost.setEntity(entity);
 
			if (logger.isDebugEnabled())
				logger.debug("executing request :{}" + httpPost.getRequestLine());
 
			HttpUriRequest reqMethod = RequestBuilder.post().setUri(url).setEntity(entity).setConfig(requestConfig)
					.build();
			CloseableHttpResponse response = null;
			if (httpclient != null)
				response = httpclient.execute(reqMethod);
 
			if (response != null && response.getStatusLine().getStatusCode() == 200)
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			else {
				if (response != null)
					logger.warn(" status code {} " + response.getStatusLine().getStatusCode());
				logger.warn(" server error, return null");
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			if (httpclient != null)
				httpclient.close();
		}
	}
 
	public static String ZX(String url, org.apache.commons.httpclient.NameValuePair[] data, String merId)
			throws Exception {
 
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new PostMethod(url);
		String result = null;
		try {
			postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
			httpClient.getHttpConnectionManager().getParams().setSoTimeout(60000);
			postMethod.addRequestHeader("Xposp-Authorization", "noneed");
			postMethod.addRequestHeader("Request-Source", merId);
 
			// 将表单的值放入postMethod中
			postMethod.setRequestBody(data);
			int statusCode = httpClient.executeMethod(postMethod);
			if (statusCode == 200) {
				byte[] responseBody = postMethod.getResponseBody();
				result = new String(responseBody, "utf-8");
				System.out.println("[响应数据: " + result + "]");
			} else {
				postMethod.releaseConnection();
				httpClient.getHttpConnectionManager().closeIdleConnections(0);
			}
		} catch (HttpException e) {
			System.out.println("[Http请求异常]" + e);
			return result;
		} catch (IOException e) {
			System.out.println("[I/O读写异常]" + e);
			return result;
		} finally {
			// 释放链接
			if (postMethod != null) {
				try {
					postMethod.releaseConnection();
					httpClient.getHttpConnectionManager().closeIdleConnections(0);
				} catch (Exception e) {
					System.out.println("[Http应答异常]" + e);
				}
			}
		}
		return result;
	}
}
//---------------------
//作者：荡漾-
//来源：CSDN
//原文：https://blog.csdn.net/qq_38380025/article/details/80826345
//版权声明：本文为博主原创文章，转载请附上博文链接！