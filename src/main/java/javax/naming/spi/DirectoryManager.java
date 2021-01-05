

package javax.naming.spi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.NamingException;
import javax.naming.CannotProceedException;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attributes;

import com.sun.naming.internal.ResourceManager;
import com.sun.naming.internal.FactoryEnumeration;




public class DirectoryManager extends NamingManager {


    DirectoryManager() {}


    @SuppressWarnings("unchecked")
    public static DirContext getContinuationDirContext(
            CannotProceedException cpe) throws NamingException {

        Hashtable<Object,Object> env = (Hashtable<Object,Object>)cpe.getEnvironment();
        if (env == null) {
            env = new Hashtable<>(7);
        } else {
            env = (Hashtable<Object,Object>) env.clone();
        }
        env.put(CPE, cpe);

        return (new ContinuationDirContext(cpe, env));
    }


    public static Object
        getObjectInstance(Object refInfo, Name name, Context nameCtx,
                          Hashtable<?,?> environment, Attributes attrs)
        throws Exception {

            ObjectFactory factory;

            ObjectFactoryBuilder builder = getObjectFactoryBuilder();
            if (builder != null) {
                factory = builder.createObjectFactory(refInfo, environment);
                if (factory instanceof DirObjectFactory) {
                    return ((DirObjectFactory)factory).getObjectInstance(
                        refInfo, name, nameCtx, environment, attrs);
                } else {
                    return factory.getObjectInstance(refInfo, name, nameCtx,
                        environment);
                }
            }

            Reference ref = null;
            if (refInfo instanceof Reference) {
                ref = (Reference) refInfo;
            } else if (refInfo instanceof Referenceable) {
                ref = ((Referenceable)(refInfo)).getReference();
            }

            Object answer;

            if (ref != null) {
                String f = ref.getFactoryClassName();
                if (f != null) {
                    factory = getObjectFactoryFromReference(ref, f);
                    if (factory instanceof DirObjectFactory) {
                        return ((DirObjectFactory)factory).getObjectInstance(
                            ref, name, nameCtx, environment, attrs);
                    } else if (factory != null) {
                        return factory.getObjectInstance(ref, name, nameCtx,
                                                         environment);
                    }
                    return refInfo;

                } else {
                    answer = processURLAddrs(ref, name, nameCtx, environment);
                    if (answer != null) {
                        return answer;
                    }
                }
            }

            answer = createObjectFromFactories(refInfo, name, nameCtx,
                                               environment, attrs);
            return (answer != null) ? answer : refInfo;
    }

    private static Object createObjectFromFactories(Object obj, Name name,
            Context nameCtx, Hashtable<?,?> environment, Attributes attrs)
        throws Exception {

        FactoryEnumeration factories = ResourceManager.getFactories(
            Context.OBJECT_FACTORIES, environment, nameCtx);

        if (factories == null)
            return null;

        ObjectFactory factory;
        Object answer = null;
        while (answer == null && factories.hasMore()) {
            factory = (ObjectFactory)factories.next();
            if (factory instanceof DirObjectFactory) {
                answer = ((DirObjectFactory)factory).
                    getObjectInstance(obj, name, nameCtx, environment, attrs);
            } else {
                answer =
                    factory.getObjectInstance(obj, name, nameCtx, environment);
            }
        }
        return answer;
    }


    public static DirStateFactory.Result
        getStateToBind(Object obj, Name name, Context nameCtx,
                       Hashtable<?,?> environment, Attributes attrs)
        throws NamingException {

        FactoryEnumeration factories = ResourceManager.getFactories(
            Context.STATE_FACTORIES, environment, nameCtx);

        if (factories == null) {
            return new DirStateFactory.Result(obj, attrs);
        }

        StateFactory factory;
        Object objanswer;
        DirStateFactory.Result answer = null;
        while (answer == null && factories.hasMore()) {
            factory = (StateFactory)factories.next();
            if (factory instanceof DirStateFactory) {
                answer = ((DirStateFactory)factory).
                    getStateToBind(obj, name, nameCtx, environment, attrs);
            } else {
                objanswer =
                    factory.getStateToBind(obj, name, nameCtx, environment);
                if (objanswer != null) {
                    answer = new DirStateFactory.Result(objanswer, attrs);
                }
            }
        }

        return (answer != null) ? answer :
            new DirStateFactory.Result(obj, attrs); }
}
