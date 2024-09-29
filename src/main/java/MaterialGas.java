public class MaterialGas extends Material {
    void resolvePhysics(Subworld sw, int x, int y) {
        int PixelBuf = sw.getPixel(x, y);
        int PixelAbove = sw.getPixel(x, y + 1);
        int PixelAboveLeft = sw.getPixel(x - 1, y + 1);
        int PixelAboveRight = sw.getPixel(x + 1, y + 1);

        if (PixelAbove == 0) {
            sw.setPixel(x, y + 1, PixelBuf);
            sw.setPixel(x, y, 0);
        } else if (PixelAboveLeft == 0 || PixelAboveRight == 0) {
            if (PixelAboveLeft == 0 && (PixelAboveRight != 0 || sw.random.nextBoolean())) {
                sw.setPixel(x - 1, y + 1, PixelBuf);
                sw.setPixel(x, y, 0);
            } else {
                sw.setPixel(x + 1, y + 1, PixelBuf);
                sw.setPixel(x, y, 0);
            }
        }
    }
}
