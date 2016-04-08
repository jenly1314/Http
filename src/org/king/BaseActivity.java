package org.king;

import java.lang.reflect.Field;

import org.king.inject.EventListener;
import org.king.inject.ItemSelected;
import org.king.inject.ViewInject;
import org.king.utils.LogUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity{
	
	protected static final String COLON = ":";

	protected static final String ARROW = "->";
	
	protected static final String VERTICAL = "|";
	
	protected static final String ACTION_EXIT = "action_exit_jenly";
	
	protected static boolean isShowTitle;
	
	private StackTraceElement[] stacks = new Throwable().getStackTrace(); 
	
	protected Context context = this;
	
	private Toast toast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!isShowTitle){
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		registerExitReceiver();
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(exitReceiver);
		super.onDestroy();
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T findView(T t,int resId){
		return (T)findViewById(resId);
	}
	
	
	public void setView(int layoutResID) {
		super.setContentView(layoutResID);
		initInjectedView(this);
	}
	
	public void setView(View view) {
		super.setContentView(view);
		initInjectedView(this);
	}
	
	public void setView(View view, LayoutParams params) {
		super.setContentView(view, params);
		initInjectedView(this);
	}

	
	//--------------------------------------
	/**
	 * 获取资源文件
	 * @param resId
	 * @return
	 */
	protected String getResourcesString(int resId){
		return getResources().getString(resId);
	}
	protected Drawable getResourcesDrawable(int resId){
		return getResources().getDrawable(resId);
	}
	protected int getResourcesColor(int resId){
		return getResources().getColor(resId);
	}
	
	
	//--------------------------------------
	/**
	 * Toast弹出框
	 * @param msg
	 */
	protected void showToast(int resId){
		showToast(getResourcesString(resId),Toast.LENGTH_SHORT);
	}
	protected void showToast(String msg){
		showToast(msg,Toast.LENGTH_SHORT);
	}

	protected void showToast(String msg,int duration){
		showToast(context,msg,duration);
	}

	protected void showToast(Context context,String msg){
		showToast(context, msg, Toast.LENGTH_SHORT);;
	}

	protected void showToast(Context context,String msg,int duration){
		if(toast!=null){
			toast.cancel();			
		}
		toast = Toast.makeText(context, msg, duration);
		toast.show();
	}
	
	//--------------------------------------
	/**
	 * 注册退出广播
	 */
	protected void registerExitReceiver(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_EXIT);
		registerReceiver(exitReceiver, intentFilter);
	}
	
	/**
	 * 退出
	 */
	protected void exit(){
		Intent intent = new Intent(ACTION_EXIT);
		sendBroadcast(intent);
	}

	
	/**
	 * 退出广播
	 */
	private BroadcastReceiver exitReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
		
	};
	
	//--------------------------------------
	/**
	 * Click
	 * @param v
	 */
	protected void click(View v){
		
	}
	
	/**
	 * ItemClick
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	public void itemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}
	
	/**
	 * LongClick
	 * @param v
	 * @return
	 */
	protected boolean longClick(View v) {
		return false;
	}
	
	/**
	 * ItemLongClick
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 * @return
	 */
	protected boolean itemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		return false;
	}
	
	
	/**
	 * ItemSelected
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	protected void itemSelected(AdapterView<?> parent, View view,
			int position, long id) {
		
	}
	/**
	 * NoSelected
	 * @param parent
	 */
	protected void noSelected(AdapterView<?> parent) {
		
	}
	
	/**
	 * Touch
	 * @param v
	 * @param event
	 * @return
	 */
	protected boolean touch(View v, MotionEvent event) {
		return false;
	}
	
	
	
	
	//--------------------------------------
	/**
	 * 初始化注解
	 * @param activity
	 */
	public static void initInjectedView(Activity activity){
		initInjectedView(activity, activity.getWindow().getDecorView());
	}
	
	
	public static void initInjectedView(Object injectedSource,View sourceView){
		Field[] fields = injectedSource.getClass().getDeclaredFields();
		if(fields!=null && fields.length>0){
			for(Field field : fields){
				try {
					field.setAccessible(true);
					
					if(field.get(injectedSource)!= null )
						continue;
				
					ViewInject viewInject = field.getAnnotation(ViewInject.class);
					if(viewInject!=null){
						
						int viewId = viewInject.id();
					    field.set(injectedSource,sourceView.findViewById(viewId));
					
					    setListener(injectedSource,field,viewInject.click(),Method.Click);
						setListener(injectedSource,field,viewInject.longClick(),Method.LongClick);
						setListener(injectedSource,field,viewInject.itemClick(),Method.ItemClick);
						setListener(injectedSource,field,viewInject.itemLongClick(),Method.ItemLongClick);
						setListener(injectedSource,field,viewInject.touch(),Method.Touch);
						
						ItemSelected select = viewInject.ItemSelected();
						if(!TextUtils.isEmpty(select.selected())){
							setViewSelectListener(injectedSource,field,select.selected(),select.noSelected());
						}
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private static void setViewSelectListener(Object injectedSource,Field field,String select,String noSelect)throws Exception{
		Object obj = field.get(injectedSource);
		if(obj instanceof View){
			((AbsListView)obj).setOnItemSelectedListener(new EventListener(injectedSource).select(select).noSelect(noSelect));
		}
	}
	
	
	private static void setListener(Object injectedSource,Field field,String methodName,Method method)throws Exception{
		if(TextUtils.isEmpty(methodName))
			return;
		
		Object obj = field.get(injectedSource);
		
		switch (method) {
			case Click:
				if(obj instanceof View){
					((View)obj).setOnClickListener(new EventListener(injectedSource).click(methodName));
				}
				break;
			case ItemClick:
				if(obj instanceof AbsListView){
					((AbsListView)obj).setOnItemClickListener(new EventListener(injectedSource).itemClick(methodName));
				}
				break;
			case LongClick:
				if(obj instanceof View){
					((View)obj).setOnLongClickListener(new EventListener(injectedSource).longClick(methodName));
				}
				break;
			case ItemLongClick:
				if(obj instanceof AbsListView){
					((AbsListView)obj).setOnItemLongClickListener(new EventListener(injectedSource).itemLongClick(methodName));
				}
				break;
			case Touch:
				if(obj instanceof View){
					((View)obj).setOnClickListener(new EventListener(injectedSource).onTouch(methodName));
				}
				break;
			default:
				break;
		}
	}
	
	public enum Method{
		Click,LongClick,ItemClick,ItemLongClick,Touch
	}

}
