package com.catglo.dashplaygroundfree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.catglo.dashplaygroundfree.BitmapCache.BunnyFrames;

class BunnyInfo {
	BunnyFrames frames;
}


class CharactarAnimation {
	//Animation States
	static final int STATE_RUNNING=1;
	static final int STATE_STIITNG=2;
	int state=STATE_STIITNG;
	
	//how to draw variables
	int actualX;
	int actualY;
	private int targetX;
	private int targetY;
	int getTargetX(){return targetX;}
	int getTargetY(){return targetY;}
	
	Matrix imageMatrix;
	boolean lastAnimationMirrored=false;
	Bitmap bunnyImage;
	
	//set to true when action areas are disabled
	boolean supressActions=false;
	
	//variables that drive animation timings
	int animationInterval=250;   
	int distanceTight=10; 
	int distanceOpen=80; 
	
	
	BunnyInfo bunnies;
	
	DrawingView parent;
	AnimationAction action;
	CharactarAnimation(AnimationAction action, BunnyInfo bunnyFrames){
		imageMatrix=new Matrix();
		this.action = action;
		this.bunnies = bunnyFrames;
	}
	
	ArrayList<Rect> areasToAvoid = new ArrayList<Rect>();
	void addAreaToAvoid(Rect avoidRect){
		areasToAvoid.add(avoidRect);
	}

	Point fromPoint;// = new Point();
	private Point[] toPoint = new Point[40];
	private int toPointCount=0;
	
	boolean processAvoidAreas=true;
	void setTarget(int X, int Y){
		if (parent==null){
			return;
		}
		synchronized (parent) {
			Point from = new Point(targetX,targetY);
			int toPointIndex=0;
			targetX=X;
			targetY=Y;
			if (processAvoidAreas){
				Rect bunnyRect = new Rect(targetX-20,targetY-20,targetX+20,targetY+20);
				boolean right=true;
				boolean down=true;
				boolean moved=false;
				
				//First it is necessary to check all the rectangles for one to avoid.
				Iterator<Rect> foreach = areasToAvoid.iterator();
				while (foreach.hasNext()){
					Rect avoidRect = foreach.next();
		 		
					if (Rect.intersects(bunnyRect,avoidRect)){
						if (targetY >= avoidRect.centerY() && targetY <= avoidRect.bottom){
							//Log.i("BUNNY","bunz avoid Y1="+targetY+"-->"+avoidRect.bottom);
							targetY = avoidRect.bottom+40;
							down=true;
							moved=true;
						}
						if (targetY <= avoidRect.centerY() && targetY >= avoidRect.top){
							//Log.i("BUNNY","bunz avoid Y2="+targetY+"-->"+avoidRect.top);
							targetY = avoidRect.top-40;
							down=false;
							moved=true;
						}
						if (toPointIndex<toPoint.length)
							toPoint[toPointIndex++] = new Point(targetX,targetY);
					}
					
					bunnyRect = new Rect(targetX-20,targetY-20,targetX+20,targetY+20);
					if (Rect.intersects(bunnyRect,avoidRect)){
						if (targetX >= avoidRect.centerX() && targetX <= avoidRect.right){
							//Log.i("BUNNY","bunz avoid X1="+targetX+"-->"+avoidRect.right+1);
							targetX = avoidRect.right+40;
							right=true;
							moved=true;
						}
						if (targetX <= avoidRect.centerX() && targetX >= avoidRect.left){
							//Log.i("BUNNY","bunz avoid X2="+targetX+"-->"+avoidRect.left);
							targetX = avoidRect.left-40;
							right=false;
							moved=true;
						}
					}
				}
				
				//If we moved our target due to the avoid areas we need to check if we moved 
				//in to another avoid area and move out of that one
				while (moved){
					moved=false;
					foreach = areasToAvoid.iterator();
					while (foreach.hasNext()){
						Rect avoidRect = foreach.next();
						if (Rect.intersects(bunnyRect,avoidRect)){
							if (targetY >= avoidRect.top && targetY <= avoidRect.bottom){
								if (down){
									targetY = avoidRect.bottom+40;
								}else{
									targetY = avoidRect.top-40;
								}
								moved=true;
								if (toPointIndex<toPoint.length)
								toPoint[toPointIndex++] = new Point(targetX,targetY);
							}
							
						}
						
						bunnyRect = new Rect(targetX-20,targetY-20,targetX+20,targetY+20);
						if (Rect.intersects(bunnyRect,avoidRect)){
							if (targetX >= avoidRect.left && targetX <= avoidRect.right){
								if (right) {
									targetX = avoidRect.right+40;
								} else {
									targetX = avoidRect.left-40;
								}
								moved=true;
								if (toPointIndex<toPoint.length)
								toPoint[toPointIndex++] = new Point(targetX,targetY);
							}
						}
					}
				}
			}
			toPointCount=toPointIndex;
		}
	}
	
	void bunnyRunningAnimation() {
		
		state=STATE_RUNNING;
		driveBunnyRunningAnimation();
	}
	
	LinkedList<Runnable> onDoneMovingHandler = new LinkedList<Runnable>();
	void onBunnyDoneMoving(){
		parent.onBunnyDoneMoving();
		while (onDoneMovingHandler.size()>0){
			Runnable r = onDoneMovingHandler.remove();
			r.run();
		}
	}
	
