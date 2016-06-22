package com.kawakawaplanning.winecraft.wiimote;
import java.util.EventListener;

public interface OnButtonListener extends EventListener {
    void onPushed(int button);
    void onReleased(int button);
}