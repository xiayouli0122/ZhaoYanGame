package com.zhaoyan.game;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

//game index activity
public class IndexActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "IndexActivity";
	private ImageView mBackBtn, mHelpBtn;

	private ImageView mGameKillerView, mGameSpyView, mGameFoolView,
			mGameHuopinView, mGamePunishView, mWishView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_index);

		initView();
	}

	private void initView() {
		//game list
		mGameKillerView = (ImageView) findViewById(R.id.game_killer);
		mGameSpyView = (ImageView) findViewById(R.id.game_spy);
		mGameFoolView = (ImageView) findViewById(R.id.game_fool);
		mGameHuopinView = (ImageView) findViewById(R.id.game_huopin);
		mGamePunishView = (ImageView) findViewById(R.id.game_punish);
		mWishView = (ImageView) findViewById(R.id.game_wish);

		mGameKillerView.setOnClickListener(this);
		mGameSpyView.setOnClickListener(this);
		mGameFoolView.setOnClickListener(this);
		mGameHuopinView.setOnClickListener(this);
		mGamePunishView.setOnClickListener(this);
		mWishView.setOnClickListener(this);

		mBackBtn = (ImageView) findViewById(R.id.btn_back);
		mHelpBtn = (ImageView) findViewById(R.id.btn_help);

		mBackBtn.setOnClickListener(this);
		mHelpBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.game_killer:
			showToast("game_killer");
			//you can call openActivity(Class<?> class); 
			break;
		case R.id.game_spy:
			showToast("game_spy");
			break;
		case R.id.game_fool:
			showToast("game_fool");
			break;
		case R.id.game_huopin:
			showToast("game_huopin");
			break;
		case R.id.game_punish:
			showToast("game_punish");
			break;
		case R.id.game_wish:
			showToast("game_wish");
			break;
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_help:
			showToast("btn_help");
			break;

		default:
			break;
		}
	}
}
