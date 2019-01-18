package com.apabi.flow.processing.service.impl;

import com.apabi.flow.book.dao.BookMetaDao;
import com.apabi.flow.book.model.BookMeta;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.config.ApplicationConfig;
import com.apabi.flow.douban.dao.ApabiBookMetaDataTempDao;
import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import com.apabi.flow.douban.service.DoubanMetaService;
import com.apabi.flow.douban.util.StringToolUtil;
import com.apabi.flow.processing.constant.*;
import com.apabi.flow.processing.dao.BatchMapper;
import com.apabi.flow.processing.dao.BibliothecaMapper;
import com.apabi.flow.processing.dao.OutUnitMapper;
import com.apabi.flow.processing.model.Batch;
import com.apabi.flow.processing.model.Bibliotheca;
import com.apabi.flow.processing.model.BibliothecaExcelModel;
import com.apabi.flow.processing.model.DuplicationCheckEntity;
import com.apabi.flow.processing.service.BibliothecaService;
import com.apabi.flow.publisher.dao.PublisherDao;
import com.apabi.flow.publisher.model.Publisher;
import com.apabi.maker.MakerAgent;
import com.apabi.maker.MakerUtil;
import com.github.pagehelper.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 功能描述： <br>
 * <书目 实现类>
 *
 * @author supeng
 * @date 2018/9/13 10:58
 * @since 1.0.0
 */
@Service
public class BibliothecaServiceImpl implements BibliothecaService {

    private static final Logger logger = LoggerFactory.getLogger(BibliothecaServiceImpl.class);

    private static boolean MAKER_INIT = true;

    @Autowired
    private BibliothecaMapper bibliothecaMapper;

    @Autowired
    private BookMetaDao bookMetaDao;

    @Autowired
    private PublisherDao publisherDao;

    //@Autowired
    //private ApabiBookMetaTempPublishDao apabiBookMetaTempPublishDao;
    @Autowired
    private ApabiBookMetaDataTempDao apabiBookMetaDataTempDao;

    @Autowired
    private BatchMapper batchMapper;

    @Autowired
    private OutUnitMapper outUnitMapper;

    @Autowired
    private DoubanMetaService doubanMetaService;

    @Autowired
    ApplicationConfig config;

    /**
     * 根据 书目 id 逻辑删除当前批次
     * 暂时不提供逻辑删除的功能
     *
     * @param id
     * @return
     */
    @Override
    public boolean deleteBibliothecaById(String id) {
        return bibliothecaMapper.deleteByPrimaryKey(id) == 1;
    }

