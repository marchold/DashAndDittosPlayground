package com.catglo.dashplayground;

import java.util.LinkedList;



import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;

public class MinitureGolf extends DrawingView implements OnGestureListener {
	int whoAmI(){
		return BunnyGames.MINI_GOLF;
	}
	int whereAmI(){
		return 3;
	}
	MediaPlayer mp;
	void helpSystem(){
		super.helpSystem();
		if (mp!=null){
			mp.start(); 
		}
	}
	protected void setupExitAreas(){
		addEventRect(new Rect(264,365,320,480), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.RIGHT);
			}
        });
		addEventRect(new Rect(0,295,68,439), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.LEFT);
			}
        });
	}

	
	boolean drawBall=true;
	
	//static final int GAME_MODE_NORMAL=1;
	//static final int GAME_MODE_THROWING=2;
 
	private MediaPlayer player;
	
	Point ballStartPoint=new Point(196,403);
	Point ballHolePoint=new Point(75,95);
	private int ballX;
	private int ballY;
	
	void onMenuExit(){
		super.onMenuExit();
		resetGame();
	} 
	
	int numberOfSwings;
	void resetGame(){
		//drawBall=true;
		if (ballStartPoint!=null){
			ballX = ballStartPoint.x;
			ballY = ballStartPoint.y;
		}
		drawBall=true;
		numberOfSwings=0;
	}
	
	protected void resume(){
		super.resume();
		resetGame();
		
	
	}

	
	//private int gameMode;
	private float velocityX;
	private float velocityY;
	private long tossTime;
	private int ballStartX;
	private int ballStartY;
	static final float TOSS_TIME_DAMP=0.4f;//0.4f;
	static final int TOSS_INTERVAL=30;
	private long timeStartTossStage;
	private GestureDetector gestureDector;
	private OnTouchListener gestureListener;

	final Point[] polygon = {
			new Point(33,80),
            new Point(96,58),
            new Point(124,127),
            new Point(226,178),
            new Point(198,300),
            new Point(237,410),
            new Point(173,431),
            new Point(127,298),
            new Point(147,217),
            new Point(65,173)
	};
	
	
	
	float[] lines;
	private Matrix ballMatrix;
	private int ballInCup;
	private int ballTap;

	private int shortClap;

	private int femaleCheer;

	private int familyClap;
	
	static final int WINDMILL_X = 170;
	static final int WINDMILL_Y = 10;
	
	public MinitureGolf(Context context,int width, int height, BitmapCache bitmapCache,Point origin) {
		super(context,width,height,bitmapCache,origin);

		startFlagAnimation();
		mp = MediaPlayer.create(getContext(), R.raw.golf_help);
		
		
		Rect[] avoid = {
				new Rect(WINDMILL_X,WINDMILL_Y-10,WINDMILL_X+350, bitmapCache.windmillTower.getHeight()+20),
				new Rect(40,60,150,130),
				new Rect(55,110,175,193),
				new Rect(166,113,246,311),
				new Rect(90,178,181,220),
				new Rect(130,210,173,430),
				new Rect(165,300,246,436)};
	   
	    for (int j = 0; j < avoid.length; j++){
			for (int i = 0; i < NUMBER_OF_BABIES;i++){
	        	baby[i].addAreaToAvoid(avoid[j]);
	        }
	        friend.addAreaToAvoid(avoid[j]);
	    }
		
		
		
		lines = new float[polygon.length*4];
		int li=0;
		for (int i = 0; i < polygon.length-1;i++){
			lines[li++] = polygon[i].x;
			lines[li++] = polygon[i].y;
			lines[li++] = polygon[i+1].x;
			lines[li++] = polygon[i+1].y;
		}
		lines[li++] = polygon[polygon.length-1].x;
		lines[li++] = polygon[polygon.length-1].y;
		lines[li++] = polygon[0].x;
		lines[li++] = polygon[0].y;
	
		//gameMode = GAME_MODE_THROWING;
		
	
		gestureDector = new GestureDetector(this);
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(final View v, final MotionEvent event) {
				if (gestureDector.onTouchEvent(event)) return true;
				return false;
			}
		};
		setOnTouchListener(gestureListener);

		resetGame();
		
		ballMatrix = new Matrix();
		ballMatrix.postTranslate(-bitmapCache.golfBall.getWidth()/2, -bitmapCache.golfBall.getHeight()/2);
		
		windmill.actionTime=WINDMILL_INTERVAL+System.currentTimeMillis();
		addAction(windmill);
	
	}
	Matrix windmillBladeMatrix = new Matrix();
	float windmillBladeAngle=0;
	
	static final int WINDMILL_INTERVAL=100;
	final AnimationAction windmill = new AnimationAction(WINDMILL_INTERVAL,new Runnable(){public void run() {			
		synchronized (MinitureGolf.this) {
			windmillBladeAngle-=2;
			windmillBladeMatrix.reset();
			windmillBladeMatrix.postTranslate(-bitmapCache.windmillBlades.getWidth()/2, -bitmapCache.windmillBlades.getHeight()/2);
			windmillBladeMatrix.postRotate(windmillBladeAngle);
			windmillBladeMatrix.postTranslate(bitmapCache.windmillBlades.getWidth()/2, bitmapCache.windmillBlades.getHeight()/2);
	
			
			postInvalidate();
			windmill.actionTime=WINDMILL_INTERVAL+System.currentTimeMillis();
			
		}
	}});
	
	Point oldintersection;
	Point oldoldintersection;
	boolean intersect=false;
	Point intersection=null;
	Point _intersection=null;
	protected void drawSurface(Canvas canvas){
		super.drawSurface(canvas);
	
		
		if (showDebug)
		{
			Path p = new Path();
			Paint pp = new Paint();
			pp.setColor(Color.RED);
		    pp.setStrokeWidth(3);
			
			canvas.drawLines(lines, pp);
			
			pp.setColor(Color.WHITE);
			
			if (intersect)
				pp.setColor(Color.GREEN);
			canvas.drawLines(ballLine, pp);
			
			pp.setColor(Color.YELLOW);
			canvas.drawLines(lastBallLine,pp);
			
			pp.setColor(Color.CYAN);
			canvas.drawLines(_lastBallLine,pp);
		
			
			if (intersection!=null){
				pp.setColor(Color.BLUE);
				canvas.drawPoint(ballStartX, ballStartY, pp);
				canvas.drawPoint(ballX, ballY, pp);
			
				pp.setColor(Color.YELLOW);
				pp.setStrokeWidth(10);
				canvas.drawPoint(intersection.x, intersection.y, pp);		
			}
			
			pp.setStrokeWidth(10);
			if (oldintersection!=null){
				pp.setColor(Color.GREEN);
				canvas.drawPoint(oldintersection.x, oldintersection.y, pp);
			}
			
			if (oldoldintersection!=null){
				pp.setColor(Color.DKGRAY);
				canvas.drawPoint(oldoldintersection.x, oldoldintersection.y, pp);
			}
			
			pp.setColor(Color.WHITE);
			canvas.drawCircle(ballHolePoint.x, ballHolePoint.y, 10, pp);
			
		}
		
		Matrix b = new Matrix();
		b.set(ballMatrix);
		b.postTranslate(ballX, ballY);
		
		if (drawBall)
			canvas.drawBitmap(bitmapCache.golfBall, ballX, ballY, null);
		
		canvas.drawBitmap(bitmapCache.windmillTower, WINDMILL_X,WINDMILL_Y, null);
		b.set(windmillBladeMatrix);
		b.postTranslate(WINDMILL_X-32, WINDMILL_Y-3);
		canvas.drawBitmap(bitmapCache.windmillBlades, b, null);
		
		if (numberOfSwings>0 && bitmapCache.initDone){
			Matrix matrix=new Matrix();
			matrix.postScale(.15f, .15f);
			matrix.postTranslate(10, 10);
			if (numberOfSwings<10){
				canvas.drawBitmap(bitmapCache.bigNumber[numberOfSwings], matrix, null);
			} else {
				String s = new String(""+numberOfSwings);
				for (int i = 0; i < s.length(); i++){
					Integer n =  new Integer(""+s.charAt(i));
					canvas.drawBitmap(bitmapCache.bigNumber[n], matrix, null);
					matrix.postTranslate((bitmapCache.bigNumber[n].getWidth()*0.15f)+15, 0);
				}
			}
		}
		
	}
	//List of reasons ball goes outside course
	// 1, ball travels parellel to bumper 8 then bounces off backside of bumper 7
	//
	
	
	//Tasks:
	//  1, Determine when the ball has touched a bumper wall
	//      - ? Use color analysis
	//        ? Use math and coordinates
	//  2, Calculate bounce velocities
	//      - For a vertical wall just invert Y velocity
	//      - For a horizontal wall just invert X velocity
	//      - For a 45degre X=Y and Y=X
	//      - For 1degree off horizontal X=(44/45)X+(1/45)Y  Y=(44/45)Y+(1/45)X
	//
	//  Since what I really need to know is the angle maybe the best way to do the golf course 
	//  would be as a vector of (angle,distance), I can calculate the angle from the points on a
	//  coordinate system.
	
	
	
	//Map 
	//                  0     
	//                         12,6
	//          5,9----------     \
	//           \                     \     1
	//            \                        \
	//          9   \                        15,14
	//            10,21                        \     2
	//                 \                          \
	//                 8    \                       28,21
	//                       19,26                /
	//                7     /                    /      3
	//                  /                   /
	//                16,36               24,35
	//                  \                    \
	//                   \                    \        4
	//              6     \                    \
	//                     \ ------------------- 30,50
	//                      22,52       5
	
	
	float[] ballLine = new float[4];

	float[] lastBallLine = new float[4];
	float[] _lastBallLine = new float[4];

	float getHforABCD(Point A, Point B, Point C, Point D){
		
		Point E = new Point(B.x-A.x, B.y-A.y );
		Point F = new Point(D.x-C.x, D.y-C.y );
		Point P = new Point(-E.y,E.x);//P = ( -Ey, Ex )
		//h = ( (A-C) * P ) / ( F * P )
		//Point Q = new Point()
		
		
		//F*P (Fx*Px)+(Fy*Py)
		float FxP = (F.x*P.x)+(F.y*P.y);
		if (FxP==0){
			//The lines are parallel
		} else {
			Point AmC = new Point(A.x-C.x,A.y-C.y);
			float AmCxP = (AmC.x*P.x)+(AmC.y*P.y);
			float h = AmCxP/FxP;
			//If h is exactly 0 or 1 the lines touch at an end-point. You can consider this an "intersection" or not as you see fit.
			//Specifically, h is how much you have to multiply the length of the line in order to exactly touch the other line.
			//Therefore, If h<0, it means the rectangle line is "behind" the given line (with "direction" being "from A to B"), and if h>1 the rectangle line is "in front" of the given line.
			if (h>=0 && h <=1){
				_intersection = new Point((int)(C.x+F.x*h),(int)(C.y+F.y*h));//C + F*h.
				return h;
				
			} 
		}
		return Float.MAX_VALUE;
	}
	
    public boolean PointInPolygon(PointF point)  
    {  
        int j = polygon.length - 1;  
        boolean oddNodes = false;  
  
        for (int i = 0; i < polygon.length; i++)  
        {  
            if (polygon[i].y < point.y && polygon[j].y >= point.y ||  
                polygon[j].y < point.y && polygon[i].y >= point.y)  
            {  
                if (polygon[i].x +  
                    (point.y - polygon[i].y)/(polygon[j].y - polygon[i].y)*(polygon[j].x - polygon[i].x) < point.x)  
                {  
                    oddNodes = !oddNodes;  
                }  
            }  
            j = i;  
        }  
  
        return oddNodes;  
    } 
    
    public boolean pointOnLine(Point lineA, Point lineB, Point point){
    	boolean intersect=false;
	
    	Point C = lineA;
		Point D = lineB;
			
		float g = getHforABCD(C,D,point,point);
		float h = getHforABCD(point,point,C,D);
		if (h >=0 && h <= 1 && g >=0 && g <=1){
			intersect=true;
		}	
		return intersect;
    }
    
    boolean findInsideOutsidePoints(Point inside, Point outside, Point origin, float velocityX, float velocityY){
    	
		
		//Find points on the original trajectory which are inside 
		//and outside the polygon, and not on the bumper line.
		Point pointA = new Point(origin);
		Point pointB = new Point(origin);
		
		float d = 0;
		PointF temp = new PointF(pointA);
		PointF temp2 = new PointF(pointB);
		boolean p1 = PointInPolygon(temp);
		boolean p2 = PointInPolygon(temp2);
		
		int endlessLoopCounter=0;
		while (p1==p2){
			d+=0.0001f;
			temp = new PointF((pointA.x+velocityX*d),(pointA.y+velocityY*d));
			temp2 = new PointF((pointB.x+velocityX*(-d)),(pointB.y+velocityY*(-d)));
			p1 = PointInPolygon(temp);
			p2 = PointInPolygon(temp2);
			endlessLoopCounter++;
			if (endlessLoopCounter>1000){
				Log.i("BUNNY","ENDLESS LOOP BREAKOUT");
				throw new IllegalStateException();
			}
		}
		
		if (p2){
			inside.x = (int) temp2.x;
			inside.y = (int) temp2.y;
			outside.x = (int) temp.x;
			outside.y = (int) temp.y;
			return true;
		} else {
			inside.x = (int) temp.x;
			inside.y = (int) temp.y;
			outside.x = (int) temp2.x;
			outside.y = (int) temp2.y;
			return false;
		}
    }
    
    void outOfBounds(){
    	resetGame();
    }
    
    int ballOutsideCount=0;
	int lastBunper=-1;
	final AnimationAction tossing = new AnimationAction(TOSS_INTERVAL,new Runnable(){public void run() {			
		synchronized (MinitureGolf.this) {
			Point bumperA=null;
			Point bumperB=null;
		
			int interCnt=0;
			
			int delta = (int)(System.currentTimeMillis()-timeStartTossStage);
			final float velocityScalingX = 0.0003f;
			final float velocityScalingY = 0.0003f;
			
			int lastBallX = ballX;
			int lastBallY = ballY;
			ballX = (int) (ballStartX + (delta * velocityX * velocityScalingX));
			ballY = (int) (ballStartY + (delta * velocityY * velocityScalingY));
			
			float distanceFromHole = (float)Math.sqrt((ballHolePoint.x-ballX)*(ballHolePoint.x-ballX)+
              	  (ballHolePoint.y-ballY)*(ballHolePoint.y-ballY));

			float ballSpeed = Math.abs(velocityX*velocityScalingX)+Math.abs(velocityY*velocityScalingY); 
		//	Log.i("BUNNY","distance from hole="+distanceFromHole+" , "+ballSpeed);
			if (distanceFromHole<30){
				if (distanceFromHole*ballSpeed < 7){
					ballX=ballHolePoint.x;
					ballY=ballHolePoint.y;
					downTheHole();
					removeAction(tossing);
					return;
				}
			}
			
			
			boolean ballOutside=false;
			
			if (PointInPolygon(new PointF(ballX,ballY)) == false){
				Log.i("BUNNY","BALL OUTSIDE!!!");
				ballOutside=true;
				ballOutsideCount++;
			} else {
				ballOutsideCount=0;
			}
			
			if (ballOutsideCount>6){
				outOfBounds();
			}
			
			//float distanceFromStart = (float)Math.sqrt((ballStartX-ballX)*(ballStartX-ballX)+
             //       (ballStartY-ballY)+(ballStartY-ballY));

			//if (distanceFromStart>50)
			{
				
			
				
			
			//I need to see if the line between (ballX,ballY) and (ballStartX,ballStartY)
			//crosses any of the lines defined by the golf course polygon
			Point A = new Point(ballX,ballY);
			Point B = new Point(ballStartX,ballStartY);
			ballLine[0] = A.x;
			ballLine[1] = A.y;
			ballLine[2] = B.x;
			ballLine[3] = B.y;
			int lineWhichIntersects=0;
			
			intersect=false;
			for (int i = 0; i < polygon.length; i++)
			{
				if (i != lastBunper) 
				{
					Point C = polygon[i];
					Point D;
					if (i == polygon.length-1) {
						D = polygon[0];
					} else {
						D = polygon[i+1];
					}	
					float g = getHforABCD(C,D,A,B);
					float h = getHforABCD(A,B,C,D);
					if (h >=0 && h <= 1 && g >=0 && g <=1){
						if (intersect){
							//if we intersect multiple bumpers then we must only bounce off the closeset one
							float distNew = (float)Math.sqrt((_intersection.x-ballStartX)*(_intersection.x-ballStartX)+
									                         (_intersection.y-ballStartY)*(_intersection.y-ballStartY));
							float distOld = (float)Math.sqrt((intersection.x-ballStartX)*(intersection.x-ballStartX)+
									                         (intersection.y-ballStartY)*(intersection.y-ballStartY));
							
							if (distOld > distNew){
								Point inside = new Point();
								Point outside = new Point();
							
								try {
									if (findInsideOutsidePoints(inside, outside, _intersection, velocityX, velocityY)){	
									
										intersection=_intersection;
									    lineWhichIntersects = i;
										Log.i("BUNNY","Bumped bumber "+lineWhichIntersects);
										bumperA = C;
										bumperB = D;
										interCnt++;
									}
								}catch (IllegalStateException e){
									outOfBounds();
									removeAction(tossing);
									return;
								}
							}
						
						} else {
							Point inside = new Point();
							Point outside = new Point();
						
							try {
								if (findInsideOutsidePoints(inside, outside, _intersection, velocityX, velocityY)){
									intersect=true;
									intersection=_intersection;
								    lineWhichIntersects = i;
									Log.i("BUNNY","Bumped bumber "+lineWhichIntersects);
									bumperA = C;
									bumperB = D;
									interCnt++;
								} 
							}catch (IllegalStateException e){
									outOfBounds();
									removeAction(tossing);
									return;
								}
						}
						
					}	
				}
			}
			
			//if (interCnt>1){
			//	Log.i("BUNNY","multiple intersectiong bumpers "+interCnt);
			//}
			
			
		//	if (intersect && lastBunper==lineWhichIntersects){
		//		Log.i("BUNNY","Supress bumber "+lineWhichIntersects+"   ball="+ballX+","+ballY);
		//	} else {
		//		Log.i("BUNNY","                   ball="+ballX+","+ballY);
		//	}
			
			if (intersect){
				Point inside=new Point();
				Point outside=new Point();
				
				boolean ignoreBumper=false;
				try {
					if (findInsideOutsidePoints(inside, outside, intersection, velocityX, velocityY)){
						ignoreBumper=false;
					} else {
						ignoreBumper=true;
					}
				} catch (IllegalStateException e){
					//outOfBounds();
					return;
				}
			
				
			//	if (inside!=null && outside!=null){
			//		Log.i("BUNNY","inside ="+inside.toString()+"  outside="+outside.toString());
			//	} else {
			//		Log.i("BUNNY","It did not WORK!!!!!");	
			//	}
				
				
				if (!ignoreBumper)
				{
					
					_lastBallLine[0] = lastBallLine[0];
					_lastBallLine[1] = lastBallLine[1];
					_lastBallLine[2] = lastBallLine[2];
					_lastBallLine[3] = lastBallLine[3];
						
					
				lastBallLine[0] = ballLine[0];
				lastBallLine[1] = ballLine[1];
				lastBallLine[2] = ballLine[2];
				lastBallLine[3] = ballLine[3];
				
				oldoldintersection= oldintersection;
				oldintersection = intersection;
				
					first=true;
					
					lastBunper=lineWhichIntersects;
					
					float ff = (ballX-intersection.x)*(ballX-intersection.x)+ (ballY-intersection.y)*(ballY-intersection.y);
					float distancePastBumper = (float)Math.sqrt(ff);
				
					//velocityX=-velocityX;
				//	velocityY=-velocityY;
					
				
			//		Log.i("BUNNY","distancePastBumper="+distancePastBumper);
					
					
					Point C = new Point(polygon[lineWhichIntersects].x,polygon[lineWhichIntersects].y);
					Point D;
					if (lineWhichIntersects>=polygon.length-2){
						D = new Point(polygon[0].x,polygon[0].y);
					} else {
						D = new Point(polygon[lineWhichIntersects+1].x,polygon[lineWhichIntersects+1].y);
					}
					
					float xDiff = D.x-C.x; 
			        float yDiff = D.y-C.y;
			        float distance = (float) Math.sqrt(xDiff*xDiff+yDiff*yDiff);
			        float theta = (float) Math.atan2(yDiff,xDiff);
			        float angle=(float) ((theta/Math.PI)*180f);
			        
			        
			        /*Public Function getAngleBetweenLinesVect(commonX As Single, commonY As Single, X1 As Single, 
	        		Y1 As Single, Y2 As Single, X2 As Single) As Double
	        		'check if both the lines are the same, if they are exit the function
	        		If (commonY = Y1 And commonX = X1) Or (commonY = Y2 And commonX = X2) Or 
	        		(Y1 = Y2 And X1 = X2) Then
	
	
	        		Else
	        		Dim differenceX1 As Double, differenceX2 As Double, differenceY1 As Double, differenceY2 As Double
	        		'set the variables 
	        		differenceX1 = commonX - X1
	        		differenceY1 = commonY - Y1
	        		differenceX2 = commonX - X2
	        		differenceY2 = commonY - Y2
	
	        		'applying the angle between two lines vector rule, calculate the angle
	        		'in radians
	        		getAngleBetweenLinesVect = invCos(((differenceX1 * differenceX2) + (differenceY1 * differenceY2)) / _
	        		(Sqr(CDbl(differenceX1) ^ 2 + CDbl(differenceY1) ^ 2) * Sqr(CDbl(differenceX2) ^ 2 + 
	        		CDbl(differenceY2) ^ 2)))*/
			        if (velocityX>0 && velocityY>0){
			        	//ball traveling up and right
			        	//-90 is vertical wall 
			        	//    Y is unchanged
			        	//    X is inverted
			        	
			        	// -111 is slightly tilted like this \
			        	//    Y looses a little speed   
			        	//    X is inverted and gains a little speed
			        	
			        	//  -180  -----
			        	//    Y is inverted
			        	//    X is unchanged
			        	
			        	float ratio = angle+180; //0..90
			        	ratio = (1/90)*ratio;
			        	//ratio of 0 means x is unchanged
			        	//ratio of 1 means x is inverted
			        	
			        	float xBounce = velocityX*ratio*2;
			        	float newXVelocity = velocityX+xBounce;	
			        }
			        
			        //Calculate Normal vector (perpendicular to bumper)
			      
			        float normalY = (float) Math.cos( theta );
			        float normalX = (float) -Math.sin( theta );
			        
			      
			        float v_n = (velocityX*normalX)+(velocityY*normalY);
			        float n_n = (normalX*normalX)+(normalY*normalY);
			        
			
			        
			        float resultX = velocityX - 2 * normalX * v_n;
			        float resultY = velocityY - 2 * normalY * v_n;
			        //		ballX = (int) (ballStartX + (delta * velocityX * velocityScalingX));
					//      ballY = (int) (ballStartY + (delta * velocityY * velocityScalingY));
					
			     
			       
			        // if (intersection.x+ (delta))
			        
			        
			       // Point predictedPoint = new Point((int)(intersection.x+resultX*1000),(int)(intersection.y+resultY*1000));
			        
			    /*	float h;
					do {
						if (velocityY>0){
							intersection.y-=1;
						} else if (velocityY<0){
							intersection.y+=1;
						}
						if (velocityX>0){
							intersection.x-=1;
						}if (velocityX<0){
							intersection.x+=1;
						}
						h = getHforABCD(A,B,intersection,intersection);
						
					} while (h >= -0.8 && h <= 1.9);
					*/
					
					ballX = ballStartX = inside.x;
					ballY = ballStartY = inside.y;
					
					
					//Next check to see if this velocity send us out of the polygon
			        
	
			       // Log.i("BUNNY","old veloity="+velocityX+","+velocityY);
			        	velocityX = resultX;
			        	velocityY = resultY;
			        
			        bitmapCache.ballTap();
			      
			        	
			        //Log.i("BUNNY","new veloity="+velocityX+","+velocityY);
				
			        
			     //   ballX = (int) (inside.x + distancePastBumper*Math.cos(theta));
			     //   ballY = (int) (inside.y + distancePastBumper*Math.sin(theta));
			        
			        timeStartTossStage=System.currentTimeMillis();	
			       /* 
			        while (ballOutside && delta>0) {
						delta--;
						ballX = (int) (ballStartX + (delta * velocityX * velocityScalingX));
						ballY = (int) (ballStartY + (delta * velocityY * velocityScalingY));
						
						if (PointInPolygon(new PointF(ballX,ballY)) == false){
							Log.i("BUNNY","BALL OUTSIDE!!!");
							ballOutside=true;
						} else {
							ballOutside=false;
						}
					} 
					if (ballOutside){
						int adjust=1;
						while (ballOutside){
							if (PointInPolygon(new PointF(ballX+adjust,ballY))){
								ballX = ballX+adjust;
								ballOutside=false;
							}
							else if (PointInPolygon(new PointF(ballX,ballY+adjust))){
								ballY = ballY+adjust;
								ballOutside=false;
							}
							else if (PointInPolygon(new PointF(ballX-adjust,ballY))){
								ballX = ballX-adjust;
								ballOutside=false;
							}
							else if (PointInPolygon(new PointF(ballX,ballY-adjust))){
								ballY = ballY-adjust;
								ballOutside=false;
							} else {
								adjust++;
							}
	
						}
						
					}*/
					
				}
			
			}		       
		     
				
			}
			
			
			postInvalidate();
			if (System.currentTimeMillis() >= tossTime || (lastBallX==ballX && lastBallY==ballY)){
				//gameMode=GAME_MODE_JUMPING;	
				removeAction(tossing);
				
				if (ballOutsideCount>1){
					outOfBounds();
				}
				
				distanceFromHole = (float)Math.sqrt((ballHolePoint.x-ballX)*(ballHolePoint.x-ballX)+
					                                	  (ballHolePoint.y-ballY)*(ballHolePoint.y-ballY));

				//Log.i("BUNNY","distance from hole="+distanceFromHole);
				if (distanceFromHole<10){
					downTheHole();
					drawBall=false;
				} else {
					mainBunny.sendBunnyTo(60, 200);
					mainBunny.onDoneMovingHandler.add(new Runnable(){public void run() {
						mainBunny.supressActions=false;
					}});
				}
			} else {
				tossing.actionTime=TOSS_INTERVAL+System.currentTimeMillis();
			}
		}
	}});
	
	void downTheHole(){
		int confetti=0;
		switch (numberOfSwings){
		case 1: confetti+=5;
		case 2: confetti+=5;
		        bitmapCache.femaleCheer();
		case 3: confetti+=5;
		case 4: confetti+=3;
		        bitmapCache.familyClap();
		case 5: confetti+=1;
		case 6: confetti+=1;
		case 7: bitmapCache.shortClap();
		}
		launchConfetti(confetti);
		drawBall=false;
		bitmapCache.ballInCup();
		mainBunny.jumpAndShake(0, 0, new Runnable(){public void run(){
			//mainBunny.sendBunnyTo(280, 80);
			
			//Seems like these were not running, will need to fix this in a future version
		}});
		mainBunny.supressActions=false;
		resetGame();
	}
	
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	boolean first=false;
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		//if (gameMode==GAME_MODE_THROWING)
		{
			removeAction(tossing); //Random hack to try and fix a rare bug where the ball is not flingable
			
			this.velocityX=velocityX;
			this.velocityY=velocityY;
			//gameMode=GAME_MODE_BALLFLYING;
			//Log.i("BUNNY","BALL velocity="+velocityX+"   "+velocityY);
	    	
			tossTime= (System.currentTimeMillis()+(long)((Math.abs(velocityY)+Math.abs(velocityX))*TOSS_TIME_DAMP));
			timeStartTossStage=System.currentTimeMillis();
			tossing.name="BALL MOVEMENT";
			tossing.actionTime=TOSS_INTERVAL+System.currentTimeMillis();
			
			//Log.i("BUNNY","BALL duration "+(long)((Math.abs(velocityY)+Math.abs(velocityX))*TOSS_TIME_DAMP));
			//
			//thisBall=myBall;
			ballStartX = ballX;
			ballStartY = ballY;
			
			first=false;
			lastBunper=-1;
			numberOfSwings++;
			mainBunny.supressActions=true;
			addAction(tossing);
			synchronized (thread) {
				thread.notify();
			}
			
		}
		return false;
	
	}

	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	AnimationFrames flagAnimation;
	void startFlagAnimation() {
		final LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		frames.add(new AnimationFrame(43,12,bitmapCache.flag[0],580,0));
		frames.add(new AnimationFrame(43,12,bitmapCache.flag[1],580,0));
		frames.add(new AnimationFrame(43,12,bitmapCache.flag[2],580,0));
		frames.add(new AnimationFrame(43,12,bitmapCache.flag[1],550,0));
		
			
		flagAnimation = new AnimationFrames();
		flagAnimation.frames = frames;
		flagAnimation.background=true;
		flagAnimation.endless=true;
		playAnimationFrames(flagAnimation);
	}
	

}
/*float mapX = ballX/8;
			float mapY = ballY/8;
			int color = template.getPixel((int)mapX, (int)mapY);
			int r = (color>>8) & 0xFF;
			int g = (color>>16) & 0xFF;
			int b = (color>>24) & 0xFF;
			
			int delta2 = (int)(System.currentTimeMillis()-timeStartTossStage+TOSS_INTERVAL);
			
			if (r < 60 && g > 128 && b < 60) { 
				int Xy = (int) (ballStartX + (delta * velocityX * velocityScalingX));
				int Yy = (int) (ballStartY + (delta2 * velocityY * velocityScalingY));
				
				int Xx = (int) (ballStartX + (delta2 * velocityX * velocityScalingX));
				int Yx = (int) (ballStartY + (delta * velocityY * velocityScalingY));
			}
			*/
