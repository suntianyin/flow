package com.apabi.flow.audio.service;

import com.apabi.flow.audio.model.AudioMeta;
import com.github.pagehelper.Page;

public interface AudioMetaService {
    //分页查询
    Page<AudioMeta> queryPage();

    //添加数据
    int addAudioMeta(AudioMeta audioMeta);

    //根据id查询数据
    AudioMeta selectdataById(String id);

    //编辑数据
    int updateAudioMeta(AudioMeta audioMeta);

    int deleteByPrimaryKey(String id);
}
