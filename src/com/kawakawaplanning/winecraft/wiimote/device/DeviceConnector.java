package com.kawakawaplanning.winecraft.wiimote.device;

import java.io.IOException;
import java.util.List;

import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;

public class DeviceConnector {
	
	private HidDeviceInfo devInfo;
	private int device;
	
	public static final int NOT_CONNECTED    = 0;
	public static final int WIIMOTE          = 1;
	public static final int WIIBALANCE_BOARD = 2;
	public static final int OTHER_DEVICE     = 3;

	public boolean connect(int timeout,int deviceId){
		if (devInfo == null){
			List<HidDeviceInfo> devList;
			devInfo = null;
			if (deviceId == WIIMOTE){
				System.out.println("Searching now...\n\nPlease push wiimote 1 & 2 button.");
			}else if(deviceId == WIIBALANCE_BOARD){
				System.out.println("Searching now...\n\nPlease push balance board power button.");
			}else{
				return false;
			}
			
			if(timeout == 0){
				device: while(true){
					devList = PureJavaHidApi.enumerateDevices();
					for (HidDeviceInfo info : devList) {
						if (info.getVendorId() == (short) 0x057E && info.getProductId() == (short) 0x0306) {
							devInfo = info;
							
							if(info.getProductString().equals("Nintendo RVL-CNT-01")){
								if(deviceId == WIIMOTE){
									device = WIIMOTE;
									break device;
								}
							}else if(info.getProductString().equals("Nintendo RVL-WBC-01")){
								if(deviceId == WIIBALANCE_BOARD){
									device = WIIBALANCE_BOARD;
									break device;
								}
							}else{
								if(deviceId == OTHER_DEVICE){
									device = OTHER_DEVICE;
									break device;
								}
							}
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
							
							if(info.getProductString().equals("Nintendo RVL-CNT-01")){
								device = WIIMOTE;
							}else if(info.getProductString().equals("Nintendo RVL-WBC-01")){
								device = WIIBALANCE_BOARD;
							}else{
								device = OTHER_DEVICE;
							}
							break device;
						}
					}
					try { Thread.sleep(1000); } catch (InterruptedException e) {}
				}
			}
			if (devInfo == null){
				System.out.println("Device not found.");
				return false;
			} else {
				System.out.println("Found device.");
				if(device == WIIMOTE){
					System.out.println("This device is Wiimote.");
				}else if(device == WIIBALANCE_BOARD){
					System.out.println("This device is WiiBalanceBoard");
				}else{
					System.out.println("This device is unknown.");
				}
				return true;
			}
		}else{
			System.err.println("Already connected.");
			return true;
		}
	}
	
	public boolean isConnected(){
		return device != NOT_CONNECTED;
	}
	
	// その引数のデバイスかどうか
	public boolean isDevice(int dev){
		return device == dev;
	}
	
	public HidDeviceInfo getDevInfo(){
		return devInfo;
	}

}
