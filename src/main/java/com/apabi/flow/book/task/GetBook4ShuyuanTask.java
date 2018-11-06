package com.apabi.flow.book.task;

import com.apabi.flow.book.model.BookMeta;
import com.apabi.flow.book.service.BookMetaService;
import com.apabi.flow.book.util.SqlServerJdbc;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.shuyuan.book.dao.CmfBookMetaDao;
import com.apabi.shuyuan.book.model.CmfBookMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Date;

/**
 * @author guanpp
 * @date 2018/10/31 11:19
 * @description 0：成功，-1：错误，-2：异常，1：任务执行完一次的时间节点
 */
@Component
public class GetBook4ShuyuanTask {

    private Logger logger = LoggerFactory.getLogger(GetBook4ShuyuanTask.class);

    @Autowired
    private BookMetaService bookMetaService;

    @Autowired
    private CmfBookMetaDao cmfBookMetaDao;

    //每隔一个小时执行一次
    //@Scheduled(cron = "* * * * * ?")
    private void insertBook2Oracle() {
        Integer lastDrid = 0;
        Integer maxDrid = 0;
        try {
            //获取上次最后更新的drid
            lastDrid = 0;
            //获取数据库连接
            //SqlServerJdbc.getConn();
            //获取最大drid
            String sql = "SELECT MAX(DRID) as maxDrid FROM CMF_META_0001";
            maxDrid = cmfBookMetaDao.getMaxDrid();
            //从书苑获取数据，并新增到流式图书服务
            CmfBookMeta cmfBookMeta;
            for (int i = lastDrid; i < maxDrid + 1; i++) {
                //用于错误日志记录
                lastDrid = i;
                sql = "SELECT * FROM CMF_META_0001 WHERE DRID = '" + i + "'";
                cmfBookMeta = null;
                if (cmfBookMeta != null) {
                    BookMeta bookMeta = createBookMeta(cmfBookMeta);
                    if (bookMeta != null) {
                        /*int res = bookMetaService.saveBookMeta(bookMeta);
                        if (res > 0) {
                            logger.info("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                    0, i, "success", new Date());
                        } else {
                            logger.debug("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                    -2, i, "exception", new Date());
                        }*/
                    } else {
                        logger.debug("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                -2, i, "exception", new Date());
                    }
                } else {
                    logger.debug("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                            -2, i, "exception", new Date());
                }
            }
            //将maxDrid，写入数据文件，便于下次更新
            logger.warn("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                    1, maxDrid, "total", new Date());

        } catch (Exception e) {
            logger.warn("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                    -1, lastDrid, e.getMessage(), new Date());
        }

        //获取数据库连接
        //SqlServerJdbc.getConn();
    }

    //生成流式服务元数据
    private BookMeta createBookMeta(CmfBookMeta cmfBookMeta) {
        if (cmfBookMeta != null) {
            BookMeta bookMeta = new BookMeta();
            bookMeta.setMetaId(UUIDCreater.nextId());
            bookMeta.setDrId(bookMeta.getDrId());
            bookMeta.setTitle(bookMeta.getTitle());
            bookMeta.setAlternativeTitle(bookMeta.getAlternativeTitle());
            bookMeta.setCreator(bookMeta.getCreator());
            bookMeta.setSubject(bookMeta.getSubject());
            return bookMeta;
        }
        return null;
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
