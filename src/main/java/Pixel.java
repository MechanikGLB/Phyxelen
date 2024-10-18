public class Pixel {
    Chunk chunk;
    int i;

    public Pixel(Chunk chunk, int i) {
        this.chunk = chunk;
        this.i = i;
    }

    Material material() {
        return chunk.materials[i];
    }

    byte color() {
        return chunk.colors[i];
    }
}
