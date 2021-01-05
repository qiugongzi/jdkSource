

package javax.lang.model.util;

import javax.lang.model.type.*;
import javax.annotation.processing.SupportedSourceVersion;
import static javax.lang.model.SourceVersion.*;
import javax.lang.model.SourceVersion;


@SupportedSourceVersion(RELEASE_7)
public class TypeKindVisitor7<R, P> extends TypeKindVisitor6<R, P> {

    protected TypeKindVisitor7() {
        super(null);
    }


    protected TypeKindVisitor7(R defaultValue) {
        super(defaultValue);
    }


    @Override
    public R visitUnion(UnionType t, P p) {
        return defaultAction(t, p);
    }
}
