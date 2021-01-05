
    public ValueExp apply(ObjectName name) throws BadStringOperationException, BadBinaryOpValueExpException,
        BadAttributeValueExpException, InvalidApplicationException  {
        return this;
    }

    @Deprecated
    public void setMBeanServer(MBeanServer s) {
        super.setMBeanServer(s);
    }


 }
