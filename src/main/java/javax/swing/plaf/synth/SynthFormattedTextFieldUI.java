
    @Override
    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintFormattedTextFieldBorder(context, g, x, y,
                                                           w, h);
    }
}
