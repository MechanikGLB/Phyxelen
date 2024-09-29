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
        for (var chunk : loadedChunks.entrySet()) {
            int x = chunk.getKey().x * Chunk.size();
            int y = chunk.getKey().y * Chunk.size();
            // TODO: integrate Pixel physics
            for (int i = 0; i < Chunk.area(); i++) {
                Material pixelDef = world.pixelIds[chunk.getValue().pixels[i]];
                PowderPhys(x + i % Chunk.size(), y + i / Chunk.size());
            }
        }
    }

    public void PowderPhys(int x, int y) {
        int PixelBuf = getPixel(x, y);
        int PixelUnder = getPixel(x, y - 1);
        int PixelUnderLeft = getPixel(x - 1, y - 1);
        int PixelUnderRight = getPixel(x + 1, y - 1);

        if (PixelUnder == 0) {
            setPixel(x, y - 1, PixelBuf);
            setPixel(x, y, 0);
        }
        else if (PixelUnderLeft == 0 || PixelUnderRight == 0) {
            if ((getPixel(x - 1, y - 1) == 0) == (getPixel(x - 1, y + 1) == 0)){
                //тут рандом прописать
            }
            else {
                if (PixelUnderLeft == 0){
                    setPixel(x - 1, y - 1, PixelBuf);
                    setPixel(x, y, 0);
                }
                else{
                    setPixel(x + 1, y - 1, PixelBuf);
                    setPixel(x, y, 0);
                }
            }
        }
    }// TODO: physics for another materials




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
        if (x < 0) x += Chunk.size();
        if (y < 0) y += Chunk.size();
        return chunk.getPixel(x, y);
    }
}
