package com.kawakawaplanning.winecraft.wiimote;

public class WalkManager {

	public static final int BOARD_BOTH  = 0;
	public static final int BOARD_LEFT  = 1;
	public static final int BOARD_RIGHT = 2;
	
	public static final int WALK_THRESHOLD = 10;

	public int boardStatus = BOARD_BOTH;
	public int beforeBoardState = 0;
	
	private OnBoardStateChangeListener boardListener = null;
	
	public WalkManager() {
		// TODO Auto-generated constructor stub
	}

	public void checkWalkStatus(int right,int left){
		if     (right > WALK_THRESHOLD && left <= WALK_THRESHOLD){
			if(boardStatus != BOARD_RIGHT && beforeBoardState == BOARD_RIGHT){
				boardStatus = BOARD_RIGHT;
				boardListener.onChanged(boardStatus);
			}
			beforeBoardState = BOARD_RIGHT;
			
		}else if(right <= WALK_THRESHOLD && left > WALK_THRESHOLD){
			if(boardStatus != BOARD_LEFT && beforeBoardState == BOARD_LEFT){
				boardStatus = BOARD_LEFT;
				boardListener.onChanged(boardStatus);
			}
			beforeBoardState = BOARD_LEFT;
			
		}else {
			if(Math.abs(right - left) >= 5){
				if(right > left){
					if(boardStatus != BOARD_RIGHT && beforeBoardState == BOARD_RIGHT){
						boardStatus = BOARD_RIGHT;
						boardListener.onChanged(boardStatus);
					}
					beforeBoardState = BOARD_RIGHT;
				}else{
					if(boardStatus != BOARD_LEFT && beforeBoardState == BOARD_LEFT){
						boardStatus = BOARD_LEFT;
						boardListener.onChanged(boardStatus);
					}
					beforeBoardState = BOARD_LEFT;
				}
			}else{
				if(boardStatus != BOARD_BOTH && beforeBoardState == BOARD_BOTH){
					boardStatus = BOARD_BOTH;
					boardListener.onChanged(boardStatus);
				}
				beforeBoardState = BOARD_BOTH;
			}
		}
	}
	
	public void setBoardStateChangeListener(OnBoardStateChangeListener listener){
        this.boardListener = listener;
    }
	
	public void removeBoardStateChangeListener(){
        this.boardListener = null;
    }

}
