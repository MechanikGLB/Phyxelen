public class Pixel {
    Material material;
    byte color;
    Chunk chunk;
    int x;
    int y;
    boolean solved = false;
//    Pixel[] neighbors = new Pixel[8];

    public Pixel(Material material, byte color, Chunk chunk, int x, int y) {
        this.material = material;
        this.color = color;
        this.chunk = chunk;
        this.x = x;
        this.y = y;
    }
    public Pixel(int legacyPixel, Chunk chunk, int x, int y) {
        this.chunk = chunk;
        material = Main.getGame().activeWorld.pixelIds[Pixels.getId(legacyPixel)];
        color = (byte) Pixels.getColor(legacyPixel);
        this.x = x;
        this.y = y;
    }

    void solvePhysic() {
        if (solved) return;
        solved = true;
        material.solvePhysic(chunk.subworld, this);
    }

    void swapByChunk(Pixel other) {
        Chunk chunkBuffer = chunk;
        other.chunk.setPixel(this);
        chunkBuffer.setPixel(other);
    }
}
