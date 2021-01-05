

package com.sun.corba.se.spi.transport;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.omg.CORBA.SystemException;

import com.sun.org.omg.SendingContext.CodeBase;

import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ResponseWaitingRoom;

import com.sun.corba.se.spi.ior.IOR ;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;


public interface CorbaConnection
    extends
        Connection,
        com.sun.corba.se.spi.legacy.connection.Connection
{
    public boolean shouldUseDirectByteBuffers();

    public boolean shouldReadGiopHeaderOnly();

    public ByteBuffer read(int size, int offset, int length, long max_wait_time)
        throws IOException;

    public ByteBuffer read(ByteBuffer byteBuffer, int offset,
                          int length, long max_wait_time) throws IOException;

    public void write(ByteBuffer byteBuffer)
        throws IOException;

    public void dprint(String msg);

    public int getNextRequestId();
    public ORB getBroker();
    public CodeSetComponentInfo.CodeSetContext getCodeSetContext();
    public void setCodeSetContext(CodeSetComponentInfo.CodeSetContext csc);

    public MessageMediator clientRequestMapGet(int requestId);

    public void clientReply_1_1_Put(MessageMediator x);
    public MessageMediator clientReply_1_1_Get();
    public void clientReply_1_1_Remove();

    public void serverRequest_1_1_Put(MessageMediator x);
    public MessageMediator serverRequest_1_1_Get();
    public void serverRequest_1_1_Remove();

    public boolean isPostInitialContexts();

    public void setPostInitialContexts();

    public void purgeCalls(SystemException systemException,
                           boolean die, boolean lockHeld);

    public static final int OPENING = 1;
    public static final int ESTABLISHED = 2;
    public static final int CLOSE_SENT = 3;
    public static final int CLOSE_RECVD = 4;
    public static final int ABORT = 5;

    void setCodeBaseIOR(IOR ior);

    IOR getCodeBaseIOR();

    CodeBase getCodeBase();

    public void sendCloseConnection(GIOPVersion giopVersion)
        throws IOException;

    public void sendMessageError(GIOPVersion giopVersion)
        throws IOException;

    public void sendCancelRequest(GIOPVersion giopVersion, int requestId)
        throws
            IOException;

    public void sendCancelRequestWithLock(GIOPVersion giopVersion,
                                          int requestId)
        throws
            IOException;

    public ResponseWaitingRoom getResponseWaitingRoom();

    public void serverRequestMapPut(int requestId,
                                    CorbaMessageMediator messageMediator);
    public CorbaMessageMediator serverRequestMapGet(int requestId);
    public void serverRequestMapRemove(int requestId);

    public SocketChannel getSocketChannel();

    public void serverRequestProcessingBegins();
    public void serverRequestProcessingEnds();


    public void closeConnectionResources();
}

