package com.zhaoyan.game.spy;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zhaoyan.game.BaseActivity;
import com.zhaoyan.game.R;
import com.zhaoyan.game.db.DBHelper;
import com.zhaoyan.game.db.DbData;
import com.zhaoyan.game.db.DbData.SpyColumns;

/**
 * Spy game main activity </br>
 * civilian:Æ½Ãñ     spy:ÎÔµ×
 * @author Yuri
 * @date 20140321
 */
public class SpyMainActivity extends BaseActivity implements OnClickListener, OnSeekBarChangeListener, OnCheckedChangeListener {
	private static final String TAG = "SpyMainActivity";
	
	//init player num
	private int mTotalPlayerNum = SpyConstant.INIT_PLAYER_NUM;
	private int mSpyNum = 1;
	private boolean mHasBlank = false;
	
	//word select
	//0: ÓÑ²Ë  1£ºÎÒµÄ´Ê¿â
	private int mSelectedPos = SpyConstant.GUESS_WORDS;
	
	private List<SpyWord> mMyWordsList = new ArrayList<SpyWord>();
	private List<SpyWord> mGuessWordsList = new ArrayList<SpyWord>();
	
	private DBHelper mDbHelper;
	
	//spy init view
	private ImageButton mGuessWordsIB, mMyWordsIB;
	private TextView mGuessCountTV, mMyCountTV;
	private ImageButton mMyAddIB;
	private TextView mPlayerNumTV;
	private ImageView mSubtractIV,mPlusIV;
	private SeekBar mPlayerSettingBar;
	private int mProgress = 0;
	private TextView mCivilianNumTV,mSpyNumTV;
	private ToggleButton mBlankTB;
	private ImageView mSpyStartIV;
	private ImageView mSpyBackTV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spy_home);
		
		initView();
		//init player num ui
		setPlayerNum();
		setWordsBg();
		
		//get dbHelper
		mDbHelper = new DBHelper(getApplicationContext(), DbData.DATABASE_NAME, null, DbData.DATABASE_VERSION);
		//query db
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.query(SpyColumns.TABLE_NAME, null, null, null, null, null, null);
		SpyWord word = null;
		if (cursor != null && cursor.getCount() != 0) {
			//there has data,do nothing
			word = new SpyWord();
			while (cursor.moveToNext()) {
				String word1 = cursor.getString(cursor.getColumnIndex(SpyColumns.WORD1));
				String word2 = cursor.getString(cursor.getColumnIndex(SpyColumns.WORD2));
				int group = cursor.getInt(cursor.getColumnIndex(SpyColumns.GROUP));
				
				word.setWord1(word1);
				word.setWord2(word2);
				word.setGroup(group);
				
				if (group == SpyConstant.MY_WORDS) {
					mMyWordsList.add(word);
				} else {
					mGuessWordsList.add(word);
				}
			}
		} 
		cursor.close();
		
		mMyCountTV.setText(mMyWordsList.size() + "´Ê");
		mGuessCountTV.setText(mGuessWordsList.size() + "´Ê");
	}
	
	private void initView(){
		mGuessWordsIB = (ImageButton) findViewById(R.id.ib_spy_cate_guess);
		mMyWordsIB = (ImageButton) findViewById(R.id.ib_spy_cate_my);
		mMyAddIB = (ImageButton) findViewById(R.id.ib_spy_cate_my_add);
		mGuessWordsIB.setOnClickListener(this);
		mMyWordsIB.setOnClickListener(this);
		mMyAddIB.setOnClickListener(this);
		
		mGuessCountTV = (TextView) findViewById(R.id.tv_spy_cate_guess_count);
		mMyCountTV = (TextView) findViewById(R.id.tv_spy_cate_my_count);
		
		mPlayerNumTV = (TextView) findViewById(R.id.tv_player_num);
		mCivilianNumTV = (TextView) findViewById(R.id.tv_civilian_num);
		mSpyNumTV = (TextView) findViewById(R.id.tv_spy_num);
		
		mSubtractIV = (ImageView) findViewById(R.id.iv_spy_subtract);
		mPlusIV = (ImageView) findViewById(R.id.iv_spy_plus);
		mSubtractIV.setOnClickListener(this);
		mPlusIV.setOnClickListener(this);
		
		mPlayerSettingBar = (SeekBar) findViewById(R.id.sb_player_num_slide);
		mPlayerSettingBar.setOnSeekBarChangeListener(this);
		mPlayerSettingBar.setProgress(mProgress);
		
		mBlankTB = (ToggleButton) findViewById(R.id.tb_spy_switch_blank);
		mBlankTB.setOnCheckedChangeListener(this);
		
		mSpyBackTV = (ImageView) findViewById(R.id.iv_spy_back);
		mSpyStartIV = (ImageView) findViewById(R.id.iv_spy_start);
		mSpyBackTV.setOnClickListener(this);
		mSpyStartIV.setOnClickListener(this);
	}
	
	/**
	 * set player num and ui
	 */
	private void setPlayerNum(){
		switch (mTotalPlayerNum) {
		case 9:
		case 10:
		case 11:
		case 12:
			mSpyNum = 2;
			break;
		case 13:
		case 14:
		case 15:
		case 16:
			mSpyNum = 3;
			break;
		default:
			mSpyNum = 1;
			break;
		}
		mPlayerNumTV.setText(mTotalPlayerNum + "");
		mSpyNumTV.setText(mSpyNum + "");
		int civilianNum = 0;
		if (mHasBlank) {
			civilianNum = mTotalPlayerNum - mSpyNum - 1;
		} else {
			civilianNum = mTotalPlayerNum - mSpyNum;
		}
		mCivilianNumTV.setText(civilianNum + "");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_spy_cate_guess:
			mSelectedPos = SpyConstant.GUESS_WORDS;
			setWordsBg();
			break;
		case R.id.ib_spy_cate_my:
			mSelectedPos = SpyConstant.MY_WORDS;
			setWordsBg();
			break;
		case R.id.ib_spy_cate_my_add:

			break;
		case R.id.iv_spy_subtract:
			if (mProgress != 0) {
				mProgress --;
				mPlayerSettingBar.setProgress(mProgress);
			}
			break;
		case R.id.iv_spy_plus:
			if (mProgress != 16) {
				mProgress ++;
				mPlayerSettingBar.setProgress(mProgress);
			}
			break;
		case R.id.iv_spy_start:
			Bundle bundle = new Bundle();
			bundle.putInt(SpyConstant.EXTRA_TOTAL_PLAYER_NUM, mTotalPlayerNum);
			bundle.putInt(SpyConstant.EXTRA_SPY_NUM, mSpyNum);
			bundle.putBoolean(SpyConstant.EXTRA_HAS_BLANK, mHasBlank);
			List<SpyWord> wordList = getWordsList();
			int pos = (int) (Math.random() * wordList.size());
			bundle.putParcelable(SpyConstant.EXTRA_WORD, wordList.get(pos));
			openActivity(SpyGameActivity.class, bundle);
			overridePendingTransition(0, 0);
			break;
		case R.id.iv_spy_back:
			this.finish();
			break;

		default:
			break;
		}
	}
	
	/**
	 * update words imagebutton bg
	 */
	private void setWordsBg(){
		switch (mSelectedPos) {
		case SpyConstant.GUESS_WORDS:
			mGuessWordsIB.setBackgroundResource(R.drawable.spy_words_bg_selected);
			mMyWordsIB.setBackgroundResource(R.drawable.spy_words_bg_normal);
			break;
		case SpyConstant.MY_WORDS:
			mGuessWordsIB.setBackgroundResource(R.drawable.spy_words_bg_normal);
			mMyWordsIB.setBackgroundResource(R.drawable.spy_words_bg_selected);
			break;
		default:
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		mProgress = progress;
		mTotalPlayerNum = SpyConstant.INIT_PLAYER_NUM + progress;
		setPlayerNum();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		//do nothing
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		//do nothing
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mHasBlank = isChecked;
		setPlayerNum();
	}
	
	private List<SpyWord> getWordsList(){
		switch (mSelectedPos) {
		case SpyConstant.MY_WORDS:
			return mMyWordsList;
		case SpyConstant.GUESS_WORDS:
			return mGuessWordsList;
		default:
			return null;
		}
	}
}
