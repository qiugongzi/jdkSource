
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class TextAreaNotInScrollPaneState extends State {
    TextAreaNotInScrollPaneState() {
        super("NotInScrollPane");
    }

    @Override protected boolean isInState(JComponent c) {

                          return !(c.getParent() instanceof javax.swing.JViewport);
    }
}

