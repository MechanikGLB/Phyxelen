package game;

import java.util.ArrayList;

public class Pixel {
    Chunk chunk;
    int i;

    static private ArrayList<Pixel> pixelPool = new ArrayList<>(Chunk.area() * 10);
    static private int poolIndex = 0;

    public Pixel(Chunk chunk, int i) {
        this.chunk = chunk;
        this.i = i;
    }

    public static Pixel get(Chunk chunk, int i) {
        if (poolIndex == pixelPool.size()) {
            pixelPool.add(new Pixel(chunk, i));
            poolIndex++;
            return pixelPool.get(poolIndex - 1);
        }
        else {
            var pixel = pixelPool.get(poolIndex);
            pixel.chunk = chunk;
            pixel.i = i;
            poolIndex++;
            return pixel;
        }
    }

    public static void rewindPool() {
        poolIndex = 0;
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

    public boolean solved() { return chunk.pixelSolved.get(i); }

    public boolean isAir() {
        return material() == Content.airMaterial;
    }

    public boolean canBeReplaced() {
        return isAir() || !solved();
    }
}
