public class MaterialPowder extends Material {
    void solvePhysic(Subworld sw, Pixel pixel) {
        Pixel pixelUnder = sw.getPixel(pixel.x, pixel.y - 1);
        if (pixelUnder == null)
            return;
        if (pixelUnder.material.density < pixel.material.density) {
            pixel.y -= 1;
            pixelUnder.y += 1;
            pixel.swapByChunk(pixelUnder);
//            sw.swapPixels(pixel, pixelUnder);
        } else {
            Pixel pixelUnderLeft = sw.getPixel(pixel.x - 1, pixel.y - 1);
            boolean canMoveLeft =
                    (pixelUnderLeft != null &&
                            pixelUnderLeft.material.density < pixel.material.density);
            Pixel pixelUnderRight = sw.getPixel(pixel.x + 1, pixel.y - 1);
            boolean canMoveRight =
                    (pixelUnderRight != null &&
                            pixelUnderRight.material.density < pixel.material.density);
            if (canMoveLeft || canMoveRight) {
                if (canMoveLeft && (!canMoveRight || sw.random.nextBoolean())) {
                    pixel.x -= 1;
                    pixel.y -= 1;
                    pixelUnderLeft.x += 1;
                    pixelUnderLeft.y += 1;
                    pixel.swapByChunk(pixelUnderLeft);
                } else {
                    pixel.x += 1;
                    pixel.y -= 1;
                    pixelUnderRight.x -= 1;
                    pixelUnderRight.y += 1;
                    pixel.swapByChunk(pixelUnderRight);
                }
            }
        }
    }
}
