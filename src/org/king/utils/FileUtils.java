package org.king.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

/**
 * 文件工具类
 * 
 * @author jenly
 * @date 2013-3-25
 * 
 *  <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
 *  <!-- 往SDCard读写数据权限 -->
 *  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 */
public class FileUtils {
	
	public static final String CLAZZ = "FileUtils";
	
	public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath()+File.separator;
	
	public static final String CACHE_DIR_NAME = ".cache";
	
	public enum FileType{
		TXT,DOC,Image, VIDEO, AUDIO, APK, OTHER
	}
	
	
	public static final int DEFAULT_BUFFER_SIZE = 1024 * 8;
	
    /** 
     * 获取储存Image的目录 
     * @return 
     */  
    public static String getStorageDirectory(){  
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?  
        		SDCARD_PATH + CACHE_DIR_NAME : File.separator + CACHE_DIR_NAME;  
    }  
	
	
	//=========================	创建文件
	/**
	 * 创建文件实例
	 * @param path
	 * @return
	 */
	public static File newFile(String path){

		return new File(path);
	}
	
	/**
	 * 创建新文件
	 * @param path
	 * @return
	 */
	public static File createNewFile(String path){
		
		String dirName = path.substring(0, path.lastIndexOf("/"));
		createNewDir(dirName);
		
		File file = new File(path);
		try {
			if(!file.exists()){
				file.createNewFile();
			}
		}  catch (IOException e) {
			LogUtils.e(CLAZZ,e);
		}
		return new File(path);
	}

	/**
	 * 创建新目录
	 * @param path
	 * @return
	 */
	public static File createNewDir(String path){
		File file = new File(path);
		try {
			if(!file.exists()){
				file.mkdirs();
			}
		} catch (Exception e) {
			LogUtils.e(CLAZZ,e);
		}
		return file;
	}
	
	public static File createNewDir(String dir,String name){
		File file = new File(dir,name);
		try {
			if(!file.exists()){
				file.mkdirs();
			}
		} catch (Exception e) {
			LogUtils.e(CLAZZ,e);
		}
		return file;
	}
	
	//=========================	判断文件是否存在
	/**
	 * 判断文件是否存在
	 * @param path
	 * @return
	 */
	public static boolean isFileExist(String path){
		if(newFile(path).exists()){
			return true;
		}
		
		return false;
		
	}
	
	//=========================	获取文件大小
	/**
	 * 获取文件大小
	 * PS:有单位（例如1KB)
	 * @param path
	 * @return
	 */
	public static String getFileSize(String path){
		return getFileSize(newFile(path));
	}
	//-------------------------------
	public static String getFileSize(File file){
		if(file.exists()){
			return getFileSize(file.length());
		}
		return "";
	}
	//-------------------------------
	public static String getFileSize(long fileSize){
		
		DecimalFormat df = new DecimalFormat("0.00");
		
		if(fileSize<1024){
			return fileSize + "B";
		}else if(fileSize<1024 * 1024){
			return df.format(fileSize/1024)+"KB";
		}else if(fileSize<1024 * 1024 * 1024){
			return df.format(fileSize/(1024*1024))+"MB";
		}else{
			return df.format(fileSize/(1024*1024*1024))+"GB";
		}
	}
	
	
	//=========================	获取文件MIME类型
	/**
	 * 根据文件名后缀获取文件MIME类型
	 * @param fileName 文件名
	 * @return
	 */
	private static String getMIMEType(String fileName){
		
		String mimeType = "/*";
		
		String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
		
		if("apk".equalsIgnoreCase(suffix)){
			mimeType = "application/vnd.android.package-archive";
		}else if("jpg".equalsIgnoreCase(suffix)||"jpeg".equalsIgnoreCase(suffix)||"png".equalsIgnoreCase(suffix)){
			mimeType = "image/*";
		}else if("doc".equalsIgnoreCase(suffix)||"docx".equalsIgnoreCase(suffix)){
			mimeType = "application/msword";
		}else if("xls".equalsIgnoreCase(suffix)||"xlsx".equalsIgnoreCase(suffix)){
			mimeType = "application/vnd.ms-excel";
		}else if("ppt".equalsIgnoreCase(suffix)||"pptx".equalsIgnoreCase(suffix)){
			mimeType = "application/vnd.ms-powerpoint";
		}else if("pdf".equalsIgnoreCase(suffix)){
			mimeType = "application/pdf";
		}else if("txt".equalsIgnoreCase(suffix)){
			mimeType = "text/plain";
		}else if("htm".equalsIgnoreCase(suffix)||"html".equalsIgnoreCase(suffix)){
			mimeType = "text/html";
		}else if("mp3".equalsIgnoreCase(suffix)||"wav".equalsIgnoreCase(suffix)||"wma".equalsIgnoreCase(suffix)||"wav".equalsIgnoreCase(suffix)||"ogg".equalsIgnoreCase(suffix)||"ape".equalsIgnoreCase(suffix)){
			mimeType = "audio/*";
		}else if("mp4".equalsIgnoreCase(suffix)||"3gp".equalsIgnoreCase(suffix)||"rmvb".equalsIgnoreCase(suffix)||"avi".equalsIgnoreCase(suffix)){
			mimeType = "video/*";
		}else if("chm".equalsIgnoreCase(suffix)){
			mimeType = "application/x-chm";
		}
		
		return mimeType;
	}
	
