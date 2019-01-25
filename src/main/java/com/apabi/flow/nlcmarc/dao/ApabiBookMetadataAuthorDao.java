package com.apabi.flow.nlcmarc.dao;

import com.apabi.flow.nlcmarc.model.ApabiBookMetadataAuthor;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ApabiBookMetadataAuthorDao {
    ApabiBookMetadataAuthor findById(String id);
    void deleteById(String id);
    void insert(ApabiBookMetadataAuthor apabiBookMetadataAuthor);
    void update(ApabiBookMetadataAuthor apabiBookMetadataAuthor);
    Integer count();
    List<ApabiBookMetadataAuthor> findByNlcMarcIdentifierOrderByPriority(String nlcMarcIdentifier);
    List<ApabiBookMetadataAuthor> findByNlcMarcIdentifier(String nlcMarcIdentifier);
}