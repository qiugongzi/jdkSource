
package javax.swing.plaf.nimbus;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.Painter;


final class ArrowButtonPainter extends AbstractRegionPainter {
    static final int BACKGROUND_ENABLED = 1;
    static final int FOREGROUND_DISABLED = 2;
    static final int FOREGROUND_ENABLED = 3;


    private int state; private PaintContext ctx;

    private Path2D path = new Path2D.Float();
    private Rectangle2D rect = new Rectangle2D.Float(0, 0, 0, 0);
    private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, 0, 0, 0, 0);
    private Ellipse2D ellipse = new Ellipse2D.Float(0, 0, 0, 0);

    private Color color1 = decodeColor("nimbusBase", 0.027408898f, -0.57391655f, 0.1490196f, 0);
    private Color color2 = decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.37254906f, 0);


    private Object[] componentColors;

    public ArrowButtonPainter(PaintContext ctx, int state) {
        super();
        this.state = state;
        this.ctx = ctx;
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        componentColors = extendedCacheKeys;
        switch(state) {
            case FOREGROUND_DISABLED: paintForegroundDisabled(g); break;
            case FOREGROUND_ENABLED: paintForegroundEnabled(g); break;

        }
    }
        


    @Override
    protected final PaintContext getPaintContext() {
        return ctx;
    }

    private void paintForegroundDisabled(Graphics2D g) {
        path = decodePath1();
        g.setPaint(color1);
        g.fill(path);

    }

    private void paintForegroundEnabled(Graphics2D g) {
        path = decodePath1();
        g.setPaint(color2);
        g.fill(path);

    }



    private Path2D decodePath1() {
        path.reset();
        path.moveTo(decodeX(1.8f), decodeY(1.2f));
        path.lineTo(decodeX(1.2f), decodeY(1.5f));
        path.lineTo(decodeX(1.8f), decodeY(1.8f));
        path.lineTo(decodeX(1.8f), decodeY(1.2f));
        path.closePath();
        return path;
    }




}
