package com.catglo.dashplaygroundfree;

import android.graphics.Point;
import android.graphics.Rect;

public class MainBunnyAnimation extends CharactarAnimation {
	MainBunnyAnimation(DrawingView parent, AnimationAction action, BunnyInfo bunnyFrames) {
		super(action, bunnyFrames);
	}
	
	void onBunnyDoneMoving(){
		super.onBunnyDoneMoving();
		/*******************************
         * Process action areas
         * 
         *******************************/
        if (!supressActions){
        	for (int i = 0; i < parent.actionAreas.size();i++){
        
            	MenuItem m = parent.actionAreas.get(i);
            	Rect r = parent.scaleRect(m.area);
            	
            	if (m.inverted) {
            		if (!(actualX >= r.left && actualX <= r.right && actualY >= r.top && actualY <= r.bottom)){
            			m.inverted=false;
            			//Log.i("BUNNY","ACTION INVERT "+r.toString());
            		} 
            	}else{
	            	if (actualX >= r.left && actualX <= r.right && actualY >= r.top && actualY <= r.bottom
	            			&& parent.thread.currentlyRunning==true){
	            		//Log.i("BUNNY","ACTION GO "+r.toString());
	            		m.go();
	            	
	            	}
            	}
            }
    	}
	}

	public void jumpInHole(final int i,final int j) {
		
		
	}
	
	protected void playingFixedAnimation(){
		super.playingFixedAnimation();
		parent.suspendClick=true;
		supressActions=true;
	}
	
	void onDoneWithFixedAnimation(){
		super.onDoneWithFixedAnimation();
		parent.suspendClick=false;
		supressActions=false;
	}

	static Point followMePoint = new Point();
	protected void onKeepMoving(){
		super.onKeepMoving();
		Point cur = new Point(parent.origin.x+actualX,parent.origin.y+actualY);
		followMePoint.x = cur.x;
		followMePoint.y = cur.y;
	}
	
	/*void draw(Canvas canvas){
		super.draw(canvas);
		Iterator<Point> i = followMePoints.iterator();
		int width=2;
		while (i.hasNext()){
			Point p = i.next();
			Paint paint = new Paint();
			paint.setStrokeWidth(width);width+=1;
			canvas.drawPoint(p.x-parent.origin.x, p.y-parent.origin.y, paint);
		}
	}	*/
}
