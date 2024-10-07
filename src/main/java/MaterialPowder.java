public class MaterialPowder extends Material {
    void resolvePhysics(Subworld sw, int x, int y, int interactionDepth) {
        int pixelBuf = sw.getPixel(x, y, 3);
        int pixelUnder = sw.getPixel(x, y - 1, interactionDepth);

        if (pixelUnder == 0) {
            sw.setPixel(x, y - 1, pixelBuf);
            sw.setPixel(x, y, 0);
        } else {
            int pixelUnderLeft = sw.getPixel(x - 1, y - 1, interactionDepth);
            int pixelUnderRight = sw.getPixel(x + 1, y - 1, interactionDepth);
            if (pixelUnderLeft == 0 || pixelUnderRight == 0) {
                if (pixelUnderLeft == 0 && (pixelUnderRight != 0 || sw.random.nextBoolean())) {
                    sw.setPixel(x - 1, y - 1, pixelBuf);
                    sw.setPixel(x, y, 0);
                } else {
                    sw.setPixel(x + 1, y - 1, pixelBuf);
                    sw.setPixel(x, y, 0);
                }
            }
        }
    }
}
