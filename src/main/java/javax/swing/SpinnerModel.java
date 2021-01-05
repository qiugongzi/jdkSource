

package javax.swing;

import java.awt.event.*;
import javax.swing.event.*;



public interface SpinnerModel
{

    Object getValue();



    void setValue(Object value);



    Object getNextValue();



    Object getPreviousValue();



    void addChangeListener(ChangeListener l);



    void removeChangeListener(ChangeListener l);
}
