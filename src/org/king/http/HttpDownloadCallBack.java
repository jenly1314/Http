package org.king.http;

import java.io.File;

/**
 * Http下载回调
 * @author Jenly
 * @date 2014-8-7
 */
public abstract class HttpDownloadCallBack implements HttpCallBack<File>{

	@Override
	public abstract void onSuccess(File file);
}