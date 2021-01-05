

package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import static javax.lang.model.SourceVersion.*;


@SupportedSourceVersion(RELEASE_7)
public class ElementKindVisitor7<R, P> extends ElementKindVisitor6<R, P> {

    protected ElementKindVisitor7() {
        super(null);
    }


    protected ElementKindVisitor7(R defaultValue) {
        super(defaultValue);
    }


    @Override
    public R visitVariableAsResourceVariable(VariableElement e, P p) {
        return defaultAction(e, p);
    }
}