    /**
     * 添加书目，需要做数据校验，比如重复添加等，条件一般为 batchId + isbn，在事务中循环插入
     *
     * @param bibliothecaList
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean addBibliothecaList(List<Bibliotheca> bibliothecaList) throws Exception {
        if (bibliothecaList == null) {
            return false;
        }

        int size = 0;
        for (Bibliotheca bibliotheca : bibliothecaList) {
            try {
                String publishTime = bibliotheca.getPublishTime();
                if (StringUtils.isNotBlank(publishTime)) {
                    publishTime = StringToolUtil.issuedDateFormat(publishTime);
                    publishTime = publishTime.replaceAll(" 00:00:00", "");
                    if (StringUtils.isBlank(publishTime)) {
                        throw new Exception("出版日期格式错误！");
                    }
                    bibliotheca.setPublishTime(publishTime);
                }
                bibliotheca.setId(UUIDCreater.nextId());
                bibliotheca.setCreateTime(new Date());
                bibliotheca.setUpdateTime(new Date());
//                bibliotheca.setBibliothecaState(bibliotheca.getBibliothecaState());
                bibliotheca.setDeleteFlag(DeleteFlagEnum.NORMAL);
                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (userDetails != null) {
                    bibliotheca.setCreator(userDetails.getUsername());
                }
                size += bibliothecaMapper.insertSelective(bibliotheca);
            } catch (DuplicateKeyException e) {
                throw new Exception("identifier : " + bibliotheca.getIdentifier() + " 已存在！", e);
            } catch (DataAccessException e) {
                throw new Exception("操作失败！", e);
            }
        }
        if (size == bibliothecaList.size()) {
            return true;
        } else {
            throw new Exception("数据条目不一致！");
        }
    }

    /**
     * 根据主键获取书目实体信息
     *
     * @param id
     * @return
     */
    @Override
    public Bibliotheca getBibliothecaById(String id) {
        return bibliothecaMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询所有特定条件下书目的列表信息
     *
     * @param map
     * @return
     */
    @Override
    public List<Bibliotheca> listBibliotheca(Map map) {
        return bibliothecaMapper.listBibliothecaSelective(map);
    }

    /**
     * 获取分页书目列表信息
     *
     * @param map
     * @return
     */
    @Override
    public Page<Bibliotheca> listBibliothecaPage(Map map) {
        return bibliothecaMapper.listBibliothecaSelectiveByPage(map);
    }

    /**
     * 更新书目信息
     *
     * @param bibliotheca
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public int updateBibliotheca(Bibliotheca bibliotheca) throws Exception {
        try {
            String publishTime = bibliotheca.getPublishTime();
            if (StringUtils.isNotBlank(publishTime)) {
                publishTime = StringToolUtil.issuedDateFormat(publishTime);
                publishTime = publishTime.replaceAll(" 00:00:00", "");
                if (StringUtils.isBlank(publishTime)) {
                    return -1;
                }
                bibliotheca.setPublishTime(publishTime);
            }
            bibliotheca.setUpdateTime(new Date());
            return bibliothecaMapper.updateByPrimaryKeySelective(bibliotheca);
        } catch (DataAccessException e) {
            throw new Exception("identifier : " + bibliotheca.getIdentifier() + " 已存在！", e);
        } catch (Exception e) {
            throw new Exception("操作失败！", e);
        }
    }

    /**
     * 对书目信息进行批处理
     *
     * @param bibliothecaList
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void listUpdateBibliotheca(List<Bibliotheca> bibliothecaList) throws Exception {

        int size = 0;

        Bibliotheca b = null;
        try {
            for (Bibliotheca bibliotheca : bibliothecaList) {
                b = bibliotheca;
                size += bibliothecaMapper.updateByPrimaryKeySelective(bibliotheca);
            }
        } catch (DuplicateKeyException e) {
            throw new Exception("identifier : " + b.getIdentifier() + " 已存在！", e);
        } catch (DataAccessException e) {
            throw new Exception("数据更新出现异常，请重新尝试或联系管理员！");
        } catch (Exception e) {
            throw new Exception("服务器异常，请重新尝试或联系管理员！");
        }

        if (size != bibliothecaList.size()) {
            throw new Exception("数据更新不一致，请重新尝试或联系管理员！");
        }
    }

    /**
     * 获取对比书目重复的列表
     *
     * @param batchId 批次ID
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public List<DuplicationCheckEntity> listDuplicationCheckEntity(String batchId) throws Exception {

        try {
            //1. 查询该批次下的所有书目
            Map paramMap = new HashMap();
            List<Bibliotheca> bibliothecaList = new ArrayList<>();
            paramMap.put("bibliothecaState", BibliothecaStateEnum.NEW);
            paramMap.put("batchId", batchId);
            List<Bibliotheca> bibliothecaList1 = bibliothecaMapper.listBibliothecaSelective(paramMap);
            bibliothecaList.addAll(bibliothecaList1);
            paramMap.put("bibliothecaState", BibliothecaStateEnum.REPEAT);
            paramMap.put("batchId", batchId);
            List<Bibliotheca> bibliothecaList2 = bibliothecaMapper.listBibliothecaSelective(paramMap);
            bibliothecaList.addAll(bibliothecaList2);
            paramMap.put("bibliothecaState", BibliothecaStateEnum.NOREPEAT);
            paramMap.put("batchId", batchId);
            List<Bibliotheca> bibliothecaList3 = bibliothecaMapper.listBibliothecaSelective(paramMap);
            bibliothecaList.addAll(bibliothecaList3);
            if (bibliothecaList == null || bibliothecaList.isEmpty()) {
                return null;
            }

            List<DuplicationCheckEntity> duplicationCheckEntities = new ArrayList<>();

            //循环
            for (Bibliotheca bibliotheca : bibliothecaList) {
                //2. 数据为空时，则插入空项
                if (StringUtils.isBlank(bibliotheca.getIsbn())) {
                    //NO_DATA
                    duplicationCheckEntities.add(genDuplicationCheckEntity(null, bibliotheca, null, DuplicationCheckEntity.RateFlag.NO_DATA,
                            DuplicationCheckEntity.CheckFlag.EMPTY));
                    continue;
                }
                //根据所有书目进行筛选去掉本身
                Map map = new HashMap();
                map.put("isbn", bibliotheca.getIsbn());
                List<Bibliotheca> bibliothecas = bibliothecaMapper.listBibliothecaSelective(map);
                Iterator<Bibliotheca> iterator = bibliothecas.iterator();
                while (iterator.hasNext()) {
                    Bibliotheca b = iterator.next();
                    if (b.getBatchId().equalsIgnoreCase(batchId))
                        iterator.remove();   //注意这个地方
                }

                //3. 根据 isbn 或 isbn13 查询 bookMeta 信息
                List<BookMeta> bookMetas = bookMetaDao.listBookMetaByIsbn(bibliotheca.getIsbn());
                if (bookMetas == null || bookMetas.isEmpty()) {
                    String isbn13 = bibliotheca.getIsbn().replaceAll("-", "");
                    //换 isbn13 来查
                    bookMetas = bookMetaDao.listBookMetaByIsbn13(isbn13);
                    if (bookMetas == null || bookMetas.isEmpty() && bibliothecas.size() == 0) {
                        //NO_DATA
                        duplicationCheckEntities.add(genDuplicationCheckEntity(null, bibliotheca, null, DuplicationCheckEntity.RateFlag.NO_DATA,
                                DuplicationCheckEntity.CheckFlag.EMPTY));
                        continue;
                    } else if (bookMetas == null || bookMetas.isEmpty() && bibliothecas.size() > 0) {
                        for (Bibliotheca b : bibliothecas) {
                            duplicationCheckEntities.add(genDuplicationCheckEntity(b, bibliotheca, null, DuplicationCheckEntity.RateFlag.NO_DATA,
                                    DuplicationCheckEntity.CheckFlag.EMPTY));
                        }
                    }
                }

                //4. 查到 bookMeta 数据，然后进行重叠率查询匹配
                for (BookMeta bookMeta : bookMetas) {
                    //5. 这其中有一个为空，则不可能达到 95% 以上的重叠率，故为空时，则添加 空的 查重对象
                    if (StringUtils.isBlank(bibliotheca.getTitle())
                            || StringUtils.isBlank(bibliotheca.getAuthor())
                            || StringUtils.isBlank(bibliotheca.getPublisher())
                            || StringUtils.isBlank(bookMeta.getTitle())
                            || StringUtils.isBlank(bookMeta.getCreator())
                            || StringUtils.isBlank(bookMeta.getPublisher())) {
                        //LT RATE
                        if (bookMeta.getHasCebx() == 1) {
                            //has CEBX
                            if (bibliothecas.size() == 0) {
                                duplicationCheckEntities.add(genDuplicationCheckEntity(null, bibliotheca, bookMeta, DuplicationCheckEntity.RateFlag.DUPLICATE_RATE_LT_FLAG,
                                        DuplicationCheckEntity.CheckFlag.DUPLICATE_YES));
                            } else {
                                for (Bibliotheca b : bibliothecas) {
                                    duplicationCheckEntities.add(genDuplicationCheckEntity(b, bibliotheca, bookMeta, DuplicationCheckEntity.RateFlag.DUPLICATE_RATE_LT_FLAG,
                                            DuplicationCheckEntity.CheckFlag.DUPLICATE_YES));
                                }
                            }


                        } else {
                            //no CEBX
                            if (bibliothecas.size() == 0) {
                                duplicationCheckEntities.add(genDuplicationCheckEntity(null, bibliotheca, bookMeta, DuplicationCheckEntity.RateFlag.DUPLICATE_RATE_LT_FLAG,
                                        DuplicationCheckEntity.CheckFlag.DUPLICATE_NO));
                            } else {
                                for (Bibliotheca b : bibliothecas) {
                                    duplicationCheckEntities.add(genDuplicationCheckEntity(b, bibliotheca, bookMeta, DuplicationCheckEntity.RateFlag.DUPLICATE_RATE_LT_FLAG,
                                            DuplicationCheckEntity.CheckFlag.DUPLICATE_NO));
                                }
                            }

                        }
                        continue;
                    }

                    //6. 重叠率计算
                    double titleRate = overlapRate(bibliotheca.getTitle(), bookMeta.getTitle());
                    double authorRate = overlapRate(bibliotheca.getAuthor(), bookMeta.getCreator());
                    double publisherRate = overlapRate(bibliotheca.getPublisher(), bookMeta.getPublisher());

                    double resultRate = titleRate * 0.5d + authorRate * 0.4d + publisherRate * 0.1d;

                    //7. 当重叠率未达到 0.95，则添加 小于标识
                    if (resultRate < DuplicationCheckEntity.RateFlag.RATE) {
                        //LT RATE
                        if (bookMeta.getHasCebx() == 1) {
                            //has CEBX
                            if (bibliothecas.size() == 0) {
                                duplicationCheckEntities.add(genDuplicationCheckEntity(null, bibliotheca, bookMeta, DuplicationCheckEntity.RateFlag.DUPLICATE_RATE_LT_FLAG,
                                        DuplicationCheckEntity.CheckFlag.DUPLICATE_YES));
                            } else {
                                for (Bibliotheca b : bibliothecas) {
                                    duplicationCheckEntities.add(genDuplicationCheckEntity(b, bibliotheca, bookMeta, DuplicationCheckEntity.RateFlag.DUPLICATE_RATE_LT_FLAG,
                                            DuplicationCheckEntity.CheckFlag.DUPLICATE_YES));
                                }
                            }

                        } else {
                            //no CEBX
                            if (bibliothecas.size() == 0) {

                                duplicationCheckEntities.add(genDuplicationCheckEntity(null, bibliotheca, bookMeta, DuplicationCheckEntity.RateFlag.DUPLICATE_RATE_LT_FLAG,
                                        DuplicationCheckEntity.CheckFlag.DUPLICATE_NO));
                            } else {
                                for (Bibliotheca b : bibliothecas) {
                                    duplicationCheckEntities.add(genDuplicationCheckEntity(b, bibliotheca, bookMeta, DuplicationCheckEntity.RateFlag.DUPLICATE_RATE_LT_FLAG,
                                            DuplicationCheckEntity.CheckFlag.DUPLICATE_NO));
                                }
                            }

                        }
                        continue;
                    }

                    //8. 达到0.95，则需要匹配 cebx
                    //GT EQ RATE
                    if (bookMeta.getHasCebx() == 1) {
                        //has CEBX
                        if (bibliothecas.size() == 0) {
                            duplicationCheckEntities.add(genDuplicationCheckEntity(null, bibliotheca, bookMeta, DuplicationCheckEntity.RateFlag.DUPLICATE_RATE_GT_EQ_FLAG,
                                    DuplicationCheckEntity.CheckFlag.DUPLICATE_YES));
                        } else {
                            for (Bibliotheca b : bibliothecas) {
                                duplicationCheckEntities.add(genDuplicationCheckEntity(b, bibliotheca, bookMeta, DuplicationCheckEntity.RateFlag.DUPLICATE_RATE_GT_EQ_FLAG,
                                        DuplicationCheckEntity.CheckFlag.DUPLICATE_YES));
                            }
                        }

                    } else {
                        //no CEBX
                        if (bibliothecas.size() == 0) {
                            duplicationCheckEntities.add(genDuplicationCheckEntity(null, bibliotheca, bookMeta, DuplicationCheckEntity.RateFlag.DUPLICATE_RATE_GT_EQ_FLAG,
                                    DuplicationCheckEntity.CheckFlag.DUPLICATE_NO));
                        } else {
                            for (Bibliotheca b : bibliothecas) {
                                duplicationCheckEntities.add(genDuplicationCheckEntity(b, bibliotheca, bookMeta, DuplicationCheckEntity.RateFlag.DUPLICATE_RATE_GT_EQ_FLAG,
                                        DuplicationCheckEntity.CheckFlag.DUPLICATE_NO));
                            }
                        }

                    }
                /*DuplicationCheckEntity dce = new DuplicationCheckEntity();
                dce.setBibliotheca(bibliotheca);
                dce.setBookMeta(bookMeta);

                //9. 有 cebx，则重复
                if (bookMeta.getHasCebx() == 1){
                    dce.setFlag(DuplicationCheckEntity.CheckFlag.DUPLICATE_YES);
                    duplicationCheckEntities.add(dce);
                    continue;
                }
                //10. 没有cebx 值 或 为0，则说明不重复，故添加 不重复对象
                dce.setFlag(DuplicationCheckEntity.CheckFlag.DUPLICATE_NO);
                duplicationCheckEntities.add(dce);*/
                }
            }
            return duplicationCheckEntities;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("操作出现异常！");
        }


    }

    private DuplicationCheckEntity genDuplicationCheckEntity(Bibliotheca bibliotheca2, Bibliotheca bibliotheca, BookMeta bookMeta, int rateFlag, int checkFlag) {
        DuplicationCheckEntity dce = new DuplicationCheckEntity();
        dce.setBibliotheca(bibliotheca);
        dce.setBookMeta(bookMeta);
        dce.setRateFlag(rateFlag);
        dce.setFlag(checkFlag);
        dce.setBibliotheca2(bibliotheca2);
        return dce;
    }

    /**
     * 查重信息批量处理
     *
     * @param bibliothecaIdList
     * @param metaIdList
     * @param bibliothecaState
     * @param duplicateFlag
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void listSureOperation(List<String> bibliothecaIdList, List<String> metaIdList, String dataType, String btnType,
                                  BibliothecaStateEnum bibliothecaState, DuplicateFlagEnum duplicateFlag) throws Exception {

        try {
            int sum = 0;

            //当数据为 noMatch 和 按钮为 make 类型时，需要做 元数据 插入 TEMP 库操作
            if ("noMatch".equals(dataType) && "make".equals(btnType)) {
                for (String id : bibliothecaIdList) {
                    //ApabiBookMetaTempPublish2 apabiBookMetaTempPublish2 = new ApabiBookMetaTempPublish2();
                    ApabiBookMetaDataTemp apabiBookMetaDataTemp = new ApabiBookMetaDataTemp();
                    Bibliotheca bibliotheca = bibliothecaMapper.selectByPrimaryKey(id);

                    String issuedDate = StringToolUtil.issuedDateFormat(bibliotheca.getPublishTime());

                    if (StringUtils.isBlank(issuedDate)) {
                        throw new Exception("请检查出版日期是否完整！");
                    }
                    String metaId = StringToolUtil.metaidFormat(issuedDate);
                    if (StringUtils.isBlank(metaId)) {
                        throw new Exception("请检查数据出版日期是否存在或符合 2018-10-01 格式");
                    }
                    // 元数据库中，不用修改电子书价格
                    apabiBookMetaDataTemp.setMetaId(metaId);
                    apabiBookMetaDataTemp.setTitle(bibliotheca.getTitle());
                    apabiBookMetaDataTemp.setCreator(bibliotheca.getAuthor());
                    apabiBookMetaDataTemp.setPublisher(bibliotheca.getPublisher());
                    apabiBookMetaDataTemp.setIsbn(bibliotheca.getIsbn());
                    apabiBookMetaDataTemp.setIssuedDate(issuedDate);
                    apabiBookMetaDataTemp.setEditionOrder(bibliotheca.getEdition());
                    apabiBookMetaDataTemp.setPaperPrice(bibliotheca.getPaperPrice());
                    apabiBookMetaDataTemp.setCreateTime(new Date());
                    apabiBookMetaDataTemp.setUpdateTime(new Date());
                    apabiBookMetaDataTempDao.insert(apabiBookMetaDataTemp);

                    //apabiBookMetaTempPublish2.setMetaId(metaId);
                    //apabiBookMetaTempPublish2.setTitle(bibliotheca.getTitle());
                    //apabiBookMetaTempPublish2.setCreator(bibliotheca.getAuthor());
                    //apabiBookMetaTempPublish2.setPublisher(bibliotheca.getPublisher());
                    //apabiBookMetaTempPublish2.setIsbn(bibliotheca.getIsbn());
                    //apabiBookMetaTempPublish2.setIssuedDate(issuedDate);
                    //apabiBookMetaTempPublish2.setEditionOrder(bibliotheca.getEdition());
                    //apabiBookMetaTempPublish2.setPaperPrice(bibliotheca.getPaperPrice());
                    //apabiBookMetaTempPublish2.setCreateTime(new Date());
                    //apabiBookMetaTempPublish2.setUpdateTime(new Date());
                    //apabiBookMetaTempPublishDao.saveAndFlush(apabiBookMetaTempPublish2);

                    Bibliotheca bibliotheca1 = new Bibliotheca();
                    bibliotheca1.setId(bibliotheca.getId());
                    // metaId 回填
                    bibliotheca1.setMetaId(metaId);
                    bibliotheca1.setUpdateTime(new Date());
                    try {
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        bibliotheca1.seteBookPrice(decimalFormat.format(Double.valueOf(bibliotheca.getPaperPrice()) * (1 / 3.0d)));
                    } catch (Exception e) {
                        System.out.println("价格计算错误 : 纸书价格=" + bibliotheca.getPaperPrice());
                    }
                    //区分 确认重复和确认制作的区别
                    /*if ("duplicate".equals(btnType)){
                        bibliotheca1.setDuplicateFlag(DuplicateFlagEnum.YES);
                        bibliotheca1.setBibliothecaState(BibliothecaStateEnum.DUPLICATE_CHECKED);
                    }else {}
                    */
                    bibliotheca1.setDuplicateFlag(duplicateFlag);
                    bibliotheca1.setBibliothecaState(bibliothecaState);

                    bibliothecaMapper.updateByPrimaryKeySelective(bibliotheca1);
                    sum++;
                    production(bibliotheca);

                }
            } else if ("noMatch".equals(dataType) && "duplicate".equals(btnType)) {
                // 数据为 noMatch 和 操作类型为 duplicate 类型时，只需要进行查询和更新即可
                for (int i = 0; i < bibliothecaIdList.size(); i++) {

                    Bibliotheca bibliotheca = bibliothecaMapper.selectByPrimaryKey(bibliothecaIdList.get(i));

                    Bibliotheca bibliotheca1 = new Bibliotheca();
                    bibliotheca1.setUpdateTime(new Date());
                    bibliotheca1.setId(bibliothecaIdList.get(i));

                    bibliotheca1.setBibliothecaState(bibliothecaState);
                    bibliotheca1.setDuplicateFlag(duplicateFlag);
//                    bibliotheca1.setMetaId(bookMeta.getMetaId());
                    try {
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        bibliotheca1.seteBookPrice(decimalFormat.format(Double.valueOf(bibliotheca.getPaperPrice()) * (1 / 3.0d)));
                    } catch (Exception e) {
                        System.out.println("价格计算错误 : 纸书价格=" + bibliotheca.getPaperPrice());
                    }

                    sum += bibliothecaMapper.updateByPrimaryKeySelective(bibliotheca1);
                    production(bibliotheca);
                }
            } else {
                // 其它类型，只需要进行查询和更新即可
                for (int i = 0; i < bibliothecaIdList.size(); i++) {
                    BookMeta bookMeta = bookMetaDao.findBookMetaById(metaIdList.get(i));
                    if (bookMeta == null) {
                        throw new Exception("元数据异常，请检查数据并重新尝试或联系管理员！");
                    }

                    Bibliotheca bibliotheca = bibliothecaMapper.selectByPrimaryKey(bibliothecaIdList.get(i));

                    Bibliotheca bibliotheca1 = new Bibliotheca();
                    bibliotheca1.setUpdateTime(new Date());
                    bibliotheca1.setId(bibliothecaIdList.get(i));

                    bibliotheca1.setBibliothecaState(bibliothecaState);
                    bibliotheca1.setDuplicateFlag(duplicateFlag);
                    bibliotheca1.setMetaId(bookMeta.getMetaId());

                    try {
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        bibliotheca1.seteBookPrice(decimalFormat.format(Double.valueOf(bibliotheca.getPaperPrice()) * (1 / 3.0d)));
                    } catch (Exception e) {
                        System.out.println("价格计算错误 : 纸书价格=" + bibliotheca.getPaperPrice());
                    }

                    sum += bibliothecaMapper.updateByPrimaryKeySelective(bibliotheca1);
                    production(bibliotheca);
                }
            }


            if (sum != bibliothecaIdList.size()) {
                throw new Exception("数据更新异常，请检查数据并重新尝试或联系管理员！");
            }
        } catch (Exception e) {
            throw new Exception("服务器异常，请重新尝试或联系管理员");
        }
    }

    public void production(Bibliotheca bibliotheca) {
        //判断是否是待排产
        Map map = new HashMap();
        map.put("batchId", bibliotheca.getBatchId());
        List<Bibliotheca> bibliothecas = bibliothecaMapper.listBibliothecaSelective(map);
        int num = 0;
        for (Bibliotheca bibliotheca2 : bibliothecas) {
            if (bibliotheca2.getBibliothecaState().getCode() != 0) {
                num++;
            }
        }
        if (num == bibliothecas.size()) {
            Batch batch = batchMapper.selectByBatchId(bibliotheca.getBatchId());
            batch.setBatchState(BatchStateEnum.WAITING_PRODUCTION);
            batch.setUpdateTime(new Date());
            batchMapper.updateByPrimaryKey(batch);
        }
    }

    /**
     * 解析文件，得到 书目列表
     *
     * @param data
     * @param batchId
     * @return
     */
    @Override
    public List<Bibliotheca> resolveBibliothecaData(Map<Integer, Map<Object, Object>> data, String batchId) throws Exception {
        List<Bibliotheca> list = new ArrayList<>(data.size());

        List<Publisher> publishers = publisherDao.findAll();
        if (publishers == null) {
            throw new BizException("出版社校验出错，请重新尝试或联系管理员！");
        }

        try {
            for (Map.Entry<Integer, Map<Object, Object>> entry : data.entrySet()) {
                Bibliotheca bibliotheca = new Bibliotheca();
                String identifier = (String) entry.getValue().get("编号");
                String title = (String) entry.getValue().get("标题");
                String originalFilename = (String) entry.getValue().get("原文件名");

                if (StringUtils.isBlank(identifier)) {
                    throw new BizException("编号不能为空，请检查数据重新导入！");
                }
                if (StringUtils.isBlank(title)) {
                    throw new BizException("标题不能为空，请检查数据重新导入！");
                }
                if (StringUtils.isBlank(originalFilename)) {
                    throw new BizException("原文件名不能为空，请检查数据重新导入！");
                }

                String author = (String) entry.getValue().get("作者");
                String publisher = (String) entry.getValue().get("出版社");

                // 书目信息中存储的是 出版社id
                if (StringUtils.isNotBlank(publisher) && publishers != null) {
                    bibliotheca.setPublisherName(publisher);
                    List<String> idList =
                            publishers.stream()
                                    .filter(p -> publisher.equals(p.getTitle()))
                                    .map(p -> p.getId())
                                    .collect(Collectors.toCollection(ArrayList::new));
                    if (idList != null && idList.size() == 1) {
                        bibliotheca.setPublisher(idList.get(0));

                    }
                }
                String isbn = (String) entry.getValue().get("ISBN");
                String publishTime = (String) entry.getValue().get("出版时间");
                if (StringUtils.isNotBlank(publishTime)) {
                    publishTime = StringToolUtil.issuedDateFormat(publishTime);
                    publishTime = publishTime.replaceAll(" 00:00:00", "");
                    if (StringUtils.isBlank(publishTime)) {
                        throw new BizException("出版日期格式错误，请检查数据重新导入！");
                    }
                }
                String edition = (String) entry.getValue().get("版次");
                String paperPrice = (String) entry.getValue().get("纸书价格");
                String documentFormat = (String) entry.getValue().get("文档格式");

                bibliotheca.setIdentifier(identifier);
                bibliotheca.setBatchId(batchId);
                bibliotheca.setTitle(title);
                bibliotheca.setOriginalFilename(originalFilename);

                bibliotheca.setAuthor(null2EmptyString(author));
                bibliotheca.setIsbn(null2EmptyString(isbn));
                bibliotheca.setPublishTime(null2EmptyString(publishTime));
                bibliotheca.setEdition(null2EmptyString(edition));
                bibliotheca.setPaperPrice(null2EmptyString(paperPrice));
                bibliotheca.setDocumentFormat(null2EmptyString(documentFormat));
                if(StringUtils.isNotBlank(publisher)&&StringUtils.isNotBlank(author)&&StringUtils.isNotBlank(documentFormat)&&StringUtils.isNotBlank(paperPrice)&&StringUtils.isNotBlank(edition)&&StringUtils.isNotBlank(isbn)&&StringUtils.isNotBlank(publishTime)){
                    bibliotheca.setBibliothecaState(BibliothecaStateEnum.NEW);
                }else {
                    bibliotheca.setBibliothecaState(BibliothecaStateEnum.INFORMATION_NO);
                }
                list.add(bibliotheca);
            }
        } catch (Exception e) {
            if (e instanceof BizException) {
                throw new Exception(e.getMessage());
            } else {
                throw new Exception("数据解析异常，请检查文件合适是否正确或联系管理员！");
            }
        }
        return list;
    }

    /**
     * 将 数据写入到 Excel 表中
     *
     * @param fileName
     * @param excelTitle
     * @param excelModelList
     * @param sheetName
     * @param response
     */
    @Override
    public String writeData2Excel(int type, String fileName, String[] excelTitle, List<BibliothecaExcelModel> excelModelList, String sheetName, HttpServletResponse response) throws Exception {
//        // 拿到模板文件
//        String path = System.getProperty("user.dir");
//        String filePath = path + "/src/main/resources/temp/temp.xls";
//        FileInputStream tps = new FileInputStream(new File(filePath));
//        final Workbook tpWorkbook = WorkbookFactory.create(tps);
        Workbook workbook = null;
        if (fileName.toLowerCase().endsWith("xls")) {//2003
            workbook = new XSSFWorkbook();
        } else if (fileName.toLowerCase().endsWith("xlsx")) {//2007
            workbook = new HSSFWorkbook();
        } else {
            throw new BizException("文件名后缀必须是 .xls 或 .xlsx");
        }
        ServletOutputStream out = null;
//        workbook=tpWorkbook;
        //create sheet
//        Sheet sheet =workbook.getSheet("Sheet1");
        Sheet sheet = workbook.createSheet(sheetName);
        //遍历数据集，将其写入excel中
        try {
            //写表头数据
            Row titleRow = sheet.createRow(0);
            for (int i = 0; i < excelTitle.length; i++) {
                //创建表头单元格,填值
                titleRow.createCell(i).setCellValue(excelTitle[i]);
            }
            if (type == 1) {
                autoCreateRow(sheet, excelModelList);
            } else if (type == 2) {
                autoCreateRow2(sheet, excelModelList);
            }
            // 设置响应头
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            return fileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new Exception("文件未找到！");
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("服务器异常！");
        } catch (Exception e) {
            throw new Exception("数据解析异常！");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 自动添加 excel 行数据
     *
     * @param sheet
     * @param excelModelList
     */
    private void autoCreateRow(Sheet sheet, List<BibliothecaExcelModel> excelModelList) throws Exception {

        List<String> list = null;
        // 遍历所有数据
        for (int i = 0; i < excelModelList.size(); i++) {
            BibliothecaExcelModel bem = excelModelList.get(i);
            list = Arrays.asList(bem.getIdentifier(), bem.getMetaId(), bem.getBatchId(), bem.getOriginalFilename(), bem.getTitle(),
                    bem.getAuthor(), bem.getPublisher(), bem.getIsbn(), bem.getPublishTime(), bem.getEdition(),
                    bem.getPaperPrice(), bem.geteBookPrice(), bem.getDocumentFormat(), bem.getMemo(), bem.getDuplicateFlag() == null ? "" : bem.getDuplicateFlag().getDesc(),
                    bem.getBibliothecaState() == null ? "" : bem.getBibliothecaState().getDesc(), bem.getCompletedFlag() == null ? "" : bem.getCompletedFlag().getDesc(),
                    bem.getCreator(), bem.getCreateTime() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(bem.getCreateTime()));
            Row row = sheet.createRow(i + 1);

            // 逐个单元格添加数据
            for (int j = 0; j < list.size(); j++) {
                row.createCell(j).setCellValue(list.get(j));
            }
        }
    }

    private void autoCreateRow2(Sheet sheet, List<BibliothecaExcelModel> excelModelList) throws Exception {

        List<String> list = null;
        // 遍历所有数据
        for (int i = 0; i < excelModelList.size(); i++) {
            BibliothecaExcelModel bem = excelModelList.get(i);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            list = Arrays.asList(String.valueOf(i + 1), bem.getIdentifier(), bem.getIsbn(), bem.getTitle(), bem.getAuthor(), bem.getPublisher(), bem.getPublishTime() == null ? "" : new SimpleDateFormat("yyyy/MM/dd").format(sdf.parse(StringToolUtil.issuedDateFormat(bem.getPublishTime()))),
                    bem.getPaperPrice(), bem.geteBookPrice(), "", "", bem.getMetaId(), bem.getBatchId());
            Row row = sheet.createRow(i + 1);

            // 逐个单元格添加数据
            for (int j = 0; j < list.size(); j++) {
                row.createCell(j).setCellValue(list.get(j));
            }
        }
    }

    /**
     * null to ""
     *
     * @param s
     * @return
     */
    private String null2EmptyString(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    /**
     * 计算 重叠率
     *
     * @param s1
     * @param s2
     * @return
     */
    private double overlapRate(String s1, String s2) {

        //去除特殊字符
        s1 = noSignCheckPattern(s1);
        s2 = noSignCheckPattern(s2);

        // str1 长度大于 str2
        String str1, str2;

        if (s1.length() > s2.length()) {
            str1 = s1;
            str2 = s2;
        } else {
            str1 = s2;
            str2 = s1;
        }

        int len = str2.length();
        int sum = 0;
        for (int i = 0; i < len; i++) {
            if (str1.charAt(i) == str2.charAt(i)) {
                sum++;
            } else {
                break;
            }
        }
        //计算重叠率，上下字符串相同索引匹配，不能跨下标
        DecimalFormat df = new DecimalFormat("0.000");
        double rate = Double.parseDouble(df.format((float) sum / str1.length()));
        return rate;
    }

    /**
     * 无特殊符号正则匹配提取
     *
     * @param str
     * @return
     */
    private String noSignCheckPattern(String str) {
        //只提取其中的字母，数字和汉字
        String regExp = "[a-zA-Z0-9\\u4e00-\\u9fa5]";

        Pattern p = Pattern.compile(regExp);
        Matcher matcher = p.matcher(str);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(matcher.group());
        }
        return sb.toString();
    }


    /**
     * 转换pdf文件为cebx
     * fileMap中的数组：0-文件名，1-出版日期，2-metaId
     */
    @Override
    @Async
    public void batchConvert2Cebx(String dirPath, String batchId, String fileInfos) {
        if (StringUtils.isNotBlank(dirPath)
                && StringUtils.isNotBlank(batchId)
                && StringUtils.isNotBlank(fileInfos)) {
            Map<String, String[]> fileMap = convertFile2Map(fileInfos);
            //生成job文件
            String jobPath = ApplicationConfig.getCebxMaker() + File.separator + "job";
            Map<String, String> jobMap = MakerUtil.createJobXml(dirPath, config.getUploadCebx(), jobPath, fileMap);
            if (jobMap != null && jobMap.size() > 0) {
                //初始化maker
                synchronized (this) {
                    if (MAKER_INIT) {
                        MakerAgent.init();
                        MAKER_INIT = false;
                    }
                }
                long startS = System.currentTimeMillis();
                logger.info("路径{}转换cebx已开始", dirPath);
                //更新转换状态
                Batch batch = new Batch();
                batch.setBatchId(batchId);
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
                            int res = MakerAgent.DoConvert(fileJob, 1000);
                            if (res == 0) {
                                bibliotheca.setConvertStatus(2);
                                bibliotheca.setUpdateTime(new Date());
                                bibliothecaMapper.updateByPrimaryKeySelective(bibliotheca);
                                long end = System.currentTimeMillis();
                                logger.info("文件{}转换cebx成功，耗时{}", fileInfo[0], (end - start));
                            } else {
                                bibliotheca.setConvertStatus(-1);
                                bibliotheca.setUpdateTime(new Date());
                                bibliothecaMapper.updateByPrimaryKeySelective(bibliotheca);
                                logger.info("文件{}转换cebx失败", fileInfo[0]);
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
                //更新转换状态
                batch.setConvertStatus(2);
                batch.setUpdateTime(new Date());
                batchMapper.updateByBatchId(batch);
                long endS = System.currentTimeMillis();
                logger.info("路径{}转换cebx已结束，耗时{}", dirPath, (endS - startS));
            }
        }
    }

    //将文件信息转换成指定的map格式
    private Map convertFile2Map(String fileInfos) {
        if (StringUtils.isNotBlank(fileInfos)) {
            Map<String, String[]> fileMap = new HashMap<>(16);
            String[] files = fileInfos.split(";");
            for (String file : files) {
                String[] fileAttr = new String[3];
                String[] fileInfo = file.split(",");
                fileMap.put(fileInfo[0], null);
                try {
                    fileAttr[0] = fileInfo[1];
                    fileAttr[1] = fileInfo[2].substring(0, 10);
                    fileAttr[2] = fileInfo[3];
                    fileMap.put(fileInfo[0], fileAttr);
                } catch (Exception e) {
                    logger.warn("生成文件{}的map信息时，出现异常{}", fileInfo[0], e.getMessage());
                }
            }
            return fileMap;
        }
        return null;
    }

    //书目解析
    @Async
    @Override
    public void parsing(String path, String id, String username, String batchId) throws InterruptedException {
        // 判断给定目录是否是一个合法的目录
        try {
            ArrayList<File> fileList = new ArrayList<>();
            ArrayList<File> files = getFiles(fileList, path);
            ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 200, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(files.size()));

            for (int i = 1; i <= files.size(); i++) {
                MyTask myTask = new MyTask(i, files.get(i - 1), username, batchId, publisherDao, bibliothecaMapper, config);
                executor.execute(myTask);
                logger.info("线程池中线程数目：" + executor.getPoolSize() + "，队列中等待执行的任务数目：" +
                        executor.getQueue().size() + "，已执行玩别的任务数目：" + executor.getCompletedTaskCount());
            }
            executor.shutdown();
            boolean a = true;
            while (a) {
                if (executor.isTerminated()) {
                    Batch batch = new Batch();
                    batch.setId(id);
                    //扫描完成
                    batch.setBatchState(BatchStateEnum.FINISH_SCANNING);
                    batch.setUpdateTime(new Date());
                    batchMapper.updateStateByPrimaryKey(batch);
                    logger.info("批次扫描完成------");
                    a = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    //递归获取里面的文件 排除隐藏文件
    private static ArrayList<File> getFiles(ArrayList<File> fileList, String path) {
        File[] allFiles = new File(path).listFiles();
        for (int i = 0; i < allFiles.length; i++) {
            File file = allFiles[i];
            if (file.isHidden()) {
            } else if (file.isFile()) {
                fileList.add(file);
            } else {
                getFiles(fileList, file.getAbsolutePath());
            }
        }
        return fileList;
    }
}
