



package com.sun.image.codec.jpeg;




import java.io.OutputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;



public interface JPEGImageEncoder {

        public OutputStream getOutputStream();


        public void setJPEGEncodeParam(JPEGEncodeParam jep);


        public JPEGEncodeParam getJPEGEncodeParam();


        public JPEGEncodeParam getDefaultJPEGEncodeParam(BufferedImage bi)
                throws ImageFormatException;


        public void encode(BufferedImage bi)
                throws IOException, ImageFormatException;


        public void encode(BufferedImage bi, JPEGEncodeParam jep)
                throws IOException, ImageFormatException;


        public int getDefaultColorId(ColorModel cm);


        public JPEGEncodeParam getDefaultJPEGEncodeParam(Raster ras, int colorID)
                throws ImageFormatException;


        public JPEGEncodeParam getDefaultJPEGEncodeParam(int numBands,
                                                         int colorID)
                throws ImageFormatException;


        public JPEGEncodeParam getDefaultJPEGEncodeParam(JPEGDecodeParam jdp)
            throws ImageFormatException;


        public void encode(Raster ras)
          throws IOException, ImageFormatException;


        public void encode(Raster ras, JPEGEncodeParam jep)
                throws IOException, ImageFormatException;
}
