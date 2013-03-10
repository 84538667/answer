package com.example.answer;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author TWTlhc
 * TWT党校答题手机端答题主activity
 * 通过判断选择题为单选，多选 ，以及是否有第五个答案刷新页面，
 * 显示或隐藏单选，多选页面
 * 答题时会修改answers数组 选择第一项 数组+1 第二项 +2 第三项+4 第四项 +8 第五项+16
 * 全部打完后根据answer数组以及Question的选项权值来修改最后的result数组从而得到最后结果
 *
 */

public class MainActivity extends SherlockActivity {

	Question []question = new Question[20];
	//当期题目编号
	int num = 0;
	//所选择的答案
	int []answers = new int[20];
	//根据提交给服务器的答案
	int []result = new int[20];
	
	//答题者的twt 用户名
	final String USERNAME = "xubowensm";
	//所答章节
	final int CLASS = 4;
	//特殊值，判断是否是当前账号登录
	final int AUTHKEY = 0;
	//答完题之后挑跳转的class
	final Class<MainActivity> NEXTCLASS = com.example.answer.MainActivity.class;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//设置样式
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setSupportProgressBarIndeterminateVisibility(false);
		
		final QuickAction quickAction = new QuickAction(this);
		
		//将各种控件实例化
		Button previousButton = (Button)findViewById(R.id.buttonPrevious);
		Button nextButton = (Button)findViewById(R.id.buttonNext);
		Button submitButton = (Button)findViewById(R.id.buttonSubmit);
		RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
		CheckBox []box = new CheckBox[5];
		box[0] = (CheckBox)findViewById(R.id.checkBox0);
		box[1] = (CheckBox)findViewById(R.id.checkBox1);
		box[2] = (CheckBox)findViewById(R.id.checkBox2);
		box[3] = (CheckBox)findViewById(R.id.checkBox3);
		box[4] = (CheckBox)findViewById(R.id.checkBox4);
		
		//为各个控件添加listener
		
