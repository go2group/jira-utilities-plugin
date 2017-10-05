package com.go2group.jira.plugin.cfcreator.pojo;

public class CustomFieldRepresentation {

	private Integer id;
	private String name;
	private String type;
	private String searcherType;
	
	public CustomFieldRepresentation(Integer id, String name, String type, String searcherType) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.searcherType = searcherType;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSearcherType() {
		return searcherType;
	}
	public void setSearcherType(String searcherType) {
		this.searcherType = searcherType;
	}
}
