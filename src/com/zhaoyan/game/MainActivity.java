package com.zhaoyan.game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zhaoyan.game.db.DbData.SpyColumns;

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
		Cursor cursor = getContentResolver().query(SpyColumns.CONTENT_URI, null, null, null, null);
		
		if (cursor != null && cursor.getCount() != 0) {
			// there has data,do nothing
		} else {
			//spy words db
			//使用assets，中文会有乱码，暂时未找到解决办法，改用arrays
//			List<String> wordsList = getStringFromAssets("spy.zy");
//			for (String sqlStr : wordsList) {
//				db.execSQL(sqlStr);
//			}
			String[][] words = getSpyWords(); 
			ContentValues values = null;
			for (int i = 0; i < words.length; i++) {
				values = new ContentValues();
				values.put(SpyColumns.WORD1, words[i][0]);
				values.put(SpyColumns.WORD2, words[i][1]);
				values.put(SpyColumns.GROUP, 1);
				getContentResolver().insert(SpyColumns.CONTENT_URI, values);
			}
			
		}
		cursor.close();
		
		getSpyWords();
	}
	
	private List<String> getStringFromAssets(String fileName) {
		List<String> resultList = new ArrayList<String>();
		try {
			InputStreamReader inputReader = new InputStreamReader(
					getResources().getAssets().open(fileName),"UTF-8");
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
	
	/**
	 * get spy words from array resource
	 * @return
	 */
	private String[][] getSpyWords(){
		String[][] results = null;
		String[] words = getResources().getStringArray(R.array.spy_words);
		results = new String[words.length][2];
		//separated by ","
		for(int i = 0; i < words.length ; i++){
			String[] itemWord = words[i].split(",");
			results[i][0] = itemWord[0];
			results[i][1] = itemWord[1];
		}
		return results;
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
