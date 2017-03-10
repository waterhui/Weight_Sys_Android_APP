package com.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class MySqliteHelper extends SQLiteOpenHelper {

	
	public MySqliteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	public MySqliteHelper(Context context) {
		super(context, ConstWeight.DATABASE_NAME, null, ConstWeight.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("TAG","---onCreate---");
		
		
		String sql_weight = "create table "+ConstWeight.TABLE_NAME+"( " +
            ConstWeight.ATTR_ID + " TEXT(20) not null," +
            ConstWeight.ATTR_Weight + " REAL(20,1) not null," +
            ConstWeight.ATTR_BMI + " REAL(20,1) not null,"  +
            ConstWeight.ATTR_STATUS + " TEXT(10) not null," +
            ConstWeight.ATTR_Time + " TEXT(20) not null " +
            ")";
		
		String sql_member_Info = "create table " + ConstUserInfo.TABLE_NAME + "( " +
			ConstUserInfo.ATTR_ID + " TEXT(20) primary key not null, " +
			ConstUserInfo.ATTR_PWD + " TEXT(20) not null, " +
			ConstUserInfo.ATTR_NAME + " TEXT(20) not null, " + 
			ConstUserInfo.ATTR_SEX + " TEXT(5) not null, " + 
			ConstUserInfo.ATTR_HEIGHT + " TEXT(5) not null " + 
			")";
		
		db.execSQL(sql_weight);
		db.execSQL(sql_member_Info);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}
	
}
