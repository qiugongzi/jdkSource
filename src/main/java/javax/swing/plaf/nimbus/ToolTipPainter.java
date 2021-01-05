
package javax.swing.plaf.nimbus;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.Painter;


final class ToolTipPainter extends AbstractRegionPainter {
    static final int BACKGROUND_ENABLED = 1;


    private int state; private PaintContext ctx;

    private Path2D path = new Path2D.Float();
    private Rectangle2D rect = new Rectangle2D.Float(0, 0, 0, 0);
    private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, 0, 0, 0, 0);
    private Ellipse2D ellipse = new Ellipse2D.Float(0, 0, 0, 0);

    private Color color1 = decodeColor("nimbusBorder", 0.0f, 0.0f, 0.0f, 0);
    private Color color2 = decodeColor("info", 0.0f, 0.0f, 0.0f, 0);


    private Object[] componentColors;

    public ToolTipPainter(PaintContext ctx, int state) {
        super();
        this.state = state;
        this.ctx = ctx;
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        componentColors = extendedCacheKeys;
        switch(state) {
            case BACKGROUND_ENABLED: paintBackgroundEnabled(g); break;

        }
    }
        


    @Override
    protected final PaintContext getPaintContext() {
        return ctx;
    }

    private void paintBackgroundEnabled(Graphics2D g) {
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
        rect = decodeRect5();
        g.setPaint(color2);
        g.fill(rect);

    }



    private Rectangle2D decodeRect1() {
            rect.setRect(decodeX(2.0f), decodeY(1.0f), decodeX(3.0f) - decodeX(2.0f), decodeY(2.0f) - decodeY(1.0f)); return rect;
    }

    private Rectangle2D decodeRect2() {
            rect.setRect(decodeX(0.0f), decodeY(1.0f), decodeX(1.0f) - decodeX(0.0f), decodeY(2.0f) - decodeY(1.0f)); return rect;
    }

    private Rectangle2D decodeRect3() {
            rect.setRect(decodeX(0.0f), decodeY(2.0f), decodeX(3.0f) - decodeX(0.0f), decodeY(3.0f) - decodeY(2.0f)); return rect;
    }

    private Rectangle2D decodeRect4() {
            rect.setRect(decodeX(0.0f), decodeY(0.0f), decodeX(3.0f) - decodeX(0.0f), decodeY(1.0f) - decodeY(0.0f)); return rect;
    }

    private Rectangle2D decodeRect5() {
            rect.setRect(decodeX(1.0f), decodeY(1.0f), decodeX(2.0f) - decodeX(1.0f), decodeY(2.0f) - decodeY(1.0f)); return rect;
    }




}
