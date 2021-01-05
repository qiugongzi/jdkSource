

package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageReader;


public interface IIOReadProgressListener extends EventListener {


    void sequenceStarted(ImageReader source, int minIndex);


    void sequenceComplete(ImageReader source);


    void imageStarted(ImageReader source, int imageIndex);


    void imageProgress(ImageReader source, float percentageDone);


    void imageComplete(ImageReader source);


    void thumbnailStarted(ImageReader source,
                          int imageIndex, int thumbnailIndex);


    void thumbnailProgress(ImageReader source, float percentageDone);


    void thumbnailComplete(ImageReader source);


    void readAborted(ImageReader source);
}
