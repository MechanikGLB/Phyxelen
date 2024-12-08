package game;

import java.util.*;
//import java.
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL21.*;

class HorizontalChunkTree extends TreeSet<Chunk> {
//    private final Comparator<Chunk> comparator = (o1, o2) -> o2.xIndex - o1.xIndex;

    public HorizontalChunkTree() {
        super((o1, o2) -> o2.xIndex - o1.xIndex);
    }
}

class ChunkTree extends TreeSet<HorizontalChunkTree> {
    public ChunkTree() {
        super((o1, o2) -> o1.first().yIndex - o2.first().yIndex);
    }

    public boolean add(Chunk chunk) {
        HorizontalChunkTree subtree = null;

        for (var chunks : this)
            if (chunks.first().yIndex == chunk.yIndex) {
                subtree = chunks;
                break;
            }
        if (subtree != null)
            return subtree.add(chunk);
        else {
            var newSubtree = new HorizontalChunkTree();
            newSubtree.add(chunk);
            return add(newSubtree);
        }
    }

    public boolean remove(Chunk chunk) {
        HorizontalChunkTree subtree = null;

        for (var chunks : this)
            if (chunks.first().yIndex == chunk.yIndex) {
                subtree = chunks;
                break;
            }
        if (subtree != null) {
            if (subtree.size() == 1)
                remove(subtree);
            return subtree.remove(chunk);
        } else
            return false;
    }
}


