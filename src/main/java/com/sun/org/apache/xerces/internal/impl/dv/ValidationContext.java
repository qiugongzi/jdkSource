
public interface ValidationContext {

    public boolean needFacetChecking();


    public boolean needExtraChecking();


    public boolean needToNormalize();


    public boolean useNamespaces();


    public boolean isEntityDeclared (String name);
    public boolean isEntityUnparsed (String name);


    public boolean isIdDeclared (String name);
    public void    addId(String name);


    public void addIdRef(String name);


    public String getSymbol (String symbol);


    public String getURI(String prefix);


    public Locale getLocale();

}
