public class Chunk {
    private static short size = 32;
    int[] pixels;

    /// Chunk side size in pixels
    static short size() {return size;}
    static int area() {return size * size;}
}
