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
 * TWT��У�����ֻ��˴�����activity
 * ͨ���ж�ѡ����Ϊ��ѡ����ѡ ���Լ��Ƿ��е������ˢ��ҳ�棬
 * ��ʾ�����ص�ѡ����ѡҳ��
 * ����ʱ���޸�answers���� ѡ���һ�� ����+1 �ڶ��� +2 ������+4 ������ +8 ������+16
 * ȫ����������answer�����Լ�Question��ѡ��Ȩֵ���޸�����result����Ӷ��õ������
 *
 */

public class MainActivity extends SherlockActivity {

	Question []question = new Question[20];
	//������Ŀ���
	int num = 0;
	//��ѡ��Ĵ�
	int []answers = new int[20];
	//�����ύ���������Ĵ�
	int []result = new int[20];
	
	//�����ߵ�twt �û���
	final String USERNAME = "xubowensm";
	//�����½�
	final int CLASS = 4;
	//����ֵ���ж��Ƿ��ǵ�ǰ�˺ŵ�¼
	final int AUTHKEY = 0;
	//������֮������ת��class
	final Class<MainActivity> NEXTCLASS = com.example.answer.MainActivity.class;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//������ʽ
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setSupportProgressBarIndeterminateVisibility(false);
		
		final QuickAction quickAction = new QuickAction(this);
		
		//�����ֿؼ�ʵ����
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
		
		//Ϊ�����ؼ����listener
		
		//ѡ���listrner
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
		//��ѡ���listener
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
		
		//��һ��
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
		
		//��һ��
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
		