	protected void onKeepMoving(){
		
	}
	
	void driveBunnyRunningAnimation() {
		setNextBunnyAnimation(animationInterval+1,"running1",new Runnable(){public void run() {
			if (parent==null){return;}synchronized (parent){
    			float xDiff = targetX-actualX; 
	            float yDiff = targetY-actualY;
	            float distance = (float) Math.sqrt(xDiff*xDiff+yDiff*yDiff);
	            float theta = (float) Math.atan2(yDiff,xDiff);
	          	
	            float od = distance;
	    		 
	    		if (distance < -distanceTight)
	    			distance=-distanceTight;
	    		if (distance > distanceTight)
	    			distance = distanceTight;
	    		
	    		actualX += (float) (distance * Math.cos( theta ));
	    		actualY += (float) (distance * Math.sin( theta ));
	    		parent.onBunnyMoved();
	    		
	    		
	    		//06-25 13:36:14.167: ERROR/AndroidRuntime(6490): Uncaught handler: thread Thread-18 exiting due to uncaught exception
	    		//06-25 13:36:14.307: ERROR/AndroidRuntime(6490): java.lang.NullPointerException
	    		//06-25 13:36:14.307: ERROR/AndroidRuntime(6490):     at com.catglo.dashplayground.CharactarAnimation$1.run(CharactarAnimation.java:196)
	    		//06-25 13:36:14.307: ERROR/AndroidRuntime(6490):     at com.catglo.dashplayground.DrawingView.run(DrawingView.java:153)
	    		//06-25 13:36:14.307: ERROR/AndroidRuntime(6490):     at com.catglo.dashplayground.DrawingView$DrawingThread.run(DrawingView.java:300)

	    		//Log.i("BUNNY","set hoptight");
	    		if (bunnies.frames!=null)                     //So I blindly added
	    			setBunnyBitmap(bunnies.frames.hopTight);
	    		
	    		if (od < 80 && od > -80)
	    		{
	    		//	done=true;
	    	        addBunnyNotMovingAnimations();
	    	        actualX = targetX;
	    	        actualY = targetY;
	    	        onBunnyDoneMoving();
		    		
	    	        return;
	    		} else {
	    			onKeepMoving();
	    		}
	    		
	    		setNextBunnyAnimation(animationInterval-1,"running2",new Runnable(){public void run() {
	    			if (parent==null)
	    				return;
	    			synchronized (parent){
	    				float xDiff = targetX-actualX; 
				        float yDiff = targetY-actualY;
				        float distance = (float) Math.sqrt(xDiff*xDiff+yDiff*yDiff);
				        float theta = (float) Math.atan2(yDiff,xDiff);
				       
						if (distance < -distanceOpen)
		        			distance=-distanceOpen;
		        		if (distance > distanceOpen)
		        			distance = distanceOpen;
		        		
		        		actualX += (float) (distance * Math.cos( theta ));
		        		actualY += (float) (distance * Math.sin( theta ));
		        	    parent.onBunnyMoved();
		        	    onKeepMoving();
		        		//Log.i("BUNNY","set hopopen");
			    		if (bunnies.frames!=null)
			    			setBunnyBitmap(bunnies.frames.hopOpen);
						if (state==STATE_RUNNING)
							driveBunnyRunningAnimation();
	    			}
	    		}});
    		}
    	}});
    }
	
	protected void setNextBunnyAnimation(int t, String debugString,Runnable r){
		//Log.i("BuNNY","set next annimation "+initThread);
		action.name=debugString;
		action.action = r;
		action.actionTime=System.currentTimeMillis()+t;
	}
	
	protected void addBunnyNotMovingAnimations() {
		state=STATE_STIITNG;
		driveBunnyNotMovingAnimations();
	}
	
	protected void startBunnyAtAttention(final int time) {
		setNextBunnyAnimation(0,"at attention",new Runnable(){public void run() {
			synchronized (parent){
				if (bunnies.frames != null && bunnies.frames.attention != null){
					setBunnyBitmap(bunnies.frames.attention,getSittingBunnyMatrix());
				}
				setNextBunnyAnimation(time, "not moving 2", new Runnable(){public void run() {
					synchronized (parent){
						addBunnyNotMovingAnimations();
					}
				}});
			}
		}});
	}

	
	protected void startBunnySpinningJumprope(final boolean rightFacing, final int X, final int Y) {
		setNextBunnyAnimation(0,"jumprope spinning",new Runnable(){public void run() {
			synchronized (parent){
				Matrix m = new Matrix();
				if (!rightFacing)
					m.preScale(-1.0f,1.0f);
				actualX=X;
				actualY=Y;
				setBunnyBitmap(bunnies.frames.push3,m);
				action.actionTime=Long.MAX_VALUE;
			}
		}});
	}
	
	void setRopeSpinStage(int ropeSpinStage){
		//action.actionTime=System.currentTimeMillis()+1000;
		
	}
	
