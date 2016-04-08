
package org.king.inject;

import java.lang.reflect.Method;


import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class EventListener implements OnClickListener, OnLongClickListener, OnItemClickListener, OnItemSelectedListener,OnItemLongClickListener,OnTouchListener {

	private Object handler;
	
	private String clickMethod;
	private String longClickMethod;
	private String itemClickMethod;
	private String itemSelectMethod;
	private String nothingSelectedMethod;
	private String itemLongClickMehtod;
	private String touchMehtod;
	
	public EventListener(Object handler) {
		this.handler = handler;
	}
	
	public EventListener click(String method){
		this.clickMethod = method;
		return this;
	}
	
	public EventListener longClick(String method){
		this.longClickMethod = method;
		return this;
	}
	
	public EventListener itemLongClick(String method){
		this.itemLongClickMehtod = method;
		return this;
	}
	
	public EventListener itemClick(String method){
		this.itemClickMethod = method;
		return this;
	}
	
	public EventListener select(String method){
		this.itemSelectMethod = method;
		return this;
	}
	
	public EventListener noSelect(String method){
		this.nothingSelectedMethod = method;
		return this;
	}
	
	public EventListener onTouch(String method){
		this.touchMehtod = method;
		return this;
	}
	
	
	
	@Override
	public void onClick(View v) {
		invokeClickMethod(handler, clickMethod, v);
	}
	
	@Override
	public boolean onLongClick(View v) {
		return invokeLongClickMethod(handler,longClickMethod,v);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		invokeItemClickMethod(handler, itemClickMethod, parent,view,position,id);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		return invokeItemLongClickMethod(handler,itemLongClickMehtod,parent,view,position,id);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		
		invokeItemSelectMethod(handler, itemSelectMethod, parent,view,position,id);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		invokeNoSelectMethod(handler, nothingSelectedMethod, parent);
	}


	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return invokeTouchMethod(handler, touchMehtod,v,event);
	}
	
	
	//------------------------------
	
	
	
	private static void invokeClickMethod(Object handler, String methodName,View v){
		if(handler == null) return;
		Method method = null;
		try{  
			//public void onClick(View v)
			method = handler.getClass().getDeclaredMethod(methodName,View.class);
			if(method!=null)
				method.invoke(handler, v);	
			else
				throw new RuntimeException("no such method:"+methodName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private static boolean invokeLongClickMethod(Object handler, String methodName,View v){
		if(handler == null) return false;
		Method method = null;
		try{   
			//public boolean onLongClick(View v)
			method = handler.getClass().getDeclaredMethod(methodName,View.class);
			if(method!=null){
				Object obj = method.invoke(handler,v);
				return obj==null?false:Boolean.valueOf(obj.toString());	
			}
			else
				throw new RuntimeException("no such method:"+methodName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	
	private static void invokeItemClickMethod(Object handler, String methodName,AdapterView<?> parent, View view, int position,
			long id){
		if(handler == null) return;
		Method method = null;
		try{   
			//onItemClick(AdapterView<?> parent, View view, int position,long id)
			method = handler.getClass().getDeclaredMethod(methodName,AdapterView.class,View.class,int.class,long.class);
			if(method!=null)
				method.invoke(handler, parent,view,position,id);	
			else
				throw new RuntimeException("no such method:"+methodName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	private static boolean invokeItemLongClickMethod(Object handler, String methodName,AdapterView<?> parent, View view,int position, long id){
		if(handler == null) throw new RuntimeException("invokeItemLongClickMethod: handler is null");
		Method method = null;
		try{   
			//public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id)
			method = handler.getClass().getDeclaredMethod(methodName,AdapterView.class,View.class,int.class,long.class);
			if(method!=null){
				Object obj = method.invoke(handler,parent,view,position,id);
				return Boolean.valueOf(obj==null?false:Boolean.valueOf(obj.toString()));	
			}
			else
				throw new RuntimeException("no such method:"+methodName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	private static void invokeItemSelectMethod(Object handler, String methodName,AdapterView<?> parent, View view,int position, long id){
		if(handler == null) return;
		Method method = null;
		try{   
			//onItemSelected(AdapterView<?> parent, View view,int position, long id)
			method = handler.getClass().getDeclaredMethod(methodName,AdapterView.class,View.class,int.class,long.class);
			if(method!=null)
				method.invoke(handler, parent,view,position,id);	
			else
				throw new RuntimeException("no such method:"+methodName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private static void invokeNoSelectMethod(Object handler, String methodName,AdapterView<?> parent){
		if(handler == null) return;
		Method method = null;
		try{   
			//onNothingSelected(AdapterView<?> parent)
			method = handler.getClass().getDeclaredMethod(methodName,AdapterView.class);
			if(method!=null)
				method.invoke(handler, parent);	
			else
				throw new RuntimeException("no such method:"+methodName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private static boolean invokeTouchMethod(Object handler, String methodName,View v, MotionEvent event){
		if(handler == null) throw new RuntimeException("invokeTouchMethod: handler is null");
		Method method = null;
		try{   
			//onTouch(View v, MotionEvent event)
			method = handler.getClass().getDeclaredMethod(methodName,View.class,MotionEvent.class);
			if(method!=null){
				Object obj = method.invoke(handler,v,event);
				return Boolean.valueOf(obj==null?false:Boolean.valueOf(obj.toString()));
				
			}else
				throw new RuntimeException("no such method:"+methodName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}


	
	
}
