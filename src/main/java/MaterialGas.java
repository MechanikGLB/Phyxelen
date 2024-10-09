public class MaterialGas extends Material {
    void solvePhysic(Subworld sw, Pixel pixel) {
        Pixel pixelAbove = sw.getPixel(pixel.x, pixel.y + 1);
        if (pixelAbove == null)
            return;
        if (pixelAbove.material.density > pixel.material.density && gasOrAir(pixelAbove)) {
            pixel.y += 1;
            sw.setPixel(pixel);
            pixelAbove.y -= 1;
            sw.setPixel(pixelAbove);
        } else {
            Pixel pixelAboveLeft = sw.getPixel(pixel.x - 1, pixel.y + 1);
            boolean canMoveUpLeft =
                    (pixelAboveLeft != null &&
                            pixelAboveLeft.material.density > pixel.material.density &&
                            gasOrAir(pixelAboveLeft));
            Pixel pixelAboveRight = sw.getPixel(pixel.x + 1, pixel.y + 1);
            boolean canMoveUpRight =
                    (pixelAboveRight != null &&
                            pixelAboveRight.material.density > pixel.material.density &&
                            gasOrAir(pixelAboveRight));
            if (canMoveUpLeft || canMoveUpRight) {
                if (canMoveUpLeft && (!canMoveUpRight || sw.random.nextBoolean())) {
                    pixel.x -= 1;
                    pixel.y += 1;
                    pixelAboveLeft.x += 1;
                    pixelAboveLeft.y -= 1;
                    sw.setPixel(pixel);
                    sw.setPixel(pixelAboveLeft);
                } else {
                    pixel.x += 1;
                    pixel.y += 1;
                    pixelAboveRight.x -= 1;
                    pixelAboveRight.y -= 1;
                    sw.setPixel(pixel);
                    sw.setPixel(pixelAboveRight);
                }
            } else {
                if (sw.random.nextBoolean()) {
                    Pixel pixelLeft = sw.getPixel(pixel.x - 1, pixel.y);
                    boolean canMoveLeft =
                            (pixelLeft != null &&
                                    pixelLeft.material instanceof MaterialAir);
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
                                pixelRight.material instanceof MaterialAir);
                if (canMoveRight) {
                    pixel.x += 1;
                    pixelRight.x -= 1;
                    sw.setPixel(pixel);
                    sw.setPixel(pixelRight);
                }
            }
        }
    }

    boolean gasOrAir(Pixel pixel) {
        return pixel.material instanceof MaterialGas ||
                pixel.material instanceof MaterialAir;
    }
}
