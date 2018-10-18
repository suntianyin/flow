package com.apabi.flow.admin.service;

import com.apabi.flow.admin.model.Org;

import java.util.List;

public interface OrgService {

    /**
     * 全部查询
     */
    List<Org> queryAll();

    /**
     * 根据ID查询
     */
    Org getById(Integer id);
}
