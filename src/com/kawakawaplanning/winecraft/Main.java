package com.kawakawaplanning.winecraft;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import com.kawakawaplanning.winecraft.wiimote.MovingControler;
import com.kawakawaplanning.winecraft.wiimote.OnButtonListener;
import com.kawakawaplanning.winecraft.wiimote.WiimoteButton;
import com.kawakawaplanning.winecraft.wiimote.device.Device;
import com.kawakawaplanning.winecraft.wiimote.device.DeviceConnector;
import com.kawakawaplanning.winecraft.wiimote.device.Wiimote;

import purejavahidapi.HidDevice;
import purejavahidapi.InputReportListener;

public class Main {

	public static MovingControler mc;
	public static int displayWidth;
	public static int displayHeight;
	public static Robot r;
	public static WiimoteButton wiimoteButton;
	
	public static int mode;
	public static final int WIIMOTE_NUNCHUCK_CONTROL_MODE = 0;
	public static final int WIIMOTE_BALANCE_CONTROL_MODE = 1;
	public static final int WIIMOTE_BALANCE_NUNCHUCK_CONTROL_MODE = 2;
	
	public static int status;
	public static final int STATUS_NOT_CONNECTED_EXTENSIONS = 0;
	public static final int STATUS_WAITING = 1;
	public static final int STATUS_PLAYING = 2;
	
	public static boolean isConnectedNunchuck = false;
	public static boolean isConnectedBalanceBoard = false;
	
