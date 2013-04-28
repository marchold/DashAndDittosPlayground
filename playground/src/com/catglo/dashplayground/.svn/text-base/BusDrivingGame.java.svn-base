package com.catglo.dashplayground;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Bitmap.Config;
import android.graphics.Path.Direction;
import android.media.MediaPlayer;
import android.os.Process;
import android.util.Log;
import android.view.MotionEvent;


//items that appear on the side of the road but are not part of the road
class BigPlaneItem {
	public BigPlaneItem(Bitmap image, Point point, int whichFriend) {
		this.point = point;
		this.image = image;
		width = image.getWidth();
		height = image.getHeight();
		this.whichFriend = whichFriend;
	}
	public BigPlaneItem(Bitmap image, Point point) {
		this.point = point;
		this.image = image;
		width = image.getWidth();
		height = image.getHeight();
		this.whichFriend = -1;
	}
	Point point;
	Bitmap image;
	
	//the bitmap width and height so we don'initThread place anything on top or under it
	int width;
	int height;
	int whichFriend; //-1 for not a character, otherwise the friend number
}


public class BusDrivingGame extends DrawingView {
	int whoAmI(){
		return BunnyGames.BUS_DRIVING_GAME;
	}
	int whereAmI(){
		return 2;
	}
	int helpSystemStage=0;
	
