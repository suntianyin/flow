package com.apabi.flow.processing.controller;

import com.apabi.flow.auth.constant.ResourceStatusEnum;
import com.apabi.flow.auth.dao.CopyrightOwnerMapper;
import com.apabi.flow.auth.model.CopyrightOwner;
import com.apabi.flow.auth.model.Resource;
import com.apabi.flow.auth.service.ResourceService;
import com.apabi.flow.book.model.BookMeta;
import com.apabi.flow.book.service.BookMetaService;
import com.apabi.flow.common.ResultEntity;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.common.util.ParamsUtils;
import com.apabi.flow.config.ApplicationConfig;
import com.apabi.flow.douban.util.StringToolUtil;
import com.apabi.flow.processing.constant.*;
import com.apabi.flow.processing.dao.BatchMapper;
import com.apabi.flow.processing.dao.OutUnitMapper;
import com.apabi.flow.processing.model.*;
import com.apabi.flow.processing.service.BatchService;
import com.apabi.flow.processing.service.BibliothecaService;
import com.apabi.flow.processing.util.IsbnCheck;
import com.apabi.flow.processing.util.ReadExcelTextUtils;
import com.apabi.flow.publisher.dao.PublisherDao;
import com.apabi.flow.publisher.model.Publisher;
import com.apabi.maker.MakerAgent;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能描述： <br>
 * <批次Controller>
 *
 * @author supeng
 * @date 2018/9/13 10:01
 * @since 1.0.0
 */
@Controller
@RequestMapping("/processing/bibliotheca")
public class BibliothecaController {

    private static final Logger logger = LoggerFactory.getLogger(BibliothecaController.class);

    @Autowired
    private BibliothecaService bibliothecaService;

//    @Autowired
//    private OutUnitMapper outUnitMapper;
//
//    @Autowired
//    private BookMetaService bookMetaService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private PublisherDao publisherDao;

    @Autowired
    private BatchMapper batchMapper;

    @Autowired
    private OutUnitMapper outUnitMapper;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ApplicationConfig config;

    @Autowired
    private BookMetaService bookMetaService;

    @Autowired
    CopyrightOwnerMapper copyrightOwnerMapper;

    /**
     * 外协 书目信息 页面
     *
     * @param batchId
     * @param title
     * @param publisher
     * @param bibliothecaState
     * @param pageNum
     * @param pageSize
     * @param model
     * @return
     */
    @RequestMapping("/outUnit/index")
    public String outUnitIndex(@RequestParam(value = "batchId", required = true) String batchId,
                               @RequestParam(value = "title", required = false) String title,
                               @RequestParam(value = "publisher", required = false) String publisher,
//                        @RequestParam(value = "duplicateFlag", required = false)DuplicateFlagEnum duplicateFlag,
                               @RequestParam(value = "bibliothecaState", required = false) BibliothecaStateEnum bibliothecaState,
                               @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                               Model model) {
        try {
            long start = System.currentTimeMillis();
            Map<String, Object> paramsMap = new HashMap<>();
            ParamsUtils.checkParameterAndPut2Map(paramsMap, "batchId", batchId, "title", title, "publisher", publisher);
//            paramsMap.put("duplicateFlag", duplicateFlag);
            paramsMap.put("bibliothecaState", bibliothecaState);

            List<Bibliotheca> list = bibliothecaService.listBibliotheca(paramsMap);
            Batch batch = batchService.selectByBatchId(batchId);
            if (batch.getSubmitTime() != null) {
                model.addAttribute("timeState", 11);

            }
            model.addAttribute("bibliothecaList", list);
            model.addAttribute("batchId", batchId);
            model.addAttribute("title", title);
            model.addAttribute("publisher", publisher);
//            model.addAttribute("duplicateFlag", duplicateFlag);
            model.addAttribute("bibliothecaState", bibliothecaState);
            //动态改变前端 书目状态 下拉列表信息展示
            model.addAttribute("bibliothecaStateList", Arrays.asList(BibliothecaStateEnum.values()));
            if (list != null) {
                model.addAttribute("total", list.size());
                long count1 = list.stream().filter(bibliotheca -> bibliotheca.getBibliothecaState().getCode() == 3).count();
                model.addAttribute("num1", count1);//已分拣
                long count2 = list.stream().filter(bibliotheca -> bibliotheca.getBibliothecaState().getCode() == 1).count();
                model.addAttribute("num2", count2);//重复
                long count3 = list.stream().filter(bibliotheca -> bibliotheca.getBibliothecaState().getCode() == 6).count();
                model.addAttribute("num3", count3);//制作成功
                long count4 = list.stream().filter(bibliotheca -> bibliotheca.getBibliothecaState().getCode() == 7).count();
                model.addAttribute("num4", count4);//制作失败
            }
            logger.info("书目查询耗时： {}", System.currentTimeMillis() - start);
        } catch (Exception e) {
            logger.error("Exception {}", e);
        }
        return "processing/outUnitBibliotheca";
    }

