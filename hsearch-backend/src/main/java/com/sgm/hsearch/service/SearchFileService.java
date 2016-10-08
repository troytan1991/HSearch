package com.sgm.hsearch.service;

import java.util.List;

import com.sgm.hsearch.dto.SearchResultDto;
import com.sgm.hsearch.entity.HsDocument;

public interface SearchFileService {

	List<SearchResultDto> searchFile(String category,String keyStr);
	HsDocument getDocument(Long id);
	void outAllFiles();
}
