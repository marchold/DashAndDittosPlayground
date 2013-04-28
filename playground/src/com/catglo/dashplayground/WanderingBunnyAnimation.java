package com.catglo.dashplayground;

import java.util.ArrayList;

import com.catglo.dashplayground.BitmapCache.BunnyFrames;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;


public class WanderingBunnyAnimation extends CharactarAnimation {
	Point myPoint;
	WanderingBunnyAnimation(AnimationAction action,BunnyInfo info, Point myPoint) {
		super(action, info);
		this.myPoint = myPoint;
	}
	boolean friendInLastFrame=false;
	boolean supressAI=false;

	synchronized protected void sendBunnyTo(int X, int Y) {
		Log.i("BUNNY","friend send to "+X+"  "+Y);
		int fx = myPoint.x;
		int fy = myPoint.y;
		int ox = parent.origin.x;
		int oy = parent.origin.y;
		if (fx < ox){
			myPoint.x = parent.origin.x;	
		}
		if (fx > ox+320){
			myPoint.x = parent.origin.x+320;	
		}
		if (fy < oy){
			myPoint.y = parent.origin.y;
		}
		if (fy > oy+480){
			myPoint.y = parent.origin.y+480;
		}
		actualX = myPoint.x-parent.origin.x;
		actualY = myPoint.y-parent.origin.y;
		
		myPoint.x = parent.origin.x+X;
		myPoint.y = parent.origin.y+Y;
		setTarget(X,Y);
		bunnyRunningAnimation();
	}
	
	static final int FRIEND_AT_ACTION_INTERVAL=2000;
	static final int FRIEND_AT_ACTION_INTERVAL_RANDOM=2000;
	
	void setTarget(int X, int Y){
		super.setTarget(X, Y);
	}
	
	protected void playingFixedAnimation(){
		super.playingFixedAnimation();
		supressAI=true;
	}
	
	void onDoneWithFixedAnimation(){
		super.onDoneWithFixedAnimation();
		supressAI=false;
	}

}
