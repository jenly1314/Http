package org.king.http;

/**
 * Http连接回调接口
 * @author Jenly
 * @date 2014-8-7
 */
public interface HttpCallBack<T>{
	
	/**
	 * 开始
	 * @param url
	 */
	void onStart(String url);
	
	/**
	 * 加载…
	 * @param progress
	 * @param count
	 */
	void onLoading(long progress,long count);
	
	/**
	 * 成功
	 * @param t 返回的对象
	 */
	void onSuccess(T t);
	
	/**
	 * 失败
	 * @param error
	 * @param e
	 */
	void onFailure(String error,Throwable e);
	

	/**
	 * 取消
	 */
	void onCancel();
}