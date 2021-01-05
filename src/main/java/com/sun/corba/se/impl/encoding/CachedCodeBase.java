
package com.sun.corba.se.impl.encoding;

import java.util.Hashtable;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.org.omg.SendingContext.CodeBaseHelper;
import com.sun.org.omg.SendingContext._CodeBaseImplBase;
import com.sun.org.omg.SendingContext._CodeBaseStub;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;

public class CachedCodeBase extends _CodeBaseImplBase
{
    private Hashtable implementations, fvds, bases;
    private volatile CodeBase delegate;
    private CorbaConnection conn;

    private static Object iorMapLock = new Object();
    private static Hashtable<IOR,CodeBase> iorMap = new Hashtable<>();

    public static synchronized void cleanCache( ORB orb ) {
        synchronized (iorMapLock) {
            for (IOR ior : iorMap.keySet()) {
                if (ior.getORB() == orb) {
                    iorMap.remove(ior);
                }
            }
        }
    }

    public CachedCodeBase(CorbaConnection connection) {
        conn = connection;
    }

    public com.sun.org.omg.CORBA.Repository get_ir () {
        return null;
    }

    public synchronized String implementation (String repId) {
        String urlResult = null;

        if (implementations == null)
            implementations = new Hashtable();
        else
            urlResult = (String)implementations.get(repId);

        if (urlResult == null && connectedCodeBase()) {
            urlResult = delegate.implementation(repId);

            if (urlResult != null)
                implementations.put(repId, urlResult);
        }

        return urlResult;
    }

    public synchronized String[] implementations (String[] repIds) {
        String[] urlResults = new String[repIds.length];

        for (int i = 0; i < urlResults.length; i++)
            urlResults[i] = implementation(repIds[i]);

        return urlResults;
    }

    public synchronized FullValueDescription meta (String repId) {
        FullValueDescription result = null;

        if (fvds == null)
            fvds = new Hashtable();
        else
            result = (FullValueDescription)fvds.get(repId);

        if (result == null && connectedCodeBase()) {
            result = delegate.meta(repId);

            if (result != null)
                fvds.put(repId, result);
        }

        return result;
    }

    public synchronized FullValueDescription[] metas (String[] repIds) {
        FullValueDescription[] results
            = new FullValueDescription[repIds.length];

        for (int i = 0; i < results.length; i++)
            results[i] = meta(repIds[i]);

        return results;
    }

    public synchronized String[] bases (String repId) {

        String[] results = null;

        if (bases == null)
            bases = new Hashtable();
        else
            results = (String[])bases.get(repId);

        if (results == null && connectedCodeBase()) {
            results = delegate.bases(repId);

            if (results != null)
                bases.put(repId, results);
        }

        return results;
    }

    private synchronized boolean connectedCodeBase() {
        if (delegate != null)
            return true;

        if (conn.getCodeBaseIOR() == null) {
            if (conn.getBroker().transportDebugFlag)
                conn.dprint("CodeBase unavailable on connection: " + conn);

            return false;
        }

        synchronized(iorMapLock) {

            if (delegate != null)
                return true;

            delegate = CachedCodeBase.iorMap.get(conn.getCodeBaseIOR());

            if (delegate != null)
                return true;

            delegate = CodeBaseHelper.narrow(getObjectFromIOR());

            CachedCodeBase.iorMap.put(conn.getCodeBaseIOR(), delegate);
        }

        return true;
    }

    private final org.omg.CORBA.Object getObjectFromIOR() {
        return CDRInputStream_1_0.internalIORToObject(
            conn.getCodeBaseIOR(), null , conn.getBroker());
    }
}

