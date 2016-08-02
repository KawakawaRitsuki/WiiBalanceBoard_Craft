package com.kawakawaplanning.winecraft.wiimote;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface MovingManager extends Library {
	  MovingManager INSTANCE = (MovingManager) Native.loadLibrary("move", MovingManager.class);
	  void move(double width,double height,double dx,double dy);
	  void press(double height,boolean left);
	  void release(double height,boolean left);
}