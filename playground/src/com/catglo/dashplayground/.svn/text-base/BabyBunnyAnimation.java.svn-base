package com.catglo.dashplayground;

import java.util.LinkedList;
import java.util.Random;

import com.catglo.dashplayground.AnimationAction;
import com.catglo.dashplayground.AnimationFrame;
import com.catglo.dashplayground.BunnyGames;
import com.catglo.dashplayground.BunnyInfo;
import com.catglo.dashplayground.CharactarAnimation;
import com.catglo.dashplayground.MainBunnyAnimation;
import com.catglo.dashplayground.WanderingBunnyAnimation;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

class BabyInfo extends BunnyInfo {
	int mode=BabyBunnyAnimation.MODE_WANDERING;	
	int followOrder=0;
}

public class BabyBunnyAnimation extends WanderingBunnyAnimation {
	
	
	static final int MODE_WANDERING=1;
	static final int MODE_IN_CAGE=2;
	static final int MODE_EXITING_CAGE=3;
	static final int MODE_STUCK_IN_CAGE=4;
	static final int MODE_FOLLOWING = 5;


	public void setFollowOrder(int followOrder) {
		info.followOrder = followOrder;
	}

	BabyInfo info; 
	
	BabyBunnyAnimation(AnimationAction action, Point babyPoint, BabyInfo info) {
		super( action, (BunnyInfo)info, babyPoint);
		distanceTight=5; 
		distanceOpen=40; 	
		this.info = info;
		//variables that drive animation timings
		animationInterval=300;   
		distanceTight=5; 
		distanceOpen=40; 
		
	}

	void clipMyPoint(){
		if (myPoint.x<0)
			myPoint.x=10;
		if (myPoint.y<0)
			myPoint.y=10;
		if (myPoint.x>320*7)  
			myPoint.x=320*7-10;
		if (myPoint.y>480)
			myPoint.y=480;
	}
	
