package com.yinshuo.usbconnect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yinshuo.R;
import com.yinshuo.handwriting.DialogListener;
import com.yinshuo.handwriting.RatDialogInterface;
import com.yinshuo.handwriting.RatingBarDialog;
import com.yinshuo.handwriting.SignNameDialog;
import com.yinshuo.keyboard.KeyboardUtil;
import com.yinshuo.utils.BitMapUtil;
import com.yinshuo.utils.BulkEnum;
import com.yinshuo.utils.FileHelper;
import com.yinshuo.utils.FileUtil;
import com.yinshuo.utils.MyUtil;
import com.yinshuo.utils.Util;
import com.yinshuo.viewpager.ViewPagerActivity;
public class UsbConnect extends Activity {

    DataReceiver dataReceiver;//BroadcastReceiver对象  
	private ImageView iv_Show_Image = null;  
    private int index = 0;  
    private EditText edit; 
    KeyboardUtil mKeyboard;
    private Boolean mIsRunningAdv = true;  
    private ArrayList<String> ImageList;

    private Bitmap mSignBitmap;
	private String signPath;
	private ImageView ivSign;
	private RatingBarDialog ratingBarDialog;
	private SignNameDialog signNameDialog;
	private boolean visible = true;
	
	
	
	
    @SuppressLint("NewApi")
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题  
        super.onCreate(savedInstanceState);  
        Util.setSystemBarVisible(this, false); 
        setContentView(R.layout.main);  
        this.iv_Show_Image = (ImageView) this.findViewById(R.id.iv_show_image);  
 
        
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
		
		mKeyboard = new KeyboardUtil(UsbConnect.this, UsbConnect.this, edit);
		


		Intent intent = new Intent();
		
		intent.setAction("android.intent.action.SCREEN_OFF1");

		this.sendBroadcast(intent);
		
    }  

	private class DataReceiver extends BroadcastReceiver {// 继承自BroadcastReceiver的子类
		@Override
		public void onReceive(Context context, Intent intent) {// 重写onReceive方法
			int data = intent.getIntExtra("data", 0);
			Log.i("sc", "Service的数据为:" + data);
			if (data == BulkEnum.ADV_STATUS.ADV_START) {	         	

				 intent.setComponent(new ComponentName("com.yinshuo",   "com.yinshuo.viewpager.ViewPagerActivity"));  
	              //intent = new Intent(UsbConnect.this, ViewPagerActivity.class);   
	             intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);   
	             startActivity(intent); 
				
			} else if (data == BulkEnum.ADV_STATUS.ADV_STOP) {
				mIsRunningAdv = false;
				iv_Show_Image.setVisibility(View.INVISIBLE);
			 }else if(data==BulkEnum.KEYBOARD_STATUS.KEYBOARD_OPEN){
				 mKeyboard.showKeyboard();
				 mIsRunningAdv = false;
				 iv_Show_Image.setVisibility(View.INVISIBLE);
				 edit.getText().clear();
				
			 }else if(data==BulkEnum.KEYBOARD_STATUS.KEYBOARD_CLOSE){
				 mKeyboard.hideKeyboard();
			} else if (data == BulkEnum.SIGN_STATUS.SIGN_OPEN) {
				if (signNameDialog == null) {
					signNameDialog = new SignNameDialog(UsbConnect.this,
							new DialogListener() {
								@Override
								public void refreshActivity(Object object) {
									mSignBitmap = (Bitmap) object;
									signPath = createFile();				
									byte[] filebytes;
									try {
										filebytes = FileHelper.readFile(signPath);
										Log.i("sc", "fileszie = " + filebytes.length);
										byte[] filelength = new byte[4];  // 将整数转成4字节byte数组 
										filelength = MyUtil.intToByte(filebytes.length);										
										ThreadReadWriterIOSocket.getOut().write(filelength);

										System.out.println("write data to android");
										ThreadReadWriterIOSocket.getOut().write(filebytes);
										ThreadReadWriterIOSocket.getOut().flush();
										System.out.println("*********");
										System.out.println("=============================================");

									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}				
								}
							});
				}
				if (!signNameDialog.isShowing()) {
					signNameDialog.show();
				}

			} else if (data == BulkEnum.EVALUTE_STATUS.EVALUTE_OPEN) {
				if (ratingBarDialog == null) {
					ratingBarDialog = new RatingBarDialog(UsbConnect.this,
							new RatDialogInterface() {
								@Override
								public void refreshActivity(float num) {

								}
							});
				}
				if (!ratingBarDialog.isShowing()) {
					ratingBarDialog.show();
				}
			}
			 
			
			 
		}
	}

	@Override
	protected void onStart() {// 重写onStart方法
		dataReceiver = new DataReceiver();
		IntentFilter filter = new IntentFilter();// 创建IntentFilter对象
		filter.addAction("com.yinshuo.dataReceiver");
		registerReceiver(dataReceiver, filter);// 注册Broadcast Receiver
		super.onStart();
	}

	@Override
	protected void onStop() {// 重写onStop方法
		unregisterReceiver(dataReceiver);// 取消注册Broadcast Receiver
		super.onStop();
	}
    


	/**
	 * 创建手写签名文件
	 * 
	 * @return
	 */
	private String createFile() {
		ByteArrayOutputStream baos = null;
		String _path = null;
		try {
			
			   
			File destDir = new File("data/data/com.yinshuo/signFiles");
			  if (!destDir.exists()) {
			   destDir.mkdirs();
			  }
			
			String sign_dir = destDir.getAbsolutePath() + File.separator;			
			_path = sign_dir + System.currentTimeMillis() + ".jpg";
			baos = new ByteArrayOutputStream();
			mSignBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] photoBytes = baos.toByteArray();
			if (photoBytes != null) {
				new FileOutputStream(new File(_path)).write(photoBytes);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return _path;
	}
    
}
