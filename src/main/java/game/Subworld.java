package game;

import game.NetMessage.RequestChunk;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.*;

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
    int seed = random.nextInt();
    byte counter = 0;
    float pixelPhysicCounter; /// Counter for pixel update rate
    byte pixelPhysicPhase = 0; // Maybe temporary
    byte pixelPhysicFreezingCountdown = -1; /// -1 -- do not stop, 0 -- stop, n -- stop after "n" steps

    ConcurrentHashMap<VectorI, Chunk> activeChunks = new ConcurrentHashMap<>();
    ArrayList<VectorI> loadingChunks = new ArrayList<>();
    ChunkTree activeChunkTree = new ChunkTree();
    Stack<Chunk> chunksToAdd = new Stack<>();
    Hashtable<VectorI, Chunk> passiveChunks = new Hashtable<>();

    ArrayList<Entity> entities = new ArrayList<>();
    ArrayList<EntityWithCollision> collidableEntities = new ArrayList<>();
    ArrayList<Player> players = new ArrayList<>();

    ArrayList<Entity> entitiesToAdd = new ArrayList<>();
    ArrayList<Entity> entitiesToRemove = new ArrayList<>();
    File saveFile;


    public Subworld(World world, String subworldId) {
        this.world = world;

        saveFile = new File(world.path + File.separator + "subworlds" + File.separator + subworldId);
        /*TEMP*/ generator = new WorldGenerator(this);
    }

    public Chunk getActiveChunk(VectorI indexes) { return activeChunks.get(indexes); }
    public ArrayList<Player> getPlayers() { return players; }

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
        if (!chunksToAdd.isEmpty()) {
            loadedChunk(chunksToAdd.pop());
        }

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

        if (Main.getGame().gameState != GameApp.GameState.Local && counter % 8 == 0) {

        }
