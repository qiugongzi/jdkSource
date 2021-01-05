
package com.sun.corba.se.impl.encoding;

import java.nio.ByteBuffer;

import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.impl.encoding.BufferManagerWrite;
import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.encoding.OutputObject;
import org.omg.CORBA.SystemException;


public class BufferManagerWriteStream extends BufferManagerWrite
{
    private int fragmentCount = 0;

    BufferManagerWriteStream( ORB orb )
    {
        super(orb) ;
    }

    public boolean sentFragment() {
        return fragmentCount > 0;
    }


    public int getBufferSize() {
        return orb.getORBData().getGIOPFragmentSize();
    }

    public void overflow (ByteBufferWithInfo bbwi)
    {
        MessageBase.setFlag(bbwi.byteBuffer, Message.MORE_FRAGMENTS_BIT);

        try {
           sendFragment(false);
        } catch(SystemException se){
                orb.getPIHandler().invokeClientPIEndingPoint(
                        ReplyMessage.SYSTEM_EXCEPTION, se);
                throw se;
        }

        bbwi.position(0);
        bbwi.buflen = bbwi.byteBuffer.limit();
        bbwi.fragmented = true;

        FragmentMessage header = ((CDROutputObject)outputObject).getMessageHeader().createFragmentMessage();

        header.write(((CDROutputObject)outputObject));
    }

    private void sendFragment(boolean isLastFragment)
    {
        Connection conn = ((OutputObject)outputObject).getMessageMediator().getConnection();

        conn.writeLock();

        try {
            conn.sendWithoutLock(((OutputObject)outputObject));

            fragmentCount++;

        } finally {

            conn.writeUnlock();
        }

    }

    public void sendMessage ()
    {
        sendFragment(true);

        sentFullMessage = true;
    }


    public void close(){};

}
