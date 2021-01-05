
package javax.swing.plaf.nimbus;

import java.awt.BorderLayout;
import static java.awt.BorderLayout.*;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthStyleFactory;
import javax.swing.plaf.UIResource;
import java.security.AccessController;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import sun.swing.ImageIconUIResource;
import sun.swing.plaf.synth.SynthIcon;
import sun.swing.plaf.GTKKeybindings;
import sun.swing.plaf.WindowsKeybindings;
import sun.security.action.GetPropertyAction;


public class NimbusLookAndFeel extends SynthLookAndFeel {


    private static final String[] COMPONENT_KEYS = new String[]{"ArrowButton", "Button",
                    "CheckBox", "CheckBoxMenuItem", "ColorChooser", "ComboBox",
                    "DesktopPane", "DesktopIcon", "EditorPane", "FileChooser",
                    "FormattedTextField", "InternalFrame",
                    "InternalFrameTitlePane", "Label", "List", "Menu",
                    "MenuBar", "MenuItem", "OptionPane", "Panel",
                    "PasswordField", "PopupMenu", "PopupMenuSeparator",
                    "ProgressBar", "RadioButton", "RadioButtonMenuItem",
                    "RootPane", "ScrollBar", "ScrollBarTrack", "ScrollBarThumb",
                    "ScrollPane", "Separator", "Slider", "SliderTrack",
                    "SliderThumb", "Spinner", "SplitPane", "TabbedPane",
                    "Table", "TableHeader", "TextArea", "TextField", "TextPane",
                    "ToggleButton", "ToolBar", "ToolTip", "Tree", "Viewport"};


    private NimbusDefaults defaults;


    private UIDefaults uiDefaults;

    private DefaultsListener defaultsListener = new DefaultsListener();


    public NimbusLookAndFeel() {
        super();
        defaults = new NimbusDefaults();
    }


    @Override public void initialize() {
        super.initialize();
        defaults.initialize();
        setStyleFactory(new SynthStyleFactory() {
            @Override
            public SynthStyle getStyle(JComponent c, Region r) {
                return defaults.getStyle(c, r);
            }
        });
    }



    @Override public void uninitialize() {
        super.uninitialize();
        defaults.uninitialize();
        ImageCache.getInstance().flush();
        UIManager.getDefaults().removePropertyChangeListener(defaultsListener);
    }


    @Override public UIDefaults getDefaults() {
        if (uiDefaults == null){
            String osName = getSystemProperty("os.name");
            boolean isWindows = osName != null && osName.contains("Windows");

            uiDefaults = super.getDefaults();
            defaults.initializeDefaults(uiDefaults);

            if (isWindows) {
                WindowsKeybindings.installKeybindings(uiDefaults);
            } else {
                GTKKeybindings.installKeybindings(uiDefaults);
            }

            uiDefaults.put("TitledBorder.titlePosition",
                    TitledBorder.ABOVE_TOP);
            uiDefaults.put("TitledBorder.border", new BorderUIResource(
                    new LoweredBorder()));
            uiDefaults.put("TitledBorder.titleColor",
                    getDerivedColor("text",0.0f,0.0f,0.23f,0,true));
            uiDefaults.put("TitledBorder.font",
                    new NimbusDefaults.DerivedFont("defaultFont",
                            1f, true, null));

            uiDefaults.put("OptionPane.isYesLast", !isWindows);

            uiDefaults.put("Table.scrollPaneCornerComponent",
                    new UIDefaults.ActiveValue() {
                        @Override
                        public Object createValue(UIDefaults table) {
                            return new TableScrollPaneCorner();
                        }
                    });

            uiDefaults.put("ToolBarSeparator[Enabled].backgroundPainter",
                    new ToolBarSeparatorPainter());

            for (String componentKey : COMPONENT_KEYS) {
                String key = componentKey+".foreground";
                if (!uiDefaults.containsKey(key)){
                    uiDefaults.put(key,
                            new NimbusProperty(componentKey,"textForeground"));
                }
                key = componentKey+".background";
                if (!uiDefaults.containsKey(key)){
                    uiDefaults.put(key,
                            new NimbusProperty(componentKey,"background"));
                }
                key = componentKey+".font";
                if (!uiDefaults.containsKey(key)){
                    uiDefaults.put(key,
                            new NimbusProperty(componentKey,"font"));
                }
                key = componentKey+".disabledText";
                if (!uiDefaults.containsKey(key)){
                    uiDefaults.put(key,
                            new NimbusProperty(componentKey,"Disabled",
                                   "textForeground"));
                }
                key = componentKey+".disabled";
                if (!uiDefaults.containsKey(key)){
                    uiDefaults.put(key,
                            new NimbusProperty(componentKey,"Disabled",
                                    "background"));
                }
            }

            uiDefaults.put("FileView.computerIcon",
                    new LinkProperty("FileChooser.homeFolderIcon"));
            uiDefaults.put("FileView.directoryIcon",
                    new LinkProperty("FileChooser.directoryIcon"));
            uiDefaults.put("FileView.fileIcon",
                    new LinkProperty("FileChooser.fileIcon"));
            uiDefaults.put("FileView.floppyDriveIcon",
                    new LinkProperty("FileChooser.floppyDriveIcon"));
            uiDefaults.put("FileView.hardDriveIcon",
                    new LinkProperty("FileChooser.hardDriveIcon"));
        }
        return uiDefaults;
    }


