import java.util.ArrayList;
import java.util.Hashtable;
//import java.
import java.io.*;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Subworld extends GameObject {
    World world = null;
    SubworldRenderer renderer;
    WorldGenerator generator;
    Random random = new Random();
    byte counter = 0;
    ConcurrentHashMap<VectorI, Chunk> activeChunks = new ConcurrentHashMap<>();
    Hashtable<VectorI, Chunk> passiveChunks = new Hashtable<>();
    ArrayList<Entity> entities = new ArrayList<>();

    ArrayList<Entity> entitiesToAdd = new ArrayList<>();
    ArrayList<Entity> entitiesToRemove = new ArrayList<>();
    File saveFile;


    public Subworld(World world, String subworldId) {
        this.world = world;

        saveFile = new File(world.path + File.separator + "subworlds" + File.separator + subworldId);
        /*TEMP*/ generator = new WorldGenerator(this);
    }


    public void update(float dt) {
        GameApp.Profiler.startProfile("tick", (byte)0, (byte)100, (byte)100);
//        Thread[] threads = new Thread[activeChunks.size()];
//        activeChunks.values().toArray(threads);
        for (var chunk : activeChunks.values())
            chunk.tick();
//        for (Thread thread : threads)
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }

        for (var chunk : activeChunks.values())
            chunk.pixelSolved.clear();
//        for (var chunk : activeChunks.entrySet())
//            chunk.getValue().swapBuffer();
        for (Entity entity : entities)
            entity.update(dt);

        entities.removeAll(entitiesToRemove);
        entitiesToRemove.clear();
        entities.addAll(entitiesToAdd);
        entitiesToAdd.clear();
        counter++;
        GameApp.Profiler.endProfile("tick");
    }


    @Override
    void draw(float fdt) {
        renderer.draw(fdt);
        for (var entity : entities)
            entity.draw(fdt);
    }

    void loadChunk(VectorI indexes) {
        if (activeChunks.containsKey(indexes)) return;
        if (Main.getGame().gameState == GameApp.GameState.Server) return; // TODO: multiplayer, require chunk via net

        //if () {} // TODO: load from file. True if found
        Chunk chunk = generator.generateChunk(indexes);
        activeChunks.put(indexes, chunk);
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


    Pixel getPixel(int x, int y) {
        return new Pixel(
                getChunkHavingPixel(x, y),
                Chunk.toRelative(x) + Chunk.toRelative(y) * Chunk.size()
        );
    }


    void setPixel(int x, int y, Material material, byte color) {
//        assert world.pixelIds.length >= pixel;

//        Main.getGame().logicSemaphore.tryAcquire();
        Chunk chunk = getChunkHavingPixel(x, y);
        if (chunk == null) return; // TODO: decide what to do in this case

        chunk.setPixel(x, y, material, color);
//        Main.getGame().logicSemaphore.release();
    }


    void presetPixel(int x, int y, Material material, byte color) {
//        assert world.pixelIds.length >= pixel.material.id;
        Chunk chunk = getChunkHavingPixel(x, y);
        if (chunk == null) return;
        chunk.presetPixel(x, y, material, color);
    }


//    void swapPixels(Pixel pixel1, Pixel pixel2) {
//        int xBuffer = pixel1.x;
//        int yBuffer = pixel1.y;
//        pixel1.x = pixel2.x;
//        pixel1.y = pixel2.y;
//        pixel2.x = xBuffer;
//        pixel2.y = yBuffer;
//        setPixel(pixel1);
//        setPixel(pixel2);
//    }


    Material getPixelMaterial(int x, int y) {
        Chunk chunk = getChunkHavingPixel(x, y);
        if (chunk == null) return null;
        return chunk.getPixelMaterialChecked(x, y);
    }


//    boolean getPixelPhysicSolved(int x, int y) {
//        Chunk chunk = getChunkHavingPixel(x, y);
//        if (chunk == null) return true;
//        return chunk.getPixelPhysicSolved(Chunk.toRelative(x), Chunk.toRelative(y));
//    }


//    Material getMaterial(int pixel) {
//        return world.pixelIds[Pixels.getId(pixel)];
//    }
//    Material getMaterial(int x, int y)


    void addEntity(Entity entity) {
        entitiesToAdd.add(entity);
    }

    void removeEntity(Entity entity) {
        entitiesToRemove.add(entity);
    }
}
