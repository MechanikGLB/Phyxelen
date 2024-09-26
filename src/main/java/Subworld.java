import java.util.ArrayList;
import java.util.Hashtable;
//import java.
import java.io.*;

public class Subworld {
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
        if (Main.game.gameState == GameApp.GameState.Server) return; // TODO: multiplayer, require chunk via net

        //if () {} // TODO: load from file. True if found

        loadedChunks.put(indexes, generator.generateChunk(indexes));
    }


    void unloadChunk(VectorI indexes) {
        // TODO: write to file
        loadedChunks.remove(indexes);
    }
}
