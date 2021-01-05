

package javax.lang.model.util;

import javax.lang.model.element.*;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import static javax.lang.model.SourceVersion.*;


@SupportedSourceVersion(RELEASE_7)
public class SimpleElementVisitor7<R, P> extends SimpleElementVisitor6<R, P> {

    protected SimpleElementVisitor7(){
        super(null);
    }


    protected SimpleElementVisitor7(R defaultValue){
        super(defaultValue);
    }


    @Override
    public R visitVariable(VariableElement e, P p) {
        return defaultAction(e, p);
    }
}
