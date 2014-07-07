package com.yinshuo.usbconnect;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
	private static final String tag = "zinFrameReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub      
		 Log.d(tag, "service start");
		 Log.d(androidService.TAG, Thread.currentThread().getName() + "---->"  + "start APP");  
    	 intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         intent.setComponent(new ComponentName("com.yinshuo",   "com.yinshuo.usbconnect.UsbConnect"));  
         context.startActivity(intent);  
	}

}
