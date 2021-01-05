

package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.ServiceName;
import static com.sun.jmx.defaults.JmxProperties.MBEANSERVER_LOGGER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.Map;
import java.util.Set;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.RuntimeOperationsException;


public class Repository {


    public interface RegistrationContext {

        public void registering();


        public void unregistered();
    }

    private final Map<String,Map<String,NamedObject>> domainTb;


    private volatile int nbElements = 0;


    private final String domain;


    private final ReentrantReadWriteLock lock;

    private final static class ObjectNamePattern {
        private final String[] keys;
        private final String[] values;
        private final String   properties;
        private final boolean  isPropertyListPattern;
        private final boolean  isPropertyValuePattern;


        public final ObjectName pattern;


        public ObjectNamePattern(ObjectName pattern) {
            this(pattern.isPropertyListPattern(),
                 pattern.isPropertyValuePattern(),
                 pattern.getCanonicalKeyPropertyListString(),
                 pattern.getKeyPropertyList(),
                 pattern);
        }


        ObjectNamePattern(boolean propertyListPattern,
                          boolean propertyValuePattern,
                          String canonicalProps,
                          Map<String,String> keyPropertyList,
                          ObjectName pattern) {
            this.isPropertyListPattern = propertyListPattern;
            this.isPropertyValuePattern = propertyValuePattern;
            this.properties = canonicalProps;
            final int len = keyPropertyList.size();
            this.keys   = new String[len];
            this.values = new String[len];
            int i = 0;
            for (Map.Entry<String,String> entry : keyPropertyList.entrySet()) {
                keys[i]   = entry.getKey();
                values[i] = entry.getValue();
                i++;
            }
            this.pattern = pattern;
        }


        public boolean matchKeys(ObjectName name) {
            if (isPropertyValuePattern &&
                !isPropertyListPattern &&
                (name.getKeyPropertyList().size() != keys.length))
                return false;

            if (isPropertyValuePattern || isPropertyListPattern) {
                for (int i = keys.length - 1; i >= 0 ; i--) {
                    String v = name.getKeyProperty(keys[i]);
                    if (v == null) return false;
                    if (isPropertyValuePattern &&
                        pattern.isPropertyValuePattern(keys[i])) {
                        if (Util.wildmatch(v,values[i]))
                            continue;
                        else
                            return false;
                    }
                    if (v.equals(values[i])) continue;
                    return false;
                }
                return true;
            }

            final String p1 = name.getCanonicalKeyPropertyListString();
            final String p2 = properties;
            return (p1.equals(p2));
        }
    }


    private void addAllMatching(final Map<String,NamedObject> moiTb,
                                final Set<NamedObject> result,
                                final ObjectNamePattern pattern) {
        synchronized (moiTb) {
            for (NamedObject no : moiTb.values()) {
                final ObjectName on = no.getName();
                if (pattern.matchKeys(on)) result.add(no);
            }
        }
    }

    private void addNewDomMoi(final DynamicMBean object,
                              final String dom,
                              final ObjectName name,
                              final RegistrationContext context) {
        final Map<String,NamedObject> moiTb =
            new HashMap<String,NamedObject>();
        final String key = name.getCanonicalKeyPropertyListString();
        addMoiToTb(object,name,key,moiTb,context);
        domainTb.put(dom, moiTb);
        nbElements++;
    }

    private void registering(RegistrationContext context) {
        if (context == null) return;
        try {
            context.registering();
        } catch (RuntimeOperationsException x) {
            throw x;
        } catch (RuntimeException x) {
            throw new RuntimeOperationsException(x);
        }
    }

    private void unregistering(RegistrationContext context, ObjectName name) {
        if (context == null) return;
        try {
            context.unregistered();
        } catch (Exception x) {
            MBEANSERVER_LOGGER.log(Level.FINE,
                    "Unexpected exception while unregistering "+name,
                    x);
        }
    }

    private void addMoiToTb(final DynamicMBean object,
            final ObjectName name,
            final String key,
            final Map<String,NamedObject> moiTb,
            final RegistrationContext context) {
        registering(context);
        moiTb.put(key,new NamedObject(name, object));
    }


    private NamedObject retrieveNamedObject(ObjectName name) {

        if (name.isPattern()) return null;

        String dom = name.getDomain().intern();

        if (dom.length() == 0) {
            dom = domain;
        }

        Map<String,NamedObject> moiTb = domainTb.get(dom);
        if (moiTb == null) {
            return null; }

        return moiTb.get(name.getCanonicalKeyPropertyListString());
    }

    public Repository(String domain) {
        this(domain,true);
    }


    public Repository(String domain, boolean fairLock) {
        lock = new ReentrantReadWriteLock(fairLock);

        domainTb = new HashMap<String,Map<String,NamedObject>>(5);

        if (domain != null && domain.length() != 0)
            this.domain = domain.intern(); else
            this.domain = ServiceName.DOMAIN;

        domainTb.put(this.domain, new HashMap<String,NamedObject>());
    }


    public String[] getDomains() {

        lock.readLock().lock();
        final List<String> result;
        try {
            result = new ArrayList<String>(domainTb.size());
            for (Map.Entry<String,Map<String,NamedObject>> entry :
                     domainTb.entrySet()) {
                Map<String,NamedObject> t = entry.getValue();
                if (t != null && t.size() != 0)
                    result.add(entry.getKey());
            }
        } finally {
            lock.readLock().unlock();
        }

        return result.toArray(new String[result.size()]);
    }


