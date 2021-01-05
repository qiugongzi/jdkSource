

package java.lang;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;


public final
class Character implements java.io.Serializable, Comparable<Character> {

    public static final int MIN_RADIX = 2;


    public static final int MAX_RADIX = 36;


    public static final char MIN_VALUE = '\u0000';


    public static final char MAX_VALUE = '\uFFFF';


    @SuppressWarnings("unchecked")
    public static final Class<Character> TYPE = (Class<Character>) Class.getPrimitiveClass("char");






    public static final byte UNASSIGNED = 0;


    public static final byte UPPERCASE_LETTER = 1;


    public static final byte LOWERCASE_LETTER = 2;


    public static final byte TITLECASE_LETTER = 3;


    public static final byte MODIFIER_LETTER = 4;


    public static final byte OTHER_LETTER = 5;


    public static final byte NON_SPACING_MARK = 6;


    public static final byte ENCLOSING_MARK = 7;


    public static final byte COMBINING_SPACING_MARK = 8;


    public static final byte DECIMAL_DIGIT_NUMBER        = 9;


    public static final byte LETTER_NUMBER = 10;


    public static final byte OTHER_NUMBER = 11;


    public static final byte SPACE_SEPARATOR = 12;


    public static final byte LINE_SEPARATOR = 13;


    public static final byte PARAGRAPH_SEPARATOR = 14;


    public static final byte CONTROL = 15;


    public static final byte FORMAT = 16;


    public static final byte PRIVATE_USE = 18;


    public static final byte SURROGATE = 19;


    public static final byte DASH_PUNCTUATION = 20;


    public static final byte START_PUNCTUATION = 21;


    public static final byte END_PUNCTUATION = 22;


    public static final byte CONNECTOR_PUNCTUATION = 23;


    public static final byte OTHER_PUNCTUATION = 24;


    public static final byte MATH_SYMBOL = 25;


    public static final byte CURRENCY_SYMBOL = 26;


    public static final byte MODIFIER_SYMBOL = 27;


    public static final byte OTHER_SYMBOL = 28;


    public static final byte INITIAL_QUOTE_PUNCTUATION = 29;


    public static final byte FINAL_QUOTE_PUNCTUATION = 30;


    static final int ERROR = 0xFFFFFFFF;



    public static final byte DIRECTIONALITY_UNDEFINED = -1;


    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT = 0;


    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT = 1;


    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC = 2;


    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER = 3;


    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR = 4;


    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR = 5;


    public static final byte DIRECTIONALITY_ARABIC_NUMBER = 6;


    public static final byte DIRECTIONALITY_COMMON_NUMBER_SEPARATOR = 7;


    public static final byte DIRECTIONALITY_NONSPACING_MARK = 8;


    public static final byte DIRECTIONALITY_BOUNDARY_NEUTRAL = 9;


    public static final byte DIRECTIONALITY_PARAGRAPH_SEPARATOR = 10;


    public static final byte DIRECTIONALITY_SEGMENT_SEPARATOR = 11;


    public static final byte DIRECTIONALITY_WHITESPACE = 12;


    public static final byte DIRECTIONALITY_OTHER_NEUTRALS = 13;


    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING = 14;


    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE = 15;


    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING = 16;


    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE = 17;


    public static final byte DIRECTIONALITY_POP_DIRECTIONAL_FORMAT = 18;


    public static final char MIN_HIGH_SURROGATE = '\uD800';


    public static final char MAX_HIGH_SURROGATE = '\uDBFF';


    public static final char MIN_LOW_SURROGATE  = '\uDC00';


    public static final char MAX_LOW_SURROGATE  = '\uDFFF';


    public static final char MIN_SURROGATE = MIN_HIGH_SURROGATE;


    public static final char MAX_SURROGATE = MAX_LOW_SURROGATE;


    public static final int MIN_SUPPLEMENTARY_CODE_POINT = 0x010000;


    public static final int MIN_CODE_POINT = 0x000000;


    public static final int MAX_CODE_POINT = 0X10FFFF;



    public static class Subset  {

        private String name;


        protected Subset(String name) {
            if (name == null) {
                throw new NullPointerException("name");
            }
            this.name = name;
        }


        public final boolean equals(Object obj) {
            return (this == obj);
        }


        public final int hashCode() {
            return super.hashCode();
        }


        public final String toString() {
            return name;
        }
    }

    public static final class UnicodeBlock extends Subset {

        private static Map<String, UnicodeBlock> map = new HashMap<>(256);


        private UnicodeBlock(String idName) {
            super(idName);
            map.put(idName, this);
        }


        private UnicodeBlock(String idName, String alias) {
            this(idName);
            map.put(alias, this);
        }


        private UnicodeBlock(String idName, String... aliases) {
            this(idName);
            for (String alias : aliases)
                map.put(alias, this);
        }


        public static final UnicodeBlock  BASIC_LATIN =
            new UnicodeBlock("BASIC_LATIN",
                             "BASIC LATIN",
                             "BASICLATIN");


        public static final UnicodeBlock LATIN_1_SUPPLEMENT =
            new UnicodeBlock("LATIN_1_SUPPLEMENT",
                             "LATIN-1 SUPPLEMENT",
                             "LATIN-1SUPPLEMENT");


        public static final UnicodeBlock LATIN_EXTENDED_A =
            new UnicodeBlock("LATIN_EXTENDED_A",
                             "LATIN EXTENDED-A",
                             "LATINEXTENDED-A");


        public static final UnicodeBlock LATIN_EXTENDED_B =
            new UnicodeBlock("LATIN_EXTENDED_B",
                             "LATIN EXTENDED-B",
                             "LATINEXTENDED-B");


        public static final UnicodeBlock IPA_EXTENSIONS =
            new UnicodeBlock("IPA_EXTENSIONS",
                             "IPA EXTENSIONS",
                             "IPAEXTENSIONS");


        public static final UnicodeBlock SPACING_MODIFIER_LETTERS =
            new UnicodeBlock("SPACING_MODIFIER_LETTERS",
                             "SPACING MODIFIER LETTERS",
                             "SPACINGMODIFIERLETTERS");


        public static final UnicodeBlock COMBINING_DIACRITICAL_MARKS =
            new UnicodeBlock("COMBINING_DIACRITICAL_MARKS",
                             "COMBINING DIACRITICAL MARKS",
                             "COMBININGDIACRITICALMARKS");


        public static final UnicodeBlock GREEK =
            new UnicodeBlock("GREEK",
                             "GREEK AND COPTIC",
                             "GREEKANDCOPTIC");


        public static final UnicodeBlock CYRILLIC =
            new UnicodeBlock("CYRILLIC");


        public static final UnicodeBlock ARMENIAN =
            new UnicodeBlock("ARMENIAN");


        public static final UnicodeBlock HEBREW =
            new UnicodeBlock("HEBREW");


        public static final UnicodeBlock ARABIC =
            new UnicodeBlock("ARABIC");


        public static final UnicodeBlock DEVANAGARI =
            new UnicodeBlock("DEVANAGARI");


        public static final UnicodeBlock BENGALI =
            new UnicodeBlock("BENGALI");


        public static final UnicodeBlock GURMUKHI =
            new UnicodeBlock("GURMUKHI");


        public static final UnicodeBlock GUJARATI =
            new UnicodeBlock("GUJARATI");


        public static final UnicodeBlock ORIYA =
            new UnicodeBlock("ORIYA");


        public static final UnicodeBlock TAMIL =
            new UnicodeBlock("TAMIL");


        public static final UnicodeBlock TELUGU =
            new UnicodeBlock("TELUGU");


        public static final UnicodeBlock KANNADA =
            new UnicodeBlock("KANNADA");


        public static final UnicodeBlock MALAYALAM =
            new UnicodeBlock("MALAYALAM");


        public static final UnicodeBlock THAI =
            new UnicodeBlock("THAI");


        public static final UnicodeBlock LAO =
            new UnicodeBlock("LAO");


        public static final UnicodeBlock TIBETAN =
            new UnicodeBlock("TIBETAN");


        public static final UnicodeBlock GEORGIAN =
            new UnicodeBlock("GEORGIAN");


        public static final UnicodeBlock HANGUL_JAMO =
            new UnicodeBlock("HANGUL_JAMO",
                             "HANGUL JAMO",
                             "HANGULJAMO");


        public static final UnicodeBlock LATIN_EXTENDED_ADDITIONAL =
            new UnicodeBlock("LATIN_EXTENDED_ADDITIONAL",
                             "LATIN EXTENDED ADDITIONAL",
                             "LATINEXTENDEDADDITIONAL");


        public static final UnicodeBlock GREEK_EXTENDED =
            new UnicodeBlock("GREEK_EXTENDED",
                             "GREEK EXTENDED",
                             "GREEKEXTENDED");


        public static final UnicodeBlock GENERAL_PUNCTUATION =
            new UnicodeBlock("GENERAL_PUNCTUATION",
                             "GENERAL PUNCTUATION",
                             "GENERALPUNCTUATION");


        public static final UnicodeBlock SUPERSCRIPTS_AND_SUBSCRIPTS =
            new UnicodeBlock("SUPERSCRIPTS_AND_SUBSCRIPTS",
                             "SUPERSCRIPTS AND SUBSCRIPTS",
                             "SUPERSCRIPTSANDSUBSCRIPTS");


        public static final UnicodeBlock CURRENCY_SYMBOLS =
            new UnicodeBlock("CURRENCY_SYMBOLS",
                             "CURRENCY SYMBOLS",
                             "CURRENCYSYMBOLS");


        public static final UnicodeBlock COMBINING_MARKS_FOR_SYMBOLS =
            new UnicodeBlock("COMBINING_MARKS_FOR_SYMBOLS",
                             "COMBINING DIACRITICAL MARKS FOR SYMBOLS",
                             "COMBININGDIACRITICALMARKSFORSYMBOLS",
                             "COMBINING MARKS FOR SYMBOLS",
                             "COMBININGMARKSFORSYMBOLS");


        public static final UnicodeBlock LETTERLIKE_SYMBOLS =
            new UnicodeBlock("LETTERLIKE_SYMBOLS",
                             "LETTERLIKE SYMBOLS",
                             "LETTERLIKESYMBOLS");


        public static final UnicodeBlock NUMBER_FORMS =
            new UnicodeBlock("NUMBER_FORMS",
                             "NUMBER FORMS",
                             "NUMBERFORMS");


        public static final UnicodeBlock ARROWS =
            new UnicodeBlock("ARROWS");


        public static final UnicodeBlock MATHEMATICAL_OPERATORS =
            new UnicodeBlock("MATHEMATICAL_OPERATORS",
                             "MATHEMATICAL OPERATORS",
                             "MATHEMATICALOPERATORS");


        public static final UnicodeBlock MISCELLANEOUS_TECHNICAL =
            new UnicodeBlock("MISCELLANEOUS_TECHNICAL",
                             "MISCELLANEOUS TECHNICAL",
                             "MISCELLANEOUSTECHNICAL");


        public static final UnicodeBlock CONTROL_PICTURES =
            new UnicodeBlock("CONTROL_PICTURES",
                             "CONTROL PICTURES",
                             "CONTROLPICTURES");


        public static final UnicodeBlock OPTICAL_CHARACTER_RECOGNITION =
            new UnicodeBlock("OPTICAL_CHARACTER_RECOGNITION",
                             "OPTICAL CHARACTER RECOGNITION",
                             "OPTICALCHARACTERRECOGNITION");


        public static final UnicodeBlock ENCLOSED_ALPHANUMERICS =
            new UnicodeBlock("ENCLOSED_ALPHANUMERICS",
                             "ENCLOSED ALPHANUMERICS",
                             "ENCLOSEDALPHANUMERICS");


        public static final UnicodeBlock BOX_DRAWING =
            new UnicodeBlock("BOX_DRAWING",
                             "BOX DRAWING",
                             "BOXDRAWING");


        public static final UnicodeBlock BLOCK_ELEMENTS =
            new UnicodeBlock("BLOCK_ELEMENTS",
                             "BLOCK ELEMENTS",
                             "BLOCKELEMENTS");


