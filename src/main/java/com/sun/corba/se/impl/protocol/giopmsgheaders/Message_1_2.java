
    public void unmarshalRequestID(ByteBuffer byteBuffer) {
        int b1, b2, b3, b4;

        if (!isLittleEndian()) {
            b1 = (byteBuffer.get(GIOPMessageHeaderLength+0) << 24) & 0xFF000000;
            b2 = (byteBuffer.get(GIOPMessageHeaderLength+1) << 16) & 0x00FF0000;
            b3 = (byteBuffer.get(GIOPMessageHeaderLength+2) << 8)  & 0x0000FF00;
            b4 = (byteBuffer.get(GIOPMessageHeaderLength+3) << 0)  & 0x000000FF;
        } else {
            b1 = (byteBuffer.get(GIOPMessageHeaderLength+3) << 24) & 0xFF000000;
            b2 = (byteBuffer.get(GIOPMessageHeaderLength+2) << 16) & 0x00FF0000;
            b3 = (byteBuffer.get(GIOPMessageHeaderLength+1) << 8)  & 0x0000FF00;
            b4 = (byteBuffer.get(GIOPMessageHeaderLength+0) << 0)  & 0x000000FF;
        }

        this.request_id = (b1 | b2 | b3 | b4);
    }

    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        if (this.encodingVersion == Message.CDR_ENC_VERSION) {
            super.write(ostream);
            return;
        }
        GIOPVersion gv = this.GIOP_version;
        this.GIOP_version = GIOPVersion.getInstance((byte)13,
                                                    this.encodingVersion);
        super.write(ostream);
        this.GIOP_version = gv;
    }
}
