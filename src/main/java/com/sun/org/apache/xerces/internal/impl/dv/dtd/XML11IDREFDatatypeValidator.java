


package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.*;
import com.sun.org.apache.xerces.internal.util.XML11Char;


public class XML11IDREFDatatypeValidator extends IDREFDatatypeValidator {

    public XML11IDREFDatatypeValidator() {
        super();
    }


    public void validate(String content, ValidationContext context) throws InvalidDatatypeValueException {

        if(context.useNamespaces()) {
            if (!XML11Char.isXML11ValidNCName(content)) {
                throw new InvalidDatatypeValueException("IDREFInvalidWithNamespaces", new Object[]{content});
            }
        }
        else {
            if (!XML11Char.isXML11ValidName(content)) {
                throw new InvalidDatatypeValueException("IDREFInvalid", new Object[]{content});
            }
        }

        context.addIdRef(content);

    }

}
