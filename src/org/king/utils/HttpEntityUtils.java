package org.king.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.king.http.HttpConstats;

/**
 * 
 * @author Jenly
 * @date 2014-8-8
 */
public class HttpEntityUtils {

	
	/**
	 * 将Map<String,String> 转成 HttpEntity
	 * @param map
	 * @return
	 */
	public static HttpEntity toHttpEntity(Map<String,String> map){
	
		return toHttpEntity(map, HttpConstats.UTF_8);
	}
	
	/**
	 * 将Map<String,String> 转成 HttpEntity
	 * @param map
	 * @param encoding 字符编码
	 * @return
	 */
	public static HttpEntity toHttpEntity(Map<String,String> map,String encoding){
		
		if(map!=null){
			
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for(Map.Entry<String, String> entry : map.entrySet()){
				list.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
			}
			
			try {
			
				LogUtils.i(URLEncodedUtils.format(list, encoding));
				return new UrlEncodedFormEntity(list,encoding);
			} catch (UnsupportedEncodingException e) {
				LogUtils.e(e);
				e.printStackTrace();
			}
		}
		
		return null;
	}
}
