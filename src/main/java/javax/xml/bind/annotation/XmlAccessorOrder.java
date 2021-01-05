

@Inherited @Retention(RUNTIME) @Target({PACKAGE, TYPE})
public @interface XmlAccessorOrder {
        XmlAccessOrder value() default XmlAccessOrder.UNDEFINED;
}
