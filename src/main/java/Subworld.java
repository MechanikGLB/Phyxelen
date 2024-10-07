import java.util.ArrayList;
import java.util.Hashtable;
//import java.
import java.io.*;
import java.util.Random;

public class Subworld {
    World world = null;
    WorldGenerator generator;
    Random random = new Random();
    Hashtable<VectorI, Chunk> activeChunks = new Hashtable<>();
    Hashtable<VectorI, Chunk> passiveChunks = new Hashtable<>();
    ArrayList<Entity> entities = new ArrayList<>();
    ArrayList<Entity> entitiesToRemove = new ArrayList<>();
    File saveFile;


    public Subworld(World world, String subworldId) {
        this.world = world;

        saveFile = new File(world.path + File.separator + "subworlds" + File.separator + subworldId);
        /*TEMP*/ generator = new WorldGenerator();
    }


    public void tick(float dt) {
        for (var chunk : activeChunks.entrySet()) {
            int x = chunk.getKey().x * Chunk.size();
            int y = chunk.getKey().y * Chunk.size();
            for (int i = 0; i < Chunk.area(); i++) {
                Material material = world.pixelIds[Pixels.getId(chunk.getValue().pixels[i])];
                material.resolvePhysics(this, x + i % Chunk.size(), y + i / Chunk.size());
            }
        }
        for (Entity entity : entities)
            entity.tick(dt);

        entities.removeAll(entitiesToRemove);
        entitiesToRemove.clear();
    }


    void loadChunk(VectorI indexes) {
        if (activeChunks.containsKey(indexes)) return;
        if (Main.getGame().gameState == GameApp.GameState.Server) return; // TODO: multiplayer, require chunk via net

        //if () {} // TODO: load from file. True if found

        activeChunks.put(indexes, generator.generateChunk(indexes));
    }


    void unloadChunk(VectorI indexes) {
        // TODO: write to file
        activeChunks.remove(indexes);
    }


    void updateChunksForUser(int centerX, int centerY, int width, int height) {
        ArrayList<VectorI> toDeactivate = new ArrayList<>();
        for (var active : activeChunks.entrySet()) {
            if (active.getKey().x < centerX - width ||
                active.getKey().x > centerX + width ||
                active.getKey().y < centerX - height ||
                active.getKey().y > centerX + height
            ) {
                passiveChunks.put(active.getKey(), active.getValue());
                toDeactivate.add(active.getKey());
            }
        }
        for (var key : toDeactivate)
            activeChunks.remove(key);

        for (int x = -width; x <= width; x++) {
            for (int y = -height; y <= height; y++) {
                VectorI indexes = new VectorI(x + centerX, y + centerY);
                Chunk passive = passiveChunks.get(indexes);
                if (passive != null) {
                    activeChunks.put(indexes, passive);
                    passiveChunks.remove(indexes);
                }
                else if (!activeChunks.containsKey(indexes)) {
                    loadChunk(indexes);
                }
            }
        }
    }


    public Chunk getChunkHavingPixel(int x, int y) {
        return activeChunks.get(new VectorI(
                x >= 0 ? x / Chunk.size() : (x+1) / Chunk.size() - 1,
                y >= 0 ? y / Chunk.size() : (y+1) / Chunk.size() - 1));
    }


    /// Sets `pixel` at absolute `x` and `y`
    void setPixel(int x, int y, int pixel) {
        assert world.pixelIds.length >= pixel;

        Chunk chunk = getChunkHavingPixel(x, y);
        if (chunk == null) return; // TODO: decide what to do in this case

        chunk.setPixel(Chunk.toRelative(x), Chunk.toRelative(y), pixel);
    }


    int getPixel(int x, int y) {
        Chunk chunk = getChunkHavingPixel(x, y);
        if (chunk == null) return Pixels.notLoadedPixel;
        return chunk.getPixel(Chunk.toRelative(x), Chunk.toRelative(y));
    }


    void removeEntity(Entity entity) {
        entitiesToRemove.add(entity);
    }
}