	protected void driveBunnyNotMovingAnimations() {
		setNextBunnyAnimation(200,"not moving 1",new Runnable(){public void run() {
			if (parent==null)
				return;
			synchronized (parent){
				if (bunnies.frames!=null && bunnies.frames.laying!=null)
					setBunnyBitmap(bunnies.frames.laying,getSittingBunnyMatrix());
				setNextBunnyAnimation(3000, "not moving 2", new Runnable(){public void run() {
					if (parent==null)
						return;
					synchronized (parent){
						if (bunnies.frames!=null && bunnies.frames.lookup!=null)
							setBunnyBitmap(bunnies.frames.lookup,getSittingBunnyMatrix());
						if (state==STATE_STIITNG)
							driveBunnyNotMovingAnimations();
					}
				}});
			}
		}});
	}

	protected void bunnyEnteringBorrowedAnimation(final Bitmap bitmap, final Matrix matrix) {
		setNextBunnyAnimation(0,"borrowed",new Runnable(){public void run() {
			synchronized (parent){
				setBunnyBitmap(bitmap,matrix);
				//Log.i("BUNNY","FORCE BITMAP");
				parent.postInvalidate();
				setNextBunnyAnimation(500,"end borrow",new Runnable(){public void run() {//TODO:change to smaller non debug value
					synchronized (parent){
						addBunnyNotMovingAnimations();		
					}
				}});
			}
		}});
	}
	
	void setBunnyBitmap(Bitmap bitmap, Matrix m){
		
		imageMatrix = new Matrix();
		imageMatrix.set(m);
		
		imageMatrix.preTranslate(-32f, -54f);
		imageMatrix.postTranslate(actualX, actualY);
		
		
		bunnyImage=bitmap;
		parent.postInvalidate();
	}
	
	void draw(Canvas canvas){
		if (bunnyImage!=null)
			canvas.drawBitmap(bunnyImage, imageMatrix, null);  
		
		
		if (processAvoidAreas && parent.showDebug){
			Paint p = new Paint();
			p.setColor(Color.RED);
			p.setAlpha(128);
			
			
			//First it is necessary to check all the rectangles for one to avoid.
			Iterator<Rect> foreach = areasToAvoid.iterator();
			while (foreach.hasNext()){
				Rect avoidRect = foreach.next();
				canvas.drawRect(avoidRect, p);
			}
			
			p.setColor(Color.WHITE);
			p.setAlpha(255);
			int width=4;
			p.setStrokeWidth(width);
			if (fromPoint!=null)
				canvas.drawPoint(fromPoint.x, fromPoint.y, p);
			p.setColor(Color.BLACK);
			for (int i = 0; i < toPointCount;i++){
				if (toPoint[i]!=null)
					canvas.drawPoint(toPoint[i].x, toPoint[i].y, p);
				p.setStrokeWidth(++width);
			}
			
		}
			
	}
	
	private void moveBunnyAndShowTight(Matrix matrix,int xOffset, int yOffset) {
     	//bunnyImage=Bitmap.createBitmap(bunnyHopTight, 0, 0,bunnyHopTight.getWidth(),  bunnyHopTight.getHeight(), matrix, true);
		actualX = actualX+xOffset;
 		actualY = actualY+yOffset;
 		
 		if (actualX > 320) actualX=320;
 		if (actualX < 0)   actualX = 0;
 		if (actualY > 480) actualY = 480;
 		if (actualY < 0)   actualY = 0;
 		
 		setBunnyBitmap(bunnies.frames.hopTight,matrix);
 		
 		//showingTight=true;
			
	}
	
	synchronized protected void sendBunnyTo(int X, int Y) {
		//Log.i("BUNNY","Send Bunny Home "+X+"  "+Y);
		targetX = X;
		targetY = Y;
	//	touchTime=System.currentTimeMillis();
	//	timeStartMoving=System.currentTimeMillis();
		synchronized(parent.thread){
       		parent.thread.notify();
       	}
		bunnyRunningAnimation();
		synchronized(parent.thread){
       		parent.thread.notify();
       	}
		
	}

	float lastAngle=0;
	Matrix getBunnyMatrix() {
		Matrix matrix = new Matrix();
      	float xDiff = targetX-actualX; 
        float yDiff = targetY-actualY;
        
        //check to see if we are no longer moving and use cached values
        if (xDiff+yDiff==0){
        	if (lastAnimationMirrored){
        		matrix.preScale(-1.0f,1.0f);
        	//	Log.i("BUNNY","MIRROR - no angle ");
        	}
        	return matrix;
        }
      
        float theta = (float) Math.atan2(yDiff,xDiff);  	
		float angle=(float) ((theta/Math.PI)*180f);
		
     	//These 2 if statements deal with the fact that when the bunny
    	//is running right he should not be drawn upside down, but instead
    	//mirrored and adjust the angle
	  	if (angle >= 90 && angle <= 180) {
	  		angle-=180;
	  		matrix.preScale(-1.0f,1.0f);
	  		lastAnimationMirrored=true;
	 // 		Log.i("BUNNY","MIRROR - positive angle "+angle);
	  	} else
	  	if (angle >= -180 && angle <= -90){
	  		angle+=180;
	  		matrix.preScale(-1.0f,1.0f);
	  		lastAnimationMirrored=true;
	  //		Log.i("BUNNY","MIRROR - negative angle "+angle);
	  	} else {
	  		lastAnimationMirrored=false;
	 // 		Log.i("BUNNY","MIRROR - not mirrored angle "+angle);
	  	}
	 	matrix.postRotate(angle); 
	 	lastAngle=angle;
	 	return matrix;
   
	}
	
