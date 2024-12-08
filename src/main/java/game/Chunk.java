package game;

import java.util.BitSet;

public class Chunk {
    /// Width (and height) of chunk in world pixels. Always same for all chunks during session
    private static final short size = 32;
    private static final int area = size * size;
    /// Owning subworld
    Subworld subworld;
    // Index within `chunks` array of subworld
    int xIndex;
    int yIndex;

    Material[] materials;
    byte[] colors;
    BitSet pixelSolved;

    /// Is physic has been solved for this chunk. If yes, it will be skipped. Not used now
    boolean solved = false;

    public Chunk(Subworld subworld) {
        this.subworld = subworld;
        materials = new Material[area];
        colors = new byte[area];
        pixelSolved = new BitSet(area);
    }

    /// game.Chunk side size in pixels
    static short size() {return size;}
    static int area() {return area;}

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
        if (color == -1) {
            color = (byte) GameApp.activeSubworld.random.nextInt(material.colors.length);
        }
        colors[i] = color;
    }

    public void presetPixel(int x, int y, Material material, byte color) {
//        assert pixel.x < size && pixel.y < size;
        presetPixel(toRelative(x) + toRelative(y) * size, material, color);
    }

//    public game.Pixel getPixel(int x, int y) {
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
//        if (solved && (Main.getGame().counter + yIndex) % 8 != 0) return;
//        solved = true;
//            threads[i] = new Thread(() -> {
        if (subworld.pixelPhysicPhase % 2 == 0) {
            for (int i = 0; i < area; i += 1) {
                if (pixelSolved.get(i))
                    continue;
//                pixelSolved.set(i, true);
                materials[i].solvePhysic(this, i);
            }
        } else {
            for (int y = 0; y < size; y++) {
                for (int x = size - 1; x >= 0; x--) {
                    var i = x + y * size;
                    if (pixelSolved.get(i))
                        continue;
                    materials[i].solvePhysic(this, i);
                }
            }
        }
//        else
//            for (int i = area - 1; i > 0 ; i--) {
//                if (pixelSolved.get(i))
//                    continue;
//                materials[i].solvePhysic(this, i);
//        if (subworld.pixelPhysicPhase)
//            for (int x = 0; x < size; x++) {
//                for (int y = 0; y < size; y++) {
//                    var i = x + y * size;
//                    if (pixelSolved.get(i))
//                        continue;
//                    materials[i].solvePhysic(this, i);
//                }
//            }
//        else
//            for (int y = 0; y < size; y++) {
//                for (int x = size - 1; x >= 0; x--) {
//                    var i = x + y * size;
//                    if (pixelSolved.get(i))
//                        continue;
//                    materials[i].solvePhysic(this, i);
//                }
//            }
//                int x = i % size; int y = i / size;
//                int newI = y + x * size;
//                materials[i].solvePhysic(this, i);
//            }
    }

    Pixel getSolvedPixel(int i) {
        if (!pixelSolved.get(i)) {
            pixelSolved.set(i, true);
            materials[i].solvePhysic(this, i);
        }
        return new Pixel(this, i);
    }


    Pixel getPixelRightNeighbor(int i) {
        if (i % size == size - 1) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex + 1, yIndex));
            if (neighborChunk == null)
                return null;
            return Pixel.get(neighborChunk, i - size + 1);
        } else
            return Pixel.get(this, i + 1);
    }
    Pixel getPixelTopNeighbor(int i) {
        if (i < area - size)
            return new Pixel(this, i + size);
        else {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex, yIndex + 1));
            if (neighborChunk == null)
                return null;
            return new Pixel(neighborChunk, size - (area - i));
        }
    }
    Pixel getPixelTopNeighborChecked(int i) {
        if (i < area - size) {
            return getSolvedPixel(i + size);
        }
        else {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex, yIndex + 1));
            if (neighborChunk == null)
                return null;
            return neighborChunk.getSolvedPixel(size - (area - i));
        }
    }
    Pixel getPixelLeftNeighbor(int i) {
        if (i % size == 0) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex - 1, yIndex));
            if (neighborChunk == null)
                return null;
            return Pixel.get(neighborChunk, i + size - 1);
        } else
            return new Pixel(this, i - 1);
    }
    // TODO: reorder conditions to make the most frequent first
    Pixel getPixelBottomLeftNeighbor(int i) {
        if (i < size && i % size == 0) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex - 1, yIndex - 1));
            if (neighborChunk == null)
                return null;
            return Pixel.get(neighborChunk, area - 1);
        }
        else if (i < size) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex, yIndex - 1));
            if (neighborChunk == null)
                return null;
            return Pixel.get(neighborChunk, i + size * (size - 1) - 1);
        }
        else if (i % size == 0) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex - 1, yIndex));
            if (neighborChunk == null)
                return null;
            return Pixel.get(neighborChunk, i - 1);
        }
        else
            return Pixel.get(this, i - size - 1);
    }
    Pixel getPixelBottomLeftNeighborChecked(int i) {
        if (i < size && i % size == 0) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex - 1, yIndex - 1));
            if (neighborChunk == null)
                return null;
            return neighborChunk.getSolvedPixel(area - 1);
        }
        else if (i < size) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex, yIndex - 1));
            if (neighborChunk == null)
                return null;
            return neighborChunk.getSolvedPixel(i + size * (size - 1) - 1);
        }
        else if (i % size == 0) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex - 1, yIndex));
            if (neighborChunk == null)
                return null;
            return neighborChunk.getSolvedPixel(i - 1);
        }
        else
            return getSolvedPixel(i - size - 1);
    }
    Pixel getPixelBottomNeighbor(int i) {
        if (i < size) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex, yIndex - 1));
            if (neighborChunk == null)
                return null;
            else
                return Pixel.get(neighborChunk, i + size * (size - 1));
        }
        else
            return Pixel.get(this, i - size);
    }
    Pixel getPixelBottomRightNeighbor(int i) {
        if (i < size && i % size == size - 1) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex + 1, yIndex - 1));
            if (neighborChunk == null)
                return null;
            return Pixel.get(neighborChunk, area - size);
        }
        else if (i < size) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex, yIndex - 1));
            if (neighborChunk == null)
                return null;
            return Pixel.get(neighborChunk, i + size * (size - 1) + 1);
        }
        else if (i % size == size - 1) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex + 1, yIndex));
            if (neighborChunk == null)
                return null;
            return Pixel.get(neighborChunk, i - size - size + 1);
        }
        else
            return Pixel.get(this, i - size + 1);
    }
    Pixel getPixelBottomRightNeighborChecked(int i) {
        if (i < size && i % size == size - 1) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex + 1, yIndex - 1));
            if (neighborChunk == null)
                return null;
            return neighborChunk.getSolvedPixel(area - size);
        }
        else if (i < size) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex, yIndex - 1));
            if (neighborChunk == null)
                return null;
            return neighborChunk.getSolvedPixel(i + size * (size - 1) + 1);
        }
        else if (i % size == size - 1) {
            Chunk neighborChunk = subworld.activeChunks.get(new VectorI(xIndex + 1, yIndex));
            if (neighborChunk == null)
                return null;
            return neighborChunk.getSolvedPixel(i - size - size + 1);
        }
        else
            return getSolvedPixel( i - size + 1);
    }
}
