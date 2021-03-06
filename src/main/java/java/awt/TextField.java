
package java.awt;

import java.awt.peer.TextFieldPeer;
import java.awt.event.*;
import java.util.EventListener;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import javax.accessibility.*;



public class TextField extends TextComponent {


    int columns;


    char echoChar;

    transient ActionListener actionListener;

    private static final String base = "textfield";
    private static int nameCounter = 0;


    private static final long serialVersionUID = -2966288784432217853L;


    private static native void initIDs();

    static {

        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
    }


    public TextField() throws HeadlessException {
        this("", 0);
    }


    public TextField(String text) throws HeadlessException {
        this(text, (text != null) ? text.length() : 0);
    }


    public TextField(int columns) throws HeadlessException {
        this("", columns);
    }


    public TextField(String text, int columns) throws HeadlessException {
        super(text);
        this.columns = (columns >= 0) ? columns : 0;
    }


    String constructComponentName() {
        synchronized (TextField.class) {
            return base + nameCounter++;
        }
    }


    public void addNotify() {
        synchronized (getTreeLock()) {
            if (peer == null)
                peer = getToolkit().createTextField(this);
            super.addNotify();
        }
    }


    public char getEchoChar() {
        return echoChar;
    }


    public void setEchoChar(char c) {
        setEchoCharacter(c);
    }


    @Deprecated
    public synchronized void setEchoCharacter(char c) {
        if (echoChar != c) {
            echoChar = c;
            TextFieldPeer peer = (TextFieldPeer)this.peer;
            if (peer != null) {
                peer.setEchoChar(c);
            }
        }
    }


    public void setText(String t) {
        super.setText(t);

        invalidateIfValid();
    }


    public boolean echoCharIsSet() {
        return echoChar != 0;
    }


    public int getColumns() {
        return columns;
    }


    public void setColumns(int columns) {
        int oldVal;
        synchronized (this) {
            oldVal = this.columns;
            if (columns < 0) {
                throw new IllegalArgumentException("columns less than zero.");
            }
            if (columns != oldVal) {
                this.columns = columns;
            }
        }

        if (columns != oldVal) {
            invalidate();
        }
    }


    public Dimension getPreferredSize(int columns) {
        return preferredSize(columns);
    }


    @Deprecated
    public Dimension preferredSize(int columns) {
        synchronized (getTreeLock()) {
            TextFieldPeer peer = (TextFieldPeer)this.peer;
            return (peer != null) ?
                       peer.getPreferredSize(columns) :
                       super.preferredSize();
        }
    }


    public Dimension getPreferredSize() {
        return preferredSize();
    }


    @Deprecated
    public Dimension preferredSize() {
        synchronized (getTreeLock()) {
            return (columns > 0) ?
                       preferredSize(columns) :
                       super.preferredSize();
        }
    }


    public Dimension getMinimumSize(int columns) {
        return minimumSize(columns);
    }


    @Deprecated
    public Dimension minimumSize(int columns) {
        synchronized (getTreeLock()) {
            TextFieldPeer peer = (TextFieldPeer)this.peer;
            return (peer != null) ?
                       peer.getMinimumSize(columns) :
                       super.minimumSize();
        }
    }


    public Dimension getMinimumSize() {
        return minimumSize();
    }


    @Deprecated
    public Dimension minimumSize() {
        synchronized (getTreeLock()) {
            return (columns > 0) ?
                       minimumSize(columns) :
                       super.minimumSize();
        }
    }


    public synchronized void addActionListener(ActionListener l) {
        if (l == null) {
            return;
        }
        actionListener = AWTEventMulticaster.add(actionListener, l);
        newEventsOnly = true;
    }


    public synchronized void removeActionListener(ActionListener l) {
        if (l == null) {
            return;
        }
        actionListener = AWTEventMulticaster.remove(actionListener, l);
    }


    public synchronized ActionListener[] getActionListeners() {
        return getListeners(ActionListener.class);
    }


    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        EventListener l = null;
        if  (listenerType == ActionListener.class) {
            l = actionListener;
        } else {
            return super.getListeners(listenerType);
        }
        return AWTEventMulticaster.getListeners(l, listenerType);
    }

    boolean eventEnabled(AWTEvent e) {
        if (e.id == ActionEvent.ACTION_PERFORMED) {
            if ((eventMask & AWTEvent.ACTION_EVENT_MASK) != 0 ||
                actionListener != null) {
                return true;
            }
            return false;
        }
        return super.eventEnabled(e);
    }


    protected void processEvent(AWTEvent e) {
        if (e instanceof ActionEvent) {
            processActionEvent((ActionEvent)e);
            return;
        }
        super.processEvent(e);
    }


    protected void processActionEvent(ActionEvent e) {
        ActionListener listener = actionListener;
        if (listener != null) {
            listener.actionPerformed(e);
        }
    }


    protected String paramString() {
        String str = super.paramString();
        if (echoChar != 0) {
            str += ",echo=" + echoChar;
        }
        return str;
    }




    private int textFieldSerializedDataVersion = 1;


    private void writeObject(ObjectOutputStream s)
      throws IOException
    {
        s.defaultWriteObject();

        AWTEventMulticaster.save(s, actionListenerK, actionListener);
        s.writeObject(null);
    }


    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException, HeadlessException
    {
        s.defaultReadObject();

        if (columns < 0) {
            columns = 0;
        }

        Object keyOrNull;
        while(null != (keyOrNull = s.readObject())) {
            String key = ((String)keyOrNull).intern();

            if (actionListenerK == key) {
                addActionListener((ActionListener)(s.readObject()));
            } else {
                s.readObject();
            }
        }
    }


public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTTextField();
        }
        return accessibleContext;
    }


    protected class AccessibleAWTTextField extends AccessibleAWTTextComponent
    {

        private static final long serialVersionUID = 6219164359235943158L;


        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
            states.add(AccessibleState.SINGLE_LINE);
            return states;
        }
    }

}
