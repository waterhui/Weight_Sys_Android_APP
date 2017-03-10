package com.example.fragmentcom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import lecho.lib.hellocharts.view.LineChartView;

public class MyLineChartView extends LineChartView {

	public MyLineChartView(Context context) {
		super(context);
	}

	public MyLineChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLineChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	@SuppressLint("ClickableViewAccessibility") @Override
	public boolean onTouchEvent(MotionEvent event) {
		 try {  
	            return super.onTouchEvent(event);  
	        } catch (IllegalArgumentException  e) {  
	            Log.e( "ImageOriginPager-error" , "IllegalArgumentException 错误被活捉了！");  
	            e.printStackTrace();  
	        }  
	        return false;  
	}
	
}