		//选题的listrner
		for(int i = 0 ; i < 20 ; i++){
			final int k = i;
			quickAction.setOnClickListener(i, new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					num = k;
					refreshQuestion();
				}
			
			});
		}
		//单选题的listener
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == findViewById(R.id.radioButton0).getId()) {
	               	answers[num] = 1;
	            } else if (checkedId == findViewById(R.id.radioButton1).getId()) {
	               	answers[num] = 2;
	            }else if (checkedId == findViewById(R.id.radioButton2).getId()) {
	               	answers[num] = 4;
	           	}else if (checkedId == findViewById(R.id.radioButton3).getId()) {
	               	answers[num] = 8;
	           	}else if (checkedId == findViewById(R.id.radioButton4).getId()) {
	               	answers[num] = 16;
	           	}
			}
			
		});
		
		//上一题
		previousButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(num > 0){
					num -= 1;
					refreshQuestion();
				}
			}
			
		});
		
		//下一题
		nextButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(num < 19){
					num += 1;
					refreshQuestion();
				}
			}
		});
		
		//提交结果
		submitButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				quickAction.setOnImage(answers);
				quickAction.show(v);
			}
			
		});
		
		//多选题listener
		for(int i = 0 ; i < 5 ; i++){
			final int k = i;
			box[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					//根据选项修改answer
					if (isChecked) {
						answers[num] += Math.pow(2, k);
					} else {
						answers[num] -= Math.pow(2, k);
					}
				}
			});
		}
		
		//使用线程获取题目
		new Thread(new Runnable() {
			@Override
			public void run() {
				//显示progressBar
				setSupportProgressBarIndeterminateVisibility(true);
				//获取题目信息
				question = GetQuestion.getQuestion(AnswerConst.TWT_QUESTION_URL, USERNAME, ""+CLASS , ""+AUTHKEY );
				getQuestionHandle.sendMessage(new Message());
			}
		}).start();
				
	}

	/**
	 * 异步的handler，在获取题目后调用用来更改主线程中UI的显示
	 */
	private Handler getQuestionHandle = new Handler(){
		public void handleMessage(Message message){
				setSupportProgressBarIndeterminateVisibility(false);
				findViewById(R.id.buttonPrevious).setVisibility(View.VISIBLE);
				findViewById(R.id.buttonNext).setVisibility(View.VISIBLE);
				findViewById(R.id.buttonSubmit).setVisibility(View.VISIBLE);
				findViewById(R.id.radioGroup).setVisibility(View.VISIBLE);
				findViewById(R.id.linearLayout).setVisibility(View.VISIBLE);
				findViewById(R.id.textView).setVisibility(View.VISIBLE);
				
				//刷新题目
				refreshQuestion();
		}
	};
		
	/**
	 * 在提交答案后，调用用来更改主线程中UI的显示
	 */
	private Handler getScoreHandle = new Handler(){
		public void handleMessage(Message message){
			Bundle b = message.getData();
			int score = b.getInt("score");
			setSupportProgressBarIndeterminateVisibility(false);
			//根据分数显示不同的提示信息
			if( score < 60 ){
				Toast.makeText( MainActivity.this, "您的分数为：" + score + "未通过考试", 1000).show();
			}else if( score < 101){
				Toast.makeText( MainActivity.this, "恭喜您通过考试，分数为：" + score, 1000).show();
			}else if( score > 100){
				int lastScore = GetQuestion.getSharedScore( MainActivity.this, num );
				Toast.makeText( MainActivity.this, "您的分数为："+(score-200)+ "低于历史最高分：" + lastScore, 1000).show();
			}
			
			//使用intent跳转至新的activity
			Intent intent = new Intent();
			intent.setClass( MainActivity.this, NEXTCLASS);
			startActivity(intent);
		}
	};
	
	/**
	 * 刷新题目的function
	 * 判断当前题目为单选还是多选，之后根据情况刷新题目
	 */
	public void refreshQuestion(){
		
		//初始化各空间
		RadioButton []Button = new RadioButton[5];
		Button[0] = (RadioButton)findViewById(R.id.radioButton0);
		Button[1] = (RadioButton)findViewById(R.id.radioButton1);
		Button[2] = (RadioButton)findViewById(R.id.radioButton2);
		Button[3] = (RadioButton)findViewById(R.id.radioButton3);
		Button[4] = (RadioButton)findViewById(R.id.radioButton4);
		TextView questionTextView = (TextView)findViewById(R.id.textView);
		CheckBox []box = new CheckBox[5];
		box[0] = (CheckBox)findViewById(R.id.checkBox0);
		box[1] = (CheckBox)findViewById(R.id.checkBox1);
		box[2] = (CheckBox)findViewById(R.id.checkBox2);
		box[3] = (CheckBox)findViewById(R.id.checkBox3);
		box[4] = (CheckBox)findViewById(R.id.checkBox4);
		RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
		LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
		
		//根据题目形式 单/多选来设置UI
		
		//如果题目为单选，则隐藏多选框，显示单选框，
		//同时查看题目答案，如果已经做了本题，则根据答案显示相应 选项
		if( question[num].isSingle() ){
			
			//隐藏复选框
			linearLayout.setVisibility(View.GONE);
			//显示单选框
			radioGroup.setVisibility(View.VISIBLE);
			//显示问题
			questionTextView.setText("第"+(num+1)+"题（单选）"+question[num].getQuestion());
			String []choices = question[num].getChoices();
			//显示答案
			for(int i = 0 ; i < 5 ; i++){
				Button[i].setText(choices[i] );
				//将选框设置为可见
				Button[i].setVisibility(View.VISIBLE);
				if(choices[i].equals("")){
					//如果不存在这个选项，则隐藏相应radiobutton
					Button[i].setVisibility(View.GONE);
				}
			}

			//显示已做答案
			switch(answers[num]){
			case 0:
				for(int i = 0 ; i < 5 ; i++){
					Button[i].setChecked(false);
				}
				break;
			case 1:
				Button[0].setChecked(true);
				break;
			case 2:
				Button[1].setChecked(true);
				break;
			case 4:
				Button[2].setChecked(true);
				break;
			case 8:
				Button[3].setChecked(true);
				break;
			case 16:
				Button[4].setChecked(true);
				break;	
			}
		
		}else{
		//如果当钱题目为多选，则要隐藏单选框，显示多选框，并显示一坐的答案
			//隐藏单选框
			radioGroup.setVisibility(View.GONE);
			//显示复选框
			linearLayout.setVisibility(View.VISIBLE);
			//显示题目
			questionTextView.setText("第"+(num+1)+"题：（多选）"+question[num].getQuestion());
			String []choices = question[num].getChoices();
			//清空选项
			for(int i = 0 ; i < 5 ; i++){
				final int k = i;
				//显示答案
				box[i].setText(choices[i]);
				box[i].setVisibility(View.VISIBLE);
				if( choices[i].equals("") )
					//如不存在此选项，影藏对应checkedbox
					box[i].setVisibility(View.GONE);
				//如果checkedbox改变状态，则在题答案数组中更改
				if(box[i].isChecked()){
					box[i].setChecked(false);
					answers[num] += Math.pow(2, k);
				}
			}
			//显示已做答案，为0表示未做
			int ans = answers[num];
			for(int i = 4 ; i >= 0 ; i--){
				final int k = i;
				if(ans >= Math.pow(2, k) ){
					if(!box[i].isChecked()){
						box[i].setChecked(true);
						answers[num] -= Math.pow(2, k);
					}
					ans -= Math.pow(2, k) ;
				}
			}
		}
	}

	/**
	 * 通过已做的答案以及各选项权值 得到最后的结果
	 */
	public void getResult(){
		//初始化数组
		for(int i = 0 ; i< 20 ; i++){
			result[i] = 0;
		}
		
		//计算结果
		for(int j = 0 ; j < 20 ; j++){
			int ans = answers[j];
			for(int i = 4 ; i >= 0 ; i--){
				final int k = i;
				if(ans >= Math.pow(2, k) ){
					result[j] += question[j].getWeight(i);
					ans -= Math.pow(2, k) ;
				}
			}
		}
	}

	/**
	 * 提交结果到服务器并等带返回结果
	 */
	public void submitResult(){
		//设置UI
		setSupportProgressBarIndeterminateVisibility(true);
		findViewById(R.id.buttonPrevious).setVisibility(View.GONE);
		findViewById(R.id.buttonNext).setVisibility(View.GONE);
		findViewById(R.id.buttonSubmit).setVisibility(View.GONE);
		findViewById(R.id.radioGroup).setVisibility(View.GONE);
		findViewById(R.id.linearLayout).setVisibility(View.GONE);
		findViewById(R.id.textView).setVisibility(View.GONE);
		
		getResult();
		
		//记录题目答案信息的arraylist
		List<Answer> answerList = new ArrayList<Answer>();  
        for(int i = 0 ; i < 20 ; i++){
        	answerList.add(new Answer(question[i].getId(),""+result[i]));
        }
        //传入username，class，以及authkey
        answerList.add(new Answer("-1", USERNAME) );
        answerList.add(new Answer("-2", ""+CLASS) );
        answerList.add(new Answer("-3", ""+AUTHKEY) );
        //将arraylist 转化成为 string 
        final JSONArray resultArr = new JSONArray(answerList );  
        Log.i("resultArr", resultArr.toString() );
        
        //创建线程提交答案至服务器
        new Thread(new Runnable() {
			@Override
			public void run() {
				Bundle b = new Bundle();
				Message msg = new Message();
				//获取分数
				int score = GetQuestion.submitResult( MainActivity.this, resultArr.toString() );
				b.putInt("score", score);
				msg.setData( b);
				getScoreHandle.sendMessage( msg);
			}
		}).start();
		
	} 
	
	//添加menu(这是调用的Actionsherlock中的menu)主要是给actionBar添加上menu
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
   	 
   	 
   	 	//设置subMenu 属性。
   	 	SubMenu submenu = menu.addSubMenu("Other action");
   	 	submenu.add(R.string.submit);
   	 	
   	 	MenuItem item = submenu.getItem();
   	 	item.setIcon(R.drawable.other_action);
   	 	//设置这个item总是在actionBar第一个显示
   	 	item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        super.onCreateOptionsMenu(menu);
        return true;
    }
    
	@Override
	//当菜单被选中时，触发的时间
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getTitle().equals(this.getResources().getString(R.string.submit))){
			int k = 0;
			//如果没有答完全部题，不允许提交
			for( ; k < 20 ; k++ ){
				if( answers[k] == 0 )
					break;
			}
			if( k == 20 ){
				submitResult();
			}
			else
				Toast.makeText( MainActivity.this, "您还有题没答", 1000).show();
		}
		
		return super.onOptionsItemSelected(item);
	}
}