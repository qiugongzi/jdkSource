
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class TableHeaderRendererSortedState extends State {
    TableHeaderRendererSortedState() {
        super("Sorted");
    }

    @Override protected boolean isInState(JComponent c) {

                    String sortOrder = (String)c.getClientProperty("Table.sortOrder");
                    return  sortOrder != null && ("ASCENDING".equals(sortOrder) || "DESCENDING".equals(sortOrder)); 
    }
}

