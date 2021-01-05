

package java.lang;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



final class ProcessImpl extends Process {
    private static final sun.misc.JavaIOFileDescriptorAccess fdAccess
        = sun.misc.SharedSecrets.getJavaIOFileDescriptorAccess();


    private static FileOutputStream newFileOutputStream(File f, boolean append)
        throws IOException
    {
        if (append) {
            String path = f.getPath();
            SecurityManager sm = System.getSecurityManager();
            if (sm != null)
                sm.checkWrite(path);
            long handle = openForAtomicAppend(path);
            final FileDescriptor fd = new FileDescriptor();
            fdAccess.setHandle(fd, handle);
            return AccessController.doPrivileged(
                new PrivilegedAction<FileOutputStream>() {
                    public FileOutputStream run() {
                        return new FileOutputStream(fd);
                    }
                }
            );
        } else {
            return new FileOutputStream(f);
        }
    }

    static Process start(String cmdarray[],
                         java.util.Map<String,String> environment,
                         String dir,
                         ProcessBuilder.Redirect[] redirects,
                         boolean redirectErrorStream)
        throws IOException
    {
        String envblock = ProcessEnvironment.toEnvironmentBlock(environment);

        FileInputStream  f0 = null;
        FileOutputStream f1 = null;
        FileOutputStream f2 = null;

        try {
            long[] stdHandles;
            if (redirects == null) {
                stdHandles = new long[] { -1L, -1L, -1L };
            } else {
                stdHandles = new long[3];

                if (redirects[0] == Redirect.PIPE)
                    stdHandles[0] = -1L;
                else if (redirects[0] == Redirect.INHERIT)
                    stdHandles[0] = fdAccess.getHandle(FileDescriptor.in);
                else {
                    f0 = new FileInputStream(redirects[0].file());
                    stdHandles[0] = fdAccess.getHandle(f0.getFD());
                }

                if (redirects[1] == Redirect.PIPE)
                    stdHandles[1] = -1L;
                else if (redirects[1] == Redirect.INHERIT)
                    stdHandles[1] = fdAccess.getHandle(FileDescriptor.out);
                else {
                    f1 = newFileOutputStream(redirects[1].file(),
                                             redirects[1].append());
                    stdHandles[1] = fdAccess.getHandle(f1.getFD());
                }

                if (redirects[2] == Redirect.PIPE)
                    stdHandles[2] = -1L;
                else if (redirects[2] == Redirect.INHERIT)
                    stdHandles[2] = fdAccess.getHandle(FileDescriptor.err);
                else {
                    f2 = newFileOutputStream(redirects[2].file(),
                                             redirects[2].append());
                    stdHandles[2] = fdAccess.getHandle(f2.getFD());
                }
            }

            return new ProcessImpl(cmdarray, envblock, dir,
                                   stdHandles, redirectErrorStream);
        } finally {
            try { if (f0 != null) f0.close(); }
            finally {
                try { if (f1 != null) f1.close(); }
                finally { if (f2 != null) f2.close(); }
            }
        }

    }

    private static class LazyPattern {
        private static final Pattern PATTERN =
            Pattern.compile("[^\\s\"]+|\"[^\"]*\"");
    };


    private static String[] getTokensFromCommand(String command) {
        ArrayList<String> matchList = new ArrayList<>(8);
        Matcher regexMatcher = LazyPattern.PATTERN.matcher(command);
        while (regexMatcher.find())
            matchList.add(regexMatcher.group());
        return matchList.toArray(new String[matchList.size()]);
    }

    private static final int VERIFICATION_CMD_BAT = 0;
    private static final int VERIFICATION_WIN32 = 1;
    private static final int VERIFICATION_LEGACY = 2;
    private static final char ESCAPE_VERIFICATION[][] = {
        {' ', '\t', '<', '>', '&', '|', '^'},

        {' ', '\t', '<', '>'},
        {' ', '\t'}
    };

