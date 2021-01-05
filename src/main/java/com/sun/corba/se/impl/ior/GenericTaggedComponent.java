
    public org.omg.IOP.TaggedComponent getIOPComponent( ORB orb )
    {
        return new org.omg.IOP.TaggedComponent( getId(),
            getData() ) ;
    }
}
