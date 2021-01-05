
package javax.swing.plaf.nimbus;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.Painter;


final class InternalFrameTitlePanePainter extends AbstractRegionPainter {




    static final int BACKGROUND_ENABLED = 1;


    private int state;
    private PaintContext ctx;


    private Path2D path = new Path2D.Float();
    private Rectangle2D rect = new Rectangle2D.Float(0, 0, 0, 0);
    private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, 0, 0, 0, 0);
    private Ellipse2D ellipse = new Ellipse2D.Float(0, 0, 0, 0);







    private Object[] componentColors;

    public InternalFrameTitlePanePainter(PaintContext ctx, int state) {
        super();
        this.state = state;
        this.ctx = ctx;
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {

        componentColors = extendedCacheKeys;


        switch(state) {

        }
    }
        


    @Override
    protected final PaintContext getPaintContext() {
        return ctx;
    }






}
