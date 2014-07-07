package com.yinshuo.usbconnect;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.yinshuo.utils.BulkEnum;
import com.yinshuo.utils.FileHelper;
import com.yinshuo.utils.MyUtil;

public class ThreadReadWriterIOSocket implements Runnable
{
	private Socket client;
	private Context context;
	public static String TAG = "sc";
	static BufferedOutputStream out;
	BufferedInputStream in;
	
	public ThreadReadWriterIOSocket(Context context, Socket client)
	{
		this.client = client;
		this.context = context;		
	}
	
	public static BufferedOutputStream getOut() {
		return out;
	}

	public void setOut(BufferedOutputStream out) {
		this.out = out;
	}

	@Override
	public void run()
	{
		Log.d("chl", "a client has connected to server!");
		
		try
		{
			out = new BufferedOutputStream(client.getOutputStream());
			Log.i("sc", "out"+out);
			in = new BufferedInputStream(client.getInputStream());
			/* PC端发来的数据msg */
			int currCMD = 0;
			
			androidService.ioThreadFlag = true;
			
			Intent intent = new Intent();//创建Intent对象  
            intent.setAction("com.yinshuo.dataReceiver");  
			
			while (androidService.ioThreadFlag)
			{
				try
				{
					if (!client.isConnected())
					{
						break;
					}
					/* 接收PC发来的数据 */ 
					Log.v(androidService.TAG, Thread.currentThread().getName() + "---->" + "will read......");
					/* 读操作命令 */
					String jsonString = readCMDFromSocket(in);
					
					
					JSONObject jsonObject = new JSONObject(jsonString);
					currCMD = jsonObject.getInt("cmd");
					
					
					Log.v(androidService.TAG, Thread.currentThread().getName() + "---->" + "**currCMD ==== " + currCMD+jsonString);
					
					switch(currCMD){
					
					case BulkEnum.DEVICE_STATUS.DEVICE_GET_ID:// 得到设备的ID
						
						 Log.i("chl", "---------tmDevice");
						final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
						 
					    final String tmDevice;
					    tmDevice = tm.getDeviceId();
					    Log.i("chl", tmDevice+"---------tmDevice");
					    out.write(tmDevice.getBytes());
						out.flush();
					
					break;
					case BulkEnum.ADV_STATUS.ADV_START:

						Log.i("sc", "ADV_START ");
						intent.putExtra("data", BulkEnum.ADV_STATUS.ADV_START);
						context.sendBroadcast(intent);// 发送广播
						Log.i("sc", "ADV_START ");
						out.write("ADV_START ".getBytes());
						out.flush();
					
					break;
					case BulkEnum.ADV_STATUS.ADV_STOP:
						intent.putExtra("data", BulkEnum.ADV_STATUS.ADV_STOP);  
                        context.sendBroadcast(intent);//发送广播  
						out.write("ADV_STOP".getBytes());
						out.flush();
						
					break;
					case BulkEnum.KEYBOARD_STATUS.KEYBOARD_OPEN://打开键盘
						intent.putExtra("data", BulkEnum.KEYBOARD_STATUS.KEYBOARD_OPEN);  
                        context.sendBroadcast(intent);//发送广播  
						out.write("KEYBOARD_NUM-----------".getBytes());
						out.flush();
						
					break;
					case BulkEnum.KEYBOARD_STATUS.KEYBOARD_CLOSE: //关闭键盘
						intent.putExtra("data", BulkEnum.KEYBOARD_STATUS.KEYBOARD_CLOSE);  
                        context.sendBroadcast(intent);//发送广播  
						out.write("KEYBOARD_CLOSE".getBytes()); 
						out.flush();
						
					break;
					
					case BulkEnum.KEYBOARD_STATUS.KEYBOARD_SET_TIMEOUT: //设置键盘超时
						
						int  timeOut = jsonObject.getInt("time");		
						SharedPreferences sp = context.getSharedPreferences("setting", Context.MODE_APPEND);  
				        //获取到编辑对象  
				        SharedPreferences.Editor edit = sp.edit();  
				        //添加新的值，可见是键值对的形式添加  
				        edit.putInt("timeOut", timeOut);  
				        //提交.  
				        edit.commit();
				        
				        intent.putExtra("data", BulkEnum.KEYBOARD_STATUS.KEYBOARD_OPEN);  
                        context.sendBroadcast(intent);//发送广播  
						out.write("KEYBOARD_NUM-----------".getBytes());
						out.flush();
					break;
					case BulkEnum.DEVICE_STATUS.DEVICE_CONNECT: // 连接设备
						
						
						break;
					case BulkEnum.DEVICE_STATUS.DEVICE_IS_CONNECT: // 判断连接状态  返回 0 连接上   返回-1 连接失败  返回 -2 异常
					
					
					break;
					case BulkEnum.DEVICE_STATUS.DEVICE_STATUS_REBOOT:// 重启设备
					
					
					break;
			
					case BulkEnum.DEVICE_STATUS.DEVICE_OPEN_APP:// 打开应用程序 
					
					
					break;
					case BulkEnum.SIGN_STATUS.SIGN_OPEN:// 打开签名
					intent.putExtra("data", BulkEnum.SIGN_STATUS.SIGN_OPEN);  
                    context.sendBroadcast(intent);//发送广播  
					out.write("SIGN_START".getBytes());
					out.flush();
						
					break;
					case BulkEnum.EVALUTE_STATUS.EVALUTE_OPEN:
						intent.putExtra("data", BulkEnum.EVALUTE_STATUS.EVALUTE_OPEN);  
	                    context.sendBroadcast(intent);//发送广播  
						out.write("EVALUTE_OPEN".getBytes());
						out.flush();
							
					break;
					case BulkEnum.OTHER_MODULE.TTS_TRANSFER:
						
						String  content = jsonObject.getString("content");		
						intent.putExtra("content", content);  
						intent.putExtra("data", BulkEnum.OTHER_MODULE.TTS_TRANSFER);  
                        context.sendBroadcast(intent);//发送广播  
					break;
					case BulkEnum.OTHER_MODULE.NEW_DIALOG:
						intent.putExtra("jsonObject", jsonString);
						
						intent.putExtra("data", BulkEnum.OTHER_MODULE.NEW_DIALOG);  
                        context.sendBroadcast(intent);//发送广播  
					break;
					
				}
					
				} catch (Exception e)
				{
					Log.e(androidService.TAG, e.toString());
					e.printStackTrace();
				}
			}
			out.close();
			in.close();
		} catch (Exception e)
		{
			Log.e(androidService.TAG, e.toString());
			e.printStackTrace();
		} finally
		{
			try
			{
				if (client != null)
				{
					Log.v(androidService.TAG, Thread.currentThread().getName() + "---->" + "client.close()");
					client.close();
				}
			} catch (IOException e)
			{
				Log.e(androidService.TAG, Thread.currentThread().getName() + "---->" + "read write error333333");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 功能：从socket流中读取完整文件数据
	 * 
	 * InputStream in：socket输入流
	 * 
	 * byte[] filelength: 流的前4个字节存储要转送的文件的字节数
	 * 
	 * byte[] fileformat：流的前5-8字节存储要转送的文件的格式（如.apk）
	 * 
	 * */
	public static byte[] receiveFileFromSocket(InputStream in, OutputStream out, byte[] filelength, byte[] fileformat)
	{
		byte[] filebytes = null;// 文件数据
		try
		{
			in.read(filelength);// 读文件长度
			int filelen = MyUtil.bytesToInt(filelength);// 文件长度从4字节byte[]转成Int
			String strtmp = "read file length ok:" + filelen;
			out.write(strtmp.getBytes("utf-8"));
			out.flush();

			filebytes = new byte[filelen];
			int pos = 0;
			int rcvLen = 0;
			while ((rcvLen = in.read(filebytes, pos, filelen - pos)) > 0)
			{
				//Log.i("sc", pos+"---pos");
				pos += rcvLen;
			}
			Log.v(androidService.TAG, Thread.currentThread().getName() + "---->" + "read file OK:file size="+ filebytes.length);
			out.write("read file ok".getBytes("utf-8"));
			out.flush();
		} catch (Exception e)
		{
			Log.v(androidService.TAG, Thread.currentThread().getName() + "---->" + "receiveFileFromSocket error");
			e.printStackTrace();
		}
		return filebytes;
	}
	
	
	
	

	/* 读取命令 */
	/*public int readCMDFromSocket(InputStream in)
	{
		int ret = 0;
		String msg = "";
		byte[] tempbuffer = new byte[4];
		try
		{
			int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
			ret = MyUtil.bytesToInt(tempbuffer);
			
			tempbuffer = null;
		} catch (Exception e)
		{
			Log.v(androidService.TAG, Thread.currentThread().getName() + "---->" + "readFromSocket error");
			e.printStackTrace();
		}

		return ret;
	}*/
	
	/* 读取命令 */
	public String readCMDFromSocket(InputStream in)
	{
		int MAX_BUFFER_BYTES = 2048;
		String msg = "";
		byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
		try
		{
			int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
			msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");
			tempbuffer = null;
		} catch (Exception e)
		{
			Log.v(androidService.TAG, Thread.currentThread().getName() + "---->" + "readFromSocket error");
			e.printStackTrace();
		}
		// Log.v(Service139.TAG, "msg=" + msg);
		return msg;
	}
}