package com.apabi.flow.book.controller;

import com.apabi.flow.book.model.BookBatchRes;
import com.apabi.flow.book.model.BookMetaVo;
import com.apabi.flow.book.model.BookTask;
import com.apabi.flow.book.model.BookTaskResult;
import com.apabi.flow.book.service.BookMetaService;
import com.apabi.flow.book.service.BookTaskResultService;
import com.apabi.flow.book.service.BookTaskService;
import com.apabi.flow.book.service.impl.BookTaskServiceImpl;
import com.apabi.flow.book.task.BookBatchTask;
import com.apabi.flow.book.util.ReadBook;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author guanpp
 * @date 2018/12/7 17:01
 * @description
 */
@Controller("bookTask")
@RequestMapping(value = "/bookTask")
public class BookTaskController {

    private static final Logger log = LoggerFactory.getLogger(BookTaskServiceImpl.class);

    public static final Integer DEFAULT_PAGESIZE = 10;

    @Autowired
    BookTaskService bookTaskService;

    @Autowired
    BookTaskResultService bookTaskResultService;

    @Autowired
    ReadBook readBook;

    //创建扫描任务
    @RequestMapping(value = "/taskFileScan", method = RequestMethod.POST)
    @ResponseBody
    public String taskFileScan(@RequestParam("filePath") String filePath,
                               @RequestParam("fileType") String fileType,
                               @RequestParam("isCover") Integer isCover) {
        if (!StringUtils.isEmpty(filePath) && !StringUtils.isEmpty(fileType)) {
            bookTaskService.createBookTask(filePath, fileType, isCover);
            return "success";
        }
        return "error";
    }

    //查看任务列表
    @RequestMapping(value = "/showTaskList")
    public String showTaskList(HttpServletRequest request, Model model,
                               @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNum) {
        try {
            long start = System.currentTimeMillis();
            Map<String, String[]> params = request.getParameterMap();
            Map<String, Object> queryMap = new HashMap<>();
            PageHelper.startPage(pageNum, DEFAULT_PAGESIZE, "CREATE_TIME DESC");
            Page<BookTask> page = bookTaskService.showTaskList(queryMap);
            if (page == null) {
                model.addAttribute("bookTaskList", null);
            } else {
                model.addAttribute("bookTaskList", page.getResult());
                model.addAttribute("pages", page.getPages());
                model.addAttribute("pageNum", page.getPageNum());
            }
            model.addAttribute("page", page);
            long end = System.currentTimeMillis();
            log.info("图书任务列表查询耗时：{}毫秒", (end - start));
            return "bookTask/bookTaskList";
        } catch (Exception e) {
            log.warn("图书任务列表查询异常{}", e.getMessage());
        }
        return null;
    }

    //查看任务详情
    @RequestMapping(value = "showTaskInfo")
    public String showTaskInfo(Model model, @RequestParam("taskId") String taskId) {
        if (!StringUtils.isEmpty(taskId)) {
            List<BookTaskResult> resultList = bookTaskResultService.showTaskInfo(taskId);
            //获取路径和文件类型
            BookTask bookTask = bookTaskService.selectBookTask(taskId);
            if (bookTask != null) {
                model.addAttribute("fileType", bookTask.getFileType());
                model.addAttribute("filePath", bookTask.getTaskPath());
            }
            model.addAttribute("taskResultList", resultList);
            return "bookTask/bookTaskResult";
        }
        return null;
    }

    //批量抽取章节内容
    @RequestMapping(value = "/batchChapter", method = RequestMethod.POST)
    @ResponseBody
    public String batchChapter(@RequestParam("fileInfo") String fileInfo,
                               @RequestParam("filePath") String filePath,
                               @RequestParam("fileType") String fileType) {
        if (!StringUtils.isEmpty(fileInfo)) {
            if (!StringUtils.isEmpty(filePath)) {
                try {
                    if (fileType.equals("epub")) {
                        readBook.batchEpubTask(fileInfo, filePath);
                    } else if (fileType.equals("cebx")) {
                        readBook.batchCebxTask(fileInfo, filePath);
                    }
                    log.info("批量上传任务{}已开始", filePath);
                    return "success";
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            } else {
                return "path_null";
            }
        } else {
            return "id_null";
        }
        return "error";
    }

    //删除任务
    @RequestMapping(value = "/deleteBookTask", method = RequestMethod.GET)
    @ResponseBody
    public String deleteBookTask(@RequestParam("id") String id) {
        if (!StringUtils.isEmpty(id)) {
            long start = System.currentTimeMillis();
            try {
                int res = bookTaskService.deleteBookTask(id);
                if (res > 0) {
                    long end = System.currentTimeMillis();
                    log.info("删除任务{}，耗时：{}", id, (end - start));
                    return "success";
                }
            } catch (Exception e) {
                log.info("删除任务{}，时出现异常{}", id, e.getMessage());
            }
        }
        return "error";
    }
}
