
    public Popup getPopup(JPopupMenu popup, int x, int y) {
        PopupFactory popupFactory = PopupFactory.getSharedInstance();

        return popupFactory.getPopup(popup.getInvoker(), popup, x, y);
    }
}
