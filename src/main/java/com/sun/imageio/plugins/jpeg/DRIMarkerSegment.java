
    void write(ImageOutputStream ios) throws IOException {

    }

    void print() {
        printTag("DRI");
        System.out.println("Interval: "
                           + Integer.toString(restartInterval));
    }
}
