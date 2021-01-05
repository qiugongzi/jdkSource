


package com.sun.org.apache.xerces.internal.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;


 public class URI implements Serializable {


  public static class MalformedURIException extends IOException {


   static final long serialVersionUID = -6695054834342951930L;


    public MalformedURIException() {
      super();
    }


    public MalformedURIException(String p_msg) {
      super(p_msg);
    }
  }


  static final long serialVersionUID = 1601921774685357214L;

  private static final byte [] fgLookupTable = new byte[128];




  private static final int RESERVED_CHARACTERS = 0x01;


  private static final int MARK_CHARACTERS = 0x02;


  private static final int SCHEME_CHARACTERS = 0x04;


  private static final int USERINFO_CHARACTERS = 0x08;


  private static final int ASCII_ALPHA_CHARACTERS = 0x10;


  private static final int ASCII_DIGIT_CHARACTERS = 0x20;


  private static final int ASCII_HEX_CHARACTERS = 0x40;


  private static final int PATH_CHARACTERS = 0x80;


  private static final int MASK_ALPHA_NUMERIC = ASCII_ALPHA_CHARACTERS | ASCII_DIGIT_CHARACTERS;


  private static final int MASK_UNRESERVED_MASK = MASK_ALPHA_NUMERIC | MARK_CHARACTERS;


  private static final int MASK_URI_CHARACTER = MASK_UNRESERVED_MASK | RESERVED_CHARACTERS;


  private static final int MASK_SCHEME_CHARACTER = MASK_ALPHA_NUMERIC | SCHEME_CHARACTERS;


  private static final int MASK_USERINFO_CHARACTER = MASK_UNRESERVED_MASK | USERINFO_CHARACTERS;


  private static final int MASK_PATH_CHARACTER = MASK_UNRESERVED_MASK | PATH_CHARACTERS;

  static {
      for (int i = '0'; i <= '9'; ++i) {
          fgLookupTable[i] |= ASCII_DIGIT_CHARACTERS | ASCII_HEX_CHARACTERS;
      }

      for (int i = 'A'; i <= 'F'; ++i) {
          fgLookupTable[i] |= ASCII_ALPHA_CHARACTERS | ASCII_HEX_CHARACTERS;
          fgLookupTable[i+0x00000020] |= ASCII_ALPHA_CHARACTERS | ASCII_HEX_CHARACTERS;
      }

      for (int i = 'G'; i <= 'Z'; ++i) {
          fgLookupTable[i] |= ASCII_ALPHA_CHARACTERS;
          fgLookupTable[i+0x00000020] |= ASCII_ALPHA_CHARACTERS;
      }

      fgLookupTable[';'] |= RESERVED_CHARACTERS;
      fgLookupTable['/'] |= RESERVED_CHARACTERS;
      fgLookupTable['?'] |= RESERVED_CHARACTERS;
      fgLookupTable[':'] |= RESERVED_CHARACTERS;
      fgLookupTable['@'] |= RESERVED_CHARACTERS;
      fgLookupTable['&'] |= RESERVED_CHARACTERS;
      fgLookupTable['='] |= RESERVED_CHARACTERS;
      fgLookupTable['+'] |= RESERVED_CHARACTERS;
      fgLookupTable['$'] |= RESERVED_CHARACTERS;
      fgLookupTable[','] |= RESERVED_CHARACTERS;
      fgLookupTable['['] |= RESERVED_CHARACTERS;
      fgLookupTable[']'] |= RESERVED_CHARACTERS;

      fgLookupTable['-'] |= MARK_CHARACTERS;
      fgLookupTable['_'] |= MARK_CHARACTERS;
      fgLookupTable['.'] |= MARK_CHARACTERS;
      fgLookupTable['!'] |= MARK_CHARACTERS;
      fgLookupTable['~'] |= MARK_CHARACTERS;
      fgLookupTable['*'] |= MARK_CHARACTERS;
      fgLookupTable['\''] |= MARK_CHARACTERS;
      fgLookupTable['('] |= MARK_CHARACTERS;
      fgLookupTable[')'] |= MARK_CHARACTERS;

      fgLookupTable['+'] |= SCHEME_CHARACTERS;
      fgLookupTable['-'] |= SCHEME_CHARACTERS;
      fgLookupTable['.'] |= SCHEME_CHARACTERS;

      fgLookupTable[';'] |= USERINFO_CHARACTERS;
      fgLookupTable[':'] |= USERINFO_CHARACTERS;
      fgLookupTable['&'] |= USERINFO_CHARACTERS;
      fgLookupTable['='] |= USERINFO_CHARACTERS;
      fgLookupTable['+'] |= USERINFO_CHARACTERS;
      fgLookupTable['$'] |= USERINFO_CHARACTERS;
      fgLookupTable[','] |= USERINFO_CHARACTERS;

      fgLookupTable[';'] |= PATH_CHARACTERS;
      fgLookupTable['/'] |= PATH_CHARACTERS;
      fgLookupTable[':'] |= PATH_CHARACTERS;
      fgLookupTable['@'] |= PATH_CHARACTERS;
      fgLookupTable['&'] |= PATH_CHARACTERS;
      fgLookupTable['='] |= PATH_CHARACTERS;
      fgLookupTable['+'] |= PATH_CHARACTERS;
      fgLookupTable['$'] |= PATH_CHARACTERS;
      fgLookupTable[','] |= PATH_CHARACTERS;
  }


  private String m_scheme = null;


  private String m_userinfo = null;


  private String m_host = null;


  private int m_port = -1;


  private String m_regAuthority = null;


  private String m_path = null;


  private String m_queryString = null;


  private String m_fragment = null;

  private static boolean DEBUG = false;


  public URI() {
  }


  public URI(URI p_other) {
    initialize(p_other);
  }


  public URI(String p_uriSpec) throws MalformedURIException {
    this((URI)null, p_uriSpec);
  }


  public URI(String p_uriSpec, boolean allowNonAbsoluteURI) throws MalformedURIException {
      this((URI)null, p_uriSpec, allowNonAbsoluteURI);
  }


  public URI(URI p_base, String p_uriSpec) throws MalformedURIException {
    initialize(p_base, p_uriSpec);
  }


  public URI(URI p_base, String p_uriSpec, boolean allowNonAbsoluteURI) throws MalformedURIException {
      initialize(p_base, p_uriSpec, allowNonAbsoluteURI);
  }


  public URI(String p_scheme, String p_schemeSpecificPart)
             throws MalformedURIException {
    if (p_scheme == null || p_scheme.trim().length() == 0) {
      throw new MalformedURIException(
            "Cannot construct URI with null/empty scheme!");
    }
    if (p_schemeSpecificPart == null ||
        p_schemeSpecificPart.trim().length() == 0) {
      throw new MalformedURIException(
          "Cannot construct URI with null/empty scheme-specific part!");
    }
    setScheme(p_scheme);
    setPath(p_schemeSpecificPart);
  }


  public URI(String p_scheme, String p_host, String p_path,
             String p_queryString, String p_fragment)
         throws MalformedURIException {
    this(p_scheme, null, p_host, -1, p_path, p_queryString, p_fragment);
  }


  public URI(String p_scheme, String p_userinfo,
             String p_host, int p_port, String p_path,
             String p_queryString, String p_fragment)
         throws MalformedURIException {
    if (p_scheme == null || p_scheme.trim().length() == 0) {
      throw new MalformedURIException("Scheme is required!");
    }

    if (p_host == null) {
      if (p_userinfo != null) {
        throw new MalformedURIException(
             "Userinfo may not be specified if host is not specified!");
      }
      if (p_port != -1) {
        throw new MalformedURIException(
             "Port may not be specified if host is not specified!");
      }
    }

    if (p_path != null) {
      if (p_path.indexOf('?') != -1 && p_queryString != null) {
        throw new MalformedURIException(
          "Query string cannot be specified in path and query string!");
      }

      if (p_path.indexOf('#') != -1 && p_fragment != null) {
        throw new MalformedURIException(
          "Fragment cannot be specified in both the path and fragment!");
      }
    }

    setScheme(p_scheme);
    setHost(p_host);
    setPort(p_port);
    setUserinfo(p_userinfo);
    setPath(p_path);
    setQueryString(p_queryString);
    setFragment(p_fragment);
  }


  private void initialize(URI p_other) {
    m_scheme = p_other.getScheme();
    m_userinfo = p_other.getUserinfo();
    m_host = p_other.getHost();
    m_port = p_other.getPort();
    m_regAuthority = p_other.getRegBasedAuthority();
    m_path = p_other.getPath();
    m_queryString = p_other.getQueryString();
    m_fragment = p_other.getFragment();
  }


  private void initialize(URI p_base, String p_uriSpec, boolean allowNonAbsoluteURI)
      throws MalformedURIException {

      String uriSpec = p_uriSpec;
      int uriSpecLen = (uriSpec != null) ? uriSpec.length() : 0;

      if (p_base == null && uriSpecLen == 0) {
          if (allowNonAbsoluteURI) {
              m_path = "";
              return;
          }
          throw new MalformedURIException("Cannot initialize URI with empty parameters.");
      }

      if (uriSpecLen == 0) {
          initialize(p_base);
          return;
      }

      int index = 0;

      int colonIdx = uriSpec.indexOf(':');
      if (colonIdx != -1) {
          final int searchFrom = colonIdx - 1;
          int slashIdx = uriSpec.lastIndexOf('/', searchFrom);
          int queryIdx = uriSpec.lastIndexOf('?', searchFrom);
          int fragmentIdx = uriSpec.lastIndexOf('#', searchFrom);

          if (colonIdx == 0 || slashIdx != -1 ||
              queryIdx != -1 || fragmentIdx != -1) {
              if (colonIdx == 0 || (p_base == null && fragmentIdx != 0 && !allowNonAbsoluteURI)) {
                  throw new MalformedURIException("No scheme found in URI.");
              }
          }
          else {
              initializeScheme(uriSpec);
              index = m_scheme.length()+1;

              if (colonIdx == uriSpecLen - 1 || uriSpec.charAt(colonIdx+1) == '#') {
                  throw new MalformedURIException("Scheme specific part cannot be empty.");
              }
          }
      }
      else if (p_base == null && uriSpec.indexOf('#') != 0 && !allowNonAbsoluteURI) {
          throw new MalformedURIException("No scheme found in URI.");
      }

      if (((index+1) < uriSpecLen) &&
          (uriSpec.charAt(index) == '/' && uriSpec.charAt(index+1) == '/')) {
          index += 2;
          int startPos = index;

          char testChar = '\0';
          while (index < uriSpecLen) {
              testChar = uriSpec.charAt(index);
              if (testChar == '/' || testChar == '?' || testChar == '#') {
                  break;
              }
              index++;
          }

          if (index > startPos) {
              if (!initializeAuthority(uriSpec.substring(startPos, index))) {
                  index = startPos - 2;
              }
          }
          else {
              m_host = "";
          }
      }

      initializePath(uriSpec, index);

      if (p_base != null) {
          absolutize(p_base);
      }
  }


  private void initialize(URI p_base, String p_uriSpec)
                         throws MalformedURIException {

    String uriSpec = p_uriSpec;
    int uriSpecLen = (uriSpec != null) ? uriSpec.length() : 0;

    if (p_base == null && uriSpecLen == 0) {
      throw new MalformedURIException(
                  "Cannot initialize URI with empty parameters.");
    }

    if (uriSpecLen == 0) {
      initialize(p_base);
      return;
    }

    int index = 0;

    int colonIdx = uriSpec.indexOf(':');
    if (colonIdx != -1) {
        final int searchFrom = colonIdx - 1;
        int slashIdx = uriSpec.lastIndexOf('/', searchFrom);
        int queryIdx = uriSpec.lastIndexOf('?', searchFrom);
        int fragmentIdx = uriSpec.lastIndexOf('#', searchFrom);

        if (colonIdx == 0 || slashIdx != -1 ||
            queryIdx != -1 || fragmentIdx != -1) {
            if (colonIdx == 0 || (p_base == null && fragmentIdx != 0)) {
                throw new MalformedURIException("No scheme found in URI.");
            }
        }
        else {
            initializeScheme(uriSpec);
            index = m_scheme.length()+1;

            if (colonIdx == uriSpecLen - 1 || uriSpec.charAt(colonIdx+1) == '#') {
                throw new MalformedURIException("Scheme specific part cannot be empty.");
            }
        }
    }
    else if (p_base == null && uriSpec.indexOf('#') != 0) {
        throw new MalformedURIException("No scheme found in URI.");
    }

    if (((index+1) < uriSpecLen) &&
        (uriSpec.charAt(index) == '/' && uriSpec.charAt(index+1) == '/')) {
      index += 2;
      int startPos = index;

      char testChar = '\0';
      while (index < uriSpecLen) {
        testChar = uriSpec.charAt(index);
        if (testChar == '/' || testChar == '?' || testChar == '#') {
          break;
        }
        index++;
      }

      if (index > startPos) {
        if (!initializeAuthority(uriSpec.substring(startPos, index))) {
          index = startPos - 2;
        }
      } else if (index < uriSpecLen) {
        m_host = "";
      } else {
        throw new MalformedURIException("Expected authority.");
      }
    }

    initializePath(uriSpec, index);

    if (p_base != null) {
        absolutize(p_base);
    }
  }


  public void absolutize(URI p_base) {

      if (m_path.length() == 0 && m_scheme == null &&
          m_host == null && m_regAuthority == null) {
          m_scheme = p_base.getScheme();
          m_userinfo = p_base.getUserinfo();
          m_host = p_base.getHost();
          m_port = p_base.getPort();
          m_regAuthority = p_base.getRegBasedAuthority();
          m_path = p_base.getPath();

          if (m_queryString == null) {
              m_queryString = p_base.getQueryString();

              if (m_fragment == null) {
                  m_fragment = p_base.getFragment();
              }
          }
          return;
      }

      if (m_scheme == null) {
          m_scheme = p_base.getScheme();
      }
      else {
          return;
      }

      if (m_host == null && m_regAuthority == null) {
          m_userinfo = p_base.getUserinfo();
          m_host = p_base.getHost();
          m_port = p_base.getPort();
          m_regAuthority = p_base.getRegBasedAuthority();
      }
      else {
          return;
      }

      if (m_path.length() > 0 &&
              m_path.startsWith("/")) {
          return;
      }

      String path = "";
      String basePath = p_base.getPath();

      if (basePath != null && basePath.length() > 0) {
          int lastSlash = basePath.lastIndexOf('/');
          if (lastSlash != -1) {
              path = basePath.substring(0, lastSlash+1);
          }
      }
      else if (m_path.length() > 0) {
          path = "/";
      }

      path = path.concat(m_path);

      int index = -1;
      while ((index = path.indexOf("/./")) != -1) {
          path = path.substring(0, index+1).concat(path.substring(index+3));
      }

      if (path.endsWith("/.")) {
          path = path.substring(0, path.length()-1);
      }

      index = 1;
      int segIndex = -1;
      String tempString = null;

      while ((index = path.indexOf("/../", index)) > 0) {
          tempString = path.substring(0, path.indexOf("/../"));
          segIndex = tempString.lastIndexOf('/');
          if (segIndex != -1) {
              if (!tempString.substring(segIndex).equals("..")) {
                  path = path.substring(0, segIndex+1).concat(path.substring(index+4));
                  index = segIndex;
              }
              else {
                  index += 4;
              }
          }
          else {
              index += 4;
          }
      }

      if (path.endsWith("/..")) {
          tempString = path.substring(0, path.length()-3);
          segIndex = tempString.lastIndexOf('/');
          if (segIndex != -1) {
              path = path.substring(0, segIndex+1);
          }
      }
      m_path = path;
  }


  private void initializeScheme(String p_uriSpec)
                 throws MalformedURIException {
    int uriSpecLen = p_uriSpec.length();
    int index = 0;
    String scheme = null;
    char testChar = '\0';

    while (index < uriSpecLen) {
      testChar = p_uriSpec.charAt(index);
      if (testChar == ':' || testChar == '/' ||
          testChar == '?' || testChar == '#') {
        break;
      }
      index++;
    }
    scheme = p_uriSpec.substring(0, index);

    if (scheme.length() == 0) {
      throw new MalformedURIException("No scheme found in URI.");
    }
    else {
      setScheme(scheme);
    }
  }


  private boolean initializeAuthority(String p_uriSpec) {

    int index = 0;
    int start = 0;
    int end = p_uriSpec.length();

    char testChar = '\0';
    String userinfo = null;

    if (p_uriSpec.indexOf('@', start) != -1) {
      while (index < end) {
        testChar = p_uriSpec.charAt(index);
        if (testChar == '@') {
          break;
        }
        index++;
      }
      userinfo = p_uriSpec.substring(start, index);
      index++;
    }

    String host = null;
    start = index;
    boolean hasPort = false;
    if (index < end) {
      if (p_uriSpec.charAt(start) == '[') {
        int bracketIndex = p_uriSpec.indexOf(']', start);
        index = (bracketIndex != -1) ? bracketIndex : end;
        if (index+1 < end && p_uriSpec.charAt(index+1) == ':') {
          ++index;
          hasPort = true;
        }
        else {
          index = end;
        }
      }
      else {
        int colonIndex = p_uriSpec.lastIndexOf(':', end);
        index = (colonIndex > start) ? colonIndex : end;
        hasPort = (index != end);
      }
    }
    host = p_uriSpec.substring(start, index);
    int port = -1;
    if (host.length() > 0) {
      if (hasPort) {
        index++;
        start = index;
        while (index < end) {
          index++;
        }
        String portStr = p_uriSpec.substring(start, index);
        if (portStr.length() > 0) {
          try {
            port = Integer.parseInt(portStr);
            if (port == -1) --port;
          }
          catch (NumberFormatException nfe) {
            port = -2;
          }
        }
      }
    }

    if (isValidServerBasedAuthority(host, port, userinfo)) {
      m_host = host;
      m_port = port;
      m_userinfo = userinfo;
      return true;
    }
    else if (isValidRegistryBasedAuthority(p_uriSpec)) {
      m_regAuthority = p_uriSpec;
      return true;
    }
    return false;
  }


  private boolean isValidServerBasedAuthority(String host, int port, String userinfo) {

    if (!isWellFormedAddress(host)) {
      return false;
    }

    if (port < -1 || port > 65535) {
      return false;
    }

    if (userinfo != null) {
      int index = 0;
      int end = userinfo.length();
      char testChar = '\0';
      while (index < end) {
        testChar = userinfo.charAt(index);
        if (testChar == '%') {
          if (index+2 >= end ||
            !isHex(userinfo.charAt(index+1)) ||
            !isHex(userinfo.charAt(index+2))) {
            return false;
          }
          index += 2;
        }
        else if (!isUserinfoCharacter(testChar)) {
          return false;
        }
        ++index;
      }
    }
    return true;
  }


  private boolean isValidRegistryBasedAuthority(String authority) {
    int index = 0;
    int end = authority.length();
    char testChar;

    while (index < end) {
      testChar = authority.charAt(index);

      if (testChar == '%') {
        if (index+2 >= end ||
            !isHex(authority.charAt(index+1)) ||
            !isHex(authority.charAt(index+2))) {
            return false;
        }
        index += 2;
      }
      else if (!isPathCharacter(testChar)) {
        return false;
      }
      ++index;
    }
    return true;
  }


  private void initializePath(String p_uriSpec, int p_nStartIndex)
                 throws MalformedURIException {
    if (p_uriSpec == null) {
      throw new MalformedURIException(
                "Cannot initialize path from null string!");
    }

    int index = p_nStartIndex;
    int start = p_nStartIndex;
    int end = p_uriSpec.length();
    char testChar = '\0';

    if (start < end) {
        if (getScheme() == null || p_uriSpec.charAt(start) == '/') {

            while (index < end) {
                testChar = p_uriSpec.charAt(index);

                if (testChar == '%') {
                    if (index+2 >= end ||
                    !isHex(p_uriSpec.charAt(index+1)) ||
                    !isHex(p_uriSpec.charAt(index+2))) {
                        throw new MalformedURIException(
                            "Path contains invalid escape sequence!");
                    }
                    index += 2;
                }
                else if (!isPathCharacter(testChar)) {
                    if (testChar == '?' || testChar == '#') {
                        break;
                    }
                    throw new MalformedURIException(
                        "Path contains invalid character: " + testChar);
                }
                ++index;
            }
        }
        else {

            while (index < end) {
                testChar = p_uriSpec.charAt(index);

                if (testChar == '?' || testChar == '#') {
                    break;
                }

                if (testChar == '%') {
                    if (index+2 >= end ||
                    !isHex(p_uriSpec.charAt(index+1)) ||
                    !isHex(p_uriSpec.charAt(index+2))) {
                        throw new MalformedURIException(
                            "Opaque part contains invalid escape sequence!");
                    }
                    index += 2;
                }
                else if (!isURICharacter(testChar)) {
                    throw new MalformedURIException(
                        "Opaque part contains invalid character: " + testChar);
                }
                ++index;
            }
        }
    }
    m_path = p_uriSpec.substring(start, index);

    if (testChar == '?') {
      index++;
      start = index;
      while (index < end) {
        testChar = p_uriSpec.charAt(index);
        if (testChar == '#') {
          break;
        }
        if (testChar == '%') {
           if (index+2 >= end ||
              !isHex(p_uriSpec.charAt(index+1)) ||
              !isHex(p_uriSpec.charAt(index+2))) {
            throw new MalformedURIException(
                    "Query string contains invalid escape sequence!");
           }
           index += 2;
        }
        else if (!isURICharacter(testChar)) {
          throw new MalformedURIException(
                "Query string contains invalid character: " + testChar);
        }
        index++;
      }
      m_queryString = p_uriSpec.substring(start, index);
    }

    if (testChar == '#') {
      index++;
      start = index;
      while (index < end) {
        testChar = p_uriSpec.charAt(index);

        if (testChar == '%') {
           if (index+2 >= end ||
              !isHex(p_uriSpec.charAt(index+1)) ||
              !isHex(p_uriSpec.charAt(index+2))) {
            throw new MalformedURIException(
                    "Fragment contains invalid escape sequence!");
           }
           index += 2;
        }
        else if (!isURICharacter(testChar)) {
          throw new MalformedURIException(
                "Fragment contains invalid character: "+testChar);
        }
        index++;
      }
      m_fragment = p_uriSpec.substring(start, index);
    }
  }


  public String getScheme() {
    return m_scheme;
  }


  public String getSchemeSpecificPart() {
    final StringBuilder schemespec = new StringBuilder();

    if (m_host != null || m_regAuthority != null) {
      schemespec.append("if (m_host != null) {

        if (m_userinfo != null) {
          schemespec.append(m_userinfo);
          schemespec.append('@');
        }

        schemespec.append(m_host);

        if (m_port != -1) {
          schemespec.append(':');
          schemespec.append(m_port);
        }
      }
      else {
        schemespec.append(m_regAuthority);
      }
    }

    if (m_path != null) {
      schemespec.append((m_path));
    }

    if (m_queryString != null) {
      schemespec.append('?');
      schemespec.append(m_queryString);
    }

    if (m_fragment != null) {
      schemespec.append('#');
      schemespec.append(m_fragment);
    }

    return schemespec.toString();
  }


  public String getUserinfo() {
    return m_userinfo;
  }


  public String getHost() {
    return m_host;
  }


  public int getPort() {
    return m_port;
  }


  public String getRegBasedAuthority() {
    return m_regAuthority;
  }


  public String getAuthority() {
      final StringBuilder authority = new StringBuilder();
      if (m_host != null || m_regAuthority != null) {
          authority.append("if (m_host != null) {

              if (m_userinfo != null) {
                  authority.append(m_userinfo);
                  authority.append('@');
              }

              authority.append(m_host);

              if (m_port != -1) {
                  authority.append(':');
                  authority.append(m_port);
              }
          }
          else {
              authority.append(m_regAuthority);
          }
      }
      return authority.toString();
  }


  public String getPath(boolean p_includeQueryString,
                        boolean p_includeFragment) {
    final StringBuilder pathString = new StringBuilder(m_path);

    if (p_includeQueryString && m_queryString != null) {
      pathString.append('?');
      pathString.append(m_queryString);
    }

    if (p_includeFragment && m_fragment != null) {
      pathString.append('#');
      pathString.append(m_fragment);
    }
    return pathString.toString();
  }


  public String getPath() {
    return m_path;
  }


  public String getQueryString() {
    return m_queryString;
  }


  public String getFragment() {
    return m_fragment;
  }


  public void setScheme(String p_scheme) throws MalformedURIException {
    if (p_scheme == null) {
      throw new MalformedURIException(
                "Cannot set scheme from null string!");
    }
    if (!isConformantSchemeName(p_scheme)) {
      throw new MalformedURIException("The scheme is not conformant.");
    }

    m_scheme = p_scheme.toLowerCase();
  }


  public void setUserinfo(String p_userinfo) throws MalformedURIException {
    if (p_userinfo == null) {
      m_userinfo = null;
      return;
    }
    else {
      if (m_host == null) {
        throw new MalformedURIException(
                     "Userinfo cannot be set when host is null!");
      }

      int index = 0;
      int end = p_userinfo.length();
      char testChar = '\0';
      while (index < end) {
        testChar = p_userinfo.charAt(index);
        if (testChar == '%') {
          if (index+2 >= end ||
              !isHex(p_userinfo.charAt(index+1)) ||
              !isHex(p_userinfo.charAt(index+2))) {
            throw new MalformedURIException(
                  "Userinfo contains invalid escape sequence!");
          }
        }
        else if (!isUserinfoCharacter(testChar)) {
          throw new MalformedURIException(
                  "Userinfo contains invalid character:"+testChar);
        }
        index++;
      }
    }
    m_userinfo = p_userinfo;
  }


  public void setHost(String p_host) throws MalformedURIException {
    if (p_host == null || p_host.length() == 0) {
      if (p_host != null) {
        m_regAuthority = null;
      }
      m_host = p_host;
      m_userinfo = null;
      m_port = -1;
      return;
    }
    else if (!isWellFormedAddress(p_host)) {
      throw new MalformedURIException("Host is not a well formed address!");
    }
    m_host = p_host;
    m_regAuthority = null;
  }


  public void setPort(int p_port) throws MalformedURIException {
    if (p_port >= 0 && p_port <= 65535) {
      if (m_host == null) {
        throw new MalformedURIException(
                      "Port cannot be set when host is null!");
      }
    }
    else if (p_port != -1) {
      throw new MalformedURIException("Invalid port number!");
    }
    m_port = p_port;
  }


  public void setRegBasedAuthority(String authority)
    throws MalformedURIException {

        if (authority == null) {
          m_regAuthority = null;
          return;
        }
        else if (authority.length() < 1 ||
          !isValidRegistryBasedAuthority(authority) ||
          authority.indexOf('/') != -1) {
      throw new MalformedURIException("Registry based authority is not well formed.");
        }
        m_regAuthority = authority;
        m_host = null;
        m_userinfo = null;
        m_port = -1;
  }


  public void setPath(String p_path) throws MalformedURIException {
    if (p_path == null) {
      m_path = null;
      m_queryString = null;
      m_fragment = null;
    }
    else {
      initializePath(p_path, 0);
    }
  }


  public void appendPath(String p_addToPath)
                         throws MalformedURIException {
    if (p_addToPath == null || p_addToPath.trim().length() == 0) {
      return;
    }

    if (!isURIString(p_addToPath)) {
      throw new MalformedURIException(
              "Path contains invalid character!");
    }

    if (m_path == null || m_path.trim().length() == 0) {
      if (p_addToPath.startsWith("/")) {
        m_path = p_addToPath;
      }
      else {
        m_path = "/" + p_addToPath;
      }
    }
    else if (m_path.endsWith("/")) {
      if (p_addToPath.startsWith("/")) {
        m_path = m_path.concat(p_addToPath.substring(1));
      }
      else {
        m_path = m_path.concat(p_addToPath);
      }
    }
    else {
      if (p_addToPath.startsWith("/")) {
        m_path = m_path.concat(p_addToPath);
      }
      else {
        m_path = m_path.concat("/" + p_addToPath);
      }
    }
  }


  public void setQueryString(String p_queryString) throws MalformedURIException {
    if (p_queryString == null) {
      m_queryString = null;
    }
    else if (!isGenericURI()) {
      throw new MalformedURIException(
              "Query string can only be set for a generic URI!");
    }
    else if (getPath() == null) {
      throw new MalformedURIException(
              "Query string cannot be set when path is null!");
    }
    else if (!isURIString(p_queryString)) {
      throw new MalformedURIException(
              "Query string contains invalid character!");
    }
    else {
      m_queryString = p_queryString;
    }
  }


  public void setFragment(String p_fragment) throws MalformedURIException {
    if (p_fragment == null) {
      m_fragment = null;
    }
    else if (!isGenericURI()) {
      throw new MalformedURIException(
         "Fragment can only be set for a generic URI!");
    }
    else if (getPath() == null) {
      throw new MalformedURIException(
              "Fragment cannot be set when path is null!");
    }
    else if (!isURIString(p_fragment)) {
      throw new MalformedURIException(
              "Fragment contains invalid character!");
    }
    else {
      m_fragment = p_fragment;
    }
  }


  @Override
  public boolean equals(Object p_test) {
    if (p_test instanceof URI) {
      URI testURI = (URI) p_test;
      if (((m_scheme == null && testURI.m_scheme == null) ||
           (m_scheme != null && testURI.m_scheme != null &&
            m_scheme.equals(testURI.m_scheme))) &&
          ((m_userinfo == null && testURI.m_userinfo == null) ||
           (m_userinfo != null && testURI.m_userinfo != null &&
            m_userinfo.equals(testURI.m_userinfo))) &&
          ((m_host == null && testURI.m_host == null) ||
           (m_host != null && testURI.m_host != null &&
            m_host.equals(testURI.m_host))) &&
            m_port == testURI.m_port &&
          ((m_path == null && testURI.m_path == null) ||
           (m_path != null && testURI.m_path != null &&
            m_path.equals(testURI.m_path))) &&
          ((m_queryString == null && testURI.m_queryString == null) ||
           (m_queryString != null && testURI.m_queryString != null &&
            m_queryString.equals(testURI.m_queryString))) &&
          ((m_fragment == null && testURI.m_fragment == null) ||
           (m_fragment != null && testURI.m_fragment != null &&
            m_fragment.equals(testURI.m_fragment)))) {
        return true;
      }
    }
    return false;
  }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.m_scheme);
        hash = 47 * hash + Objects.hashCode(this.m_userinfo);
        hash = 47 * hash + Objects.hashCode(this.m_host);
        hash = 47 * hash + this.m_port;
        hash = 47 * hash + Objects.hashCode(this.m_path);
        hash = 47 * hash + Objects.hashCode(this.m_queryString);
        hash = 47 * hash + Objects.hashCode(this.m_fragment);
        return hash;
    }


  @Override
  public String toString() {
    final StringBuilder uriSpecString = new StringBuilder();

    if (m_scheme != null) {
      uriSpecString.append(m_scheme);
      uriSpecString.append(':');
    }
    uriSpecString.append(getSchemeSpecificPart());
    return uriSpecString.toString();
  }


  public boolean isGenericURI() {
    return (m_host != null);
  }


  public boolean isAbsoluteURI() {
      return (m_scheme != null);
  }


  public static boolean isConformantSchemeName(String p_scheme) {
    if (p_scheme == null || p_scheme.trim().length() == 0) {
      return false;
    }

    if (!isAlpha(p_scheme.charAt(0))) {
      return false;
    }

    char testChar;
    int schemeLength = p_scheme.length();
    for (int i = 1; i < schemeLength; ++i) {
      testChar = p_scheme.charAt(i);
      if (!isSchemeCharacter(testChar)) {
        return false;
      }
    }

    return true;
  }


  public static boolean isWellFormedAddress(String address) {
    if (address == null) {
      return false;
    }

    int addrLength = address.length();
    if (addrLength == 0) {
      return false;
    }

    if (address.startsWith("[")) {
      return isWellFormedIPv6Reference(address);
    }

    if (address.startsWith(".") ||
        address.startsWith("-") ||
        address.endsWith("-")) {
      return false;
    }

    int index = address.lastIndexOf('.');
    if (address.endsWith(".")) {
      index = address.substring(0, index).lastIndexOf('.');
    }

    if (index+1 < addrLength && isDigit(address.charAt(index+1))) {
      return isWellFormedIPv4Address(address);
    }
    else {
      if (addrLength > 255) {
        return false;
      }

      char testChar;
      int labelCharCount = 0;

      for (int i = 0; i < addrLength; i++) {
        testChar = address.charAt(i);
        if (testChar == '.') {
          if (!isAlphanum(address.charAt(i-1))) {
            return false;
          }
          if (i+1 < addrLength && !isAlphanum(address.charAt(i+1))) {
            return false;
          }
          labelCharCount = 0;
        }
        else if (!isAlphanum(testChar) && testChar != '-') {
          return false;
        }
        else if (++labelCharCount > 63) {
          return false;
        }
      }
    }
    return true;
  }


  public static boolean isWellFormedIPv4Address(String address) {

      int addrLength = address.length();
      char testChar;
      int numDots = 0;
      int numDigits = 0;

      for (int i = 0; i < addrLength; i++) {
        testChar = address.charAt(i);
        if (testChar == '.') {
          if ((i > 0 && !isDigit(address.charAt(i-1))) ||
              (i+1 < addrLength && !isDigit(address.charAt(i+1)))) {
            return false;
          }
          numDigits = 0;
          if (++numDots > 3) {
            return false;
          }
        }
        else if (!isDigit(testChar)) {
          return false;
        }
        else if (++numDigits > 3) {
          return false;
        }
        else if (numDigits == 3) {
          char first = address.charAt(i-2);
          char second = address.charAt(i-1);
          if (!(first < '2' ||
               (first == '2' &&
               (second < '5' ||
               (second == '5' && testChar <= '5'))))) {
            return false;
          }
        }
      }
      return (numDots == 3);
  }


  public static boolean isWellFormedIPv6Reference(String address) {

      int addrLength = address.length();
      int index = 1;
      int end = addrLength-1;

      if (!(addrLength > 2 && address.charAt(0) == '['
          && address.charAt(end) == ']')) {
          return false;
      }

      int [] counter = new int[1];

      index = scanHexSequence(address, index, end, counter);
      if (index == -1) {
          return false;
      }
      else if (index == end) {
          return (counter[0] == 8);
      }

      if (index+1 < end && address.charAt(index) == ':') {
          if (address.charAt(index+1) == ':') {
              if (++counter[0] > 8) {
                  return false;
              }
              index += 2;
              if (index == end) {
                 return true;
              }
          }
          else {
              return (counter[0] == 6) &&
                  isWellFormedIPv4Address(address.substring(index+1, end));
          }
      }
      else {
          return false;
      }

      int prevCount = counter[0];
      index = scanHexSequence(address, index, end, counter);

      return (index == end) ||
          (index != -1 && isWellFormedIPv4Address(
          address.substring((counter[0] > prevCount) ? index+1 : index, end)));
  }


  private static int scanHexSequence (String address, int index, int end, int [] counter) {

      char testChar;
      int numDigits = 0;
      int start = index;

      for (; index < end; ++index) {
        testChar = address.charAt(index);
        if (testChar == ':') {
            if (numDigits > 0 && ++counter[0] > 8) {
                return -1;
            }
            if (numDigits == 0 || ((index+1 < end) && address.charAt(index+1) == ':')) {
                return index;
            }
            numDigits = 0;
        }
        else if (!isHex(testChar)) {
            if (testChar == '.' && numDigits < 4 && numDigits > 0 && counter[0] <= 6) {
                int back = index - numDigits - 1;
                return (back >= start) ? back : (back+1);
            }
            return -1;
        }
        else if (++numDigits > 4) {
            return -1;
        }
      }
      return (numDigits > 0 && ++counter[0] <= 8) ? end : -1;
  }



  private static boolean isDigit(char p_char) {
    return p_char >= '0' && p_char <= '9';
  }


  private static boolean isHex(char p_char) {
    return (p_char <= 'f' && (fgLookupTable[p_char] & ASCII_HEX_CHARACTERS) != 0);
  }


  private static boolean isAlpha(char p_char) {
      return ((p_char >= 'a' && p_char <= 'z') || (p_char >= 'A' && p_char <= 'Z' ));
  }


  private static boolean isAlphanum(char p_char) {
     return (p_char <= 'z' && (fgLookupTable[p_char] & MASK_ALPHA_NUMERIC) != 0);
  }


  private static boolean isReservedCharacter(char p_char) {
     return (p_char <= ']' && (fgLookupTable[p_char] & RESERVED_CHARACTERS) != 0);
  }


  private static boolean isUnreservedCharacter(char p_char) {
     return (p_char <= '~' && (fgLookupTable[p_char] & MASK_UNRESERVED_MASK) != 0);
  }


  private static boolean isURICharacter (char p_char) {
      return (p_char <= '~' && (fgLookupTable[p_char] & MASK_URI_CHARACTER) != 0);
  }


  private static boolean isSchemeCharacter (char p_char) {
      return (p_char <= 'z' && (fgLookupTable[p_char] & MASK_SCHEME_CHARACTER) != 0);
  }


  private static boolean isUserinfoCharacter (char p_char) {
      return (p_char <= 'z' && (fgLookupTable[p_char] & MASK_USERINFO_CHARACTER) != 0);
  }


  private static boolean isPathCharacter (char p_char) {
      return (p_char <= '~' && (fgLookupTable[p_char] & MASK_PATH_CHARACTER) != 0);
  }



  private static boolean isURIString(String p_uric) {
    if (p_uric == null) {
      return false;
    }
    int end = p_uric.length();
    char testChar = '\0';
    for (int i = 0; i < end; i++) {
      testChar = p_uric.charAt(i);
      if (testChar == '%') {
        if (i+2 >= end ||
            !isHex(p_uric.charAt(i+1)) ||
            !isHex(p_uric.charAt(i+2))) {
          return false;
        }
        else {
          i += 2;
          continue;
        }
      }
      if (isURICharacter(testChar)) {
          continue;
      }
      else {
        return false;
      }
    }
    return true;
  }
}