	Matrix getSittingBunnyMatrix() {
		Matrix matrix = new Matrix();
      
		if (lastAnimationMirrored){
        	matrix.preScale(-1.0f,1.0f);
        }
        return matrix;
	}
	
	
	
	void setBunnyBitmap(Bitmap bitmap){
		imageMatrix = getBunnyMatrix();
      	
		imageMatrix.preTranslate(-32f, -54f);
		imageMatrix.postTranslate(actualX, actualY);
		
		bunnyImage=bitmap;
		parent.postInvalidate();
	}
	
	void addJump(LinkedList<AnimationFrame> frames){
		if (bunnies.frames     == null || 
		    bunnies.frames.hop1==null  ||
		    bunnies.frames.hop2==null  ||
		    bunnies.frames.hop3==null){
			return;
		}
		frames.add(new AnimationFrame(0,0-30 ,bunnies.frames.hop1,  0,-1));
		frames.add(new AnimationFrame(0,0-30 ,bunnies.frames.hop2,100,-1));
		frames.add(new AnimationFrame(0,0-30 ,bunnies.frames.hop1,100,-1));
		frames.add(new AnimationFrame(0,0+30 ,bunnies.frames.hop3,100,-1));
		frames.add(new AnimationFrame(0,0+30 ,bunnies.frames.hop3,100,-1));	
		frames.add(new AnimationFrame(0,0+30 ,bunnies.frames.hop1,100,-1));
		
	}
	
	void addJumpForJoy(LinkedList<AnimationFrame> frames,int ys){
		if (bunnies.frames     == null || 
		    bunnies.frames.hop1==null  ||
		    bunnies.frames.hop2==null  ||
		    bunnies.frames.hop3==null){
			return;
		}
		frames.add(new AnimationFrame(0,0-30    ,bunnies.frames.hop1,  0,-1));
		/*// Tag: AndroidRuntime
// java.lang.NullPointerException: 
//   at com.catglo.dashplayground.CharactarAnimation.addJumpForJoy(CharactarAnimation.java:481)
//   at com.catglo.dashplayground.CharactarAnimation.jumpForJoy(CharactarAnimation.java:506)
//   at com.catglo.dashplayground.DrawingView.onTouchEvent(DrawingView.java:692)
//   at android.view.View.dispatchTouchEvent(View.java:3672)
//   at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:820)
//   at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:820)
//   at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:820)
//   at com.android.internal.policy.impl.PhoneWindow$DecorView.superDispatchTouchEvent(PhoneWindow.java:1712)
//   at com.android.internal.policy.impl.PhoneWindow.superDispatchTouchEvent(PhoneWindow.java:1202)
//   at android.app.Activity.dispatchTouchEvent(Activity.java:1987)
//   at com.android.internal.policy.impl.PhoneWindow$DecorView.dispatchTouchEvent(PhoneWindow.java:1696)
//   at android.view.ViewRoot.handleMessage(ViewRoot.java:1658)
//   at android.os.Handler.dispatchMessage(Handler.java:99)
//   at android.os.Looper.loop(Looper.java:123)
//   at android.app.ActivityThread.main(ActivityThread.java:4203)
//   at java.lang.reflect.Method.invokeNative(Method.java:-2)
//   at java.lang.reflect.Method.invoke(Method.java:521)
//   at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:791)
//   at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:549)
//   at dalvik.system.NativeStart.main(NativeStart.java:-2)

** Monkey aborted due to error.
Events injected: 512
## Network stats: elapsed time=20149ms (7070ms mobile, 0ms wifi, 13079ms not connected)
** System appears to have crashed at event 512 of 100000 using seed 0
*/
		
		frames.add(new AnimationFrame(0,0-30    ,bunnies.frames.hop2,100,-1));
		frames.add(new AnimationFrame(0,0-30    ,bunnies.frames.hop1,100,-1));
		
		frames.add(new AnimationFrame(0,0+30+ys ,bunnies.frames.hop3,100,-1));
		frames.add(new AnimationFrame(0,0+30    ,bunnies.frames.hop3,100,-1));
		frames.add(new AnimationFrame(0,0+30+ys ,bunnies.frames.hop1,100,-1));
		
		frames.add(new AnimationFrame(0,0-30+ys ,bunnies.frames.hop2,100,-1));
		frames.add(new AnimationFrame(0,0-30    ,bunnies.frames.hop1,100,-1));
		frames.add(new AnimationFrame(0,0+30    ,bunnies.frames.hop3,100,-1));
		frames.add(new AnimationFrame(0,0+30+ys ,bunnies.frames.hop3,100,-1));
		
		frames.add(new AnimationFrame(0,0-30+ys ,bunnies.frames.hop1,100,-1));
		frames.add(new AnimationFrame(0,0-30    ,bunnies.frames.hop2,100,-1));
		frames.add(new AnimationFrame(0,0-30+ys ,bunnies.frames.hop1,100,-1));
		
		frames.add(new AnimationFrame(0,0+30    ,bunnies.frames.hop3,100,-1));
		frames.add(new AnimationFrame(0,0+30    ,bunnies.frames.hop3,100,-1));
		frames.add(new AnimationFrame(0,0+30    ,bunnies.frames.hop1,100,-1));
		
	}
	
