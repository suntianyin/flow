package com.apabi.flow.douban.util;


import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpUtils2 {
	private static int connectionRequestTimeout = 100000;
	private static int connectTimeout = '';
	private static int socketTimeout = 100000;
	public static final int DEFAULT_TIMEOUT = 60000;

	public static HttpResponse httpPut(String url, HttpEntity entity) throws Exception {
		return httpPut(url, (Map) null, entity);
	}

	public static HttpResponse httpPut(String url, Map<String, String> httpHeaders, HttpEntity entity)
			throws Exception {
		HttpPut httpPut = new HttpPut(url);
		setRequestConfig(httpPut);
		setRequestHeaders(httpPut, httpHeaders);
		httpPut.setEntity(entity);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return httpClient.execute(httpPut);
	}

	public static HttpResponse httpPost(String url, HttpEntity entity) throws Exception {
		return httpPost(url, (Map) null, entity);
	}

	public static HttpResponse httpPost(String url, Map<String, String> httpHeaders, HttpEntity entity)
			throws Exception {
		HttpPost httpPost = new HttpPost(url);
		setRequestConfig(httpPost);
		setRequestHeaders(httpPost, httpHeaders);
		httpPost.setEntity(entity);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return httpClient.execute(httpPost);
	}

	public static HttpResponse httpPost(String url, Map<String, String> httpHeaders, HttpEntity entity, HttpContext hc)
			throws Exception {
		HttpPost httpPost = new HttpPost(url);
		setRequestConfig(httpPost);
		setRequestHeaders(httpPost, httpHeaders);
		httpPost.setEntity(entity);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return httpClient.execute(httpPost, hc);
	}

	public static HttpResponse httpGet(String url) throws Exception {
		return httpGet(url, (Map) null);
	}

	public static HttpResponse httpGet(String url, Map<String, String> httpHeaders)
			throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(url);
		setRequestConfig(httpGet);
		setRequestHeaders(httpGet, httpHeaders);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return httpClient.execute(httpGet);
	}

	public static HttpResponse httpGet(String url, Map<String, String> httpHeaders, HttpClientContext hcc)
			throws Exception {
		HttpGet httpGet = new HttpGet(url);
		setRequestConfig(httpGet);
		setRequestHeaders(httpGet, httpHeaders);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return httpClient.execute(httpGet, hcc);
	}

	public static InputStream getResponseInputStream(HttpResponse httpResponse) throws Exception {
		HttpEntity entity = httpResponse.getEntity();
		return entity != null ? entity.getContent() : null;
	}

	public static HttpEntity toStringEntity(String src) {
		return toStringEntity(src, Consts.UTF_8.name());
	}

	public static HttpEntity toStringEntity(String src, String charset) {
		StringEntity stringEntity = new StringEntity(src, charset);
		return stringEntity;
	}

	public static HttpEntity toInputStreamEntity(InputStream inputStream) throws Exception {
		return toInputStreamEntity(inputStream, (String) null, (String) null);
	}

	public static HttpResponse doGetEntity(String url, Map<String, String> httpHeaders)
			throws ClientProtocolException, IOException {
		HttpResponse response = httpGet(url, httpHeaders);
		return response;
	}

	public static HttpEntity doGetEntity(String url, Map<String, String> httpHeaders, HttpClientContext hcc)
			throws Exception {
		HttpResponse response = httpGet(url, httpHeaders, hcc);
		return getEntityFromResponse(response);
	}

	public static HttpResponse doGetEntity(String url) throws ClientProtocolException, IOException {
		return doGetEntity(url, (Map) null);
	}

	public static HttpEntity doPostEntity(String url, Map<String, String> httpHeaders, HttpEntity entity)
			throws Exception {
		HttpResponse response = httpPost(url, httpHeaders, entity);
		return getEntityFromResponse(response);
	}

	public static HttpEntity doPostEntity(String url, HttpEntity entity) throws Exception {
		return doPostEntity(url, (Map) null, entity);
	}

	public static HttpEntity doPutEntity(String url, Map<String, String> httpHeaders, HttpEntity entity)
			throws Exception {
		HttpResponse httpResponse = httpPut(url, httpHeaders, entity);
		return getEntityFromResponse(httpResponse);
	}

	public static HttpEntity doPutEntity(String url, HttpEntity entity) throws Exception {
		return doPutEntity(url, (Map) null, entity);
	}

	public static byte[] doGetByteArray(String url) throws Exception {
		HttpResponse response = httpGet(url, (Map) null);
		HttpEntity entity = getEntityFromResponse(response);
		byte[] byteArr = IOUtils.toByteArray(entity.getContent());
		return byteArr;
	}

	public static HttpEntity toInputStreamEntity(InputStream inputStream, String contentType, String charset)
			throws Exception {
		InputStreamEntity entity = new InputStreamEntity(inputStream);
		entity.setContentEncoding(charset);
		entity.setContentType(contentType);
		return entity;
	}

	private static void setRequestHeaders(HttpRequest httpRequest, Map<String, String> httpHeaders) {
		if (httpHeaders != null) {
			Set headEntries = httpHeaders.entrySet();
			Iterator arg2 = headEntries.iterator();

			while (arg2.hasNext()) {
				Entry entry = (Entry) arg2.next();
				httpRequest.addHeader((String) entry.getKey(), (String) entry.getValue());
			}
		}

	}

	private static void setRequestConfig(HttpRequestBase httpRequest) {
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
				.setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
		httpRequest.setConfig(requestConfig);
	}

	private static HttpEntity getEntityFromResponse(HttpResponse response) {
		return response.getStatusLine().getStatusCode() == 200 ? response.getEntity() : null;
	}

	public static HttpEntity doGetEntityForSolrPaper(String url) throws Exception {
		HashMap header = new HashMap();
		HttpClientContext context = makeCredentials();
		HttpEntity he = doGetEntity(url, header, context);
		return he;
	}

	public static HttpEntity doGetEntityForSolrTopic(String url) throws Exception {
		HashMap header = new HashMap();
		HttpClientContext context = makeCredentialsForTopic();
		HttpEntity he = doGetEntity(url, header, context);
		return he;
	}

	private static HttpClientContext makeCredentials() {
		BasicCredentialsProvider cp = new BasicCredentialsProvider();
		String username = "apabi";
		String password = "Founder123";
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
		cp.setCredentials(AuthScope.ANY, credentials);
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(cp);
		return context;
	}

	private static HttpClientContext makeCredentialsForTopic() {
		BasicCredentialsProvider cp = new BasicCredentialsProvider();
		String username = "apabi";
		String password = "Founder123";
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
		cp.setCredentials(AuthScope.ANY, credentials);
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(cp);
		return context;
	}

	public static InputStream httpPostByStream(String strUrl, String data, String dataType) throws Exception {
		Map<String, String> httpheader  = new HashMap<String,String>();
		httpheader.put("accept", "*/*");
		httpheader.put("connection", "Keep-Alive");
		httpheader.put("content-type", dataType);
		httpheader.put("Charsert", "UTF-8");
		httpheader.put("user-agent", "Android-Large");
		StringEntity entity = new StringEntity(data, "UTF-8");
		HttpClientContext context = makeCredentials();
		HttpResponse hr = httpPost(strUrl, httpheader, entity, context);
		InputStream in = null;
		if(hr.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
			in = hr.getEntity().getContent();
		}
		return in;
	}

	public static void main(String[] args) throws Exception {
		HttpEntity in = doGetEntityForSolrPaper(
				"http://rtest1.apabi.com/solr/newspaper/select?q=id%3A%22n.D110000xinhuamrdx%22&fl=nid&start=0&rows=10&wt=xml&indent=true");
		System.out.println(in.getContentLength());
		InputStream ins = in.getContent();
		System.out.println(ins.toString());
	}
}