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
        for (int y = 0; y < Chunk.size(); y++)
            for (int x = 0; x < Chunk.size(); x++) {
                if (baseY + y + Math.sin((baseX + x)*0.5)*50 < 0) {
//                if (Math.sqrt(Math.pow(baseX + x, 2) + Math.pow(baseY + y, 2)) < 40) {
                    chunk.pixels[i] = 1;
                } else {
                    chunk.pixels[i] = 0;
                }
        i++;
    }
        return chunk;
    }
}
