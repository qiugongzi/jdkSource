
public interface CorbaContactInfoList
    extends
        ContactInfoList
{
    public void setTargetIOR(IOR ior);
    public IOR getTargetIOR();

    public void setEffectiveTargetIOR(IOR locatedIor);
    public IOR getEffectiveTargetIOR();

    public LocalClientRequestDispatcher getLocalClientRequestDispatcher();

    public int hashCode();
}


