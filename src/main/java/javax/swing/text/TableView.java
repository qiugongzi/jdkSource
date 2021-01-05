
package javax.swing.text;

import java.awt.*;
import java.util.BitSet;
import java.util.Vector;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;

import javax.swing.text.html.HTML;


public abstract class TableView extends BoxView {


    public TableView(Element elem) {
        super(elem, View.Y_AXIS);
        rows = new Vector<TableRow>();
        gridValid = false;
    }


    protected TableRow createTableRow(Element elem) {
        return new TableRow(elem);
    }


    @Deprecated
    protected TableCell createTableCell(Element elem) {
        return new TableCell(elem);
    }


    int getColumnCount() {
        return columnSpans.length;
    }


    int getColumnSpan(int col) {
        return columnSpans[col];
    }


    int getRowCount() {
        return rows.size();
    }


    int getRowSpan(int row) {
        View rv = getRow(row);
        if (rv != null) {
            return (int) rv.getPreferredSpan(Y_AXIS);
        }
        return 0;
    }

    TableRow getRow(int row) {
        if (row < rows.size()) {
            return rows.elementAt(row);
        }
        return null;
    }


     int getColumnsOccupied(View v) {
        AttributeSet a = v.getElement().getAttributes();
        String s = (String) a.getAttribute(HTML.Attribute.COLSPAN);
        if (s != null) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException nfe) {
                }
        }

        return 1;
    }


     int getRowsOccupied(View v) {
        AttributeSet a = v.getElement().getAttributes();
        String s = (String) a.getAttribute(HTML.Attribute.ROWSPAN);
        if (s != null) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException nfe) {
                }
        }

        return 1;
    }

     void invalidateGrid() {
        gridValid = false;
    }

    protected void forwardUpdate(DocumentEvent.ElementChange ec,
                                     DocumentEvent e, Shape a, ViewFactory f) {
        super.forwardUpdate(ec, e, a, f);
        if (a != null) {
            Component c = getContainer();
            if (c != null) {
                Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a :
                                   a.getBounds();
                c.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
            }
        }
    }


    public void replace(int offset, int length, View[] views) {
        super.replace(offset, length, views);
        invalidateGrid();
    }


    void updateGrid() {
        if (! gridValid) {
            rows.removeAllElements();
            int n = getViewCount();
            for (int i = 0; i < n; i++) {
                View v = getView(i);
                if (v instanceof TableRow) {
                    rows.addElement((TableRow) v);
                    TableRow rv = (TableRow) v;
                    rv.clearFilledColumns();
                    rv.setRow(i);
                }
            }

            int maxColumns = 0;
            int nrows = rows.size();
            for (int row = 0; row < nrows; row++) {
                TableRow rv = getRow(row);
                int col = 0;
                for (int cell = 0; cell < rv.getViewCount(); cell++, col++) {
                    View cv = rv.getView(cell);
                    for (; rv.isFilled(col); col++);
                    int rowSpan = getRowsOccupied(cv);
                    int colSpan = getColumnsOccupied(cv);
                    if ((colSpan > 1) || (rowSpan > 1)) {
                        int rowLimit = row + rowSpan;
                        int colLimit = col + colSpan;
                        for (int i = row; i < rowLimit; i++) {
                            for (int j = col; j < colLimit; j++) {
                                if (i != row || j != col) {
                                    addFill(i, j);
                                }
                            }
                        }
                        if (colSpan > 1) {
                            col += colSpan - 1;
                        }
                    }
                }
                maxColumns = Math.max(maxColumns, col);
            }

            columnSpans = new int[maxColumns];
            columnOffsets = new int[maxColumns];
            columnRequirements = new SizeRequirements[maxColumns];
            for (int i = 0; i < maxColumns; i++) {
                columnRequirements[i] = new SizeRequirements();
            }
            gridValid = true;
        }
    }


    void addFill(int row, int col) {
        TableRow rv = getRow(row);
        if (rv != null) {
            rv.fillColumn(col);
        }
    }


    protected void layoutColumns(int targetSpan, int[] offsets, int[] spans,
                                 SizeRequirements[] reqs) {
        SizeRequirements.calculateTiledPositions(targetSpan, null, reqs,
                                                 offsets, spans);
    }


    protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
        updateGrid();

        int n = getRowCount();
        for (int i = 0; i < n; i++) {
            TableRow row = getRow(i);
            row.layoutChanged(axis);
        }

        layoutColumns(targetSpan, columnOffsets, columnSpans, columnRequirements);

        super.layoutMinorAxis(targetSpan, axis, offsets, spans);
    }


    protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
        updateGrid();

        calculateColumnRequirements(axis);


        if (r == null) {
            r = new SizeRequirements();
        }
        long min = 0;
        long pref = 0;
        long max = 0;
        for (SizeRequirements req : columnRequirements) {
            min += req.minimum;
            pref += req.preferred;
            max += req.maximum;
        }
        r.minimum = (int) min;
        r.preferred = (int) pref;
        r.maximum = (int) max;
        r.alignment = 0;
        return r;
    }




    void calculateColumnRequirements(int axis) {
        boolean hasMultiColumn = false;
        int nrows = getRowCount();
        for (int i = 0; i < nrows; i++) {
            TableRow row = getRow(i);
            int col = 0;
            int ncells = row.getViewCount();
            for (int cell = 0; cell < ncells; cell++, col++) {
                View cv = row.getView(cell);
                for (; row.isFilled(col); col++); int rowSpan = getRowsOccupied(cv);
                int colSpan = getColumnsOccupied(cv);
                if (colSpan == 1) {
                    checkSingleColumnCell(axis, col, cv);
                } else {
                    hasMultiColumn = true;
                    col += colSpan - 1;
                }
            }
        }

        if (hasMultiColumn) {
            for (int i = 0; i < nrows; i++) {
                TableRow row = getRow(i);
                int col = 0;
                int ncells = row.getViewCount();
                for (int cell = 0; cell < ncells; cell++, col++) {
                    View cv = row.getView(cell);
                    for (; row.isFilled(col); col++); int colSpan = getColumnsOccupied(cv);
                    if (colSpan > 1) {
                        checkMultiColumnCell(axis, col, colSpan, cv);
                        col += colSpan - 1;
                    }
                }
            }
        }


    }


    void checkSingleColumnCell(int axis, int col, View v) {
        SizeRequirements req = columnRequirements[col];
        req.minimum = Math.max((int) v.getMinimumSpan(axis), req.minimum);
        req.preferred = Math.max((int) v.getPreferredSpan(axis), req.preferred);
        req.maximum = Math.max((int) v.getMaximumSpan(axis), req.maximum);
    }


    void checkMultiColumnCell(int axis, int col, int ncols, View v) {
        long min = 0;
        long pref = 0;
        long max = 0;
        for (int i = 0; i < ncols; i++) {
            SizeRequirements req = columnRequirements[col + i];
            min += req.minimum;
            pref += req.preferred;
            max += req.maximum;
        }

        int cmin = (int) v.getMinimumSpan(axis);
        if (cmin > min) {

            SizeRequirements[] reqs = new SizeRequirements[ncols];
            for (int i = 0; i < ncols; i++) {
                SizeRequirements r = reqs[i] = columnRequirements[col + i];
                r.maximum = Math.max(r.maximum, (int) v.getMaximumSpan(axis));
            }
            int[] spans = new int[ncols];
            int[] offsets = new int[ncols];
            SizeRequirements.calculateTiledPositions(cmin, null, reqs,
                                                     offsets, spans);
            for (int i = 0; i < ncols; i++) {
                SizeRequirements req = reqs[i];
                req.minimum = Math.max(spans[i], req.minimum);
                req.preferred = Math.max(req.minimum, req.preferred);
                req.maximum = Math.max(req.preferred, req.maximum);
            }
        }

        int cpref = (int) v.getPreferredSpan(axis);
        if (cpref > pref) {

            SizeRequirements[] reqs = new SizeRequirements[ncols];
            for (int i = 0; i < ncols; i++) {
                SizeRequirements r = reqs[i] = columnRequirements[col + i];
            }
            int[] spans = new int[ncols];
            int[] offsets = new int[ncols];
            SizeRequirements.calculateTiledPositions(cpref, null, reqs,
                                                     offsets, spans);
            for (int i = 0; i < ncols; i++) {
                SizeRequirements req = reqs[i];
                req.preferred = Math.max(spans[i], req.preferred);
                req.maximum = Math.max(req.preferred, req.maximum);
            }
        }

    }


    protected View getViewAtPosition(int pos, Rectangle a) {
        int n = getViewCount();
        for (int i = 0; i < n; i++) {
            View v = getView(i);
            int p0 = v.getStartOffset();
            int p1 = v.getEndOffset();
            if ((pos >= p0) && (pos < p1)) {
                if (a != null) {
                    childAllocation(i, a);
                }
                return v;
            }
        }
        if (pos == getEndOffset()) {
            View v = getView(n - 1);
            if (a != null) {
                this.childAllocation(n - 1, a);
            }
            return v;
        }
        return null;
    }

    int[] columnSpans;
    int[] columnOffsets;
    SizeRequirements[] columnRequirements;
    Vector<TableRow> rows;
    boolean gridValid;
    static final private BitSet EMPTY = new BitSet();


    public class TableRow extends BoxView {


        public TableRow(Element elem) {
            super(elem, View.X_AXIS);
            fillColumns = new BitSet();
        }

        void clearFilledColumns() {
            fillColumns.and(EMPTY);
        }

        void fillColumn(int col) {
            fillColumns.set(col);
        }

        boolean isFilled(int col) {
            return fillColumns.get(col);
        }


        int getRow() {
            return row;
        }


        void setRow(int row) {
            this.row = row;
        }


        int getColumnCount() {
            int nfill = 0;
            int n = fillColumns.size();
            for (int i = 0; i < n; i++) {
                if (fillColumns.get(i)) {
                    nfill ++;
                }
            }
            return getViewCount() + nfill;
        }


        public void replace(int offset, int length, View[] views) {
            super.replace(offset, length, views);
            invalidateGrid();
        }


        protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
            int col = 0;
            int ncells = getViewCount();
            for (int cell = 0; cell < ncells; cell++, col++) {
                View cv = getView(cell);
                for (; isFilled(col); col++); int colSpan = getColumnsOccupied(cv);
                spans[cell] = columnSpans[col];
                offsets[cell] = columnOffsets[col];
                if (colSpan > 1) {
                    int n = columnSpans.length;
                    for (int j = 1; j < colSpan; j++) {
                        if ((col+j) < n) {
                            spans[cell] += columnSpans[col+j];
                        }
                    }
                    col += colSpan - 1;
                }
            }
        }


        protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
            super.layoutMinorAxis(targetSpan, axis, offsets, spans);
            int col = 0;
            int ncells = getViewCount();
            for (int cell = 0; cell < ncells; cell++, col++) {
                View cv = getView(cell);
                for (; isFilled(col); col++); int colSpan = getColumnsOccupied(cv);
                int rowSpan = getRowsOccupied(cv);
                if (rowSpan > 1) {
                    for (int j = 1; j < rowSpan; j++) {
                        int row = getRow() + j;
                        if (row < TableView.this.getViewCount()) {
                            int span = TableView.this.getSpan(Y_AXIS, getRow()+j);
                            spans[cell] += span;
                        }
                    }
                }
                if (colSpan > 1) {
                    col += colSpan - 1;
                }
            }
        }


        public int getResizeWeight(int axis) {
            return 1;
        }


        protected View getViewAtPosition(int pos, Rectangle a) {
            int n = getViewCount();
            for (int i = 0; i < n; i++) {
                View v = getView(i);
                int p0 = v.getStartOffset();
                int p1 = v.getEndOffset();
                if ((pos >= p0) && (pos < p1)) {
                    if (a != null) {
                        childAllocation(i, a);
                    }
                    return v;
                }
            }
            if (pos == getEndOffset()) {
                View v = getView(n - 1);
                if (a != null) {
                    this.childAllocation(n - 1, a);
                }
                return v;
            }
            return null;
        }


        BitSet fillColumns;

        int row;
    }


    @Deprecated
    public class TableCell extends BoxView implements GridCell {


        public TableCell(Element elem) {
            super(elem, View.Y_AXIS);
        }

        public int getColumnCount() {
            return 1;
        }


        public int getRowCount() {
            return 1;
        }



        public void setGridLocation(int row, int col) {
            this.row = row;
            this.col = col;
        }


        public int getGridRow() {
            return row;
        }


        public int getGridColumn() {
            return col;
        }

        int row;
        int col;
    }


    interface GridCell {


        public void setGridLocation(int row, int col);


        public int getGridRow();


        public int getGridColumn();


        public int getColumnCount();


        public int getRowCount();

    }

}
