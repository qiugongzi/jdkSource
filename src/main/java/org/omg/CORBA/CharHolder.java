


package org.omg.CORBA;

import org.omg.CORBA.portable.Streamable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;


public final class CharHolder implements Streamable {


    public char value;


    public CharHolder() {
    }


    public CharHolder(char initial) {
        value = initial;
    }


    public void _read(InputStream input) {
        value = input.read_char();
    }


    public void _write(OutputStream output) {
        output.write_char(value);
    }


    public org.omg.CORBA.TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_char);
    }
}
