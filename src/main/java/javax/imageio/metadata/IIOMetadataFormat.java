

package javax.imageio.metadata;

import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;


public interface IIOMetadataFormat {

    int CHILD_POLICY_EMPTY = 0;


    int CHILD_POLICY_ALL = 1;


    int CHILD_POLICY_SOME = 2;


    int CHILD_POLICY_CHOICE = 3;


    int CHILD_POLICY_SEQUENCE = 4;


    int CHILD_POLICY_REPEAT = 5;


    int CHILD_POLICY_MAX = CHILD_POLICY_REPEAT;


    int VALUE_NONE = 0;


    int VALUE_ARBITRARY = 1;


    int VALUE_RANGE = 2;


    int VALUE_RANGE_MIN_INCLUSIVE_MASK = 4;


    int VALUE_RANGE_MAX_INCLUSIVE_MASK = 8;


    int VALUE_RANGE_MIN_INCLUSIVE = VALUE_RANGE |
        VALUE_RANGE_MIN_INCLUSIVE_MASK;


    int VALUE_RANGE_MAX_INCLUSIVE = VALUE_RANGE |
        VALUE_RANGE_MAX_INCLUSIVE_MASK;


    int VALUE_RANGE_MIN_MAX_INCLUSIVE =
        VALUE_RANGE |
        VALUE_RANGE_MIN_INCLUSIVE_MASK |
        VALUE_RANGE_MAX_INCLUSIVE_MASK;


    int VALUE_ENUMERATION = 16;


    int VALUE_LIST = 32;


    int DATATYPE_STRING = 0;


    int DATATYPE_BOOLEAN = 1;


    int DATATYPE_INTEGER = 2;


    int DATATYPE_FLOAT = 3;


    int DATATYPE_DOUBLE = 4;

    String getRootName();

    boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType);


    int getElementMinChildren(String elementName);


    int getElementMaxChildren(String elementName);


    String getElementDescription(String elementName, Locale locale);

    int getChildPolicy(String elementName);


    String[] getChildNames(String elementName);

    String[] getAttributeNames(String elementName);


    int getAttributeValueType(String elementName, String attrName);


    int getAttributeDataType(String elementName, String attrName);


    boolean isAttributeRequired(String elementName, String attrName);


    String getAttributeDefaultValue(String elementName, String attrName);


    String[] getAttributeEnumerations(String elementName, String attrName);


    String getAttributeMinValue(String elementName, String attrName);


    String getAttributeMaxValue(String elementName, String attrName);


    int getAttributeListMinLength(String elementName, String attrName);


    int getAttributeListMaxLength(String elementName, String attrName);


    String getAttributeDescription(String elementName, String attrName,
                                   Locale locale);

    int getObjectValueType(String elementName);


    Class<?> getObjectClass(String elementName);


    Object getObjectDefaultValue(String elementName);


    Object[] getObjectEnumerations(String elementName);


    Comparable<?> getObjectMinValue(String elementName);


    Comparable<?> getObjectMaxValue(String elementName);


    int getObjectArrayMinLength(String elementName);


    int getObjectArrayMaxLength(String elementName);
}
