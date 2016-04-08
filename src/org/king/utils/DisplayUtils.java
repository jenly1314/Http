package org.king.utils;


import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

public class DisplayUtils {
	private static final String TAG = DisplayUtils.class.getSimpleName();
	/**
	 * dip转px
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue){            
		final float scale = context.getResources().getDisplayMetrics().density;                 
		return (int)(dipValue * scale + 0.5f);         
	}     
	
	/**
	 * px转dip
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue){                
		final float scale = context.getResources().getDisplayMetrics().density;                 
		return (int)(pxValue / scale + 0.5f);         
	} 
	
	/**
	 * 获取屏幕宽度和高度，单位为px
	 * @param context
	 * @return
	 */
	public static Point getScreenMetrics(Context context){
		DisplayMetrics dm =context.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		LogUtils.i("Screen—>Width = " + width + " Height = " + height + " densityDpi = " + dm.densityDpi);
		return new Point(width, height);
		
	}
	
	/**
	 * 获取屏幕长宽比
	 * @param context
	 * @return
	 */
	public static float getScreenRate(Context context){
		Point p = getScreenMetrics(context);
		float h = p.y;
		float w = p.x;
		return (h/w);
	}
}

