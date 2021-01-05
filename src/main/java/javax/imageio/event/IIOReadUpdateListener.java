

package javax.imageio.event;

import java.awt.image.BufferedImage;
import java.util.EventListener;
import javax.imageio.ImageReader;


public interface IIOReadUpdateListener extends EventListener {


    void passStarted(ImageReader source,
                     BufferedImage theImage,
                     int pass,
                     int minPass, int maxPass,
                     int minX, int minY,
                     int periodX, int periodY,
                     int[] bands);


    void imageUpdate(ImageReader source,
                     BufferedImage theImage,
                     int minX, int minY,
                     int width, int height,
                     int periodX, int periodY,
                     int[] bands);


    void passComplete(ImageReader source, BufferedImage theImage);


    void thumbnailPassStarted(ImageReader source,
                              BufferedImage theThumbnail,
                              int pass,
                              int minPass, int maxPass,
                              int minX, int minY,
                              int periodX, int periodY,
                              int[] bands);


    void thumbnailUpdate(ImageReader source,
                         BufferedImage theThumbnail,
                         int minX, int minY,
                         int width, int height,
                         int periodX, int periodY,
                         int[] bands);


    void thumbnailPassComplete(ImageReader source, BufferedImage theThumbnail);
}
