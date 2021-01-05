
public interface CollatorFactory {

    public Collator getCollator(String lang, String country);
    public Collator getCollator(Locale locale);
}
