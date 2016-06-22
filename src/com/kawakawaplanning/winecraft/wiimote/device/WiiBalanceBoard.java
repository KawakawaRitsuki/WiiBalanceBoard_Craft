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

class WiiBalanceBoard extends Device {
	
	public WiiBalanceBoard(HidDeviceInfo info) {
		super(info);
		// TODO Auto-generated constructor stub
	}

	private HidDevice mDev;

	
	public boolean connect(int timeout){
		if (mDev == null){
			List<HidDeviceInfo> devList;
			HidDeviceInfo devInfo = null;
			System.out.println("Searching now...\nPlease push wiimote 1 & 2 button.");
			
			if(timeout == 0){
				device: while(true){
					devList = PureJavaHidApi.enumerateDevices();
					for (HidDeviceInfo info : devList) {
						if (info.getVendorId() == (short) 0x057E && info.getProductId() == (short) 0x0306) {
							devInfo = info;
							break device;
						}
					}
					try { Thread.sleep(1000); } catch (InterruptedException e) {}
				}
			}else{
				device: for(int i = 0;i < timeout;i++){
					devList = PureJavaHidApi.enumerateDevices();
					for (HidDeviceInfo info : devList) {
						if (info.getVendorId() == (short) 0x057E && info.getProductId() == (short) 0x0306) {
							devInfo = info;
							break device;
						}
					}
					try { Thread.sleep(1000); } catch (InterruptedException e) {}
				}
			}
			
			if (devInfo == null){
				System.err.println("Wiimote not found.");
				return false;
			} else {
				System.out.println("Found wiimote.");
				try {
					mDev = PureJavaHidApi.openDevice(devInfo.getPath());
					System.out.println("Connected wiimote.");
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("Not connected wiimote.");
					return false;
				}
				return true;
			}
		}else{
			System.err.println("Already connected.");
			return true;
		}
	}
	
	public boolean isConnected(){
		return mDev != null;
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
	
	public void requestStatus(){
		byte[] b = {0x15,0x00};
		mDev.setOutputReport(b[0],b , b.length);
	}
	
}
