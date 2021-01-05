

package javax.xml.bind;


public interface Validator {


    public void setEventHandler( ValidationEventHandler handler )
        throws JAXBException;


    public ValidationEventHandler getEventHandler()
        throws JAXBException;


    public boolean validate( Object subrootObj ) throws JAXBException;


    public boolean validateRoot( Object rootObj ) throws JAXBException;


    public void setProperty( String name, Object value )
        throws PropertyException;


    public Object getProperty( String name ) throws PropertyException;

}
