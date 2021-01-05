


package javax.management.openmbean;


import javax.management.MBeanParameterInfo;



public interface OpenMBeanConstructorInfo {

    public String getDescription() ;


    public String getName() ;


    public MBeanParameterInfo[] getSignature() ;


    public boolean equals(Object obj);


    public int hashCode();


    public String toString();

}
