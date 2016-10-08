package com.sgm.hsearch.service;

import com.sgm.hsearch.entity.HsDocument;

public interface IndexService {

	int indexDbDocs();
	void cleanIndex();
	boolean indexDoc(HsDocument doc);
}
