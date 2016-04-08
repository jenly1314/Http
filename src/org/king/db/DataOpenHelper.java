package org.king.db;

import org.king.utils.LogUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataOpenHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "database.db";
	public static final int DATABASE_VERSION = 1;
	
	public DataOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		LogUtils.d("Start to create database.");
		createTables(db);
		
		LogUtils.d("Finish to create database.");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		LogUtils.d("Start to upgrade database: oldVersion="
				+ oldVersion + ", newVersion=" + newVersion);
		
		dropTables(db);
		createTables(db);
		
		LogUtils.d("Finish to upgrade database: oldVersion="
				+ oldVersion + ", newVersion=" + newVersion);
	}
	
	public void createTables(SQLiteDatabase db){
		LogUtils.d("Start to create tables...");
		createTest(db);
		
		LogUtils.d("Finish to create tables...");
	}
	
	public void createTest(SQLiteDatabase db){
		LogUtils.d("Start to create table -> " + DataConentProvider.TABLE_TEST);
		StringBuffer sql = new StringBuffer();
		sql.append("CREATE TABLE if not exists ");
		sql.append(DataConentProvider.TABLE_TEST);
		sql.append("(");
		sql.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
		sql.append("test_title varchar(20),");
		sql.append("test_date varchar(10),");
		sql.append("test_content varchar(200)");
		sql.append(")");
		db.execSQL(sql.toString());
		LogUtils.d("Finish to create table -> " + DataConentProvider.TABLE_TEST);
	}
	
	
	public void dropTables(SQLiteDatabase db){
		LogUtils.d("Start to drop tables...");
		
		dropTable(db,DataConentProvider.TABLE_TEST);
		
		LogUtils.d("Finish to drop tables...");
	}
	
	public void dropTable(SQLiteDatabase db,String tableName){
		LogUtils.d("Start to drop table -> " + tableName);
		
		db.execSQL("DROP TABLE if exists " + tableName);
		
		LogUtils.d("Finish to drop table -> " + tableName);
	}
	

}
