package com.apabi.flow.video.dao;


import com.apabi.flow.publisher.model.Publisher;
import com.apabi.flow.video.model.VideoMeta;
import com.github.pagehelper.Page;

import java.util.List;

public interface VideoMetaMapper {
    Page<VideoMeta> queryPage();

    int addVideoMeta(VideoMeta videoMeta);

    VideoMeta selectdataById(String id);

    int updateVideoMeta(VideoMeta videoMeta);

    List<VideoMeta> findAll();

    int deleteByPrimaryKey(String id);
}