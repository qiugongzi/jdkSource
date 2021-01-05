

package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.TooManyListenersException;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.text.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.DragRecognitionSupport.BeforeDrag;
import sun.swing.SwingUtilities2;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import sun.swing.DefaultLookup;
import sun.swing.UIAction;


public class BasicTableUI extends TableUI
{
    private static final StringBuilder BASELINE_COMPONENT_KEY =
        new StringBuilder("Table.baselineComponent");

protected JTable table;
    protected CellRendererPane rendererPane;

    protected KeyListener keyListener;
    protected FocusListener focusListener;
    protected MouseInputListener mouseInputListener;

    private Handler handler;


    private boolean isFileList = false;

private static class Actions extends UIAction {
        private static final String CANCEL_EDITING = "cancel";
        private static final String SELECT_ALL = "selectAll";
        private static final String CLEAR_SELECTION = "clearSelection";
        private static final String START_EDITING = "startEditing";

        private static final String NEXT_ROW = "selectNextRow";
        private static final String NEXT_ROW_CELL = "selectNextRowCell";
        private static final String NEXT_ROW_EXTEND_SELECTION =
                "selectNextRowExtendSelection";
        private static final String NEXT_ROW_CHANGE_LEAD =
                "selectNextRowChangeLead";
        private static final String PREVIOUS_ROW = "selectPreviousRow";
        private static final String PREVIOUS_ROW_CELL = "selectPreviousRowCell";
        private static final String PREVIOUS_ROW_EXTEND_SELECTION =
                "selectPreviousRowExtendSelection";
        private static final String PREVIOUS_ROW_CHANGE_LEAD =
                "selectPreviousRowChangeLead";

        private static final String NEXT_COLUMN = "selectNextColumn";
        private static final String NEXT_COLUMN_CELL = "selectNextColumnCell";
        private static final String NEXT_COLUMN_EXTEND_SELECTION =
                "selectNextColumnExtendSelection";
        private static final String NEXT_COLUMN_CHANGE_LEAD =
                "selectNextColumnChangeLead";
        private static final String PREVIOUS_COLUMN = "selectPreviousColumn";
        private static final String PREVIOUS_COLUMN_CELL =
                "selectPreviousColumnCell";
        private static final String PREVIOUS_COLUMN_EXTEND_SELECTION =
                "selectPreviousColumnExtendSelection";
        private static final String PREVIOUS_COLUMN_CHANGE_LEAD =
                "selectPreviousColumnChangeLead";

        private static final String SCROLL_LEFT_CHANGE_SELECTION =
                "scrollLeftChangeSelection";
        private static final String SCROLL_LEFT_EXTEND_SELECTION =
                "scrollLeftExtendSelection";
        private static final String SCROLL_RIGHT_CHANGE_SELECTION =
                "scrollRightChangeSelection";
        private static final String SCROLL_RIGHT_EXTEND_SELECTION =
                "scrollRightExtendSelection";

        private static final String SCROLL_UP_CHANGE_SELECTION =
                "scrollUpChangeSelection";
        private static final String SCROLL_UP_EXTEND_SELECTION =
                "scrollUpExtendSelection";
        private static final String SCROLL_DOWN_CHANGE_SELECTION =
                "scrollDownChangeSelection";
        private static final String SCROLL_DOWN_EXTEND_SELECTION =
                "scrollDownExtendSelection";

        private static final String FIRST_COLUMN =
                "selectFirstColumn";
        private static final String FIRST_COLUMN_EXTEND_SELECTION =
                "selectFirstColumnExtendSelection";
        private static final String LAST_COLUMN =
                "selectLastColumn";
        private static final String LAST_COLUMN_EXTEND_SELECTION =
                "selectLastColumnExtendSelection";

        private static final String FIRST_ROW =
                "selectFirstRow";
        private static final String FIRST_ROW_EXTEND_SELECTION =
                "selectFirstRowExtendSelection";
        private static final String LAST_ROW =
                "selectLastRow";
        private static final String LAST_ROW_EXTEND_SELECTION =
                "selectLastRowExtendSelection";

        private static final String ADD_TO_SELECTION = "addToSelection";

        private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";

        private static final String EXTEND_TO = "extendTo";

        private static final String MOVE_SELECTION_TO = "moveSelectionTo";

        private static final String FOCUS_HEADER = "focusHeader";

        protected int dx;
        protected int dy;
        protected boolean extend;
        protected boolean inSelection;

        protected boolean forwards;
        protected boolean vertically;
        protected boolean toLimit;

        protected int leadRow;
        protected int leadColumn;

        Actions(String name) {
            super(name);
        }

        Actions(String name, int dx, int dy, boolean extend,
                boolean inSelection) {
            super(name);

            if (inSelection) {
                this.inSelection = true;

                dx = sign(dx);
                dy = sign(dy);

                assert (dx == 0 || dy == 0) && !(dx == 0 && dy == 0);
            }

            this.dx = dx;
            this.dy = dy;
            this.extend = extend;
        }

        Actions(String name, boolean extend, boolean forwards,
                boolean vertically, boolean toLimit) {
            this(name, 0, 0, extend, false);
            this.forwards = forwards;
            this.vertically = vertically;
            this.toLimit = toLimit;
        }

        private static int clipToRange(int i, int a, int b) {
            return Math.min(Math.max(i, a), b-1);
        }

        private void moveWithinTableRange(JTable table, int dx, int dy) {
            leadRow = clipToRange(leadRow+dy, 0, table.getRowCount());
            leadColumn = clipToRange(leadColumn+dx, 0, table.getColumnCount());
        }

        private static int sign(int num) {
            return (num < 0) ? -1 : ((num == 0) ? 0 : 1);
        }


        private boolean moveWithinSelectedRange(JTable table, int dx, int dy,
                ListSelectionModel rsm, ListSelectionModel csm) {

            int totalCount;
            int minX, maxX, minY, maxY;

            boolean rs = table.getRowSelectionAllowed();
            boolean cs = table.getColumnSelectionAllowed();

            if (rs && cs) {
                totalCount = table.getSelectedRowCount() * table.getSelectedColumnCount();
                minX = csm.getMinSelectionIndex();
                maxX = csm.getMaxSelectionIndex();
                minY = rsm.getMinSelectionIndex();
                maxY = rsm.getMaxSelectionIndex();
            } else if (rs) {
                totalCount = table.getSelectedRowCount();
                minX = 0;
                maxX = table.getColumnCount() - 1;
                minY = rsm.getMinSelectionIndex();
                maxY = rsm.getMaxSelectionIndex();
            } else if (cs) {
                totalCount = table.getSelectedColumnCount();
                minX = csm.getMinSelectionIndex();
                maxX = csm.getMaxSelectionIndex();
                minY = 0;
                maxY = table.getRowCount() - 1;
            } else {
                totalCount = 0;
                minX = maxX = minY = maxY = 0;
            }

            boolean stayInSelection;

            if (totalCount == 0 ||
                    (totalCount == 1 && table.isCellSelected(leadRow, leadColumn))) {

                stayInSelection = false;

                maxX = table.getColumnCount() - 1;
                maxY = table.getRowCount() - 1;

                minX = Math.min(0, maxX);
                minY = Math.min(0, maxY);
            } else {
                stayInSelection = true;
            }

            if (dy == 1 && leadColumn == -1) {
                leadColumn = minX;
                leadRow = -1;
            } else if (dx == 1 && leadRow == -1) {
                leadRow = minY;
                leadColumn = -1;
            } else if (dy == -1 && leadColumn == -1) {
                leadColumn = maxX;
                leadRow = maxY + 1;
            } else if (dx == -1 && leadRow == -1) {
                leadRow = maxY;
                leadColumn = maxX + 1;
            }

            leadRow = Math.min(Math.max(leadRow, minY - 1), maxY + 1);
            leadColumn = Math.min(Math.max(leadColumn, minX - 1), maxX + 1);

            do {
                calcNextPos(dx, minX, maxX, dy, minY, maxY);
            } while (stayInSelection && !table.isCellSelected(leadRow, leadColumn));

            return stayInSelection;
        }


