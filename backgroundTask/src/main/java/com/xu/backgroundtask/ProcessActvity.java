package com.xu.backgroundtask;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProcessActvity extends Activity implements BackgroundTaskViewListener{

	
	private DoTaskOnBackground mytask;
//	private String taskId;
	private String msg;
	private Button button_redo;
	private Button button_cancel;
	private View layout_button;
	private ProgressBar progressBar_content;
	private TextView textView_content;
	private boolean canCancel=true;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process);
		if(getIntent().getExtras()==null){
			finish();
			return;
		}
		
		
//		taskId=getIntent().getExtras().getString("taskId");
		msg=getIntent().getExtras().getString("msg");
		canCancel=getIntent().getExtras().getBoolean("canCancel");
		mytask=DoTaskOnBackground.regiserActivity(this);
	
	
		iniView();
		iniViewData();
		initViewCase();
	}

	









	private void iniView(){
		button_redo=(Button) findViewById(R.id.button_redo);
		button_cancel=(Button) findViewById(R.id.button_cancel);
		layout_button=findViewById(R.id.layout_button);
		progressBar_content=(ProgressBar) findViewById(R.id.progressBar_content);
		textView_content=(TextView) findViewById(R.id.textView_content);
	}
	
	private void iniViewData() {
		
		textView_content.setText(msg);
	}
	private void initViewCase() {
		button_redo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				textView_content.setText("正在重新运行");
				redo();
				
			}
		});
		if(!canCancel){
			button_cancel.setVisibility(View.GONE);
		}
		button_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancel();
				
			}
		});
	}
	
	
	/* (non-Javadoc)
	 * @see com.xu.backgroundtask.BackgroundTaskViewListener#whenCacthError()
	 */
	@Override
	public void whenCacthError(String msg){
		textView_content.setText("出现异常："+msg+"是否重试？");
		showRedoButton();
		
	}










	private Animation showButonAni;
	public void showRedoButton() {
		if(showButonAni==null){
			showButonAni=new AlphaAnimation(0,1);
			showButonAni.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					button_redo.setVisibility(View.VISIBLE);
				}
			});
			showButonAni.setDuration(500);
		}
		
		
		button_redo.startAnimation(showButonAni);
	}
	
	private Animation hiddenButonAni;
	public void hiddenRedoButton() {
		if(hiddenButonAni==null){
			hiddenButonAni=new AlphaAnimation(1,0);
			hiddenButonAni.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					button_redo.setVisibility(View.GONE);
				}
			});
			showButonAni.setDuration(500);
		}
		
		
		button_redo.startAnimation(showButonAni);
	}
	
	private void redo(){
		if(mytask!=null){
			mytask.run();
			hiddenRedoButton();
		}else{
			finish();
		}
	}
	
	private void cancel(){
		mytask.cancel();
		
		finish();
	}




//	@Override
//	protected void onDestroy() {
//		DoTaskOnBackground.unRegiserActivity(this);
//		super.onDestroy();
//	}











	@Override
	public void onBackPressed() {
		if(canCancel){
			cancel();
			super.onBackPressed();
		}
	}












	@Override
	public String getBackgroundTaskId() {
		return getIntent().getExtras().getString("taskId");
	}



	@Override
	public void dismiss() {
		finish();
	}


	@Override
	public void updateTitle(String msg) {
		textView_content.setText(msg);
	}
}
