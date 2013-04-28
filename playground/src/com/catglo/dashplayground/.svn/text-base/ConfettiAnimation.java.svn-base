package com.catglo.dashplayground;

import android.graphics.Bitmap;

class ConfettiAnimation extends AnimationAction {
	static final int CONFETII_INTERVAL=50;
	float X;
	float Y;
	float volocityX;
	float volocityY;
	Bitmap bitmap;
	DrawingView parent;

	Runnable a = new Runnable() {public void run() {			
		synchronized (parent){
			X+=volocityX;
			
			Y+=volocityY;
			
			volocityY+=5;
			parent.postInvalidate();
			if (Y > 480 && volocityY>0){
				parent.removeAction(ConfettiAnimation.this);
			} else {
				actionTime=System.currentTimeMillis()+CONFETII_INTERVAL;
			}
		}
	}};
	ConfettiAnimation(DrawingView parent,int X,int Y,float volocityX, Bitmap bitmap, int time) {
		super(time, null);
		this.parent = parent;
		this.action=a;
		this.X=X;
		this.Y=Y;
		this.volocityX=volocityX;
		this.bitmap = bitmap;
		this.actionTime=System.currentTimeMillis()+time;
		volocityY=-70;
	}
	
}