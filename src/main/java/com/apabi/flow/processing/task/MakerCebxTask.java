package com.apabi.flow.processing.task;

import com.apabi.flow.book.dao.BookMetaDao;
import com.apabi.flow.book.model.BookMeta;
import com.apabi.flow.book.task.ReadEpubook;
import com.apabi.flow.config.ApplicationConfig;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.douban.model.DoubanMeta;
import com.apabi.flow.processing.dao.BatchMapper;
import com.apabi.flow.processing.dao.BibliothecaMapper;
import com.apabi.flow.processing.model.Batch;
import com.apabi.flow.processing.model.Bibliotheca;
import com.apabi.flow.systemconf.util.FileUtil;
import com.apabi.maker.MakerAgent;
import com.apabi.maker.MakerUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author guanpp
 * @date 2019/1/24 13:56
 * @description
 */
public class MakerCebxTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MakerCebxTask.class);

    private LinkedBlockingQueue<MakerTaskBo> makerTaskQueue;

    private ApplicationConfig config;

    private BatchMapper batchMapper;

    private BibliothecaMapper bibliothecaMapper;

    private BookMetaDao bookMetaDao;

    private DoubanMetaDao doubanMetaDao;

    private Set<String> metaIdSet;

    public MakerCebxTask(LinkedBlockingQueue<MakerTaskBo> makerTaskQueue,
                         ApplicationConfig config,
                         BatchMapper batchMapper,
                         BibliothecaMapper bibliothecaMapper,
                         BookMetaDao bookMetaDao,
                         DoubanMetaDao doubanMetaDao,
                         Set<String> metaIdSet) {
        this.makerTaskQueue = makerTaskQueue;
        this.config = config;
        this.batchMapper = batchMapper;
        this.bibliothecaMapper = bibliothecaMapper;
        this.bookMetaDao = bookMetaDao;
        this.doubanMetaDao = doubanMetaDao;
        this.metaIdSet = metaIdSet;
    }

    //fileMap : 0-文件名，1-出版日期，2-metaId，3-是否存在任务中:0不存在，1存在
    @Override
    public void run() {
        //存储待加密的cebx文件路径
        List<String> cebxPath = new ArrayList<>();
        //生成job文件
        String jobPath = ApplicationConfig.getCebxMaker() + File.separator + "job";
        MakerTaskBo makerTaskBo;
        String dirPath;
        try {
            makerTaskBo = makerTaskQueue.take();
            Map<String, String[]> fileMap = makerTaskBo.getFileMap();
            dirPath = makerTaskBo.getDirPath();
            Map<String, String> jobMap = MakerUtil.createJobXml(dirPath, config.getUploadCebx(), jobPath, fileMap);
            if (jobMap != null && jobMap.size() > 0) {
                long startS = System.currentTimeMillis();
                logger.info("路径{}转换cebx已开始", makerTaskBo.getDirPath());
                //更新转换状态
                Batch batch = new Batch();
                batch.setBatchId(makerTaskBo.getBatchId());
                batch.setConvertStatus(1);
                batch.setUpdateTime(new Date());
                batchMapper.updateByBatchId(batch);
                for (Map.Entry entry : fileMap.entrySet()) {
                    String[] fileInfo = (String[]) entry.getValue();
                    Bibliotheca bibliotheca = new Bibliotheca();
                    try {
                        logger.info("文件{}转换cebx已开始", fileInfo[0]);
                        long start = System.currentTimeMillis();
                        bibliotheca.setId((String) entry.getKey());
                        bibliotheca.setConvertStatus(1);
                        bibliotheca.setUpdateTime(new Date());
                        bibliothecaMapper.updateByPrimaryKeySelective(bibliotheca);
                        String fileJob = jobMap.get(entry.getKey());
                        if (StringUtils.isNotBlank(fileJob)) {
                            //新任务进行转换
                            if (fileInfo[3].equals("0")){
                                int res = MakerAgent.DoConvert(fileJob, 1000);
                                if (res == 0) {
                                    bibliotheca.setConvertStatus(2);
                                    bibliotheca.setUpdateTime(new Date());
                                    bibliothecaMapper.updateByPrimaryKeySelective(bibliotheca);
                                    long end = System.currentTimeMillis();
                                    cebxPath.add(config.getUploadCebx() + File.separator + fileInfo[1] + File.separator + fileInfo[2] + ".CEBX");
                                    //从验证任务set中，去掉该任务
                                    metaIdSet.remove(fileInfo[2]);
                                    logger.info("文件{}转换cebx成功，耗时{}", fileInfo[0], (end - start));
                                } else {
                                    bibliotheca.setConvertStatus(-1);
                                    bibliotheca.setUpdateTime(new Date());
                                    bibliothecaMapper.updateByPrimaryKeySelective(bibliotheca);
                                    logger.info("文件{}转换cebx失败", fileInfo[0]);
                                }
                            }
                        } else {
                            bibliotheca.setConvertStatus(-1);
                            bibliotheca.setUpdateTime(new Date());
                            bibliothecaMapper.updateByPrimaryKeySelective(bibliotheca);
                            logger.info("文件{}转换cebx时，job文件未生成", fileInfo[0]);
                        }
                    } catch (Exception e) {
                        bibliotheca.setConvertStatus(-1);
                        bibliotheca.setUpdateTime(new Date());
                        bibliothecaMapper.updateByPrimaryKeySelective(bibliotheca);
                        logger.warn("文件{}转换cebx时，出现异常{}", fileInfo[0], e.getMessage());
                    }
                }
                //转存源文件到cebx目录下
                logger.info("路径{}下的文件，转存开始", dirPath);
                long startC = System.currentTimeMillis();
                copy2Cebx(dirPath, fileMap);
                long endC = System.currentTimeMillis();
                logger.info("路径{}下的文件，已转存到{}，耗时{}", dirPath, config.getUploadCebx(), (endC - startC));
                //cebx文件加密
                logger.info("路径{}转换后的cebx，加密开始", dirPath);
                long start = System.currentTimeMillis();
                encryptCebx(cebxPath);
                long end = System.currentTimeMillis();
                logger.info("路径{}转换后的cebx，已加密完成，耗时{}", dirPath, (end - start));
                //更新转换状态
                batch.setConvertStatus(2);
                batch.setUpdateTime(new Date());
                batchMapper.updateByBatchId(batch);
                long endS = System.currentTimeMillis();
                logger.info("路径{}转换cebx已结束，耗时{}", dirPath, (endS - startS));
                //下载图书封面
                logger.info("路径{}下的文件，下载封面开始", dirPath);
                long startD = System.currentTimeMillis();
                downloadCover(cebxPath);
                long endD = System.currentTimeMillis();
                logger.info("路径{}下的文件，下载封面已结束，耗时{}", dirPath, (endD - startD));
            }
        } catch (InterruptedException e) {
            logger.warn("获取转换任务时出现异常{}", e.getMessage());
        }
    }

    //下载图书封面
    private void downloadCover(List<String> cebxPath) {
        if (cebxPath != null && cebxPath.size() > 0) {
            for (String cebx : cebxPath) {
                try {
                    //获取metaId和路径
                    String path = cebx.substring(0, cebx.lastIndexOf(File.separator));
                    String metaId = cebx.replace(path + File.separator, "")
                            .replaceAll("(?i).cebx", "");
                    BookMeta bookMeta = bookMetaDao.findBookMetaById(metaId);
                    DoubanMeta doubanMeta = doubanMetaDao.findById(bookMeta.getDoubanId());
                    if (doubanMeta != null) {
                        //下载大图
                        FileUtil.download4Url(doubanMeta.getLargeCover(), path, metaId + "_L.JPG");
                        //下载中图
                        FileUtil.download4Url(doubanMeta.getMediumCover(), path, metaId + "_M.JPG");
                        //下载小图
                        FileUtil.download4Url(doubanMeta.getSmallCover(), path, metaId + "_S.JPG");
                    } else {
                        logger.warn("文件{}，在豆瓣网上不存在", cebx);
                    }
                } catch (Exception e) {
                    logger.warn("文件{}，在下载封面时出现异常{}", e.getMessage());
                }
            }
        }
    }

    //转存文件到cebx目录下
    private void copy2Cebx(String dirPath, Map<String, String[]> fileMap) {
        if (StringUtils.isNotBlank(dirPath)
                && fileMap != null
                && fileMap.size() > 0) {
            for (Map.Entry entry : fileMap.entrySet()) {
                String[] fileInfo = (String[]) entry.getValue();
                //源文件路径
                String filePath = dirPath + File.separator + fileInfo[0];
                //新文件路径
                String cebxParentPath = config.getUploadCebx() + File.separator + fileInfo[1];
                File parent = new File(cebxParentPath);
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                //获取源文件后缀
                String suffix = fileInfo[0].substring(fileInfo[0].lastIndexOf(".") + 1);
                String newFilePath = cebxParentPath + File.separator + fileInfo[2] + "." + suffix;
                //写文件
                try {
                    Files.copy(new File(filePath).toPath(), new File(newFilePath).toPath());
                } catch (IOException e) {
                    logger.warn("转存文件{}，时出现异常{}", filePath, e.getMessage());
                }
            }
        }
    }

    //cebx文件加密
    private void encryptCebx(List<String> cebxPath) {
        if (cebxPath != null && cebxPath.size() > 0) {
            //调用cmd
            Runtime runtime = Runtime.getRuntime();
            for (String path : cebxPath) {
                try {
                    String cmd = config.getCebxCryptExe() +
                            " -src " + "\"" + path + "\"" +
                            " -des " + "\"" + path + "M\"" +
                            " -mode encrypt";
                    Process process = runtime.exec(cmd);
                    //int ress = process.exitValue();
                    int ress = process.waitFor();
                    if (ress == 0) {
                        logger.info("加密文件：{}成功", path);
                        //删除cebx文件
                        File cebx = new File(path);
                        if (cebx.exists()) {
                            cebx.delete();
                            logger.info("cebx文件{}，删除成功", cebx.getName());
                        }
                    } else {
                        logger.warn("加密文件：{}失败", path);
                    }
                } catch (IOException e) {
                    logger.warn("加密文件：{}，时出现异常{}", path, e.getMessage());
                } catch (InterruptedException e) {
                    logger.warn("加密文件：{}，时出现异常{}", path, e.getMessage());
                }

            }
        }
    }
}
