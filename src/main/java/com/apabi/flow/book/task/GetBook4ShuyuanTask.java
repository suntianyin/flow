package com.apabi.flow.book.task;

import com.apabi.flow.book.dao.BookMetaDao;
import com.apabi.flow.book.dao.CmfDigitObjectDao;
import com.apabi.flow.book.dao.CmfDigitResfileSiteDao;
import com.apabi.flow.book.dao.CmfMetaDao;
import com.apabi.flow.book.model.BookMeta;
import com.apabi.flow.book.model.CmfDigitObject;
import com.apabi.flow.book.model.CmfDigitResfileSite;
import com.apabi.flow.book.model.CmfMeta;
import com.apabi.flow.book.util.BookUtil;
import com.apabi.flow.douban.dao.ApabiBookMetaDataTempDao;
import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import com.apabi.shuyuan.book.dao.SCmfDigitObjectDao;
import com.apabi.shuyuan.book.dao.SCmfDigitResfileSiteDao;
import com.apabi.shuyuan.book.dao.SCmfMetaDao;
import com.apabi.shuyuan.book.model.SCmfDigitObject;
import com.apabi.shuyuan.book.model.SCmfDigitResfileSite;
import com.apabi.shuyuan.book.model.SCmfMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author guanpp
 * @date 2018/10/31 11:19
 * @description 0：成功，-1：错误，-2：异常，1：任务执行完一次的时间节点
 */
@Component
public class GetBook4ShuyuanTask {

    private Logger logger = LoggerFactory.getLogger(GetBook4ShuyuanTask.class);

    @Autowired
    private BookMetaDao bookMetaDao;

    @Autowired
    private SCmfMetaDao sCmfMetaDao;

    @Autowired
    private SCmfDigitResfileSiteDao sCmfDigitResfileSiteDao;

    @Autowired
    private SCmfDigitObjectDao sCmfDigitObjectDao;

    @Autowired
    private CmfMetaDao cmfMetaDao;

    @Autowired
    private CmfDigitObjectDao cmfDigitObjectDao;

    @Autowired
    private CmfDigitResfileSiteDao cmfDigitResfileSiteDao;

    @Autowired
    private ApabiBookMetaDataTempDao apabiBookMetaDataTempDao;

