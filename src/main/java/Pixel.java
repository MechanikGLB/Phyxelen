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

    int x() {
        return chunk.xIndex * Chunk.size() + i % Chunk.size();
    }

    int y() {
        return chunk.yIndex * Chunk.size() + i / Chunk.size();
    }
}
