
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class ToolBarWestState extends State {
    ToolBarWestState() {
        super("West");
    }

    @Override protected boolean isInState(JComponent c) {

        return (c instanceof JToolBar) &&
               NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)c) == BorderLayout.WEST;
               
    }
}

