

package java.lang;

import java.io.ObjectStreamField;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * final修饰类
 * 1：String类不能被继承
 * 2：成员方法默认都为final
 * <p>
 * note
 * 其实可以通过反射的方式来修改String
 * 通过反射可以获取到 char[] value，
 * 既可以改变value的指向，又可以改变value指向内存中char的值了
 */
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence {

  /**
   * char value[]  means char[] value
   * final修饰符 意味着 value指向的地址不可变
   * 但地址那块内存存放的数据可变
   */
  private final char value[];

  // 存放hashcode
  private int hash;

  // 序列化ID
  private static final long serialVersionUID = -6849794470754667710L;

  /**
   * 主要用来提取序列化过程中某个对象内的字段【成员属性】元数据信息，
   * 包括字段的类型、类型代码、签名等
   * <p>
   * serialPersistentFields属性用于指定哪些字段需要被默认序列化，具体用法为：
   * <p>
   * private static final ObjectStreamField[] serialPersistentFields = {
   * new ObjectStreamField("name", String.class),
   * new ObjectStreamField("age", Integer.Type)
   * }
   * <p>
   * transient用于指定哪些字段不会被默认序列化，两者同时使用时，transient会被忽略。
   */
  private static final ObjectStreamField[] serialPersistentFields =
      new ObjectStreamField[0];

  /**
   * 无参构造函数
   * 完全没必要使用此构造器来创建一个String对象，
   * 因为String自身已经被设计为不可变
   */
  public String() {
    this.value = "".value;
  }

  /**
   * String构造
   *
   * @param original
   */
  public String(String original) {
    this.value = original.value;
    this.hash = original.hash;
  }

  /**
   * char[] 构造
   */
  public String(char value[]) {
    this.value = Arrays.copyOf(value, value.length);
  }

  /**
   * char[]的一部分来构造
   * offset:开始位置
   * count：截取长度
   */
  public String(char value[], int offset, int count) {
    if (offset < 0) {
      throw new StringIndexOutOfBoundsException(offset);
    }
    if (count <= 0) {
      if (count < 0) {
        throw new StringIndexOutOfBoundsException(count);
      }
      if (offset <= value.length) {
        this.value = "".value;
        return;
      }
    }
    if (offset > value.length - count) {
      throw new StringIndexOutOfBoundsException(offset + count);
    }
    this.value = Arrays.copyOfRange(value, offset, offset + count);
  }

  public boolean equals(Object anObject) {
    if (this == anObject) {
      return true;
    }
    if (anObject instanceof String) {
      String anotherString = (String) anObject;
      int n = value.length;
      if (n == anotherString.value.length) {
        char v1[] = value;
        char v2[] = anotherString.value;
        int i = 0;
        while (n-- != 0) {
          if (v1[i] != v2[i])
            return false;
          i++;
        }
        return true;
      }
    }
    return false;
  }


  public int compareTo(String anotherString) {
    int len1 = value.length;
    int len2 = anotherString.value.length;
    int lim = Math.min(len1, len2);
    char v1[] = value;
    char v2[] = anotherString.value;

    int k = 0;
    while (k < lim) {
      char c1 = v1[k];
      char c2 = v2[k];
      if (c1 != c2) {
        return c1 - c2;
      }
      k++;
    }
    return len1 - len2;
  }

  public int hashCode() {
    int h = hash;
    if (h == 0 && value.length > 0) {
      char val[] = value;

      for (int i = 0; i < value.length; i++) {
        h = 31 * h + val[i];
      }
      hash = h;
    }
    return h;
  }

  public boolean startsWith(String prefix, int toffset) {
    char ta[] = value;
    int to = toffset;
    char pa[] = prefix.value;
    int po = 0;
    int pc = prefix.value.length;
    if ((toffset < 0) || (toffset > value.length - pc)) {
      return false;
    }
    while (--pc >= 0) {
      if (ta[to++] != pa[po++]) {
        return false;
      }
    }
    return true;
  }

  public String concat(String str) {
    int otherLen = str.length();
    if (otherLen == 0) {
      return this;
    }
    int len = value.length;
    char buf[] = Arrays.copyOf(value, len + otherLen);
    str.getChars(buf, len);
    return new String(buf, true);
  }

  public String replace(char oldChar, char newChar) {
    if (oldChar != newChar) {
      int len = value.length;
      int i = -1;
      char[] val = value;

      while (++i < len) {
        if (val[i] == oldChar) {
          break;
        }
      }
      if (i < len) {
        char buf[] = new char[len];
        for (int j = 0; j < i; j++) {
          buf[j] = val[j];
        }
        while (i < len) {
          char c = val[i];
          buf[i] = (c == oldChar) ? newChar : c;
          i++;
        }
        return new String(buf, true);
      }
    }
    return this;
  }

  public String trim() {
    int len = value.length;
    int st = 0;
    char[] val = value;

    while ((st < len) && (val[st] <= ' ')) {
      st++;
    }
    while ((st < len) && (val[len - 1] <= ' ')) {
      len--;
    }
    return ((st > 0) || (len < value.length)) ? substring(st, len) : this;
  }

  public int length() {
    return value.length;
  }


  public boolean isEmpty() {
    return value.length == 0;
  }


  public char charAt(int index) {
    if ((index < 0) || (index >= value.length)) {
      throw new StringIndexOutOfBoundsException(index);
    }
    return value[index];
  }

  public byte[] getBytes(String charsetName)
      throws UnsupportedEncodingException {
    if (charsetName == null) throw new NullPointerException();
    return StringCoding.encode(charsetName, value, 0, value.length);
  }


  public String[] split(String regex) {
    return split(regex, 0);
  }


  public static String join(CharSequence delimiter, CharSequence... elements) {
    Objects.requireNonNull(delimiter);
    Objects.requireNonNull(elements);
    StringJoiner joiner = new StringJoiner(delimiter);
    for (CharSequence cs : elements) {
      joiner.add(cs);
    }
    return joiner.toString();
  }


  public String substring(int beginIndex, int endIndex) {
    if (beginIndex < 0) {
      throw new StringIndexOutOfBoundsException(beginIndex);
    }
    if (endIndex > value.length) {
      throw new StringIndexOutOfBoundsException(endIndex);
    }
    int subLen = endIndex - beginIndex;
    if (subLen < 0) {
      throw new StringIndexOutOfBoundsException(subLen);
    }
    return ((beginIndex == 0) && (endIndex == value.length)) ? this
        : new String(value, beginIndex, subLen);
  }

  public String toString() {
    return this;
  }


  public native String intern();


  public String(int[] codePoints, int offset, int count) {
    if (offset < 0) {
      throw new StringIndexOutOfBoundsException(offset);
    }
    if (count <= 0) {
      if (count < 0) {
        throw new StringIndexOutOfBoundsException(count);
      }
      if (offset <= codePoints.length) {
        this.value = "".value;
        return;
      }
    }
    if (offset > codePoints.length - count) {
      throw new StringIndexOutOfBoundsException(offset + count);
    }

    final int end = offset + count;

    int n = count;
    for (int i = offset; i < end; i++) {
      int c = codePoints[i];
      if (Character.isBmpCodePoint(c))
        continue;
      else if (Character.isValidCodePoint(c))
        n++;
      else throw new IllegalArgumentException(Integer.toString(c));
    }

    final char[] v = new char[n];

    for (int i = offset, j = 0; i < end; i++, j++) {
      int c = codePoints[i];
      if (Character.isBmpCodePoint(c))
        v[j] = (char) c;
      else
        Character.toSurrogates(c, v, j++);
    }

    this.value = v;
  }


  @Deprecated
  public String(byte ascii[], int hibyte, int offset, int count) {
    checkBounds(ascii, offset, count);
    char value[] = new char[count];

    if (hibyte == 0) {
      for (int i = count; i-- > 0; ) {
        value[i] = (char) (ascii[i + offset] & 0xff);
      }
    } else {
      hibyte <<= 8;
      for (int i = count; i-- > 0; ) {
        value[i] = (char) (hibyte | (ascii[i + offset] & 0xff));
      }
    }
    this.value = value;
  }


  @Deprecated
  public String(byte ascii[], int hibyte) {
    this(ascii, hibyte, 0, ascii.length);
  }


  private static void checkBounds(byte[] bytes, int offset, int length) {
    if (length < 0)
      throw new StringIndexOutOfBoundsException(length);
    if (offset < 0)
      throw new StringIndexOutOfBoundsException(offset);
    if (offset > bytes.length - length)
      throw new StringIndexOutOfBoundsException(offset + length);
  }


  public String(byte bytes[], int offset, int length, String charsetName)
      throws UnsupportedEncodingException {
    if (charsetName == null)
      throw new NullPointerException("charsetName");
    checkBounds(bytes, offset, length);
    this.value = StringCoding.decode(charsetName, bytes, offset, length);
  }


  public String(byte bytes[], int offset, int length, Charset charset) {
    if (charset == null)
      throw new NullPointerException("charset");
    checkBounds(bytes, offset, length);
    this.value = StringCoding.decode(charset, bytes, offset, length);
  }


  public String(byte bytes[], String charsetName)
      throws UnsupportedEncodingException {
    this(bytes, 0, bytes.length, charsetName);
  }


  public String(byte bytes[], Charset charset) {
    this(bytes, 0, bytes.length, charset);
  }


  public String(byte bytes[], int offset, int length) {
    checkBounds(bytes, offset, length);
    this.value = StringCoding.decode(bytes, offset, length);
  }


  public String(byte bytes[]) {
    this(bytes, 0, bytes.length);
  }


  public String(StringBuffer buffer) {
    synchronized (buffer) {
      this.value = Arrays.copyOf(buffer.getValue(), buffer.length());
    }
  }


  public String(StringBuilder builder) {
    this.value = Arrays.copyOf(builder.getValue(), builder.length());
  }


  String(char[] value, boolean share) {
    this.value = value;
  }


  public int codePointAt(int index) {
    if ((index < 0) || (index >= value.length)) {
      throw new StringIndexOutOfBoundsException(index);
    }
    return Character.codePointAtImpl(value, index, value.length);
  }


  public int codePointBefore(int index) {
    int i = index - 1;
    if ((i < 0) || (i >= value.length)) {
      throw new StringIndexOutOfBoundsException(index);
    }
    return Character.codePointBeforeImpl(value, index, 0);
  }


  public int codePointCount(int beginIndex, int endIndex) {
    if (beginIndex < 0 || endIndex > value.length || beginIndex > endIndex) {
      throw new IndexOutOfBoundsException();
    }
    return Character.codePointCountImpl(value, beginIndex, endIndex - beginIndex);
  }


  public int offsetByCodePoints(int index, int codePointOffset) {
    if (index < 0 || index > value.length) {
      throw new IndexOutOfBoundsException();
    }
    return Character.offsetByCodePointsImpl(value, 0, value.length,
        index, codePointOffset);
  }


  void getChars(char dst[], int dstBegin) {
    System.arraycopy(value, 0, dst, dstBegin, value.length);
  }


  public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
    if (srcBegin < 0) {
      throw new StringIndexOutOfBoundsException(srcBegin);
    }
    if (srcEnd > value.length) {
      throw new StringIndexOutOfBoundsException(srcEnd);
    }
    if (srcBegin > srcEnd) {
      throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
    }
    System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
  }


  @Deprecated
  public void getBytes(int srcBegin, int srcEnd, byte dst[], int dstBegin) {
    if (srcBegin < 0) {
      throw new StringIndexOutOfBoundsException(srcBegin);
    }
    if (srcEnd > value.length) {
      throw new StringIndexOutOfBoundsException(srcEnd);
    }
    if (srcBegin > srcEnd) {
      throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
    }
    Objects.requireNonNull(dst);

    int j = dstBegin;
    int n = srcEnd;
    int i = srcBegin;
    char[] val = value;

    while (i < n) {
      dst[j++] = (byte) val[i++];
    }
  }


  public byte[] getBytes(Charset charset) {
    if (charset == null) throw new NullPointerException();
    return StringCoding.encode(charset, value, 0, value.length);
  }


  public byte[] getBytes() {
    return StringCoding.encode(value, 0, value.length);
  }


  public boolean contentEquals(StringBuffer sb) {
    return contentEquals((CharSequence) sb);
  }

  private boolean nonSyncContentEquals(AbstractStringBuilder sb) {
    char v1[] = value;
    char v2[] = sb.getValue();
    int n = v1.length;
    if (n != sb.length()) {
      return false;
    }
    for (int i = 0; i < n; i++) {
      if (v1[i] != v2[i]) {
        return false;
      }
    }
    return true;
  }


  public boolean contentEquals(CharSequence cs) {
    if (cs instanceof AbstractStringBuilder) {
      if (cs instanceof StringBuffer) {
        synchronized (cs) {
          return nonSyncContentEquals((AbstractStringBuilder) cs);
        }
      } else {
        return nonSyncContentEquals((AbstractStringBuilder) cs);
      }
    }
    if (cs instanceof String) {
      return equals(cs);
    }
    char v1[] = value;
    int n = v1.length;
    if (n != cs.length()) {
      return false;
    }
    for (int i = 0; i < n; i++) {
      if (v1[i] != cs.charAt(i)) {
        return false;
      }
    }
    return true;
  }


  public boolean equalsIgnoreCase(String anotherString) {
    return (this == anotherString) ? true
        : (anotherString != null)
        && (anotherString.value.length == value.length)
        && regionMatches(true, 0, anotherString, 0, value.length);
  }


  public static final Comparator<String> CASE_INSENSITIVE_ORDER
      = new CaseInsensitiveComparator();

  private static class CaseInsensitiveComparator
      implements Comparator<String>, java.io.Serializable {
    private static final long serialVersionUID = 8575799808933029326L;

    public int compare(String s1, String s2) {
      int n1 = s1.length();
      int n2 = s2.length();
      int min = Math.min(n1, n2);
      for (int i = 0; i < min; i++) {
        char c1 = s1.charAt(i);
        char c2 = s2.charAt(i);
        if (c1 != c2) {
          c1 = Character.toUpperCase(c1);
          c2 = Character.toUpperCase(c2);
          if (c1 != c2) {
            c1 = Character.toLowerCase(c1);
            c2 = Character.toLowerCase(c2);
            if (c1 != c2) {
              return c1 - c2;
            }
          }
        }
      }
      return n1 - n2;
    }


    private Object readResolve() {
      return CASE_INSENSITIVE_ORDER;
    }
  }


  public int compareToIgnoreCase(String str) {
    return CASE_INSENSITIVE_ORDER.compare(this, str);
  }


  public boolean regionMatches(int toffset, String other, int ooffset,
                               int len) {
    char ta[] = value;
    int to = toffset;
    char pa[] = other.value;
    int po = ooffset;
    if ((ooffset < 0) || (toffset < 0)
        || (toffset > (long) value.length - len)
        || (ooffset > (long) other.value.length - len)) {
      return false;
    }
    while (len-- > 0) {
      if (ta[to++] != pa[po++]) {
        return false;
      }
    }
    return true;
  }


  public boolean regionMatches(boolean ignoreCase, int toffset,
                               String other, int ooffset, int len) {
    char ta[] = value;
    int to = toffset;
    char pa[] = other.value;
    int po = ooffset;
    if ((ooffset < 0) || (toffset < 0)
        || (toffset > (long) value.length - len)
        || (ooffset > (long) other.value.length - len)) {
      return false;
    }
    while (len-- > 0) {
      char c1 = ta[to++];
      char c2 = pa[po++];
      if (c1 == c2) {
        continue;
      }
      if (ignoreCase) {
        char u1 = Character.toUpperCase(c1);
        char u2 = Character.toUpperCase(c2);
        if (u1 == u2) {
          continue;
        }
        if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
          continue;
        }
      }
      return false;
    }
    return true;
  }


  public boolean startsWith(String prefix) {
    return startsWith(prefix, 0);
  }


  public boolean endsWith(String suffix) {
    return startsWith(suffix, value.length - suffix.value.length);
  }


  public int indexOf(int ch) {
    return indexOf(ch, 0);
  }


  public int indexOf(int ch, int fromIndex) {
    final int max = value.length;
    if (fromIndex < 0) {
      fromIndex = 0;
    } else if (fromIndex >= max) {
      return -1;
    }

    if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
      final char[] value = this.value;
      for (int i = fromIndex; i < max; i++) {
        if (value[i] == ch) {
          return i;
        }
      }
      return -1;
    } else {
      return indexOfSupplementary(ch, fromIndex);
    }
  }


  private int indexOfSupplementary(int ch, int fromIndex) {
    if (Character.isValidCodePoint(ch)) {
      final char[] value = this.value;
      final char hi = Character.highSurrogate(ch);
      final char lo = Character.lowSurrogate(ch);
      final int max = value.length - 1;
      for (int i = fromIndex; i < max; i++) {
        if (value[i] == hi && value[i + 1] == lo) {
          return i;
        }
      }
    }
    return -1;
  }


  public int lastIndexOf(int ch) {
    return lastIndexOf(ch, value.length - 1);
  }


  public int lastIndexOf(int ch, int fromIndex) {
    if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
      final char[] value = this.value;
      int i = Math.min(fromIndex, value.length - 1);
      for (; i >= 0; i--) {
        if (value[i] == ch) {
          return i;
        }
      }
      return -1;
    } else {
      return lastIndexOfSupplementary(ch, fromIndex);
    }
  }


  private int lastIndexOfSupplementary(int ch, int fromIndex) {
    if (Character.isValidCodePoint(ch)) {
      final char[] value = this.value;
      char hi = Character.highSurrogate(ch);
      char lo = Character.lowSurrogate(ch);
      int i = Math.min(fromIndex, value.length - 2);
      for (; i >= 0; i--) {
        if (value[i] == hi && value[i + 1] == lo) {
          return i;
        }
      }
    }
    return -1;
  }


  public int indexOf(String str) {
    return indexOf(str, 0);
  }


  public int indexOf(String str, int fromIndex) {
    return indexOf(value, 0, value.length,
        str.value, 0, str.value.length, fromIndex);
  }


  static int indexOf(char[] source, int sourceOffset, int sourceCount,
                     String target, int fromIndex) {
    return indexOf(source, sourceOffset, sourceCount,
        target.value, 0, target.value.length,
        fromIndex);
  }


  static int indexOf(char[] source, int sourceOffset, int sourceCount,
                     char[] target, int targetOffset, int targetCount,
                     int fromIndex) {
    if (fromIndex >= sourceCount) {
      return (targetCount == 0 ? sourceCount : -1);
    }
    if (fromIndex < 0) {
      fromIndex = 0;
    }
    if (targetCount == 0) {
      return fromIndex;
    }

    char first = target[targetOffset];
    int max = sourceOffset + (sourceCount - targetCount);

    for (int i = sourceOffset + fromIndex; i <= max; i++) {

      if (source[i] != first) {
        while (++i <= max && source[i] != first) ;
      }


      if (i <= max) {
        int j = i + 1;
        int end = j + targetCount - 1;
        for (int k = targetOffset + 1; j < end && source[j]
            == target[k]; j++, k++)
          ;

        if (j == end) {

          return i - sourceOffset;
        }
      }
    }
    return -1;
  }


  public int lastIndexOf(String str) {
    return lastIndexOf(str, value.length);
  }


  public int lastIndexOf(String str, int fromIndex) {
    return lastIndexOf(value, 0, value.length,
        str.value, 0, str.value.length, fromIndex);
  }


  static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
                         String target, int fromIndex) {
    return lastIndexOf(source, sourceOffset, sourceCount,
        target.value, 0, target.value.length,
        fromIndex);
  }


  static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
                         char[] target, int targetOffset, int targetCount,
                         int fromIndex) {

    int rightIndex = sourceCount - targetCount;
    if (fromIndex < 0) {
      return -1;
    }
    if (fromIndex > rightIndex) {
      fromIndex = rightIndex;
    }

    if (targetCount == 0) {
      return fromIndex;
    }

    int strLastIndex = targetOffset + targetCount - 1;
    char strLastChar = target[strLastIndex];
    int min = sourceOffset + targetCount - 1;
    int i = min + fromIndex;

    startSearchForLastChar:
    while (true) {
      while (i >= min && source[i] != strLastChar) {
        i--;
      }
      if (i < min) {
        return -1;
      }
      int j = i - 1;
      int start = j - (targetCount - 1);
      int k = strLastIndex - 1;

      while (j > start) {
        if (source[j--] != target[k--]) {
          i--;
          continue startSearchForLastChar;
        }
      }
      return start - sourceOffset + 1;
    }
  }


  public String substring(int beginIndex) {
    if (beginIndex < 0) {
      throw new StringIndexOutOfBoundsException(beginIndex);
    }
    int subLen = value.length - beginIndex;
    if (subLen < 0) {
      throw new StringIndexOutOfBoundsException(subLen);
    }
    return (beginIndex == 0) ? this : new String(value, beginIndex, subLen);
  }


  public CharSequence subSequence(int beginIndex, int endIndex) {
    return this.substring(beginIndex, endIndex);
  }


  public boolean matches(String regex) {
    return Pattern.matches(regex, this);
  }


  public boolean contains(CharSequence s) {
    return indexOf(s.toString()) > -1;
  }


  public String replaceFirst(String regex, String replacement) {
    return Pattern.compile(regex).matcher(this).replaceFirst(replacement);
  }


  public String replaceAll(String regex, String replacement) {
    return Pattern.compile(regex).matcher(this).replaceAll(replacement);
  }


  public String replace(CharSequence target, CharSequence replacement) {
    return Pattern.compile(target.toString(), Pattern.LITERAL).matcher(
        this).replaceAll(Matcher.quoteReplacement(replacement.toString()));
  }


  public String[] split(String regex, int limit) {

    char ch = 0;
    if (((regex.value.length == 1 &&
        ".$|()[{^?*+\\".indexOf(ch = regex.charAt(0)) == -1) ||
        (regex.length() == 2 &&
            regex.charAt(0) == '\\' &&
            (((ch = regex.charAt(1)) - '0') | ('9' - ch)) < 0 &&
            ((ch - 'a') | ('z' - ch)) < 0 &&
            ((ch - 'A') | ('Z' - ch)) < 0)) &&
        (ch < Character.MIN_HIGH_SURROGATE ||
            ch > Character.MAX_LOW_SURROGATE)) {
      int off = 0;
      int next = 0;
      boolean limited = limit > 0;
      ArrayList<String> list = new ArrayList<>();
      while ((next = indexOf(ch, off)) != -1) {
        if (!limited || list.size() < limit - 1) {
          list.add(substring(off, next));
          off = next + 1;
        } else {
          list.add(substring(off, value.length));
          off = value.length;
          break;
        }
      }
      if (off == 0)
        return new String[]{this};

      if (!limited || list.size() < limit)
        list.add(substring(off, value.length));

      int resultSize = list.size();
      if (limit == 0) {
        while (resultSize > 0 && list.get(resultSize - 1).length() == 0) {
          resultSize--;
        }
      }
      String[] result = new String[resultSize];
      return list.subList(0, resultSize).toArray(result);
    }
    return Pattern.compile(regex).split(this, limit);
  }


  public static String join(CharSequence delimiter,
                            Iterable<? extends CharSequence> elements) {
    Objects.requireNonNull(delimiter);
    Objects.requireNonNull(elements);
    StringJoiner joiner = new StringJoiner(delimiter);
    for (CharSequence cs : elements) {
      joiner.add(cs);
    }
    return joiner.toString();
  }


  public String toLowerCase(Locale locale) {
    if (locale == null) {
      throw new NullPointerException();
    }

    int firstUpper;
    final int len = value.length;


    scan:
    {
      for (firstUpper = 0; firstUpper < len; ) {
        char c = value[firstUpper];
        if ((c >= Character.MIN_HIGH_SURROGATE)
            && (c <= Character.MAX_HIGH_SURROGATE)) {
          int supplChar = codePointAt(firstUpper);
          if (supplChar != Character.toLowerCase(supplChar)) {
            break scan;
          }
          firstUpper += Character.charCount(supplChar);
        } else {
          if (c != Character.toLowerCase(c)) {
            break scan;
          }
          firstUpper++;
        }
      }
      return this;
    }

    char[] result = new char[len];
    int resultOffset = 0;


    System.arraycopy(value, 0, result, 0, firstUpper);

    String lang = locale.getLanguage();
    boolean localeDependent =
        (lang == "tr" || lang == "az" || lang == "lt");
    char[] lowerCharArray;
    int lowerChar;
    int srcChar;
    int srcCount;
    for (int i = firstUpper; i < len; i += srcCount) {
      srcChar = (int) value[i];
      if ((char) srcChar >= Character.MIN_HIGH_SURROGATE
          && (char) srcChar <= Character.MAX_HIGH_SURROGATE) {
        srcChar = codePointAt(i);
        srcCount = Character.charCount(srcChar);
      } else {
        srcCount = 1;
      }
      if (localeDependent ||
          srcChar == '\u03A3' || srcChar == '\u0130') {
        lowerChar = ConditionalSpecialCasing.toLowerCaseEx(this, i, locale);
      } else {
        lowerChar = Character.toLowerCase(srcChar);
      }
      if ((lowerChar == Character.ERROR)
          || (lowerChar >= Character.MIN_SUPPLEMENTARY_CODE_POINT)) {
        if (lowerChar == Character.ERROR) {
          lowerCharArray =
              ConditionalSpecialCasing.toLowerCaseCharArray(this, i, locale);
        } else if (srcCount == 2) {
          resultOffset += Character.toChars(lowerChar, result, i + resultOffset) - srcCount;
          continue;
        } else {
          lowerCharArray = Character.toChars(lowerChar);
        }


        int mapLen = lowerCharArray.length;
        if (mapLen > srcCount) {
          char[] result2 = new char[result.length + mapLen - srcCount];
          System.arraycopy(result, 0, result2, 0, i + resultOffset);
          result = result2;
        }
        for (int x = 0; x < mapLen; ++x) {
          result[i + resultOffset + x] = lowerCharArray[x];
        }
        resultOffset += (mapLen - srcCount);
      } else {
        result[i + resultOffset] = (char) lowerChar;
      }
    }
    return new String(result, 0, len + resultOffset);
  }


  public String toLowerCase() {
    return toLowerCase(Locale.getDefault());
  }


  public String toUpperCase(Locale locale) {
    if (locale == null) {
      throw new NullPointerException();
    }

    int firstLower;
    final int len = value.length;


    scan:
    {
      for (firstLower = 0; firstLower < len; ) {
        int c = (int) value[firstLower];
        int srcCount;
        if ((c >= Character.MIN_HIGH_SURROGATE)
            && (c <= Character.MAX_HIGH_SURROGATE)) {
          c = codePointAt(firstLower);
          srcCount = Character.charCount(c);
        } else {
          srcCount = 1;
        }
        int upperCaseChar = Character.toUpperCaseEx(c);
        if ((upperCaseChar == Character.ERROR)
            || (c != upperCaseChar)) {
          break scan;
        }
        firstLower += srcCount;
      }
      return this;
    }


    int resultOffset = 0;
    char[] result = new char[len];


    System.arraycopy(value, 0, result, 0, firstLower);

    String lang = locale.getLanguage();
    boolean localeDependent =
        (lang == "tr" || lang == "az" || lang == "lt");
    char[] upperCharArray;
    int upperChar;
    int srcChar;
    int srcCount;
    for (int i = firstLower; i < len; i += srcCount) {
      srcChar = (int) value[i];
      if ((char) srcChar >= Character.MIN_HIGH_SURROGATE &&
          (char) srcChar <= Character.MAX_HIGH_SURROGATE) {
        srcChar = codePointAt(i);
        srcCount = Character.charCount(srcChar);
      } else {
        srcCount = 1;
      }
      if (localeDependent) {
        upperChar = ConditionalSpecialCasing.toUpperCaseEx(this, i, locale);
      } else {
        upperChar = Character.toUpperCaseEx(srcChar);
      }
      if ((upperChar == Character.ERROR)
          || (upperChar >= Character.MIN_SUPPLEMENTARY_CODE_POINT)) {
        if (upperChar == Character.ERROR) {
          if (localeDependent) {
            upperCharArray =
                ConditionalSpecialCasing.toUpperCaseCharArray(this, i, locale);
          } else {
            upperCharArray = Character.toUpperCaseCharArray(srcChar);
          }
        } else if (srcCount == 2) {
          resultOffset += Character.toChars(upperChar, result, i + resultOffset) - srcCount;
          continue;
        } else {
          upperCharArray = Character.toChars(upperChar);
        }


        int mapLen = upperCharArray.length;
        if (mapLen > srcCount) {
          char[] result2 = new char[result.length + mapLen - srcCount];
          System.arraycopy(result, 0, result2, 0, i + resultOffset);
          result = result2;
        }
        for (int x = 0; x < mapLen; ++x) {
          result[i + resultOffset + x] = upperCharArray[x];
        }
        resultOffset += (mapLen - srcCount);
      } else {
        result[i + resultOffset] = (char) upperChar;
      }
    }
    return new String(result, 0, len + resultOffset);
  }


  public String toUpperCase() {
    return toUpperCase(Locale.getDefault());
  }


  public char[] toCharArray() {
    char result[] = new char[value.length];
    System.arraycopy(value, 0, result, 0, value.length);
    return result;
  }


  public static String format(String format, Object... args) {
    return new Formatter().format(format, args).toString();
  }


  public static String format(Locale l, String format, Object... args) {
    return new Formatter(l).format(format, args).toString();
  }


  public static String valueOf(Object obj) {
    return (obj == null) ? "null" : obj.toString();
  }


  public static String valueOf(char data[]) {
    return new String(data);
  }


  public static String valueOf(char data[], int offset, int count) {
    return new String(data, offset, count);
  }


  public static String copyValueOf(char data[], int offset, int count) {
    return new String(data, offset, count);
  }


  public static String copyValueOf(char data[]) {
    return new String(data);
  }


  public static String valueOf(boolean b) {
    return b ? "true" : "false";
  }


  public static String valueOf(char c) {
    char data[] = {c};
    return new String(data, true);
  }


  public static String valueOf(int i) {
    return Integer.toString(i);
  }


  public static String valueOf(long l) {
    return Long.toString(l);
  }


  public static String valueOf(float f) {
    return Float.toString(f);
  }


  public static String valueOf(double d) {
    return Double.toString(d);
  }


}
