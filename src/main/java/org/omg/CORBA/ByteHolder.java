


package org.omg.CORBA;

import org.omg.CORBA.portable.Streamable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;


public final class ByteHolder implements Streamable {


    public byte value;


    public ByteHolder() {
    }


    public ByteHolder(byte initial) {
        value = initial;
    }


    public void _read(InputStream input) {
        value = input.read_octet();
    }


    public void _write(OutputStream output) {
        output.write_octet(value);
    }


    public org.omg.CORBA.TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_octet);
    }
}
