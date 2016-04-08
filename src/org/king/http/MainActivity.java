package org.king.http;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.king.BaseActivity;
import org.king.db.DataConentProvider;
import org.king.http.AbstractHttp.HttpMethod;
import org.king.inject.ViewInject;
import org.king.utils.LogUtils;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends BaseActivity {
	
	View rootView;
	
	private Button btn_share;
	
	private Button btn_print_screen;
	
	private Button btn_download;
	
	private TextView tv;
	
	boolean isBool;
	
	private ImageView iv_img;
	private CheckBox cb;

	Notification notification = null;
	
	NotificationManager nm;
	
	int i = 0;
	
    SimpleDateFormat sdf = new SimpleDateFormat("hh:MM");
    
    
   
    @ViewInject(id=R.id.btn_search,click="OnClick") Button btn_search;
	Button btn_click;
	Button btn_db;
	
	Button btn_find;
	
	GestureDetector gestureDetector;
	
	String url = "http://bbs.suizhou.com/suizhoudzapi/tagsapi/api.php?mod=news&ac=toutiao&pageno=1&recommend=0&pagesize=10";
	
	public void onV(View v){
		
		Toast.makeText(this, "追截", Toast.LENGTH_SHORT).show();
//		showToast("注解");
	}
	
	private static boolean invokeMethod(Object handler, String methodName,Object... obj1){
		if(handler == null) return false;
		java.lang.reflect.Method method = null;
		try{   
			//public boolean onLongClick(View v)
			method = handler.getClass().getDeclaredMethod(methodName, new Class[]{View.class});
			if(method!=null){
				Object obj = method.invoke(handler, obj1);
				return obj==null?false:Boolean.valueOf(obj.toString());	
			}
			else
				throw new RuntimeException("no such method:"+methodName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	
	public void onClick(View v) {
		showToast("点击");
//		test(url);
		int id = v.getId();
		if(id == R.id.btn_click){
			
			
			showToast("注解点击事件");
		}else if(id == R.id.btn_db){
//			showToast("测试数据");
//			delete();
//			query();
		}else if(id == R.id.btn_search){
			LogUtils.d("btn_search");
			startActivity(new Intent(context,SlideActivity.class));
		}
	}
	
	public Bitmap createVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
//            retriever.setMode(MediaMetadataRetriever.MODE_CAPTURE_FRAME_ONLY);
            if(Build.VERSION.SDK_INT >= 14){
            	retriever.setDataSource(filePath,new HashMap<String,String>());
            }else{
            	retriever.setDataSource(filePath);
            }
            bitmap = retriever.getFrameAtTime();
        } catch(IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        	LogUtils.e(ex);
        } catch (RuntimeException ex) {
        	LogUtils.e(ex);
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            	LogUtils.e(ex);
                // Ignore failures while cleaning up.
            }
        }
        if(bitmap==null){
        	System.out.println("bitmap==null");
        }
        return bitmap;
    }
	
	@Override
	protected boolean longClick(View v) {
		
		int id = v.getId();
		if(id== R.id.btn_click){
			showToast("注解长按事件");
			
		}
//		switch (v.getId()) {
//		
//		case R.id.btn_click:
//			break;
//			
//
//		default:
//			break;
//		}
		
		return super.longClick(v);
	}
	
	@Override
	protected boolean touch(View v, MotionEvent event) {
		
		showToast("注解触摸监听");
		
		return super.touch(v, event);
	}
	
	
	public void test(String url){
		AbstractHttp http = new Http();
		http.asyncConnect(url, new HttpCallBack<String>() {
			
			@Override
			public void onSuccess(String t) {
				System.out.println(t);
			}
			
			@Override
			public void onStart(String url) {
				
			}
			
			@Override
			public void onLoading(long progress, long count) {
			}
			
			@Override
			public void onFailure(String error, Throwable e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void insert(){
		ContentValues values = new ContentValues();
		
		values.put("test_title", "测试");
		values.put("test_date", "2015-3-5");
		values.put("test_content", "测试内容-为何");
		getContentResolver().insert(DataConentProvider.URI_TEST, values);
	}
	
	public void update(){
		ContentValues values = new ContentValues();
		
		values.put("test_title", "测试修改");
		values.put("test_date", "2015-3-6");
		values.put("test_content", "测试内容-修改");
		getContentResolver().update(DataConentProvider.URI_TEST, values,"_id=?",new String[]{"1"});
	}
	
	public void query(){
		
		Cursor cursor = getContentResolver().query(DataConentProvider.URI_TEST, null, null, null, null);
		
		if(cursor!=null){
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			while(cursor.moveToNext()){
				Map<String,String> map = new HashMap<String,String>();
				map.put("_id",getCursorString(cursor,"_id"));
				map.put("test_title",getCursorString(cursor,"test_title"));
				map.put("test_date",getCursorString(cursor,"test_date"));
				map.put("test_content",getCursorString(cursor,"test_content"));
				LogUtils.println(map);
				list.add(map);
			}
			
		}
	}
	public void delete(){
		getContentResolver().delete(DataConentProvider.URI_TEST,"test_date=?",new String[]{"2015-3-5"});
	}
	
	public String getCursorString(Cursor cursor,String columnName){
		return cursor.getString(cursor.getColumnIndex(columnName));
	}
	
	
	public void init(){
		
		
		OnItemSelectedListener s = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		};
		
		nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		
        long when = System.currentTimeMillis();  
        
        notification = new Notification(R.drawable.ic_launcher, "下载", when);  
        
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        String time = sdf.format(new Date(when));
        
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);  
        contentView.setImageViewResource(R.id.iv_img, R.drawable.ic_launcher);  
        contentView.setTextViewText(R.id.tv_title, "uploading");  
        contentView.setTextViewText(R.id.tv_time, time);  
        contentView.setProgressBar(R.id.pb, 100, 0, true);
        notification.contentView = contentView;  
         
        String ns = Context.NOTIFICATION_SERVICE;  
        nm.notify(1, notification);  
	}
	
	private Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case 0:
				init();
				break;
			case 1:
				int progress = msg.arg1;
				int count = msg.arg2;
				String pct = (int)((float)progress/count*100.0f) + "%";
				System.out.println(pct);
				notification.contentView.setTextViewText(R.id.tv_pct, pct); 
				notification.contentView.setTextViewText(R.id.tv_progress, progress+ "/" + count); 
				notification.contentView.setProgressBar(R.id.pb, msg.arg2, msg.arg1, true);
				nm.notify(1, notification);  
				break;
			case 2:
				File file = (File)msg.obj;
				LogUtils.i("下载完成！");
				
				notification.contentView.setTextViewText(R.id.tv_pct, "下载完成！"); 
				
				Intent intent = new Intent(Intent.ACTION_VIEW);
				
				Uri uriData = Uri.fromFile(file);
				String type = "application/vnd.android.package-archive";
				intent.setDataAndType(uriData, type);
				
		        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);  
		        notification.contentIntent = contentIntent;  
		        nm.notify(1, notification);
				break;
			case 3:
				
				break;

			default:
				break;
			}
		};
	};
	
	public void download(){
		
		if(notification!=null){
			
			return;
		}
		handler.sendEmptyMessage(0);
		
		
		AbstractHttp httpService = new Http();
//		String url = "http://192.168.0.37/yshow/interface/searchRoomList.php";
		String url = "http://gdown.baidu.com/data/wisegame/a8e7b016fc7fabd5/yijianjieyasuo_1.apk";
//		String url = "http://192.168.0.37/4hii/download/Vshow1.4.6.apk?referrer=refcode=Dhuythansau30";
		String filename = "/sdcard/123.apk";
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("curPage", "2");
		map.put("pageSize", "2");
		
		MultipartEntity me = new MultipartEntity();
		
		me.addPart("curPage", "2");
		me.addPart("pageSize", "2");
		
//		httpService.asyncConnect(url,HttpEntityUtils.toHttpEntity(map),HttpMethod.POST, new HttpConnectCallBack() {
//			httpService.asyncConnect(url,me,HttpMethod.POST, new HttpConnectCallBack() {
//			httpService.asyncConnect(url, null,HttpMethod.ETg, new HttpConnectCallBack() {
			httpService.asyncDownloadFile(url,filename,new HttpDownloadCallBack(){

				@Override
				public void onStart(String url) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onLoading(long progress, long count) {
					LogUtils.i(progress+ "/" + count);
					
//					i++;
//					if(progress<count){
//						
//						if(i%100 == 0){
//							handler.obtainMessage(1, (int)progress, (int)count).sendToTarget();
//							i = 0;
//						}
//					}else{
						handler.obtainMessage(1, (int)progress, (int)count).sendToTarget();
//					}
				}

				@Override
				public void onFailure(String error, Throwable e) {
					// TODO Auto-generated method stub
					handler.sendEmptyMessage(3);
				}

				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(File file) {
					// TODO Auto-generated method stub
					handler.obtainMessage(2, file).sendToTarget();
				}
				
			});
	}
	
	public void testURLEncoded(){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for(int i=0;i<3;i++){
			NameValuePair nameValuePair = new BasicNameValuePair("k"+i, "v"+i);
			list.add(nameValuePair);
		}
		 System.out.println(URLEncodedUtils.format(list,"UTF-8"));
	}
	
	
	public void test(View v){
		System.out.println("反射");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_main);
		String url = "http://idnew.halloshow.com/yshow/xml/config.php";
		test(url);
		
		LogUtils.v("test");
		try{
			Integer.parseInt("fda");
		}catch(Exception e){
			LogUtils.e("tt",e);
			LogUtils.e(e);
		}
		
