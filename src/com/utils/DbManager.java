package com.utils;


import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbManager {
	private static MySqliteHelper data_helper = null;
	public static String cur_user_name = null;

	public DbManager() {
		
	}

	public static MySqliteHelper getInstanceDb(Context context) {
		if (data_helper == null) {
			data_helper = new MySqliteHelper(context);
		}
		return data_helper;
	}
	
	
	public static void MyExecuteSql(String sql,Object[] para ) {
		if (data_helper == null) {
			return;
		}
		
		SQLiteDatabase db = data_helper.getWritableDatabase();
		if (para == null) {
			db.execSQL(sql);
		} else {
			db.execSQL(sql, para);		
		}
		db.close();
	}
	
	public static boolean isExistUser() {
		boolean flag = true;
		
		
		String sql_query = "select * from UserInfo";
		SQLiteDatabase db = data_helper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery(sql_query, null);
		
		if (cursor.getCount() == 0) {
			flag = false;
		} else {
			flag = true;
			cursor.moveToNext();
			cur_user_name = cursor.getString(0);
		}
		return flag;
	}
	
	/*
	 * 清空用户表
	 */
	public static void delUser() {
		String sql_user = "delete from UserInfo";
		SQLiteDatabase db = data_helper.getReadableDatabase();
		db.execSQL(sql_user);
	}
	
	public static void delWeight() {
		String sql_weight = "delete from WeightInfo";
		SQLiteDatabase db = data_helper.getReadableDatabase();
		db.execSQL(sql_weight);
	}
	
	public static void add(String sql) {
		SQLiteDatabase db = data_helper.getReadableDatabase();
		db.execSQL(sql);
	}
	
	public static void RawQueryWeight(List<ConstWeight> lst) {
		String sql_weight = "select * from WeightInfo";
		SQLiteDatabase db = data_helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql_weight, null);
		while(cursor.moveToNext()) {
			ConstWeight weight = new ConstWeight();
			weight.set_id(cursor.getString(0));
			weight.setWeight(cursor.getString(1));
			weight.setBmi(cursor.getString(2));
			weight.setStatus(cursor.getString(3));
			weight.setTime(cursor.getString(4));
			lst.add(weight);
		}
	}
	
	public static void RawQueryUser(ConstUserInfo userInfo) {
		String sql_user = "select * from UserInfo";
		SQLiteDatabase db = data_helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql_user, null);
		
		while(cursor.moveToNext()) {
			userInfo.set_id(cursor.getString(0));
			userInfo.setPwd(cursor.getString(1));
			userInfo.setName(cursor.getString(2));
			userInfo.setSex(cursor.getString(3));
			userInfo.setHeight(cursor.getString(4));
		}
	}
	
	/*
	public static void MyRawQuery(String sql, String[] para, int choice) {
		if (data_helper == null) {
			return;
		}
		SQLiteDatabase db = data_helper.getWritableDatabase();		
		Cursor cursor = db.rawQuery(sql, para);
		switch (choice) {
		case 0:
			while (cursor.moveToNext()) {
				WeightInfo info = new WeightInfo();
				info.setId(cursor.getString(0));
				info.setWeight(cursor.getDouble(1));
				info.setBMI(cursor.getDouble(2));
				info.setStatus(cursor.getString(3));
				info.setTime(cursor.getString(4));
				Log.i("WeightInfo", info.toString());
			}
			break;
		
		case 1:
			while (cursor.moveToNext()) {
				UserInfo info = new UserInfo();
				info.setId(cursor.getString(0));
				info.setPwd(cursor.getString(1));
				info.setName(cursor.getString(2));
				info.setSex(cursor.getString(3));
				info.setHeight(cursor.getInt(4));
				Log.i("UserInfo", info.toString());
			}
			break;
		default:
			break;
		}
		db.close();
	}
	*/
	
}
