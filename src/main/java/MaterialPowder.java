public class MaterialPowder extends Material {
    void resolvePhysics(Subworld sw, int x, int y) {
        int pixelSelf = sw.getPixel(x, y);
        Material materialSelf = sw.getMaterial(pixelSelf);
        int pixelUnder = sw.getPixel(x, y - 1);
        Material materialUnder = sw.getMaterial(pixelUnder);
        if (pixelUnder != Pixels.notLoadedPixel && sw.getMaterial(pixelUnder).density < materialSelf.density) {
            sw.setPixel(x, y - 1, pixelSelf);
            sw.setPixel(x, y, pixelUnder);
        } else {
            int pixelUnderLeft = sw.getPixel(x - 1, y - 1);
            boolean canMoveLeft =
                    (pixelUnderLeft != Pixels.notLoadedPixel &&
                            sw.getMaterial(pixelUnderLeft).density < materialSelf.density);
            int pixelUnderRight = sw.getPixel(x + 1, y - 1);
            boolean canMoveRight =
                    (pixelUnderRight != Pixels.notLoadedPixel &&
                            sw.getMaterial(pixelUnderRight).density < materialSelf.density);
            if (canMoveLeft || canMoveRight) {
                if (canMoveLeft && (!canMoveRight || sw.random.nextBoolean())) {
                    sw.setPixel(x - 1, y - 1, pixelSelf);
                    sw.setPixel(x, y, pixelUnderLeft);
                } else {
                    sw.setPixel(x + 1, y - 1, pixelSelf);
                    sw.setPixel(x, y, pixelUnderRight);
                }
            }
        }
    }
}
