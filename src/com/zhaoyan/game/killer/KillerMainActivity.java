package com.zhaoyan.game.killer;

import com.dreamlink.zhaoyan.tianheisharen.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * 至少5个人， 1killer，1police，3people
 * 
 * 1.天黑所有人闭眼 2.杀人睁眼，杀人，闭眼 3.警察睁眼，法官告知结果，警察闭眼 4.天亮，睁眼，宣布死者if killer >=
 * 死者人数，死者宣布遗言，else 无遗言 5.death 左边的人开始发表意见 6.发言完毕，投票表决（也可弃权不投）得票最多者死，if killer
 * >= 死者人数，死者宣布遗言，else 无遗言。如果多人票数相同，则这些人发言表决，其余人投票。 7.重复上述步骤
 * killer全死，police胜利，police/people全死，则killer胜利。
 * 
 * 
 * */
public class KillerMainActivity extends Activity implements OnClickListener {
	int peopleNum = 5, killerNum = 1;
	int numberP = 5;
	TextView peopleNumber, killerNumber, policeNumber, personNumber;
	SeekBar seekBar;
	ImageButton starButton, backButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.killer_first_layout);
		peopleNumber = (TextView) findViewById(R.id.textView4);
		killerNumber = (TextView) findViewById(R.id.textView1);
		policeNumber = (TextView) findViewById(R.id.textView2);
		personNumber = (TextView) findViewById(R.id.textView3);
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		starButton = (ImageButton) findViewById(R.id.start_button);
		backButton = (ImageButton) findViewById(R.id.imageButton1);

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				peopleNum = numberP + progress;
				setRoleNumber(peopleNum);

			}
		});
		starButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void setRoleNumber(int number) {
		killerNum = 0;
		switch (number) {
		case 5:
		case 6:
		case 7:
			killerNum = 1;
			break;
		case 8:
		case 9:
		case 10:
			killerNum = 2;
			break;
		case 11:
		case 12:
		case 13:
		case 14:
			killerNum = 3;
			break;
		case 15:
		case 16:
			killerNum = 4;
			break;
		default:
			break;
		}
		setInfo(killerNum, number);
	}

	private void setInfo(int n, int number) {
		peopleNumber.setText(number + "人");
		killerNumber.setText("杀手" + n + "人");
		policeNumber.setText("警察" + n + "人");
		personNumber.setText("平民" + (number - 2 * n) + "人");

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.imageButton1:
			this.finish();
			break;
		case R.id.start_button:
			Intent intent = new Intent();
			intent.setClass(this, KillerGameActivity.class);
			intent.putExtra("peopleNumber", peopleNum);
			intent.putExtra("killerNumber", killerNum);
			startActivity(intent);
			overridePendingTransition(0, 0);
			overridePendingTransition(0, 0);
			break;
		default:
			break;
		}
	}
}
