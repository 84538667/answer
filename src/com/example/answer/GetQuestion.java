package com.example.answer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import android.content.Context;
import android.content.SharedPreferences;  
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author TWTlhc
 * 通过这个class获取答题的题目信息
 * 提交答案等操作
 *
 */

public class GetQuestion {
	
	/**
	 * 通过这个函数获得题目信息并处理信息
	 * @param url
	 * @param username
	 * @return
	 */
	public static Question[] getQuestion(String url, String userName, String classNum , String authKey){
		
		//首先使用NameValuePair封装将要查询的年数和关键字绑定  
		ArrayList<NameValuePair> userpair = new ArrayList<NameValuePair>(2);  
     	userpair.add(new BasicNameValuePair(AnswerConst.TWT_USER_NAME,userName));  
     	userpair.add(new BasicNameValuePair(AnswerConst.TWT_CLASS_NUM,classNum));
     	userpair.add(new BasicNameValuePair(AnswerConst.TWT_AUTO_KEY,authKey));
     	
		Question []questions = new Question[20];
		InputStream content = null;
     	try {
     		HttpClient client = new DefaultHttpClient();
	     	HttpPost post = new HttpPost(AnswerConst.TWT_QUESTION_URL);
	     	post.setEntity(new UrlEncodedFormEntity(userpair));
			HttpResponse response = client.execute(post);
			
			//解析相应得到响应中的主体
			HttpEntity entity = response.getEntity();  
 	        content = entity.getContent();
		} catch (Exception e) {
			//如果抛错，输出错误信息到日志
			Log.e(AnswerConst.TWT_QUESTION_ERROR,"Connect to server error!"+e.toString());
			//直接登陆失败
			//return false;
		}
     	//将InputReader中的内容转化为string
     	StringBuilder b = null;
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader(content,"UTF-8"), 8);
			b = new StringBuilder();
	     	String tmp = null;
	     	try {
				while((tmp = reader.readLine()) != null){
					b.append(tmp);
				}
				
	     	} catch (UnsupportedEncodingException e1) {
			
	     		e1.printStackTrace();
	     	}
		} catch (IOException e) {
			//如果抛错，输出错误信息到日志
			Log.e(AnswerConst.TWT_GET_ERROR,"Convert InpterStream to String error!");
		}
     	
     	//处理请求得到的数据
		try {
			Log.i("length", ""+b.toString().length());
			Log.i("string", b.toString());
			JSONArray arr = new JSONArray(b.toString());
			for(int i = 0 ; i < 20 ; i++){
			
				JSONObject jsonObject= (JSONObject)arr.get(i);
				
				int type = Integer.parseInt(jsonObject.getString("type"));
				String id = jsonObject.getString("id");
				String question = replace(jsonObject.getString("question"));
				String option = jsonObject.getString("option");
				JSONArray answerArr = new JSONArray(option);
				String []answer = new String[5];
				for(int k = 1 ; k < 6 ; k++){
					if(answerArr.length() >= k )
						answer[k-1] = replace(answerArr.get(k-1).toString());
					else
						answer[k-1] = "";
				}

				String value = jsonObject.getString("value");
				JSONArray valueArr = new JSONArray(value);
				int []weight = new int[5];
				for(int k = 1 ; k < 6 ; k++){
					if(valueArr.length() >= k )
						weight[k-1] = Integer.parseInt(valueArr.get(k-1).toString());
				}

				
				questions[i] = new Question(id, question, answer, weight, type);
			
			}
		} catch (JSONException e) {
			//日志输出转换错误
			Log.e(AnswerConst.TWT_GET_ERROR, "Convert Json to String error");
		} 	
		return questions;
	}
	
	/**
	 * 将最后的结果提交给服务器
	 * @param score
	 * @return
	 */
	public static int submitResult( Context context, String result ){
     	
		InputStream content = null;
		int score = 0;
		int lastScore = 0;
		try {
     		HttpClient client = new DefaultHttpClient();
	     	//HttpPost post = new HttpPost(AnswerConst.TWT_SUBMIT_URL);
     		HttpPost post = new HttpPost(AnswerConst.TWT_SUBMIT_URL);
     		post.setEntity(new StringEntity(result));
			HttpResponse response = client.execute(post);
			
			//解析相应得到响应中的主体
			HttpEntity entity = response.getEntity();  
 	        content = entity.getContent();
		} catch (Exception e) {
			//如果抛错，输出错误信息到日志
			Log.e(AnswerConst.TWT_QUESTION_ERROR,"Connect to server error!"+e.toString());
			//提交失败
			return 0;
		}
		
		StringBuilder b = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(content,"UTF-8"),8);
			b = new StringBuilder();
	     	String tmp = null;
	     	try {
				while((tmp = reader.readLine()) != null){
					b.append(tmp);
				}
				
	     	} catch (UnsupportedEncodingException e1) {
			
	     		e1.printStackTrace();
	     	}
		} catch (IOException e) {
			//如果抛错，输出错误信息到日志
			Log.e(AnswerConst.TWT_GET_ERROR,"Convert InpterStream to String error!");
		}
	     	
		//得到成绩
		try {
			Log.i("string", b.toString());
			JSONObject resultJSON = new JSONObject(b.toString());
			score = resultJSON.getInt("score");
			if(!resultJSON.getString("lastscore").equals("null"))
				lastScore = Integer.parseInt((String)resultJSON.getString("lastscore"));
		} catch (JSONException e) {
			//日志输出转换错误
			Log.e(AnswerConst.TWT_GET_ERROR, "Convert Json to String error");
		} 	
        
		if( score < lastScore )
			return 200+score;
		else{
			return score;
		}
		
	}

	/**
	 * 获取的JSON中可能有乱码
	 * 转换乱码++
	 * @param s
	 * @return
	 */
	private static String replace( String s ){
		s = s.replaceAll("&#8226;", "·");
		s = s.replaceAll("&amp;#8226;", "·");
		return s;
	}
	
	/**
	 * 更新分数的sharedpreference
	 * @param context
	 * @return
	 */
	public static void updateSharedScore(Context context , int score , int classNum ){
		SharedPreferences sharedScore = 
				context.getSharedPreferences(AnswerConst.TWT_SHAREDPREFENCE_SCORE + classNum,
						 Context.MODE_PRIVATE);
        sharedScore.edit().putString("score", ""+score).commit();
	}
	
	/**
	 * 获取分数的sharedpreference
	 * @param context
	 * @return
	 */
	public static int getSharedScore( Context context , int classNum){
		SharedPreferences sharedScore = 
				context.getSharedPreferences(AnswerConst.TWT_SHAREDPREFENCE_SCORE + classNum,
						 Context.MODE_PRIVATE);
        int score = Integer.parseInt(sharedScore.getString( "score", ""));  
		return score;
	}
	
}