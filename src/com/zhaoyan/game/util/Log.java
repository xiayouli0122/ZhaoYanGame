package com.zhaoyan.game.util;

public class Log {
	public static final boolean isDebug = true;
	private static final String TAG = "zhaoyan/";

	public static void v(String tag, String message) {
		if (isDebug) {
			android.util.Log.v(TAG + tag, message);
		}
	}

	public static void i(String tag, String message) {
		if (isDebug) {
			android.util.Log.i(TAG + tag, message);
		}
	}

	public static void d(String tag, String message) {
		if (isDebug) {
			android.util.Log.d(TAG + tag, message);
		}
	}

	public static void w(String tag, String message) {
		if (isDebug) {
			android.util.Log.w(TAG + tag, message);
		}
	}

	public static void e(String tag, String message) {
		if (isDebug) {
			android.util.Log.e(TAG + tag, message);
		}
	}

}
