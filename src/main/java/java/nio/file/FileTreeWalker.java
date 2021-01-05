

package java.nio.file;

import java.nio.file.attribute.BasicFileAttributes;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import sun.nio.fs.BasicFileAttributesHolder;



class FileTreeWalker implements Closeable {
    private final boolean followLinks;
    private final LinkOption[] linkOptions;
    private final int maxDepth;
    private final ArrayDeque<DirectoryNode> stack = new ArrayDeque<>();
    private boolean closed;


    private static class DirectoryNode {
        private final Path dir;
        private final Object key;
        private final DirectoryStream<Path> stream;
        private final Iterator<Path> iterator;
        private boolean skipped;

        DirectoryNode(Path dir, Object key, DirectoryStream<Path> stream) {
            this.dir = dir;
            this.key = key;
            this.stream = stream;
            this.iterator = stream.iterator();
        }

        Path directory() {
            return dir;
        }

        Object key() {
            return key;
        }

        DirectoryStream<Path> stream() {
            return stream;
        }

        Iterator<Path> iterator() {
            return iterator;
        }

        void skip() {
            skipped = true;
        }

        boolean skipped() {
            return skipped;
        }
    }


    static enum EventType {

        START_DIRECTORY,

        END_DIRECTORY,

        ENTRY;
    }


    static class Event {
        private final EventType type;
        private final Path file;
        private final BasicFileAttributes attrs;
        private final IOException ioe;

        private Event(EventType type, Path file, BasicFileAttributes attrs, IOException ioe) {
            this.type = type;
            this.file = file;
            this.attrs = attrs;
            this.ioe = ioe;
        }

        Event(EventType type, Path file, BasicFileAttributes attrs) {
            this(type, file, attrs, null);
        }

        Event(EventType type, Path file, IOException ioe) {
            this(type, file, null, ioe);
        }

        EventType type() {
            return type;
        }

        Path file() {
            return file;
        }

        BasicFileAttributes attributes() {
            return attrs;
        }

        IOException ioeException() {
            return ioe;
        }
    }


    FileTreeWalker(Collection<FileVisitOption> options, int maxDepth) {
        boolean fl = false;
        for (FileVisitOption option: options) {
            switch (option) {
                case FOLLOW_LINKS : fl = true; break;
                default:
                    throw new AssertionError("Should not get here");
            }
        }
        if (maxDepth < 0)
            throw new IllegalArgumentException("'maxDepth' is negative");

        this.followLinks = fl;
        this.linkOptions = (fl) ? new LinkOption[0] :
            new LinkOption[] { LinkOption.NOFOLLOW_LINKS };
        this.maxDepth = maxDepth;
    }


    private BasicFileAttributes getAttributes(Path file, boolean canUseCached)
        throws IOException
    {
        if (canUseCached &&
            (file instanceof BasicFileAttributesHolder) &&
            (System.getSecurityManager() == null))
        {
            BasicFileAttributes cached = ((BasicFileAttributesHolder)file).get();
            if (cached != null && (!followLinks || !cached.isSymbolicLink())) {
                return cached;
            }
        }

        BasicFileAttributes attrs;
        try {
            attrs = Files.readAttributes(file, BasicFileAttributes.class, linkOptions);
        } catch (IOException ioe) {
            if (!followLinks)
                throw ioe;

            attrs = Files.readAttributes(file,
                                         BasicFileAttributes.class,
                                         LinkOption.NOFOLLOW_LINKS);
        }
        return attrs;
    }


    private boolean wouldLoop(Path dir, Object key) {
        for (DirectoryNode ancestor: stack) {
            Object ancestorKey = ancestor.key();
            if (key != null && ancestorKey != null) {
                if (key.equals(ancestorKey)) {
                    return true;
                }
            } else {
                try {
                    if (Files.isSameFile(dir, ancestor.directory())) {
                        return true;
                    }
                } catch (IOException | SecurityException x) {
                    }
            }
        }
        return false;
    }


    private Event visit(Path entry, boolean ignoreSecurityException, boolean canUseCached) {
        BasicFileAttributes attrs;
        try {
            attrs = getAttributes(entry, canUseCached);
        } catch (IOException ioe) {
            return new Event(EventType.ENTRY, entry, ioe);
        } catch (SecurityException se) {
            if (ignoreSecurityException)
                return null;
            throw se;
        }

        int depth = stack.size();
        if (depth >= maxDepth || !attrs.isDirectory()) {
            return new Event(EventType.ENTRY, entry, attrs);
        }

        if (followLinks && wouldLoop(entry, attrs.fileKey())) {
            return new Event(EventType.ENTRY, entry,
                             new FileSystemLoopException(entry.toString()));
        }

        DirectoryStream<Path> stream = null;
        try {
            stream = Files.newDirectoryStream(entry);
        } catch (IOException ioe) {
            return new Event(EventType.ENTRY, entry, ioe);
        } catch (SecurityException se) {
            if (ignoreSecurityException)
                return null;
            throw se;
        }

        stack.push(new DirectoryNode(entry, attrs.fileKey(), stream));
        return new Event(EventType.START_DIRECTORY, entry, attrs);
    }



    Event walk(Path file) {
        if (closed)
            throw new IllegalStateException("Closed");

        Event ev = visit(file,
                         false,   false);  assert ev != null;
        return ev;
    }


    Event next() {
        DirectoryNode top = stack.peek();
        if (top == null)
            return null;      Event ev;
        do {
            Path entry = null;
            IOException ioe = null;

            if (!top.skipped()) {
                Iterator<Path> iterator = top.iterator();
                try {
                    if (iterator.hasNext()) {
                        entry = iterator.next();
                    }
                } catch (DirectoryIteratorException x) {
                    ioe = x.getCause();
                }
            }

            if (entry == null) {
                try {
                    top.stream().close();
                } catch (IOException e) {
                    if (ioe != null) {
                        ioe = e;
                    } else {
                        ioe.addSuppressed(e);
                    }
                }
                stack.pop();
                return new Event(EventType.END_DIRECTORY, top.directory(), ioe);
            }

            ev = visit(entry,
                       true,   true);  } while (ev == null);

        return ev;
    }


    void pop() {
        if (!stack.isEmpty()) {
            DirectoryNode node = stack.pop();
            try {
                node.stream().close();
            } catch (IOException ignore) { }
        }
    }


    void skipRemainingSiblings() {
        if (!stack.isEmpty()) {
            stack.peek().skip();
        }
    }


    boolean isOpen() {
        return !closed;
    }


    @Override
    public void close() {
        if (!closed) {
            while (!stack.isEmpty()) {
                pop();
            }
            closed = true;
        }
    }
}
