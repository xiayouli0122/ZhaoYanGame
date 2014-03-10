package com.zhaoyan.game.killer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.dreamlink.zhaoyan.tianheisharen.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class KillerGameActivity extends Activity implements OnClickListener {
	int cameraId;
	TextView numberTextView, roleTextView;
	ImageView userIcon, roleInfo;
	SurfaceView mSurfaceView;
	Button nextButton;
	int peopleNumber, killerNumber;
	SurfaceHolder holder;
	Camera mCamera;
	Random mRandom;
	int index = 1;
	ArrayList<Integer> role;
	ArrayList<RoleInfomation> data;
	boolean clickFlag = false;
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
				// TODO: handle exception
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

	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (data == null) {
			data = new ArrayList<RoleInfomation>();
		}
		peopleNumber = getIntent().getIntExtra("peopleNumber", 0);
		killerNumber = getIntent().getIntExtra("killerNumber", 0);
		if (2 * killerNumber >= peopleNumber || peopleNumber < 5
				|| peopleNumber > 16 || killerNumber < 1) {
			Toast.makeText(this, "参加游戏人数不对", Toast.LENGTH_SHORT).show();
			finish();
		}
		initRole(killerNumber, peopleNumber);
		setContentView(R.layout.killer_check_userinfo);
		cameraId = getCameraInfo();
		mSurfaceView = (SurfaceView) findViewById(R.id.killer_get_icon_for_user);
		userIcon = (ImageView) findViewById(R.id.killer_icon_for_user);
		nextButton = (Button) findViewById(R.id.killer_next);
		numberTextView = (TextView) findViewById(R.id.killer_number);
		roleTextView = (TextView) findViewById(R.id.killer_role);
		nextButton.setText("1号查看身份");
		roleInfo = (ImageView) findViewById(R.id.imageView1);
		numberTextView.setVisibility(View.INVISIBLE);
		roleTextView.setVisibility(View.INVISIBLE);
		roleInfo.setVisibility(View.INVISIBLE);
		nextButton.setOnClickListener(this);
		if (cameraId < 0) {
			mSurfaceView.setVisibility(View.GONE);
		} else {
			userIcon.setVisibility(View.INVISIBLE);
			holder = mSurfaceView.getHolder();
			holder.addCallback(callback);
		}
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

	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.killer_next:
			if (index > peopleNumber) {
				// TODO 身份检查完毕
				return;
			}
			if (clickFlag) {
				mCamera.startPreview();
				numberTextView.setVisibility(View.INVISIBLE);
				roleTextView.setVisibility(View.INVISIBLE);
				roleInfo.setVisibility(View.INVISIBLE);
				if (mCamera != null && cameraId >= 0)
					userIcon.setVisibility(View.INVISIBLE);
				nextButton.setText(index + "号查看身份");
				clickFlag = false;
			} else {
				index++;
				if (mCamera != null && cameraId >= 0) {
					nextButton.setEnabled(false);
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
								handler.sendEmptyMessage(0);
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
				String info = confirmRole();
				RoleInfomation roleInfomation = new RoleInfomation();
				roleInfomation.icon = userIcon.getDrawingCache();
				roleInfomation.index = index - 1;
				roleInfomation.role = info;
				roleInfomation.isDead = false;
				if (data == null) {
					data = new ArrayList<RoleInfomation>();
				}
				data.add(roleInfomation);
				if (index > peopleNumber) {
					testData();
					nextButton.setText("完成");
				} else {
					nextButton.setText("传递给" + index + "号");
				}
				numberTextView.setText((index - 1) + "号");
				roleTextView.setText(info);
				numberTextView.setVisibility(View.VISIBLE);
				roleTextView.setVisibility(View.VISIBLE);
				roleInfo.setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}

	}

	private String confirmRole() {
		if (mRandom == null) {
			mRandom = new Random();
		}
		if (role.size() > 0) {
			int n = mRandom.nextInt(role.size());
			n = role.remove(n);
			switch (n) {
			case 0:
				return "杀手";
			case 1:
				return "警察";
			case 2:
				return "平民";
			default:
				return "平民";
			}
		} else {
			return "平民";
		}

	}

	/**
	 * in role list if 0 mean killer,1 mean police ,2 mean person
	 * */
	private void initRole(int killerNumber, int peopleNumber) {
		if (role == null)
			role = new ArrayList<Integer>();
		else
			role.clear();
		for (int i = 0; i < peopleNumber; i++) {
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
		if (data != null) {
			for (int i = 0; i < data.size(); i++) {
				RoleInfomation info = data.get(i);
				Log.e("ArbiterLiu", "index is : " + info.index
						+ "    role is : " + info.role);
			}
		}
	}

	private Handler handler = new Handler() {

		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			nextButton.setEnabled(true);
			clickFlag = true;
		}

	};
}
