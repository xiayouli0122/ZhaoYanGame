package com.zhaoyan.game.killer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoyan.game.BaseActivity;
import com.zhaoyan.game.R;
import com.zhaoyan.game.R.id;
import com.zhaoyan.game.util.Log;
import com.zhaoyan.game.util.Constants.Killers;

//police:警察   killer:杀手  civilian:平民
public class KillerGameActivity extends BaseActivity implements
		OnClickListener, OnTouchListener, OnItemLongClickListener,
		OnItemClickListener, OnSeekBarChangeListener {
	private static final String TAG = "KillerGameActivity";

	// player lists
	private List<KPlayer> mPlayerLists = new ArrayList<KPlayer>();

	// finish current game
	private ImageView mBackBtn;
	// check all player identify
	private ImageView mCheckIdBtn;

	// killer init
	private View mInitView;
	private ImageView mStartDealBtn;
	// init total num
	private int mTotalPlayerNum = 5;
	//init total player num 
	private int mInitNum = 5;
	// init killer & police num,killer == police
	private int mKillerNum = 1;
	// init civilian numm
	private int mCivilianNum = 5;
	private TextView mCivilianNumTv, mKillerNumTv, mPoliceNumTv, mTotalNumTv;
	private SeekBar mSettingBar;
	
	//check id view
	//killler check userinfo view
	private View mCheckUserInfoView;
	private Button mNextBtn;
	private int cameraId;
	private TextView mPlayerNumTv, mRoleTv;
	private ImageView userIcon;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;
	private Random mRandom;
	private int index = 1;
	private ArrayList<Integer> role;
	private boolean clickFlag = false;
	private SurfaceHolder.Callback2 callback = new SurfaceHolder.Callback2() {
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}

		}

		@SuppressLint("NewApi")
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				mCamera = Camera.open(cameraId);
			} catch (Exception e) {
				Log.e(TAG, "open camera fail!");
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if (mCamera != null) {
				try {
					mCamera.setPreviewDisplay(holder);
					mCamera.setDisplayOrientation(90);
					mCamera.startPreview();
				} catch (IOException e) {
					e.printStackTrace();
					mCamera.release();
					mCamera = null;
				}

			}
		}

		@Override
		public void surfaceRedrawNeeded(SurfaceHolder holder) {
			// TODO Auto-generated method stub

		}
	};

	// killer content
	private View mKillerContentView;
	private GridView mPlayerGridView;
	private PlayerAdapter mPlayerAdapter;
	private ImageView mKillerStartBtn;
	private TextView mTipView;
	private ProgressBar mVoteBar;
	private ImageView mCheckFinishBtn;

	// killer guide view
	private View mKillGuideView;
	private ImageView mKillBtn;

	// killer check guide view
	private View mCheckGuideView;
	private ImageView mCheckBtn;

	// Killer check guide resule
	private View mCheckGuideResultView;
	private ImageView mContinueBtn;

	// killer result guide first view
	private View mResultGuideFirstView;
	private ImageView mVoteBtn;
	private TextView mDeadStatusFirstTV;

	// bottom view
	private RelativeLayout mBottomView;

	// player content view height
	private int mContentHeight = 0;

	// record what u doing
	private static final int START = 0;
	private static final int KILL = 1;
	private static final int CHECK = 2;
	private static final int CHECKED = 3;
	private int mStatus = START;

	private static final int MSG_GET_HEIGHT = 0;
	private static final int MSG_SET_GRIDVIEW_LAYOUT = 1;
	private static final int MSG_CHECK_GUIDE = 2;
	private static final int MSG_CHECK_ROLE_FINISH = 3;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GET_HEIGHT:
				int height = mBottomView.getMeasuredHeight();
				height = (int) (height + height * 0.7);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						RelativeLayout.LayoutParams.MATCH_PARENT);
				lp.setMargins(0, 0, 0, height);
				mKillerContentView.setLayoutParams(lp);

				break;
			case MSG_SET_GRIDVIEW_LAYOUT:
				mContentHeight = mKillerContentView.getMeasuredHeight();
				Log.d(TAG, "mContentHeight=" + mContentHeight);

				int gridviewHeight = (int) (mContentHeight * 0.7);
				if (mTotalPlayerNum > 9) {
					gridviewHeight = (int) (mContentHeight * 0.8);
				}
				Log.d(TAG, "gridViewHeight=" + gridviewHeight);
				int marginHeight = (int) (gridviewHeight * 0.1);
				
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, gridviewHeight);
				params.setMargins(0, marginHeight, 0, 0);
				
				// 根据玩家数量设定gridview的每行个数
				int columns = 3;//default is 3
				if (mTotalPlayerNum == 5 || mTotalPlayerNum == 6) {
					columns = 2;
				} else if (mTotalPlayerNum >= 7 && mTotalPlayerNum <= 9) {
					columns = 3;
				} else {
					columns = 4;
//					marginHight = 45;
					marginHeight = (int) (gridviewHeight * 0.06);
					params.setMargins(0, 10, 0, 0);
				}
				Log.d(TAG, "margHeight=" + marginHeight);

				mPlayerGridView.setLayoutParams(params);
				
				
				mPlayerGridView.setNumColumns(columns);
				mPlayerGridView.setVerticalSpacing(marginHeight);

				mPlayerAdapter = new PlayerAdapter(getApplicationContext(),
						mPlayerLists);
				mPlayerGridView.setAdapter(mPlayerAdapter);
				// test
				break;
			case MSG_CHECK_GUIDE:
				mVoteBar.setVisibility(View.INVISIBLE);
				// into check step
				mCheckGuideView.setVisibility(View.VISIBLE);

				mCheckIdBtn.setVisibility(View.INVISIBLE);
				mKillerContentView.setVisibility(View.GONE);
				setAnimation(mKillerContentView, R.anim.slide_up_out);
				break;
			case MSG_CHECK_ROLE_FINISH:
				mNextBtn.setEnabled(true);
				clickFlag = true;
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

	private void initView() {
		mBottomView = (RelativeLayout) findViewById(R.id.killer_game_footer);
		//底部返回按钮
		mBackBtn = (ImageView) findViewById(R.id.iv_killer_game_back);
		mBackBtn.setOnClickListener(this);

		//底部检查身份按钮
		mCheckIdBtn = (ImageView) findViewById(R.id.iv_killer_show_identity);
		mCheckIdBtn.setOnClickListener(this);
		mCheckIdBtn.setOnTouchListener(this);
		
		//初始化界面，设置游戏人数
		mInitView = findViewById(R.id.kill_init);
		mStartDealBtn = (ImageView) findViewById(R.id.iv_start_deal);
		mStartDealBtn.setOnClickListener(this);
		mPoliceNumTv = (TextView) findViewById(R.id.tv_police_num);
		mCivilianNumTv = (TextView) findViewById(R.id.tv_civilian_num);
		mKillerNumTv = (TextView) findViewById(R.id.tv_killer_num);
		mTotalNumTv = (TextView) findViewById(R.id.tv_total_num);
		mSettingBar = (SeekBar) findViewById(R.id.bar_set_player);
		mSettingBar.setOnSeekBarChangeListener(this);
		
		//发牌界面，设置玩家头像，身份等
		mCheckUserInfoView = findViewById(R.id.kill_check_userinfo);
		mNextBtn = (Button) findViewById(R.id.btn_next);
		mNextBtn.setText("1号查看身份");
		mNextBtn.setOnClickListener(this);
		mPlayerNumTv = (TextView) findViewById(R.id.tv_player_number);
		mPlayerNumTv.setVisibility(View.INVISIBLE);
		mRoleTv = (TextView) findViewById(R.id.tv_player_role);
		mRoleTv.setVisibility(View.INVISIBLE);
		cameraId = getCameraInfo();
		mSurfaceView = (SurfaceView) findViewById(R.id.killer_get_icon_for_user);
		userIcon = (ImageView) findViewById(R.id.killer_icon_for_user);
		if (cameraId < 0) {
			mSurfaceView.setVisibility(View.GONE);
		} else {
			userIcon.setVisibility(View.INVISIBLE);
			mSurfaceHolder = mSurfaceView.getHolder();
			mSurfaceHolder.addCallback(callback);
		}

		//玩家列表，Gridview
		mKillerContentView = findViewById(R.id.kill_content);
		mPlayerGridView = (GridView) mKillerContentView
				.findViewById(R.id.gv_player);
		mPlayerGridView.setOnItemClickListener(this);
		mPlayerGridView.setOnItemLongClickListener(this);
		mKillerStartBtn = (ImageView) findViewById(R.id.iv_killer_start);
		mKillerStartBtn.setOnClickListener(this);
		mTipView = (TextView) findViewById(R.id.tv_phrase_tip);
		mVoteBar = (ProgressBar) findViewById(R.id.bar_vote);
		mCheckFinishBtn = (ImageView) findViewById(R.id.iv_check_finish);
		mCheckFinishBtn.setOnClickListener(this);

		mHandler.sendEmptyMessageDelayed(MSG_GET_HEIGHT, 2000);

		//杀人指导，法官台词界面
		mKillGuideView = findViewById(R.id.kill_guide);
		mKillBtn = (ImageView) findViewById(R.id.iv_killer_ensure);
		mKillBtn.setOnClickListener(this);

		//法官台词，验人界面
		mCheckGuideView = findViewById(R.id.check_guide);
		mCheckBtn = (ImageView) findViewById(R.id.iv_check_img);
		mCheckBtn.setOnClickListener(this);

		//法官台词，验人结束界面
		mCheckGuideResultView = findViewById(R.id.check_result);
		mContinueBtn = (ImageView) findViewById(R.id.iv_to_kill_result);
		mContinueBtn.setOnClickListener(this);

		//法官台词，杀人结束界面
		mResultGuideFirstView = findViewById(R.id.kill_result_first);
		mVoteBtn = (ImageView) findViewById(R.id.iv_to_vote);
		mVoteBtn.setOnClickListener(this);
		mDeadStatusFirstTV = (TextView) findViewById(R.id.tv_dead_status_first);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_killer_game_back:
			finish();
			break;
		case R.id.iv_start_deal:
			mInitView.setVisibility(View.GONE);
			
			mCheckUserInfoView.setVisibility(View.VISIBLE);
			
			if (2 * mKillerNum >= mTotalPlayerNum || mTotalPlayerNum < 5
					|| mTotalPlayerNum > 16 || mKillerNum < 1) {
				Toast.makeText(this, "参加游戏人数不对", Toast.LENGTH_SHORT).show();
				finish();
			}
			initRole(mKillerNum, mTotalPlayerNum);
			break;
		case R.id.btn_next:
			if (index > mTotalPlayerNum) {
				// TODO 身份检查完毕
				mCheckUserInfoView.setVisibility(View.GONE);

				mCheckIdBtn.setVisibility(View.VISIBLE);
				mKillerContentView.setVisibility(View.VISIBLE);
				setAnimation(mKillerContentView, R.anim.slide_down_in);
				return;
			}
			 
			if (clickFlag) {
				if (mCamera != null) {
					mCamera.startPreview();
				}
				
				mPlayerNumTv.setVisibility(View.INVISIBLE);
				mRoleTv.setVisibility(View.INVISIBLE);
				if (mCamera != null && cameraId >= 0) {
					userIcon.setVisibility(View.INVISIBLE);
				}
				
				mNextBtn.setText(index + "号查看身份");
				clickFlag = false;
			} else {
				index++;
				if (mCamera != null && cameraId >= 0) {
					mNextBtn.setEnabled(false);
					try {
						mCamera.takePicture(null, null, new PictureCallback() {
							@Override
							public void onPictureTaken(byte[] data,
									Camera camera) {
								Bitmap bitmap = BitmapFactory.decodeByteArray(
										data, 0, data.length);
								Matrix m = new Matrix();
								if (bitmap == null) {
									Toast.makeText(getApplicationContext(),
											"照片消失了。。。。", Toast.LENGTH_SHORT)
											.show();
									bitmap = BitmapFactory.decodeResource(
											getResources(),
											R.drawable.default_killer_03);
								} else {
									int width = bitmap.getWidth();
									int height = bitmap.getHeight();
									m.setRotate(-90);
									bitmap = Bitmap.createBitmap(bitmap, 0, 0,
											width, height, m, true);
								}
								userIcon.setImageBitmap(bitmap);
								userIcon.setVisibility(View.VISIBLE);
								mHandler.sendMessage(mHandler
										.obtainMessage(MSG_CHECK_ROLE_FINISH));
							}
						});
					} catch (Exception e) {
						cameraId = -1;
						if (mCamera != null) {
							mCamera.release();
						}
						mSurfaceView.setVisibility(View.GONE);
						userIcon.setImageResource(R.drawable.default_killer_03);
						clickFlag = true;
					}
				} else {
					userIcon.setImageResource(R.drawable.default_killer_03);
					clickFlag = true;
				}

				Killers killers = confirmRole();
				KPlayer player = new KPlayer(index - 1);
				player.setHeadIcon(userIcon.getDrawingCache());
				player.setIdentity(killers);

				mPlayerLists.add(player);
				if (index > mTotalPlayerNum) {
					//setting player gridview ui
					mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_GRIDVIEW_LAYOUT));
					
					testData();
					mNextBtn.setText("完成");
				} else {
					mNextBtn.setText("传递给下一个人");
				}
				mPlayerNumTv.setText((index - 1) + "号");
				String roleStr = "";
				if (Killers.Police == killers) {
					roleStr = getString(R.string.police);
				} else if (Killers.Killer == killers) {
					roleStr = getString(R.string.killer);
				} else {
					roleStr = getString(R.string.civilian);
				}

				mRoleTv.setText(roleStr);
				mPlayerNumTv.setVisibility(View.VISIBLE);
				mRoleTv.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.iv_killer_start:
			// start game
			mCheckIdBtn.setVisibility(View.INVISIBLE);

			mKillerContentView.setVisibility(View.GONE);
			setAnimation(mKillerContentView, R.anim.slide_up_out);

			mKillGuideView.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_killer_ensure:
			// start kill
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
			// start check
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
			// start vote guide
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
		// deal with kill event
		KPlayer player = null;
		for (int i = 0; i < mPlayerLists.size(); i++) {
			player = mPlayerLists.get(i);
			if (i == position) {
				player.setDead(true);
				mPlayerAdapter.notifyDataSetChanged();
				if (Killers.Police == player.getIdentity()) {
					// If killer killed police
					// judge police count
					if (0 == getPlayerCount(Killers.Police)) {
						// game over,killer win

					}
					// going on
					mHandler.sendEmptyMessageDelayed(MSG_CHECK_GUIDE, 1000);
				} else if (Killers.Cilivian == player.getIdentity()) {
					// If killer killed civilian
					// judge civilian count
					if (0 == getPlayerCount(Killers.Cilivian)) {
						// game over,killer win
					}
					// going on
				} else {
					// If killer killed Killer
					// joke me? killer kill killer............................
					// judge killer count
					if (0 == getPlayerCount(Killers.Killer)) {
						// game over,police & civilian win
					}
					// going on
				}
			}
		}
		return false;
	}

	private int getPlayerCount(Killers killer) {
		int count = 0;
		for (KPlayer player : mPlayerLists) {
			if (killer == player.getIdentity()) {
				count++;
			}
		}
		return count;
	}
	
	@SuppressLint("NewApi")
	private int getCameraInfo() {
		int n = Camera.getNumberOfCameras();
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < n; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo != null) {
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		mTotalPlayerNum = mInitNum + progress;
		setRoleNumber(mTotalPlayerNum);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	private void setRoleNumber(int number) {
		mKillerNum = 0;
		switch (number) {
		case 5:
		case 6:
		case 7:
			mKillerNum = 1;
			break;
		case 8:
		case 9:
		case 10:
			mKillerNum = 2;
			break;
		case 11:
		case 12:
		case 13:
		case 14:
			mKillerNum = 3;
			break;
		case 15:
		case 16:
			mKillerNum = 4;
			break;
		default:
			break;
		}
		setInfo(mKillerNum, number);
	}

	private void setInfo(int n, int number) {
		mTotalNumTv.setText(number + "人");
		mKillerNumTv.setText("杀手" + n + "人");
		mPoliceNumTv.setText("警察" + n + "人");
		mCivilianNumTv.setText("平民" + (number - 2 * n) + "人");
	}

	private Killers confirmRole() {
		if (mRandom == null) {
			mRandom = new Random();
		}
		if (role.size() > 0) {
			int n = mRandom.nextInt(role.size());
			n = role.remove(n);
			switch (n) {
			case 0:
				return Killers.Killer;
			case 1:
				return Killers.Police;
			case 2:
				return Killers.Cilivian;
			default:
				return Killers.Cilivian;
			}
		} else {
			return Killers.Cilivian;
		}
	}
	 /**
	 * in role list if 0 mean killer,1 mean police ,2 mean civilian
	 * */
	private void initRole(int killerNumber, int totalNumber) {
		if (role == null)
			role = new ArrayList<Integer>();
		else
			role.clear();
		for (int i = 0; i < totalNumber; i++) {
			if (i < 2 * killerNumber) {
				role.add(0);
				role.add(1);
				i++;
			} else {
				role.add(2);
			}
		}
	}
	
	 /** just test method */
	private void testData() {
		Log.d(TAG, "testData.size=" + mPlayerLists.size());
		for (int i = 0; i < mPlayerLists.size(); i++) {
			KPlayer player = mPlayerLists.get(i);
			Log.d(TAG, "index is : " + player.getNumber() + "    role is : "
					+ player.getIdentity());
		}
	}
}
