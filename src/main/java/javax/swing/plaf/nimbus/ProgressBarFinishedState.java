
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class ProgressBarFinishedState extends State {
    ProgressBarFinishedState() {
        super("Finished");
    }

    @Override protected boolean isInState(JComponent c) {

        return c instanceof JProgressBar &&
               ((JProgressBar)c).getPercentComplete() == 1.0;
                
    }
}