    private static String createCommandLine(int verificationType,
                                     final String executablePath,
                                     final String cmd[])
    {
        StringBuilder cmdbuf = new StringBuilder(80);

        cmdbuf.append(executablePath);

        for (int i = 1; i < cmd.length; ++i) {
            cmdbuf.append(' ');
            String s = cmd[i];
            if (needsEscaping(verificationType, s)) {
                cmdbuf.append('"').append(s);

                if ((verificationType != VERIFICATION_CMD_BAT) && s.endsWith("\\")) {
                    cmdbuf.append('\\');
                }
                cmdbuf.append('"');
            } else {
                cmdbuf.append(s);
            }
        }
        return cmdbuf.toString();
    }

    private static boolean isQuoted(boolean noQuotesInside, String arg,
            String errorMessage) {
        int lastPos = arg.length() - 1;
        if (lastPos >=1 && arg.charAt(0) == '"' && arg.charAt(lastPos) == '"') {
            if (noQuotesInside) {
                if (arg.indexOf('"', 1) != lastPos) {
                    throw new IllegalArgumentException(errorMessage);
                }
            }
            return true;
        }
        if (noQuotesInside) {
            if (arg.indexOf('"') >= 0) {
                throw new IllegalArgumentException(errorMessage);
            }
        }
        return false;
    }

