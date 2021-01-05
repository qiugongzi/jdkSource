
    public final void setEntityState(EntityState state) {
        for (int i = fVSs.size()-1; i >= 0; i--) {
            ((ValidationState)fVSs.elementAt(i)).setEntityState(state);
        }
    }

    public final void setGrammarFound(boolean grammar){
        fGrammarFound = grammar;
    }

    public final boolean isGrammarFound(){
        return fGrammarFound;
    }

    public final void setCachedDTD(boolean cachedDTD) {
        fCachedDTD = cachedDTD;
    }

    public final boolean isCachedDTD() {
        return fCachedDTD;
    }


    public final void reset (){
        fVSs.removeAllElements();
        fGrammarFound = false;
        fCachedDTD = false;
    }
}
