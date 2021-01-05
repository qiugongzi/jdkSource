
    void connect( ORB orb ) throws RemoteException ;

    boolean isLocal() ;

    OutputStream request( String operation, boolean responseExpected ) ;
}
