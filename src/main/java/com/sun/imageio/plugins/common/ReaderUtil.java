

package com.sun.imageio.plugins.common;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;


public class ReaderUtil {

    private static void computeUpdatedPixels(int sourceOffset,
                                             int sourceExtent,
                                             int destinationOffset,
                                             int dstMin,
                                             int dstMax,
                                             int sourceSubsampling,
                                             int passStart,
                                             int passExtent,
                                             int passPeriod,
                                             int[] vals,
                                             int offset)
    {
        boolean gotPixel = false;
        int firstDst = -1;
        int secondDst = -1;
        int lastDst = -1;

        for (int i = 0; i < passExtent; i++) {
            int src = passStart + i*passPeriod;
            if (src < sourceOffset) {
                continue;
            }
            if ((src - sourceOffset) % sourceSubsampling != 0) {
                continue;
            }
            if (src >= sourceOffset + sourceExtent) {
                break;
            }

            int dst = destinationOffset +
                (src - sourceOffset)/sourceSubsampling;
            if (dst < dstMin) {
                continue;
            }
            if (dst > dstMax) {
                break;
            }

            if (!gotPixel) {
                firstDst = dst; gotPixel = true;
            } else if (secondDst == -1) {
                secondDst = dst; }
            lastDst = dst; }

        vals[offset] = firstDst;

        if (!gotPixel) {
            vals[offset + 2] = 0;
        } else {
            vals[offset + 2] = lastDst - firstDst + 1;
        }

        vals[offset + 4] = Math.max(secondDst - firstDst, 1);
    }


    public static int[] computeUpdatedPixels(Rectangle sourceRegion,
                                             Point destinationOffset,
                                             int dstMinX,
                                             int dstMinY,
                                             int dstMaxX,
                                             int dstMaxY,
                                             int sourceXSubsampling,
                                             int sourceYSubsampling,
                                             int passXStart,
                                             int passYStart,
                                             int passWidth,
                                             int passHeight,
                                             int passPeriodX,
                                             int passPeriodY)
    {
        int[] vals = new int[6];
        computeUpdatedPixels(sourceRegion.x, sourceRegion.width,
                             destinationOffset.x,
                             dstMinX, dstMaxX, sourceXSubsampling,
                             passXStart, passWidth, passPeriodX,
                             vals, 0);
        computeUpdatedPixels(sourceRegion.y, sourceRegion.height,
                             destinationOffset.y,
                             dstMinY, dstMaxY, sourceYSubsampling,
                             passYStart, passHeight, passPeriodY,
                             vals, 1);
        return vals;
    }

    public static int readMultiByteInteger(ImageInputStream iis)
        throws IOException
    {
        int value = iis.readByte();
        int result = value & 0x7f;
        while((value & 0x80) == 0x80) {
            result <<= 7;
            value = iis.readByte();
            result |= (value & 0x7f);
        }
        return result;
    }
}
