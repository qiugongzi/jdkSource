

package com.sun.corba.se.impl.encoding;

import org.omg.CORBA.TypeCode ;
import org.omg.CORBA.StructMember ;
import org.omg.CORBA.UnionMember ;
import org.omg.CORBA.ValueMember ;
import org.omg.CORBA.TCKind ;
import org.omg.CORBA.Any ;
import org.omg.CORBA.Principal ;
import org.omg.CORBA.CompletionStatus ;

import org.omg.CORBA.TypeCodePackage.BadKind ;

import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.impl.encoding.CodeSetConversion;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.CDROutputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import sun.corba.EncapsInputStreamFactory;

public final class TypeCodeOutputStream extends EncapsOutputStream
{
    private OutputStream enclosure = null;
    private Map typeMap = null;
    private boolean isEncapsulation = false;

    public TypeCodeOutputStream(ORB orb) {
        super(orb, false);
    }

    public TypeCodeOutputStream(ORB orb, boolean littleEndian) {
        super(orb, littleEndian);
    }

    public org.omg.CORBA.portable.InputStream create_input_stream()
    {
        TypeCodeInputStream tcis = EncapsInputStreamFactory
                .newTypeCodeInputStream((ORB) orb(), getByteBuffer(),
                        getIndex(), isLittleEndian(), getGIOPVersion());
        return tcis;
    }

    public void setEnclosingOutputStream(OutputStream enclosure) {
        this.enclosure = enclosure;
    }



    public TypeCodeOutputStream getTopLevelStream() {
        if (enclosure == null)
            return this;
        if (enclosure instanceof TypeCodeOutputStream)
            return ((TypeCodeOutputStream)enclosure).getTopLevelStream();
        return this;
    }

    public int getTopLevelPosition() {
        if (enclosure != null && enclosure instanceof TypeCodeOutputStream) {
            int pos = ((TypeCodeOutputStream)enclosure).getTopLevelPosition() + getPosition();
            if (isEncapsulation) pos += 4;
            return pos;
        }
        return getPosition();
    }

    public void addIDAtPosition(String id, int position) {
        if (typeMap == null)
            typeMap = new HashMap(16);
        typeMap.put(id, new Integer(position));
    }

    public int getPositionForID(String id) {
        if (typeMap == null)
            throw wrapper.refTypeIndirType( CompletionStatus.COMPLETED_NO ) ;
        return ((Integer)typeMap.get(id)).intValue();
    }

    public void writeRawBuffer(org.omg.CORBA.portable.OutputStream s, int firstLong) {
        s.write_long(firstLong);
        ByteBuffer byteBuffer = getByteBuffer();
        if (byteBuffer.hasArray())
        {
             s.write_octet_array(byteBuffer.array(), 4, getIndex() - 4);
        }
        else
        {
             byte[] buf = new byte[byteBuffer.limit()];
             for (int i = 0; i < buf.length; i++)
                  buf[i] = byteBuffer.get(i);
             s.write_octet_array(buf, 4, getIndex() - 4);
        }
        }

    public TypeCodeOutputStream createEncapsulation(org.omg.CORBA.ORB _orb) {
        TypeCodeOutputStream encap =
            sun.corba.OutputStreamFactory.newTypeCodeOutputStream((ORB)_orb, isLittleEndian());
        encap.setEnclosingOutputStream(this);
        encap.makeEncapsulation();
        return encap;
    }

    protected void makeEncapsulation() {
        putEndian();
        isEncapsulation = true;
    }

    public static TypeCodeOutputStream wrapOutputStream(OutputStream os) {
        boolean littleEndian = ((os instanceof CDROutputStream) ? ((CDROutputStream)os).isLittleEndian() : false);
        TypeCodeOutputStream tos =
            sun.corba.OutputStreamFactory.newTypeCodeOutputStream((ORB)os.orb(), littleEndian);
        tos.setEnclosingOutputStream(os);
        return tos;
    }

    public int getPosition() {
        return getIndex();
    }

    public int getRealIndex(int index) {
        int topPos = getTopLevelPosition();
        return topPos;
    }

    public byte[] getTypeCodeBuffer() {
        ByteBuffer theBuffer = getByteBuffer();
        byte[] tcBuffer = new byte[getIndex() - 4];
        for (int i = 0; i < tcBuffer.length; i++)
            tcBuffer[i] = theBuffer.get(i+4);
        return tcBuffer;
    }

    public void printTypeMap() {
        System.out.println("typeMap = {");
        Iterator i = typeMap.keySet().iterator();
        while (i.hasNext()) {
            String id = (String)i.next();
            Integer pos = (Integer)typeMap.get(id);
            System.out.println("  key = " + id + ", value = " + pos);
        }
        System.out.println("}");
    }
}
