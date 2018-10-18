package com.apabi.flow.author.controller;

import com.apabi.flow.author.model.Author;
import com.apabi.flow.author.service.AuthorService;
import com.apabi.flow.common.UUIDCreater;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 功能描述： <br>
 * <作者 controller层>
 *
 * @author supeng
 * @date 2018/8/27 16:47
 * @since 1.0.0
 */
@Controller
@RequestMapping("/author")
public class AuthorController {
    private Logger log = LoggerFactory.getLogger(AuthorController.class);

    @Autowired
    private AuthorService authorService;

    @GetMapping("/index")
    public String index(@RequestParam(value = "id", required = false)String id,
                        @RequestParam(value = "title", required = false)String title,
                        @RequestParam(value = "page", required = false, defaultValue = "1")Integer pageNum,
                        @RequestParam(value = "pageSize", required = false, defaultValue = "10")Integer pageSize,
                        Model model){
        try {
            long start = System.currentTimeMillis();

            //搜索保留
            model.addAttribute("authorId",id);
            model.addAttribute("authorTitle", title);

            Map<String, String> map = new HashMap();
            PageHelper.startPage(pageNum, pageSize);
            Page<Author> page = authorService.listAuthorsByPage(id, title);
            if (page != null && !page.isEmpty()){
                model.addAttribute("authorList", page.getResult());
                model.addAttribute("pages",page.getPages());
                model.addAttribute("pageNum", page.getPageNum());
            }else {
                model.addAttribute("authorList", Collections.emptyList());
                model.addAttribute("pages",1);
                model.addAttribute("pageNum", 1);
            }

            long end = System.currentTimeMillis();
            log.info("作者信息列表查询耗时：" + (end - start) + "毫秒");
            return "author/author";
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return "author/author";
    }

    /**
     * 根据id 或 title 查询作者列表
     *
     * @param id
     * @param title
     * @return 可能会有多个重名的作者，故返回list 列表
     */
    @GetMapping("/list")
    public List<Author> listAuthorsByIdAndTitle(@RequestParam(value = "id", required = false) String id,
                                                @RequestParam(value = "title", required = false) String title) {

        if (StringUtils.isBlank(id) && StringUtils.isBlank(title)){
            log.error("参数列表 id，title 不能同时为空");
            return null;
        }
        List<Author> list = authorService.listAuthorsByIdAndTitle(id, title);
        return list;
    }

    @GetMapping("/add/index")
    public String addIndex() {
        try {
            return "author/addAuthor";
        } catch (Exception e) {
            log.warn("Exception {}" , e);
        }
        return null;
    }

    /**
     * 添加作者信息
     *
     * @param author
     * @return 添加是否成功
     */
    @PostMapping("/add")
    public String addAuthor(@RequestBody Author author) {
        author.setId(UUIDCreater.nextId());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //todo 这里存储的是用户的名称，而不是用户id， 可能会出现数据不一致的现象，需要注意
        author.setOperator(userDetails.getUsername());
        author.setCreateTime(new Date());
        authorService.addAuthor(author);
        return "redirect:/author/index";
    }

    @GetMapping ("/edit/index")
    public String editPublisherMessage(@RequestParam("id") String id, Model model) {
        try {
            long start = System.currentTimeMillis();
            Author author = authorService.getAuthorById(id);
            model.addAttribute("author",author);
            long end = System.currentTimeMillis();
            log.info("修改作者信息耗时：" + (end - start) + "毫秒");
            return "author/editAuthor";
        } catch (Exception e) {
            log.warn("Exception {}" , e);
        }
        return null;
    }

    /**
     * 更新作者信息
     *
     * @param author
     * @return
     */
    @PostMapping("/edit")
    public String editAuthor(@RequestBody Author author) {
        if (StringUtils.isNotBlank(author.getId())){
            author.setUpdateTime(new Date());
            authorService.updateAuthor(author);
        }
        return "redirect:/author/index";
    }

    /**
     * 移除作者信息
     *
     * @param id
     * @return
     */
    @GetMapping("/delete")
    public String removeAuthor(String id) {
        authorService.removeAuthor(id);
        return "author/author";
    }

    @PostMapping("/batch/import")
    @ResponseBody
    public String batchImportAuthor(@RequestParam("file")MultipartFile file){

        if (!file.getOriginalFilename().endsWith(".csv")){
            return "文件格式不正确，仅支持 .csv 格式的文件";
        }

        Integer addedNum = 0;
        try {
            addedNum = authorService.batchAddAuthorFromFile(file);
        }catch (Exception e){
            log.error("异常信息： {}", e);
        }

        return addedNum > 0 ? "成功":"失败";
    }
}
