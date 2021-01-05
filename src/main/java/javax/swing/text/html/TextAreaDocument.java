
    void storeInitialText() {
        try {
            initialText = getText(0, getLength());
        } catch (BadLocationException e) {
        }
    }
}