	void helpSystem(){
		super.helpSystem();
		if (viewModeZoomed){
			helpSystemStage=1;
			busHelp1.seekTo(0);
			busHelp1.start();
		} else {
			busHelpInPlay.seekTo(0);
			busHelpInPlay.start(); 
		}
	}
	protected void setupExitAreas(){
		addEventRect(new Rect(0,316,54,431), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.LEFT);
			}
        });
		addEventRect(new Rect(259,285,320,400), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.RIGHT);
			}
        });
	}

	private MediaPlayer player;
	private float busX;
	int slowDown=0;
	float scale = 2;
	
	//drive mode - is the bus driving or stopped
	final int DRIVE_MODE_GO=1;
	final int DRIVE_MODE_STOP=2;
	int driveMode=DRIVE_MODE_STOP;
	
	//x,y coordinates of the bus
	Matrix busPosition;
	
	static boolean firstTime=true;

	//Save and free roadBitmaps in onPause and onResume for memory 
	private Bitmap[] roadBitmaps = new Bitmap[8];
	protected void pause(){
		super.pause();
		if (player!=null && player.isPlaying()==true)
			player.pause();
		
	
		//if (bigNote!=null){
		//	bigNote.recycle();
		//	bigNote=null;
		//}
		if (roadBitmaps!=null){
			for (int i = 3; i < roadBitmaps.length; i++){
				if (roadBitmaps[i]!=null){
					roadBitmaps[i].recycle();
					roadBitmaps[i]=null;
				}
			}
		}
	}
	 
	protected void resume(){
		super.resume();
		if (player != null)
			player.start();
		turn=160;
		BitmapFactory.Options ops = new BitmapFactory.Options(); //Workaround for an android garbage collection issue with bitmaps.
		ops.inPurgeable=true;
		ops.inInputShareable=true;
		ops.inPreferredConfig = Bitmap.Config.RGB_565;
		//bigNote = BitmapFactory.decodeResource(getResources(),R.drawable.post_it_big,null);
		roadBitmaps[0] = bitmapCache.road[0];
		roadBitmaps[1] = bitmapCache.road[1];
		roadBitmaps[2] = bitmapCache.road[2];
		roadBitmaps[RoadSegment.WIDE_STREIGHT]  = BitmapFactory.decodeResource(getResources(),R.drawable.h_road_streight_wide,ops);
		roadBitmaps[RoadSegment.WIDE_TO_NARROW] = BitmapFactory.decodeResource(getResources(),R.drawable.h_road_streight_wide_to_narrow,ops);
		roadBitmaps[RoadSegment.NARROW_TO_WIDE] = BitmapFactory.decodeResource(getResources(),R.drawable.h_road_streight_narrow_to_wide,ops);
		roadBitmaps[RoadSegment.WIDE_RIGHT]     = BitmapFactory.decodeResource(getResources(),R.drawable.h_road_right_wide,ops);
		roadBitmaps[RoadSegment.WIDE_LEFT]      = BitmapFactory.decodeResource(getResources(),R.drawable.h_road_left_wide,ops);
		
		resetGame();
	}
	
	void onMenuExit(){
		super.onMenuExit();
	}
	
	void resetGame(){
		roadDefinition.clear();
		planeItems.clear();
		Iterator<AnimationAction> itterator = removeOnMenuExit.iterator();
		while (itterator.hasNext()){
			removeAction(itterator.next());
			itterator.remove();
		}
		drawBunnies=true;
		viewModeZoomed=true;
		whichFriend=0;
		supressBabyActions=false;
		zoomOutStep=0;
		zoomOutBusAngle=90;
		zoomOutBusScale=2;
		zoomOutBusPoint=new Point(ZOOM_OUT_START);
		doorFrame=0;
		bunnyDriving=0;
		processBabyMenu=true;
		bigNoteMatrix=null;
		
		initRoad();
	}

	static final int BUS_X_LOCATION = 60;
	void initRoad(){
		screenPoint = new Point(0,0);
		startPoint = new Point(0,0);
		velocity = new Velocity(0,0.0f);
		startTime=System.currentTimeMillis();
		
		busPosition = new Matrix();
		busPosition.postTranslate(BUS_X_LOCATION, 200);
		
		roadDefinition.add(new RoadSegment(BUS_X_LOCATION-30, 300, RoadSegment.STREIGHT));
		addRoadSegment(RoadSegment.STREIGHT);
		addRoadSegment(RoadSegment.STREIGHT);
		addRoadSegment(RoadSegment.STREIGHT);
		
		
		driveMode=DRIVE_MODE_STOP;
		suspendClick=false;
		if (mainBunny.bunnyImage==null){
			mainBunny.startBunnyAtAttention(200);
			addAction(bunnyAction);
		}
	}
	
	LinkedList<RoadSegment> roadDefinition = new LinkedList<RoadSegment>();
	
	LinkedList<BigPlaneItem> planeItems = new LinkedList<BigPlaneItem>();
	Velocity velocity;
	
	private int currentRoad;

	private float turn;
	private Matrix bigNoteMatrix;
	private Matrix friendNoteMatrix;
	//Bitmap bigNote;
	
	Point screenPoint;
	Point startPoint;
	long startTime;
	private MediaPlayer busHelp1;
	private MediaPlayer busHelp2;
	private MediaPlayer busHelp3;
	private MediaPlayer busHelpInPlay;
	
	RoadSegment addRoadSegment(int type){
		RoadSegment retVal=null;
		int Y = roadDefinition.getLast().y-RoadSegment.ROAD_LENGTH[type];//roadDefinition.getLast().length;
		int X = roadDefinition.getLast().endX;
		retVal = new RoadSegment(X, Y, type);
		roadDefinition.addLast(retVal);
		return retVal;
	}
	
	public BusDrivingGame(Context context,int width, int height, BitmapCache bitmapCache,Point origin) {
		super(context,width,height,bitmapCache,origin);
		
		initRoad();
		busX=30;
		
		busHelp1 = MediaPlayer.create(getContext(), R.raw.bus_1);
		busHelp2 = MediaPlayer.create(getContext(), R.raw.bus_2);
		busHelp3 = MediaPlayer.create(getContext(), R.raw.bus_3);

		busHelpInPlay = MediaPlayer.create(getContext(), R.raw.bus_ingame);

		
		
		addEventRect(new Rect(100,70,180,170), new MenuExecutor(){
			public void execute() {
				bunnyDriving=1;
				startCloseDoor(true);
				drawBunnies=false;
				supressBabyActions=true;
			}
        });
		
		currentRoad = 70;
		//actions.remove(friend.freindAI);
		Rect avoid = new Rect(0,0,320,220);
		for (int i = 0; i < NUMBER_OF_BABIES;i++){
        	baby[i].addAreaToAvoid(avoid);
        }
        friend.addAreaToAvoid(avoid);
	}
	
	
	final static int NOTE_SPIN_INTERVAL=50;
	int noteSpinStage;
	final AnimationAction noteSpinningLoop = new AnimationAction(NOTE_SPIN_INTERVAL,new Runnable(){public void run() {
		noteSpinStage++;
		bigNoteMatrix=new Matrix();
		//bigNoteMatrix.reset();
       //changed above line becuase of this exception
		// java.lang.NullPointerException: 
	//   at com.catglo.dashplayground.BusDrivingGame$1.run(BusDrivingGame.java:222)
	//   at com.catglo.dashplayground.DrawingView.run(DrawingView.java:209)
	//   at com.catglo.dashplayground.DrawingView$DrawingThread.run(DrawingView.java:403)


		
	//	if (bigNote!=null) {
			bigNoteMatrix.postTranslate(-bitmapCache.postIt.getWidth()/2, -bitmapCache.postIt.getHeight()/2);
	
			//06-27 17:47:49.746: WARN/dalvikvm(9445): threadid=53: thread exiting with uncaught exception (group=0x4001da28)
			//06-27 17:47:49.756: ERROR/AndroidRuntime(9445): Uncaught handler: thread Thread-25 exiting due to uncaught exception
			//06-27 17:47:49.796: ERROR/AndroidRuntime(9445): java.lang.NullPointerException
			//06-27 17:47:49.796: ERROR/AndroidRuntime(9445):     at com.catglo.dashplayground.BusDrivingGame$1.run(BusDrivingGame.java:257)
			//06-27 17:47:49.796: ERROR/AndroidRuntime(9445):     at com.catglo.dashplayground.DrawingView.run(DrawingView.java:203)
			//06-27 17:47:49.796: ERROR/AndroidRuntime(9445):     at com.catglo.dashplayground.DrawingView$DrawingThread.run(DrawingView.java:355)
		/*// Tag: AndroidRuntime
// java.lang.NullPointerException: 
//   at com.catglo.dashplayground.BusDrivingGame$1.run(BusDrivingGame.java:223)
//   at com.catglo.dashplayground.DrawingView.run(DrawingView.java:209)
//   at com.catglo.dashplayground.DrawingView$DrawingThread.run(DrawingView.java:403)
*/
			
			bigNoteMatrix.postRotate(20*noteSpinStage);
			bigNoteMatrix.postTranslate(bitmapCache.postIt.getWidth()/2+(noteSpinStage*noteSpinStage*0.09f), bitmapCache.postIt.getHeight()/2+70 +(noteSpinStage*noteSpinStage*0.3f));
		//}
		float baseScale=0.5f;
		float thisScale= 1-((1.0f/50f)*(float)noteSpinStage);
		thisScale += baseScale;
		bigNoteMatrix.postScale(thisScale, thisScale);
		
		noteSpinningLoop.actionTime = System.currentTimeMillis()+NOTE_SPIN_INTERVAL;
		postInvalidate();
		if (noteSpinStage>36) {
			removeAction(noteSpinningLoop);
			bigNoteMatrix=null;
			roadBuildingLoop();
			if (helpSystemStage==2){
				helpSystemStage=0;
				busHelp3.seekTo(0);
				busHelp3.start();
			}
		}
	}});
	
	int whichFriend;
	Bitmap note;
	synchronized void startShowNote(){
		
		babyMenuPoint=null;
		friendNoteMatrix = new Matrix();
		//friendNoteMatrix.   postScale(1.5f, 1.5f);
		RectF src = new RectF(0,0,bitmapCache.friends[whichFriend].waiting.getWidth(),bitmapCache.friends[whichFriend].waiting.getHeight());
		
		RectF dst = new RectF(0,0,bitmapCache.postIt.getWidth(),bitmapCache.postIt.getHeight());
		/*// CRASH: com.catglo.dashplayground (pid 21338)
// Short Msg: java.lang.NullPointerException
// Long Msg: java.lang.NullPointerException
// Build Label: android:tmobile/kila/dream/trout:1.6/DMD64/21415:user/ota-rel-keys,release-keys
// Build Changelist: 21415
// Build Time: 1259893752
// ID: 
// Tag: AndroidRuntime
// java.lang.NullPointerException: 
//   at com.catglo.dashplayground.BusDrivingGame.startShowNote(BusDrivingGame.java:313)
//   at com.catglo.dashplayground.BusDrivingGame$2.run(BusDrivingGame.java:422)
//   at com.catglo.dashplayground.DrawingView.run(DrawingView.java:210)
//   at com.catglo.dashplayground.DrawingView$DrawingThread.run(DrawingView.java:407)


		 * 
		 * 
		 * 07-03 17:23:27.756: ERROR/AndroidRuntime(5147): Uncaught handler: thread Thread-25 exiting due to uncaught exception
		07-03 17:23:27.796: ERROR/AndroidRuntime(5147): java.lang.NullPointerException
		07-03 17:23:27.796: ERROR/AndroidRuntime(5147):     at com.catglo.dashplayground.BusDrivingGame.startShowNote(BusDrivingGame.java:296)
		07-03 17:23:27.796: ERROR/AndroidRuntime(5147):     at com.catglo.dashplayground.BusDrivingGame$2.run(BusDrivingGame.java:395)
		07-03 17:23:27.796: ERROR/AndroidRuntime(5147):     at com.catglo.dashplayground.DrawingView.run(DrawingView.java:210)
		07-03 17:23:27.796: ERROR/AndroidRuntime(5147):     at com.catglo.dashplayground.DrawingView$DrawingThread.run(DrawingView.java:407)
		
		I added synchronized to the function assumoing the problem was the thread had not exited
		*
		*/
		friendNoteMatrix.mapRect(dst, src);
		friendNoteMatrix.postTranslate(40, 40);
		
		if (note!=null)
			note.recycle();
		/* TODO: tried to fix with above line but did not work
		 * 
		 * 06-29 23:23:45.322: ERROR/AndroidRuntime(29178): Uncaught handler: thread Thread-71 exiting due to uncaught exception
06-29 23:23:45.322: ERROR/AndroidRuntime(29178): Uncaught handler: thread Thread-55 exiting due to uncaught exception
06-29 23:23:45.542: ERROR/AndroidRuntime(29178): java.lang.OutOfMemoryError: bitmap size exceeds VM budget
06-29 23:23:45.542: ERROR/AndroidRuntime(29178):     at android.graphics.Bitmap.nativeCopy(Native Method)
06-29 23:23:45.542: ERROR/AndroidRuntime(29178):     at android.graphics.Bitmap.copy(Bitmap.java:311)
06-29 23:23:45.542: ERROR/AndroidRuntime(29178):     at com.catglo.dashplayground.BusDrivingGame.startShowNote(BusDrivingGame.java:302)
06-29 23:23:45.542: ERROR/AndroidRuntime(29178):     at com.catglo.dashplayground.BusDrivingGame$2.run(BusDrivingGame.java:374)
06-29 23:23:45.542: ERROR/AndroidRuntime(29178):     at com.catglo.dashplayground.DrawingView.run(DrawingView.java:204)
06-29 23:23:45.542: ERROR/AndroidRuntime(29178):     at com.catglo.dashplayground.DrawingView$DrawingThread.run(DrawingView.java:365)
06-29 23:23:45.562: ERROR/AndroidRuntime(29178): java.lang.OutOfMemoryError: bitmap size exceeds VM budget
06-29 23:23:45.562: ERROR/AndroidRuntime(29178):     at android.graphics.Bitmap.nativeCreate(Native Method)
06-29 23:23:45.562: ERROR/AndroidRuntime(29178):     at android.graphics.Bitmap.createBitmap(Bitmap.java:464)
06-29 23:23:45.562: ERROR/AndroidRuntime(29178):     at com.catglo.dashplayground.BitmapCache$BunnyFrames.createColorizedBitmap(BitmapCache.java:362)
06-29 23:23:45.562: ERROR/AndroidRuntime(29178):     at com.catglo.dashplayground.BitmapCache$BunnyFrames.<init>(BitmapCache.java:310)
06-29 23:23:45.562: ERROR/AndroidRuntime(29178):     at com.catglo.dashplayground.BitmapCache$BabyBunnyFrames.<init>(BitmapCache.java:407)
06-29 23:23:45.562: ERROR/AndroidRuntime(29178):     at com.catglo.dashplayground.BitmapCache$1.run(BitmapCache.java:569)
06-29 23:23:45.562: ERROR/AndroidRuntime(29178):     at java.lang.Thread.run(Thread.java:1060)

		 */
		note = bitmapCache.postIt.copy(Config.ARGB_8888, true);
		Canvas c = new Canvas(note);
		c.drawBitmap(bitmapCache.friends[whichFriend].waiting,friendNoteMatrix, null);
		
		noteSpinStage=0;
		bigNoteMatrix = new Matrix();
		bigNoteMatrix.postTranslate(0, 70 );

		noteSpinningLoop.actionTime = System.currentTimeMillis()+NOTE_SPIN_INTERVAL;	
		addAction(noteSpinningLoop);
		removeOnMenuExit.add(noteSpinningLoop);
		
	}
	
	protected void drawBackground(Canvas canvas){
		//Matrix m = canvas.getMatrix();
		//canvas.translate(-320/2, -480/2);
		//canvas.scale(scale, scale);
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		
		canvas.drawRect(new Rect(0,0,width,height), p);
		

	}

	static boolean toggleOn=false;
	Point busRight=new Point(70,200);
	Point busLeft =new Point(70,200);
	Point busCenter = new Point(0,0);
	Point busFront = new Point(0,0);
	int roadCenterLeft;
	int roadCenterRight;
	boolean wideRoad=false;
	private int roadTopRight;
	private int roadTopLeft;
	
	boolean viewModeZoomed=true;
	
	final static int ZOOM_OUT_INTERVAL = 50;
	final static int ZOOM_OUT_DONE_AT = 20; 
	final static Point ZOOM_OUT_START = new Point(570, -140);
	final static Point ZOOM_OUT_DONE = new Point(0,0);
	 
	int zoomOutStep=0;
	int zoomOutBusAngle=90;
	int friendAlpha=255;
	int doorFrame=0;
	int bunnyDriving=0;
	
	float zoomOutBusScale=2;
	Point zoomOutBusPoint=new Point(ZOOM_OUT_START);
	
	final AnimationAction zoomOut = new AnimationAction(ZOOM_OUT_INTERVAL,new Runnable(){public void run() {			
		zoomOutBusAngle = 90-(int) (zoomOutStep * (90f/ZOOM_OUT_DONE_AT));
		zoomOutBusScale = 2-((zoomOutStep * (1f/ZOOM_OUT_DONE_AT)));
		float invRatio = (1f/ZOOM_OUT_DONE_AT)*zoomOutStep;
		float ratio = 1-invRatio;
		zoomOutBusPoint.x = (int) ((ZOOM_OUT_START.x*ratio)+(ZOOM_OUT_DONE.x*invRatio));
		zoomOutBusPoint.y = (int) ((ZOOM_OUT_START.y*ratio)+(ZOOM_OUT_DONE.y*invRatio));
		friendAlpha+=15;
		if (friendAlpha>255)
			friendAlpha=255;
		zoomOutStep++;
		if (zoomOutStep>=(ZOOM_OUT_DONE_AT+1)){
			viewModeZoomed=false;
			removeAction(zoomOut);
			suspendClick=true;
			removeAction(bunnyAction);
			mainBunny.bunnyImage=null;
			startShowNote();
			friendAlpha=255;
		}
		zoomOut.actionTime = System.currentTimeMillis()+ZOOM_OUT_INTERVAL;
		postInvalidate();
	}});
	void startZoomOut(){
		if (helpSystemStage==1){
			helpSystemStage++;
			busHelp1.pause();
			busHelp2.seekTo(0);
			busHelp2.start();
		}
		drawBunnies=false;
		zoomOutStep=0;
		startedZoomIn=false;
		zoomOut.actionTime = System.currentTimeMillis()+ZOOM_OUT_INTERVAL;
		zoomOutBusPoint=new Point(ZOOM_OUT_START);
		addAction(zoomOut);//addAction(zoomOut);
		removeOnMenuExit.add(zoomOut);
	}
	 
	final AnimationAction zoomIn = new AnimationAction(ZOOM_OUT_INTERVAL,new Runnable(){public void run() {			
		zoomOutBusAngle = 90-(int) (zoomOutStep * (90f/ZOOM_OUT_DONE_AT));
		zoomOutBusScale = 2-((zoomOutStep * (1f/ZOOM_OUT_DONE_AT)));
		float invRatio = (1f/ZOOM_OUT_DONE_AT)*zoomOutStep;
		float ratio = 1-invRatio;
		zoomOutBusPoint.x = (int) ((ZOOM_OUT_START.x*ratio)+(ZOOM_OUT_DONE.x*invRatio));
		zoomOutBusPoint.y = (int) ((ZOOM_OUT_START.y*ratio)+(ZOOM_OUT_DONE.y*invRatio));
		friendAlpha-=15;
		if (friendAlpha<0)
			friendAlpha=0;
		
		zoomOutStep--;
		if (zoomOutStep<0){
			viewModeZoomed=true;
			removeAction(zoomIn);
			startOpenDoor();
			
			//now kick off the friend boarding the bus animation
			Iterator<Bitmap> walk = bitmapCache.friends[whichFriend].getWalking();
			Bitmap[] step = bitmapCache.friends[whichFriend].stepping;
			LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
			frames.add(new AnimationFrame(-40,180,walk.next(),150,0,true,0));
			frames.add(new AnimationFrame(-20,180,walk.next(),150,0,true,0));
			frames.add(new AnimationFrame(0,180,walk.next(),150,0,true,0));
			frames.add(new AnimationFrame(20,180,walk.next(),150,0,true,0));
			frames.add(new AnimationFrame(40,180,walk.next(),150,0,true,0));
			frames.add(new AnimationFrame(60,180,walk.next(),150,0,true,0));
			frames.add(new AnimationFrame(80,180,walk.next(),150,0,true,0));
			frames.add(new AnimationFrame(100,180,walk.next(),150,0,true,0));
			frames.add(new AnimationFrame(120,180,step[0],200,0,true,0));
			frames.add(new AnimationFrame(140,140,step[1],200,0,true,0));
			frames.add(new AnimationFrame(160,120,step[1],200,0,true,0));
			AnimationFrame f = new AnimationFrame(150,120,step[0],0,0,true,0);
			f.extraAction = new Runnable(){public void run() {
				//Now the friend has boarded the bus, start the next friend note
				whichFriend++;
				if (whichFriend >= bitmapCache.friends.length) {
					startWinningSequence();
				
				} else {
					startCloseDoor(true);
				}
			}};
			frames.add(f);
			
			AnimationFrames animation = new AnimationFrames();
			animation.frames = frames;
			playAnimationFrames(animation);
			
		}
		zoomIn.actionTime = System.currentTimeMillis()+ZOOM_OUT_INTERVAL;
		postInvalidate();
	}});
	synchronized void startZoomIn(){
		if (!containsAction(zoomIn)){
			zoomOutStep=ZOOM_OUT_DONE_AT-1;
			zoomOut.actionTime = System.currentTimeMillis()+ZOOM_OUT_INTERVAL;
			zoomOutBusPoint=new Point(ZOOM_OUT_DONE);
			addAction(zoomIn);//addAction(zoomOut);
			removeOnMenuExit.add(zoomIn);
		}
	}
	
	static final int CLOSE_DOOR_INTERVAL = 100;
	boolean closeDoorLaunchZoomOut=false;
	final AnimationAction closeDoor = new AnimationAction(CLOSE_DOOR_INTERVAL,new Runnable(){public void run() {			
		doorFrame++;
		if (doorFrame>=bitmapCache.busSide.length){
			doorFrame=bitmapCache.busSide.length-1;
			removeAction(closeDoor);
			if (closeDoorLaunchZoomOut)
				startZoomOut();
		}
		closeDoor.actionTime = System.currentTimeMillis()+CLOSE_DOOR_INTERVAL;
		postInvalidate();
	}});
	void startCloseDoor(boolean zoomOut){
		closeDoorLaunchZoomOut=zoomOut;
		if (zoomOut)
			processBabyMenu=false;
		closeDoor.actionTime = System.currentTimeMillis()+CLOSE_DOOR_INTERVAL;
		addAction(closeDoor);
		removeOnMenuExit.add(closeDoor);
	}
	final AnimationAction openDoor = new AnimationAction(CLOSE_DOOR_INTERVAL,new Runnable(){public void run() {			
		doorFrame--;
		if (doorFrame<0){
			doorFrame=0;
			removeAction(openDoor);
		}
		openDoor.actionTime = System.currentTimeMillis()+CLOSE_DOOR_INTERVAL;
		postInvalidate();
	}});
	void startOpenDoor(){
		openDoor.actionTime = System.currentTimeMillis()+CLOSE_DOOR_INTERVAL;
		addAction(openDoor);
		removeOnMenuExit.add(openDoor);
	}
	
	
	void drawBusStop(Canvas canvas){
		if (viewModeZoomed && whichFriend==0){
			Matrix mm = new Matrix();
			mm.postScale(0.5f, 0.5f);
			mm.postRotate(-90);
			mm.postTranslate(110,280);
			
			canvas.drawBitmap(bitmapCache.busStopSign, mm,null);
			mm.postTranslate(50, -100);
			canvas.drawBitmap(bitmapCache.newsStand, mm,null);
		}
	}
	
	//TODO: someday fix! there was a bug which made things end up in the road.
	//      setting this to 0 seems to solve the problem 
	static final int INSERT_POINT=0;//-100;
	protected void drawSurface(Canvas canvas){
		//if (viewModeZoomed){
		Matrix matrix = new Matrix();
		Matrix originalMatrix = canvas.getMatrix();
		matrix.postRotate(zoomOutBusAngle);
		matrix.postScale(zoomOutBusScale, zoomOutBusScale);
		matrix.postTranslate(zoomOutBusPoint.x,zoomOutBusPoint.y);
//		canvas.drawBitmap(bitmapCache.busSideView, matrix, null);
		matrix.postConcat(originalMatrix);
		canvas.setMatrix(matrix);
		
		if (viewModeZoomed && whichFriend==0 && backgroundImage!=null && backgroundImage.isRecycled()==false){
			Matrix m = new Matrix();
			m.postTranslate(-570, 140);
			m.postRotate(-90);
			m.postScale(0.5f, 0.5f);
			m.postTranslate(0, 0);
			canvas.drawBitmap(backgroundImage, m,null);
		}
		
		//	super.drawSurface(canvas);
		//} else 
		{

	
			/**************************************
			 * Draw road and determine where the 
			 * left and right side of the road is
			 */
			roadCenterRight=0; //these will hold the final left and
			roadCenterLeft=0;  //right side values
	
			//Find the road points above and below the bus on both
			//sides of the road.
			Point rightJustAbove = new Point(0,Integer.MIN_VALUE);
			Point rightJustBelow = new Point(0,Integer.MAX_VALUE);
			Point leftJustAbove = new Point(0,Integer.MIN_VALUE);
			Point leftJustBelow = new Point(0,Integer.MAX_VALUE);
			Point rightJustAboveTop = new Point(0,Integer.MIN_VALUE);
			Point rightJustBelowTop = new Point(0,Integer.MAX_VALUE);
			Point leftJustAboveTop = new Point(0,Integer.MIN_VALUE);
			Point leftJustBelowTop = new Point(0,Integer.MAX_VALUE);
			
			
			
			//smallestY is used so we know when we need to generate a new
			//road segment
			int smallestY=Integer.MAX_VALUE; 
		
			ListIterator<RoadSegment> roads = roadDefinition.listIterator();
			while (roads.hasNext()){
				RoadSegment road = roads.next();
				
				//Calculate the real on screen point for the upper left point of the bitmap
				Point realPoint = new Point(road.startX-screenPoint.x,road.y-screenPoint.y);
			    
				if (realPoint.y<smallestY) smallestY=realPoint.y;
			
			    //if (roadBitmapsReady)
			    if (roadBitmaps[road.type]!=null)
			    	canvas.drawBitmap(roadBitmaps[road.type], realPoint.x, realPoint.y, null);
			    //else if (road.type<3)
			    //	canvas.drawBitmap(bitmapCache.road[road.type], realPoint.x, realPoint.y, null);
				
			    //Find the points closest to the middle left side of the road
				roadCenterRight = 0; //where the road is
				roadCenterLeft = 0;
				roadTopRight = 0;    //top of the screen
				roadTopLeft = 0;
				
				
				//For the bus position
				for (int i = 0; i < road.rightSide.linePoints.length; i++){
					int y1 = road.rightSide.linePoints[i].y + realPoint.y;
					if (y1 < 240 && rightJustAbove.y < y1){
						rightJustAbove.y = y1;
						rightJustAbove.x = road.rightSide.linePoints[i].x + realPoint.x;
					}
					if (y1 > 240 && rightJustBelow.y > y1){
						rightJustBelow.y = y1;
						rightJustBelow.x = road.rightSide.linePoints[i].x + realPoint.x; 
					}
				}
				for (int i = 0; i < road.leftSide.linePoints.length; i++){
					int y1 = road.leftSide.linePoints[i].y + realPoint.y;
					if (y1 < 240 && leftJustAbove.y < y1){
						leftJustAbove.y = y1;
						leftJustAbove.x = road.leftSide.linePoints[i].x + realPoint.x;
					}
					if (y1 > 240 && leftJustBelow.y > y1){
						leftJustBelow.y = y1;
						leftJustBelow.x = road.leftSide.linePoints[i].x + realPoint.x; 
					}	
				}
				
				//for the top of the screen position
				for (int i = 0; i < road.rightSide.linePoints.length; i++){
					int y1 = road.rightSide.linePoints[i].y + realPoint.y;
					if (y1 < INSERT_POINT && rightJustAboveTop.y < y1){
						rightJustAboveTop.y = y1;
						rightJustAboveTop.x = road.rightSide.linePoints[i].x + realPoint.x;
					}
					if (y1 > INSERT_POINT && rightJustBelowTop.y > y1){
						rightJustBelowTop.y = y1;
						rightJustBelowTop.x = road.rightSide.linePoints[i].x + realPoint.x; 
					}
				}
				for (int i = 0; i < road.leftSide.linePoints.length; i++){
					int y1 = road.leftSide.linePoints[i].y + realPoint.y;
					if (y1 < INSERT_POINT && leftJustAboveTop.y < y1){
						leftJustAboveTop.y = y1;
						leftJustAboveTop.x = road.leftSide.linePoints[i].x + realPoint.x;
					}
					if (y1 > INSERT_POINT && leftJustBelowTop.y > y1){
						leftJustBelowTop.y = y1;
						leftJustBelowTop.x = road.leftSide.linePoints[i].x + realPoint.x; 
					}
				}
				
				if (showDebug){
					//Draw lines on left side of road
					float[] right = new float[road.rightSide.linePoints.length*4-4];
					int j=0;
					for (int i = 0; i < road.rightSide.linePoints.length-1; i++){
						right[j++] = road.rightSide.linePoints[i].x + realPoint.x;
						right[j++] = road.rightSide.linePoints[i].y + realPoint.y;
						right[j++] = road.rightSide.linePoints[i+1].x + realPoint.x;
						right[j++] = road.rightSide.linePoints[i+1].y + realPoint.y;	
					}
					float[] left = new float[road.leftSide.linePoints.length*4-4];
					j=0;
					for (int i = 0; i < road.leftSide.linePoints.length-1; i++){
						left[j++] = road.leftSide.linePoints[i].x + realPoint.x;
						left[j++] = road.leftSide.linePoints[i].y + realPoint.y;
						left[j++] = road.leftSide.linePoints[i+1].x + realPoint.x;
						left[j++] = road.leftSide.linePoints[i+1].y + realPoint.y;
					}
					Paint p = new Paint();
					p.setStrokeWidth(5);
					p.setColor(Color.RED);
					Paint pp = new Paint();
					pp.setStrokeWidth(5);
					pp.setColor(Color.GRAY);
					
					canvas.drawLines(right, p);
					canvas.drawLines(left, p);
				}
			}
			
			//Use points found to interpolate real point	
			float range = rightJustBelow.y-rightJustAbove.y;
			float rangeScaling = 100/range;
			float rightTopPercent = ((rightJustBelow.y-240)*rangeScaling)/100;
			float rightBotPercent = 1f - rightTopPercent;
			
			roadCenterRight = (int)((rightJustBelow.x*rightBotPercent + rightJustAbove.x*rightTopPercent));
			
			range = leftJustBelow.y-leftJustAbove.y;
			rangeScaling = 100/range;
			float leftTopPercent = ((leftJustBelow.y-240)*rangeScaling)/100;
			float leftBotPercent = 1f - leftTopPercent;
		
			roadCenterLeft = (int)((leftJustBelow.x*leftBotPercent + leftJustAbove.x*leftTopPercent));
			
			range = rightJustBelowTop.y-rightJustAboveTop.y;
			rangeScaling = 100/range;
			rightTopPercent = ((rightJustBelowTop.y)*rangeScaling)/100;
			rightBotPercent = 1f - rightTopPercent;
			
			//TODO: Debug this value graphaclly so we can figure out why friends are in the road
			//   or maybe friends are being added at a diffrent y road offset
			roadTopRight = (int)((rightJustBelowTop.x*rightBotPercent + rightJustAboveTop.x*rightTopPercent));
			
			range = leftJustBelowTop.y-leftJustAboveTop.y;
			rangeScaling = 100/range;
			leftTopPercent = ((leftJustBelowTop.y)*rangeScaling)/100;
			leftBotPercent = 1f - leftTopPercent;
		
			roadTopLeft = (int)((leftJustBelowTop.x*leftBotPercent + leftJustAboveTop.x*leftTopPercent));
			
			
			if (showDebug){
				Paint p = new Paint();
				p.setStrokeWidth(10);
				p.setColor(Color.RED);
				canvas.drawPoint(roadCenterLeft, 240, p);
				p.setColor(Color.WHITE);
				canvas.drawPoint(roadCenterRight,240,p);
			}
			
			roadCenterLeft+=screenPoint.x;
			roadCenterRight+=screenPoint.x;
			
			/****************************************
			 * Add a new road segment if we need one
			 */
			if (smallestY>(INSERT_POINT-20) && driveMode == DRIVE_MODE_GO){
		//		Log.i("BUNNY","ADDING ROAD SEGMENT");
				RoadSegment road=null;
				
				if (r.nextInt(10)==1){
					//switch between wide and narrow
					if (wideRoad){
						road = addRoadSegment(RoadSegment.WIDE_TO_NARROW);
					} else {
						road = addRoadSegment(RoadSegment.NARROW_TO_WIDE);
					}
					wideRoad=!wideRoad;
				} else 
				{
					int type = r.nextInt(3);
					if (roadDefinition.getLast().type == RoadSegment.LEFT && type==RoadSegment.LEFT)
						type=RoadSegment.STREIGHT;
					if (roadDefinition.getLast().type == RoadSegment.RIGHT && type==RoadSegment.RIGHT)
						type=RoadSegment.STREIGHT;
					if (wideRoad){	
						switch (type){
						case RoadSegment.STREIGHT:road = addRoadSegment(RoadSegment.WIDE_STREIGHT);
						break;
						case RoadSegment.RIGHT:road = addRoadSegment(RoadSegment.WIDE_RIGHT);
						break; 
						case RoadSegment.LEFT:road = addRoadSegment(RoadSegment.WIDE_LEFT);
						break;
						}
					} else {
						switch (type){
						case RoadSegment.STREIGHT:road = addRoadSegment(RoadSegment.STREIGHT);
						break;
						case RoadSegment.RIGHT:road = addRoadSegment(RoadSegment.RIGHT);
						break; 
						case RoadSegment.LEFT:road = addRoadSegment(RoadSegment.LEFT);
						break;
						}
					}
				}
			}
			
			/****************************************
			 * Draw BigPlaneItems
			 */
			ListIterator<BigPlaneItem> foreach = planeItems.listIterator();
			int delta = (int)(System.currentTimeMillis() - startTime);
			screenPoint.x = (int)(startPoint.x+(delta*velocity.x));
			screenPoint.y = (int)(startPoint.y+(-delta*velocity.y));
			
			while (foreach.hasNext()){
				BigPlaneItem item = foreach.next();
			    Point realPoint = new Point(item.point.x-screenPoint.x,item.point.y-screenPoint.y);
			    Paint p = new Paint();
			    p.setAlpha(friendAlpha);
			    if (item.image!=null) {
			    	int y = realPoint.y - item.height/2;
			    	canvas.drawBitmap(item.image, realPoint.x, y, p);
			    }
			    /* crashed on G1 then I added a check to see if init was done in the beimapCache before adding any items.
			     * 
			     * 
			     * crahed on 2.1 big screen. added the null pointer check.
			     * 07-03 19:24:57.203: ERROR/AndroidRuntime(565): Uncaught handler: thread main exiting due to uncaught exception
07-03 19:24:57.333: ERROR/AndroidRuntime(565): java.lang.NullPointerException
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.graphics.Canvas.throwIfRecycled(Canvas.java:954)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.graphics.Canvas.drawBitmap(Canvas.java:980)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at com.catglo.dashplayground.BusDrivingGame.drawSurface(BusDrivingGame.java:547)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at com.catglo.dashplayground.DrawingView.onDraw(DrawingView.java:296)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.view.View.draw(View.java:6535)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.view.ViewGroup.drawChild(ViewGroup.java:1531)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.view.ViewGroup.dispatchDraw(ViewGroup.java:1258)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.view.ViewGroup.drawChild(ViewGroup.java:1529)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.view.ViewGroup.dispatchDraw(ViewGroup.java:1258)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.view.ViewGroup.drawChild(ViewGroup.java:1529)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.view.ViewGroup.dispatchDraw(ViewGroup.java:1258)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.view.View.draw(View.java:6538)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.widget.FrameLayout.draw(FrameLayout.java:352)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at com.android.internal.policy.impl.PhoneWindow$DecorView.draw(PhoneWindow.java:1830)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.view.ViewRoot.draw(ViewRoot.java:1349)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.view.ViewRoot.performTraversals(ViewRoot.java:1114)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.view.ViewRoot.handleMessage(ViewRoot.java:1633)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.os.Handler.dispatchMessage(Handler.java:99)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.os.Looper.loop(Looper.java:123)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at android.app.ActivityThread.main(ActivityThread.java:4363)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at java.lang.reflect.Method.invokeNative(Native Method)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at java.lang.reflect.Method.invoke(Method.java:521)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:860)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:618)
07-03 19:24:57.333: ERROR/AndroidRuntime(565):     at dalvik.system.NativeStart.main(Native Method)
*/
			}
			
			/******************************************
			 * Draw bus rotated according to velocity
			 */
			float rotation = (turn/5)-122;
			Matrix m = new Matrix();
			Bitmap bus = null;
			bus=bitmapCache.busTopView;
		
			if (viewModeZoomed){
				m.postTranslate(-bitmapCache.busSide[0].getWidth()/2, -bitmapCache.busSide[0].getHeight()/2);
				m.postRotate(-90);
				m.postTranslate(bitmapCache.busSide[0].getWidth()/2, bitmapCache.busSide[0].getHeight()/2);
				m.postScale(0.5f, 0.5f);
				m.postConcat(busPosition);
				
				Matrix friends = new Matrix();
				friends.set(m);
				friends.postTranslate(10, -5);
				if (whichFriend+bunnyDriving>0)
				    canvas.drawBitmap(bitmapCache.busFriends[whichFriend+bunnyDriving], friends, null);
				else 
					canvas.drawBitmap(bitmapCache.busFriends[0], friends, null);
				canvas.drawBitmap(bitmapCache.busSide[doorFrame],m,null); 
			}else{
				m.postTranslate(-bus.getWidth()/2, -bus.getHeight()/2);
				if (velocity.total() > 0){
					m.postRotate(-(velocity.getAngle()-90));
				}
				m.postTranslate(bus.getWidth()/2, bus.getHeight()/2);
				m.postScale(0.5f, 0.5f);
				m.postConcat(busPosition);
				
				canvas.drawBitmap(bus,m,null); 
			}
			
			/**************************************
			 * Determine a center point for the bus
			 * that we can use to determine if the bus
			 * went off the road
			 */
			Path busP = new Path();
			busP.addRect(0, 0, bitmapCache.busTopView.getWidth(), bitmapCache.busTopView.getHeight(), Direction.CCW);
			busP.transform(m);
			Region r = new Region();
			r.set(0, 0, 1000, 1000);
			r.setPath(busP, r);
			Rect rect = new Rect();
			r.getBounds(rect);
			
			
			Paint pp = new Paint();
			pp.setStrokeWidth(6);
			pp.setColor(Color.RED);
			//canvas.drawPoint(rect.left, 200, pp);
			//pp.setColor(Color.BLUE);
			//canvas.drawPoint(rect.right, 200, pp);
			busLeft.x = rect.left+screenPoint.x;
			busLeft.y = 240+screenPoint.y;
			busRight.x = rect.right+screenPoint.x;
			busRight.y = 240+screenPoint.y;
			
			busCenter.x = ((rect.left+rect.right)/2);
			busCenter.y = ((240));
			
			busFront.x = (int) (busCenter.x + 40 * Math.cos(-velocity.getTheta()));
			busFront.y = (int) (busCenter.y + 40 * Math.sin(-velocity.getTheta()));
			if (showDebug){
				pp.setColor(Color.GREEN);
				pp.setStrokeWidth(14);
				canvas.drawPoint(busCenter.x,busCenter.y,pp);//BS.x-screenPoint.x, BS.y-screenPoint.y, pp);
			}
			
			busCenter.x += screenPoint.x;
			busCenter.y += screenPoint.y;
			busFront.x += screenPoint.x;
			busFront.y += screenPoint.y;
			
			busLeft.x = busFront.x-20;
			busRight.x = busFront.x+20;
			
			if (showDebug){
				pp.setColor(Color.BLUE);
				pp.setStrokeWidth(6);
				canvas.drawLines(debugLine.getPoints(),pp);
				
			
				StreightLine _busLeft = new StreightLine(new Point(busLeft.x-screenPoint.x,busLeft.y-screenPoint.y),new Point(busRight.x-screenPoint.x,busRight.y-screenPoint.y));
				Paint paint = new Paint();
				paint.setColor(Color.GREEN);
				canvas.drawLines(_busLeft.getPoints(), paint);
			}
			//canvas.setMatrix(m);
			
			
			drawBusStop(canvas);
			
			/***************************************
			 * Draw the steering wheel 
			 * and the note on it
			 */
			canvas.setMatrix(originalMatrix);
			rotation+=90;
			if (suspendClick && viewModeZoomed==false){
				m.reset();
				m.postTranslate(-bitmapCache.steeringWheel.getWidth()/2, -bitmapCache.steeringWheel.getHeight()/2);
				m.postRotate(rotation);
				m.postTranslate(bitmapCache.steeringWheel.getWidth()/2+10,bitmapCache.steeringWheel.getHeight()/2+290);
				canvas.drawBitmap(bitmapCache.steeringWheel, m,null);
				
				if (!(bigNoteMatrix!=null && bitmapCache.postIt!=null) && note!=null){
					m.reset();
					m.preScale(0.8f, 0.8f);
					m.postTranslate(-note.getWidth()/2,-note.getHeight()/2);
					m.postRotate(rotation);
					m.postTranslate(note.getWidth()/2+100,note.getHeight()/2+390);
					canvas.drawBitmap(note,m,null);	
				}
			}
			else {
				super.drawSurface(canvas);
			}
			
			//Draw stop/go buttons
			if (suspendClick && viewModeZoomed==false){
				canvas.drawBitmap(bitmapCache.stopButton, stopButton.x,stopButton.y,null);
				canvas.drawBitmap(bitmapCache.goButton,   goButton.x  ,goButton.y  ,null);
			}
			
			//Draw big spinning note
			if (bigNoteMatrix!=null && bitmapCache.postIt!=null){
				canvas.drawBitmap(note, bigNoteMatrix, null);
				//canvas.drawBitmap(bitmapCache.friends[whichFriend][0], friendNoteMatrix, null);
			}
			
			//If we are driving do this as fast as possible
			if (driveMode==DRIVE_MODE_GO)
				postInvalidate();
		}
		
	
		
		canvas.setMatrix(originalMatrix);
		
		
	} 
	
	
	
	static final Point stopButton = new Point(0,250);
	static final Point goButton = new Point(320-80,250);
	
	Point steerDownPoint = new Point();
	int currentSteeringDistance=0;
	boolean steeringRight;
	
	synchronized public boolean onTouchEvent (MotionEvent event){
		super.onTouchEvent(event);
		int X = (int) (event.getRawX()*XScalingRecripricol);
		int Y = (int) (event.getRawY()*YScalingRecripricol);
		
		/*
    	if (event.getAction() == MotionEvent.ACTION_DOWN ||
    	    event.getAction() == MotionEvent.ACTION_MOVE) 
    	{	
    		if (suspendClick){
    			if (driveMode==DRIVE_MODE_GO) {
	    			turn = event.getRawX()*XScalingRecripricol;
	    			postInvalidate();
	    			return true;
    			} 
    		}
    	} else {
    		turn=160;
    	} */
		//If we are driving and this could be a steering wheel action
		if (suspendClick && driveMode==DRIVE_MODE_GO && viewModeZoomed==false){
			if (event.getAction() == MotionEvent.ACTION_DOWN){
				steerDownPoint.x = X;
				steerDownPoint.y = Y;
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE){
				currentSteeringDistance = (int) Math.sqrt((steerDownPoint.x-X)*(steerDownPoint.x-X)+(steerDownPoint.y-Y)*(steerDownPoint.y-Y));
				if (steerDownPoint.x-X>0)
					steeringRight=true;
				else
					steeringRight=false;
			}
			if (event.getAction() == MotionEvent.ACTION_UP){
				currentSteeringDistance=0;
			}
			if (steeringRight){
				turn = 160-currentSteeringDistance;
			} else {
				turn = 160+currentSteeringDistance;
			}
			//Log.i("BUNNY","steer dist ="+currentSteeringDistance);
		}
    	
    	if (event.getAction() == MotionEvent.ACTION_UP && suspendClick && viewModeZoomed==false){
			Rect stop = new Rect(stopButton.x,
					             stopButton.y,
					             stopButton.x+bitmapCache.stopButton.getWidth(),
					             stopButton.y+bitmapCache.stopButton.getHeight());
			
			
			Rect go = new Rect(goButton.x,
					           goButton.y,
					           goButton.x+bitmapCache.goButton.getWidth(),
					           goButton.y+bitmapCache.goButton.getHeight());
			
			
			if (go.contains((int)(event.getRawX()*XScalingRecripricol), (int)(event.getRawY()*YScalingRecripricol)) && driveMode==DRIVE_MODE_STOP){
				if (driveMode != DRIVE_MODE_GO){
					driveMode=DRIVE_MODE_GO;
					startTime = System.currentTimeMillis();
					velocity.y=0.1f;
					distanceTraveledStartTime = startTime;
				} 
			}
			if (go.contains((int)(event.getRawX()*XScalingRecripricol), (int)(event.getRawY()*YScalingRecripricol)) && driveMode==DRIVE_MODE_GO){
				if (velocity.total() < MAX_BUS_VELOCITY){
					int delta = (int)(System.currentTimeMillis() - startTime);
					distanceTraveled += delta * velocity.y;
					startPoint.x = (int)(startPoint.x+(delta*velocity.x));
					startPoint.y = (int)(startPoint.y+(-delta*velocity.y));
					velocity.scale(1.3f);
					Log.i("BUNNY","velocity="+velocity.total());
				}
			}
			if (currentSteeringDistance<5 && stop.contains((int)(event.getRawX()*XScalingRecripricol), (int)(event.getRawY()*YScalingRecripricol)) && driveMode==DRIVE_MODE_GO) {
				driveMode=DRIVE_MODE_STOP;
				
				//Check if our chosen friend is on screen
				if (!startedZoomIn){
					Iterator<BigPlaneItem> itterator = planeItems.iterator();
					while (itterator.hasNext()){
						BigPlaneItem item = itterator.next();
						Point realPoint = new Point(item.point.x-screenPoint.x,item.point.y-screenPoint.y);
						Rect itemRect = new Rect(realPoint.x,realPoint.y,realPoint.x+item.width,realPoint.y+item.height);
						Rect fullScreen = new Rect(0,0,320+175,480);
						
						if (whichFriend == item.whichFriend){
							if (Rect.intersects(fullScreen, itemRect)){
								//it is time to play the friend boarding the bus animation
								startedZoomIn=true;
								viewModeZoomed=true;
								drawBunnies=false;
								startZoomIn();
							}
						}
					}
				}
				
				//keep our direction, but near zero speed
				velocity.setTotal(Float.MIN_VALUE);
			}
    	}
    	
     
    	return true;
	}

	void startFriendBordingBus(){
		
	}
	
	static final int ROAD_SHOW_INTERVAL = 50;
	
	Point BS = new Point();
	
	static final float MIN_BUS_VELOCITY = 0.1f;
	static final float MAX_BUS_VELOCITY = 0.8f;
	
	int counter=0;
	int supressUntilCounter=0;
	private int roadLength=0;
	private int friendVisible=-1;
	boolean startedZoomIn=false;
	int distanceTraveled = 0;
	long distanceTraveledStartTime = System.currentTimeMillis();
	int distanceCharactar=0;
	int distanceItem=0;
	static final int CHARACTAR_SPACING = 150;
	static final int ITEM_SPACING = 173;

	
	final Random r = new Random();	
	StreightLine debugLine = new StreightLine(new Point(0,0), new Point(1,1));
	final AnimationAction showRoadSegment = new AnimationAction(ROAD_SHOW_INTERVAL,new Runnable(){public void run() {			
		synchronized (BusDrivingGame.this) {
			if (driveMode==DRIVE_MODE_GO){
				
				if (bitmapCache.initDone){
					//Log.i("BUNNY","DISTANCE = "+distanceTraveled+" , "+distanceCharactar);
					
					if (distanceCharactar < distanceTraveled){
						distanceCharactar = distanceTraveled+CHARACTAR_SPACING;
						int x;
						x = roadTopRight+40;
						
						int whichFriend = r.nextInt(bitmapCache.friends.length);
						BigPlaneItem item = new BigPlaneItem(bitmapCache.friends[whichFriend].waiting,
								                             new Point(screenPoint.x+x,screenPoint.y+INSERT_POINT),whichFriend);
						planeItems.add(item);
					}
					
					if (distanceItem < distanceTraveled){
						distanceItem = (int) (distanceTraveled+ITEM_SPACING);
						int which = r.nextInt(bitmapCache.treasures.length);
						int x;
						if (r.nextBoolean()){
							x = roadTopRight+40+r.nextInt(50);
						} else {
							x = roadTopLeft-r.nextInt(80)-50 - bitmapCache.treasures[which].getWidth();
						}
						BigPlaneItem item = new BigPlaneItem(bitmapCache.treasures[which],
								                             new Point(screenPoint.x+x,screenPoint.y+INSERT_POINT));
						planeItems.add(item);
					}
					
					
					/*++counter;
					if (counter>supressUntilCounter) {
						//Pick a spot on the side of the road for the object
						
						
						//TODO:Fix a problem where we get less stuff on the side of the road
						//     when we are traveling faster.
						//Random 1 out of 10 place a friend
						if (r.nextInt(10)==1){  
							
							supressUntilCounter=counter+8;
							
						} else if (r.nextInt(10)==1){//Random 1 out of 10 place an object
						
							supressUntilCounter=counter+8;
							int which = r.nextInt(bitmapCache.treasures.length);
							BigPlaneItem item = new BigPlaneItem(bitmapCache.treasures[which],
									                             new Point(screenPoint.x+x,screenPoint.y-80));
							planeItems.add(item);
						}
					}*/
					
					int delta = (int)(System.currentTimeMillis() - startTime);
					screenPoint.x = (int)(startPoint.x+(delta*velocity.x));
					screenPoint.y = (int)(startPoint.y+(-delta*velocity.y));
					
					//Determine if any bigPlaneItems are under the bus
					Iterator<BigPlaneItem> itterator = planeItems.iterator();
					while (itterator.hasNext()){
						BigPlaneItem item = itterator.next();
						Point realPoint = new Point(item.point.x-screenPoint.x,item.point.y-screenPoint.y);
						Rect itemRect = new Rect(realPoint.x,realPoint.y,realPoint.x+item.width,realPoint.y+item.height);
						Rect busArea = new Rect(BUS_X_LOCATION+8,225,BUS_X_LOCATION+12,255);
						if (Rect.intersects(itemRect, busArea)){
							item.image = bitmapCache.carrots;
						}
					}
					
					//Determine if we are about to hit something
					// this code worked but I decided not to spend time animating characters running out of the way
					//Point busTargetCenter = new Point((int)(busCenter.x + 120 * Math.cos(-velocity.getTheta())),
					//					 			    (int)(busCenter.y + 120 * Math.sin(-velocity.getTheta())));
		
				}
				
				//Adjust the bus velocity based on sides of the road
				StreightLine bus = new StreightLine(busLeft,busRight);
				float rotation = (turn/5)-122;
				rotation+=90;
				if (busLeft.x < roadCenterLeft){
					//turn the bus to the right
					if (velocity.getAngle() > 45)
						if (velocity.getAngle() < 90 ) rotation = 20;
						else  rotation = 40;
					if (velocity.total() > MIN_BUS_VELOCITY)
						velocity.scale(0.95f);
					
				} else
				if (busRight.x > roadCenterRight){
					if (velocity.getAngle() < 135)
						if (velocity.getAngle() > 90) rotation = -20;
						else rotation = -40;
					if (velocity.total() > MIN_BUS_VELOCITY)
						velocity.scale(0.95f);
				} else{
					//increase velocity
					if (velocity.total() < MAX_BUS_VELOCITY){
						velocity.scale(1.005f);
					
					}
				}
				
				
				//Clip velocity so we can not go backwards
				if (velocity.getAngle() < 20){
					velocity.setAngle(20);
				}
				if (velocity.getAngle() > 160){
					velocity.setAngle(160);
				}
				
				int delta = (int)(System.currentTimeMillis() - startTime);
				distanceTraveled += delta * velocity.y;
				startPoint.x = (int)(startPoint.x+(delta*velocity.x));
				startPoint.y = (int)(startPoint.y+(-delta*velocity.y));
				float turnRatio=rotation;
				velocity.turn(-turnRatio/4);
				startTime = System.currentTimeMillis();
					 
				
				postInvalidate();
			} 
			showRoadSegment.actionTime=ROAD_SHOW_INTERVAL+System.currentTimeMillis();
		}
	}});

	private void roadBuildingLoop() {
		showRoadSegment.name="CARROT POPING OUT ANIMATION";
		showRoadSegment.actionTime=ROAD_SHOW_INTERVAL+System.currentTimeMillis();
		addAction(showRoadSegment);
		removeOnMenuExit.add(showRoadSegment);
	}
	
	static final Point BUS_DOOR_POINT = new Point(160,120);
	static final Point EXIT_POINT = new Point(400,400);
	static final int FRIEND_DANCE_X_START=80;
	void startWinningSequence(){
		resetGame(); 
		launchConfetti(40);
		mainBunny.sendBunnyTo(50, 200);
		mainBunny.onDoneMovingHandler.add(new Runnable(){public void run(){
			mainBunny.randomDance();
		}});
		friend.sendBunnyTo(200, 200);
		friend.onDoneMovingHandler.add(new Runnable(){public void run(){
			friend.randomDance();
		}});
		
		
		Point thisFriendPoint = new Point(FRIEND_DANCE_X_START,190);
		
		for (int i = bitmapCache.friends.length-1; i >= 0; i--){
			LinkedList<AnimationFrame> anim = new LinkedList<AnimationFrame>();
			Iterator<Bitmap> walk = bitmapCache.friends[i].getWalking();
			Bitmap[] step = bitmapCache.friends[i].stepping;
			Point cur = new Point(BUS_DOOR_POINT);
			int xDelta=0;
			int distance = Integer.MAX_VALUE;
			do {
				float xDiff = (thisFriendPoint.x-BUS_DOOR_POINT.x);
				float yDiff = (thisFriendPoint.y-BUS_DOOR_POINT.y);
				float theta = (float) Math.atan2(yDiff,xDiff);
				xDelta = (int) (20 * Math.cos( theta ));
				Bitmap image = walk.next();
				if (xDelta<0){
					anim.add(new AnimationFrame(cur.x-image.getWidth(),cur.y,image,150,0,false,0));
				} else {
					anim.add(new AnimationFrame(cur.x,cur.y,image,150,0,true,0));
				}
				cur.x += xDelta; 
				cur.y += (int) (20 * Math.sin( theta ));	
				distance = (int) Math.sqrt((cur.x-thisFriendPoint.x)*(cur.x-thisFriendPoint.x)
			 		                      +(cur.y-thisFriendPoint.y)*(cur.y-thisFriendPoint.y));
			}while (distance > 20);
			
			thisFriendPoint.x += 90;
			if (thisFriendPoint.x>380){
				thisFriendPoint.x=FRIEND_DANCE_X_START;
				thisFriendPoint.y+=80;
			}
			
			Iterator<Bitmap> cheer = bitmapCache.friends[i].getHurray();
			for (int j = 0; j < i*5; j++){
				Bitmap image = cheer.next();
				if (xDelta<0){
					anim.add(new AnimationFrame(cur.x-image.getWidth(),cur.y,image,150,0,false,0));
				} else {
					anim.add(new AnimationFrame(cur.x,cur.y,image,150,0,true,0));
				}
			}
			
			walk = bitmapCache.friends[i].getWalking();
			distance = Integer.MAX_VALUE;
			do {
				float xDiff = (EXIT_POINT.x-cur.x);
				float yDiff = (EXIT_POINT.y-cur.y);
				float theta = (float) Math.atan2(yDiff,xDiff);
				xDelta = (int) (20 * Math.cos( theta ));
				Bitmap image = walk.next();
				if (xDelta<0){
					anim.add(new AnimationFrame(cur.x-image.getWidth(),cur.y,image,150,0,false,0));
				} else {
					anim.add(new AnimationFrame(cur.x,cur.y,image,150,0,true,0));
				}
				cur.x += xDelta; 
				cur.y += (int) (20 * Math.sin( theta ));	
				distance = (int) Math.sqrt(xDiff*xDiff+yDiff*yDiff);
			}while (distance > 20);
	
			
			AnimationFrames animation = new AnimationFrames();
			animation.frames = anim;
			playAnimationFrames(animation);
		}
	}
	
	
}