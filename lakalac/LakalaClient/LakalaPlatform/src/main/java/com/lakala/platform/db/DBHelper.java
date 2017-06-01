package com.lakala.platform.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lidroid.xutils.DbUtils;
/**
 * Created by huwei on 31/8/16.
 */
public class DBHelper extends SQLiteOpenHelper {
	private static final int version = 1;

	public DBHelper(Context context) {
		super(context, "", null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	private static DbUtils utils;

	public static void init(Context context) {
		utils = DbUtils.create(context, "Lakala.db");
		utils.configDebug(true);
		utils.configAllowTransaction(true);

	}

	public static DbUtils getUtils() {
		return utils;
	}

}
