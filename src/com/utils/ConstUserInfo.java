package com.utils;

public class ConstUserInfo {

	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	
	public ConstUserInfo() {
		
	}

	public static final String DATABASE_NAME = "weight.db";
    public static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "UserInfo";
	
	public static final String ATTR_ID = "Id";
	public static final String ATTR_PWD = "Pwd";
	public static final String ATTR_NAME = "Name";
	public static final String ATTR_SEX = "Sex";
    public static final String ATTR_HEIGHT = "Height";
	
	private String _id;
	private String pwd;
	private String name;
	private String sex;
	private String height;
	
}
