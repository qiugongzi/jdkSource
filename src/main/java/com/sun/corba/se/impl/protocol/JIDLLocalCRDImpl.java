

package com.sun.corba.se.impl.protocol;

import javax.rmi.CORBA.Tie;

import org.omg.CORBA.portable.ServantObject;

import com.sun.corba.se.spi.orb.ORB ;

import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory ;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher ;

import com.sun.corba.se.spi.ior.IOR ;

import com.sun.corba.se.impl.protocol.LocalClientRequestDispatcherBase ;

import com.sun.corba.se.pept.broker.Broker;

public class JIDLLocalCRDImpl extends LocalClientRequestDispatcherBase
{
    public JIDLLocalCRDImpl( ORB orb, int scid, IOR ior )
    {
        super( (com.sun.corba.se.spi.orb.ORB)orb, scid, ior ) ;
    }

    protected ServantObject servant;

    public ServantObject servant_preinvoke(org.omg.CORBA.Object self,
                                           String operation,
                                           Class expectedType)
    {
        if (!checkForCompatibleServant( servant, expectedType ))
            return null ;

        return servant;
    }

    public void servant_postinvoke( org.omg.CORBA.Object self,
        ServantObject servant )
    {

    }


    public void setServant( java.lang.Object servant )
    {
        if (servant != null && servant instanceof Tie) {
            this.servant = new ServantObject();
            this.servant.servant = ((Tie)servant).getTarget();
        } else {
            this.servant = null;
        }
    }

    public void unexport() {






        servant = null;
    }
}

