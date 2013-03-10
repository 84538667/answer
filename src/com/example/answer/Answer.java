package com.example.answer;

/**
 * @author TWTlhc
 * 创建提交结果的Answer class
 * 为了使用ArrayList
 *
 */
public class Answer {

	String id;
	String value;
	
	
	/**
	 * 构造函数
	 * @param id
	 * @param value
	 */
	Answer(String id , String value ){
		this.id = id;
		this.value = value;
	}
	
	/* 转化为String
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {  
	    return id+" "+value;  
	}  
}
