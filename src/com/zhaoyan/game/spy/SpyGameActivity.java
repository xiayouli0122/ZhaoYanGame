package com.zhaoyan.game.spy;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zhaoyan.game.BaseActivity;
import com.zhaoyan.game.R;
import com.zhaoyan.game.db.DBHelper;
import com.zhaoyan.game.db.DbData;
import com.zhaoyan.game.db.DbData.SpyColumns;
import com.zhaoyan.game.dialog.ConfirmDialog;
import com.zhaoyan.game.dialog.ConfirmDialog.ZYOnClickListener;
import com.zhaoyan.game.punish.PunishMainActivity;
import com.zhaoyan.game.spy.SpyConstant.Spys;
import com.zhaoyan.game.util.Log;
import com.zhaoyan.game.util.Utils;

public class SpyGameActivity extends BaseActivity implements OnClickListener, OnItemLongClickListener, OnItemClickListener, OnCheckedChangeListener{
	private static final String TAG = "SpyGameActivity";
	
	private int mTotalPlayerNum = 4;
	private int mSpyNum = 1;
	private boolean mHasBlank = false;
	
	private int mIndex = 1;
	
	private String[] mWords = new String[2];
	private String mSpyWord,mCivilianWord;
	
	private List<Spys> mRoleList = null;
	
	private List<Splayer> mPlayerList = new ArrayList<Splayer>();
	private Splayer mCurrentSplayer = null;
	
	private TextView mStatusTipTV;
	private GridView mGridView;
	private ProgressBar mVoteBar;
	private ImageView mCardForeIV;
	private RelativeLayout mCardBackRL;
	private TextView mWordTV;
	private ImageView mGotItIV;
	private ImageView mBackIV;
	private ImageView mForgotWordIV;
	
	//over view
	private View mOverView;
	private ImageView mShiningIV;
	private ImageView mWinBoradIV;
	private TextView mSpyNoTV,mBlankNoTV;
	private TextView mSpyWordTV,mCivilianWordTV;
	private ToggleButton mLikeTB;
	private ImageView mPunishIV,mBackToIV,mRestartIV,mShareIV;
	
	private SpyAvatarAdapter mAvatarAdapter;
	
	private DBHelper mDbHelper = null;
	
	private static final int INIT = 0;
	private static final int START = 1;
	private int mStatus = INIT;
	
