

package javax.swing.plaf.synth;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import sun.swing.SwingUtilities2;


public class SynthProgressBarUI extends BasicProgressBarUI
                                implements SynthUI, PropertyChangeListener {
    private SynthStyle style;
    private int progressPadding;
    private boolean rotateText; private boolean paintOutsideClip;
    private boolean tileWhenIndeterminate; private int tileWidth; private Dimension minBarSize; private int glowWidth; public static ComponentUI createUI(JComponent x) {
        return new SynthProgressBarUI();
    }


    @Override
    protected void installListeners() {
        super.installListeners();
        progressBar.addPropertyChangeListener(this);
    }


    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        progressBar.removePropertyChangeListener(this);
    }


    @Override
    protected void installDefaults() {
        updateStyle(progressBar);
    }

    private void updateStyle(JProgressBar c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);
        setCellLength(style.getInt(context, "ProgressBar.cellLength", 1));
        setCellSpacing(style.getInt(context, "ProgressBar.cellSpacing", 0));
        progressPadding = style.getInt(context,
                "ProgressBar.progressPadding", 0);
        paintOutsideClip = style.getBoolean(context,
                "ProgressBar.paintOutsideClip", false);
        rotateText = style.getBoolean(context,
                "ProgressBar.rotateText", false);
        tileWhenIndeterminate = style.getBoolean(context, "ProgressBar.tileWhenIndeterminate", false);
        tileWidth = style.getInt(context, "ProgressBar.tileWidth", 15);
        String scaleKey = (String)progressBar.getClientProperty(
                "JComponent.sizeVariant");
        if (scaleKey != null){
            if ("large".equals(scaleKey)){
                tileWidth *= 1.15;
            } else if ("small".equals(scaleKey)){
                tileWidth *= 0.857;
            } else if ("mini".equals(scaleKey)){
                tileWidth *= 0.784;
            }
        }
        minBarSize = (Dimension)style.get(context, "ProgressBar.minBarSize");
        glowWidth = style.getInt(context, "ProgressBar.glowWidth", 0);
        context.dispose();
    }


    @Override
    protected void uninstallDefaults() {
        SynthContext context = getContext(progressBar, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }


    @Override
    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(c, style, state);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }


    @Override
    public int getBaseline(JComponent c, int width, int height) {
        super.getBaseline(c, width, height);
        if (progressBar.isStringPainted() &&
                progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            SynthContext context = getContext(c);
            Font font = context.getStyle().getFont(context);
            FontMetrics metrics = progressBar.getFontMetrics(font);
            context.dispose();
            return (height - metrics.getAscent() - metrics.getDescent()) / 2 +
                    metrics.getAscent();
        }
        return -1;
    }


    @Override
    protected Rectangle getBox(Rectangle r) {
        if (tileWhenIndeterminate) {
            return SwingUtilities.calculateInnerArea(progressBar, r);
        } else {
            return super.getBox(r);
        }
    }


    @Override
    protected void setAnimationIndex(int newValue) {
        if (paintOutsideClip) {
            if (getAnimationIndex() == newValue) {
                return;
            }
            super.setAnimationIndex(newValue);
            progressBar.repaint();
        } else {
            super.setAnimationIndex(newValue);
        }
    }


    @Override
    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        context.getPainter().paintProgressBarBackground(context,
                          g, 0, 0, c.getWidth(), c.getHeight(),
                          progressBar.getOrientation());
        paint(context, g);
        context.dispose();
    }


    @Override
    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }


    protected void paint(SynthContext context, Graphics g) {
        JProgressBar pBar = (JProgressBar)context.getComponent();
        int x = 0, y = 0, width = 0, height = 0;
        if (!pBar.isIndeterminate()) {
            Insets pBarInsets = pBar.getInsets();
            double percentComplete = pBar.getPercentComplete();
            if (percentComplete != 0.0) {
                if (pBar.getOrientation() == JProgressBar.HORIZONTAL) {
                    x = pBarInsets.left + progressPadding;
                    y = pBarInsets.top + progressPadding;
                    width = (int)(percentComplete * (pBar.getWidth()
                            - (pBarInsets.left + progressPadding
                             + pBarInsets.right + progressPadding)));
                    height = pBar.getHeight()
                            - (pBarInsets.top + progressPadding
                             + pBarInsets.bottom + progressPadding);

                    if (!SynthLookAndFeel.isLeftToRight(pBar)) {
                        x = pBar.getWidth() - pBarInsets.right - width
                                - progressPadding - glowWidth;
                    }
                } else {  x = pBarInsets.left + progressPadding;
                    width = pBar.getWidth()
                            - (pBarInsets.left + progressPadding
                            + pBarInsets.right + progressPadding);
                    height = (int)(percentComplete * (pBar.getHeight()
                            - (pBarInsets.top + progressPadding
                             + pBarInsets.bottom + progressPadding)));
                    y = pBar.getHeight() - pBarInsets.bottom - height
                            - progressPadding;

                    if (SynthLookAndFeel.isLeftToRight(pBar)) {
                        y -= glowWidth;
                    }
                }
            }
        } else {
            boxRect = getBox(boxRect);
            x = boxRect.x + progressPadding;
            y = boxRect.y + progressPadding;
            width = boxRect.width - progressPadding - progressPadding;
            height = boxRect.height - progressPadding - progressPadding;
        }

        if (tileWhenIndeterminate && pBar.isIndeterminate()) {
            double percentComplete = (double)getAnimationIndex() / (double)getFrameCount();
            int offset = (int)(percentComplete * tileWidth);
            Shape clip = g.getClip();
            g.clipRect(x, y, width, height);
            if (pBar.getOrientation() == JProgressBar.HORIZONTAL) {
                for (int i=x-tileWidth+offset; i<=width; i+=tileWidth) {
                    context.getPainter().paintProgressBarForeground(
                            context, g, i, y, tileWidth, height, pBar.getOrientation());
                }
            } else { for (int i=y-offset; i<height+tileWidth; i+=tileWidth) {
                    context.getPainter().paintProgressBarForeground(
                            context, g, x, i, width, tileWidth, pBar.getOrientation());
                }
            }
            g.setClip(clip);
        } else {
            if (minBarSize == null || (width >= minBarSize.width
                    && height >= minBarSize.height)) {
                context.getPainter().paintProgressBarForeground(context, g,
                        x, y, width, height, pBar.getOrientation());
            }
        }

        if (pBar.isStringPainted()) {
            paintText(context, g, pBar.getString());
        }
    }


    protected void paintText(SynthContext context, Graphics g, String title) {
        if (progressBar.isStringPainted()) {
            SynthStyle style = context.getStyle();
            Font font = style.getFont(context);
            FontMetrics fm = SwingUtilities2.getFontMetrics(
                    progressBar, g, font);
            int strLength = style.getGraphicsUtils(context).
                computeStringWidth(context, font, fm, title);
            Rectangle bounds = progressBar.getBounds();

            if (rotateText &&
                    progressBar.getOrientation() == JProgressBar.VERTICAL){
                Graphics2D g2 = (Graphics2D)g;
                Point textPos;
                AffineTransform rotation;
                if (progressBar.getComponentOrientation().isLeftToRight()){
                    rotation = AffineTransform.getRotateInstance(-Math.PI/2);
                    textPos = new Point(
                        (bounds.width+fm.getAscent()-fm.getDescent())/2,
                           (bounds.height+strLength)/2);
                } else {
                    rotation = AffineTransform.getRotateInstance(Math.PI/2);
                    textPos = new Point(
                        (bounds.width-fm.getAscent()+fm.getDescent())/2,
                           (bounds.height-strLength)/2);
                }

                if (textPos.x < 0) {
                    return;
                }

                font = font.deriveFont(rotation);
                g2.setFont(font);
                g2.setColor(style.getColor(context, ColorType.TEXT_FOREGROUND));
                style.getGraphicsUtils(context).paintText(context, g, title,
                                                     textPos.x, textPos.y, -1);
            } else {
                Rectangle textRect = new Rectangle(
                    (bounds.width / 2) - (strLength / 2),
                    (bounds.height -
                        (fm.getAscent() + fm.getDescent())) / 2,
                    0, 0);

                if (textRect.y < 0) {
                    return;
                }

                g.setColor(style.getColor(context, ColorType.TEXT_FOREGROUND));
                g.setFont(font);
                style.getGraphicsUtils(context).paintText(context, g, title,
                                                     textRect.x, textRect.y, -1);
            }
        }
    }


    @Override
    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintProgressBarBorder(context, g, x, y, w, h,
                                                    progressBar.getOrientation());
    }


    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e) ||
                "indeterminate".equals(e.getPropertyName())) {
            updateStyle((JProgressBar)e.getSource());
        }
    }


    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension size = null;
        Insets border = progressBar.getInsets();
        FontMetrics fontSizer = progressBar.getFontMetrics(progressBar.getFont());
        String progString = progressBar.getString();
        int stringHeight = fontSizer.getHeight() + fontSizer.getDescent();

        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            size = new Dimension(getPreferredInnerHorizontal());
            if (progressBar.isStringPainted()) {
                if (stringHeight > size.height) {
                    size.height = stringHeight;
                }

                int stringWidth = SwingUtilities2.stringWidth(
                                       progressBar, fontSizer, progString);
                if (stringWidth > size.width) {
                    size.width = stringWidth;
                }
            }
        } else {
            size = new Dimension(getPreferredInnerVertical());
            if (progressBar.isStringPainted()) {
                if (stringHeight > size.width) {
                    size.width = stringHeight;
                }

                int stringWidth = SwingUtilities2.stringWidth(
                                       progressBar, fontSizer, progString);
                if (stringWidth > size.height) {
                    size.height = stringWidth;
                }
            }
        }

        String scaleKey = (String)progressBar.getClientProperty(
                "JComponent.sizeVariant");
        if (scaleKey != null){
            if ("large".equals(scaleKey)){
                size.width *= 1.15f;
                size.height *= 1.15f;
            } else if ("small".equals(scaleKey)){
                size.width *= 0.90f;
                size.height *= 0.90f;
            } else if ("mini".equals(scaleKey)){
                size.width *= 0.784f;
                size.height *= 0.784f;
            }
        }

        size.width += border.left + border.right;
        size.height += border.top + border.bottom;

        return size;
    }
}
