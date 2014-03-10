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
 * ����5���ˣ� 1killer��1police��3people
 * 
 * 1.��������˱��� 2.ɱ�����ۣ�ɱ�ˣ����� 3.�������ۣ����ٸ�֪������������ 4.���������ۣ���������if killer >=
 * ���������������������ԣ�else ������ 5.death ��ߵ��˿�ʼ������� 6.������ϣ�ͶƱ�����Ҳ����Ȩ��Ͷ����Ʊ���������if killer
 * >= ���������������������ԣ�else �����ԡ��������Ʊ����ͬ������Щ�˷��Ա����������ͶƱ�� 7.�ظ���������
 * killerȫ����policeʤ����police/peopleȫ������killerʤ����
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
		peopleNumber.setText(number + "��");
		killerNumber.setText("ɱ��" + n + "��");
		policeNumber.setText("����" + n + "��");
		personNumber.setText("ƽ��" + (number - 2 * n) + "��");

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
