package com.example.answer;

/**
 * @author TWTlhc
 * ��Ŀ�⺯����ʵ������õ���Ŀ��ʵ��
 * ��������Ŀ�ĸ�����Ϣ�Լ���ȡ����Ϣ�ķ���
 *
 */
public class Question {
	
	//��Ŀ
	private String question ;
	//ѡ��
	private String []choices = new String[5];
	//ѡ��Ȩֵ
	private int []weight = new int[5];
	//�Ƿ�ѡ
	private int type ;
	//��Ŀid
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
	
	//��ȡid
	public String getId(){
		return id;
	}
	
	//��ȡ��Ŀ
	public String getQuestion(){
		return question;
	}
	
	//��ȡѡ��
	public String[] getChoices(){
		return choices;
	}
	
	//��ȡѡ��Ȩֵ
	public int getWeight(int i){
		return weight[i];
	}
	
	//��ȡ�Ƿ�ѡ
	public boolean isSingle(){
		return (type == 0);
	}
}
