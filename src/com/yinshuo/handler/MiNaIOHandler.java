package com.yinshuo.handler;


import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;

import com.yinshuo.utils.BulkEnum;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;


/**
 * Class the extends IoHandlerAdapter in order to properly handle
 * connections and the data the connections send
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class MiNaIOHandler extends IoHandlerAdapter {

	private static final String DEBUG_TAG = "sc";
	private Context context;
	private static IoSession mSession;
	
	/*private IoConnector connector;*/
	
    public static IoSession getmSession() {
		return mSession;
	}

	public MiNaIOHandler(Context context) {
    	this.context = context;		
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
       
        session.close(true);
        Log.d(DEBUG_TAG, "exceptionCaught : "+ cause.getMessage());
    }
    JSONObject jsonObject;
    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
    	
		Intent intent = new Intent();// 创建Intent对象
		intent.setAction("com.yinshuo.dataReceiver");
		int currCMD = 0;
		
		Log.i("sc", message.toString()+"---message.toString()");
		jsonObject = new JSONObject(message.toString());
		
		Log.i("sc", jsonObject.toString()+"---jsonObject.toString()");
		
		currCMD = jsonObject.getInt("cmd");

		switch (currCMD) {
			case BulkEnum.DEVICE_STATUS.DEVICE_GET_ID:// 得到设备的ID
				final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				final String tmDevice;
				tmDevice = tm.getDeviceId();
				Log.i("sc", tmDevice + "---------tmDevice");
				session.write(tmDevice.getBytes());
				break;
			case BulkEnum.ADV_STATUS.ADV_DEFAULT:
				intent.putExtra("data", BulkEnum.ADV_STATUS.ADV_DEFAULT);
			
				intent.putExtra("TransitionEffect", jsonObject.getInt("TransitionEffect"));
				context.sendBroadcast(intent);// 发送广播
				session.write(message.toString());
				break;
			case BulkEnum.ADV_STATUS.ADV_START:
				intent.putExtra("data", BulkEnum.ADV_STATUS.ADV_START);
				intent.putExtra("jsonObject", jsonObject.toString());
				context.sendBroadcast(intent);// 发送广播
				Log.i("sc", "ADV_START ");
				session.write(message.toString());
				break;
			case BulkEnum.ADV_STATUS.ADV_STOP:
				intent.putExtra("data", BulkEnum.ADV_STATUS.ADV_STOP);
				context.sendBroadcast(intent);// 发送广播
				session.write(message.toString());
	
				break;
			case BulkEnum.KEYBOARD_STATUS.KEYBOARD_OPEN:// 打开键盘
				intent.putExtra("data", BulkEnum.KEYBOARD_STATUS.KEYBOARD_OPEN);
				context.sendBroadcast(intent);// 发送广播
				session.write(message.toString());
	
				break;
			case BulkEnum.KEYBOARD_STATUS.KEYBOARD_CLOSE: // 关闭键盘
				intent.putExtra("data", BulkEnum.KEYBOARD_STATUS.KEYBOARD_CLOSE);
				context.sendBroadcast(intent);// 发送广播
				session.write(message.toString());
				break;
	
			case BulkEnum.KEYBOARD_STATUS.KEYBOARD_SET_TIMEOUT: // 设置键盘超时
	
				int timeOut = jsonObject.getInt("time");
				SharedPreferences sp = context.getSharedPreferences("setting",Context.MODE_APPEND);
				SharedPreferences.Editor edit = sp.edit();
				edit.putInt("timeOut", timeOut);
				edit.commit();
	
				intent.putExtra("data", BulkEnum.KEYBOARD_STATUS.KEYBOARD_OPEN);
				context.sendBroadcast(intent);// 发送广播
				session.write(message.toString());
				break;
			case BulkEnum.DEVICE_STATUS.DEVICE_CONNECT: // 连接设备
	
				break;
			case BulkEnum.DEVICE_STATUS.DEVICE_IS_CONNECT: // 判断连接状态 返回 0 连接上 返回-1
															// 连接失败 返回 -2 异常
	
				break;
			case BulkEnum.DEVICE_STATUS.DEVICE_STATUS_REBOOT:// 重启设备
	
				break;
	
			case BulkEnum.DEVICE_STATUS.DEVICE_OPEN_APP:// 打开应用程序
	
				break;
			case BulkEnum.SIGN_STATUS.SIGN_OPEN:// 打开签名
				intent.putExtra("data", BulkEnum.SIGN_STATUS.SIGN_OPEN);
				context.sendBroadcast(intent);// 发送广播
				session.write(message.toString());
				break;
			case BulkEnum.EVALUTE_STATUS.EVALUTE_OPEN:
				intent.putExtra("data", BulkEnum.EVALUTE_STATUS.EVALUTE_OPEN);
				context.sendBroadcast(intent);// 发送广播
				session.write(message.toString());
				break;
			case BulkEnum.OTHER_MODULE.TTS_TRANSFER:
	
				String content = jsonObject.getString("content");
				intent.putExtra("content", content);
				intent.putExtra("data", BulkEnum.OTHER_MODULE.TTS_TRANSFER);
				context.sendBroadcast(intent);// 发送广播
				break;
			case BulkEnum.OTHER_MODULE.NEW_DIALOG:
				
				 intent.putExtra("jsonObject", message.toString());
				 intent.putExtra("data", BulkEnum.OTHER_MODULE.NEW_DIALOG);
				 context.sendBroadcast(intent);//发送广播
				 break;
				 
			case BulkEnum.DEVICE_STATUS.DEVICE_SCREEN_CLOSE:  // 关闭屏幕
				 intent.putExtra("jsonObject", message.toString());
				 intent.putExtra("data", BulkEnum.DEVICE_STATUS.DEVICE_SCREEN_CLOSE);
				 context.sendBroadcast(intent);//发送广播
				break;
			case BulkEnum.DEVICE_STATUS.DEVICE_SCREEN_OPEN:  // 打开屏幕

				 intent.putExtra("jsonObject", message.toString());
				 intent.putExtra("data", BulkEnum.DEVICE_STATUS.DEVICE_SCREEN_OPEN);
				 context.sendBroadcast(intent);//发送广播

			       KeyguardManager km = (KeyguardManager)context. getSystemService(Context.KEYGUARD_SERVICE); // //得到键盘锁管理器对象
			        KeyguardLock kl = km.newKeyguardLock("unLock");  ////参数是LogCat里用的Tag
			        kl.disableKeyguard();    //解锁
			        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
			        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");  ////获取电源管理器对象
			        ////获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是LogCat里用的Tag
			        wl.acquire();//点亮屏幕
			        wl.release();//释放
			        Log.i("sc", "it works???");
				break;	

		}

		/******************* 方式1：解析返回字符串 start ***********************/
		if (message instanceof IoBuffer) {
			IoBuffer buffer = (IoBuffer) message;
			int len = buffer.getInt();
			byte[] bytes = new byte[len];
			buffer.get(bytes);
			String jsonString = new String(bytes);
			Log.d(DEBUG_TAG, "Session recv : " + jsonString);
		}
          /*******************方式1：解析返回字符串    end ***********************/
    }
    
    
    private void sendData(IoSession session) throws InterruptedException {
    	/*******************方式1：传递字符串    start***********************/
    	String value = (String) "server return";  
        IoBuffer buf = IoBuffer.allocate(value.getBytes().length);  
        buf.setAutoExpand(true);  
        
        buf.putInt(value.getBytes().length);
        
        if (value != null)  
            buf.put(value.trim().getBytes());  
        buf.flip();  
        
        session.write(buf);  
        /*******************方式1：传递字符串    end***********************/
    }
    

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        Log.d(DEBUG_TAG, "Session sent...");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        Log.d(DEBUG_TAG, "Session closed...");
        if(mSession!=null){
            mSession = null;
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        Log.d(DEBUG_TAG, "Session created...");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        Log.d(DEBUG_TAG, "Session idle...");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        Log.d(DEBUG_TAG, "Session opened...");
        mSession = session;
    }
}