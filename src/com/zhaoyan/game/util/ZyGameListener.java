package com.zhaoyan.game.util;

import android.os.Bundle;

public interface ZyGameListener {
	
	public static final int MSG_DELETE_WORD = 1;
	
	public static final String CALLBACK_FLAG = "callback_flag";
	public static final String KEY_ITEM_POSITION = "key_item_position";
	public static final String KEY_ITEM_ID = "key_item_id";
	
	void onCallBack(Bundle bundle);
}
