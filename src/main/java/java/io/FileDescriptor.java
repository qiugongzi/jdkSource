

package java.io;

import java.util.ArrayList;
import java.util.List;


public final class FileDescriptor {

    private int fd;

    private long handle;

    private Closeable parent;
    private List<Closeable> otherParents;
    private boolean closed;


    public
    public static final FileDescriptor in = standardStream(0);


    public static final FileDescriptor out = standardStream(1);


    public static final FileDescriptor err = standardStream(2);


    public boolean valid() {
        return ((handle != -1) || (fd != -1));
    }


    public native void sync() throws SyncFailedException;


    private static native void initIDs();

    private static native long set(int d);

    private static FileDescriptor standardStream(int fd) {
        FileDescriptor desc = new FileDescriptor();
        desc.handle = set(fd);
        return desc;
    }




    synchronized void attach(Closeable c) {
        if (parent == null) {
            parent = c;
        } else if (otherParents == null) {
            otherParents = new ArrayList<>();
            otherParents.add(parent);
            otherParents.add(c);
        } else {
            otherParents.add(c);
        }
    }


    @SuppressWarnings("try")
    synchronized void closeAll(Closeable releaser) throws IOException {
        if (!closed) {
            closed = true;
            IOException ioe = null;
            try (Closeable c = releaser) {
                if (otherParents != null) {
                    for (Closeable referent : otherParents) {
                        try {
                            referent.close();
                        } catch(IOException x) {
                            if (ioe == null) {
                                ioe = x;
                            } else {
                                ioe.addSuppressed(x);
                            }
                        }
                    }
                }
            } catch(IOException ex) {

                if (ioe != null)
                    ex.addSuppressed(ioe);
                ioe = ex;
            } finally {
                if (ioe != null)
                    throw ioe;
            }
        }
    }
}
