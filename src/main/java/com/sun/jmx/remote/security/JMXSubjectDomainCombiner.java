

package com.sun.jmx.remote.security;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import javax.security.auth.Subject;
import javax.security.auth.SubjectDomainCombiner;


public class JMXSubjectDomainCombiner extends SubjectDomainCombiner {

    public JMXSubjectDomainCombiner(Subject s) {
        super(s);
    }

    public ProtectionDomain[] combine(ProtectionDomain[] current,
                                      ProtectionDomain[] assigned) {
        ProtectionDomain[] newCurrent;
        if (current == null || current.length == 0) {
            newCurrent = new ProtectionDomain[1];
            newCurrent[0] = pdNoPerms;
        } else {
            newCurrent = new ProtectionDomain[current.length + 1];
            for (int i = 0; i < current.length; i++) {
                newCurrent[i] = current[i];
            }
            newCurrent[current.length] = pdNoPerms;
        }
        return super.combine(newCurrent, assigned);
    }


    private static final CodeSource nullCodeSource =
        new CodeSource(null, (java.security.cert.Certificate[]) null);


    private static final ProtectionDomain pdNoPerms =
        new ProtectionDomain(nullCodeSource, new Permissions(), null, null);


    public static AccessControlContext getContext(Subject subject) {
        return new AccessControlContext(AccessController.getContext(),
                                        new JMXSubjectDomainCombiner(subject));
    }


    public static AccessControlContext
        getDomainCombinerContext(Subject subject) {
        return new AccessControlContext(
            new AccessControlContext(new ProtectionDomain[0]),
            new JMXSubjectDomainCombiner(subject));
    }
}
