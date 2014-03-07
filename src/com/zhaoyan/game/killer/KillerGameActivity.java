package com.zhaoyan.game.killer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhaoyan.game.BaseActivity;
import com.zhaoyan.game.R;
import com.zhaoyan.game.R.id;
import com.zhaoyan.game.util.Log;
import com.zhaoyan.game.util.Constants.Killers;

public class KillerGameActivity extends BaseActivity implements
		OnClickListener, OnTouchListener, OnItemLongClickListener, OnItemClickListener {
	private static final String TAG = "KillerGameActivity";
	
	//player lists
	private List<KPlayer> mPlayerLists = new ArrayList<KPlayer>();

	private ImageView mFinishBtn;
	// finish current game
	private ImageView mBackBtn;
	// check all player identify
	private ImageView mCheckIdBtn;

	// killer content
	private View mKillerContentView;
	private GridView mPlayerGridView;
	private PlayerAdapter mPlayerAdapter;
	private ImageView mKillerStartBtn;
	private TextView mTipView;
	private ProgressBar mVoteBar;
	private ImageView mCheckFinishBtn;
	
	//killer guide view
	private View mKillGuideView;
	private ImageView mKillBtn;
	
	//killer check guide view
	private View mCheckGuideView;
	private ImageView mCheckBtn;
	
	//Killer check guide resule
	private View mCheckGuideResultView;
	private ImageView mContinueBtn;

	// bottom view
	private RelativeLayout mBottomView;

	//player content view height
	private int mContentHeight = 0;
	
	//record what u doing
	private static final int START = 0;
	private static final int KILL = 1;
	private static final int CHECK = 2;
	private static final int CHECKED = 3;
	private int mStatus = START;
	//init police num
	private int mPoliceNum = 0;
	//init killer num
	private int mKillerNum = 0;
	//init civilian numm
	private int mCivilianNum = 0;
	
	private static final int MSG_GET_HEIGHT = 0;
	private static final int MSG_SET_GRIDVIEW_LAYOUT = 1;
	private static final int MSG_CHECK_GUIDE = 2;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GET_HEIGHT:
				int height = mBottomView.getMeasuredHeight();
				height = (int) (height + height * 0.7);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
				lp.setMargins(0, 0, 0, height);
				mKillerContentView.setLayoutParams(lp);
				
				mHandler.sendEmptyMessageDelayed(MSG_SET_GRIDVIEW_LAYOUT, 1000);
				break;
			case MSG_SET_GRIDVIEW_LAYOUT:
				mContentHeight = mKillerContentView.getMeasuredHeight();
				Log.d(TAG, "mContentHeight=" + mContentHeight);
				
				//test set gridview
				int players = 8;
				//根据玩家人数确认警察，杀手以及平民的人数
				mPoliceNum = 2;
				mKillerNum = 2;
				mCivilianNum = 4;
				//two police,two killer,four civilian
				KPlayer kPlayer = null;
				//the first player
				kPlayer = new KPlayer(1);
				kPlayer.setIdentity(Killers.Police);
				mPlayerLists.add(kPlayer);
				
				//the second player
				kPlayer = new KPlayer(2);
				kPlayer.setIdentity(Killers.Killer);
				mPlayerLists.add(kPlayer);
				
				//the third player
				kPlayer = new KPlayer(3);
				kPlayer.setIdentity(Killers.Cilivian);
				mPlayerLists.add(kPlayer);
				
				//the fourth player
				kPlayer = new KPlayer(4);
				kPlayer.setIdentity(Killers.Cilivian);
				mPlayerLists.add(kPlayer);
				
				//the fifth player
				kPlayer = new KPlayer(5);
				kPlayer.setIdentity(Killers.Cilivian);
				mPlayerLists.add(kPlayer);
				
				//the sixth player
				kPlayer = new KPlayer(6);
				kPlayer.setIdentity(Killers.Killer);
				mPlayerLists.add(kPlayer);
				
				//the seventh player
				kPlayer = new KPlayer(7);
				kPlayer.setIdentity(Killers.Cilivian);
				mPlayerLists.add(kPlayer);
				
				//the eighth player
				kPlayer = new KPlayer(8);
				kPlayer.setIdentity(Killers.Police);
				mPlayerLists.add(kPlayer);
				
				int gridviewHeight = (int) (mContentHeight * 0.7);
				Log.d(TAG, "gridViewHeight=" + gridviewHeight);
				int marginHight = (int) (gridviewHeight * 0.1);
				
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, gridviewHeight);
				params.setMargins(0, marginHight, 0, 0);
				mPlayerGridView.setLayoutParams(params);
				
				
				//根据玩家数量设定gridview的每行个数
				mPlayerGridView.setNumColumns(3);
				mPlayerGridView.setVerticalSpacing(marginHight);
				
				mPlayerAdapter = new PlayerAdapter(getApplicationContext(), mPlayerLists);
				mPlayerGridView.setAdapter(mPlayerAdapter);
				//test
				break;
			case MSG_CHECK_GUIDE:
				mVoteBar.setVisibility(View.INVISIBLE);
				//into check step
				mCheckGuideView.setVisibility(View.VISIBLE);
				
				mCheckIdBtn.setVisibility(View.INVISIBLE);
				mKillerContentView.setVisibility(View.GONE);
				setAnimation(mKillerContentView, R.anim.slide_up_out);
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.killer_game);

		initView();
	}

	private void initView(){
		mFinishBtn = (ImageView) findViewById(R.id.iv_finish);
		mFinishBtn.setOnClickListener(this);
		
		mBackBtn = (ImageView) findViewById(R.id.iv_killer_game_back);
		mBackBtn.setOnClickListener(this);
		
		mCheckIdBtn = (ImageView) findViewById(R.id.iv_killer_show_identity);
		mCheckIdBtn.setOnClickListener(this);
		mCheckIdBtn.setOnTouchListener(this);
		
		mKillerContentView = findViewById(R.id.kill_content);
		mPlayerGridView = (GridView) mKillerContentView.findViewById(R.id.gv_player);
		mPlayerGridView.setOnItemClickListener(this);
		mPlayerGridView.setOnItemLongClickListener(this);
		mKillerStartBtn = (ImageView) findViewById(R.id.iv_killer_start);
		mKillerStartBtn.setOnClickListener(this);
		mTipView = (TextView) findViewById(R.id.tv_phrase_tip);
		mVoteBar = (ProgressBar) findViewById(R.id.bar_vote);
		mCheckFinishBtn = (ImageView) findViewById(R.id.iv_check_finish);
		mCheckFinishBtn.setOnClickListener(this);
		
		mBottomView = (RelativeLayout) findViewById(R.id.killer_game_footer);
		
		mHandler.sendEmptyMessageDelayed(MSG_GET_HEIGHT, 2000);
		
		mKillGuideView = findViewById(R.id.kill_guide);
		mKillBtn = (ImageView) findViewById(R.id.iv_killer_ensure);
		mKillBtn.setOnClickListener(this);
		
		mCheckGuideView = findViewById(R.id.check_guide);
		mCheckBtn = (ImageView) findViewById(R.id.iv_check_img);
		mCheckBtn.setOnClickListener(this);
		
		mCheckGuideResultView = findViewById(R.id.check_result);
		mContinueBtn = (ImageView) findViewById(R.id.iv_to_kill_result);
		mContinueBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_finish:
			//check identity complete
			mFinishBtn.setVisibility(View.GONE);
			
			mCheckIdBtn.setVisibility(View.VISIBLE);
			mKillerContentView.setVisibility(View.VISIBLE);
			setAnimation(mKillerContentView, R.anim.slide_down_in);
			break;
		case R.id.iv_killer_start:
			//start game
			mCheckIdBtn.setVisibility(View.INVISIBLE);
			
			mKillerContentView.setVisibility(View.GONE);
			setAnimation(mKillerContentView, R.anim.slide_up_out);
			
			mKillGuideView.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_killer_ensure:
			//start kill
			mStatus = KILL;
			mCheckIdBtn.setVisibility(View.VISIBLE);
			mKillerContentView.setVisibility(View.VISIBLE);
			setAnimation(mKillerContentView, R.anim.slide_down_in);
			mTipView.setText(R.string.killer_tip_one);
			mTipView.setVisibility(View.VISIBLE);
			mKillerStartBtn.setVisibility(View.GONE);
			
			mKillGuideView.setVisibility(View.INVISIBLE);
			break;
		case R.id.iv_check_img:
			//start check
			mStatus = CHECK;
			mCheckIdBtn.setVisibility(View.VISIBLE);
			mKillerContentView.setVisibility(View.VISIBLE);
			setAnimation(mKillerContentView, R.anim.slide_down_in);
			mTipView.setText(R.string.killer_tip_two);
			mTipView.setVisibility(View.VISIBLE);
			mCheckFinishBtn.setVisibility(View.VISIBLE);
			
			mCheckGuideView.setVisibility(View.INVISIBLE);
			break;
		case R.id.iv_check_finish:
			mStatus = CHECKED;
			
			mCheckIdBtn.setVisibility(View.INVISIBLE);
			
			mKillerContentView.setVisibility(View.GONE);
			setAnimation(mKillerContentView, R.anim.slide_up_out);
			
			mCheckGuideResultView.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_to_kill_result:
			//start vote guide
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mPlayerAdapter.setShowId(true);
			break;
		case MotionEvent.ACTION_UP:
			mPlayerAdapter.setShowId(false);
			break;

		default:
			break;
		}
		return false;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mStatus != CHECK) {
			return;
		}
		
		if (mPlayerLists.get(position).isDead()) {
			return;
		}
		mStatus = CHECKED;
		mPlayerLists.get(position).setChecked(true);
		mPlayerAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (mStatus != KILL) {
			return false;
		}
		mVoteBar.setVisibility(View.VISIBLE);
		//deal with kill event
		KPlayer player = null;
		for (int i = 0; i < mPlayerLists.size(); i++) {
			player = mPlayerLists.get(i);
			if (i == position) {
				player.setDead(true);
				if (Killers.Police == player.getIdentity()) {
					//If killer killed police
					//judge police count
					if (0 == getPlayerCount(Killers.Police)) {
						//game over,killer win
					} 
					//going on
					mPlayerAdapter.notifyDataSetChanged();
					
					mHandler.sendEmptyMessageDelayed(MSG_CHECK_GUIDE, 1000);
				} else if (Killers.Cilivian == player.getIdentity()) {
					//If killer killed civilian
					//judge civilian count
					if (0 == getPlayerCount(Killers.Cilivian)) {
						//game over,killer win
					} 
					//going on
				} else {
					//If killer killed Killer
					//joke me? killer kill killer............................
					//judge killer count
					if (0 == getPlayerCount(Killers.Killer)) {
						//game over,police & civilian win
					} 
					//going on
				}
			}
		}
		return false;
	}
	
	private int getPlayerCount(Killers killer){
		int count = 0;
		for(KPlayer player : mPlayerLists){
			if (killer == player.getIdentity()) {
				count ++ ;
			}
		}
		return count;
	}

}
