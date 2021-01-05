
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class ToolBarSouthState extends State {
    ToolBarSouthState() {
        super("South");
    }

    @Override protected boolean isInState(JComponent c) {

        return (c instanceof JToolBar) &&
               NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)c) == BorderLayout.SOUTH;
               
    }
}

