

package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageWriter;


public interface IIOWriteProgressListener extends EventListener {


    void imageStarted(ImageWriter source, int imageIndex);


    void imageProgress(ImageWriter source,
                       float percentageDone);


    void imageComplete(ImageWriter source);


    void thumbnailStarted(ImageWriter source,
                          int imageIndex, int thumbnailIndex);


    void thumbnailProgress(ImageWriter source, float percentageDone);


    void thumbnailComplete(ImageWriter source);


    void writeAborted(ImageWriter source);
}