	//-------------------------------
	private static String getMIMEType(File file){
		
		return getMIMEType(file.getName());
	}
	
	
	
	//=========================	打开文件
	/**
	 * 打开文件
	 * PS:会根据文件后缀自动辨别文件格式并打开
	 * @param context 上下文
	 * @param path 文件的绝对路径
	 */
	public static void openFile(Context context,String path){
		openFile(context,newFile(path));
	}
	
	//-------------------------------
	public static void openFile(Context context,File file){
		
		if(file.exists()){
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(Intent.ACTION_VIEW);
			
			String type = getMIMEType(file);
			intent.setDataAndType(Uri.fromFile(file), type);
			context.startActivity(intent);
		}
	}
	
	
	//=========================	将流写入文件
	/**
	 * 将流写入到文件
	 * @param path 文件存放的路径
	 * @param input 要写入的流
	 * @return 返回写入的文件
	 * @throws IOException
	 */
	public static File writeFileByImputStream(String path,InputStream input)throws IOException{
		
		File file = null;
		
		OutputStream output = null;
		try{
			file = newFile(path);
			output = new FileOutputStream(file); 
			
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			
			while((input.read(buffer))!=-1){
				output.write(buffer);
			}
			output.flush();
			
		}catch(IOException e){
			throw e;
		}finally{
			try{
				if(output!=null){
					output.close();
				}
			}catch (IOException e) {
				output = null;
			}
		}
		return file;
	}
	
	/**
	 * 文件转字节数组
	 * @param file
	 * @return
	 */
	public static byte[] fileToBytes(File file){
		
		byte[] bytes = null;
		
		InputStream is = null;
		try {
			 is = new FileInputStream(file);
			 bytes = new byte[is.available()];
			 is.read(bytes);
			 
		} catch (Exception e) {
			LogUtils.e(CLAZZ,e);
		}finally{
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					LogUtils.e(CLAZZ,e);
				}
			}
		}
		
		return bytes;
		
	}
	
	/**
	 * 保存图片
	 * @param url
	 * @param bitmap
	 */
	public static void saveBitmap(String url,Bitmap bitmap){
		if(TextUtils.isEmpty(url) || bitmap==null)
			return;
		String path = getStorageDirectory();
		createNewDir(path);
        try {
        	File file = new File(path + File.separator + DigestUtils.md5(url));  
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);  
			bitmap.compress(CompressFormat.JPEG, 100, fos);  
			fos.flush();  
			fos.close(); 
		} catch (IOException e) {
			LogUtils.e(e);
		}  
		
	}
	
	/**
	 * 获取缓存的图片
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmap(String url){
		if(TextUtils.isEmpty(url))
			return null;
		Bitmap bitmap = null;
		try{
			String filename = getStorageDirectory() + File.separator + DigestUtils.md5(url);
			bitmap = BitmapFactory.decodeFile(filename);
		}catch(Exception e){
			LogUtils.e(e);
		}
		return bitmap;
	}

}
