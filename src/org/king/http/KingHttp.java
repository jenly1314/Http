package org.king.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.king.utils.LogUtils;

import android.os.AsyncTask;
import android.text.TextUtils;

/**
 * Http网络通信(HttpURLConnection实现)
 * @author Jenly
 * @date 2014-8-7
 */
public class KingHttp extends AbstractHttp{
	

	public KingHttp() {
		super();
	}

	public KingHttp(int connectionTimeOut, int soTimeOut) {
		super(connectionTimeOut, soTimeOut);
	}

	private HttpURLConnection getHttpURLConnection(String url,HttpMethod httpMethod,Map<String,String> params) throws MalformedURLException, IOException{
		
		String paras = null;
		if(params!=null){
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for(String key : params.keySet()){
				list.add(new BasicNameValuePair(key, params.get(key)));
			}
			paras = URLEncodedUtils.format(list, DEFAULT_ENCODING);
		}
		
		if(HttpMethod.GET ==httpMethod && paras!=null){
			url += URL_AND_PARA_SEPARATOR;
			url += paras;
		}
		HttpURLConnection httpURLConnection = (HttpURLConnection)new URL(url).openConnection();
		httpURLConnection.setConnectTimeout(connectionTimeOut);
		httpURLConnection.setReadTimeout(soTimeOut);
		httpURLConnection.setUseCaches(false);
		
		
		if(HttpMethod.POST ==httpMethod){
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST");
			LogUtils.v("POST:"+ url);
			if(paras!=null)
				httpURLConnection.getOutputStream().write(paras.getBytes());
		}else{
			httpURLConnection.setRequestMethod("GET");
			LogUtils.v("GET:"+ url);
		}
		
		return httpURLConnection;
	}

	@Override
	public void asyncConnect(String url, HttpCallBack<String> httpCallBack) {
		asyncConnect(url, null, httpCallBack);
	}

	@Override
	public void asyncConnect(String url, Map<String, String> params,
			HttpCallBack<String> httpCallBack) {
		asyncConnect(url, params, HttpMethod.GET, httpCallBack);
		
	}

	@Override
	public void asyncConnect(final String url,final Map<String, String> params,
			final HttpMethod httpMethod,final HttpCallBack<String> httpCallBack) {
		asyncThread(new Runnable() {
			@Override
			public void run() {
				syncConnect(url, params, httpMethod, httpCallBack);
			}
		});
		
	}

	@Override
	public String syncConnect(String url) {
		return syncConnect(url, null);
	}

	@Override
	public String syncConnect(String url, Map<String, String> params) {
		return syncConnect(url, params, HttpMethod.GET);
	}

	@Override
	public String syncConnect(String url, Map<String, String> params,
			HttpMethod httpMethod) {
		return syncConnect(url, params, httpMethod, null);
	}

	@Override
	public String syncConnect(String url, Map<String, String> params,
			HttpCallBack<String> httpCallBack) {
		return syncConnect(url, params, HttpMethod.GET, httpCallBack);
	}

	@Override
	public String syncConnect(String url, Map<String, String> params,
			HttpMethod httpMethod, HttpCallBack<String> httpCallBack) {
		
		if(TextUtils.isEmpty(url)){
			return null;
		}
		
		BufferedReader reader = null;
		
		HttpURLConnection httpURLConnection = null;
		
		int statusCode = -1;
		try {
			LogUtils.v(url);
			
			if(httpCallBack!=null){
				httpCallBack.onStart(url);
			}
			httpURLConnection = getHttpURLConnection(url,httpMethod,params);
			httpURLConnection.connect();
			statusCode = httpURLConnection.getResponseCode();
			if(statusCode==HttpURLConnection.HTTP_OK){
				
				reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
				
				StringBuffer buffer = new StringBuffer();
				String line = null;
				
				long progress = 0;
				
				long count = httpURLConnection.getContentLength();
				isCancel = false;
				if(httpCallBack != null && count!=-1)
					httpCallBack.onLoading(progress, count);
				while ((!isCancel) && (line = reader.readLine())!=null) {
					buffer.append(line);
					
					if(httpCallBack != null && count!=-1){
						progress+= line.getBytes().length;
						httpCallBack.onLoading(progress, count);
					}
				}
				
				
				if(httpCallBack != null){
					if(!isCancel){
						progress = count;
						httpCallBack.onLoading(progress, count);
					}else{
						reader.close();
						httpCallBack.onCancel();
						return null;
					}
				}
				reader.close();
				if(httpCallBack != null && !isCancel)
					httpCallBack.onSuccess(buffer.toString());
				
				if(httpURLConnection!=null)
					httpURLConnection.disconnect();
				
				return buffer.toString();
			}else{
				if(httpCallBack != null)
					httpCallBack.onFailure(String.valueOf(statusCode), null);
			}
		} catch (MalformedURLException e) {
			LogUtils.e(e);
			if(httpCallBack != null)
				httpCallBack.onFailure(String.valueOf(statusCode), e);
		} catch (IOException e) {
			LogUtils.e(e);
			if(httpCallBack != null)
				httpCallBack.onFailure(String.valueOf(statusCode), e);
		} catch (Exception e) {
			LogUtils.e(e);
			if(httpCallBack != null)
				httpCallBack.onFailure(String.valueOf(statusCode), e);
		}finally{
			
			if(httpURLConnection!=null){
				httpURLConnection.disconnect();
			}
		}
		
		return null;
	}
	

