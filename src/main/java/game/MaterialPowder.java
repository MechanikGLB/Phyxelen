package game;

public class MaterialPowder extends Material {
    void solvePhysic(Chunk chunk, int i) {
        Material selfMaterial = chunk.materials[i];
        byte selfColor = chunk.colors[i];
        Pixel pixelUnder = chunk.getPixelBottomNeighbor(i);
        if (pixelUnder == null)
            return;
        Material pixelUnderMaterial = pixelUnder.material();
        if (pixelUnderMaterial.density < selfMaterial.density) {
            chunk.setPixel(i, pixelUnderMaterial, pixelUnder.color());
            pixelUnder.chunk.setPixel(pixelUnder.i, selfMaterial, selfColor);
        } else {
            Pixel pixelUnderLeft = chunk.getPixelBottomLeftNeighbor(i);
            if (pixelUnderLeft == null)
                return;
            Material pixelUnderLeftMaterial = pixelUnderLeft.material();
            boolean canMoveLeft = pixelUnderLeftMaterial.density < selfMaterial.density;
            Pixel pixelUnderRight = chunk.getPixelBottomRightNeighbor(i);
            if (pixelUnderRight == null)
                return;
            Material pixelUnderRightMaterial = pixelUnderRight.material();
            boolean canMoveRight = pixelUnderRightMaterial.density < selfMaterial.density;
            if (canMoveLeft || canMoveRight) {
                if (canMoveLeft && (!canMoveRight || chunk.subworld.random.nextBoolean())) {
                    chunk.setPixel(i, pixelUnderLeftMaterial, pixelUnderLeft.color());
                    pixelUnderLeft.chunk.setPixel(pixelUnderLeft.i, selfMaterial, selfColor);
                } else {
                    chunk.setPixel(i, pixelUnderRightMaterial, pixelUnderRight.color());
                    pixelUnderRight.chunk.setPixel(pixelUnderRight.i, selfMaterial, selfColor);
                }
            }
        }
    }
}
