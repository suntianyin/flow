package com.apabi.flow.common.model;

import java.io.Serializable;

public class ResponseData implements Serializable{
	private Integer code;
	private String msg;
	private Object data;
	
	public ResponseData(){
		
	}
	
	public ResponseData(Integer code, String msg){
		this.code = code;
		this.msg = msg;
	}

	public ResponseData apply(Integer code, String msg){
		this.code = code;
		this.msg = msg;
		return this;
	}

	public ResponseData apply(Integer code, String msg, Object data){
		this.code = code;
		this.msg = msg;
		this.data = data;
		return this;
	}
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
	
	
}
