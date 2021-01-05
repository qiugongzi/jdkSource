
package javax.swing.text;

import java.util.Vector;
import java.io.Serializable;
import javax.swing.undo.*;
import javax.swing.SwingUtilities;


public final class StringContent implements AbstractDocument.Content, Serializable {


    public StringContent() {
        this(10);
    }


    public StringContent(int initialLength) {
        if (initialLength < 1) {
            initialLength = 1;
        }
        data = new char[initialLength];
        data[0] = '\n';
        count = 1;
    }


    public int length() {
        return count;
    }


    public UndoableEdit insertString(int where, String str) throws BadLocationException {
        if (where >= count || where < 0) {
            throw new BadLocationException("Invalid location", count);
        }
        char[] chars = str.toCharArray();
        replace(where, 0, chars, 0, chars.length);
        if (marks != null) {
            updateMarksForInsert(where, str.length());
        }
        return new InsertUndo(where, str.length());
    }


    public UndoableEdit remove(int where, int nitems) throws BadLocationException {
        if (where + nitems >= count) {
            throw new BadLocationException("Invalid range", count);
        }
        String removedString = getString(where, nitems);
        UndoableEdit edit = new RemoveUndo(where, removedString);
        replace(where, nitems, empty, 0, 0);
        if (marks != null) {
            updateMarksForRemove(where, nitems);
        }
        return edit;

    }


    public String getString(int where, int len) throws BadLocationException {
        if (where + len > count) {
            throw new BadLocationException("Invalid range", count);
        }
        return new String(data, where, len);
    }


    public void getChars(int where, int len, Segment chars) throws BadLocationException {
        if (where + len > count) {
            throw new BadLocationException("Invalid location", count);
        }
        chars.array = data;
        chars.offset = where;
        chars.count = len;
    }


    public Position createPosition(int offset) throws BadLocationException {
        if (marks == null) {
            marks = new Vector<PosRec>();
        }
        return new StickyPosition(offset);
    }

    void replace(int offset, int length,
                 char[] replArray, int replOffset, int replLength) {
        int delta = replLength - length;
        int src = offset + length;
        int nmove = count - src;
        int dest = src + delta;
        if ((count + delta) >= data.length) {
            int newLength = Math.max(2*data.length, count + delta);
            char[] newData = new char[newLength];
            System.arraycopy(data, 0, newData, 0, offset);
            System.arraycopy(replArray, replOffset, newData, offset, replLength);
            System.arraycopy(data, src, newData, dest, nmove);
            data = newData;
        } else {
            System.arraycopy(data, src, data, dest, nmove);
            System.arraycopy(replArray, replOffset, data, offset, replLength);
        }
        count = count + delta;
    }

    void resize(int ncount) {
        char[] ndata = new char[ncount];
        System.arraycopy(data, 0, ndata, 0, Math.min(ncount, count));
        data = ndata;
    }

    synchronized void updateMarksForInsert(int offset, int length) {
        if (offset == 0) {
            offset = 1;
        }
        int n = marks.size();
        for (int i = 0; i < n; i++) {
            PosRec mark = marks.elementAt(i);
            if (mark.unused) {
                marks.removeElementAt(i);
                i -= 1;
                n -= 1;
            } else if (mark.offset >= offset) {
                mark.offset += length;
            }
        }
    }

    synchronized void updateMarksForRemove(int offset, int length) {
        int n = marks.size();
        for (int i = 0; i < n; i++) {
            PosRec mark = marks.elementAt(i);
            if (mark.unused) {
                marks.removeElementAt(i);
                i -= 1;
                n -= 1;
            } else if (mark.offset >= (offset + length)) {
                mark.offset -= length;
            } else if (mark.offset >= offset) {
                mark.offset = offset;
            }
        }
    }


    protected Vector getPositionsInRange(Vector v, int offset,
                                                      int length) {
        int n = marks.size();
        int end = offset + length;
        Vector placeIn = (v == null) ? new Vector() : v;
        for (int i = 0; i < n; i++) {
            PosRec mark = marks.elementAt(i);
            if (mark.unused) {
                marks.removeElementAt(i);
                i -= 1;
                n -= 1;
            } else if(mark.offset >= offset && mark.offset <= end)
                placeIn.addElement(new UndoPosRef(mark));
        }
        return placeIn;
    }


    protected void updateUndoPositions(Vector positions) {
        for(int counter = positions.size() - 1; counter >= 0; counter--) {
            UndoPosRef ref = (UndoPosRef)positions.elementAt(counter);
            if(ref.rec.unused) {
                positions.removeElementAt(counter);
            }
            else
                ref.resetLocation();
        }
    }

    private static final char[] empty = new char[0];
    private char[] data;
    private int count;
    transient Vector<PosRec> marks;


    final class PosRec {

        PosRec(int offset) {
            this.offset = offset;
        }

        int offset;
        boolean unused;
    }


    final class StickyPosition implements Position {

        StickyPosition(int offset) {
            rec = new PosRec(offset);
            marks.addElement(rec);
        }

        public int getOffset() {
            return rec.offset;
        }

        protected void finalize() throws Throwable {
            rec.unused = true;
        }

        public String toString() {
            return Integer.toString(getOffset());
        }

        PosRec rec;
    }


    final class UndoPosRef {
        UndoPosRef(PosRec rec) {
            this.rec = rec;
            this.undoLocation = rec.offset;
        }


        protected void resetLocation() {
            rec.offset = undoLocation;
        }


        protected int undoLocation;

        protected PosRec rec;
    }


    class InsertUndo extends AbstractUndoableEdit {
        protected InsertUndo(int offset, int length) {
            super();
            this.offset = offset;
            this.length = length;
        }

        public void undo() throws CannotUndoException {
            super.undo();
            try {
                synchronized(StringContent.this) {
                    if(marks != null)
                        posRefs = getPositionsInRange(null, offset, length);
                    string = getString(offset, length);
                    remove(offset, length);
                }
            } catch (BadLocationException bl) {
              throw new CannotUndoException();
            }
        }

        public void redo() throws CannotRedoException {
            super.redo();
            try {
                synchronized(StringContent.this) {
                    insertString(offset, string);
                    string = null;
                    if(posRefs != null) {
                        updateUndoPositions(posRefs);
                        posRefs = null;
                    }
              }
            } catch (BadLocationException bl) {
              throw new CannotRedoException();
            }
        }

        protected int offset;
        protected int length;
        protected String string;
        protected Vector posRefs;
    }



    class RemoveUndo extends AbstractUndoableEdit {
        protected RemoveUndo(int offset, String string) {
            super();
            this.offset = offset;
            this.string = string;
            this.length = string.length();
            if(marks != null)
                posRefs = getPositionsInRange(null, offset, length);
        }

        public void undo() throws CannotUndoException {
            super.undo();
            try {
                synchronized(StringContent.this) {
                    insertString(offset, string);
                    if(posRefs != null) {
                        updateUndoPositions(posRefs);
                        posRefs = null;
                    }
                    string = null;
                }
            } catch (BadLocationException bl) {
              throw new CannotUndoException();
            }
        }

        public void redo() throws CannotRedoException {
            super.redo();
            try {
                synchronized(StringContent.this) {
                    string = getString(offset, length);
                    if(marks != null)
                        posRefs = getPositionsInRange(null, offset, length);
                    remove(offset, length);
                }
            } catch (BadLocationException bl) {
              throw new CannotRedoException();
            }
        }

        protected int offset;
        protected int length;
        protected String string;
        protected Vector posRefs;
    }
}
