package com.sgm.hsearch.dao;

import com.sgm.hsearch.entity.HsDocument;
import java.util.List;

public interface HsDocumentMapper {
    int deleteByPrimaryKey(Long id);
    int insert(HsDocument record);
    HsDocument selectByPrimaryKey(Long id);
    HsDocument selectByPath(String path);
    List<HsDocument> selectAll();
    List<String> selectDisableDocs();
    int updateByPrimaryKey(HsDocument record);
    void addVisitCount(Long id);
    void disableDocs();
    void enableDoc(Long id);
}