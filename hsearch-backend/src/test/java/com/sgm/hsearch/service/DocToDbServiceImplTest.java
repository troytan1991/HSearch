package com.sgm.hsearch.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.sgm.hsearch.entity.ExecuteResult;
import com.sgm.hsearch.entity.HsDocument;

@DirtiesContext
@ContextConfiguration(locations = {"/applicationContext.xml"})
@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=false)
public class DocToDbServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{

	@Autowired
	private DocToDbService doc2DbService;
	
	@Test
	public void testDocToDb() {
		ExecuteResult executeResult = doc2DbService.docToDb("E:/java/git/file/docs/����");
		System.out.println(executeResult);
	}

}