	public static void main(String[] args) {
		
		mode = WIIMOTE_NUNCHUCK_CONTROL_MODE;
		status = STATUS_NOT_CONNECTED_EXTENSIONS;
		mc = MovingControler.INSTANCE;
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		displayWidth = d.width;
		displayHeight = d.height;

		wiimoteButton = new WiimoteButton();
		
		try {r = new Robot();} catch (AWTException e) {}
		
		
		DeviceConnector wiimoteDC = new DeviceConnector();
		wiimoteDC.connect(0,DeviceConnector.WIIMOTE);
		
		Wiimote wiimote = new Wiimote(wiimoteDC.getDevInfo());
		wiimote.vibrate(250);
		
		wiimote.setInputReportListener(new InputReportListener() {
			@Override
			public void onInputReport(HidDevice source, byte Id, byte[] b, int len) {
				
				String[] data = new String[len]; for (int i = 0; i < len; i++) data[i] = Integer.toBinaryString(b[i]);
				
				if(data[0].equals("110100")){ //ヌンチャク接続時
					
					wiimoteButton.check(data[1],data[2],data[8]);
					
					if(status == STATUS_PLAYING){
						int axn = 0;
						int ayn = 0;
						
						byte nunchuckXX = (byte) ((b[3] ^ 0x17 ) + 0x17);
						byte nunchuckYY = (byte) ((b[4] ^ 0x17 ) + 0x17);
						
						if (nunchuckXX > 0x90) axn = (nunchuckXX - 0x90)/8;
						if (nunchuckXX < 0x70) axn = (nunchuckXX - 0x70)/8;
						if (nunchuckYY > 0x90) ayn = (nunchuckYY - 0x90)/8;
						if (nunchuckYY < 0x70) ayn = (nunchuckYY - 0x70)/8;
						
						if (axn < -10) axn = 30 + axn;       if (ayn < -10) ayn = 30 + ayn;
						if (axn == 1 || axn == -1) axn = 0;	 if (ayn == 1 || ayn == -1) ayn = 0;
						
						Point mp=MouseInfo.getPointerInfo().getLocation();
						
						if (displayHeight > (mp.y - ayn)
						  &&            0 < (mp.y - ayn)
						  && displayWidth > (mp.x + axn)
						  &&            0 < (mp.x + axn)
						  &&      (axn != 0 || ayn != 0))
									mc.move(displayWidth,displayHeight,axn,ayn*-1);
						
						if (mp.y < 15)
							r.mouseMove(mp.x, displayHeight-20);
						else if(mp.y > displayHeight-15)
							r.mouseMove(mp.x, 20);
						
					    if (mp.x < 15)
							r.mouseMove(displayWidth-20, mp.y);
						else if(mp.x > displayWidth-15)
							r.mouseMove(20, mp.y);
					}
					
				}else if(data[0].equals("110000")){ //ヌンチャク未接続時
					
					wiimoteButton.check(data[1],data[2]);
					
				}else if(data[0].equals("100000")){//情報請求時のレスポンスなど
					
					String connectedData = String.format("%10s", data[3]).replace(" ", "0");
					connectedData = connectedData.substring(connectedData.length() - 2, connectedData.length());
					System.out.println(data[3]);
					if(connectedData.equals("00")){
						wiimote.setReportMode((byte) 0x30); 
						isConnectedNunchuck = false;
						
						System.out.println("Disconnected nunchuck.");
					}else if(connectedData.equals("10")){
						wiimote.setReportMode((byte) 0x34); 
						wiimote.setNunchuckData();
						isConnectedNunchuck = true;
						
						System.out.println("Connected nunchuck.");
					}
					checkStatus();
					changePlayerLED(wiimote);
				}else{
					for (int i = 0; i < len; i++)
						System.out.print(Integer.toBinaryString(b[i])+" ");
					System.out.println();
				}
			}
		});
		wiimote.requestStatus();
		wiimoteButton.setOnButtonListener(new OnButtonListener() {
			
			@Override
			public void onReleased(int button) {
				if(status == STATUS_PLAYING){
					switch (button) {
					case WiimoteButton.BUTTON_A:
						mc.release(displayHeight,true);
						break;
					case WiimoteButton.BUTTON_B:
						r.keyRelease(KeyEvent.VK_SPACE);
						break;
					case WiimoteButton.BUTTON_LEFT:
						r.keyRelease(KeyEvent.VK_A);
						break;
					case WiimoteButton.BUTTON_UP:
						r.keyRelease(KeyEvent.VK_W);
						break;
					case WiimoteButton.BUTTON_RIGHT:
						r.keyRelease(KeyEvent.VK_D);
						break;
					case WiimoteButton.BUTTON_DOWN:
						r.keyRelease(KeyEvent.VK_S);
						break;
					case WiimoteButton.BUTTON_HOME:
						r.keyRelease(KeyEvent.VK_ESCAPE);
						break;
					case WiimoteButton.BUTTON_PLUS:
						
						break;
					case WiimoteButton.BUTTON_MINUS:
						
						break;
					case WiimoteButton.BUTTON_1:
						
						break;
					case WiimoteButton.BUTTON_2:
						
						break;
					case WiimoteButton.BUTTON_Z:
						mc.release(displayHeight,false);
						break;
					case WiimoteButton.BUTTON_C:
						mc.release(displayHeight,true);
						break;
					}
				}else{
					
				}
			}
			
			@Override
			public void onPushed(int button) {
				if(status == STATUS_PLAYING){
					switch (button) {
					case WiimoteButton.BUTTON_A:
						mc.press(displayHeight,true);
						break;
					case WiimoteButton.BUTTON_B:
						r.keyPress(KeyEvent.VK_SPACE);
						break;
					case WiimoteButton.BUTTON_LEFT:
						r.keyPress(KeyEvent.VK_A);
						break;
					case WiimoteButton.BUTTON_UP:
						r.keyPress(KeyEvent.VK_W);
						break;
					case WiimoteButton.BUTTON_RIGHT:
						r.keyPress(KeyEvent.VK_D);
						break;
					case WiimoteButton.BUTTON_DOWN:
						r.keyPress(KeyEvent.VK_S);
						break;
					case WiimoteButton.BUTTON_HOME:
						r.keyPress(KeyEvent.VK_ESCAPE);
						break;
					case WiimoteButton.BUTTON_PLUS:
						
						break;
					case WiimoteButton.BUTTON_MINUS:
						break;
					case WiimoteButton.BUTTON_1:
						break;
					case WiimoteButton.BUTTON_2:
						status = STATUS_WAITING;
						changePlayerLED(wiimote);
						break;
					case WiimoteButton.BUTTON_Z:
						mc.press(displayHeight,false);
						break;
					case WiimoteButton.BUTTON_C:
						mc.press(displayHeight,true);
						break;
					}
				}else{
					switch (button) {
					case WiimoteButton.BUTTON_A:
						break;
					case WiimoteButton.BUTTON_B:
						break;
					case WiimoteButton.BUTTON_LEFT:
						break;
					case WiimoteButton.BUTTON_UP:
						break;
					case WiimoteButton.BUTTON_RIGHT:
						break;
					case WiimoteButton.BUTTON_DOWN:
						break;
					case WiimoteButton.BUTTON_HOME:
						break;
					case WiimoteButton.BUTTON_PLUS:
						
						break;
					case WiimoteButton.BUTTON_MINUS:
						
						break;
					case WiimoteButton.BUTTON_1:
						changeMode();
						checkStatus();
						changePlayerLED(wiimote);
						break;
					case WiimoteButton.BUTTON_2:
						checkStatus();
						if(status == STATUS_WAITING){
							status = STATUS_PLAYING;
							changePlayerLED(wiimote);
						}
						break;
					case WiimoteButton.BUTTON_Z:
						break;
					case WiimoteButton.BUTTON_C:
						break;
					}
				}
			}
		});
	}

