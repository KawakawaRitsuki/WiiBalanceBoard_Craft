package com.kawakawaplanning.winecraft.wiimote.device;

import java.io.IOException;

import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.PureJavaHidApi;

public class Device {
	
	protected HidDevice mDev;
	
	public Device(HidDeviceInfo info){
		try {
			mDev = PureJavaHidApi.openDevice(info.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnected(){
		return mDev != null;
	}
	
	public void requestStatus(){
		byte[] b = {0x15,0x00};
		mDev.setOutputReport(b[0],b , b.length);
	}
	

	public void disconnect(){
//		mDev.setOutputReport(arg0, arg1, arg2);
	}

}
