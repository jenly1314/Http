package org.king.bean;

import java.io.Serializable;

public class Music implements Serializable{
	
	private int id;
	
	private String name;
	
	private String artist;
	
	private String album;
	
	private long time;
	
	private int size;
	
	private String path;
	
	public Music() {
		super();
	}

	public Music(int id, String name, String artist, String album, long time,
			int size, String path) {
		super();
		this.id = id;
		this.name = name;
		this.artist = artist;
		this.album = album;
		this.time = time;
		this.size = size;
		this.path = path;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	

}
