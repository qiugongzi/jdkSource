
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {

            throw new InternalError(e);
        }
    }

}
