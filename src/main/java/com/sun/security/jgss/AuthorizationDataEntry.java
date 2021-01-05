
    public byte[] getData() {
        return data.clone();
    }

    public String toString() {
        return "AuthorizationDataEntry: type="+type+", data=" +
                data.length + " bytes:\n" +
                new sun.misc.HexDumpEncoder().encodeBuffer(data);
    }
}
