
    public UnsupportedFlavorException(DataFlavor flavor) {
        super((flavor != null) ? flavor.getHumanPresentableName() : null);
    }
}
