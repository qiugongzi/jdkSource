

package javax.lang.model.util;

import javax.lang.model.type.*;


public abstract class AbstractTypeVisitor8<R, P> extends AbstractTypeVisitor7<R, P> {

    protected AbstractTypeVisitor8() {
        super();
    }


    public abstract R visitIntersection(IntersectionType t, P p);
}
