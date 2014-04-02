package com.zhaoyan.game.spy;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoyan.game.R;
import com.zhaoyan.game.db.DbData.SpyColumns;
import com.zhaoyan.game.util.Log;
import com.zhaoyan.game.util.ZyGameListener;

public class MyWordsAdapter extends CursorAdapter {
	private static final String TAG = "MyWordsAdapter";

	private final LayoutInflater mInflater;
	
	private int mCount = 0;
	private DeleteOnClick mDeleteOnClick = new DeleteOnClick();

	public MyWordsAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	protected void onContentChanged() {
		super.onContentChanged();
	}
	
	@Override
	public Cursor swapCursor(Cursor newCursor) {
		if (null == newCursor) {
			return newCursor;
		}
		Cursor oldCursor = super.swapCursor(newCursor);
		mCount = newCursor.getCount();
		Log.d(TAG, "new COunt = " + mCount);
		return oldCursor;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView civilianWordTV = (TextView) view.findViewById(R.id.tv_civilian_words);
		TextView spyWordTV = (TextView) view.findViewById(R.id.tv_spy_words);
		ImageView deleteIV = (ImageView) view.findViewById(R.id.iv_del_btn);
		deleteIV.setOnClickListener(mDeleteOnClick);
		
		MsgData msgData = new MsgData(cursor.getPosition());
		deleteIV.setTag(msgData);
		
		String word1 = cursor.getString(cursor.getColumnIndex(SpyColumns.WORD1));
		String word2 = cursor.getString(cursor.getColumnIndex(SpyColumns.WORD2));
		
		civilianWordTV.setText(word1);
		spyWordTV.setText(word2);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.spy_pop_words_list, parent, false);
	}
	
	class MsgData {
		int position;

		public MsgData(int position) {
			this.position = position;
		}
	}
	
	private class DeleteOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			MsgData msgData = (MsgData) v.getTag();
			int position = msgData.position;
			
			Cursor cursor = getCursor();
			cursor.moveToPosition(position);
			long key_id = cursor.getLong(cursor.getColumnIndex(SpyColumns._ID));
			
			Bundle bundle = new Bundle(2);
			bundle.putInt(ZyGameListener.CALLBACK_FLAG, ZyGameListener.MSG_DELETE_WORD);
			bundle.putLong(ZyGameListener.KEY_ITEM_ID, key_id);
			notifyActivityStateChanged(bundle);
		}
	}
	
	private static class Record{
		int mHashCode;
		ZyGameListener mCallBack;
	}
	
	private ArrayList<Record> mRecords = new ArrayList<Record>();
	public void registerKeyListener(ZyGameListener callBack){
		synchronized (mRecords) {
			//register callback in adapter,if the callback is exist,just replace the event
			Record record = null;
			int hashCode = callBack.hashCode();
			final int n = mRecords.size();
			for(int i = 0; i < n ; i++){
				record = mRecords.get(i);
				if (hashCode == record.mHashCode) {
					return;
				}
			}
			
			record = new Record();
			record.mHashCode = hashCode;
			record.mCallBack = callBack;
			mRecords.add(record);
		}
	}
	
	private void notifyActivityStateChanged(Bundle bundle){
		if (!mRecords.isEmpty()) {
			Log.d(TAG, "notifyActivityStateChanged.clients = " + mRecords.size());
			synchronized (mRecords) {
				Iterator<Record> iterator = mRecords.iterator();
				while (iterator.hasNext()) {
					Record record = iterator.next();
					
					ZyGameListener listener = record.mCallBack;
					if (listener == null) {
						iterator.remove();
						return;
					}
					
					listener.onCallBack(bundle);
				}
			}
		}
	}
	
	public void unregisterMyKeyListener(ZyGameListener callBack){
		remove(callBack.hashCode());
	}
	
	private void remove(int hashCode){
		synchronized (mRecords) {
			Iterator<Record> iterator =mRecords.iterator();
			while(iterator.hasNext()){
				Record record = iterator.next();
				if (record.mHashCode == hashCode) {
					iterator.remove();
				}
			}
		}
	}
}
