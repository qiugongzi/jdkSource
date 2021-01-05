
package javax.swing.plaf.nimbus;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.Painter;


final class MenuBarPainter extends AbstractRegionPainter {
    static final int BACKGROUND_ENABLED = 1;
    static final int BORDER_ENABLED = 2;


    private int state; private PaintContext ctx;

    private Path2D path = new Path2D.Float();
    private Rectangle2D rect = new Rectangle2D.Float(0, 0, 0, 0);
    private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, 0, 0, 0, 0);
    private Ellipse2D ellipse = new Ellipse2D.Float(0, 0, 0, 0);

    private Color color1 = decodeColor("nimbusBlueGrey", 0.0f, -0.07016757f, 0.12941176f, 0);
    private Color color2 = decodeColor("nimbusBlueGrey", -0.027777791f, -0.10255819f, 0.23921567f, 0);
    private Color color3 = decodeColor("nimbusBlueGrey", -0.111111104f, -0.10654225f, 0.23921567f, -29);
    private Color color4 = decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -255);
    private Color color5 = decodeColor("nimbusBorder", 0.0f, 0.0f, 0.0f, 0);


    private Object[] componentColors;

    public MenuBarPainter(PaintContext ctx, int state) {
        super();
        this.state = state;
        this.ctx = ctx;
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        componentColors = extendedCacheKeys;
        switch(state) {
            case BACKGROUND_ENABLED: paintBackgroundEnabled(g); break;
            case BORDER_ENABLED: paintBorderEnabled(g); break;

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
        g.setPaint(decodeGradient1(rect));
        g.fill(rect);

    }

    private void paintBorderEnabled(Graphics2D g) {
        rect = decodeRect3();
        g.setPaint(color5);
        g.fill(rect);

    }



    private Rectangle2D decodeRect1() {
            rect.setRect(decodeX(1.0f), decodeY(0.0f), decodeX(2.0f) - decodeX(1.0f), decodeY(1.9523809f) - decodeY(0.0f)); return rect;
    }

    private Rectangle2D decodeRect2() {
            rect.setRect(decodeX(1.0f), decodeY(0.0f), decodeX(2.0f) - decodeX(1.0f), decodeY(2.0f) - decodeY(0.0f)); return rect;
    }

    private Rectangle2D decodeRect3() {
            rect.setRect(decodeX(1.0f), decodeY(2.0f), decodeX(2.0f) - decodeX(1.0f), decodeY(3.0f) - decodeY(2.0f)); return rect;
    }



    private Paint decodeGradient1(Shape s) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();
        return decodeGradient((1.0f * w) + x, (0.0f * h) + y, (1.0f * w) + x, (1.0f * h) + y,
                new float[] { 0.0f,0.015f,0.03f,0.23354445f,0.7569444f },
                new Color[] { color2,
                            decodeColor(color2,color3,0.5f),
                            color3,
                            decodeColor(color3,color4,0.5f),
                            color4});
    }


}
