import java.util.Arrays;

public class Chunk {
    /// Width (and height) of chunk in world pixels. Always same for all chunks during session
    private static short size = 32;
    /// Owning subworld
    Subworld subworld;
    // Index within `chunks` array of subworld
    int yIndex = 0;
    /// World pixels of this chunk
    Pixel[] pixels;
    /// Is physic has been solved for this chunk. If yes, it will be skipped. Not used now
    boolean solved = false;

    public Chunk(Subworld subworld) {
        this.subworld = subworld;
        pixels = new Pixel[area()];
        for (int i = 0; i < area(); i++)
            pixels[i] = new Pixel(0, this, i % size, i / size);
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

    public void setPixel(Pixel pixel) {
//        assert pixel.x < size && pixel.y < size;
        solved = false;
        pixels[toRelative(pixel.x) + toRelative(pixel.y) * size] = pixel;
//        if (!(pixel.material instanceof MaterialAir))
            pixel.solved = true;
//        pixelPhysicSolved[x + y * size] = true;
//            pixels[x + y * size] = pixel;
        pixel.chunk = this;
    }

    public void presetPixel(Pixel pixel) {
//        assert pixel.x < size && pixel.y < size;
        solved = false;
        pixels[toRelative(pixel.x) + toRelative(pixel.y) * size] = pixel;
        pixel.chunk = this;
//        pixelBuffer[x + y * size] = pixel;
    }

    public Pixel getPixel(int x, int y) {
//        assert x < size && y < size;
//        if (pixelBuffer[x + y * size] != 0)
//            return pixelBuffer[x + y * size];
        return pixels[toRelative(x) + toRelative(y) * size];
    }

    public void tick() {
        if (solved && (Main.getGame().counter + yIndex) % 8 != 0) return;
        solved = true;
//            threads[i] = new Thread(() -> {
        for (Pixel pixel : pixels) {
//            if (pixel.solved)
//                solved = false;
            pixel.solvePhysic();
        }
    }

    void swapBuffer() {
//        pixels = pixelBuffer.clone();
        // TODO set not solved?
//        Arrays.fill(pixelPhysicSolved, false);
    }
}
