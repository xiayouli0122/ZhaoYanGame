package com.zhaoyan.game.killer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoyan.game.BaseActivity;
import com.zhaoyan.game.R;
import com.zhaoyan.game.dialog.ConfirmDialog;
import com.zhaoyan.game.dialog.ConfirmDialog.ZYOnClickListener;
import com.zhaoyan.game.util.Constants.Killers;
import com.zhaoyan.game.util.Log;

//police:警察   killer:杀手  civilian:平民 identity:身份
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
	// init total player num
	private int mInitNum = 5;
	// init killer & police num,killer == police
	private int mKillerNum = 1;
	private TextView mCivilianNumTv, mKillerNumTv, mPoliceNumTv, mTotalNumTv;
	private SeekBar mSettingBar;

	// check id view
	// killler check userinfo view
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
	private View mRoleLayout;
	private Size mPreviewSize, mPictureSize;

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

	// Killer check guide result
	private View mCheckGuideResultView;
	private ImageView mContinueBtn;

	// killer result guide first view
	//有遗言死亡界面
	private View mResultGuideFirstView;
	private ImageView mVoteBtn;
	private TextView mDeadStatusFirstTV;
	
	//killer result guide second view
	//无遗言死亡界面
	private View mResultGuideSecondView;
	private ImageView mNextStepBtn;
	private TextView mDeadStatusSecondTv;
	
	//killer lose view
	private View mKillerLoseView;
	private ImageView mPunishBtn;
	private ImageView mBackToBtn;
	private ImageView mRestartBtn;
	private ImageView mShareBtn;
	private LinearLayout mKillerLoseShowLL;
	
	//killer win view
	private View mKillerWinView;
	private TextView mPoliceTipsTv;
	private ImageView mSecPunishBtn;
	private ImageView mSecBackToBtn;
	private ImageView mSecRestartBtn;
	private ImageView mSecShareBtn;
	private LinearLayout mKillerWinShowLL;

	// bottom view
	private RelativeLayout mBottomView;

	// player content view height
	private int mContentHeight = 0;

	// record what u doing
	private static final int INIT = 0;
	private static final int START = 1;
	private static final int KILL = 2;
	private static final int CHECK = 3;
	private static final int CHECKED = 4;
	private static final int VOTE = 5;
	private int mStatus = INIT;
	
	//current killed people
	private Killers mCurrentKilledIndetity;
	private int mCurrentKillerIndex;

	private static final int MSG_GET_HEIGHT = 0;
	private static final int MSG_SET_GRIDVIEW_LAYOUT = 1;
	private static final int MSG_CHECK_GUIDE = 2;
	private static final int MSG_CHECK_ROLE_FINISH = 3;
	private static final int MSG_KILLER_LOSE = 4;
	private static final int MSG_KILLER_WIN = 5;
	private static final int MSG_KILL_RESULT = 6;
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
				int columns = 3;// default is 3
				if (mTotalPlayerNum == 5 || mTotalPlayerNum == 6) {
					columns = 2;
				} else if (mTotalPlayerNum >= 7 && mTotalPlayerNum <= 9) {
					columns = 3;
				} else {
					columns = 4;
					// marginHight = 45;
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
				if (mPlayerAdapter != null)
					mPlayerAdapter.notifyDataSetChanged();
				mNextBtn.setEnabled(true);
				clickFlag = true;
				break;
			case MSG_KILLER_LOSE:
				mVoteBar.setVisibility(View.INVISIBLE);
				
				mCheckIdBtn.setVisibility(View.INVISIBLE);
				mKillerContentView.setVisibility(View.GONE);
				setAnimation(mKillerContentView, R.anim.slide_up_out);
				
				mKillerLoseView.setVisibility(View.VISIBLE);
				
				List<Integer> killerList = getPlayerList(Killers.Killer);
				View killerLoseView = null;
				for (int i = 0; i < killerList.size(); i++) {
					killerLoseView = getKillerLoseItemView(killerList.get(i));
					mKillerLoseShowLL.addView(killerLoseView);
				}
				break;
			case MSG_KILLER_WIN:
				List<Integer> policeList = getPlayerList(Killers.Police);
				String policeTip = "";
				switch (policeList.size()) {
				case 1:
					policeTip = getString(R.string.one_police_tip, policeList.get(0));
					break;
				case 2:
					policeTip = getString(R.string.one_police_tip, policeList.get(0), policeList.get(1));
					break;
				case 3:
					policeTip = getString(R.string.one_police_tip, policeList.get(0), policeList.get(1),
							policeList.get(2));
					break;
				case 4:
					policeTip = getString(R.string.one_police_tip, policeList.get(0), policeList.get(1),
							policeList.get(2), policeList.get(3));
					break;
				default:
					Log.e(TAG, "Error police num:" + policeList.size());
					break;
				}
				mVoteBar.setVisibility(View.INVISIBLE);
				
				mCheckIdBtn.setVisibility(View.INVISIBLE);
				mKillerContentView.setVisibility(View.GONE);
				setAnimation(mKillerContentView, R.anim.slide_up_out);
				
				mKillerWinView.setVisibility(View.VISIBLE);
				mPoliceTipsTv.setText(policeTip);
				
				List<Integer> killerList2 = getPlayerList(Killers.Killer);
				View killerWinItemView = null;
				for (int i = 0; i < killerList2.size(); i++) {
					killerWinItemView = getKillerWinItemView(killerList2.get(i), i);
					mKillerWinShowLL.addView(killerWinItemView);
				}
				break;
			case MSG_KILL_RESULT:
				String indetity = "";
				String statusTip = "";
				if (Killers.Police == mCurrentKilledIndetity) {
					indetity = getString(R.string.police);
				} else if (Killers.Killer == mCurrentKilledIndetity) {
					indetity = getString(R.string.killer);
				} else {
					indetity = getString(R.string.civilian);
				}
				
				if (getPlayerCount(Killers.Killer) >= getDeadCount()) {
					Log.d(TAG, "the dead has something to say");
					//死者有遗言
					statusTip = getString(R.string.killer_person_status, mCurrentKillerIndex, indetity);
				} else {
					Log.d(TAG, "the dead cannot say anything");
					//死者无遗言
					statusTip = getString(R.string.killer_person_no_status, mCurrentKillerIndex, indetity);
				}
				
				if (mStatus == CHECKED) {
					mCheckGuideResultView.setVisibility(View.GONE);
					
					mResultGuideFirstView.setVisibility(View.VISIBLE);
					mResultGuideSecondView.setVisibility(View.GONE);
					
					mDeadStatusFirstTV.setText(statusTip);
				} else if (mStatus == VOTE) {
					mVoteBar.setVisibility(View.INVISIBLE);
					
					mKillerContentView.setVisibility(View.GONE);
					setAnimation(mKillerContentView, R.anim.slide_up_out);
					mCheckIdBtn.setVisibility(View.GONE);
					
					mResultGuideFirstView.setVisibility(View.GONE);
					mResultGuideSecondView.setVisibility(View.VISIBLE);
					mDeadStatusSecondTv.setText(statusTip);
				}
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
		// 底部返回按钮
		mBackBtn = (ImageView) findViewById(R.id.iv_killer_game_back);
		mBackBtn.setOnClickListener(this);

		// 底部检查身份按钮
		mCheckIdBtn = (ImageView) findViewById(R.id.iv_killer_show_identity);
		mCheckIdBtn.setOnClickListener(this);
		mCheckIdBtn.setOnTouchListener(this);

		// 初始化界面，设置游戏人数
		mInitView = findViewById(R.id.kill_init);
		mStartDealBtn = (ImageView) findViewById(R.id.iv_start_deal);
		mStartDealBtn.setOnClickListener(this);
		mPoliceNumTv = (TextView) findViewById(R.id.tv_police_num);
		mCivilianNumTv = (TextView) findViewById(R.id.tv_civilian_num);
		mKillerNumTv = (TextView) findViewById(R.id.tv_killer_num);
		mTotalNumTv = (TextView) findViewById(R.id.tv_total_num);
		mSettingBar = (SeekBar) findViewById(R.id.bar_set_player);
		mSettingBar.setOnSeekBarChangeListener(this);
		updatePlayerNumber(mKillerNum, mTotalPlayerNum);

		// 发牌界面，设置玩家头像，身份等
		mCheckUserInfoView = findViewById(R.id.kill_check_userinfo);
		mNextBtn = (Button) findViewById(R.id.btn_next);
		mNextBtn.setText("1号查看身份");
		mNextBtn.setOnClickListener(this);
		mPlayerNumTv = (TextView) findViewById(R.id.tv_player_number);
		mRoleTv = (TextView) findViewById(R.id.tv_player_role);
		mSurfaceView = (SurfaceView) findViewById(R.id.killer_get_icon_for_user);
		userIcon = (ImageView) findViewById(R.id.killer_icon_for_user);
		mRoleLayout = findViewById(R.id.role_layout);
		mRoleLayout.setVisibility(View.INVISIBLE);
		cameraId = getCameraInfo();
		if (cameraId < 0) {
			mSurfaceView.setVisibility(View.GONE);
			userIcon.setImageResource(R.drawable.default_killer_03);
		} else {
			userIcon.setVisibility(View.INVISIBLE);
			mSurfaceHolder = mSurfaceView.getHolder();
			mSurfaceHolder.addCallback(mSurfaceCallback);
		}

		// 玩家列表，Gridview
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

		// 杀人指导，法官台词界面
		mKillGuideView = findViewById(R.id.kill_guide);
		mKillBtn = (ImageView) findViewById(R.id.iv_killer_ensure);
		mKillBtn.setOnClickListener(this);

		// 法官台词，验人界面
		mCheckGuideView = findViewById(R.id.check_guide);
		mCheckBtn = (ImageView) findViewById(R.id.iv_check_img);
		mCheckBtn.setOnClickListener(this);

		// 法官台词，验人结束界面
		mCheckGuideResultView = findViewById(R.id.check_result);
		mContinueBtn = (ImageView) findViewById(R.id.iv_to_kill_result);
		mContinueBtn.setOnClickListener(this);

		// 法官台词，杀人结束界面, 发言
		mResultGuideFirstView = findViewById(R.id.kill_result_first);
		mVoteBtn = (ImageView) findViewById(R.id.iv_to_vote);
		mVoteBtn.setOnClickListener(this);
		mDeadStatusFirstTV = (TextView) findViewById(R.id.tv_dead_status_first);
		
		//法官台词，杀人结束界面，不能发言
		mResultGuideSecondView = findViewById(R.id.kill_result_second);
		mNextStepBtn = (ImageView) findViewById(R.id.btn_next_step);
		mNextStepBtn.setOnClickListener(this);
		mDeadStatusSecondTv = (TextView) findViewById(R.id.tv_dead_status_second);
		
		//好人胜利界面
		mKillerLoseView = findViewById(R.id.killer_lose);
		mPunishBtn = (ImageView) findViewById(R.id.killer_punish);
		mBackToBtn = (ImageView) findViewById(R.id.btn_tokiller);
		mRestartBtn = (ImageView) findViewById(R.id.btn_restart);
		mShareBtn = (ImageView) findViewById(R.id.btn_share);
		mPunishBtn.setOnClickListener(this);
		mBackToBtn.setOnClickListener(this);
		mRestartBtn.setOnClickListener(this);
		mShareBtn.setOnClickListener(this);		
		mKillerLoseShowLL = (LinearLayout) findViewById(R.id.ll_killer_lose_show);
		
		//杀手胜利界面
		mKillerWinView = findViewById(R.id.killer_win);
		mPoliceTipsTv = (TextView) findViewById(R.id.tv_police_tips);
		mSecPunishBtn = (ImageView) findViewById(R.id.sec_killer_punish);
		mSecBackToBtn = (ImageView) findViewById(R.id.sec_btn_tokiller);
		mSecRestartBtn = (ImageView) findViewById(R.id.sec_btn_restart);
		mSecShareBtn = (ImageView) findViewById(R.id.sec_btn_share);
		mSecPunishBtn.setOnClickListener(this);
		mSecBackToBtn.setOnClickListener(this);
		mSecRestartBtn.setOnClickListener(this);
		mSecShareBtn.setOnClickListener(this);		
		mKillerWinShowLL = (LinearLayout) findViewById(R.id.ll_killer_win_show);
	}
	
	private SurfaceHolder.Callback2 mSurfaceCallback = new SurfaceHolder.Callback2() {
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
				Parameters parameters = mCamera.getParameters();
				mPreviewSize = parameters.getSupportedPreviewSizes().get(0);
				mPictureSize = parameters.getSupportedPictureSizes().get(0);
			} catch (Exception e) {
				Log.e(TAG, "open camera fail!");
				e.printStackTrace();
				cameraId = -1;
				mSurfaceView.setVisibility(View.GONE);
				userIcon.setImageResource(R.drawable.default_killer_03);
				userIcon.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if (mCamera != null) {
				try {
					Camera.Parameters parameters = mCamera.getParameters();
					// 设置照片格式
					parameters.setPictureFormat(PixelFormat.JPEG);
					// 设置预浏尺寸
					parameters.setPreviewSize(mPreviewSize.width,
							mPreviewSize.height);
					// 设置照片分辨率
					parameters.setPictureSize(mPictureSize.width,
							mPictureSize.height);
					mCamera.setParameters(parameters);
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
			//do nothing
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_killer_game_back:
			if (mStatus != INIT) {
				showExitGameDialog();
			} else {
				finish();
			}
			break;
		case R.id.iv_start_deal:
			mStatus = START;
			mInitView.setVisibility(View.GONE);
			mCheckUserInfoView.setVisibility(View.VISIBLE);
			if (2 * mKillerNum >= mTotalPlayerNum || mTotalPlayerNum < 5
					|| mTotalPlayerNum > 16 || mKillerNum < 1) {
				Toast.makeText(this, "参加游戏人数不对", Toast.LENGTH_SHORT).show();
				finish();
			}
			initAllPlayerIdentity(mKillerNum, mTotalPlayerNum);
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
				mRoleLayout.setVisibility(View.INVISIBLE);
				if (mCamera != null && cameraId >= 0) {
					userIcon.setVisibility(View.INVISIBLE);
				}
				mNextBtn.setText(getString(R.string.n_check_indetity, index));
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
								if (bitmap == null) {
									Toast.makeText(getApplicationContext(),
											"照片消失了。。。。", Toast.LENGTH_SHORT)
											.show();
									userIcon.setImageResource(R.drawable.default_killer_03);
								} else {
									int width = bitmap.getWidth();
									int height = bitmap.getHeight();
									Matrix m = new Matrix();
									m.setScale(64.0f / width, 48.0f / height);
									m.postRotate(-(getDisplayOritation(
											getDispalyRotation(), cameraId)));
									mPlayerLists.get(index - 2).setHeadIcon(
											Bitmap.createBitmap(bitmap, 0, 0,
													width, height, m, true));
									userIcon.setImageBitmap(mPlayerLists.get(
											index - 2).getHeadIcon());
									bitmap.recycle();
									bitmap = null;
								}
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

				Killers killers = getPlayerIdentity();
				KPlayer player = new KPlayer(index - 1);
				player.setIdentity(killers);
				mPlayerLists.add(player);
				if (index > mTotalPlayerNum) {
					// setting player gridview ui
					mHandler.sendMessage(mHandler
							.obtainMessage(MSG_SET_GRIDVIEW_LAYOUT));

					mNextBtn.setText(R.string.complete);
				} else {
					mNextBtn.setText(R.string.transfer_to_next);
				}
				mPlayerNumTv.setText(getString(R.string.n_number, index - 1));
				String roleStr = "";
				if (Killers.Police == killers) {
					roleStr = getString(R.string.police);
				} else if (Killers.Killer == killers) {
					roleStr = getString(R.string.killer);
				} else {
					roleStr = getString(R.string.civilian);
				}
				mRoleTv.setText(roleStr);
				mRoleLayout.setVisibility(View.VISIBLE);
				mNextBtn.setEnabled(true);
			}
			break;
		case R.id.iv_killer_start:
			// start game
			mCheckIdBtn.setVisibility(View.INVISIBLE);

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
			//init checked status
			for(KPlayer player : mPlayerLists){
				if (player.isChecked()) {
					player.setChecked(false);
					mPlayerAdapter.notifyDataSetChanged();
				}
			}
			mStatus = CHECKED;

			mCheckIdBtn.setVisibility(View.INVISIBLE);
			mKillerContentView.setVisibility(View.GONE);
			setAnimation(mKillerContentView, R.anim.slide_up_out);

			mCheckGuideResultView.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_to_kill_result:
			//continue
			mHandler.sendMessage(mHandler.obtainMessage(MSG_KILL_RESULT));
			break;
		case R.id.iv_to_vote:
			//go to vote view
			mStatus = VOTE;
			mResultGuideFirstView.setVisibility(View.GONE);
			
			mCheckIdBtn.setVisibility(View.VISIBLE);
			mKillerContentView.setVisibility(View.VISIBLE);
			setAnimation(mKillerContentView, R.anim.slide_down_in);
			mTipView.setText(R.string.killer_tip_vote);
			mTipView.setVisibility(View.VISIBLE);
			mKillerStartBtn.setVisibility(View.GONE);
			
			mCheckFinishBtn.setVisibility(View.GONE);
			break;
		case R.id.killer_punish:
		case R.id.sec_killer_punish:
			//wait for code
			break;
		case R.id.btn_tokiller:
		case R.id.sec_btn_tokiller:
			//回到杀人游戏主界面，重新设置人数
			Log.d(TAG, "back to kill main ui");
			backToInitView();
			mKillerWinShowLL.removeAllViews();
			mKillerLoseShowLL.removeAllViews();
			break;
		case R.id.btn_restart:
		case R.id.sec_btn_restart:
			//重新开始，按当前的人数重新检查身份
			Log.d(TAG, "restart");
			restartGame();
			mKillerWinShowLL.removeAllViews();
			mKillerLoseShowLL.removeAllViews();
			break;
		case R.id.btn_share:
		case R.id.sec_btn_share:
			//Share your result
			break;
		case R.id.btn_next_step:
			//下一轮
			mResultGuideSecondView.setVisibility(View.GONE);
			mKillGuideView.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}
	
	/**
	 * back to kill game main ui and set player again
	 */
	private void backToInitView(){
		mTotalPlayerNum = 5;
		mKillerNum = 1;
		
		mInitView.setVisibility(View.VISIBLE);
		mSettingBar.setProgress(0);
		
		mPlayerLists.clear();
		index = 1;
		
		mStatus = INIT;
		mTipView.setVisibility(View.GONE);
		mKillerStartBtn.setVisibility(View.VISIBLE);
		
		initAllPlayerIdentity(mKillerNum, mTotalPlayerNum);
		setPlayerNumber(mTotalPlayerNum);
		mNextBtn.setText(getString(R.string.n_check_indetity, index));
		clickFlag = false;
		
		mRoleLayout.setVisibility(View.INVISIBLE);
		
		mCheckUserInfoView.setVisibility(View.GONE);
		mKillerContentView.setVisibility(View.GONE);
		mCheckIdBtn.setVisibility(View.GONE);
		mKillGuideView.setVisibility(View.GONE);
		mCheckGuideView.setVisibility(View.GONE);
		mCheckGuideResultView.setVisibility(View.GONE);
		mResultGuideFirstView.setVisibility(View.GONE);
		mResultGuideSecondView.setVisibility(View.GONE);
		mKillerLoseView.setVisibility(View.GONE);
		mKillerWinView.setVisibility(View.GONE);
	}
	
	/**
	 * restart kill game
	 */
	private void restartGame(){
		mPlayerLists.clear();
		index = 1;
		
		mStatus = START;
		mTipView.setVisibility(View.GONE);
		mKillerStartBtn.setVisibility(View.VISIBLE);
		
		initAllPlayerIdentity(mKillerNum, mTotalPlayerNum);
		mNextBtn.setText(getString(R.string.n_check_indetity, index));
		clickFlag = false;
		
		mRoleLayout.setVisibility(View.INVISIBLE);
		
		mCheckUserInfoView.setVisibility(View.VISIBLE);
		mKillerLoseView.setVisibility(View.GONE);
		mKillerWinView.setVisibility(View.GONE);
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
		//当为警察验人时，点击方有效，否则无效
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
		if (mStatus != KILL && mStatus != VOTE) {
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
				mCurrentKilledIndetity = player.getIdentity();
				mCurrentKillerIndex = player.getNumber();
				if (Killers.Police == mCurrentKilledIndetity) {
					Log.d(TAG, "you killed a police");
					// If killer killed police
					// judge police count
					if (0 == getPlayerCount(Killers.Police)) {
						// game over,killer win
						mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_KILLER_WIN), 1000);
						return false;
					}
				} else if (Killers.Cilivian == mCurrentKilledIndetity) {
					Log.d(TAG, "you killed a Cilivian");
					// If killer killed civilian
					// judge civilian count
					if (0 == getPlayerCount(Killers.Cilivian)) {
						// game over,killer win
						mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_KILLER_WIN), 1000);
						return false;
					}
				} else {
					Log.d(TAG, "you killed a Killer");
					// If killer killed Killer
					// judge killer count
					if (0 == getPlayerCount(Killers.Killer)) {
						// game over, killer lose
						mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_KILLER_LOSE), 1000);
						return false;
					}
				}
				// going on
				if (mStatus == VOTE) {
					mHandler.sendEmptyMessageDelayed(MSG_KILL_RESULT, 1000);
				} else {
					mHandler.sendEmptyMessageDelayed(MSG_CHECK_GUIDE, 1000);
				}
			}
		}
		return false;
	}

	/**get spec player people num,获得指定身份还活着的人数*/
	private int getPlayerCount(Killers killer) {
		int count = 0;
		for (KPlayer player : mPlayerLists) {
			if (killer == player.getIdentity() && !player.isDead()) {
				count++;
			}
		}
		return count;
	}
	
	/**get dead people number*/
	private int getDeadCount(){
		int count = 0;
		for(KPlayer player : mPlayerLists){
			if (player.isDead()) {
				count ++ ;
			}
		}
		return count;
	}
	
	/**get police list*/
	private List<Integer> getPlayerList(Killers player){
		List<Integer> list = new ArrayList<Integer>();
		for (KPlayer kPlayer : mPlayerLists) {
			if (player == kPlayer.getIdentity()) {
				list.add(kPlayer.getNumber());
			}
		}
		return list;
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
		setPlayerNumber(mTotalPlayerNum);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		//do nothing
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		//do nothing
	}

	/**
	 * set killer number by total player number
	 * @param totalPlayerNumber
	 */
	private void setPlayerNumber(int totalPlayerNumber) {
		mKillerNum = 0;
		switch (totalPlayerNumber) {
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
		updatePlayerNumber(mKillerNum, totalPlayerNumber);
	}

	/**
	 * set player numbers
	 * @param n killer & police numbers
	 * @param number total players number
	 */
	private void updatePlayerNumber(int n, int number) {
		mTotalNumTv.setText(getString(R.string.n_people, number));
		setSpannableString(mKillerNumTv, getString(R.string.n_killer, n));
		setSpannableString(mPoliceNumTv, getString(R.string.n_police, n));
		setSpannableString(mCivilianNumTv, getString(R.string.n_civilian, number - 2 * n));
	}
	
	private void setSpannableString(TextView tv, String msg){
		SpannableString spannableString = new SpannableString(msg);
		ForegroundColorSpan span = new ForegroundColorSpan(Color.WHITE);
		spannableString.setSpan(span, 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(spannableString);
	}

	/**
	 * get player identity by random way
	 * @return a identity
	 */
	private Killers getPlayerIdentity() {
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
	 * init all player identity by totalNumber & killerNumber
	 * 根据总共玩家和杀手玩家数，预先分配好警察，杀手和平民的人数
	 * @param killerNumber
	 * @param totalNumber
	 */
	private void initAllPlayerIdentity(int killerNumber, int totalNumber) {
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
	
	/**
	 * get Killer win item View
	 * @param killerNumber the killer number
	 * @param index the killer index,like: the n killer
	 * @return item view
	 */
	private View getKillerWinItemView(int killerNumber, int index){
		View killerWinItemView = null;
		ImageView killerWinBg = null;
		ImageView killerWinPhoto = null;
		TextView killerWinNum = null;
		
		killerWinItemView = getLayoutInflater().inflate(R.layout.killer_win_item, null);
		killerWinBg = (ImageView) killerWinItemView.findViewById(R.id.killer_win_bg);
		killerWinPhoto = (ImageView) killerWinItemView.findViewById(R.id.killer_avatar_photo);
		killerWinNum = (TextView) killerWinItemView.findViewById(R.id.killer_win_num);
		killerWinNum.setText(getString(R.string.n_number, killerNumber));
		
		Bitmap bitmap = mPlayerLists.get(killerNumber - 1).getHeadIcon();
		if (bitmap != null) {
			killerWinPhoto.setImageBitmap(bitmap);
		} else {
			killerWinPhoto.setImageResource(R.drawable.killer_profile_color);
		}
		
		switch (index) {
		case 0:
			killerWinBg.setImageResource(R.drawable.killer_win_redflag);
			break;
		case 1:
			killerWinBg.setImageResource(R.drawable.killer_win_blueflag);
			break;
		case 2:
			killerWinBg.setImageResource(R.drawable.killer_win_greenflag);
			break;
		case 3:
			killerWinBg.setImageResource(R.drawable.killer_win_yellowflag);
			break;
		default:
			break;
		}
		
		return killerWinItemView;
	}
	
	/**
	 * get Killer lose item view
	 * @param killerNumber the killer number
	 */
	private View getKillerLoseItemView(int killerNumber){
		ImageView imageView = new ImageView(getApplicationContext());
		int height = (int) getResources().getDimension(R.dimen.photo_height);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				height, height);
		int marginRight = (int) getResources().getDimension(R.dimen.gridview_vertion_space);
		params.setMargins(0, 0, marginRight, 0);
		
		imageView.setLayoutParams(params);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		
		Bitmap bitmap = mPlayerLists.get(killerNumber - 1).getHeadIcon();
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageResource(R.drawable.killer_grey);
		}
		
		return imageView;
	}
	
	private void showExitGameDialog(){
		ConfirmDialog dialog = new ConfirmDialog(this, R.layout.dialog_confirm, R.style.MyDialog);
		dialog.setOnClickListener(new ZYOnClickListener() {
			@Override
			public void onClick(Dialog dialog, View view) {
				switch (view.getId()) {
				case R.id.btn_confirm:
					dialog.cancel();
					backToInitView();
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

	/** this method below just for camera take picture start */
	private int getDisplayOritation(int degrees, int cameraId) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;
		} else {
			result = (info.orientation - degrees + 360) % 360;
		}
		return result;
	}

	private int getDispalyRotation() {
		int i = getWindowManager().getDefaultDisplay().getRotation();
		switch (i) {
		case Surface.ROTATION_0:
			return 0;
		case Surface.ROTATION_90:
			return 90;
		case Surface.ROTATION_180:
			return 180;
		case Surface.ROTATION_270:
			return 270;
		}
		return 0;
	}
	/** this method up just for camera take picture end */
}
