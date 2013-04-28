package com.catglo.dashplaygroundfree;

import java.util.LinkedList;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;





public  class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {
    float XScaling;
	float YScaling;
	
	protected void updatePhysics(){
	}
	
	protected Bitmap mBackgroundImage;

	protected void loadBitmaps(Resources res){}
	protected void drawSurface(Canvas canvas){}

	class DrawingThread extends Thread {
        private SurfaceHolder mSurfaceHolder;
		int surfaceWidth;
        int surfaceHeight;
        boolean currentlyRunning=false;
     	
		
        public DrawingThread(SurfaceHolder surfaceHolder, Context context,Handler handler) {
            // get handles to some important objects
        	Resources res;
            mSurfaceHolder = surfaceHolder;
            res = context.getResources();
            loadBitmaps(res);
        }
 
       
        /**
         * Pauses the physics update & animation.
         */
        public void pause() {
            synchronized (mSurfaceHolder) {
            	currentlyRunning=false;
            }
        }

        /**
         * Restores game state from the indicated Bundle. Typically called when
         * the Activity is being restored after having been previously
         * destroyed.
         * 
         * @param savedState Bundle containing the game state
         */
        public synchronized void restoreState(Bundle savedState) {
            synchronized (mSurfaceHolder) {
                
            }
        }

        @Override
        public void run() {
            while (currentlyRunning) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                       // if (mMode == STATE_RUNNING) updatePhysics();
         
                        doDraw(c);
                    }
                } catch (Exception e) {
                	Log.i("CRASH",""+e.getMessage());
                }finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don'initThread leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        /**
         * Dump game state to the provided Bundle. Typically called when the
         * Activity is being suspended.
         * 
         * @return Bundle with this view's state
         */
        public Bundle saveState(Bundle map) {    
            return map;
        }

        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
        	surfaceWidth=width;
        	surfaceHeight=height;
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                // don'initThread forget to resize the background image
                Bitmap b = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
                XScaling = (float)b.getWidth()/(float)mBackgroundImage.getWidth();
                YScaling = (float)b.getHeight()/(float)mBackgroundImage.getHeight();
                mBackgroundImage=b;
            }
        }

        /**
         * Resumes from a pause.
         */
        public void unpause() {
            // Move the real time clock up to now
            synchronized (mSurfaceHolder) {
            	currentlyRunning=true;
            }
        }

		
		private void doDraw(Canvas canvas) {
	       	// Draw the background image. Operations on the Canvas accumulate so this is like clearing the screen.
            canvas.drawBitmap(mBackgroundImage, 0, 0, null);
            updatePhysics();
 
			drawSurface(canvas);
		}
	}

	private DrawingThread thread;

	SwitchViews switcher;
	public void setSwitcher(SwitchViews switcher) {
		this.switcher = switcher;
	}
	

	public Rect scaleRect(Rect area) {
		Rect ret = new Rect((int)(area.left*XScaling),(int)(area.top*YScaling), (int)(area.right*XScaling), (int)(area.bottom*YScaling));
		return ret;
	}

	long touchTime=System.currentTimeMillis();
    public DrawingSurface(Context context) {
		super(context);
	    
		// register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        
        // create thread only; it's started in surfaceCreated()
        thread = new DrawingThread(holder, context, new Handler() {});
        
        
        
        
        setFocusable(true); // make sure we get key events
    }
        
	 public DrawingThread getThread() {
	        return thread;
	 }

	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		 thread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		thread.currentlyRunning = true;
        thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.currentlyRunning = false;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) 
            {
            }
        }
    }

	
 
	
}
