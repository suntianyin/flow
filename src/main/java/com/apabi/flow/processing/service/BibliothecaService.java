package com.apabi.flow.processing.service;

import com.apabi.flow.processing.constant.BibliothecaStateEnum;
import com.apabi.flow.processing.constant.BizException;
import com.apabi.flow.processing.constant.CompletedFlagEnum;
import com.apabi.flow.processing.constant.DuplicateFlagEnum;
import com.apabi.flow.processing.model.Bibliotheca;
import com.apabi.flow.processing.model.BibliothecaExcelModel;
import com.apabi.flow.processing.model.DuplicationCheckEntity;
import com.github.pagehelper.Page;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 功能描述： <br>
 * <书目 Service>
 *
 * @author supeng
 * @date 2018/9/13 10:49
 * @since 1.0.0
 */
public interface BibliothecaService {

    /**
     * 根据 书目 id 逻辑删除当前批次
     * 暂时不提供逻辑删除的功能
     *
     * @param id
     * @return
     */
    boolean deleteBibliothecaById(String id);

    /**
     * 添加书目，需要做数据校验，比如重复添加等，条件一般为 batchId + isbn，
     *
     * @param bibliothecaList
     * @return
     */
    boolean addBibliothecaList(List<Bibliotheca> bibliothecaList) throws Exception;

    /**
     * 根据主键获取书目实体信息
     *
     * @param id
     * @return
     */
    Bibliotheca getBibliothecaById(String id);

    /**
     * 查询所有特定条件下书目的列表信息
     *
     * @param map
     * @return
     */
    List<Bibliotheca> listBibliotheca(Map map);

    /**
     * 获取分页书目列表信息
     *
     * @param map
     * @return
     */
    Page<Bibliotheca> listBibliothecaPage(Map map);

    /**
     * 更新书目信息
     *
     * @param bibliotheca
     * @return
     */
    int updateBibliotheca(Bibliotheca bibliotheca) throws Exception;

    /**
     * 对书目信息进行批处理
     *
     * @param bibliothecaList
     * @return
     */
    void listUpdateBibliotheca(List<Bibliotheca> bibliothecaList) throws Exception;

    /**
     * 获取对比书目重复的列表
     *
     * @param batchId 批次ID
     * @return
     */
    List<DuplicationCheckEntity> listDuplicationCheckEntity(String batchId) throws Exception;

    /**
     * 查重信息批量处理
     *
     * @param bibliothecaIdList
     * @param metaIdList
     * @param bibliothecaState
     * @param duplicateFlag
     */
    void listSureOperation(List<String> bibliothecaIdList, List<String> metaIdList, String dataType, String btnType,
                           BibliothecaStateEnum bibliothecaState, DuplicateFlagEnum duplicateFlag) throws Exception;

    /**
     * 解析文件，得到 书目列表
     *
     * @param data
     * @param batchId
     * @return
     */
    List<Bibliotheca> resolveBibliothecaData(Map<Integer, Map<Object, Object>> data, String batchId) throws Exception;

    /**
     * 将 数据写入到 Excel 表中
     *
     * @param fileName
     * @param excelTitle
     * @param excelModelList
     * @param sheet1
     * @param
     * @return
     */
    String writeData2Excel(int type, String fileName, String[] excelTitle, List<BibliothecaExcelModel> excelModelList, String sheet1, HttpServletResponse response) throws Exception;


    void parsing(String path, String id, String username, String batchId) throws InterruptedException;

    //转换pdf文件为cebx
    //void batchConvert2Cebx(String dirPath, String batchId, String fileInfos);

    //批量转换文件控制
    boolean ctlBatchConvert2Cebx(String dirPath, String batchId, String fileInfos);
}
