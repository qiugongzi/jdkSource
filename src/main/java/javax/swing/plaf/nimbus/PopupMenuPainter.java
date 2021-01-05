
package javax.swing.plaf.nimbus;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.Painter;


final class PopupMenuPainter extends AbstractRegionPainter {
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;


    private int state; private PaintContext ctx;

    private Path2D path = new Path2D.Float();
    private Rectangle2D rect = new Rectangle2D.Float(0, 0, 0, 0);
    private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, 0, 0, 0, 0);
    private Ellipse2D ellipse = new Ellipse2D.Float(0, 0, 0, 0);

    private Color color1 = decodeColor("nimbusBlueGrey", -0.6111111f, -0.110526316f, -0.39607844f, 0);
    private Color color2 = decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, 0);
    private Color color3 = decodeColor("nimbusBase", 0.021348298f, -0.6150531f, 0.39999998f, 0);


    private Object[] componentColors;

    public PopupMenuPainter(PaintContext ctx, int state) {
        super();
        this.state = state;
        this.ctx = ctx;
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        componentColors = extendedCacheKeys;
        switch(state) {
            case BACKGROUND_DISABLED: paintBackgroundDisabled(g); break;
            case BACKGROUND_ENABLED: paintBackgroundEnabled(g); break;

        }
    }
        


    @Override
    protected final PaintContext getPaintContext() {
        return ctx;
    }

    private void paintBackgroundDisabled(Graphics2D g) {
        rect = decodeRect1();
        g.setPaint(color1);
        g.fill(rect);
        rect = decodeRect2();
        g.setPaint(decodeGradient1(rect));
        g.fill(rect);

    }

    private void paintBackgroundEnabled(Graphics2D g) {
        rect = decodeRect3();
        g.setPaint(color1);
        g.fill(rect);
        rect = decodeRect4();
        g.setPaint(decodeGradient1(rect));
        g.fill(rect);

    }



    private Rectangle2D decodeRect1() {
            rect.setRect(decodeX(1.0f), decodeY(0.0f), decodeX(2.0f) - decodeX(1.0f), decodeY(3.0f) - decodeY(0.0f)); return rect;
    }

    private Rectangle2D decodeRect2() {
            rect.setRect(decodeX(1.0045455f), decodeY(0.11111111f), decodeX(1.9954545f) - decodeX(1.0045455f), decodeY(2.909091f) - decodeY(0.11111111f)); return rect;
    }

    private Rectangle2D decodeRect3() {
            rect.setRect(decodeX(0.0f), decodeY(0.0f), decodeX(3.0f) - decodeX(0.0f), decodeY(3.0f) - decodeY(0.0f)); return rect;
    }

    private Rectangle2D decodeRect4() {
            rect.setRect(decodeX(0.5f), decodeY(0.09090909f), decodeX(2.5f) - decodeX(0.5f), decodeY(2.909091f) - decodeY(0.09090909f)); return rect;
    }



    private Paint decodeGradient1(Shape s) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();
        return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
                new float[] { 0.0f,0.003f,0.02f,0.5f,0.98f,0.996f,1.0f },
                new Color[] { color2,
                            decodeColor(color2,color3,0.5f),
                            color3,
                            decodeColor(color3,color3,0.5f),
                            color3,
                            decodeColor(color3,color2,0.5f),
                            color2});
    }


}
