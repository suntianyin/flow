package com.apabi.flow.crawlTask.jd;

import com.alibaba.fastjson.JSONObject;
import com.apabi.flow.common.ResultEntity;
import com.apabi.flow.douban.model.ApabiBookMetaData;
import com.apabi.flow.jd.dao.JdMetadataDao;
import com.apabi.flow.jd.model.JdMetadata;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2019-1-14 14:36
 **/
public class GenerateMetaDataFromJdConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateMetaDataFromJdConsumer.class);
    private LinkedBlockingQueue<JdMetadata> jdMetadataQueue;
    private CountDownLatch countDownLatch;
    private CloseableHttpClient httpClient;
    private JdMetadataDao jdMetadataDao;

    public GenerateMetaDataFromJdConsumer(LinkedBlockingQueue<JdMetadata> jdMetadataQueue, JdMetadataDao jdMetadataDao, CountDownLatch countDownLatch, CloseableHttpClient httpClient) {
        this.jdMetadataQueue = jdMetadataQueue;
        this.countDownLatch = countDownLatch;
        this.httpClient = httpClient;
        this.jdMetadataDao = jdMetadataDao;
    }

    @Override
    public void run() {
        JdMetadata jdMetadata = null;
        String isbn = null;
        try {
            jdMetadata = jdMetadataQueue.take();
            isbn = jdMetadata.getIsbn13();
            if (StringUtils.isNotEmpty(isbn)) {
                HttpGet httpGet = new HttpGet("http://flow.apabi.com/flow/meta/find/" + isbn);
                CloseableHttpResponse response = httpClient.execute(httpGet);
                String html = EntityUtils.toString(response.getEntity());
                ResultEntity resultEntity = JSONObject.parseObject(html, ResultEntity.class);
                if (resultEntity != null && resultEntity.getStatus() == HttpStatus.SC_OK) {
                    List<ApabiBookMetaData> apabiBookMetaDataList = JSONObject.parseArray(resultEntity.getBody().toString(), ApabiBookMetaData.class);
                    if (apabiBookMetaDataList != null && apabiBookMetaDataList.size() > 0) {
                        ApabiBookMetaData apabiBookMetaData = apabiBookMetaDataList.get(0);
                        if (apabiBookMetaData != null && StringUtils.isNotEmpty(apabiBookMetaData.getMetaId())) {
                            jdMetadata.setMetaId(apabiBookMetaData.getMetaId());
                        }
                        jdMetadataDao.update(jdMetadata);
                        LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "在jd生成" + jdMetadata.getIsbn13() + "成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                    }
                }
            } else {
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "在jd生成" + jdMetadata.getIsbn13() + "成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}