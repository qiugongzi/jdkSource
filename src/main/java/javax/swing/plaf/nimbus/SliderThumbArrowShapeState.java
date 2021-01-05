
package javax.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;


class SliderThumbArrowShapeState extends State {
    SliderThumbArrowShapeState() {
        super("ArrowShape");
    }

    @Override protected boolean isInState(JComponent c) {
 return c.getClientProperty("Slider.paintThumbArrowShape") == Boolean.TRUE; 
    }
}

