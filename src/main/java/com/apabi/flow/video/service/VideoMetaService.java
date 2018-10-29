package com.apabi.flow.video.service;

import com.apabi.flow.publisher.model.Publisher;
import com.apabi.flow.video.model.VideoMeta;
import com.github.pagehelper.Page;

public interface VideoMetaService {
    //分页查询
    Page<VideoMeta> queryPage();

    //添加数据
    int addVideoMeta(VideoMeta videoMeta);

    //根据id查询数据
    VideoMeta selectdataById(String id);

    //编辑数据
    int updateVideoMeta(VideoMeta videoMeta);

    int deleteByPrimaryKey(String id);
}
