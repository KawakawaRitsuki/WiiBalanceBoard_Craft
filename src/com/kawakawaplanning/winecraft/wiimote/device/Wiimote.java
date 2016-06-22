package com.kawakawaplanning.winecraft.wiimote.device;

import java.io.IOException;
import java.util.List;

import purejavahidapi.DeviceRemovalListener;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;

/**
 * Wiimote
 * Wiimoteを扱うクラス。
 * 
 * @author KawakawaRitsuki
 * @version 0.1
 * @since 0.1
 */

public class Wiimote extends Device {
	
	public Wiimote(HidDeviceInfo info) {
		super(info);
		// TODO Auto-generated constructor stub
	}

	//mDevをDeviceが保持
	private boolean nuchuck = false;
	
	public boolean isNunchuckConnected(){
		return nuchuck;
	}
	
	public void setNunchuckConnected(boolean connect){
		nuchuck = connect;
	}
	
	public void setPlayerID(String flag){
		byte[] b = {0x11,(byte) Integer.parseInt(new StringBuilder(flag).reverse()+"0000",2)};
		mDev.setOutputReport(b[0],b , b.length);
	}
	
	public void vibrate(long sec){
		byte[] b = {0x11,(byte) Integer.parseInt("00010001",2)};
		mDev.setOutputReport(b[0],b , b.length);
		
		try {Thread.sleep(sec);} catch (InterruptedException e) {}
		
		byte[] b2 = {0x11,(byte) Integer.parseInt("00010000",2)};
		mDev.setOutputReport(b[0],b2 , b2.length);
	}
	
	public final static byte WIMOTE_ONLY_REPORT_MODE = 0x30;
	public final static byte WIMOTE_NUNCHUCK_REPORT_MODE = 0x34;
	
	public void setReportMode(byte mode){
		byte[] b = {0x12,0x00,mode};
		mDev.setOutputReport(b[0],b , b.length);
	}

	public void setNunchuckData(){
		byte[] b = {0x16,0x04,(byte) 0xA4,0x00,0x40,0x01,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        
        mDev.setOutputReport(b[0],b , b.length);
	}
	
	public void setDeviceRemovalListener(DeviceRemovalListener listener){
		mDev.setDeviceRemovalListener(listener);
	}
	
	public void setInputReportListener(InputReportListener listener){
		mDev.setInputReportListener(listener);
	}
	
}
