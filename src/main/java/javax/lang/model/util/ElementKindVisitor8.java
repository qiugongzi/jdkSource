

package javax.lang.model.util;

import javax.lang.model.element.*;
import javax.annotation.processing.SupportedSourceVersion;
import static javax.lang.model.SourceVersion.*;
import javax.lang.model.SourceVersion;


@SupportedSourceVersion(RELEASE_8)
public class ElementKindVisitor8<R, P> extends ElementKindVisitor7<R, P> {

    protected ElementKindVisitor8() {
        super(null);
    }


    protected ElementKindVisitor8(R defaultValue) {
        super(defaultValue);
    }
}
