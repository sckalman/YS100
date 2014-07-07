package com.yinshuo.handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.yinshuo.viewpager.ViewPagerActivity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MinaIOService extends Service {
	public static final int PORT = 10087; 
	NioSocketAcceptor acceptor;
	//private NioSocketAcceptor acceptor;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		System.setProperty("java.net.preferIPv6Addresses", "false"); 
		
		Log.i("sc", "MinaIOService onCreate "+PORT);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(null!=acceptor){
			acceptor.dispose(true);
			acceptor = null;
		}

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(acceptor==null){
			acceptor = new NioSocketAcceptor();
			this.acceptor = new NioSocketAcceptor();
		      this.acceptor.setReuseAddress(true);
		      this.acceptor.getSessionConfig().setReuseAddress(true);
		      MiNaIOHandler localMiNaIOHandler = new MiNaIOHandler(getApplicationContext());
		      this.acceptor.setHandler(localMiNaIOHandler);
		      this.acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

	        try {
				acceptor.bind(new InetSocketAddress(PORT));
			} catch (IOException e) {
				 Log.i("sc", e.toString());
			}
	        Log.i("sc", "onStartCommand"+PORT);
		}
		
     
		return START_NOT_STICKY;
	}
	

}