        private void calcNextPos(int dx, int minX, int maxX,
                                 int dy, int minY, int maxY) {

            if (dx != 0) {
                leadColumn += dx;
                if (leadColumn > maxX) {
                    leadColumn = minX;
                    leadRow++;
                    if (leadRow > maxY) {
                        leadRow = minY;
                    }
                } else if (leadColumn < minX) {
                    leadColumn = maxX;
                    leadRow--;
                    if (leadRow < minY) {
                        leadRow = maxY;
                    }
                }
            } else {
                leadRow += dy;
                if (leadRow > maxY) {
                    leadRow = minY;
                    leadColumn++;
                    if (leadColumn > maxX) {
                        leadColumn = minX;
                    }
                } else if (leadRow < minY) {
                    leadRow = maxY;
                    leadColumn--;
                    if (leadColumn < minX) {
                        leadColumn = maxX;
                    }
                }
            }
        }

        public void actionPerformed(ActionEvent e) {
            String key = getName();
            JTable table = (JTable)e.getSource();

            ListSelectionModel rsm = table.getSelectionModel();
            leadRow = getAdjustedLead(table, true, rsm);

            ListSelectionModel csm = table.getColumnModel().getSelectionModel();
            leadColumn = getAdjustedLead(table, false, csm);

            if (key == SCROLL_LEFT_CHANGE_SELECTION ||        key == SCROLL_LEFT_EXTEND_SELECTION ||
                    key == SCROLL_RIGHT_CHANGE_SELECTION ||
                    key == SCROLL_RIGHT_EXTEND_SELECTION ||
                    key == SCROLL_UP_CHANGE_SELECTION ||
                    key == SCROLL_UP_EXTEND_SELECTION ||
                    key == SCROLL_DOWN_CHANGE_SELECTION ||
                    key == SCROLL_DOWN_EXTEND_SELECTION ||
                    key == FIRST_COLUMN ||
                    key == FIRST_COLUMN_EXTEND_SELECTION ||
                    key == FIRST_ROW ||
                    key == FIRST_ROW_EXTEND_SELECTION ||
                    key == LAST_COLUMN ||
                    key == LAST_COLUMN_EXTEND_SELECTION ||
                    key == LAST_ROW ||
                    key == LAST_ROW_EXTEND_SELECTION) {
                if (toLimit) {
                    if (vertically) {
                        int rowCount = table.getRowCount();
                        this.dx = 0;
                        this.dy = forwards ? rowCount : -rowCount;
                    }
                    else {
                        int colCount = table.getColumnCount();
                        this.dx = forwards ? colCount : -colCount;
                        this.dy = 0;
                    }
                }
                else {
                    if (!(SwingUtilities.getUnwrappedParent(table).getParent() instanceof
                            JScrollPane)) {
                        return;
                    }

                    Dimension delta = table.getParent().getSize();

                    if (vertically) {
                        Rectangle r = table.getCellRect(leadRow, 0, true);
                        if (forwards) {
                            r.y += Math.max(delta.height, r.height);
                        } else {
                            r.y -= delta.height;
                        }

                        this.dx = 0;
                        int newRow = table.rowAtPoint(r.getLocation());
                        if (newRow == -1 && forwards) {
                            newRow = table.getRowCount();
                        }
                        this.dy = newRow - leadRow;
                    }
                    else {
                        Rectangle r = table.getCellRect(0, leadColumn, true);

                        if (forwards) {
                            r.x += Math.max(delta.width, r.width);
                        } else {
                            r.x -= delta.width;
                        }

                        int newColumn = table.columnAtPoint(r.getLocation());
                        if (newColumn == -1) {
                            boolean ltr = table.getComponentOrientation().isLeftToRight();

                            newColumn = forwards ? (ltr ? table.getColumnCount() : 0)
                                                 : (ltr ? 0 : table.getColumnCount());

                        }
                        this.dx = newColumn - leadColumn;
                        this.dy = 0;
                    }
                }
            }
            if (key == NEXT_ROW ||  key == NEXT_ROW_CELL ||
                    key == NEXT_ROW_EXTEND_SELECTION ||
                    key == NEXT_ROW_CHANGE_LEAD ||
                    key == NEXT_COLUMN ||
                    key == NEXT_COLUMN_CELL ||
                    key == NEXT_COLUMN_EXTEND_SELECTION ||
                    key == NEXT_COLUMN_CHANGE_LEAD ||
                    key == PREVIOUS_ROW ||
                    key == PREVIOUS_ROW_CELL ||
                    key == PREVIOUS_ROW_EXTEND_SELECTION ||
                    key == PREVIOUS_ROW_CHANGE_LEAD ||
                    key == PREVIOUS_COLUMN ||
                    key == PREVIOUS_COLUMN_CELL ||
                    key == PREVIOUS_COLUMN_EXTEND_SELECTION ||
                    key == PREVIOUS_COLUMN_CHANGE_LEAD ||
                    key == SCROLL_LEFT_CHANGE_SELECTION ||
                    key == SCROLL_LEFT_EXTEND_SELECTION ||
                    key == SCROLL_RIGHT_CHANGE_SELECTION ||
                    key == SCROLL_RIGHT_EXTEND_SELECTION ||
                    key == SCROLL_UP_CHANGE_SELECTION ||
                    key == SCROLL_UP_EXTEND_SELECTION ||
                    key == SCROLL_DOWN_CHANGE_SELECTION ||
                    key == SCROLL_DOWN_EXTEND_SELECTION ||
                    key == FIRST_COLUMN ||
                    key == FIRST_COLUMN_EXTEND_SELECTION ||
                    key == FIRST_ROW ||
                    key == FIRST_ROW_EXTEND_SELECTION ||
                    key == LAST_COLUMN ||
                    key == LAST_COLUMN_EXTEND_SELECTION ||
                    key == LAST_ROW ||
                    key == LAST_ROW_EXTEND_SELECTION) {

                if (table.isEditing() &&
                        !table.getCellEditor().stopCellEditing()) {
                    return;
                }

                boolean changeLead = false;
                if (key == NEXT_ROW_CHANGE_LEAD || key == PREVIOUS_ROW_CHANGE_LEAD) {
                    changeLead = (rsm.getSelectionMode()
                                     == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                } else if (key == NEXT_COLUMN_CHANGE_LEAD || key == PREVIOUS_COLUMN_CHANGE_LEAD) {
                    changeLead = (csm.getSelectionMode()
                                     == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                }

                if (changeLead) {
                    moveWithinTableRange(table, dx, dy);
                    if (dy != 0) {
                        ((DefaultListSelectionModel)rsm).moveLeadSelectionIndex(leadRow);
                        if (getAdjustedLead(table, false, csm) == -1
                                && table.getColumnCount() > 0) {

                            ((DefaultListSelectionModel)csm).moveLeadSelectionIndex(0);
                        }
                    } else {
                        ((DefaultListSelectionModel)csm).moveLeadSelectionIndex(leadColumn);
                        if (getAdjustedLead(table, true, rsm) == -1
                                && table.getRowCount() > 0) {

                            ((DefaultListSelectionModel)rsm).moveLeadSelectionIndex(0);
                        }
                    }

                    Rectangle cellRect = table.getCellRect(leadRow, leadColumn, false);
                    if (cellRect != null) {
                        table.scrollRectToVisible(cellRect);
                    }
                } else if (!inSelection) {
                    moveWithinTableRange(table, dx, dy);
                    table.changeSelection(leadRow, leadColumn, false, extend);
                }
                else {
                    if (table.getRowCount() <= 0 || table.getColumnCount() <= 0) {
                        return;
                    }

                    if (moveWithinSelectedRange(table, dx, dy, rsm, csm)) {
                        if (rsm.isSelectedIndex(leadRow)) {
                            rsm.addSelectionInterval(leadRow, leadRow);
                        } else {
                            rsm.removeSelectionInterval(leadRow, leadRow);
                        }

                        if (csm.isSelectedIndex(leadColumn)) {
                            csm.addSelectionInterval(leadColumn, leadColumn);
                        } else {
                            csm.removeSelectionInterval(leadColumn, leadColumn);
                        }

                        Rectangle cellRect = table.getCellRect(leadRow, leadColumn, false);
                        if (cellRect != null) {
                            table.scrollRectToVisible(cellRect);
                        }
                    }
                    else {
                        table.changeSelection(leadRow, leadColumn,
                                false, false);
                    }
                }


            } else if (key == CANCEL_EDITING) {
                table.removeEditor();
            } else if (key == SELECT_ALL) {
                table.selectAll();
            } else if (key == CLEAR_SELECTION) {
                table.clearSelection();
            } else if (key == START_EDITING) {
                if (!table.hasFocus()) {
                    CellEditor cellEditor = table.getCellEditor();
                    if (cellEditor != null && !cellEditor.stopCellEditing()) {
                        return;
                    }
                    table.requestFocus();
                    return;
                }
                table.editCellAt(leadRow, leadColumn, e);
                Component editorComp = table.getEditorComponent();
                if (editorComp != null) {
                    editorComp.requestFocus();
                }
            } else if (key == ADD_TO_SELECTION) {
                if (!table.isCellSelected(leadRow, leadColumn)) {
                    int oldAnchorRow = rsm.getAnchorSelectionIndex();
                    int oldAnchorColumn = csm.getAnchorSelectionIndex();
                    rsm.setValueIsAdjusting(true);
                    csm.setValueIsAdjusting(true);
                    table.changeSelection(leadRow, leadColumn, true, false);
                    rsm.setAnchorSelectionIndex(oldAnchorRow);
                    csm.setAnchorSelectionIndex(oldAnchorColumn);
                    rsm.setValueIsAdjusting(false);
                    csm.setValueIsAdjusting(false);
                }
            } else if (key == TOGGLE_AND_ANCHOR) {
                table.changeSelection(leadRow, leadColumn, true, false);
            } else if (key == EXTEND_TO) {
                table.changeSelection(leadRow, leadColumn, false, true);
            } else if (key == MOVE_SELECTION_TO) {
                table.changeSelection(leadRow, leadColumn, false, false);
            } else if (key == FOCUS_HEADER) {
                JTableHeader th = table.getTableHeader();
                if (th != null) {
                    int col = table.getSelectedColumn();
                    if (col >= 0) {
                        TableHeaderUI thUI = th.getUI();
                        if (thUI instanceof BasicTableHeaderUI) {
                            ((BasicTableHeaderUI)thUI).selectColumn(col);
                        }
                    }

                    th.requestFocusInWindow();
                }
            }
        }

        public boolean isEnabled(Object sender) {
            String key = getName();

            if (sender instanceof JTable &&
                Boolean.TRUE.equals(((JTable)sender).getClientProperty("Table.isFileList"))) {
                if (key == NEXT_COLUMN ||
                        key == NEXT_COLUMN_CELL ||
                        key == NEXT_COLUMN_EXTEND_SELECTION ||
                        key == NEXT_COLUMN_CHANGE_LEAD ||
                        key == PREVIOUS_COLUMN ||
                        key == PREVIOUS_COLUMN_CELL ||
                        key == PREVIOUS_COLUMN_EXTEND_SELECTION ||
                        key == PREVIOUS_COLUMN_CHANGE_LEAD ||
                        key == SCROLL_LEFT_CHANGE_SELECTION ||
                        key == SCROLL_LEFT_EXTEND_SELECTION ||
                        key == SCROLL_RIGHT_CHANGE_SELECTION ||
                        key == SCROLL_RIGHT_EXTEND_SELECTION ||
                        key == FIRST_COLUMN ||
                        key == FIRST_COLUMN_EXTEND_SELECTION ||
                        key == LAST_COLUMN ||
                        key == LAST_COLUMN_EXTEND_SELECTION ||
                        key == NEXT_ROW_CELL ||
                        key == PREVIOUS_ROW_CELL) {

                    return false;
                }
            }

            if (key == CANCEL_EDITING && sender instanceof JTable) {
                return ((JTable)sender).isEditing();
            } else if (key == NEXT_ROW_CHANGE_LEAD ||
                       key == PREVIOUS_ROW_CHANGE_LEAD) {
                return sender != null &&
                       ((JTable)sender).getSelectionModel()
                           instanceof DefaultListSelectionModel;
            } else if (key == NEXT_COLUMN_CHANGE_LEAD ||
                       key == PREVIOUS_COLUMN_CHANGE_LEAD) {
                return sender != null &&
                       ((JTable)sender).getColumnModel().getSelectionModel()
                           instanceof DefaultListSelectionModel;
            } else if (key == ADD_TO_SELECTION && sender instanceof JTable) {
                JTable table = (JTable)sender;
                int leadRow = getAdjustedLead(table, true);
                int leadCol = getAdjustedLead(table, false);
                return !(table.isEditing() || table.isCellSelected(leadRow, leadCol));
            } else if (key == FOCUS_HEADER && sender instanceof JTable) {
                JTable table = (JTable)sender;
                return table.getTableHeader() != null;
            }

            return true;
        }
    }


public class KeyHandler implements KeyListener {
        public void keyPressed(KeyEvent e) {
            getHandler().keyPressed(e);
        }

        public void keyReleased(KeyEvent e) {
            getHandler().keyReleased(e);
        }

        public void keyTyped(KeyEvent e) {
            getHandler().keyTyped(e);
        }
    }

public class FocusHandler implements FocusListener {
        public void focusGained(FocusEvent e) {
            getHandler().focusGained(e);
        }

        public void focusLost(FocusEvent e) {
            getHandler().focusLost(e);
        }
    }

public class MouseInputHandler implements MouseInputListener {
        public void mouseClicked(MouseEvent e) {
            getHandler().mouseClicked(e);
        }

        public void mousePressed(MouseEvent e) {
            getHandler().mousePressed(e);
        }

        public void mouseReleased(MouseEvent e) {
            getHandler().mouseReleased(e);
        }

        public void mouseEntered(MouseEvent e) {
            getHandler().mouseEntered(e);
        }

        public void mouseExited(MouseEvent e) {
            getHandler().mouseExited(e);
        }

        public void mouseMoved(MouseEvent e) {
            getHandler().mouseMoved(e);
        }

        public void mouseDragged(MouseEvent e) {
            getHandler().mouseDragged(e);
        }
    }

    private class Handler implements FocusListener, MouseInputListener,
            PropertyChangeListener, ListSelectionListener, ActionListener,
            BeforeDrag {

        private void repaintLeadCell( ) {
            int lr = getAdjustedLead(table, true);
            int lc = getAdjustedLead(table, false);

            if (lr < 0 || lc < 0) {
                return;
            }

            Rectangle dirtyRect = table.getCellRect(lr, lc, false);
            table.repaint(dirtyRect);
        }

        public void focusGained(FocusEvent e) {
            repaintLeadCell();
        }

        public void focusLost(FocusEvent e) {
            repaintLeadCell();
        }


        public void keyPressed(KeyEvent e) { }

        public void keyReleased(KeyEvent e) { }

        public void keyTyped(KeyEvent e) {
            KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getKeyChar(),
                    e.getModifiers());

            InputMap map = table.getInputMap(JComponent.WHEN_FOCUSED);
            if (map != null && map.get(keyStroke) != null) {
                return;
            }
            map = table.getInputMap(JComponent.
                                  WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            if (map != null && map.get(keyStroke) != null) {
                return;
            }

            keyStroke = KeyStroke.getKeyStrokeForEvent(e);

            if (e.getKeyChar() == '\r') {
                return;
            }

            int leadRow = getAdjustedLead(table, true);
            int leadColumn = getAdjustedLead(table, false);
            if (leadRow != -1 && leadColumn != -1 && !table.isEditing()) {
                if (!table.editCellAt(leadRow, leadColumn)) {
                    return;
                }
            }

            Component editorComp = table.getEditorComponent();
            if (table.isEditing() && editorComp != null) {
                if (editorComp instanceof JComponent) {
                    JComponent component = (JComponent)editorComp;
                    map = component.getInputMap(JComponent.WHEN_FOCUSED);
                    Object binding = (map != null) ? map.get(keyStroke) : null;
                    if (binding == null) {
                        map = component.getInputMap(JComponent.
                                         WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                        binding = (map != null) ? map.get(keyStroke) : null;
                    }
                    if (binding != null) {
                        ActionMap am = component.getActionMap();
                        Action action = (am != null) ? am.get(binding) : null;
                        if (action != null && SwingUtilities.
                            notifyAction(action, keyStroke, e, component,
                                         e.getModifiers())) {
                            e.consume();
                        }
                    }
                }
            }
        }


        private Component dispatchComponent;

        public void mouseClicked(MouseEvent e) {}

        private void setDispatchComponent(MouseEvent e) {
            Component editorComponent = table.getEditorComponent();
            Point p = e.getPoint();
            Point p2 = SwingUtilities.convertPoint(table, p, editorComponent);
            dispatchComponent =
                    SwingUtilities.getDeepestComponentAt(editorComponent,
                            p2.x, p2.y);
            SwingUtilities2.setSkipClickCount(dispatchComponent,
                                              e.getClickCount() - 1);
        }

        private boolean repostEvent(MouseEvent e) {
            if (dispatchComponent == null || !table.isEditing()) {
                return false;
            }
            MouseEvent e2 = SwingUtilities.convertMouseEvent(table, e,
                    dispatchComponent);
            dispatchComponent.dispatchEvent(e2);
            return true;
        }

        private void setValueIsAdjusting(boolean flag) {
            table.getSelectionModel().setValueIsAdjusting(flag);
            table.getColumnModel().getSelectionModel().
                    setValueIsAdjusting(flag);
        }

        private int pressedRow;
        private int pressedCol;
        private MouseEvent pressedEvent;

        private boolean dragPressDidSelection;

        private boolean dragStarted;

        private boolean shouldStartTimer;

        private boolean outsidePrefSize;

        private Timer timer = null;

        private boolean canStartDrag() {
            if (pressedRow == -1 || pressedCol == -1) {
                return false;
            }

            if (isFileList) {
                return !outsidePrefSize;
            }

            if ((table.getSelectionModel().getSelectionMode() ==
                     ListSelectionModel.SINGLE_SELECTION) &&
                (table.getColumnModel().getSelectionModel().getSelectionMode() ==
                     ListSelectionModel.SINGLE_SELECTION)) {

                return true;
            }

            return table.isCellSelected(pressedRow, pressedCol);
        }

        public void mousePressed(MouseEvent e) {
            if (SwingUtilities2.shouldIgnore(e, table)) {
                return;
            }

            if (table.isEditing() && !table.getCellEditor().stopCellEditing()) {
                Component editorComponent = table.getEditorComponent();
                if (editorComponent != null && !editorComponent.hasFocus()) {
                    SwingUtilities2.compositeRequestFocus(editorComponent);
                }
                return;
            }

            Point p = e.getPoint();
            pressedRow = table.rowAtPoint(p);
            pressedCol = table.columnAtPoint(p);
            outsidePrefSize = pointOutsidePrefSize(pressedRow, pressedCol, p);

            if (isFileList) {
                shouldStartTimer =
                    table.isCellSelected(pressedRow, pressedCol) &&
                    !e.isShiftDown() &&
                    !BasicGraphicsUtils.isMenuShortcutKeyDown(e) &&
                    !outsidePrefSize;
            }

            if (table.getDragEnabled()) {
                mousePressedDND(e);
            } else {
                SwingUtilities2.adjustFocus(table);
                if (!isFileList) {
                    setValueIsAdjusting(true);
                }
                adjustSelection(e);
            }
        }

        private void mousePressedDND(MouseEvent e) {
            pressedEvent = e;
            boolean grabFocus = true;
            dragStarted = false;

            if (canStartDrag() && DragRecognitionSupport.mousePressed(e)) {

                dragPressDidSelection = false;

                if (BasicGraphicsUtils.isMenuShortcutKeyDown(e) && isFileList) {
                    return;
                } else if (!e.isShiftDown() && table.isCellSelected(pressedRow, pressedCol)) {
                    table.getSelectionModel().addSelectionInterval(pressedRow,
                                                                   pressedRow);
                    table.getColumnModel().getSelectionModel().
                        addSelectionInterval(pressedCol, pressedCol);

                    return;
                }

                dragPressDidSelection = true;

                grabFocus = false;
            } else if (!isFileList) {
                setValueIsAdjusting(true);
            }

            if (grabFocus) {
                SwingUtilities2.adjustFocus(table);
            }

            adjustSelection(e);
        }

        private void adjustSelection(MouseEvent e) {
            if (outsidePrefSize) {
                if (e.getID() ==  MouseEvent.MOUSE_PRESSED &&
                    (!e.isShiftDown() ||
                     table.getSelectionModel().getSelectionMode() ==
                     ListSelectionModel.SINGLE_SELECTION)) {
                    table.clearSelection();
                    TableCellEditor tce = table.getCellEditor();
                    if (tce != null) {
                        tce.stopCellEditing();
                    }
                }
                return;
            }
            if ((pressedCol == -1) || (pressedRow == -1)) {
                return;
            }

            boolean dragEnabled = table.getDragEnabled();

            if (!dragEnabled && !isFileList && table.editCellAt(pressedRow, pressedCol, e)) {
                setDispatchComponent(e);
                repostEvent(e);
            }

            CellEditor editor = table.getCellEditor();
            if (dragEnabled || editor == null || editor.shouldSelectCell(e)) {
                table.changeSelection(pressedRow, pressedCol,
                        BasicGraphicsUtils.isMenuShortcutKeyDown(e),
                        e.isShiftDown());
            }
        }

        public void valueChanged(ListSelectionEvent e) {
            if (timer != null) {
                timer.stop();
                timer = null;
            }
        }

        public void actionPerformed(ActionEvent ae) {
            table.editCellAt(pressedRow, pressedCol, null);
            Component editorComponent = table.getEditorComponent();
            if (editorComponent != null && !editorComponent.hasFocus()) {
                SwingUtilities2.compositeRequestFocus(editorComponent);
            }
            return;
        }

        private void maybeStartTimer() {
            if (!shouldStartTimer) {
                return;
            }

            if (timer == null) {
                timer = new Timer(1200, this);
                timer.setRepeats(false);
            }

            timer.start();
        }

        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities2.shouldIgnore(e, table)) {
                return;
            }

            if (table.getDragEnabled()) {
                mouseReleasedDND(e);
            } else {
                if (isFileList) {
                    maybeStartTimer();
                }
            }

            pressedEvent = null;
            repostEvent(e);
            dispatchComponent = null;
            setValueIsAdjusting(false);
        }

        private void mouseReleasedDND(MouseEvent e) {
            MouseEvent me = DragRecognitionSupport.mouseReleased(e);
            if (me != null) {
                SwingUtilities2.adjustFocus(table);
                if (!dragPressDidSelection) {
                    adjustSelection(me);
                }
            }

            if (!dragStarted) {
                if (isFileList) {
                    maybeStartTimer();
                    return;
                }

                Point p = e.getPoint();

                if (pressedEvent != null &&
                        table.rowAtPoint(p) == pressedRow &&
                        table.columnAtPoint(p) == pressedCol &&
                        table.editCellAt(pressedRow, pressedCol, pressedEvent)) {

                    setDispatchComponent(pressedEvent);
                    repostEvent(pressedEvent);

                    CellEditor ce = table.getCellEditor();
                    if (ce != null) {
                        ce.shouldSelectCell(pressedEvent);
                    }
                }
            }
        }

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {}

        public void mouseMoved(MouseEvent e) {}

        public void dragStarting(MouseEvent me) {
            dragStarted = true;

            if (BasicGraphicsUtils.isMenuShortcutKeyDown(me) && isFileList) {
                table.getSelectionModel().addSelectionInterval(pressedRow,
                                                               pressedRow);
                table.getColumnModel().getSelectionModel().
                    addSelectionInterval(pressedCol, pressedCol);
            }

            pressedEvent = null;
        }

        public void mouseDragged(MouseEvent e) {
            if (SwingUtilities2.shouldIgnore(e, table)) {
                return;
            }

            if (table.getDragEnabled() &&
                    (DragRecognitionSupport.mouseDragged(e, this) || dragStarted)) {

                return;
            }

            repostEvent(e);

            if (isFileList || table.isEditing()) {
                return;
            }

            Point p = e.getPoint();
            int row = table.rowAtPoint(p);
            int column = table.columnAtPoint(p);
            if ((column == -1) || (row == -1)) {
                return;
            }

            table.changeSelection(row, column,
                    BasicGraphicsUtils.isMenuShortcutKeyDown(e), true);
        }


        public void propertyChange(PropertyChangeEvent event) {
            String changeName = event.getPropertyName();

            if ("componentOrientation" == changeName) {
                InputMap inputMap = getInputMap(
                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

                SwingUtilities.replaceUIInputMap(table,
                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                    inputMap);

                JTableHeader header = table.getTableHeader();
                if (header != null) {
                    header.setComponentOrientation(
                            (ComponentOrientation)event.getNewValue());
                }
            } else if ("dropLocation" == changeName) {
                JTable.DropLocation oldValue = (JTable.DropLocation)event.getOldValue();
                repaintDropLocation(oldValue);
                repaintDropLocation(table.getDropLocation());
            } else if ("Table.isFileList" == changeName) {
                isFileList = Boolean.TRUE.equals(table.getClientProperty("Table.isFileList"));
                table.revalidate();
                table.repaint();
                if (isFileList) {
                    table.getSelectionModel().addListSelectionListener(getHandler());
                } else {
                    table.getSelectionModel().removeListSelectionListener(getHandler());
                    timer = null;
                }
            } else if ("selectionModel" == changeName) {
                if (isFileList) {
                    ListSelectionModel old = (ListSelectionModel)event.getOldValue();
                    old.removeListSelectionListener(getHandler());
                    table.getSelectionModel().addListSelectionListener(getHandler());
                }
            }
        }

        private void repaintDropLocation(JTable.DropLocation loc) {
            if (loc == null) {
                return;
            }

            if (!loc.isInsertRow() && !loc.isInsertColumn()) {
                Rectangle rect = table.getCellRect(loc.getRow(), loc.getColumn(), false);
                if (rect != null) {
                    table.repaint(rect);
                }
                return;
            }

            if (loc.isInsertRow()) {
                Rectangle rect = extendRect(getHDropLineRect(loc), true);
                if (rect != null) {
                    table.repaint(rect);
                }
            }

            if (loc.isInsertColumn()) {
                Rectangle rect = extendRect(getVDropLineRect(loc), false);
                if (rect != null) {
                    table.repaint(rect);
                }
            }
        }
    }



    private boolean pointOutsidePrefSize(int row, int column, Point p) {
        if (!isFileList) {
            return false;
        }

        return SwingUtilities2.pointOutsidePrefSize(table, row, column, p);
    }

private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }


