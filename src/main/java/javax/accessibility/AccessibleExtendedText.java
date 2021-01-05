

package javax.accessibility;


import java.util.*;
import java.awt.*;
import javax.swing.text.*;



public interface AccessibleExtendedText {


    public static final int LINE = 4; public static final int ATTRIBUTE_RUN = 5; public String getTextRange(int startIndex, int endIndex);


    public AccessibleTextSequence getTextSequenceAt(int part, int index);


    public AccessibleTextSequence getTextSequenceAfter(int part, int index);


    public AccessibleTextSequence getTextSequenceBefore(int part, int index);


    public Rectangle getTextBounds(int startIndex, int endIndex);
}
