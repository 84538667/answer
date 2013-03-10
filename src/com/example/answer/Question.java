package com.example.answer;

/**
 * @author TWTlhc
 * 题目库函数，实例化后得到题目的实例
 * 保存有题目的各种信息以及获取各信息的方法
 *
 */
public class Question {
	
	//题目
	private String question ;
	//选项
	private String []choices = new String[5];
	//选项权值
	private int []weight = new int[5];
	//是否单选
	private int type ;
	//题目id
	private String id;
	
	public Question(String id, String question , String []answer, int []weight, int type){
		this.id = id;
		this.question =question;
		for( int i = 0 ; i < 5 ; i++){
			choices[i] = answer[i];
			this.weight[i] = weight[i];	
		}
		this.type = type;
	}
	
	//获取id
	public String getId(){
		return id;
	}
	
	//获取题目
	public String getQuestion(){
		return question;
	}
	
	//获取选项
	public String[] getChoices(){
		return choices;
	}
	
	//获取选项权值
	public int getWeight(int i){
		return weight[i];
	}
	
	//获取是否单选
	public boolean isSingle(){
		return (type == 0);
	}
}
