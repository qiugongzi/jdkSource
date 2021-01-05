
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class InternalFrameTitlePaneWindowFocusedState extends State {
    InternalFrameTitlePaneWindowFocusedState() {
        super("WindowFocused");
    }

    @Override protected boolean isInState(JComponent c) {

                         return c instanceof JInternalFrame && ((JInternalFrame)c).isSelected();
    }
}

