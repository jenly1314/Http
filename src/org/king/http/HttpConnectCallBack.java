package org.king.http;

/**
 * Http连接请求回调
 * @author Jenly
 * @date 2014-8-7
 */
public abstract class HttpConnectCallBack implements HttpCallBack<String>{

	@Override
	public abstract void onSuccess(String content);
	
}