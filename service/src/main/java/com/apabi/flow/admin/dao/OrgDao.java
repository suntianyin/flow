package com.apabi.flow.admin.dao;

import com.apabi.flow.admin.model.Org;

import java.util.List;

public interface OrgDao {

    List<Org> queryAll();

    Org getById(Integer id);
}
