public class MaterialLiquid extends Material {
    void resolvePhysics(Subworld sw, int x, int y, int interactionDepth) {
        int pixelBuf = sw.getPixel(x, y);
        int pixelUnder = sw.getPixel(x, y - 1);
        int pixelUnderLeft = sw.getPixel(x - 1, y - 1);
        int pixelUnderRight = sw.getPixel(x + 1, y - 1);
        int pixelLeft = sw.getPixel(x - 1, y);
        int pixelRight = sw.getPixel(x + 1, y);

        if (pixelUnder == 0) {
            sw.setPixel(x, y - 1, pixelBuf);
            sw.setPixel(x, y, 0);
        } else if (pixelUnderLeft == 0 || pixelUnderRight == 0) {
            if (pixelUnderLeft == 0 && (pixelUnderRight != 0 || sw.random.nextBoolean())) {
                sw.setPixel(x - 1, y - 1, pixelBuf);
                sw.setPixel(x, y, 0);
            } else {
                sw.setPixel(x + 1, y - 1, pixelBuf);
                sw.setPixel(x, y, 0);
            }
        } else if (pixelLeft == 0 || pixelRight == 0) {
            if (pixelLeft == 0 && (pixelRight != 0 || sw.random.nextBoolean())) {
                sw.setPixel(x - 1, y, pixelBuf);
                sw.setPixel(x, y, 0);
            } else {
                sw.setPixel(x + 1, y, pixelBuf);
                sw.setPixel(x, y, 0);
            }
        }
    }
}