		//�ύ���
		submitButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				quickAction.setOnImage(answers);
				quickAction.show(v);
			}
			
		});
		
		//��ѡ��listener
		for(int i = 0 ; i < 5 ; i++){
			final int k = i;
			box[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					//����ѡ���޸�answer
					if (isChecked) {
						answers[num] += Math.pow(2, k);
					} else {
						answers[num] -= Math.pow(2, k);
					}
				}
			});
		}
		
		//ʹ���̻߳�ȡ��Ŀ
		new Thread(new Runnable() {
			@Override
			public void run() {
				//��ʾprogressBar
				setSupportProgressBarIndeterminateVisibility(true);
				//��ȡ��Ŀ��Ϣ
				question = GetQuestion.getQuestion(AnswerConst.TWT_QUESTION_URL, USERNAME, ""+CLASS , ""+AUTHKEY );
				getQuestionHandle.sendMessage(new Message());
			}
		}).start();
				
	}

	/**
	 * �첽��handler���ڻ�ȡ��Ŀ����������������߳���UI����ʾ
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
				
				//ˢ����Ŀ
				refreshQuestion();
		}
	};
		
	/**
	 * ���ύ�𰸺󣬵��������������߳���UI����ʾ
	 */
	private Handler getScoreHandle = new Handler(){
		public void handleMessage(Message message){
			Bundle b = message.getData();
			int score = b.getInt("score");
			setSupportProgressBarIndeterminateVisibility(false);
			//���ݷ�����ʾ��ͬ����ʾ��Ϣ
			if( score < 60 ){
				Toast.makeText( MainActivity.this, "���ķ���Ϊ��" + score + "δͨ������", 1000).show();
			}else if( score < 101){
				Toast.makeText( MainActivity.this, "��ϲ��ͨ�����ԣ�����Ϊ��" + score, 1000).show();
			}else if( score > 100){
				int lastScore = GetQuestion.getSharedScore( MainActivity.this, num );
				Toast.makeText( MainActivity.this, "���ķ���Ϊ��"+(score-200)+ "������ʷ��߷֣�" + lastScore, 1000).show();
			}
			
			//ʹ��intent��ת���µ�activity
			Intent intent = new Intent();
			intent.setClass( MainActivity.this, NEXTCLASS);
			startActivity(intent);
		}
	};
	
	/**
	 * ˢ����Ŀ��function
	 * �жϵ�ǰ��ĿΪ��ѡ���Ƕ�ѡ��֮��������ˢ����Ŀ
	 */
	public void refreshQuestion(){
		
		//��ʼ�����ռ�
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
		
		//������Ŀ��ʽ ��/��ѡ������UI
		
		//�����ĿΪ��ѡ�������ض�ѡ����ʾ��ѡ��
		//ͬʱ�鿴��Ŀ�𰸣�����Ѿ����˱��⣬����ݴ���ʾ��Ӧ ѡ��
		if( question[num].isSingle() ){
			
			//���ظ�ѡ��
			linearLayout.setVisibility(View.GONE);
			//��ʾ��ѡ��
			radioGroup.setVisibility(View.VISIBLE);
			//��ʾ����
			questionTextView.setText("��"+(num+1)+"�⣨��ѡ��"+question[num].getQuestion());
			String []choices = question[num].getChoices();
			//��ʾ��
			for(int i = 0 ; i < 5 ; i++){
				Button[i].setText(choices[i] );
				//��ѡ������Ϊ�ɼ�
				Button[i].setVisibility(View.VISIBLE);
				if(choices[i].equals("")){
					//������������ѡ���������Ӧradiobutton
					Button[i].setVisibility(View.GONE);
				}
			}

			//��ʾ������
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
		//�����Ǯ��ĿΪ��ѡ����Ҫ���ص�ѡ����ʾ��ѡ�򣬲���ʾһ���Ĵ�
			//���ص�ѡ��
			radioGroup.setVisibility(View.GONE);
			//��ʾ��ѡ��
			linearLayout.setVisibility(View.VISIBLE);
			//��ʾ��Ŀ
			questionTextView.setText("��"+(num+1)+"�⣺����ѡ��"+question[num].getQuestion());
			String []choices = question[num].getChoices();
			//���ѡ��
			for(int i = 0 ; i < 5 ; i++){
				final int k = i;
				//��ʾ��
				box[i].setText(choices[i]);
				box[i].setVisibility(View.VISIBLE);
				if( choices[i].equals("") )
					//�粻���ڴ�ѡ�Ӱ�ض�Ӧcheckedbox
					box[i].setVisibility(View.GONE);
				//���checkedbox�ı�״̬��������������и���
				if(box[i].isChecked()){
					box[i].setChecked(false);
					answers[num] += Math.pow(2, k);
				}
			}
			//��ʾ�����𰸣�Ϊ0��ʾδ��
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
	 * ͨ�������Ĵ��Լ���ѡ��Ȩֵ �õ����Ľ��
	 */
	public void getResult(){
		//��ʼ������
		for(int i = 0 ; i< 20 ; i++){
			result[i] = 0;
		}
		
		//������
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
	 * �ύ��������������ȴ����ؽ��
	 */
	public void submitResult(){
		//����UI
		setSupportProgressBarIndeterminateVisibility(true);
		findViewById(R.id.buttonPrevious).setVisibility(View.GONE);
		findViewById(R.id.buttonNext).setVisibility(View.GONE);
		findViewById(R.id.buttonSubmit).setVisibility(View.GONE);
		findViewById(R.id.radioGroup).setVisibility(View.GONE);
		findViewById(R.id.linearLayout).setVisibility(View.GONE);
		findViewById(R.id.textView).setVisibility(View.GONE);
		
		getResult();
		
		//��¼��Ŀ����Ϣ��arraylist
		List<Answer> answerList = new ArrayList<Answer>();  
        for(int i = 0 ; i < 20 ; i++){
        	answerList.add(new Answer(question[i].getId(),""+result[i]));
        }
        //����username��class���Լ�authkey
        answerList.add(new Answer("-1", USERNAME) );
        answerList.add(new Answer("-2", ""+CLASS) );
        answerList.add(new Answer("-3", ""+AUTHKEY) );
        //��arraylist ת����Ϊ string 
        final JSONArray resultArr = new JSONArray(answerList );  
        Log.i("resultArr", resultArr.toString() );
        
        //�����߳��ύ����������
        new Thread(new Runnable() {
			@Override
			public void run() {
				Bundle b = new Bundle();
				Message msg = new Message();
				//��ȡ����
				int score = GetQuestion.submitResult( MainActivity.this, resultArr.toString() );
				b.putInt("score", score);
				msg.setData( b);
				getScoreHandle.sendMessage( msg);
			}
		}).start();
		
	} 
	
	//���menu(���ǵ��õ�Actionsherlock�е�menu)��Ҫ�Ǹ�actionBar�����menu
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
   	 
   	 
   	 	//����subMenu ���ԡ�
   	 	SubMenu submenu = menu.addSubMenu("Other action");
   	 	submenu.add(R.string.submit);
   	 	
   	 	MenuItem item = submenu.getItem();
   	 	item.setIcon(R.drawable.other_action);
   	 	//�������item������actionBar��һ����ʾ
   	 	item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        super.onCreateOptionsMenu(menu);
        return true;
    }
    
	@Override
	//���˵���ѡ��ʱ��������ʱ��
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getTitle().equals(this.getResources().getString(R.string.submit))){
			int k = 0;
			//���û�д���ȫ���⣬�������ύ
			for( ; k < 20 ; k++ ){
				if( answers[k] == 0 )
					break;
			}
			if( k == 20 ){
				submitResult();
			}
			else
				Toast.makeText( MainActivity.this, "��������û��", 1000).show();
		}
		
		return super.onOptionsItemSelected(item);
	}
}