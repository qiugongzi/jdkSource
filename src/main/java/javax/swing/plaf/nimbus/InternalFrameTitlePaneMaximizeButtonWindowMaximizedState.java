
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class InternalFrameTitlePaneMaximizeButtonWindowMaximizedState extends State {
    InternalFrameTitlePaneMaximizeButtonWindowMaximizedState() {
        super("WindowMaximized");
    }

    @Override protected boolean isInState(JComponent c) {

                               Component parent = c;
                               while (parent.getParent() != null) {
                                   if (parent instanceof JInternalFrame) {
                                       break;
                                   }
                                   parent = parent.getParent();
                               }
                               if (parent instanceof JInternalFrame) {
                                   return ((JInternalFrame)parent).isMaximum();
                               }
                               return false;
    }
}

