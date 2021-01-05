

package javax.management.openmbean;

import java.io.ObjectStreamException;
import java.lang.reflect.Array;



public class ArrayType<T> extends OpenType<T> {


    static final long serialVersionUID = 720504429830309770L;


    private int dimension;


    private OpenType<?> elementType;


    private boolean primitiveArray;

    private transient Integer  myHashCode = null;       private transient String   myToString = null;       private static final int PRIMITIVE_WRAPPER_NAME_INDEX = 0;
    private static final int PRIMITIVE_TYPE_NAME_INDEX = 1;
    private static final int PRIMITIVE_TYPE_KEY_INDEX  = 2;
    private static final int PRIMITIVE_OPEN_TYPE_INDEX  = 3;

    private static final Object[][] PRIMITIVE_ARRAY_TYPES = {
        { Boolean.class.getName(),   boolean.class.getName(), "Z", SimpleType.BOOLEAN },
        { Character.class.getName(), char.class.getName(),    "C", SimpleType.CHARACTER },
        { Byte.class.getName(),      byte.class.getName(),    "B", SimpleType.BYTE },
        { Short.class.getName(),     short.class.getName(),   "S", SimpleType.SHORT },
        { Integer.class.getName(),   int.class.getName(),     "I", SimpleType.INTEGER },
        { Long.class.getName(),      long.class.getName(),    "J", SimpleType.LONG },
        { Float.class.getName(),     float.class.getName(),   "F", SimpleType.FLOAT },
        { Double.class.getName(),    double.class.getName(),  "D", SimpleType.DOUBLE }
    };

    static boolean isPrimitiveContentType(final String primitiveKey) {
        for (Object[] typeDescr : PRIMITIVE_ARRAY_TYPES) {
            if (typeDescr[PRIMITIVE_TYPE_KEY_INDEX].equals(primitiveKey)) {
                return true;
            }
        }
        return false;
    }


    static String getPrimitiveTypeKey(String elementClassName) {
        for (Object[] typeDescr : PRIMITIVE_ARRAY_TYPES) {
            if (elementClassName.equals(typeDescr[PRIMITIVE_WRAPPER_NAME_INDEX]))
                return (String)typeDescr[PRIMITIVE_TYPE_KEY_INDEX];
        }
        return null;
    }


    static String getPrimitiveTypeName(String elementClassName) {
        for (Object[] typeDescr : PRIMITIVE_ARRAY_TYPES) {
            if (elementClassName.equals(typeDescr[PRIMITIVE_WRAPPER_NAME_INDEX]))
                return (String)typeDescr[PRIMITIVE_TYPE_NAME_INDEX];
        }
        return null;
    }


    static SimpleType<?> getPrimitiveOpenType(String primitiveTypeName) {
        for (Object[] typeDescr : PRIMITIVE_ARRAY_TYPES) {
            if (primitiveTypeName.equals(typeDescr[PRIMITIVE_TYPE_NAME_INDEX]))
                return (SimpleType<?>)typeDescr[PRIMITIVE_OPEN_TYPE_INDEX];
        }
        return null;
    }




    public ArrayType(int dimension,
                     OpenType<?> elementType) throws OpenDataException {
        super(buildArrayClassName(dimension, elementType),
              buildArrayClassName(dimension, elementType),
              buildArrayDescription(dimension, elementType));

        if (elementType.isArray()) {
            ArrayType<?> at = (ArrayType<?>) elementType;
            this.dimension = at.getDimension() + dimension;
            this.elementType = at.getElementOpenType();
            this.primitiveArray = at.isPrimitiveArray();
        } else {
            this.dimension = dimension;
            this.elementType = elementType;
            this.primitiveArray = false;
        }
    }


    public ArrayType(SimpleType<?> elementType,
                     boolean primitiveArray) throws OpenDataException {

        super(buildArrayClassName(1, elementType, primitiveArray),
              buildArrayClassName(1, elementType, primitiveArray),
              buildArrayDescription(1, elementType, primitiveArray),
              true);

        this.dimension = 1;
        this.elementType = elementType;
        this.primitiveArray = primitiveArray;
    }


    ArrayType(String className, String typeName, String description,
              int dimension, OpenType<?> elementType,
              boolean primitiveArray) {
        super(className, typeName, description, true);
        this.dimension = dimension;
        this.elementType = elementType;
        this.primitiveArray = primitiveArray;
    }

    private static String buildArrayClassName(int dimension,
                                              OpenType<?> elementType)
        throws OpenDataException {
        boolean isPrimitiveArray = false;
        if (elementType.isArray()) {
            isPrimitiveArray = ((ArrayType<?>) elementType).isPrimitiveArray();
        }
        return buildArrayClassName(dimension, elementType, isPrimitiveArray);
    }

