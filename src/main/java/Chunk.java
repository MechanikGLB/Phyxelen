public class Chunk {
    private static short size = 32;
    int[] pixels;

    /// Chunk side size in pixels
    static short size() {return size;}
    static int area() {return size * size;}


    void setPixel(int x, int y, int pixel) {
        assert x < size && y < size;
        pixels[x + y * size] = pixel;
    }

    int getPixel(int x, int y) {
        assert x < size && y < size;
        return pixels[x + y * size];
    }
}
