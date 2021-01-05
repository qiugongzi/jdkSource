

package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class ResolverFragment extends ResourceResolverSpi {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(ResolverFragment.class.getName());

    @Override
    public boolean engineIsThreadSafe() {
        return true;
    }


    public XMLSignatureInput engineResolveURI(ResourceResolverContext context)
        throws ResourceResolverException {

        Document doc = context.attr.getOwnerElement().getOwnerDocument();

        Node selectedElem = null;
        if (context.uriToResolve.equals("")) {

            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "ResolverFragment with empty URI (means complete document)");
            }
            selectedElem = doc;
        } else {

            String id = context.uriToResolve.substring(1);

            selectedElem = doc.getElementById(id);
            if (selectedElem == null) {
                Object exArgs[] = { id };
                throw new ResourceResolverException(
                    "signature.Verification.MissingID", exArgs, context.attr, context.baseUri
                );
            }
            if (context.secureValidation) {
                Element start = context.attr.getOwnerDocument().getDocumentElement();
                if (!XMLUtils.protectAgainstWrappingAttack(start, id)) {
                    Object exArgs[] = { id };
                    throw new ResourceResolverException(
                        "signature.Verification.MultipleIDs", exArgs, context.attr, context.baseUri
                    );
                }
            }
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE,
                    "Try to catch an Element with ID " + id + " and Element was " + selectedElem
                );
            }
        }

        XMLSignatureInput result = new XMLSignatureInput(selectedElem);
        result.setExcludeComments(true);

        result.setMIMEType("text/xml");
        if (context.baseUri != null && context.baseUri.length() > 0) {
            result.setSourceURI(context.baseUri.concat(context.uriToResolve));
        } else {
            result.setSourceURI(context.uriToResolve);
        }
        return result;
    }


    public boolean engineCanResolveURI(ResourceResolverContext context) {
        if (context.uriToResolve == null) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "Quick fail for null uri");
            }
            return false;
        }

        if (context.uriToResolve.equals("") ||
            ((context.uriToResolve.charAt(0) == '#') && !context.uriToResolve.startsWith("#xpointer("))
        ) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "State I can resolve reference: \"" + context.uriToResolve + "\"");
            }
            return true;
        }
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Do not seem to be able to resolve reference: \"" + context.uriToResolve + "\"");
        }
        return false;
    }

}