	private static final int MSG_INVISIBLE_CARD = 0;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_INVISIBLE_CARD:
				mCardBackRL.setVisibility(View.GONE);
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spy_game);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			Log.e(TAG, "ERROR");
		} else {
			mTotalPlayerNum = bundle.getInt(SpyConstant.EXTRA_TOTAL_PLAYER_NUM);
			mSpyNum = bundle.getInt(SpyConstant.EXTRA_SPY_NUM);
			mHasBlank = bundle.getBoolean(SpyConstant.EXTRA_HAS_BLANK);
			List<SpyWord> wordList = bundle.getParcelableArrayList(SpyConstant.EXTRA_WORD);
			int pos = (int) (Math.random() * wordList.size());
			SpyWord spyWord = wordList.get(pos);
			mWords[0] = spyWord.getWord1();
			mWords[1] = spyWord.getWord2();
			initWord();
			initAllPlayerIdentity(mSpyNum, mTotalPlayerNum);
			Log.d(TAG, "total:" + mTotalPlayerNum + ","
					+ "spynum:" + mSpyNum + ","
					+ "blank:" + mHasBlank + ","
					+ "word1:" + mWords[0] + ","
					+ "word2:" + mWords[1]);
		}
		mDbHelper = new DBHelper(getApplicationContext(), DbData.DATABASE_NAME, null, DbData.DATABASE_VERSION);
		
		initView();
		setGridViewParmars();
		mAvatarAdapter = new SpyAvatarAdapter(getApplicationContext(), mPlayerList);
		mGridView.setAdapter(mAvatarAdapter);
		//
		mStatusTipTV.setText(getString(R.string.spy_flop, mIndex));
		setResult(RESULT_CANCELED);
	}
	
	private void initView(){
		mStatusTipTV = (TextView) findViewById(R.id.tv_status_tip);
		
		mGridView = (GridView) findViewById(R.id.gv_avatar);
		mGridView.setOnItemLongClickListener(this);
		mGridView.setOnItemClickListener(this);
		
		mVoteBar = (ProgressBar) findViewById(R.id.pb_vote);
		mCardForeIV = (ImageView) findViewById(R.id.iv_word_card);
		mCardForeIV.setOnClickListener(this);
		mCardBackRL = (RelativeLayout) findViewById(R.id.rl_word_show_container);
		mWordTV = (TextView) findViewById(R.id.tv_spy_word);
		mGotItIV = (ImageView) findViewById(R.id.iv_spy_gotit);
		mGotItIV.setOnClickListener(this);
		mBackIV = (ImageView) findViewById(R.id.iv_btn_back);
		mBackIV.setOnClickListener(this);
		mForgotWordIV = (ImageView) findViewById(R.id.iv_btn_forget);
		mForgotWordIV.setOnClickListener(this);
		
		mOverView = findViewById(R.id.spy_game_over);
		mShiningIV = (ImageView) mOverView.findViewById(R.id.iv_shining_light);
		mWinBoradIV = (ImageView) mOverView.findViewById(R.id.iv_win_board);
		mSpyNoTV = (TextView) mOverView.findViewById(R.id.tv_spy_no);
		mBlankNoTV = (TextView) mOverView.findViewById(R.id.tv_blank_no);
		mBlankNoTV.setVisibility(mHasBlank ? View.VISIBLE : View.GONE);
		mSpyWordTV = (TextView) mOverView.findViewById(R.id.tv_spy_over_spy_word);
		mCivilianWordTV = (TextView) mOverView.findViewById(R.id.tv_spy_over_civilian_word);
		mLikeTB = (ToggleButton) mOverView.findViewById(R.id.tb_spy_words_like);
		boolean isExist = isWordExist();
		Log.d(TAG, "is exist=" + isExist);
		if (isExist) {
			mLikeTB.setChecked(false);
			mLikeTB.setClickable(false);
		} else {
			mLikeTB.setChecked(true);
			mLikeTB.setClickable(true);
		}
		mPunishIV = (ImageView) mOverView.findViewById(R.id.iv_btn_punish);
		mBackToIV = (ImageView) mOverView.findViewById(R.id.iv_btn_tospy);
		mRestartIV = (ImageView) mOverView.findViewById(R.id.iv_btn_restart);
		mShareIV = (ImageView) mOverView.findViewById(R.id.iv_btn_share);
		mLikeTB.setOnCheckedChangeListener(this);
		mPunishIV.setOnClickListener(this);
		mBackToIV.setOnClickListener(this);
		mRestartIV.setOnClickListener(this);
		mShareIV.setOnClickListener(this);
	}
	
	/** 
	 * accord to the words,set the spy and civilian's word
	 */
	private void initWord(){
		int index = (int) (Math.random() * 2);
		Log.d(TAG, "initWord.index=" + index);
		mSpyWord = mWords[index];
		mCivilianWord = mWords[1 - index];
		Log.d(TAG, "initWord.spyWord=" + mSpyWord);
		Log.d(TAG, "initWord.civilianWord=" + mCivilianWord);
	}
	
	/**
	 * init all player identity by totalNumber & spyNumber
	 * 根据总共玩家和卧底玩家数，预先分配好卧底，平民和白板的人数
	 * 
	 * @param spyNumber
	 * @param totalNumber    4 1
	 */
	private void initAllPlayerIdentity(int spyNumber, int totalNumber) {
		if (mRoleList == null) {
			mRoleList = new ArrayList<Spys>();
		} else {
			mRoleList.clear();
		}
		
		if (mHasBlank) {
			mRoleList.add(Spys.Blank);
			totalNumber = totalNumber - 1;
		}
		
		for (int i = 0; i < totalNumber; i++) {
			if (i < spyNumber) {
				mRoleList.add(Spys.Spy);
			} else {
				mRoleList.add(Spys.Civilian);
			}
		}
	}
	
	/**
	 * get player identity by random way
	 * @return a identity
	 */
	private Spys getPlayerIdentity() {
		if (mRoleList.size() > 0) {
			int index = (int) (Math.random() * mRoleList.size());
			Log.d(TAG, "index=" + index);
			Spys player = mRoleList.remove(index);
			Log.d(TAG, "player=" + player);
			return player;
		} else {
			return Spys.Civilian;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_word_card:
			mCardForeIV.setVisibility(View.GONE);
//			setAnimation(mCardForeIV, R.anim.spy_fffff);
			mCardBackRL.setVisibility(View.VISIBLE);
//			setAnimation(mCardBackRL, R.anim.spy_fffff);
			mGotItIV.setVisibility(View.VISIBLE);
			Spys role = getPlayerIdentity();
			String word = "";
			if (role == Spys.Blank) {
				//do nothing
			} else if (role == Spys.Spy) {
				word = mSpyWord;
			} else {
				word = mCivilianWord;
			}
			
			mCurrentSplayer = new Splayer(mIndex);
			mCurrentSplayer.setIdentity(role);
			mCurrentSplayer.setWord(word);
			
			mWordTV.setText(word);
			break;
		case R.id.iv_spy_gotit:
			mPlayerList.add(mCurrentSplayer);
			if (mIndex == mTotalPlayerNum) {
				mStatus = START;
				Log.d(TAG, "check word finish,player.size=" + mPlayerList.size());
				mCardForeIV.setVisibility(View.GONE);
				mCardBackRL.setVisibility(View.GONE);
				mGotItIV.setVisibility(View.GONE);
				mForgotWordIV.setVisibility(View.VISIBLE);
				int number = Utils.getRandomNumber(mTotalPlayerNum);
				mStatusTipTV.setText(getString(R.string.spy_status_vote, (number + 1)));
			} else {
				mIndex ++ ;
				mCardForeIV.setVisibility(View.VISIBLE);
				mCardBackRL.setVisibility(View.GONE);
				mGotItIV.setVisibility(View.GONE);
				mStatusTipTV.setText(getString(R.string.spy_flop_two, mIndex));
			}
			break;
		case R.id.iv_btn_forget:
			if (mAvatarAdapter.isForget()) {
				mAvatarAdapter.setForget(false);
				mForgotWordIV.setSelected(false);
			} else {
				mAvatarAdapter.setForget(true);
				mForgotWordIV.setSelected(true);
			}
			mAvatarAdapter.notifyDataSetChanged();
			break;
		case R.id.iv_btn_back:
			showExitGameDialog();
			break;
		case R.id.iv_btn_punish:
			openActivity(PunishMainActivity.class);
			break;
		case R.id.iv_btn_tospy:
			this.finish();
			break;
		case R.id.iv_btn_restart:
			mStatus = INIT;
			mIndex = 1;
			mRoleList.clear();
			mPlayerList.clear();
			onCreate(null);
			break;
		case R.id.iv_btn_share:
			break;
		default:
			break;
		}
	}
	
	private boolean isWordExist(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String selection = SpyColumns.WORD1 + "=?"
				+ " and " + SpyColumns.GROUP + "=" + 0;
		String[] selectionArgs = {mWords[0]};
		Cursor cursor = db.query(SpyColumns.TABLE_NAME, 
				null, selection, selectionArgs, null, null, null);
		Log.d(TAG, "cursor:" + cursor + ",curso.count=" + cursor.getCount());
		if (cursor == null || cursor.getCount() == 0) {
			cursor.close();
			return false;
		} 
		db.close();
		return true;
	}
	
	private void showWord(String word){
		mAvatarAdapter.setForget(false);
		mForgotWordIV.setSelected(false);
		mAvatarAdapter.notifyDataSetChanged();
		mCardBackRL.setVisibility(View.VISIBLE);
		mWordTV.setText(word);
		mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_INVISIBLE_CARD), 1500);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mStatus == INIT) {
			return;
		}
		
		if (mAvatarAdapter.isForget()) {
			showWord(mPlayerList.get(position).getWord());
		} else {
			showToast(getString(R.string.spy_vote_tip));
		}
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (mStatus == INIT) {
			return true;
		}
		
		if (mAvatarAdapter.isForget()) {
			showWord(mPlayerList.get(position).getWord());
			return true;
		}
		mVoteBar.setVisibility(View.VISIBLE);
		
		Splayer splayer = mPlayerList.get(position);
		splayer.setDead(true);
		mAvatarAdapter.notifyDataSetChanged();
		
		Spys winRole = getWinRole();
		if (winRole == null) {
			mVoteBar.setVisibility(View.INVISIBLE);
			String statusTipStr = "";
			int speaknumber = getSpeakPlayerNumber();
			switch (splayer.getIdentity()) {
			case Blank:
				statusTipStr = getString(R.string.spy_blank_dead, splayer.getNumber(), speaknumber);
				break;
			case Spy:
				statusTipStr = getString(R.string.spy_spy_dead, splayer.getNumber(), speaknumber);
				break;
			case Civilian:
				statusTipStr = getString(R.string.spy_civilian_dead, splayer.getNumber(), speaknumber);
				break;
			default:
				break;
			}
			mStatusTipTV.setText(statusTipStr);
			return true;
		}
		
		mOverView.setVisibility(View.VISIBLE);
		setAnimation(mShiningIV, R.anim.rotate_infinite);
		mSpyNoTV.setText(getSpyNumbers());
		mBlankNoTV.setText(getBlankNumber() + "");
		mSpyWordTV.setText(mSpyWord);
		mCivilianWordTV.setText(mCivilianWord);
		switch (winRole) {
		case Blank:
			mWinBoradIV.setImageResource(R.drawable.blank_win_bg);
			break;
		case Spy:
			mWinBoradIV.setImageResource(R.drawable.spy_win_bg);
			break;
		case Civilian:
			mWinBoradIV.setImageResource(R.drawable.civilian_win_bg);
			break;
		default:
			break;
		}
		
		mVoteBar.setVisibility(View.INVISIBLE);
		return true;
	}
	
	private int getSpeakPlayerNumber(){
		for(int i = 1; i <= mTotalPlayerNum; i++){
			if (!mPlayerList.get(i- 1).isDead()) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * get which role win the game
	 * @return
	 */
	private Spys getWinRole(){
		//civilian win: all spy & blank is dead
		if (getPlayerLivedCount(Spys.Blank) == 0 && getPlayerLivedCount(Spys.Spy) == 0) {
			return Spys.Civilian;
		}
		
		//player number < 6
		if (mTotalPlayerNum < 6 && getPlayerLivedCount() == 2) {
			if (getPlayerLivedCount(Spys.Spy) > 0) {
				return Spys.Spy;
			} else if (getPlayerLivedCount(Spys.Spy) == 0 && getPlayerLivedCount(Spys.Blank) > 0) {
				return Spys.Blank;
			}
		} else if(mTotalPlayerNum >= 6 && getPlayerLivedCount() == 3){
			if (getPlayerLivedCount(Spys.Spy) > 0) {
				return Spys.Spy;
			} else if (getPlayerLivedCount(Spys.Spy) == 0 && getPlayerLivedCount(Spys.Blank) > 0) {
				return Spys.Blank;
			}
		}
		return null;
	}
	
	/**get spec player people num,获得指定身份还活着的人数*/
	private int getPlayerLivedCount(Spys role) {
		int count = 0;
		for (Splayer player : mPlayerList) {
			if (role == player.getIdentity() && !player.isDead()) {
				count++;
			}
		}
		return count;
	}
	
	/**get dead people number*/
	private int getPlayerLivedCount(){
		int count = 0;
		for(Splayer player : mPlayerList){
			if (!player.isDead()) {
				count ++ ;
			}
		}
		return count;
	}
	
	private String getSpyNumbers(){
		String result = "";
		for (Splayer splayer : mPlayerList) {
			if (Spys.Spy == splayer.getIdentity()) {
				result += splayer.getNumber() + ",";
			}
		}
		return result.substring(0, result.length() - 1);
	}
	
	/**
	 * get blank role number.
	 * @return
	 */
	private Integer getBlankNumber(){
		int number = 0;
		for (Splayer splayer : mPlayerList) {
			if (Spys.Blank == splayer.getIdentity()) {
				number = splayer.getNumber();
			}
		}
		return number;
	}
	
	
	private void setGridViewParmars(){
		// 根据玩家数量设定gridview的每行个数
		int columns = 3;// default is 3
		int vertionSpace = 50;
		if (mTotalPlayerNum >= 4 && mTotalPlayerNum < 7) {
			columns = 2;
			vertionSpace = 100;
		} else if (mTotalPlayerNum >= 7 && mTotalPlayerNum <= 9) {
			columns = 3;
			vertionSpace = 50;
		} else {
			columns = 4;
			vertionSpace = 20;
		}
		mGridView.setNumColumns(columns);
		mGridView.setVerticalSpacing(vertionSpace);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SpyColumns.WORD1, mWords[0]);
		values.put(SpyColumns.WORD2, mWords[1]);
		values.put(SpyColumns.GROUP, 0);
		db.insert(SpyColumns.TABLE_NAME, null, values);
		mLikeTB.setChecked(false);
		mLikeTB.setClickable(false);
		showToast(getString(R.string.spy_add_to_myword));
		setResult(RESULT_OK);
	}
	
	private void showExitGameDialog(){
		ConfirmDialog dialog = new ConfirmDialog(this, R.layout.dialog_confirm, R.style.MyDialog);
		dialog.setOnClickListener(new ZYOnClickListener() {
			@Override
			public void onClick(Dialog dialog, View view) {
				switch (view.getId()) {
				case R.id.btn_confirm:
					dialog.cancel();
					SpyGameActivity.this.finish();
					break;
				case R.id.btn_cancel:
					//do nothing
					dialog.cancel();
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}

}