    protected KeyListener createKeyListener() {
        return null;
    }


    protected FocusListener createFocusListener() {
        return getHandler();
    }


    protected MouseInputListener createMouseInputListener() {
        return getHandler();
    }

public static ComponentUI createUI(JComponent c) {
        return new BasicTableUI();
    }

public void installUI(JComponent c) {
        table = (JTable)c;

        rendererPane = new CellRendererPane();
        table.add(rendererPane);
        installDefaults();
        installDefaults2();
        installListeners();
        installKeyboardActions();
    }


    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(table, "Table.background",
                                         "Table.foreground", "Table.font");
        LookAndFeel.installProperty(table, "opaque", Boolean.TRUE);

        Color sbg = table.getSelectionBackground();
        if (sbg == null || sbg instanceof UIResource) {
            sbg = UIManager.getColor("Table.selectionBackground");
            table.setSelectionBackground(sbg != null ? sbg : UIManager.getColor("textHighlight"));
        }

        Color sfg = table.getSelectionForeground();
        if (sfg == null || sfg instanceof UIResource) {
            sfg = UIManager.getColor("Table.selectionForeground");
            table.setSelectionForeground(sfg != null ? sfg : UIManager.getColor("textHighlightText"));
        }

        Color gridColor = table.getGridColor();
        if (gridColor == null || gridColor instanceof UIResource) {
            gridColor = UIManager.getColor("Table.gridColor");
            table.setGridColor(gridColor != null ? gridColor : Color.GRAY);
        }

