

package java.lang;

import java.util.Arrays;

/**
 * StringBuffer是线程安全的，但是效率比StringBuilder低
 * <p>
 * 其实StringBuffer和StringBuilder的区别只有两点
 * 1：里面的方法都加了Synchronized关键字，保证线程安全
 * 2：使用了toStringCache来提高toString的效率
 * <p>
 * fixme
 * 因为StringBuffer本身就比StringBuilder效率低，所以才用缓存来平衡一下
 */
public final class StringBuffer
    extends AbstractStringBuilder
    implements java.io.Serializable, CharSequence {

  /**
   * toStringCache 用来缓存toString()方法返回的最近一次的value数组中的字符。
   * <p>
   * 当修改StringBuffer对象时会被清除。
   * 只要char[]数组发生改变，就会将 toStringCache=null
   * <p>
   * 所以只要toStringCache有效，就可以直接用toStringCache来new String 返回，提升效率
   * note
   * transient关键字说明toStringCache是不会被序列化的
   */
  private transient char[] toStringCache;


  static final long serialVersionUID = 3388685877147921107L;


  public StringBuffer() {
    super(16);
  }


  public StringBuffer(int capacity) {
    super(capacity);
  }


  public StringBuffer(String str) {
    super(str.length() + 16);
    append(str);
  }


  public StringBuffer(CharSequence seq) {
    this(seq.length() + 16);
    append(seq);
  }

  @Override
  public synchronized int length() {
    return count;
  }

  @Override
  public synchronized int capacity() {
    return value.length;
  }


  @Override
  public synchronized void ensureCapacity(int minimumCapacity) {
    super.ensureCapacity(minimumCapacity);
  }


  @Override
  public synchronized void trimToSize() {
    super.trimToSize();
  }


  @Override
  public synchronized void setLength(int newLength) {
    toStringCache = null;
    super.setLength(newLength);
  }


  @Override
  public synchronized char charAt(int index) {
    if ((index < 0) || (index >= count))
      throw new StringIndexOutOfBoundsException(index);
    return value[index];
  }


  @Override
  public synchronized int codePointAt(int index) {
    return super.codePointAt(index);
  }


  @Override
  public synchronized int codePointBefore(int index) {
    return super.codePointBefore(index);
  }


  @Override
  public synchronized int codePointCount(int beginIndex, int endIndex) {
    return super.codePointCount(beginIndex, endIndex);
  }


  @Override
  public synchronized int offsetByCodePoints(int index, int codePointOffset) {
    return super.offsetByCodePoints(index, codePointOffset);
  }


  @Override
  public synchronized void getChars(int srcBegin, int srcEnd, char[] dst,
                                    int dstBegin) {
    super.getChars(srcBegin, srcEnd, dst, dstBegin);
  }


  @Override
  public synchronized void setCharAt(int index, char ch) {
    if ((index < 0) || (index >= count))
      throw new StringIndexOutOfBoundsException(index);
    toStringCache = null;
    value[index] = ch;
  }

  @Override
  public synchronized StringBuffer append(Object obj) {
    toStringCache = null;
    super.append(String.valueOf(obj));
    return this;
  }

  @Override
  public synchronized StringBuffer append(String str) {
    toStringCache = null;
    super.append(str);
    return this;
  }


  public synchronized StringBuffer append(StringBuffer sb) {
    toStringCache = null;
    super.append(sb);
    return this;
  }


  @Override
  synchronized StringBuffer append(AbstractStringBuilder asb) {
    toStringCache = null;
    super.append(asb);
    return this;
  }


  @Override
  public synchronized StringBuffer append(CharSequence s) {
    toStringCache = null;
    super.append(s);
    return this;
  }


  @Override
  public synchronized StringBuffer append(CharSequence s, int start, int end) {
    toStringCache = null;
    super.append(s, start, end);
    return this;
  }

  @Override
  public synchronized StringBuffer append(char[] str) {
    toStringCache = null;
    super.append(str);
    return this;
  }


  @Override
  public synchronized StringBuffer append(char[] str, int offset, int len) {
    toStringCache = null;
    super.append(str, offset, len);
    return this;
  }

  @Override
  public synchronized StringBuffer append(boolean b) {
    toStringCache = null;
    super.append(b);
    return this;
  }

  @Override
  public synchronized StringBuffer append(char c) {
    toStringCache = null;
    super.append(c);
    return this;
  }

  @Override
  public synchronized StringBuffer append(int i) {
    toStringCache = null;
    super.append(i);
    return this;
  }


  @Override
  public synchronized StringBuffer appendCodePoint(int codePoint) {
    toStringCache = null;
    super.appendCodePoint(codePoint);
    return this;
  }

  @Override
  public synchronized StringBuffer append(long lng) {
    toStringCache = null;
    super.append(lng);
    return this;
  }

  @Override
  public synchronized StringBuffer append(float f) {
    toStringCache = null;
    super.append(f);
    return this;
  }

  @Override
  public synchronized StringBuffer append(double d) {
    toStringCache = null;
    super.append(d);
    return this;
  }


  @Override
  public synchronized StringBuffer delete(int start, int end) {
    toStringCache = null;
    super.delete(start, end);
    return this;
  }


  @Override
  public synchronized StringBuffer deleteCharAt(int index) {
    toStringCache = null;
    super.deleteCharAt(index);
    return this;
  }


  @Override
  public synchronized StringBuffer replace(int start, int end, String str) {
    toStringCache = null;
    super.replace(start, end, str);
    return this;
  }


  @Override
  public synchronized String substring(int start) {
    return substring(start, count);
  }


  @Override
  public synchronized CharSequence subSequence(int start, int end) {
    return super.substring(start, end);
  }


  @Override
  public synchronized String substring(int start, int end) {
    return super.substring(start, end);
  }


  @Override
  public synchronized StringBuffer insert(int index, char[] str, int offset,
                                          int len) {
    toStringCache = null;
    super.insert(index, str, offset, len);
    return this;
  }


  @Override
  public synchronized StringBuffer insert(int offset, Object obj) {
    toStringCache = null;
    super.insert(offset, String.valueOf(obj));
    return this;
  }


  @Override
  public synchronized StringBuffer insert(int offset, String str) {
    toStringCache = null;
    super.insert(offset, str);
    return this;
  }


  @Override
  public synchronized StringBuffer insert(int offset, char[] str) {
    toStringCache = null;
    super.insert(offset, str);
    return this;
  }


  @Override
  public StringBuffer insert(int dstOffset, CharSequence s) {
    super.insert(dstOffset, s);
    return this;
  }


  @Override
  public synchronized StringBuffer insert(int dstOffset, CharSequence s,
                                          int start, int end) {
    toStringCache = null;
    super.insert(dstOffset, s, start, end);
    return this;
  }


  @Override
  public StringBuffer insert(int offset, boolean b) {
    super.insert(offset, b);
    return this;
  }


  @Override
  public synchronized StringBuffer insert(int offset, char c) {
    toStringCache = null;
    super.insert(offset, c);
    return this;
  }


  @Override
  public StringBuffer insert(int offset, int i) {
    super.insert(offset, i);
    return this;
  }


  @Override
  public StringBuffer insert(int offset, long l) {
    super.insert(offset, l);
    return this;
  }


  @Override
  public StringBuffer insert(int offset, float f) {
    super.insert(offset, f);
    return this;
  }


  @Override
  public StringBuffer insert(int offset, double d) {
    super.insert(offset, d);
    return this;
  }


  @Override
  public int indexOf(String str) {
    return super.indexOf(str);
  }


  @Override
  public synchronized int indexOf(String str, int fromIndex) {
    return super.indexOf(str, fromIndex);
  }


  @Override
  public int lastIndexOf(String str) {
    return lastIndexOf(str, count);
  }


  @Override
  public synchronized int lastIndexOf(String str, int fromIndex) {
    return super.lastIndexOf(str, fromIndex);
  }


  @Override
  public synchronized StringBuffer reverse() {
    toStringCache = null;
    super.reverse();
    return this;
  }

  /**
   * 如果toStringCache==null，说明char[]改变后还没调用过toString()方法
   * 那么先将char[]内容拷贝到toStringCache中缓存起来
   * <p>
   * 通过缓存可以提高效率
   * 因为不用调用System.arrayCopy拷贝数组，只需要改变String中char[]的引用
   *
   * @return
   */
  @Override
  public synchronized String toString() {
    if (toStringCache == null) {
      toStringCache = Arrays.copyOfRange(value, 0, count);
    }
    return new String(toStringCache, true);
  }


  private static final java.io.ObjectStreamField[] serialPersistentFields =
      {
          new java.io.ObjectStreamField("value", char[].class),
          new java.io.ObjectStreamField("count", Integer.TYPE),
          new java.io.ObjectStreamField("shared", Boolean.TYPE),
      };


  private synchronized void writeObject(java.io.ObjectOutputStream s)
      throws java.io.IOException {
    java.io.ObjectOutputStream.PutField fields = s.putFields();
    fields.put("value", value);
    fields.put("count", count);
    fields.put("shared", false);
    s.writeFields();
  }


  private void readObject(java.io.ObjectInputStream s)
      throws java.io.IOException, ClassNotFoundException {
    java.io.ObjectInputStream.GetField fields = s.readFields();
    value = (char[]) fields.get("value", null);
    count = fields.get("count", 0);
  }
}
