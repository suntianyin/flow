package com.apabi.flow.publish.service;

import com.alibaba.fastjson.JSON;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.douban.dao.ApabiBookMetaDataTempDao;
import com.apabi.flow.douban.model.ApabiBookMetaData;
import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import com.apabi.flow.douban.util.StringToolUtil;
import com.apabi.flow.publish.dao.PublishResultDao;
import com.apabi.flow.publish.model.PublishResult;
import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * @Author pipi
 * @Date 2018/8/15 17:20
 **/
@Service
public class ApabiBookMetaPublishServiceImpl implements ApabiBookMetaPublishService {

    @Autowired
    private ApabiBookMetaDataTempDao apabiBookMetaDataTempDao;

    @Autowired
    private ApabiBookMetaDataDao apabiBookMetaDataDao;

    @Autowired
    private PublishResultDao publishResultDao;

    /**
     * 查询数据
     *
     * @param queryMap
     * @return
     */
    @Override
    public Page<ApabiBookMetaDataTemp> queryPage(Map<String, Object> queryMap) {
        return apabiBookMetaDataTempDao.findByPage(queryMap);
    }

    /**
     * 根据metaId查询temp库中的数据
     *
     * @param metaId
     * @return
     */
    @Override
    public ApabiBookMetaDataTemp findApabiBookMetaTempPublish(String metaId) {
        if (!StringUtils.isEmpty(metaId)) {
            ApabiBookMetaDataTemp apabiBookMetaDataTemp = apabiBookMetaDataTempDao.findById(metaId);
            return apabiBookMetaDataTemp;
        }
        return null;
    }

    /**
     * 根据metaId查询meta_data库中的数据
     *
     * @param metaId
     * @return
     */
    @Override
    public ApabiBookMetaData findApabiBookMetaPublish(String metaId) {
        if (!StringUtils.isEmpty(metaId)) {
            return apabiBookMetaDataDao.findById(metaId);
        }
        return null;
    }

    /**
     * 如果发布数据，则将hasPublish字段改为1
     *
     * @param metaId
     */
    @Override
    public void publishApabiBookMetaTemp(String metaId) {
        if (!StringUtils.isEmpty(metaId)) {
            // 查询temp库中的数据
            ApabiBookMetaDataTemp apabiBookMetaTempPublish = apabiBookMetaDataTempDao.findById(metaId);
            // 对temp库中的hasPublish字段改为1
            apabiBookMetaTempPublish.setHasPublish(1);
            apabiBookMetaDataTempDao.update(apabiBookMetaTempPublish);
            // 查询meta_data库中的数据
            ApabiBookMetaData apabiBookMetaPublish = apabiBookMetaDataDao.findById(metaId);
            // 记录更新apabi_book_metadata表前的的数据内容
            ApabiBookMetaData preApabiBookMetaPublish = apabiBookMetaPublish;
            // hasCebx和hasFlow字段不做更改
            Integer hasCebx = 0;
            Integer hasFlow = 0;
            Date createTime = null;
            String issueddate = null;
            if (apabiBookMetaPublish != null) {
                hasCebx = apabiBookMetaPublish.getHasCebx();
                hasFlow = apabiBookMetaPublish.getHasFlow();
                createTime = apabiBookMetaPublish.getCreateTime();
                issueddate = apabiBookMetaPublish.getIssuedDate();
            } else {
                createTime = new Date();
            }
            if (apabiBookMetaPublish == null) {
                apabiBookMetaPublish = new ApabiBookMetaData();
            }
            // 将属性进行拷贝
            BeanUtils.copyProperties(apabiBookMetaTempPublish, apabiBookMetaPublish);
            if (apabiBookMetaTempPublish.getHasCebx() == null) {
                apabiBookMetaTempPublish.setHasCebx(0);
            }
            if (apabiBookMetaTempPublish.getHasFlow() == null) {
                apabiBookMetaTempPublish.setHasFlow(0);
            }
            if(!StringUtils.isEmpty(apabiBookMetaTempPublish.getIssuedDate())){
                issueddate = StringToolUtil.issuedDateFormat(apabiBookMetaTempPublish.getIssuedDate()).replaceAll(" 00:00:00","" );
            }
            apabiBookMetaPublish.setIssuedDate(issueddate);
            System.out.println(apabiBookMetaPublish);
            // 将数据插入到meta_data库中
            apabiBookMetaPublish.setUpdateTime(new Date());
            // 不更改hasCebx和hasFlow，利用meta_data原始的值
            apabiBookMetaPublish.setHasCebx(hasCebx == null ? 0 : hasCebx);
            apabiBookMetaPublish.setHasFlow(hasFlow == null ? 0 : hasFlow);
            apabiBookMetaPublish.setHasPublish(1);

            // 对于meta库中已经存在的数据，不修改createTime字段
            apabiBookMetaPublish.setCreateTime(createTime);
            ApabiBookMetaData byId = apabiBookMetaDataDao.findById(metaId);
            if (byId == null) {
                apabiBookMetaDataDao.insert(apabiBookMetaPublish);
            } else {
                apabiBookMetaDataDao.update(apabiBookMetaPublish);
                // 记录更新apabi_book_metadata表后的的数据内容
                ApabiBookMetaData postApabiBookMetaPublish = apabiBookMetaPublish;
                PublishResult publishResult = new PublishResult();
                publishResult.setId(UUIDCreater.nextId());
                publishResult.setOperateDataType("book");
                publishResult.setMetaId(apabiBookMetaTempPublish.getMetaId());
                // 获取当前用户名
                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                String username = userDetails.getUsername();
                publishResult.setOperator(username);
                publishResult.setOperateTime(new Date());
                publishResult.setOperateResult("1");
                publishResult.setDataSource(apabiBookMetaTempPublish.getDataSource());
                String preContent = JSON.toJSONString(preApabiBookMetaPublish);
                String postContent = JSON.toJSONString(postApabiBookMetaPublish);
                publishResult.setPreContent(preContent);
                publishResult.setPostContent(postContent);
                publishResult.setCreateTime(new Date());
                publishResult.setUpdateTime(new Date());
                publishResult.setHasSync(0);
                publishResultDao.insertPublishResult(publishResult);
            }
        }
    }

    @Override
    public void updateApabiBookMetaTemp(ApabiBookMetaDataTemp apabiBookMetaTempPublish) {
        System.out.println(apabiBookMetaTempPublish);
        apabiBookMetaTempPublish.setHasPublish(0);
        apabiBookMetaDataTempDao.update(apabiBookMetaTempPublish);
    }
}