    private static boolean needsEscaping(int verificationType, String arg) {
        boolean argIsQuoted = isQuoted(
            (verificationType == VERIFICATION_CMD_BAT),
            arg, "Argument has embedded quote, use the explicit CMD.EXE call.");

        if (!argIsQuoted) {
            char testEscape[] = ESCAPE_VERIFICATION[verificationType];
            for (int i = 0; i < testEscape.length; ++i) {
                if (arg.indexOf(testEscape[i]) >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getExecutablePath(String path)
        throws IOException
    {
        boolean pathIsQuoted = isQuoted(true, path,
                "Executable name has embedded quote, split the arguments");

        File fileToRun = new File(pathIsQuoted
            ? path.substring(1, path.length() - 1)
            : path);

        return fileToRun.getPath();
    }


    private boolean isShellFile(String executablePath) {
        String upPath = executablePath.toUpperCase();
        return (upPath.endsWith(".CMD") || upPath.endsWith(".BAT"));
    }

    private String quoteString(String arg) {
        StringBuilder argbuf = new StringBuilder(arg.length() + 2);
        return argbuf.append('"').append(arg).append('"').toString();
    }


    private long handle = 0;
    private OutputStream stdin_stream;
    private InputStream stdout_stream;
    private InputStream stderr_stream;

    private ProcessImpl(String cmd[],
                        final String envblock,
                        final String path,
                        final long[] stdHandles,
                        final boolean redirectErrorStream)
        throws IOException
    {
        String cmdstr;
        SecurityManager security = System.getSecurityManager();
        boolean allowAmbiguousCommands = false;
        if (security == null) {
            allowAmbiguousCommands = true;
            String value = System.getProperty("jdk.lang.Process.allowAmbiguousCommands");
            if (value != null)
                allowAmbiguousCommands = !"false".equalsIgnoreCase(value);
        }
        if (allowAmbiguousCommands) {
            String executablePath = new File(cmd[0]).getPath();

            if (needsEscaping(VERIFICATION_LEGACY, executablePath) )
                executablePath = quoteString(executablePath);

            cmdstr = createCommandLine(
                VERIFICATION_LEGACY,
                executablePath,
                cmd);
        } else {
            String executablePath;
            try {
                executablePath = getExecutablePath(cmd[0]);
            } catch (IllegalArgumentException e) {
                StringBuilder join = new StringBuilder();
                for (String s : cmd)
                    join.append(s).append(' ');

                cmd = getTokensFromCommand(join.toString());
                executablePath = getExecutablePath(cmd[0]);

                if (security != null)
                    security.checkExec(executablePath);
            }

            cmdstr = createCommandLine(
                    isShellFile(executablePath)
                        ? VERIFICATION_CMD_BAT
                        : VERIFICATION_WIN32,
                    quoteString(executablePath),
                    cmd);
        }

        handle = create(cmdstr, envblock, path,
                        stdHandles, redirectErrorStream);

        java.security.AccessController.doPrivileged(
        new java.security.PrivilegedAction<Void>() {
        public Void run() {
            if (stdHandles[0] == -1L)
                stdin_stream = ProcessBuilder.NullOutputStream.INSTANCE;
            else {
                FileDescriptor stdin_fd = new FileDescriptor();
                fdAccess.setHandle(stdin_fd, stdHandles[0]);
                stdin_stream = new BufferedOutputStream(
                    new FileOutputStream(stdin_fd));
            }

            if (stdHandles[1] == -1L)
                stdout_stream = ProcessBuilder.NullInputStream.INSTANCE;
            else {
                FileDescriptor stdout_fd = new FileDescriptor();
                fdAccess.setHandle(stdout_fd, stdHandles[1]);
                stdout_stream = new BufferedInputStream(
                    new FileInputStream(stdout_fd));
            }

            if (stdHandles[2] == -1L)
                stderr_stream = ProcessBuilder.NullInputStream.INSTANCE;
            else {
                FileDescriptor stderr_fd = new FileDescriptor();
                fdAccess.setHandle(stderr_fd, stdHandles[2]);
                stderr_stream = new FileInputStream(stderr_fd);
            }

            return null; }});
    }

    public OutputStream getOutputStream() {
        return stdin_stream;
    }

    public InputStream getInputStream() {
        return stdout_stream;
    }

    public InputStream getErrorStream() {
        return stderr_stream;
    }

    protected void finalize() {
        closeHandle(handle);
    }

    private static final int STILL_ACTIVE = getStillActive();
    private static native int getStillActive();

    public int exitValue() {
        int exitCode = getExitCodeProcess(handle);
        if (exitCode == STILL_ACTIVE)
            throw new IllegalThreadStateException("process has not exited");
        return exitCode;
    }
    private static native int getExitCodeProcess(long handle);

    public int waitFor() throws InterruptedException {
        waitForInterruptibly(handle);
        if (Thread.interrupted())
            throw new InterruptedException();
        return exitValue();
    }

    private static native void waitForInterruptibly(long handle);

    @Override
    public boolean waitFor(long timeout, TimeUnit unit)
        throws InterruptedException
    {
        if (getExitCodeProcess(handle) != STILL_ACTIVE) return true;
        if (timeout <= 0) return false;

        long remainingNanos  = unit.toNanos(timeout);
        long deadline = System.nanoTime() + remainingNanos ;

        do {
            long msTimeout = TimeUnit.NANOSECONDS.toMillis(remainingNanos + 999_999L);
            waitForTimeoutInterruptibly(handle, msTimeout);
            if (Thread.interrupted())
                throw new InterruptedException();
            if (getExitCodeProcess(handle) != STILL_ACTIVE) {
                return true;
            }
            remainingNanos = deadline - System.nanoTime();
        } while (remainingNanos > 0);

        return (getExitCodeProcess(handle) != STILL_ACTIVE);
    }

    private static native void waitForTimeoutInterruptibly(
        long handle, long timeout);

    public void destroy() { terminateProcess(handle); }

    @Override
    public Process destroyForcibly() {
        destroy();
        return this;
    }

    private static native void terminateProcess(long handle);

    @Override
    public boolean isAlive() {
        return isProcessAlive(handle);
    }

    private static native boolean isProcessAlive(long handle);


    private static synchronized native long create(String cmdstr,
                                      String envblock,
                                      String dir,
                                      long[] stdHandles,
                                      boolean redirectErrorStream)
        throws IOException;


    private static native long openForAtomicAppend(String path)
        throws IOException;

    private static native boolean closeHandle(long handle);
}
