package com.kawakawaplanning.winecraft.wiimote;

public class ButtonManager {
	
	private boolean A     = false;
	private boolean B     = false;
	private boolean left  = false;
	private boolean up    = false;
	private boolean down  = false;
	private boolean right = false;
	private boolean home  = false;
	private boolean plus  = false;
	private boolean minus = false;
	private boolean b1    = false;
	private boolean b2    = false;
	private boolean Z     = false;
	private boolean C     = false;
	private boolean power = false;
	
	public static final int BUTTON_A             = 0;
	public static final int BUTTON_B             = 1;
	public static final int BUTTON_LEFT          = 2;
	public static final int BUTTON_UP            = 3;
	public static final int BUTTON_DOWN          = 4;
	public static final int BUTTON_RIGHT         = 5;
	public static final int BUTTON_HOME          = 6;
	public static final int BUTTON_PLUS          = 7;
	public static final int BUTTON_MINUS         = 8;
	public static final int BUTTON_1             = 9;
	public static final int BUTTON_2             = 10;
	public static final int BUTTON_Z             = 11;
	public static final int BUTTON_C             = 12;
	public static final int BUTTON_BALANCE_POWER = 13;
	
	private OnButtonListener buttonListener = null;
	
	public void check(int button1){
		power = checkButton( button1 == 8, power, BUTTON_BALANCE_POWER);
	}
	
	public void check(String button1,String button2){
		button1 = String.format("%5s", button1).replace(" ", "0");
		button2 = String.format("%32s", button2).replace(" ", "0");
		button2 = button2.substring(button2.length()-8);

		for(int i = 0;button1.length() > i;i++){
			boolean btn = getCharactor(button1, i);
			
    		switch (i) {
				case 0:
					plus = checkButton(btn, plus, BUTTON_PLUS);
					break;
				case 1:
					up = checkButton(btn, up, BUTTON_UP);
					break;
				case 2:
					down = checkButton(btn, down, BUTTON_DOWN);
					break;
				case 3:
					right = checkButton(btn, right, BUTTON_RIGHT);
					break;
				case 4:
					left = checkButton(btn, left, BUTTON_LEFT);
					break;
			}
		}
		
		for(int i = 0;button2.length() > i;i++){
			boolean btn = getCharactor(button2, i);
			
    		switch (i) {
				case 0:
					home = checkButton(btn, home, BUTTON_HOME);
					break;
				case 3:
					minus = checkButton(btn, minus, BUTTON_MINUS);
					break;
				case 4:
					A = checkButton(btn, A, BUTTON_A);
					break;
				case 5:
					B = checkButton(btn, B, BUTTON_B);
					break;
				case 6:
					b1 = checkButton(btn, b1, BUTTON_1);
					break;
				case 7:
					b2 = checkButton(btn, b2, BUTTON_2);
					break;
			}
		}
	}
	
	public void check(String button1,String button2,String button3){
		button1 = String.format("%5s", button1).replace(" ", "0");
		button2 = String.format("%32s", button2).replace(" ", "0");
		button2 = button2.substring(button2.length()-8);
		
//		System.out.println(button1 + "\t" +button2 + "\t" +button3 );

		for(int i = 0;button1.length() > i;i++){
			boolean btn = getCharactor(button1, i);
//			System.out.println(btn);
			
    		switch (i) {
				case 0:
					plus = checkButton(btn, plus, BUTTON_PLUS);
					break;
				case 1:
					up = checkButton(btn, up, BUTTON_UP);
					break;
				case 2:
					down = checkButton(btn, down, BUTTON_DOWN);
					break;
				case 3:
					right = checkButton(btn, right, BUTTON_RIGHT);
					break;
				case 4:
					left = checkButton(btn, left, BUTTON_LEFT);
					break;
			}
		}
		
		for(int i = 0;button2.length() > i;i++){
			boolean btn = getCharactor(button2, i);
			
    		switch (i) {
				case 0:
					home = checkButton(btn, home, BUTTON_HOME);
					break;
				case 3:
					minus = checkButton(btn, minus, BUTTON_MINUS);
					break;
				case 4:
					A = checkButton(btn, A, BUTTON_A);
					break;
				case 5:
					B = checkButton(btn, B, BUTTON_B);
					break;
				case 6:
					b1 = checkButton(btn, b1, BUTTON_1);
					break;
				case 7:
					b2 = checkButton(btn, b2, BUTTON_2);
					break;
			}
		}
		
		if(button3.length() >= 2){
			switch (button3.substring(button3.length()-2,button3.length())) {
			case "00":
				Z = checkButton(true, Z, BUTTON_Z);
				C = checkButton(false, C, BUTTON_C);
				break;
			case "01":
				Z = checkButton(false, Z, BUTTON_Z);
				C = checkButton(true, C, BUTTON_C);
				break;
			case "10":
				Z = checkButton(true, Z, BUTTON_Z);
				C = checkButton(true, C, BUTTON_C);
				break;
			case "11":
				Z = checkButton(false, Z, BUTTON_Z);
				C = checkButton(false, C, BUTTON_C);
				break;
			}
		}
	}
	
	private boolean checkButton(boolean btn,boolean flag,int id){
		if(btn != flag){
			if(btn)
//				if(buttonListener != null)
					buttonListener.onPushed(id);
			else
//				if(buttonListener != null)
					buttonListener.onReleased(id);
		}
		return btn;
	}
	
	public void setOnButtonListener(OnButtonListener listener){
        this.buttonListener = listener;
    }
	
	public void removeOnButtonListener(){
        this.buttonListener = null;
    }

	private boolean getCharactor(String button,int place) {
		if(button.substring(place,place+1).equals("1"))
			return true;
		else
			return false;
	}
}
