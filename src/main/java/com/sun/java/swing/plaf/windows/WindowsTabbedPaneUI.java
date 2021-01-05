

package com.sun.java.swing.plaf.windows;

import java.awt.*;

import javax.swing.plaf.basic.*;
import javax.swing.plaf.*;
import javax.swing.*;
import java.util.Set;
import java.util.HashSet;
import java.awt.event.*;

import static com.sun.java.swing.plaf.windows.TMSchema.*;
import static com.sun.java.swing.plaf.windows.XPStyle.Skin;



public class WindowsTabbedPaneUI extends BasicTabbedPaneUI {

    private static Set<KeyStroke> managingFocusForwardTraversalKeys;


    private static Set<KeyStroke> managingFocusBackwardTraversalKeys;

    private boolean contentOpaque = true;

    protected void installDefaults() {
        super.installDefaults();
        contentOpaque = UIManager.getBoolean("TabbedPane.contentOpaque");

        if (managingFocusForwardTraversalKeys==null) {
            managingFocusForwardTraversalKeys = new HashSet<KeyStroke>();
            managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
        }
        tabPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, managingFocusForwardTraversalKeys);
        if (managingFocusBackwardTraversalKeys==null) {
            managingFocusBackwardTraversalKeys = new HashSet<KeyStroke>();
            managingFocusBackwardTraversalKeys.add( KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK));
        }
        tabPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, managingFocusBackwardTraversalKeys);
    }

    protected void uninstallDefaults() {
        tabPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        tabPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        super.uninstallDefaults();
    }

    public static ComponentUI createUI(JComponent c) {
        return new WindowsTabbedPaneUI();
    }

    protected void setRolloverTab(int index) {
        if (XPStyle.getXP() != null) {
            int oldRolloverTab = getRolloverTab();
            super.setRolloverTab(index);
            Rectangle r1 = null;
            Rectangle r2 = null;
            if ( (oldRolloverTab >= 0) && (oldRolloverTab < tabPane.getTabCount()) ) {
                r1 = getTabBounds(tabPane, oldRolloverTab);
            }
            if (index >= 0) {
                r2 = getTabBounds(tabPane, index);
            }
            if (r1 != null) {
                if (r2 != null) {
                    tabPane.repaint(r1.union(r2));
                } else {
                    tabPane.repaint(r1);
                }
            } else if (r2 != null) {
                tabPane.repaint(r2);
            }
        }
    }

    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
        XPStyle xp = XPStyle.getXP();
        if (xp != null && (contentOpaque || tabPane.isOpaque())) {
            Skin skin = xp.getSkin(tabPane, Part.TABP_PANE);
            if (skin != null) {
                Insets insets = tabPane.getInsets();
                Insets tabAreaInsets = UIManager.getInsets("TabbedPane.tabAreaInsets");
                int x = insets.left;
                int y = insets.top;
                int w = tabPane.getWidth() - insets.right - insets.left;
                int h = tabPane.getHeight() - insets.top - insets.bottom;

                if (tabPlacement == LEFT || tabPlacement == RIGHT) {
                    int tabWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                    if (tabPlacement == LEFT) {
                        x += (tabWidth - tabAreaInsets.bottom);
                    }
                    w -= (tabWidth - tabAreaInsets.bottom);
                } else {
                    int tabHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                    if (tabPlacement == TOP) {
                        y += (tabHeight - tabAreaInsets.bottom);
                    }
                    h -= (tabHeight - tabAreaInsets.bottom);
                }

                paintRotatedSkin(g, skin, tabPlacement, x, y, w, h, null);
                return;
            }
        }
        super.paintContentBorder(g, tabPlacement, selectedIndex);
    }

    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected ) {
        if (XPStyle.getXP() == null) {
            super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
        }
    }

    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                  int x, int y, int w, int h, boolean isSelected ) {
        XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            Part part;

            int tabCount = tabPane.getTabCount();
            int tabRun = getRunForTab(tabCount, tabIndex);
            if (tabRuns[tabRun] == tabIndex) {
                part = Part.TABP_TABITEMLEFTEDGE;
            } else if (tabCount > 1 && lastTabInRun(tabCount, tabRun) == tabIndex) {
                part = Part.TABP_TABITEMRIGHTEDGE;
                if (isSelected) {
                    if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                        w++;
                    } else {
                        h++;
                    }
                }
            } else {
                part = Part.TABP_TABITEM;
            }

            State state = State.NORMAL;
            if (isSelected) {
                state = State.SELECTED;
            } else if (tabIndex == getRolloverTab()) {
                state = State.HOT;
            }

            paintRotatedSkin(g, xp.getSkin(tabPane, part), tabPlacement, x, y, w, h, state);
        } else {
            super.paintTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
        }
    }

    private void paintRotatedSkin(Graphics g, Skin skin, int tabPlacement,
                                  int x, int y, int w, int h, State state) {
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.translate(x, y);
        switch (tabPlacement) {
           case RIGHT:  g2d.translate(w, 0);
                        g2d.rotate(Math.toRadians(90.0));
                        skin.paintSkin(g2d, 0, 0, h, w, state);
                        break;

           case LEFT:   g2d.scale(-1.0, 1.0);
                        g2d.rotate(Math.toRadians(90.0));
                        skin.paintSkin(g2d, 0, 0, h, w, state);
                        break;

           case BOTTOM: g2d.translate(0, h);
                        g2d.scale(-1.0, 1.0);
                        g2d.rotate(Math.toRadians(180.0));
                        skin.paintSkin(g2d, 0, 0, w, h, state);
                        break;

           case TOP:
           default:     skin.paintSkin(g2d, 0, 0, w, h, state);
        }
        g2d.dispose();
    }
}
