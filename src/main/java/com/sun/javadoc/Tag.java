

package com.sun.javadoc;

import java.text.BreakIterator;
import java.util.Locale;


public interface Tag {


    String name();


    Doc holder();


    String kind();


    String text();


    String toString();


    Tag[] inlineTags();


    Tag[] firstSentenceTags();


    public SourcePosition position();
}
