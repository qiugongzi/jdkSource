

package java.nio.channels;

import java.io.IOException;
import java.util.concurrent.Future;  public interface AsynchronousChannel
    extends Channel
{

    @Override
    void close() throws IOException;
}
