package com.kawakawaplanning.wiimocraft;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.jws.WebParam.Mode;

import com.kawakawaplanning.wiimocraft.wiimote.ButtonManager;
import com.kawakawaplanning.wiimocraft.wiimote.MouseManager;
import com.kawakawaplanning.wiimocraft.wiimote.OnBoardStateChangeListener;
import com.kawakawaplanning.wiimocraft.wiimote.OnButtonListener;
import com.kawakawaplanning.wiimocraft.wiimote.WalkManager;
import com.kawakawaplanning.wiimocraft.wiimote.device.DeviceConnector;
import com.kawakawaplanning.wiimocraft.wiimote.device.WiiBalanceBoard;
import com.kawakawaplanning.wiimocraft.wiimote.device.Wiimote;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import purejavahidapi.DeviceRemovalListener;
import purejavahidapi.HidDevice;
import purejavahidapi.InputReportListener;

public class Main {
	// TODO: isバランスボード
	// TODO: 再接続処理
	// TODO: DisconnectedListener
	// TODO: バランスボード切断
	
	
	//モードごとの部分
	//ヌンチャク、Wiimote　Balance

	public static int displayWidth;
	public static int displayHeight;
	public static Robot r;

	public static boolean isConnectedNunchuck = false;
	public static boolean isConnectedBalanceBoard = false;

	public static int lf = 0;
	public static int lb = 0;
	public static int rf = 0;
	public static int rb = 0;

	public static int cabLf = 0;
	public static int cabLb = 0;
	public static int cabRf = 0;
	public static int cabRb = 0;

	public static int right = 0;
	public static int left = 0;

	public static final int balanceDisconTime = 3000;

	public static long current = 0;
	public static long balanceDisconFlag = 0;

	public static ButtonManager bm;
	public static WalkManager wm;
	public static CraftManager cm;
	public static MouseManager mm;

	public static int mode;
	public static int status;
	public static int boat_status;

	public static final int BOAT_RUNNING = 0;
	public static final int BOAT_STOPING = 1;

	public static final int MODE_MINECRAFT_STANDARD = 0;
	public static final int MODE_MINECRAFT_BOAT = 1;
	public static final int MODE_MOUSE = 2;

	public static final int STATUS_NOT_CONNECTED_EXTENSIONS = 0;
	public static final int STATUS_WAITING = 1;
	public static final int STATUS_PLAYING = 2;

	public static Wiimote wiimote;
	public static WiiBalanceBoard balance;

	public static void main(String[] args) {
		
		mode = MODE_MINECRAFT_STANDARD;
		status = STATUS_NOT_CONNECTED_EXTENSIONS;
		boat_status = BOAT_STOPING;

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		displayWidth = d.width;
		displayHeight = d.height;

		initManager();
		wiimoteConnect();
		balanceConnect();
		
	}

