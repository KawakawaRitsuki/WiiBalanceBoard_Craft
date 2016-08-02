package com.kawakawaplanning.wiimocraft.wiimote;
import java.util.EventListener;

/**
 * OnButtonListener
 * 
 * @author KawakawaRitsuki
 * @since 0.1
 */
public interface OnButtonListener extends EventListener {
    void onPushed(int button);
    void onReleased(int button);
}