	public static void sleep(long ms){
		try {Thread.sleep(ms);} catch (InterruptedException e) {}
	}
	
	public static void checkStatus(){
		switch (mode) {
		case WIIMOTE_NUNCHUCK_CONTROL_MODE:
			if(isConnectedNunchuck){
				status = STATUS_WAITING;
			}else{
				status = STATUS_NOT_CONNECTED_EXTENSIONS;
			}
			break;
		case WIIMOTE_BALANCE_CONTROL_MODE:
			if(isConnectedBalanceBoard){
				status = STATUS_WAITING;
			}else{
				status = STATUS_NOT_CONNECTED_EXTENSIONS;
			}
			break;
		case WIIMOTE_BALANCE_NUNCHUCK_CONTROL_MODE:
			if(isConnectedNunchuck && isConnectedBalanceBoard){
				status = STATUS_WAITING;
			}else{
				status = STATUS_NOT_CONNECTED_EXTENSIONS;
			}
			break;
		}
	}
	
	public static void changePlayerLED(Wiimote wiimote){
		String led = "";
		switch (status) {
		case STATUS_NOT_CONNECTED_EXTENSIONS:
			led = "10";
			break;
		case STATUS_PLAYING:
			led = "11";
			break;
		case STATUS_WAITING:
			led = "01";
			break;
		}
		switch (mode) {
		case WIIMOTE_NUNCHUCK_CONTROL_MODE:
			led = led + "10";
			break;
		case WIIMOTE_BALANCE_CONTROL_MODE:
			led = led + "01";
			break;
		case WIIMOTE_BALANCE_NUNCHUCK_CONTROL_MODE:
			led = led + "11";
			break;
		}
		wiimote.setPlayerID(led);
	}
	
	public static void changeMode(){
		if(mode == WIIMOTE_NUNCHUCK_CONTROL_MODE){
			mode = WIIMOTE_BALANCE_CONTROL_MODE;
		}else if(mode == WIIMOTE_BALANCE_CONTROL_MODE){
			mode = WIIMOTE_BALANCE_NUNCHUCK_CONTROL_MODE;
		}else if(mode == WIIMOTE_BALANCE_NUNCHUCK_CONTROL_MODE){
			mode = WIIMOTE_NUNCHUCK_CONTROL_MODE;
		}
	}
	
}
