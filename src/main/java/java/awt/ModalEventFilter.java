
package java.awt;

import java.awt.event.*;

import sun.awt.AppContext;

abstract class ModalEventFilter implements EventFilter {

    protected Dialog modalDialog;
    protected boolean disabled;

    protected ModalEventFilter(Dialog modalDialog) {
        this.modalDialog = modalDialog;
        disabled = false;
    }

    Dialog getModalDialog() {
        return modalDialog;
    }

    public FilterAction acceptEvent(AWTEvent event) {
        if (disabled || !modalDialog.isVisible()) {
            return FilterAction.ACCEPT;
        }
        int eventID = event.getID();
        if ((eventID >= MouseEvent.MOUSE_FIRST &&
             eventID <= MouseEvent.MOUSE_LAST) ||
            (eventID >= ActionEvent.ACTION_FIRST &&
             eventID <= ActionEvent.ACTION_LAST) ||
            eventID == WindowEvent.WINDOW_CLOSING)
        {
            Object o = event.getSource();
            if (o instanceof sun.awt.ModalExclude) {
                } else if (o instanceof Component) {
                Component c = (Component)o;
                while ((c != null) && !(c instanceof Window)) {
                    c = c.getParent_NoClientCode();
                }
                if (c != null) {
                    return acceptWindow((Window)c);
                }
            }
        }
        return FilterAction.ACCEPT;
    }

    protected abstract FilterAction acceptWindow(Window w);

    void disable() {
        disabled = true;
    }

    int compareTo(ModalEventFilter another) {
        Dialog anotherDialog = another.getModalDialog();
        Component c = modalDialog;
        while (c != null) {
            if (c == anotherDialog) {
                return 1;
            }
            c = c.getParent_NoClientCode();
        }
        c = anotherDialog;
        while (c != null) {
            if (c == modalDialog) {
                return -1;
            }
            c = c.getParent_NoClientCode();
        }
        Dialog blocker = modalDialog.getModalBlocker();
        while (blocker != null) {
            if (blocker == anotherDialog) {
                return -1;
            }
            blocker = blocker.getModalBlocker();
        }
        blocker = anotherDialog.getModalBlocker();
        while (blocker != null) {
            if (blocker == modalDialog) {
                return 1;
            }
            blocker = blocker.getModalBlocker();
        }
        return modalDialog.getModalityType().compareTo(anotherDialog.getModalityType());
    }

    static ModalEventFilter createFilterForDialog(Dialog modalDialog) {
        switch (modalDialog.getModalityType()) {
            case DOCUMENT_MODAL: return new DocumentModalEventFilter(modalDialog);
            case APPLICATION_MODAL: return new ApplicationModalEventFilter(modalDialog);
            case TOOLKIT_MODAL: return new ToolkitModalEventFilter(modalDialog);
        }
        return null;
    }

    private static class ToolkitModalEventFilter extends ModalEventFilter {

        private AppContext appContext;

        ToolkitModalEventFilter(Dialog modalDialog) {
            super(modalDialog);
            appContext = modalDialog.appContext;
        }

        protected FilterAction acceptWindow(Window w) {
            if (w.isModalExcluded(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE)) {
                return FilterAction.ACCEPT;
            }
            if (w.appContext != appContext) {
                return FilterAction.REJECT;
            }
            while (w != null) {
                if (w == modalDialog) {
                    return FilterAction.ACCEPT_IMMEDIATELY;
                }
                w = w.getOwner();
            }
            return FilterAction.REJECT;
        }
    }

    private static class ApplicationModalEventFilter extends ModalEventFilter {

        private AppContext appContext;

        ApplicationModalEventFilter(Dialog modalDialog) {
            super(modalDialog);
            appContext = modalDialog.appContext;
        }

        protected FilterAction acceptWindow(Window w) {
            if (w.isModalExcluded(Dialog.ModalExclusionType.APPLICATION_EXCLUDE)) {
                return FilterAction.ACCEPT;
            }
            if (w.appContext == appContext) {
                while (w != null) {
                    if (w == modalDialog) {
                        return FilterAction.ACCEPT_IMMEDIATELY;
                    }
                    w = w.getOwner();
                }
                return FilterAction.REJECT;
            }
            return FilterAction.ACCEPT;
        }
    }

    private static class DocumentModalEventFilter extends ModalEventFilter {

        private Window documentRoot;

        DocumentModalEventFilter(Dialog modalDialog) {
            super(modalDialog);
            documentRoot = modalDialog.getDocumentRoot();
        }

        protected FilterAction acceptWindow(Window w) {
            if (w.isModalExcluded(Dialog.ModalExclusionType.APPLICATION_EXCLUDE)) {
                Window w1 = modalDialog.getOwner();
                while (w1 != null) {
                    if (w1 == w) {
                        return FilterAction.REJECT;
                    }
                    w1 = w1.getOwner();
                }
                return FilterAction.ACCEPT;
            }
            while (w != null) {
                if (w == modalDialog) {
                    return FilterAction.ACCEPT_IMMEDIATELY;
                }
                if (w == documentRoot) {
                    return FilterAction.REJECT;
                }
                w = w.getOwner();
            }
            return FilterAction.ACCEPT;
        }
    }
}
