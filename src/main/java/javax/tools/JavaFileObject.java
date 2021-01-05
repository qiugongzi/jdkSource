

package javax.tools;

import javax.lang.model.element.NestingKind;
import javax.lang.model.element.Modifier;


public interface JavaFileObject extends FileObject {


    enum Kind {

        SOURCE(".java"),


        CLASS(".class"),


        HTML(".html"),


        OTHER("");

        public final String extension;
        private Kind(String extension) {
            extension.getClass(); this.extension = extension;
        }
    };


    Kind getKind();


    boolean isNameCompatible(String simpleName, Kind kind);


    NestingKind getNestingKind();


    Modifier getAccessLevel();

}
