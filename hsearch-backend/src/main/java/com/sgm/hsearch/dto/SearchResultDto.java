package com.sgm.hsearch.dto;

import java.util.Date;

public class SearchResultDto {

	private Long id;
	private String title;
	private String highlightText;
	private String docUrl;
	private String path;
	private int visitCount;
	private Date createdOn;
	private String updatedOn;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getHighlightText() {
		return highlightText;
	}
	public void setHighlightText(String highlightText) {
		this.highlightText = highlightText;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getVisitCount() {
		return visitCount;
	}
	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public String getDocUrl() {
		return docUrl;
	}
	public void setDocUrl(String docUrl) {
		this.docUrl = docUrl;
	}
	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("title:"+title).append(" ID:"+id)
			.append("\r\ncontent:"+highlightText)
			.append("\r\npath:"+path)
			.append("\r\nvisitcount:"+visitCount);
		return sBuilder.toString();
	}
	public String getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}
	
}
