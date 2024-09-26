import java.math.*;

/// WorldGenerator holds data for one subworld generation.
public class WorldGenerator {
    int[] rectangles;

    Chunk generateChunk(VectorI indexes) {
        System.out.printf("Generates chunk (%d;%d)\n", indexes.x, indexes.y);
        Chunk chunk = new Chunk();
        int baseX = Chunk.size() * indexes.x;
        int baseY = Chunk.size() * indexes.y;
        chunk.pixels = new int[Chunk.area()];
        int i = 0;
        for (int x = 0; x < Chunk.size(); x++)
        for (int y = 0; y < Chunk.size(); y++) {
//            if (startY + y < Math.sin((startX + x) / 10.0)) {
            if (Math.sin((baseX + x) * 0.3f) - Math.cos((baseY + y)*0.3f) > 0) {
                chunk.pixels[i] = 1;
            } else {
                chunk.pixels[i] = 0;
            }
            i++;
        }
        return chunk;
    }
}
