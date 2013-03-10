package com.example.answer;

/**
 * @author TWTlhc
 * �����ύ�����Answer class
 * Ϊ��ʹ��ArrayList
 *
 */
public class Answer {

	String id;
	String value;
	
	
	/**
	 * ���캯��
	 * @param id
	 * @param value
	 */
	Answer(String id , String value ){
		this.id = id;
		this.value = value;
	}
	
	/* ת��ΪString
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {  
	    return id+" "+value;  
	}  
}
