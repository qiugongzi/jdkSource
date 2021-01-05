

package com.sun.corba.se.impl.naming.pcosnaming;

import java.io.Serializable;
import org.omg.CosNaming.NameComponent;



public class InternalBindingKey
        implements Serializable
{

    private static final long serialVersionUID = -5410796631793704055L;

    public String id;
    public String kind;

    public InternalBindingKey() {}

    public InternalBindingKey(NameComponent n)
    {
        setup(n);
    }

    protected void setup(NameComponent n) {
        this.id = n.id;
        this.kind = n.kind;
    }

    public boolean equals(java.lang.Object o) {
        if (o == null)
            return false;
        if (o instanceof InternalBindingKey) {
            InternalBindingKey that = (InternalBindingKey)o;
            if( this.id != null && that.id != null )
            {
                if (this.id.length() != that.id.length() )
                {
                        return false;
                }
                if (this.id.length() > 0 && this.id.equals(that.id) == false)
                {
                        return false;
                }
            }
            else
            {
                if( ( this.id == null && that.id != null )
                ||  ( this.id !=null && that.id == null ) )
                {
                        return false;
                }
            }
            if( this.kind != null && that.kind != null )
            {
                if (this.kind.length() != that.kind.length() )
                {
                        return false;
                }
                if (this.kind.length() > 0 && this.kind.equals(that.kind) == false)
                {
                        return false;
                }
            }
            else
            {
                if( ( this.kind == null && that.kind != null )
                ||  ( this.kind !=null && that.kind == null ) )
                {
                        return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }


    public int hashCode() {
        int hashVal = 0;
        if (this.id.length() > 0)
        {
            hashVal += this.id.hashCode();
        }
        if (this.kind.length() > 0)
        {
            hashVal += this.kind.hashCode();
        }
        return hashVal;
    }
}