        Container parent = SwingUtilities.getUnwrappedParent(table);  if (parent != null) {
            parent = parent.getParent();  if (parent != null && parent instanceof JScrollPane) {
                LookAndFeel.installBorder((JScrollPane)parent, "Table.scrollPaneBorder");
            }
        }

        isFileList = Boolean.TRUE.equals(table.getClientProperty("Table.isFileList"));
    }

    private void installDefaults2() {
        TransferHandler th = table.getTransferHandler();
        if (th == null || th instanceof UIResource) {
            table.setTransferHandler(defaultTransferHandler);
            if (table.getDropTarget() instanceof UIResource) {
                table.setDropTarget(null);
            }
        }
    }


    protected void installListeners() {
        focusListener = createFocusListener();
        keyListener = createKeyListener();
        mouseInputListener = createMouseInputListener();

        table.addFocusListener(focusListener);
        table.addKeyListener(keyListener);
        table.addMouseListener(mouseInputListener);
        table.addMouseMotionListener(mouseInputListener);
        table.addPropertyChangeListener(getHandler());
        if (isFileList) {
            table.getSelectionModel().addListSelectionListener(getHandler());
        }
    }


    protected void installKeyboardActions() {
        LazyActionMap.installLazyActionMap(table, BasicTableUI.class,
                "Table.actionMap");

        InputMap inputMap = getInputMap(JComponent.
                                        WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        SwingUtilities.replaceUIInputMap(table,
                                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                                inputMap);
    }

    InputMap getInputMap(int condition) {
        if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            InputMap keyMap =
                (InputMap)DefaultLookup.get(table, this,
                                            "Table.ancestorInputMap");
            InputMap rtlKeyMap;

            if (table.getComponentOrientation().isLeftToRight() ||
                ((rtlKeyMap = (InputMap)DefaultLookup.get(table, this,
                                            "Table.ancestorInputMap.RightToLeft")) == null)) {
                return keyMap;
            } else {
                rtlKeyMap.setParent(keyMap);
                return rtlKeyMap;
            }
        }
        return null;
    }

