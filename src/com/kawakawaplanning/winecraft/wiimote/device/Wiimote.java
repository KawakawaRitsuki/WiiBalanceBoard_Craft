package com.kawakawaplanning.winecraft.wiimote.device;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import purejavahidapi.DeviceRemovalListener;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;

/**
 * Wiimote
 * Wiimoteのデータの送受信用クラス。
 * 
 * @author KawakawaRitsuki
 * @version 0.1
 */

public class Wiimote extends Device {
	
	public Wiimote(HidDeviceInfo info) {
		super(info);
	}
	
	/**
	 * WiimoteのプレイヤーLEDを指定します。
	 * パラメーターには0と1で形成された4桁の文字列を指定します。
	 * 1で点灯、0で消灯します。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param flag LEDの情報
	 * @throws IllegalArgumentException パラメータが不正だった際にスローされます。
	 */
	public void setPlayerLed(String flag){
		Pattern p = Pattern.compile("^[01]{4}$");
	    Matcher m = p.matcher(flag);
	    
	    if(m.find()){
	    	byte[] b = {0x11,(byte) Integer.parseInt(new StringBuilder(flag).reverse()+"0000",2)};
			mDev.setOutputReport(b[0],b , b.length);
	    }else{
	    	throw new IllegalArgumentException();
	    }
		
	}
	
	/**
	 * Wiimoteのバイブレータを動作させます。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param sec 動作させる時間(ms)
	 */
	// TODO:今のLED状態を取得しないと1になってしまう。
	public void vibrate(long sec){
		byte[] b = {0x11,(byte) Integer.parseInt("00010001",2)};
		mDev.setOutputReport(b[0],b , b.length);
		
		try {Thread.sleep(sec);} catch (InterruptedException e) {}
		
		byte[] b2 = {0x11,(byte) Integer.parseInt("00010000",2)};
		mDev.setOutputReport(b2[0],b2 , b2.length);
	}
	
	public final static byte WIMOTE_ONLY_REPORT_MODE = 0x30;
	public final static byte WIMOTE_NUNCHUCK_REPORT_MODE = 0x34;
	
	/**
	 * Wiimoteのリポートモードを指定します。
	 * パラメータには定数を指定することをおすすめします。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param mode リポートモード番号
	 */
	public void setReportMode(byte mode){
		byte[] b = {0x12,0x00,mode};
		mDev.setOutputReport(b[0],b , b.length);
	}

	/**
	 * Wiimoteに接続されたヌンチャクを動作させる際に呼び出します。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 */
	public void setNunchuckData(){
		byte[] b = {0x16,0x04,(byte) 0xA4,0x00,0x40,0x01,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        
        mDev.setOutputReport(b[0],b , b.length);
	}
	
	/**
	 * Wiimoteが切断された際に呼び出されるリスナを設定します。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param listener 設定するリスナ
	 */
	public void setDeviceRemovalListener(DeviceRemovalListener listener){
		mDev.setDeviceRemovalListener(listener);
	}
	
	/**
	 * Wiimoteからデータを受信した際に呼び出されるリスナを設定します。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param listener 設定するリスナ
	 */
	public void setInputReportListener(InputReportListener listener){
		mDev.setInputReportListener(listener);
	}
	
}
