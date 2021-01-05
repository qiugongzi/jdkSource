
    static public abstract class LayerPainter implements Highlighter.HighlightPainter {
        public abstract Shape paintLayer(Graphics g, int p0, int p1,
                                        Shape viewBounds,JTextComponent editor,
                                        View view);
    }
}
