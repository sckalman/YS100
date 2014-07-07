package com.yinshuo.keyboard;

import java.lang.reflect.Method;

import com.yinshuo.R;
import com.yinshuo.usbconnect.UsbConnect;
import com.yinshuo.utils.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

public class KeyboardActivity extends Activity{
    private EditText edit; 
    KeyboardUtil mKeyboard;
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题    
        super.onCreate(savedInstanceState);  
        Util.setSystemBarVisible(this, false);
        setContentView(R.layout.main);  
     
        edit = (EditText) this.findViewById(R.id.edit);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Method setShowSoftInputOnFocus = null;
		try {
			setShowSoftInputOnFocus = edit.getClass().getMethod("setShowSoftInputOnFocus", boolean.class);
			setShowSoftInputOnFocus.setAccessible(true);
			setShowSoftInputOnFocus.invoke(edit, false);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		mKeyboard = new KeyboardUtil(KeyboardActivity.this, KeyboardActivity.this, edit);
		
		mKeyboard.showKeyboard();

		edit.getText().clear();
		Log.i("sc", "KeyboardActivity onCreate");

    }
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}  
	public  void finishThisActivity(){
		KeyboardActivity.this.finish();
	}
}
