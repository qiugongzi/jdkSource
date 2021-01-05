
package javax.swing.plaf.nimbus;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.Painter;


final class SeparatorPainter extends AbstractRegionPainter {
    static final int BACKGROUND_ENABLED = 1;


    private int state; private PaintContext ctx;

    private Path2D path = new Path2D.Float();
    private Rectangle2D rect = new Rectangle2D.Float(0, 0, 0, 0);
    private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, 0, 0, 0, 0);
    private Ellipse2D ellipse = new Ellipse2D.Float(0, 0, 0, 0);

    private Color color1 = decodeColor("nimbusBlueGrey", -0.008547008f, -0.03830409f, -0.039215684f, 0);


    private Object[] componentColors;

    public SeparatorPainter(PaintContext ctx, int state) {
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

    }



    private Rectangle2D decodeRect1() {
            rect.setRect(decodeX(0.0f), decodeY(1.3333334f), decodeX(3.0f) - decodeX(0.0f), decodeY(1.6666667f) - decodeY(1.3333334f)); return rect;
    }




}
