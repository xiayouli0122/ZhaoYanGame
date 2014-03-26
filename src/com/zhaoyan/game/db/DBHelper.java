package com.zhaoyan.game.db;

import com.zhaoyan.game.db.DbData.SpyColumns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//create table
		String sql = "create table "
				+SpyColumns.TABLE_NAME
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ SpyColumns.WORD1 + " TEXT, "
				+ SpyColumns.WORD2 + " TEXT, "
				+ SpyColumns.GROUP + " INTEGER);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//������ݿ�汾�����仯����ɾ���ؽ�
		String dropTable = "DROP TABLE IF EXISTS " + DbData.SpyColumns.TABLE_NAME;
		db.execSQL(dropTable);
		onCreate(db);
	}

}