package game;

public class Pixel {
    Chunk chunk;
    int i;

    public Pixel(Chunk chunk, int i) {
        this.chunk = chunk;
        this.i = i;
    }

    public Chunk chunk() { return chunk; }

    public int i() { return i; }

    public Material material() {
        return chunk.materials[i];
    }

    public byte color() {
        return chunk.colors[i];
    }

    public int x() {
        return chunk.xIndex * Chunk.size() + i % Chunk.size();
    }

    public int y() {
        return chunk.yIndex * Chunk.size() + i / Chunk.size();
    }
}
