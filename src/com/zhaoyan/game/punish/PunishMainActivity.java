package com.zhaoyan.game.punish;

import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zhaoyan.game.BaseActivity;
import com.zhaoyan.game.R;

public class PunishMainActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "PunishMainActivity";
	
	private TextView mStateTV,mActionTV;
	private ToggleButton mAgainBtn;
	private ImageView mBackBtn,mFindPunishBtn,mHelpBtn;
	private String[] mStates = null;
	private String[] mActions = null;
	private Timer mStateTimer = null;
	private Timer mActionTimer = null;
	
	private int mStateCount = 40;
	private int mActionCount = 40;
	private int count2 = 0;
	private int cycle = 1;
	
	private static final int MSG_UPDATE_STATE = 0;
	private static final int MSG_UPDATE_ACTION = 1;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_STATE:
				String stateText = "";
				if (mStateCount == 0) {
					mStateCount = 60;
					mStateTimer.cancel();
					stateText = mStates[(int)(Math.random() * mStates.length)];
					mStateTV.setText(stateText);
					mStateTV.setTextColor(Color.RED);
					mAgainBtn.setBackgroundResource(R.drawable.handle_up);
					mAgainBtn.setClickable(true);
					return;
				} else {
					mStateCount --;
				}
				stateText = mStates[(int)(Math.random() * mStates.length)];
				mStateTV.setText(stateText);
				mStateTV.setTextColor(Color.BLACK);
				break;
			case MSG_UPDATE_ACTION:
				String actionText = "";
				if (mActionCount == 0) {
					mActionCount = 60;
					mActionTimer.cancel();
					actionText = mActions[(int)(Math.random() * mStates.length)];
					mActionTV.setText(actionText);
					mActionTV.setTextColor(Color.RED);
					mAgainBtn.setBackgroundResource(R.drawable.handle_up);
					mAgainBtn.setClickable(true);
					return;
				} else {
					mActionCount --;
				}
				actionText = mActions[(int)(Math.random() * mStates.length)];
				mActionTV.setText(actionText);
				mActionTV.setTextColor(Color.BLACK);
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.punish_main);
		mStateTV = (TextView) findViewById(R.id.tv_state);
		mActionTV = (TextView) findViewById(R.id.tv_action);
		mAgainBtn = (ToggleButton) findViewById(R.id.punish_troggle);
		mAgainBtn.setOnClickListener(this);
		mBackBtn = (ImageView) findViewById(R.id.iv_punish_back);
		mFindPunishBtn = (ImageView) findViewById(R.id.iv_punish_rank);
		mHelpBtn = (ImageView) findViewById(R.id.iv_punish_help);
		mBackBtn.setOnClickListener(this);
		mFindPunishBtn.setOnClickListener(this);
		mHelpBtn.setOnClickListener(this);
		
		mStates = getResources().getStringArray(R.array.punish_state);
		mActions = getResources().getStringArray(R.array.punish_action);
		
		startStateTimer();
		startActionTimer();
	}
	
	private void startStateTimer(){
		mAgainBtn.setBackgroundResource(R.drawable.handle_down);
		mAgainBtn.setClickable(false);
		mStateTimer = new Timer();
		mStateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_STATE));
			}
		}, 500, 50);
	}
	
	private void startActionTimer(){
		mActionTimer = new Timer();
		mActionTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_ACTION));
			}
		}, 500, 50);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.punish_troggle:
			mAgainBtn.setBackgroundResource(R.drawable.handle_down);
			startStateTimer();
			startActionTimer();
			break;

		default:
			break;
		}
	}

}
