
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class ToolBarNorthState extends State {
    ToolBarNorthState() {
        super("North");
    }

    @Override protected boolean isInState(JComponent c) {

        return (c instanceof JToolBar) &&
               NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)c) == BorderLayout.NORTH;
               
    }
}

