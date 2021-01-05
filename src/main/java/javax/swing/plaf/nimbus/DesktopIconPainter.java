
package javax.swing.plaf.nimbus;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.Painter;


final class DesktopIconPainter extends AbstractRegionPainter {
    static final int BACKGROUND_ENABLED = 1;


    private int state; private PaintContext ctx;

    private Path2D path = new Path2D.Float();
    private Rectangle2D rect = new Rectangle2D.Float(0, 0, 0, 0);
    private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, 0, 0, 0, 0);
    private Ellipse2D ellipse = new Ellipse2D.Float(0, 0, 0, 0);

    private Color color1 = decodeColor("nimbusBase", 0.02551502f, -0.47885156f, -0.34901965f, 0);
    private Color color2 = decodeColor("nimbusBlueGrey", -0.027777791f, -0.102261856f, 0.20392156f, 0);
    private Color color3 = decodeColor("nimbusBlueGrey", 0.0f, -0.0682728f, 0.09019607f, 0);
    private Color color4 = decodeColor("nimbusBlueGrey", -0.01111114f, -0.088974595f, 0.16470587f, 0);
    private Color color5 = decodeColor("nimbusBlueGrey", 0.0f, -0.029445238f, -0.019607842f, 0);


    private Object[] componentColors;

    public DesktopIconPainter(PaintContext ctx, int state) {
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
        roundRect = decodeRoundRect1();
        g.setPaint(color1);
        g.fill(roundRect);
        roundRect = decodeRoundRect2();
        g.setPaint(decodeGradient1(roundRect));
        g.fill(roundRect);
        rect = decodeRect1();
        g.setPaint(decodeGradient2(rect));
        g.fill(rect);

    }



    private RoundRectangle2D decodeRoundRect1() {
        roundRect.setRoundRect(decodeX(0.4f), decodeY(0.0f), decodeX(2.8f) - decodeX(0.4f), decodeY(2.6f) - decodeY(0.0f), 4.8333335f, 4.8333335f); return roundRect;
    }

    private RoundRectangle2D decodeRoundRect2() {
        roundRect.setRoundRect(decodeX(0.6f), decodeY(0.2f), decodeX(2.8f) - decodeX(0.6f), decodeY(2.4f) - decodeY(0.2f), 3.1f, 3.1f); return roundRect;
    }

    private Rectangle2D decodeRect1() {
            rect.setRect(decodeX(0.8f), decodeY(0.4f), decodeX(2.4f) - decodeX(0.8f), decodeY(2.2f) - decodeY(0.4f)); return rect;
    }



    private Paint decodeGradient1(Shape s) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();
        return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
                new float[] { 0.0f,0.5f,1.0f },
                new Color[] { color2,
                            decodeColor(color2,color3,0.5f),
                            color3});
    }

    private Paint decodeGradient2(Shape s) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();
        return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
                new float[] { 0.0f,0.24f,1.0f },
                new Color[] { color4,
                            decodeColor(color4,color5,0.5f),
                            color5});
    }


}
