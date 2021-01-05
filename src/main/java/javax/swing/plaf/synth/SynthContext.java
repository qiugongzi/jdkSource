
package javax.swing.plaf.synth;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JComponent;


public class SynthContext {
    private static final Queue<SynthContext> queue = new ConcurrentLinkedQueue<>();

    private JComponent component;
    private Region region;
    private SynthStyle style;
    private int state;

    static SynthContext getContext(JComponent c, SynthStyle style, int state) {
        return getContext(c, SynthLookAndFeel.getRegion(c), style, state);
    }

    static SynthContext getContext(JComponent component,
                                   Region region, SynthStyle style,
                                   int state) {
        SynthContext context = queue.poll();
        if (context == null) {
            context = new SynthContext();
        }
        context.reset(component, region, style, state);
        return context;
    }

    static void releaseContext(SynthContext context) {
        queue.offer(context);
    }

    SynthContext() {
    }


    public SynthContext(JComponent component, Region region, SynthStyle style,
                        int state) {
        if (component == null || region == null || style == null) {
            throw new NullPointerException(
                "You must supply a non-null component, region and style");
        }
        reset(component, region, style, state);
    }



    public JComponent getComponent() {
        return component;
    }


    public Region getRegion() {
        return region;
    }


    boolean isSubregion() {
        return getRegion().isSubregion();
    }

    void setStyle(SynthStyle style) {
        this.style = style;
    }


    public SynthStyle getStyle() {
        return style;
    }

    void setComponentState(int state) {
        this.state = state;
    }


    public int getComponentState() {
        return state;
    }


    void reset(JComponent component, Region region, SynthStyle style,
               int state) {
        this.component = component;
        this.region = region;
        this.style = style;
        this.state = state;
    }

    void dispose() {
        this.component = null;
        this.style = null;
        releaseContext(this);
    }


    SynthPainter getPainter() {
        SynthPainter painter = getStyle().getPainter(this);

        if (painter != null) {
            return painter;
        }
        return SynthPainter.NULL_PAINTER;
    }
}
