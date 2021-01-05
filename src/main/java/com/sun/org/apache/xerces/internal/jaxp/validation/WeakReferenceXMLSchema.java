

    public synchronized XMLGrammarPool getGrammarPool() {
        XMLGrammarPool grammarPool = (XMLGrammarPool) fGrammarPool.get();


        if (grammarPool == null) {
            grammarPool = new SoftReferenceGrammarPool();
            fGrammarPool = new WeakReference(grammarPool);
        }
        return grammarPool;
    }

    public boolean isFullyComposed() {
        return false;
    }

}
