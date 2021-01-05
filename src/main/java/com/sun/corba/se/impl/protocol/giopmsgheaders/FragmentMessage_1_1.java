
    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        super.write(ostream);
    }

    public void callback(MessageHandler handler)
        throws java.io.IOException
    {
        handler.handleInput(this);
    }
}
