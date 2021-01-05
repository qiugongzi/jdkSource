

package javax.annotation.processing;

import javax.annotation.*;
import javax.tools.Diagnostic;
import javax.lang.model.element.*;


public interface Messager {

    void printMessage(Diagnostic.Kind kind, CharSequence msg);


    void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e);


    void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a);


    void printMessage(Diagnostic.Kind kind,
                      CharSequence msg,
                      Element e,
                      AnnotationMirror a,
                      AnnotationValue v);
}
