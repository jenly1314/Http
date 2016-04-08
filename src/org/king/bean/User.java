//package org.king.bean;
//
//import java.io.Serializable;
//
//import org.litepal.annotation.Column;
//import org.litepal.crud.DataSupport;
//
//import com.google.gson.Gson;
//import com.google.gson.annotations.SerializedName;
//
//public class User extends DataSupport implements Serializable{
//	
//	private static final long serialVersionUID = -5098062399733322974L;
//	
//
//	@SerializedName("_id")  
//	private int id;
//	
//	@SerializedName("id")  
//	private int sId;
//
//	@Column(unique = true, defaultValue = "unknown")
//	private String name;
//	
//	private String sign;
//	
//	private int age;
//	
//	private String sex;
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public String getSign() {
//		return sign;
//	}
//
//	public void setSign(String sign) {
//		this.sign = sign;
//	}
//
//	public int getAge() {
//		return age;
//	}
//
//	public void setAge(int age) {
//		this.age = age;
//	}
//
//	public String getSex() {
//		return sex;
//	}
//
//	public void setSex(String sex) {
//		this.sex = sex;
//	}
//	
//    public int getsId() {
//		return sId;
//	}
//
//	public void setsId(int sId) {
//		this.sId = sId;
//	}
//
//	private int get_id() {
//    	return id;
//	}
//
//	private int getId() {
//		return id;
//	}
//	
//	private void setId(int id) {
//		this.id = id;
//	}
//
//	@Override
//	public String toString() {
//		return new Gson().toJson(this)+"\n";
//	}
//	
//
//}
