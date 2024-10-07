public class MaterialGas extends Material {
    void resolvePhysics(Subworld sw, int x, int y) {
        int pixelBuf = sw.getPixel(x, y);
        int pixelAbove = sw.getPixel(x, y + 1);

        if (pixelAbove == 0) {
            sw.setPixel(x, y + 1, pixelBuf);
            sw.setPixel(x, y, 0);
        } else {
            int pixelAboveLeft = sw.getPixel(x - 1, y + 1);
            int pixelAboveRight = sw.getPixel(x + 1, y + 1);
            if (pixelAboveLeft == 0 || pixelAboveRight == 0) {
                if (pixelAboveLeft == 0 && (pixelAboveRight != 0 || sw.random.nextBoolean())) {
                    sw.setPixel(x - 1, y + 1, pixelBuf);
                    sw.setPixel(x, y, 0);
                } else {
                    sw.setPixel(x + 1, y + 1, pixelBuf);
                    sw.setPixel(x, y, 0);
                }
            } else {
                int pixelLeft = sw.getPixel(x - 1, y);
                int pixelRight = sw.getPixel(x + 1, y);
                if (pixelLeft == 0 || pixelRight == 0) {
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
    }
}
