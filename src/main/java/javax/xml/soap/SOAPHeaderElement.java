

package javax.xml.soap;


public interface SOAPHeaderElement extends SOAPElement {


    public void setActor(String actorURI);


    public void setRole(String uri) throws SOAPException;


    public String getActor();


    public String getRole();


    public void setMustUnderstand(boolean mustUnderstand);


    public boolean getMustUnderstand();


    public void setRelay(boolean relay) throws SOAPException;


    public boolean getRelay();
}
