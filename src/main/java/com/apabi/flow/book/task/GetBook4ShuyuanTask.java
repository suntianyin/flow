package com.apabi.flow.book.task;

import com.apabi.flow.book.dao.BookMetaDao;
import com.apabi.flow.book.dao.CmfDigitObjectDao;
import com.apabi.flow.book.dao.CmfDigitResfileSiteDao;
import com.apabi.flow.book.dao.CmfMetaDao;
import com.apabi.flow.book.model.BookMeta;
import com.apabi.flow.book.model.CmfDigitObject;
import com.apabi.flow.book.model.CmfDigitResfileSite;
import com.apabi.flow.book.model.CmfMeta;
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
                            BookMeta bookMeta = createBookMeta(sCmfMeta);
                            if (bookMeta != null) {
                                int res = bookMetaDao.insertBookMeta(bookMeta);
                                if (res > 0) {
                                    ApabiBookMetaDataTemp metaDataTemp = createBookMetaTemp(sCmfMeta);
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
            CmfMeta cmfMeta = createCmfMeta(sCmfMeta);
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
                    CmfDigitObject cmfDigitObject = createCmfDigitObject(sCmfDigitObject);
                    if (cmfDigitObject != null) {
                        cmfDigitObjectDao.insertCmfDigitObject(cmfDigitObject);
                        List<SCmfDigitResfileSite> sCmfDigitResfileSites =
                                sCmfDigitResfileSiteDao.findSCmfDigitResfileSiteByFileId(sCmfDigitObject.getFileId());
                        if (sCmfDigitResfileSites != null && sCmfDigitResfileSites.size() > 0) {
                            for (SCmfDigitResfileSite sCmfDigitResfileSite : sCmfDigitResfileSites) {
                                //cmf_digitresfile_site表新增
                                CmfDigitResfileSite cmfDigitResfileSite = createCmfDigitResfileSite(sCmfDigitResfileSite);
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

    //生成流式服务CmfDigitResfileSite
    private CmfDigitResfileSite createCmfDigitResfileSite(SCmfDigitResfileSite sCmfDigitResfileSite) {
        if (sCmfDigitResfileSite != null) {
            CmfDigitResfileSite cmfDigitResfileSite = new CmfDigitResfileSite();
            cmfDigitResfileSite.setFileId(sCmfDigitResfileSite.getFileId());
            cmfDigitResfileSite.setSiteId(sCmfDigitResfileSite.getSiteId());
            cmfDigitResfileSite.setUrlFileName(sCmfDigitResfileSite.getUrlFileName());
            cmfDigitResfileSite.setUrlFilePath(sCmfDigitResfileSite.getUrlFilePath());
            return cmfDigitResfileSite;
        }
        return null;
    }

    //生成流式服务cmfDigitObject
    private CmfDigitObject createCmfDigitObject(SCmfDigitObject sCmfDigitObject) {
        if (sCmfDigitObject != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            CmfDigitObject cmfDigitObject = new CmfDigitObject();
            cmfDigitObject.setFileId(sCmfDigitObject.getFileId());
            cmfDigitObject.setDrId(sCmfDigitObject.getDrId());
            cmfDigitObject.setPfileId(sCmfDigitObject.getPfileId());
            cmfDigitObject.setFileName(sCmfDigitObject.getFileName());
            cmfDigitObject.setFileDesc(sCmfDigitObject.getFileDesc());

            cmfDigitObject.setFilePath(sCmfDigitObject.getFilePath());
            cmfDigitObject.setOrderNew(sCmfDigitObject.getOrder());
            cmfDigitObject.setObjId(sCmfDigitObject.getObjId());
            cmfDigitObject.setPobjId(sCmfDigitObject.getPobjId());
            cmfDigitObject.setDoi(sCmfDigitObject.getDoi());

            cmfDigitObject.setFileSize(sCmfDigitObject.getFileSize());
            cmfDigitObject.setFormatNew(sCmfDigitObject.getFormat());
            cmfDigitObject.setContentTableInfo(sCmfDigitObject.getContentTableInfo());
            cmfDigitObject.setEncryptInfo(sCmfDigitObject.getEncryptInfo());
            cmfDigitObject.setObjType(sCmfDigitObject.getObjType());

            cmfDigitObject.setImgWidth(sCmfDigitObject.getImgWidth());
            cmfDigitObject.setImgHeigth(sCmfDigitObject.getImgHeigth());
            cmfDigitObject.setChapterIndex(sCmfDigitObject.getChapterIndex());
            cmfDigitObject.setFileCreatedDate(sdf.format(sCmfDigitObject.getFileCreatedDate()));
            cmfDigitObject.setFileLastModDate(sdf.format(sCmfDigitObject.getFileLastModDate()));

            cmfDigitObject.setContentFormat(sCmfDigitObject.getContentFormat());
            cmfDigitObject.setCebxSubset(sCmfDigitObject.getCebxSubset());
            cmfDigitObject.setSecurityNew(sCmfDigitObject.getSecurity());
            //cmfDigitObject.setcatalog
            return cmfDigitObject;
        }
        return null;
    }

    //生成流式服务CmfMeta
    private CmfMeta createCmfMeta(SCmfMeta sCmfMeta) {
        if (sCmfMeta != null) {
            CmfMeta cmfMeta = new CmfMeta();
            cmfMeta.setDrId(sCmfMeta.getDrid());
            cmfMeta.setTitle(sCmfMeta.getTitle());
            cmfMeta.setAlternativeTitle(sCmfMeta.getAlternativeTitle());
            cmfMeta.setCreator(sCmfMeta.getCreator());
            cmfMeta.setSubject(sCmfMeta.getSubject());

            cmfMeta.setAbstract(sCmfMeta.getAbstract());
            cmfMeta.setPublisher(sCmfMeta.getPublisher());
            cmfMeta.setType(sCmfMeta.getType());
            cmfMeta.setContributor(sCmfMeta.getContributor());
            cmfMeta.setIssuedDate(sCmfMeta.getIssuedDate());

            cmfMeta.setYear(sCmfMeta.getYear());
            cmfMeta.setIdType(sCmfMeta.getIdType());
            cmfMeta.setIdentifier(sCmfMeta.getIdentifier());
            cmfMeta.setRelation(sCmfMeta.getRelation());
            cmfMeta.setPrice(sCmfMeta.getPrice());

            cmfMeta.setPaperPrice(sCmfMeta.getPaperPrice());
            cmfMeta.setForeignPrice(sCmfMeta.getForeignPrice());
            cmfMeta.setForeignPriceType(sCmfMeta.getForeignPriceType());
            cmfMeta.setEditionOrder(sCmfMeta.getEditionOrder());
            cmfMeta.setEditionNote(sCmfMeta.getEditionNote());

            cmfMeta.setPressOrder(sCmfMeta.getPressOrder());
            cmfMeta.setContentNum(sCmfMeta.getContentNum());
            cmfMeta.setClassCode(sCmfMeta.getClassCode());
            cmfMeta.setLanguage(sCmfMeta.getLanguage());
            cmfMeta.setApabiClass(sCmfMeta.getApabiClass());

            cmfMeta.setIllustration(sCmfMeta.getIllustration());
            cmfMeta.setReditor(sCmfMeta.getReditor());
            cmfMeta.setNotes(sCmfMeta.getNotes());
            cmfMeta.setRealISBN(sCmfMeta.getRealISBN());
            cmfMeta.setPlace(sCmfMeta.getPlace());

            return cmfMeta;
        }
        return null;
    }

    //生成流式服务元数据
    private BookMeta createBookMeta(SCmfMeta sCmfMeta) {
        if (sCmfMeta != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            BookMeta bookMeta = new BookMeta();
            bookMeta.setMetaId(sCmfMeta.getIdentifier());
            bookMeta.setDrId(sCmfMeta.getDrid());
            bookMeta.setTitle(sCmfMeta.getTitle());
            bookMeta.setAlternativeTitle(sCmfMeta.getAlternativeTitle());
            bookMeta.setCreator(sCmfMeta.getCreator());
            bookMeta.setSubject(sCmfMeta.getSubject());
            bookMeta.setAbstract_(sCmfMeta.getAbstract());
            bookMeta.setPublisher(sCmfMeta.getPublisher());
            bookMeta.setType(sCmfMeta.getType());
            bookMeta.setContributor(sCmfMeta.getContributor());
            bookMeta.setIssuedDate(sdf.format(sCmfMeta.getIssuedDate()));
            bookMeta.setIdType(sCmfMeta.getIdType());
            bookMeta.setRelation(sCmfMeta.getRelation());
            bookMeta.setEbookPrice(String.valueOf(sCmfMeta.getPrice()));
            bookMeta.setPaperPrice(String.valueOf(sCmfMeta.getPaperPrice()));
            bookMeta.setForeignPrice(String.valueOf(sCmfMeta.getForeignPrice()));
            bookMeta.setForeignPriceType(sCmfMeta.getForeignPriceType());
            bookMeta.setEditionOrder(sCmfMeta.getEditionOrder());
            bookMeta.setEditionNote(sCmfMeta.getEditionNote());
            bookMeta.setPressOrder(sCmfMeta.getPressOrder());
            bookMeta.setContentNum(sCmfMeta.getContentNum());
            bookMeta.setClassCode(sCmfMeta.getClassCode());
            bookMeta.setLanguage(sCmfMeta.getLanguage());
            bookMeta.setApabiClass(sCmfMeta.getApabiClass());
            bookMeta.setIllustration(sCmfMeta.getIllustration());
            bookMeta.setReditor(sCmfMeta.getReditor());
            bookMeta.setNotes(sCmfMeta.getNotes());
            bookMeta.setIsbn(sCmfMeta.getRealISBN());
            bookMeta.setPlace(sCmfMeta.getPlace());
            return bookMeta;
        }
        return null;
    }

    //生成流式服务元数据，temp表
    private ApabiBookMetaDataTemp createBookMetaTemp(SCmfMeta sCmfMeta) {
        if (sCmfMeta != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ApabiBookMetaDataTemp metaDataTemp = new ApabiBookMetaDataTemp();
            metaDataTemp.setMetaId(sCmfMeta.getIdentifier());
            //metaDataTemp.setDrId(SCmfMeta.getDrid());
            metaDataTemp.setTitle(sCmfMeta.getTitle());
            metaDataTemp.setAlternativeTitle(sCmfMeta.getAlternativeTitle());
            metaDataTemp.setCreator(sCmfMeta.getCreator());
            metaDataTemp.setSubject(sCmfMeta.getSubject());
            metaDataTemp.setAbstract_(sCmfMeta.getAbstract());
            metaDataTemp.setPublisher(sCmfMeta.getPublisher());
            metaDataTemp.setType(sCmfMeta.getType());
            metaDataTemp.setContributor(sCmfMeta.getContributor());
            metaDataTemp.setIssuedDate(sdf.format(sCmfMeta.getIssuedDate()));
            metaDataTemp.setIdType(sCmfMeta.getIdType());
            metaDataTemp.setRelation(sCmfMeta.getRelation());
            metaDataTemp.setEbookPrice(String.valueOf(sCmfMeta.getPrice()));
            metaDataTemp.setPaperPrice(String.valueOf(sCmfMeta.getPaperPrice()));
            metaDataTemp.setForeignPrice(String.valueOf(sCmfMeta.getForeignPrice()));
            metaDataTemp.setForeignPriceType(sCmfMeta.getForeignPriceType());
            metaDataTemp.setEditionOrder(sCmfMeta.getEditionOrder());
            metaDataTemp.setEditionNote(sCmfMeta.getEditionNote());
            metaDataTemp.setPressOrder(sCmfMeta.getPressOrder());
            metaDataTemp.setContentNum(sCmfMeta.getContentNum());
            metaDataTemp.setClassCode(sCmfMeta.getClassCode());
            metaDataTemp.setLanguage(sCmfMeta.getLanguage());
            metaDataTemp.setApabiClass(sCmfMeta.getApabiClass());
            metaDataTemp.setIllustration(sCmfMeta.getIllustration());
            metaDataTemp.setReditor(sCmfMeta.getReditor());
            metaDataTemp.setNotes(sCmfMeta.getNotes());
            metaDataTemp.setIsbn(sCmfMeta.getRealISBN());
            metaDataTemp.setPlace(sCmfMeta.getPlace());
            return metaDataTemp;
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
