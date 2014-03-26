package com.zhaoyan.game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.zhaoyan.game.db.DBHelper;
import com.zhaoyan.game.db.DbData;
import com.zhaoyan.game.db.DbData.SpyColumns;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;

public class MainActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "MainActivity";
	private Button mStartBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homepage);
		
		mStartBtn = (Button) findViewById(R.id.btn_start);
		mStartBtn.setOnClickListener(this);
		
		//init db
		//get dbHelper
		DBHelper dbHelper = new DBHelper(getApplicationContext(), DbData.DATABASE_NAME,
				null, DbData.DATABASE_VERSION);
		// query db
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(SpyColumns.TABLE_NAME, null, null, null, null,
				null, null);
		if (cursor != null && cursor.getCount() != 0) {
			// there has data,do nothing
		} else {
			//spy words db
			List<String> wordsList = getStringFromAssets("spy.zy");
			for (String sqlStr : wordsList) {
				db.execSQL(sqlStr);
			}
		}
		cursor.close();
	}
	
	private List<String> getStringFromAssets(String fileName) {
		List<String> resultList = new ArrayList<String>();
		try {
			InputStreamReader inputReader = new InputStreamReader(
					getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null){
				resultList.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_start:
			openActivity(IndexActivity.class);
			break;

		default:
			break;
		}
	}

}
