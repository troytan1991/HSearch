package com.sgm.hsearch.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.sgm.hsearch.dto.SearchResultDto;


@DirtiesContext
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class SearchFileServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private SearchFileService searchFileService;
	
	@Test
	public void TestSearchFile(){
		List<SearchResultDto> list = searchFileService.searchFile(null, "tt test");
		System.out.println(list.size());
	}
	
	@Test
	public void testOutAllFiles(){
		searchFileService.outAllFiles();
	}
}