    private static String buildArrayClassName(int dimension,
                                              OpenType<?> elementType,
                                              boolean isPrimitiveArray)
        throws OpenDataException {
        if (dimension < 1) {
            throw new IllegalArgumentException(
                "Value of argument dimension must be greater than 0");
        }
        StringBuilder result = new StringBuilder();
        String elementClassName = elementType.getClassName();
        for (int i = 1; i <= dimension; i++) {
            result.append('[');
        }
        if (elementType.isArray()) {
            result.append(elementClassName);
        } else {
            if (isPrimitiveArray) {
                final String key = getPrimitiveTypeKey(elementClassName);
                if (key == null)
                    throw new OpenDataException("Element type is not primitive: "
                            + elementClassName);
                result.append(key);
            } else {
                result.append("L");
                result.append(elementClassName);
                result.append(';');
            }
        }
        return result.toString();
    }

    private static String buildArrayDescription(int dimension,
                                                OpenType<?> elementType)
        throws OpenDataException {
        boolean isPrimitiveArray = false;
        if (elementType.isArray()) {
            isPrimitiveArray = ((ArrayType<?>) elementType).isPrimitiveArray();
        }
        return buildArrayDescription(dimension, elementType, isPrimitiveArray);
    }

    private static String buildArrayDescription(int dimension,
                                                OpenType<?> elementType,
                                                boolean isPrimitiveArray)
        throws OpenDataException {
        if (elementType.isArray()) {
            ArrayType<?> at = (ArrayType<?>) elementType;
            dimension += at.getDimension();
            elementType = at.getElementOpenType();
            isPrimitiveArray = at.isPrimitiveArray();
        }
        StringBuilder result =
            new StringBuilder(dimension + "-dimension array of ");
        final String elementClassName = elementType.getClassName();
        if (isPrimitiveArray) {
            final String primitiveType =
                    getPrimitiveTypeName(elementClassName);

            if (primitiveType == null)
                throw new OpenDataException("Element is not a primitive type: "+
                        elementClassName);
            result.append(primitiveType);
        } else {
            result.append(elementClassName);
        }
        return result.toString();
    }




    public int getDimension() {

        return dimension;
    }


    public OpenType<?> getElementOpenType() {

        return elementType;
    }


    public boolean isPrimitiveArray() {

        return primitiveArray;
    }


    public boolean isValue(Object obj) {

        if (obj == null) {
            return false;
        }

        Class<?> objClass = obj.getClass();
        String objClassName = objClass.getName();

        if ( ! objClass.isArray() ) {
            return false;
        }

        if ( this.getClassName().equals(objClassName) ) {
            return true;
        }

        if ( (this.elementType.getClassName().equals(TabularData.class.getName()))  ||
             (this.elementType.getClassName().equals(CompositeData.class.getName()))   ) {

            boolean isTabular =
                (elementType.getClassName().equals(TabularData.class.getName()));
            int[] dims = new int[getDimension()];
            Class<?> elementClass = isTabular ? TabularData.class : CompositeData.class;
            Class<?> targetClass = Array.newInstance(elementClass, dims).getClass();

            if  ( ! targetClass.isAssignableFrom(objClass) ) {
                return false;
            }

            if ( ! checkElementsType( (Object[]) obj, this.dimension) ) { return false;
            }

            return true;
        }

        return false;
    }


    private boolean checkElementsType(Object[] x_dim_Array, int dim) {

        if ( dim > 1 ) {
            for (int i=0; i<x_dim_Array.length; i++) {
                if ( ! checkElementsType((Object[])x_dim_Array[i], dim-1) ) {
                    return false;
                }
            }
            return true;
        }
        else {
            for (int i=0; i<x_dim_Array.length; i++) {
                if ( (x_dim_Array[i] != null) && (! this.getElementOpenType().isValue(x_dim_Array[i])) ) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    boolean isAssignableFrom(OpenType<?> ot) {
        if (!(ot instanceof ArrayType<?>))
            return false;
        ArrayType<?> at = (ArrayType<?>) ot;
        return (at.getDimension() == getDimension() &&
                at.isPrimitiveArray() == isPrimitiveArray() &&
                at.getElementOpenType().isAssignableFrom(getElementOpenType()));
    }





    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof ArrayType<?>))
            return false;
        ArrayType<?> other = (ArrayType<?>) obj;

        if (this.dimension != other.dimension) {
            return false;
        }

        if (!this.elementType.equals(other.elementType)) {
            return false;
        }

        return this.primitiveArray == other.primitiveArray;
    }


