

package java.util.zip;

import java.io.OutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Vector;
import java.util.HashSet;
import static java.util.zip.ZipConstants64.*;
import static java.util.zip.ZipUtils.*;


public
class ZipOutputStream extends DeflaterOutputStream implements ZipConstants {


    private static final boolean inhibitZip64 =
        Boolean.parseBoolean(
            java.security.AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction(
                    "jdk.util.zip.inhibitZip64", "false")));

    private static class XEntry {
        final ZipEntry entry;
        final long offset;
        public XEntry(ZipEntry entry, long offset) {
            this.entry = entry;
            this.offset = offset;
        }
    }

    private XEntry current;
    private Vector<XEntry> xentries = new Vector<>();
    private HashSet<String> names = new HashSet<>();
    private CRC32 crc = new CRC32();
    private long written = 0;
    private long locoff = 0;
    private byte[] comment;
    private int method = DEFLATED;
    private boolean finished;

    private boolean closed = false;

    private final ZipCoder zc;

    private static int version(ZipEntry e) throws ZipException {
        switch (e.method) {
        case DEFLATED: return 20;
        case STORED:   return 10;
        default: throw new ZipException("unsupported compression method");
        }
    }


    private void ensureOpen() throws IOException {
        if (closed) {
            throw new IOException("Stream closed");
        }
    }

    public static final int STORED = ZipEntry.STORED;


    public static final int DEFLATED = ZipEntry.DEFLATED;


    public ZipOutputStream(OutputStream out) {
        this(out, StandardCharsets.UTF_8);
    }


    public ZipOutputStream(OutputStream out, Charset charset) {
        super(out, new Deflater(Deflater.DEFAULT_COMPRESSION, true));
        if (charset == null)
            throw new NullPointerException("charset is null");
        this.zc = ZipCoder.get(charset);
        usesDefaultDeflater = true;
    }


    public void setComment(String comment) {
        if (comment != null) {
            this.comment = zc.getBytes(comment);
            if (this.comment.length > 0xffff)
                throw new IllegalArgumentException("ZIP file comment too long.");
        }
    }


    public void setMethod(int method) {
        if (method != DEFLATED && method != STORED) {
            throw new IllegalArgumentException("invalid compression method");
        }
        this.method = method;
    }


    public void setLevel(int level) {
        def.setLevel(level);
    }


    public void putNextEntry(ZipEntry e) throws IOException {
        ensureOpen();
        if (current != null) {
            closeEntry();       }
        if (e.xdostime == -1) {
            e.setTime(System.currentTimeMillis());
        }
        if (e.method == -1) {
            e.method = method;  }
        e.flag = 0;
        switch (e.method) {
        case DEFLATED:
            if (e.size  == -1 || e.csize == -1 || e.crc   == -1)
                e.flag = 8;

            break;
        case STORED:
            if (e.size == -1) {
                e.size = e.csize;
            } else if (e.csize == -1) {
                e.csize = e.size;
            } else if (e.size != e.csize) {
                throw new ZipException(
                    "STORED entry where compressed != uncompressed size");
            }
            if (e.size == -1 || e.crc == -1) {
                throw new ZipException(
                    "STORED entry missing size, compressed size, or crc-32");
            }
            break;
        default:
            throw new ZipException("unsupported compression method");
        }
        if (! names.add(e.name)) {
            throw new ZipException("duplicate entry: " + e.name);
        }
        if (zc.isUTF8())
            e.flag |= EFS;
        current = new XEntry(e, written);
        xentries.add(current);
        writeLOC(current);
    }


    public void closeEntry() throws IOException {
        ensureOpen();
        if (current != null) {
            ZipEntry e = current.entry;
            switch (e.method) {
            case DEFLATED:
                def.finish();
                while (!def.finished()) {
                    deflate();
                }
                if ((e.flag & 8) == 0) {
                    if (e.size != def.getBytesRead()) {
                        throw new ZipException(
                            "invalid entry size (expected " + e.size +
                            " but got " + def.getBytesRead() + " bytes)");
                    }
                    if (e.csize != def.getBytesWritten()) {
                        throw new ZipException(
                            "invalid entry compressed size (expected " +
                            e.csize + " but got " + def.getBytesWritten() + " bytes)");
                    }
                    if (e.crc != crc.getValue()) {
                        throw new ZipException(
                            "invalid entry CRC-32 (expected 0x" +
                            Long.toHexString(e.crc) + " but got 0x" +
                            Long.toHexString(crc.getValue()) + ")");
                    }
                } else {
                    e.size  = def.getBytesRead();
                    e.csize = def.getBytesWritten();
                    e.crc = crc.getValue();
                    writeEXT(e);
                }
                def.reset();
                written += e.csize;
                break;
            case STORED:
                if (e.size != written - locoff) {
                    throw new ZipException(
                        "invalid entry size (expected " + e.size +
                        " but got " + (written - locoff) + " bytes)");
                }
                if (e.crc != crc.getValue()) {
                    throw new ZipException(
                         "invalid entry crc-32 (expected 0x" +
                         Long.toHexString(e.crc) + " but got 0x" +
                         Long.toHexString(crc.getValue()) + ")");
                }
                break;
            default:
                throw new ZipException("invalid compression method");
            }
            crc.reset();
            current = null;
        }
    }


    public synchronized void write(byte[] b, int off, int len)
        throws IOException
    {
        ensureOpen();
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }

        if (current == null) {
            throw new ZipException("no current ZIP entry");
        }
        ZipEntry entry = current.entry;
        switch (entry.method) {
        case DEFLATED:
            super.write(b, off, len);
            break;
        case STORED:
            written += len;
            if (written - locoff > entry.size) {
                throw new ZipException(
                    "attempt to write past end of STORED entry");
            }
            out.write(b, off, len);
            break;
        default:
            throw new ZipException("invalid compression method");
        }
        crc.update(b, off, len);
    }


    public void finish() throws IOException {
        ensureOpen();
        if (finished) {
            return;
        }
        if (current != null) {
            closeEntry();
        }
        long off = written;
        for (XEntry xentry : xentries)
            writeCEN(xentry);
        writeEND(off, written - off);
        finished = true;
    }


    public void close() throws IOException {
        if (!closed) {
            super.close();
            closed = true;
        }
    }


    private void writeLOC(XEntry xentry) throws IOException {
        ZipEntry e = xentry.entry;
        int flag = e.flag;
        boolean hasZip64 = false;
        int elen = getExtraLen(e.extra);

        writeInt(LOCSIG);               if ((flag & 8) == 8) {
            writeShort(version(e));     writeShort(flag);           writeShort(e.method);       writeInt(e.xdostime);       writeInt(0);
            writeInt(0);
            writeInt(0);
        } else {
            if (e.csize >= ZIP64_MAGICVAL || e.size >= ZIP64_MAGICVAL) {
                hasZip64 = true;
                writeShort(45);         } else {
                writeShort(version(e)); }
            writeShort(flag);           writeShort(e.method);       writeInt(e.xdostime);       writeInt(e.crc);            if (hasZip64) {
                writeInt(ZIP64_MAGICVAL);
                writeInt(ZIP64_MAGICVAL);
                elen += 20;        } else {
                writeInt(e.csize);  writeInt(e.size);   }
        }
        byte[] nameBytes = zc.getBytes(e.name);
        writeShort(nameBytes.length);

        int elenEXTT = 0;               int flagEXTT = 0;
        if (e.mtime != null) {
            elenEXTT += 4;
            flagEXTT |= EXTT_FLAG_LMT;
        }
        if (e.atime != null) {
            elenEXTT += 4;
            flagEXTT |= EXTT_FLAG_LAT;
        }
        if (e.ctime != null) {
            elenEXTT += 4;
            flagEXTT |= EXTT_FLAT_CT;
        }
        if (flagEXTT != 0)
            elen += (elenEXTT + 5);    writeShort(elen);
        writeBytes(nameBytes, 0, nameBytes.length);
        if (hasZip64) {
            writeShort(ZIP64_EXTID);
            writeShort(16);
            writeLong(e.size);
            writeLong(e.csize);
        }
        if (flagEXTT != 0) {
            writeShort(EXTID_EXTT);
            writeShort(elenEXTT + 1);      writeByte(flagEXTT);
            if (e.mtime != null)
                writeInt(fileTimeToUnixTime(e.mtime));
            if (e.atime != null)
                writeInt(fileTimeToUnixTime(e.atime));
            if (e.ctime != null)
                writeInt(fileTimeToUnixTime(e.ctime));
        }
        writeExtra(e.extra);
        locoff = written;
    }


    private void writeEXT(ZipEntry e) throws IOException {
        writeInt(EXTSIG);           writeInt(e.crc);            if (e.csize >= ZIP64_MAGICVAL || e.size >= ZIP64_MAGICVAL) {
            writeLong(e.csize);
            writeLong(e.size);
        } else {
            writeInt(e.csize);          writeInt(e.size);           }
    }


    private void writeCEN(XEntry xentry) throws IOException {
        ZipEntry e  = xentry.entry;
        int flag = e.flag;
        int version = version(e);
        long csize = e.csize;
        long size = e.size;
        long offset = xentry.offset;
        int elenZIP64 = 0;
        boolean hasZip64 = false;

        if (e.csize >= ZIP64_MAGICVAL) {
            csize = ZIP64_MAGICVAL;
            elenZIP64 += 8;              hasZip64 = true;
        }
        if (e.size >= ZIP64_MAGICVAL) {
            size = ZIP64_MAGICVAL;    elenZIP64 += 8;
            hasZip64 = true;
        }
        if (xentry.offset >= ZIP64_MAGICVAL) {
            offset = ZIP64_MAGICVAL;
            elenZIP64 += 8;              hasZip64 = true;
        }
        writeInt(CENSIG);           if (hasZip64) {
            writeShort(45);         writeShort(45);
        } else {
            writeShort(version);    writeShort(version);    }
        writeShort(flag);           writeShort(e.method);       writeInt(e.xdostime);       writeInt(e.crc);            writeInt(csize);            writeInt(size);             byte[] nameBytes = zc.getBytes(e.name);
        writeShort(nameBytes.length);

        int elen = getExtraLen(e.extra);
        if (hasZip64) {
            elen += (elenZIP64 + 4);}
        int flagEXTT = 0;
        if (e.mtime != null) {
            elen += 4;              flagEXTT |= EXTT_FLAG_LMT;
        }
        if (e.atime != null) {
            flagEXTT |= EXTT_FLAG_LAT;
        }
        if (e.ctime != null) {
            flagEXTT |= EXTT_FLAT_CT;
        }
        if (flagEXTT != 0) {
            elen += 5;             }
        writeShort(elen);
        byte[] commentBytes;
        if (e.comment != null) {
            commentBytes = zc.getBytes(e.comment);
            writeShort(Math.min(commentBytes.length, 0xffff));
        } else {
            commentBytes = null;
            writeShort(0);
        }
        writeShort(0);              writeShort(0);              writeInt(0);                writeInt(offset);           writeBytes(nameBytes, 0, nameBytes.length);

        if (hasZip64) {
            writeShort(ZIP64_EXTID);writeShort(elenZIP64);
            if (size == ZIP64_MAGICVAL)
                writeLong(e.size);
            if (csize == ZIP64_MAGICVAL)
                writeLong(e.csize);
            if (offset == ZIP64_MAGICVAL)
                writeLong(xentry.offset);
        }
        if (flagEXTT != 0) {
            writeShort(EXTID_EXTT);
            if (e.mtime != null) {
                writeShort(5);      writeByte(flagEXTT);
                writeInt(fileTimeToUnixTime(e.mtime));
            } else {
                writeShort(1);      writeByte(flagEXTT);
            }
        }
        writeExtra(e.extra);
        if (commentBytes != null) {
            writeBytes(commentBytes, 0, Math.min(commentBytes.length, 0xffff));
        }
    }


    private void writeEND(long off, long len) throws IOException {
        boolean hasZip64 = false;
        long xlen = len;
        long xoff = off;
        if (xlen >= ZIP64_MAGICVAL) {
            xlen = ZIP64_MAGICVAL;
            hasZip64 = true;
        }
        if (xoff >= ZIP64_MAGICVAL) {
            xoff = ZIP64_MAGICVAL;
            hasZip64 = true;
        }
        int count = xentries.size();
        if (count >= ZIP64_MAGICCOUNT) {
            hasZip64 |= !inhibitZip64;
            if (hasZip64) {
                count = ZIP64_MAGICCOUNT;
            }
        }
        if (hasZip64) {
            long off64 = written;
            writeInt(ZIP64_ENDSIG);        writeLong(ZIP64_ENDHDR - 12);  writeShort(45);                writeShort(45);                writeInt(0);                   writeInt(0);                   writeLong(xentries.size());    writeLong(xentries.size());    writeLong(len);                writeLong(off);                writeInt(ZIP64_LOCSIG);        writeInt(0);                   writeLong(off64);              writeInt(1);                   }
        writeInt(ENDSIG);                 writeShort(0);                    writeShort(0);                    writeShort(count);                writeShort(count);                writeInt(xlen);                   writeInt(xoff);                   if (comment != null) {            writeShort(comment.length);
            writeBytes(comment, 0, comment.length);
        } else {
            writeShort(0);
        }
    }


    private int getExtraLen(byte[] extra) {
        if (extra == null)
            return 0;
        int skipped = 0;
        int len = extra.length;
        int off = 0;
        while (off + 4 <= len) {
            int tag = get16(extra, off);
            int sz = get16(extra, off + 2);
            if (sz < 0 || (off + 4 + sz) > len) {
                break;
            }
            if (tag == EXTID_EXTT || tag == EXTID_ZIP64) {
                skipped += (sz + 4);
            }
            off += (sz + 4);
        }
        return len - skipped;
    }


    private void writeExtra(byte[] extra) throws IOException {
        if (extra != null) {
            int len = extra.length;
            int off = 0;
            while (off + 4 <= len) {
                int tag = get16(extra, off);
                int sz = get16(extra, off + 2);
                if (sz < 0 || (off + 4 + sz) > len) {
                    writeBytes(extra, off, len - off);
                    return;
                }
                if (tag != EXTID_EXTT && tag != EXTID_ZIP64) {
                    writeBytes(extra, off, sz + 4);
                }
                off += (sz + 4);
            }
            if (off < len) {
                writeBytes(extra, off, len - off);
            }
        }
    }


    private void writeByte(int v) throws IOException {
        OutputStream out = this.out;
        out.write(v & 0xff);
        written += 1;
    }


    private void writeShort(int v) throws IOException {
        OutputStream out = this.out;
        out.write((v >>> 0) & 0xff);
        out.write((v >>> 8) & 0xff);
        written += 2;
    }


    private void writeInt(long v) throws IOException {
        OutputStream out = this.out;
        out.write((int)((v >>>  0) & 0xff));
        out.write((int)((v >>>  8) & 0xff));
        out.write((int)((v >>> 16) & 0xff));
        out.write((int)((v >>> 24) & 0xff));
        written += 4;
    }


    private void writeLong(long v) throws IOException {
        OutputStream out = this.out;
        out.write((int)((v >>>  0) & 0xff));
        out.write((int)((v >>>  8) & 0xff));
        out.write((int)((v >>> 16) & 0xff));
        out.write((int)((v >>> 24) & 0xff));
        out.write((int)((v >>> 32) & 0xff));
        out.write((int)((v >>> 40) & 0xff));
        out.write((int)((v >>> 48) & 0xff));
        out.write((int)((v >>> 56) & 0xff));
        written += 8;
    }


    private void writeBytes(byte[] b, int off, int len) throws IOException {
        super.out.write(b, off, len);
        written += len;
    }
}
