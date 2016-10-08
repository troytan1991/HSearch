package com.sgm.hsearch.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sgm.hsearch.entity.ExecuteResult;
import com.sgm.hsearch.service.DocToDbService;
import com.sgm.hsearch.service.IndexService;

@Controller
@RequestMapping("/indexManage")
public class IndexManageAction {
	@Autowired
	private IndexService indexService;
	@Autowired
	private DocToDbService docToDbService;
	
	
	@RequestMapping(value = "/initIndex", method={RequestMethod.GET})
	public @ResponseBody String initIndex(HttpServletRequest request, HttpServletResponse response){
		indexService.cleanIndex();
		int updateNum = indexService.indexDbDocs();
		return "successful update index records:"+updateNum;
	}
	
	@RequestMapping(value = "/storeDocs", method={RequestMethod.GET})
	public @ResponseBody ExecuteResult storeDocs(@RequestParam("docPath") String docPath,HttpServletRequest request, HttpServletResponse response){
		ExecuteResult executeResult = docToDbService.docToDb(docPath);
		return executeResult;
	}
}
