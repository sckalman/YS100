package com.yinshuo.usbconnect;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class androidService extends Service
{

	public static final String TAG = "sc";
	public static Boolean mainThreadFlag = true;
	public static Boolean ioThreadFlag = true;
	ServerSocket serverSocket = null;
	final int SERVER_PORT = 10086;
	File testFile;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.d(TAG, "androidService--->onCreate()"); 
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d("chl", "androidService----->onStartCommand()");
		mainThreadFlag = true;
		new Thread()
		{
			public void run()
			{
				doListen();
			};
		}.start();
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		// 关闭线程
		mainThreadFlag = false;
		ioThreadFlag = false;
		// 关闭服务器
		try
		{
			Log.v(TAG, Thread.currentThread().getName() + "---->" + "serverSocket.close()");
			if (serverSocket != null) serverSocket.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		Log.v(TAG, Thread.currentThread().getName() + "---->" + "**************** onDestroy****************");
	}

	private void doListen()
	{
		serverSocket = null;
		try
		{
			Log.d("chl", "doListen()");
			serverSocket = new ServerSocket(SERVER_PORT);
			Log.d("chl", "doListen() 2");
			/*while (mainThreadFlag)
			{
				Log.d("chl", "doListen() 4");
				Socket socket = serverSocket.accept();
				Log.d("chl", "doListen() 3");
				new Thread(new ThreadReadWriterIOSocket(this, socket)).start();
			}*/
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
