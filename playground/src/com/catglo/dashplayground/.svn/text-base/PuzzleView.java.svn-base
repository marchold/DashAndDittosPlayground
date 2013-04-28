package com.catglo.dashplayground;

import java.util.LinkedList;



import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

class PuzzlePiece {
	public Bitmap bitmap;
	public Bitmap originalBitmap;
	public int snapToX;
	public int snapToY;
	public int X;
	public int Y;
	public int width;
	public int height;
	public int grabOffsetX;
	public int grabOffsetY;
	
	
	public PuzzlePiece(Bitmap b){
		originalBitmap = bitmap = b;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}

	boolean isTouched(int touchX, int touchY){
		int w = width;
		int h = height;
		Rect pieceRect = new Rect(X,Y,X+w,Y+h);
		if (pieceRect.contains(touchX, touchY) ){
			//TODO: check pixel value, because we must not touch a transparent portion
			return true;
		}
		return false;
	}
	
	void dragTo(float touchX, float touchY){
		X = (int)touchX-grabOffsetX;
		Y = (int)touchY-grabOffsetY;
		Log.i("BUNNY","dragging to "+X+"  "+Y);
	}

	public void setGrabOffset(float touchX, float touchY) {
		grabOffsetX = width/2;//touchX - X;
		grabOffsetY = height/2;//touchY - Y;	
	}

	public boolean isNearSnap() {
		if (Math.abs(X-snapToX) + Math.abs(Y-snapToY) < 90){
			return true;
		}
		return false;
	}
}

public class PuzzleView extends DrawingView implements OnTouchListener{
	int whoAmI(){return -1;};
	int whereAmI(){return -1;};
	protected LinkedList<PuzzlePiece> gamePieces = new LinkedList<PuzzlePiece>();
	private int exitFrame; 
	Bitmap[] pushImages = new Bitmap[3];
	
	protected void resume(){
		super.resume();
		exitFrame = 3;
	}
	
	Context context;
	long touchTime=System.currentTimeMillis();


