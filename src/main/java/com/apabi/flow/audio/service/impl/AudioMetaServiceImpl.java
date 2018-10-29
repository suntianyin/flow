package com.apabi.flow.audio.service.impl;

import com.apabi.flow.audio.dao.AudioMetaMapper;
import com.apabi.flow.audio.model.AudioMeta;
import com.apabi.flow.audio.service.AudioMetaService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: sunty
 * @date: 2018/10/24 14:03
 * @description:
 */
@Service
public class AudioMetaServiceImpl implements AudioMetaService {


    @Autowired
    AudioMetaMapper audioMetaMapper;


    @Override
    public Page<AudioMeta> queryPage() {
        return audioMetaMapper.queryPage();
    }

    @Override
    public int addAudioMeta(AudioMeta audioMeta) {
        return audioMetaMapper.addAudioMeta(audioMeta);
    }

    @Override
    public AudioMeta selectdataById(String id) {
        return audioMetaMapper.selectdataById(id);
    }

    @Override
    public int updateAudioMeta(AudioMeta audioMeta) {
        return audioMetaMapper.updateAudioMeta(audioMeta);
    }

    @Override
    public int deleteByPrimaryKey(String id) {
        return audioMetaMapper.deleteByPrimaryKey(id);
    }
}
