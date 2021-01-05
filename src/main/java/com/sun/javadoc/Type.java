

package com.sun.javadoc;


public interface Type {


    String typeName();


    String qualifiedTypeName();


    String simpleTypeName();


    String dimension();


    String toString();


    boolean isPrimitive();


    ClassDoc asClassDoc();


    ParameterizedType asParameterizedType();


    TypeVariable asTypeVariable();


    WildcardType asWildcardType();


    AnnotatedType asAnnotatedType();


    AnnotationTypeDoc asAnnotationTypeDoc();


    Type getElementType();
}
