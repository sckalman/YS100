package com.yinshuo.viewpager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SynthesizerListener;
import com.yinshuo.R;
import com.yinshuo.device.AdminReceiver;
import com.yinshuo.handler.MiNaIOHandler;
import com.yinshuo.handwriting.ConfirmDialog;
import com.yinshuo.handwriting.ConfirmDialog.ListenerThree;
import com.yinshuo.handwriting.DialogListener;
import com.yinshuo.handwriting.RatDialogInterface;
import com.yinshuo.handwriting.RatingBarDialog;
import com.yinshuo.handwriting.SignNameDialog;
import com.yinshuo.keyboard.KeyboardUtil;
import com.yinshuo.utils.BulkEnum;
import com.yinshuo.utils.FileUploadRequest;
import com.yinshuo.utils.FileUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author frankiewei
 * ViewPager控件使用的Demo.
 */
public class ViewPagerActivity extends Activity {
	
	private static String TAG = "sc"; 	
    private int oldPosition = 0;//记录上一次点的位置
    private int currentItem; //当前页面
    private ScheduledExecutorService scheduledExecutorService;  
	private ArrayList<String> ImageList;
	NioSocketAcceptor acceptor;
	private ViewPagerAdapter mViewPagerAdapter;  //适配器.
	private JSONArray mJsonArray;  //数据源.

	private ArrayList<View> dots;
	private DataReceiver dataReceiver;//BroadcastReceiver对象  
	private KeyboardUtil mKeyboard;
	private EditText edit; 
	RelativeLayout keyboardLayout;
	private SharedPreferences mSharedPreferences;
	
	
	private SpeechSynthesizer mTts; // 语音合成对象
	private Toast mToast;
	public static String SPEAKER = "speaker";
	
