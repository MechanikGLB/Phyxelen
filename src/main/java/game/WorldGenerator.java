package game;

import java.util.Random;

import static java.lang.Math.*;

/// game.WorldGenerator holds data for one subworld generation.
public class WorldGenerator {
    Subworld subworld;
    int[] rectangles;
//    Random random = new Random(subworld.seed);

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
        Material air = Content.airMaterial;// GameApp.activeWorld.pixelIds[0];
        Material land = Content.getMaterial("sand");
        Material land2 = Content.getMaterial("stone");
        int i = 0;
        for (int y = 0; y < Chunk.size(); y++)
            for (int x = 0; x < Chunk.size(); x++) {
                float noise = OpenSimplex2S.noise2(subworld.seed, (baseX + x) * 0.01, (baseY + y) * 0.01);
                float factor = noise + (baseY + y + 60) * 0.006f;
                if (abs(baseX) > 600)
                    factor -= pow((abs(baseX + x) - 600) * 0.01f, 2f);
                if (factor < 0.05) {
                    chunk.presetPixel(i, land2, (byte) -1);
                } else if (factor < 0.2) {
                    chunk.presetPixel(i, land, (byte) -1);
                } else {
                    chunk.materials[i] = air;
                }
        i++;
    }
        return chunk;
    }
}
