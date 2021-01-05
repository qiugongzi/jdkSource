
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class ToolBarEastState extends State {
    ToolBarEastState() {
        super("East");
    }

    @Override protected boolean isInState(JComponent c) {

        return (c instanceof JToolBar) &&
               NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)c) == BorderLayout.EAST;
               
    }
}

