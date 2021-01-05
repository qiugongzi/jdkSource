
package javax.swing.plaf.nimbus;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.Painter;


final class TreeCellPainter extends AbstractRegionPainter {
    static final int BACKGROUND_ENABLED = 1;
    static final int BACKGROUND_ENABLED_FOCUSED = 2;
    static final int BACKGROUND_ENABLED_SELECTED = 3;
    static final int BACKGROUND_SELECTED_FOCUSED = 4;


    private int state; private PaintContext ctx;

    private Path2D path = new Path2D.Float();
    private Rectangle2D rect = new Rectangle2D.Float(0, 0, 0, 0);
    private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, 0, 0, 0, 0);
    private Ellipse2D ellipse = new Ellipse2D.Float(0, 0, 0, 0);

    private Color color1 = decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
    private Color color2 = decodeColor("nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0);


    private Object[] componentColors;

    public TreeCellPainter(PaintContext ctx, int state) {
        super();
        this.state = state;
        this.ctx = ctx;
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        componentColors = extendedCacheKeys;
        switch(state) {
            case BACKGROUND_ENABLED_FOCUSED: paintBackgroundEnabledAndFocused(g); break;
            case BACKGROUND_ENABLED_SELECTED: paintBackgroundEnabledAndSelected(g); break;
            case BACKGROUND_SELECTED_FOCUSED: paintBackgroundSelectedAndFocused(g); break;

        }
    }
        


    @Override
    protected final PaintContext getPaintContext() {
        return ctx;
    }

    private void paintBackgroundEnabledAndFocused(Graphics2D g) {
        path = decodePath1();
        g.setPaint(color1);
        g.fill(path);

    }

    private void paintBackgroundEnabledAndSelected(Graphics2D g) {
        rect = decodeRect1();
        g.setPaint(color2);
        g.fill(rect);

    }

    private void paintBackgroundSelectedAndFocused(Graphics2D g) {
        rect = decodeRect1();
        g.setPaint(color2);
        g.fill(rect);
        path = decodePath1();
        g.setPaint(color1);
        g.fill(path);

    }



    private Path2D decodePath1() {
        path.reset();
        path.moveTo(decodeX(0.0f), decodeY(0.0f));
        path.lineTo(decodeX(0.0f), decodeY(3.0f));
        path.lineTo(decodeX(3.0f), decodeY(3.0f));
        path.lineTo(decodeX(3.0f), decodeY(0.0f));
        path.lineTo(decodeX(0.24000001f), decodeY(0.0f));
        path.lineTo(decodeX(0.24000001f), decodeY(0.24000001f));
        path.lineTo(decodeX(2.7600007f), decodeY(0.24000001f));
        path.lineTo(decodeX(2.7600007f), decodeY(2.7599998f));
        path.lineTo(decodeX(0.24000001f), decodeY(2.7599998f));
        path.lineTo(decodeX(0.24000001f), decodeY(0.0f));
        path.lineTo(decodeX(0.0f), decodeY(0.0f));
        path.closePath();
        return path;
    }

    private Rectangle2D decodeRect1() {
            rect.setRect(decodeX(0.0f), decodeY(0.0f), decodeX(3.0f) - decodeX(0.0f), decodeY(3.0f) - decodeY(0.0f)); return rect;
    }




}