	protected void jumpForJoy(int ys) {
		LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();	
		addJumpForJoy(frames,ys);
		frames.getLast().extraAction =  new Runnable(){public void run() {addBunnyNotMovingAnimations();}};
		play(frames);
 		
	}
	
	void addHipShakeAndSlide(LinkedList<AnimationFrame> frames,final int xs){
		if (bunnies.frames     == null || 
		    bunnies.frames.winnerRight ==null  ||
		    bunnies.frames.winnerCenter==null  ||
		    bunnies.frames.winnerLeft  ==null){
			return;
		}
		frames.add(new AnimationFrame(0 + (xs),0, bunnies.frames.winnerRight ,200,-1));
        frames.add(new AnimationFrame(0       ,0, bunnies.frames.winnerCenter,200,-1));
        frames.add(new AnimationFrame(0 + (xs),0, bunnies.frames.winnerLeft  ,200,-1));
        frames.add(new AnimationFrame(0       ,0, bunnies.frames.winnerRight ,200,-1));
        frames.add(new AnimationFrame(0 + (xs),0, bunnies.frames.winnerCenter,200,-1));
        frames.add(new AnimationFrame(0       ,0, bunnies.frames.winnerLeft  ,200,-1));
        frames.add(new AnimationFrame(0 + (xs),0, bunnies.frames.winnerRight ,200,-1));
        frames.add(new AnimationFrame(0 + (xs),0, bunnies.frames.winnerCenter,200,-1));
        frames.add(new AnimationFrame(0 + (xs),0, bunnies.frames.winnerLeft  ,200,-1));
	}
	
	protected void jumpAndShake(final int xs,final int ys,Runnable runWhenDone) {
		LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();	
		
		frames.add(        new AnimationFrame(0,0    ,bunnies.frames.hop1,  0,-1));
                
		addJumpForJoy(frames,ys);
		addHipShakeAndSlide(frames,xs);
		addJumpForJoy(frames,ys);
		addHipShakeAndSlide(frames,xs);
		addJumpForJoy(frames,ys);
		addHipShakeAndSlide(frames,xs);
		addJumpForJoy(frames,ys);
		frames.getLast().extraAction = runWhenDone;
		play(frames);
 		
	}
	
	
	void addClapSequence(LinkedList<AnimationFrame> frames) {
		if (bunnies.frames     == null || 
		    bunnies.frames.clapClosed1 ==null  ||
		    bunnies.frames.clapClosed2 ==null  ||
		    bunnies.frames.clapOpen    ==null){
			return;
		}
		frames.add(new AnimationFrame(0,0    ,bunnies.frames.clapClosed1,  80,-1));
		frames.add(new AnimationFrame(0,0    ,bunnies.frames.clapOpen   ,  80,-1));
        frames.add(new AnimationFrame(0,0    ,bunnies.frames.clapClosed2,  80,-1));
        frames.add(new AnimationFrame(0,0    ,bunnies.frames.clapOpen   ,  80,-1));
	}
	
	public void clap(int howLong) {
		LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		int j=0;
		for (int i = 0; i < howLong; i++){
			addClapSequence(frames);
		}
		play(frames);
	}
	
	public void addLeftSpin(LinkedList<AnimationFrame> frames){
		if (bunnies.frames            == null || 
		    bunnies.frames.jmpFlat    ==null  ||
		    bunnies.frames.dance1Left ==null  ||
		    bunnies.frames.dance1Right==null  ||
		    bunnies.frames.hopFlat    ==null){
			return;
		}
        frames.add(new AnimationFrame(9,0    ,bunnies.frames.jmpFlat       ,160,-1));
	    frames.add(new AnimationFrame(9,0    ,bunnies.frames.dance1Left    ,160,-1));
        frames.add(new AnimationFrame(9,0    ,bunnies.frames.hopFlat       ,160,-1));
        frames.add(new AnimationFrame(9,0    ,bunnies.frames.dance1Right   ,160,-1));
        frames.add(new AnimationFrame(9,0    ,bunnies.frames.jmpFlat       ,160,-1));
	}
	
	public void addRightSpin(LinkedList<AnimationFrame> frames){
		if (bunnies.frames            == null || 
		    bunnies.frames.jmpFlat    ==null  ||
		    bunnies.frames.dance1Left ==null  ||
		    bunnies.frames.dance1Right==null  ||
		    bunnies.frames.hopFlat    ==null){
			return;
		}
        frames.add(new AnimationFrame(-9,0    ,bunnies.frames.jmpFlat       ,160,-1));
        frames.add(new AnimationFrame(-9,0    ,bunnies.frames.dance1Right   ,160,-1));
        frames.add(new AnimationFrame(-9,0    ,bunnies.frames.hopFlat       ,160,-1));
        frames.add(new AnimationFrame(-9,0    ,bunnies.frames.dance1Left    ,160,-1));
        frames.add(new AnimationFrame(-9,0    ,bunnies.frames.jmpFlat       ,160,-1));
	}
	
