package org.king.bean;

import java.io.Serializable;

public class Contact implements Serializable{
	
	private static final long serialVersionUID = -4608280279637205346L;

	private String id;
	
	private String name;
	
	private String phone;
	
	private String sortKey;
	
	private String pingying;
	

	public Contact() {
		super();
	}
	
	public Contact(String id, String name, String phone, String sortKey) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.sortKey = sortKey;
	}




	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}
	
	public String getPingying() {
		return pingying;
	}

	public void setPingying(String pingying) {
		this.pingying = pingying;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+":{"+"id:"+id+",name:"+name+",phone:"+phone+",sortkey:"+sortKey+",pingying:"+pingying+"}";
	}
	

}
