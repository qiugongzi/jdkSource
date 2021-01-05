

package com.sun.corba.se.impl.encoding;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.omg.CORBA.Any;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;

import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;

import com.sun.corba.se.spi.encoding.CorbaOutputObject ;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.logging.CORBALogDomains;

import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.impl.encoding.BufferManagerFactory;
import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.encoding.CDROutputStream_1_0;
import com.sun.corba.se.impl.encoding.CodeSetConversion;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;


public class CDROutputObject extends CorbaOutputObject
{
    private Message header;
    private ORB orb;
    private ORBUtilSystemException wrapper;
    private OMGSystemException omgWrapper;

    private CorbaConnection connection;

    private CDROutputObject(
        ORB orb, GIOPVersion giopVersion, Message header,
        BufferManagerWrite manager, byte streamFormatVersion,
        CorbaMessageMediator mediator)
    {
        super(orb, giopVersion, header.getEncodingVersion(),
              false, manager, streamFormatVersion,
              ((mediator != null && mediator.getConnection() != null) ?
               ((CorbaConnection)mediator.getConnection()).
                     shouldUseDirectByteBuffers() : false));

        this.header = header;
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get( orb, CORBALogDomains.RPC_ENCODING ) ;
        this.omgWrapper = OMGSystemException.get( orb, CORBALogDomains.RPC_ENCODING ) ;

        getBufferManager().setOutputObject(this);
        this.corbaMessageMediator = mediator;
    }

    public CDROutputObject(ORB orb,
                           MessageMediator messageMediator,
                           Message header,
                           byte streamFormatVersion)
    {
        this(
            orb,
            ((CorbaMessageMediator)messageMediator).getGIOPVersion(),
            header,
            BufferManagerFactory.newBufferManagerWrite(
                ((CorbaMessageMediator)messageMediator).getGIOPVersion(),
                header.getEncodingVersion(),
                orb),
            streamFormatVersion,
            (CorbaMessageMediator)messageMediator);
    }

    public CDROutputObject(ORB orb,
                           MessageMediator messageMediator,
                           Message header,
                           byte streamFormatVersion,
                           int strategy)
    {
        this(
            orb,
            ((CorbaMessageMediator)messageMediator).getGIOPVersion(),
            header,
            BufferManagerFactory.
                newBufferManagerWrite(strategy,
                                      header.getEncodingVersion(),
                                      orb),
            streamFormatVersion,
            (CorbaMessageMediator)messageMediator);
    }

    public CDROutputObject(ORB orb, CorbaMessageMediator mediator,
                           GIOPVersion giopVersion,
                           CorbaConnection connection, Message header,
                           byte streamFormatVersion)
    {
        this(
            orb,
            giopVersion,
            header,
            BufferManagerFactory.
            newBufferManagerWrite(giopVersion,
                                  header.getEncodingVersion(),
                                  orb),
            streamFormatVersion,
            mediator);
        this.connection = connection ;
    }

    public Message getMessageHeader() {
        return header;
    }

    public final void finishSendingMessage() {
        getBufferManager().sendMessage();
    }


    public void writeTo(CorbaConnection connection)
        throws java.io.IOException
    {

        ByteBufferWithInfo bbwi = getByteBufferWithInfo();

        getMessageHeader().setSize(bbwi.byteBuffer, bbwi.getSize());

        if (orb() != null) {
            if (((ORB)orb()).transportDebugFlag) {
                dprint(".writeTo: " + connection);
            }
            if (((ORB)orb()).giopDebugFlag) {
                CDROutputStream_1_0.printBuffer(bbwi);
            }
        }
        bbwi.byteBuffer.position(0).limit(bbwi.getSize());
        connection.write(bbwi.byteBuffer);
    }


    public org.omg.CORBA.portable.InputStream create_input_stream()
    {
        return null;
        }

    public CorbaConnection getConnection()
    {
        if (connection != null) {
            return connection;
        }
        return (CorbaConnection) corbaMessageMediator.getConnection();
    }

    public final ByteBufferWithInfo getByteBufferWithInfo() {
        return super.getByteBufferWithInfo();
    }

    public final void setByteBufferWithInfo(ByteBufferWithInfo bbwi) {
        super.setByteBufferWithInfo(bbwi);
    }


    protected CodeSetConversion.CTBConverter createCharCTBConverter() {
        CodeSetComponentInfo.CodeSetContext codesets = getCodeSets();

        if (codesets == null)
            return super.createCharCTBConverter();

        OSFCodeSetRegistry.Entry charSet
            = OSFCodeSetRegistry.lookupEntry(codesets.getCharCodeSet());

        if (charSet == null)
            throw wrapper.unknownCodeset( charSet ) ;

        return CodeSetConversion.impl().getCTBConverter(charSet,
                                                        isLittleEndian(),
                                                        false);
    }

    protected CodeSetConversion.CTBConverter createWCharCTBConverter() {

        CodeSetComponentInfo.CodeSetContext codesets = getCodeSets();

        if (codesets == null) {
            if (getConnection().isServer())
                throw omgWrapper.noClientWcharCodesetCtx() ;
            else
                throw omgWrapper.noServerWcharCodesetCmp() ;
        }

        OSFCodeSetRegistry.Entry wcharSet
            = OSFCodeSetRegistry.lookupEntry(codesets.getWCharCodeSet());

        if (wcharSet == null)
            throw wrapper.unknownCodeset( wcharSet ) ;

        boolean useByteOrderMarkers
            = ((ORB)orb()).getORBData().useByteOrderMarkers();

        if (wcharSet == OSFCodeSetRegistry.UTF_16) {
            if (getGIOPVersion().equals(GIOPVersion.V1_2)) {
                return CodeSetConversion.impl().getCTBConverter(wcharSet,
                                                                false,
                                                                useByteOrderMarkers);
            }

            if (getGIOPVersion().equals(GIOPVersion.V1_1)) {
                return CodeSetConversion.impl().getCTBConverter(wcharSet,
                                                                isLittleEndian(),
                                                                false);
            }
        }

        return CodeSetConversion.impl().getCTBConverter(wcharSet,
                                                        isLittleEndian(),
                                                        useByteOrderMarkers);
    }

    private CodeSetComponentInfo.CodeSetContext getCodeSets() {
        if (getConnection() == null)
            return CodeSetComponentInfo.LOCAL_CODE_SETS;
        else
            return getConnection().getCodeSetContext();
    }

    protected void dprint(String msg)
    {
        ORBUtility.dprint("CDROutputObject", msg);
    }
}

