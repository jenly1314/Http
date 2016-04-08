package org.king.dao;

import java.io.Serializable;

import org.king.utils.LogUtils;

import android.database.Cursor;
/**
 * @author Jenly
 *
 */
public class BaseDao implements Serializable{

	private static final long serialVersionUID = -6997239290880731728L;
	
	
	/**
	 * 关闭游标
	 * @param cursor
	 */
	public static void closeCursor(Cursor cursor){
		if(cursor != null){
			try{
				cursor.close();
			}catch(Throwable t){
				LogUtils.e("cursor close exception");
			}
			cursor = null;
		}
	}

}
