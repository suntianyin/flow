package com.apabi.flow.match_data.test;

import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.douban.model.ApabiBookMetaData;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @Author pipi
 * @Date 2019-2-2 9:34
 **/
@Component
@RequestMapping("testApabiNlcController")
public class TestApabiNlcController {
    @Autowired
    private ApabiBookMetaDataDao apabiBookMetaDataDao;


    public static CloseableHttpClient getCloseableHttpClient() {
        // 把代理设置到请求配置
        RequestConfig config = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).setConnectionRequestTimeout(60000).build();
        // SocketConfig
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(60000).build();
        // 实例化CloseableHttpClient对象
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).setDefaultSocketConfig(socketConfig).build();
        return client;
    }

    @RequestMapping("test")
    @ResponseBody
    public String test() throws IOException, URISyntaxException {
        int index = 0;
        CloseableHttpClient httpClient = getCloseableHttpClient();
        BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\pirui\\Desktop\\in.txt"));
        String line = "";
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("C:\\Users\\pirui\\Desktop\\error.txt"));
        while ((line = bufferedReader.readLine()) != null) {
            index++;
            ApabiBookMetaData apabiBookMetaData = apabiBookMetaDataDao.findById(line);
            String urlStr = "http://flow.apabi.com/flowadmin/bookSearch/index?pageNumber=1&metaId=" + line + "&title=&creator=&flowSource=&publisher=&isbn=isbn&isbnVal=&hasCebx=&hasFlow=&isPublicCopyRight=&saleStatus=";
            URL url = new URL(urlStr);
            URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("Cookie", "JSESSIONID=9C0ED4A42999ACE35E65F3A069B65368; Hm_lvt_07cfb20d5f1d7402d600e55de80e9127=1544177511,1544582097,1546077520,1546077585");
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String html = EntityUtils.toString(response.getEntity());
            Document document = Jsoup.parse(html);
            Elements elements = document.select("td[align='center']");
            String apabiTitle = apabiBookMetaData.getTitle();
            String title = elements.get(1).text();
            String apabiAuthor = apabiBookMetaData.getCreator();
            String author = elements.get(2).text();
            String apabiPublisher = apabiBookMetaData.getPublisher();
            String publisher = elements.get(3).text();
            if (apabiTitle != null && apabiTitle.equals(title) && apabiAuthor != null && apabiAuthor.equals(author) && apabiPublisher != null && apabiPublisher.equals(publisher)) {
            } else {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            System.out.println(index);
        }
        bufferedReader.close();
        bufferedWriter.close();
        return "";
    }
}
