package com.apabi.flow.audio.dao;

import com.apabi.flow.audio.model.AudioMeta;
import com.apabi.flow.video.model.VideoMeta;
import com.github.pagehelper.Page;

import java.util.List;

public interface AudioMetaMapper {

    Page<AudioMeta> queryPage();

    int addAudioMeta(AudioMeta audioMeta);

    AudioMeta selectdataById(String id);

    int updateAudioMeta(AudioMeta audioMeta);

    List<AudioMeta> findAll();

    int deleteByPrimaryKey(String id);
}