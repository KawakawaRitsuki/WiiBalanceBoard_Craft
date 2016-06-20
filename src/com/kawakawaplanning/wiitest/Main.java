package com.kawakawaplanning.wiitest;

import java.util.List;

import purejavahidapi.DeviceRemovalListener;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;

public class Main{

	public static HidDeviceInfo devInfo;
	public static HidDevice dev;
	
	public static void main(String[] args) {
		try {
			List<HidDeviceInfo> devList = PureJavaHidApi.enumerateDevices();
			for (HidDeviceInfo info : devList) {
				if (info.getVendorId() == (short) 0x057E && info.getProductId() == (short) 0x0306) {
					devInfo = info;
					break;
				}
			}
			if (devInfo == null)
				System.err.println("device not found");
			else {
				dev = PureJavaHidApi.openDevice(devInfo.getPath());
				dev.setDeviceRemovalListener(new DeviceRemovalListener() {
					@Override
					public void onDeviceRemoval(HidDevice source) {
						System.out.println("Disconnected wiimote.");
					}
				});
				dev.setInputReportListener(new InputReportListener() {
					@Override
					public void onInputReport(HidDevice source, byte Id, byte[] data, int len) {
						
						String[] dataStr = new String[len];
						for (int i = 0; i < len; i++)
							dataStr[i] = Integer.toBinaryString(data[i]);
						
						
						if(dataStr[0].equals("110100")){//ヌンチャク接続中処理
							String status = dataStr[0];
							String button1 = dataStr[1];
							String button2 = dataStr[2];
							String nunchuckX = dataStr[3];
							String nunchuckY = dataStr[4];
							
							int axn = 0,ayn = 0;
							byte nunchuckXX = data[3];
							byte nunchuckYY = data[4];
							if (nunchuckXX > 0x90) { axn = (nunchuckXX - 0x90)/8; }
							if (nunchuckXX < 0x70) { axn = (nunchuckXX - 0x70)/8; }
							if (nunchuckYY > 0x90) { ayn = (nunchuckYY - 0x90)/8; }
							if (nunchuckYY < 0x70) { ayn = (nunchuckYY - 0x70)/8; }
							
							String button3;
							if (dataStr[8].length() >= 2)
								button3 = dataStr[8].substring(dataStr[8].length()-2,dataStr[8].length());//字数が少ない時に落ちる
							else
								button3 = dataStr[8];
							
//							System.out.print("status:"+status+"\tbutton1:"+button1+"\tbutton2:"+button2+"\tnunchuckX:"+nunchuckXX+"\tnunchuckY:"+nunchuckYY+"\tbutton3:"+button3);
							System.out.print("status:"+status+"\tbutton1:"+button1+"\tbutton2:"+button2+"\tnunchuckX:"+axn+"\tnunchuckY:"+ayn+"\tbutton3:"+button3);
//							System.out.print("status:"+status+"\tbutton1:"+button1+"\tbutton2:"+button2+"\tnunchuckX:"+Integer.parseInt(nunchuckX,2)+"\tnunchuckY:"+nunchuckY+"\tbutton3:"+button3);
						}else if(dataStr[0].equals("100010")){//起動時処理
							if(dataStr[len-1].equals("0")){//ヌンチャク
								setReportMode((byte) 0x34); 
								System.out.print("Connected wiimote. Connect nunchuck.");
							}else{
								setReportMode((byte) 0x30); 
								System.out.print("Connected wiimote. Not connect nunchuck.");
							}
						}else if(dataStr[0].equals("100000")){//ヌンチャク接続/切断時処理
							if(dataStr[3].equals("10000")){//ヌンチャク切断
								setReportMode((byte) 0x30); 
								System.out.print("Disconnected nunchuck.");
							}else if(dataStr[3].equals("10010")){//ヌンチャク接続
								setReportMode((byte) 0x34); 
								setNunchuck();
								System.out.print("Connected nunchuck.");
							}
						}else{
							for (int i = 0; i < len; i++)
								System.out.print(Integer.toBinaryString(data[i])+" ");
						}
						System.out.println();
					}
				});
				
				//両方押してない : 11
				//Zを押している : 00
				//Cを押している : 01
				//両方押している : 10

				setNunchuck();
				setPlayerID("1000");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setReportMode(byte mode){
		byte[] b = {0x12,0x00,mode};//下一桁がアクセラ？
		dev.setOutputReport(b[0],b , b.length);
	}
	
	public static void setPlayerID(String flag){
		byte[] b = {0x11,(byte) Integer.parseInt(new StringBuilder(flag).reverse()+"0000",2)};
		dev.setOutputReport(b[0],b , b.length);
	}
	
	public static void setNunchuck(){
		byte[] b = {0x16,0x04,(byte) 0xA4,0x00,0x40,0x01,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        
        dev.setOutputReport(b[0],b , b.length);
	}
	
//	public static void setNunchuck(){
//		byte[] b = {0x55,(byte) 0xA400F0};
//        dev.setOutputReport(b[0],b , b.length);
//        byte[] bb = {0x00,(byte) 0xA400FB};
//        
//        dev.setOutputReport(bb[0],bb , bb.length);
//	}
	
}
