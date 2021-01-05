
    @Override
    protected Rectangle computePopupBounds(int px, int py, int pw, int ph) {
        ComboBoxUI ui = comboBox.getUI();
        if (ui instanceof SynthComboBoxUI) {
            SynthComboBoxUI sui = (SynthComboBoxUI) ui;
            if (sui.popupInsets != null) {
                Insets i = sui.popupInsets;
                return super.computePopupBounds(
                        px + i.left,
                        py + i.top,
                        pw - i.left - i.right,
                        ph - i.top - i.bottom);
            }
        }
        return super.computePopupBounds(px, py, pw, ph);
    }
}
