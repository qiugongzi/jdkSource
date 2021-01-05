
package javax.swing.plaf.nimbus;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.Painter;


final class TableHeaderPainter extends AbstractRegionPainter {
    static final int ASCENDINGSORTICON_ENABLED = 1;
    static final int DESCENDINGSORTICON_ENABLED = 2;


    private int state; private PaintContext ctx;

    private Path2D path = new Path2D.Float();
    private Rectangle2D rect = new Rectangle2D.Float(0, 0, 0, 0);
    private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, 0, 0, 0, 0);
    private Ellipse2D ellipse = new Ellipse2D.Float(0, 0, 0, 0);

    private Color color1 = decodeColor("nimbusBase", 0.0057927966f, -0.21904764f, 0.15686274f, 0);
    private Color color2 = decodeColor("nimbusBase", 0.0038565993f, 0.02012986f, 0.054901958f, 0);


    private Object[] componentColors;

    public TableHeaderPainter(PaintContext ctx, int state) {
        super();
        this.state = state;
        this.ctx = ctx;
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        componentColors = extendedCacheKeys;
        switch(state) {
            case ASCENDINGSORTICON_ENABLED: paintascendingSortIconEnabled(g); break;
            case DESCENDINGSORTICON_ENABLED: paintdescendingSortIconEnabled(g); break;

        }
    }
        


    @Override
    protected final PaintContext getPaintContext() {
        return ctx;
    }

    private void paintascendingSortIconEnabled(Graphics2D g) {
        path = decodePath1();
        g.setPaint(decodeGradient1(path));
        g.fill(path);

    }

    private void paintdescendingSortIconEnabled(Graphics2D g) {
        path = decodePath2();
        g.setPaint(decodeGradient1(path));
        g.fill(path);

    }



    private Path2D decodePath1() {
        path.reset();
        path.moveTo(decodeX(1.0f), decodeY(2.0f));
        path.lineTo(decodeX(1.7070175f), decodeY(0.0f));
        path.lineTo(decodeX(3.0f), decodeY(2.0f));
        path.lineTo(decodeX(1.0f), decodeY(2.0f));
        path.closePath();
        return path;
    }

    private Path2D decodePath2() {
        path.reset();
        path.moveTo(decodeX(1.0f), decodeY(1.0f));
        path.lineTo(decodeX(2.0f), decodeY(1.0f));
        path.lineTo(decodeX(1.5025063f), decodeY(2.0f));
        path.lineTo(decodeX(1.0f), decodeY(1.0f));
        path.closePath();
        return path;
    }



    private Paint decodeGradient1(Shape s) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();
        return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
                new float[] { 0.0f,0.5f,1.0f },
                new Color[] { color1,
                            decodeColor(color1,color2,0.5f),
                            color2});
    }


}