//		btn_click = findView(btn_click, R.id.btn_click);
//		btn_db = findView(btn_click, R.id.btn_db);
//		btn_click.setText("测试哦");
//		
//		
//		
//		gestureDetector = new GestureDetector(this,new SimpleOnGestureListener(){
//
//			@Override
//			public boolean onScroll(MotionEvent e1, MotionEvent e2,
//					float distanceX, float distanceY) {
//				showToast("x:"+distanceX + "---y:"+distanceY);
//				if(Math.abs(distanceX)>Math.abs(distanceY) && distanceX<-100){
//					finish();
//				}
//				System.out.println("onScroll");
//				
//				return super.onScroll(e1, e2, distanceX, distanceY);
//			} 
//
//			@Override
//			public boolean onSingleTapUp(MotionEvent e) {
//				System.out.println("onSingleTapUp");
//				return super.onSingleTapUp(e);
//			}
//
//			@Override
//			public void onLongPress(MotionEvent e) {
//				System.out.println("onLongPress");
//				super.onLongPress(e);
//			}
//
//			@Override
//			public boolean onFling(MotionEvent e1, MotionEvent e2,
//					float velocityX, float velocityY) {
//				System.out.println("onFling");
//				return super.onFling(e1, e2, velocityX, velocityY);
//			}
//
//			@Override
//			public void onShowPress(MotionEvent e) {
//				System.out.println("onShowPress");
//				super.onShowPress(e);
//			}
//
//			@Override
//			public boolean onDown(MotionEvent e) {
//				System.out.println("onDown");
//				return super.onDown(e);
//			}
//
//			@Override
//			public boolean onDoubleTap(MotionEvent e) {
//				System.out.println("onDoubleTap");
//				return super.onDoubleTap(e);
//			}
//
//			
//			
//		});
//		
//		btn_find = findView(btn_find,R.id.btn_find);
////		btn_find.setText("有点叼");
//		try {
//			int a=3/0;
//		} catch (Exception e) {
//			i(e);
//		}
//		i("testLog");
//		invokeMethod(this, "test", btn_click);
//		
//		testURLEncoded();
//		btn_share = (Button)findViewById(R.id.btn_share);
//		btn_share.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
////				if(isInstall(MainActivity.this,""))
////				lineShare("text","测试文本");
////				smsShare("text","测试文本");
////				facebookShare("text","测试文本");
//				share();
////				facebookShare("text","测试文本");
//			}
//		});
//		
		btn_download = (Button)findViewById(R.id.btn_download);
		btn_download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				download();
			}
		});
