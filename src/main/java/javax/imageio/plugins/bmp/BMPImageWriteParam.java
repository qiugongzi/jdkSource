

package javax.imageio.plugins.bmp;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

import com.sun.imageio.plugins.bmp.BMPConstants;
import com.sun.imageio.plugins.bmp.BMPCompressionTypes;


public class BMPImageWriteParam extends ImageWriteParam {

    private boolean topDown = false;


    public BMPImageWriteParam(Locale locale) {
        super(locale);

        compressionTypes = BMPCompressionTypes.getCompressionTypes();

        canWriteCompressed = true;
        compressionMode = MODE_COPY_FROM_METADATA;
        compressionType = compressionTypes[BMPConstants.BI_RGB];
    }


    public BMPImageWriteParam() {
        this(null);
    }


    public void setTopDown(boolean topDown) {
        this.topDown = topDown;
    }


    public boolean isTopDown() {
        return topDown;
    }
}
