package com.zyouke.bean;


public class Area{

    private Long id;

    private String code;
    
    private String value;
    
    private String parent;

    private Integer level;

    private String fullName;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value == null ? null : value.trim();
    }

    public String getParent() {
	return parent;
    }

    public void setParent(String parent) {
	this.parent = parent;
    }

    public Integer getLevel() {
	return level;
    }

    public void setLevel(Integer level) {
	this.level = level;
    }

    public String getFullName() {
	return fullName;
    }

    public void setFullName(String fullName) {
	this.fullName = fullName == null ? null : fullName.trim();
    }

    @Override
    public String toString() {
	return "id=" + this.id + "," + "code=" + this.code + "," + "value=" + this.value + "," + "parent=" + this.parent + "," + "level=" + this.level + "," + "fullName=" + this.fullName;
    }

}
