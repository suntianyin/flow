package com.apabi.flow.book.util;

import com.apabi.flow.book.dao.*;
import com.apabi.flow.book.model.*;
import com.apabi.flow.book.service.BookMetaService;
import com.apabi.flow.book.task.ReadCebxBook;
import com.apabi.flow.book.task.ReadEpubook;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author guanpp
 * @date 2018/8/1 16:10
 * @description
 */
@Service("readBook")
public class ReadBook {

    private static final Logger log = LoggerFactory.getLogger(ReadBook.class);

    @Autowired
    GetEpubookChapter getEpubookChapter;

    @Autowired
    GetCebxChapter getCebxChapter;

    @Autowired
    BookMetaService bookMetaService;

    @Autowired
    ApplicationConfig config;

    @Autowired
    BookTaskResultMapper bookTaskResultMapper;

    @Autowired
    BookTaskMapper bookTaskMapper;

    @Autowired
    BookChapterDao bookChapterDao;

    @Autowired
    BookShardDao bookShardDao;

    @Autowired
    BookChapterBakDao bookChapterBakDao;

    //读取源文件
    public int readEpubook(String filePath, String metId) throws Exception {
        if (!StringUtils.isEmpty(filePath) && !StringUtils.isEmpty(metId)) {
            File file = new File(filePath);
            if (file != null) {
                List<EpubookMeta> epubookMetas = new ArrayList<>();
                EpubookMeta epubookMeta = bookMetaService.selectEpubookMetaById(metId);
                //判断是否生成章节内容
                boolean flag = (epubookMeta.getChapterNum() != null) && (epubookMeta.getChapterNum() > 0);
                if (flag) {
                    return -1;
                }
                try {
                    //epubookMeta.setFileName(file.getName());
                    EpubookMeta res = getEpubookChapter.insertEpubook(filePath, epubookMeta, file.getName());
                    if (res != null && res.getMetaid() != null) {
                        epubookMetas.add(res);
                        return 1;
                    }
                } catch (IOException e) {
                    log.warn("解析图书：" + file + "时{}" + e);
                }
                //生成文件名和图书id映射表
                //BookUtil.exportExcel(epubookMetas);
            } else {
                log.warn(filePath + "：该路径下没有文件！");
            }
        }
        return 0;
    }

    //批量发布Epub
    public List<BookBatchRes> batchChapterEpub(String fileInfo, String filePath) throws Exception {
        if (!StringUtils.isEmpty(fileInfo) && !StringUtils.isEmpty(filePath)) {
            String[] fileInfos = fileInfo.split(";");
            if (fileInfos != null && fileInfos.length > 0) {
                List<BookBatchRes> bookBatchResList = new ArrayList<>();
                for (String file : fileInfos) {
                    String[] fileId = file.split(",");
                    if (fileId != null && fileId.length == 2) {
                        //传入文件路径和图书metaId
                        int res = readEpubook(filePath + File.separator + fileId[1], fileId[0]);
                        BookBatchRes bookBatchRes = new BookBatchRes();
                        bookBatchRes.setFileName(fileId[1]);
                        bookBatchRes.setMetaId(fileId[0]);
                        if (res > 0) {
                            bookBatchRes.setStatus(1);
                        } else {
                            bookBatchRes.setStatus(0);
                        }
                        bookBatchResList.add(bookBatchRes);
                    }
                }
                return bookBatchResList;
            }
        }
        return null;
    }