//		
//  
//		
//		btn_print_screen = (Button)findViewById(R.id.btn_print_screen);
//		btn_print_screen.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(!isBool){
//					rootView = getWindow().getDecorView().getRootView();
//					rootView.setDrawingCacheEnabled(true); 
//					iv_img.setImageBitmap(rootView.getDrawingCache());
//				}
//			}
//		});
//		
//		tv = (TextView)findViewById(R.id.tv);
//		tv.setText(android.os.Build.MODEL);
//		
//		LogUtils.v("MODEL:" + android.os.Build.MODEL);
//		LogUtils.v("SDK:" + android.os.Build.VERSION.SDK);
//		LogUtils.v("SDK_INI:" + android.os.Build.VERSION.SDK_INT);
//		LogUtils.v("RELEASE:" + android.os.Build.VERSION.RELEASE);
//		LogUtils.v("CODENAME:" + android.os.Build.VERSION.CODENAME);
//		LogUtils.v("INCREMENTAL:" + android.os.Build.VERSION.INCREMENTAL);
//		LogUtils.v("ID:" + android.os.Build.ID);
//		LogUtils.v("TIME:" + android.os.Build.TIME);
//		LogUtils.v("DEVICE:" + android.os.Build.DEVICE);
//		LogUtils.v("PRODUCT:" + android.os.Build.PRODUCT);
//		LogUtils.v("DISPLAY:" + android.os.Build.DISPLAY);
//		LogUtils.v("TYPE:" + android.os.Build.TYPE);
//		LogUtils.v("getRadioVersion:" + android.os.Build.getRadioVersion());
//		
//		
		iv_img = (ImageView)findViewById(R.id.iv_img1);
		iv_img.setImageResource(R.drawable.ic_launcher);;
		iv_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					@Override
					public void run() {
						String url = "http://www.hallostar.in.th/mirror/video/2015/12/02/210085_20151202092851.mp4";
						System.out.println(url);
						Bitmap bmp = createVideoThumbnail(url);
						setImage(bmp);
					}
				}).start();
			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = "http://www.hallostar.in.th/mirror/video/2015/12/02/210085_20151202092851.mp4";
				System.out.println(url);
				Bitmap bmp = createVideoThumbnail(url);
				setImage(bmp);
			}
		}).start();
