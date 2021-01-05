

package javax.xml.bind.annotation.adapters;


public abstract class XmlAdapter<ValueType,BoundType> {


    protected XmlAdapter() {}


    public abstract BoundType unmarshal(ValueType v) throws Exception;


    public abstract ValueType marshal(BoundType v) throws Exception;
}
