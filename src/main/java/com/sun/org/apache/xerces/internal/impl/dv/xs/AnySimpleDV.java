
public class AnySimpleDV extends TypeValidator {

    public short getAllowedFacets() {

        return 0;
    }

    public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
        return content;
    }

}
