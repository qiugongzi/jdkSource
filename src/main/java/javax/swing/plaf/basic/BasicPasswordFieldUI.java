

    ActionMap createActionMap() {
        ActionMap map = super.createActionMap();
        if (map.get(DefaultEditorKit.selectWordAction) != null) {
            Action a = map.get(DefaultEditorKit.selectLineAction);
            if (a != null) {
                map.remove(DefaultEditorKit.selectWordAction);
                map.put(DefaultEditorKit.selectWordAction, a);
            }
        }
        return map;
    }

}
