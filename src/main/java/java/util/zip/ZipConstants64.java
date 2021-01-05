

package java.util.zip;



class ZipConstants64 {


    static final long ZIP64_ENDSIG = 0x06064b50L;  static final long ZIP64_LOCSIG = 0x07064b50L;  static final int  ZIP64_ENDHDR = 56;           static final int  ZIP64_LOCHDR = 20;           static final int  ZIP64_EXTHDR = 24;           static final int  ZIP64_EXTID  = 0x0001;       static final int  ZIP64_MAGICCOUNT = 0xFFFF;
    static final long ZIP64_MAGICVAL = 0xFFFFFFFFL;


    static final int  ZIP64_ENDLEN = 4;       static final int  ZIP64_ENDVEM = 12;      static final int  ZIP64_ENDVER = 14;      static final int  ZIP64_ENDNMD = 16;      static final int  ZIP64_ENDDSK = 20;      static final int  ZIP64_ENDTOD = 24;      static final int  ZIP64_ENDTOT = 32;      static final int  ZIP64_ENDSIZ = 40;      static final int  ZIP64_ENDOFF = 48;      static final int  ZIP64_ENDEXT = 56;      static final int  ZIP64_LOCDSK = 4;       static final int  ZIP64_LOCOFF = 8;       static final int  ZIP64_LOCTOT = 16;      static final int  ZIP64_EXTCRC = 4;       static final int  ZIP64_EXTSIZ = 8;       static final int  ZIP64_EXTLEN = 16;      static final int EFS = 0x800;       static final int  EXTID_ZIP64 = 0x0001;    static final int  EXTID_NTFS  = 0x000a;    static final int  EXTID_UNIX  = 0x000d;    static final int  EXTID_EXTT  = 0x5455;    static final int  EXTT_FLAG_LMT = 0x1;       static final int  EXTT_FLAG_LAT = 0x2;       static final int  EXTT_FLAT_CT  = 0x4;       private ZipConstants64() {}
}
