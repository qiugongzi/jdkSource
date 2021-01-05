
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class InternalFrameWindowFocusedState extends State {
    InternalFrameWindowFocusedState() {
        super("WindowFocused");
    }

    @Override protected boolean isInState(JComponent c) {

                         return c instanceof JInternalFrame && ((JInternalFrame)c).isSelected();
    }
}

