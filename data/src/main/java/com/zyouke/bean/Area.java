package com.zyouke.bean;




public class Area {

    private Long id;// 主键

    private String code;// 字典编码

    private String value;// 字典编码对应的值

    private String parent;// 父id

    private Integer level;// 层级

    private String fullName;// 全名，比如河北省沧州市衡水市

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
