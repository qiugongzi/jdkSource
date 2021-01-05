
public class UnionDV extends TypeValidator{

    public short getAllowedFacets(){
          return (XSSimpleTypeDecl.FACET_PATTERN | XSSimpleTypeDecl.FACET_ENUMERATION );
    }



    public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException{
        return content;
    }

}
