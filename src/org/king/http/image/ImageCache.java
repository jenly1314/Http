package org.king.http.image;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.king.utils.FileUtils;
import org.king.utils.LogUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.util.LruCache;

public class ImageCache {
	
	private int maxSize = 4 * 1024 * 1024;
	
	private int timeout = 10000;
	
	private LruCache<String, Bitmap> lruCache;
	
	private ExecutorService imageThreadPool;
	
	private static ImageCache instace;
	
	private ImageCache(){
		lruCache = new LruCache<>(maxSize);
	}
	
	public static ImageCache getInstace(){
		if(instace == null){
			synchronized (ImageCache.class) {
				if(instace == null){
					instace = new ImageCache();
				}
			}
		}
		
		return instace;
	}
	
	public interface OnImageLoaderListener{
		void onImageLoader(String url,Bitmap bitmap);
	}
	
	private ExecutorService getThreadPool(){  
        if(imageThreadPool == null){  
            synchronized(ExecutorService.class){  
                if(imageThreadPool == null){  
                    //为了下载图片更加的流畅，我们用了3个线程来下载图片  
                    imageThreadPool = Executors.newFixedThreadPool(3);  
                }  
            }  
        }  
          
        return imageThreadPool;  
          
    }
    
    /**
     * 停止工作线程
     */
    public void cancelTask(){
    	if(imageThreadPool!=null){
    		imageThreadPool.shutdown();	
    		imageThreadPool = null;
    	}
    }
	
	/**
	 * 加载
	 * @param url
	 * @param onImageLoaderListener
	 * @return
	 */
	public Bitmap loader(final String url,final OnImageLoaderListener onImageLoaderListener){
		
		Bitmap bitmap = getCacheBitmap(url);
		
		if(bitmap!=null){
			if(onImageLoaderListener!=null)
				onImageLoaderListener.onImageLoader(url, bitmap);
			return bitmap;
		}else{
			
			final Handler handler = new Handler(){
				public void handleMessage(android.os.Message msg) {
					if(onImageLoaderListener!=null)
						onImageLoaderListener.onImageLoader(url, (Bitmap)msg.obj);
				};
			};
			
			getThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					Bitmap bitmap = getNetworkBitmap(url);
					handler.obtainMessage(1, bitmap).sendToTarget();
					FileUtils.saveBitmap(url, bitmap);
					lruCache.put(url, bitmap);
					
				}
			});
			
		}
		
		
		return bitmap;
	}
	
	/**
	 * 获取缓存图片（内存缓存或存储卡缓存）
	 * @param url
	 * @return
	 */
	private Bitmap getCacheBitmap(String url){
		Bitmap bitmap = getMemoryBitmap(url);
		if(bitmap == null)//从存储卡去缓存图片
			bitmap = getDiscBitmap(url);
			
		return bitmap;
	}
	
	/**
	 * 获得存储卡的缓存图片
	 * @param url
	 * @return
	 */
	private Bitmap getDiscBitmap(String url){
		return FileUtils.getBitmap(url);
	}
	
	/**
	 * 获得内存缓存图片
	 * @param url
	 * @return
	 */
	private Bitmap getMemoryBitmap(String url){
		return lruCache.get(url);
	}
	
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getNetworkBitmap(String url){
		Bitmap bitmap = null;
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection)new URL(url).openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			bitmap = BitmapFactory.decodeStream(connection.getInputStream());
			
		} catch (Exception e){
			LogUtils.e(e);
		}finally{
			if(connection != null)
				connection.disconnect();
		}
		
		return bitmap;
	}

}
