
@Target({ElementType.TYPE})
@Retention(RUNTIME)
public @interface XmlSeeAlso {
    Class[] value();
}