    static void loadActionMap(LazyActionMap map) {
        map.put(new Actions(Actions.NEXT_COLUMN, 1, 0,
                false, false));
        map.put(new Actions(Actions.NEXT_COLUMN_CHANGE_LEAD, 1, 0,
                false, false));
        map.put(new Actions(Actions.PREVIOUS_COLUMN, -1, 0,
                false, false));
        map.put(new Actions(Actions.PREVIOUS_COLUMN_CHANGE_LEAD, -1, 0,
                false, false));
        map.put(new Actions(Actions.NEXT_ROW, 0, 1,
                false, false));
        map.put(new Actions(Actions.NEXT_ROW_CHANGE_LEAD, 0, 1,
                false, false));
        map.put(new Actions(Actions.PREVIOUS_ROW, 0, -1,
                false, false));
        map.put(new Actions(Actions.PREVIOUS_ROW_CHANGE_LEAD, 0, -1,
                false, false));
        map.put(new Actions(Actions.NEXT_COLUMN_EXTEND_SELECTION,
                1, 0, true, false));
        map.put(new Actions(Actions.PREVIOUS_COLUMN_EXTEND_SELECTION,
                -1, 0, true, false));
        map.put(new Actions(Actions.NEXT_ROW_EXTEND_SELECTION,
                0, 1, true, false));
        map.put(new Actions(Actions.PREVIOUS_ROW_EXTEND_SELECTION,
                0, -1, true, false));
        map.put(new Actions(Actions.SCROLL_UP_CHANGE_SELECTION,
                false, false, true, false));
        map.put(new Actions(Actions.SCROLL_DOWN_CHANGE_SELECTION,
                false, true, true, false));
        map.put(new Actions(Actions.FIRST_COLUMN,
                false, false, false, true));
        map.put(new Actions(Actions.LAST_COLUMN,
                false, true, false, true));

        map.put(new Actions(Actions.SCROLL_UP_EXTEND_SELECTION,
                true, false, true, false));
        map.put(new Actions(Actions.SCROLL_DOWN_EXTEND_SELECTION,
                true, true, true, false));
        map.put(new Actions(Actions.FIRST_COLUMN_EXTEND_SELECTION,
                true, false, false, true));
        map.put(new Actions(Actions.LAST_COLUMN_EXTEND_SELECTION,
                true, true, false, true));

        map.put(new Actions(Actions.FIRST_ROW, false, false, true, true));
        map.put(new Actions(Actions.LAST_ROW, false, true, true, true));

        map.put(new Actions(Actions.FIRST_ROW_EXTEND_SELECTION,
                true, false, true, true));
        map.put(new Actions(Actions.LAST_ROW_EXTEND_SELECTION,
                true, true, true, true));

        map.put(new Actions(Actions.NEXT_COLUMN_CELL,
                1, 0, false, true));
        map.put(new Actions(Actions.PREVIOUS_COLUMN_CELL,
                -1, 0, false, true));
        map.put(new Actions(Actions.NEXT_ROW_CELL, 0, 1, false, true));
        map.put(new Actions(Actions.PREVIOUS_ROW_CELL,
                0, -1, false, true));

        map.put(new Actions(Actions.SELECT_ALL));
        map.put(new Actions(Actions.CLEAR_SELECTION));
        map.put(new Actions(Actions.CANCEL_EDITING));
        map.put(new Actions(Actions.START_EDITING));

        map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());

