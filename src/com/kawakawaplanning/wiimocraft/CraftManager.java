package com.kawakawaplanning.wiimocraft;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;

import javax.xml.stream.events.StartDocument;

public class CraftManager{

	//リセットするメソッドがいる
	//ボートで前進してる時にモードを変更した時など
	//長時間動作でラグがまだ
	
    Robot robot;
    Thread keyPressThread;
    Thread stopCheckThread;

    public int BPM;
    public long before;
    public int nowTime[];
    public int count;
    public boolean isFirst;
    boolean isDash;
    
//    public final static int CRAFT_MODE_NO       = 0;
//    public final static int CRAFT_MODE_STANDARD = 1;
//    public final static int CRAFT_MODE_BOAT     = 2;
//    public final static int CRAFT_MODE_MOUSE    = 3;
    
//    private int mode;
    
    public CraftManager(Robot r){
//    	mode = CRAFT_MODE_NO;
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
    
    public void start(){
    	switch (Main.mode) {
		case Main.MODE_MINECRAFT_STANDARD:
			startStandard();
			break;

		case Main.MODE_MINECRAFT_BOAT:
			break;
			
		case Main.MODE_MOUSE:
			break;
		}
    }
    
//    public void startBoat(){
//    	mode = CRAFT_MODE_BOAT;
//    }
    
    public void forward(){
    	robot.keyRelease(KeyEvent.VK_S);
		robot.keyPress(KeyEvent.VK_W);
    }
    
    public void back(){
    	robot.keyRelease(KeyEvent.VK_W);
		robot.keyPress(KeyEvent.VK_S);
    }
    
    public void stopMove(){
    	robot.keyRelease(KeyEvent.VK_S);
    	robot.keyRelease(KeyEvent.VK_W);
    }
    
    public void stop(){
    	stopMove();
    	variableInit();
    	robot.keyRelease(KeyEvent.VK_S);
    	robot.keyRelease(KeyEvent.VK_W);
    	robot.keyRelease(KeyEvent.VK_L);
    }

    public void startStandard() {
        variableInit();
        
        keyPressThread = new Thread() {//スレッド終了されるようになってる
            public void run() {
                while(Main.mode == Main.MODE_MINECRAFT_STANDARD){
                    if(BPM >= 90) {
                        isDash = true;
                        robot.keyPress(KeyEvent.VK_W);
                        robot.keyPress(KeyEvent.VK_L);
                        new Thread(new Runnable() {
							@Override
							public void run() {
								 robot.delay(100);
								 robot.keyRelease(KeyEvent.VK_L);
							}
						}).start();
                        
                    } else if (BPM >= 40) {
                        if(isDash){
                            robot.keyRelease(KeyEvent.VK_W);
                            robot.delay(100);
                        }
                        isDash = false;
                        robot.keyPress(KeyEvent.VK_W);
                    }

                    if(BPM != 0 && (System.currentTimeMillis() - before) >= 1000 && before != 0){
                        variableInit();
                        robot.keyRelease(KeyEvent.VK_W);
                        System.out.println("stop");
                    }
                    System.out.println(BPM);
//                    robot.delay(100);
                    //TODO: すでにしてることは出来る限りしない主義で。
                }
            }
        };
        keyPressThread.start();
        System.out.println("Started");
    }
    public void updateBPM(){
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
            }

         } catch (Exception e) {
             e.printStackTrace();
         }
    	
    }
    

}