


package javax.management.openmbean;


import java.util.Set;
import java.lang.Comparable; public interface OpenMBeanParameterInfo {


    public String getDescription() ;


    public String getName() ;


    public OpenType<?> getOpenType() ;


    public Object getDefaultValue() ;


    public Set<?> getLegalValues() ;


    public Comparable<?> getMinValue() ;


    public Comparable<?> getMaxValue() ;


    public boolean hasDefaultValue() ;


    public boolean hasLegalValues() ;


    public boolean hasMinValue() ;


    public boolean hasMaxValue() ;


    public boolean isValue(Object obj) ;



    public boolean equals(Object obj);


    public int hashCode();


    public String toString();

}
