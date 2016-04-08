package org.king.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.king.utils.LogUtils;

import android.text.TextUtils;

/**
 * Http网络通信(httpClient实现)
 * @author Jenly
 * @date 2014-8-7
 */
public class Http extends AbstractHttp{
	

	public Http() {
		super();
	}

	public Http(int connectionTimeOut, int soTimeOut) {
		super(connectionTimeOut, soTimeOut);
	}


	/**
	 * 获得一个HttpClient
	 * @return
	 */
	private HttpClient getHttpClient(){
		
		HttpParams httpParams = new BasicHttpParams();
		
		if(connectionTimeOut != LONG_TIME && soTimeOut != LONG_TIME){
			HttpConnectionParams.setConnectionTimeout(httpParams,connectionTimeOut);
			HttpConnectionParams.setSoTimeout(httpParams, soTimeOut);
		}
		
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams,DEFAULT_BYTE_LENGTH);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));
		
		ThreadSafeClientConnManager threadSafeClientConnManager = new ThreadSafeClientConnManager(
				httpParams, schemeRegistry);
		
		return new DefaultHttpClient(threadSafeClientConnManager,httpParams);
	}
	
	
	/**
	 * 获得HttpUriRequest
	 * @param url
	 * @param httpMethod
	 * @param params
	 * @return
	 */
	private HttpUriRequest getHttpUriRequest(String url,HttpMethod httpMethod,Map<String,String> params){
		
		if(HttpMethod.POST == httpMethod){
			
			HttpPost httpPost = new HttpPost(url);
			if(params!=null){
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				for(String key : params.keySet()){
					list.add(new BasicNameValuePair(key, params.get(key)));
				}
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(list,DEFAULT_ENCODING));
				} catch (UnsupportedEncodingException e) {
					LogUtils.e(e);
				}
			}
			return httpPost;
		}else{
			if(params!=null){
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				
				for(String key : params.keySet()){
					list.add(new BasicNameValuePair(key, params.get(key)));
				}
				url += URL_AND_PARA_SEPARATOR;
				
				url += URLEncodedUtils.format(list,DEFAULT_ENCODING);
			}
			
			LogUtils.v("GET:"+ url);
			HttpGet httpGet = new HttpGet(url);
			
			return httpGet;
		}
		
		
	}

	@Override
	public void asyncConnect(String url,HttpCallBack<String> httpCallBack) {
		asyncConnect(url, null, HttpMethod.GET, httpCallBack);
	}
	
	@Override
	public void asyncConnect(String url, Map<String,String> params,HttpCallBack<String> httpCallBack) {
		asyncConnect(url, params, HttpMethod.GET, httpCallBack);
	}
	
	@Override
	public void asyncConnect(final String url,final Map<String,String> params,final HttpMethod httpMethod,final HttpCallBack<String> httpCallBack) {
		asyncThread(new Runnable(){
			@Override
			public void run() {
				syncConnect(url, params,httpMethod, httpCallBack);
			}
		});
	}
	
	@Override
	public String syncConnect(String url) {
		return syncConnect(url, null, HttpMethod.GET, null);
	}
	
	@Override
	public String syncConnect(String url, Map<String,String> params) {
		return syncConnect(url, params, HttpMethod.GET, null);
	}
	
	@Override
	public String syncConnect(String url, Map<String,String> params,HttpCallBack<String> httpCallBack) {
		return syncConnect(url, params, HttpMethod.GET, httpCallBack);
	}
	
	@Override
	public String syncConnect(String url, Map<String,String> params,HttpMethod httpMethod) {
		return syncConnect(url, params,httpMethod, null);
	}

	@Override
	public String syncConnect(String url, Map<String,String> params,HttpMethod httpMethod,HttpCallBack<String> httpCallBack) {
		
		if(TextUtils.isEmpty(url)){
			return null;
		}
		
		BufferedReader reader = null;
		
		HttpClient httpClient = null;
		
		int statusCode = -1;
		try {
			LogUtils.v(url);
			
			if(httpCallBack != null)
				httpCallBack.onStart(url);
			
			HttpUriRequest request = getHttpUriRequest(url,httpMethod,params);
			httpClient = getHttpClient();
			HttpResponse httpResponse = httpClient.execute(request);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			if(statusCode==HttpStatus.SC_OK){
				
				reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				
				StringBuffer buffer = new StringBuffer();
				String line = null;
				
				long progress = 0;
				
				long count = httpResponse.getEntity().getContentLength();
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
				
				if(httpClient!=null)
					httpClient.getConnectionManager().shutdown();
				
				return buffer.toString();
			}else{
				if(httpCallBack != null)
					httpCallBack.onFailure(String.valueOf(statusCode), null);
			}
		} catch (ClientProtocolException e) {
			LogUtils.e(e);
			if(httpCallBack != null)
				httpCallBack.onFailure(String.valueOf(statusCode),e);
		} catch (IOException e) {
			LogUtils.e(e);
			if(httpCallBack != null)
				httpCallBack.onFailure(String.valueOf(statusCode),e);
		}catch (Exception e) {
			LogUtils.e(e);
			if(httpCallBack != null)
				httpCallBack.onFailure(String.valueOf(statusCode),e);
		}finally{
			if(httpClient!=null)
				httpClient.getConnectionManager().shutdown();
		}
		
		return null;
		
	}

	@Override
	public void asyncDownloadFile(final String url,final String fileName,final HttpCallBack<File> httpDownloadCallBack) {
		
		asyncThread(new Runnable() {
			@Override
			public void run() {
				syncDownloadFile(url,fileName,httpDownloadCallBack);
			}
		});
	}

	@Override
	public File syncDownloadFile(String url,String fileName) {
		return syncDownloadFile(url, fileName, null);
	}
	
	public File syncDownloadFile(String url,String fileName,HttpCallBack<File> httpDownloadCallBack) {
		
		
		if(TextUtils.isEmpty(url)){
			return null;
		}
		
		File file = null;
		
		BufferedInputStream bis = null;
		
		FileOutputStream fos = null;
		
		HttpClient httpClient = null;
		
		int statusCode = -1;
		try {
			LogUtils.v(url);
			
			if(TextUtils.isEmpty(fileName)){
				return null;
			}
			
			if(httpDownloadCallBack!=null)
				httpDownloadCallBack.onStart(url);
			
			HttpUriRequest httpUriRequest = getHttpUriRequest(url, HttpMethod.GET, null);
			httpClient = getHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpUriRequest);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK){

				file = new File(fileName);
				fos = new FileOutputStream(file);
				
				long progress = 0;
				
				long count = httpResponse.getEntity().getContentLength();
				
				bis = new BufferedInputStream(httpResponse.getEntity().getContent());
				
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
						
						if(httpClient!=null)
							httpClient.getConnectionManager().shutdown();
						
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
			e.printStackTrace();
		} catch (IOException e) {
			LogUtils.e(e);
			if(httpDownloadCallBack!=null)
				httpDownloadCallBack.onFailure(String.valueOf(statusCode), e);
			e.printStackTrace();
		}catch (Throwable e) {
			LogUtils.e(e);
			if(httpDownloadCallBack!=null)
				httpDownloadCallBack.onFailure(String.valueOf(statusCode), e);
		}finally{
			if(httpClient!=null)
				httpClient.getConnectionManager().shutdown();
		}
		
		return file;
	}

	
}
