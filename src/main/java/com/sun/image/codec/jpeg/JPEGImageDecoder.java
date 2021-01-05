



package com.sun.image.codec.jpeg;




import java.io.InputStream;
import java.io.IOException;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;



public interface JPEGImageDecoder {


    public JPEGDecodeParam getJPEGDecodeParam();


    public void setJPEGDecodeParam(JPEGDecodeParam jdp);


        public InputStream getInputStream();


    public Raster decodeAsRaster()
                throws IOException, ImageFormatException;


    public BufferedImage decodeAsBufferedImage()
                throws IOException, ImageFormatException;

}