//        if (Main.getGame().gameState != GameApp.GameState.Local && counter % 8 == 0) {
//            for (Player player : players)
//               if (Main.getClient() != null)
//                   Main.getClient().addMessage(new PlayerSync(player));
//               else
//                   Main.getServer().broadcastMessage(new PlayerSync(player));
//        }

        counter++;
        GameApp.Profiler.endProfile("tick");
    }


    @Override
    void draw(float fdt) {
        renderer.draw(fdt);
        for (var entity : entities)
            entity.draw(fdt);
    }

    /// For host: makes requested chunk available in list of active chunks after function return.
    /// For joined client: requests chunk from server.
    public void loadChunk(VectorI indexes) {
        if (activeChunks.containsKey(indexes) || loadingChunks.contains(indexes))
            return;
        loadingChunks.add(indexes);
        if (Main.getGame().gameState == GameApp.GameState.Client){
            Main.getClient().addMessage(new RequestChunk(indexes.x, indexes.y));
            return;
        };

        //if () {} // TODO: load from file. True if found
        loadedChunk(generator.generateChunk(indexes));
    }

    public void loadedChunk(Chunk chunk) {
        var indexes = new VectorI(chunk.xIndex, chunk.yIndex);
        System.out.println("Loaded chunk "+chunk.xIndex+" "+chunk.yIndex);
        loadingChunks.remove(indexes);
        activeChunks.put(indexes, chunk);
        activeChunkTree.add(chunk);
    }

    public void receivedChunk(Chunk chunk) {
        chunksToAdd.add(chunk);
    }


    public World world() { return world; }
    public Random random() { return random; }


    public void updateChunksForUsers() {
        ArrayList<VectorI> toDeactivate = new ArrayList<>();
        int chunksX = 10;
        int chunksY = 8;

        // Unload chunks

        int[] startChunkX = new int[players.size()];
        int[] startChunkY = new int[players.size()];
        for (int i = 0; i < players.size(); i++) {
            startChunkX[i] = (int) (players.get(i).x / Chunk.size()) - chunksX / 2;
            startChunkY[i] = (int) (players.get(i).y / Chunk.size()) - chunksY / 2;
        }

        for (var active : activeChunks.entrySet()) {
            boolean seen = false;
            var chunk = active.getKey();
            for (int i = 0; i < players.size(); i++) {
                if (chunk.x >= startChunkX[i]
                        && chunk.x < startChunkX[i] + chunksX
                        && chunk.y >= startChunkY[i]
                        && chunk.y < startChunkY[i] + chunksY
                ) {
                    seen = true;
                    break;
                }
            }
            if (!seen) {
                passiveChunks.put(chunk, active.getValue());
                toDeactivate.add(chunk);
            }

        }
        for (var key : toDeactivate) {
            activeChunkTree.remove(activeChunks.get(key));
            activeChunks.remove(key);
        }

        // Load chunks

        if (players.isEmpty()) {
            return;
        }

        // Only for host, as clients request them themselves

        for (int x = 0; x < chunksX; x++) {
            for (int y = 0; y < chunksY; y++) {
                VectorI indexes = new VectorI(startChunkX[0] + x, startChunkY[0] + y);
                Chunk passive = passiveChunks.get(indexes);
                if (passive != null) {
                    loadedChunk(passive);
                    passiveChunks.remove(indexes);
                }
                else if (!activeChunks.containsKey(indexes)) {
                    loadChunk(indexes);
                }
            }
        }
    }


    public int worldCoordinateToChunkIndex(int coordinate) {
        return coordinate >= 0 ? coordinate / Chunk.size() : (coordinate+1) / Chunk.size() - 1;
    }


    public Chunk getChunkHavingPixel(int x, int y) {
        return activeChunks.get(new VectorI(
                worldCoordinateToChunkIndex(x),
                worldCoordinateToChunkIndex(y)));
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
    public void fillPixels(int x, int y, int w, int h, Material material, byte color, float maxReplaceDensity) {
        for (int dx = 0; dx < w; dx++) {
            for (int dy = 0; dy < h; dy++) {
                Pixel pixel = getPixel(x + dx, y + dy);
                if (pixel.chunk == null || maxReplaceDensity >= 0 && pixel.material().density > maxReplaceDensity)
                    continue;
                pixel.chunk.setPixel(pixel.i, material, color);
            }
        }
    }


//    Pixel vectorCast(float x, float y, float dx, float dy)


    Pixel rayCast(float x1, float y1, float x2, float y2, float minDensity) {
        int stepCount = round(max(abs(x2-x1), abs(y2-y1)));
        float xStep = (x2-x1)/stepCount;
        float yStep = (y2-y1)/stepCount;
        for (int i = 0; i < stepCount; i++) {
            float x = x1 + xStep * i;
            float y = y1 + yStep * i;
            Pixel pixel = getPixel(x, y);
            if (pixel.chunk == null)
                break;
            Material material = pixel.material();
            if (material.density >= minDensity)
                return pixel;
        }
        return null;
    }


    public void addEntity(Entity entity) {
        if (entity.isLocal()){
            switch (Main.getGame().getGameState()){
                case GameApp.GameState.Client:
                    Main.getClient().addMessage(entity.getSpawnMessage());
                    break;
                case GameApp.GameState.Server:
                    Main.getServer().broadcastMessage(entity.getSpawnMessage());
                    break;
                case GameApp.GameState.Local:
                    break;
            }
        }
        entitiesToAdd.add(entity);
    }

    public void addEntity(Entity entity,boolean isJet) {
        if (entity.isLocal() && !isJet){
            addEntity(entity);
            return;
        }
        entitiesToAdd.add(entity);
    }

    public void removeEntity(Entity entity) {
        entitiesToRemove.add(entity);
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void spawnPlayer(Player player) {
        int x = random.nextInt(-200, 200);
        int y = 220;

        // find on surface
        while (true) {
            boolean found = false;
            Chunk chunk = getChunkHavingPixel(x, y);
            if (chunk == null) {
                loadChunk(new VectorI(worldCoordinateToChunkIndex(x), worldCoordinateToChunkIndex(y)));
                chunk = getChunkHavingPixel(x, y);
            }
            for (int i = 0; i < Chunk.size(); i++) {
                if (chunk.getPixelMaterial(x, y) == Content.air()) {
                    y = chunk.yIndex * Chunk.size() + i;
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
            y += Chunk.size();
            chunk = getChunkHavingPixel(x, y);
        }

        y += 10;
        if (!players.contains(player)) {
            players.add(player);
            addEntity(player);

        }
        player.spawn(x, y, (short) random.nextInt());
    }

    public void jetPixels(int x, int y, int size) {
        size += 2;
        for (int dx = -size/2; dx <= size/2; dx++) {
            for (int dy = size/2; dy > -size/2; dy--) {
                Pixel pixel = getPixel(
                        x + dx, y + dy);
                if (pixel.chunk == null)
                    return;
                Material material = pixel.material();
                if (!(material instanceof MaterialAir) ) {
                    double angle = random.nextDouble(-Math.PI, Math.PI);
                    addEntity(new PixelEntity(
                            x + dx, y + dy,
                            this, material, pixel.color(),
                            (float)Math.sin(angle) * 100.f, (float)Math.cos(angle) * 100.f,
                            0, -9.8f
                    ),true);
                    pixel.chunk.setPixel(pixel.i, Content.airMaterial, (byte) 0);
                }
            }
        }
    }
}
