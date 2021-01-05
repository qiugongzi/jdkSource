

package javax.lang.model.util;

import javax.lang.model.type.*;


public abstract class AbstractTypeVisitor7<R, P> extends AbstractTypeVisitor6<R, P> {

    protected AbstractTypeVisitor7() {
        super();
    }


    public abstract R visitUnion(UnionType t, P p);
}
