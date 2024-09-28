import java.util.ArrayList;
import java.util.Hashtable;
//import java.
import java.io.*;

public class Subworld {
    static int notLoadedPixel = 0x80000000;

    World world = null;
    WorldGenerator generator;
    Hashtable<VectorI, Chunk> loadedChunks = new Hashtable<>();
    File saveFile;


    public Subworld(World world, String subworldId) {
        this.world = world;

        saveFile = new File(world.path + File.separator + "subworlds" + File.separator + subworldId);
        /*TEMP*/ generator = new WorldGenerator();
    }


    public void tick(float dt) {
        for (Chunk chunk : loadedChunks.values()) {
            int x = 0;
            int y = 0;
            // TODO: Pixel physics
//            for (int i = 0; i < chunk.length; i++) {
//                PixelDefinition pixelDef = world.pixelIds[chunk[i]];
//
//            }
        }
    }


    void loadChunk(VectorI indexes) {
        if (loadedChunks.containsKey(indexes)) return;
        if (Main.getGame().gameState == GameApp.GameState.Server) return; // TODO: multiplayer, require chunk via net

        //if () {} // TODO: load from file. True if found

        loadedChunks.put(indexes, generator.generateChunk(indexes));
    }


    void unloadChunk(VectorI indexes) {
        // TODO: write to file
        loadedChunks.remove(indexes);
    }


    /// Sets `pixel` at absolute `x` and `y`
    void setPixel(int x, int y, int pixel) {
        assert world.pixelIds.length >= pixel;

        Chunk chunk = loadedChunks.get(new VectorI(
                x >= 0 ? x / Chunk.size() : (x+1) / Chunk.size() - 1,
                y >= 0 ? y / Chunk.size() : (y+1) / Chunk.size() - 1));

        if (chunk == null) return; // TODO: decide what to do in this case

        x %= Chunk.size();
        y %= Chunk.size();

        // Correcting coordinates in negative chunks
        if (x < 0) x += Chunk.size();
        if (y < 0) y += Chunk.size();
        chunk.setPixel(x, y, pixel);
    }


    int getPixel(int x, int y) {
        Chunk chunk = loadedChunks.get(new VectorI(
                x >= 0 ? x / Chunk.size() : (x+1) / Chunk.size() - 1,
                y >= 0 ? y / Chunk.size() : (y+1) / Chunk.size() - 1));
        if (chunk == null) return notLoadedPixel;
        x %= Chunk.size();
        y %= Chunk.size();
        // Correcting coordinates in negative chunks
        if (x < 0) x = Chunk.size() - x;
        if (y < 0) y = Chunk.size() - y;
        return chunk.getPixel(x, y);
    }
}
