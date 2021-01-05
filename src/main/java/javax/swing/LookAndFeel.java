

package javax.swing;

import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.awt.Toolkit;
import sun.awt.SunToolkit;

import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import java.net.URL;
import sun.swing.SwingUtilities2;
import sun.swing.DefaultLayoutStyle;
import sun.swing.ImageIconUIResource;

import java.util.StringTokenizer;



public abstract class LookAndFeel
{


    public static void installColors(JComponent c,
                                     String defaultBgName,
                                     String defaultFgName)
    {
        Color bg = c.getBackground();
        if (bg == null || bg instanceof UIResource) {
            c.setBackground(UIManager.getColor(defaultBgName));
        }

        Color fg = c.getForeground();
        if (fg == null || fg instanceof UIResource) {
            c.setForeground(UIManager.getColor(defaultFgName));
        }
    }



    public static void installColorsAndFont(JComponent c,
                                         String defaultBgName,
                                         String defaultFgName,
                                         String defaultFontName) {
        Font f = c.getFont();
        if (f == null || f instanceof UIResource) {
            c.setFont(UIManager.getFont(defaultFontName));
        }

        installColors(c, defaultBgName, defaultFgName);
    }



    public static void installBorder(JComponent c, String defaultBorderName) {
        Border b = c.getBorder();
        if (b == null || b instanceof UIResource) {
            c.setBorder(UIManager.getBorder(defaultBorderName));
        }
    }



    public static void uninstallBorder(JComponent c) {
        if (c.getBorder() instanceof UIResource) {
            c.setBorder(null);
        }
    }


    public static void installProperty(JComponent c,
                                       String propertyName, Object propertyValue) {
        if (SunToolkit.isInstanceOf(c, "javax.swing.JPasswordField")) {
            if (!((JPasswordField)c).customSetUIProperty(propertyName, propertyValue)) {
                c.setUIProperty(propertyName, propertyValue);
            }
        } else {
            c.setUIProperty(propertyName, propertyValue);
        }
    }


    public static JTextComponent.KeyBinding[] makeKeyBindings(Object[] keyBindingList)
    {
        JTextComponent.KeyBinding[] rv = new JTextComponent.KeyBinding[keyBindingList.length / 2];

        for(int i = 0; i < rv.length; i ++) {
            Object o = keyBindingList[2 * i];
            KeyStroke keystroke = (o instanceof KeyStroke)
                ? (KeyStroke) o
                : KeyStroke.getKeyStroke((String) o);
            String action = (String) keyBindingList[2 * i + 1];
            rv[i] = new JTextComponent.KeyBinding(keystroke, action);
        }

        return rv;
    }


    public static InputMap makeInputMap(Object[] keys) {
        InputMap retMap = new InputMapUIResource();
        loadKeyBindings(retMap, keys);
        return retMap;
    }


    public static ComponentInputMap makeComponentInputMap(JComponent c,
                                                          Object[] keys) {
        ComponentInputMap retMap = new ComponentInputMapUIResource(c);
        loadKeyBindings(retMap, keys);
        return retMap;
    }



    public static void loadKeyBindings(InputMap retMap, Object[] keys) {
        if (keys != null) {
            for (int counter = 0, maxCounter = keys.length;
                 counter < maxCounter; counter++) {
                Object keyStrokeO = keys[counter++];
                KeyStroke ks = (keyStrokeO instanceof KeyStroke) ?
                                (KeyStroke)keyStrokeO :
                                KeyStroke.getKeyStroke((String)keyStrokeO);
                retMap.put(ks, keys[counter]);
            }
        }
    }


    public static Object makeIcon(final Class<?> baseClass, final String gifFile) {
        return SwingUtilities2.makeIcon(baseClass, baseClass, gifFile);
    }


    public LayoutStyle getLayoutStyle() {
        return DefaultLayoutStyle.getInstance();
    }


    public void provideErrorFeedback(Component component) {
        Toolkit toolkit = null;
        if (component != null) {
            toolkit = component.getToolkit();
        } else {
            toolkit = Toolkit.getDefaultToolkit();
        }
        toolkit.beep();
    } public static Object getDesktopPropertyValue(String systemPropertyName, Object fallbackValue) {
        Object value = Toolkit.getDefaultToolkit().getDesktopProperty(systemPropertyName);
        if (value == null) {
            return fallbackValue;
        } else if (value instanceof Color) {
            return new ColorUIResource((Color)value);
        } else if (value instanceof Font) {
            return new FontUIResource((Font)value);
        }
        return value;
    }


    public Icon getDisabledIcon(JComponent component, Icon icon) {
        if (icon instanceof ImageIcon) {
            return new ImageIconUIResource(GrayFilter.
                   createDisabledImage(((ImageIcon)icon).getImage()));
        }
        return null;
    }


    public Icon getDisabledSelectedIcon(JComponent component, Icon icon) {
        return getDisabledIcon(component, icon);
    }


    public abstract String getName();



    public abstract String getID();



    public abstract String getDescription();



    public boolean getSupportsWindowDecorations() {
        return false;
    }


    public abstract boolean isNativeLookAndFeel();



    public abstract boolean isSupportedLookAndFeel();



    public void initialize() {
    }



    public void uninitialize() {
    }


    public UIDefaults getDefaults() {
        return null;
    }


    public String toString() {
        return "[" + getDescription() + " - " + getClass().getName() + "]";
    }
}
