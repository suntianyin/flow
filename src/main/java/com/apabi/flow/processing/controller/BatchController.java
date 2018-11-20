package com.apabi.flow.processing.controller;

import com.apabi.flow.admin.service.AuthUserService;
import com.apabi.flow.common.ResultEntity;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.common.util.ParamsUtils;
import com.apabi.flow.processing.constant.BatchStateEnum;
import com.apabi.flow.processing.constant.BibliothecaStateEnum;
import com.apabi.flow.processing.constant.DeleteFlagEnum;
import com.apabi.flow.processing.dao.BibliothecaMapper;
import com.apabi.flow.processing.dao.OutUnitMapper;
import com.apabi.flow.processing.dao.UserUnitMapper;
import com.apabi.flow.processing.model.Batch;
import com.apabi.flow.processing.model.Bibliotheca;
import com.apabi.flow.processing.model.OutUnit;
import com.apabi.flow.processing.model.UserUnit;
import com.apabi.flow.processing.service.BatchService;
import com.apabi.flow.publisher.dao.PublisherDao;
import com.apabi.flow.publisher.model.Publisher;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 功能描述： <br>
 * <批次Controller>
 *
 * @author supeng
 * @date 2018/9/5 14:24
 * @since 1.0.0
 */
@Controller
@RequestMapping("/processing/batch")
public class BatchController {

    private static final Logger logger = LoggerFactory.getLogger(BatchController.class);

    @Autowired
    private BatchService batchService;

    @Autowired
    private OutUnitMapper outUnitMapper;

    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private UserUnitMapper userUnitMapper;

    @Autowired
    private BibliothecaMapper bibliothecaMapper;


    @Autowired
    private PublisherDao publisherDao;

    /**
     * 批次展示页面
     * 外协单位名称 模糊查询在 条件中使用 outUnit， 如果使用外协单位id 查询，则参数使用 outUnitId，二选一即可
     *
     * @param id
     * @param manager
     * @param copyrightOwner
     * @param batchState
     * @param outUnit
     * @param beginTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @param model
     * @return
     */
    @RequestMapping("/index")
    public String index(@RequestParam(value = "id", required = false) String id,
                        @RequestParam(value = "manager", required = false) String manager,
                        @RequestParam(value = "copyrightOwner", required = false) String copyrightOwner,
                        @RequestParam(value = "batchState", required = false) BatchStateEnum batchState,
                        @RequestParam(value = "outUnit", required = false) String outUnit,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date beginTime,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
                        @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                        Model model) {
        try {
            long start = System.currentTimeMillis();
            PageHelper.startPage(pageNum, pageSize);
            Map<String, Object> paramsMap = new HashMap<>();
            ParamsUtils.checkParameterAndPut2Map(paramsMap, "id", id, "manager", manager, "copyrightOwner", copyrightOwner, "outUnit", outUnit);
            paramsMap.put("batchState", batchState);
            paramsMap.put("beginTime", beginTime);
            if (endTime != null) {
                paramsMap.put("endTime", new Date(new DateTime(endTime.getTime()).plusDays(1).getMillis()));
            }
            Page<Batch> page = batchService.listBatchPage(paramsMap);
            model.addAttribute("pages", page.getPages());
            model.addAttribute("pageNum", page.getPageNum());
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("total", page.getTotal());
            model.addAttribute("batchList", page.getResult());
            model.addAttribute("manager", manager);
            model.addAttribute("copyrightOwner", copyrightOwner);
            model.addAttribute("outUnit", outUnit);
            model.addAttribute("beginTime", beginTime);
            model.addAttribute("endTime", endTime);
            model.addAttribute("batchState", batchState);
            logger.info("批次查询耗时： {}", System.currentTimeMillis() - start);
        } catch (Exception e) {
            logger.error("Exception {}", e);
        }
        return "processing/batch";
    }

