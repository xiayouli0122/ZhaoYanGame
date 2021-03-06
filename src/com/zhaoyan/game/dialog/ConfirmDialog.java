package com.zhaoyan.game.dialog;

import com.zhaoyan.game.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class ConfirmDialog extends Dialog implements android.view.View.OnClickListener {
	private ImageView mConfirmBtn,mCancelBtn;
	private ZYOnClickListener mListener;

	public ConfirmDialog(Context context) {
		super(context);
	}

	public ConfirmDialog(Context context, int layout, int theme) {
		super(context, theme);
		setContentView(layout);
		
		mConfirmBtn = (ImageView) findViewById(R.id.btn_confirm);
		mCancelBtn = (ImageView) findViewById(R.id.btn_cancel);
		mConfirmBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		
		//set window parmars
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		
		//get height & width
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		int height = display.getHeight();
		int width = display.getWidth();
		
//		params.height = (int) (height * 0.35);
		params.height = LayoutParams.WRAP_CONTENT;
		params.width = (int) (width * 0.8);
		params.gravity = Gravity.CENTER;
		
		window.setAttributes(params);
	}

	public ConfirmDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}
	
	public void setOnClickListener(ZYOnClickListener listener){
		mListener = listener;
	}
	
	public interface ZYOnClickListener{
		public void onClick(Dialog dialog, View view);
	}

	@Override
	public void onClick(View v) {
		mListener.onClick(this, v);
	}
	

}
