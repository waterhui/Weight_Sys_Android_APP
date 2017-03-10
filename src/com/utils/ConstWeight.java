package com.utils;

public class ConstWeight {

	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getBmi() {
		return bmi;
	}
	public void setBmi(String bmi) {
		this.bmi = bmi;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public ConstWeight() {
		
	}

	public static final String DATABASE_NAME = "weight.db";
    public static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "WeightInfo";
	
	public static final String ATTR_ID = "Id";
	public static final String ATTR_Weight = "Weight";
	public static final String ATTR_BMI = "BMI";
	public static final String ATTR_STATUS = "Status";
    public static final String ATTR_Time = "Time";
			
	private String _id;
	private String weight;
	private String bmi;
	private String status;
	private String time;
}