        public static final UnicodeBlock GEOMETRIC_SHAPES =
            new UnicodeBlock("GEOMETRIC_SHAPES",
                             "GEOMETRIC SHAPES",
                             "GEOMETRICSHAPES");


        public static final UnicodeBlock MISCELLANEOUS_SYMBOLS =
            new UnicodeBlock("MISCELLANEOUS_SYMBOLS",
                             "MISCELLANEOUS SYMBOLS",
                             "MISCELLANEOUSSYMBOLS");


        public static final UnicodeBlock DINGBATS =
            new UnicodeBlock("DINGBATS");


        public static final UnicodeBlock CJK_SYMBOLS_AND_PUNCTUATION =
            new UnicodeBlock("CJK_SYMBOLS_AND_PUNCTUATION",
                             "CJK SYMBOLS AND PUNCTUATION",
                             "CJKSYMBOLSANDPUNCTUATION");


        public static final UnicodeBlock HIRAGANA =
            new UnicodeBlock("HIRAGANA");


        public static final UnicodeBlock KATAKANA =
            new UnicodeBlock("KATAKANA");


        public static final UnicodeBlock BOPOMOFO =
            new UnicodeBlock("BOPOMOFO");


        public static final UnicodeBlock HANGUL_COMPATIBILITY_JAMO =
            new UnicodeBlock("HANGUL_COMPATIBILITY_JAMO",
                             "HANGUL COMPATIBILITY JAMO",
                             "HANGULCOMPATIBILITYJAMO");


        public static final UnicodeBlock KANBUN =
            new UnicodeBlock("KANBUN");


        public static final UnicodeBlock ENCLOSED_CJK_LETTERS_AND_MONTHS =
            new UnicodeBlock("ENCLOSED_CJK_LETTERS_AND_MONTHS",
                             "ENCLOSED CJK LETTERS AND MONTHS",
                             "ENCLOSEDCJKLETTERSANDMONTHS");


        public static final UnicodeBlock CJK_COMPATIBILITY =
            new UnicodeBlock("CJK_COMPATIBILITY",
                             "CJK COMPATIBILITY",
                             "CJKCOMPATIBILITY");


        public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS =
            new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS",
                             "CJK UNIFIED IDEOGRAPHS",
                             "CJKUNIFIEDIDEOGRAPHS");


        public static final UnicodeBlock HANGUL_SYLLABLES =
            new UnicodeBlock("HANGUL_SYLLABLES",
                             "HANGUL SYLLABLES",
                             "HANGULSYLLABLES");


        public static final UnicodeBlock PRIVATE_USE_AREA =
            new UnicodeBlock("PRIVATE_USE_AREA",
                             "PRIVATE USE AREA",
                             "PRIVATEUSEAREA");


        public static final UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS =
            new UnicodeBlock("CJK_COMPATIBILITY_IDEOGRAPHS",
                             "CJK COMPATIBILITY IDEOGRAPHS",
                             "CJKCOMPATIBILITYIDEOGRAPHS");


        public static final UnicodeBlock ALPHABETIC_PRESENTATION_FORMS =
            new UnicodeBlock("ALPHABETIC_PRESENTATION_FORMS",
                             "ALPHABETIC PRESENTATION FORMS",
                             "ALPHABETICPRESENTATIONFORMS");


        public static final UnicodeBlock ARABIC_PRESENTATION_FORMS_A =
            new UnicodeBlock("ARABIC_PRESENTATION_FORMS_A",
                             "ARABIC PRESENTATION FORMS-A",
                             "ARABICPRESENTATIONFORMS-A");


        public static final UnicodeBlock COMBINING_HALF_MARKS =
            new UnicodeBlock("COMBINING_HALF_MARKS",
                             "COMBINING HALF MARKS",
                             "COMBININGHALFMARKS");


        public static final UnicodeBlock CJK_COMPATIBILITY_FORMS =
            new UnicodeBlock("CJK_COMPATIBILITY_FORMS",
                             "CJK COMPATIBILITY FORMS",
                             "CJKCOMPATIBILITYFORMS");


        public static final UnicodeBlock SMALL_FORM_VARIANTS =
            new UnicodeBlock("SMALL_FORM_VARIANTS",
                             "SMALL FORM VARIANTS",
                             "SMALLFORMVARIANTS");


        public static final UnicodeBlock ARABIC_PRESENTATION_FORMS_B =
            new UnicodeBlock("ARABIC_PRESENTATION_FORMS_B",
                             "ARABIC PRESENTATION FORMS-B",
                             "ARABICPRESENTATIONFORMS-B");


        public static final UnicodeBlock HALFWIDTH_AND_FULLWIDTH_FORMS =
            new UnicodeBlock("HALFWIDTH_AND_FULLWIDTH_FORMS",
                             "HALFWIDTH AND FULLWIDTH FORMS",
                             "HALFWIDTHANDFULLWIDTHFORMS");


        public static final UnicodeBlock SPECIALS =
            new UnicodeBlock("SPECIALS");


        @Deprecated
        public static final UnicodeBlock SURROGATES_AREA =
            new UnicodeBlock("SURROGATES_AREA");


        public static final UnicodeBlock SYRIAC =
            new UnicodeBlock("SYRIAC");


        public static final UnicodeBlock THAANA =
            new UnicodeBlock("THAANA");


        public static final UnicodeBlock SINHALA =
            new UnicodeBlock("SINHALA");


        public static final UnicodeBlock MYANMAR =
            new UnicodeBlock("MYANMAR");


        public static final UnicodeBlock ETHIOPIC =
            new UnicodeBlock("ETHIOPIC");


        public static final UnicodeBlock CHEROKEE =
            new UnicodeBlock("CHEROKEE");


        public static final UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS =
            new UnicodeBlock("UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS",
                             "UNIFIED CANADIAN ABORIGINAL SYLLABICS",
                             "UNIFIEDCANADIANABORIGINALSYLLABICS");


        public static final UnicodeBlock OGHAM =
            new UnicodeBlock("OGHAM");


        public static final UnicodeBlock RUNIC =
            new UnicodeBlock("RUNIC");


        public static final UnicodeBlock KHMER =
            new UnicodeBlock("KHMER");


        public static final UnicodeBlock MONGOLIAN =
            new UnicodeBlock("MONGOLIAN");


        public static final UnicodeBlock BRAILLE_PATTERNS =
            new UnicodeBlock("BRAILLE_PATTERNS",
                             "BRAILLE PATTERNS",
                             "BRAILLEPATTERNS");


        public static final UnicodeBlock CJK_RADICALS_SUPPLEMENT =
            new UnicodeBlock("CJK_RADICALS_SUPPLEMENT",
                             "CJK RADICALS SUPPLEMENT",
                             "CJKRADICALSSUPPLEMENT");


        public static final UnicodeBlock KANGXI_RADICALS =
            new UnicodeBlock("KANGXI_RADICALS",
                             "KANGXI RADICALS",
                             "KANGXIRADICALS");


        public static final UnicodeBlock IDEOGRAPHIC_DESCRIPTION_CHARACTERS =
            new UnicodeBlock("IDEOGRAPHIC_DESCRIPTION_CHARACTERS",
                             "IDEOGRAPHIC DESCRIPTION CHARACTERS",
                             "IDEOGRAPHICDESCRIPTIONCHARACTERS");


        public static final UnicodeBlock BOPOMOFO_EXTENDED =
            new UnicodeBlock("BOPOMOFO_EXTENDED",
                             "BOPOMOFO EXTENDED",
                             "BOPOMOFOEXTENDED");


