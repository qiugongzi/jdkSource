
package javax.swing.plaf.nimbus;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.Painter;


final class ScrollPanePainter extends AbstractRegionPainter {
    static final int BACKGROUND_ENABLED = 1;
    static final int BORDER_ENABLED_FOCUSED = 2;
    static final int BORDER_ENABLED = 3;


    private int state; private PaintContext ctx;

    private Path2D path = new Path2D.Float();
    private Rectangle2D rect = new Rectangle2D.Float(0, 0, 0, 0);
    private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, 0, 0, 0, 0);
    private Ellipse2D ellipse = new Ellipse2D.Float(0, 0, 0, 0);

    private Color color1 = decodeColor("nimbusBorder", 0.0f, 0.0f, 0.0f, 0);
    private Color color2 = decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);


    private Object[] componentColors;

    public ScrollPanePainter(PaintContext ctx, int state) {
        super();
        this.state = state;
        this.ctx = ctx;
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        componentColors = extendedCacheKeys;
        switch(state) {
            case BORDER_ENABLED_FOCUSED: paintBorderEnabledAndFocused(g); break;
            case BORDER_ENABLED: paintBorderEnabled(g); break;

        }
    }
        


    @Override
    protected final PaintContext getPaintContext() {
        return ctx;
    }

    private void paintBorderEnabledAndFocused(Graphics2D g) {
        rect = decodeRect1();
        g.setPaint(color1);
        g.fill(rect);
        rect = decodeRect2();
        g.setPaint(color1);
        g.fill(rect);
        rect = decodeRect3();
        g.setPaint(color1);
        g.fill(rect);
        rect = decodeRect4();
        g.setPaint(color1);
        g.fill(rect);
        path = decodePath1();
        g.setPaint(color2);
        g.fill(path);

    }

    private void paintBorderEnabled(Graphics2D g) {
        rect = decodeRect1();
        g.setPaint(color1);
        g.fill(rect);
        rect = decodeRect2();
        g.setPaint(color1);
        g.fill(rect);
        rect = decodeRect3();
        g.setPaint(color1);
        g.fill(rect);
        rect = decodeRect4();
        g.setPaint(color1);
        g.fill(rect);

    }



    private Rectangle2D decodeRect1() {
            rect.setRect(decodeX(0.6f), decodeY(0.4f), decodeX(2.4f) - decodeX(0.6f), decodeY(0.6f) - decodeY(0.4f)); return rect;
    }

    private Rectangle2D decodeRect2() {
            rect.setRect(decodeX(0.4f), decodeY(0.4f), decodeX(0.6f) - decodeX(0.4f), decodeY(2.6f) - decodeY(0.4f)); return rect;
    }

    private Rectangle2D decodeRect3() {
            rect.setRect(decodeX(2.4f), decodeY(0.4f), decodeX(2.6f) - decodeX(2.4f), decodeY(2.6f) - decodeY(0.4f)); return rect;
    }

    private Rectangle2D decodeRect4() {
            rect.setRect(decodeX(0.6f), decodeY(2.4f), decodeX(2.4f) - decodeX(0.6f), decodeY(2.6f) - decodeY(2.4f)); return rect;
    }

    private Path2D decodePath1() {
        path.reset();
        path.moveTo(decodeX(0.4f), decodeY(0.4f));
        path.lineTo(decodeX(0.4f), decodeY(2.6f));
        path.lineTo(decodeX(2.6f), decodeY(2.6f));
        path.lineTo(decodeX(2.6f), decodeY(0.4f));
        path.curveTo(decodeAnchorX(2.5999999046325684f, 0.0f), decodeAnchorY(0.4000000059604645f, 0.0f), decodeAnchorX(2.880000352859497f, 0.09999999999999432f), decodeAnchorY(0.4000000059604645f, 0.0f), decodeX(2.8800004f), decodeY(0.4f));
        path.curveTo(decodeAnchorX(2.880000352859497f, 0.09999999999999432f), decodeAnchorY(0.4000000059604645f, 0.0f), decodeAnchorX(2.880000352859497f, 0.0f), decodeAnchorY(2.879999876022339f, 0.0f), decodeX(2.8800004f), decodeY(2.8799999f));
        path.lineTo(decodeX(0.120000005f), decodeY(2.8799999f));
        path.lineTo(decodeX(0.120000005f), decodeY(0.120000005f));
        path.lineTo(decodeX(2.8800004f), decodeY(0.120000005f));
        path.lineTo(decodeX(2.8800004f), decodeY(0.4f));
        path.lineTo(decodeX(0.4f), decodeY(0.4f));
        path.closePath();
        return path;
    }




}