    /**
     * 管理员 书目信息 页面
     *
     * @param batchId
     * @param title
     * @param publisher
     * @param bibliothecaState
     * @param pageNum
     * @param pageSize
     * @param model
     * @return
     */
    @RequestMapping("/index")
    public String Index(@RequestParam(value = "batchId", required = true) String batchId,
                        @RequestParam(value = "title", required = false) String title,
                        @RequestParam(value = "publisher", required = false) String publisher,
//                               @RequestParam(value = "duplicateFlag", required = false)DuplicateFlagEnum duplicateFlag,
                        @RequestParam(value = "bibliothecaState", required = false) BibliothecaStateEnum bibliothecaState,
                        @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                        Model model) {
        try {
            long start = System.currentTimeMillis();
            Map<String, Object> paramsMap = new HashMap<>();
            ParamsUtils.checkParameterAndPut2Map(paramsMap, "batchId", batchId, "title", title, "publisher", publisher);
//            paramsMap.put("duplicateFlag", duplicateFlag);
            paramsMap.put("bibliothecaState", bibliothecaState);
            List<Bibliotheca> list = bibliothecaService.listBibliotheca(paramsMap);
            Batch batch = batchService.selectByBatchId(batchId);
            model.addAttribute("BatchStateEnum", batch.getBatchState().getCode());
            model.addAttribute("bibliothecaList", list);

            model.addAttribute("title", title);
            model.addAttribute("batchId", batchId);
            //资源路径
            model.addAttribute("resourcePath", batch.getResourcePath());
            model.addAttribute("publisher", publisher);
//            model.addAttribute("duplicateFlag", duplicateFlag);
            model.addAttribute("bibliothecaState", bibliothecaState);
            //动态改变前端 书目状态 下拉列表信息展示
            model.addAttribute("bibliothecaStateList", Arrays.asList(BibliothecaStateEnum.values()));
            if (list != null) {
                model.addAttribute("total", list.size());
                long count1 = list.stream().filter(bibliotheca -> bibliotheca.getBibliothecaState().getCode() == 3).count();
                model.addAttribute("num1", count1);//已分拣
                long count2 = list.stream().filter(bibliotheca -> bibliotheca.getBibliothecaState().getCode() == 1).count();
                model.addAttribute("num2", count2);//重复
                long count3 = list.stream().filter(bibliotheca -> bibliotheca.getBibliothecaState().getCode() == 6).count();
                model.addAttribute("num3", count3);//制作成功
                long count4 = list.stream().filter(bibliotheca -> bibliotheca.getBibliothecaState().getCode() == 7).count();
                model.addAttribute("num4", count4);//制作失败
            }
            logger.info("书目查询耗时： {}", System.currentTimeMillis() - start);
        } catch (Exception e) {
            logger.error("Exception {}", e);
        }
        return "processing/bibliotheca";
    }

    /**
     * 管理员 查重 页面
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/bibliothecaDuplication/index")
    public String bibliothecaDuplicationIndex(@RequestParam(value = "id") String id, Model model) {
        try {
            long start = System.currentTimeMillis();

            Batch batch = batchService.getBatchById(id);

            List<DuplicationCheckEntity> list = new ArrayList<>();

            if (batch != null && StringUtils.isNotBlank(batch.getBatchId())) {

                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (userDetails != null) {
                    //更新查重人
                    Batch paramBatch = new Batch();
                    paramBatch.setId(id);
                    paramBatch.setUpdateTime(new Date());
                    paramBatch.setChecker(userDetails.getUsername());
                    paramBatch.setCheckTime(new Date());
                    batchService.updateBatch(paramBatch);
                }
                //查重列表
                list = bibliothecaService.listDuplicationCheckEntity(batch.getBatchId());
            }

//            List<DuplicationCheckEntity> gtRateCheckFlagYesList = new ArrayList<>();
//            List<DuplicationCheckEntity> gtRateCheckFlagNoList = new ArrayList<>();
            List<DuplicationCheckEntity> ltRateCheckFlagYesList = new ArrayList<>();
            List<DuplicationCheckEntity> ltRateCheckFlagNoList = new ArrayList<>();

            List<DuplicationCheckEntity> noMetaDataList = new ArrayList<>();

            //数据分组：1. 重复率>=95,有cebx  2. 重复率>=95,无cebx  3. 重复率<95,有cebx  4. 重复率<95,无cebx  5.isbn 不匹配
            if (list.size() > 0) {
                for (DuplicationCheckEntity dce : list) {

                    if (dce.getFlag() == DuplicationCheckEntity.CheckFlag.DUPLICATE_YES) {
//                    if (dce.getFlag() == DuplicationCheckEntity.CheckFlag.DUPLICATE_YES){
//                        gtRateCheckFlagYesList.add(dce);
//                    }else {
//                        gtRateCheckFlagNoList.add(dce);
//                    }
                        ltRateCheckFlagYesList.add(dce);//有cebx
                    } else if (dce.getFlag() == DuplicationCheckEntity.CheckFlag.DUPLICATE_NO) {
//                    if (dce.getFlag() == DuplicationCheckEntity.CheckFlag.DUPLICATE_YES){
//                        ltRateCheckFlagYesList.add(dce);
//                    }else {
//                        ltRateCheckFlagNoList.add(dce);
//                    }
                        ltRateCheckFlagNoList.add(dce);//无ceb
                    } else {
                        noMetaDataList.add(dce);
                    }
                }
            }

//            model.addAttribute("gtRateCheckFlagYesList",gtRateCheckFlagYesList);
//            model.addAttribute("gtRateCheckFlagNoList",gtRateCheckFlagNoList);
            model.addAttribute("ltRateCheckFlagYesList", ltRateCheckFlagYesList.stream().sorted((dc1, dc2) -> dc1.getRateFlag().compareTo(dc2.getRateFlag())).collect(Collectors.toList()));
            model.addAttribute("ltRateCheckFlagNoList", ltRateCheckFlagNoList);
            model.addAttribute("noMetaDataList", noMetaDataList);

            logger.info("书目查询耗时： {}", System.currentTimeMillis() - start);
        } catch (Exception e) {
            logger.error("Exception {}", e);
        }
        return "processing/bibliothecaDuplication";
    }

    /**
     * 批次添加页面
     *
     * @param model
     * @return
     */
    @GetMapping("/add/index")
    public String addIndex(@RequestParam("batchId") String batchId, Model model) {
        model.addAttribute("batchId", batchId);
        model.addAttribute("publisherList", publisherDao.findAll());
        return "processing/addBibliotheca";
    }

