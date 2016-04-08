package org.king.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.king.bean.Contact;
import org.king.utils.LogUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

/**
 * 手机联系人
 * @author Jenly
 *
 */
public class ContactDao extends BaseDao{

	private static final long serialVersionUID = 8403092581381134455L;

	/**
	 * 获取所有联系人
	 * @param context
	 * @return
	 */
	public static List<Contact> getAllContact(Context context){
		List<Contact> list = new ArrayList<>();
		Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI, null, "data1 is not null", null, "sort_key COLLATE LOCALIZED ASC");
		
		if(cursor!=null){
			while(cursor.moveToNext()){
				list.add(getContactByCursor(context,cursor));
			}
			closeCursor(cursor);
		}
		return list;
	}
	
	/**
	 * 通过号码获取联系人信息
	 * @param context
	 * @param number
	 * @return
	 */
	public static Contact getContactByNumber(Context context,String number){
		
		Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI, null, "data1=?", new String[]{number},null);
		
		Contact contact = null;
		if(cursor!=null){
			if(cursor.moveToFirst()){
				contact = getContactByCursor(context,cursor);
			}
			closeCursor(cursor);
		}
		return contact;
	}
	
	/**
	 * 通过游标获取联系人
	 * @param cursor
	 * @return
	 */
	private static Contact getContactByCursor(Context context,Cursor cursor){
		
		Contact contact = new Contact();
		contact.setId(cursor.getString(cursor.getColumnIndex(Phone._ID)));
		contact.setName(cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME)));
		contact.setPhone(cursor.getString(cursor.getColumnIndex(Phone.DATA1)));
		int rawIndx = cursor.getColumnIndex(Phone.CONTACT_ID);
		contact.setSortKey(fromatSortKey(getSortKeyString(context,rawIndx)));
		contact.setPingying(cursor.getString(cursor.getColumnIndex(Phone.SORT_KEY_PRIMARY)));
		LogUtils.d(contact.toString());
		return contact;
	}
	
	private static String getSortKeyString(Context context,long rawContactId) {  
	    String where = ContactsContract.RawContacts.CONTACT_ID + " ="  
	            + rawContactId;  
	    String[] projection = { Phone.SORT_KEY_PRIMARY };  
	    Cursor cur = context.getContentResolver().query(  
	            ContactsContract.RawContacts.CONTENT_URI, projection, where,  
	            null, null);  
	    int sortIndex = cur.getColumnIndex(Phone.SORT_KEY_PRIMARY);  
	    cur.moveToFirst();  
	    String sortValue = cur.getString(sortIndex);  
	    closeCursor(cur); 
	    return sortValue;  
	}
	
	/**
	 * 格式化
	 * @param sortKey
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static  String fromatSortKey(String sortKey) {
		if (TextUtils.isEmpty(sortKey) || sortKey.trim().length()==0) {
			return "#";
		}
		
		char c = sortKey.trim().substring(0, 1).charAt(0);
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		}
		
//		String c = sortKey.trim().substring(0, 1);
//		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
//		if (pattern.matcher(c).matches()) {
//			return (c).toUpperCase();
//		}
		return "#";
		
	}

}
