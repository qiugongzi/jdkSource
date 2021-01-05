

package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import static sun.swing.SwingUtilities2.drawHLine;
import static sun.swing.SwingUtilities2.drawRect;
import static sun.swing.SwingUtilities2.drawVLine;



public class MetalScrollBarUI extends BasicScrollBarUI
{
    private static Color shadowColor;
    private static Color highlightColor;
    private static Color darkShadowColor;
    private static Color thumbColor;
    private static Color thumbShadow;
    private static Color thumbHighlightColor;


    protected MetalBumps bumps;

    protected MetalScrollButton increaseButton;
    protected MetalScrollButton decreaseButton;

    protected  int scrollBarWidth;

    public static final String FREE_STANDING_PROP = "JScrollBar.isFreeStanding";
    protected boolean isFreeStanding = true;

    public static ComponentUI createUI( JComponent c )
    {
        return new MetalScrollBarUI();
    }

    protected void installDefaults() {
        scrollBarWidth = ((Integer)(UIManager.get( "ScrollBar.width" ))).intValue();
        super.installDefaults();
        bumps = new MetalBumps( 10, 10, thumbHighlightColor, thumbShadow, thumbColor );
    }

    protected void installListeners(){
        super.installListeners();
        ((ScrollBarListener)propertyChangeListener).handlePropertyChange( scrollbar.getClientProperty( FREE_STANDING_PROP ) );
    }

    protected PropertyChangeListener createPropertyChangeListener(){
        return new ScrollBarListener();
    }

    protected void configureScrollBarColors()
    {
        super.configureScrollBarColors();
        shadowColor         = UIManager.getColor("ScrollBar.shadow");
        highlightColor      = UIManager.getColor("ScrollBar.highlight");
        darkShadowColor     = UIManager.getColor("ScrollBar.darkShadow");
        thumbColor          = UIManager.getColor("ScrollBar.thumb");
        thumbShadow         = UIManager.getColor("ScrollBar.thumbShadow");
        thumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");


    }

    public Dimension getPreferredSize( JComponent c )
    {
        if ( scrollbar.getOrientation() == JScrollBar.VERTICAL )
        {
            return new Dimension( scrollBarWidth, scrollBarWidth * 3 + 10 );
        }
        else  {
            return new Dimension( scrollBarWidth * 3 + 10, scrollBarWidth );
        }

    }


    protected JButton createDecreaseButton( int orientation )
    {
        decreaseButton = new MetalScrollButton( orientation, scrollBarWidth, isFreeStanding );
        return decreaseButton;
    }


    protected JButton createIncreaseButton( int orientation )
    {
        increaseButton =  new MetalScrollButton( orientation, scrollBarWidth, isFreeStanding );
        return increaseButton;
    }

    protected void paintTrack( Graphics g, JComponent c, Rectangle trackBounds )
    {
        g.translate( trackBounds.x, trackBounds.y );

        boolean leftToRight = MetalUtils.isLeftToRight(c);

        if ( scrollbar.getOrientation() == JScrollBar.VERTICAL )
        {
            if ( !isFreeStanding ) {
                trackBounds.width += 2;
                if ( !leftToRight ) {
                    g.translate( -1, 0 );
                }
            }

            if ( c.isEnabled() ) {
                g.setColor( darkShadowColor );
                drawVLine(g, 0, 0, trackBounds.height - 1);
                drawVLine(g, trackBounds.width - 2, 0, trackBounds.height - 1);
                drawHLine(g, 2, trackBounds.width - 1, trackBounds.height - 1);
                drawHLine(g, 2, trackBounds.width - 2, 0);

                g.setColor( shadowColor );
                drawVLine(g, 1, 1, trackBounds.height - 2);
                drawHLine(g, 1, trackBounds.width - 3, 1);
                if (scrollbar.getValue() != scrollbar.getMaximum()) {  int y = thumbRect.y + thumbRect.height - trackBounds.y;
                    drawHLine(g, 1, trackBounds.width - 1, y);
                }
                g.setColor(highlightColor);
                drawVLine(g, trackBounds.width - 1, 0, trackBounds.height - 1);
            } else {
                MetalUtils.drawDisabledBorder(g, 0, 0, trackBounds.width, trackBounds.height );
            }

            if ( !isFreeStanding ) {
                trackBounds.width -= 2;
                if ( !leftToRight ) {
                    g.translate( 1, 0 );
                }
            }
        }
        else  {
            if ( !isFreeStanding ) {
                trackBounds.height += 2;
            }

            if ( c.isEnabled() ) {
                g.setColor( darkShadowColor );
                drawHLine(g, 0, trackBounds.width - 1, 0);  drawVLine(g, 0, 2, trackBounds.height - 2); drawHLine(g, 0, trackBounds.width - 1, trackBounds.height - 2 ); drawVLine(g, trackBounds.width - 1, 2,  trackBounds.height - 1 ); g.setColor( shadowColor );
                drawHLine(g, 1, trackBounds.width - 2, 1 );  drawVLine(g, 1, 1, trackBounds.height - 3 ); drawHLine(g, 0, trackBounds.width - 1, trackBounds.height - 1 ); if (scrollbar.getValue() != scrollbar.getMaximum()) {  int x = thumbRect.x + thumbRect.width - trackBounds.x;
                    drawVLine(g, x, 1, trackBounds.height-1);
                }
            } else {
                MetalUtils.drawDisabledBorder(g, 0, 0, trackBounds.width, trackBounds.height );
            }

            if ( !isFreeStanding ) {
                trackBounds.height -= 2;
            }
        }

        g.translate( -trackBounds.x, -trackBounds.y );
    }

