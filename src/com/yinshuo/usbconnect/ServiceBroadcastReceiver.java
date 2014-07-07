package com.yinshuo.usbconnect;

import com.yinshuo.handler.MinaIOService;
import com.yinshuo.keyboard.KeyboardActivity;
import com.yinshuo.utils.BulkEnum;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

public class ServiceBroadcastReceiver extends BroadcastReceiver {

	private static String START_ACTION = "NotifyServiceStart";
	private static String STOP_ACTION = "NotifyServiceStop";
	private static String START_APP = "NotifyAppStart";
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(androidService.TAG, Thread.currentThread().getName() + "---->"  + "ServiceBroadcastReceiver onReceive");  
  
        String action = intent.getAction();  
        if (START_ACTION.equalsIgnoreCase(action))
        {  
            //context.startService(new Intent(context, androidService.class));

            context.startService(new Intent(context, MinaIOService.class));  
            Log.d(androidService.TAG, Thread.currentThread().getName() + "---->"  + "ServiceBroadcastReceiver onReceive start end");  
        }
        else if (STOP_ACTION.equalsIgnoreCase(action)) 
        {  
            context.stopService(new Intent(context, MinaIOService.class));   
           // context.stopService(new Intent(context, androidService.class));  
            Log.d(androidService.TAG, Thread.currentThread().getName() + "---->"  + "ServiceBroadcastReceiver onReceive stop end");  
        }
        
        
        
        
        else if (START_APP.equalsIgnoreCase(action)) 
        {     
        	 Log.d(androidService.TAG, Thread.currentThread().getName() + "---->"  + "start APP");  
        	 intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        	 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             intent.setComponent(new ComponentName("com.yinshuo",   "com.yinshuo.usbconnect.UsbConnect"));  
             context.startActivity(intent); 
        } 
        else if ("com.yinshuo.dataReveiver".equalsIgnoreCase(action)) 
        {     
        	int data = intent.getIntExtra("data", 0);
			Log.i("sc", "Service的数据为:" + data);
			if (data == BulkEnum.ADV_STATUS.ADV_START) {	         	
				 intent.setComponent(new ComponentName("com.yinshuo",   "com.yinshuo.viewpager.ViewPagerActivity"));  
	             intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
	             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	             context.startActivity(intent); 		
			}else if(data==BulkEnum.KEYBOARD_STATUS.KEYBOARD_OPEN){		 
				 Log.i("sc", "201");
				 intent.setComponent(new ComponentName("com.yinshuo",   "com.yinshuo.keyboard.KeyboardActivity"));  
				 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				 intent.putExtra("reload", true);
				 context.startActivity(intent); 	
				
			 }
        } 
	}

}
