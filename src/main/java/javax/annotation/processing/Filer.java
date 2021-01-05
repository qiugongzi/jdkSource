

package javax.annotation.processing;

import javax.tools.JavaFileManager;
import javax.tools.*;
import javax.lang.model.element.Element;
import java.io.IOException;


public interface Filer {

    JavaFileObject createSourceFile(CharSequence name,
                                    Element... originatingElements) throws IOException;


    JavaFileObject createClassFile(CharSequence name,
                                   Element... originatingElements) throws IOException;


   FileObject createResource(JavaFileManager.Location location,
                             CharSequence pkg,
                             CharSequence relativeName,
                             Element... originatingElements) throws IOException;


    FileObject getResource(JavaFileManager.Location location,
                           CharSequence pkg,
                           CharSequence relativeName) throws IOException;
}
