
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class ProgressBarIndeterminateState extends State {
    ProgressBarIndeterminateState() {
        super("Indeterminate");
    }

    @Override protected boolean isInState(JComponent c) {

        return c instanceof JProgressBar &&
               ((JProgressBar)c).isIndeterminate();
               
    }
}

