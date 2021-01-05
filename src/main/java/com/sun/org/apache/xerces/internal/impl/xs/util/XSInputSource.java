
public final class XSInputSource extends XMLInputSource {

    private SchemaGrammar[] fGrammars;
    private XSObject[] fComponents;

    public XSInputSource(SchemaGrammar[] grammars) {
        super(null, null, null);
        fGrammars = grammars;
        fComponents = null;
    }

    public XSInputSource(XSObject[] component) {
        super(null, null, null);
        fGrammars = null;
        fComponents = component;
    }

    public SchemaGrammar[] getGrammars() {
        return fGrammars;
    }

    public void setGrammars(SchemaGrammar[] grammars) {
        fGrammars = grammars;
    }

    public XSObject[] getComponents() {
        return fComponents;
    }

    public void setComponents(XSObject[] components) {
        fComponents = components;
    }
}
