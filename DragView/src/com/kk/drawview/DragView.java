package com.kk.drawview;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification.Action;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AnalogClock;
import android.widget.RelativeLayout;
import android.widget.Toast;

 public class DragView extends RelativeLayout {
	private Context context;
	private WindowManager wm;
	private WindowManager.LayoutParams params;
	private int w,h;
	private AccuratePoint startPoint;
	private AccuratePoint endPoint;
	public DragView(Context context) {
		super(context);
		this.context = context;
		startPoint = new AccuratePoint();
		endPoint = new AccuratePoint();
		// TODO Auto-generated constructor stub
	}
	
	
	class AccuratePoint {
		float x;
		float y;
	}
	
	@SuppressLint("ClickableViewAccessibility") @Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			startPoint.x = event.getRawX();
			startPoint.y = event.getRawY();
		} else {
			move(event);
		}
		return super.onTouchEvent(event);
	}
	
	@Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return true;
    }
	
	//显示到phoneWindow
	public void show(int w, int h){
		wm = (WindowManager) context
                .getSystemService(Application.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.gravity = Gravity.LEFT | Gravity.TOP; 
        params.windowAnimations = android.R.style.Animation_Translucent;
        params.x = 0;
        params.y = 0;
        params.width = w;
        this.w = w;
        this.h = h;
        params.height = h;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;   
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wm.addView(this, params);
	}
	
	
	private void move(MotionEvent event){
		endPoint.x = event.getRawX();
		endPoint.y = event.getRawY();
		AccuratePoint end = getAccuratePoint(startPoint,endPoint);
		if(event.getAction() == MotionEvent.ACTION_MOVE){
			if(check(end)){
				updateViewLayout(end);
			}
		}
		if(event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP){
			animate(end);
		}
		startPoint.x = endPoint.x;
		startPoint.y = endPoint.y;
	}
	
	private AccuratePoint getAccuratePoint(AccuratePoint start, AccuratePoint end){
		AccuratePoint point = new AccuratePoint();
		point.x = end.x - (start.x - params.x);
		point.y = end.y - (start.y - params.y);
		return point;
	}
	
	private final Handler handler = new Handler();
	
	private void animate(final AccuratePoint end){
		final AccuratePoint point = new AccuratePoint();
		final int type;
		float sheight = wm.getDefaultDisplay().getHeight();
		float swidth = wm.getDefaultDisplay().getWidth();
		if(end.x + this.w/2 < swidth/2){
			if(end.y < end.x ) {
				point.y = 0;
				type = 0;
			}else if(sheight - end.y - this.h < end.x){
				point.y = sheight - this.h;
				type = 0;
			} else {
				point.x = 0;	
				type = 1;
			}
		}else {
			if(end.y < swidth- end.x - this.w){
				point.y = 0;
				type = 0;
			} else if(sheight - end.y - this.h < swidth- end.x - this.w){
				point.y = sheight - this.h;
				type = 0;
			}else {
				point.x = swidth - this.w;	
				type = 1;
			}
		}
		final float step ;
		 if(type == 1){
			 step = Math.abs(point.x - end.x) / 30;
		 } else {
			 step = Math.abs(point.y - end.y) / 30;
		 }
		 Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(type == 0){
					if(point.y == 0){
						end.y -= step;
					} else {
						end.y += step;
					}
					updateViewLayout(end);
					if(end.y <= 0 || end.y >= (wm.getDefaultDisplay().getHeight() - DragView.this.h)){
						return;
					}
				}
				if(type == 1){
					if(point.x == 0){
						end.x -= step;
					} else {
						end.x += step;
					}
					updateViewLayout(end);
					if(end.x <= 0 || end.x >= (wm.getDefaultDisplay().getWidth() - DragView.this.w)){
						return;
					}
				}
			    handler.postDelayed(this, 1);
			}
		};
	    handler.postDelayed(runnable, 1);
	}
	
	private void updateViewLayout(AccuratePoint point){
		 params.x = (int)point.x;
		 params.y =  (int)point.y;
		 wm.updateViewLayout(this, params);
	}
	
	@SuppressWarnings("deprecation")
	private boolean check(AccuratePoint point){
		float sheight = wm.getDefaultDisplay().getHeight();
		float swidth = wm.getDefaultDisplay().getWidth();
		if(point.x < 0 && -point.x >= w/2){
			return false;
		}
		if(point.x >= sheight - w/2){
			return false;
		}
		if(-point.y >= w/2){
			return false;
		}
		if(point.x >= swidth - w/2){
			return false;
		}
		return true;
	}
}
