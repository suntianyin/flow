package com.apabi.flow.video.service.impl;

import com.apabi.flow.video.dao.VideoMetaMapper;
import com.apabi.flow.video.model.VideoMeta;
import com.apabi.flow.video.service.VideoMetaService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: sunty
 * @date: 2018/10/23 16:22
 * @description:
 */
@Service
public class VideoMetaServiceImpl implements VideoMetaService {

    @Autowired
    VideoMetaMapper videoMetaMapper;

    @Override
    public Page<VideoMeta> queryPage() {
        return videoMetaMapper.queryPage();
    }

    @Override
    public int addVideoMeta(VideoMeta videoMeta) {
        return videoMetaMapper.addVideoMeta(videoMeta);
    }


    @Override
    public VideoMeta selectdataById(String id) {
        return videoMetaMapper.selectdataById(id);
    }

    @Override
    public int updateVideoMeta(VideoMeta videoMeta) {
        return videoMetaMapper.updateVideoMeta(videoMeta);
    }

    @Override
    public int deleteByPrimaryKey(String id) {
        return videoMetaMapper.deleteByPrimaryKey(id);
    }
}