public class Subworld extends GameObject {
    World world = null;
    SubworldRenderer renderer;
    WorldGenerator generator;
    Random random = new Random();
    byte counter = 0;
    float pixelPhysicCounter; /// Counter for pixel update rate
    byte pixelPhysicPhase = 0; // Maybe temporary
    byte pixelPhysicFreezingCountdown = -1; /// -1 -- do not stop, 0 -- stop, n -- stop after "n" steps
    ConcurrentHashMap<VectorI, Chunk> activeChunks = new ConcurrentHashMap<>();
    ChunkTree activeChunkTree = new ChunkTree();
//    TreeSet<Chunk> activeChunkTree = new TreeSet<>((chunk, t1) -> {
//        if (chunk.yIndex != t1.yIndex)
//            return chunk.yIndex - t1.yIndex;
//        return chunk.xIndex - t1.xIndex;
//    });

//    ArrayList<game.Chunk> activeChunkArray = new ArrayList<>();
    Hashtable<VectorI, Chunk> passiveChunks = new Hashtable<>();
    ArrayList<Entity> entities = new ArrayList<>();
    ArrayList<EntityWithCollision> collidableEntities = new ArrayList<>();

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
//        var chunkLineIterator = activeChunkTree.iterator().forEachRemaining();

//        for (Thread thread : threads)
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }

        pixelPhysicCounter += dt;
        if (pixelPhysicCounter >= 0.03) {
            if (pixelPhysicFreezingCountdown != 0) {
                for (var chunk : activeChunks.values())
                    chunk.pixelSolved.clear();
                Pixel.rewindPool();
                for (var chunkTree : activeChunkTree)
                    if (pixelPhysicPhase / 8 % 2 == 0) {
                        for (var chunk : chunkTree)
                            if (!chunk.solved)
                                chunk.tick();
                    } else {
                        chunkTree.descendingIterator().forEachRemaining((chunk) -> {
                            if (!chunk.solved)
                                chunk.tick();
                        });
                    }

                pixelPhysicCounter = 0;
                pixelPhysicPhase += 1;
            }
            if (pixelPhysicFreezingCountdown > 0)
                pixelPhysicFreezingCountdown -= 1;
        }

        for (Entity entity : entities)
            entity.update(dt);

        entities.removeAll(entitiesToRemove);
        collidableEntities.removeAll(entitiesToRemove);
        entitiesToRemove.clear();
        entities.addAll(entitiesToAdd);
        for (var entity : entitiesToAdd)
            if (entity instanceof EntityWithCollision && ((EntityWithCollision) entity).collidable)
                collidableEntities.add((EntityWithCollision) entity);
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

    public void loadChunk(VectorI indexes) {
        if (activeChunks.containsKey(indexes)) return;
        if (Main.getGame().gameState == GameApp.GameState.Client) return; // TODO: multiplayer, require chunk via net

        //if () {} // TODO: load from file. True if found
        Chunk chunk = generator.generateChunk(indexes);
        activeChunks.put(indexes, chunk);
        activeChunkTree.add(chunk);
    }


    public World world() { return world; }
    public Random random() { return random; }


    void unloadChunk(VectorI indexes) {
        // TODO: write to file
        activeChunks.remove(indexes);
    }


    public void updateChunksForUser(int centerX, int centerY, int width, int height) {
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
        for (var key : toDeactivate) {
            activeChunkTree.remove(activeChunks.get(key));
            activeChunks.remove(key);
        }

        for (int x = -width; x <= width; x++) {
            for (int y = -height; y <= height; y++) {
                VectorI indexes = new VectorI(x + centerX, y + centerY);
                Chunk passive = passiveChunks.get(indexes);
                if (passive != null) {
                    activeChunks.put(indexes, passive);
                    activeChunkTree.add(passive);
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


    public Pixel getPixel(int x, int y) {
        return Pixel.get(
                getChunkHavingPixel(x, y),
                Chunk.toRelative(x) + Chunk.toRelative(y) * Chunk.size()
        );
    }

    public Pixel getPixel(float x, float y) {
        return getPixel(round(x), round(y));
    }


    public void setPixel(int x, int y, Material material, byte color) {
//        assert world.pixelIds.length >= pixel;
        Chunk chunk = getChunkHavingPixel(x, y);
        if (chunk == null) return; // TODO: decide what to do in this case

        chunk.setPixel(x, y, material, color);
    }


    public void presetPixel(int x, int y, Material material, byte color) {
//        assert world.pixelIds.length >= pixel.material.id;
        Chunk chunk = getChunkHavingPixel(x, y);
        if (chunk == null) return;
        chunk.presetPixel(x, y, material, color);
    }


    /// Fills area with pixels of `material` with `color`
    public void fillPixels(int x, int y, int w, int h, Material material, byte color) {
        for (int dx = 0; dx < w; dx++) {
            for (int dy = 0; dy < h; dy++) {
                setPixel(x + dx, y + dy, material, color);
            }
        }
    }


//    Pixel vectorCast(float x, float y, float dx, float dy)


    Pixel rayCast(float x1, float y1, float x2, float y2) {
        int stepCount = round(max(abs(x2-x1), abs(y2-y1)));
        float xStep = (x2-x1)/stepCount;
        float yStep = (y2-y1)/stepCount;
        for (int i = 0; i < stepCount; i++) {
            float x = x1 + xStep * i;
            float y = y1 + yStep * i;
            Pixel pixel = getPixel(x, y);
            if (pixel.chunk == null)
                break;
            if (!pixel.isAir())
                return pixel;
        }
        return null;
    }


//    void swapPixels(game.Pixel pixel1, game.Pixel pixel2) {
//        int xBuffer = pixel1.x;
//        int yBuffer = pixel1.y;
//        pixel1.x = pixel2.x;
//        pixel1.y = pixel2.y;
//        pixel2.x = xBuffer;
//        pixel2.y = yBuffer;
//        setPixel(pixel1);
//        setPixel(pixel2);
//    }


//    Material getPixelMaterial(int x, int y) {
//        Chunk chunk = getChunkHavingPixel(x, y);
//        if (chunk == null) return null;
//        return chunk.getPixelMaterialChecked(x, y);
//    }


//    boolean getPixelPhysicSolved(int x, int y) {
//        game.Chunk chunk = getChunkHavingPixel(x, y);
//        if (chunk == null) return true;
//        return chunk.getPixelPhysicSolved(game.Chunk.toRelative(x), game.Chunk.toRelative(y));
//    }


//    game.Material getMaterial(int pixel) {
//        return world.pixelIds[game.Pixels.getId(pixel)];
//    }
//    game.Material getMaterial(int x, int y)


    public void addEntity(Entity entity) {
        entitiesToAdd.add(entity);
    }

    public void removeEntity(Entity entity) {
        entitiesToRemove.add(entity);
    }
}
