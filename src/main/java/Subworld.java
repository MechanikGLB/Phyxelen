import java.util.ArrayList;
import java.util.Hashtable;
//import java.
import java.io.*;

public class Subworld {
    World world = null;
//    ArrayList<int[]> pixels;
    Hashtable<VectorI, Chunk> loadedChunks = new Hashtable<>();
    File saveFile;


    public Subworld(World world, String subworldId) {
        this.world = world;

        saveFile = new File(world.path + File.separator + "subworlds" + File.separator + subworldId);
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


    }


    void unloadChunk(VectorI indexes) {

    }
}
