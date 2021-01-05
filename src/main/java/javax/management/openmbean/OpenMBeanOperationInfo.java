


package javax.management.openmbean;


import javax.management.MBeanParameterInfo;


public interface OpenMBeanOperationInfo  {

    public String getDescription() ;


    public String getName() ;


    public MBeanParameterInfo[] getSignature() ;


    public int getImpact() ;


    public String getReturnType() ;


    public OpenType<?> getReturnOpenType() ; public boolean equals(Object obj);


    public int hashCode();


    public String toString();

}
