

package java.nio.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;   import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.spi.FileSystemProvider;
import java.nio.file.spi.FileTypeDetector;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;



public final class Files {
    private Files() { }


    private static FileSystemProvider provider(Path path) {
        return path.getFileSystem().provider();
    }


    private static Runnable asUncheckedRunnable(Closeable c) {
        return () -> {
            try {
                c.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    public static InputStream newInputStream(Path path, OpenOption... options)
        throws IOException
    {
        return provider(path).newInputStream(path, options);
    }


    public static OutputStream newOutputStream(Path path, OpenOption... options)
        throws IOException
    {
        return provider(path).newOutputStream(path, options);
    }


    public static SeekableByteChannel newByteChannel(Path path,
                                                     Set<? extends OpenOption> options,
                                                     FileAttribute<?>... attrs)
        throws IOException
    {
        return provider(path).newByteChannel(path, options, attrs);
    }


    public static SeekableByteChannel newByteChannel(Path path, OpenOption... options)
        throws IOException
    {
        Set<OpenOption> set = new HashSet<OpenOption>(options.length);
        Collections.addAll(set, options);
        return newByteChannel(path, set);
    }

    private static class AcceptAllFilter
        implements DirectoryStream.Filter<Path>
    {
        private AcceptAllFilter() { }

        @Override
        public boolean accept(Path entry) { return true; }

        static final AcceptAllFilter FILTER = new AcceptAllFilter();
    }


    public static DirectoryStream<Path> newDirectoryStream(Path dir)
        throws IOException
    {
        return provider(dir).newDirectoryStream(dir, AcceptAllFilter.FILTER);
    }


    public static DirectoryStream<Path> newDirectoryStream(Path dir, String glob)
        throws IOException
    {
        if (glob.equals("*"))
            return newDirectoryStream(dir);

        FileSystem fs = dir.getFileSystem();
        final PathMatcher matcher = fs.getPathMatcher("glob:" + glob);
        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry)  {
                return matcher.matches(entry.getFileName());
            }
        };
        return fs.provider().newDirectoryStream(dir, filter);
    }


    public static DirectoryStream<Path> newDirectoryStream(Path dir,
                                                           DirectoryStream.Filter<? super Path> filter)
        throws IOException
    {
        return provider(dir).newDirectoryStream(dir, filter);
    }

    public static Path createFile(Path path, FileAttribute<?>... attrs)
        throws IOException
    {
        EnumSet<StandardOpenOption> options =
            EnumSet.<StandardOpenOption>of(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        newByteChannel(path, options, attrs).close();
        return path;
    }


    public static Path createDirectory(Path dir, FileAttribute<?>... attrs)
        throws IOException
    {
        provider(dir).createDirectory(dir, attrs);
        return dir;
    }


    public static Path createDirectories(Path dir, FileAttribute<?>... attrs)
        throws IOException
    {
        try {
            createAndCheckIsDirectory(dir, attrs);
            return dir;
        } catch (FileAlreadyExistsException x) {
            throw x;
        } catch (IOException x) {
            }
        SecurityException se = null;
        try {
            dir = dir.toAbsolutePath();
        } catch (SecurityException x) {
            se = x;
        }
        Path parent = dir.getParent();
        while (parent != null) {
            try {
                provider(parent).checkAccess(parent);
                break;
            } catch (NoSuchFileException x) {
                }
            parent = parent.getParent();
        }
        if (parent == null) {
            if (se == null) {
                throw new FileSystemException(dir.toString(), null,
                    "Unable to determine if root directory exists");
            } else {
                throw se;
            }
        }

        Path child = parent;
        for (Path name: parent.relativize(dir)) {
            child = child.resolve(name);
            createAndCheckIsDirectory(child, attrs);
        }
        return dir;
    }


    private static void createAndCheckIsDirectory(Path dir,
                                                  FileAttribute<?>... attrs)
        throws IOException
    {
        try {
            createDirectory(dir, attrs);
        } catch (FileAlreadyExistsException x) {
            if (!isDirectory(dir, LinkOption.NOFOLLOW_LINKS))
                throw x;
        }
    }


    public static Path createTempFile(Path dir,
                                      String prefix,
                                      String suffix,
                                      FileAttribute<?>... attrs)
        throws IOException
    {
        return TempFileHelper.createTempFile(Objects.requireNonNull(dir),
                                             prefix, suffix, attrs);
    }


    public static Path createTempFile(String prefix,
                                      String suffix,
                                      FileAttribute<?>... attrs)
        throws IOException
    {
        return TempFileHelper.createTempFile(null, prefix, suffix, attrs);
    }


    public static Path createTempDirectory(Path dir,
                                           String prefix,
                                           FileAttribute<?>... attrs)
        throws IOException
    {
        return TempFileHelper.createTempDirectory(Objects.requireNonNull(dir),
                                                  prefix, attrs);
    }


    public static Path createTempDirectory(String prefix,
                                           FileAttribute<?>... attrs)
        throws IOException
    {
        return TempFileHelper.createTempDirectory(null, prefix, attrs);
    }


    public static Path createSymbolicLink(Path link, Path target,
                                          FileAttribute<?>... attrs)
        throws IOException
    {
        provider(link).createSymbolicLink(link, target, attrs);
        return link;
    }


    public static Path createLink(Path link, Path existing) throws IOException {
        provider(link).createLink(link, existing);
        return link;
    }


    public static void delete(Path path) throws IOException {
        provider(path).delete(path);
    }


    public static boolean deleteIfExists(Path path) throws IOException {
        return provider(path).deleteIfExists(path);
    }

    public static Path copy(Path source, Path target, CopyOption... options)
        throws IOException
    {
        FileSystemProvider provider = provider(source);
        if (provider(target) == provider) {
            provider.copy(source, target, options);
        } else {
            CopyMoveHelper.copyToForeignTarget(source, target, options);
        }
        return target;
    }


    public static Path move(Path source, Path target, CopyOption... options)
        throws IOException
    {
        FileSystemProvider provider = provider(source);
        if (provider(target) == provider) {
            provider.move(source, target, options);
        } else {
            CopyMoveHelper.moveToForeignTarget(source, target, options);
        }
        return target;
    }

    public static Path readSymbolicLink(Path link) throws IOException {
        return provider(link).readSymbolicLink(link);
    }


    public static FileStore getFileStore(Path path) throws IOException {
        return provider(path).getFileStore(path);
    }


    public static boolean isSameFile(Path path, Path path2) throws IOException {
        return provider(path).isSameFile(path, path2);
    }


    public static boolean isHidden(Path path) throws IOException {
        return provider(path).isHidden(path);
    }

    private static class FileTypeDetectors{
        static final FileTypeDetector defaultFileTypeDetector =
            createDefaultFileTypeDetector();
        static final List<FileTypeDetector> installeDetectors =
            loadInstalledDetectors();

        private static FileTypeDetector createDefaultFileTypeDetector() {
            return AccessController
                .doPrivileged(new PrivilegedAction<FileTypeDetector>() {
                    @Override public FileTypeDetector run() {
                        return sun.nio.fs.DefaultFileTypeDetector.create();
                }});
        }

        private static List<FileTypeDetector> loadInstalledDetectors() {
            return AccessController
                .doPrivileged(new PrivilegedAction<List<FileTypeDetector>>() {
                    @Override public List<FileTypeDetector> run() {
                        List<FileTypeDetector> list = new ArrayList<>();
                        ServiceLoader<FileTypeDetector> loader = ServiceLoader
                            .load(FileTypeDetector.class, ClassLoader.getSystemClassLoader());
                        for (FileTypeDetector detector: loader) {
                            list.add(detector);
                        }
                        return list;
                }});
        }
    }


    public static String probeContentType(Path path)
        throws IOException
    {
        for (FileTypeDetector detector: FileTypeDetectors.installeDetectors) {
            String result = detector.probeContentType(path);
            if (result != null)
                return result;
        }

        return FileTypeDetectors.defaultFileTypeDetector.probeContentType(path);
    }

    public static <V extends FileAttributeView> V getFileAttributeView(Path path,
                                                                       Class<V> type,
                                                                       LinkOption... options)
    {
        return provider(path).getFileAttributeView(path, type, options);
    }


    public static <A extends BasicFileAttributes> A readAttributes(Path path,
                                                                   Class<A> type,
                                                                   LinkOption... options)
        throws IOException
    {
        return provider(path).readAttributes(path, type, options);
    }


    public static Path setAttribute(Path path, String attribute, Object value,
                                    LinkOption... options)
        throws IOException
    {
        provider(path).setAttribute(path, attribute, value, options);
        return path;
    }


    public static Object getAttribute(Path path, String attribute,
                                      LinkOption... options)
        throws IOException
    {
        if (attribute.indexOf('*') >= 0 || attribute.indexOf(',') >= 0)
            throw new IllegalArgumentException(attribute);
        Map<String,Object> map = readAttributes(path, attribute, options);
        assert map.size() == 1;
        String name;
        int pos = attribute.indexOf(':');
        if (pos == -1) {
            name = attribute;
        } else {
            name = (pos == attribute.length()) ? "" : attribute.substring(pos+1);
        }
        return map.get(name);
    }


    public static Map<String,Object> readAttributes(Path path, String attributes,
                                                    LinkOption... options)
        throws IOException
    {
        return provider(path).readAttributes(path, attributes, options);
    }


    public static Set<PosixFilePermission> getPosixFilePermissions(Path path,
                                                                   LinkOption... options)
        throws IOException
    {
        return readAttributes(path, PosixFileAttributes.class, options).permissions();
    }


    public static Path setPosixFilePermissions(Path path,
                                               Set<PosixFilePermission> perms)
        throws IOException
    {
        PosixFileAttributeView view =
            getFileAttributeView(path, PosixFileAttributeView.class);
        if (view == null)
            throw new UnsupportedOperationException();
        view.setPermissions(perms);
        return path;
    }


    public static UserPrincipal getOwner(Path path, LinkOption... options) throws IOException {
        FileOwnerAttributeView view =
            getFileAttributeView(path, FileOwnerAttributeView.class, options);
        if (view == null)
            throw new UnsupportedOperationException();
        return view.getOwner();
    }


    public static Path setOwner(Path path, UserPrincipal owner)
        throws IOException
    {
        FileOwnerAttributeView view =
            getFileAttributeView(path, FileOwnerAttributeView.class);
        if (view == null)
            throw new UnsupportedOperationException();
        view.setOwner(owner);
        return path;
    }


    public static boolean isSymbolicLink(Path path) {
        try {
            return readAttributes(path,
                                  BasicFileAttributes.class,
                                  LinkOption.NOFOLLOW_LINKS).isSymbolicLink();
        } catch (IOException ioe) {
            return false;
        }
    }


    public static boolean isDirectory(Path path, LinkOption... options) {
        try {
            return readAttributes(path, BasicFileAttributes.class, options).isDirectory();
        } catch (IOException ioe) {
            return false;
        }
    }


    public static boolean isRegularFile(Path path, LinkOption... options) {
        try {
            return readAttributes(path, BasicFileAttributes.class, options).isRegularFile();
        } catch (IOException ioe) {
            return false;
        }
    }


    public static FileTime getLastModifiedTime(Path path, LinkOption... options)
        throws IOException
    {
        return readAttributes(path, BasicFileAttributes.class, options).lastModifiedTime();
    }


    public static Path setLastModifiedTime(Path path, FileTime time)
        throws IOException
    {
        getFileAttributeView(path, BasicFileAttributeView.class)
            .setTimes(time, null, null);
        return path;
    }


    public static long size(Path path) throws IOException {
        return readAttributes(path, BasicFileAttributes.class).size();
    }

    private static boolean followLinks(LinkOption... options) {
        boolean followLinks = true;
        for (LinkOption opt: options) {
            if (opt == LinkOption.NOFOLLOW_LINKS) {
                followLinks = false;
                continue;
            }
            if (opt == null)
                throw new NullPointerException();
            throw new AssertionError("Should not get here");
        }
        return followLinks;
    }


    public static boolean exists(Path path, LinkOption... options) {
        try {
            if (followLinks(options)) {
                provider(path).checkAccess(path);
            } else {
                readAttributes(path, BasicFileAttributes.class,
                               LinkOption.NOFOLLOW_LINKS);
            }
            return true;
        } catch (IOException x) {
            return false;
        }

    }


    public static boolean notExists(Path path, LinkOption... options) {
        try {
            if (followLinks(options)) {
                provider(path).checkAccess(path);
            } else {
                readAttributes(path, BasicFileAttributes.class,
                               LinkOption.NOFOLLOW_LINKS);
            }
            return false;
        } catch (NoSuchFileException x) {
            return true;
        } catch (IOException x) {
            return false;
        }
    }


    private static boolean isAccessible(Path path, AccessMode... modes) {
        try {
            provider(path).checkAccess(path, modes);
            return true;
        } catch (IOException x) {
            return false;
        }
    }


    public static boolean isReadable(Path path) {
        return isAccessible(path, AccessMode.READ);
    }


    public static boolean isWritable(Path path) {
        return isAccessible(path, AccessMode.WRITE);
    }


    public static boolean isExecutable(Path path) {
        return isAccessible(path, AccessMode.EXECUTE);
    }

    public static Path walkFileTree(Path start,
                                    Set<FileVisitOption> options,
                                    int maxDepth,
                                    FileVisitor<? super Path> visitor)
        throws IOException
    {

        try (FileTreeWalker walker = new FileTreeWalker(options, maxDepth)) {
            FileTreeWalker.Event ev = walker.walk(start);
            do {
                FileVisitResult result;
                switch (ev.type()) {
                    case ENTRY :
                        IOException ioe = ev.ioeException();
                        if (ioe == null) {
                            assert ev.attributes() != null;
                            result = visitor.visitFile(ev.file(), ev.attributes());
                        } else {
                            result = visitor.visitFileFailed(ev.file(), ioe);
                        }
                        break;

                    case START_DIRECTORY :
                        result = visitor.preVisitDirectory(ev.file(), ev.attributes());

                        if (result == FileVisitResult.SKIP_SUBTREE ||
                            result == FileVisitResult.SKIP_SIBLINGS)
                            walker.pop();
                        break;

                    case END_DIRECTORY :
                        result = visitor.postVisitDirectory(ev.file(), ev.ioeException());

                        if (result == FileVisitResult.SKIP_SIBLINGS)
                            result = FileVisitResult.CONTINUE;
                        break;

                    default :
                        throw new AssertionError("Should not get here");
                }

                if (Objects.requireNonNull(result) != FileVisitResult.CONTINUE) {
                    if (result == FileVisitResult.TERMINATE) {
                        break;
                    } else if (result == FileVisitResult.SKIP_SIBLINGS) {
                        walker.skipRemainingSiblings();
                    }
                }
                ev = walker.next();
            } while (ev != null);
        }

        return start;
    }


    public static Path walkFileTree(Path start, FileVisitor<? super Path> visitor)
        throws IOException
    {
        return walkFileTree(start,
                            EnumSet.noneOf(FileVisitOption.class),
                            Integer.MAX_VALUE,
                            visitor);
    }


    private static final int BUFFER_SIZE = 8192;


    public static BufferedReader newBufferedReader(Path path, Charset cs)
        throws IOException
    {
        CharsetDecoder decoder = cs.newDecoder();
        Reader reader = new InputStreamReader(newInputStream(path), decoder);
        return new BufferedReader(reader);
    }


    public static BufferedReader newBufferedReader(Path path) throws IOException {
        return newBufferedReader(path, StandardCharsets.UTF_8);
    }


    public static BufferedWriter newBufferedWriter(Path path, Charset cs,
                                                   OpenOption... options)
        throws IOException
    {
        CharsetEncoder encoder = cs.newEncoder();
        Writer writer = new OutputStreamWriter(newOutputStream(path, options), encoder);
        return new BufferedWriter(writer);
    }


    public static BufferedWriter newBufferedWriter(Path path, OpenOption... options) throws IOException {
        return newBufferedWriter(path, StandardCharsets.UTF_8, options);
    }


    private static long copy(InputStream source, OutputStream sink)
        throws IOException
    {
        long nread = 0L;
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
            nread += n;
        }
        return nread;
    }


    public static long copy(InputStream in, Path target, CopyOption... options)
        throws IOException
    {
        Objects.requireNonNull(in);

        boolean replaceExisting = false;
        for (CopyOption opt: options) {
            if (opt == StandardCopyOption.REPLACE_EXISTING) {
                replaceExisting = true;
            } else {
                if (opt == null) {
                    throw new NullPointerException("options contains 'null'");
                }  else {
                    throw new UnsupportedOperationException(opt + " not supported");
                }
            }
        }

        SecurityException se = null;
        if (replaceExisting) {
            try {
                deleteIfExists(target);
            } catch (SecurityException x) {
                se = x;
            }
        }

        OutputStream ostream;
        try {
            ostream = newOutputStream(target, StandardOpenOption.CREATE_NEW,
                                              StandardOpenOption.WRITE);
        } catch (FileAlreadyExistsException x) {
            if (se != null)
                throw se;
            throw x;
        }

        try (OutputStream out = ostream) {
            return copy(in, out);
        }
    }


    public static long copy(Path source, OutputStream out) throws IOException {
        Objects.requireNonNull(out);

        try (InputStream in = newInputStream(source)) {
            return copy(in, out);
        }
    }


    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;


    private static byte[] read(InputStream source, int initialSize) throws IOException {
        int capacity = initialSize;
        byte[] buf = new byte[capacity];
        int nread = 0;
        int n;
        for (;;) {
            while ((n = source.read(buf, nread, capacity - nread)) > 0)
                nread += n;

            if (n < 0 || (n = source.read()) < 0)
                break;

            if (capacity <= MAX_BUFFER_SIZE - capacity) {
                capacity = Math.max(capacity << 1, BUFFER_SIZE);
            } else {
                if (capacity == MAX_BUFFER_SIZE)
                    throw new OutOfMemoryError("Required array size too large");
                capacity = MAX_BUFFER_SIZE;
            }
            buf = Arrays.copyOf(buf, capacity);
            buf[nread++] = (byte)n;
        }
        return (capacity == nread) ? buf : Arrays.copyOf(buf, nread);
    }


    public static byte[] readAllBytes(Path path) throws IOException {
        try (SeekableByteChannel sbc = Files.newByteChannel(path);
             InputStream in = Channels.newInputStream(sbc)) {
            long size = sbc.size();
            if (size > (long)MAX_BUFFER_SIZE)
                throw new OutOfMemoryError("Required array size too large");

            return read(in, (int)size);
        }
    }


    public static List<String> readAllLines(Path path, Charset cs) throws IOException {
        try (BufferedReader reader = newBufferedReader(path, cs)) {
            List<String> result = new ArrayList<>();
            for (;;) {
                String line = reader.readLine();
                if (line == null)
                    break;
                result.add(line);
            }
            return result;
        }
    }


    public static List<String> readAllLines(Path path) throws IOException {
        return readAllLines(path, StandardCharsets.UTF_8);
    }


    public static Path write(Path path, byte[] bytes, OpenOption... options)
        throws IOException
    {
        Objects.requireNonNull(bytes);

        try (OutputStream out = Files.newOutputStream(path, options)) {
            int len = bytes.length;
            int rem = len;
            while (rem > 0) {
                int n = Math.min(rem, BUFFER_SIZE);
                out.write(bytes, (len-rem), n);
                rem -= n;
            }
        }
        return path;
    }


    public static Path write(Path path, Iterable<? extends CharSequence> lines,
                             Charset cs, OpenOption... options)
        throws IOException
    {
        Objects.requireNonNull(lines);
        CharsetEncoder encoder = cs.newEncoder();
        OutputStream out = newOutputStream(path, options);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, encoder))) {
            for (CharSequence line: lines) {
                writer.append(line);
                writer.newLine();
            }
        }
        return path;
    }


    public static Path write(Path path,
                             Iterable<? extends CharSequence> lines,
                             OpenOption... options)
        throws IOException
    {
        return write(path, lines, StandardCharsets.UTF_8, options);
    }

    public static Stream<Path> list(Path dir) throws IOException {
        DirectoryStream<Path> ds = Files.newDirectoryStream(dir);
        try {
            final Iterator<Path> delegate = ds.iterator();

            Iterator<Path> it = new Iterator<Path>() {
                @Override
                public boolean hasNext() {
                    try {
                        return delegate.hasNext();
                    } catch (DirectoryIteratorException e) {
                        throw new UncheckedIOException(e.getCause());
                    }
                }
                @Override
                public Path next() {
                    try {
                        return delegate.next();
                    } catch (DirectoryIteratorException e) {
                        throw new UncheckedIOException(e.getCause());
                    }
                }
            };

            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, Spliterator.DISTINCT), false)
                                .onClose(asUncheckedRunnable(ds));
        } catch (Error|RuntimeException e) {
            try {
                ds.close();
            } catch (IOException ex) {
                try {
                    e.addSuppressed(ex);
                } catch (Throwable ignore) {}
            }
            throw e;
        }
    }


    public static Stream<Path> walk(Path start,
                                    int maxDepth,
                                    FileVisitOption... options)
        throws IOException
    {
        FileTreeIterator iterator = new FileTreeIterator(start, maxDepth, options);
        try {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.DISTINCT), false)
                                .onClose(iterator::close)
                                .map(entry -> entry.file());
        } catch (Error|RuntimeException e) {
            iterator.close();
            throw e;
        }
    }


    public static Stream<Path> walk(Path start, FileVisitOption... options) throws IOException {
        return walk(start, Integer.MAX_VALUE, options);
    }


    public static Stream<Path> find(Path start,
                                    int maxDepth,
                                    BiPredicate<Path, BasicFileAttributes> matcher,
                                    FileVisitOption... options)
        throws IOException
    {
        FileTreeIterator iterator = new FileTreeIterator(start, maxDepth, options);
        try {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.DISTINCT), false)
                                .onClose(iterator::close)
                                .filter(entry -> matcher.test(entry.file(), entry.attributes()))
                                .map(entry -> entry.file());
        } catch (Error|RuntimeException e) {
            iterator.close();
            throw e;
        }
    }


    public static Stream<String> lines(Path path, Charset cs) throws IOException {
        BufferedReader br = Files.newBufferedReader(path, cs);
        try {
            return br.lines().onClose(asUncheckedRunnable(br));
        } catch (Error|RuntimeException e) {
            try {
                br.close();
            } catch (IOException ex) {
                try {
                    e.addSuppressed(ex);
                } catch (Throwable ignore) {}
            }
            throw e;
        }
    }


    public static Stream<String> lines(Path path) throws IOException {
        return lines(path, StandardCharsets.UTF_8);
    }
}
