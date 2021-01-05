
    @Deprecated
    public java.rmi.server.RemoteCall newCall(RemoteObject obj,
            java.rmi.server.Operation[] op, int opnum,
                              long hash) throws RemoteException {
        return ref.newCall(obj, op, opnum, hash);
    }

    public boolean remoteEquals(RemoteRef obj) {
        return ref.remoteEquals(obj);
    }

    public int remoteHashCode() {
        return ref.remoteHashCode();
    }

    public String remoteToString() {
        return ref.remoteToString();
    }

    protected RemoteRef ref;
}