    public static NimbusStyle getStyle(JComponent c, Region r) {
        return (NimbusStyle)SynthLookAndFeel.getStyle(c, r);
    }


    @Override public String getName() {
        return "Nimbus";
    }


    @Override public String getID() {
        return "Nimbus";
    }


    @Override public String getDescription() {
        return "Nimbus Look and Feel";
    }


    @Override public boolean shouldUpdateStyleOnAncestorChanged() {
        return true;
    }


    @Override
    protected boolean shouldUpdateStyleOnEvent(PropertyChangeEvent ev) {
        String eName = ev.getPropertyName();

        if ("name" == eName ||
            "ancestor" == eName ||
            "Nimbus.Overrides" == eName ||
            "Nimbus.Overrides.InheritDefaults" == eName ||
            "JComponent.sizeVariant" == eName) {

            JComponent c = (JComponent) ev.getSource();
            defaults.clearOverridesCache(c);
            return true;
        }

        return super.shouldUpdateStyleOnEvent(ev);
    }


    public void register(Region region, String prefix) {
        defaults.register(region, prefix);
    }


    private String getSystemProperty(String key) {
        return AccessController.doPrivileged(new GetPropertyAction(key));
    }

    @Override
    public Icon getDisabledIcon(JComponent component, Icon icon) {
        if (icon instanceof SynthIcon) {
            SynthIcon si = (SynthIcon)icon;
            BufferedImage img = EffectUtils.createCompatibleTranslucentImage(
                    si.getIconWidth(), si.getIconHeight());
            Graphics2D gfx = img.createGraphics();
            si.paintIcon(component, gfx, 0, 0);
            gfx.dispose();
            return new ImageIconUIResource(GrayFilter.createDisabledImage(img));
        } else {
            return super.getDisabledIcon(component, icon);
        }
    }


    public Color getDerivedColor(String uiDefaultParentName,
                                 float hOffset, float sOffset,
                                 float bOffset, int aOffset,
                                 boolean uiResource) {
        return defaults.getDerivedColor(uiDefaultParentName, hOffset, sOffset,
                bOffset, aOffset, uiResource);
    }


    protected final Color getDerivedColor(Color color1, Color color2,
                                      float midPoint, boolean uiResource) {
        int argb = deriveARGB(color1, color2, midPoint);
        if (uiResource) {
            return new ColorUIResource(argb);
        } else {
            return new Color(argb);
        }
    }


    protected final Color getDerivedColor(Color color1, Color color2,
                                      float midPoint) {
        return getDerivedColor(color1, color2, midPoint, true);
    }


