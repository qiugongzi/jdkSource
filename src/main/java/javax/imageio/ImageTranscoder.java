

package javax.imageio;

import javax.imageio.metadata.IIOMetadata;


public interface ImageTranscoder {


    IIOMetadata convertStreamMetadata(IIOMetadata inData,
                                      ImageWriteParam param);


    IIOMetadata convertImageMetadata(IIOMetadata inData,
                                     ImageTypeSpecifier imageType,
                                     ImageWriteParam param);
}
