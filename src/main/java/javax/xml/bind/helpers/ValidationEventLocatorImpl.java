

package javax.xml.bind.helpers;

import java.net.URL;
import java.net.MalformedURLException;
import java.text.MessageFormat;

import javax.xml.bind.ValidationEventLocator;
import org.w3c.dom.Node;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;


public class ValidationEventLocatorImpl implements ValidationEventLocator
{

    public ValidationEventLocatorImpl() {
    }


    public ValidationEventLocatorImpl( Locator loc ) {
        if( loc == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "loc" ) );
        }

        this.url = toURL(loc.getSystemId());
        this.columnNumber = loc.getColumnNumber();
        this.lineNumber = loc.getLineNumber();
    }


    public ValidationEventLocatorImpl( SAXParseException e ) {
        if( e == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "e" ) );
        }

        this.url = toURL(e.getSystemId());
        this.columnNumber = e.getColumnNumber();
        this.lineNumber = e.getLineNumber();
    }


    public ValidationEventLocatorImpl(Node _node) {
        if( _node == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "_node" ) );
        }

        this.node = _node;
    }


    public ValidationEventLocatorImpl(Object _object) {
        if( _object == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "_object" ) );
        }

        this.object = _object;
    }


    private static URL toURL( String systemId ) {
        try {
            return new URL(systemId);
        } catch( MalformedURLException e ) {
            return null;    }
    }

    private URL url = null;
    private int offset = -1;
    private int lineNumber = -1;
    private int columnNumber = -1;
    private Object object = null;
    private Node node = null;



    public URL getURL() {
        return url;
    }


    public void setURL( URL _url ) {
        this.url = _url;
    }


    public int getOffset() {
        return offset;
    }


    public void setOffset( int _offset ) {
        this.offset = _offset;
    }


    public int getLineNumber() {
        return lineNumber;
    }


    public void setLineNumber( int _lineNumber ) {
        this.lineNumber = _lineNumber;
    }


    public int getColumnNumber() {
        return columnNumber;
    }


    public void setColumnNumber( int _columnNumber ) {
        this.columnNumber = _columnNumber;
    }


    public Object getObject() {
        return object;
    }


    public void setObject( Object _object ) {
        this.object = _object;
    }


    public Node getNode() {
        return node;
    }


    public void setNode( Node _node ) {
        this.node = _node;
    }


    public String toString() {
        return MessageFormat.format("[node={0},object={1},url={2},line={3},col={4},offset={5}]",
            getNode(),
            getObject(),
            getURL(),
            String.valueOf(getLineNumber()),
            String.valueOf(getColumnNumber()),
            String.valueOf(getOffset()));
    }
}
