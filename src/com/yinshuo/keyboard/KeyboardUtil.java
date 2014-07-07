package com.yinshuo.keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.session.IoSession;
import org.json.JSONException;
import org.json.JSONObject;

import com.yinshuo.R;
import com.yinshuo.handler.MiNaIOHandler;
import com.yinshuo.usbconnect.ThreadReadWriterIOSocket;
import com.yinshuo.usbconnect.androidService;
import com.yinshuo.utils.BulkEnum;
import com.yinshuo.utils.DES;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class KeyboardUtil {
	private Activity mActivity;
	@SuppressWarnings("unused")
	private Context mContext;
	@SuppressWarnings("unused")
	private KeyboardView keyboardView;
	private Keyboard keyAlp;// 字母键盘
	private Keyboard keyDig;// 数字键盘
	public boolean isnun = true;// 是否数字键盘
	public boolean isupper = false;// 是否大写
	RelativeLayout keyboard_layout;
	private EditText ed;
	
	long mstartTime;
	long timeInterval;
	Timer timer = new Timer();  
	private MyTimerTask mTimerTask;
	long timeOut;

	
	public KeyboardUtil(Activity mActivity,Context mContext, EditText edit) {
		this.mActivity=mActivity;
		this.mContext = mContext;
		this.ed = edit;
		keyAlp = new Keyboard(mContext, R.xml.qwerty);
		keyDig = new Keyboard(mContext, R.xml.symbols);
		keyboardView = (KeyboardView) mActivity.findViewById(R.id.keyboard_view);
		keyboard_layout = (RelativeLayout) mActivity.findViewById(R.id.keyboard_layout);
		randomdigkey();
		//keyboardView.setKeyboard(keyAlp); 
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(true);
		keyboardView.setOnKeyboardActionListener(listener);

	}

	class MyTimerTask extends TimerTask {
		  
				JSONObject jsonObject = new JSONObject();
			      public void run() {     	   	  
			    	  timeInterval = SystemClock.uptimeMillis()-mstartTime;
			    	  if(timeInterval>timeOut){
			    		
			    		  try {	 
			    			  
			    			  jsonObject.put("cmd", BulkEnum.KEYBOARD_STATUS.KEYBOARD_SET_TIMEOUT);	  
			    			  jsonObject.put("timeInterval", timeInterval);	  
			    			
			    			  Log.i("sc", "time out  jsonObject"+jsonObject.toString());  
			    			  
			    			  IoSession ioSession = MiNaIOHandler.getmSession();  
			    			  if(null!=ioSession){
			    				  ioSession.write(jsonObject.toString().getBytes());
			    				  mTimerTask.cancel();
			    			  }
			    			  
			    			 /* if(null!=ThreadReadWriterIOSocket.getOut()){
			    				ThreadReadWriterIOSocket.getOut().write(jsonObject.toString().getBytes());
			  					ThreadReadWriterIOSocket.getOut().flush();
			  					timer.cancel();
			    			  }*/
							
							return;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
			    	  }
			   }  
			}
/*	
	
	TimerTask task = new TimerTask(){  
		JSONObject jsonObject = new JSONObject();
	      public void run() {     	   	  
	    	  timeInterval = SystemClock.uptimeMillis()-mstartTime;
	    	  if(timeInterval>timeOut){
	    		
	    		  try {	    			 
	    			jsonObject.put("timeInterval", timeInterval);	  
	    			
	    			  Log.i("sc", "time out  jsonObject"+jsonObject.toString());  
	    			  if(null!=ThreadReadWriterIOSocket.getOut()){
	    				 ThreadReadWriterIOSocket.getOut().write(jsonObject.toString().getBytes());
	  					ThreadReadWriterIOSocket.getOut().flush();
	  					timer.cancel();
	    			  }
					
					return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
	    	  }
	   }  
	};*/
	
	
	private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			SoundManager.getInstance(KeyboardUtil.this.mContext).playKeyDown();
			mstartTime = SystemClock.uptimeMillis();
			 // Log.i("sc", "mstartTime key"+mstartTime);
			Editable editable = ed.getText();
			int start = ed.getSelectionStart();
			if (primaryCode == Keyboard.KEYCODE_DONE) {// 完成
				//hideKeyboard();
				//mActivity.finish();
				Toast.makeText(mContext, ed.getText().toString(), Toast.LENGTH_LONG).show();	
				 IoSession ioSession = MiNaIOHandler.getmSession();  
	   			  if(null!=ioSession){			  
	   				  JSONObject jsonObject = new JSONObject();
	   				  try {
						jsonObject.put("cmd", BulkEnum.KEYBOARD_STATUS.KEYBOARD_RECV_CMD);
						//jsonObject.put("content", ed.getText().toString());
						 String str1 = ed.getText().toString();
						 String str2 = DES.encryptDES(str1, "87654321");
				          String str3 = DES.decryptDES(str2, "87654321");
				          Log.i("sc", str2 + "result1");
				          Log.i("sc", str3 + "result2");
				          jsonObject.put("content", str2);
				          Log.i("sc", ioSession.write(jsonObject.toString()).isDone() + "----write.isDone");
						
						ioSession.write(jsonObject.toString());
						mTimerTask.cancel();	
						ed.setText("");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	   			  }
			} else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
				if (!TextUtils.isEmpty(editable)) {
					if (start > 0) {
						editable.delete(start - 1, start);
					}
				}
			} else if (primaryCode == Keyboard.KEYCODE_SHIFT) {// 大小写切换
				changeKey();
				keyboardView.setKeyboard(keyAlp);

			} else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {// 数字键盘切换
				if (isnun) {
					isnun = false;
					randomalpkey();
				} else {
					isnun = true;
					randomdigkey();
				}
			}else if(primaryCode==Keyboard.KEYCODE_CANCEL){
				ed.setText("");			
			}else if(primaryCode==-6){
				ed.setText("");			
			}			
			else {
				editable.insert(start, Character.toString((char) primaryCode));
			}
		}
	};

	/**
	 * 键盘大小写切换
	 */
	private void changeKey() {
		List<Key> keylist = keyAlp.getKeys();
		if (isupper) {// 大写切换小写
			isupper = false;
			for (Key key : keylist) {
				if (key.label != null && isword(key.label.toString())) {
					key.label = key.label.toString().toLowerCase();
					key.codes[0] = key.codes[0] + 32;
				}
			}
		} else {// 小写切换大写
			isupper = true;
			for (Key key : keylist) {
				if (key.label != null && isword(key.label.toString())) {
					key.label = key.label.toString().toUpperCase();
					key.codes[0] = key.codes[0] - 32;
				}
			}
		}
	}

	public void showKeyboard() {
		randomdigkey();
		int visibility = keyboardView.getVisibility();
		if (visibility == View.GONE || visibility == View.INVISIBLE) {
			keyboardView.setVisibility(View.VISIBLE);
			//ed.setVisibility(View.VISIBLE);
			keyboard_layout.setVisibility(View.VISIBLE);
			SharedPreferences sp = mContext.getSharedPreferences("setting", Context.MODE_PRIVATE);  
			timeOut = sp.getInt("timeOut", 10)*1000;
			
			Log.i("sc", timeOut+"time out");   	
			mTimerTask = new MyTimerTask();
			timer.schedule(mTimerTask, 1000, timeOut); 
			mstartTime = SystemClock.uptimeMillis();
		}
	}

	public void hideKeyboard() {
		int visibility = keyboardView.getVisibility();
		if (visibility == View.VISIBLE) {
			keyboardView.setVisibility(View.INVISIBLE);
			ed.setVisibility(View.INVISIBLE);
			
			keyboard_layout.setVisibility(View.GONE);
			mTimerTask.cancel();
		}
	}

	private boolean isNumber(String str) {
		String wordstr = "0123456789";
		if (wordstr.indexOf(str) > -1) {
			return true;
		}
		return false;
	}

	private boolean isword(String str) {
		String wordstr = "abcdefghijklmnopqrstuvwxyz";
		if (wordstr.indexOf(str.toLowerCase()) > -1) {
			return true;
		}
		return false;
	}
	private void randomdigkey(){
		List<Key> keyList = keyDig.getKeys();
		// 查找出0-9的数字键
		List<Key> newkeyList = new ArrayList<Key>();
		for (int i = 0; i < keyList.size(); i++) {
			/*if (keyList.get(i).label != null&& isNumber(keyList.get(i).label.toString())) {
				newkeyList.add(keyList.get(i));
				
			}*/
			if(keyList.get(i).codes[0]>=48){
				newkeyList.add(keyList.get(i));
			}
		}
		// 数组长度
		int count = newkeyList.size();
		// 结果集
		List<KeyModel> resultList = new ArrayList<KeyModel>();
		// 用一个LinkedList作为中介
		LinkedList<KeyModel> temp = new LinkedList<KeyModel>();
		// 初始化temp
		for (int i = 0; i < count; i++) {
			//temp.add(new KeyModel(48 + i, i + ""));
			temp.add(new KeyModel(48 + i, String.valueOf(R.drawable.num0+i)));
		}
		// 取数
		Random rand = new Random(); // 随机取0-10  从temp 中取出 依次赋值给 resultList[i] ,取出后删除，防止再次取到相同的值
		for (int i = 0; i < count; i++) {
			int num = rand.nextInt(count - i);

			resultList.add(new KeyModel(temp.get(num).getCode(),temp.get(num).getLable()));		
			temp.remove(num);
		}
		for (int i = 0; i < newkeyList.size(); i++) {
			//newkeyList.get(i).label = resultList.get(i).getLable();
			newkeyList.get(i).codes[0] = resultList.get(i).getCode();
			newkeyList.get(i).icon = this.mContext.getResources().getDrawable(Integer.parseInt(resultList.get(i).getLable()));
		}

		keyboardView.setKeyboard(keyDig);
	}
	private void randomalpkey(){
		List<Key> keyList = keyAlp.getKeys();
		// 查找出a-z的数字键
		List<Key> newkeyList = new ArrayList<Key>();
		for (int i = 0; i < keyList.size(); i++) {
			if (keyList.get(i).label != null
					&& isword(keyList.get(i).label.toString())) {
				newkeyList.add(keyList.get(i));
			}
		}
		// 数组长度
		int count = newkeyList.size();
		// 结果集
		List<KeyModel> resultList = new ArrayList<KeyModel>();
		// 用一个LinkedList作为中介
		LinkedList<KeyModel> temp = new LinkedList<KeyModel>();
		// 初始化temp
		for (int i = 0; i < count; i++) {
			temp.add(new KeyModel(97 + i, "" + (char) (97 + i)));
		}
		// 取数
		Random rand = new Random();
		for (int i = 0; i < count; i++) {
			int num = rand.nextInt(count - i);
			resultList.add(new KeyModel(temp.get(num).getCode(),
					temp.get(num).getLable()));
			temp.remove(num);
		}
		for (int i = 0; i < newkeyList.size(); i++) {
			newkeyList.get(i).label = resultList.get(i).getLable();
			newkeyList.get(i).codes[0] = resultList.get(i)
					.getCode();
		}

		keyboardView.setKeyboard(keyAlp);
	}
}
