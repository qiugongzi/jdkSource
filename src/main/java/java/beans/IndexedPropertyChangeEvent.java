
    public int getIndex() {
        return index;
    }

    void appendTo(StringBuilder sb) {
        sb.append("; index=").append(getIndex());
    }
}