    //每天0点执行一次
    //@Scheduled(cron = "0 0 0 * * ?")
    //@Scheduled(cron = "0 * * * * ?")
    public void insertBook2Oracle() {
        Integer lastDrid = 0;
        Integer maxDrid;
        try {
            //获取上次最后更新的drid
            lastDrid = bookMetaDao.getMaxDrid();
            //获取书苑最大drid
            maxDrid = sCmfMetaDao.getMaxDrid();
            //从书苑获取数据，并新增到流式图书服务
            if (lastDrid < maxDrid) {
                SCmfMeta sCmfMeta;
                for (int i = lastDrid; i < maxDrid + 1; i++) {
                    try {
                        sCmfMeta = sCmfMetaDao.findSCmfBookMetaByDrid(i);
                        if (sCmfMeta != null) {
                            BookMeta bookMeta = BookUtil.createBookMeta(sCmfMeta);
                            if (bookMeta != null) {
                                int res = bookMetaDao.insertBookMeta(bookMeta);
                                if (res > 0) {
                                    ApabiBookMetaDataTemp metaDataTemp = BookUtil.createBookMetaTemp(sCmfMeta);
                                    //新增到图书元数据temp表
                                    apabiBookMetaDataTempDao.insert(metaDataTemp);
                                    //获取书苑数据，更新到流式图书
                                    boolean ress = insertShuyuanData(sCmfMeta);
                                    if (ress) {
                                        logger.info("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                                0, i, "success", new Date());
                                    }else {
                                        logger.debug("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                                -2, i, "新增书苑数据异常", new Date());
                                    }
                                } else {
                                    logger.debug("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                            -2, i, "新增图书元数据异常", new Date());
                                }
                            } else {
                                logger.debug("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                        -2, i, "生成图书元数据异常", new Date());
                            }
                        } else {
                            logger.debug("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                    -2, i, "从书苑获取图书元数据异常", new Date());
                        }
                    } catch (Exception e) {
                        logger.warn("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                -1, i, e.getMessage(), new Date());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                    -1, lastDrid, e.getMessage(), new Date());
        }
    }

    //获取书苑数据cmf_meta、cmf_digitobject、cmf_digitresfile_site，更新到流式图书
    private boolean insertShuyuanData(SCmfMeta sCmfMeta) throws Exception {
        if (sCmfMeta != null) {
            //cmf_meta表新增
            CmfMeta cmfMeta = BookUtil.createCmfMeta(sCmfMeta);
            if (cmfMeta != null) {
                cmfMetaDao.insertCmfMeta(cmfMeta);
            } else {
                logger.warn("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                        -1, sCmfMeta.getDrid(), "生成CmfMeta失败", new Date());
            }
            List<SCmfDigitObject> sCmfDigitObjects =
                    sCmfDigitObjectDao.findSCmfDigitObjectByDrid(sCmfMeta.getDrid());
            if (sCmfDigitObjects != null && sCmfDigitObjects.size() > 0) {
                for (SCmfDigitObject sCmfDigitObject : sCmfDigitObjects) {
                    //cmf_digitobject表新增
                    CmfDigitObject cmfDigitObject = BookUtil.createCmfDigitObject(sCmfDigitObject);
                    if (cmfDigitObject != null) {
                        cmfDigitObjectDao.insertCmfDigitObject(cmfDigitObject);
                        List<SCmfDigitResfileSite> sCmfDigitResfileSites =
                                sCmfDigitResfileSiteDao.findSCmfDigitResfileSiteByFileId(sCmfDigitObject.getFileId());
                        if (sCmfDigitResfileSites != null && sCmfDigitResfileSites.size() > 0) {
                            for (SCmfDigitResfileSite sCmfDigitResfileSite : sCmfDigitResfileSites) {
                                //cmf_digitresfile_site表新增
                                CmfDigitResfileSite cmfDigitResfileSite = BookUtil.createCmfDigitResfileSite(sCmfDigitResfileSite);
                                if (cmfDigitResfileSite != null) {
                                    cmfDigitResfileSiteDao.insertCmfDigitResfileSite(cmfDigitResfileSite);
                                } else {
                                    logger.warn("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                            -1, sCmfMeta.getDrid(), "生成CmfDigitResfileSite失败", new Date());
                                }
                            }
                        } else {
                            logger.warn("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                    -1, sCmfMeta.getDrid(), "获取SCmfDigitResfileSite失败", new Date());
                        }
                    } else {
                        logger.warn("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                -1, sCmfMeta.getDrid(), "生成CmfDigitObject失败", new Date());
                    }
                }
            } else {
                logger.warn("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                        -1, sCmfMeta.getDrid(), "获取SCmfDigitObject失败", new Date());
            }
            return true;

        }
        return false;
    }

    //写文件
    private boolean writeFile(File file, Integer maxDrid) {
        if (file != null) {
            FileWriter fw = null;
            BufferedWriter bw = null;
            try {
                fw = new FileWriter(file.getAbsoluteFile());
                bw = new BufferedWriter(fw);
                bw.write(maxDrid);
                bw.close();
                fw.close();
                return true;
            } catch (IOException e) {
                logger.warn("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                        -1, maxDrid, "error", new Date());
            } finally {
                try {
                    bw.close();
                    fw.close();
                } catch (IOException e) {
                    logger.warn("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                            -1, maxDrid, "error", new Date());
                }
            }
        }
        return false;
    }

    //获取文档最后一行
    private String getLastLine(String log) throws IOException {
        if (!StringUtils.isEmpty(log)) {
            RandomAccessFile rf;
            rf = new RandomAccessFile(log, "r");
            long len = rf.length();
            long start = rf.getFilePointer();
            long nextEnd = start + len - 1;
            String line;
            rf.seek(nextEnd);
            int c;
            while (nextEnd > start) {
                c = rf.read();
                if (c == '\n' || c == '\r') {
                    line = rf.readLine();
                    if (StringUtils.isEmpty(line)) {
                        continue;
                    } else {
                        return new String(line.getBytes("UTF-8"), "UTF-8");
                    }
                }
                nextEnd--;
                rf.seek(nextEnd);
                if (nextEnd == 0) {
                    // 当文件指针退至文件开始处，输出第一行
                    return rf.readLine();
                }
            }

        }
        return null;
    }
}
