package com.apabi.flow.processing.service.impl;

import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.processing.constant.BatchStateEnum;
import com.apabi.flow.processing.constant.BibliothecaStateEnum;
import com.apabi.flow.processing.dao.BatchMapper;
import com.apabi.flow.processing.dao.BibliothecaMapper;
import com.apabi.flow.processing.model.Batch;
import com.apabi.flow.processing.service.BatchService;
import com.github.pagehelper.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述： <br>
 * <批次Service 实现层>
 *
 * @author supeng
 * @date 2018/9/5 14:00
 * @since 1.0.0
 */
@Service
public class BatchServiceImpl implements BatchService {

    @Autowired
    private BatchMapper batchMapper;

    @Autowired
    private BibliothecaMapper bibliothecaMapper;

    /**
     * 根据 批次 id 逻辑删除当前批次
     * 暂时不提供逻辑删除的功能
     *
     * @param id
     * @return
     */
    @Override
    public boolean deleteBatchById(String id) {
        return false;
    }

    /**
     * 添加批次，需要做数据校验，比如重复添加等
     *
     * @param batch
     * @return
     */
    @Override
    public boolean addBatch(Batch batch) {
        if (batch != null && StringUtils.isNotBlank(batch.getId())){
            return batchMapper.insert(batch) == 1;
        }
        return false;
    }

    /**
     * 根据主键获取批次实体信息
     *
     * @param id
     * @return
     */
    @Override
    public Batch getBatchById(String id) {
        return batchMapper.selectByPrimaryKey(id);
    }

    @Override
    public Batch selectByBatchId(String batchId) {
        return batchMapper.selectByBatchId(batchId);
    }

    /**
     * 查询所有批次的列表信息
     *
     * @param map
     * @return
     */
    @Override
    public List<Batch> listBatch(Map map) {
        return batchMapper.listBatchSelective(map);
    }

    /**
     * 获取分页批次列表信息
     *
     * @param map
     * @return
     */
    @Override
    public Page<Batch> listBatchPage(Map map) {
        return batchMapper.listBatchSelectiveByPage(map);
    }

    /**
     * 更新批次信息
     *
     * @param batch
     * @return
     */
    @Override
    public int updateBatch(Batch batch) {
        return batchMapper.updateByPrimaryKeySelective(batch);
    }

    /**
     * 修改批次状态和书目状态，书目状态修改是 基于前一个状态
     * 超时时间为 6s
     *
     * @param batch
     * @param currentState
     * @param nextState
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, timeout = 6)
    public boolean updateBatchStateAndBibliothecaState(Batch batch, BibliothecaStateEnum currentState, BibliothecaStateEnum nextState) throws Exception {
        //根据条件更新

        if (batchMapper.updateByPrimaryKeySelective(batch) != 1){
            throw new Exception("批次更新失败");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("batchId", batch.getBatchId());
        map.put("currentState", currentState);
        map.put("nextState", nextState);
        map.put("updateTime", new Date());
        try {
            bibliothecaMapper.updateByBatchIdAndState(map);
            return true;
        }catch (Exception e){
            throw new Exception(e);
        }

    }

    @Override
    public int updateStateByPrimaryKey(Batch record) {
        return batchMapper.updateStateByPrimaryKey(record);
    }

}
