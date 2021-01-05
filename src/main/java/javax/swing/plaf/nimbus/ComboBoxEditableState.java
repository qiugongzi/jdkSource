
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class ComboBoxEditableState extends State {
    ComboBoxEditableState() {
        super("Editable");
    }

    @Override protected boolean isInState(JComponent c) {

        return c instanceof JComboBox && ((JComboBox)c).isEditable();
                
    }
}

