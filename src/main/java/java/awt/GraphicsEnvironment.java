


package java.awt;

import java.awt.image.BufferedImage;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;

import sun.font.FontManager;
import sun.font.FontManagerFactory;
import sun.java2d.HeadlessGraphicsEnvironment;
import sun.java2d.SunGraphicsEnvironment;
import sun.security.action.GetPropertyAction;



public abstract class GraphicsEnvironment {
    private static GraphicsEnvironment localEnv;


    private static Boolean headless;


    private static Boolean defaultHeadless;


    protected GraphicsEnvironment() {
    }


    public static synchronized GraphicsEnvironment getLocalGraphicsEnvironment() {
        if (localEnv == null) {
            localEnv = createGE();
        }

        return localEnv;
    }


    private static GraphicsEnvironment createGE() {
        GraphicsEnvironment ge;
        String nm = AccessController.doPrivileged(new GetPropertyAction("java.awt.graphicsenv", null));
        try {
Class<GraphicsEnvironment> geCls;
            try {
                geCls = (Class<GraphicsEnvironment>)Class.forName(nm);
            } catch (ClassNotFoundException ex) {
                ClassLoader cl = ClassLoader.getSystemClassLoader();
                geCls = (Class<GraphicsEnvironment>)Class.forName(nm, true, cl);
            }
            ge = geCls.newInstance();
if (isHeadless()) {
                ge = new HeadlessGraphicsEnvironment(ge);
            }
        } catch (ClassNotFoundException e) {
            throw new Error("Could not find class: "+nm);
        } catch (InstantiationException e) {
            throw new Error("Could not instantiate Graphics Environment: "
                            + nm);
        } catch (IllegalAccessException e) {
            throw new Error ("Could not access Graphics Environment: "
                             + nm);
        }
        return ge;
    }


    public static boolean isHeadless() {
        return getHeadlessProperty();
    }


    static String getHeadlessMessage() {
        if (headless == null) {
            getHeadlessProperty(); }
        return defaultHeadless != Boolean.TRUE ? null :
            "\nNo X11 DISPLAY variable was set, " +
            "but this program performed an operation which requires it.";
    }


    private static boolean getHeadlessProperty() {
        if (headless == null) {
            AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                String nm = System.getProperty("java.awt.headless");

                if (nm == null) {

                    if (System.getProperty("javaplugin.version") != null) {
                        headless = defaultHeadless = Boolean.FALSE;
                    } else {
                        String osName = System.getProperty("os.name");
                        if (osName.contains("OS X") && "sun.awt.HToolkit".equals(
                                System.getProperty("awt.toolkit")))
                        {
                            headless = defaultHeadless = Boolean.TRUE;
                        } else {
                            final String display = System.getenv("DISPLAY");
                            headless = defaultHeadless =
                                ("Linux".equals(osName) ||
                                 "SunOS".equals(osName) ||
                                 "FreeBSD".equals(osName) ||
                                 "NetBSD".equals(osName) ||
                                 "OpenBSD".equals(osName) ||
                                 "AIX".equals(osName)) &&
                                 (display == null || display.trim().isEmpty());
                        }
                    }
                } else {
                    headless = Boolean.valueOf(nm);
                }
                return null;
            });
        }
        return headless;
    }


    static void checkHeadless() throws HeadlessException {
        if (isHeadless()) {
            throw new HeadlessException();
        }
    }


    public boolean isHeadlessInstance() {
        return getHeadlessProperty();
    }


    public abstract GraphicsDevice[] getScreenDevices()
        throws HeadlessException;


    public abstract GraphicsDevice getDefaultScreenDevice()
        throws HeadlessException;


    public abstract Graphics2D createGraphics(BufferedImage img);


    public abstract Font[] getAllFonts();


    public abstract String[] getAvailableFontFamilyNames();


    public abstract String[] getAvailableFontFamilyNames(Locale l);


    public boolean registerFont(Font font) {
        if (font == null) {
            throw new NullPointerException("font cannot be null.");
        }
        FontManager fm = FontManagerFactory.getInstance();
        return fm.registerFont(font);
    }


    public void preferLocaleFonts() {
        FontManager fm = FontManagerFactory.getInstance();
        fm.preferLocaleFonts();
    }


    public void preferProportionalFonts() {
        FontManager fm = FontManagerFactory.getInstance();
        fm.preferProportionalFonts();
    }


    public Point getCenterPoint() throws HeadlessException {
    Rectangle usableBounds =
         SunGraphicsEnvironment.getUsableBounds(getDefaultScreenDevice());
        return new Point((usableBounds.width / 2) + usableBounds.x,
                         (usableBounds.height / 2) + usableBounds.y);
    }


    public Rectangle getMaximumWindowBounds() throws HeadlessException {
    return SunGraphicsEnvironment.getUsableBounds(getDefaultScreenDevice());
    }
}
