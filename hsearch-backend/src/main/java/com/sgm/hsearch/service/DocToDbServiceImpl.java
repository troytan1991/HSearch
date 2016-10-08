package com.sgm.hsearch.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sgm.hsearch.dao.HsDocumentMapper;
import com.sgm.hsearch.entity.ExecuteResult;
import com.sgm.hsearch.entity.HsDocument;
import com.sgm.hsearch.utils.FileService;

@Service
public class DocToDbServiceImpl implements DocToDbService {
	
	private static Logger logger = LoggerFactory.getLogger(DocToDbServiceImpl.class);
	@Autowired
	private HsDocumentMapper documentMapper;
	private ExecuteResult executeResult;	//global commit result
	
	//rollback when db commit interrupt
	@Transactional
	public ExecuteResult docToDb(String path) {
		executeResult = new ExecuteResult();	//clean object
		Path docDir = Paths.get(path);			//nio get dir
		if (Files.isDirectory(docDir)) {		//continue only directory
			setDocDisable();					//disable all db docs, for recording delete num
			try {
				Files.walkFileTree(docDir, new SimpleFileVisitor<Path>() {	//nio loop through all files in directory
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						if (!Files.isDirectory(file)) {
							File f = file.toFile();				//to get file properties
							if (!f.getName().startsWith("~")) {			//exclude cache file
								HsDocument document = new HsDocument();
								document.setContent(new FileService().readFile(f));
								document.setPath(f.getAbsolutePath());
								document.setTitle(f.getName().substring(0, f.getName().lastIndexOf(".")));	//get title from path
								document.setUpdatedOn(new Date(f.lastModified()));
								document.setClassfy(file.getName(2).toString());
								document.setCreatedBy("init");
								document.setVisitCount(0l);
								logger.info("push doc:"+document.getPath());
								pushDoc(document);			//update or insert doc
							}	
						} else {
							docToDb(file.toString());	//iterate if dir
						}
						return FileVisitResult.CONTINUE;

					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		getDisableDocs();		//calculate delete doc
		return executeResult;
	}
	/**
	 * disable all docs to record delete info
	 */
	private void setDocDisable(){					
		documentMapper.disableDocs();
	}
	/**
	 * select all disabled docs, coordinate with method "setDocDisable"
	 */
	private void getDisableDocs(){
		List<String> list = documentMapper.selectDisableDocs();
		executeResult.setDeleteNum(list.size());
	}
	
	/**
	 *push doc into db, include update、insert 
	 * @param document
	 */
	private void pushDoc(HsDocument document){
		HsDocument hsDocument = documentMapper.selectByPath(document.getPath());
		//更新
		if(hsDocument != null){
			Date updateOn = hsDocument.getUpdatedOn();
			if(updateOn!=null && (document.getUpdatedOn().getTime()-updateOn.getTime())>1000){	//needn't update condition
				logger.info(document.getTitle());
				documentMapper.enableDoc(hsDocument.getId());								//enable doc
			}else{																		//update doc
				document.setEnable(true);
				document.setVisitCount(hsDocument.getVisitCount());							//keep visit info
				document.setId(hsDocument.getId());
				logger.info("update doc:"+document.getTitle());
				int result = documentMapper.updateByPrimaryKey(document);
				if(result>0){
					executeResult.setUpdateNum(executeResult.getUpdateNum()+1);
					executeResult.setSuccessNum(executeResult.getSuccessNum()+1);
				}else{
					executeResult.setFailNum(executeResult.getFailNum()+1);
				}
			}
		}else{																			//insert doc
			document.setEnable(true);
			logger.info("insert doc:"+document.getTitle());
			int result = documentMapper.insert(document);
			if(result>0){
				executeResult.setAddNum(executeResult.getAddNum()+1);
				executeResult.setSuccessNum(executeResult.getSuccessNum()+1);
			}else{
				executeResult.setFailNum(executeResult.getFailNum()+1);
			}
		}
	}
}
