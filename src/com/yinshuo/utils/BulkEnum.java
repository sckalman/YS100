package com.yinshuo.utils;

/*
 * 传输 以byte【】 进行传输 以；进行分割，；号前的数字代表相应的 函数调用，； 之后的表示参数
 * */
public final class BulkEnum {

	public static final class ADV_STATUS{
		public static final int ADV_DEFAULT         =  100; //默认广告
		public static final int ADV_START           =  101; //广告播放
		public static final int ADV_STOP            =  102; // 停止播放广告
		public static final int ADV_STATUS          =  103; // 停止播放广告
		public static final int ADV_STATUS_NULL     =  104;  //
		public static final int ADV_SET_DIRECTION   =  105;  //
		public static final int ADV_SET_TIMEOUT     =  106;  //	
		public static final int ADV_PIC_ADD         =  107;  // 传输 PC 目录的图片文件到 Android 设备  /data/data/yinshuo 
		public static final int ADV_PIC_DELETE      =  108;  // 删除 Android 设备端的广告图片 
		
	}
	public static final class KEYBOARD_STATUS{
		public static final int KEYBOARD_OPEN            =  201; //打开键盘
		public static final int KEYBOARD_CLOSE           =  202; // 关闭键盘
		public static final int KEYBOARD_STATUS          =  203; // 得到键盘的状态
		public static final int KEYBOARD_SET_TIMEOUT     =  204; // 超时
		public static final int KEYBOARD_RECV_CMD        =  205; // 接收键盘传递的信息
	}
	public static final class SIGN_STATUS{
		public static final int SIGN_OPEN  		         =  301; //打开签名
		public static final int SIGN_CLOSE                 =  302; // 关闭签名
		public static final int SIGN_STATUS                =  303; //  得到签名的状态
		public static final int SIGN_SET_TIMEOUT         =  304; // 超时
		public static final int SIGN_RECV_CMD            =  305; // 接收签名图片
		public static final int SIGN_RECV_CMD_FINISH     =  306; // 接收签名图片结束标记
	}
	public static final class EVALUTE_STATUS{
		public static final int EVALUTE_OPEN             =  401; //打开评价
		public static final int EVALUTE_CLOSE            =  402; // 关闭评价
		public static final int EVALUTE_STATUS           =  403; // 得到评价的状态
		public static final int EVALUTE_SET_TIMEOUT      =  404; // 
		public static final int EVALUTE_RECV_CMD         =  405; // 接收评价信息
	}
	public static final class DEVICE_STATUS{
		public static final int DEVICE_CONNECT            =  500; // 连接设备
		public static final int DEVICE_IS_CONNECT         =  501; // 判断连接状态  返回 0 连接上   返回-1 连接失败  返回 -2 异常
		public static final int DEVICE_STATUS_REBOOT      =  502;  //重启设备
		public static final int DEVICE_GET_ID             =  503;  // 得到设备的ID
		public static final int DEVICE_OPEN_APP           =  504; // 打开应用程序 
		
		public static final int DEVICE_CLOSE_APP          =  505; // 打开应用程序 
		
		public static final int DEVICE_SCREEN_CLOSE       =  506; // 关闭屏幕
		
		public static final int DEVICE_SCREEN_OPEN        =  507; // 打开屏幕
		
		
	}
	public static final class OTHER_MODULE{
		public static final int TTS_TRANSFER              =  601;  // TTS 传输	
		public static final int NEW_DIALOG                =  602;  //新建提示对话框
	}
	
	
	public enum TransitionEffect {   //广告特效
		Standard,
		Tablet,
		CubeIn,
		CubeOut,
		FlipVertical,
		FlipHorizontal,
		Stack,
		ZoomIn,
		ZoomOut,
		RotateUp,
		RotateDown,
		Accordion
	}
	
	
	
	public static final class TRANSFER{
		
		public static final String CMD = "cmd";
		public static final String CONTENT = "content";
		public static final String FILE_LENGTH = "fileLength";
		public static final String FILE_NAME = "fileName";
		public static final String FILE_CONTENT = "fileContent";

	}
	
	public static final String ImagePath = "/data/data/yinshuo/";
	
	
	
	public static final class HANDLE_STATE{
		public static final int DISCONNECT_SERVICE						= 0;
		public static final int CONNECT_SERVICE						    = 1;
		public static final int DEVICE_PLUGIN							= 2;
		public static final int DEVICE_PLUGOUT							= 3;
		public static final int DEVICE_START							= 4;
		public static final int DEVICE_STOP							= 5;
		public static final int RECV_POINT							= 6;
	}
	public static final class TUNING{
		public static final int SDK_FINE_TUNING_FLAG_SAVE						= 0;
		public static final int SDK_FINE_TUNING_FLAG_RESET						= 1;
		public static final int SDK_FINE_TUNING_FLAG_TOP_STRETCH				= 2;
		public static final int SDK_FINE_TUNING_FLAG_BOTTOM_STRETCH				= 3;
		public static final int SDK_FINE_TUNING_FLAG_LEFT_STRETCH				= 4;
		public static final int SDK_FINE_TUNING_FLAG_RIGHT_STRETCH				= 5;
	}
	public static final class SYSTEM_ERROR{
		public static final int ZERROR_NULL										= 0;
		public static final int ZERROR_DEVICE_OPENFAILED						= 1;
		public static final int ZERROR_DEVICE_INFOUNINIT						= 2;
		public static final int ZERROR_DEVICE_DATAUNINIT						= 3;
		public static final int ZERROR_DEVICE_UNSUPPORT							= 10;
		public static final int ZERROR_DEVICE_OUTOFDATE							= 4;
		public static final int ZERROR_DEVICE_NOTSTABLE							= 5;
		public static final int ZERROR_DEVICE_INITINTERRUPT						= 11;
	}

}