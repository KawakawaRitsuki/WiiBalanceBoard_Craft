package com.kawakawaplanning.wiimocraft.wiimote;
import java.util.EventListener;

/**
 * OnBoardStateChangeListener
 * 
 * @author KawakawaRitsuki
 * @since 0.1
 */
public interface OnBoardStateChangeListener extends EventListener {
    void onChanged(int status);
}