package com.zhaoyan.game.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.zhaoyan.game.R;

public class AddWordDialog extends Dialog implements android.view.View.OnClickListener {
	private ImageView mAddBtn,mCancelBtn;
	private EditText mCivilianET,mSpyET;
	private TextView mErrorMsgTv;
	private ZYOnClickListener mListener;

	public AddWordDialog(Context context) {
		super(context, R.style.AddWordDialog);
		setContentView(R.layout.dialog_add_word);
		
		mAddBtn = (ImageView) findViewById(R.id.btn_add);
		mCancelBtn = (ImageView) findViewById(R.id.btn_cancel);
		mAddBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		
		mCivilianET = (EditText) findViewById(R.id.et_civilian_word);
		mSpyET = (EditText) findViewById(R.id.et_spy_word);
		
		mErrorMsgTv = (TextView) findViewById(R.id.tv_err_msg);
		
		//set window parmars
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		
		//get height & width
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		
		params.height = LayoutParams.WRAP_CONTENT;
		params.width = (int) (width * 0.8);
		params.gravity = Gravity.CENTER;
		
		window.setAttributes(params);
	}

	public AddWordDialog(Context context, boolean cancelable,
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
	
	public String getCivilianWord(){
		return mCivilianET.getText().toString().trim();
	}
	
	public String getSpyWord(){
		return mSpyET.getText().toString().trim();
	}
	
	public void setErrorMsg(String msg){
		mErrorMsgTv.setText(msg);
	}
	
	public void setErrorMsg(int msgId){
		mErrorMsgTv.setText(msgId);
	}

}
