package interfaces;

import java.awt.event.MouseEvent;

public interface UIMouseReactive {
    void onMouseEnter(MouseEvent e);
    void onMouseLeave(MouseEvent e);
    void onMouseDown(MouseEvent e, boolean within);
    void onMouseUp(MouseEvent e, boolean within);
    boolean isEntered();
}