	public static void initManager() {
		

		try {
			r = new Robot();
		} catch (AWTException e) {
		}
		
		mm = MouseManager.INSTANCE;
		cm = new CraftManager(r);
		bm = new ButtonManager();
		wm = new WalkManager();
		bm.setOnButtonListener(new OnButtonListener() {

			@Override
			public void onReleased(int button) {
				if (status == STATUS_PLAYING) {
					switch (mode) {
					case MODE_MINECRAFT_STANDARD:
					case MODE_MINECRAFT_BOAT:
					
						switch (button) {
						case ButtonManager.BUTTON_A:
							mm.release(displayHeight, true);
							break;
						case ButtonManager.BUTTON_B:
							r.keyRelease(KeyEvent.VK_SPACE);
							break;
						case ButtonManager.BUTTON_LEFT:
							r.keyRelease(KeyEvent.VK_A);
							break;
						case ButtonManager.BUTTON_UP:
							r.keyRelease(KeyEvent.VK_W);
							break;
						case ButtonManager.BUTTON_RIGHT:
							r.keyRelease(KeyEvent.VK_D);
							break;
						case ButtonManager.BUTTON_DOWN:
							r.keyRelease(KeyEvent.VK_S);
							break;
						case ButtonManager.BUTTON_HOME:
							r.keyRelease(KeyEvent.VK_ESCAPE);
							break;
						case ButtonManager.BUTTON_Z:
							mm.release(displayHeight, false);
							break;
						case ButtonManager.BUTTON_C:
							mm.release(displayHeight, true);
							break;
						}
						break;
					}
					
				} else {

				}
				switch (button) {
					case ButtonManager.BUTTON_BALANCE_POWER:
						if (System.currentTimeMillis() - balanceDisconFlag <= balanceDisconTime) {
							set0kg();
							System.out.println("Calibration successed.");							
						}else{
							//TODO: バランスボードの切断をどうにかする
							balance.disconnect();
						}
						
						break;
				}
			}

			@Override
			public void onPushed(int button) {
				if (status == STATUS_PLAYING) {
					switch (mode) {
					case MODE_MINECRAFT_STANDARD:
					case MODE_MINECRAFT_BOAT:
						switch (button) {
						case ButtonManager.BUTTON_A:
							mm.press(displayHeight, true);
							break;
						case ButtonManager.BUTTON_B:
							r.keyPress(KeyEvent.VK_SPACE);
							break;
						case ButtonManager.BUTTON_LEFT:
							r.keyPress(KeyEvent.VK_A);
							break;
						case ButtonManager.BUTTON_UP:
							r.keyPress(KeyEvent.VK_W);
							break;
						case ButtonManager.BUTTON_RIGHT:
							r.keyPress(KeyEvent.VK_D);
							break;
						case ButtonManager.BUTTON_DOWN:
							r.keyPress(KeyEvent.VK_S);
							break;
						case ButtonManager.BUTTON_HOME:
							r.keyPress(KeyEvent.VK_ESCAPE);
							break;
						case ButtonManager.BUTTON_Z:
							mm.press(displayHeight, false);
							break;
						case ButtonManager.BUTTON_C:
							mm.press(displayHeight, true);
							break;
						}
						break;
					}
					
				} else {
					switch (button) {
						case ButtonManager.BUTTON_1:
							changeMode();
							checkStatus();
							changePlayerLED();
							break;
					}
				}
				switch (button) {
					case ButtonManager.BUTTON_2:
						if (status == STATUS_WAITING) {
							status = STATUS_PLAYING;
							switch (mode) {
							case MODE_MINECRAFT_STANDARD:
								cm.startStandard();
								break;

							case MODE_MINECRAFT_BOAT:
								cm.startBoat();
								break;
							}
						}else if (status == STATUS_PLAYING){
							status = STATUS_WAITING;
						}
						changePlayerLED();
						break;
					case ButtonManager.BUTTON_BALANCE_POWER:
						balanceDisconFlag = System.currentTimeMillis();
						break;
				}
			}
		});

		wm.setBoardStateChangeListener(new OnBoardStateChangeListener() {
			@Override
			public void onChanged(int status) {
				switch (status) {
				case WalkManager.BOARD_RIGHT:
					switch (mode) {
					case MODE_MINECRAFT_STANDARD:
						cm.walk();
						break;

					case MODE_MINECRAFT_BOAT:
						cm.forward();
						break;
					}
					break;
				case WalkManager.BOARD_LEFT:
					switch (mode) {
					case MODE_MINECRAFT_BOAT:
						cm.back();
						break;
					}
					break;
				case WalkManager.BOARD_BOTH:
					switch (mode) {
					case MODE_MINECRAFT_BOAT:
						cm.stopMove();
						break;
					}
					break;
				}
			}
		});

	}

	public static void balanceConnect() {
		DeviceConnector balanceDC = new DeviceConnector();
		balanceDC.connect(0, DeviceConnector.WIIBALANCE_BOARD);

		balance = new WiiBalanceBoard(balanceDC.getDevInfo());
		balance.setInputReportListener(new InputReportListener() {
			@Override
			public void onInputReport(HidDevice source, byte Id, byte[] b, int len) {
				if ((System.currentTimeMillis() - current) >= 50) {
					bm.check(Byte.toUnsignedInt(b[2]));

					rf = Byte.toUnsignedInt(b[3]);
					rb = Byte.toUnsignedInt(b[5]);
					lf = Byte.toUnsignedInt(b[7]);
					lb = Byte.toUnsignedInt(b[9]);

					right = rf + rb - cabRf - cabRb;
					left = lf + lb - cabLf - cabLb;

					if (cabRf == 0 && cabRb == 0 && cabLf == 0 && cabLb == 0)
						set0kg();

					if (status == STATUS_PLAYING)
						wm.checkWalkStatus(right, left);

					current = System.currentTimeMillis();
				}
			}
		});

		balance.setDeviceRemovalListener(new DeviceRemovalListener() {
			@Override
			public void onDeviceRemoval(HidDevice arg0) {
				balanceConnect();
			}
		});

		balance.setPlayerID(true);
		balance.setReportMode((byte) 0x32);
	}

