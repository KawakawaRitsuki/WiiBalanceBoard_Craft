package com.kawakawaplanning.winecraft.wiimote;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface MovingControler extends Library {
	  MovingControler INSTANCE = (MovingControler) Native.loadLibrary("move", MovingControler.class);
	  void move(double width,double height,double dx,double dy);
	  void press(double height,boolean left);
	  void release(double height,boolean left);
}