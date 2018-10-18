package com.apabi.flow.book.model.mapper;

import com.apabi.flow.book.model.EpubookMeta;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author guanpp
 * @date 2018/8/10 13:36
 * @description
 */
public class EpubookMetaMapper implements RowMapper<EpubookMeta> {
    //rs为返回结果集，以每行为单位封装着
    public EpubookMeta mapRow(ResultSet rs, int rowNum) throws SQLException {
        EpubookMeta epubookMeta = new EpubookMeta();
        epubookMeta.setMetaid(rs.getString("metaid"));
        epubookMeta.setCreatetime(rs.getDate("createtime"));
        epubookMeta.setUpdatetime(rs.getDate("updatetime"));
        epubookMeta.setIssueddate(rs.getString("issueddate"));
        epubookMeta.setTitle(rs.getString("title"));
        epubookMeta.setCreator(rs.getString("creator"));
        epubookMeta.setSummary(rs.getString("abstract"));
        epubookMeta.setPublisher(rs.getString("publisher"));
        epubookMeta.setIsbn(rs.getString("isbn"));
        epubookMeta.setContentNum(rs.getInt("contentNum"));
        epubookMeta.setType(rs.getString("type"));
        epubookMeta.setIsoptimize(rs.getInt("isoptimize"));
        epubookMeta.setLanguage(rs.getString("language"));
        epubookMeta.setStreamCatalog(rs.getString("streamCatalog"));
        epubookMeta.setCoverUrl(rs.getString("coverUrl"));
        epubookMeta.setChapterNum(rs.getInt("chapterNum"));
        epubookMeta.setStyleUrl(rs.getString("styleUrl"));
        epubookMeta.setThumimgUrl(rs.getString("thumimgUrl"));
        epubookMeta.setStyleClass(rs.getString("styleClass"));
        return epubookMeta;
    }

}