        map.put(new Actions(Actions.SCROLL_LEFT_CHANGE_SELECTION,
                false, false, false, false));
        map.put(new Actions(Actions.SCROLL_RIGHT_CHANGE_SELECTION,
                false, true, false, false));
        map.put(new Actions(Actions.SCROLL_LEFT_EXTEND_SELECTION,
                true, false, false, false));
        map.put(new Actions(Actions.SCROLL_RIGHT_EXTEND_SELECTION,
                true, true, false, false));

        map.put(new Actions(Actions.ADD_TO_SELECTION));
        map.put(new Actions(Actions.TOGGLE_AND_ANCHOR));
        map.put(new Actions(Actions.EXTEND_TO));
        map.put(new Actions(Actions.MOVE_SELECTION_TO));
        map.put(new Actions(Actions.FOCUS_HEADER));
    }

public void uninstallUI(JComponent c) {
        uninstallDefaults();
        uninstallListeners();
        uninstallKeyboardActions();

        table.remove(rendererPane);
        rendererPane = null;
        table = null;
    }

    protected void uninstallDefaults() {
        if (table.getTransferHandler() instanceof UIResource) {
            table.setTransferHandler(null);
        }
    }

    protected void uninstallListeners() {
        table.removeFocusListener(focusListener);
        table.removeKeyListener(keyListener);
        table.removeMouseListener(mouseInputListener);
        table.removeMouseMotionListener(mouseInputListener);
        table.removePropertyChangeListener(getHandler());
        if (isFileList) {
            table.getSelectionModel().removeListSelectionListener(getHandler());
        }

        focusListener = null;
        keyListener = null;
        mouseInputListener = null;
        handler = null;
    }

    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIInputMap(table, JComponent.
                                   WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
        SwingUtilities.replaceUIActionMap(table, null);
    }


    public int getBaseline(JComponent c, int width, int height) {
        super.getBaseline(c, width, height);
        UIDefaults lafDefaults = UIManager.getLookAndFeelDefaults();
        Component renderer = (Component)lafDefaults.get(
                BASELINE_COMPONENT_KEY);
        if (renderer == null) {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
            renderer = tcr.getTableCellRendererComponent(
                    table, "a", false, false, -1, -1);
            lafDefaults.put(BASELINE_COMPONENT_KEY, renderer);
        }
        renderer.setFont(table.getFont());
        int rowMargin = table.getRowMargin();
        return renderer.getBaseline(Integer.MAX_VALUE, table.getRowHeight() -
                                    rowMargin) + rowMargin / 2;
    }


    public Component.BaselineResizeBehavior getBaselineResizeBehavior(
            JComponent c) {
        super.getBaselineResizeBehavior(c);
        return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
    }

