package com.apabi.maker;

import com.apabi.flow.config.ApplicationConfig;
import com.apabi.flow.processing.service.impl.BibliothecaServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author 闫进兵
 * @ClassName: MakerAgent
 * @Description: Apabi MakerSDK 封装调用
 * @date 2014-3-10 上午11:23:17
 */
@Component
public class MakerAgent {

    private static final Logger logger = LoggerFactory.getLogger(MakerAgent.class);

    public static int concurrancyNum = 0;

//    static {
//        String dll = ApplicationConfig.getCebxMaker() + "/Runtime/Bin/MakerAgent.dll";
//        System.load(dll);
//        run();
//    }

    public static void init() {
        String dll = ApplicationConfig.getCebxMaker() + "/Runtime/Bin/MakerAgent.dll";
        System.load(dll);
        run();
    }

    public native static int DoConvert(String filePath, int nTimeOut);

    public native static int InitServerUrl(String AppID, String serverUrl);

    public native static String GetUserInfo(String AppID, String szSN, boolean bIsLocal);

    public native static int GetConcurrancyNum(String AppID, String szSN, boolean bIsLocal, String szKey);

    public native static String GetExpiredTime(String AppID, String szSN, boolean bIsLocal);

    public native static String GetSN(String AppID, boolean bIsLocal);

    public static void run() {

        String strAppID = "CEBX Maker Server-SM5ZFMV60W1M";
        String strSN = "";
        String strKey = "Concurrent";

        int result1 = MakerAgent.GetConcurrancyNum(strAppID, strSN, true, strKey);
        concurrancyNum = result1;
        logger.info("并发数 = " + result1);

        String result2 = MakerAgent.GetUserInfo(strAppID, strSN, true);
        logger.info("获取用户信息:" + result2);

        String result3 = MakerAgent.GetExpiredTime(strAppID, strSN, true);
        logger.info("获取过期时间:" + result3);

        String result4 = MakerAgent.GetSN(strAppID, true);
        logger.info("获取注册码:" + result4);
    }

    public static void main(String[] args) {

        //String fileJob = MakerAgent.class.getClassLoader().getResource("").getPath() + "MakerSDK/job/job.xml";
        String fileJob = "C:\\Users\\guanpp\\Desktop\\test\\job\\20190108102251job.xml";
        //fileJob = fileJob.substring(1, fileJob.length());
        MakerAgent.init();
        MakerAgent.DoConvert(fileJob, 1000);
    }
}
