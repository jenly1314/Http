package org.king.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class DataConentProvider extends ContentProvider{
	
	
	
	public static final String AUTHORITY = "org.king.db.DataContentProvider";
	
	private static final String URI_PREFIX = "content://";
	private static final String URI_SEPATATER = "/";
	
	
	public static final String TABLE_TEST = "test";
	private static final int CODE_TEST = 1;
	
	private static final String TYPE_TEST = AUTHORITY + URI_SEPATATER + TABLE_TEST;
	
	public static final Uri URI_TEST = Uri.parse(URI_PREFIX + TYPE_TEST);
	
	
	public static final UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	
	private SQLiteOpenHelper sqLiteOpenHelper;
	
	static{
		URI_MATCHER.addURI(AUTHORITY, TABLE_TEST, CODE_TEST);
	}

	@Override
	public boolean onCreate() {
		sqLiteOpenHelper = new DataOpenHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
		
		Cursor c = db.query(getTableName(uri), projection, selection, selectionArgs, null, null, sortOrder);
		
		if(c!=null){
			c.setNotificationUri(getContext().getContentResolver(), uri);
		}
		
		return c;
	}

	@Override
	public String getType(Uri uri) {
		int match = URI_MATCHER.match(uri);
		
		switch (match) {
		case CODE_TEST:
			
			return TYPE_TEST;

		default:
			throw new IllegalArgumentException("Unknown Uri:" + uri);
		}
		
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
		
		long rowId = db.insert(getTableName(uri), null, values);
		if (rowId < 0) {
			throw new SQLException("Failed to insert row into " + uri);
		}
		
		Uri retUri = ContentUris.withAppendedId(uri, rowId);
		
		notifyChange(retUri);
		
		return retUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
		int count = db.delete(getTableName(uri), selection, selectionArgs);
		notifyChange(uri);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
		int count = db.update(getTableName(uri), values, selection, selectionArgs);
		notifyChange(uri);
		return count;
	}
	
	public void notifyChange(Uri uri) {
		notifyChange(uri,null);
	}
	
	public void notifyChange(Uri uri, ContentObserver observer) {
		getContext().getContentResolver().notifyChange(uri, observer);
	}
	
	public String getTableName(Uri uri){
		
		int match = URI_MATCHER.match(uri);
		
		switch (match) {
		case CODE_TEST:
			return TABLE_TEST;
			
		default:
			throw new IllegalArgumentException("Unknown Uri:" + uri);
		}
	}

}
