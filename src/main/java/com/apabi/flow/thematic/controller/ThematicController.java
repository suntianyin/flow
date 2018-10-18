package com.apabi.flow.thematic.controller;

import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.thematic.model.ThematicSeries;
import com.apabi.flow.thematic.model.ThematicSeriesData;
import com.apabi.flow.thematic.service.ThematicSeriesDataService;
import com.apabi.flow.thematic.service.ThematicSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author pipi
 * @Date 2018/8/21 15:37
 **/
@Controller
@RequestMapping("/thematic")
public class ThematicController {
    @Autowired
    private ThematicSeriesService thematicSeriesService;

    @Autowired
    private ThematicSeriesDataService thematicSeriesDataService;

    @RequestMapping("/thematicAdd")
    public String thematicAdd() {
        return "thematic/thematicAdd";
    }

    // 录入论题信息
    @RequestMapping(value = "/thematicSave", method = RequestMethod.POST)
    public String thematicSave(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        ThematicSeries thematicSeries = new ThematicSeries();
        thematicSeries.setId(UUIDCreater.nextId());
        thematicSeries.setDataSource(parameterMap.get("dataSource")[0]);
        thematicSeries.setCreateTime(new Date());
        thematicSeries.setCollator(parameterMap.get("collator")[0]);
        thematicSeries.setTitle(parameterMap.get("title")[0]);
        thematicSeries.setUpdateTime(new Date());
        thematicSeries.setOperator(parameterMap.get("operator")[0]);
        thematicSeriesService.addThematicSeries(thematicSeries);
        return "thematicInfoDisplay";
    }

    // 展示所有论题信息
    @RequestMapping("/thematicInfoDisplay")
    public String thematicInfo(Model model) {
        List<ThematicSeries> thematicSeriesList = thematicSeriesService.findAllThematicSeries();
        model.addAttribute("thematicSeriesList", thematicSeriesList);
        return "thematic/thematicInfoDisplay";
    }

    // 查看论题信息
    @RequestMapping("/thematicCheck")
    public String thematicCheck(@RequestParam("id") String id, Model model) {
        List<ThematicSeriesData> thematicSeriesDataList = thematicSeriesDataService.findAllThematicSeriesDataByThematicSeriesId(id);
        model.addAttribute("thematicSeriesDataList", thematicSeriesDataList);
        return "thematic/thematicDataDetail";
    }

    @RequestMapping("/thematicEdit")
    public String thematicEdit(@RequestParam("id") String id, Model model) {
        ThematicSeries thematicSeries = thematicSeriesService.findThematicSeriesById(id);
        model.addAttribute("thematicSeries", thematicSeries);
        return "thematic/thematicEdit";
    }

    // 编辑论题信息并保存
    @RequestMapping(value = "/thematicEditSave", method = RequestMethod.POST)
    public String thematicEditSave(HttpServletRequest request, Model model) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        String id = parameterMap.get("id")[0];
        ThematicSeries thematicSeries = thematicSeriesService.findThematicSeriesById(id);
        thematicSeries.setDataSource(parameterMap.get("dataSource")[0]);
        thematicSeries.setCollator(parameterMap.get("collator")[0]);
        thematicSeries.setTitle(parameterMap.get("title")[0]);
        thematicSeries.setUpdateTime(new Date());
        thematicSeries.setOperator(parameterMap.get("operator")[0]);
        thematicSeriesService.updateThematic(thematicSeries);
        List<ThematicSeries> thematicSeriesList = thematicSeriesService.findAllThematicSeries();
        model.addAttribute("thematicSeriesList", thematicSeriesList);
        return "thematic/thematicInfoDisplay";
    }

    // 上传excel文件
    @RequestMapping(value = "/upload")
    @ResponseBody
    public void thematicUploadExcel(@RequestParam("file") MultipartFile file, @RequestParam("id") String id) throws IOException {
        thematicSeriesService.addThematicSeriesDataFromExcel(file, id);
        System.out.println(file.getOriginalFilename());
        System.out.println(id);
    }

    /*@RequestMapping("/thematicInfoDisplayByPage")
    public String thematicSeriesDataByPage(@RequestParam("id") String id, Model model) {
        PageInfo<ThematicSeriesData> pageInfo = thematicSeriesDataService.findThematicSeriesDataByIdPage(id);
        model.addAttribute("page", pageInfo);
        List<ThematicSeriesData> list = pageInfo.getList();
        for (ThematicSeriesData thematicSeriesData : list) {
            System.out.println(thematicSeriesData);
        }
        return "thematic/thematicDataDetail";
    }*/
}