    static Object resolveToolbarConstraint(JToolBar toolbar) {
        if (toolbar != null) {
            Container parent = toolbar.getParent();
            if (parent != null) {
                LayoutManager m = parent.getLayout();
                if (m instanceof BorderLayout) {
                    BorderLayout b = (BorderLayout)m;
                    Object con = b.getConstraints(toolbar);
                    if (con == SOUTH || con == EAST || con == WEST) {
                        return con;
                    }
                    return NORTH;
                }
            }
        }
        return NORTH;
    }


    static int deriveARGB(Color color1, Color color2, float midPoint) {
        int r = color1.getRed() +
                Math.round((color2.getRed() - color1.getRed()) * midPoint);
        int g = color1.getGreen() +
                Math.round((color2.getGreen() - color1.getGreen()) * midPoint);
        int b = color1.getBlue() +
                Math.round((color2.getBlue() - color1.getBlue()) * midPoint);
        int a = color1.getAlpha() +
                Math.round((color2.getAlpha() - color1.getAlpha()) * midPoint);
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                (b & 0xFF);
    }


    private class LinkProperty implements UIDefaults.ActiveValue, UIResource{
        private String dstPropName;

        private LinkProperty(String dstPropName) {
            this.dstPropName = dstPropName;
        }

        @Override
        public Object createValue(UIDefaults table) {
            return UIManager.get(dstPropName);
        }
    }


    private class NimbusProperty implements UIDefaults.ActiveValue, UIResource {
        private String prefix;
        private String state = null;
        private String suffix;
        private boolean isFont;

        private NimbusProperty(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
            isFont = "font".equals(suffix);
        }

        private NimbusProperty(String prefix, String state, String suffix) {
            this(prefix,suffix);
            this.state = state;
        }


        @Override
        public Object createValue(UIDefaults table) {
            Object obj = null;
            if (state!=null){
                obj = uiDefaults.get(prefix+"["+state+"]."+suffix);
            }
            if (obj==null){
                obj = uiDefaults.get(prefix+"[Enabled]."+suffix);
            }
            if (obj==null){
                if (isFont) {
                    obj = uiDefaults.get("defaultFont");
                } else {
                    obj = uiDefaults.get(suffix);
                }
            }
            return obj;
        }
    }

    private Map<String, Map<String, Object>> compiledDefaults = null;
    private boolean defaultListenerAdded = false;

    static String parsePrefix(String key) {
        if (key == null) {
            return null;
        }
        boolean inquotes = false;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c == '"') {
                inquotes = !inquotes;
            } else if ((c == '[' || c == '.') && !inquotes) {
                return key.substring(0, i);
            }
        }
        return null;
    }

    Map<String, Object> getDefaultsForPrefix(String prefix) {
        if (compiledDefaults == null) {
            compiledDefaults = new HashMap<String, Map<String, Object>>();
            for (Map.Entry<Object, Object> entry: UIManager.getDefaults().entrySet()) {
                if (entry.getKey() instanceof String) {
                    addDefault((String) entry.getKey(), entry.getValue());
                }
            }
            if (! defaultListenerAdded) {
                UIManager.getDefaults().addPropertyChangeListener(defaultsListener);
                defaultListenerAdded = true;
            }
        }
        return compiledDefaults.get(prefix);
    }

    private void addDefault(String key, Object value) {
        if (compiledDefaults == null) {
            return;
        }

        String prefix = parsePrefix(key);
        if (prefix != null) {
            Map<String, Object> keys = compiledDefaults.get(prefix);
            if (keys == null) {
                keys = new HashMap<String, Object>();
                compiledDefaults.put(prefix, keys);
            }
            keys.put(key, value);
        }
    }

    private class DefaultsListener implements PropertyChangeListener {
        @Override public void propertyChange(PropertyChangeEvent ev) {
            String key = ev.getPropertyName();
            if ("UIDefaults".equals(key)) {
                compiledDefaults = null;
            } else {
                addDefault(key, ev.getNewValue());
            }
        }
    }
}
