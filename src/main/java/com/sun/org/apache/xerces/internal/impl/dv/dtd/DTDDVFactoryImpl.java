


package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class DTDDVFactoryImpl extends DTDDVFactory {

    static final Map<String, DatatypeValidator> fBuiltInTypes;
    static {
        Map<String, DatatypeValidator> builtInTypes = new HashMap<>();
        DatatypeValidator dvTemp;

        builtInTypes.put("string", new StringDatatypeValidator());
        builtInTypes.put("ID", new IDDatatypeValidator());
        dvTemp = new IDREFDatatypeValidator();
        builtInTypes.put("IDREF", dvTemp);
        builtInTypes.put("IDREFS", new ListDatatypeValidator(dvTemp));
        dvTemp = new ENTITYDatatypeValidator();
        builtInTypes.put("ENTITY", new ENTITYDatatypeValidator());
        builtInTypes.put("ENTITIES", new ListDatatypeValidator(dvTemp));
        builtInTypes.put("NOTATION", new NOTATIONDatatypeValidator());
        dvTemp = new NMTOKENDatatypeValidator();
        builtInTypes.put("NMTOKEN", dvTemp);
        builtInTypes.put("NMTOKENS", new ListDatatypeValidator(dvTemp));

        fBuiltInTypes = Collections.unmodifiableMap(builtInTypes);
    }


    @Override
    public DatatypeValidator getBuiltInDV(String name) {
        return fBuiltInTypes.get(name);
    }


    @Override
    public Map<String, DatatypeValidator> getBuiltInTypes() {
        return new HashMap<>(fBuiltInTypes);
    }

}