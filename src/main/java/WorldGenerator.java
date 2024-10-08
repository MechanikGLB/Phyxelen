import java.math.*;
import java.util.Random;

/// WorldGenerator holds data for one subworld generation.
public class WorldGenerator {
    Subworld subworld;
    int[] rectangles;
    Random random = new Random();

    public WorldGenerator(Subworld subworld) {
        this.subworld = subworld;
    }

    Chunk generateChunk(VectorI indexes) {
        System.out.printf("Generates chunk (%d;%d)\n", indexes.x, indexes.y);
        Chunk chunk = new Chunk(subworld);
        int baseX = Chunk.size() * indexes.x;
        int baseY = Chunk.size() * indexes.y;
        int i = 0;
        for (int y = 0; y < Chunk.size(); y++)
            for (int x = 0; x < Chunk.size(); x++) {
                if (baseY + y + Math.sin((baseX + x)*0.5)*50 < 0) {
//                if (Math.sqrt(Math.pow(baseX + x, 2) + Math.pow(baseY + y, 2)) < 40) {
                    chunk.presetPixel(new Pixel(Pixels.getPixelWithRandomColor(1), null, baseX + x, baseY + y));
                } else {
                    chunk.pixels[i] = (new Pixel(0, chunk, baseX + x, baseY + y));
                }
        i++;
    }
        return chunk;
    }
}
