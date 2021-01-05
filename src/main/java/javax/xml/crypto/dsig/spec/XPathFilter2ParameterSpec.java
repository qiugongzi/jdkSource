

package javax.xml.crypto.dsig.spec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.dsig.Transform;


public final class XPathFilter2ParameterSpec implements TransformParameterSpec {

    private final List<XPathType> xPathList;


    @SuppressWarnings("rawtypes")
    public XPathFilter2ParameterSpec(List xPathList) {
        if (xPathList == null) {
            throw new NullPointerException("xPathList cannot be null");
        }
        List<?> xPathListCopy = new ArrayList<>((List<?>)xPathList);
        if (xPathListCopy.isEmpty()) {
            throw new IllegalArgumentException("xPathList cannot be empty");
        }
        int size = xPathListCopy.size();
        for (int i = 0; i < size; i++) {
            if (!(xPathListCopy.get(i) instanceof XPathType)) {
                throw new ClassCastException
                    ("xPathList["+i+"] is not a valid type");
            }
        }

        @SuppressWarnings("unchecked")
        List<XPathType> temp = (List<XPathType>)xPathListCopy;

        this.xPathList = Collections.unmodifiableList(temp);
    }


    @SuppressWarnings("rawtypes")
    public List getXPathList() {
        return xPathList;
    }
}