    protected void paintThumb( Graphics g, JComponent c, Rectangle thumbBounds )
    {
        if (!c.isEnabled()) {
            return;
        }

        if (MetalLookAndFeel.usingOcean()) {
            oceanPaintThumb(g, c, thumbBounds);
            return;
        }

        boolean leftToRight = MetalUtils.isLeftToRight(c);

        g.translate( thumbBounds.x, thumbBounds.y );

        if ( scrollbar.getOrientation() == JScrollBar.VERTICAL )
        {
            if ( !isFreeStanding ) {
                thumbBounds.width += 2;
                if ( !leftToRight ) {
                    g.translate( -1, 0 );
                }
            }

            g.setColor( thumbColor );
            g.fillRect( 0, 0, thumbBounds.width - 2, thumbBounds.height - 1 );

            g.setColor( thumbShadow );
            drawRect(g, 0, 0, thumbBounds.width - 2, thumbBounds.height - 1);

            g.setColor( thumbHighlightColor );
            drawHLine(g, 1, thumbBounds.width - 3, 1);
            drawVLine(g, 1, 1, thumbBounds.height - 2);

            bumps.setBumpArea( thumbBounds.width - 6, thumbBounds.height - 7 );
            bumps.paintIcon( c, g, 3, 4 );

            if ( !isFreeStanding ) {
                thumbBounds.width -= 2;
                if ( !leftToRight ) {
                    g.translate( 1, 0 );
                }
            }
        }
        else  {
            if ( !isFreeStanding ) {
                thumbBounds.height += 2;
            }

            g.setColor( thumbColor );
            g.fillRect( 0, 0, thumbBounds.width - 1, thumbBounds.height - 2 );

            g.setColor( thumbShadow );
            drawRect(g, 0, 0, thumbBounds.width - 1, thumbBounds.height - 2);

            g.setColor( thumbHighlightColor );
            drawHLine(g, 1, thumbBounds.width - 3, 1);
            drawVLine(g, 1, 1, thumbBounds.height - 3);

            bumps.setBumpArea( thumbBounds.width - 7, thumbBounds.height - 6 );
            bumps.paintIcon( c, g, 4, 3 );

            if ( !isFreeStanding ) {
                thumbBounds.height -= 2;
            }
        }

        g.translate( -thumbBounds.x, -thumbBounds.y );
    }

