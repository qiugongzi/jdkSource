

package javax.naming.ldap;


public class BasicControl implements Control {


    protected String id;


    protected boolean criticality = false; protected byte[] value = null;

    private static final long serialVersionUID = -4233907508771791687L;


    public BasicControl(String id) {
        this.id = id;
    }


    public BasicControl(String id, boolean criticality, byte[] value) {
        this.id = id;
        this.criticality = criticality;
        this.value = value;
    }


    public String getID() {
        return id;
    }


    public boolean isCritical() {
        return criticality;
    }


    public byte[] getEncodedValue() {
        return value;
    }
}
