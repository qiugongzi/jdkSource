
package javax.swing.plaf.nimbus;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.Painter;


final class MenuPainter extends AbstractRegionPainter {
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int BACKGROUND_ENABLED_SELECTED = 3;
    static final int ARROWICON_DISABLED = 4;
    static final int ARROWICON_ENABLED = 5;
    static final int ARROWICON_ENABLED_SELECTED = 6;


    private int state; private PaintContext ctx;

    private Path2D path = new Path2D.Float();
    private Rectangle2D rect = new Rectangle2D.Float(0, 0, 0, 0);
    private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, 0, 0, 0, 0);
    private Ellipse2D ellipse = new Ellipse2D.Float(0, 0, 0, 0);

    private Color color1 = decodeColor("nimbusSelection", 0.0f, 0.0f, 0.0f, 0);
    private Color color2 = decodeColor("nimbusBlueGrey", 0.0f, -0.08983666f, -0.17647058f, 0);
    private Color color3 = decodeColor("nimbusBlueGrey", 0.055555582f, -0.09663743f, -0.4627451f, 0);
    private Color color4 = new Color(255, 255, 255, 255);


    private Object[] componentColors;

    public MenuPainter(PaintContext ctx, int state) {
        super();
        this.state = state;
        this.ctx = ctx;
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        componentColors = extendedCacheKeys;
        switch(state) {
            case BACKGROUND_ENABLED_SELECTED: paintBackgroundEnabledAndSelected(g); break;
            case ARROWICON_DISABLED: paintarrowIconDisabled(g); break;
            case ARROWICON_ENABLED: paintarrowIconEnabled(g); break;
            case ARROWICON_ENABLED_SELECTED: paintarrowIconEnabledAndSelected(g); break;

        }
    }
        


    @Override
    protected final PaintContext getPaintContext() {
        return ctx;
    }

    private void paintBackgroundEnabledAndSelected(Graphics2D g) {
        rect = decodeRect1();
        g.setPaint(color1);
        g.fill(rect);

    }

    private void paintarrowIconDisabled(Graphics2D g) {
        path = decodePath1();
        g.setPaint(color2);
        g.fill(path);

    }

    private void paintarrowIconEnabled(Graphics2D g) {
        path = decodePath1();
        g.setPaint(color3);
        g.fill(path);

    }

    private void paintarrowIconEnabledAndSelected(Graphics2D g) {
        path = decodePath2();
        g.setPaint(color4);
        g.fill(path);

    }



    private Rectangle2D decodeRect1() {
            rect.setRect(decodeX(1.0f), decodeY(1.0f), decodeX(2.0f) - decodeX(1.0f), decodeY(2.0f) - decodeY(1.0f)); return rect;
    }

    private Path2D decodePath1() {
        path.reset();
        path.moveTo(decodeX(0.0f), decodeY(0.2f));
        path.lineTo(decodeX(2.7512195f), decodeY(2.102439f));
        path.lineTo(decodeX(0.0f), decodeY(3.0f));
        path.lineTo(decodeX(0.0f), decodeY(0.2f));
        path.closePath();
        return path;
    }

    private Path2D decodePath2() {
        path.reset();
        path.moveTo(decodeX(0.0f), decodeY(1.0f));
        path.lineTo(decodeX(1.9529617f), decodeY(1.5625f));
        path.lineTo(decodeX(0.0f), decodeY(3.0f));
        path.lineTo(decodeX(0.0f), decodeY(1.0f));
        path.closePath();
        return path;
    }




}
