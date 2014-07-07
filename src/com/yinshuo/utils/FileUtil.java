package com.yinshuo.utils;

import java.io.File;

import android.util.Log;


public class FileUtil {


	public static String[] indexs = { "bmp", "pcx", "tiff", "gif", "jpeg", "jpg",
			"tga", "exif", "fpx", "svg", "psd", "cdr", "pcd", "dxf", "ufo",
			"eps", "png", "hdri", "ai", "raw" };
	/**
	 * 判断文件是否是图片文件
	 * @param file
	 * @return
	 */
	public static boolean isPicture(File file) {
		int start = file.getName().lastIndexOf(".");
		int end = file.getName().length();
		if (start != -1) {
			String indexName = file.getName().substring(start + 1, end);
			if (inIndexs(indexName)) {
				return true;
			}
		} else {
			Log.i("myTag", "没有后缀名的文件");
		}
		return false;
	}

	/**
	 * 判断后缀名是否是图片后缀名
	 * @param index
	 * @return
	 */
	private static boolean inIndexs(String index) {
		for (String string : FileUtil.indexs) {
			if (index.equals(string)) {
				return true;
			}
		}
		return false;
	}

}