    /**
     * 书单录入页面，外协人员只能看到自己单位的批次信息
     *
     * @param id
     * @param manager
     * @param copyrightOwner
     * @param batchState
     * @param beginTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @param model
     * @return
     */
    @RequestMapping("/booklist/index")
    public String booklistIndex(@RequestParam(value = "id", required = false) String id,
                                @RequestParam(value = "manager", required = false) String manager,
                                @RequestParam(value = "copyrightOwner", required = false) String copyrightOwner,
                                @RequestParam(value = "batchState", required = false) BatchStateEnum batchState,
                                @RequestParam(value = "batchId", required = false) String batchId,
                                @DateTimeFormat(pattern = "yyyy-MM-dd") Date beginTime,
                                @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
                                @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                Model model) {
        try {

            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            Integer userId = authUserService.findUserIdByUserName(userDetails.getUsername());

            UserUnit userUnit = null;
            if (userId != null) {
                userUnit = userUnitMapper.selectByUserId(userId);
            }

            Page<Batch> page = null;

            //当前该用户所有的外协单位存在
            if (userUnit != null) {
                PageHelper.startPage(pageNum, pageSize);
                Map<String, Object> paramsMap = new HashMap<>();
                ParamsUtils.checkParameterAndPut2Map(paramsMap, "id", id,
                        "manager", manager, "copyrightOwner", copyrightOwner, "outUnitId", userUnit.getUnitId(), "batchId", batchId);
                paramsMap.put("batchState", batchState);
                paramsMap.put("beginTime", beginTime);
                if (endTime != null) {
                    paramsMap.put("endTime", new Date(new DateTime(endTime.getTime()).plusDays(1).getMillis()));
                }
                page = batchService.listBatchPage(paramsMap);
            }
            //当前用户不再某个外协单位中时，需要对分页做一些处理，或许也可能不需要
            if (page == null) {
                model.addAttribute("pages", 1);
                model.addAttribute("pageNum", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("total", 0);
                model.addAttribute("batchList", null);
            } else {
                model.addAttribute("pages", page.getPages());
                model.addAttribute("pageNum", page.getPageNum());
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("total", page.getTotal());
                model.addAttribute("batchList", page.getResult());
            }

            model.addAttribute("manager", manager);
            model.addAttribute("batchId", batchId);
            model.addAttribute("copyrightOwner", copyrightOwner);
            model.addAttribute("beginTime", beginTime);
            model.addAttribute("endTime", endTime);
            model.addAttribute("batchState", batchState);
        } catch (Exception e) {
            logger.error("Exception {}", e);
        }
        return "processing/booklist";
    }

    /**
     * 批次添加页面
     *
     * @param model
     * @return
     */
    @GetMapping("/add/index")
    public String addIndex(Model model) {
        model.addAttribute("outUnitList", outUnitMapper.selectAll());
        model.addAttribute("copyrightOwnerList", publisherDao.findAll());
        return "processing/addBatch";
    }

    /**
     * 批次添加
     *
     * @param batch
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public synchronized ResultEntity addBatch(@RequestBody Batch batch) {
        try {
            if (StringUtils.isBlank(batch.getManager())
                    || StringUtils.isBlank(batch.getBatchId())
                    || StringUtils.isBlank(batch.getCopyrightOwner())
                    || batch.getSourceType() == null) {
                return new ResultEntity(400, "请检查必填项是否完整！");
            }
            //batchId 查重
            List<Batch> list = batchService.listBatch(Collections.singletonMap("batchId", batch.getBatchId()));
            if (list != null && !list.isEmpty()) {
                return new ResultEntity(403, "该批次号已经存在！");
            }
            batch.setId(UUIDCreater.nextId());
            batch.setCreateTime(new Date());
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userDetails != null) {
                //记录操作人
                batch.setCreator(userDetails.getUsername());
            }

            //状态流转，没有选择外协是待分配 选择外协是待书单
            if (StringUtils.isNotBlank(batch.getOutUnit())) {
                batch.setBatchState(BatchStateEnum.WAITING_INPUT);
                batch.setDistributionOutTime(new Date());
            } else {
                batch.setBatchState(BatchStateEnum.WAITING_DISTRIBUTION);
            }

            batch.setDeleteFlag(DeleteFlagEnum.NORMAL);
            if (batchService.addBatch(batch)) {
                return new ResultEntity(200, "添加成功！");
            }
            return new ResultEntity(500, "添加失败！");
        } catch (Exception e) {
            logger.error("添加批次出现异常： {}", e);
            return new ResultEntity(500, "添加失败！");
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

            Batch batch = batchService.getBatchById(id);

            model.addAttribute("batch", batch);
            model.addAttribute("outUnitList", outUnitMapper.selectAll());
            model.addAttribute("copyrightOwnerList", publisherDao.findAll());

            long end = System.currentTimeMillis();
            logger.info("修改批次信息耗时：" + (end - start) + "毫秒");
        } catch (Exception e) {
            logger.error("Exception {}", e);
        }
        return "processing/editBatch";
    }

    /**
     * 批次编辑
     *
     * @param batch
     * @return
     */
    @PostMapping("/edit")
    @ResponseBody
    public ResultEntity editBatch(@RequestBody Batch batch) {
        try {
            if (StringUtils.isBlank(batch.getId())
                    || StringUtils.isBlank(batch.getManager())
                    || StringUtils.isBlank(batch.getBatchId())
                    || StringUtils.isBlank(batch.getCopyrightOwner())
                    || batch.getSourceType() == null) {
                return new ResultEntity(400, "请检查必填项是否完整！");
            }
            //batchId 查重
            List<Batch> list = batchService.listBatch(Collections.singletonMap("batchId", batch.getBatchId()));
            if (list != null) {
                for (Batch b : list) {
                    if (!batch.getId().equals(b.getId())) {
                        return new ResultEntity(403, "该批次号已经存在！");
                    }
                }
            }
            //状态流转，没有选择外协是待分配 选择外协是待书单
            if (StringUtils.isNotBlank(batch.getOutUnit())) {
                batch.setBatchState(BatchStateEnum.WAITING_INPUT);
                batch.setDistributionOutTime(new Date());
            } else {
                batch.setBatchState(BatchStateEnum.WAITING_DISTRIBUTION);
            }
            batch.setUpdateTime(new Date());

            if (batchService.updateBatch(batch) == 1) {
                return new ResultEntity(200, "修改成功！");
            }
        } catch (Exception e) {
            logger.error("添加批次出现异常： {}", e);
        }
        return new ResultEntity(500, "修改失败！");
    }

    /**
     * 批次 修改状态 页面
     *
     * @param id
     * @param model
     * @return
     */
//    @GetMapping("/editBatchState/index")
//    public String editBatchStateIndex(@RequestParam("id") String id, Model model) {
//        try {
//            long start = System.currentTimeMillis();
//            Batch batch = batchService.getBatchById(id);
//
//            model.addAttribute("batch", batch);
//
//            long end = System.currentTimeMillis();
//            logger.info("修改批次状态耗时：" + (end - start) + "毫秒");
//        } catch (Exception e) {
//            logger.error("Exception {}", e);
//        }
//        return "processing/editBatchState";
//    }

    /**
     * 排产
     *
     * @param id
     * @return
     */
    @GetMapping("/editBatchState")
    @ResponseBody
    public ResultEntity editBatchState(@RequestParam("id") String id) {
        try {
            Batch batch = batchService.getBatchById(id);
            if (batch.getBatchState().getCode() != 3) {
                return new ResultEntity(500, "当前批次状态非待排产，请确认后排产！");
            }
            batch.setBatchState(BatchStateEnum.PRODUCTION);
            batch.setUpdateTime(new Date());
            batch.setProductionSchedulingTime(new Date());
            batchService.updateBatch(batch);
            Map map = new HashMap();
            map.put("batchId", batch.getBatchId());
            List<Bibliotheca> bibliothecas = bibliothecaMapper.listBibliothecaSelective(map);
            for (Bibliotheca bibliotheca : bibliothecas) {
                if (bibliotheca.getBibliothecaState().getCode() == 2) {
                    bibliotheca.setBibliothecaState(BibliothecaStateEnum.HAS_PROCESS);
                    bibliothecaMapper.updateByPrimaryKeySelective(bibliotheca);
                }
            }
        } catch (Exception e) {
            logger.error("排产出现异常： {}", e);
            return new ResultEntity(500, "请联系管理员，系统异常！");
        }
        return new ResultEntity(200, "排产成功！");
    }

    /**
     * 书单审核 页面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/auditBatch/index")
    public String auditBatchIndex(@RequestParam("id") String id, Model model) {
        try {
            Batch batch = batchService.getBatchById(id);
            model.addAttribute("batch", batch);
        } catch (Exception e) {
            logger.error("Exception {}", e);
        }
        return "processing/auditBatch";
    }

    /**
     * 书单审核（批次审核）
     * @param batch
     * @return
     */
//    @PostMapping("/auditBatch")
//    @ResponseBody
//    public ResultEntity auditBatch(@RequestBody Batch batch){
//        try{
//            if (StringUtils.isBlank(batch.getId())
//                    || StringUtils.isBlank(batch.getBatchId())
//                    || (batch.getBatchState() != BatchStateEnum.AUDITED
//                            && batch.getBatchState() != BatchStateEnum.AUDIT_FAILED)
//                    ){
//                return new ResultEntity(400,"请检查数据是否有效！");
//            }
//
//            Batch paramBatch = new Batch();
//            paramBatch.setId(batch.getId());
//            paramBatch.setBatchId(batch.getBatchId());
//            paramBatch.setBatchState(batch.getBatchState());
//            paramBatch.setUpdateTime(new Date());
//            paramBatch.setAuditTime(new Date());
//            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            if (userDetails != null){
//                //记录操作人
//                paramBatch.setAuditor(userDetails.getUsername());
//            }
//
//            if (paramBatch.getBatchState() == BatchStateEnum.AUDIT_FAILED) {
//                if (batchService.updateBatchStateAndBibliothecaState(paramBatch,
//                        null, BibliothecaStateEnum.EDITABLE)) {
//                    return new ResultEntity(200, "操作成功！");
//                }
//            }else if (paramBatch.getBatchState() == BatchStateEnum.AUDITED){
//                //修改批次状态和书目状态
//                if (batchService.updateBatchStateAndBibliothecaState(paramBatch,
//                        BibliothecaStateEnum.WAITING_AUDIT, BibliothecaStateEnum.AUDITED)){
//                    return new ResultEntity(200, "操作成功！");
//                }
//            }
//        }catch (Exception e){
//            logger.error("操作失败： {}", e);
//        }
//        return new ResultEntity(500, "操作失败！");
//    }


    /**
     * 批次 提交书单审核
     *
     * @param batchId
     * @return
     */
    @GetMapping("/uploadBooklistAudit")
    @ResponseBody
    public ResultEntity uploadBooklistAudit(@RequestParam("batchId") String batchId) {
        try {
            if (StringUtils.isBlank(batchId)) {
                return new ResultEntity(400, "数据异常！");
            }
            UserUnit userUnit = getUserUnit();

            if (userUnit == null) {
                return new ResultEntity(403, "您与单位没有绑定外协关系，请联系管理员！");
            }
            Batch batch = batchService.selectByBatchId(batchId);
                batch.setUpdateTime(new Date());
                batch.setBatchId(batch.getBatchId());
                //当 提交书单审核时，则设置 当前批次为待查从
                batch.setBatchState(BatchStateEnum.WAITING_CHECKED);
                batch.setSubmitTime(new Date());
                batchService.updateBatch(batch);
                return new ResultEntity(200, "提交书单审核成功！");
        } catch (Exception e) {
            logger.error("修改批次状态出现异常： {}", e);
        }
        return new ResultEntity(500, "提交书单审核失败，请重新尝试或联系管理员！");
    }

    /**
     * 批次 提交制作审核
     *
     * @param batchId
     * @return
     */
    @GetMapping("/uploadMakeAudit")
    @ResponseBody
    public ResultEntity uploadMakeAudit(@RequestParam("batchId") String batchId) {
        try {
            if (StringUtils.isBlank(batchId)) {
                return new ResultEntity(400, "数据异常！");
            }

            UserUnit userUnit = getUserUnit();

            if (userUnit == null) {
                return new ResultEntity(403, "您与单位没有绑定外协关系，请联系管理员！");
            }

            //查询当前用户所属外协单位 和 参数批次号 所匹配的批次信息，防止通过参数修改到其他外协单位的批次状态信息
            Map paramMap = new HashMap();
            paramMap.put("outUnitId", userUnit.getUnitId());
            paramMap.put("batchId", batchId);
            List<Batch> list = batchService.listBatch(paramMap);

            //涉及到的批次号是唯一的，故只需取 index = 0 的批次信息即可
            if (list != null || !list.isEmpty()) {
                Batch batch = list.get(0);
                //这里只修改几个固定的参数值即可
                Batch paramBatch = new Batch();
                paramBatch.setId(batch.getId());
                paramBatch.setUpdateTime(new Date());
                //当 提交书单审核时，则设置 当前批次为 书单录入完成
                paramBatch.setBatchState(BatchStateEnum.WAITING_CHECKED);
                if (batchService.updateBatch(paramBatch) == 1) {
                    return new ResultEntity(200, "提交制作审核成功！");
                }
            }
        } catch (Exception e) {
            logger.error("提交制作审核出现异常： {}", e);
        }
        return new ResultEntity(500, "提交制作审核失败，请重新尝试或联系管理员！");
    }

    /**
     * 获取 用户和 外协单位之间的关系实体信息，仅适用于 外协用户 与 外协单位
     *
     * @return
     */
    private UserUnit getUserUnit() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = authUserService.findUserIdByUserName(userDetails.getUsername());

        return userUnitMapper.selectByUserId(userId);
    }

}