	public void addLeftSpin2(LinkedList<AnimationFrame> frames){
		if (bunnies.frames            == null || 
		    bunnies.frames.jmpLeft    ==null  ||
		    bunnies.frames.dance2Left ==null  ||
		    bunnies.frames.dance2Right==null  ||
		    bunnies.frames.hopLeft    ==null){
			return;
		}
        frames.add(new AnimationFrame(9,0    ,bunnies.frames.jmpLeft       ,160,-1));
	    frames.add(new AnimationFrame(9,0    ,bunnies.frames.dance2Left    ,160,-1));
        frames.add(new AnimationFrame(9,0    ,bunnies.frames.hopLeft       ,160,-1));
        frames.add(new AnimationFrame(9,0    ,bunnies.frames.dance2Right   ,160,-1));
        frames.add(new AnimationFrame(9,0    ,bunnies.frames.jmpLeft       ,160,-1));
	}
	
	public void addRightSpin2(LinkedList<AnimationFrame> frames){
		if (bunnies.frames             == null || 
		    bunnies.frames.jmpRight    ==null  ||
		    bunnies.frames.dance2Right ==null  ||
		    bunnies.frames.dance2Left  ==null  ||
		    bunnies.frames.hopRight    ==null){
			return;
		}
        frames.add(new AnimationFrame(9,0    ,bunnies.frames.jmpRight       ,160,-1));
	    frames.add(new AnimationFrame(9,0    ,bunnies.frames.dance2Right    ,160,-1));
        frames.add(new AnimationFrame(9,0    ,bunnies.frames.hopRight       ,160,-1));
        frames.add(new AnimationFrame(9,0    ,bunnies.frames.dance2Left     ,160,-1));
        frames.add(new AnimationFrame(9,0    ,bunnies.frames.jmpRight       ,160,-1));
	}

	
	public void addCartWheelRight(LinkedList<AnimationFrame> frames){
		if (bunnies.frames            == null || 
		    bunnies.frames.jmpRight    ==null  ||
		    bunnies.frames.jmpLeft    ==null){
			return;
		}
 		frames.add(new AnimationFrame(4,0, bunnies.frames.jmpRight, 50, -1, false, 40f));
		frames.add(new AnimationFrame(4,0, bunnies.frames.jmpRight, 50, -1, false, 80f));
		frames.add(new AnimationFrame(4,0, bunnies.frames.jmpRight, 50, -1, false, 120f));
		frames.add(new AnimationFrame(4,0, bunnies.frames.jmpRight, 50, -1, false, 160f));
		frames.add(new AnimationFrame(4,0, bunnies.frames.jmpRight, 50, -1, false, 200f));
		frames.add(new AnimationFrame(4,0, bunnies.frames.jmpLeft,  50, -1, false, 240f));
		frames.add(new AnimationFrame(4,0, bunnies.frames.jmpLeft,  50, -1, false, 280f));
		frames.add(new AnimationFrame(4,0, bunnies.frames.jmpLeft,  50, -1, false, 320f));
		frames.add(new AnimationFrame(4,0, bunnies.frames.jmpLeft,  50, -1, false, 360f));
		frames.add(new AnimationFrame(4,0, bunnies.frames.jmpRight, 50, -1, false, 360f));
	}
	
	public void addCartWheelLeft(LinkedList<AnimationFrame> frames){
		if (bunnies.frames            == null || 
		    bunnies.frames.jmpRight    ==null  ||
		    bunnies.frames.jmpLeft    ==null){
			return;
		}
		frames.add(new AnimationFrame(-4,0, bunnies.frames.jmpLeft, 50, -1, false, 320f));
		frames.add(new AnimationFrame(-4,0, bunnies.frames.jmpLeft, 50, -1, false, 280f));
		frames.add(new AnimationFrame(-4,0, bunnies.frames.jmpLeft, 50, -1, false, 240f));
		frames.add(new AnimationFrame(-4,0, bunnies.frames.jmpLeft, 50, -1, false, 200f));
		frames.add(new AnimationFrame(-4,0, bunnies.frames.jmpLeft, 50, -1, false, 160f));
		frames.add(new AnimationFrame(-4,0, bunnies.frames.jmpRight,  50, -1, false, 120f));
		frames.add(new AnimationFrame(-4,0, bunnies.frames.jmpRight,  50, -1, false, 80f));
		frames.add(new AnimationFrame(-4,0, bunnies.frames.jmpRight,  50, -1, false, 40f));
		frames.add(new AnimationFrame(-4,0, bunnies.frames.jmpRight,  50, -1));
		frames.add(new AnimationFrame(-4,0, bunnies.frames.jmpLeft, 50, -1));
	}
	
	public void rightLeftSpinWhoWho(){
		if (bunnies.frames          == null || 
		    bunnies.frames.push2    ==null  ||
		    bunnies.frames.push3    ==null){
			return;
		}
		LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		addRightSpin(frames);
		frames.add(new AnimationFrame(-5,0, bunnies.frames.push2,  160, -1, true, 0f));
		frames.add(new AnimationFrame(-5,0, bunnies.frames.push3,  160, -1, true, 0f));
		addLeftSpin(frames);
		frames.add(new AnimationFrame(5,0, bunnies.frames.push2,  160, -1, false, 0f));
		frames.add(new AnimationFrame(5,0, bunnies.frames.push3,  160, -1, false, 0f));
		addRightSpin(frames);
		frames.add(new AnimationFrame(-5,0, bunnies.frames.push2,  160, -1, true, 0f));
		frames.add(new AnimationFrame(-5,0, bunnies.frames.push3,  160, -1, true, 0f));
		addLeftSpin(frames);
		frames.add(new AnimationFrame(5,0, bunnies.frames.push2,  160, -1, false, 0f));
		frames.add(new AnimationFrame(5,0, bunnies.frames.push3,  160, -1, false, 0f));
		play(frames);
	}
	
