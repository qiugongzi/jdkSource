


class NamedWeakReference<T> extends java.lang.ref.WeakReference<T> {

    private final String name;

    NamedWeakReference(T referent, String name) {
        super(referent);
        this.name = name;
    }

    String getName() {
        return name;
    }
}