        public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A =
            new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A",
                             "CJK UNIFIED IDEOGRAPHS EXTENSION A",
                             "CJKUNIFIEDIDEOGRAPHSEXTENSIONA");


        public static final UnicodeBlock YI_SYLLABLES =
            new UnicodeBlock("YI_SYLLABLES",
                             "YI SYLLABLES",
                             "YISYLLABLES");


        public static final UnicodeBlock YI_RADICALS =
            new UnicodeBlock("YI_RADICALS",
                             "YI RADICALS",
                             "YIRADICALS");


        public static final UnicodeBlock CYRILLIC_SUPPLEMENTARY =
            new UnicodeBlock("CYRILLIC_SUPPLEMENTARY",
                             "CYRILLIC SUPPLEMENTARY",
                             "CYRILLICSUPPLEMENTARY",
                             "CYRILLIC SUPPLEMENT",
                             "CYRILLICSUPPLEMENT");


        public static final UnicodeBlock TAGALOG =
            new UnicodeBlock("TAGALOG");


        public static final UnicodeBlock HANUNOO =
            new UnicodeBlock("HANUNOO");


        public static final UnicodeBlock BUHID =
            new UnicodeBlock("BUHID");


        public static final UnicodeBlock TAGBANWA =
            new UnicodeBlock("TAGBANWA");


        public static final UnicodeBlock LIMBU =
            new UnicodeBlock("LIMBU");


        public static final UnicodeBlock TAI_LE =
            new UnicodeBlock("TAI_LE",
                             "TAI LE",
                             "TAILE");


        public static final UnicodeBlock KHMER_SYMBOLS =
            new UnicodeBlock("KHMER_SYMBOLS",
                             "KHMER SYMBOLS",
                             "KHMERSYMBOLS");


        public static final UnicodeBlock PHONETIC_EXTENSIONS =
            new UnicodeBlock("PHONETIC_EXTENSIONS",
                             "PHONETIC EXTENSIONS",
                             "PHONETICEXTENSIONS");


        public static final UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A =
            new UnicodeBlock("MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A",
                             "MISCELLANEOUS MATHEMATICAL SYMBOLS-A",
                             "MISCELLANEOUSMATHEMATICALSYMBOLS-A");


        public static final UnicodeBlock SUPPLEMENTAL_ARROWS_A =
            new UnicodeBlock("SUPPLEMENTAL_ARROWS_A",
                             "SUPPLEMENTAL ARROWS-A",
                             "SUPPLEMENTALARROWS-A");


        public static final UnicodeBlock SUPPLEMENTAL_ARROWS_B =
            new UnicodeBlock("SUPPLEMENTAL_ARROWS_B",
                             "SUPPLEMENTAL ARROWS-B",
                             "SUPPLEMENTALARROWS-B");


        public static final UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B =
            new UnicodeBlock("MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B",
                             "MISCELLANEOUS MATHEMATICAL SYMBOLS-B",
                             "MISCELLANEOUSMATHEMATICALSYMBOLS-B");


        public static final UnicodeBlock SUPPLEMENTAL_MATHEMATICAL_OPERATORS =
            new UnicodeBlock("SUPPLEMENTAL_MATHEMATICAL_OPERATORS",
                             "SUPPLEMENTAL MATHEMATICAL OPERATORS",
                             "SUPPLEMENTALMATHEMATICALOPERATORS");


        public static final UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_ARROWS =
            new UnicodeBlock("MISCELLANEOUS_SYMBOLS_AND_ARROWS",
                             "MISCELLANEOUS SYMBOLS AND ARROWS",
                             "MISCELLANEOUSSYMBOLSANDARROWS");


        public static final UnicodeBlock KATAKANA_PHONETIC_EXTENSIONS =
            new UnicodeBlock("KATAKANA_PHONETIC_EXTENSIONS",
                             "KATAKANA PHONETIC EXTENSIONS",
                             "KATAKANAPHONETICEXTENSIONS");


        public static final UnicodeBlock YIJING_HEXAGRAM_SYMBOLS =
            new UnicodeBlock("YIJING_HEXAGRAM_SYMBOLS",
                             "YIJING HEXAGRAM SYMBOLS",
                             "YIJINGHEXAGRAMSYMBOLS");


        public static final UnicodeBlock VARIATION_SELECTORS =
            new UnicodeBlock("VARIATION_SELECTORS",
                             "VARIATION SELECTORS",
                             "VARIATIONSELECTORS");


        public static final UnicodeBlock LINEAR_B_SYLLABARY =
            new UnicodeBlock("LINEAR_B_SYLLABARY",
                             "LINEAR B SYLLABARY",
                             "LINEARBSYLLABARY");


        public static final UnicodeBlock LINEAR_B_IDEOGRAMS =
            new UnicodeBlock("LINEAR_B_IDEOGRAMS",
                             "LINEAR B IDEOGRAMS",
                             "LINEARBIDEOGRAMS");


        public static final UnicodeBlock AEGEAN_NUMBERS =
            new UnicodeBlock("AEGEAN_NUMBERS",
                             "AEGEAN NUMBERS",
                             "AEGEANNUMBERS");


        public static final UnicodeBlock OLD_ITALIC =
            new UnicodeBlock("OLD_ITALIC",
                             "OLD ITALIC",
                             "OLDITALIC");


        public static final UnicodeBlock GOTHIC =
            new UnicodeBlock("GOTHIC");


        public static final UnicodeBlock UGARITIC =
            new UnicodeBlock("UGARITIC");


        public static final UnicodeBlock DESERET =
            new UnicodeBlock("DESERET");


        public static final UnicodeBlock SHAVIAN =
            new UnicodeBlock("SHAVIAN");


        public static final UnicodeBlock OSMANYA =
            new UnicodeBlock("OSMANYA");


        public static final UnicodeBlock CYPRIOT_SYLLABARY =
            new UnicodeBlock("CYPRIOT_SYLLABARY",
                             "CYPRIOT SYLLABARY",
                             "CYPRIOTSYLLABARY");


        public static final UnicodeBlock BYZANTINE_MUSICAL_SYMBOLS =
            new UnicodeBlock("BYZANTINE_MUSICAL_SYMBOLS",
                             "BYZANTINE MUSICAL SYMBOLS",
                             "BYZANTINEMUSICALSYMBOLS");


        public static final UnicodeBlock MUSICAL_SYMBOLS =
            new UnicodeBlock("MUSICAL_SYMBOLS",
                             "MUSICAL SYMBOLS",
                             "MUSICALSYMBOLS");


        public static final UnicodeBlock TAI_XUAN_JING_SYMBOLS =
            new UnicodeBlock("TAI_XUAN_JING_SYMBOLS",
                             "TAI XUAN JING SYMBOLS",
                             "TAIXUANJINGSYMBOLS");


        public static final UnicodeBlock MATHEMATICAL_ALPHANUMERIC_SYMBOLS =
            new UnicodeBlock("MATHEMATICAL_ALPHANUMERIC_SYMBOLS",
                             "MATHEMATICAL ALPHANUMERIC SYMBOLS",
                             "MATHEMATICALALPHANUMERICSYMBOLS");


        public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B =
            new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B",
                             "CJK UNIFIED IDEOGRAPHS EXTENSION B",
                             "CJKUNIFIEDIDEOGRAPHSEXTENSIONB");


        public static final UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT =
            new UnicodeBlock("CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT",
                             "CJK COMPATIBILITY IDEOGRAPHS SUPPLEMENT",
                             "CJKCOMPATIBILITYIDEOGRAPHSSUPPLEMENT");


        public static final UnicodeBlock TAGS =
            new UnicodeBlock("TAGS");


        public static final UnicodeBlock VARIATION_SELECTORS_SUPPLEMENT =
            new UnicodeBlock("VARIATION_SELECTORS_SUPPLEMENT",
                             "VARIATION SELECTORS SUPPLEMENT",
                             "VARIATIONSELECTORSSUPPLEMENT");


        public static final UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_A =
            new UnicodeBlock("SUPPLEMENTARY_PRIVATE_USE_AREA_A",
                             "SUPPLEMENTARY PRIVATE USE AREA-A",
                             "SUPPLEMENTARYPRIVATEUSEAREA-A");


        public static final UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_B =
            new UnicodeBlock("SUPPLEMENTARY_PRIVATE_USE_AREA_B",
                             "SUPPLEMENTARY PRIVATE USE AREA-B",
                             "SUPPLEMENTARYPRIVATEUSEAREA-B");


        public static final UnicodeBlock HIGH_SURROGATES =
            new UnicodeBlock("HIGH_SURROGATES",
                             "HIGH SURROGATES",
                             "HIGHSURROGATES");


        public static final UnicodeBlock HIGH_PRIVATE_USE_SURROGATES =
            new UnicodeBlock("HIGH_PRIVATE_USE_SURROGATES",
                             "HIGH PRIVATE USE SURROGATES",
                             "HIGHPRIVATEUSESURROGATES");


        public static final UnicodeBlock LOW_SURROGATES =
            new UnicodeBlock("LOW_SURROGATES",
                             "LOW SURROGATES",
                             "LOWSURROGATES");


        public static final UnicodeBlock ARABIC_SUPPLEMENT =
            new UnicodeBlock("ARABIC_SUPPLEMENT",
                             "ARABIC SUPPLEMENT",
                             "ARABICSUPPLEMENT");


        public static final UnicodeBlock NKO =
            new UnicodeBlock("NKO");


        public static final UnicodeBlock SAMARITAN =
            new UnicodeBlock("SAMARITAN");


        public static final UnicodeBlock MANDAIC =
            new UnicodeBlock("MANDAIC");


        public static final UnicodeBlock ETHIOPIC_SUPPLEMENT =
            new UnicodeBlock("ETHIOPIC_SUPPLEMENT",
                             "ETHIOPIC SUPPLEMENT",
                             "ETHIOPICSUPPLEMENT");


        public static final UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED =
            new UnicodeBlock("UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED",
                             "UNIFIED CANADIAN ABORIGINAL SYLLABICS EXTENDED",
                             "UNIFIEDCANADIANABORIGINALSYLLABICSEXTENDED");


        public static final UnicodeBlock NEW_TAI_LUE =
            new UnicodeBlock("NEW_TAI_LUE",
                             "NEW TAI LUE",
                             "NEWTAILUE");


        public static final UnicodeBlock BUGINESE =
            new UnicodeBlock("BUGINESE");


        public static final UnicodeBlock TAI_THAM =
            new UnicodeBlock("TAI_THAM",
                             "TAI THAM",
                             "TAITHAM");


        public static final UnicodeBlock BALINESE =
            new UnicodeBlock("BALINESE");


        public static final UnicodeBlock SUNDANESE =
            new UnicodeBlock("SUNDANESE");


        public static final UnicodeBlock BATAK =
            new UnicodeBlock("BATAK");


        public static final UnicodeBlock LEPCHA =
            new UnicodeBlock("LEPCHA");


        public static final UnicodeBlock OL_CHIKI =
            new UnicodeBlock("OL_CHIKI",
                             "OL CHIKI",
                             "OLCHIKI");


        public static final UnicodeBlock VEDIC_EXTENSIONS =
            new UnicodeBlock("VEDIC_EXTENSIONS",
                             "VEDIC EXTENSIONS",
                             "VEDICEXTENSIONS");


        public static final UnicodeBlock PHONETIC_EXTENSIONS_SUPPLEMENT =
            new UnicodeBlock("PHONETIC_EXTENSIONS_SUPPLEMENT",
                             "PHONETIC EXTENSIONS SUPPLEMENT",
                             "PHONETICEXTENSIONSSUPPLEMENT");


        public static final UnicodeBlock COMBINING_DIACRITICAL_MARKS_SUPPLEMENT =
            new UnicodeBlock("COMBINING_DIACRITICAL_MARKS_SUPPLEMENT",
                             "COMBINING DIACRITICAL MARKS SUPPLEMENT",
                             "COMBININGDIACRITICALMARKSSUPPLEMENT");


        public static final UnicodeBlock GLAGOLITIC =
            new UnicodeBlock("GLAGOLITIC");


        public static final UnicodeBlock LATIN_EXTENDED_C =
            new UnicodeBlock("LATIN_EXTENDED_C",
                             "LATIN EXTENDED-C",
                             "LATINEXTENDED-C");


        public static final UnicodeBlock COPTIC =
            new UnicodeBlock("COPTIC");


        public static final UnicodeBlock GEORGIAN_SUPPLEMENT =
            new UnicodeBlock("GEORGIAN_SUPPLEMENT",
                             "GEORGIAN SUPPLEMENT",
                             "GEORGIANSUPPLEMENT");


        public static final UnicodeBlock TIFINAGH =
            new UnicodeBlock("TIFINAGH");


        public static final UnicodeBlock ETHIOPIC_EXTENDED =
            new UnicodeBlock("ETHIOPIC_EXTENDED",
                             "ETHIOPIC EXTENDED",
                             "ETHIOPICEXTENDED");


        public static final UnicodeBlock CYRILLIC_EXTENDED_A =
            new UnicodeBlock("CYRILLIC_EXTENDED_A",
                             "CYRILLIC EXTENDED-A",
                             "CYRILLICEXTENDED-A");


        public static final UnicodeBlock SUPPLEMENTAL_PUNCTUATION =
            new UnicodeBlock("SUPPLEMENTAL_PUNCTUATION",
                             "SUPPLEMENTAL PUNCTUATION",
                             "SUPPLEMENTALPUNCTUATION");


        public static final UnicodeBlock CJK_STROKES =
            new UnicodeBlock("CJK_STROKES",
                             "CJK STROKES",
                             "CJKSTROKES");


        public static final UnicodeBlock LISU =
            new UnicodeBlock("LISU");


        public static final UnicodeBlock VAI =
            new UnicodeBlock("VAI");


        public static final UnicodeBlock CYRILLIC_EXTENDED_B =
            new UnicodeBlock("CYRILLIC_EXTENDED_B",
                             "CYRILLIC EXTENDED-B",
                             "CYRILLICEXTENDED-B");


        public static final UnicodeBlock BAMUM =
            new UnicodeBlock("BAMUM");


        public static final UnicodeBlock MODIFIER_TONE_LETTERS =
            new UnicodeBlock("MODIFIER_TONE_LETTERS",
                             "MODIFIER TONE LETTERS",
                             "MODIFIERTONELETTERS");


        public static final UnicodeBlock LATIN_EXTENDED_D =
            new UnicodeBlock("LATIN_EXTENDED_D",
                             "LATIN EXTENDED-D",
                             "LATINEXTENDED-D");


        public static final UnicodeBlock SYLOTI_NAGRI =
            new UnicodeBlock("SYLOTI_NAGRI",
                             "SYLOTI NAGRI",
                             "SYLOTINAGRI");


        public static final UnicodeBlock COMMON_INDIC_NUMBER_FORMS =
            new UnicodeBlock("COMMON_INDIC_NUMBER_FORMS",
                             "COMMON INDIC NUMBER FORMS",
                             "COMMONINDICNUMBERFORMS");


        public static final UnicodeBlock PHAGS_PA =
            new UnicodeBlock("PHAGS_PA",
                             "PHAGS-PA");


        public static final UnicodeBlock SAURASHTRA =
            new UnicodeBlock("SAURASHTRA");


        public static final UnicodeBlock DEVANAGARI_EXTENDED =
            new UnicodeBlock("DEVANAGARI_EXTENDED",
                             "DEVANAGARI EXTENDED",
                             "DEVANAGARIEXTENDED");


        public static final UnicodeBlock KAYAH_LI =
            new UnicodeBlock("KAYAH_LI",
                             "KAYAH LI",
                             "KAYAHLI");


        public static final UnicodeBlock REJANG =
            new UnicodeBlock("REJANG");


        public static final UnicodeBlock HANGUL_JAMO_EXTENDED_A =
            new UnicodeBlock("HANGUL_JAMO_EXTENDED_A",
                             "HANGUL JAMO EXTENDED-A",
                             "HANGULJAMOEXTENDED-A");


        public static final UnicodeBlock JAVANESE =
            new UnicodeBlock("JAVANESE");


        public static final UnicodeBlock CHAM =
            new UnicodeBlock("CHAM");


        public static final UnicodeBlock MYANMAR_EXTENDED_A =
            new UnicodeBlock("MYANMAR_EXTENDED_A",
                             "MYANMAR EXTENDED-A",
                             "MYANMAREXTENDED-A");


        public static final UnicodeBlock TAI_VIET =
            new UnicodeBlock("TAI_VIET",
                             "TAI VIET",
                             "TAIVIET");


        public static final UnicodeBlock ETHIOPIC_EXTENDED_A =
            new UnicodeBlock("ETHIOPIC_EXTENDED_A",
                             "ETHIOPIC EXTENDED-A",
                             "ETHIOPICEXTENDED-A");


        public static final UnicodeBlock MEETEI_MAYEK =
            new UnicodeBlock("MEETEI_MAYEK",
                             "MEETEI MAYEK",
                             "MEETEIMAYEK");


        public static final UnicodeBlock HANGUL_JAMO_EXTENDED_B =
            new UnicodeBlock("HANGUL_JAMO_EXTENDED_B",
                             "HANGUL JAMO EXTENDED-B",
                             "HANGULJAMOEXTENDED-B");


        public static final UnicodeBlock VERTICAL_FORMS =
            new UnicodeBlock("VERTICAL_FORMS",
                             "VERTICAL FORMS",
                             "VERTICALFORMS");


        public static final UnicodeBlock ANCIENT_GREEK_NUMBERS =
            new UnicodeBlock("ANCIENT_GREEK_NUMBERS",
                             "ANCIENT GREEK NUMBERS",
                             "ANCIENTGREEKNUMBERS");


        public static final UnicodeBlock ANCIENT_SYMBOLS =
            new UnicodeBlock("ANCIENT_SYMBOLS",
                             "ANCIENT SYMBOLS",
                             "ANCIENTSYMBOLS");


        public static final UnicodeBlock PHAISTOS_DISC =
            new UnicodeBlock("PHAISTOS_DISC",
                             "PHAISTOS DISC",
                             "PHAISTOSDISC");


        public static final UnicodeBlock LYCIAN =
            new UnicodeBlock("LYCIAN");


        public static final UnicodeBlock CARIAN =
            new UnicodeBlock("CARIAN");


        public static final UnicodeBlock OLD_PERSIAN =
            new UnicodeBlock("OLD_PERSIAN",
                             "OLD PERSIAN",
                             "OLDPERSIAN");


        public static final UnicodeBlock IMPERIAL_ARAMAIC =
            new UnicodeBlock("IMPERIAL_ARAMAIC",
                             "IMPERIAL ARAMAIC",
                             "IMPERIALARAMAIC");


        public static final UnicodeBlock PHOENICIAN =
            new UnicodeBlock("PHOENICIAN");


        public static final UnicodeBlock LYDIAN =
            new UnicodeBlock("LYDIAN");


        public static final UnicodeBlock KHAROSHTHI =
            new UnicodeBlock("KHAROSHTHI");


        public static final UnicodeBlock OLD_SOUTH_ARABIAN =
            new UnicodeBlock("OLD_SOUTH_ARABIAN",
                             "OLD SOUTH ARABIAN",
                             "OLDSOUTHARABIAN");


        public static final UnicodeBlock AVESTAN =
            new UnicodeBlock("AVESTAN");


        public static final UnicodeBlock INSCRIPTIONAL_PARTHIAN =
            new UnicodeBlock("INSCRIPTIONAL_PARTHIAN",
                             "INSCRIPTIONAL PARTHIAN",
                             "INSCRIPTIONALPARTHIAN");


        public static final UnicodeBlock INSCRIPTIONAL_PAHLAVI =
            new UnicodeBlock("INSCRIPTIONAL_PAHLAVI",
                             "INSCRIPTIONAL PAHLAVI",
                             "INSCRIPTIONALPAHLAVI");


        public static final UnicodeBlock OLD_TURKIC =
            new UnicodeBlock("OLD_TURKIC",
                             "OLD TURKIC",
                             "OLDTURKIC");


        public static final UnicodeBlock RUMI_NUMERAL_SYMBOLS =
            new UnicodeBlock("RUMI_NUMERAL_SYMBOLS",
                             "RUMI NUMERAL SYMBOLS",
                             "RUMINUMERALSYMBOLS");


        public static final UnicodeBlock BRAHMI =
            new UnicodeBlock("BRAHMI");


        public static final UnicodeBlock KAITHI =
            new UnicodeBlock("KAITHI");


        public static final UnicodeBlock CUNEIFORM =
            new UnicodeBlock("CUNEIFORM");


        public static final UnicodeBlock CUNEIFORM_NUMBERS_AND_PUNCTUATION =
            new UnicodeBlock("CUNEIFORM_NUMBERS_AND_PUNCTUATION",
                             "CUNEIFORM NUMBERS AND PUNCTUATION",
                             "CUNEIFORMNUMBERSANDPUNCTUATION");


        public static final UnicodeBlock EGYPTIAN_HIEROGLYPHS =
            new UnicodeBlock("EGYPTIAN_HIEROGLYPHS",
                             "EGYPTIAN HIEROGLYPHS",
                             "EGYPTIANHIEROGLYPHS");


        public static final UnicodeBlock BAMUM_SUPPLEMENT =
            new UnicodeBlock("BAMUM_SUPPLEMENT",
                             "BAMUM SUPPLEMENT",
                             "BAMUMSUPPLEMENT");


        public static final UnicodeBlock KANA_SUPPLEMENT =
            new UnicodeBlock("KANA_SUPPLEMENT",
                             "KANA SUPPLEMENT",
                             "KANASUPPLEMENT");


        public static final UnicodeBlock ANCIENT_GREEK_MUSICAL_NOTATION =
            new UnicodeBlock("ANCIENT_GREEK_MUSICAL_NOTATION",
                             "ANCIENT GREEK MUSICAL NOTATION",
                             "ANCIENTGREEKMUSICALNOTATION");


        public static final UnicodeBlock COUNTING_ROD_NUMERALS =
            new UnicodeBlock("COUNTING_ROD_NUMERALS",
                             "COUNTING ROD NUMERALS",
                             "COUNTINGRODNUMERALS");


        public static final UnicodeBlock MAHJONG_TILES =
            new UnicodeBlock("MAHJONG_TILES",
                             "MAHJONG TILES",
                             "MAHJONGTILES");


        public static final UnicodeBlock DOMINO_TILES =
            new UnicodeBlock("DOMINO_TILES",
                             "DOMINO TILES",
                             "DOMINOTILES");


        public static final UnicodeBlock PLAYING_CARDS =
            new UnicodeBlock("PLAYING_CARDS",
                             "PLAYING CARDS",
                             "PLAYINGCARDS");


        public static final UnicodeBlock ENCLOSED_ALPHANUMERIC_SUPPLEMENT =
            new UnicodeBlock("ENCLOSED_ALPHANUMERIC_SUPPLEMENT",
                             "ENCLOSED ALPHANUMERIC SUPPLEMENT",
                             "ENCLOSEDALPHANUMERICSUPPLEMENT");


        public static final UnicodeBlock ENCLOSED_IDEOGRAPHIC_SUPPLEMENT =
            new UnicodeBlock("ENCLOSED_IDEOGRAPHIC_SUPPLEMENT",
                             "ENCLOSED IDEOGRAPHIC SUPPLEMENT",
                             "ENCLOSEDIDEOGRAPHICSUPPLEMENT");


        public static final UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS =
            new UnicodeBlock("MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS",
                             "MISCELLANEOUS SYMBOLS AND PICTOGRAPHS",
                             "MISCELLANEOUSSYMBOLSANDPICTOGRAPHS");


        public static final UnicodeBlock EMOTICONS =
            new UnicodeBlock("EMOTICONS");


        public static final UnicodeBlock TRANSPORT_AND_MAP_SYMBOLS =
            new UnicodeBlock("TRANSPORT_AND_MAP_SYMBOLS",
                             "TRANSPORT AND MAP SYMBOLS",
                             "TRANSPORTANDMAPSYMBOLS");


        public static final UnicodeBlock ALCHEMICAL_SYMBOLS =
            new UnicodeBlock("ALCHEMICAL_SYMBOLS",
                             "ALCHEMICAL SYMBOLS",
                             "ALCHEMICALSYMBOLS");


        public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C =
            new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C",
                             "CJK UNIFIED IDEOGRAPHS EXTENSION C",
                             "CJKUNIFIEDIDEOGRAPHSEXTENSIONC");


        public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D =
            new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D",
                             "CJK UNIFIED IDEOGRAPHS EXTENSION D",
                             "CJKUNIFIEDIDEOGRAPHSEXTENSIOND");


        public static final UnicodeBlock ARABIC_EXTENDED_A =
            new UnicodeBlock("ARABIC_EXTENDED_A",
                             "ARABIC EXTENDED-A",
                             "ARABICEXTENDED-A");


        public static final UnicodeBlock SUNDANESE_SUPPLEMENT =
            new UnicodeBlock("SUNDANESE_SUPPLEMENT",
                             "SUNDANESE SUPPLEMENT",
                             "SUNDANESESUPPLEMENT");


        public static final UnicodeBlock MEETEI_MAYEK_EXTENSIONS =
            new UnicodeBlock("MEETEI_MAYEK_EXTENSIONS",
                             "MEETEI MAYEK EXTENSIONS",
                             "MEETEIMAYEKEXTENSIONS");


        public static final UnicodeBlock MEROITIC_HIEROGLYPHS =
            new UnicodeBlock("MEROITIC_HIEROGLYPHS",
                             "MEROITIC HIEROGLYPHS",
                             "MEROITICHIEROGLYPHS");


        public static final UnicodeBlock MEROITIC_CURSIVE =
            new UnicodeBlock("MEROITIC_CURSIVE",
                             "MEROITIC CURSIVE",
                             "MEROITICCURSIVE");


        public static final UnicodeBlock SORA_SOMPENG =
            new UnicodeBlock("SORA_SOMPENG",
                             "SORA SOMPENG",
                             "SORASOMPENG");


        public static final UnicodeBlock CHAKMA =
            new UnicodeBlock("CHAKMA");


        public static final UnicodeBlock SHARADA =
            new UnicodeBlock("SHARADA");


        public static final UnicodeBlock TAKRI =
            new UnicodeBlock("TAKRI");


        public static final UnicodeBlock MIAO =
            new UnicodeBlock("MIAO");


        public static final UnicodeBlock ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS =
            new UnicodeBlock("ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS",
                             "ARABIC MATHEMATICAL ALPHABETIC SYMBOLS",
                             "ARABICMATHEMATICALALPHABETICSYMBOLS");

        private static final int blockStarts[] = {
            0x0000,   0x0080,   0x0100,   0x0180,   0x0250,   0x02B0,   0x0300,   0x0370,   0x0400,   0x0500,   0x0530,   0x0590,   0x0600,   0x0700,   0x0750,   0x0780,   0x07C0,   0x0800,   0x0840,   0x0860,   0x08A0,   0x0900,   0x0980,   0x0A00,   0x0A80,   0x0B00,   0x0B80,   0x0C00,   0x0C80,   0x0D00,   0x0D80,   0x0E00,   0x0E80,   0x0F00,   0x1000,   0x10A0,   0x1100,   0x1200,   0x1380,   0x13A0,   0x1400,   0x1680,   0x16A0,   0x1700,   0x1720,   0x1740,   0x1760,   0x1780,   0x1800,   0x18B0,   0x1900,   0x1950,   0x1980,   0x19E0,   0x1A00,   0x1A20,   0x1AB0,   0x1B00,   0x1B80,   0x1BC0,   0x1C00,   0x1C50,   0x1C80,   0x1CC0,   0x1CD0,   0x1D00,   0x1D80,   0x1DC0,   0x1E00,   0x1F00,   0x2000,   0x2070,   0x20A0,   0x20D0,   0x2100,   0x2150,   0x2190,   0x2200,   0x2300,   0x2400,   0x2440,   0x2460,   0x2500,   0x2580,   0x25A0,   0x2600,   0x2700,   0x27C0,   0x27F0,   0x2800,   0x2900,   0x2980,   0x2A00,   0x2B00,   0x2C00,   0x2C60,   0x2C80,   0x2D00,   0x2D30,   0x2D80,   0x2DE0,   0x2E00,   0x2E80,   0x2F00,   0x2FE0,   0x2FF0,   0x3000,   0x3040,   0x30A0,   0x3100,   0x3130,   0x3190,   0x31A0,   0x31C0,   0x31F0,   0x3200,   0x3300,   0x3400,   0x4DC0,   0x4E00,   0xA000,   0xA490,   0xA4D0,   0xA500,   0xA640,   0xA6A0,   0xA700,   0xA720,   0xA800,   0xA830,   0xA840,   0xA880,   0xA8E0,   0xA900,   0xA930,   0xA960,   0xA980,   0xA9E0,   0xAA00,   0xAA60,   0xAA80,   0xAAE0,   0xAB00,   0xAB30,   0xABC0,   0xAC00,   0xD7B0,   0xD800,   0xDB80,   0xDC00,   0xE000,   0xF900,   0xFB00,   0xFB50,   0xFE00,   0xFE10,   0xFE20,   0xFE30,   0xFE50,   0xFE70,   0xFF00,   0xFFF0,   0x10000,  0x10080,  0x10100,  0x10140,  0x10190,  0x101D0,  0x10200,  0x10280,  0x102A0,  0x102E0,  0x10300,  0x10330,  0x10350,  0x10380,  0x103A0,  0x103E0,  0x10400,  0x10450,  0x10480,  0x104B0,  0x10800,  0x10840,  0x10860,  0x10900,  0x10920,  0x10940,  0x10980,  0x109A0,  0x10A00,  0x10A60,  0x10A80,  0x10B00,  0x10B40,  0x10B60,  0x10B80,  0x10C00,  0x10C50,  0x10E60,  0x10E80,  0x11000,  0x11080,  0x110D0,  0x11100,  0x11150,  0x11180,  0x111E0,  0x11680,  0x116D0,  0x12000,  0x12400,  0x12480,  0x13000,  0x13430,  0x16800,  0x16A40,  0x16F00,  0x16FA0,  0x1B000,  0x1B100,  0x1D000,  0x1D100,  0x1D200,  0x1D250,  0x1D300,  0x1D360,  0x1D380,  0x1D400,  0x1D800,  0x1EE00,  0x1EF00,  0x1F000,  0x1F030,  0x1F0A0,  0x1F100,  0x1F200,  0x1F300,  0x1F600,  0x1F650,  0x1F680,  0x1F700,  0x1F780,  0x20000,  0x2A6E0,  0x2A700,  0x2B740,  0x2B820,  0x2F800,  0x2FA20,  0xE0000,  0xE0080,  0xE0100,  0xE01F0,  0xF0000,  0x100000  };

        private static final UnicodeBlock[] blocks = {
            BASIC_LATIN,
            LATIN_1_SUPPLEMENT,
            LATIN_EXTENDED_A,
            LATIN_EXTENDED_B,
            IPA_EXTENSIONS,
            SPACING_MODIFIER_LETTERS,
            COMBINING_DIACRITICAL_MARKS,
            GREEK,
            CYRILLIC,
            CYRILLIC_SUPPLEMENTARY,
            ARMENIAN,
            HEBREW,
            ARABIC,
            SYRIAC,
            ARABIC_SUPPLEMENT,
            THAANA,
            NKO,
            SAMARITAN,
            MANDAIC,
            null,
            ARABIC_EXTENDED_A,
            DEVANAGARI,
            BENGALI,
            GURMUKHI,
            GUJARATI,
            ORIYA,
            TAMIL,
            TELUGU,
            KANNADA,
            MALAYALAM,
            SINHALA,
            THAI,
            LAO,
            TIBETAN,
            MYANMAR,
            GEORGIAN,
            HANGUL_JAMO,
            ETHIOPIC,
            ETHIOPIC_SUPPLEMENT,
            CHEROKEE,
            UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS,
            OGHAM,
            RUNIC,
            TAGALOG,
            HANUNOO,
            BUHID,
            TAGBANWA,
            KHMER,
            MONGOLIAN,
            UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED,
            LIMBU,
            TAI_LE,
            NEW_TAI_LUE,
            KHMER_SYMBOLS,
            BUGINESE,
            TAI_THAM,
            null,
            BALINESE,
            SUNDANESE,
            BATAK,
            LEPCHA,
            OL_CHIKI,
            null,
            SUNDANESE_SUPPLEMENT,
            VEDIC_EXTENSIONS,
            PHONETIC_EXTENSIONS,
            PHONETIC_EXTENSIONS_SUPPLEMENT,
            COMBINING_DIACRITICAL_MARKS_SUPPLEMENT,
            LATIN_EXTENDED_ADDITIONAL,
            GREEK_EXTENDED,
            GENERAL_PUNCTUATION,
            SUPERSCRIPTS_AND_SUBSCRIPTS,
            CURRENCY_SYMBOLS,
            COMBINING_MARKS_FOR_SYMBOLS,
            LETTERLIKE_SYMBOLS,
            NUMBER_FORMS,
            ARROWS,
            MATHEMATICAL_OPERATORS,
            MISCELLANEOUS_TECHNICAL,
            CONTROL_PICTURES,
            OPTICAL_CHARACTER_RECOGNITION,
            ENCLOSED_ALPHANUMERICS,
            BOX_DRAWING,
            BLOCK_ELEMENTS,
            GEOMETRIC_SHAPES,
            MISCELLANEOUS_SYMBOLS,
            DINGBATS,
            MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A,
            SUPPLEMENTAL_ARROWS_A,
            BRAILLE_PATTERNS,
            SUPPLEMENTAL_ARROWS_B,
            MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B,
            SUPPLEMENTAL_MATHEMATICAL_OPERATORS,
            MISCELLANEOUS_SYMBOLS_AND_ARROWS,
            GLAGOLITIC,
            LATIN_EXTENDED_C,
            COPTIC,
            GEORGIAN_SUPPLEMENT,
            TIFINAGH,
            ETHIOPIC_EXTENDED,
            CYRILLIC_EXTENDED_A,
            SUPPLEMENTAL_PUNCTUATION,
            CJK_RADICALS_SUPPLEMENT,
            KANGXI_RADICALS,
            null,
            IDEOGRAPHIC_DESCRIPTION_CHARACTERS,
            CJK_SYMBOLS_AND_PUNCTUATION,
            HIRAGANA,
            KATAKANA,
            BOPOMOFO,
            HANGUL_COMPATIBILITY_JAMO,
            KANBUN,
            BOPOMOFO_EXTENDED,
            CJK_STROKES,
            KATAKANA_PHONETIC_EXTENSIONS,
            ENCLOSED_CJK_LETTERS_AND_MONTHS,
            CJK_COMPATIBILITY,
            CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A,
            YIJING_HEXAGRAM_SYMBOLS,
            CJK_UNIFIED_IDEOGRAPHS,
            YI_SYLLABLES,
            YI_RADICALS,
            LISU,
            VAI,
            CYRILLIC_EXTENDED_B,
            BAMUM,
            MODIFIER_TONE_LETTERS,
            LATIN_EXTENDED_D,
            SYLOTI_NAGRI,
            COMMON_INDIC_NUMBER_FORMS,
            PHAGS_PA,
            SAURASHTRA,
            DEVANAGARI_EXTENDED,
            KAYAH_LI,
            REJANG,
            HANGUL_JAMO_EXTENDED_A,
            JAVANESE,
            null,
            CHAM,
            MYANMAR_EXTENDED_A,
            TAI_VIET,
            MEETEI_MAYEK_EXTENSIONS,
            ETHIOPIC_EXTENDED_A,
            null,
            MEETEI_MAYEK,
            HANGUL_SYLLABLES,
            HANGUL_JAMO_EXTENDED_B,
            HIGH_SURROGATES,
            HIGH_PRIVATE_USE_SURROGATES,
            LOW_SURROGATES,
            PRIVATE_USE_AREA,
            CJK_COMPATIBILITY_IDEOGRAPHS,
            ALPHABETIC_PRESENTATION_FORMS,
            ARABIC_PRESENTATION_FORMS_A,
            VARIATION_SELECTORS,
            VERTICAL_FORMS,
            COMBINING_HALF_MARKS,
            CJK_COMPATIBILITY_FORMS,
            SMALL_FORM_VARIANTS,
            ARABIC_PRESENTATION_FORMS_B,
            HALFWIDTH_AND_FULLWIDTH_FORMS,
            SPECIALS,
            LINEAR_B_SYLLABARY,
            LINEAR_B_IDEOGRAMS,
            AEGEAN_NUMBERS,
            ANCIENT_GREEK_NUMBERS,
            ANCIENT_SYMBOLS,
            PHAISTOS_DISC,
            null,
            LYCIAN,
            CARIAN,
            null,
            OLD_ITALIC,
            GOTHIC,
            null,
            UGARITIC,
            OLD_PERSIAN,
            null,
            DESERET,
            SHAVIAN,
            OSMANYA,
            null,
            CYPRIOT_SYLLABARY,
            IMPERIAL_ARAMAIC,
            null,
            PHOENICIAN,
            LYDIAN,
            null,
            MEROITIC_HIEROGLYPHS,
            MEROITIC_CURSIVE,
            KHAROSHTHI,
            OLD_SOUTH_ARABIAN,
            null,
            AVESTAN,
            INSCRIPTIONAL_PARTHIAN,
            INSCRIPTIONAL_PAHLAVI,
            null,
            OLD_TURKIC,
            null,
            RUMI_NUMERAL_SYMBOLS,
            null,
            BRAHMI,
            KAITHI,
            SORA_SOMPENG,
            CHAKMA,
            null,
            SHARADA,
            null,
            TAKRI,
            null,
            CUNEIFORM,
            CUNEIFORM_NUMBERS_AND_PUNCTUATION,
            null,
            EGYPTIAN_HIEROGLYPHS,
            null,
            BAMUM_SUPPLEMENT,
            null,
            MIAO,
            null,
            KANA_SUPPLEMENT,
            null,
            BYZANTINE_MUSICAL_SYMBOLS,
            MUSICAL_SYMBOLS,
            ANCIENT_GREEK_MUSICAL_NOTATION,
            null,
            TAI_XUAN_JING_SYMBOLS,
            COUNTING_ROD_NUMERALS,
            null,
            MATHEMATICAL_ALPHANUMERIC_SYMBOLS,
            null,
            ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS,
            null,
            MAHJONG_TILES,
            DOMINO_TILES,
            PLAYING_CARDS,
            ENCLOSED_ALPHANUMERIC_SUPPLEMENT,
            ENCLOSED_IDEOGRAPHIC_SUPPLEMENT,
            MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS,
            EMOTICONS,
            null,
            TRANSPORT_AND_MAP_SYMBOLS,
            ALCHEMICAL_SYMBOLS,
            null,
            CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B,
            null,
            CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C,
            CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D,
            null,
            CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT,
            null,
            TAGS,
            null,
            VARIATION_SELECTORS_SUPPLEMENT,
            null,
            SUPPLEMENTARY_PRIVATE_USE_AREA_A,
            SUPPLEMENTARY_PRIVATE_USE_AREA_B
        };



        public static UnicodeBlock of(char c) {
            return of((int)c);
        }


        public static UnicodeBlock of(int codePoint) {
            if (!isValidCodePoint(codePoint)) {
                throw new IllegalArgumentException();
            }

            int top, bottom, current;
            bottom = 0;
            top = blockStarts.length;
            current = top/2;

            while (top - bottom > 1) {
                if (codePoint >= blockStarts[current]) {
                    bottom = current;
                } else {
                    top = current;
                }
                current = (top + bottom) / 2;
            }
            return blocks[current];
        }


        public static final UnicodeBlock forName(String blockName) {
            UnicodeBlock block = map.get(blockName.toUpperCase(Locale.US));
            if (block == null) {
                throw new IllegalArgumentException();
            }
            return block;
        }
    }



    public static enum UnicodeScript {

        COMMON,


        LATIN,


        GREEK,


        CYRILLIC,


        ARMENIAN,


        HEBREW,


        ARABIC,


        SYRIAC,


        THAANA,


        DEVANAGARI,


        BENGALI,


        GURMUKHI,


        GUJARATI,


        ORIYA,


        TAMIL,


        TELUGU,


        KANNADA,


        MALAYALAM,


        SINHALA,


        THAI,


        LAO,


        TIBETAN,


        MYANMAR,


        GEORGIAN,


        HANGUL,


        ETHIOPIC,


        CHEROKEE,


        CANADIAN_ABORIGINAL,


        OGHAM,


        RUNIC,


        KHMER,


        MONGOLIAN,


        HIRAGANA,


        KATAKANA,


        BOPOMOFO,


        HAN,


        YI,


        OLD_ITALIC,


        GOTHIC,


        DESERET,


        INHERITED,


        TAGALOG,


        HANUNOO,


        BUHID,


        TAGBANWA,


        LIMBU,


        TAI_LE,


        LINEAR_B,


        UGARITIC,


        SHAVIAN,


        OSMANYA,


        CYPRIOT,


        BRAILLE,


        BUGINESE,


        COPTIC,


        NEW_TAI_LUE,


        GLAGOLITIC,


        TIFINAGH,


        SYLOTI_NAGRI,


        OLD_PERSIAN,


        KHAROSHTHI,


        BALINESE,


        CUNEIFORM,


        PHOENICIAN,


        PHAGS_PA,


        NKO,


        SUNDANESE,


        BATAK,


        LEPCHA,


        OL_CHIKI,


        VAI,


        SAURASHTRA,


        KAYAH_LI,


        REJANG,


        LYCIAN,


        CARIAN,


        LYDIAN,


        CHAM,


        TAI_THAM,


        TAI_VIET,


        AVESTAN,


        EGYPTIAN_HIEROGLYPHS,


        SAMARITAN,


        MANDAIC,


        LISU,


        BAMUM,


        JAVANESE,


        MEETEI_MAYEK,


        IMPERIAL_ARAMAIC,


        OLD_SOUTH_ARABIAN,


        INSCRIPTIONAL_PARTHIAN,


        INSCRIPTIONAL_PAHLAVI,


        OLD_TURKIC,


        BRAHMI,


        KAITHI,


        MEROITIC_HIEROGLYPHS,


        MEROITIC_CURSIVE,


        SORA_SOMPENG,


        CHAKMA,


        SHARADA,


        TAKRI,


        MIAO,


        UNKNOWN;

        private static final int[] scriptStarts = {
            0x0000,   0x0041,   0x005B,   0x0061,   0x007B,   0x00AA,   0x00AB,   0x00BA,   0x00BB,   0x00C0,   0x00D7,   0x00D8,   0x00F7,   0x00F8,   0x02B9,   0x02E0,   0x02E5,   0x02EA,   0x02EC,   0x0300,   0x0370,   0x0374,   0x0375,   0x037E,   0x0384,   0x0385,   0x0386,   0x0387,   0x0388,   0x03E2,   0x03F0,   0x0400,   0x0485,   0x0487,   0x0531,   0x0589,   0x058A,   0x0591,   0x0600,   0x060C,   0x060D,   0x061B,   0x061E,   0x061F,   0x0620,   0x0640,   0x0641,   0x064B,   0x0656,   0x0660,   0x066A,   0x0670,   0x0671,   0x06DD,   0x06DE,   0x0700,   0x0750,   0x0780,   0x07C0,   0x0800,   0x0840,   0x08A0,   0x0900,   0x0951,   0x0953,   0x0964,   0x0966,   0x0981,   0x0A01,   0x0A81,   0x0B01,   0x0B82,   0x0C01,   0x0C82,   0x0D02,   0x0D82,   0x0E01,   0x0E3F,   0x0E40,   0x0E81,   0x0F00,   0x0FD5,   0x0FD9,   0x1000,   0x10A0,   0x10FB,   0x10FC,   0x1100,   0x1200,   0x13A0,   0x1400,   0x1680,   0x16A0,   0x16EB,   0x16EE,   0x1700,   0x1720,   0x1735,   0x1740,   0x1760,   0x1780,   0x1800,   0x1802,   0x1804,   0x1805,   0x1806,   0x18B0,   0x1900,   0x1950,   0x1980,   0x19E0,   0x1A00,   0x1A20,   0x1B00,   0x1B80,   0x1BC0,   0x1C00,   0x1C50,   0x1CC0,   0x1CD0,   0x1CD3,   0x1CD4,   0x1CE1,   0x1CE2,   0x1CE9,   0x1CED,   0x1CEE,   0x1CF4,   0x1CF5,   0x1D00,   0x1D26,   0x1D2B,   0x1D2C,   0x1D5D,   0x1D62,   0x1D66,   0x1D6B,   0x1D78,   0x1D79,   0x1DBF,   0x1DC0,   0x1E00,   0x1F00,   0x2000,   0x200C,   0x200E,   0x2071,   0x2074,   0x207F,   0x2080,   0x2090,   0x20A0,   0x20D0,   0x2100,   0x2126,   0x2127,   0x212A,   0x212C,   0x2132,   0x2133,   0x214E,   0x214F,   0x2160,   0x2189,   0x2800,   0x2900,   0x2C00,   0x2C60,   0x2C80,   0x2D00,   0x2D30,   0x2D80,   0x2DE0,   0x2E00,   0x2E80,   0x2FF0,   0x3005,   0x3006,   0x3007,   0x3008,   0x3021,   0x302A,   0x302E,   0x3030,   0x3038,   0x303C,   0x3041,   0x3099,   0x309B,   0x309D,   0x30A0,   0x30A1,   0x30FB,   0x30FD,   0x3105,   0x3131,   0x3190,   0x31A0,   0x31C0,   0x31F0,   0x3200,   0x3220,   0x3260,   0x327F,   0x32D0,   0x3358,   0x3400,   0x4DC0,   0x4E00,   0xA000,   0xA4D0,   0xA500,   0xA640,   0xA6A0,   0xA700,   0xA722,   0xA788,   0xA78B,   0xA800,   0xA830,   0xA840,   0xA880,   0xA8E0,   0xA900,   0xA930,   0xA960,   0xA980,   0xAA00,   0xAA60,   0xAA80,   0xAAE0,   0xAB01,   0xABC0,   0xAC00,   0xD7FC,   0xF900,   0xFB00,   0xFB13,   0xFB1D,   0xFB50,   0xFD3E,   0xFD50,   0xFDFD,   0xFE00,   0xFE10,   0xFE20,   0xFE30,   0xFE70,   0xFEFF,   0xFF21,   0xFF3B,   0xFF41,   0xFF5B,   0xFF66,   0xFF70,   0xFF71,   0xFF9E,   0xFFA0,   0xFFE0,   0x10000,  0x10100,  0x10140,  0x10190,  0x101FD,  0x10280,  0x102A0,  0x10300,  0x10330,  0x10380,  0x103A0,  0x10400,  0x10450,  0x10480,  0x10800,  0x10840,  0x10900,  0x10920,  0x10980,  0x109A0,  0x10A00,  0x10A60,  0x10B00,  0x10B40,  0x10B60,  0x10C00,  0x10E60,  0x11000,  0x11080,  0x110D0,  0x11100,  0x11180,  0x11680,  0x12000,  0x13000,  0x16800,  0x16F00,  0x1B000,  0x1B001,  0x1D000,  0x1D167,  0x1D16A,  0x1D17B,  0x1D183,  0x1D185,  0x1D18C,  0x1D1AA,  0x1D1AE,  0x1D200,  0x1D300,  0x1EE00,  0x1F000,  0x1F200,  0x1F201,  0x20000,  0xE0001,  0xE0100,  0xE01F0   };

        private static final UnicodeScript[] scripts = {
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            BOPOMOFO,
            COMMON,
            INHERITED,
            GREEK,
            COMMON,
            GREEK,
            COMMON,
            GREEK,
            COMMON,
            GREEK,
            COMMON,
            GREEK,
            COPTIC,
            GREEK,
            CYRILLIC,
            INHERITED,
            CYRILLIC,
            ARMENIAN,
            COMMON,
            ARMENIAN,
            HEBREW,
            ARABIC,
            COMMON,
            ARABIC,
            COMMON,
            ARABIC,
            COMMON,
            ARABIC,
            COMMON,
            ARABIC,
            INHERITED,
            ARABIC,
            COMMON,
            ARABIC,
            INHERITED,
            ARABIC,
            COMMON,
            ARABIC,
            SYRIAC,
            ARABIC,
            THAANA,
            NKO,
            SAMARITAN,
            MANDAIC,
            ARABIC,
            DEVANAGARI,
            INHERITED,
            DEVANAGARI,
            COMMON,
            DEVANAGARI,
            BENGALI,
            GURMUKHI,
            GUJARATI,
            ORIYA,
            TAMIL,
            TELUGU,
            KANNADA,
            MALAYALAM,
            SINHALA,
            THAI,
            COMMON,
            THAI,
            LAO,
            TIBETAN,
            COMMON,
            TIBETAN,
            MYANMAR,
            GEORGIAN,
            COMMON,
            GEORGIAN,
            HANGUL,
            ETHIOPIC,
            CHEROKEE,
            CANADIAN_ABORIGINAL,
            OGHAM,
            RUNIC,
            COMMON,
            RUNIC,
            TAGALOG,
            HANUNOO,
            COMMON,
            BUHID,
            TAGBANWA,
            KHMER,
            MONGOLIAN,
            COMMON,
            MONGOLIAN,
            COMMON,
            MONGOLIAN,
            CANADIAN_ABORIGINAL,
            LIMBU,
            TAI_LE,
            NEW_TAI_LUE,
            KHMER,
            BUGINESE,
            TAI_THAM,
            BALINESE,
            SUNDANESE,
            BATAK,
            LEPCHA,
            OL_CHIKI,
            SUNDANESE,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            LATIN,
            GREEK,
            CYRILLIC,
            LATIN,
            GREEK,
            LATIN,
            GREEK,
            LATIN,
            CYRILLIC,
            LATIN,
            GREEK,
            INHERITED,
            LATIN,
            GREEK,
            COMMON,
            INHERITED,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            INHERITED,
            COMMON,
            GREEK,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            BRAILLE,
            COMMON,
            GLAGOLITIC,
            LATIN,
            COPTIC,
            GEORGIAN,
            TIFINAGH,
            ETHIOPIC,
            CYRILLIC,
            COMMON,
            HAN,
            COMMON,
            HAN,
            COMMON,
            HAN,
            COMMON,
            HAN,
            INHERITED,
            HANGUL,
            COMMON,
            HAN,
            COMMON,
            HIRAGANA,
            INHERITED,
            COMMON,
            HIRAGANA,
            COMMON,
            KATAKANA,
            COMMON,
            KATAKANA,
            BOPOMOFO,
            HANGUL,
            COMMON,
            BOPOMOFO,
            COMMON,
            KATAKANA,
            HANGUL,
            COMMON,
            HANGUL,
            COMMON,
            KATAKANA,
            COMMON,
            HAN,
            COMMON,
            HAN,
            YI,
            LISU,
            VAI,
            CYRILLIC,
            BAMUM,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            SYLOTI_NAGRI,
            COMMON,
            PHAGS_PA,
            SAURASHTRA,
            DEVANAGARI,
            KAYAH_LI,
            REJANG,
            HANGUL,
            JAVANESE,
            CHAM,
            MYANMAR,
            TAI_VIET,
            MEETEI_MAYEK,
            ETHIOPIC,
            MEETEI_MAYEK,
            HANGUL,
            UNKNOWN     ,
            HAN,
            LATIN,
            ARMENIAN,
            HEBREW,
            ARABIC,
            COMMON,
            ARABIC,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            ARABIC,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            KATAKANA,
            COMMON,
            KATAKANA,
            COMMON,
            HANGUL,
            COMMON,
            LINEAR_B,
            COMMON,
            GREEK,
            COMMON,
            INHERITED,
            LYCIAN,
            CARIAN,
            OLD_ITALIC,
            GOTHIC,
            UGARITIC,
            OLD_PERSIAN,
            DESERET,
            SHAVIAN,
            OSMANYA,
            CYPRIOT,
            IMPERIAL_ARAMAIC,
            PHOENICIAN,
            LYDIAN,
            MEROITIC_HIEROGLYPHS,
            MEROITIC_CURSIVE,
            KHAROSHTHI,
            OLD_SOUTH_ARABIAN,
            AVESTAN,
            INSCRIPTIONAL_PARTHIAN,
            INSCRIPTIONAL_PAHLAVI,
            OLD_TURKIC,
            ARABIC,
            BRAHMI,
            KAITHI,
            SORA_SOMPENG,
            CHAKMA,
            SHARADA,
            TAKRI,
            CUNEIFORM,
            EGYPTIAN_HIEROGLYPHS,
            BAMUM,
            MIAO,
            KATAKANA,
            HIRAGANA,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            GREEK,
            COMMON,
            ARABIC,
            COMMON,
            HIRAGANA,
            COMMON,
            HAN,
            COMMON,
            INHERITED,
            UNKNOWN
        };

        private static HashMap<String, Character.UnicodeScript> aliases;
        static {
            aliases = new HashMap<>(128);
            aliases.put("ARAB", ARABIC);
            aliases.put("ARMI", IMPERIAL_ARAMAIC);
            aliases.put("ARMN", ARMENIAN);
            aliases.put("AVST", AVESTAN);
            aliases.put("BALI", BALINESE);
            aliases.put("BAMU", BAMUM);
            aliases.put("BATK", BATAK);
            aliases.put("BENG", BENGALI);
            aliases.put("BOPO", BOPOMOFO);
            aliases.put("BRAI", BRAILLE);
            aliases.put("BRAH", BRAHMI);
            aliases.put("BUGI", BUGINESE);
            aliases.put("BUHD", BUHID);
            aliases.put("CAKM", CHAKMA);
            aliases.put("CANS", CANADIAN_ABORIGINAL);
            aliases.put("CARI", CARIAN);
            aliases.put("CHAM", CHAM);
            aliases.put("CHER", CHEROKEE);
            aliases.put("COPT", COPTIC);
            aliases.put("CPRT", CYPRIOT);
            aliases.put("CYRL", CYRILLIC);
            aliases.put("DEVA", DEVANAGARI);
            aliases.put("DSRT", DESERET);
            aliases.put("EGYP", EGYPTIAN_HIEROGLYPHS);
            aliases.put("ETHI", ETHIOPIC);
            aliases.put("GEOR", GEORGIAN);
            aliases.put("GLAG", GLAGOLITIC);
            aliases.put("GOTH", GOTHIC);
            aliases.put("GREK", GREEK);
            aliases.put("GUJR", GUJARATI);
            aliases.put("GURU", GURMUKHI);
            aliases.put("HANG", HANGUL);
            aliases.put("HANI", HAN);
            aliases.put("HANO", HANUNOO);
            aliases.put("HEBR", HEBREW);
            aliases.put("HIRA", HIRAGANA);
            aliases.put("ITAL", OLD_ITALIC);
            aliases.put("JAVA", JAVANESE);
            aliases.put("KALI", KAYAH_LI);
            aliases.put("KANA", KATAKANA);
            aliases.put("KHAR", KHAROSHTHI);
            aliases.put("KHMR", KHMER);
            aliases.put("KNDA", KANNADA);
            aliases.put("KTHI", KAITHI);
            aliases.put("LANA", TAI_THAM);
            aliases.put("LAOO", LAO);
            aliases.put("LATN", LATIN);
            aliases.put("LEPC", LEPCHA);
            aliases.put("LIMB", LIMBU);
            aliases.put("LINB", LINEAR_B);
            aliases.put("LISU", LISU);
            aliases.put("LYCI", LYCIAN);
            aliases.put("LYDI", LYDIAN);
            aliases.put("MAND", MANDAIC);
            aliases.put("MERC", MEROITIC_CURSIVE);
            aliases.put("MERO", MEROITIC_HIEROGLYPHS);
            aliases.put("MLYM", MALAYALAM);
            aliases.put("MONG", MONGOLIAN);
            aliases.put("MTEI", MEETEI_MAYEK);
            aliases.put("MYMR", MYANMAR);
            aliases.put("NKOO", NKO);
            aliases.put("OGAM", OGHAM);
            aliases.put("OLCK", OL_CHIKI);
            aliases.put("ORKH", OLD_TURKIC);
            aliases.put("ORYA", ORIYA);
            aliases.put("OSMA", OSMANYA);
            aliases.put("PHAG", PHAGS_PA);
            aliases.put("PLRD", MIAO);
            aliases.put("PHLI", INSCRIPTIONAL_PAHLAVI);
            aliases.put("PHNX", PHOENICIAN);
            aliases.put("PRTI", INSCRIPTIONAL_PARTHIAN);
            aliases.put("RJNG", REJANG);
            aliases.put("RUNR", RUNIC);
            aliases.put("SAMR", SAMARITAN);
            aliases.put("SARB", OLD_SOUTH_ARABIAN);
            aliases.put("SAUR", SAURASHTRA);
            aliases.put("SHAW", SHAVIAN);
            aliases.put("SHRD", SHARADA);
            aliases.put("SINH", SINHALA);
            aliases.put("SORA", SORA_SOMPENG);
            aliases.put("SUND", SUNDANESE);
            aliases.put("SYLO", SYLOTI_NAGRI);
            aliases.put("SYRC", SYRIAC);
            aliases.put("TAGB", TAGBANWA);
            aliases.put("TALE", TAI_LE);
            aliases.put("TAKR", TAKRI);
            aliases.put("TALU", NEW_TAI_LUE);
            aliases.put("TAML", TAMIL);
            aliases.put("TAVT", TAI_VIET);
            aliases.put("TELU", TELUGU);
            aliases.put("TFNG", TIFINAGH);
            aliases.put("TGLG", TAGALOG);
            aliases.put("THAA", THAANA);
            aliases.put("THAI", THAI);
            aliases.put("TIBT", TIBETAN);
            aliases.put("UGAR", UGARITIC);
            aliases.put("VAII", VAI);
            aliases.put("XPEO", OLD_PERSIAN);
            aliases.put("XSUX", CUNEIFORM);
            aliases.put("YIII", YI);
            aliases.put("ZINH", INHERITED);
            aliases.put("ZYYY", COMMON);
            aliases.put("ZZZZ", UNKNOWN);
        }


        public static UnicodeScript of(int codePoint) {
            if (!isValidCodePoint(codePoint))
                throw new IllegalArgumentException();
            int type = getType(codePoint);
            if (type == UNASSIGNED)
                return UNKNOWN;
            int index = Arrays.binarySearch(scriptStarts, codePoint);
            if (index < 0)
                index = -index - 2;
            return scripts[index];
        }


        public static final UnicodeScript forName(String scriptName) {
            scriptName = scriptName.toUpperCase(Locale.ENGLISH);
                                 UnicodeScript sc = aliases.get(scriptName);
            if (sc != null)
                return sc;
            return valueOf(scriptName);
        }
    }


    private final char value;


    private static final long serialVersionUID = 3786198910865385080L;


    public Character(char value) {
        this.value = value;
    }

    private static class CharacterCache {
        private CharacterCache(){}

        static final Character cache[] = new Character[127 + 1];

        static {
            for (int i = 0; i < cache.length; i++)
                cache[i] = new Character((char)i);
        }
    }


    public static Character valueOf(char c) {
        if (c <= 127) { return CharacterCache.cache[(int)c];
        }
        return new Character(c);
    }


    public char charValue() {
        return value;
    }


    @Override
    public int hashCode() {
        return Character.hashCode(value);
    }


    public static int hashCode(char value) {
        return (int)value;
    }


    public boolean equals(Object obj) {
        if (obj instanceof Character) {
            return value == ((Character)obj).charValue();
        }
        return false;
    }


    public String toString() {
        char buf[] = {value};
        return String.valueOf(buf);
    }


    public static String toString(char c) {
        return String.valueOf(c);
    }


    public static boolean isValidCodePoint(int codePoint) {
        int plane = codePoint >>> 16;
        return plane < ((MAX_CODE_POINT + 1) >>> 16);
    }


    public static boolean isBmpCodePoint(int codePoint) {
        return codePoint >>> 16 == 0;
        }


    public static boolean isSupplementaryCodePoint(int codePoint) {
        return codePoint >= MIN_SUPPLEMENTARY_CODE_POINT
            && codePoint <  MAX_CODE_POINT + 1;
    }


    public static boolean isHighSurrogate(char ch) {
        return ch >= MIN_HIGH_SURROGATE && ch < (MAX_HIGH_SURROGATE + 1);
    }


    public static boolean isLowSurrogate(char ch) {
        return ch >= MIN_LOW_SURROGATE && ch < (MAX_LOW_SURROGATE + 1);
    }


    public static boolean isSurrogate(char ch) {
        return ch >= MIN_SURROGATE && ch < (MAX_SURROGATE + 1);
    }


    public static boolean isSurrogatePair(char high, char low) {
        return isHighSurrogate(high) && isLowSurrogate(low);
    }


    public static int charCount(int codePoint) {
        return codePoint >= MIN_SUPPLEMENTARY_CODE_POINT ? 2 : 1;
    }


    public static int toCodePoint(char high, char low) {
        return ((high << 10) + low) + (MIN_SUPPLEMENTARY_CODE_POINT
                                       - (MIN_HIGH_SURROGATE << 10)
                                       - MIN_LOW_SURROGATE);
    }


    public static int codePointAt(CharSequence seq, int index) {
        char c1 = seq.charAt(index);
        if (isHighSurrogate(c1) && ++index < seq.length()) {
            char c2 = seq.charAt(index);
            if (isLowSurrogate(c2)) {
                return toCodePoint(c1, c2);
            }
        }
        return c1;
    }


    public static int codePointAt(char[] a, int index) {
        return codePointAtImpl(a, index, a.length);
    }


    public static int codePointAt(char[] a, int index, int limit) {
        if (index >= limit || limit < 0 || limit > a.length) {
            throw new IndexOutOfBoundsException();
        }
        return codePointAtImpl(a, index, limit);
    }

    static int codePointAtImpl(char[] a, int index, int limit) {
        char c1 = a[index];
        if (isHighSurrogate(c1) && ++index < limit) {
            char c2 = a[index];
            if (isLowSurrogate(c2)) {
                return toCodePoint(c1, c2);
            }
        }
        return c1;
    }


    public static int codePointBefore(CharSequence seq, int index) {
        char c2 = seq.charAt(--index);
        if (isLowSurrogate(c2) && index > 0) {
            char c1 = seq.charAt(--index);
            if (isHighSurrogate(c1)) {
                return toCodePoint(c1, c2);
            }
        }
        return c2;
    }


    public static int codePointBefore(char[] a, int index) {
        return codePointBeforeImpl(a, index, 0);
    }


    public static int codePointBefore(char[] a, int index, int start) {
        if (index <= start || start < 0 || start >= a.length) {
            throw new IndexOutOfBoundsException();
        }
        return codePointBeforeImpl(a, index, start);
    }

    static int codePointBeforeImpl(char[] a, int index, int start) {
        char c2 = a[--index];
        if (isLowSurrogate(c2) && index > start) {
            char c1 = a[--index];
            if (isHighSurrogate(c1)) {
                return toCodePoint(c1, c2);
            }
        }
        return c2;
    }


    public static char highSurrogate(int codePoint) {
        return (char) ((codePoint >>> 10)
            + (MIN_HIGH_SURROGATE - (MIN_SUPPLEMENTARY_CODE_POINT >>> 10)));
    }


    public static char lowSurrogate(int codePoint) {
        return (char) ((codePoint & 0x3ff) + MIN_LOW_SURROGATE);
    }


    public static int toChars(int codePoint, char[] dst, int dstIndex) {
        if (isBmpCodePoint(codePoint)) {
            dst[dstIndex] = (char) codePoint;
            return 1;
        } else if (isValidCodePoint(codePoint)) {
            toSurrogates(codePoint, dst, dstIndex);
            return 2;
        } else {
            throw new IllegalArgumentException();
        }
    }


    public static char[] toChars(int codePoint) {
        if (isBmpCodePoint(codePoint)) {
            return new char[] { (char) codePoint };
        } else if (isValidCodePoint(codePoint)) {
            char[] result = new char[2];
            toSurrogates(codePoint, result, 0);
            return result;
        } else {
            throw new IllegalArgumentException();
        }
    }

    static void toSurrogates(int codePoint, char[] dst, int index) {
        dst[index+1] = lowSurrogate(codePoint);
        dst[index] = highSurrogate(codePoint);
    }


    public static int codePointCount(CharSequence seq, int beginIndex, int endIndex) {
        int length = seq.length();
        if (beginIndex < 0 || endIndex > length || beginIndex > endIndex) {
            throw new IndexOutOfBoundsException();
        }
        int n = endIndex - beginIndex;
        for (int i = beginIndex; i < endIndex; ) {
            if (isHighSurrogate(seq.charAt(i++)) && i < endIndex &&
                isLowSurrogate(seq.charAt(i))) {
                n--;
                i++;
            }
        }
        return n;
    }


    public static int codePointCount(char[] a, int offset, int count) {
        if (count > a.length - offset || offset < 0 || count < 0) {
            throw new IndexOutOfBoundsException();
        }
        return codePointCountImpl(a, offset, count);
    }

    static int codePointCountImpl(char[] a, int offset, int count) {
        int endIndex = offset + count;
        int n = count;
        for (int i = offset; i < endIndex; ) {
            if (isHighSurrogate(a[i++]) && i < endIndex &&
                isLowSurrogate(a[i])) {
                n--;
                i++;
            }
        }
        return n;
    }


    public static int offsetByCodePoints(CharSequence seq, int index,
                                         int codePointOffset) {
        int length = seq.length();
        if (index < 0 || index > length) {
            throw new IndexOutOfBoundsException();
        }

        int x = index;
        if (codePointOffset >= 0) {
            int i;
            for (i = 0; x < length && i < codePointOffset; i++) {
                if (isHighSurrogate(seq.charAt(x++)) && x < length &&
                    isLowSurrogate(seq.charAt(x))) {
                    x++;
                }
            }
            if (i < codePointOffset) {
                throw new IndexOutOfBoundsException();
            }
        } else {
            int i;
            for (i = codePointOffset; x > 0 && i < 0; i++) {
                if (isLowSurrogate(seq.charAt(--x)) && x > 0 &&
                    isHighSurrogate(seq.charAt(x-1))) {
                    x--;
                }
            }
            if (i < 0) {
                throw new IndexOutOfBoundsException();
            }
        }
        return x;
    }


    public static int offsetByCodePoints(char[] a, int start, int count,
                                         int index, int codePointOffset) {
        if (count > a.length-start || start < 0 || count < 0
            || index < start || index > start+count) {
            throw new IndexOutOfBoundsException();
        }
        return offsetByCodePointsImpl(a, start, count, index, codePointOffset);
    }

    static int offsetByCodePointsImpl(char[]a, int start, int count,
                                      int index, int codePointOffset) {
        int x = index;
        if (codePointOffset >= 0) {
            int limit = start + count;
            int i;
            for (i = 0; x < limit && i < codePointOffset; i++) {
                if (isHighSurrogate(a[x++]) && x < limit &&
                    isLowSurrogate(a[x])) {
                    x++;
                }
            }
            if (i < codePointOffset) {
                throw new IndexOutOfBoundsException();
            }
        } else {
            int i;
            for (i = codePointOffset; x > start && i < 0; i++) {
                if (isLowSurrogate(a[--x]) && x > start &&
                    isHighSurrogate(a[x-1])) {
                    x--;
                }
            }
            if (i < 0) {
                throw new IndexOutOfBoundsException();
            }
        }
        return x;
    }


    public static boolean isLowerCase(char ch) {
        return isLowerCase((int)ch);
    }


    public static boolean isLowerCase(int codePoint) {
        return getType(codePoint) == Character.LOWERCASE_LETTER ||
               CharacterData.of(codePoint).isOtherLowercase(codePoint);
    }


    public static boolean isUpperCase(char ch) {
        return isUpperCase((int)ch);
    }


    public static boolean isUpperCase(int codePoint) {
        return getType(codePoint) == Character.UPPERCASE_LETTER ||
               CharacterData.of(codePoint).isOtherUppercase(codePoint);
    }


    public static boolean isTitleCase(char ch) {
        return isTitleCase((int)ch);
    }


    public static boolean isTitleCase(int codePoint) {
        return getType(codePoint) == Character.TITLECASE_LETTER;
    }


    public static boolean isDigit(char ch) {
        return isDigit((int)ch);
    }


    public static boolean isDigit(int codePoint) {
        return getType(codePoint) == Character.DECIMAL_DIGIT_NUMBER;
    }


    public static boolean isDefined(char ch) {
        return isDefined((int)ch);
    }


    public static boolean isDefined(int codePoint) {
        return getType(codePoint) != Character.UNASSIGNED;
    }


    public static boolean isLetter(char ch) {
        return isLetter((int)ch);
    }


    public static boolean isLetter(int codePoint) {
        return ((((1 << Character.UPPERCASE_LETTER) |
            (1 << Character.LOWERCASE_LETTER) |
            (1 << Character.TITLECASE_LETTER) |
            (1 << Character.MODIFIER_LETTER) |
            (1 << Character.OTHER_LETTER)) >> getType(codePoint)) & 1)
            != 0;
    }


    public static boolean isLetterOrDigit(char ch) {
        return isLetterOrDigit((int)ch);
    }


    public static boolean isLetterOrDigit(int codePoint) {
        return ((((1 << Character.UPPERCASE_LETTER) |
            (1 << Character.LOWERCASE_LETTER) |
            (1 << Character.TITLECASE_LETTER) |
            (1 << Character.MODIFIER_LETTER) |
            (1 << Character.OTHER_LETTER) |
            (1 << Character.DECIMAL_DIGIT_NUMBER)) >> getType(codePoint)) & 1)
            != 0;
    }


    @Deprecated
    public static boolean isJavaLetter(char ch) {
        return isJavaIdentifierStart(ch);
    }


    @Deprecated
    public static boolean isJavaLetterOrDigit(char ch) {
        return isJavaIdentifierPart(ch);
    }


    public static boolean isAlphabetic(int codePoint) {
        return (((((1 << Character.UPPERCASE_LETTER) |
            (1 << Character.LOWERCASE_LETTER) |
            (1 << Character.TITLECASE_LETTER) |
            (1 << Character.MODIFIER_LETTER) |
            (1 << Character.OTHER_LETTER) |
            (1 << Character.LETTER_NUMBER)) >> getType(codePoint)) & 1) != 0) ||
            CharacterData.of(codePoint).isOtherAlphabetic(codePoint);
    }


    public static boolean isIdeographic(int codePoint) {
        return CharacterData.of(codePoint).isIdeographic(codePoint);
    }


    public static boolean isJavaIdentifierStart(char ch) {
        return isJavaIdentifierStart((int)ch);
    }


    public static boolean isJavaIdentifierStart(int codePoint) {
        return CharacterData.of(codePoint).isJavaIdentifierStart(codePoint);
    }


    public static boolean isJavaIdentifierPart(char ch) {
        return isJavaIdentifierPart((int)ch);
    }


    public static boolean isJavaIdentifierPart(int codePoint) {
        return CharacterData.of(codePoint).isJavaIdentifierPart(codePoint);
    }


    public static boolean isUnicodeIdentifierStart(char ch) {
        return isUnicodeIdentifierStart((int)ch);
    }


    public static boolean isUnicodeIdentifierStart(int codePoint) {
        return CharacterData.of(codePoint).isUnicodeIdentifierStart(codePoint);
    }


    public static boolean isUnicodeIdentifierPart(char ch) {
        return isUnicodeIdentifierPart((int)ch);
    }


    public static boolean isUnicodeIdentifierPart(int codePoint) {
        return CharacterData.of(codePoint).isUnicodeIdentifierPart(codePoint);
    }


    public static boolean isIdentifierIgnorable(char ch) {
        return isIdentifierIgnorable((int)ch);
    }


    public static boolean isIdentifierIgnorable(int codePoint) {
        return CharacterData.of(codePoint).isIdentifierIgnorable(codePoint);
    }


    public static char toLowerCase(char ch) {
        return (char)toLowerCase((int)ch);
    }


    public static int toLowerCase(int codePoint) {
        return CharacterData.of(codePoint).toLowerCase(codePoint);
    }


    public static char toUpperCase(char ch) {
        return (char)toUpperCase((int)ch);
    }


    public static int toUpperCase(int codePoint) {
        return CharacterData.of(codePoint).toUpperCase(codePoint);
    }


    public static char toTitleCase(char ch) {
        return (char)toTitleCase((int)ch);
    }


    public static int toTitleCase(int codePoint) {
        return CharacterData.of(codePoint).toTitleCase(codePoint);
    }


    public static int digit(char ch, int radix) {
        return digit((int)ch, radix);
    }


    public static int digit(int codePoint, int radix) {
        return CharacterData.of(codePoint).digit(codePoint, radix);
    }


    public static int getNumericValue(char ch) {
        return getNumericValue((int)ch);
    }


    public static int getNumericValue(int codePoint) {
        return CharacterData.of(codePoint).getNumericValue(codePoint);
    }


    @Deprecated
    public static boolean isSpace(char ch) {
        return (ch <= 0x0020) &&
            (((((1L << 0x0009) |
            (1L << 0x000A) |
            (1L << 0x000C) |
            (1L << 0x000D) |
            (1L << 0x0020)) >> ch) & 1L) != 0);
    }



    public static boolean isSpaceChar(char ch) {
        return isSpaceChar((int)ch);
    }


    public static boolean isSpaceChar(int codePoint) {
        return ((((1 << Character.SPACE_SEPARATOR) |
                  (1 << Character.LINE_SEPARATOR) |
                  (1 << Character.PARAGRAPH_SEPARATOR)) >> getType(codePoint)) & 1)
            != 0;
    }


    public static boolean isWhitespace(char ch) {
        return isWhitespace((int)ch);
    }


    public static boolean isWhitespace(int codePoint) {
        return CharacterData.of(codePoint).isWhitespace(codePoint);
    }


    public static boolean isISOControl(char ch) {
        return isISOControl((int)ch);
    }


    public static boolean isISOControl(int codePoint) {
        return codePoint <= 0x9F &&
            (codePoint >= 0x7F || (codePoint >>> 5 == 0));
    }


    public static int getType(char ch) {
        return getType((int)ch);
    }


    public static int getType(int codePoint) {
        return CharacterData.of(codePoint).getType(codePoint);
    }


    public static char forDigit(int digit, int radix) {
        if ((digit >= radix) || (digit < 0)) {
            return '\0';
        }
        if ((radix < Character.MIN_RADIX) || (radix > Character.MAX_RADIX)) {
            return '\0';
        }
        if (digit < 10) {
            return (char)('0' + digit);
        }
        return (char)('a' - 10 + digit);
    }


    public static byte getDirectionality(char ch) {
        return getDirectionality((int)ch);
    }


    public static byte getDirectionality(int codePoint) {
        return CharacterData.of(codePoint).getDirectionality(codePoint);
    }


    public static boolean isMirrored(char ch) {
        return isMirrored((int)ch);
    }


    public static boolean isMirrored(int codePoint) {
        return CharacterData.of(codePoint).isMirrored(codePoint);
    }


    public int compareTo(Character anotherCharacter) {
        return compare(this.value, anotherCharacter.value);
    }


    public static int compare(char x, char y) {
        return x - y;
    }


    static int toUpperCaseEx(int codePoint) {
        assert isValidCodePoint(codePoint);
        return CharacterData.of(codePoint).toUpperCaseEx(codePoint);
    }


    static char[] toUpperCaseCharArray(int codePoint) {
        assert isBmpCodePoint(codePoint);
        return CharacterData.of(codePoint).toUpperCaseCharArray(codePoint);
    }


    public static final int SIZE = 16;


    public static final int BYTES = SIZE / Byte.SIZE;


    public static char reverseBytes(char ch) {
        return (char) (((ch & 0xFF00) >> 8) | (ch << 8));
    }


    public static String getName(int codePoint) {
        if (!isValidCodePoint(codePoint)) {
            throw new IllegalArgumentException();
        }
        String name = CharacterName.get(codePoint);
        if (name != null)
            return name;
        if (getType(codePoint) == UNASSIGNED)
            return null;
        UnicodeBlock block = UnicodeBlock.of(codePoint);
        if (block != null)
            return block.toString().replace('_', ' ') + " "
                   + Integer.toHexString(codePoint).toUpperCase(Locale.ENGLISH);
        return Integer.toHexString(codePoint).toUpperCase(Locale.ENGLISH);
    }
}
