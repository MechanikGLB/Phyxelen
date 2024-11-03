package game;

import java.util.Random;

/// game.WorldGenerator holds data for one subworld generation.
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
        chunk.xIndex = indexes.x;
        chunk.yIndex = indexes.y;
        int baseX = Chunk.size() * indexes.x;
        int baseY = Chunk.size() * indexes.y;
        Material air = GameApp.activeWorld.pixelIds[0];
        Material land = GameApp.activeWorld.pixelIds[1];
        int i = 0;
        for (int y = 0; y < Chunk.size(); y++)
            for (int x = 0; x < Chunk.size(); x++) {
                if (baseY + y < Math.sin((baseX + x)*0.1)*0.5*Math.abs(x+baseX)) {
                    chunk.presetPixel(i, land, (byte)GameApp.activeSubworld.random.nextInt(land.colors.length - 1));
                } else {
                    chunk.materials[i] = air;
                }
        i++;
    }
        return chunk;
    }
}
