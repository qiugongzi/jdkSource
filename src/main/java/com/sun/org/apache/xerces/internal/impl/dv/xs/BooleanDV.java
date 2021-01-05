
public class BooleanDV extends TypeValidator{

    private static final String fValueSpace[] = {"false", "true", "0", "1"};

    public short getAllowedFacets(){
        return (XSSimpleTypeDecl.FACET_PATTERN | XSSimpleTypeDecl.FACET_WHITESPACE);
    }

    public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
        Boolean ret = null;

        if (content.equals(fValueSpace[0]) || content.equals(fValueSpace[2]))
            ret = Boolean.FALSE;
        else if (content.equals(fValueSpace[1]) || content.equals(fValueSpace[3]))
            ret = Boolean.TRUE;
        else
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "boolean"});
        return ret;
    }

}