    //多线程上传epub
    public List<BookBatchRes> batchEpub(String fileInfo, String filePath) {
        if (!StringUtils.isEmpty(fileInfo) && !StringUtils.isEmpty(filePath)) {
            try {
                String[] fileInfos = fileInfo.split(";");
                if (fileInfos != null && fileInfos.length > 0) {
                    List<BookBatchRes> bookBatchResList = new ArrayList<>();
                    LinkedBlockingQueue<String> filePathQueue = new LinkedBlockingQueue<>();
                    Map<String, String> fileInfoMap = new Hashtable<>();
                    //将文件信息存入队列
                    for (String file : fileInfos) {
                        String[] fileId = file.split(",");
                        if (fileId != null && fileId.length == 2) {
                            /*filePathQueue.put(filePath + File.separator + fileId[1]);
                            fileInfoMap.put(filePath + File.separator + fileId[1], fileId[0]);*/
                            filePathQueue.put(fileId[0]);
                            fileInfoMap.put(fileId[0], filePath + File.separator + fileId[1]);
                        }
                    }
                    //创建上传epub任务
                    ReadEpubook readEpubook = new ReadEpubook(filePathQueue,
                            fileInfoMap,
                            bookBatchResList,
                            bookMetaService,
                            getEpubookChapter);
                    //获取cpu的核数
                    int cpuSum = Runtime.getRuntime().availableProcessors();
                    ThreadPoolExecutor executor = new ThreadPoolExecutor(config.getThreadTime() * cpuSum,
                            config.getThreadTime() * cpuSum,
                            60,
                            TimeUnit.SECONDS,
                            new LinkedBlockingDeque<>());
                    //创建线程任务
                    int threadSum = filePathQueue.size();
                    for (int i = 0; i < threadSum; i++) {
                        executor.execute(readEpubook);
                    }
                    executor.shutdown();
                    while (true) {
                        if (executor.isTerminated()) {
                            return bookBatchResList;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("批量上传目录{}，时出现异常{}", filePath, e.getMessage());
            }
        }
        return null;
    }

    //多线程上传epub任务
    @Async
    public void batchEpubTask(String fileInfo, String filePath, String taskId) {
        if (!StringUtils.isEmpty(fileInfo) && !StringUtils.isEmpty(filePath)) {
            try {
                long start = System.currentTimeMillis();
                String[] fileInfos = fileInfo.split(";");
                if (fileInfos != null && fileInfos.length > 0) {
                    List<BookBatchRes> bookBatchResList = new ArrayList<>();
                    LinkedBlockingQueue<String> filePathQueue = new LinkedBlockingQueue<>();
                    Map<String, String> fileInfoMap = new Hashtable<>();
                    //将文件信息存入队列
                    for (String file : fileInfos) {
                        String[] fileId = file.split(",");
                        if (fileId != null && fileId.length == 2) {
                            filePathQueue.put(fileId[0]);
                            fileInfoMap.put(fileId[0], filePath + File.separator + fileId[1]);
                        }
                    }
                    //更改任务状态
                    BookTask bookTask = new BookTask();
                    bookTask.setId(taskId);
                    bookTask.setStatus(3);
                    bookTask.setUpdateTime(new Date());
                    bookTaskMapper.updateByPrimaryKeySelective(bookTask);
                    //创建上传epub任务
                    ReadEpubook readEpubook = new ReadEpubook(filePathQueue,
                            fileInfoMap,
                            bookBatchResList,
                            bookMetaService,
                            getEpubookChapter);
                    //获取cpu的核数
                    int cpuSum = Runtime.getRuntime().availableProcessors();
                    ThreadPoolExecutor executor = new ThreadPoolExecutor(config.getThreadTime() * cpuSum,
                            config.getThreadTime() * cpuSum,
                            60,
                            TimeUnit.SECONDS,
                            new LinkedBlockingDeque<>());
                    //创建线程任务
                    int threadSum = filePathQueue.size();
                    for (int i = 0; i < threadSum; i++) {
                        executor.execute(readEpubook);
                    }
                    executor.shutdown();
                    while (true) {
                        if (executor.isTerminated()) {
                            //上传结果入库
                            List<BookTaskResult> taskResultList = createBookTask(bookBatchResList);
                            for (BookTaskResult result : taskResultList) {
                                result.setUpdateTime(new Date());
                                bookTaskResultMapper.updateTaskByMetaId(result);
                            }
                            //更改任务状态
                            bookTask.setStatus(4);
                            bookTask.setUpdateTime(new Date());
                            bookTaskMapper.updateByPrimaryKeySelective(bookTask);
                            long end = System.currentTimeMillis();
                            log.info("批量上传目录{}已完成，耗时：{}毫秒", filePath, (end - start));
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("批量上传目录{}，时出现异常{}", filePath, e.getMessage());
            }
        }
    }

    //转换为扫描文件任务
    private List<BookTaskResult> createBookTask(List<BookBatchRes> bookMetaList) {
        if (bookMetaList != null && bookMetaList.size() > 0) {
            List<BookTaskResult> bookTaskList = new ArrayList<>();
            for (BookBatchRes bookBatchRes : bookMetaList) {
                BookTaskResult taskResult = new BookTaskResult();
                taskResult.setStatus(bookBatchRes.getStatus());
                if (bookBatchRes.getStatus() == 1) {
                    taskResult.setHasFlow(1);
                }
                taskResult.setMetaId(bookBatchRes.getMetaId());
                bookTaskList.add(taskResult);
            }
            return bookTaskList;
        }
        return null;
    }

    //读取cebx源文件
    public int readCebxBook(String filePath, String metId) throws Exception {
        if (!StringUtils.isEmpty(filePath) && !StringUtils.isEmpty(metId)) {
            File file = new File(filePath);
            if (file != null) {
                List<EpubookMeta> epubookMetas = new ArrayList<>();
                EpubookMeta epubookMeta = bookMetaService.selectEpubookMetaById(metId);
                //判断是否生成章节内容
                boolean flag = (epubookMeta.getChapterNum() != null) && (epubookMeta.getChapterNum() > 0);
                if (flag) {
                    return -1;
                }
                try {
                    EpubookMeta res = getCebxChapter.insertCebx(filePath, epubookMeta, file.getName());
                    if (res != null && res.getMetaid() != null) {
                        epubookMetas.add(res);
                        return 1;
                    }
                } catch (Exception e) {
                    log.warn("解析图书失败：" + file + "时{}" + e);
                    e.printStackTrace();
                } finally {
                    File dir = new File(config.getTargetCebxDir());
                    File[] fileList = dir.listFiles();
                    deleteFiles(fileList);
                    log.info("cebx的html文件删除成功");
                }
                //生成文件名和图书id映射表
                //BookUtil.exportExcel(epubookMetas);
            } else {
                log.warn(filePath + "：该路径下没有文件！");
            }
        }
        return 0;
    }

    //批量发布Cebx
    public List<BookBatchRes> batchChapterCebx(String fileInfo, String filePath) throws Exception {
        if (!StringUtils.isEmpty(fileInfo) && !StringUtils.isEmpty(filePath)) {
            String[] fileInfos = fileInfo.split(";");
            if (fileInfos != null && fileInfos.length > 0) {
                List<BookBatchRes> bookBatchResList = new ArrayList<>();
                for (String file : fileInfos) {
                    String[] fileId = file.split(",");
                    if (fileId != null && fileId.length == 2) {
                        //传入文件路径和图书metaId
                        int res = readCebxBook(filePath + File.separator + fileId[1], fileId[0]);
                        BookBatchRes bookBatchRes = new BookBatchRes();
                        bookBatchRes.setFileName(fileId[1]);
                        bookBatchRes.setMetaId(fileId[0]);
                        if (res > 0) {
                            bookBatchRes.setStatus(1);
                        } else {
                            bookBatchRes.setStatus(0);
                        }
                        bookBatchResList.add(bookBatchRes);
                    }
                }
                return bookBatchResList;
            }
        }
        return null;
    }

    //多线程上传cebx
    public List<BookBatchRes> batchCebx(String fileInfo, String filePath) {
        if (!StringUtils.isEmpty(fileInfo) && !StringUtils.isEmpty(filePath)) {
            try {
                String[] fileInfos = fileInfo.split(";");
                if (fileInfos != null && fileInfos.length > 0) {
                    List<BookBatchRes> bookBatchResList = new ArrayList<>();
                    LinkedBlockingQueue<String> filePathQueue = new LinkedBlockingQueue<>();
                    Map<String, String> fileInfoMap = new Hashtable<>();
                    for (String file : fileInfos) {
                        String[] fileId = file.split(",");
                        if (fileId != null && fileId.length == 2) {
                            filePathQueue.put(fileId[0]);
                            fileInfoMap.put(fileId[0], filePath + File.separator + fileId[1]);
                        }
                    }
                    //创建上传epub任务
                    ReadCebxBook readCebxBook = new ReadCebxBook(filePathQueue,
                            fileInfoMap,
                            bookBatchResList,
                            bookMetaService,
                            getCebxChapter,
                            config);
                    //获取cpu的核数
                    int cpuSum = Runtime.getRuntime().availableProcessors();
                    ThreadPoolExecutor executor = new ThreadPoolExecutor(config.getThreadTime() * cpuSum,
                            config.getThreadTime() * cpuSum,
                            60,
                            TimeUnit.SECONDS,
                            new LinkedBlockingDeque<>());
                    //创建线程任务
                    int threadSum = filePathQueue.size();
                    for (int i = 0; i < threadSum; i++) {
                        executor.execute(readCebxBook);
                    }
                    executor.shutdown();
                    while (true) {
                        if (executor.isTerminated()) {
                            return bookBatchResList;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("批量上传目录{}，时出现异常{}", filePath, e.getMessage());
            } finally {
                File dir = new File(config.getTargetCebxDir());
                File[] fileList = dir.listFiles();
                deleteFiles(fileList);
                log.info("cebx的html文件删除成功");
            }
        }
        return null;
    }

    //多线程上传cebx任务
    @Async
    public void batchCebxTask(String fileInfo, String filePath, String taskId) {
        if (!StringUtils.isEmpty(fileInfo) && !StringUtils.isEmpty(filePath)) {
            try {
                long start = System.currentTimeMillis();
                String[] fileInfos = fileInfo.split(";");
                if (fileInfos != null && fileInfos.length > 0) {
                    List<BookBatchRes> bookBatchResList = new ArrayList<>();
                    LinkedBlockingQueue<String> filePathQueue = new LinkedBlockingQueue<>();
                    Map<String, String> fileInfoMap = new Hashtable<>();
                    for (String file : fileInfos) {
                        String[] fileId = file.split(",");
                        if (fileId != null && fileId.length == 2) {
                            filePathQueue.put(fileId[0]);
                            fileInfoMap.put(fileId[0], filePath + File.separator + fileId[1]);
                        }
                    }
                    //更改任务状态
                    BookTask bookTask = new BookTask();
                    bookTask.setId(taskId);
                    bookTask.setStatus(3);
                    bookTask.setUpdateTime(new Date());
                    bookTaskMapper.updateByPrimaryKeySelective(bookTask);
                    //创建上传cebx任务
                    ReadCebxBook readCebxBook = new ReadCebxBook(filePathQueue,
                            fileInfoMap,
                            bookBatchResList,
                            bookMetaService,
                            getCebxChapter,
                            config);
                    //获取cpu的核数
                    int cpuSum = Runtime.getRuntime().availableProcessors();
                    ThreadPoolExecutor executor = new ThreadPoolExecutor(config.getThreadTime() * cpuSum,
                            config.getThreadTime() * cpuSum,
                            60,
                            TimeUnit.SECONDS,
                            new LinkedBlockingDeque<>());
                    //创建线程任务
                    int threadSum = filePathQueue.size();
                    for (int i = 0; i < threadSum; i++) {
                        executor.execute(readCebxBook);
                    }
                    executor.shutdown();
                    while (true) {
                        if (executor.isTerminated()) {
                            //上传结果入库
                            List<BookTaskResult> taskResultList = createBookTask(bookBatchResList);
                            for (BookTaskResult result : taskResultList) {
                                result.setUpdateTime(new Date());
                                bookTaskResultMapper.updateTaskByMetaId(result);
                            }
                            //更改任务状态
                            bookTask.setStatus(4);
                            bookTask.setUpdateTime(new Date());
                            bookTaskMapper.updateByPrimaryKeySelective(bookTask);
                            long end = System.currentTimeMillis();
                            log.info("批量上传目录{}已完成，耗时：{}毫秒", filePath, (end - start));
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("批量上传目录{}，时出现异常{}", filePath, e.getMessage());
            } finally {
                File dir = new File(config.getTargetCebxDir());
                File[] fileList = dir.listFiles();
                deleteFiles(fileList);
                log.info("cebx的html文件删除成功");
            }
        }
    }

    //删除文件
    private void deleteFiles(File[] files) {
        if (files != null && files.length > 0) {
            for (File file : files) {
                File[] children = file.listFiles();
                if (children != null && children.length > 0) {
                    deleteFiles(children);
                } else {
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }
    }

    //删除文件
    private void deleteFiles(String[] filePaths) {
        if (filePaths != null && filePaths.length > 0) {
            for (String filePath : filePaths) {
                File file = new File(config.getTargetCebxDir() + File.separator + filePath);
                if (file.exists()) {
                    file.delete();
                }
            }
            log.info("cebx的html文件删除成功");
        }
    }

}
