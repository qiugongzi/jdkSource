

package javax.sound.sampled;

import java.security.BasicPermission;







public class AudioPermission extends BasicPermission {


    public AudioPermission(String name) {

        super(name);
    }


    public AudioPermission(String name, String actions) {

        super(name, actions);
    }
}
