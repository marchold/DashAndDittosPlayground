package com.catglo.dashplaygroundfree;

import java.util.Random;

import android.graphics.Point;
import android.graphics.Rect;

public class FriendBunnyAnimation extends WanderingBunnyAnimation {
	FriendBunnyAnimation(AnimationAction action, BunnyInfo info, Point myPoint) {
		super( action, info , myPoint);
		animationInterval=300;   
		distanceTight=9; 
		distanceOpen=75; 
	}

 	int friendMode=FRIEND_MODE_FOLLOW_RANDOM;
    static final int FRIEND_MODE_FOLLOW_RANDOM=1;
    static final int FRIEND_MODE_GOING_TO_LOCATION=2;
    static final int FRIEND_MODE_STAYING_STILL=3; 
  
    static final int FRIEND_AT_ACTION_INTERVAL=2000;
    static final int FRIEND_AT_ACTION_INTERVAL_RANDOM=2000;
    
	final AnimationAction freindAI = new AnimationAction(FRIEND_AT_ACTION_INTERVAL,new Runnable(){public void run() {			
		final Random r = new Random();
		synchronized (parent) {
			if (!supressAI && parent.bitmapCache.initDone) {
				//Randomly pick a new spot to go to
				parent.friendPoint.x += (r.nextInt(7)-3)*25; 
				parent.friendPoint.y += (r.nextInt(7)-3)*25;
				
				//Check the distance from the bunny and get closer if we are too far
				Point mainPoint = new Point(parent.mainBunny.actualX+parent.origin.x,parent.mainBunny.actualY+parent.origin.y);
				float xDiff = parent.friendPoint.x-mainPoint.x; 
	            float yDiff = parent.friendPoint.y-mainPoint.y; 
	            float distance = (float) Math.sqrt(xDiff*xDiff+yDiff*yDiff);
	            float theta = (float) Math.atan2(yDiff,xDiff);
	            if (distance > 200){	
	            	parent.friendPoint.x -= (float) (80 * Math.cos( theta ));
	            	parent.friendPoint.y -= (float) (80 * Math.sin( theta ));
	            }
	            
	          	
				Rect friendRect = new Rect(parent.friendPoint.x-50,parent.friendPoint.y-50,parent.friendPoint.x+100,parent.friendPoint.y+100);//TODO: une the size of the bitmap instead of 100
				Rect frame = new Rect(parent.origin.x-1,parent.origin.y-1,parent.origin.x+321,parent.origin.y+481);			
				if (frame.intersect(friendRect)){
					
					setTarget(parent.friendPoint.x-parent.origin.x,parent.friendPoint.y-parent.origin.y);
					friendInLastFrame=true;
					bunnyRunningAnimation();	
				}
			}
			freindAI.actionTime=FRIEND_AT_ACTION_INTERVAL+System.currentTimeMillis()+r.nextInt(FRIEND_AT_ACTION_INTERVAL_RANDOM);
		}
	}});
	
	void onDoneWithFixedAnimation(){
		super.onDoneWithFixedAnimation();
		parent.addAction(freindAI);
	}

	synchronized void playFixedAnimation(final AnimationFrames frames){
		parent.removeAction(freindAI);
		super.playFixedAnimation(frames);
	}
}
