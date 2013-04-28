package com.catglo.dashplaygroundfree;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


interface MenuExecutor {
	void execute();
}

class MenuItem extends Object {
	Rect area;
	private MenuExecutor executor;
	boolean inverted=false;
	
	public MenuItem(Rect r,MenuExecutor executor){
		area = r;
		this.executor = executor;
	}
	
	void go(){
		executor.execute();
	}

	public void invert() {
		inverted=true;
	}
}