    public void addMBean(final DynamicMBean object, ObjectName name,
            final RegistrationContext context)
        throws InstanceAlreadyExistsException {

        if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(),
                    "addMBean", "name = " + name);
        }

        String dom = name.getDomain().intern();
        boolean to_default_domain = false;

        if (dom.length() == 0)
            name = Util.newObjectName(domain + name.toString());

        if (dom == domain) {  to_default_domain = true;
            dom = domain;
        } else {
            to_default_domain = false;
        }

        if (name.isPattern()) {
            throw new RuntimeOperationsException(
             new IllegalArgumentException("Repository: cannot add mbean for " +
                                          "pattern name " + name.toString()));
        }

        lock.writeLock().lock();
        try {
            if ( !to_default_domain &&
                    dom.equals("JMImplementation") &&
                    domainTb.containsKey("JMImplementation")) {
                throw new RuntimeOperationsException(
                        new IllegalArgumentException(
                        "Repository: domain name cannot be JMImplementation"));
            }

            final Map<String,NamedObject> moiTb = domainTb.get(dom);
            if (moiTb == null) {
                addNewDomMoi(object, dom, name, context);
                return;
            } else {
                String cstr = name.getCanonicalKeyPropertyListString();
                NamedObject elmt= moiTb.get(cstr);
                if (elmt != null) {
                    throw new InstanceAlreadyExistsException(name.toString());
                } else {
                    nbElements++;
                    addMoiToTb(object,name,cstr,moiTb,context);
                }
            }

        } finally {
            lock.writeLock().unlock();
        }
    }


    public boolean contains(ObjectName name) {
        if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(),
                    "contains", " name = " + name);
        }
        lock.readLock().lock();
        try {
            return (retrieveNamedObject(name) != null);
        } finally {
            lock.readLock().unlock();
        }
    }


    public DynamicMBean retrieve(ObjectName name) {
        if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(),
                    "retrieve", "name = " + name);
        }

        lock.readLock().lock();
        try {
            NamedObject no = retrieveNamedObject(name);
            if (no == null) return null;
            else return no.getObject();
        } finally {
            lock.readLock().unlock();
        }
    }


    public Set<NamedObject> query(ObjectName pattern, QueryExp query) {

        final Set<NamedObject> result = new HashSet<NamedObject>();

        ObjectName name;
        if (pattern == null ||
            pattern.getCanonicalName().length() == 0 ||
            pattern.equals(ObjectName.WILDCARD))
           name = ObjectName.WILDCARD;
        else name = pattern;

        lock.readLock().lock();
        try {

            if (!name.isPattern()) {
                final NamedObject no = retrieveNamedObject(name);
                if (no != null) result.add(no);
                return result;
            }

            if (name == ObjectName.WILDCARD) {
                for (Map<String,NamedObject> moiTb : domainTb.values()) {
                    result.addAll(moiTb.values());
                }
                return result;
            }

            final String canonical_key_property_list_string =
                    name.getCanonicalKeyPropertyListString();
            final boolean allNames =
                    (canonical_key_property_list_string.length()==0);
            final ObjectNamePattern namePattern =
                (allNames?null:new ObjectNamePattern(name));

            if (name.getDomain().length() == 0) {
                final Map<String,NamedObject> moiTb = domainTb.get(domain);
                if (allNames)
                    result.addAll(moiTb.values());
                else
                    addAllMatching(moiTb, result, namePattern);
                return result;
            }

            if (!name.isDomainPattern()) {
                final Map<String,NamedObject> moiTb = domainTb.get(name.getDomain());
                if (moiTb == null) return Collections.emptySet();
                if (allNames)
                    result.addAll(moiTb.values());
                else
                    addAllMatching(moiTb, result, namePattern);
                return result;
            }

            final String dom2Match = name.getDomain();
            for (String dom : domainTb.keySet()) {
                if (Util.wildmatch(dom, dom2Match)) {
                    final Map<String,NamedObject> moiTb = domainTb.get(dom);
                    if (allNames)
                        result.addAll(moiTb.values());
                    else
                        addAllMatching(moiTb, result, namePattern);
                }
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }


    public void remove(final ObjectName name,
            final RegistrationContext context)
        throws InstanceNotFoundException {

        if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(),
                    "remove", "name = " + name);
        }

        String dom= name.getDomain().intern();

        if (dom.length() == 0) dom = domain;

        lock.writeLock().lock();
        try {
            final Map<String,NamedObject> moiTb = domainTb.get(dom);
            if (moiTb == null) {
                throw new InstanceNotFoundException(name.toString());
            }

            if (moiTb.remove(name.getCanonicalKeyPropertyListString())==null) {
                throw new InstanceNotFoundException(name.toString());
            }

            nbElements--;

            if (moiTb.isEmpty()) {
                domainTb.remove(dom);

                if (dom == domain) domainTb.put(domain, new HashMap<String,NamedObject>());
            }

            unregistering(context,name);

        } finally {
            lock.writeLock().unlock();
        }
    }


    public Integer getCount() {
        return nbElements;
    }


    public String getDefaultDomain() {
        return domain;
    }

    }
