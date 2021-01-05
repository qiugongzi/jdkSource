



package com.sun.image.codec.jpeg;

import java.awt.image.Raster;
import java.awt.image.BufferedImage;

public
class TruncatedFileException extends RuntimeException {
        private Raster                  ras = null;
        private BufferedImage   bi  = null;



    public TruncatedFileException(BufferedImage bi) {
                super("Premature end of input file");
                this.bi  = bi;
                this.ras = bi.getData();
    }


    public TruncatedFileException(Raster ras) {
                super("Premature end of input file");
                this.ras = ras;
    }


        public Raster getRaster() { return ras; }


        public BufferedImage getBufferedImage() { return bi; }
}
