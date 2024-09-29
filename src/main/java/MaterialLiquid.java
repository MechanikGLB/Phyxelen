public class MaterialLiquid extends Material {
    void resolvePhysics(Subworld sw, int x, int y) {
        int PixelBuf = sw.getPixel(x, y);
        int PixelUnder = sw.getPixel(x, y - 1);
        int PixelUnderLeft = sw.getPixel(x - 1, y - 1);
        int PixelUnderRight = sw.getPixel(x + 1, y - 1);
        int pixelLeft = sw.getPixel(x - 1, y);
        int pixelRight = sw.getPixel(x + 1, y);

        if (PixelUnder == 0) {
            sw.setPixel(x, y - 1, PixelBuf);
            sw.setPixel(x, y, 0);
        } else if (PixelUnderLeft == 0 || PixelUnderRight == 0) {
            if (PixelUnderLeft == 0 && (PixelUnderRight != 0 || sw.random.nextBoolean())) {
                sw.setPixel(x - 1, y - 1, PixelBuf);
                sw.setPixel(x, y, 0);
            } else {
                sw.setPixel(x + 1, y - 1, PixelBuf);
                sw.setPixel(x, y, 0);
            }
        } else if (pixelLeft == 0 || pixelRight == 0) {
            if (pixelLeft == 0 && (pixelRight != 0 || sw.random.nextBoolean())) {
                sw.setPixel(x - 1, y, PixelBuf);
                sw.setPixel(x, y, 0);
            } else {
                sw.setPixel(x + 1, y, PixelBuf);
                sw.setPixel(x, y, 0);
            }
        }
    }
}
