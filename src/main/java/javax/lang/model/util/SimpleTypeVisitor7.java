

package javax.lang.model.util;

import javax.lang.model.type.*;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import static javax.lang.model.SourceVersion.*;


@SupportedSourceVersion(RELEASE_7)
public class SimpleTypeVisitor7<R, P> extends SimpleTypeVisitor6<R, P> {

    protected SimpleTypeVisitor7(){
        super(null);
    }


    protected SimpleTypeVisitor7(R defaultValue){
        super(defaultValue);
    }


    @Override
    public R visitUnion(UnionType t, P p) {
        return defaultAction(t, p);
    }
}
