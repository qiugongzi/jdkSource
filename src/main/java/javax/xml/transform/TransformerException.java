

package javax.xml.transform;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Objects;


public class TransformerException extends Exception {

    private static final long serialVersionUID = 975798773772956428L;


    SourceLocator locator;


    public SourceLocator getLocator() {
        return this.locator;
    }


    public void setLocator(SourceLocator location) {
        this.locator = location;
    }


    Throwable containedException;


    public Throwable getException() {
        return containedException;
    }


    @Override
    public Throwable getCause() {

        return ((containedException == this)
                ? null
                : containedException);
    }


    @Override
    public synchronized Throwable initCause(Throwable cause) {

        if (this.containedException != null) {
            throw new IllegalStateException("Can't overwrite cause");
        }

        if (cause == this) {
            throw new IllegalArgumentException(
                "Self-causation not permitted");
        }

        this.containedException = cause;

        return this;
    }


    public TransformerException(String message) {
        this(message, null, null);
    }


    public TransformerException(Throwable e) {
        this(null, null, e);
    }


    public TransformerException(String message, Throwable e) {
        this(message, null, e);
    }


    public TransformerException(String message, SourceLocator locator) {
        this(message, locator, null);
    }


    public TransformerException(String message, SourceLocator locator,
                                Throwable e) {
        super(((message == null) || (message.length() == 0))
              ? ((e == null) ? "" : e.toString())
              : message);

        this.containedException = e;
        this.locator            = locator;
    }


    public String getMessageAndLocation() {
        StringBuilder sbuffer = new StringBuilder();
        sbuffer.append(Objects.toString(super.getMessage(), ""));
        sbuffer.append(Objects.toString(getLocationAsString(), ""));

        return sbuffer.toString();
    }


    public String getLocationAsString() {
        if (locator == null) {
            return null;
        }

        if (System.getSecurityManager() == null) {
            return getLocationString();
        } else {
            return (String) AccessController.doPrivileged(
                new PrivilegedAction<String>() {
                    public String run() {
                        return getLocationString();
                    }
                },
                new AccessControlContext(new ProtectionDomain[] {getNonPrivDomain()})
            );
        }
    }


    private String getLocationString() {
        if (locator == null) {
            return null;
        }

        StringBuilder sbuffer  = new StringBuilder();
            String       systemID = locator.getSystemId();
            int          line     = locator.getLineNumber();
            int          column   = locator.getColumnNumber();

            if (null != systemID) {
                sbuffer.append("; SystemID: ");
                sbuffer.append(systemID);
            }

            if (0 != line) {
                sbuffer.append("; Line#: ");
                sbuffer.append(line);
            }

            if (0 != column) {
                sbuffer.append("; Column#: ");
                sbuffer.append(column);
            }

            return sbuffer.toString();
    }


    @Override
    public void printStackTrace() {
        printStackTrace(new java.io.PrintWriter(System.err, true));
    }


    @Override
    public void printStackTrace(java.io.PrintStream s) {
        printStackTrace(new java.io.PrintWriter(s));
    }


    @Override
    public void printStackTrace(java.io.PrintWriter s) {

        if (s == null) {
            s = new java.io.PrintWriter(System.err, true);
        }

        try {
            String locInfo = getLocationAsString();

            if (null != locInfo) {
                s.println(locInfo);
            }

            super.printStackTrace(s);
        } catch (Throwable e) {}

        Throwable exception = getException();

        for (int i = 0; (i < 10) && (null != exception); i++) {
            s.println("---------");

            try {
                if (exception instanceof TransformerException) {
                    String locInfo =
                        ((TransformerException) exception)
                            .getLocationAsString();

                    if (null != locInfo) {
                        s.println(locInfo);
                    }
                }

                exception.printStackTrace(s);
            } catch (Throwable e) {
                s.println("Could not print stack trace...");
            }

            try {
                Method meth =
                    ((Object) exception).getClass().getMethod("getException",
                        (Class[]) null);

                if (null != meth) {
                    Throwable prev = exception;

                    exception = (Throwable) meth.invoke(exception, (Object[]) null);

                    if (prev == exception) {
                        break;
                    }
                } else {
                    exception = null;
                }
                } catch (InvocationTargetException | IllegalAccessException
                        | NoSuchMethodException e) {
                exception = null;
            }
        }
        s.flush();
    }


    private ProtectionDomain getNonPrivDomain() {
        CodeSource nullSource = new CodeSource(null, (CodeSigner[]) null);
        PermissionCollection noPermission = new Permissions();
        return new ProtectionDomain(nullSource, noPermission);
}
}
