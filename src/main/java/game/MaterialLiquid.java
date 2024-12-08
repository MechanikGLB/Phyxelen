package game;

public class MaterialLiquid extends Material {
    void solvePhysic(Chunk chunk, int i) {
        Material selfMaterial = chunk.materials[i];
        byte selfColor = chunk.colors[i];
        Pixel pixelUnder = chunk.getPixelBottomNeighbor(i);
        if (pixelUnder == null)
            return;
        Material pixelUnderMaterial = pixelUnder.material();
        if (pixelUnderMaterial.density < selfMaterial.density && pixelUnder.canBeReplaced()) {
            chunk.setPixel(i, pixelUnderMaterial, pixelUnder.color());
            pixelUnder.chunk.setPixel(pixelUnder.i, selfMaterial, selfColor);
        } else {
            Pixel pixelUnderLeft = chunk.getPixelBottomLeftNeighbor(i);
            if (pixelUnderLeft == null)
                return;
            Material pixelUnderLeftMaterial = pixelUnderLeft.material();
            boolean canMoveUnderLeft = pixelUnderLeftMaterial.density < selfMaterial.density && pixelUnderLeft.canBeReplaced();
            Pixel pixelUnderRight = chunk.getPixelBottomRightNeighbor(i);
            if (pixelUnderRight == null)
                return;
            Material pixelUnderRightMaterial = pixelUnderRight.material();
            boolean canMoveUnderRight = pixelUnderRightMaterial.density < selfMaterial.density && pixelUnderRight.canBeReplaced();
            if (canMoveUnderLeft || canMoveUnderRight) {
                if (canMoveUnderLeft && (!canMoveUnderRight || chunk.subworld.random.nextBoolean())) {
                    chunk.setPixel(i, pixelUnderLeftMaterial, pixelUnderLeft.color());
                    pixelUnderLeft.chunk.setPixel(pixelUnderLeft.i, selfMaterial, selfColor);
                } else {
                    chunk.setPixel(i, pixelUnderRightMaterial, pixelUnderRight.color());
                    pixelUnderRight.chunk.setPixel(pixelUnderRight.i, selfMaterial, selfColor);
                }
            } else {
                Pixel pixelLeft = chunk.getPixelLeftNeighbor(i);
                if (pixelLeft == null)
                    return;
                Material pixelLeftMaterial = pixelLeft.material();
                boolean canMoveLeft = pixelLeftMaterial.density < selfMaterial.density && pixelLeft.canBeReplaced();
                Pixel pixelRight = chunk.getPixelRightNeighbor(i);
                if (pixelRight == null)
                    return;
                Material pixelRightMaterial = pixelRight.material();
                boolean canMoveRight = pixelRightMaterial.density < selfMaterial.density && pixelRight.canBeReplaced();
                if (canMoveLeft || canMoveRight) {
                    if (canMoveLeft && (!canMoveRight || chunk.subworld.random.nextBoolean())) {
                        chunk.setPixel(i, pixelLeftMaterial, pixelLeft.color());
                        pixelLeft.chunk.setPixel(pixelLeft.i, selfMaterial, selfColor);
                    } else {
                        chunk.setPixel(i, pixelRightMaterial, pixelRight.color());
                        pixelRight.chunk.setPixel(pixelRight.i, selfMaterial, selfColor);
                    }
                }
            }
        }
    }
}
