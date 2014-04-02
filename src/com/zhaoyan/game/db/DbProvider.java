package com.zhaoyan.game.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.zhaoyan.game.db.DbData.SpyColumns;
import com.zhaoyan.game.util.Log;

public class DbProvider extends ContentProvider {
	private static final String TAG = "DbProvider";
	private SQLiteDatabase mSqLiteDatabase;
	private DBHelper mDatabaseHelper;
	
	public static final int SPY_COLLECTION = 1;
	public static final int SPY_SINGLE = 2;
	public static final int SPY_FILTER = 3;
	
	public static final UriMatcher uriMatcher;
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(DbData.AUTHORITY, "spy", SPY_COLLECTION);
		uriMatcher.addURI(DbData.AUTHORITY, "spy/#", SPY_SINGLE);
		uriMatcher.addURI(DbData.AUTHORITY, "spy_filter/*", SPY_FILTER);
	}
	
	@Override
	public boolean onCreate() {
		mDatabaseHelper = new DBHelper(getContext());
		return (mDatabaseHelper == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		switch (uriMatcher.match(uri)) {
		case SPY_COLLECTION:
			qb.setTables(SpyColumns.TABLE_NAME);
			break;
		case SPY_SINGLE:
			qb.setTables(SpyColumns.TABLE_NAME);
			qb.appendWhere("_id=");
			qb.appendWhere(uri.getPathSegments().get(1));
			break;
		case SPY_FILTER:
			Log.d(TAG, "KEY_FILTER:" + uri + ",serarch:" + uri.getPathSegments().get(1));
			qb.setTables(SpyColumns.TABLE_NAME);
			//TODO
			break;
		default:
			throw new IllegalArgumentException("Unknow uri:" + uri);
		}
		
		mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();
		Cursor ret = qb.query(mSqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
		
		if (ret != null) {
			ret.setNotificationUri(getContext().getContentResolver(), uri);
		}
		
		return ret;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case SPY_COLLECTION:
			return SpyColumns.CONTENT_TYPE;
		case SPY_SINGLE:
			return SpyColumns.CONTENT_TYPE_ITEM;
		default:
			throw new IllegalArgumentException("Unkonw uri:" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, "insert db:" + uri);
		switch (uriMatcher.match(uri)) {
		case SPY_COLLECTION:
		case SPY_SINGLE:
			mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
			long rowId = mSqLiteDatabase.insertWithOnConflict(SpyColumns.TABLE_NAME, "", 
					values, SQLiteDatabase.CONFLICT_REPLACE);
			if (rowId > 0) {
				Uri rowUri = ContentUris.withAppendedId(SpyColumns.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(uri, null);
				return rowUri;
			}
			throw new IllegalArgumentException("Cannot insert into uri:" + uri);
		default:
			throw new IllegalArgumentException("Unknow uri:" + uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
		
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case SPY_COLLECTION:
			count = mSqLiteDatabase.delete(SpyColumns.TABLE_NAME, selection, selectionArgs);
			break;
		case SPY_SINGLE:
			String segment = uri.getPathSegments().get(1);
			if (selection != null && segment.length() > 0) {
				selection = "_id=" + segment + " AND (" + selection + ")";
			}else {
				selection = "_id=" +  segment;
			}
			count = mSqLiteDatabase.delete(SpyColumns.TABLE_NAME, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("UnKnow Uri:" + uri);
		}
		
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count;
		long rowId = 0;
		int match = uriMatcher.match(uri);
		mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
		
		switch (match) {
		case SPY_SINGLE:
			String segment = uri.getPathSegments().get(1);
			rowId = Long.parseLong(segment);
			
			count = mSqLiteDatabase.update(SpyColumns.TABLE_NAME, values, "_id=" + rowId, null);
			break;
		case SPY_COLLECTION:
			count = mSqLiteDatabase.update(SpyColumns.TABLE_NAME, values, selection, null);
			break;
		default:
			throw new UnsupportedOperationException("Cannot update uri:" + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

}
