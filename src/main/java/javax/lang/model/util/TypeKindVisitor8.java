

package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.*;
import static javax.lang.model.SourceVersion.*;


@SupportedSourceVersion(RELEASE_8)
public class TypeKindVisitor8<R, P> extends TypeKindVisitor7<R, P> {

    protected TypeKindVisitor8() {
        super(null);
    }


    protected TypeKindVisitor8(R defaultValue) {
        super(defaultValue);
    }


    @Override
    public R visitIntersection(IntersectionType t, P p) {
        return defaultAction(t, p);
    }
}
