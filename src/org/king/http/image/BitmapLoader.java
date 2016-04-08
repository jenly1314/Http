package org.king.http.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import org.king.utils.FileUtils;
import org.king.utils.LogUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * 图片异步加载并存储(双缓存)
 * @author Jenly
 * @date 2013-8-02
 */
public class BitmapLoader {
	
	public static final String DEFAULAT_CACHE_DIR = ".cache";
	/** 是否压缩图片 */
	public static boolean isCompressBitmap = false;
	
	public static final int REQUIRED_SIZE = 200;
	
	private static BitmapLoader sBtiBitmapLoader;
	
	private Map<ImageView,String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView,String>());
	
	//记忆缓存
	private HashMap<String, SoftReference<Bitmap>> memoryCache;
	
	//文件缓存
	private FileCache fileCache;
	
	
	//默认图片id
	private int defaultDrawableId;

	//-------------------------
	
	private ImageStack imageStack;
	
	private ImageLoaderThread imageLoaderThread;
	
	
	public static synchronized BitmapLoader getInstance(){
		
		if(sBtiBitmapLoader == null){
			sBtiBitmapLoader = new BitmapLoader(0);
		}
		
		return sBtiBitmapLoader;
	}
	
	
	public BitmapLoader(int defaultDrawableId){
		
		this.defaultDrawableId = defaultDrawableId;
		init(null);

	}
	
	public BitmapLoader(int defaultDrawableId,String pojectName){
		
		this.defaultDrawableId = defaultDrawableId;
		init(SDCARD_PATH + pojectName + File.separator + DEFAULAT_CACHE_DIR);
		
	}
	
	private void init(String dirName){
		memoryCache = new HashMap<String, SoftReference<Bitmap>>();
		
		fileCache = new FileCache(dirName);
		
		imageStack = new ImageStack();
		
		imageLoaderThread = new ImageLoaderThread();
		imageLoaderThread.setPriority(Thread.NORM_PRIORITY-1);
	}
	
	//----------------------------------------------
	/**
	 * 显示图片
	 * @param url
	 * @param imageView
	 */
	public void displayImage(String url,ImageView imageView){
		imageViews.put(imageView,url);
		
		Bitmap bitmap = null;
		if(memoryCache.containsKey(url)){
			bitmap = memoryCache.get(url).get();
		}
		
		if(bitmap!=null){
			imageView.setImageBitmap(bitmap);
		}else{
			//清扫之前存在的
			imageStack.clean(imageView);
			ImageInfo imageInfo = new ImageInfo(url, imageView);
			synchronized (imageStack.imageStacks) {
				//压入栈
				imageStack.imageStacks.push(imageInfo);
				imageStack.imageStacks.notifyAll();
			}
			//...
			imageView.setImageResource(defaultDrawableId);
			
			if(imageLoaderThread.getState() == Thread.State.NEW){
				imageLoaderThread.start();
			}
		}
		
	}
	
	/**
	 * 停止线程
	 */
	public void stopThread(){
		imageLoaderThread.interrupt();
	}
	
	/**
	 * 清除缓存
	 */
	public void clearCache(){
		memoryCache.clear();
		fileCache.clear();
	}
	
	
	//----------------------------------------------
	/**
	 * 图片加载线程
	 */
	class ImageLoaderThread extends Thread{
		
		
		@Override
		public void run() {
			try{
				while(true){
					if(imageStack.imageStacks.size()==0){
						synchronized (imageStack.imageStacks) {
							imageStack.imageStacks.wait();
						}
					}
					if(imageStack.imageStacks.size()!=0){
						ImageInfo imageInfo;
						synchronized (imageStack.imageStacks) {
							 imageInfo = imageStack.imageStacks.pop();
						}
						final Bitmap bitmap = downloadBitmap(imageInfo.url);
						memoryCache.put(imageInfo.url,new SoftReference<Bitmap>(bitmap));
						String url = imageViews.get(imageInfo.imageView);
						if(url!=null && url.equals(imageInfo.url)){
							
							Activity activity = (Activity)imageInfo.imageView.getContext();
							activity.runOnUiThread(new ImageViewRunnable(imageInfo.imageView,bitmap,defaultDrawableId));
						}
					}
					
					if(Thread.interrupted()){
						break;
					}
				}
			}catch(InterruptedException e){
				LogUtils.e(e);
			}
		}
		
		/**
		 * 下载图片
		 * @param url
		 * @return
		 */
		public Bitmap downloadBitmap(String url){
			//...
			
			File file = fileCache.getFile(url);
			
			Bitmap bitmap = decodeFile(file);
			
			if(bitmap!=null){
				return bitmap;
			}
			
			try{
				
				URL imageUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);
				
				InputStream is=conn.getInputStream();
				FileUtils.writeFileByImputStream(file.getAbsolutePath(),is);
				return decodeFile(file);
				
			}catch(Exception e){
				LogUtils.e(e);
			}
			
			return null;
			
		}
		
		
		private Bitmap decodeFile(File file){
			

			try{
				
				if(!isCompressBitmap)
					return BitmapFactory.decodeFile(file.getAbsolutePath());
				
				LogUtils.i(file.getPath());
				
				BitmapFactory.Options opt = new BitmapFactory.Options();
				
				opt.inJustDecodeBounds = true;
				
				 BitmapFactory.decodeStream(new FileInputStream(file),null,opt);
				
	            
	            int width_tmp=opt.outWidth, height_tmp=opt.outHeight;
	            int scale=1;
	            while(true){
	                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
	                    break;
	                width_tmp/=2;
	                height_tmp/=2;
	                scale*=2;
	            }
	            
	            BitmapFactory.Options opt2 = new BitmapFactory.Options();
	            opt2.inSampleSize=scale;
	            return BitmapFactory.decodeStream(new FileInputStream(file), null, opt2);
				
			}catch(FileNotFoundException e){
				LogUtils.e(e);
			}
			
			return null;
		}
		
		/**
		 * 图片显示
		 */
		class ImageViewRunnable implements Runnable{
			
			ImageView imageView;
			Bitmap bitmap;
			int defaultDrawable;
			
			public ImageViewRunnable(ImageView iv,Bitmap bmp,int defaultDrawableId){
				 this.imageView = iv;
				 this.bitmap = bmp;
				 this.defaultDrawable = defaultDrawableId;
			}
			
			public void run(){
				if(bitmap!=null){
					imageView.setImageBitmap(bitmap);
				}else{
					if(defaultDrawable!=0)
						imageView.setImageResource(defaultDrawable);
				}
			}
			
		}
		
	}
	
	
	//----------------------------------------------
	/**
	 * 图片栈
	 */
	class ImageStack{
		
		private Stack<ImageInfo> imageStacks = new Stack<ImageInfo>();
		
		private void clean(ImageView imageView){
			
			for(int i=0;i<imageStacks.size();){
				if(imageStacks.get(i).imageView==imageView){
					imageStacks.remove(i);
				}else{
					++i;
				}
			}
		}
		
		
	}
	
	/**
	 * 图片信息
	 */
	class ImageInfo{
		String url;
		ImageView imageView;
		
		public ImageInfo(String url,ImageView imageView){
			this.url = url;
			this.imageView = imageView;
		}
	}
	
	//----------------------------------------------
	/**
	 * 本地文件缓存
	 */
	class FileCache{
		
		File dirCache = null;
		
		public FileCache(){
			if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
				dirCache = FileUtils.createNewDir(SDCARD_PATH+DEFAULAT_CACHE_DIR);
				
			}
		}
		
		/**
		 * 构造
		 * @param path 本地缓存
		 */
		public FileCache(String dirName){
			if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){ 
				
				if(dirName!=null){
					dirCache = FileUtils.createNewDir(dirName);
				}else{
					dirCache = FileUtils.createNewDir(SDCARD_PATH+DEFAULAT_CACHE_DIR);
				}
				
			}
		}
		
		/**
		 * 获取缓存文件
		 * @param url
		 * @return
		 */
		public File getFile(String url){
			
			if(dirCache!=null){
				String fileName = String.valueOf(url.hashCode());
				return new File(dirCache,fileName);
			}
			
			return null;
		}
		
		/**
		 * 清空缓存文件
		 */
		public void clear(){
		  if(dirCache!=null){
			  File[] files = dirCache.listFiles();
			  
			  for(File file:files){
				  file.delete();
			  }
		  }
		}
		
		
	}
	
	//----------------------------------------------
	
	public static final String SDCARD_PATH = FileUtils.SDCARD_PATH;
	
}
