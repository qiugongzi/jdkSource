
package com.sun.org.apache.bcel.internal.generic;


import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;


public final class ObjectType extends ReferenceType {
  private String class_name; public ObjectType(String class_name) {
    super(Constants.T_REFERENCE, "L" + class_name.replace('.', '/') + ";");
    this.class_name = class_name.replace('/', '.');
  }


  public String getClassName() { return class_name; }


  public int hashCode()  { return class_name.hashCode(); }


  public boolean equals(Object type) {
    return (type instanceof ObjectType)?
      ((ObjectType)type).class_name.equals(class_name) : false;
  }


  public boolean referencesClass(){
    JavaClass jc = Repository.lookupClass(class_name);
    if (jc == null)
      return false;
    else
      return jc.isClass();
  }


  public boolean referencesInterface(){
    JavaClass jc = Repository.lookupClass(class_name);
    if (jc == null)
      return false;
    else
      return !jc.isClass();
  }

  public boolean subclassOf(ObjectType superclass){
    if (this.referencesInterface() || superclass.referencesInterface())
      return false;

    return Repository.instanceOf(this.class_name, superclass.class_name);
  }


  public boolean accessibleTo(ObjectType accessor) {
    JavaClass jc = Repository.lookupClass(class_name);

    if(jc.isPublic()) {
      return true;
    } else {
      JavaClass acc = Repository.lookupClass(accessor.class_name);
      return acc.getPackageName().equals(jc.getPackageName());
    }
  }
}
