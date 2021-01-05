
public interface IORTransformer {
    IOR unmarshal( CorbaInputObject io ) ;

    void marshal( CorbaOutputObject oo, IOR ior ) ;
}
