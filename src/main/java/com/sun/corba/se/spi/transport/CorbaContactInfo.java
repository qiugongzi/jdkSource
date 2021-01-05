
public interface CorbaContactInfo
    extends
        ContactInfo
{
    public IOR getTargetIOR();
    public IOR getEffectiveTargetIOR();
    public IIOPProfile getEffectiveProfile();
    public void setAddressingDisposition(short addressingDisposition);
    public short getAddressingDisposition();
    public String getMonitoringName();
}


