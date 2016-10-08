package com.sgm.hsearch.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@DirtiesContext
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class IndexServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private IndexService indexService;
	
	@Test
	public void TestIndexDocs(){
		indexService.indexDbDocs();
	}
}
