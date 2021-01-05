
    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY)
    {
        int mnemIndex = l.getDisplayedMnemonicIndex();
        g.setColor(UIManager.getColor("Label.disabledForeground"));
        SwingUtilities2.drawStringUnderlineCharAt(l, g, s, mnemIndex,
                                                   textX, textY);
    }
}
