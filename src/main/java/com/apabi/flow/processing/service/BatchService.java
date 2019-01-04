package com.apabi.flow.processing.service;

import com.apabi.flow.processing.constant.BatchStateEnum;
import com.apabi.flow.processing.constant.BibliothecaStateEnum;
import com.apabi.flow.processing.model.Batch;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

/**
 * 功能描述： <br>
 * <批次加工>
 *
 * @author supeng
 * @date 2018/9/5 13:27
 * @since 1.0.0
 */
public interface BatchService {

    /**
     * 根据 批次 id 逻辑删除当前批次
     * 暂时不提供逻辑删除的功能
     * @param id
     * @return
     */
    @Deprecated
    boolean deleteBatchById(String id);

    /**
     * 添加批次，需要做数据校验，比如重复添加等
     * @param batch
     * @return
     */
    boolean addBatch(Batch batch);

    /**
     * 根据主键获取批次实体信息
     * @param id
     * @return
     */
    Batch getBatchById(String id);
    /**
     * 根据主键获取批次实体信息
     * @param batchId
     * @return
     */
    Batch selectByBatchId(String batchId);



    /**
     * 查询所有批次的列表信息
     * @param map
     * @return
     */
    List<Batch> listBatch(Map map);

    /**
     * 获取分页批次列表信息
     * @param map
     * @return
     */
    Page<Batch> listBatchPage(Map map);

    /**
     * 更新批次信息
     * @param batch
     * @return
     */
    int updateBatch(Batch batch);

    /**
     * 修改批次状态和书目状态，书目状态修改是 基于前一个状态
     * @param batch
     * @param currentState
     * @param nextState
     * @return
     */
    boolean updateBatchStateAndBibliothecaState(Batch batch, BibliothecaStateEnum currentState, BibliothecaStateEnum nextState)throws Exception ;

    int updateStateByPrimaryKey(Batch record);

}
