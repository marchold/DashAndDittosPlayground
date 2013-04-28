package com.catglo.dashplaygroundfree;

import java.util.LinkedList;

class AnimationFrames {
	LinkedList<AnimationFrame> frames  = new LinkedList<AnimationFrame>();
	int current;
	AnimationAction parent;
	public boolean background=false;
	public boolean endless;
	
	AnimationFrame get(){
		return frames.get(current);
	}
}