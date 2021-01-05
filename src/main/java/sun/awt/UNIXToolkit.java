
package sun.awt;

import java.awt.RenderingHints;
import static java.awt.RenderingHints.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetIntegerAction;
import com.sun.java.swing.plaf.gtk.GTKConstants.TextDirection;
import sun.java2d.opengl.OGLRenderQueue;
import sun.security.action.GetPropertyAction;

public abstract class UNIXToolkit extends SunToolkit
{

  public static final Object GTK_LOCK = new Object();

  private static final int[] BAND_OFFSETS = { 0, 1, 2 };
  private static final int[] BAND_OFFSETS_ALPHA = { 0, 1, 2, 3 };
  private static final int DEFAULT_DATATRANSFER_TIMEOUT = 10000;

  public enum GtkVersions {
    ANY(0),
    GTK2(Constants.GTK2_MAJOR_NUMBER),
    GTK3(Constants.GTK3_MAJOR_NUMBER);

    static class Constants {
      static final int GTK2_MAJOR_NUMBER = 2;
      static final int GTK3_MAJOR_NUMBER = 3;
    }

    final int number;

    GtkVersions(int number) {
      this.number = number;
    }

    public static GtkVersions getVersion(int number) {
      switch (number) {
        case Constants.GTK2_MAJOR_NUMBER:
          return GTK2;
        case Constants.GTK3_MAJOR_NUMBER:
          return GTK3;
        default:
          return ANY;
      }
    }

    public int getNumber() {
      return number;
    }
  };

  private Boolean nativeGTKAvailable;
  private Boolean nativeGTKLoaded;
  private BufferedImage tmpImage = null;

  public static int getDatatransferTimeout() {
    Integer dt = (Integer)AccessController.doPrivileged(
        new GetIntegerAction("sun.awt.datatransfer.timeout"));
    if (dt == null || dt <= 0) {
      return DEFAULT_DATATRANSFER_TIMEOUT;
    } else {
      return dt;
    }
  }


  @Override
  public boolean isNativeGTKAvailable() {
    synchronized (GTK_LOCK) {
      if (nativeGTKLoaded != null) {
        return nativeGTKLoaded;

      } else if (nativeGTKAvailable != null) {
        return nativeGTKAvailable;

      } else {
        boolean success = check_gtk(getEnabledGtkVersion().getNumber());
        nativeGTKAvailable = success;
        return success;
      }
    }
  }


  public boolean loadGTK() {
    synchronized (GTK_LOCK) {
      if (nativeGTKLoaded == null) {
        nativeGTKLoaded = load_gtk(getEnabledGtkVersion().getNumber(),
            isGtkVerbose());
      }
    }
    return nativeGTKLoaded;
  }


  protected Object lazilyLoadDesktopProperty(String name) {
    if (name.startsWith("gtk.icon.")) {
      return lazilyLoadGTKIcon(name);
    }
    return super.lazilyLoadDesktopProperty(name);
  }


  protected Object lazilyLoadGTKIcon(String longname) {
    Object result = desktopProperties.get(longname);
    if (result != null) {
      return result;
    }

    String str[] = longname.split("\\.");
    if (str.length != 5) {
      return null;
    }

    int size = 0;
    try {
      size = Integer.parseInt(str[3]);
    } catch (NumberFormatException nfe) {
      return null;
    }

    TextDirection dir = ("ltr".equals(str[4]) ? TextDirection.LTR :
        TextDirection.RTL);

    BufferedImage img = getStockIcon(-1, str[2], size, dir.ordinal(), null);
    if (img != null) {
      setDesktopProperty(longname, img);
    }
    return img;
  }


  public BufferedImage getGTKIcon(final String filename) {
    if (!loadGTK()) {
      return null;

    } else {
      synchronized (GTK_LOCK) {
        if (!load_gtk_icon(filename)) {
          tmpImage = null;
        }
      }
    }
    return tmpImage;
  }


