package game;

public class MaterialGas extends Material {
    void solvePhysic(Chunk chunk, int i) {
        Material selfMaterial = chunk.materials[i];
        byte selfColor = chunk.colors[i];
        Pixel pixelAbove = chunk.getPixelTopNeighborChecked(i);
        if (pixelAbove == null)
            return;
        Material pixelAboveMaterial = pixelAbove.material();
        if (pixelAboveMaterial.density > selfMaterial.density && !(pixelAboveMaterial instanceof MaterialSolid)) {
            chunk.setPixel(i, pixelAboveMaterial, pixelAbove.color());
            pixelAbove.chunk.setPixel(pixelAbove.i, selfMaterial, selfColor);
        }
//        game.Pixel pixelAbove = sw.getPixel(pixel.x, pixel.y + 1);
//        if (pixelAbove == null)
//            return;
//        if (pixelAbove.material.density > pixel.material.density && gasOrAir(pixelAbove)) {
//            pixel.y += 1;
//            sw.setPixel(pixel);
//            pixelAbove.y -= 1;
//            sw.setPixel(pixelAbove);
//        } else {
//            game.Pixel pixelAboveLeft = sw.getPixel(pixel.x - 1, pixel.y + 1);
//            boolean canMoveUpLeft =
//                    (pixelAboveLeft != null &&
//                            pixelAboveLeft.material.density > pixel.material.density &&
//                            gasOrAir(pixelAboveLeft));
//            game.Pixel pixelAboveRight = sw.getPixel(pixel.x + 1, pixel.y + 1);
//            boolean canMoveUpRight =
//                    (pixelAboveRight != null &&
//                            pixelAboveRight.material.density > pixel.material.density &&
//                            gasOrAir(pixelAboveRight));
//            if (canMoveUpLeft || canMoveUpRight) {
//                if (canMoveUpLeft && (!canMoveUpRight || sw.random.nextBoolean())) {
//                    pixel.x -= 1;
//                    pixel.y += 1;
//                    pixelAboveLeft.x += 1;
//                    pixelAboveLeft.y -= 1;
//                    sw.setPixel(pixel);
//                    sw.setPixel(pixelAboveLeft);
//                } else {
//                    pixel.x += 1;
//                    pixel.y += 1;
//                    pixelAboveRight.x -= 1;
//                    pixelAboveRight.y -= 1;
//                    sw.setPixel(pixel);
//                    sw.setPixel(pixelAboveRight);
//                }
//            } else {
//                if (sw.random.nextBoolean()) {
//                    game.Pixel pixelLeft = sw.getPixel(pixel.x - 1, pixel.y);
//                    boolean canMoveLeft =
//                            (pixelLeft != null &&
//                                    pixelLeft.material instanceof game.MaterialAir);
//                    if (canMoveLeft) {
//                        pixel.x -= 1;
//                        pixelLeft.x += 1;
//                        sw.setPixel(pixel);
//                        sw.setPixel(pixelLeft);
//                    }
//                    return;
//                }
//                game.Pixel pixelRight = sw.getPixel(pixel.x + 1, pixel.y);
//                boolean canMoveRight =
//                        (pixelRight != null &&
//                                pixelRight.material instanceof game.MaterialAir);
//                if (canMoveRight) {
//                    pixel.x += 1;
//                    pixelRight.x -= 1;
//                    sw.setPixel(pixel);
//                    sw.setPixel(pixelRight);
//                }
//            }
//        }
    }

//    boolean gasOrAir(game.Pixel pixel) {
//        return pixel.material instanceof game.MaterialGas ||
//                pixel.material instanceof game.MaterialAir;
//    }
}
