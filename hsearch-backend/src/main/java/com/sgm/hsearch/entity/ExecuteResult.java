package com.sgm.hsearch.entity;

import java.util.List;

public class ExecuteResult {
	private List<String> updateDocs;
	private List<String> insertDocs;
	private List<String> deleteDocs;
	private List<String> failDocs;
	
	private int successNum;
	private int failNum;
	private int updateNum;
	private int deleteNum;
	private int addNum;
	
	public List<String> getUpdateDocs() {
		return updateDocs;
	}
	public void setUpdateDocs(List<String> updateDocs) {
		this.updateDocs = updateDocs;
	}
	public List<String> getInsertDocs() {
		return insertDocs;
	}
	public void setInsertDocs(List<String> insertDocs) {
		this.insertDocs = insertDocs;
	}
	public List<String> getDeleteDocs() {
		return deleteDocs;
	}
	public void setDeleteDocs(List<String> deleteDocs) {
		this.deleteDocs = deleteDocs;
	}
	public List<String> getFailDocs() {
		return failDocs;
	}
	public void setFailDocs(List<String> failDocs) {
		this.failDocs = failDocs;
	}
	public int getFailNum() {
		return failNum;
	}
	public void setFailNum(int failNum) {
		this.failNum = failNum;
	}
	public int getAddNum() {
		return addNum;
	}
	public void setAddNum(int addNum) {
		this.addNum = addNum;
	}
	
	public int getSuccessNum() {
		return successNum;
	}
	public void setSuccessNum(int successNum) {
		this.successNum = successNum;
	}
	public int getUpdateNum() {
		return updateNum;
	}
	public void setUpdateNum(int updateNum) {
		this.updateNum = updateNum;
	}
	public int getDeleteNum() {
		return deleteNum;
	}
	public void setDeleteNum(int deleteNum) {
		this.deleteNum = deleteNum;
	}
	
}
