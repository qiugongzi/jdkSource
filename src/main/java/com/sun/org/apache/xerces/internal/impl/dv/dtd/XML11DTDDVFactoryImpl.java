


package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class XML11DTDDVFactoryImpl extends DTDDVFactoryImpl {

    static Map<String, DatatypeValidator> XML11BUILTINTYPES;
    static {
        Map<String, DatatypeValidator> xml11BuiltInTypes = new HashMap<>();
        xml11BuiltInTypes.put("XML11ID", new XML11IDDatatypeValidator());
        DatatypeValidator dvTemp = new XML11IDREFDatatypeValidator();
        xml11BuiltInTypes.put("XML11IDREF", dvTemp);
        xml11BuiltInTypes.put("XML11IDREFS", new ListDatatypeValidator(dvTemp));
        dvTemp = new XML11NMTOKENDatatypeValidator();
        xml11BuiltInTypes.put("XML11NMTOKEN", dvTemp);
        xml11BuiltInTypes.put("XML11NMTOKENS", new ListDatatypeValidator(dvTemp));
        XML11BUILTINTYPES = Collections.unmodifiableMap(xml11BuiltInTypes);
    } @Override
    public DatatypeValidator getBuiltInDV(String name) {
        if(XML11BUILTINTYPES.get(name) != null) {
            return XML11BUILTINTYPES.get(name);
        }
        return fBuiltInTypes.get(name);
    }


    @Override
    public Map<String, DatatypeValidator> getBuiltInTypes() {
        final HashMap<String, DatatypeValidator> toReturn = new HashMap<>(fBuiltInTypes);
        toReturn.putAll(XML11BUILTINTYPES);
        return toReturn;
    }
}