	public void whoWhoLeftRight(){
		if (bunnies.frames          == null || 
		    bunnies.frames.jmpFlat  ==null  ||
		    bunnies.frames.push3    ==null  ||
		    bunnies.frames.push2    ==null){
			return;
		}
		LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		frames.add(new AnimationFrame(0,0, bunnies.frames.jmpFlat,  160, -1));
		frames.add(new AnimationFrame(0,0, bunnies.frames.push2,    160, -1));
		frames.add(new AnimationFrame(0,0, bunnies.frames.push3,    160, -1));
		frames.add(new AnimationFrame(0,0, bunnies.frames.jmpFlat,  160, -1));
		frames.add(new AnimationFrame(0,0, bunnies.frames.push2,    160, -1, true, 0f));
		frames.add(new AnimationFrame(0,0, bunnies.frames.push3,    160, -1, true, 0f));
		frames.add(new AnimationFrame(0,0, bunnies.frames.jmpFlat,  160, -1));

		frames.add(new AnimationFrame(0,0, bunnies.frames.jmpFlat,  160, -1));
		frames.add(new AnimationFrame(0,0, bunnies.frames.push2,    160, -1));
		frames.add(new AnimationFrame(0,0, bunnies.frames.push3,    160, -1));
		frames.add(new AnimationFrame(0,0, bunnies.frames.jmpFlat,  160, -1));
		frames.add(new AnimationFrame(0,0, bunnies.frames.push2,    160, -1, true, 0f));
		frames.add(new AnimationFrame(0,0, bunnies.frames.push3,    160, -1, true, 0f));
		frames.add(new AnimationFrame(0,0, bunnies.frames.jmpFlat,  160, -1));
	
		play(frames);
	}
	
	public void dance1() {
		LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		/*addLeftSpin(frames);
		addRightSpin(frames);
		frames.add(new AnimationFrame(0,0    ,bunnies.frames.jmpRight     ,200,-1));
	    frames.add(new AnimationFrame(0,0    ,bunnies.frames.jmpLeft      ,200,-1));
	    addLeftSpin(frames);
		addRightSpin(frames);
		addJump(frames);
		addLeftSpin(frames);
		addRightSpin(frames);
		addClapSequence(frames);	*/
		if (actualX>160){
			addCartWheelLeft(frames);
		}else{
			addCartWheelRight(frames);
		}
	    play(frames);
	}
	
	public void danceSpinnLeft() {
		LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		addLeftSpin(frames);
		play(frames);
	}
	
	public void danceSpinnRight() {
		LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		addRightSpin(frames);
		play(frames);
	}
	
	public void danceSpinnLeft2() {
		LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		addLeftSpin2(frames);
		play(frames);
	}
	
	public void danceSpinnRight2() {
		LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		addRightSpin2(frames);
		play(frames);
	}
	
	public void cartwheelWiinner(int xs, int ys, Runnable whenDone){
		LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		if (xs>0){
			addCartWheelRight(frames);
			addJumpForJoy(frames, ys);
		 	addCartWheelRight(frames);
		 	addJumpForJoy(frames, ys);
			addCartWheelRight(frames);
		} else if (xs < 0){
			addCartWheelLeft(frames);
			addJumpForJoy(frames, ys);
		 	addCartWheelLeft(frames);
		 	addJumpForJoy(frames, ys);
			addCartWheelLeft(frames);
		} else {
			addCartWheelLeft(frames);
			addJumpForJoy(frames, ys);
		 	addCartWheelRight(frames);
		 	addJumpForJoy(frames, ys);
		}
		frames.getLast().extraAction=whenDone;
		play(frames);
	}
	
	//AnimationFrames playingAnimations=null;
	synchronized public void play(LinkedList<AnimationFrame> frames){
		//if (playingAnimations != null){
		//	playingAnimations.frames.addAll(playingAnimations.frames.size(),frames);
		//	Log.i("BUNNY","Appending animation sequence");
		//} else
		{
			//Log.i("BUNNY","Starting animation sequence");
			synchronized (parent.thread){
				parent.thread.notify();
			}
			AnimationFrames f = new AnimationFrames();
			f.frames = frames;
//			playingAnimations = f;
			playFixedAnimation(f);
		}
	}
	
	public void eat(){
		if (bunnies.frames            == null || 
		    bunnies.frames.eating[0]  ==null  ||
		    bunnies.frames.eating[1]  ==null  ||
		    bunnies.frames.eating[2]  ==null  ||
		    bunnies.frames.eating[3]  ==null  ||
		    bunnies.frames.eating[4]  ==null){
			return;
		}
		LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		frames.add(new AnimationFrame(0,10, bunnies.frames.eating[0],  260, -1));
		frames.add(new AnimationFrame(0,-10, bunnies.frames.eating[1],  260, -1));
		frames.add(new AnimationFrame(0,-10, bunnies.frames.eating[2],  260, -1));
		frames.add(new AnimationFrame(0,10, bunnies.frames.eating[3],  260, -1));
		frames.add(new AnimationFrame(0,10, bunnies.frames.eating[0],  260, -1));
		frames.add(new AnimationFrame(0,-10, bunnies.frames.eating[1],  260, -1));
		frames.add(new AnimationFrame(0,-10, bunnies.frames.eating[2],  260, -1));
		frames.add(new AnimationFrame(0,10, bunnies.frames.eating[3],  260, -1));
		frames.add(new AnimationFrame(0,0, bunnies.frames.eating[4],  500, -1));
		frames.add(new AnimationFrame(0,0, bunnies.frames.eating[4],  260, -1));
		play(frames);
	}
	
