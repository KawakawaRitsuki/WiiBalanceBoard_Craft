package com.kawakawaplanning.wiimocraft;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;

public class CraftManager{

	//リセットするメソッドがいる
	//ボートで前進してる時にモードを変更した時など
	//長時間動作でラグがまだ治ってない
	
    Robot robot;
    Thread keyPressThread;
    Thread stopCheckThread;

    public int BPM;
    public long before;
    public int nowTime[];
    public int count;
    public boolean isFirst;
    boolean isDash;
    
    public final static int CRAFT_MODE_NO       = 0;
    public final static int CRAFT_MODE_STANDARD = 1;
    public final static int CRAFT_MODE_BOAT     = 2;
    public final static int CRAFT_MODE_MOUSE    = 3;
    
    private int mode;
    
    public CraftManager(Robot r){
    	mode = CRAFT_MODE_NO;
    	robot = r;
    }
    
    private void variableInit(){
        BPM = 0;
        before = 0;
        nowTime = new int[2];
        count = 0;
        isFirst = true;
        isDash = false;
    }
    
    public void startBoat(){
    	mode = CRAFT_MODE_BOAT;
    }
    
    public void forward(){
    	if(mode == CRAFT_MODE_BOAT){
			robot.keyRelease(KeyEvent.VK_S);
    		robot.keyPress(KeyEvent.VK_W);
    	}
    }
    
    public void back(){

		robot.keyRelease(KeyEvent.VK_W);
		robot.keyPress(KeyEvent.VK_S);
		
    }
    
    public void stopMove(){
    	if(mode == CRAFT_MODE_BOAT){

			robot.keyRelease(KeyEvent.VK_S);
	    	robot.keyRelease(KeyEvent.VK_W);
    		
    	}
    }
    
    public void stop(){
    	mode = CRAFT_MODE_NO;
    	variableInit();
		robot.keyRelease(KeyEvent.VK_S);
		robot.keyRelease(KeyEvent.VK_W);
    }

    public void startStandard() {
    	
    	mode = CRAFT_MODE_STANDARD;
    	
        variableInit();
        keyPressThread = new Thread() {
            public void run() {
                while(mode == CRAFT_MODE_STANDARD){
                    if(BPM >= 90) {
                        isDash = true;
                        robot.keyPress(KeyEvent.VK_W);
                        robot.keyPress(KeyEvent.VK_L);
                        robot.delay(100);
                        robot.keyRelease(KeyEvent.VK_L);

                    } else if (BPM >= 40) {
                        if(isDash){
                            robot.keyRelease(KeyEvent.VK_W);
                            robot.delay(100);
                        }
                        isDash = false;
                        robot.keyPress(KeyEvent.VK_W);
                    } else {
                        isDash = false;
                        robot.keyRelease(KeyEvent.VK_W);
                    }

                    robot.delay(100);
                }
            }
        };
        stopCheckThread = new Thread() {
            @Override
            public void run() {
                while(mode == CRAFT_MODE_STANDARD){
                    if(BPM != 0 && (System.currentTimeMillis() - before) >= 1000 && before != 0){
                        BPM = 0;
                        count = 0;
                        isFirst=true;
                        before = 0;
                        nowTime[0] = 0;
                        nowTime[1] = 0;

                        robot.keyRelease(KeyEvent.VK_W);
                        robot.delay(50);
                        robot.keyPress(KeyEvent.VK_W);
                        robot.delay(50);
                        robot.keyRelease(KeyEvent.VK_W);

                        System.out.println("stop");
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        System.out.println("Started");
        keyPressThread.start();
        stopCheckThread.start();
    }
    public void walk(){
    	if(mode == CRAFT_MODE_STANDARD){
    		try {
                if(before == 0){
                    before = System.currentTimeMillis();
                    BPM = 60;
                    System.out.println("start");
                } else {
                    long now = System.currentTimeMillis();
                    nowTime[count] = (int)(now - before);
                    before = now;
                    count++;
                    if (count == 2) count = 0;

                    int sum = 0;
                    for(int i:nowTime){
                        sum = sum + i;
                    }
                    BPM = 60000 / sum * 2;
                    if(isFirst){
                        BPM = BPM / 2;
                        isFirst=false;
                    }
                    System.out.println(BPM);
                }

             } catch (Exception e) {
                 e.printStackTrace();
             }
    	}
    	
    }
    

}