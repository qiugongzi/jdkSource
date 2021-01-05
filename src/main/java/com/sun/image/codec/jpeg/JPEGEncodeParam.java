



package com.sun.image.codec.jpeg;




public interface JPEGEncodeParam
        extends Cloneable, JPEGDecodeParam
{
        public Object clone();


        public void setHorizontalSubsampling(int component,
                                                                                 int subsample);


        public void setVerticalSubsampling(int component,
                                                                           int subsample);


        public void     setQTable( int tableNum, JPEGQTable qTable );


        public void     setDCHuffmanTable( int tableNum,
                                                                   JPEGHuffmanTable huffTable);


        public void     setACHuffmanTable( int tableNum,
                                                                   JPEGHuffmanTable huffTable);



        public void setDCHuffmanComponentMapping( int component, int table);

        public void setACHuffmanComponentMapping( int component, int table);

        public void setQTableComponentMapping( int component, int table);


        public void setImageInfoValid(boolean flag);


        public void setTableInfoValid(boolean flag);


        public void setMarkerData(int marker, byte[][] data);


        public void addMarkerData(int marker, byte []data);


        public void setRestartInterval( int restartInterval );



        public void setDensityUnit( int unit);

        public void setXDensity( int density );

        public void setYDensity( int density );


        public void setQuality(float quality, boolean forceBaseline );
}
