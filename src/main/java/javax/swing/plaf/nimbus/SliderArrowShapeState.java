
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class SliderArrowShapeState extends State {
    SliderArrowShapeState() {
        super("ArrowShape");
    }

    @Override protected boolean isInState(JComponent c) {
 return c.getClientProperty("Slider.paintThumbArrowShape") == Boolean.TRUE; 
    }
}

