package com.apabi.flow.book.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.Map;
import java.util.Set;

/**
 * @author guanpp
 * @date 2018/8/2 13:38
 * @description
 */
public class HttpUtils {

    private static int connectionRequestTimeout = 100000;
    private static int connectTimeout = 60000;
    private static int socketTimeout = 250000;

    public static HttpEntity doGetEntity(String url) throws Exception {
        return doGetEntity(url, null);
    }

    public static HttpEntity doGetEntity(String url, Map<String, String> httpHeaders) throws Exception {
        HttpResponse response = httpGet(url, httpHeaders);
        return getEntityFromResponse(response);
    }

    private static HttpEntity getEntityFromResponse(HttpResponse response) {
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return response.getEntity();
        } else {
            return null;
        }
    }

    public static HttpResponse httpGet(String url, Map<String, String> httpHeaders) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        setRequestConfig(httpGet);
        setRequestHeaders(httpGet, httpHeaders);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        return httpClient.execute(httpGet);
    }

    private static void setRequestConfig(HttpRequestBase httpRequest) {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        httpRequest.setConfig(requestConfig);
    }

    private static void setRequestHeaders(HttpRequest httpRequest, Map<String, String> httpHeaders) {
        if(httpHeaders != null) {
            Set<Map.Entry<String, String>> headEntries = httpHeaders.entrySet();
            for(Map.Entry<String, String> entry : headEntries){
                httpRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }
}