	private Bitmap mSignBitmap;
	private String signPath;
	private ImageView ivSign;
	private SignNameDialog signNameDialog;
	private RatingBarDialog ratingBarDialog;
	private JazzyViewPager mJazzyViewPager;
	private JazzyViewPager.TransitionEffect mTransitionEffect = JazzyViewPager.TransitionEffect.RotateDown;
	
	
	private DevicePolicyManager dpm;
	private ComponentName componentName ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);
        ImageList  = getPictureList(new File("/data/data/yinshuo/"));
        //显示的点
        dots = new ArrayList<View>();
        seDefaultImageList();
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
		
		mKeyboard = new KeyboardUtil(ViewPagerActivity.this, ViewPagerActivity.this, edit);
		mKeyboard.hideKeyboard();
		edit.getText().clear();	
		mSharedPreferences = getSharedPreferences("com.iflytek.setting", Activity.MODE_PRIVATE);
		// 初始化合成对象
 		mTts = new SpeechSynthesizer(this, mTtsInitListener);
 		
 		mToast = Toast.makeText(this,"",Toast.LENGTH_LONG); 	
 		
 		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        Log.i("sc", dm.widthPixels+"------dm.widthPixels");
        Log.i("sc", dm.heightPixels+"-----dm.heightPixels");
	/*	if (ratingBarDialog == null) {
			ratingBarDialog = new RatingBarDialog(ViewPagerActivity.this,
					new RatDialogInterface() {
						@Override
						public void refreshActivity(float num) {

						}
					});
		}
		   if (signNameDialog!=null&&signNameDialog.isShowing()) {
			   signNameDialog.dismiss();
			    }
		
		if (!ratingBarDialog.isShowing()) {
			ratingBarDialog.show();
		}*/
		
     /*   long uptime = SystemClock.elapsedRealtime(); 
     //  mUptime.setText(); 
        
        Log.i("sc", DateUtils.formatElapsedTime(uptime / 1000));
        */
    	setTTSParam();
		ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList< RunningServiceInfo>   list =  (ArrayList<RunningServiceInfo>) activityManager.getRunningServices(100);
		
		Log.i("sc", "service size "+list.size()); 
		if(dpm==null){
			dpm  = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
	    	componentName = new ComponentName(this, AdminReceiver.class);
	    	startDeviceManager(); 
		}
    }
    
	/**
	 * 启动设备管理权限
	 */
	private void startDeviceManager(){
		//添加一个隐式意图，完成设备权限的添加 
		//这个Intent (DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)跳转到 权限提醒页面
		//并传递了两个参数EXTRA_DEVICE_ADMIN 、 EXTRA_ADD_EXPLANATION
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		
		//权限列表
		//EXTRA_DEVICE_ADMIN参数中说明了用到哪些权限，  
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        
        //描述(additional explanation)
        //EXTRA_ADD_EXPLANATION参数为附加的说明 
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "请点击激活按钮，激活设备的控制权限");
        
        startActivityForResult(intent, 0);
        
        Log.i("sc","------------startDeviceManager");
        
		}
	
	/**
	 * 禁用设备管理权限方法实现
	 */
	private void stopDeviceManager(){
		Log.i("XiaoMaGuo","------ unActiveManage ------");
		boolean active = dpm.isAdminActive(componentName);
        if (active) {
        	dpm.removeActiveAdmin(componentName);
        }
	}
	/**
	 * 调用系统锁方法实现
	 */
	private void sysLock(){
        boolean active = dpm.isAdminActive(componentName);
        if (active) {
        	dpm.lockNow();
        }
	}
    
    public static final int PORT = 10087; 
    
    public void MemoryMonitor() throws IOException {
    	
    	acceptor = new NioSocketAcceptor();
        acceptor.setHandler(new MiNaIOHandler(ViewPagerActivity.this));
        acceptor.getSessionConfig().setReuseAddress(true);
        acceptor.bind(new InetSocketAddress(PORT));
        System.out.println("UDPServer listening on port " + PORT);
    }
    
    private void initImageList(JSONArray paramJSONArray)
    {
      LinearLayout dot_layout = (LinearLayout)findViewById(R.id.dot_layout);
      LinearLayout.LayoutParams LP_FW = new LinearLayout.LayoutParams(5, 5);
      LP_FW.leftMargin = 2;
      LP_FW.rightMargin = 2;
      this.dots.clear();
      dot_layout.removeAllViews();
      
      for(int i=0;i<paramJSONArray.length();i++){
    	  
    	  ImageView localImageView = new ImageView(this);
          localImageView.setBackgroundResource(R.drawable.dot_normal);
          localImageView.setLayoutParams(LP_FW);
          this.dots.add(localImageView);
          dot_layout.addView(localImageView); 
      }
      
      this.oldPosition = 0;
      this.mJazzyViewPager = ((JazzyViewPager)findViewById(R.id.viewpager));
      this.mViewPagerAdapter = new ViewPagerAdapter(this, paramJSONArray, this.mJazzyViewPager);
      this.mJazzyViewPager.setTransitionEffect(this.mTransitionEffect);
      Log.i("sc", this.mTransitionEffect.name());
      this.mJazzyViewPager.setAdapter(this.mViewPagerAdapter);
      this.mJazzyViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() 
      {
    	  
    	  @Override
          public void onPageScrolled(int arg0, float arg1, int arg2) {
              // TODO Auto-generated method stub    
          }
          @Override
          public void onPageScrollStateChanged(int arg0) {
              // TODO Auto-generated method stub
              
          }   
          @Override
          public void onPageSelected(int position) {         
              dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
              dots.get(position).setBackgroundResource(R.drawable.dot_focused);  
              oldPosition = position;
              currentItem = position;
          }
      });
      if (this.dots.size() > 0) {
          ((View)this.dots.get(0)).setBackgroundResource(R.drawable.dot_focused);
        }
      
      try
      {
        Field localField = ViewPager.class.getDeclaredField("mScroller");
        localField.setAccessible(true);
        AccelerateDecelerateInterpolator localAccelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
        FixedSpeedScroller localFixedSpeedScroller = new FixedSpeedScroller(this.mJazzyViewPager.getContext(), localAccelerateDecelerateInterpolator);
        localField.set(this.mJazzyViewPager, localFixedSpeedScroller);
        return;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        ImageView localImageView;
        return;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        return;
      }
      catch (NoSuchFieldException localNoSuchFieldException) {
    	  
      }
    }
    
    private void seDefaultImageList()
    {
      JSONArray localJSONArray = new JSONArray();
      for(int i=0;i<ImageList.size();i++){
    	    JSONObject localJSONObject = new JSONObject();
             try {
				localJSONObject.put("path", this.ImageList.get(i));
				localJSONObject.put("name", "Album " + i);
		        localJSONArray.put(localJSONObject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
      }
      initImageList(localJSONArray);
    }
    
    
    /**
	 * 判断文件夹下是否有图片,深度遍历
	 * @param file
	 * @return
	 */
	private ArrayList<String> getPictureList(File file) {
		ImageList = new ArrayList<String>();
		File[] files = file.listFiles();
		if (null!=files && files.length > 0) {
			for (File file2 : files) {
				if (file2.isDirectory()) {
					if (getPictureList(file2).size()>0){

					}

				} else {
					if (FileUtil.isPicture(file2)){
						ImageList.add(file2.getAbsolutePath());
						//Log.i("sc", file2.getAbsolutePath());
					}
				}
			}
		}
		return ImageList;
	}

    
    //切换图片
    private class ViewPagerTask implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            currentItem = (currentItem +1) % ImageList.size();
            //更新界面
//            handler.sendEmptyMessage(0);
            handler.obtainMessage().sendToTarget();
        }
        
    }
    
    private Handler handler = new Handler()
    {
      public void handleMessage(Message msg)
      {
        mJazzyViewPager.setCurrentItem(currentItem);
        
        Log.i("sc", currentItem+"-----------currentItem");
      }
    };


	@Override
	protected void onStart() {// 重写onStart方法
        // TODO Auto-generated method stub
        super.onStart();
        
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        
        //每隔2秒钟切换一张图片
        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 10, 10, TimeUnit.SECONDS);
		
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(null!=mTts){
			mTts.destory();
		}
		//acceptor.dispose(true);
		super.onDestroy();
	}


	private class DataReceiver extends BroadcastReceiver {// 继承自BroadcastReceiver的子类
		@Override
		public void onReceive(Context context, Intent intent) {// 重写onReceive方法
			int data = intent.getIntExtra("data", 0);
			Log.i("sc", "Service的数据为:" + data);
			//pManager.userActivity(100, false);
		
			switch(data){
			case BulkEnum.ADV_STATUS.ADV_DEFAULT:
				closeAllDialog();
				mTransitionEffect  = JazzyViewPager.TransitionEffect.values()[intent.getIntExtra("TransitionEffect", 0)];			
				Log.i("sc", mTransitionEffect+"-----------mTransitionEffect");
				seDefaultImageList();
				intent.setComponent(new ComponentName("com.yinshuo", "com.yinshuo.viewpager.ViewPagerActivity"));
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);   
				startActivity(intent); 
	            mKeyboard.hideKeyboard();
	            break;
			case BulkEnum.ADV_STATUS.ADV_START:
				closeAllDialog();
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(intent.getStringExtra("jsonObject"));
					initImageList(jsonObject.getJSONArray("jsonImgArray"));
				} catch (JSONException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				 intent.setComponent(new ComponentName("com.yinshuo",   "com.yinshuo.viewpager.ViewPagerActivity"));    
	             intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);   
	             startActivity(intent); 
	             mKeyboard.hideKeyboard();
				break;
				
			case BulkEnum.ADV_STATUS.ADV_STOP:
				
				break;
				
			case BulkEnum.KEYBOARD_STATUS.KEYBOARD_OPEN:
				closeAllDialog();
				 mTts.startSpeaking("请输入密码", mTtsListener);
				mKeyboard.showKeyboard();			
				 edit.getText().clear();	 
				break;
			case BulkEnum.KEYBOARD_STATUS.KEYBOARD_CLOSE:
				mKeyboard.hideKeyboard();
				break;
			case BulkEnum.OTHER_MODULE.TTS_TRANSFER:
				String content = intent.getStringExtra("content");
			
				int code = mTts.startSpeaking(content, mTtsListener);
				if (code != 0) {
					showTip("start speak error : " + code);
				} else
					showTip("start speak success.");
				break;
			case BulkEnum.OTHER_MODULE.NEW_DIALOG:
			 	 jsonObject = null;
				try {
					jsonObject = new JSONObject(intent.getStringExtra("jsonObject"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
		 		final ConfirmDialog dialog3 = new ConfirmDialog(ViewPagerActivity.this,R.style.MyDialog,jsonObject);
				ListenerThree listener3 = new ListenerThree()
				{
					@Override 
			         public void onClick(View view) 
			         {
			        	 switch(view.getId()){
			        	 case R.id.dialog_button_ok:
			        		 Toast.makeText(ViewPagerActivity.this, "点击了OK Button", Toast.LENGTH_LONG).show();
			        		 break;
			        	 case R.id.dialog_button_cancle:
			        		 dialog3.dismiss();
			        		 Toast.makeText(ViewPagerActivity.this, "点击了Cancle Button", Toast.LENGTH_LONG).show();
			        		 break;
			        	 }
			         }
				};
				dialog3.SetListener(listener3);
				dialog3.show();
				break;
			case BulkEnum.SIGN_STATUS.SIGN_OPEN:
				 mTts.startSpeaking("请您在此处签名", mTtsListener);
				 mKeyboard.hideKeyboard();
					if (signNameDialog == null) {
						signNameDialog = new SignNameDialog(ViewPagerActivity.this,
								new DialogListener() {
									@Override
									public void refreshActivity(Object object) {
										mSignBitmap = (Bitmap) object;
										signPath = createFile();				

										try {
											IoSession ioSession = MiNaIOHandler.getmSession();  
											FileUploadRequest request = new FileUploadRequest();
											request.setFilename("aa.jpg");
											request.setHostname("localhost");				
											byte[] a = new byte[4096];
											FileInputStream fis = new FileInputStream(new File(signPath));
											while (fis.read(a, 0, a.length) != -1) {
												
												request.setFileContent(a);
												ioSession.write(request);
											}
										
											JSONObject jsonObject = new JSONObject();
											jsonObject.put("cmd", BulkEnum.SIGN_STATUS.SIGN_RECV_CMD_FINISH);
										    ioSession.write(jsonObject.toString());		   
											fis.close();
											System.out.println("=============================================");
										} catch (IOException e1) {
											e1.printStackTrace();
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								});
					}
					if(ratingBarDialog!=null&&ratingBarDialog.isShowing()){
						ratingBarDialog.dismiss();
					}
					
					if (!signNameDialog.isShowing()) {
						signNameDialog.show();
					}
				break;
			case BulkEnum.EVALUTE_STATUS.EVALUTE_OPEN:
				 mTts.startSpeaking("请您对本次服务进行评价", mTtsListener);
				if (ratingBarDialog == null) {
					ratingBarDialog = new RatingBarDialog(ViewPagerActivity.this,
							new RatDialogInterface() {
								@Override
								public void refreshActivity(float num) {

								}
							});
				}
				   if (signNameDialog!=null&&signNameDialog.isShowing()) {
					   signNameDialog.dismiss();
					    }
				
				if (!ratingBarDialog.isShowing()) {
					ratingBarDialog.show();
				}
				break;
			case BulkEnum.DEVICE_STATUS.DEVICE_SCREEN_CLOSE:
				sysLock();
				
				break;
				
			case BulkEnum.DEVICE_STATUS.DEVICE_SCREEN_OPEN:
				        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE); // //得到键盘锁管理器对象
				        KeyguardLock kl = km.newKeyguardLock("unLock");  ////参数是LogCat里用的Tag
				        kl.disableKeyguard();    //解锁
				        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");  ////获取电源管理器对象
				        ////获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是LogCat里用的Tag
				        wl.acquire();//点亮屏幕
				        wl.release();//释放
				     //   pm.userActivity(SystemClock.currentThreadTimeMillis()+10000, true);
				        Log.i("sc", "it works???");
				        break;
			}		 
		}
	}
	
	private void closeAllDialog()
	  {
	    if ((this.ratingBarDialog != null) && (this.ratingBarDialog.isShowing())) {
	      this.ratingBarDialog.dismiss();
	    }
	    if ((this.signNameDialog != null) && (this.signNameDialog.isShowing())) {
	      this.signNameDialog.dismiss();
	    }
	  }
	
	/**
     * 初期化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {

		@Override
		public void onInit(ISpeechModule arg0, int code) {
			Log.d(TAG, "InitListener init() code = " + code);
        	if (code == ErrorCode.SUCCESS) {
        		//((Button)findViewById(R.id.tts_play)).setEnabled(true);
        	}
		}
    };
        
    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {
        @Override
        public void onBufferProgress(int progress) throws RemoteException {
        	 Log.d(TAG, "onBufferProgress :" + progress);
//        	 showTip("onBufferProgress :" + progress);
        }

        @Override
        public void onCompleted(int code) throws RemoteException {
        	Log.d(TAG, "onCompleted code =" + code);
            showTip("onCompleted code =" + code);
        }

        @Override
        public void onSpeakBegin() throws RemoteException {
            Log.d(TAG, "onSpeakBegin");
            showTip("onSpeakBegin");
        }

        @Override
        public void onSpeakPaused() throws RemoteException {
        	 Log.d(TAG, "onSpeakPaused.");
        	 showTip("onSpeakPaused.");
        }

        @Override
        public void onSpeakProgress(int progress) throws RemoteException {
        	Log.d(TAG, "onSpeakProgress :" + progress);
        	showTip("onSpeakProgress :" + progress);
        }

        @Override
        public void onSpeakResumed() throws RemoteException {
        	Log.d(TAG, "onSpeakResumed.");
        	showTip("onSpeakResumed");
        }
    };
    
    private void showTip(final String str){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mToast.setText(str);
				mToast.show();
		    }
		});
	}
    

	/**
	 * 参数设置
	 * @param param
	 * @return 
	 */
	private void setTTSParam(){
		mTts.setParameter(SpeechConstant.ENGINE_TYPE,
				mSharedPreferences.getString("engine_preference", "local"));
		
		if(mSharedPreferences.getString("engine_preference", "local").equalsIgnoreCase("local")){
			mTts.setParameter(SpeechSynthesizer.VOICE_NAME,
					mSharedPreferences.getString("role_cn_preference", "xiaoyan"));
		}else{
			mTts.setParameter(SpeechSynthesizer.VOICE_NAME,
					mSharedPreferences.getString("role_cn_preference", "xiaoyan")); 
		}
		mTts.setParameter(SpeechSynthesizer.SPEED,
				mSharedPreferences.getString("speed_preference", "50"));
		
		mTts.setParameter(SpeechSynthesizer.PITCH,
				mSharedPreferences.getString("pitch_preference", "50"));
		
		mTts.setParameter(SpeechSynthesizer.VOLUME,
				mSharedPreferences.getString("volume_preference", "50"));
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

