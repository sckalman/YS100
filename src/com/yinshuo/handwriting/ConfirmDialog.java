package com.yinshuo.handwriting;

import org.json.JSONException;
import org.json.JSONObject;

import com.yinshuo.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmDialog extends Dialog implements OnClickListener{
		
	Context context;    
	private Button ok,cancle;
	private ListenerThree listener;
	private String mBtn1TextString;
	private String mBtn2TextString;
	private String mDialogTitleString;
	private String mDialogContentString;
	
	public ConfirmDialog(Context context) 
	{        
		super(context);        
		// TODO Auto-generated constructor stub        
		this.context = context;    
	}    
	public ConfirmDialog(Context context, int theme,ListenerThree listener)
	{       
		super(context, theme);       
		this.context = context;
		this.listener = listener;
	}    
	
	public ConfirmDialog(Context context, int theme,JSONObject jsonObject)
	{       
		super(context, theme);       
		this.context = context;
		
		try {
			mDialogContentString = jsonObject.getString("content");
			
			jsonObject.getJSONArray("btn").length();
			
			mBtn1TextString = jsonObject.getJSONArray("btn").getString(0);
			mBtn2TextString = jsonObject.getJSONArray("btn").getString(1);
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
   	
	public interface ListenerThree{ 
        public void onClick(View view); 
    }
	//设置应该响应onClick事件的接口
	public void SetListener(ListenerThree listener){
		this.listener = listener;
	}
	
	protected void onCreate(Bundle savedInstanceState) 
	{        
		// TODO Auto-generated method stub        
		super.onCreate(savedInstanceState);        
		this.setContentView(R.layout.confirm_dialog);   
		init();
	}
	
	public void init()
	{
		ok = (Button) findViewById(R.id.dialog_button_ok);	    
		cancle = (Button) findViewById(R.id.dialog_button_cancle);
		ok.setOnClickListener(this);
		cancle.setOnClickListener(this);
		TextView dialogTitleTextView = (TextView)findViewById(R.id.dialog_title);
		TextView dialogContentTextView = (TextView)findViewById(R.id.dialog_content);
		
		dialogContentTextView.setText(mDialogContentString);
		
		ok.setText(mBtn1TextString);
		cancle.setText(mBtn2TextString);
	}
	
	public void onClick(View v) { 
		//调用本类里面的接口中的onClick,就是所谓的接口回调吧
        listener.onClick(v); 
    }
}


