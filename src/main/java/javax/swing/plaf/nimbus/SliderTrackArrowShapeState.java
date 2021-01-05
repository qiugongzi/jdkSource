
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class SliderTrackArrowShapeState extends State {
    SliderTrackArrowShapeState() {
        super("ArrowShape");
    }

    @Override protected boolean isInState(JComponent c) {
 return c.getClientProperty("Slider.paintThumbArrowShape") == Boolean.TRUE; 
    }
}

