
package javax.swing.text.html;

import java.awt.font.TextAttribute;
import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import sun.swing.SwingUtilities2;
import static sun.swing.SwingUtilities2.IMPLIED_CR;


public class HTMLDocument extends DefaultStyledDocument {

    public HTMLDocument() {
        this(new GapContent(BUFFER_SIZE_DEFAULT), new StyleSheet());
    }


    public HTMLDocument(StyleSheet styles) {
        this(new GapContent(BUFFER_SIZE_DEFAULT), styles);
    }


    public HTMLDocument(Content c, StyleSheet styles) {
        super(c, styles);
    }


    public HTMLEditorKit.ParserCallback getReader(int pos) {
        Object desc = getProperty(Document.StreamDescriptionProperty);
        if (desc instanceof URL) {
            setBase((URL)desc);
        }
        HTMLReader reader = new HTMLReader(pos);
        return reader;
    }


    public HTMLEditorKit.ParserCallback getReader(int pos, int popDepth,
                                                  int pushDepth,
                                                  HTML.Tag insertTag) {
        return getReader(pos, popDepth, pushDepth, insertTag, true);
    }


    HTMLEditorKit.ParserCallback getReader(int pos, int popDepth,
                                           int pushDepth,
                                           HTML.Tag insertTag,
                                           boolean insertInsertTag) {
        Object desc = getProperty(Document.StreamDescriptionProperty);
        if (desc instanceof URL) {
            setBase((URL)desc);
        }
        HTMLReader reader = new HTMLReader(pos, popDepth, pushDepth,
                                           insertTag, insertInsertTag, false,
                                           true);
        return reader;
    }


    public URL getBase() {
        return base;
    }


    public void setBase(URL u) {
        base = u;
        getStyleSheet().setBase(u);
    }


    protected void insert(int offset, ElementSpec[] data) throws BadLocationException {
        super.insert(offset, data);
    }


    protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
        if(attr == null) {
            attr = contentAttributeSet;
        }

        else if (attr.isDefined(StyleConstants.ComposedTextAttribute)) {
            ((MutableAttributeSet)attr).addAttributes(contentAttributeSet);
        }

        if (attr.isDefined(IMPLIED_CR)) {
            ((MutableAttributeSet)attr).removeAttribute(IMPLIED_CR);
        }

