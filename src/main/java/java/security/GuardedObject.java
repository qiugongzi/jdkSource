
    private void writeObject(java.io.ObjectOutputStream oos)
        throws java.io.IOException
    {
        if (guard != null)
            guard.checkGuard(object);

        oos.defaultWriteObject();
    }
}
