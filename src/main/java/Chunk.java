import java.util.Arrays;

public class Chunk {
    private static short size = 32;
    Subworld subworld;
    Pixel[] pixels;
//    int[] pixelBuffer;
//    boolean[] pixelPhysicSolved;

    public Chunk(Subworld subworld) {
        this.subworld = subworld;
        pixels = new Pixel[area()];
        for (int i = 0; i < area(); i++)
            pixels[i] = new Pixel(0, this, i % size, i / size);
//        pixelBuffer = new int[area()];
//        pixelPhysicSolved = new boolean[area()];
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
        pixels[toRelative(pixel.x) + toRelative(pixel.y) * size] = pixel;
//        if (!(pixel.material instanceof MaterialAir))
            pixel.solved = true;
//        pixelPhysicSolved[x + y * size] = true;
//            pixels[x + y * size] = pixel;
        pixel.chunk = this;
    }

    public void presetPixel(Pixel pixel) {
//        assert pixel.x < size && pixel.y < size;
        pixels[toRelative(pixel.x) + toRelative(pixel.y) * size] = pixel;
        pixel.chunk = this;
//        pixelBuffer[x + y * size] = pixel;
    }

    public Pixel getPixel(int x, int y) {
        assert x < size && y < size;
//        if (pixelBuffer[x + y * size] != 0)
//            return pixelBuffer[x + y * size];
        return pixels[toRelative(x) + toRelative(y) * size];
    }

//    public boolean getPixelPhysicSolved(int x, int y) {
//        assert x < size && y < size;
//        return pixelPhysicSolved[x + y * size];
//    }

    void swapBuffer() {
//        pixels = pixelBuffer.clone();
        // TODO set not solved?
//        Arrays.fill(pixelPhysicSolved, false);
    }
}