	private int snapSound;
    public PuzzleView(Context context,final int width, final int height, BitmapCache bitmapCache,Point origin) {
		super(context,width,height,bitmapCache,origin);
		
		pushImages[0] = mainBunny.bunnies.frames.push1;
		pushImages[1] = mainBunny.bunnies.frames.push2;
		pushImages[2] = mainBunny.bunnies.frames.push3;
		
		//snapSound = sound.load(getContext(), R.raw.snap,1);

        this.setOnTouchListener(new View.OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				long t = System.currentTimeMillis();
				return true;
			} 	
        });
        
        setOnTouchListener(this);  
	}
    
	protected void drawSurface(Canvas canvas){	

        int len = gamePieces.size();
    	for (int i = 0; i < len; i++){
    		PuzzlePiece piece = gamePieces.get(i);
    		canvas.drawBitmap(piece.bitmap,piece.X,piece.Y,null);
    	}   
    	
    	if (pieceToDrag!=null){
    		canvas.drawRect(new Rect((int)pieceToDrag.snapToX-1,(int)pieceToDrag.snapToY-1,(int)pieceToDrag.snapToX+1,(int)pieceToDrag.snapToY+1),  new Paint());
    		
    		canvas.drawRect(new Rect((int)pieceToDrag.X-1,(int)pieceToDrag.Y-1,(int)pieceToDrag.X+1,(int)pieceToDrag.Y+1),  new Paint());	
    	
    		if (bunnyPushingMatrix!=null && gotThere){
    			canvas.drawBitmap(pushImages[pushFrames], bunnyPushingMatrix, null);
    		}
    	}
    	
    	super.drawSurface(canvas);
        
	}
 
    private PuzzlePiece pieceToDrag=null;
	
    Point findClosestCorner(PuzzlePiece piece){
		//find the closest corner
		Rect pieceRect = new Rect(piece.X,piece.Y,piece.width+piece.X,piece.height+piece.Y);
		//
		//    A----------B
		//    |          |
		//    |          |
		//    |          |
		//    |          |
		//    C----------D
		//
		float distA = (float) Math.sqrt((mainBunny.actualX-pieceRect.left)*(mainBunny.actualX-pieceRect.left)
				                       +(mainBunny.actualY-pieceRect.top) *(mainBunny.actualY-pieceRect.top));
		
		float distB = (float) Math.sqrt((mainBunny.actualX-pieceRect.right)*(mainBunny.actualX-pieceRect.right)
                                       +(mainBunny.actualY-pieceRect.top) *(mainBunny.actualY-pieceRect.top));

		float distC = (float) Math.sqrt((mainBunny.actualX-pieceRect.left)*(mainBunny.actualX-pieceRect.left)
                                       +(mainBunny.actualY-pieceRect.bottom) *(mainBunny.actualY-pieceRect.bottom));

		float distD = (float) Math.sqrt((mainBunny.actualX-pieceRect.right)*(mainBunny.actualX-pieceRect.right)
                                       +(mainBunny.actualY-pieceRect.bottom) *(mainBunny.actualY-pieceRect.bottom));

		Point closestPoint;
		if (distA < distB && distA < distC && distA < distD){
			closestPoint = new Point(pieceRect.left,pieceRect.top);
		} else
		if (distB < distC && distB < distD){
			closestPoint = new Point(pieceRect.right,pieceRect.top);
		} else
		if (distC < distD){
			closestPoint = new Point(pieceRect.left,pieceRect.bottom);
		} else {
			closestPoint = new Point(pieceRect.right,pieceRect.bottom);
		}
		
		return closestPoint;
    }
    
    Point findTopMidPoint(PuzzlePiece piece){
		//find the midpoint between A and B
		Rect pieceRect = new Rect(piece.X,piece.Y,piece.width+piece.X,piece.height+piece.Y);
		//
		//    A----------B
		//    |          |
		//    |          |
		//    |          |
		//    |          |
		//    C----------D
		//
		Point betweenAandB = new Point((pieceRect.left+pieceRect.right)/2,pieceRect.top);
		return betweenAandB;
    }


    Point findLeftSideMidPoint(PuzzlePiece piece){
		//find the midpoint between A and B
		Rect pieceRect = new Rect(piece.X,piece.Y,piece.width+piece.X,piece.height+piece.Y);
		//
		//    A----------B
		//    |          |
		//    |          |
		//    |          |
		//    |          |
		//    C----------D
		//
		Point betweenAandC = new Point(pieceRect.left,(pieceRect.top+pieceRect.bottom)/2);
		return betweenAandC;
    }

    Point lastPoint;
    int pushFrames=0;
    Matrix bunnyPushingMatrix;
    private void onMovePuzzlePiece() {
    	Point goToPoint = findLeftSideMidPoint(pieceToDrag);
    	
    	float distance = (float) Math.sqrt((goToPoint.x-mainBunny.actualX)*(goToPoint.x-mainBunny.actualX)+
    	    	            	           (goToPoint.y-mainBunny.actualY)*(goToPoint.y-mainBunny.actualY));
    	
    	if (gotThere==false){
    		mainBunny.setTarget(goToPoint.x, goToPoint.y);
    	} else {
    	
	    	mainBunny.supressActions=true;
	    	drawMainBunny=false;
			
			bunnyPushingMatrix = new Matrix();
			
			
			bunnyPushingMatrix.postTranslate(-55,-60);
			
			
			bunnyPushingMatrix.postTranslate(goToPoint.x, goToPoint.y);	
			
			
			
			if (distance>10){
				lastPoint = goToPoint;
				mainBunny.actualX=goToPoint.x;
				mainBunny.actualY=goToPoint.y;	
				pushFrames++;
				if (pushFrames==3){
					pushFrames=0;
				}
			}
    	}
		
	}
	
    boolean gotThere=false;
	protected void onGrabPuzzlePiece(PuzzlePiece piece){
		Point goToPoint = findLeftSideMidPoint(piece);
		lastPoint=goToPoint;
		mainBunny.animationInterval=100;
		mainBunny.distanceOpen=120;
		mainBunny.sendBunnyTo(goToPoint.x, goToPoint.y);
		gotThere=false;
		mainBunny.supressActions=true;
		mainBunny.onDoneMovingHandler.add(new Runnable(){public void run() {
			gotThere=true;
			mainBunny.animationInterval=250;
			mainBunny.distanceOpen=80;
	
		}});
		
	}
	
	protected void onDropPuzzlePiece(PuzzlePiece piece){
		if (pieceToDrag.isNearSnap()){
			pieceToDrag.X = pieceToDrag.snapToX;
			pieceToDrag.Y = pieceToDrag.snapToY;
			//sound.play(snapSound, 1, 1, 1, 0, 1);
		}
		mainBunny.supressActions=false;
    	drawMainBunny=true;
    	mainBunny.sendBunnyTo(50,50);
    	mainBunny.onDoneMovingHandler.add(new Runnable(){public void run() {
    		mainBunny.startBunnyAtAttention(500);
    		
		}});
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		int eventaction = event.getAction(); 
        
        float X = event.getX()*XScalingRecripricol; 
        float Y = event.getY()*YScalingRecripricol; 

        switch (eventaction ) { 

        case MotionEvent.ACTION_DOWN: 
        	int len = gamePieces.size();
        	for (int i = len-1; i >= 0; i--){
        		PuzzlePiece piece = gamePieces.get(i);
        		if (piece.isTouched((int)X,(int)Y)){
        			pieceToDrag = piece;
        			piece.setGrabOffset(X,Y);    
        			onGrabPuzzlePiece(piece);
        			invalidate();
        			return true; 
        		}
        	}
            return false;


        case MotionEvent.ACTION_MOVE:         	
        	if (pieceToDrag!=null){
        		pieceToDrag.dragTo(X,Y);
        		onMovePuzzlePiece();
        		invalidate();
        	}
            return true; 

        case MotionEvent.ACTION_UP:
        	 if (pieceToDrag!=null) {
        		 onDropPuzzlePiece(pieceToDrag);
        		 invalidate();
        	 }
        	 pieceToDrag=null;
        	 return true; 
        } 
        return false;
	} 

	PuzzlePiece newPiece(Bitmap which_bitmap,int x, int y, int snapX, int snapY) {
		PuzzlePiece p = new PuzzlePiece(which_bitmap);
		p.X = x;
		p.Y = y; 
		p.snapToX=snapX;
		p.snapToY=snapY;
		return p;
	}
	
}
