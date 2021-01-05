

package java.util.zip;

import static java.util.zip.ZipUtils.*;
import java.nio.file.attribute.FileTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.util.zip.ZipConstants64.*;


public
class ZipEntry implements ZipConstants, Cloneable {

    String name;        long xdostime = -1; FileTime mtime;     FileTime atime;     FileTime ctime;     long crc = -1;      long size = -1;     long csize = -1;    int method = -1;    int flag = 0;       byte[] extra;       String comment;     public static final int STORED = 0;


    public static final int DEFLATED = 8;


    static final long DOSTIME_BEFORE_1980 = (1 << 21) | (1 << 16);


    private static final long UPPER_DOSTIME_BOUND =
            128L * 365 * 24 * 60 * 60 * 1000;


    public ZipEntry(String name) {
        Objects.requireNonNull(name, "name");
        if (name.length() > 0xFFFF) {
            throw new IllegalArgumentException("entry name too long");
        }
        this.name = name;
    }


    public ZipEntry(ZipEntry e) {
        Objects.requireNonNull(e, "entry");
        name = e.name;
        xdostime = e.xdostime;
        mtime = e.mtime;
        atime = e.atime;
        ctime = e.ctime;
        crc = e.crc;
        size = e.size;
        csize = e.csize;
        method = e.method;
        flag = e.flag;
        extra = e.extra;
        comment = e.comment;
    }


    ZipEntry() {}


    public String getName() {
        return name;
    }


    public void setTime(long time) {
        this.xdostime = javaToExtendedDosTime(time);
        if (xdostime != DOSTIME_BEFORE_1980 && time <= UPPER_DOSTIME_BOUND) {
            this.mtime = null;
        } else {
            this.mtime = FileTime.from(time, TimeUnit.MILLISECONDS);
        }
    }


    public long getTime() {
        if (mtime != null) {
            return mtime.toMillis();
        }
        return (xdostime != -1) ? extendedDosToJavaTime(xdostime) : -1;
    }


    public ZipEntry setLastModifiedTime(FileTime time) {
        this.mtime = Objects.requireNonNull(time, "lastModifiedTime");
        this.xdostime = javaToExtendedDosTime(time.to(TimeUnit.MILLISECONDS));
        return this;
    }


    public FileTime getLastModifiedTime() {
        if (mtime != null)
            return mtime;
        if (xdostime == -1)
            return null;
        return FileTime.from(getTime(), TimeUnit.MILLISECONDS);
    }


    public ZipEntry setLastAccessTime(FileTime time) {
        this.atime = Objects.requireNonNull(time, "lastAccessTime");
        return this;
    }


    public FileTime getLastAccessTime() {
        return atime;
    }


    public ZipEntry setCreationTime(FileTime time) {
        this.ctime = Objects.requireNonNull(time, "creationTime");
        return this;
    }


    public FileTime getCreationTime() {
        return ctime;
    }


    public void setSize(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("invalid entry size");
        }
        this.size = size;
    }


    public long getSize() {
        return size;
    }


    public long getCompressedSize() {
        return csize;
    }


    public void setCompressedSize(long csize) {
        this.csize = csize;
    }


    public void setCrc(long crc) {
        if (crc < 0 || crc > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("invalid entry crc-32");
        }
        this.crc = crc;
    }


    public long getCrc() {
        return crc;
    }


    public void setMethod(int method) {
        if (method != STORED && method != DEFLATED) {
            throw new IllegalArgumentException("invalid compression method");
        }
        this.method = method;
    }


    public int getMethod() {
        return method;
    }


    public void setExtra(byte[] extra) {
        setExtra0(extra, false);
    }


    void setExtra0(byte[] extra, boolean doZIP64) {
        if (extra != null) {
            if (extra.length > 0xFFFF) {
                throw new IllegalArgumentException("invalid extra field length");
            }
            int off = 0;
            int len = extra.length;
            while (off + 4 < len) {
                int tag = get16(extra, off);
                int sz = get16(extra, off + 2);
                off += 4;
                if (off + sz > len)         break;
                switch (tag) {
                case EXTID_ZIP64:
                    if (doZIP64) {
                        if (sz >= 16) {
                            size = get64(extra, off);
                            csize = get64(extra, off + 8);
                        }
                    }
                    break;
                case EXTID_NTFS:
                    if (sz < 32) break;   int pos = off + 4;               if (get16(extra, pos) !=  0x0001 || get16(extra, pos + 2) != 24)
                        break;
                    mtime = winTimeToFileTime(get64(extra, pos + 4));
                    atime = winTimeToFileTime(get64(extra, pos + 12));
                    ctime = winTimeToFileTime(get64(extra, pos + 20));
                    break;
                case EXTID_EXTT:
                    int flag = Byte.toUnsignedInt(extra[off]);
                    int sz0 = 1;
                    if ((flag & 0x1) != 0 && (sz0 + 4) <= sz) {
                        mtime = unixTimeToFileTime(get32(extra, off + sz0));
                        sz0 += 4;
                    }
                    if ((flag & 0x2) != 0 && (sz0 + 4) <= sz) {
                        atime = unixTimeToFileTime(get32(extra, off + sz0));
                        sz0 += 4;
                    }
                    if ((flag & 0x4) != 0 && (sz0 + 4) <= sz) {
                        ctime = unixTimeToFileTime(get32(extra, off + sz0));
                        sz0 += 4;
                    }
                    break;
                 default:
                }
                off += sz;
            }
        }
        this.extra = extra;
    }


    public byte[] getExtra() {
        return extra;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }


    public String getComment() {
        return comment;
    }


    public boolean isDirectory() {
        return name.endsWith("/");
    }


    public String toString() {
        return getName();
    }


    public int hashCode() {
        return name.hashCode();
    }


    public Object clone() {
        try {
            ZipEntry e = (ZipEntry)super.clone();
            e.extra = (extra == null) ? null : extra.clone();
            return e;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
