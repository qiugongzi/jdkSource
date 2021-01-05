

package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import static javax.lang.model.SourceVersion.*;


@SupportedSourceVersion(RELEASE_8)
public class SimpleElementVisitor8<R, P> extends SimpleElementVisitor7<R, P> {

    protected SimpleElementVisitor8(){
        super(null);
    }


    protected SimpleElementVisitor8(R defaultValue){
        super(defaultValue);
    }
}
