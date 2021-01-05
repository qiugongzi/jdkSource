

package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import static javax.lang.model.SourceVersion.*;



@SupportedSourceVersion(RELEASE_6)
public abstract class AbstractElementVisitor6<R, P> implements ElementVisitor<R, P> {

    protected AbstractElementVisitor6(){}


    public final R visit(Element e, P p) {
        return e.accept(this, p);
    }


    public final R visit(Element e) {
        return e.accept(this, null);
    }


    public R visitUnknown(Element e, P p) {
        throw new UnknownElementException(e, p);
    }
}
