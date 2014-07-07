package com.yinshuo.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

@SuppressLint("NewApi")
public class Util {
	/**
	 * 设置系统栏可见性
	 */
	public static void setSystemBarVisible(final Activity context,boolean visible) {
		int flag = context.getWindow().getDecorView().getSystemUiVisibility();
//		int fullScreen = View.SYSTEM_UI_FLAG_SHOW_FULLSCREEN;
		int fullScreen = 0x8;
		if(visible) {
			if((flag & fullScreen) != 0) {
				context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			}
		} else {
			if((flag & fullScreen) == 0) {
				context.getWindow().getDecorView().setSystemUiVisibility(flag | fullScreen);
			}
		}
	}
	
	/**
	 * 判断状态栏是否显示
	 */
	public static boolean isSystemBarVisible(final Activity context) {
		int flag = context.getWindow().getDecorView().getSystemUiVisibility();
//		return (flag & View.SYSTEM_UI_FLAG_SHOW_FULLSCREEN) != 0; 
		return (flag & 0x8) == 0;
	}
	
	public static int dpToPx(Resources res, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
	}

}
