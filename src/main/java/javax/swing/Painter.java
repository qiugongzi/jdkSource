
package javax.swing;

import java.awt.Graphics2D;


public interface Painter<T> {

    public void paint(Graphics2D g, T object, int width, int height);
}
