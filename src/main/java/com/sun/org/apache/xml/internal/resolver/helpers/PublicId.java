
package com.sun.org.apache.xml.internal.resolver.helpers;


public abstract class PublicId {
  protected PublicId() { }


  public static String normalize(String publicId) {
    String normal = publicId.replace('\t', ' ');
    normal = normal.replace('\r', ' ');
    normal = normal.replace('\n', ' ');
    normal = normal.trim();

    int pos;

    while ((pos = normal.indexOf("  ")) >= 0) {
      normal = normal.substring(0, pos) + normal.substring(pos+1);
    }

    return normal;
  }


  public static String encodeURN(String publicId) {
    String urn = PublicId.normalize(publicId);

    urn = PublicId.stringReplace(urn, "%", "%25");
    urn = PublicId.stringReplace(urn, ";", "%3B");
    urn = PublicId.stringReplace(urn, "'", "%27");
    urn = PublicId.stringReplace(urn, "?", "%3F");
    urn = PublicId.stringReplace(urn, "#", "%23");
    urn = PublicId.stringReplace(urn, "+", "%2B");
    urn = PublicId.stringReplace(urn, " ", "+");
    urn = PublicId.stringReplace(urn, "::", ";");
    urn = PublicId.stringReplace(urn, ":", "%3A");
    urn = PublicId.stringReplace(urn, "urn = PublicId.stringReplace(urn, "/", "%2F");

    return "urn:publicid:" + urn;
  }


  public static String decodeURN(String urn) {
    String publicId = "";

    if (urn.startsWith("urn:publicid:")) {
      publicId = urn.substring(13);
    } else {
      return urn;
    }

    publicId = PublicId.stringReplace(publicId, "%2F", "/");
    publicId = PublicId.stringReplace(publicId, ":", "publicId = PublicId.stringReplace(publicId, "%3A", ":");
    publicId = PublicId.stringReplace(publicId, ";", "::");
    publicId = PublicId.stringReplace(publicId, "+", " ");
    publicId = PublicId.stringReplace(publicId, "%2B", "+");
    publicId = PublicId.stringReplace(publicId, "%23", "#");
    publicId = PublicId.stringReplace(publicId, "%3F", "?");
    publicId = PublicId.stringReplace(publicId, "%27", "'");
    publicId = PublicId.stringReplace(publicId, "%3B", ";");
    publicId = PublicId.stringReplace(publicId, "%25", "%");

    return publicId;
    }


  private static String stringReplace(String str,
                                      String oldStr,
                                      String newStr) {

    String result = "";
    int pos = str.indexOf(oldStr);

    while (pos >= 0) {
      result += str.substring(0, pos);
      result += newStr;
      str = str.substring(pos+1);

      pos = str.indexOf(oldStr);
    }

    return result + str;
  }
}
