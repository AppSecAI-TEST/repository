package com.zyouke.bean;

public enum Node {
	NODE1("node1"),
	NODE2("node2");
	private String value;
	
	private Node(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
