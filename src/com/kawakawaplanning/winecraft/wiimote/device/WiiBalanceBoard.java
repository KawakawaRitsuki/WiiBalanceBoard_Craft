package com.kawakawaplanning.winecraft.wiimote.device;

import java.io.IOException;
import java.util.List;

import purejavahidapi.DeviceRemovalListener;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;

/**
 * WiiBalanceBoard
 * WiiBalanceBoardを扱うクラス。
 * 
 * @author KawakawaRitsuki
 * @version 0.1
 * @since 0.1
 */

public class WiiBalanceBoard extends Device {
	
	public WiiBalanceBoard(HidDeviceInfo info) {
		super(info);
	}

	public boolean isConnected(){
		return mDev != null;
	}
	
	public void setPlayerID(boolean flag){
		String data;
		if(flag){
			data = "1000";
		}else{
			data = "0000";
		}
		byte[] b = {0x11,(byte) Integer.parseInt(new StringBuilder(data).reverse()+"0000",2)};
		mDev.setOutputReport(b[0],b , b.length);
	}
	
	public final static byte WIMOTE_ONLY_REPORT_MODE = 0x30;
	public final static byte WIMOTE_NUNCHUCK_REPORT_MODE = 0x34;
	
	public void setReportMode(byte mode){
		byte[] b = {0x12,0x00,mode};
		mDev.setOutputReport(b[0],b , b.length);
	}

	public void setDeviceRemovalListener(DeviceRemovalListener listener){
		mDev.setDeviceRemovalListener(listener);
	}
	
	public void setInputReportListener(InputReportListener listener){
		mDev.setInputReportListener(listener);
	}
	
	public void requestStatus(){
		byte[] b = {0x15,0x00};
		mDev.setOutputReport(b[0],b , b.length);
	}
	
	
}
