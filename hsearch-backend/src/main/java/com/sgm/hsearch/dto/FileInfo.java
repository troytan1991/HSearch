package com.sgm.hsearch.dto;

import java.util.Date;

public class FileInfo {
	
	private String title;
	private String path;
	private String content;
	private Date updateTime;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date date) {
		this.updateTime = date;
	}
	
	
}
