

package java.nio;












public abstract class DoubleBuffer
    extends Buffer
    implements Comparable<DoubleBuffer>
{

    final double[] hb;                  final int offset;
    boolean isReadOnly;                 DoubleBuffer(int mark, int pos, int lim, int cap,   double[] hb, int offset)
    {
        super(mark, pos, lim, cap);
        this.hb = hb;
        this.offset = offset;
    }

    DoubleBuffer(int mark, int pos, int lim, int cap) { this(mark, pos, lim, cap, null, 0);
    }


























    public static DoubleBuffer allocate(int capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException();
        return new HeapDoubleBuffer(capacity, capacity);
    }


    public static DoubleBuffer wrap(double[] array,
                                    int offset, int length)
    {
        try {
            return new HeapDoubleBuffer(array, offset, length);
        } catch (IllegalArgumentException x) {
            throw new IndexOutOfBoundsException();
        }
    }


    public static DoubleBuffer wrap(double[] array) {
        return wrap(array, 0, array.length);
    }































































































    public abstract DoubleBuffer slice();


    public abstract DoubleBuffer duplicate();


    public abstract DoubleBuffer asReadOnlyBuffer();


    public abstract double get();


    public abstract DoubleBuffer put(double d);


    public abstract double get(int index);















    public abstract DoubleBuffer put(int index, double d);


    public DoubleBuffer get(double[] dst, int offset, int length) {
        checkBounds(offset, length, dst.length);
        if (length > remaining())
            throw new BufferUnderflowException();
        int end = offset + length;
        for (int i = offset; i < end; i++)
            dst[i] = get();
        return this;
    }


    public DoubleBuffer get(double[] dst) {
        return get(dst, 0, dst.length);
    }


    public DoubleBuffer put(DoubleBuffer src) {
        if (src == this)
            throw new IllegalArgumentException();
        if (isReadOnly())
            throw new ReadOnlyBufferException();
        int n = src.remaining();
        if (n > remaining())
            throw new BufferOverflowException();
        for (int i = 0; i < n; i++)
            put(src.get());
        return this;
    }


    public DoubleBuffer put(double[] src, int offset, int length) {
        checkBounds(offset, length, src.length);
        if (length > remaining())
            throw new BufferOverflowException();
        int end = offset + length;
        for (int i = offset; i < end; i++)
            this.put(src[i]);
        return this;
    }


    public final DoubleBuffer put(double[] src) {
        return put(src, 0, src.length);
    }































































































    public final boolean hasArray() {
        return (hb != null) && !isReadOnly;
    }


    public final double[] array() {
        if (hb == null)
            throw new UnsupportedOperationException();
        if (isReadOnly)
            throw new ReadOnlyBufferException();
        return hb;
    }


    public final int arrayOffset() {
        if (hb == null)
            throw new UnsupportedOperationException();
        if (isReadOnly)
            throw new ReadOnlyBufferException();
        return offset;
    }


    public abstract DoubleBuffer compact();


    public abstract boolean isDirect();




    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append("[pos=");
        sb.append(position());
        sb.append(" lim=");
        sb.append(limit());
        sb.append(" cap=");
        sb.append(capacity());
        sb.append("]");
        return sb.toString();
    }







    public int hashCode() {
        int h = 1;
        int p = position();
        for (int i = limit() - 1; i >= p; i--)



            h = 31 * h + (int)get(i);

        return h;
    }


    public boolean equals(Object ob) {
        if (this == ob)
            return true;
        if (!(ob instanceof DoubleBuffer))
            return false;
        DoubleBuffer that = (DoubleBuffer)ob;
        if (this.remaining() != that.remaining())
            return false;
        int p = this.position();
        for (int i = this.limit() - 1, j = that.limit() - 1; i >= p; i--, j--)
            if (!equals(this.get(i), that.get(j)))
                return false;
        return true;
    }

    private static boolean equals(double x, double y) {

        return (x == y) || (Double.isNaN(x) && Double.isNaN(y));



    }


    public int compareTo(DoubleBuffer that) {
        int n = this.position() + Math.min(this.remaining(), that.remaining());
        for (int i = this.position(), j = that.position(); i < n; i++, j++) {
            int cmp = compare(this.get(i), that.get(j));
            if (cmp != 0)
                return cmp;
        }
        return this.remaining() - that.remaining();
    }

    private static int compare(double x, double y) {

        return ((x < y)  ? -1 :
                (x > y)  ? +1 :
                (x == y) ?  0 :
                Double.isNaN(x) ? (Double.isNaN(y) ? 0 : +1) : -1);



    }

    public abstract ByteOrder order();

































































}
