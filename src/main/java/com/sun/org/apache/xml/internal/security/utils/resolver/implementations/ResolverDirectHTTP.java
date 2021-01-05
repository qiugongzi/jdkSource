

package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;


public class ResolverDirectHTTP extends ResourceResolverSpi {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(ResolverDirectHTTP.class.getName());


    private static final String properties[] = {
                                                 "http.proxy.host", "http.proxy.port",
                                                 "http.proxy.username", "http.proxy.password",
                                                 "http.basic.username", "http.basic.password"
                                               };


    private static final int HttpProxyHost = 0;


    private static final int HttpProxyPort = 1;


    private static final int HttpProxyUser = 2;


    private static final int HttpProxyPass = 3;


    private static final int HttpBasicUser = 4;


    private static final int HttpBasicPass = 5;

    @Override
    public boolean engineIsThreadSafe() {
        return true;
    }


    @Override
    public XMLSignatureInput engineResolveURI(ResourceResolverContext context)
        throws ResourceResolverException {
        try {

            URI uriNew = getNewURI(context.uriToResolve, context.baseUri);
            URL url = uriNew.toURL();
            URLConnection urlConnection;
            urlConnection = openConnection(url);

            String auth = urlConnection.getHeaderField("WWW-Authenticate");

            if (auth != null && auth.startsWith("Basic")) {
                String user =
                    engineGetProperty(ResolverDirectHTTP.properties[ResolverDirectHTTP.HttpBasicUser]);
                String pass =
                    engineGetProperty(ResolverDirectHTTP.properties[ResolverDirectHTTP.HttpBasicPass]);

                if ((user != null) && (pass != null)) {
                    urlConnection = openConnection(url);

                    String password = user + ":" + pass;
                    String encodedPassword = Base64.encode(password.getBytes("ISO-8859-1"));

                    urlConnection.setRequestProperty("Authorization",
                                                     "Basic " + encodedPassword);
                }
            }

            String mimeType = urlConnection.getHeaderField("Content-Type");
            InputStream inputStream = urlConnection.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buf[] = new byte[4096];
            int read = 0;
            int summarized = 0;

            while ((read = inputStream.read(buf)) >= 0) {
                baos.write(buf, 0, read);
                summarized += read;
            }

            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "Fetched " + summarized + " bytes from URI " + uriNew.toString());
            }

            XMLSignatureInput result = new XMLSignatureInput(baos.toByteArray());

            result.setSourceURI(uriNew.toString());
            result.setMIMEType(mimeType);

            return result;
        } catch (URISyntaxException ex) {
            throw new ResourceResolverException("generic.EmptyMessage", ex, context.attr, context.baseUri);
        } catch (MalformedURLException ex) {
            throw new ResourceResolverException("generic.EmptyMessage", ex, context.attr, context.baseUri);
        } catch (IOException ex) {
            throw new ResourceResolverException("generic.EmptyMessage", ex, context.attr, context.baseUri);
        } catch (IllegalArgumentException e) {
            throw new ResourceResolverException("generic.EmptyMessage", e, context.attr, context.baseUri);
        }
    }

    private URLConnection openConnection(URL url) throws IOException {

        String proxyHostProp =
                engineGetProperty(ResolverDirectHTTP.properties[ResolverDirectHTTP.HttpProxyHost]);
        String proxyPortProp =
                engineGetProperty(ResolverDirectHTTP.properties[ResolverDirectHTTP.HttpProxyPort]);
        String proxyUser =
                engineGetProperty(ResolverDirectHTTP.properties[ResolverDirectHTTP.HttpProxyUser]);
        String proxyPass =
                engineGetProperty(ResolverDirectHTTP.properties[ResolverDirectHTTP.HttpProxyPass]);

        Proxy proxy = null;
        if ((proxyHostProp != null) && (proxyPortProp != null)) {
            int port = Integer.parseInt(proxyPortProp);
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHostProp, port));
        }

        URLConnection urlConnection;
        if (proxy != null) {
            urlConnection = url.openConnection(proxy);

            if ((proxyUser != null) && (proxyPass != null)) {
                String password = proxyUser + ":" + proxyPass;
                String authString = "Basic " + Base64.encode(password.getBytes("ISO-8859-1"));

                urlConnection.setRequestProperty("Proxy-Authorization", authString);
            }
        } else {
            urlConnection = url.openConnection();
        }

        return urlConnection;
    }


    public boolean engineCanResolveURI(ResourceResolverContext context) {
        if (context.uriToResolve == null) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "quick fail, uri == null");
            }
            return false;
        }

        if (context.uriToResolve.equals("") || (context.uriToResolve.charAt(0)=='#')) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "quick fail for empty URIs and local ones");
            }
            return false;
        }

        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "I was asked whether I can resolve " + context.uriToResolve);
        }

        if (context.uriToResolve.startsWith("http:") ||
            (context.baseUri != null && context.baseUri.startsWith("http:") )) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "I state that I can resolve " + context.uriToResolve);
            }
            return true;
        }

        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "I state that I can't resolve " + context.uriToResolve);
        }

        return false;
    }


    public String[] engineGetPropertyKeys() {
        return ResolverDirectHTTP.properties.clone();
    }

    private static URI getNewURI(String uri, String baseURI) throws URISyntaxException {
        URI newUri = null;
        if (baseURI == null || "".equals(baseURI)) {
            newUri = new URI(uri);
        } else {
            newUri = new URI(baseURI).resolve(uri);
        }

        if (newUri.getFragment() != null) {
            URI uriNewNoFrag =
                new URI(newUri.getScheme(), newUri.getSchemeSpecificPart(), null);
            return uriNewNoFrag;
        }
        return newUri;
    }

}
