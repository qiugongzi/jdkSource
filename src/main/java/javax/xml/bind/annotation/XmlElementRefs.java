
@Retention(RUNTIME)
@Target({FIELD,METHOD})
public @interface XmlElementRefs {
    XmlElementRef[] value();
}
