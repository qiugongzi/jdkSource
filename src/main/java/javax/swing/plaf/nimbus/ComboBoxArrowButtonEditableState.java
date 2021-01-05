
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class ComboBoxArrowButtonEditableState extends State {
    ComboBoxArrowButtonEditableState() {
        super("Editable");
    }

    @Override protected boolean isInState(JComponent c) {

                                Component parent = c.getParent();
                                return parent instanceof JComboBox && ((JComboBox)parent).isEditable();
    }
}

