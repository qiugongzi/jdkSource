

package com.sun.security.auth;

import java.security.CodeSource;
import java.security.PermissionCollection;
import javax.security.auth.Subject;


@jdk.Exported(false)
@Deprecated
public class PolicyFile extends javax.security.auth.Policy {

    private final sun.security.provider.AuthPolicyFile apf;


    public PolicyFile() {
        apf = new sun.security.provider.AuthPolicyFile();
    }


    @Override
    public void refresh() {
        apf.refresh();
    }


    @Override
    public PermissionCollection getPermissions(final Subject subject,
                                               final CodeSource codesource) {
        return apf.getPermissions(subject, codesource);
    }
}