private Dimension createTableSize(long width) {
        int height = 0;
        int rowCount = table.getRowCount();
        if (rowCount > 0 && table.getColumnCount() > 0) {
            Rectangle r = table.getCellRect(rowCount-1, 0, true);
            height = r.y + r.height;
        }
        long tmp = Math.abs(width);
        if (tmp > Integer.MAX_VALUE) {
            tmp = Integer.MAX_VALUE;
        }
        return new Dimension((int)tmp, height);
    }


    public Dimension getMinimumSize(JComponent c) {
        long width = 0;
        Enumeration enumeration = table.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = (TableColumn)enumeration.nextElement();
            width = width + aColumn.getMinWidth();
        }
        return createTableSize(width);
    }


    public Dimension getPreferredSize(JComponent c) {
        long width = 0;
        Enumeration enumeration = table.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = (TableColumn)enumeration.nextElement();
            width = width + aColumn.getPreferredWidth();
        }
        return createTableSize(width);
    }


    public Dimension getMaximumSize(JComponent c) {
        long width = 0;
        Enumeration enumeration = table.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = (TableColumn)enumeration.nextElement();
            width = width + aColumn.getMaxWidth();
        }
        return createTableSize(width);
    }

public void paint(Graphics g, JComponent c) {
        Rectangle clip = g.getClipBounds();

        Rectangle bounds = table.getBounds();
        bounds.x = bounds.y = 0;

        if (table.getRowCount() <= 0 || table.getColumnCount() <= 0 ||
                !bounds.intersects(clip)) {

            paintDropLines(g);
            return;
        }

        boolean ltr = table.getComponentOrientation().isLeftToRight();

        Point upperLeft = clip.getLocation();
        Point lowerRight = new Point(clip.x + clip.width - 1,
                                     clip.y + clip.height - 1);

        int rMin = table.rowAtPoint(upperLeft);
        int rMax = table.rowAtPoint(lowerRight);
        if (rMin == -1) {
            rMin = 0;
        }
        if (rMax == -1) {
            rMax = table.getRowCount()-1;
        }

        int cMin = table.columnAtPoint(ltr ? upperLeft : lowerRight);
        int cMax = table.columnAtPoint(ltr ? lowerRight : upperLeft);
        if (cMin == -1) {
            cMin = 0;
        }
        if (cMax == -1) {
            cMax = table.getColumnCount()-1;
        }

        paintGrid(g, rMin, rMax, cMin, cMax);

        paintCells(g, rMin, rMax, cMin, cMax);

        paintDropLines(g);
    }

    private void paintDropLines(Graphics g) {
        JTable.DropLocation loc = table.getDropLocation();
        if (loc == null) {
            return;
        }

        Color color = UIManager.getColor("Table.dropLineColor");
        Color shortColor = UIManager.getColor("Table.dropLineShortColor");
        if (color == null && shortColor == null) {
            return;
        }

        Rectangle rect;

        rect = getHDropLineRect(loc);
        if (rect != null) {
            int x = rect.x;
            int w = rect.width;
            if (color != null) {
                extendRect(rect, true);
                g.setColor(color);
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
            }
            if (!loc.isInsertColumn() && shortColor != null) {
                g.setColor(shortColor);
                g.fillRect(x, rect.y, w, rect.height);
            }
        }

        rect = getVDropLineRect(loc);
        if (rect != null) {
            int y = rect.y;
            int h = rect.height;
            if (color != null) {
                extendRect(rect, false);
                g.setColor(color);
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
            }
            if (!loc.isInsertRow() && shortColor != null) {
                g.setColor(shortColor);
                g.fillRect(rect.x, y, rect.width, h);
            }
        }
    }

    private Rectangle getHDropLineRect(JTable.DropLocation loc) {
        if (!loc.isInsertRow()) {
            return null;
        }

        int row = loc.getRow();
        int col = loc.getColumn();
        if (col >= table.getColumnCount()) {
            col--;
        }

        Rectangle rect = table.getCellRect(row, col, true);

        if (row >= table.getRowCount()) {
            row--;
            Rectangle prevRect = table.getCellRect(row, col, true);
            rect.y = prevRect.y + prevRect.height;
        }

        if (rect.y == 0) {
            rect.y = -1;
        } else {
            rect.y -= 2;
        }

        rect.height = 3;

        return rect;
    }

    private Rectangle getVDropLineRect(JTable.DropLocation loc) {
        if (!loc.isInsertColumn()) {
            return null;
        }

        boolean ltr = table.getComponentOrientation().isLeftToRight();
        int col = loc.getColumn();
        Rectangle rect = table.getCellRect(loc.getRow(), col, true);

        if (col >= table.getColumnCount()) {
            col--;
            rect = table.getCellRect(loc.getRow(), col, true);
            if (ltr) {
                rect.x = rect.x + rect.width;
            }
        } else if (!ltr) {
            rect.x = rect.x + rect.width;
        }

        if (rect.x == 0) {
            rect.x = -1;
        } else {
            rect.x -= 2;
        }

        rect.width = 3;

        return rect;
    }

    private Rectangle extendRect(Rectangle rect, boolean horizontal) {
        if (rect == null) {
            return rect;
        }

        if (horizontal) {
            rect.x = 0;
            rect.width = table.getWidth();
        } else {
            rect.y = 0;

            if (table.getRowCount() != 0) {
                Rectangle lastRect = table.getCellRect(table.getRowCount() - 1, 0, true);
                rect.height = lastRect.y + lastRect.height;
            } else {
                rect.height = table.getHeight();
            }
        }

        return rect;
    }


    private void paintGrid(Graphics g, int rMin, int rMax, int cMin, int cMax) {
        g.setColor(table.getGridColor());

        Rectangle minCell = table.getCellRect(rMin, cMin, true);
        Rectangle maxCell = table.getCellRect(rMax, cMax, true);
        Rectangle damagedArea = minCell.union( maxCell );

        if (table.getShowHorizontalLines()) {
            int tableWidth = damagedArea.x + damagedArea.width;
            int y = damagedArea.y;
            for (int row = rMin; row <= rMax; row++) {
                y += table.getRowHeight(row);
                g.drawLine(damagedArea.x, y - 1, tableWidth - 1, y - 1);
            }
        }
        if (table.getShowVerticalLines()) {
            TableColumnModel cm = table.getColumnModel();
            int tableHeight = damagedArea.y + damagedArea.height;
            int x;
            if (table.getComponentOrientation().isLeftToRight()) {
                x = damagedArea.x;
                for (int column = cMin; column <= cMax; column++) {
                    int w = cm.getColumn(column).getWidth();
                    x += w;
                    g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
                }
            } else {
                x = damagedArea.x;
                for (int column = cMax; column >= cMin; column--) {
                    int w = cm.getColumn(column).getWidth();
                    x += w;
                    g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
                }
            }
        }
    }

    private int viewIndexForColumn(TableColumn aColumn) {
        TableColumnModel cm = table.getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++) {
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        return -1;
    }

    private void paintCells(Graphics g, int rMin, int rMax, int cMin, int cMax) {
        JTableHeader header = table.getTableHeader();
        TableColumn draggedColumn = (header == null) ? null : header.getDraggedColumn();

        TableColumnModel cm = table.getColumnModel();
        int columnMargin = cm.getColumnMargin();

        Rectangle cellRect;
        TableColumn aColumn;
        int columnWidth;
        if (table.getComponentOrientation().isLeftToRight()) {
            for(int row = rMin; row <= rMax; row++) {
                cellRect = table.getCellRect(row, cMin, false);
                for(int column = cMin; column <= cMax; column++) {
                    aColumn = cm.getColumn(column);
                    columnWidth = aColumn.getWidth();
                    cellRect.width = columnWidth - columnMargin;
                    if (aColumn != draggedColumn) {
                        paintCell(g, cellRect, row, column);
                    }
                    cellRect.x += columnWidth;
                }
            }
        } else {
            for(int row = rMin; row <= rMax; row++) {
                cellRect = table.getCellRect(row, cMin, false);
                aColumn = cm.getColumn(cMin);
                if (aColumn != draggedColumn) {
                    columnWidth = aColumn.getWidth();
                    cellRect.width = columnWidth - columnMargin;
                    paintCell(g, cellRect, row, cMin);
                }
                for(int column = cMin+1; column <= cMax; column++) {
                    aColumn = cm.getColumn(column);
                    columnWidth = aColumn.getWidth();
                    cellRect.width = columnWidth - columnMargin;
                    cellRect.x -= columnWidth;
                    if (aColumn != draggedColumn) {
                        paintCell(g, cellRect, row, column);
                    }
                }
            }
        }

        if (draggedColumn != null) {
            paintDraggedArea(g, rMin, rMax, draggedColumn, header.getDraggedDistance());
        }

        rendererPane.removeAll();
    }

    private void paintDraggedArea(Graphics g, int rMin, int rMax, TableColumn draggedColumn, int distance) {
        int draggedColumnIndex = viewIndexForColumn(draggedColumn);

        Rectangle minCell = table.getCellRect(rMin, draggedColumnIndex, true);
        Rectangle maxCell = table.getCellRect(rMax, draggedColumnIndex, true);

        Rectangle vacatedColumnRect = minCell.union(maxCell);

        g.setColor(table.getParent().getBackground());
        g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
                   vacatedColumnRect.width, vacatedColumnRect.height);

        vacatedColumnRect.x += distance;

        g.setColor(table.getBackground());
        g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
                   vacatedColumnRect.width, vacatedColumnRect.height);

        if (table.getShowVerticalLines()) {
            g.setColor(table.getGridColor());
            int x1 = vacatedColumnRect.x;
            int y1 = vacatedColumnRect.y;
            int x2 = x1 + vacatedColumnRect.width - 1;
            int y2 = y1 + vacatedColumnRect.height - 1;
            g.drawLine(x1-1, y1, x1-1, y2);
            g.drawLine(x2, y1, x2, y2);
        }

        for(int row = rMin; row <= rMax; row++) {
            Rectangle r = table.getCellRect(row, draggedColumnIndex, false);
            r.x += distance;
            paintCell(g, r, row, draggedColumnIndex);

            if (table.getShowHorizontalLines()) {
                g.setColor(table.getGridColor());
                Rectangle rcr = table.getCellRect(row, draggedColumnIndex, true);
                rcr.x += distance;
                int x1 = rcr.x;
                int y1 = rcr.y;
                int x2 = x1 + rcr.width - 1;
                int y2 = y1 + rcr.height - 1;
                g.drawLine(x1, y2, x2, y2);
            }
        }
    }

    private void paintCell(Graphics g, Rectangle cellRect, int row, int column) {
        if (table.isEditing() && table.getEditingRow()==row &&
                                 table.getEditingColumn()==column) {
            Component component = table.getEditorComponent();
            component.setBounds(cellRect);
            component.validate();
        }
        else {
            TableCellRenderer renderer = table.getCellRenderer(row, column);
            Component component = table.prepareRenderer(renderer, row, column);
            rendererPane.paintComponent(g, component, table, cellRect.x, cellRect.y,
                                        cellRect.width, cellRect.height, true);
        }
    }

    private static int getAdjustedLead(JTable table,
                                       boolean row,
                                       ListSelectionModel model) {

        int index = model.getLeadSelectionIndex();
        int compare = row ? table.getRowCount() : table.getColumnCount();
        return index < compare ? index : -1;
    }

    private static int getAdjustedLead(JTable table, boolean row) {
        return row ? getAdjustedLead(table, row, table.getSelectionModel())
                   : getAdjustedLead(table, row, table.getColumnModel().getSelectionModel());
    }


    private static final TransferHandler defaultTransferHandler = new TableTransferHandler();

    static class TableTransferHandler extends TransferHandler implements UIResource {


        protected Transferable createTransferable(JComponent c) {
            if (c instanceof JTable) {
                JTable table = (JTable) c;
                int[] rows;
                int[] cols;

                if (!table.getRowSelectionAllowed() && !table.getColumnSelectionAllowed()) {
                    return null;
                }

                if (!table.getRowSelectionAllowed()) {
                    int rowCount = table.getRowCount();

                    rows = new int[rowCount];
                    for (int counter = 0; counter < rowCount; counter++) {
                        rows[counter] = counter;
                    }
                } else {
                    rows = table.getSelectedRows();
                }

                if (!table.getColumnSelectionAllowed()) {
                    int colCount = table.getColumnCount();

                    cols = new int[colCount];
                    for (int counter = 0; counter < colCount; counter++) {
                        cols[counter] = counter;
                    }
                } else {
                    cols = table.getSelectedColumns();
                }

                if (rows == null || cols == null || rows.length == 0 || cols.length == 0) {
                    return null;
                }

                StringBuffer plainBuf = new StringBuffer();
                StringBuffer htmlBuf = new StringBuffer();

                htmlBuf.append("<html>\n<body>\n<table>\n");

                for (int row = 0; row < rows.length; row++) {
                    htmlBuf.append("<tr>\n");
                    for (int col = 0; col < cols.length; col++) {
                        Object obj = table.getValueAt(rows[row], cols[col]);
                        String val = ((obj == null) ? "" : obj.toString());
                        plainBuf.append(val + "\t");
                        htmlBuf.append("  <td>" + val + "</td>\n");
                    }
                    plainBuf.deleteCharAt(plainBuf.length() - 1).append("\n");
                    htmlBuf.append("</tr>\n");
                }

                plainBuf.deleteCharAt(plainBuf.length() - 1);
                htmlBuf.append("</table>\n</body>\n</html>");

                return new BasicTransferable(plainBuf.toString(), htmlBuf.toString());
            }

            return null;
        }

        public int getSourceActions(JComponent c) {
            return COPY;
        }

    }
}