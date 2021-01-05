

package javax.xml.ws.soap;

import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;



public final class AddressingFeature extends WebServiceFeature {

    public static final String ID = "http:protected boolean required;


    public enum Responses {

        ANONYMOUS,


        NON_ANONYMOUS,


        ALL
    }

    private final Responses responses;


    public AddressingFeature() {
        this(true, false, Responses.ALL);
    }


    public AddressingFeature(boolean enabled) {
        this(enabled, false, Responses.ALL);
    }


    public AddressingFeature(boolean enabled, boolean required) {
        this(enabled, required, Responses.ALL);
    }


    public AddressingFeature(boolean enabled, boolean required, Responses responses) {
        this.enabled = enabled;
        this.required = required;
        this.responses = responses;
    }


    public String getID() {
        return ID;
    }


    public boolean isRequired() {
        return required;
    }


    public Responses getResponses() {
        return responses;
    }

}
