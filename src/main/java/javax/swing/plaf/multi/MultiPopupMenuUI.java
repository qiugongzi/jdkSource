
package javax.swing.plaf.multi;

import java.util.Vector;
import javax.swing.plaf.PopupMenuUI;
import java.awt.event.MouseEvent;
import javax.swing.Popup;
import javax.swing.JPopupMenu;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.accessibility.Accessible;


public class MultiPopupMenuUI extends PopupMenuUI {


    protected Vector uis = new Vector();

public ComponentUI[] getUIs() {
        return MultiLookAndFeel.uisToArray(uis);
    }

public boolean isPopupTrigger(MouseEvent a) {
        boolean returnValue =
            ((PopupMenuUI) (uis.elementAt(0))).isPopupTrigger(a);
        for (int i = 1; i < uis.size(); i++) {
            ((PopupMenuUI) (uis.elementAt(i))).isPopupTrigger(a);
        }
        return returnValue;
    }


    public Popup getPopup(JPopupMenu a, int b, int c) {
        Popup returnValue =
            ((PopupMenuUI) (uis.elementAt(0))).getPopup(a,b,c);
        for (int i = 1; i < uis.size(); i++) {
            ((PopupMenuUI) (uis.elementAt(i))).getPopup(a,b,c);
        }
        return returnValue;
    }

public boolean contains(JComponent a, int b, int c) {
        boolean returnValue =
            ((ComponentUI) (uis.elementAt(0))).contains(a,b,c);
        for (int i = 1; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).contains(a,b,c);
        }
        return returnValue;
    }


    public void update(Graphics a, JComponent b) {
        for (int i = 0; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).update(a,b);
        }
    }


    public static ComponentUI createUI(JComponent a) {
        ComponentUI mui = new MultiPopupMenuUI();
        return MultiLookAndFeel.createUIs(mui,
                                          ((MultiPopupMenuUI) mui).uis,
                                          a);
    }


    public void installUI(JComponent a) {
        for (int i = 0; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).installUI(a);
        }
    }


    public void uninstallUI(JComponent a) {
        for (int i = 0; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).uninstallUI(a);
        }
    }


    public void paint(Graphics a, JComponent b) {
        for (int i = 0; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).paint(a,b);
        }
    }


    public Dimension getPreferredSize(JComponent a) {
        Dimension returnValue =
            ((ComponentUI) (uis.elementAt(0))).getPreferredSize(a);
        for (int i = 1; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).getPreferredSize(a);
        }
        return returnValue;
    }


    public Dimension getMinimumSize(JComponent a) {
        Dimension returnValue =
            ((ComponentUI) (uis.elementAt(0))).getMinimumSize(a);
        for (int i = 1; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).getMinimumSize(a);
        }
        return returnValue;
    }


    public Dimension getMaximumSize(JComponent a) {
        Dimension returnValue =
            ((ComponentUI) (uis.elementAt(0))).getMaximumSize(a);
        for (int i = 1; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).getMaximumSize(a);
        }
        return returnValue;
    }


    public int getAccessibleChildrenCount(JComponent a) {
        int returnValue =
            ((ComponentUI) (uis.elementAt(0))).getAccessibleChildrenCount(a);
        for (int i = 1; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).getAccessibleChildrenCount(a);
        }
        return returnValue;
    }


    public Accessible getAccessibleChild(JComponent a, int b) {
        Accessible returnValue =
            ((ComponentUI) (uis.elementAt(0))).getAccessibleChild(a,b);
        for (int i = 1; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).getAccessibleChild(a,b);
        }
        return returnValue;
    }
}