	public static void wiimoteConnect() {
		DeviceConnector wiimoteDC = new DeviceConnector();
		wiimoteDC.connect(0, DeviceConnector.WIIMOTE);

		wiimote = new Wiimote(wiimoteDC.getDevInfo());
		wiimote.setInputReportListener(new InputReportListener() {
			@Override
			public void onInputReport(HidDevice source, byte Id, byte[] b, int len) {

				String[] data = new String[len];
				for (int i = 0; i < len; i++)
					data[i] = Integer.toBinaryString(b[i]);

				if (data[0].equals("110100")) { // ヌンチャク接続時

					bm.check(data[1], data[2], data[8]);

					if (status == STATUS_PLAYING) {
						int axn = 0;
						int ayn = 0;

						byte nunchuckXX = (byte) ((b[3] ^ 0x17) + 0x17);
						byte nunchuckYY = (byte) ((b[4] ^ 0x17) + 0x17);

						if (nunchuckXX > 0x90)
							axn = (nunchuckXX - 0x90) / 8;
						if (nunchuckXX < 0x70)
							axn = (nunchuckXX - 0x70) / 8;
						if (nunchuckYY > 0x90)
							ayn = (nunchuckYY - 0x90) / 8;
						if (nunchuckYY < 0x70)
							ayn = (nunchuckYY - 0x70) / 8;

						if (axn < -10)
							axn = 30 + axn;
						if (ayn < -10)
							ayn = 30 + ayn;
						if (axn == 1 || axn == -1)
							axn = 0;
						if (ayn == 1 || ayn == -1)
							ayn = 0;

						Point mp = MouseInfo.getPointerInfo().getLocation();

						if (displayHeight > (mp.y - ayn) && 0 < (mp.y - ayn) && displayWidth > (mp.x + axn)
								&& 0 < (mp.x + axn) && (axn != 0 || ayn != 0))
							mm.move(displayWidth, displayHeight, axn, ayn * -1);

						if (mp.y < 15)
							r.mouseMove(mp.x, displayHeight - 20);
						else if (mp.y > displayHeight - 15)
							r.mouseMove(mp.x, 20);

						if (mp.x < 15)
							r.mouseMove(displayWidth - 20, mp.y);
						else if (mp.x > displayWidth - 15)
							r.mouseMove(20, mp.y);
					}

				} else if (data[0].equals("110000")) { // ヌンチャク未接続時

					bm.check(data[1], data[2]);

				} else if (data[0].equals("100000")) {// 情報請求時のレスポンスなど

					String connectedData = String.format("%10s", data[3]).replace(" ", "0");
					connectedData = connectedData.substring(connectedData.length() - 2, connectedData.length());

					switch (connectedData) {
					case "00":
						wiimote.setReportMode((byte) 0x30);
						isConnectedNunchuck = false;

						System.out.println("Disconnected nunchuck.");
						break;
					case "10":
						wiimote.setReportMode((byte) 0x34);
						wiimote.setNunchuckData();
						isConnectedNunchuck = true;

						System.out.println("Connected nunchuck.");
						break;
					}

					checkStatus();
					changePlayerLED();
				} else {
					for (int i = 0; i < len; i++)
						System.out.print(Integer.toBinaryString(b[i]) + " ");
					System.out.println();
				}
			}
		});
		wiimote.setDeviceRemovalListener(new DeviceRemovalListener() {
			@Override
			public void onDeviceRemoval(HidDevice arg0) {
				wiimoteConnect();
			}
		});
		wiimote.vibrate(250);
		wiimote.requestStatus();
	}

	public static void set0kg() {
		cabRf = rf;
		cabRb = rb;
		cabLf = lf;
		cabLb = lb;
	}

	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}

	public static void checkStatus() {
		// TODO: isConnectedBalanceBoard
		if (isConnectedNunchuck) {
			status = STATUS_WAITING;
		} else {
			status = STATUS_NOT_CONNECTED_EXTENSIONS;
		}
	}

	public static void changePlayerLED() {
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
		case MODE_MINECRAFT_STANDARD:
			led = led + "10";
			break;
		case MODE_MINECRAFT_BOAT:
			led = led + "01";
			break;
		case MODE_MOUSE:
			led = led + "11";
			break;
		}
		wiimote.setPlayerLed(led);
	}

	public static void changeMode() {
		if (mode == MODE_MINECRAFT_STANDARD) {
			mode = MODE_MINECRAFT_BOAT;
		} else if (mode == MODE_MINECRAFT_BOAT) {
			mode = MODE_MOUSE;
		} else if (mode == MODE_MOUSE) {
			mode = MODE_MINECRAFT_STANDARD;
		}
	}

}
