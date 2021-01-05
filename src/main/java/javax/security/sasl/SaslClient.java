

package javax.security.sasl;


public abstract interface SaslClient {


    public abstract String getMechanismName();


    public abstract boolean hasInitialResponse();


    public abstract byte[] evaluateChallenge(byte[] challenge)
        throws SaslException;


    public abstract boolean isComplete();


    public abstract byte[] unwrap(byte[] incoming, int offset, int len)
        throws SaslException;


    public abstract byte[] wrap(byte[] outgoing, int offset, int len)
        throws SaslException;



    public abstract Object getNegotiatedProperty(String propName);


    public abstract void dispose() throws SaslException;
}
