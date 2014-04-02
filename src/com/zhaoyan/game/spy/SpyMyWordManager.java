package com.zhaoyan.game.spy;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhaoyan.game.R;
import com.zhaoyan.game.db.DbData.SpyColumns;
import com.zhaoyan.game.dialog.AddWordDialog;
import com.zhaoyan.game.dialog.ConfirmDialog;
import com.zhaoyan.game.dialog.ConfirmDialog.ZYOnClickListener;
import com.zhaoyan.game.util.Log;
import com.zhaoyan.game.util.ZyGameListener;

public class SpyMyWordManager extends FragmentActivity implements LoaderCallbacks<Cursor>, OnClickListener{
	private static final String TAG = "SpyMyWordManager";
	private ImageView mAddWordIV;
	private ListView mWordsListView;
	private ImageView mBackIV;
	
	private MyWordsAdapter mAdapter;
	
	public static final String[] COLUMNS_LOADER = new String[] {
		SpyColumns._ID, SpyColumns.WORD1, SpyColumns.WORD2, SpyColumns.GROUP};
	
	private ZyGameListener gameListener = new ZyGameListener() {
		@Override
		public void onCallBack(Bundle bundle) {
			int flag = bundle.getInt(ZyGameListener.CALLBACK_FLAG);
			Log.d(TAG, "onCallBack flag:" + flag);
			
			Message message = mHandler.obtainMessage(flag);
			message.setData(bundle);
			mHandler.removeMessages(flag);
			mHandler.sendMessage(message);
		}
	};
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Bundle bundle = null;
			bundle = msg.getData();
			final long id = bundle.getLong(ZyGameListener.KEY_ITEM_ID);
			switch (msg.what) {
			case ZyGameListener.MSG_DELETE_WORD:
				ConfirmDialog dialog = new ConfirmDialog(SpyMyWordManager.this, R.layout.dialog_confirm, R.style.Custom_Dialog);
				TextView textView = (TextView) dialog.findViewById(R.id.noti_txt);
				textView.setText(R.string.spy_delete_word_tip);
				dialog.setOnClickListener(new ZYOnClickListener() {
					@Override
					public void onClick(Dialog dialog, View view) {
						switch (view.getId()) {
						case R.id.btn_confirm:
							Uri uri = Uri.parse(SpyColumns.CONTENT_URI + "/" + id);
							getContentResolver().delete(uri, null, null);
							dialog.dismiss();
							setResult(Activity.RESULT_OK);
							break;
						case R.id.btn_cancel:
							dialog.dismiss();
							break;

						default:
							break;
						}
					}
				});
				dialog.show();
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spy_my_words);
		
		mAddWordIV = (ImageView) findViewById(R.id.iv_add_word);
		mAddWordIV.setOnClickListener(this);
		mBackIV = (ImageView) findViewById(R.id.iv_my_word_back);
		mBackIV.setOnClickListener(this);
		mWordsListView = (ListView) findViewById(R.id.lv_display_words);
		
		mAdapter = new MyWordsAdapter(getApplicationContext(), null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		mWordsListView.setAdapter(mAdapter);
		mAdapter.registerKeyListener(gameListener);
		
		getSupportLoaderManager().initLoader(0, null, this);
		setResult(RESULT_CANCELED);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String selection = SpyColumns.GROUP + "=?";;
		String[] selectionArgs = new String[]{SpyConstant.MY_WORDS + ""};
		return new CursorLoader(this, SpyColumns.CONTENT_URI, null, selection, selectionArgs, null);
	}
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		if (null == arg1) {
			com.zhaoyan.game.util.Log.d(TAG, "onLoadFinished cursor is null");
			return;
		}
		
		Log.d(TAG, "onLoadFinished. count = " + arg1.getCount());
		mAdapter.swapCursor(arg1);
	}
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_add_word:
			final AddWordDialog addWordDialog = new AddWordDialog(SpyMyWordManager.this);
			addWordDialog.setOnClickListener(new AddWordDialog.ZYOnClickListener() {
				@Override
				public void onClick(Dialog dialog, View view) {
					switch (view.getId()) {
					case R.id.btn_add:
						String civilianWord = addWordDialog.getCivilianWord();
						String spyWord = addWordDialog.getSpyWord();
						Log.d(TAG, "civilianword=" + civilianWord + ",spyword=" + spyWord);
						if (civilianWord.isEmpty() || spyWord.isEmpty()) {
							addWordDialog.setErrorMsg(R.string.spy_word_null);
						} else {
							if (isWordExist(civilianWord, spyWord)) {
								addWordDialog.setErrorMsg(R.string.spy_word_exist);
								break;
							}
							ContentValues values = new ContentValues();
							values.put(SpyColumns.WORD1, civilianWord);
							values.put(SpyColumns.WORD2, spyWord);
							values.put(SpyColumns.GROUP, SpyConstant.MY_WORDS);
							getContentResolver().insert(SpyColumns.CONTENT_URI, values);
							setResult(Activity.RESULT_OK);
							dialog.cancel();
						}
						
						break;
					case R.id.btn_cancel:
						dialog.cancel();
						break;
					default:
						break;
					}
				}
			});
			addWordDialog.show();
			break;
		case R.id.iv_my_word_back:
			this.finish();
			break;

		default:
			break;
		}
	}
	
	private boolean isWordExist(String word1, String word2){
		String selection = SpyColumns.WORD1 + "=?"
				+ " and " + SpyColumns.WORD2 + "=?"
				+ " and " + SpyColumns.GROUP + "=?";
		String[] selectionArgs = new String[]{word1,word2,SpyConstant.MY_WORDS + ""};
		Cursor cursor = getContentResolver().query(SpyColumns.CONTENT_URI, null, selection, selectionArgs, null);
		Log.d(TAG, "cursor:" + cursor + ",count=" + cursor.getCount());
		if (cursor == null || cursor.getCount() == 0) {
			cursor.close();
			return false;
		}
		return true;
	}
}		
