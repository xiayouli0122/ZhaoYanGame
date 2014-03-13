package com.zhaoyan.game.dialog;

import com.zhaoyan.game.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class ConfirmDialog extends Dialog implements android.view.View.OnClickListener {
	private ImageView mConfirmBtn,mCancelBtn;
	private ZYOnClickListener mListener;

	public ConfirmDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ConfirmDialog(Context context, int layout, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
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
		System.out.println("height=" + height + ",width=" + width);
		
		params.height = (int) (height * 0.35);
		params.width = (int) (width * 0.8);
		params.gravity = Gravity.CENTER;
		
		window.setAttributes(params);
	}

	public ConfirmDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
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
