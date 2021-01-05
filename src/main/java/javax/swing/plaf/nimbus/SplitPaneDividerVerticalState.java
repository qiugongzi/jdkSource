
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class SplitPaneDividerVerticalState extends State {
    SplitPaneDividerVerticalState() {
        super("Vertical");
    }

    @Override protected boolean isInState(JComponent c) {

                        return c instanceof JSplitPane && (((JSplitPane)c).getOrientation() == 1);
    }
}