        super.insertUpdate(chng, attr);
    }


    protected void create(ElementSpec[] data) {
        super.create(data);
    }


    public void setParagraphAttributes(int offset, int length, AttributeSet s,
                                       boolean replace) {
        try {
            writeLock();
            int end = Math.min(offset + length, getLength());
            Element e = getParagraphElement(offset);
            offset = e.getStartOffset();
            e = getParagraphElement(end);
            length = Math.max(0, e.getEndOffset() - offset);
            DefaultDocumentEvent changes =
                new DefaultDocumentEvent(offset, length,
                                         DocumentEvent.EventType.CHANGE);
            AttributeSet sCopy = s.copyAttributes();
            int lastEnd = Integer.MAX_VALUE;
            for (int pos = offset; pos <= end; pos = lastEnd) {
                Element paragraph = getParagraphElement(pos);
                if (lastEnd == paragraph.getEndOffset()) {
                    lastEnd++;
                }
                else {
                    lastEnd = paragraph.getEndOffset();
                }
                MutableAttributeSet attr =
                    (MutableAttributeSet) paragraph.getAttributes();
                changes.addEdit(new AttributeUndoableEdit(paragraph, sCopy, replace));
                if (replace) {
                    attr.removeAttributes(attr);
                }
                attr.addAttributes(s);
            }
            changes.end();
            fireChangedUpdate(changes);
            fireUndoableEditUpdate(new UndoableEditEvent(this, changes));
        } finally {
            writeUnlock();
        }
    }


    public StyleSheet getStyleSheet() {
        return (StyleSheet) getAttributeContext();
    }


    public Iterator getIterator(HTML.Tag t) {
        if (t.isBlock()) {
            return null;
        }
        return new LeafIterator(t, this);
    }


    protected Element createLeafElement(Element parent, AttributeSet a, int p0, int p1) {
        return new RunElement(parent, a, p0, p1);
    }


    protected Element createBranchElement(Element parent, AttributeSet a) {
        return new BlockElement(parent, a);
    }


    protected AbstractElement createDefaultRoot() {
        writeLock();
        MutableAttributeSet a = new SimpleAttributeSet();
        a.addAttribute(StyleConstants.NameAttribute, HTML.Tag.HTML);
        BlockElement html = new BlockElement(null, a.copyAttributes());
        a.removeAttributes(a);
        a.addAttribute(StyleConstants.NameAttribute, HTML.Tag.BODY);
        BlockElement body = new BlockElement(html, a.copyAttributes());
        a.removeAttributes(a);
        a.addAttribute(StyleConstants.NameAttribute, HTML.Tag.P);
        getStyleSheet().addCSSAttributeFromHTML(a, CSS.Attribute.MARGIN_TOP, "0");
        BlockElement paragraph = new BlockElement(body, a.copyAttributes());
        a.removeAttributes(a);
        a.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
        RunElement brk = new RunElement(paragraph, a, 0, 1);
        Element[] buff = new Element[1];
        buff[0] = brk;
        paragraph.replace(0, 0, buff);
        buff[0] = paragraph;
        body.replace(0, 0, buff);
        buff[0] = body;
        html.replace(0, 0, buff);
        writeUnlock();
        return html;
    }


    public void setTokenThreshold(int n) {
        putProperty(TokenThreshold, new Integer(n));
    }


    public int getTokenThreshold() {
        Integer i = (Integer) getProperty(TokenThreshold);
        if (i != null) {
            return i.intValue();
        }
        return Integer.MAX_VALUE;
    }


    public void setPreservesUnknownTags(boolean preservesTags) {
        preservesUnknownTags = preservesTags;
    }


    public boolean getPreservesUnknownTags() {
        return preservesUnknownTags;
    }


    public void processHTMLFrameHyperlinkEvent(HTMLFrameHyperlinkEvent e) {
        String frameName = e.getTarget();
        Element element = e.getSourceElement();
        String urlStr = e.getURL().toString();

        if (frameName.equals("_self")) {

            updateFrame(element, urlStr);
        } else if (frameName.equals("_parent")) {

            updateFrameSet(element.getParentElement(), urlStr);
        } else {

            Element targetElement = findFrame(frameName);
            if (targetElement != null) {
                updateFrame(targetElement, urlStr);
            }
        }
    }



    private Element findFrame(String frameName) {
        ElementIterator it = new ElementIterator(this);
        Element next;

        while ((next = it.next()) != null) {
            AttributeSet attr = next.getAttributes();
            if (matchNameAttribute(attr, HTML.Tag.FRAME)) {
                String frameTarget = (String)attr.getAttribute(HTML.Attribute.NAME);
                if (frameTarget != null && frameTarget.equals(frameName)) {
                    break;
                }
            }
        }
        return next;
    }


    static boolean matchNameAttribute(AttributeSet attr, HTML.Tag tag) {
        Object o = attr.getAttribute(StyleConstants.NameAttribute);
        if (o instanceof HTML.Tag) {
            HTML.Tag name = (HTML.Tag) o;
            if (name == tag) {
                return true;
            }
        }
        return false;
    }


    private void updateFrameSet(Element element, String url) {
        try {
            int startOffset = element.getStartOffset();
            int endOffset = Math.min(getLength(), element.getEndOffset());
            String html = "<frame";
            if (url != null) {
                html += " src=\"" + url + "\"";
            }
            html += ">";
            installParserIfNecessary();
            setOuterHTML(element, html);
        } catch (BadLocationException e1) {
            } catch (IOException ioe) {
            }
    }



    private void updateFrame(Element element, String url) {

        try {
            writeLock();
            DefaultDocumentEvent changes = new DefaultDocumentEvent(element.getStartOffset(),
                                                                    1,
                                                                    DocumentEvent.EventType.CHANGE);
            AttributeSet sCopy = element.getAttributes().copyAttributes();
            MutableAttributeSet attr = (MutableAttributeSet) element.getAttributes();
            changes.addEdit(new AttributeUndoableEdit(element, sCopy, false));
            attr.removeAttribute(HTML.Attribute.SRC);
            attr.addAttribute(HTML.Attribute.SRC, url);
            changes.end();
            fireChangedUpdate(changes);
            fireUndoableEditUpdate(new UndoableEditEvent(this, changes));
        } finally {
            writeUnlock();
        }
    }



    boolean isFrameDocument() {
        return frameDocument;
    }


    void setFrameDocumentState(boolean frameDoc) {
        this.frameDocument = frameDoc;
    }


    void addMap(Map map) {
        String     name = map.getName();

        if (name != null) {
            Object     maps = getProperty(MAP_PROPERTY);

            if (maps == null) {
                maps = new Hashtable(11);
                putProperty(MAP_PROPERTY, maps);
            }
            if (maps instanceof Hashtable) {
                ((Hashtable)maps).put("#" + name, map);
            }
        }
    }


    void removeMap(Map map) {
        String     name = map.getName();

        if (name != null) {
            Object     maps = getProperty(MAP_PROPERTY);

            if (maps instanceof Hashtable) {
                ((Hashtable)maps).remove("#" + name);
            }
        }
    }


    Map getMap(String name) {
        if (name != null) {
            Object     maps = getProperty(MAP_PROPERTY);

            if (maps != null && (maps instanceof Hashtable)) {
                return (Map)((Hashtable)maps).get(name);
            }
        }
        return null;
    }


    Enumeration getMaps() {
        Object     maps = getProperty(MAP_PROPERTY);

        if (maps instanceof Hashtable) {
            return ((Hashtable)maps).elements();
        }
        return null;
    }



    void setDefaultStyleSheetType(String contentType) {
        putProperty(StyleType, contentType);
    }



    String getDefaultStyleSheetType() {
        String retValue = (String)getProperty(StyleType);
        if (retValue == null) {
            return "text/css";
        }
        return retValue;
    }


    public void setParser(HTMLEditorKit.Parser parser) {
        this.parser = parser;
        putProperty("__PARSER__", null);
    }


    public HTMLEditorKit.Parser getParser() {
        Object p = getProperty("__PARSER__");

        if (p instanceof HTMLEditorKit.Parser) {
            return (HTMLEditorKit.Parser)p;
        }
        return parser;
    }


    public void setInnerHTML(Element elem, String htmlText) throws
                             BadLocationException, IOException {
        verifyParser();
        if (elem != null && elem.isLeaf()) {
            throw new IllegalArgumentException
                ("Can not set inner HTML of a leaf");
        }
        if (elem != null && htmlText != null) {
            int oldCount = elem.getElementCount();
            int insertPosition = elem.getStartOffset();
            insertHTML(elem, elem.getStartOffset(), htmlText, true);
            if (elem.getElementCount() > oldCount) {
                removeElements(elem, elem.getElementCount() - oldCount,
                               oldCount);
            }
        }
    }


    public void setOuterHTML(Element elem, String htmlText) throws
                            BadLocationException, IOException {
        verifyParser();
        if (elem != null && elem.getParentElement() != null &&
            htmlText != null) {
            int start = elem.getStartOffset();
            int end = elem.getEndOffset();
            int startLength = getLength();
            boolean wantsNewline = !elem.isLeaf();
            if (!wantsNewline && (end > startLength ||
                                 getText(end - 1, 1).charAt(0) == NEWLINE[0])){
                wantsNewline = true;
            }
            Element parent = elem.getParentElement();
            int oldCount = parent.getElementCount();
            insertHTML(parent, start, htmlText, wantsNewline);
            int newLength = getLength();
            if (oldCount != parent.getElementCount()) {
                int removeIndex = parent.getElementIndex(start + newLength -
                                                         startLength);
                removeElements(parent, removeIndex, 1);
            }
        }
    }


    public void insertAfterStart(Element elem, String htmlText) throws
                                 BadLocationException, IOException {
        verifyParser();

        if (elem == null || htmlText == null) {
            return;
        }

        if (elem.isLeaf()) {
            throw new IllegalArgumentException
                ("Can not insert HTML after start of a leaf");
        }
        insertHTML(elem, elem.getStartOffset(), htmlText, false);
    }


    public void insertBeforeEnd(Element elem, String htmlText) throws
                                BadLocationException, IOException {
        verifyParser();
        if (elem != null && elem.isLeaf()) {
            throw new IllegalArgumentException
                ("Can not set inner HTML before end of leaf");
        }
        if (elem != null) {
            int offset = elem.getEndOffset();
            if (elem.getElement(elem.getElementIndex(offset - 1)).isLeaf() &&
                getText(offset - 1, 1).charAt(0) == NEWLINE[0]) {
                offset--;
            }
            insertHTML(elem, offset, htmlText, false);
        }
    }


    public void insertBeforeStart(Element elem, String htmlText) throws
                                  BadLocationException, IOException {
        verifyParser();
        if (elem != null) {
            Element parent = elem.getParentElement();

            if (parent != null) {
                insertHTML(parent, elem.getStartOffset(), htmlText, false);
            }
        }
    }


    public void insertAfterEnd(Element elem, String htmlText) throws
                               BadLocationException, IOException {
        verifyParser();
        if (elem != null) {
            Element parent = elem.getParentElement();

            if (parent != null) {
                if (HTML.Tag.BODY.name.equals(parent.getName())) {
                    insertInBody = true;
                }
                int offset = elem.getEndOffset();
                if (offset > (getLength() + 1)) {
                    offset--;
                }
                else if (elem.isLeaf() && getText(offset - 1, 1).
                    charAt(0) == NEWLINE[0]) {
                    offset--;
                }
                insertHTML(parent, offset, htmlText, false);
                if (insertInBody) {
                    insertInBody = false;
                }
            }
        }
    }


    public Element getElement(String id) {
        if (id == null) {
            return null;
        }
        return getElement(getDefaultRootElement(), HTML.Attribute.ID, id,
                          true);
    }


    public Element getElement(Element e, Object attribute, Object value) {
        return getElement(e, attribute, value, true);
    }


    private Element getElement(Element e, Object attribute, Object value,
                               boolean searchLeafAttributes) {
        AttributeSet attr = e.getAttributes();

        if (attr != null && attr.isDefined(attribute)) {
            if (value.equals(attr.getAttribute(attribute))) {
                return e;
            }
        }
        if (!e.isLeaf()) {
            for (int counter = 0, maxCounter = e.getElementCount();
                 counter < maxCounter; counter++) {
                Element retValue = getElement(e.getElement(counter), attribute,
                                              value, searchLeafAttributes);

                if (retValue != null) {
                    return retValue;
                }
            }
        }
        else if (searchLeafAttributes && attr != null) {
            Enumeration names = attr.getAttributeNames();
            if (names != null) {
                while (names.hasMoreElements()) {
                    Object name = names.nextElement();
                    if ((name instanceof HTML.Tag) &&
                        (attr.getAttribute(name) instanceof AttributeSet)) {

                        AttributeSet check = (AttributeSet)attr.
                                             getAttribute(name);
                        if (check.isDefined(attribute) &&
                            value.equals(check.getAttribute(attribute))) {
                            return e;
                        }
                    }
                }
            }
        }
        return null;
    }


    private void verifyParser() {
        if (getParser() == null) {
            throw new IllegalStateException("No HTMLEditorKit.Parser");
        }
    }


    private void installParserIfNecessary() {
        if (getParser() == null) {
            setParser(new HTMLEditorKit().getParser());
        }
    }


    private void insertHTML(Element parent, int offset, String html,
                            boolean wantsTrailingNewline)
                 throws BadLocationException, IOException {
        if (parent != null && html != null) {
            HTMLEditorKit.Parser parser = getParser();
            if (parser != null) {
                int lastOffset = Math.max(0, offset - 1);
                Element charElement = getCharacterElement(lastOffset);
                Element commonParent = parent;
                int pop = 0;
                int push = 0;

                if (parent.getStartOffset() > lastOffset) {
                    while (commonParent != null &&
                           commonParent.getStartOffset() > lastOffset) {
                        commonParent = commonParent.getParentElement();
                        push++;
                    }
                    if (commonParent == null) {
                        throw new BadLocationException("No common parent",
                                                       offset);
                    }
                }
                while (charElement != null && charElement != commonParent) {
                    pop++;
                    charElement = charElement.getParentElement();
                }
                if (charElement != null) {
                    HTMLReader reader = new HTMLReader(offset, pop - 1, push,
                                                       null, false, true,
                                                       wantsTrailingNewline);

                    parser.parse(new StringReader(html), reader, true);
                    reader.flush();
                }
            }
        }
    }


    private void removeElements(Element e, int index, int count) throws BadLocationException {
        writeLock();
        try {
            int start = e.getElement(index).getStartOffset();
            int end = e.getElement(index + count - 1).getEndOffset();
            if (end > getLength()) {
                removeElementsAtEnd(e, index, count, start, end);
            }
            else {
                removeElements(e, index, count, start, end);
            }
        } finally {
            writeUnlock();
        }
    }


    private void removeElementsAtEnd(Element e, int index, int count,
                         int start, int end) throws BadLocationException {
        boolean isLeaf = (e.getElement(index - 1).isLeaf());
        DefaultDocumentEvent dde = new DefaultDocumentEvent(
                       start - 1, end - start + 1, DocumentEvent.
                       EventType.REMOVE);

        if (isLeaf) {
            Element endE = getCharacterElement(getLength());
            index--;
            if (endE.getParentElement() != e) {
                replace(dde, e, index, ++count, start, end, true, true);
            }
            else {
                replace(dde, e, index, count, start, end, true, false);
            }
        }
        else {
            Element newLineE = e.getElement(index - 1);
            while (!newLineE.isLeaf()) {
                newLineE = newLineE.getElement(newLineE.getElementCount() - 1);
            }
            newLineE = newLineE.getParentElement();
            replace(dde, e, index, count, start, end, false, false);
            replace(dde, newLineE, newLineE.getElementCount() - 1, 1, start,
                    end, true, true);
        }
        postRemoveUpdate(dde);
        dde.end();
        fireRemoveUpdate(dde);
        fireUndoableEditUpdate(new UndoableEditEvent(this, dde));
    }


    private void replace(DefaultDocumentEvent dde, Element e, int index,
                         int count, int start, int end, boolean remove,
                         boolean create) throws BadLocationException {
        Element[] added;
        AttributeSet attrs = e.getElement(index).getAttributes();
        Element[] removed = new Element[count];

        for (int counter = 0; counter < count; counter++) {
            removed[counter] = e.getElement(counter + index);
        }
        if (remove) {
            UndoableEdit u = getContent().remove(start - 1, end - start);
            if (u != null) {
                dde.addEdit(u);
            }
        }
        if (create) {
            added = new Element[1];
            added[0] = createLeafElement(e, attrs, start - 1, start);
        }
        else {
            added = new Element[0];
        }
        dde.addEdit(new ElementEdit(e, index, removed, added));
        ((AbstractDocument.BranchElement)e).replace(
                                             index, removed.length, added);
    }


    private void removeElements(Element e, int index, int count,
                             int start, int end) throws BadLocationException {
        Element[] removed = new Element[count];
        Element[] added = new Element[0];
        for (int counter = 0; counter < count; counter++) {
            removed[counter] = e.getElement(counter + index);
        }
        DefaultDocumentEvent dde = new DefaultDocumentEvent
                (start, end - start, DocumentEvent.EventType.REMOVE);
        ((AbstractDocument.BranchElement)e).replace(index, removed.length,
                                                    added);
        dde.addEdit(new ElementEdit(e, index, removed, added));
        UndoableEdit u = getContent().remove(start, end - start);
        if (u != null) {
            dde.addEdit(u);
        }
        postRemoveUpdate(dde);
        dde.end();
        fireRemoveUpdate(dde);
        if (u != null) {
            fireUndoableEditUpdate(new UndoableEditEvent(this, dde));
        }
    }


    void obtainLock() {
        writeLock();
    }

    void releaseLock() {
        writeUnlock();
    }

    protected void fireChangedUpdate(DocumentEvent e) {
        super.fireChangedUpdate(e);
    }


    protected void fireUndoableEditUpdate(UndoableEditEvent e) {
        super.fireUndoableEditUpdate(e);
    }

    boolean hasBaseTag() {
        return hasBaseTag;
    }

    String getBaseTarget() {
        return baseTarget;
    }


    private boolean frameDocument = false;
    private boolean preservesUnknownTags = true;


    private HashMap<String, ButtonGroup> radioButtonGroupsMap;


    static final String TokenThreshold = "token threshold";

    private static final int MaxThreshold = 10000;

    private static final int StepThreshold = 5;



    public static final String AdditionalComments = "AdditionalComments";


     static final String StyleType = "StyleType";


    URL base;


    boolean hasBaseTag = false;


    private String baseTarget = null;


    private HTMLEditorKit.Parser parser;


    private static AttributeSet contentAttributeSet;


    static String MAP_PROPERTY = "__MAP__";

    private static char[] NEWLINE;


    private boolean insertInBody = false;


    private static final String I18NProperty = "i18n";

    static {
        contentAttributeSet = new SimpleAttributeSet();
        ((MutableAttributeSet)contentAttributeSet).
                        addAttribute(StyleConstants.NameAttribute,
                                     HTML.Tag.CONTENT);
        NEWLINE = new char[1];
        NEWLINE[0] = '\n';
    }



    public static abstract class Iterator {


        public abstract AttributeSet getAttributes();


        public abstract int getStartOffset();


        public abstract int getEndOffset();


        public abstract void next();


        public abstract boolean isValid();


        public abstract HTML.Tag getTag();
    }


    static class LeafIterator extends Iterator {

        LeafIterator(HTML.Tag t, Document doc) {
            tag = t;
            pos = new ElementIterator(doc);
            endOffset = 0;
            next();
        }


        public AttributeSet getAttributes() {
            Element elem = pos.current();
            if (elem != null) {
                AttributeSet a = (AttributeSet)
                    elem.getAttributes().getAttribute(tag);
                if (a == null) {
                    a = elem.getAttributes();
                }
                return a;
            }
            return null;
        }


        public int getStartOffset() {
            Element elem = pos.current();
            if (elem != null) {
                return elem.getStartOffset();
            }
            return -1;
        }


        public int getEndOffset() {
            return endOffset;
        }


        public void next() {
            for (nextLeaf(pos); isValid(); nextLeaf(pos)) {
                Element elem = pos.current();
                if (elem.getStartOffset() >= endOffset) {
                    AttributeSet a = pos.current().getAttributes();

                    if (a.isDefined(tag) ||
                        a.getAttribute(StyleConstants.NameAttribute) == tag) {

                        setEndOffset();
                        break;
                    }
                }
            }
        }


        public HTML.Tag getTag() {
            return tag;
        }


        public boolean isValid() {
            return (pos.current() != null);
        }


        void nextLeaf(ElementIterator iter) {
            for (iter.next(); iter.current() != null; iter.next()) {
                Element e = iter.current();
                if (e.isLeaf()) {
                    break;
                }
            }
        }


        void setEndOffset() {
            AttributeSet a0 = getAttributes();
            endOffset = pos.current().getEndOffset();
            ElementIterator fwd = (ElementIterator) pos.clone();
            for (nextLeaf(fwd); fwd.current() != null; nextLeaf(fwd)) {
                Element e = fwd.current();
                AttributeSet a1 = (AttributeSet) e.getAttributes().getAttribute(tag);
                if ((a1 == null) || (! a1.equals(a0))) {
                    break;
                }
                endOffset = e.getEndOffset();
            }
        }

        private int endOffset;
        private HTML.Tag tag;
        private ElementIterator pos;

    }


    public class HTMLReader extends HTMLEditorKit.ParserCallback {

        public HTMLReader(int offset) {
            this(offset, 0, 0, null);
        }

        public HTMLReader(int offset, int popDepth, int pushDepth,
                          HTML.Tag insertTag) {
            this(offset, popDepth, pushDepth, insertTag, true, false, true);
        }


        HTMLReader(int offset, int popDepth, int pushDepth,
                   HTML.Tag insertTag, boolean insertInsertTag,
                   boolean insertAfterImplied, boolean wantsTrailingNewline) {
            emptyDocument = (getLength() == 0);
            isStyleCSS = "text/css".equals(getDefaultStyleSheetType());
            this.offset = offset;
            threshold = HTMLDocument.this.getTokenThreshold();
            tagMap = new Hashtable<HTML.Tag, TagAction>(57);
            TagAction na = new TagAction();
            TagAction ba = new BlockAction();
            TagAction pa = new ParagraphAction();
            TagAction ca = new CharacterAction();
            TagAction sa = new SpecialAction();
            TagAction fa = new FormAction();
            TagAction ha = new HiddenAction();
            TagAction conv = new ConvertAction();

            tagMap.put(HTML.Tag.A, new AnchorAction());
            tagMap.put(HTML.Tag.ADDRESS, ca);
            tagMap.put(HTML.Tag.APPLET, ha);
            tagMap.put(HTML.Tag.AREA, new AreaAction());
            tagMap.put(HTML.Tag.B, conv);
            tagMap.put(HTML.Tag.BASE, new BaseAction());
            tagMap.put(HTML.Tag.BASEFONT, ca);
            tagMap.put(HTML.Tag.BIG, ca);
            tagMap.put(HTML.Tag.BLOCKQUOTE, ba);
            tagMap.put(HTML.Tag.BODY, ba);
            tagMap.put(HTML.Tag.BR, sa);
            tagMap.put(HTML.Tag.CAPTION, ba);
            tagMap.put(HTML.Tag.CENTER, ba);
            tagMap.put(HTML.Tag.CITE, ca);
            tagMap.put(HTML.Tag.CODE, ca);
            tagMap.put(HTML.Tag.DD, ba);
            tagMap.put(HTML.Tag.DFN, ca);
            tagMap.put(HTML.Tag.DIR, ba);
            tagMap.put(HTML.Tag.DIV, ba);
            tagMap.put(HTML.Tag.DL, ba);
            tagMap.put(HTML.Tag.DT, pa);
            tagMap.put(HTML.Tag.EM, ca);
            tagMap.put(HTML.Tag.FONT, conv);
            tagMap.put(HTML.Tag.FORM, new FormTagAction());
            tagMap.put(HTML.Tag.FRAME, sa);
            tagMap.put(HTML.Tag.FRAMESET, ba);
            tagMap.put(HTML.Tag.H1, pa);
            tagMap.put(HTML.Tag.H2, pa);
            tagMap.put(HTML.Tag.H3, pa);
            tagMap.put(HTML.Tag.H4, pa);
            tagMap.put(HTML.Tag.H5, pa);
            tagMap.put(HTML.Tag.H6, pa);
            tagMap.put(HTML.Tag.HEAD, new HeadAction());
            tagMap.put(HTML.Tag.HR, sa);
            tagMap.put(HTML.Tag.HTML, ba);
            tagMap.put(HTML.Tag.I, conv);
            tagMap.put(HTML.Tag.IMG, sa);
            tagMap.put(HTML.Tag.INPUT, fa);
            tagMap.put(HTML.Tag.ISINDEX, new IsindexAction());
            tagMap.put(HTML.Tag.KBD, ca);
            tagMap.put(HTML.Tag.LI, ba);
            tagMap.put(HTML.Tag.LINK, new LinkAction());
            tagMap.put(HTML.Tag.MAP, new MapAction());
            tagMap.put(HTML.Tag.MENU, ba);
            tagMap.put(HTML.Tag.META, new MetaAction());
            tagMap.put(HTML.Tag.NOBR, ca);
            tagMap.put(HTML.Tag.NOFRAMES, ba);
            tagMap.put(HTML.Tag.OBJECT, sa);
            tagMap.put(HTML.Tag.OL, ba);
            tagMap.put(HTML.Tag.OPTION, fa);
            tagMap.put(HTML.Tag.P, pa);
            tagMap.put(HTML.Tag.PARAM, new ObjectAction());
            tagMap.put(HTML.Tag.PRE, new PreAction());
            tagMap.put(HTML.Tag.SAMP, ca);
            tagMap.put(HTML.Tag.SCRIPT, ha);
            tagMap.put(HTML.Tag.SELECT, fa);
            tagMap.put(HTML.Tag.SMALL, ca);
            tagMap.put(HTML.Tag.SPAN, ca);
            tagMap.put(HTML.Tag.STRIKE, conv);
            tagMap.put(HTML.Tag.S, ca);
            tagMap.put(HTML.Tag.STRONG, ca);
            tagMap.put(HTML.Tag.STYLE, new StyleAction());
            tagMap.put(HTML.Tag.SUB, conv);
            tagMap.put(HTML.Tag.SUP, conv);
            tagMap.put(HTML.Tag.TABLE, ba);
            tagMap.put(HTML.Tag.TD, ba);
            tagMap.put(HTML.Tag.TEXTAREA, fa);
            tagMap.put(HTML.Tag.TH, ba);
            tagMap.put(HTML.Tag.TITLE, new TitleAction());
            tagMap.put(HTML.Tag.TR, ba);
            tagMap.put(HTML.Tag.TT, ca);
            tagMap.put(HTML.Tag.U, conv);
            tagMap.put(HTML.Tag.UL, ba);
            tagMap.put(HTML.Tag.VAR, ca);

            if (insertTag != null) {
                this.insertTag = insertTag;
                this.popDepth = popDepth;
                this.pushDepth = pushDepth;
                this.insertInsertTag = insertInsertTag;
                foundInsertTag = false;
            }
            else {
                foundInsertTag = true;
            }
            if (insertAfterImplied) {
                this.popDepth = popDepth;
                this.pushDepth = pushDepth;
                this.insertAfterImplied = true;
                foundInsertTag = false;
                midInsert = false;
                this.insertInsertTag = true;
                this.wantsTrailingNewline = wantsTrailingNewline;
            }
            else {
                midInsert = (!emptyDocument && insertTag == null);
                if (midInsert) {
                    generateEndsSpecsForMidInsert();
                }
            }


            if (!emptyDocument && !midInsert) {
                int targetOffset = Math.max(this.offset - 1, 0);
                Element elem =
                        HTMLDocument.this.getCharacterElement(targetOffset);

                for (int i = 0; i <= this.popDepth; i++) {
                    elem = elem.getParentElement();
                }

                for (int i = 0; i < this.pushDepth; i++) {
                    int index = elem.getElementIndex(this.offset);
                    elem = elem.getElement(index);
                }
                AttributeSet attrs = elem.getAttributes();
                if (attrs != null) {
                    HTML.Tag tagToInsertInto =
                            (HTML.Tag) attrs.getAttribute(StyleConstants.NameAttribute);
                    if (tagToInsertInto != null) {
                        this.inParagraph = tagToInsertInto.isParagraph();
                    }
                }
            }
        }


        private void generateEndsSpecsForMidInsert() {
            int           count = heightToElementWithName(HTML.Tag.BODY,
                                                   Math.max(0, offset - 1));
            boolean       joinNext = false;

            if (count == -1 && offset > 0) {
                count = heightToElementWithName(HTML.Tag.BODY, offset);
                if (count != -1) {
                    count = depthTo(offset - 1) - 1;
                    joinNext = true;
                }
            }
            if (count == -1) {
                throw new RuntimeException("Must insert new content into body element-");
            }
            if (count != -1) {
                try {
                    if (!joinNext && offset > 0 &&
                        !getText(offset - 1, 1).equals("\n")) {
                        SimpleAttributeSet newAttrs = new SimpleAttributeSet();
                        newAttrs.addAttribute(StyleConstants.NameAttribute,
                                              HTML.Tag.CONTENT);
                        ElementSpec spec = new ElementSpec(newAttrs,
                                    ElementSpec.ContentType, NEWLINE, 0, 1);
                        parseBuffer.addElement(spec);
                    }
                    } catch (BadLocationException ble) {}
                while (count-- > 0) {
                    parseBuffer.addElement(new ElementSpec
                                           (null, ElementSpec.EndTagType));
                }
                if (joinNext) {
                    ElementSpec spec = new ElementSpec(null, ElementSpec.
                                                       StartTagType);

                    spec.setDirection(ElementSpec.JoinNextDirection);
                    parseBuffer.addElement(spec);
                }
            }
            }


        private int depthTo(int offset) {
            Element       e = getDefaultRootElement();
            int           count = 0;

            while (!e.isLeaf()) {
                count++;
                e = e.getElement(e.getElementIndex(offset));
            }
            return count;
        }


        private int heightToElementWithName(Object name, int offset) {
            Element       e = getCharacterElement(offset).getParentElement();
            int           count = 0;

            while (e != null && e.getAttributes().getAttribute
                   (StyleConstants.NameAttribute) != name) {
                count++;
                e = e.getParentElement();
            }
            return (e == null) ? -1 : count;
        }


        private void adjustEndElement() {
            int length = getLength();
            if (length == 0) {
                return;
            }
            obtainLock();
            try {
                Element[] pPath = getPathTo(length - 1);
                int pLength = pPath.length;
                if (pLength > 1 && pPath[1].getAttributes().getAttribute
                         (StyleConstants.NameAttribute) == HTML.Tag.BODY &&
                         pPath[1].getEndOffset() == length) {
                    String lastText = getText(length - 1, 1);
                    DefaultDocumentEvent event;
                    Element[] added;
                    Element[] removed;
                    int index;
                    added = new Element[0];
                    removed = new Element[1];
                    index = pPath[0].getElementIndex(length);
                    removed[0] = pPath[0].getElement(index);
                    ((BranchElement)pPath[0]).replace(index, 1, added);
                    ElementEdit firstEdit = new ElementEdit(pPath[0], index,
                                                            removed, added);

                    SimpleAttributeSet sas = new SimpleAttributeSet();
                    sas.addAttribute(StyleConstants.NameAttribute,
                                         HTML.Tag.CONTENT);
                    sas.addAttribute(IMPLIED_CR, Boolean.TRUE);
                    added = new Element[1];
                    added[0] = createLeafElement(pPath[pLength - 1],
                                                     sas, length, length + 1);
                    index = pPath[pLength - 1].getElementCount();
                    ((BranchElement)pPath[pLength - 1]).replace(index, 0,
                                                                added);
                    event = new DefaultDocumentEvent(length, 1,
                                            DocumentEvent.EventType.CHANGE);
                    event.addEdit(new ElementEdit(pPath[pLength - 1],
                                         index, new Element[0], added));
                    event.addEdit(firstEdit);
                    event.end();
                    fireChangedUpdate(event);
                    fireUndoableEditUpdate(new UndoableEditEvent(this, event));

                    if (lastText.equals("\n")) {
                        event = new DefaultDocumentEvent(length - 1, 1,
                                           DocumentEvent.EventType.REMOVE);
                        removeUpdate(event);
                        UndoableEdit u = getContent().remove(length - 1, 1);
                        if (u != null) {
                            event.addEdit(u);
                        }
                        postRemoveUpdate(event);
                        event.end();
                        fireRemoveUpdate(event);
                        fireUndoableEditUpdate(new UndoableEditEvent(
                                               this, event));
                    }
                }
            }
            catch (BadLocationException ble) {
            }
            finally {
                releaseLock();
            }
        }

        private Element[] getPathTo(int offset) {
            Stack<Element> elements = new Stack<Element>();
            Element e = getDefaultRootElement();
            int index;
            while (!e.isLeaf()) {
                elements.push(e);
                e = e.getElement(e.getElementIndex(offset));
            }
            Element[] retValue = new Element[elements.size()];
            elements.copyInto(retValue);
            return retValue;
        }

        public void flush() throws BadLocationException {
            if (emptyDocument && !insertAfterImplied) {
                if (HTMLDocument.this.getLength() > 0 ||
                                      parseBuffer.size() > 0) {
                    flushBuffer(true);
                    adjustEndElement();
                }
                }
            else {
                flushBuffer(true);
            }
        }


        public void handleText(char[] data, int pos) {
            if (receivedEndHTML || (midInsert && !inBody)) {
                return;
            }

            if(HTMLDocument.this.getProperty(I18NProperty).equals( Boolean.FALSE ) ) {
                Object d = getProperty(TextAttribute.RUN_DIRECTION);
                if ((d != null) && (d.equals(TextAttribute.RUN_DIRECTION_RTL))) {
                    HTMLDocument.this.putProperty( I18NProperty, Boolean.TRUE);
                } else {
                    if (SwingUtilities2.isComplexLayout(data, 0, data.length)) {
                        HTMLDocument.this.putProperty( I18NProperty, Boolean.TRUE);
                    }
                }
            }

            if (inTextArea) {
                textAreaContent(data);
            } else if (inPre) {
                preContent(data);
            } else if (inTitle) {
                putProperty(Document.TitleProperty, new String(data));
            } else if (option != null) {
                option.setLabel(new String(data));
            } else if (inStyle) {
                if (styles != null) {
                    styles.addElement(new String(data));
                }
            } else if (inBlock > 0) {
                if (!foundInsertTag && insertAfterImplied) {
                    foundInsertTag(false);
                    foundInsertTag = true;
                    inParagraph = impliedP = !insertInBody;
                }
                if (data.length >= 1) {
                    addContent(data, 0, data.length);
                }
            }
        }


        public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            if (receivedEndHTML) {
                return;
            }
            if (midInsert && !inBody) {
                if (t == HTML.Tag.BODY) {
                    inBody = true;
                    inBlock++;
                }
                return;
            }
            if (!inBody && t == HTML.Tag.BODY) {
                inBody = true;
            }
            if (isStyleCSS && a.isDefined(HTML.Attribute.STYLE)) {
                String decl = (String)a.getAttribute(HTML.Attribute.STYLE);
                a.removeAttribute(HTML.Attribute.STYLE);
                styleAttributes = getStyleSheet().getDeclaration(decl);
                a.addAttributes(styleAttributes);
            }
            else {
                styleAttributes = null;
            }
            TagAction action = tagMap.get(t);

            if (action != null) {
                action.start(t, a);
            }
        }

        public void handleComment(char[] data, int pos) {
            if (receivedEndHTML) {
                addExternalComment(new String(data));
                return;
            }
            if (inStyle) {
                if (styles != null) {
                    styles.addElement(new String(data));
                }
            }
            else if (getPreservesUnknownTags()) {
                if (inBlock == 0 && (foundInsertTag ||
                                     insertTag != HTML.Tag.COMMENT)) {
                    addExternalComment(new String(data));
                    return;
                }
                SimpleAttributeSet sas = new SimpleAttributeSet();
                sas.addAttribute(HTML.Attribute.COMMENT, new String(data));
                addSpecialElement(HTML.Tag.COMMENT, sas);
            }

            TagAction action = tagMap.get(HTML.Tag.COMMENT);
            if (action != null) {
                action.start(HTML.Tag.COMMENT, new SimpleAttributeSet());
                action.end(HTML.Tag.COMMENT);
            }
        }


        private void addExternalComment(String comment) {
            Object comments = getProperty(AdditionalComments);
            if (comments != null && !(comments instanceof Vector)) {
                return;
            }
            if (comments == null) {
                comments = new Vector();
                putProperty(AdditionalComments, comments);
            }
            ((Vector)comments).addElement(comment);
        }


        public void handleEndTag(HTML.Tag t, int pos) {
            if (receivedEndHTML || (midInsert && !inBody)) {
                return;
            }
            if (t == HTML.Tag.HTML) {
                receivedEndHTML = true;
            }
            if (t == HTML.Tag.BODY) {
                inBody = false;
                if (midInsert) {
                    inBlock--;
                }
            }
            TagAction action = tagMap.get(t);
            if (action != null) {
                action.end(t);
            }
        }


        public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            if (receivedEndHTML || (midInsert && !inBody)) {
                return;
            }

            if (isStyleCSS && a.isDefined(HTML.Attribute.STYLE)) {
                String decl = (String)a.getAttribute(HTML.Attribute.STYLE);
                a.removeAttribute(HTML.Attribute.STYLE);
                styleAttributes = getStyleSheet().getDeclaration(decl);
                a.addAttributes(styleAttributes);
            }
            else {
                styleAttributes = null;
            }

            TagAction action = tagMap.get(t);
            if (action != null) {
                action.start(t, a);
                action.end(t);
            }
            else if (getPreservesUnknownTags()) {
                addSpecialElement(t, a);
            }
        }


        public void handleEndOfLineString(String eol) {
            if (emptyDocument && eol != null) {
                putProperty(DefaultEditorKit.EndOfLineStringProperty,
                            eol);
            }
        }

        protected void registerTag(HTML.Tag t, TagAction a) {
            tagMap.put(t, a);
        }


        public class TagAction {


            public void start(HTML.Tag t, MutableAttributeSet a) {
            }


            public void end(HTML.Tag t) {
            }

        }

        public class BlockAction extends TagAction {

            public void start(HTML.Tag t, MutableAttributeSet attr) {
                blockOpen(t, attr);
            }

            public void end(HTML.Tag t) {
                blockClose(t);
            }
        }



        private class FormTagAction extends BlockAction {
            public void start(HTML.Tag t, MutableAttributeSet attr) {
                super.start(t, attr);
                radioButtonGroupsMap = new HashMap<String, ButtonGroup>();
            }

            public void end(HTML.Tag t) {
                super.end(t);
                radioButtonGroupsMap = null;
            }
        }


        public class ParagraphAction extends BlockAction {

            public void start(HTML.Tag t, MutableAttributeSet a) {
                super.start(t, a);
                inParagraph = true;
            }

            public void end(HTML.Tag t) {
                super.end(t);
                inParagraph = false;
            }
        }

        public class SpecialAction extends TagAction {

            public void start(HTML.Tag t, MutableAttributeSet a) {
                addSpecialElement(t, a);
            }

        }

        public class IsindexAction extends TagAction {

            public void start(HTML.Tag t, MutableAttributeSet a) {
                blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
                addSpecialElement(t, a);
                blockClose(HTML.Tag.IMPLIED);
            }

        }


        public class HiddenAction extends TagAction {

            public void start(HTML.Tag t, MutableAttributeSet a) {
                addSpecialElement(t, a);
            }

            public void end(HTML.Tag t) {
                if (!isEmpty(t)) {
                    MutableAttributeSet a = new SimpleAttributeSet();
                    a.addAttribute(HTML.Attribute.ENDTAG, "true");
                    addSpecialElement(t, a);
                }
            }

            boolean isEmpty(HTML.Tag t) {
                if (t == HTML.Tag.APPLET ||
                    t == HTML.Tag.SCRIPT) {
                    return false;
                }
                return true;
            }
        }



        class MetaAction extends HiddenAction {

            public void start(HTML.Tag t, MutableAttributeSet a) {
                Object equiv = a.getAttribute(HTML.Attribute.HTTPEQUIV);
                if (equiv != null) {
                    equiv = ((String)equiv).toLowerCase();
                    if (equiv.equals("content-style-type")) {
                        String value = (String)a.getAttribute
                                       (HTML.Attribute.CONTENT);
                        setDefaultStyleSheetType(value);
                        isStyleCSS = "text/css".equals
                                      (getDefaultStyleSheetType());
                    }
                    else if (equiv.equals("default-style")) {
                        defaultStyle = (String)a.getAttribute
                                       (HTML.Attribute.CONTENT);
                    }
                }
                super.start(t, a);
            }

            boolean isEmpty(HTML.Tag t) {
                return true;
            }
        }



        class HeadAction extends BlockAction {

            public void start(HTML.Tag t, MutableAttributeSet a) {
                inHead = true;
                if ((insertTag == null && !insertAfterImplied) ||
                    (insertTag == HTML.Tag.HEAD) ||
                    (insertAfterImplied &&
                     (foundInsertTag || !a.isDefined(IMPLIED)))) {
                    super.start(t, a);
                }
            }

            public void end(HTML.Tag t) {
                inHead = inStyle = false;
                if (styles != null) {
                    boolean isDefaultCSS = isStyleCSS;
                    for (int counter = 0, maxCounter = styles.size();
                         counter < maxCounter;) {
                        Object value = styles.elementAt(counter);
                        if (value == HTML.Tag.LINK) {
                            handleLink((AttributeSet)styles.
                                       elementAt(++counter));
                            counter++;
                        }
                        else {
                            String type = (String)styles.elementAt(++counter);
                            boolean isCSS = (type == null) ? isDefaultCSS :
                                            type.equals("text/css");
                            while (++counter < maxCounter &&
                                   (styles.elementAt(counter)
                                    instanceof String)) {
                                if (isCSS) {
                                    addCSSRules((String)styles.elementAt
                                                (counter));
                                }
                            }
                        }
                    }
                }
                if ((insertTag == null && !insertAfterImplied) ||
                    insertTag == HTML.Tag.HEAD ||
                    (insertAfterImplied && foundInsertTag)) {
                    super.end(t);
                }
            }

            boolean isEmpty(HTML.Tag t) {
                return false;
            }

            private void handleLink(AttributeSet attr) {
                String type = (String)attr.getAttribute(HTML.Attribute.TYPE);
                if (type == null) {
                    type = getDefaultStyleSheetType();
                }
                if (type.equals("text/css")) {
                    String rel = (String)attr.getAttribute(HTML.Attribute.REL);
                    String title = (String)attr.getAttribute
                                               (HTML.Attribute.TITLE);
                    String media = (String)attr.getAttribute
                                                   (HTML.Attribute.MEDIA);
                    if (media == null) {
                        media = "all";
                    }
                    else {
                        media = media.toLowerCase();
                    }
                    if (rel != null) {
                        rel = rel.toLowerCase();
                        if ((media.indexOf("all") != -1 ||
                             media.indexOf("screen") != -1) &&
                            (rel.equals("stylesheet") ||
                             (rel.equals("alternate stylesheet") &&
                              title.equals(defaultStyle)))) {
                            linkCSSStyleSheet((String)attr.getAttribute
                                              (HTML.Attribute.HREF));
                        }
                    }
                }
            }
        }



        class LinkAction extends HiddenAction {

            public void start(HTML.Tag t, MutableAttributeSet a) {
                String rel = (String)a.getAttribute(HTML.Attribute.REL);
                if (rel != null) {
                    rel = rel.toLowerCase();
                    if (rel.equals("stylesheet") ||
                        rel.equals("alternate stylesheet")) {
                        if (styles == null) {
                            styles = new Vector<Object>(3);
                        }
                        styles.addElement(t);
                        styles.addElement(a.copyAttributes());
                    }
                }
                super.start(t, a);
            }
        }

        class MapAction extends TagAction {

            public void start(HTML.Tag t, MutableAttributeSet a) {
                lastMap = new Map((String)a.getAttribute(HTML.Attribute.NAME));
                addMap(lastMap);
            }

            public void end(HTML.Tag t) {
            }
        }


        class AreaAction extends TagAction {

            public void start(HTML.Tag t, MutableAttributeSet a) {
                if (lastMap != null) {
                    lastMap.addArea(a.copyAttributes());
                }
            }

            public void end(HTML.Tag t) {
            }
        }


        class StyleAction extends TagAction {

            public void start(HTML.Tag t, MutableAttributeSet a) {
                if (inHead) {
                    if (styles == null) {
                        styles = new Vector<Object>(3);
                    }
                    styles.addElement(t);
                    styles.addElement(a.getAttribute(HTML.Attribute.TYPE));
                    inStyle = true;
                }
            }

            public void end(HTML.Tag t) {
                inStyle = false;
            }

            boolean isEmpty(HTML.Tag t) {
                return false;
            }
        }


        public class PreAction extends BlockAction {

            public void start(HTML.Tag t, MutableAttributeSet attr) {
                inPre = true;
                blockOpen(t, attr);
                attr.addAttribute(CSS.Attribute.WHITE_SPACE, "pre");
                blockOpen(HTML.Tag.IMPLIED, attr);
            }

            public void end(HTML.Tag t) {
                blockClose(HTML.Tag.IMPLIED);
                inPre = false;
                blockClose(t);
            }
        }

        public class CharacterAction extends TagAction {

            public void start(HTML.Tag t, MutableAttributeSet attr) {
                pushCharacterStyle();
                if (!foundInsertTag) {
                    boolean insert = canInsertTag(t, attr, false);
                    if (foundInsertTag) {
                        if (!inParagraph) {
                            inParagraph = impliedP = true;
                        }
                    }
                    if (!insert) {
                        return;
                    }
                }
                if (attr.isDefined(IMPLIED)) {
                    attr.removeAttribute(IMPLIED);
                }
                charAttr.addAttribute(t, attr.copyAttributes());
                if (styleAttributes != null) {
                    charAttr.addAttributes(styleAttributes);
                }
            }

            public void end(HTML.Tag t) {
                popCharacterStyle();
            }
        }


        class ConvertAction extends TagAction {

            public void start(HTML.Tag t, MutableAttributeSet attr) {
                pushCharacterStyle();
                if (!foundInsertTag) {
                    boolean insert = canInsertTag(t, attr, false);
                    if (foundInsertTag) {
                        if (!inParagraph) {
                            inParagraph = impliedP = true;
                        }
                    }
                    if (!insert) {
                        return;
                    }
                }
                if (attr.isDefined(IMPLIED)) {
                    attr.removeAttribute(IMPLIED);
                }
                if (styleAttributes != null) {
                    charAttr.addAttributes(styleAttributes);
                }
                charAttr.addAttribute(t, attr.copyAttributes());
                StyleSheet sheet = getStyleSheet();
                if (t == HTML.Tag.B) {
                    sheet.addCSSAttribute(charAttr, CSS.Attribute.FONT_WEIGHT, "bold");
                } else if (t == HTML.Tag.I) {
                    sheet.addCSSAttribute(charAttr, CSS.Attribute.FONT_STYLE, "italic");
                } else if (t == HTML.Tag.U) {
                    Object v = charAttr.getAttribute(CSS.Attribute.TEXT_DECORATION);
                    String value = "underline";
                    value = (v != null) ? value + "," + v.toString() : value;
                    sheet.addCSSAttribute(charAttr, CSS.Attribute.TEXT_DECORATION, value);
                } else if (t == HTML.Tag.STRIKE) {
                    Object v = charAttr.getAttribute(CSS.Attribute.TEXT_DECORATION);
                    String value = "line-through";
                    value = (v != null) ? value + "," + v.toString() : value;
                    sheet.addCSSAttribute(charAttr, CSS.Attribute.TEXT_DECORATION, value);
                } else if (t == HTML.Tag.SUP) {
                    Object v = charAttr.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
                    String value = "sup";
                    value = (v != null) ? value + "," + v.toString() : value;
                    sheet.addCSSAttribute(charAttr, CSS.Attribute.VERTICAL_ALIGN, value);
                } else if (t == HTML.Tag.SUB) {
                    Object v = charAttr.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
                    String value = "sub";
                    value = (v != null) ? value + "," + v.toString() : value;
                    sheet.addCSSAttribute(charAttr, CSS.Attribute.VERTICAL_ALIGN, value);
                } else if (t == HTML.Tag.FONT) {
                    String color = (String) attr.getAttribute(HTML.Attribute.COLOR);
                    if (color != null) {
                        sheet.addCSSAttribute(charAttr, CSS.Attribute.COLOR, color);
                    }
                    String face = (String) attr.getAttribute(HTML.Attribute.FACE);
                    if (face != null) {
                        sheet.addCSSAttribute(charAttr, CSS.Attribute.FONT_FAMILY, face);
                    }
                    String size = (String) attr.getAttribute(HTML.Attribute.SIZE);
                    if (size != null) {
                        sheet.addCSSAttributeFromHTML(charAttr, CSS.Attribute.FONT_SIZE, size);
                    }
                }
            }

            public void end(HTML.Tag t) {
                popCharacterStyle();
            }

        }

        class AnchorAction extends CharacterAction {

            public void start(HTML.Tag t, MutableAttributeSet attr) {
                emptyAnchor = true;
                super.start(t, attr);
            }

            public void end(HTML.Tag t) {
                if (emptyAnchor) {
                    char[] one = new char[1];
                    one[0] = '\n';
                    addContent(one, 0, 1);
                }
                super.end(t);
            }
        }

        class TitleAction extends HiddenAction {

            public void start(HTML.Tag t, MutableAttributeSet attr) {
                inTitle = true;
                super.start(t, attr);
            }

            public void end(HTML.Tag t) {
                inTitle = false;
                super.end(t);
            }

            boolean isEmpty(HTML.Tag t) {
                return false;
            }
        }


        class BaseAction extends TagAction {

            public void start(HTML.Tag t, MutableAttributeSet attr) {
                String href = (String) attr.getAttribute(HTML.Attribute.HREF);
                if (href != null) {
                    try {
                        URL newBase = new URL(base, href);
                        setBase(newBase);
                        hasBaseTag = true;
                    } catch (MalformedURLException ex) {
                    }
                }
                baseTarget = (String) attr.getAttribute(HTML.Attribute.TARGET);
            }
        }

        class ObjectAction extends SpecialAction {

            public void start(HTML.Tag t, MutableAttributeSet a) {
                if (t == HTML.Tag.PARAM) {
                    addParameter(a);
                } else {
                    super.start(t, a);
                }
            }

            public void end(HTML.Tag t) {
                if (t != HTML.Tag.PARAM) {
                    super.end(t);
                }
            }

            void addParameter(AttributeSet a) {
                String name = (String) a.getAttribute(HTML.Attribute.NAME);
                String value = (String) a.getAttribute(HTML.Attribute.VALUE);
                if ((name != null) && (value != null)) {
                    ElementSpec objSpec = parseBuffer.lastElement();
                    MutableAttributeSet objAttr = (MutableAttributeSet) objSpec.getAttributes();
                    objAttr.addAttribute(name, value);
                }
            }
        }


        public class FormAction extends SpecialAction {

            public void start(HTML.Tag t, MutableAttributeSet attr) {
                if (t == HTML.Tag.INPUT) {
                    String type = (String)
                        attr.getAttribute(HTML.Attribute.TYPE);

                    if (type == null) {
                        type = "text";
                        attr.addAttribute(HTML.Attribute.TYPE, "text");
                    }
                    setModel(type, attr);
                } else if (t == HTML.Tag.TEXTAREA) {
                    inTextArea = true;
                    textAreaDocument = new TextAreaDocument();
                    attr.addAttribute(StyleConstants.ModelAttribute,
                                      textAreaDocument);
                } else if (t == HTML.Tag.SELECT) {
                    int size = HTML.getIntegerAttributeValue(attr,
                                                             HTML.Attribute.SIZE,
                                                             1);
                    boolean multiple = attr.getAttribute(HTML.Attribute.MULTIPLE) != null;
                    if ((size > 1) || multiple) {
                        OptionListModel<Option> m = new OptionListModel<Option>();
                        if (multiple) {
                            m.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                        }
                        selectModel = m;
                    } else {
                        selectModel = new OptionComboBoxModel<Option>();
                    }
                    attr.addAttribute(StyleConstants.ModelAttribute,
                                      selectModel);

                }

                if (t == HTML.Tag.OPTION) {
                    option = new Option(attr);

                    if (selectModel instanceof OptionListModel) {
                        OptionListModel<Option> m = (OptionListModel<Option>) selectModel;
                        m.addElement(option);
                        if (option.isSelected()) {
                            m.addSelectionInterval(optionCount, optionCount);
                            m.setInitialSelection(optionCount);
                        }
                    } else if (selectModel instanceof OptionComboBoxModel) {
                        OptionComboBoxModel<Option> m = (OptionComboBoxModel<Option>) selectModel;
                        m.addElement(option);
                        if (option.isSelected()) {
                            m.setSelectedItem(option);
                            m.setInitialSelection(option);
                        }
                    }
                    optionCount++;
                } else {
                    super.start(t, attr);
                }
            }

            public void end(HTML.Tag t) {
                if (t == HTML.Tag.OPTION) {
                    option = null;
                } else {
                    if (t == HTML.Tag.SELECT) {
                        selectModel = null;
                        optionCount = 0;
                    } else if (t == HTML.Tag.TEXTAREA) {
                        inTextArea = false;


                        textAreaDocument.storeInitialText();
                    }
                    super.end(t);
                }
            }

            void setModel(String type, MutableAttributeSet attr) {
                if (type.equals("submit") ||
                    type.equals("reset") ||
                    type.equals("image")) {

                    attr.addAttribute(StyleConstants.ModelAttribute,
                                      new DefaultButtonModel());
                } else if (type.equals("text") ||
                           type.equals("password")) {
                    int maxLength = HTML.getIntegerAttributeValue(
                                       attr, HTML.Attribute.MAXLENGTH, -1);
                    Document doc;

                    if (maxLength > 0) {
                        doc = new FixedLengthDocument(maxLength);
                    }
                    else {
                        doc = new PlainDocument();
                    }
                    String value = (String)
                        attr.getAttribute(HTML.Attribute.VALUE);
                    try {
                        doc.insertString(0, value, null);
                    } catch (BadLocationException e) {
                    }
                    attr.addAttribute(StyleConstants.ModelAttribute, doc);
                } else if (type.equals("file")) {
                    attr.addAttribute(StyleConstants.ModelAttribute,
                                      new PlainDocument());
                } else if (type.equals("checkbox") ||
                           type.equals("radio")) {
                    JToggleButton.ToggleButtonModel model = new JToggleButton.ToggleButtonModel();
                    if (type.equals("radio")) {
                        String name = (String) attr.getAttribute(HTML.Attribute.NAME);
                        if ( radioButtonGroupsMap == null ) { radioButtonGroupsMap = new HashMap<String, ButtonGroup>();
                        }
                        ButtonGroup radioButtonGroup = radioButtonGroupsMap.get(name);
                        if (radioButtonGroup == null) {
                            radioButtonGroup = new ButtonGroup();
                            radioButtonGroupsMap.put(name,radioButtonGroup);
                        }
                        model.setGroup(radioButtonGroup);
                    }
                    boolean checked = (attr.getAttribute(HTML.Attribute.CHECKED) != null);
                    model.setSelected(checked);
                    attr.addAttribute(StyleConstants.ModelAttribute, model);
                }
            }


            Object selectModel;
            int optionCount;
        }


        protected void pushCharacterStyle() {
            charAttrStack.push(charAttr.copyAttributes());
        }


        protected void popCharacterStyle() {
            if (!charAttrStack.empty()) {
                charAttr = (MutableAttributeSet) charAttrStack.peek();
                charAttrStack.pop();
            }
        }


        protected void textAreaContent(char[] data) {
            try {
                textAreaDocument.insertString(textAreaDocument.getLength(), new String(data), null);
            } catch (BadLocationException e) {
                }
        }


        protected void preContent(char[] data) {
            int last = 0;
            for (int i = 0; i < data.length; i++) {
                if (data[i] == '\n') {
                    addContent(data, last, i - last + 1);
                    blockClose(HTML.Tag.IMPLIED);
                    MutableAttributeSet a = new SimpleAttributeSet();
                    a.addAttribute(CSS.Attribute.WHITE_SPACE, "pre");
                    blockOpen(HTML.Tag.IMPLIED, a);
                    last = i + 1;
                }
            }
            if (last < data.length) {
                addContent(data, last, data.length - last);
            }
        }


        protected void blockOpen(HTML.Tag t, MutableAttributeSet attr) {
            if (impliedP) {
                blockClose(HTML.Tag.IMPLIED);
            }

            inBlock++;

            if (!canInsertTag(t, attr, true)) {
                return;
            }
            if (attr.isDefined(IMPLIED)) {
                attr.removeAttribute(IMPLIED);
            }
            lastWasNewline = false;
            attr.addAttribute(StyleConstants.NameAttribute, t);
            ElementSpec es = new ElementSpec(
                attr.copyAttributes(), ElementSpec.StartTagType);
            parseBuffer.addElement(es);
        }


        protected void blockClose(HTML.Tag t) {
            inBlock--;

            if (!foundInsertTag) {
                return;
            }

            if(!lastWasNewline) {
                pushCharacterStyle();
                charAttr.addAttribute(IMPLIED_CR, Boolean.TRUE);
                addContent(NEWLINE, 0, 1, true);
                popCharacterStyle();
                lastWasNewline = true;
            }

            if (impliedP) {
                impliedP = false;
                inParagraph = false;
                if (t != HTML.Tag.IMPLIED) {
                    blockClose(HTML.Tag.IMPLIED);
                }
            }
            ElementSpec prev = (parseBuffer.size() > 0) ?
                parseBuffer.lastElement() : null;
            if (prev != null && prev.getType() == ElementSpec.StartTagType) {
                char[] one = new char[1];
                one[0] = ' ';
                addContent(one, 0, 1);
            }
            ElementSpec es = new ElementSpec(
                null, ElementSpec.EndTagType);
            parseBuffer.addElement(es);
        }


        protected void addContent(char[] data, int offs, int length) {
            addContent(data, offs, length, true);
        }


        protected void addContent(char[] data, int offs, int length,
                                  boolean generateImpliedPIfNecessary) {
            if (!foundInsertTag) {
                return;
            }

            if (generateImpliedPIfNecessary && (! inParagraph) && (! inPre)) {
                blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
                inParagraph = true;
                impliedP = true;
            }
            emptyAnchor = false;
            charAttr.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
            AttributeSet a = charAttr.copyAttributes();
            ElementSpec es = new ElementSpec(
                a, ElementSpec.ContentType, data, offs, length);
            parseBuffer.addElement(es);

            if (parseBuffer.size() > threshold) {
                if ( threshold <= MaxThreshold ) {
                    threshold *= StepThreshold;
                }
                try {
                    flushBuffer(false);
                } catch (BadLocationException ble) {
                }
            }
            if(length > 0) {
                lastWasNewline = (data[offs + length - 1] == '\n');
            }
        }


        protected void addSpecialElement(HTML.Tag t, MutableAttributeSet a) {
            if ((t != HTML.Tag.FRAME) && (! inParagraph) && (! inPre)) {
                nextTagAfterPImplied = t;
                blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
                nextTagAfterPImplied = null;
                inParagraph = true;
                impliedP = true;
            }
            if (!canInsertTag(t, a, t.isBlock())) {
                return;
            }
            if (a.isDefined(IMPLIED)) {
                a.removeAttribute(IMPLIED);
            }
            emptyAnchor = false;
            a.addAttributes(charAttr);
            a.addAttribute(StyleConstants.NameAttribute, t);
            char[] one = new char[1];
            one[0] = ' ';
            ElementSpec es = new ElementSpec(
                a.copyAttributes(), ElementSpec.ContentType, one, 0, 1);
            parseBuffer.addElement(es);
            if (t == HTML.Tag.FRAME) {
                lastWasNewline = true;
            }
        }


        void flushBuffer(boolean endOfStream) throws BadLocationException {
            int oldLength = HTMLDocument.this.getLength();
            int size = parseBuffer.size();
            if (endOfStream && (insertTag != null || insertAfterImplied) &&
                size > 0) {
                adjustEndSpecsForPartialInsert();
                size = parseBuffer.size();
            }
            ElementSpec[] spec = new ElementSpec[size];
            parseBuffer.copyInto(spec);

            if (oldLength == 0 && (insertTag == null && !insertAfterImplied)) {
                create(spec);
            } else {
                insert(offset, spec);
            }
            parseBuffer.removeAllElements();
            offset += HTMLDocument.this.getLength() - oldLength;
            flushCount++;
        }


        private void adjustEndSpecsForPartialInsert() {
            int size = parseBuffer.size();
            if (insertTagDepthDelta < 0) {
                int removeCounter = insertTagDepthDelta;
                while (removeCounter < 0 && size >= 0 &&
                        parseBuffer.elementAt(size - 1).
                       getType() == ElementSpec.EndTagType) {
                    parseBuffer.removeElementAt(--size);
                    removeCounter++;
                }
            }
            if (flushCount == 0 && (!insertAfterImplied ||
                                    !wantsTrailingNewline)) {
                int index = 0;
                if (pushDepth > 0) {
                    if (parseBuffer.elementAt(0).getType() ==
                        ElementSpec.ContentType) {
                        index++;
                    }
                }
                index += (popDepth + pushDepth);
                int cCount = 0;
                int cStart = index;
                while (index < size && parseBuffer.elementAt
                        (index).getType() == ElementSpec.ContentType) {
                    index++;
                    cCount++;
                }
                if (cCount > 1) {
                    while (index < size && parseBuffer.elementAt
                            (index).getType() == ElementSpec.EndTagType) {
                        index++;
                    }
                    if (index == size) {
                        char[] lastText = parseBuffer.elementAt
                                (cStart + cCount - 1).getArray();
                        if (lastText.length == 1 && lastText[0] == NEWLINE[0]){
                            index = cStart + cCount - 1;
                            while (size > index) {
                                parseBuffer.removeElementAt(--size);
                            }
                        }
                    }
                }
            }
            if (wantsTrailingNewline) {
                for (int counter = parseBuffer.size() - 1; counter >= 0;
                                   counter--) {
                    ElementSpec spec = parseBuffer.elementAt(counter);
                    if (spec.getType() == ElementSpec.ContentType) {
                        if (spec.getArray()[spec.getLength() - 1] != '\n') {
                            SimpleAttributeSet attrs =new SimpleAttributeSet();

                            attrs.addAttribute(StyleConstants.NameAttribute,
                                               HTML.Tag.CONTENT);
                            parseBuffer.insertElementAt(new ElementSpec(
                                    attrs,
                                    ElementSpec.ContentType, NEWLINE, 0, 1),
                                    counter + 1);
                        }
                        break;
                    }
                }
            }
        }


        void addCSSRules(String rules) {
            StyleSheet ss = getStyleSheet();
            ss.addRule(rules);
        }


        void linkCSSStyleSheet(String href) {
            URL url;
            try {
                url = new URL(base, href);
            } catch (MalformedURLException mfe) {
                try {
                    url = new URL(href);
                } catch (MalformedURLException mfe2) {
                    url = null;
                }
            }
            if (url != null) {
                getStyleSheet().importStyleSheet(url);
            }
        }


        private boolean canInsertTag(HTML.Tag t, AttributeSet attr,
                                     boolean isBlockTag) {
            if (!foundInsertTag) {
                boolean needPImplied = ((t == HTML.Tag.IMPLIED)
                                                          && (!inParagraph)
                                                          && (!inPre));
                if (needPImplied && (nextTagAfterPImplied != null)) {


                    if (insertTag != null) {
                        boolean nextTagIsInsertTag =
                                isInsertTag(nextTagAfterPImplied);
                        if ( (! nextTagIsInsertTag) || (! insertInsertTag) ) {
                            return false;
                        }
                    }

                 } else if ((insertTag != null && !isInsertTag(t))
                               || (insertAfterImplied
                                    && (attr == null
                                        || attr.isDefined(IMPLIED)
                                        || t == HTML.Tag.IMPLIED
                                       )
                                   )
                           ) {
                    return false;
                }

                foundInsertTag(isBlockTag);
                if (!insertInsertTag) {
                    return false;
                }
            }
            return true;
        }

        private boolean isInsertTag(HTML.Tag tag) {
            return (insertTag == tag);
        }

        private void foundInsertTag(boolean isBlockTag) {
            foundInsertTag = true;
            if (!insertAfterImplied && (popDepth > 0 || pushDepth > 0)) {
                try {
                    if (offset == 0 || !getText(offset - 1, 1).equals("\n")) {
                        AttributeSet newAttrs = null;
                        boolean joinP = true;

                        if (offset != 0) {
                            Element charElement = getCharacterElement
                                                    (offset - 1);
                            AttributeSet attrs = charElement.getAttributes();

                            if (attrs.isDefined(StyleConstants.
                                                ComposedTextAttribute)) {
                                joinP = false;
                            }
                            else {
                                Object name = attrs.getAttribute
                                              (StyleConstants.NameAttribute);
                                if (name instanceof HTML.Tag) {
                                    HTML.Tag tag = (HTML.Tag)name;
                                    if (tag == HTML.Tag.IMG ||
                                        tag == HTML.Tag.HR ||
                                        tag == HTML.Tag.COMMENT ||
                                        (tag instanceof HTML.UnknownTag)) {
                                        joinP = false;
                                    }
                                }
                            }
                        }
                        if (!joinP) {
                            newAttrs = new SimpleAttributeSet();
                            ((SimpleAttributeSet)newAttrs).addAttribute
                                              (StyleConstants.NameAttribute,
                                               HTML.Tag.CONTENT);
                        }
                        ElementSpec es = new ElementSpec(newAttrs,
                                     ElementSpec.ContentType, NEWLINE, 0,
                                     NEWLINE.length);
                        if (joinP) {
                            es.setDirection(ElementSpec.
                                            JoinPreviousDirection);
                        }
                        parseBuffer.addElement(es);
                    }
                } catch (BadLocationException ble) {}
            }
            for (int counter = 0; counter < popDepth; counter++) {
                parseBuffer.addElement(new ElementSpec(null, ElementSpec.
                                                       EndTagType));
            }
            for (int counter = 0; counter < pushDepth; counter++) {
                ElementSpec es = new ElementSpec(null, ElementSpec.
                                                 StartTagType);
                es.setDirection(ElementSpec.JoinNextDirection);
                parseBuffer.addElement(es);
            }
            insertTagDepthDelta = depthTo(Math.max(0, offset - 1)) -
                                  popDepth + pushDepth - inBlock;
            if (isBlockTag) {
                insertTagDepthDelta++;
            }
            else {
                insertTagDepthDelta--;
                inParagraph = true;
                lastWasNewline = false;
            }
        }


        private boolean receivedEndHTML;

        private int flushCount;

        private boolean insertAfterImplied;

        private boolean wantsTrailingNewline;
        int threshold;
        int offset;
        boolean inParagraph = false;
        boolean impliedP = false;
        boolean inPre = false;
        boolean inTextArea = false;
        TextAreaDocument textAreaDocument = null;
        boolean inTitle = false;
        boolean lastWasNewline = true;
        boolean emptyAnchor;

        boolean midInsert;

        boolean inBody;

        HTML.Tag insertTag;

        boolean insertInsertTag;

        boolean foundInsertTag;

        int insertTagDepthDelta;

        int popDepth;

        int pushDepth;

        Map lastMap;

        boolean inStyle = false;

        String defaultStyle;

        Vector<Object> styles;

        boolean inHead = false;

        boolean isStyleCSS;

        boolean emptyDocument;

        AttributeSet styleAttributes;


        Option option;

        protected Vector<ElementSpec> parseBuffer = new Vector<ElementSpec>();
        protected MutableAttributeSet charAttr = new TaggedAttributeSet();
        Stack<AttributeSet> charAttrStack = new Stack<AttributeSet>();
        Hashtable<HTML.Tag, TagAction> tagMap;
        int inBlock = 0;


        private HTML.Tag nextTagAfterPImplied = null;
    }



    static class TaggedAttributeSet extends SimpleAttributeSet {
        TaggedAttributeSet() {
            super();
        }
    }



    public class RunElement extends LeafElement {


        public RunElement(Element parent, AttributeSet a, int offs0, int offs1) {
            super(parent, a, offs0, offs1);
        }


        public String getName() {
            Object o = getAttribute(StyleConstants.NameAttribute);
            if (o != null) {
                return o.toString();
            }
            return super.getName();
        }


        public AttributeSet getResolveParent() {
            return null;
        }
    }


    public class BlockElement extends BranchElement {


        public BlockElement(Element parent, AttributeSet a) {
            super(parent, a);
        }


        public String getName() {
            Object o = getAttribute(StyleConstants.NameAttribute);
            if (o != null) {
                return o.toString();
            }
            return super.getName();
        }


        public AttributeSet getResolveParent() {
            return null;
        }

    }



    private static class FixedLengthDocument extends PlainDocument {
        private int maxLength;

        public FixedLengthDocument(int maxLength) {
            this.maxLength = maxLength;
        }

        public void insertString(int offset, String str, AttributeSet a)
            throws BadLocationException {
            if (str != null && str.length() + getLength() <= maxLength) {
                super.insertString(offset, str, a);
            }
        }
    }
}