//		
//		FileEntity fe= new FileEntity(new File(""), "");
		
	}
	
	public void setImage(final Bitmap bitmap){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(bitmap!=null){
					showToast("youtu");
					iv_img.setImageBitmap(bitmap);				
					
				}
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		return gestureDetector.onTouchEvent(event);
	}
	
	public boolean isInstall(Context context,String packageName){
		boolean isInstall = false;
		try {
			PackageInfo localPackageInfo = context.getPackageManager()
					.getPackageInfo("jp.naver.line.android", 0);
			return (localPackageInfo != null) ? true: false;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void lineShare(String type,String content){
		Intent localIntent = new Intent(Intent.ACTION_VIEW);
		String str = "line://msg/" + type + "/" + content;
		localIntent.setData(Uri.parse(str));
		localIntent.putExtra(Intent.EXTRA_TEXT, "分享内容Text");
		localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(localIntent);
	}
	private void smsShare(String type,String content){
		Intent localIntent = new Intent();
//		Intent localIntent = new Intent("android.intent.action.VIEW");
//		String str = "sms://msg/" + type + "/" + content;
		localIntent.setAction(Intent.ACTION_SENDTO);
		localIntent.setData(Uri.parse("sms://我勒个擦"));
		localIntent.putExtra(Intent.EXTRA_TEXT, "分享内容");
		localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(localIntent);
	}
	private void facebookShare(String type,String content){
		Intent localIntent = new Intent(); 
//		Intent localIntent = new Intent("android.intent.action.VIEW");
//		String str = "sms://msg/" + type + "/" + content;
		localIntent.setAction(Intent.ACTION_VIEW);
		localIntent.setData(Uri.parse("fb://profile/fasgd"));
		localIntent.putExtra(Intent.EXTRA_TEXT, "分享内容");
		localIntent.putExtra("link", "http://hao123.com");
		localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(localIntent);
	}
	
	private void share() {
		Intent intent=new Intent(Intent.ACTION_SEND);  
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");  
        intent.putExtra(Intent.EXTRA_TEXT, "分享内容");
        intent.setType("*/*");
        File file = new File("/mnt/sdcard/1.png"); 
        
        String url = "http://hao123.com";
        
        Uri uri = Uri.fromFile(file);
        intent.putExtra(Intent.EXTRA_ALARM_COUNT, uri);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        startActivity(Intent.createChooser(intent, getTitle()));  

        
	}
	
	
	public void testFackbook(){
		
		
		String url = "http://192.168.0.37/4hii/yshow/interface/login.php";
//		String url = "http://192.168.100.97/yshow/interface/login.php";
		
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("loginName", "gja108108@qq.com");
		map.put("password", "CAAEdQ61f7I8BADSwg7s3vXEs7JNrLYARC1tYvC7XboDEZBhf3HjlpuQSCIMcUseqKdHIe14h69CyaEI2vZAw1jQtpIGQxMV0gXhZBya7W497WocvghS3uzk6QiXSV7bHd1jpX4WU0shpt6gB5JqUTNREGcNVZCJYsYNKmz0j9BiY34ENWCupGscaxbneQzKltQW3L5oYvt4DAZB409ulK");
		map.put("loginType", "1");
		map.put("ip", "127.0.0.1");
		
		
//		{password=CAAEdQ61f7I8BAGeJx3OlL0tQPR2RTh0jHWHPvJV7WjAvQPIhTPvZA0LMUtYaWqlhZBDjXI8B9mykpOcbzZARZCrqPmGmKQat2ZBKXXzZBEDaPHdXAoM6CfZBBnXZBxZCWQC85NNKxRs0KrfM1gvBGc6Q5zQubQ8EymxQFZBf4CTDwIsbUZA26lgu5pgAIAEbZCvtwpDt2KOhMjca1QUVNorVZAZBSS, loginType=1, ip=127.0.0.1, loginName=gja108108@qq.com}
		
		
		AbstractHttp httpService = new KingHttp();
		httpService.asyncConnect(url, map, HttpMethod.POST, new HttpCallBack<String>() {
			
			@Override
			public void onSuccess(String t) {
				
				LogUtils.v(t);
			}
			
			@Override
			public void onStart(String url) {
			}
			
			@Override
			public void onLoading(long progress, long count) {
			}
			
			@Override
			public void onFailure(String error, Throwable e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onCancel() {
				
			}
		});
		
	}
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}


}
