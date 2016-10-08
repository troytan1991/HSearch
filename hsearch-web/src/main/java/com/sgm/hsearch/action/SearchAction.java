package com.sgm.hsearch.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sgm.hsearch.dto.SearchResultDto;
import com.sgm.hsearch.entity.HsDocument;
import com.sgm.hsearch.service.SearchFileService;


@Controller
@RequestMapping("/search")
public class SearchAction {

	@Autowired
	private SearchFileService searchFileService;
	
	@RequestMapping(value="getSearchResults/{category}/{searchStr}",method={RequestMethod.GET})
	public @ResponseBody
	List<SearchResultDto> getSearchResults(@PathVariable String category,@PathVariable String searchStr,HttpServletRequest request,
			HttpServletResponse response){
		System.out.println("getsearch excuted "+ searchStr+" "+category);
		List<SearchResultDto> list = searchFileService.searchFile(category, searchStr);
		return list;
	}
	
	@RequestMapping(value="getDocDetail/{docId}",method={RequestMethod.GET})
	public @ResponseBody
	HsDocument getDocDetail(@PathVariable Long docId,HttpServletRequest request,
			HttpServletResponse response){
		System.out.println("getDoc excuted "+ docId);
		
		return searchFileService.getDocument(docId);
	}
	private List<SearchResultDto> mockResults(){
		List<SearchResultDto> list= new ArrayList<SearchResultDto>();
		for(int i=0;i<10;i++){
//			SearchResultDto resultDto = new SearchResultDto();
//			resultDto.setId((long)i);
//			resultDto.setTitle("�����������ĵ�"+ i + "ƪ<font color='red'>�ĵ�</font>");
//			resultDto.setHighlightText("���еĵ�"+ i+"������,<font color='red'>��ǿ��������</strong>");
//			resultDto.setPath("E:/docs/�ĵ�"+i+".docx");
//			resultDto.setUpdatedOn("2015��");
//			resultDto.setVisitCount(new Random().nextInt(10));
//			list.add(resultDto);
		}
		
		return list;
	}
}