  public BufferedImage getStockIcon(final int widgetType, final String stockId,
                                    final int iconSize, final int direction,
                                    final String detail) {
    if (!loadGTK()) {
      return null;

    } else {
      synchronized (GTK_LOCK) {
        if (!load_stock_icon(widgetType, stockId, iconSize, direction, detail)) {
          tmpImage = null;
        }
      }
    }
    return tmpImage;  }


  public void loadIconCallback(byte[] data, int width, int height,
                               int rowStride, int bps, int channels, boolean alpha) {
    tmpImage = null;

    DataBuffer dataBuf = new DataBufferByte(data, (rowStride * height));
    WritableRaster raster = Raster.createInterleavedRaster(dataBuf,
        width, height, rowStride, channels,
        (alpha ? BAND_OFFSETS_ALPHA : BAND_OFFSETS), null);
    ColorModel colorModel = new ComponentColorModel(
        ColorSpace.getInstance(ColorSpace.CS_sRGB), alpha, false,
        ColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);

    tmpImage = new BufferedImage(colorModel, raster, false, null);
  }

  private static native boolean check_gtk(int version);
  private static native boolean load_gtk(int version, boolean verbose);
  private static native boolean unload_gtk();
  private native boolean load_gtk_icon(String filename);
  private native boolean load_stock_icon(int widget_type, String stock_id,
                                         int iconSize, int textDirection, String detail);

  private native void nativeSync();
  private static native int get_gtk_version();

  @Override
  public void sync() {
    nativeSync();
    OGLRenderQueue.sync();
  }


  public static final String FONTCONFIGAAHINT = "fontconfig/Antialias";

  @Override
  protected RenderingHints getDesktopAAHints() {

    Object aaValue = getDesktopProperty("gnome.Xft/Antialias");

    if (aaValue == null) {

      aaValue = getDesktopProperty(FONTCONFIGAAHINT);
      if (aaValue != null) {
        return new RenderingHints(KEY_TEXT_ANTIALIASING, aaValue);
      } else {
        return null; }
    }


    boolean aa = ((aaValue instanceof Number)
        && ((Number) aaValue).intValue() != 0);
    Object aaHint;
    if (aa) {
      String subpixOrder =
          (String)getDesktopProperty("gnome.Xft/RGBA");

      if (subpixOrder == null || subpixOrder.equals("none")) {
        aaHint = VALUE_TEXT_ANTIALIAS_ON;
      } else if (subpixOrder.equals("rgb")) {
        aaHint = VALUE_TEXT_ANTIALIAS_LCD_HRGB;
      } else if (subpixOrder.equals("bgr")) {
        aaHint = VALUE_TEXT_ANTIALIAS_LCD_HBGR;
      } else if (subpixOrder.equals("vrgb")) {
        aaHint = VALUE_TEXT_ANTIALIAS_LCD_VRGB;
      } else if (subpixOrder.equals("vbgr")) {
        aaHint = VALUE_TEXT_ANTIALIAS_LCD_VBGR;
      } else {

        aaHint = VALUE_TEXT_ANTIALIAS_ON;
      }
    } else {
      aaHint = VALUE_TEXT_ANTIALIAS_DEFAULT;
    }
    return new RenderingHints(KEY_TEXT_ANTIALIASING, aaHint);
  }

  private native boolean gtkCheckVersionImpl(int major, int minor,
                                             int micro);


  public boolean checkGtkVersion(int major, int minor, int micro) {
    if (loadGTK()) {
      return gtkCheckVersionImpl(major, minor, micro);
    }
    return false;
  }

  public static GtkVersions getEnabledGtkVersion() {
    String version = AccessController.doPrivileged(
        new GetPropertyAction("jdk.gtk.version"));
    if (version == null) {
      return GtkVersions.ANY;
    } else if (version.startsWith("2")) {
      return GtkVersions.GTK2;
    } else if("3".equals(version) ){
      return GtkVersions.GTK3;
    }
    return GtkVersions.ANY;
  }

  public static GtkVersions getGtkVersion() {
    return GtkVersions.getVersion(get_gtk_version());
  }

  public static boolean isGtkVerbose() {
    return AccessController.doPrivileged((PrivilegedAction<Boolean>)()
        -> Boolean.getBoolean("jdk.gtk.verbose"));
  }
}