	@Override
	public void asyncDownloadFile(final String url,final String fileName,
			final HttpCallBack<File> httpDownloadCallBack) {
		asyncThread(new Runnable() {
			@Override
			public void run() {
				syncDownloadFile(url,fileName,httpDownloadCallBack);
			}
		});
	}

	@Override
	public File syncDownloadFile(String url, String fileName) {
		return syncDownloadFile(url, fileName, null);
	}

	@Override
	public File syncDownloadFile(String url, String fileName,
			HttpCallBack<File> httpDownloadCallBack) {
		
		if(TextUtils.isEmpty(url)){
			return null;
		}
		
		File file = null;
		
		BufferedInputStream bis = null;
		
		FileOutputStream fos = null;
		
		HttpURLConnection httpURLConnection = null;
		
		int statusCode = -1;
		try {
			LogUtils.v(url);
			
			if(TextUtils.isEmpty(fileName)){
				return null;
			}
			
			if(httpDownloadCallBack!=null)
				httpDownloadCallBack.onStart(url);
			
			httpURLConnection = getHttpURLConnection(url,HttpMethod.GET,null);
			httpURLConnection.connect();
			statusCode = httpURLConnection.getResponseCode();
			if(statusCode == HttpURLConnection.HTTP_OK){

				file = new File(fileName);
				fos = new FileOutputStream(file);
				
				long progress = 0;
				
				long count = httpURLConnection.getContentLength();
				
				bis = new BufferedInputStream(httpURLConnection.getInputStream());
				
				isCancel = false;
				byte[] buffer = new byte[DEFAULT_BYTE_LENGTH];
				int len = 0;
				if(httpDownloadCallBack!=null && count!=-1)
					httpDownloadCallBack.onLoading(progress, count);
				long time = System.currentTimeMillis();
				while((!isCancel) && (len = bis.read(buffer))!=-1){
					fos.write(buffer, 0, len);
					long temp = System.currentTimeMillis();
					if(temp-time>=1000){
						time = temp;
						if(httpDownloadCallBack!=null && count!=-1){
							progress += len;
							httpDownloadCallBack.onLoading(progress, count);
						}
					}
				}
				
				if(httpDownloadCallBack!=null ){
					if(!isCancel){
						progress = count;
						httpDownloadCallBack.onLoading(progress, count);
					}else{
						bis.close();
						fos.close();
						httpDownloadCallBack.onCancel();
						
						if(httpURLConnection!=null)
							httpURLConnection.disconnect();
						
						return file;
					}
				}
				
				bis.close();
				fos.close();
				
				if(httpDownloadCallBack!=null && !isCancel)
					httpDownloadCallBack.onSuccess(file);;
				
			}else{
				if(httpDownloadCallBack!=null)
					httpDownloadCallBack.onFailure(String.valueOf(statusCode), null);
			}
			
		} catch (ClientProtocolException e) {
			LogUtils.e(e);
			if(httpDownloadCallBack!=null)
				httpDownloadCallBack.onFailure(String.valueOf(statusCode), e);
		} catch (IOException e) {
			LogUtils.e(e);
			if(httpDownloadCallBack!=null)
				httpDownloadCallBack.onFailure(String.valueOf(statusCode), e);
		}catch (Throwable e) {
			LogUtils.e(e);
			if(httpDownloadCallBack!=null)
				httpDownloadCallBack.onFailure(String.valueOf(statusCode), e);
		}finally{
			if(httpURLConnection!=null)
				httpURLConnection.disconnect();
		}
		
		return file;
	}
	
	
	
	public static class KingAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>{

		
		public KingAsyncTask(){
			
		}		
		@Override
		protected Result doInBackground(Params... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Result result) {
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Progress... values) {
			super.onProgressUpdate(values);
			
		}
		
		
		
	}

}
