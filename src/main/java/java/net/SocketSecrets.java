

    private static <T> void setOption(Object obj, SocketOption<T> name, T value) throws IOException {
        SocketImpl impl;

        if (obj instanceof Socket) {
            impl = ((Socket)obj).getImpl();
        } else if (obj instanceof ServerSocket) {
            impl = ((ServerSocket)obj).getImpl();
        } else {
            throw new IllegalArgumentException();
        }
        impl.setOption(name, value);
    }

    private static <T> T getOption(Object obj, SocketOption<T> name) throws IOException {
        SocketImpl impl;

        if (obj instanceof Socket) {
            impl = ((Socket)obj).getImpl();
        } else if (obj instanceof ServerSocket) {
            impl = ((ServerSocket)obj).getImpl();
        } else {
            throw new IllegalArgumentException();
        }
        return impl.getOption(name);
    }

    private static <T> void setOption(DatagramSocket s, SocketOption<T> name, T value) throws IOException {
        s.getImpl().setOption(name, value);
    }

    private static <T> T getOption(DatagramSocket s, SocketOption<T> name) throws IOException {
        return s.getImpl().getOption(name);
    }

}
