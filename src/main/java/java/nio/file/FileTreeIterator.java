

package java.nio.file;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.nio.file.FileTreeWalker.Event;



class FileTreeIterator implements Iterator<Event>, Closeable {
    private final FileTreeWalker walker;
    private Event next;


    FileTreeIterator(Path start, int maxDepth, FileVisitOption... options)
        throws IOException
    {
        this.walker = new FileTreeWalker(Arrays.asList(options), maxDepth);
        this.next = walker.walk(start);
        assert next.type() == FileTreeWalker.EventType.ENTRY ||
               next.type() == FileTreeWalker.EventType.START_DIRECTORY;

        IOException ioe = next.ioeException();
        if (ioe != null)
            throw ioe;
    }

    private void fetchNextIfNeeded() {
        if (next == null) {
            FileTreeWalker.Event ev = walker.next();
            while (ev != null) {
                IOException ioe = ev.ioeException();
                if (ioe != null)
                    throw new UncheckedIOException(ioe);

                if (ev.type() != FileTreeWalker.EventType.END_DIRECTORY) {
                    next = ev;
                    return;
                }
                ev = walker.next();
            }
        }
    }

    @Override
    public boolean hasNext() {
        if (!walker.isOpen())
            throw new IllegalStateException();
        fetchNextIfNeeded();
        return next != null;
    }

    @Override
    public Event next() {
        if (!walker.isOpen())
            throw new IllegalStateException();
        fetchNextIfNeeded();
        if (next == null)
            throw new NoSuchElementException();
        Event result = next;
        next = null;
        return result;
    }

    @Override
    public void close() {
        walker.close();
    }
}
