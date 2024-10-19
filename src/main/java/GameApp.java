import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public abstract class GameApp {
    public enum GameState {
        MainMenu,
        Local, // Single player
        Server,
        Client
    }
    protected GameState gameState;
    protected static Content content = new Content();
    protected static World activeWorld;
    protected static Subworld activeSubworld;
    /// Counter is used for making some computations more rare
    protected short counter = 0;

    Thread logicThread;
    Semaphore logicSemaphore = new Semaphore(1);



    public GameState getGameState() {return gameState;}


    public void run() {
        checkDirectoryStructure();
        // temp
        File testWorldDir = new File("worlds" + File.separator + "test");
        if (!testWorldDir.exists())
            World.createWorld("test", "test");
        enterWorld(World.loadWorldByPath("test"));
    };

    protected abstract void loop();

    protected void tick(float dt) {
        activeWorld.tick(dt);
    }

    private void checkFolder(Path path) {
        Files.exists(path);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkDirectoryStructure() {
        for (Path path : Arrays.asList(
                Path.of("worlds"),
                Path.of("games"),
                Path.of("mods")
        )) {
            checkFolder(path);
        }
    }

    public void enterWorld(World world) {
        activeWorld = world;
        enterSubworld(activeWorld.loadOrCreateSubworld(activeWorld.defaultSubworldId));
    }

    public void enterSubworld(Subworld subworld) {
        activeSubworld = subworld;
    }

    static class Profiler {
        static class ProfilerEntry {
            long value = 0;
            long startTime;
            byte r;
            byte g;
            byte b;

            public ProfilerEntry(long value, byte r, byte g, byte b) {
                this.startTime = value;
                this.r = r;
                this.g = g;
                this.b = b;
            }
        }

        static HashMap<String, ProfilerEntry> entries = new HashMap<>();

//        static void profile(String entryName, long startTime) {
//            long endTime = System.currentTimeMillis();
//            entries.put(entryName, (endTime - startTime) / 100f);
//        }

        static void startProfile(String entryName, byte r, byte g, byte b) {
            long startTime = System.currentTimeMillis();
            var entry = entries.get(entryName);
            if (entry == null)
                entries.put(entryName, new ProfilerEntry(startTime, r,g,b));
            else
                entry.startTime = startTime;

        }

        static void endProfile(String entryName) {
            var entry = entries.get(entryName);
            entry.value = System.currentTimeMillis() - entry.startTime;
//            entries.put(entryName, entry);
        }
    }
}
