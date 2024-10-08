public class MaterialLiquid extends Material {
    void solvePhysic(Subworld sw, Pixel pixel) {
        Pixel pixelUnder = sw.getPixel(pixel.x, pixel.y - 1);
        if (pixelUnder == null)
            return;
        if (pixelUnder.material.density < pixel.material.density) {
            pixel.y -= 1;
            sw.setPixel(pixel);
            pixelUnder.y += 1;
            sw.setPixel(pixelUnder);
        } else {
            Pixel pixelUnderLeft = sw.getPixel(pixel.x - 1, pixel.y - 1);
            boolean canMoveDownLeft =
                    (pixelUnderLeft != null &&
                            pixelUnderLeft.material.density < pixel.material.density);
            Pixel pixelUnderRight = sw.getPixel(pixel.x + 1, pixel.y - 1);
            boolean canMoveDownRight =
                    (pixelUnderRight != null &&
                            pixelUnderRight.material.density < pixel.material.density);
            if (canMoveDownLeft || canMoveDownRight) {
                if (canMoveDownLeft && (!canMoveDownRight || sw.random.nextBoolean())) {
                    pixel.x -= 1;
                    pixel.y -= 1;
                    pixelUnderLeft.x += 1;
                    pixelUnderLeft.y += 1;
                    sw.setPixel(pixel);
                    sw.setPixel(pixelUnderLeft);
                } else {
                    pixel.x += 1;
                    pixel.y -= 1;
                    pixelUnderRight.x -= 1;
                    pixelUnderRight.y += 1;
                    sw.setPixel(pixel);
                    sw.setPixel(pixelUnderRight);
                }
            } else {
                if (sw.random.nextBoolean()) {
                    Pixel pixelLeft = sw.getPixel(pixel.x - 1, pixel.y);
                    boolean canMoveLeft =
                            (pixelLeft != null &&
                                    pixelLeft.material.density < pixel.material.density);
                    if (canMoveLeft) {
                        pixel.x -= 1;
                        pixelLeft.x += 1;
                        sw.setPixel(pixel);
                        sw.setPixel(pixelLeft);
                    }
                    return;
                }
                Pixel pixelRight = sw.getPixel(pixel.x + 1, pixel.y);
                boolean canMoveRight =
                        (pixelRight != null &&
                                pixelRight.material.density < pixel.material.density);
                if (canMoveRight) {
                    pixel.x += 1;
                    pixelRight.x -= 1;
                    sw.setPixel(pixel);
                    sw.setPixel(pixelRight);
                }
            }
        }
    }
}
