
package java.beans;

import com.sun.beans.decoder.DocumentHandler;

import java.io.Closeable;
import java.io.InputStream;
import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;


public class XMLDecoder implements AutoCloseable {
    private final AccessControlContext acc = AccessController.getContext();
    private final DocumentHandler handler = new DocumentHandler();
    private final InputSource input;
    private Object owner;
    private Object[] array;
    private int index;


    public XMLDecoder(InputStream in) {
        this(in, null);
    }


    public XMLDecoder(InputStream in, Object owner) {
        this(in, owner, null);
    }


    public XMLDecoder(InputStream in, Object owner, ExceptionListener exceptionListener) {
        this(in, owner, exceptionListener, null);
    }


    public XMLDecoder(InputStream in, Object owner,
                      ExceptionListener exceptionListener, ClassLoader cl) {
        this(new InputSource(in), owner, exceptionListener, cl);
    }



    public XMLDecoder(InputSource is) {
        this(is, null, null, null);
    }


    private XMLDecoder(InputSource is, Object owner, ExceptionListener el, ClassLoader cl) {
        this.input = is;
        this.owner = owner;
        setExceptionListener(el);
        this.handler.setClassLoader(cl);
        this.handler.setOwner(this);
    }


    public void close() {
        if (parsingComplete()) {
            close(this.input.getCharacterStream());
            close(this.input.getByteStream());
        }
    }

    private void close(Closeable in) {
        if (in != null) {
            try {
                in.close();
            }
            catch (IOException e) {
                getExceptionListener().exceptionThrown(e);
            }
        }
    }

    private boolean parsingComplete() {
        if (this.input == null) {
            return false;
        }
        if (this.array == null) {
            if ((this.acc == null) && (null != System.getSecurityManager())) {
                throw new SecurityException("AccessControlContext is not set");
            }
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                    XMLDecoder.this.handler.parse(XMLDecoder.this.input);
                    return null;
                }
            }, this.acc);
            this.array = this.handler.getObjects();
        }
        return true;
    }


    public void setExceptionListener(ExceptionListener exceptionListener) {
        if (exceptionListener == null) {
            exceptionListener = Statement.defaultExceptionListener;
        }
        this.handler.setExceptionListener(exceptionListener);
    }


    public ExceptionListener getExceptionListener() {
        return this.handler.getExceptionListener();
    }


    public Object readObject() {
        return (parsingComplete())
                ? this.array[this.index++]
                : null;
    }


    public void setOwner(Object owner) {
        this.owner = owner;
    }


    public Object getOwner() {
        return owner;
    }


    public static DefaultHandler createHandler(Object owner, ExceptionListener el, ClassLoader cl) {
        DocumentHandler handler = new DocumentHandler();
        handler.setOwner(owner);
        handler.setExceptionListener(el);
        handler.setClassLoader(cl);
        return handler;
    }
}
