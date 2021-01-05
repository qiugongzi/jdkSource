

package com.sun.javadoc;


public interface SeeTag extends Tag {


    String label();


    public PackageDoc referencedPackage();


    String referencedClassName();


    ClassDoc referencedClass();


    String referencedMemberName();


    MemberDoc referencedMember();
}
