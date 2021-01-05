
final class ReadOnlyGrammarPool implements XMLGrammarPool {

    private final XMLGrammarPool core;

    public ReadOnlyGrammarPool( XMLGrammarPool pool ) {
        this.core = pool;
    }

    public void cacheGrammars(String grammarType, Grammar[] grammars) {

    }

    public void clear() {

    }

    public void lockPool() {

    }

    public Grammar retrieveGrammar(XMLGrammarDescription desc) {
        return core.retrieveGrammar(desc);
    }

    public Grammar[] retrieveInitialGrammarSet(String grammarType) {
        return core.retrieveInitialGrammarSet(grammarType);
    }

    public void unlockPool() {

    }

}
