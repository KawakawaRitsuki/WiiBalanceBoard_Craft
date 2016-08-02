package com.kawakawaplanning.winecraft.wiimote;
import java.util.EventListener;

public interface OnBoardStateChangeListener extends EventListener {
    void onChanged(int status);
}