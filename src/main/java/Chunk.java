import java.util.Arrays;
import java.util.BitSet;

public class Chunk {
    /// Width (and height) of chunk in world pixels. Always same for all chunks during session
    private static short size = 32;
    /// Owning subworld
    Subworld subworld;
    // Index within `chunks` array of subworld
    int xIndex;
    int yIndex;
    // World pixel data

    Material[] materials;
    byte[] colors;
    BitSet pixelSolved;

    /// Is physic has been solved for this chunk. If yes, it will be skipped. Not used now
    boolean solved = false;

    public Chunk(Subworld subworld) {
        this.subworld = subworld;
        materials = new Material[area()];
        colors = new byte[area()];
        pixelSolved = new BitSet(area());
//        for (int i = 0; i < area(); i++)
//            pixels[i] = new Pixel(0, this, i % size, i / size);
    }

    /// Chunk side size in pixels
    static short size() {return size;}
    static int area() {return size * size;}

    static int toRelative(int coordinate) {
        coordinate %= Chunk.size();
        if (coordinate < 0)
            return coordinate + Chunk.size();
        else return coordinate;
    }

    public void setPixel(int i, Material material, byte color) {
        solved = false;
        materials[i] = material;
        colors[i] = color;
        pixelSolved.set(i, true);
    }

    public void setPixel(int x, int y, Material material, byte color) {
//        assert pixel.x < size && pixel.y < size;
        setPixel(toRelative(x) + toRelative(y) * size, material, color);
    }

    public void presetPixel(int i, Material material, byte color) {
        solved = false;
        materials[i] = material;
        colors[i] = color;
    }

    public void presetPixel(int x, int y, Material material, byte color) {
//        assert pixel.x < size && pixel.y < size;
        presetPixel(toRelative(x) + toRelative(y) * size, material, color);
    }

//    public Pixel getPixel(int x, int y) {
////        assert x < size && y < size;
//        return pixels[toRelative(x) + toRelative(y) * size];
//    }

    public Material getPixelMaterial(int x, int y) {
//        assert x < size && y < size;
        return materials[toRelative(x) + toRelative(y) * size];
    }

    public Material getPixelMaterialChecked(int x, int y) {
//        assert x < size && y < size;
        int i = toRelative(x) + toRelative(y) * size;
        if (!pixelSolved.get(i))
            materials[i].solvePhysic(this, i);
        return materials[i];
    }

    public void tick() {
        if (solved && (Main.getGame().counter + yIndex) % 8 != 0) return;
        solved = true;
//            threads[i] = new Thread(() -> {
        for (int i = 0; i < area(); i++) {
//            if (pixel.solved)
//                solved = false;
            materials[i].solvePhysic(this, i);
        }
    }


    Pixel getPixelLeftNeighbor(int i) {
        if (i % size == 0)
            return null;
        else
            return new Pixel(this, i - 1);
    }
    // TODO: reorder conditions to make the most frequent first
    Pixel getPixelBottomLeftNeighbor(int i) {
        if (i < size && i % size == 0) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex - 1, yIndex - 1));
            if (neighborChunk == null)
                return null;
            return null;
        }
        else if (i < size) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex, yIndex - 1));
            if (neighborChunk == null)
                return null;
            return new Pixel(neighborChunk, i + size * (size - 1));
        }
        else if (i % size == 0) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex - 1, yIndex));
            if (neighborChunk == null)
                return null;
            return new Pixel(neighborChunk, i + size - 1);
        }
        else
            return new Pixel(this, i - size - 1);
    }
    Pixel getPixelBottomNeighbor(int i) {
        if (i < size) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex, yIndex - 1));
            if (neighborChunk == null)
                return null;
            else
                return new Pixel(neighborChunk, i + size * (size - 1));
        }
        else
            return new Pixel(this, i - size);
    }
    Pixel getPixelBottomRightNeighbor(int i) {
        if (i < size && i % size == size - 1) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex + 1, yIndex - 1));
            if (neighborChunk == null)
                return null;
            return null;
        }
        else if (i < size) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex, yIndex - 1));
            if (neighborChunk == null)
                return null;
            return null; //new Pixel(neighborChunk, i + size * (size + 1));
        }
        else if (i % size == size - 1) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex + 1, yIndex));
            if (neighborChunk == null)
                return null;
            return new Pixel(neighborChunk, i - size - size + 1);
        }
        else
            return new Pixel(this, i - size + 1);
    }
}
