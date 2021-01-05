
package javax.swing.plaf.basic;

import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.event.*;

import java.lang.reflect.Method;

import sun.reflect.misc.MethodUtil;


public class BasicComboBoxEditor implements ComboBoxEditor,FocusListener {
    protected JTextField editor;
    private Object oldValue;

    public BasicComboBoxEditor() {
        editor = createEditorComponent();
    }

    public Component getEditorComponent() {
        return editor;
    }


    protected JTextField createEditorComponent() {
        JTextField editor = new BorderlessTextField("",9);
        editor.setBorder(null);
        return editor;
    }


    public void setItem(Object anObject) {
        String text;

        if ( anObject != null )  {
            text = anObject.toString();
            if (text == null) {
                text = "";
            }
            oldValue = anObject;
        } else {
            text = "";
        }
        if (! text.equals(editor.getText())) {
            editor.setText(text);
        }
    }

    public Object getItem() {
        Object newValue = editor.getText();

        if (oldValue != null && !(oldValue instanceof String))  {
            if (newValue.equals(oldValue.toString()))  {
                return oldValue;
            } else {
                Class<?> cls = oldValue.getClass();
                try {
                    Method method = MethodUtil.getMethod(cls, "valueOf", new Class[]{String.class});
                    newValue = MethodUtil.invoke(method, oldValue, new Object[] { editor.getText()});
                } catch (Exception ex) {
                    }
            }
        }
        return newValue;
    }

    public void selectAll() {
        editor.selectAll();
        editor.requestFocus();
    }

    public void focusGained(FocusEvent e) {}

    public void focusLost(FocusEvent e) {}

    public void addActionListener(ActionListener l) {
        editor.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        editor.removeActionListener(l);
    }

    static class BorderlessTextField extends JTextField {
        public BorderlessTextField(String value,int n) {
            super(value,n);
        }

        public void setText(String s) {
            if (getText().equals(s)) {
                return;
            }
            super.setText(s);
        }

        public void setBorder(Border b) {
            if (!(b instanceof UIResource)) {
                super.setBorder(b);
            }
        }
    }


    public static class UIResource extends BasicComboBoxEditor
    implements javax.swing.plaf.UIResource {
    }
}
