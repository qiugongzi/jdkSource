
package javax.swing.plaf.nimbus;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.Painter;


final class CheckBoxMenuItemPainter extends AbstractRegionPainter {
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int BACKGROUND_MOUSEOVER = 3;
    static final int BACKGROUND_SELECTED_MOUSEOVER = 4;
    static final int CHECKICON_DISABLED_SELECTED = 5;
    static final int CHECKICON_ENABLED_SELECTED = 6;
    static final int CHECKICON_SELECTED_MOUSEOVER = 7;


    private int state; private PaintContext ctx;

    private Path2D path = new Path2D.Float();
    private Rectangle2D rect = new Rectangle2D.Float(0, 0, 0, 0);
    private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, 0, 0, 0, 0);
    private Ellipse2D ellipse = new Ellipse2D.Float(0, 0, 0, 0);

    private Color color1 = decodeColor("nimbusSelection", 0.0f, 0.0f, 0.0f, 0);
    private Color color2 = decodeColor("nimbusBlueGrey", 0.0f, -0.08983666f, -0.17647058f, 0);
    private Color color3 = decodeColor("nimbusBlueGrey", 0.055555582f, -0.096827686f, -0.45882353f, 0);
    private Color color4 = decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);


    private Object[] componentColors;

    public CheckBoxMenuItemPainter(PaintContext ctx, int state) {
        super();
        this.state = state;
        this.ctx = ctx;
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        componentColors = extendedCacheKeys;
        switch(state) {
            case BACKGROUND_MOUSEOVER: paintBackgroundMouseOver(g); break;
            case BACKGROUND_SELECTED_MOUSEOVER: paintBackgroundSelectedAndMouseOver(g); break;
            case CHECKICON_DISABLED_SELECTED: paintcheckIconDisabledAndSelected(g); break;
            case CHECKICON_ENABLED_SELECTED: paintcheckIconEnabledAndSelected(g); break;
            case CHECKICON_SELECTED_MOUSEOVER: paintcheckIconSelectedAndMouseOver(g); break;

        }
    }
        


    @Override
    protected final PaintContext getPaintContext() {
        return ctx;
    }

    private void paintBackgroundMouseOver(Graphics2D g) {
        rect = decodeRect1();
        g.setPaint(color1);
        g.fill(rect);

    }

    private void paintBackgroundSelectedAndMouseOver(Graphics2D g) {
        rect = decodeRect1();
        g.setPaint(color1);
        g.fill(rect);

    }

    private void paintcheckIconDisabledAndSelected(Graphics2D g) {
        path = decodePath1();
        g.setPaint(color2);
        g.fill(path);

    }

    private void paintcheckIconEnabledAndSelected(Graphics2D g) {
        path = decodePath1();
        g.setPaint(color3);
        g.fill(path);

    }

    private void paintcheckIconSelectedAndMouseOver(Graphics2D g) {
        path = decodePath1();
        g.setPaint(color4);
        g.fill(path);

    }



    private Rectangle2D decodeRect1() {
            rect.setRect(decodeX(1.0f), decodeY(1.0f), decodeX(2.0f) - decodeX(1.0f), decodeY(2.0f) - decodeY(1.0f)); return rect;
    }

    private Path2D decodePath1() {
        path.reset();
        path.moveTo(decodeX(0.0f), decodeY(1.5f));
        path.lineTo(decodeX(0.4292683f), decodeY(1.5f));
        path.lineTo(decodeX(0.7121951f), decodeY(2.4780488f));
        path.lineTo(decodeX(2.5926828f), decodeY(0.0f));
        path.lineTo(decodeX(3.0f), decodeY(0.0f));
        path.lineTo(decodeX(3.0f), decodeY(0.2f));
        path.lineTo(decodeX(2.8317075f), decodeY(0.39512196f));
        path.lineTo(decodeX(0.8f), decodeY(3.0f));
        path.lineTo(decodeX(0.5731707f), decodeY(3.0f));
        path.lineTo(decodeX(0.0f), decodeY(1.5f));
        path.closePath();
        return path;
    }




}