	final AnimationAction babyWanderingAI = new AnimationAction(FRIEND_AT_ACTION_INTERVAL,new Runnable(){public void run() {			
		final Random r = new Random();
		if (parent==null){
			babyWanderingAI.actionTime=FRIEND_AT_ACTION_INTERVAL+System.currentTimeMillis()+r.nextInt(FRIEND_AT_ACTION_INTERVAL_RANDOM);
			return;
		}
		synchronized (parent) {
			if (supressAI) {
				babyWanderingAI.actionTime=FRIEND_AT_ACTION_INTERVAL+System.currentTimeMillis()+r.nextInt(FRIEND_AT_ACTION_INTERVAL_RANDOM);
				
			} else {
				switch (info.mode){
				case MODE_IN_CAGE:
				case MODE_STUCK_IN_CAGE:
					processAvoidAreas=false;
					Rect frame = new Rect(parent.origin.x-1,parent.origin.y-1,parent.origin.x+321,parent.origin.y+481);			
					Rect babyRect = new Rect(myPoint.x-50,myPoint.y-50,myPoint.x+100,myPoint.y+100);
					
					if (Rect.intersects(frame, babyRect)){
						myPoint.x = (320*4) + 100+r.nextInt(120);
						myPoint.y = 0 + 80+r.nextInt(50);
						setTarget(myPoint.x-parent.origin.x,myPoint.y-parent.origin.y);
						actualX=getTargetX();
						actualY=getTargetY();
						friendInLastFrame=true;
						
						if (parent.bitmapCache.initDone){
							switch (r.nextInt(4)){
							case 0: setBunnyBitmap(info.frames.attention);
							break;
							case 1: setBunnyBitmap(info.frames.hop1);
							break;
							case 2: setBunnyBitmap(info.frames.laying);
							break;
							case 3: setBunnyBitmap(info.frames.lookup);
							break;
							}	
						}
					}
					
					babyWanderingAI.actionTime=FRIEND_AT_ACTION_INTERVAL+System.currentTimeMillis()+r.nextInt(FRIEND_AT_ACTION_INTERVAL_RANDOM);
					info.mode = MODE_STUCK_IN_CAGE;
					break;
				case MODE_EXITING_CAGE:
					if (parent.bitmapCache.initDone){
						processAvoidAreas=true;
						actualX=140+r.nextInt(50);
						myPoint.x = (320*4) + 10+r.nextInt(200);
						myPoint.y = 0 + 140+r.nextInt(350);
						clipMyPoint();	
						if (parent.whoAmI()==BunnyGames.BABY_BUNNIES){
							setTarget(myPoint.x-parent.origin.x,myPoint.y-parent.origin.y);
							friendInLastFrame=true;
						}
						bunnyRunningAnimation();
						info.mode = MODE_WANDERING;
					}
					babyWanderingAI.actionTime=FRIEND_AT_ACTION_INTERVAL+System.currentTimeMillis()+r.nextInt(FRIEND_AT_ACTION_INTERVAL_RANDOM);
					break;
				case MODE_WANDERING:
					if (parent.bitmapCache.initDone){
						info.mode = MODE_WANDERING;//HACK - somehow the mode was not wandering for wandering bnnies so I set it here
						processAvoidAreas=true;
						//Randomly pick a new spot to go to
						myPoint.x += (r.nextInt(7)-3)*25; 
						myPoint.y += (r.nextInt(7)-3)*25;
						clipMyPoint();	
						
						Rect friendRect = new Rect(myPoint.x-50,myPoint.y-50,myPoint.x+100,myPoint.y+100);//TODO: une the size of the bitmap instead of 100
						frame = new Rect(parent.origin.x-1,parent.origin.y-1,parent.origin.x+321,parent.origin.y+481);			
						if (frame.intersect(friendRect)){
							
							setTarget(myPoint.x-parent.origin.x,myPoint.y-parent.origin.y);
							friendInLastFrame=true;
						
							bunnyRunningAnimation();	
							
						}
					}
					babyWanderingAI.actionTime=FRIEND_AT_ACTION_INTERVAL+System.currentTimeMillis()+r.nextInt(FRIEND_AT_ACTION_INTERVAL_RANDOM);
					break;
				case MODE_FOLLOWING:
					babyWanderingAI.actionTime=750+System.currentTimeMillis()+r.nextInt(FRIEND_AT_ACTION_INTERVAL_RANDOM);;
					state=CharactarAnimation.STATE_RUNNING; //This relates to weather the baby is menuable or not
					
					Point follow = MainBunnyAnimation.followMePoint;
					//if (!(myPoint.x == follow.x && myPoint.y == follow.y)){
						myPoint.x = follow.x+r.nextInt(200)-100;
						myPoint.y = follow.y+r.nextInt(200)-100;
						Rect friendRect = new Rect(myPoint.x-50,myPoint.y-50,myPoint.x+100,myPoint.y+100);//TODO: une the size of the bitmap instead of 100
						frame = new Rect(parent.origin.x-1,parent.origin.y-1,parent.origin.x+321,parent.origin.y+481);			
						if (frame.intersect(friendRect)){
							setTarget(myPoint.x-parent.origin.x,myPoint.y-parent.origin.y);
							friendInLastFrame=true;
							bunnyRunningAnimation();		
						}
					//}
							
					
					break;
				}
			}
		}
	}});
	
	void setTarget(int X, int Y){
			super.setTarget(X, Y);
	}

	public void numberedDance(int number){
		LinkedList<AnimationFrame> frames;
		switch (number){
		case 0: danceSpinnLeft2();
			break;
		case 1: dance1();
			break;
		case 2: danceSpinnLeft();
			break;
		case 3: danceSpinnRight();
			break;
		case 4: danceSpinnRight2();
			break;
		case 5: whoWhoLeftRight();
			break;
		case 6: jumpForJoy(0);
			break;
		case 7: rightLeftSpinWhoWho();
			break;
		case 8: jumpAndShake(0, 0, null);
			break;
		}
	}
	
	public int randomDance() {
		final Random r = new Random();
		int retVal = r.nextInt(9);
		numberedDance(retVal);
		return retVal;
	}

}