	void onDoneWithFixedAnimation(){
		
	}
	
	protected void playingFixedAnimation(){
		
	}
	
	synchronized void playFixedAnimation(final AnimationFrames frames){
		if (parent.bitmapCache.initDone){
			int time=0;
			
			if (frames.current >= frames.frames.size()){
				return;
			}
			final AnimationFrame frame = frames.frames.get(frames.current);
			/* I go this crash in bus driving so I added the check to see if we were out of bounds and just ignore the animation
			 * might have some weird side effect?
			 * 
			 * 07-09 10:37:42.509: ERROR/AndroidRuntime(17319): Uncaught handler: thread main exiting due to uncaught exception
07-09 10:37:42.779: ERROR/AndroidRuntime(17319): java.lang.IndexOutOfBoundsException
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at java.util.LinkedList.get(LinkedList.java:461)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at com.catglo.dashplayground.CharactarAnimation.playFixedAnimation(CharactarAnimation.java:897)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at com.catglo.dashplayground.CharactarAnimation.play(CharactarAnimation.java:858)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at com.catglo.dashplayground.CharactarAnimation.danceSpinnRight(CharactarAnimation.java:805)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at com.catglo.dashplayground.DrawingView.onFling(DrawingView.java:911)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at android.view.GestureDetector.onTouchEvent(GestureDetector.java:517)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at com.catglo.dashplayground.DrawingView$6.onTouch(DrawingView.java:560)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at android.view.View.dispatchTouchEvent(View.java:3668)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:882)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:882)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:882)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at com.android.internal.policy.impl.PhoneWindow$DecorView.superDispatchTouchEvent(PhoneWindow.java:1712)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at com.android.internal.policy.impl.PhoneWindow.superDispatchTouchEvent(PhoneWindow.java:1202)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at android.app.Activity.dispatchTouchEvent(Activity.java:1987)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at com.android.internal.policy.impl.PhoneWindow$DecorView.dispatchTouchEvent(PhoneWindow.java:1696)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at android.view.ViewRoot.handleMessage(ViewRoot.java:1658)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at android.os.Handler.dispatchMessage(Handler.java:99)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at android.os.Looper.loop(Looper.java:123)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at android.app.ActivityThread.main(ActivityThread.java:4203)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at java.lang.reflect.Method.invokeNative(Native Method)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at java.lang.reflect.Method.invoke(Method.java:521)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:791)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:549)
07-09 10:37:42.779: ERROR/AndroidRuntime(17319):     at dalvik.system.NativeStart.main(Native Method)
*/
			if (frames.current==0)
				time=0;
			else
				time = frames.frames.get(frames.current-1).time;
			
			playingFixedAnimation();
			
			setNextBunnyAnimation(time,"custom",new Runnable(){public void run() {
				synchronized (parent){
					actualX+=frame.X;
					actualY+=frame.Y;
					
					if (actualX<0)
						actualX=0;
					if (actualX>320)
						actualX=320;
					if (actualY<0)
						actualY=0;
					if (actualY>480)
						actualY=480;
					
					
					Matrix matrix = new Matrix();
					if (frame.mirror){
						matrix.preScale(-1.0f,1.0f);
					}
					if (frame.angle!=0){
						matrix.postRotate(frame.angle);
					}
					setBunnyBitmap(frame.image, matrix);
					
					frames.current++;
					
					if (frames.current < frames.frames.size()){
						playFixedAnimation(frames);
					} else {
						//playingAnimations=null;
						frames.current=0;
						addBunnyNotMovingAnimations();
						state=STATE_STIITNG;
						onDoneWithFixedAnimation();
					}
					if (frame.extraAction!=null) {
						frame.extraAction.run();
					}
				}
			}});
		}
	}
	
	public void numberedDance(int number){
		LinkedList<AnimationFrame> frames;
		switch (number){
		case 0: clap(4);
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
		case 7: frames = new LinkedList<AnimationFrame>();
				addHipShakeAndSlide(frames, 0);
				addCartWheelRight(frames);
				addLeftSpin2(frames);
				play(frames);
			break;
		case 8: frames = new LinkedList<AnimationFrame>();
				addCartWheelLeft(frames);
				addCartWheelRight(frames);
				addCartWheelRight(frames);
				addCartWheelLeft(frames);
				play(frames);
			break;
		case 9: rightLeftSpinWhoWho();
			break;
		case 10: jumpAndShake(0, 0, null);
			break;
		}
	}
	
	public int randomDance() {
		final Random r = new Random();
		int retVal = r.nextInt(11);
		numberedDance(retVal);
		return retVal;
	}

}
