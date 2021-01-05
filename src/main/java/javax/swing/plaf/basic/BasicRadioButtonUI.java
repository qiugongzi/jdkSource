

package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;
import sun.awt.AppContext;
import java.util.Enumeration;
import java.util.HashSet;


public class BasicRadioButtonUI extends BasicToggleButtonUI
{
    private static final Object BASIC_RADIO_BUTTON_UI_KEY = new Object();


    protected Icon icon;

    private boolean defaults_initialized = false;

    private final static String propertyPrefix = "RadioButton" + ".";

    private KeyListener keyListener = null;

    public static ComponentUI createUI(JComponent b) {
        AppContext appContext = AppContext.getAppContext();
        BasicRadioButtonUI radioButtonUI =
                (BasicRadioButtonUI) appContext.get(BASIC_RADIO_BUTTON_UI_KEY);
        if (radioButtonUI == null) {
            radioButtonUI = new BasicRadioButtonUI();
            appContext.put(BASIC_RADIO_BUTTON_UI_KEY, radioButtonUI);
        }
        return radioButtonUI;
    }

    @Override
    protected String getPropertyPrefix() {
        return propertyPrefix;
    }

    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        if(!defaults_initialized) {
            icon = UIManager.getIcon(getPropertyPrefix() + "icon");
            defaults_initialized = true;
        }
    }

    @Override
    protected void uninstallDefaults(AbstractButton b) {
        super.uninstallDefaults(b);
        defaults_initialized = false;
    }


    public Icon getDefaultIcon() {
        return icon;
    }

    @Override
    protected void installListeners(AbstractButton button) {
        super.installListeners(button);

        if (!(button instanceof JRadioButton))
            return;

        keyListener = createKeyListener();
        button.addKeyListener(keyListener);

        button.setFocusTraversalKeysEnabled(false);

        button.getActionMap().put("Previous", new SelectPreviousBtn());
        button.getActionMap().put("Next", new SelectNextBtn());

        button.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
            put(KeyStroke.getKeyStroke("UP"), "Previous");
        button.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
            put(KeyStroke.getKeyStroke("DOWN"), "Next");
        button.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
            put(KeyStroke.getKeyStroke("LEFT"), "Previous");
        button.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
            put(KeyStroke.getKeyStroke("RIGHT"), "Next");
    }

    @Override
    protected void uninstallListeners(AbstractButton button) {
        super.uninstallListeners(button);

        if (!(button instanceof JRadioButton))
            return;

        button.getActionMap().remove("Previous");
        button.getActionMap().remove("Next");
        button.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .remove(KeyStroke.getKeyStroke("UP"));
        button.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .remove(KeyStroke.getKeyStroke("DOWN"));
        button.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .remove(KeyStroke.getKeyStroke("LEFT"));
        button.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .remove(KeyStroke.getKeyStroke("RIGHT"));

        if (keyListener != null) {
            button.removeKeyListener(keyListener);
            keyListener = null;
        }
    }


    private static Dimension size = new Dimension();
    private static Rectangle viewRect = new Rectangle();
    private static Rectangle iconRect = new Rectangle();
    private static Rectangle textRect = new Rectangle();


    @Override
    public synchronized void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();

        Font f = c.getFont();
        g.setFont(f);
        FontMetrics fm = SwingUtilities2.getFontMetrics(c, g, f);

        Insets i = c.getInsets();
        size = b.getSize(size);
        viewRect.x = i.left;
        viewRect.y = i.top;
        viewRect.width = size.width - (i.right + viewRect.x);
        viewRect.height = size.height - (i.bottom + viewRect.y);
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
        textRect.x = textRect.y = textRect.width = textRect.height = 0;

        Icon altIcon = b.getIcon();
        Icon selectedIcon = null;
        Icon disabledIcon = null;

        String text = SwingUtilities.layoutCompoundLabel(
            c, fm, b.getText(), altIcon != null ? altIcon : getDefaultIcon(),
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect,
            b.getText() == null ? 0 : b.getIconTextGap());

        if(c.isOpaque()) {
            g.setColor(b.getBackground());
            g.fillRect(0,0, size.width, size.height);
        }


        if(altIcon != null) {

            if(!model.isEnabled()) {
                if(model.isSelected()) {
                   altIcon = b.getDisabledSelectedIcon();
                } else {
                   altIcon = b.getDisabledIcon();
                }
            } else if(model.isPressed() && model.isArmed()) {
                altIcon = b.getPressedIcon();
                if(altIcon == null) {
                    altIcon = b.getSelectedIcon();
                }
            } else if(model.isSelected()) {
                if(b.isRolloverEnabled() && model.isRollover()) {
                        altIcon = b.getRolloverSelectedIcon();
                        if (altIcon == null) {
                                altIcon = b.getSelectedIcon();
                        }
                } else {
                        altIcon = b.getSelectedIcon();
                }
            } else if(b.isRolloverEnabled() && model.isRollover()) {
                altIcon = b.getRolloverIcon();
            }

            if(altIcon == null) {
                altIcon = b.getIcon();
            }

            altIcon.paintIcon(c, g, iconRect.x, iconRect.y);

        } else {
            getDefaultIcon().paintIcon(c, g, iconRect.x, iconRect.y);
        }


        if(text != null) {
            View v = (View) c.getClientProperty(BasicHTML.propertyKey);
            if (v != null) {
                v.paint(g, textRect);
            } else {
                paintText(g, b, textRect, text);
            }
            if(b.hasFocus() && b.isFocusPainted() &&
               textRect.width > 0 && textRect.height > 0 ) {
                paintFocus(g, textRect, size);
            }
        }
    }


    protected void paintFocus(Graphics g, Rectangle textRect, Dimension size) {
    }



    private static Rectangle prefViewRect = new Rectangle();
    private static Rectangle prefIconRect = new Rectangle();
    private static Rectangle prefTextRect = new Rectangle();
    private static Insets prefInsets = new Insets(0, 0, 0, 0);


    @Override
    public Dimension getPreferredSize(JComponent c) {
        if(c.getComponentCount() > 0) {
            return null;
        }

        AbstractButton b = (AbstractButton) c;

        String text = b.getText();

        Icon buttonIcon = b.getIcon();
        if(buttonIcon == null) {
            buttonIcon = getDefaultIcon();
        }

        Font font = b.getFont();
        FontMetrics fm = b.getFontMetrics(font);

        prefViewRect.x = prefViewRect.y = 0;
        prefViewRect.width = Short.MAX_VALUE;
        prefViewRect.height = Short.MAX_VALUE;
        prefIconRect.x = prefIconRect.y = prefIconRect.width = prefIconRect.height = 0;
        prefTextRect.x = prefTextRect.y = prefTextRect.width = prefTextRect.height = 0;

        SwingUtilities.layoutCompoundLabel(
            c, fm, text, buttonIcon,
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            prefViewRect, prefIconRect, prefTextRect,
            text == null ? 0 : b.getIconTextGap());

        int x1 = Math.min(prefIconRect.x, prefTextRect.x);
        int x2 = Math.max(prefIconRect.x + prefIconRect.width,
                          prefTextRect.x + prefTextRect.width);
        int y1 = Math.min(prefIconRect.y, prefTextRect.y);
        int y2 = Math.max(prefIconRect.y + prefIconRect.height,
                          prefTextRect.y + prefTextRect.height);
        int width = x2 - x1;
        int height = y2 - y1;

        prefInsets = b.getInsets(prefInsets);
        width += prefInsets.left + prefInsets.right;
        height += prefInsets.top + prefInsets.bottom;
        return new Dimension(width, height);
    }

    private KeyListener createKeyListener() {
         if (keyListener == null) {
            keyListener = new KeyHandler();
        }
        return keyListener;
    }


    private boolean isValidRadioButtonObj(Object obj) {
        return ((obj instanceof JRadioButton) &&
                    ((JRadioButton) obj).isVisible() &&
                    ((JRadioButton) obj).isEnabled());
    }


    private void selectRadioButton(ActionEvent event, boolean next) {
        Object eventSrc = event.getSource();

        if (!isValidRadioButtonObj(eventSrc))
            return;

        ButtonGroupInfo btnGroupInfo = new ButtonGroupInfo((JRadioButton)eventSrc);
        btnGroupInfo.selectNewButton(next);
    }

    @SuppressWarnings("serial")
    private class SelectPreviousBtn extends AbstractAction {
        public SelectPreviousBtn() {
            super("Previous");
        }

        public void actionPerformed(ActionEvent e) {
           BasicRadioButtonUI.this.selectRadioButton(e, false);
        }
    }

    @SuppressWarnings("serial")
    private class SelectNextBtn extends AbstractAction{
        public SelectNextBtn() {
            super("Next");
        }

        public void actionPerformed(ActionEvent e) {
            BasicRadioButtonUI.this.selectRadioButton(e, true);
        }
    }


    private class ButtonGroupInfo {

        JRadioButton activeBtn = null;

        JRadioButton firstBtn = null;
        JRadioButton lastBtn = null;

        JRadioButton previousBtn = null;
        JRadioButton nextBtn = null;

        HashSet<JRadioButton> btnsInGroup = null;

        boolean srcFound = false;
        public ButtonGroupInfo(JRadioButton btn) {
            activeBtn = btn;
            btnsInGroup = new HashSet<JRadioButton>();
        }

        boolean containsInGroup(Object obj){
           return btnsInGroup.contains(obj);
        }

        Component getFocusTransferBaseComponent(boolean next){
            Component focusBaseComp = activeBtn;
            Container container = focusBaseComp.getFocusCycleRootAncestor();
            if (container != null) {
                FocusTraversalPolicy policy = container.getFocusTraversalPolicy();
                Component comp = next ? policy.getComponentAfter(container, activeBtn)
                                      : policy.getComponentBefore(container, activeBtn);

                if (containsInGroup(comp)) {
                    focusBaseComp = next ? lastBtn : firstBtn;
                }
            }

            return focusBaseComp;
        }

        boolean getButtonGroupInfo() {
            if (activeBtn == null)
                return false;

            btnsInGroup.clear();

            ButtonModel model = activeBtn.getModel();
            if (!(model instanceof DefaultButtonModel))
                return false;

            DefaultButtonModel bm = (DefaultButtonModel) model;

            ButtonGroup group = bm.getGroup();
            if (group == null)
                return false;

            Enumeration<AbstractButton> e = group.getElements();
            if (e == null)
                return false;

            while (e.hasMoreElements()) {
                AbstractButton curElement = e.nextElement();
                if (!isValidRadioButtonObj(curElement))
                    continue;

                btnsInGroup.add((JRadioButton) curElement);

                if (null == firstBtn)
                    firstBtn = (JRadioButton) curElement;

                if (activeBtn == curElement)
                    srcFound = true;
                else if (!srcFound) {
                    previousBtn = (JRadioButton) curElement;
                } else if (nextBtn == null) {
                    nextBtn = (JRadioButton) curElement;
                }

                lastBtn = (JRadioButton) curElement;
            }

            return true;
        }


        void selectNewButton(boolean next) {
            if (!getButtonGroupInfo())
                return;

            if (srcFound) {
                JRadioButton newSelectedBtn = null;
                if (next) {
                    newSelectedBtn = (null == nextBtn) ? firstBtn : nextBtn;
                } else {
                    newSelectedBtn = (null == previousBtn) ? lastBtn : previousBtn;
                }
                if (newSelectedBtn != null &&
                    (newSelectedBtn != activeBtn)) {
                    newSelectedBtn.requestFocusInWindow();
                    newSelectedBtn.setSelected(true);
                }
            }
        }


        void jumpToNextComponent(boolean next) {
            if (!getButtonGroupInfo()){
                if (activeBtn != null){
                    lastBtn = activeBtn;
                    firstBtn = activeBtn;
                }
                else
                    return;
            }

            JComponent compTransferFocusFrom = activeBtn;

            Component focusBase = getFocusTransferBaseComponent(next);
            if (focusBase != null){
                if (next) {
                    KeyboardFocusManager.
                        getCurrentKeyboardFocusManager().focusNextComponent(focusBase);
                } else {
                    KeyboardFocusManager.
                        getCurrentKeyboardFocusManager().focusPreviousComponent(focusBase);
                }
            }
        }
    }


    private class KeyHandler implements KeyListener {

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_TAB) {
                 Object eventSrc = e.getSource();

                if (isValidRadioButtonObj(eventSrc)) {
                    e.consume();
                    ButtonGroupInfo btnGroupInfo = new ButtonGroupInfo((JRadioButton)eventSrc);
                    btnGroupInfo.jumpToNextComponent(!e.isShiftDown());
                }
            }
        }

        public void keyReleased(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
        }
    }
}