    /**
     * 书目添加
     *
     * @param bibliotheca
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public ResultEntity addBibliotheca(@RequestBody Bibliotheca bibliotheca) {
        try {
            if (StringUtils.isBlank(bibliotheca.getIdentifier())
                    || StringUtils.isBlank(bibliotheca.getBatchId())
                    || StringUtils.isBlank(bibliotheca.getTitle())
                    || StringUtils.isBlank(bibliotheca.getOriginalFilename())
                    || StringUtils.isBlank(bibliotheca.getBibliothecaState().getDesc())
                    ) {
                return new ResultEntity(400, "请检查必填项是否完整！");
            }
            //书目添加
            bibliotheca.setPublisherName(bibliotheca.getPublisher().split(",")[1]);
            bibliotheca.setPublisher(bibliotheca.getPublisher().split(",")[0]);
//            bibliotheca.setBibliothecaState(BibliothecaStateEnum.NEW);
            if (bibliothecaService.addBibliothecaList(Arrays.asList(bibliotheca))) {
                return new ResultEntity(200, "添加成功");
            }
            return new ResultEntity(500, "添加失败!");
        } catch (Exception e) {
            logger.error("添加书目出现异常： {}", e);
            return new ResultEntity(500, e.getMessage());
        }
    }

    /**
     * 书目批量导入
     *
     * @param batchId
     * @param file
     * @return
     */
    @PostMapping("/batch/import")
    @ResponseBody
    public ResultEntity bibliothecaBatchImport(@RequestParam String batchId, @RequestParam("file") MultipartFile file) {

        if (StringUtils.isBlank(batchId)) {
            return new ResultEntity(403, "批次号不能为空！");
        }

        // 读取Excel工具类
        Map<Integer, Map<Object, Object>> data = null;
        try (InputStream inputStream = file.getInputStream()) {
            String fileName = file.getOriginalFilename();
            ReadExcelTextUtils readExcelTextUtils = new ReadExcelTextUtils(inputStream, fileName);
            // 读取Excel中的内容
            data = readExcelTextUtils.getDataByInputStream();
            if (data == null || data.isEmpty()) {
                throw new Exception();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResultEntity(403, "文件读取出错，请重新尝试或联系管理员！");
        } catch (Exception e) {
            return new ResultEntity(500, "文件出错，请检查文件格式是否正确或内容是否完整！");
        }

        //对数据进行转换
        try {
            List<Bibliotheca> list = bibliothecaService.resolveBibliothecaData(data, batchId);

            if (list == null || list.isEmpty()) {
                return new ResultEntity(500, "数据为空，导入失败！");
            }

            if (bibliothecaService.addBibliothecaList(list)) {
                return new ResultEntity(200, "导入成功！");
            }
            return new ResultEntity(500, "导入失败！");
        } catch (Exception e) {
            return new ResultEntity(500, e.getMessage());
        }
    }

    /**
     * 批次编辑页面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/edit/index")
    public String editIndex(@RequestParam("id") String id, Model model) {
        try {
            long start = System.currentTimeMillis();
            Bibliotheca bibliotheca = bibliothecaService.getBibliothecaById(id);
            model.addAttribute("bibliotheca", bibliotheca);
            model.addAttribute("publisherList", publisherDao.findAll());
            List<BibliothecaStateEnum> list = new ArrayList();
            list.add(BibliothecaStateEnum.NEW);
            list.add(BibliothecaStateEnum.INFORMATION_NO);
            model.addAttribute("bibliothecaStateList", list);
            long end = System.currentTimeMillis();
            logger.info("修改书目信息耗时：" + (end - start) + "毫秒");
        } catch (Exception e) {
            logger.error("Exception {}", e);
        }
        return "processing/editBibliotheca";
    }

    /**
     * 书目编辑
     *
     * @param bibliotheca
     * @return
     */
    @PostMapping("/edit")
    @ResponseBody
    public ResultEntity editBibliotheca(@RequestBody Bibliotheca bibliotheca) {
        try {
            if (StringUtils.isBlank(bibliotheca.getId())
                    || StringUtils.isBlank(bibliotheca.getIdentifier())
                    || StringUtils.isBlank(bibliotheca.getTitle())
                    || StringUtils.isBlank(bibliotheca.getOriginalFilename())) {
                return new ResultEntity(400, "请检查必填项是否完整！");
            }
            bibliotheca.setPublisherName(bibliotheca.getPublisher().split(",")[1]);
            bibliotheca.setPublisher(bibliotheca.getPublisher().split(",")[0]);
            //书目更新
            int i = bibliothecaService.updateBibliotheca(bibliotheca);
            if (i == 1) {
                return new ResultEntity(200, "修改成功");
            } else if (i == -1) {
                return new ResultEntity(500, "出版日期有误!");
            }
            return new ResultEntity(500, "修改失败!");
        } catch (Exception e) {
            logger.error("修改书目出现异常： {}", e);
            return new ResultEntity(500, e.getMessage());
        }
    }

    /**
     * 书目删除
     *
     * @param id
     * @return
     */
    @GetMapping("/removeBibliotheca")
    @ResponseBody
    public ResultEntity removeBibliotheca(@RequestParam("id") String id) {
        try {
            if (StringUtils.isBlank(id)) {
                return new ResultEntity(400, "数据异常！");
            }

            if (bibliothecaService.deleteBibliothecaById(id)) {
                return new ResultEntity(200, "操作成功");
            }
            return new ResultEntity(500, "操作失败，请重新尝试或联系管理员！");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultEntity(500, e.getMessage());
        }
    }

    //分拣
    @GetMapping("/updateBibliothecaExclude")
    @ResponseBody
    public ResultEntity updateBibliothecaExclude(@RequestParam("id") String id) {
        try {
            if (StringUtils.isBlank(id)) {
                return new ResultEntity(400, "数据异常！");
            }
            Bibliotheca bibliotheca = bibliothecaService.getBibliothecaById(id);
            bibliotheca.setBibliothecaState(BibliothecaStateEnum.SORTING);
            bibliotheca.setUpdateTime(new Date());
            bibliothecaService.updateBibliotheca(bibliotheca);
            String batchId = bibliotheca.getBatchId();
            Map map = new HashMap();
            map.put("batchId", batchId);
            List<Bibliotheca> bibliothecas = bibliothecaService.listBibliotheca(map);
            int num = 0;
            for (Bibliotheca b : bibliothecas) {
                if (b.getBibliothecaState().getCode() == 0) {
                    num++;
                }
            }
            if (num == 0) {
                Batch batch = batchMapper.selectByBatchId(batchId);
                batch.setUpdateTime(new Date());
                batch.setBatchState(BatchStateEnum.WAITING_PRODUCTION);
                batchService.updateBatch(batch);
            }
            return new ResultEntity(200, "操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultEntity(500, e.getMessage());
        }
    }

    /**
     * 书目查重# 制作成功
     *
     * @param bibliothecas
     * @return
     */
    @PostMapping("/makeSuccess")
    @ResponseBody
    public ResultEntity makeSuccess(@RequestBody List<Bibliotheca> bibliothecas) {
        try {
            List<Bibliotheca> bibliothecaList = bibliothecas.stream()
                    .filter(b -> ((b.getBibliothecaState() == BibliothecaStateEnum.HAS_PROCESS) && StringUtils.isNotBlank(b.getId())))
                    .map(bi -> {
                        bi.setUpdateTime(new Date());
                        bi.setCompletedFlag(CompletedFlagEnum.YES);
                        bi.setBibliothecaState(BibliothecaStateEnum.MAKESUC);
                        return bi;
                    })
                    .collect(Collectors.toCollection(ArrayList::new));

            if (bibliothecaList == null || bibliothecaList.isEmpty()) {
                return new ResultEntity(500, "请制作已排产的书目");
            }

            bibliothecaService.listUpdateBibliotheca(bibliothecaList);
            //更改状态 制作中/已完成
            Bibliotheca bibliotheca = bibliothecaService.getBibliothecaById(bibliothecaList.get(0).getId());
            String batchId = bibliotheca.getBatchId();
            Batch batch = batchMapper.selectByBatchId(batchId);
            batch.setUpdateTime(new Date());
            Map map = new HashMap();
            map.put("batchId", batchId);
            List<Bibliotheca> bibliothecas1 = bibliothecaService.listBibliotheca(map);
            int num = 0;
            for (Bibliotheca b : bibliothecas1) {
                if (b.getBibliothecaState().getCode() == 5) {
                    num++;
                }
            }
            if (num == 0) {
                batch.setBatchState(BatchStateEnum.COMPLETED);
            } else {
                batch.setBatchState(BatchStateEnum.PROCESSING);
            }
            batchService.updateBatch(batch);
            return new ResultEntity(200, "操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultEntity(500, e.getMessage());
        }
    }

    /**
     * 书目查重# 制作失败
     *
     * @param bibliothecas
     * @return
     */
    @PostMapping("/makeFail")
    @ResponseBody
    public ResultEntity makeFail(@RequestBody List<Bibliotheca> bibliothecas) {
        try {
            List<Bibliotheca> bibliothecaList = bibliothecas.stream()
                    .filter(b -> ((b.getBibliothecaState() == BibliothecaStateEnum.HAS_PROCESS) && StringUtils.isNotBlank(b.getId())))
                    .map(bi -> {
                        bi.setUpdateTime(new Date());
                        bi.setCompletedFlag(CompletedFlagEnum.YES);
                        bi.setBibliothecaState(BibliothecaStateEnum.MAKEFAIL);
                        return bi;
                    })
                    .collect(Collectors.toCollection(ArrayList::new));

            if (bibliothecaList == null || bibliothecaList.isEmpty()) {
                return new ResultEntity(500, "请制作已排产的书目！");
            }

            bibliothecaService.listUpdateBibliotheca(bibliothecaList);
            //更改状态 制作中/已完成
            //获取batchId

            Bibliotheca bibliotheca = bibliothecaService.getBibliothecaById(bibliothecaList.get(0).getId());
            String batchId = bibliotheca.getBatchId();
            //获取batch
            Batch batch = batchMapper.selectByBatchId(batchId);
            batch.setUpdateTime(new Date());
            Map map = new HashMap();
            map.put("batchId", batchId);
            List<Bibliotheca> bibliothecas1 = bibliothecaService.listBibliotheca(map);
            int num = 0;
            for (Bibliotheca b : bibliothecas1) {
                if (b.getBibliothecaState().getCode() == 5) {
                    num++;
                }
            }
            if (num == 0) {
                batch.setBatchState(BatchStateEnum.COMPLETED);
            } else {
                batch.setBatchState(BatchStateEnum.PROCESSING);
            }
            batchService.updateBatch(batch);
            return new ResultEntity(200, "操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultEntity(500, e.getMessage());
        }
    }

    /**
     * 书目查重# 确认操作
     *
     * @param dataType gtEqYes, gtEqNo, ltYes, ltNo, noMatch : >=95 yes, >=95 no, <95 yes, <95 no, noMatch
     * @param btnType  duplicate,  make => 按钮操作：　确认重复，确认制作
     * @param ids      每一项数据都是 书目id,metaId 格式的字符串
     * @return
     */
    @PostMapping("/sureOperation")
    @ResponseBody
    public ResultEntity sureOperation(@RequestParam String dataType, @RequestParam String btnType, @RequestBody List<String> ids) {
        try {
            if (StringUtils.isBlank(dataType) || StringUtils.isBlank(btnType)) {
                return new ResultEntity(403, "数据异常");
            }
            //检测数据是否属于在当前范围

            boolean dataTypeFlag =
                    dataType.equals("ltYes")
                            || dataType.equals("ltNo")
                            || dataType.equals("noMatch");

            boolean btnTypeFlag = btnType.equals("duplicate") || btnType.equals("make");

            //当两条个条件都满足时，正常路过，否则抛出异常
            if (dataTypeFlag && btnTypeFlag) {

            } else {
                return new ResultEntity(403, "数据异常");
            }
            if (ids == null || ids.isEmpty()) {
                return new ResultEntity(403, "请选择数据！");
            }

            List<String> bibliothecaIdList = new ArrayList<>(ids.size());
            List<String> metaIdList = new ArrayList<>(ids.size());

            //将 书目id 和 元数据id 分开
            for (int i = 0; i < ids.size(); i++) {
                String[] s = StringUtils.split(ids.get(i), ',');
//                if (s[0].endsWith(","))
                //校验数据是否正常
                if (s.length == 0 || (s.length == 1 && !ids.get(i).endsWith(","))) {
                    return new ResultEntity(403, "数据异常");
                }
                //书目id 只取数组中的第一个，余下的都加入到 metaId 的数组中，它们的下标是同步的，用于后续同步处理
                bibliothecaIdList.add(s[0]);
                //当 不是 没有匹配数据(noMatch)的类型时，添加 metaIdList，否则不添加
                if (!"noMatch".equals(dataType)) {
                    metaIdList.add(ids.get(i).substring(s[0].length() + 1));
                }
            }
            //确认重复
            if ("duplicate".equals(btnType)) {
                bibliothecaService.listSureOperation(bibliothecaIdList, metaIdList, dataType, btnType,
                        BibliothecaStateEnum.REPEAT, DuplicateFlagEnum.YES);
            } else {
                //确认不重复
                bibliothecaService.listSureOperation(bibliothecaIdList, metaIdList, dataType, btnType,
                        BibliothecaStateEnum.NOREPEAT, DuplicateFlagEnum.NO);
            }
            return new ResultEntity(200, "操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultEntity(500, e.getMessage());
        }
    }

    @RequestMapping({"/outUnit/exportData", "/exportData"})
    @ResponseBody
    public String exportData(@RequestParam(value = "batchId", required = true) String batchId,
                             @RequestParam(value = "title", required = false) String title,
                             @RequestParam(value = "publisher", required = false) String publisher,
//                             @RequestParam(value = "duplicateFlag", required = false)DuplicateFlagEnum duplicateFlag,
                             @RequestParam(value = "bibliothecaState", required = false) BibliothecaStateEnum bibliothecaState,
                             HttpServletResponse response) throws IOException {
        try {
            // 设置响应头
            response.setContentType("application/binary;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");

            // 获取所有的isbn列表
            Map<String, Object> paramsMap = new HashMap<>();
            ParamsUtils.checkParameterAndPut2Map(paramsMap, "batchId", batchId, "title", title, "publisher", publisher);
//            paramsMap.put("duplicateFlag", duplicateFlag);
            paramsMap.put("bibliothecaState", bibliothecaState);
            List<Bibliotheca> list = bibliothecaService.listBibliotheca(paramsMap);
            if (list == null || list.isEmpty()) {
                return "<script type='text/javascript'>alert('当前条件查询结果为空！');history.back();</script>";
            }

            List<BibliothecaExcelModel> excelModelList = new ArrayList<>(list.size());

            for (Bibliotheca b : list) {
                BibliothecaExcelModel bem = new BibliothecaExcelModel();
                BeanUtils.copyProperties(b, bem);
                excelModelList.add(bem);
            }
            // 设置文件名
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
            String time = simpleDateFormat.format(date);
            File file = new File(time + "-" + UUIDCreater.nextId() + ".xls");
            String fileName = new String(file.getName().getBytes("UTF-8"), "ISO-8859-1");

            // 设置excel的表头
            String[] excelTitle = {"编号", "资源唯一标识", "批次号", "原文件名", "标题", "作者", "出版社", "ISBN", "出版时间",
                    "版次", "纸书价格", "电子书价格", "文档格式", "备注", "是否重复", "书目状态", "是否制作成功", "书目录入人", "书目录入时间"};

            // 将内存中读取到的内容写入到excel
            bibliothecaService.writeData2Excel(1, fileName, excelTitle, excelModelList, "sheet1", response);
            return "<script type='text/javascript'>alert('导出成功！');history.back();</script>";
        } catch (Exception e) {
            e.printStackTrace();
            return "<script type='text/javascript'>alert('" + e.getMessage() + "');history.back();</script>";
        }
    }

    //授权清单导出
    @RequestMapping("/exportData2")
    @ResponseBody
    public String exportData(@RequestParam(value = "batchId", required = true) String batchId, HttpServletResponse response) {
        try {
            // 设置响应头
            response.setContentType("application/binary;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            Map<String, Object> paramsMap = new HashMap<>();
            ParamsUtils.checkParameterAndPut2Map(paramsMap, "batchId", batchId);
            paramsMap.put("bibliothecaState", BibliothecaStateEnum.HAS_PROCESS);
            List<Bibliotheca> list = bibliothecaService.listBibliotheca(paramsMap);
            Batch batch = batchService.selectByBatchId(batchId);
            if (list == null || list.isEmpty()) {
                return "<script type='text/javascript'>alert('当前条件查询结果为空！');history.back();</script>";
            }

            List<BibliothecaExcelModel> excelModelList = new ArrayList<>(list.size());

            for (Bibliotheca b : list) {
                BibliothecaExcelModel bem = new BibliothecaExcelModel();
                BeanUtils.copyProperties(b, bem);
                excelModelList.add(bem);
            }
            // 设置文件名
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
            String time = simpleDateFormat.format(date);
            File file = new File(time + "-" + UUIDCreater.nextId() + ".xls");
            String fileName = new String(file.getName().getBytes("UTF-8"), "ISO-8859-1");

            // 设置excel的表头
            String[] excelTitle = {"序号", "编号", "书号", "书名", "作者", "出版社", "出版日期", "纸书价格（元）", "电子书价格（元）", "版权期限起始时间",
                    "版权期限终止时间", "唯一标识（MetaId）", "批次号"};
            // 将内存中读取到的内容写入到excel
            bibliothecaService.writeData2Excel(2, fileName, excelTitle, excelModelList, "sheet1", response);

            List<CopyrightOwner> all = copyrightOwnerMapper.findAll();
            Map map1 = new HashMap();
            for (CopyrightOwner c : all) {
                map1.put(c.getId(), c.getName());
            }
            //和授权资源衔接
            for (Bibliotheca bibliotheca : list) {
                Bibliotheca b = bibliothecaService.getBibliothecaById(bibliotheca.getId());
                Map map = new HashMap();
                map.put("metaId", b.getMetaId());
                map.put("batchNum", b.getBatchId());
                PageHelper.startPage(1, 10);
                Page b1 = resourceService.listResource(map);
                if (b1.getPages() > 0) {
                    continue;
                } else {
                    Resource resource = new Resource();
//                    resource.setResrId(UUIDCreater.nextId());
                    resource.setBatchNum(b.getBatchId());
                    resource.setIdentifier(b.getIdentifier());
                    resource.setMetaId(b.getMetaId());
                    resource.setInsertDate(new Date());
                    resource.setTitle(b.getTitle());
                    resource.setCreator(b.getAuthor());
                    resource.setPublisher(b.getPublisherName());
                    resource.setPublisherId(b.getPublisher());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    resource.setIssuedDate(sdf.parse(StringToolUtil.issuedDateFormat(b.getPublishTime())));
                    resource.setIsbn(b.getIsbn());
                    resource.setPaperPrice(Double.valueOf(b.getPaperPrice()));
                    resource.setePrice(Double.valueOf(b.geteBookPrice()));
                    resource.setCopyrightOwnerId(batch.getCopyrightOwner());
                    resource.setCopyrightOwner((String) map1.get(batch.getCopyrightOwner()));
                    resource.setStatus(ResourceStatusEnum.AUTHORIZATIONPERIOD);
                    resource.setOperateDate(new Date());
                    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    resource.setOperator(userDetails.getUsername());
                    resourceService.insert(resource);
                }
            }
            return "<script type='text/javascript'>alert('导出成功！');history.back();</script>";
        } catch (Exception e) {
            e.printStackTrace();
            return "<script type='text/javascript'>alert('" + e.getMessage() + "');history.back();</script>";
        }
    }

    @RequestMapping({"/checkIsbn"})
    @ResponseBody
    public Object exportData(@RequestParam(value = "isbn", required = true) String isbn) {
        boolean b = IsbnCheck.CheckISBN(isbn);
        if (b) {
            return new ResultEntity(200, "isbn校验正确");
        } else {
            return new ResultEntity(400, "isbn校验错误！请输入正确的isbn");
        }
    }

    //书目解析
    @RequestMapping(value = "/parsing", method = RequestMethod.POST)
    @ResponseBody
    public Object parsing(@RequestParam(value = "path") String path, @RequestParam(value = "id") String id, @RequestParam(value = "batchId") String batchId) throws InterruptedException {
        File file = new File(path);
        if (file.exists()) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();
            bibliothecaService.parsing(path, id, username, batchId);
            Batch batch = new Batch();
            batch.setId(id);
            batch.setBatchState(BatchStateEnum.BEGIN_SCANNING);
            batch.setUpdateTime(new Date());
            batchService.updateStateByPrimaryKey(batch);
            return new ResultEntity(200, "已经开始解析请耐心等待");
        } else {
            return new ResultEntity(500, "资源路径不存在请核实");
        }
    }

    @RequestMapping({"/pdf"})
    public String pdf(@RequestParam(value = "id") String id, Model model) {
        try {
            if (StringUtils.isNotBlank(id)) {
                Bibliotheca bibliotheca = bibliothecaService.getBibliothecaById(id);
                String originalFilename = bibliotheca.getOriginalFilename();
                Batch batch = batchService.selectByBatchId(bibliotheca.getBatchId());
                String resourcePath = batch.getResourcePath();
                if (StringUtils.isNotBlank(originalFilename) && StringUtils.isNotBlank(resourcePath)) {
                    String path = resourcePath + "/" + originalFilename;
                    path = path.split(":")[1];
                    path = path.replaceAll("\\\\\\\\", "/");
                    path = path.replaceAll("\\\\", "/");
                    model.addAttribute("path", path);
                    return "processing/canvas/index";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    //批量转换文件成cebx文件
    @RequestMapping(value = "/batchConvert2Cebx", method = RequestMethod.POST)
    @ResponseBody
    public String batchConvert2Cebx(@RequestParam(value = "dirPath") String dirPath,
                                    @RequestParam(value = "batchId") String batchId,
                                    @RequestParam(value = "fileInfo") String fileInfo) {
        if (StringUtils.isNotBlank(dirPath) && StringUtils.isNotBlank(fileInfo)) {
            // boolean res = bibliothecaService.ctlBatchConvert2Cebx(dirPath, batchId, fileInfo);
            boolean res = bibliothecaService.ctlBatchConvert(dirPath, batchId, fileInfo);
            if (res == true) {
                return "success";
            } else {
                return "warn";
            }
        }
        return "error";
    }

    //标引
    @RequestMapping(value = "/indexing")
    public String indexing(@RequestParam(value = "id") String id) {
        try {
            if (StringUtils.isNotBlank(id)) {
                Bibliotheca bibliotheca = bibliothecaService.getBibliothecaById(id);
                String path = config.getUploadCebx() + File.separator + bibliotheca.getPublishTime() + File.separator + bibliotheca.getMetaId() + ".CEBX";
                String cmd = config.getCarbonExe() + " \"" + path + "\"";
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(cmd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/processing/bibliotheca/outUnit/index";
    }

    //编辑图书元数据跳转
    @RequestMapping(value = "/editBookMeta")
    public String editBookMeta(@RequestParam(value = "metaId") String metaId, Model model) {
        if (StringUtils.isNotBlank(metaId)) {
            BookMeta bookMeta = bookMetaService.selectBookMetaDetailById(metaId);
            if (bookMeta == null) {
                bookMeta = new BookMeta();
            }
            model.addAttribute("bookMeta", bookMeta);
        }
        return "processing/editBookMeta";
    }

    //更新图书元数据
    @RequestMapping(value = "/updateBookMeta", method = RequestMethod.POST)
    @ResponseBody
    public String updateBookMeta(@RequestBody BookMeta bookMeta) {
        if (bookMeta != null) {
            bookMetaService.updateBookMetaById(bookMeta);
            return "success";
        }
        return "error";
    }

    //上传CEBXM文件跳转
    @RequestMapping(value = "/uploadCebxm")
    public String uploadCebxm(@RequestParam(value = "metaId") String metaId, Model model) {
        if (StringUtils.isNotBlank(metaId)) {
            BookMeta bookMeta = bookMetaService.selectBookMetaDetailById(metaId);
            if (bookMeta == null) {
                bookMeta = new BookMeta();
            }
            model.addAttribute("bookMeta", bookMeta);
        }
        return "processing/uploadCebxm";
    }

    //上传CEBM文件
    @RequestMapping(value = "/uploadCebxm2Server", method = RequestMethod.POST)
    @ResponseBody
    public String uploadCebxm2Server(@RequestParam(value = "file", required = false) MultipartFile file,
                                     @RequestParam("metaId") String metaId,
                                     @RequestParam("publishDate") String publishDate) {
        if (StringUtils.isNotBlank(metaId)) {
            long res = bookMetaService.countBookMetaById(metaId);
            if (res == 0) {
                return "id_0";
            }
        } else {
            return "id_null";
        }
        if (file != null) {
            try {
                //上传图书到服务端指定目录
                long start = System.currentTimeMillis();
                //获取文件后缀
                String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                String dirPath = config.getUploadCebx() + File.separator + publishDate.substring(0, 10);
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File newFile = new File(dir, metaId + "." + suffix);
                file.transferTo(newFile);
                long end = System.currentTimeMillis();
                logger.info(file.getOriginalFilename() + "图书上传成功！耗时：" + (end - start) + "毫秒");
                //解密该cebxm文件
                bibliothecaService.decrypt(dir.getPath(), metaId);
                return "success";
            } catch (Exception e) {
                logger.warn("上传图书{}，时出现异常{}", file.getOriginalFilename(), e.getMessage());
            }
        }
        return "error";
    }

    //上传图片文件跳转
    @RequestMapping(value = "/uploadImage")
    public String uploadImage(@RequestParam(value = "metaId") String metaId, Model model) {
        if (StringUtils.isNotBlank(metaId)) {
            BookMeta bookMeta = bookMetaService.selectBookMetaDetailById(metaId);
            if (bookMeta == null) {
                bookMeta = new BookMeta();
            }
            model.addAttribute("bookMeta", bookMeta);
        }
        return "processing/uploadImage";
    }

    //上传图片文件
    @RequestMapping(value = "/uploadImage2Server", method = RequestMethod.POST)
    @ResponseBody
    public String uploadImage2Server(@RequestParam(value = "file", required = false) MultipartFile file,
                                     @RequestParam("metaId") String metaId,
                                     @RequestParam("publishDate") String publishDate) {
        if (StringUtils.isNotBlank(metaId)) {
            long res = bookMetaService.countBookMetaById(metaId);
            if (res == 0) {
                return "id_0";
            }
        } else {
            return "id_null";
        }
        if (file != null) {
            try {
                //上传图书到服务端指定目录
                long start = System.currentTimeMillis();
                //获取文件后缀
                String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                String dirPath = config.getUploadCebx() + File.separator + publishDate.substring(0, 10);
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File newFile = new File(dir, metaId + "_S." + suffix);
                file.transferTo(newFile);
                long end = System.currentTimeMillis();
                logger.info(file.getOriginalFilename() + "图片上传成功！耗时：" + (end - start) + "毫秒");
                return "success";
            } catch (Exception e) {
                logger.warn("上传图片{}，时出现异常{}", file.getOriginalFilename(), e.getMessage());
            }
        }
        return "error";
    }

    //标引
    @RequestMapping(value = "/editCebxmByCarbon")
    public String editCebxmByCarbon(@RequestParam(value = "metaId") String metaId,
                                  HttpServletResponse response) {
        if (StringUtils.isNotBlank(metaId)) {
            try {
                BookMeta bookMeta = bookMetaService.selectBookMetaDetailById(metaId);
                if (bookMeta != null
                        && StringUtils.isNotBlank(bookMeta.getIssuedDate())
                        && bookMeta.getIssuedDate().length() == 10) {
                    String issuedDate = bookMeta.getIssuedDate();
                    String serverFilePath = config.getUploadCebx()
                            + File.separator + issuedDate
                            + File.separator + metaId + ".CEBXM";
                    File serverFile = new File(serverFilePath);
                    if (serverFile.exists()) {
                        response.setContentType("text/html;charset=utf-8");
                        response.setCharacterEncoding("utf-8");
                        // 设置相应头，控制浏览器下载该文件，这里就是会出现当你点击下载后，出现的下载地址框
                        response.setHeader("content-disposition",
                                "attachment;filename=" + URLEncoder.encode(serverFile.getName(), "utf-8"));
                        // 读取要下载的文件，保存到文件输入流
                        FileInputStream in = new FileInputStream(serverFilePath);
                        // 创建输出流
                        OutputStream out = response.getOutputStream();
                        // 创建缓冲区
                        byte buffer[] = new byte[1024];
                        int len = 0;
                        // 循环将输入流中的内容读取到缓冲区中
                        while ((len = in.read(buffer)) > 0) {
                            // 输出缓冲区内容到浏览器，实现文件下载
                            out.write(buffer, 0, len);
                        }
                        // 关闭文件流
                        in.close();
                        // 关闭输出流
                        out.close();
                    }else {
                        return "processing/error";
                    }
                } else {
                    return "processing/error";
                }
            } catch (Exception e) {
                logger.warn("下载文件{}，时出现异常{}", metaId, e.getMessage());
                return "processing/error";
            }
        }
        return null;
    }
}