    public int hashCode() {

        if (myHashCode == null) {
            int value = 0;
            value += dimension;
            value += elementType.hashCode();
            value += Boolean.valueOf(primitiveArray).hashCode();
            myHashCode = Integer.valueOf(value);
        }

        return myHashCode.intValue();
    }


    public String toString() {

        if (myToString == null) {
            myToString = getClass().getName() +
                         "(name=" + getTypeName() +
                         ",dimension=" + dimension +
                         ",elementType=" + elementType +
                         ",primitiveArray=" + primitiveArray + ")";
        }

        return myToString;
    }


    public static <E> ArrayType<E[]> getArrayType(OpenType<E> elementType)
        throws OpenDataException {
        return new ArrayType<E[]>(1, elementType);
    }


    @SuppressWarnings("unchecked")  public static <T> ArrayType<T> getPrimitiveArrayType(Class<T> arrayClass) {
        if (!arrayClass.isArray()) {
            throw new IllegalArgumentException("arrayClass must be an array");
        }

        int n = 1;
        Class<?> componentType = arrayClass.getComponentType();
        while (componentType.isArray()) {
            n++;
            componentType = componentType.getComponentType();
        }
        String componentTypeName = componentType.getName();

        if (!componentType.isPrimitive()) {
            throw new IllegalArgumentException(
                "component type of the array must be a primitive type");
        }

        final SimpleType<?> simpleType =
                getPrimitiveOpenType(componentTypeName);

        try {
            @SuppressWarnings("rawtypes")
            ArrayType at = new ArrayType(simpleType, true);
            if (n > 1)
                at = new ArrayType<T>(n - 1, at);
            return at;
        } catch (OpenDataException e) {
            throw new IllegalArgumentException(e); }
    }


    private Object readResolve() throws ObjectStreamException {
        if (primitiveArray) {
            return convertFromWrapperToPrimitiveTypes();
        } else {
            return this;
        }
    }

    private <T> ArrayType<T> convertFromWrapperToPrimitiveTypes() {
        String cn = getClassName();
        String tn = getTypeName();
        String d = getDescription();
        for (Object[] typeDescr : PRIMITIVE_ARRAY_TYPES) {
            if (cn.indexOf((String)typeDescr[PRIMITIVE_WRAPPER_NAME_INDEX]) != -1) {
                cn = cn.replaceFirst(
                    "L" + typeDescr[PRIMITIVE_WRAPPER_NAME_INDEX] + ";",
                    (String) typeDescr[PRIMITIVE_TYPE_KEY_INDEX]);
                tn = tn.replaceFirst(
                    "L" + typeDescr[PRIMITIVE_WRAPPER_NAME_INDEX] + ";",
                    (String) typeDescr[PRIMITIVE_TYPE_KEY_INDEX]);
                d = d.replaceFirst(
                    (String) typeDescr[PRIMITIVE_WRAPPER_NAME_INDEX],
                    (String) typeDescr[PRIMITIVE_TYPE_NAME_INDEX]);
                break;
            }
        }
        return new ArrayType<T>(cn, tn, d,
                                dimension, elementType, primitiveArray);
    }


    private Object writeReplace() throws ObjectStreamException {
        if (primitiveArray) {
            return convertFromPrimitiveToWrapperTypes();
        } else {
            return this;
        }
    }

    private <T> ArrayType<T> convertFromPrimitiveToWrapperTypes() {
        String cn = getClassName();
        String tn = getTypeName();
        String d = getDescription();
        for (Object[] typeDescr : PRIMITIVE_ARRAY_TYPES) {
            if (cn.indexOf((String) typeDescr[PRIMITIVE_TYPE_KEY_INDEX]) != -1) {
                cn = cn.replaceFirst(
                    (String) typeDescr[PRIMITIVE_TYPE_KEY_INDEX],
                    "L" + typeDescr[PRIMITIVE_WRAPPER_NAME_INDEX] + ";");
                tn = tn.replaceFirst(
                    (String) typeDescr[PRIMITIVE_TYPE_KEY_INDEX],
                    "L" + typeDescr[PRIMITIVE_WRAPPER_NAME_INDEX] + ";");
                d = d.replaceFirst(
                    (String) typeDescr[PRIMITIVE_TYPE_NAME_INDEX],
                    (String) typeDescr[PRIMITIVE_WRAPPER_NAME_INDEX]);
                break;
            }
        }
        return new ArrayType<T>(cn, tn, d,
                                dimension, elementType, primitiveArray);
    }
}
