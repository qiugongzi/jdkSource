

package java.net;

import java.net.URI;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;


class InMemoryCookieStore implements CookieStore {
    private List<HttpCookie> cookieJar = null;

    private Map<String, List<HttpCookie>> domainIndex = null;
    private Map<URI, List<HttpCookie>> uriIndex = null;

    private ReentrantLock lock = null;



    public InMemoryCookieStore() {
        cookieJar = new ArrayList<HttpCookie>();
        domainIndex = new HashMap<String, List<HttpCookie>>();
        uriIndex = new HashMap<URI, List<HttpCookie>>();

        lock = new ReentrantLock(false);
    }


    public void add(URI uri, HttpCookie cookie) {
        if (cookie == null) {
            throw new NullPointerException("cookie is null");
        }


        lock.lock();
        try {
            cookieJar.remove(cookie);

            if (cookie.getMaxAge() != 0) {
                cookieJar.add(cookie);
                if (cookie.getDomain() != null) {
                    addIndex(domainIndex, cookie.getDomain(), cookie);
                }
                if (uri != null) {
                    addIndex(uriIndex, getEffectiveURI(uri), cookie);
                }
            }
        } finally {
            lock.unlock();
        }
    }



    public List<HttpCookie> get(URI uri) {
        if (uri == null) {
            throw new NullPointerException("uri is null");
        }

        List<HttpCookie> cookies = new ArrayList<HttpCookie>();
        boolean secureLink = "https".equalsIgnoreCase(uri.getScheme());
        lock.lock();
        try {
            getInternal1(cookies, domainIndex, uri.getHost(), secureLink);
            getInternal2(cookies, uriIndex, getEffectiveURI(uri), secureLink);
        } finally {
            lock.unlock();
        }

        return cookies;
    }


    public List<HttpCookie> getCookies() {
        List<HttpCookie> rt;

        lock.lock();
        try {
            Iterator<HttpCookie> it = cookieJar.iterator();
            while (it.hasNext()) {
                if (it.next().hasExpired()) {
                    it.remove();
                }
            }
        } finally {
            rt = Collections.unmodifiableList(cookieJar);
            lock.unlock();
        }

        return rt;
    }


    public List<URI> getURIs() {
        List<URI> uris = new ArrayList<URI>();

        lock.lock();
        try {
            Iterator<URI> it = uriIndex.keySet().iterator();
            while (it.hasNext()) {
                URI uri = it.next();
                List<HttpCookie> cookies = uriIndex.get(uri);
                if (cookies == null || cookies.size() == 0) {
                    it.remove();
                }
            }
        } finally {
            uris.addAll(uriIndex.keySet());
            lock.unlock();
        }

        return uris;
    }



    public boolean remove(URI uri, HttpCookie ck) {
        if (ck == null) {
            throw new NullPointerException("cookie is null");
        }

        boolean modified = false;
        lock.lock();
        try {
            modified = cookieJar.remove(ck);
        } finally {
            lock.unlock();
        }

        return modified;
    }



    public boolean removeAll() {
        lock.lock();
        try {
            if (cookieJar.isEmpty()) {
                return false;
            }
            cookieJar.clear();
            domainIndex.clear();
            uriIndex.clear();
        } finally {
            lock.unlock();
        }

        return true;
    }






    private boolean netscapeDomainMatches(String domain, String host)
    {
        if (domain == null || host == null) {
            return false;
        }

        boolean isLocalDomain = ".local".equalsIgnoreCase(domain);
        int embeddedDotInDomain = domain.indexOf('.');
        if (embeddedDotInDomain == 0) {
            embeddedDotInDomain = domain.indexOf('.', 1);
        }
        if (!isLocalDomain && (embeddedDotInDomain == -1 || embeddedDotInDomain == domain.length() - 1)) {
            return false;
        }

        int firstDotInHost = host.indexOf('.');
        if (firstDotInHost == -1 && isLocalDomain) {
            return true;
        }

        int domainLength = domain.length();
        int lengthDiff = host.length() - domainLength;
        if (lengthDiff == 0) {
            return host.equalsIgnoreCase(domain);
        } else if (lengthDiff > 0) {
            String H = host.substring(0, lengthDiff);
            String D = host.substring(lengthDiff);

            return (D.equalsIgnoreCase(domain));
        } else if (lengthDiff == -1) {
            return (domain.charAt(0) == '.' &&
                    host.equalsIgnoreCase(domain.substring(1)));
        }

        return false;
    }

    private void getInternal1(List<HttpCookie> cookies, Map<String, List<HttpCookie>> cookieIndex,
            String host, boolean secureLink) {
        ArrayList<HttpCookie> toRemove = new ArrayList<HttpCookie>();
        for (Map.Entry<String, List<HttpCookie>> entry : cookieIndex.entrySet()) {
            String domain = entry.getKey();
            List<HttpCookie> lst = entry.getValue();
            for (HttpCookie c : lst) {
                if ((c.getVersion() == 0 && netscapeDomainMatches(domain, host)) ||
                        (c.getVersion() == 1 && HttpCookie.domainMatches(domain, host))) {
                    if ((cookieJar.indexOf(c) != -1)) {
                        if (!c.hasExpired()) {
                            if ((secureLink || !c.getSecure()) &&
                                    !cookies.contains(c)) {
                                cookies.add(c);
                            }
                        } else {
                            toRemove.add(c);
                        }
                    } else {
                        toRemove.add(c);
                    }
                }
            }
            for (HttpCookie c : toRemove) {
                lst.remove(c);
                cookieJar.remove(c);

            }
            toRemove.clear();
        }
    }

    private <T> void getInternal2(List<HttpCookie> cookies,
                                Map<T, List<HttpCookie>> cookieIndex,
                                Comparable<T> comparator, boolean secureLink)
    {
        for (T index : cookieIndex.keySet()) {
            if (comparator.compareTo(index) == 0) {
                List<HttpCookie> indexedCookies = cookieIndex.get(index);
                if (indexedCookies != null) {
                    Iterator<HttpCookie> it = indexedCookies.iterator();
                    while (it.hasNext()) {
                        HttpCookie ck = it.next();
                        if (cookieJar.indexOf(ck) != -1) {
                            if (!ck.hasExpired()) {
                                if ((secureLink || !ck.getSecure()) &&
                                        !cookies.contains(ck))
                                    cookies.add(ck);
                            } else {
                                it.remove();
                                cookieJar.remove(ck);
                            }
                        } else {
                            it.remove();
                        }
                    }
                } } } }

    private <T> void addIndex(Map<T, List<HttpCookie>> indexStore,
                              T index,
                              HttpCookie cookie)
    {
        if (index != null) {
            List<HttpCookie> cookies = indexStore.get(index);
            if (cookies != null) {
                cookies.remove(cookie);

                cookies.add(cookie);
            } else {
                cookies = new ArrayList<HttpCookie>();
                cookies.add(cookie);
                indexStore.put(index, cookies);
            }
        }
    }


    private URI getEffectiveURI(URI uri) {
        URI effectiveURI = null;
        try {
            effectiveURI = new URI("http",
                                   uri.getHost(),
                                   null,  null,  null   );
        } catch (URISyntaxException ignored) {
            effectiveURI = uri;
        }

        return effectiveURI;
    }
}
