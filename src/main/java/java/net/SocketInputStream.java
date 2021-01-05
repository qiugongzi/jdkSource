

package java.net;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import sun.net.ConnectionResetException;


class SocketInputStream extends FileInputStream
{
    static {
        init();
    }

    private boolean eof;
    private AbstractPlainSocketImpl impl = null;
    private byte temp[];
    private Socket socket = null;


    SocketInputStream(AbstractPlainSocketImpl impl) throws IOException {
        super(impl.getFileDescriptor());
        this.impl = impl;
        socket = impl.getSocket();
    }


    public final FileChannel getChannel() {
        return null;
    }


    private native int socketRead0(FileDescriptor fd,
                                   byte b[], int off, int len,
                                   int timeout)
        throws IOException;

    private int socketRead(FileDescriptor fd,
                           byte b[], int off, int len,
                           int timeout)
        throws IOException {
        return socketRead0(fd, b, off, len, timeout);
    }


    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }


    public int read(byte b[], int off, int length) throws IOException {
        return read(b, off, length, impl.getTimeout());
    }

    int read(byte b[], int off, int length, int timeout) throws IOException {
        int n;

        if (eof) {
            return -1;
        }

        if (impl.isConnectionReset()) {
            throw new SocketException("Connection reset");
        }

        if (length <= 0 || off < 0 || length > b.length - off) {
            if (length == 0) {
                return 0;
            }
            throw new ArrayIndexOutOfBoundsException("length == " + length
                    + " off == " + off + " buffer length == " + b.length);
        }

        boolean gotReset = false;

        FileDescriptor fd = impl.acquireFD();
        try {
            n = socketRead(fd, b, off, length, timeout);
            if (n > 0) {
                return n;
            }
        } catch (ConnectionResetException rstExc) {
            gotReset = true;
        } finally {
            impl.releaseFD();
        }


        if (gotReset) {
            impl.setConnectionResetPending();
            impl.acquireFD();
            try {
                n = socketRead(fd, b, off, length, timeout);
                if (n > 0) {
                    return n;
                }
            } catch (ConnectionResetException rstExc) {
            } finally {
                impl.releaseFD();
            }
        }


        if (impl.isClosedOrPending()) {
            throw new SocketException("Socket closed");
        }
        if (impl.isConnectionResetPending()) {
            impl.setConnectionReset();
        }
        if (impl.isConnectionReset()) {
            throw new SocketException("Connection reset");
        }
        eof = true;
        return -1;
    }


    public int read() throws IOException {
        if (eof) {
            return -1;
        }
        temp = new byte[1];
        int n = read(temp, 0, 1);
        if (n <= 0) {
            return -1;
        }
        return temp[0] & 0xff;
    }


    public long skip(long numbytes) throws IOException {
        if (numbytes <= 0) {
            return 0;
        }
        long n = numbytes;
        int buflen = (int) Math.min(1024, n);
        byte data[] = new byte[buflen];
        while (n > 0) {
            int r = read(data, 0, (int) Math.min((long) buflen, n));
            if (r < 0) {
                break;
            }
            n -= r;
        }
        return numbytes - n;
    }


    public int available() throws IOException {
        return impl.available();
    }


    private boolean closing = false;
    public void close() throws IOException {
        if (closing)
            return;
        closing = true;
        if (socket != null) {
            if (!socket.isClosed())
                socket.close();
        } else
            impl.close();
        closing = false;
    }

    void setEOF(boolean eof) {
        this.eof = eof;
    }


    protected void finalize() {}


    private native static void init();
}
