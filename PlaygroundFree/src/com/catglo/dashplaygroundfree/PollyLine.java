package com.catglo.dashplaygroundfree;

import java.util.Iterator;


import android.graphics.Point;

public class PollyLine extends Object implements Iterator<StreightLine>{
	PollyLine(Point[] points){
		linePoints=points;
	}
	Point[] linePoints;
	
	private int itterationCount;
	private int xoffset;
	private int yoffset;
	public Iterator<StreightLine> iterate(int xoffset, int yoffset){
		itterationCount=0;
		this.xoffset = xoffset;
		this.yoffset = yoffset;
		return this;
	}
	public Iterator<StreightLine> iterate(){
		itterationCount=0;
		this.xoffset = 0;
		this.yoffset = 0;
		return this;
	}
	
	public boolean hasNext() {
		if (itterationCount<linePoints.length-1)
			return true;
		return false;
	}
	public StreightLine next() {
		Point p1 = new Point(linePoints[itterationCount]);
		Point p2 = new Point(linePoints[itterationCount+1]);
		p1.x += xoffset;
		p1.y += yoffset;
		p2.x += xoffset;
		p2.y += yoffset;
		StreightLine sl = new StreightLine(p1,p2);
		itterationCount++;
		return sl;
	}
	public void remove() {
		throw(new UnsupportedOperationException());
	}
	
	
	
}