    private void oceanPaintThumb(Graphics g, JComponent c,
                                   Rectangle thumbBounds) {
        boolean leftToRight = MetalUtils.isLeftToRight(c);

        g.translate(thumbBounds.x, thumbBounds.y);

        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            if (!isFreeStanding) {
                thumbBounds.width += 2;
                if (!leftToRight) {
                    g.translate(-1, 0);
                }
            }

            if (thumbColor != null) {
                g.setColor(thumbColor);
                g.fillRect(0, 0, thumbBounds.width - 2,thumbBounds.height - 1);
            }

            g.setColor(thumbShadow);
            drawRect(g, 0, 0, thumbBounds.width - 2, thumbBounds.height - 1);

            g.setColor(thumbHighlightColor);
            drawHLine(g, 1, thumbBounds.width - 3, 1);
            drawVLine(g, 1, 1, thumbBounds.height - 2);

            MetalUtils.drawGradient(c, g, "ScrollBar.gradient", 2, 2,
                                    thumbBounds.width - 4,
                                    thumbBounds.height - 3, false);

            int gripSize = thumbBounds.width - 8;
            if (gripSize > 2 && thumbBounds.height >= 10) {
                g.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
                int gripY = thumbBounds.height / 2 - 2;
                for (int counter = 0; counter < 6; counter += 2) {
                    g.fillRect(4, counter + gripY, gripSize, 1);
                }

                g.setColor(MetalLookAndFeel.getWhite());
                gripY++;
                for (int counter = 0; counter < 6; counter += 2) {
                    g.fillRect(5, counter + gripY, gripSize, 1);
                }
            }
            if (!isFreeStanding) {
                thumbBounds.width -= 2;
                if (!leftToRight) {
                    g.translate(1, 0);
                }
            }
        }
        else { if (!isFreeStanding) {
                thumbBounds.height += 2;
            }

            if (thumbColor != null) {
                g.setColor(thumbColor);
                g.fillRect(0, 0, thumbBounds.width - 1,thumbBounds.height - 2);
            }

            g.setColor(thumbShadow);
            drawRect(g, 0, 0, thumbBounds.width - 1, thumbBounds.height - 2);

            g.setColor(thumbHighlightColor);
            drawHLine(g, 1, thumbBounds.width - 2, 1);
            drawVLine(g, 1, 1, thumbBounds.height - 3);

            MetalUtils.drawGradient(c, g, "ScrollBar.gradient", 2, 2,
                                    thumbBounds.width - 3,
                                    thumbBounds.height - 4, true);

            int gripSize = thumbBounds.height - 8;
            if (gripSize > 2 && thumbBounds.width >= 10) {
                g.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
                int gripX = thumbBounds.width / 2 - 2;
                for (int counter = 0; counter < 6; counter += 2) {
                    g.fillRect(gripX + counter, 4, 1, gripSize);
                }

                g.setColor(MetalLookAndFeel.getWhite());
                gripX++;
                for (int counter = 0; counter < 6; counter += 2) {
                    g.fillRect(gripX + counter, 5, 1, gripSize);
                }
            }

            if (!isFreeStanding) {
                thumbBounds.height -= 2;
            }
        }

        g.translate( -thumbBounds.x, -thumbBounds.y );
    }

    protected Dimension getMinimumThumbSize()
    {
        return new Dimension( scrollBarWidth, scrollBarWidth );
    }


    protected void setThumbBounds(int x, int y, int width, int height)
    {

        if ((thumbRect.x == x) &&
            (thumbRect.y == y) &&
            (thumbRect.width == width) &&
            (thumbRect.height == height)) {
            return;
        }


        int minX = Math.min(x, thumbRect.x);
        int minY = Math.min(y, thumbRect.y);
        int maxX = Math.max(x + width, thumbRect.x + thumbRect.width);
        int maxY = Math.max(y + height, thumbRect.y + thumbRect.height);

        thumbRect.setBounds(x, y, width, height);
        scrollbar.repaint(minX, minY, (maxX - minX)+1, (maxY - minY)+1);
    }



    class ScrollBarListener extends BasicScrollBarUI.PropertyChangeHandler
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String name = e.getPropertyName();
            if ( name.equals( FREE_STANDING_PROP ) )
            {
                handlePropertyChange( e.getNewValue() );
            }
            else {
                super.propertyChange( e );
            }
        }

        public void handlePropertyChange( Object newValue )
        {
            if ( newValue != null )
            {
                boolean temp = ((Boolean)newValue).booleanValue();
                boolean becameFlush = temp == false && isFreeStanding == true;
                boolean becameNormal = temp == true && isFreeStanding == false;

                isFreeStanding = temp;

                if ( becameFlush ) {
                    toFlush();
                }
                else if ( becameNormal ) {
                    toFreeStanding();
                }
            }
            else
            {

                if ( !isFreeStanding ) {
                    isFreeStanding = true;
                    toFreeStanding();
                }

                }

            if ( increaseButton != null )
            {
                increaseButton.setFreeStanding( isFreeStanding );
            }
            if ( decreaseButton != null )
            {
                decreaseButton.setFreeStanding( isFreeStanding );
            }
        }

        protected void toFlush() {
            scrollBarWidth -= 2;
        }

        protected void toFreeStanding() {
            scrollBarWidth += 2;
        }
    } }
