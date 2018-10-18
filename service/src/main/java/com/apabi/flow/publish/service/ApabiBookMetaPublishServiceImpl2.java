package com.apabi.flow.publish.service;

import com.apabi.flow.publish.dao.ApabiBookMetaPublishDao;
import com.apabi.flow.publish.dao.ApabiBookMetaTempPublishDao;
import com.apabi.flow.publish.model.ApabiBookMetaPublish2;
import com.apabi.flow.publish.model.ApabiBookMetaTempPublish2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author pipi
 * @Date 2018/8/15 17:20
 **/
@Service
public class ApabiBookMetaPublishServiceImpl2 implements ApabiBookMetaPublishService2 {

    @Autowired
    private ApabiBookMetaTempPublishDao apabiBookMetaTempPublishDao;

    @Autowired
    private ApabiBookMetaPublishDao apabiBookMetaPublishDao;

    @Override
    public Page<ApabiBookMetaTempPublish2> queryPage(Map queryMap, int pageNumber, int pageSize) {
        //查询字段
        String tmp;
        //获取图书id
        if (queryMap.get("metaId") != null) {
            tmp = queryMap.get("metaId").toString();
        } else {
            tmp = "";
        }
        final String metaId = tmp;
        //获取书名
        if (queryMap.get("title") != null) {
            tmp = queryMap.get("title").toString();
        } else {
            tmp = "";
        }
        final String title = tmp;
        //获取作者
        if (queryMap.get("creator") != null) {
            tmp = queryMap.get("creator").toString();
        } else {
            tmp = "";
        }
        final String creator = tmp;
        //获取出版社
        if (queryMap.get("publisher") != null) {
            tmp = queryMap.get("publisher").toString();
        } else {
            tmp = "";
        }
        final String publisher = tmp;
        //获取isbn值
        if (queryMap.get("isbnVal") != null) {
            tmp = queryMap.get("isbnVal").toString();
        } else {
            tmp = "";
        }
        final String isbnVal = tmp;
        //获取isbn类型
        if (queryMap.get("isbn") != null) {
            tmp = queryMap.get("isbn").toString();
        } else {
            tmp = "";
        }
        final String isbn = tmp;
        //构造hasPublish
        if (queryMap.get("hasPublish") != null) {
            tmp = queryMap.get("hasPublish").toString();
        } else {
            tmp = "0";
        }
        final String hasPublish = tmp;
        //构造分页
        Sort sort = new Sort(Sort.Direction.ASC, "hasPublish");
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Specification<ApabiBookMetaTempPublish2> spec = (Specification<ApabiBookMetaTempPublish2>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 设置仅仅展示hasPublish=0的数据
            if (!StringUtils.isEmpty(metaId)) {
                predicates.add(criteriaBuilder.equal(root.get("metaId"), metaId));
            }
            if (!StringUtils.isEmpty(title)) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            }
            if (!StringUtils.isEmpty(creator)) {
                predicates.add(criteriaBuilder.like(root.get("creator"), "%" + creator + "%"));
            }
            if (!StringUtils.isEmpty(publisher)) {
                predicates.add(criteriaBuilder.like(root.get("publisher"), "%" + publisher + "%"));
            }
            if (isbn.equals("isbn")) {
                if (!StringUtils.isEmpty(isbnVal)) {
                    predicates.add(criteriaBuilder.equal(root.get("isbn"), isbnVal));
                }
            } else if (isbn.equals("isbn10")) {
                if (!StringUtils.isEmpty(isbnVal)) {
                    predicates.add(criteriaBuilder.equal(root.get("isbn10"), isbnVal));
                }
            } else if (isbn.equals("isbn13")) {
                if (!StringUtils.isEmpty(isbnVal)) {
                    predicates.add(criteriaBuilder.equal(root.get("isbn13"), isbnVal));
                }
            }
            Predicate and = criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            return and;
        };
//        Page<ApabiBookMetaTempPublish2> pages = apabiBookMetaTempPublishDao.f;
//        return pages;
        return null;
    }

    // 根据metaId查询temp库中的数据
    @Override
    public ApabiBookMetaTempPublish2 findApabiBookMetaTempPublish(String metaId) {
        if (!StringUtils.isEmpty(metaId)) {
            ApabiBookMetaTempPublish2 apabiBookMetaTempPublish = apabiBookMetaTempPublishDao.findApabiBookMetaTempPublishByMetaIdIs(metaId);
            return apabiBookMetaTempPublish;
        }
        return null;
    }

    // 根据metaId查询meta_data库中的数据
    public ApabiBookMetaPublish2 findApabiBookMetaPublish(String metaId) {
        if (!StringUtils.isEmpty(metaId)) {
            return apabiBookMetaPublishDao.findApabiBookMetaPublishByMetaIdIs(metaId);
        }
        return null;
    }

    // 如果发布数据，则将hasPublish字段改为1
    @Override
    public void publishApabiBookMetaTemp(String metaId) {
        if (!StringUtils.isEmpty(metaId)) {
            // 查询temp库中的数据
            ApabiBookMetaTempPublish2 apabiBookMetaTempPublish = apabiBookMetaTempPublishDao.findApabiBookMetaTempPublishByMetaIdIs(metaId);
            // 对temp库中的hasPublish字段改为1
            apabiBookMetaTempPublish.setHasPublish(1);
            apabiBookMetaTempPublishDao.saveAndFlush(apabiBookMetaTempPublish);
            // 查询meta_data库中的数据
            ApabiBookMetaPublish2 apabiBookMetaPublish = apabiBookMetaPublishDao.findApabiBookMetaPublishByMetaIdIs(metaId);
            // hasCebx和hasFlow字段不做更改
            Integer hasCebx = 0;
            Integer hasFlow = 0;
            if (apabiBookMetaPublish != null) {
                hasCebx = apabiBookMetaPublish.getHasCebx();
                hasFlow = apabiBookMetaPublish.getHasFlow();
            }
            if (apabiBookMetaPublish == null) {
                apabiBookMetaPublish = new ApabiBookMetaPublish2();
            }
            // 将属性进行拷贝
            BeanUtils.copyProperties(apabiBookMetaTempPublish, apabiBookMetaPublish);
            if (apabiBookMetaTempPublish.getHasCebx() == null) {
                apabiBookMetaTempPublish.setHasCebx(0);
            }
            if (apabiBookMetaTempPublish.getHasFlow() == null) {
                apabiBookMetaTempPublish.setHasFlow(0);
            }
            System.out.println(apabiBookMetaPublish);
            // 将数据插入到meta_data库中
            apabiBookMetaPublish.setUpdateTime(new Date());
            // 不更改hasCebx和hasFlow，利用meta_data原始的值
            apabiBookMetaPublish.setHasCebx(hasCebx == null ? 0 : hasCebx);
            apabiBookMetaPublish.setHasFlow(hasFlow == null ? 0 : hasFlow);
            apabiBookMetaPublish.setHasPublish(1);
            apabiBookMetaPublishDao.saveAndFlush(apabiBookMetaPublish);


        }
    }

    @Override
    public void updateApabiBookMetaTemp(ApabiBookMetaTempPublish2 apabiBookMetaTempPublish) {
        apabiBookMetaTempPublish.setHasPublish(0);
        apabiBookMetaTempPublishDao.saveAndFlush(apabiBookMetaTempPublish);
    }

    @Override
    public void addApabiBookMetaTemp(ApabiBookMetaPublish2 apabiBookMetaPublish2) {
        apabiBookMetaPublishDao.saveAndFlush(apabiBookMetaPublish2);
    }
}
