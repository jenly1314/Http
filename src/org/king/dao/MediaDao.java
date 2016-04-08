package org.king.dao;

import java.util.ArrayList;
import java.util.List;

import org.king.bean.Music;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio;

public class MediaDao extends BaseDao{

	private static final long serialVersionUID = 2079650973720533690L;
	
	/**
	 * 获取所有音乐
	 * @param context
	 * @return
	 */
	public static List<Music> getAllMusic(Context context){
		List<Music> list = new ArrayList<>();
		Cursor cursor = context.getContentResolver().query(Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,null);
		
		if(cursor!=null){
			while(cursor.moveToNext()){
				list.add(getMusicByCursor(cursor));
			}
			closeCursor(cursor);
		}
		return list;
	}
	
	/**
	 * 通过游标获取音乐
	 * @param cursor
	 * @return
	 */
	private static Music getMusicByCursor(Cursor cursor){
		
		Music music = new Music();
		music.setId(cursor.getInt(cursor.getColumnIndex(Audio.Media._ID)));
		music.setName(cursor.getString(cursor.getColumnIndex(Audio.Media.DISPLAY_NAME)));
		music.setAlbum(cursor.getString(cursor.getColumnIndex(Audio.Media.ALBUM)));
		music.setArtist(cursor.getString(cursor.getColumnIndex(Audio.Media.ARTIST)));
		music.setTime(cursor.getLong(cursor.getColumnIndex(Audio.Media.TITLE)));
		music.setSize(cursor.getInt(cursor.getColumnIndex(Audio.Media.SIZE)));
		music.setPath(cursor.getString(cursor.getColumnIndex(Audio.Media.DATA)));
		
		return music;
	}

}
