
    public int filterRGB(int x, int y, int rgb) {

        int gray = (int)((0.30 * ((rgb >> 16) & 0xff) +
                         0.59 * ((rgb >> 8) & 0xff) +
                         0.11 * (rgb & 0xff)) / 3);

        if (brighter) {
            gray = (255 - ((255 - gray) * (100 - percent) / 100));
        } else {
            gray = (gray * (100 - percent) / 100);
        }

        if (gray < 0) gray = 0;
        if (gray > 255) gray = 255;
        return (rgb & 0xff000000) | (gray << 16) | (gray << 8) | (gray << 0);
    }
}
