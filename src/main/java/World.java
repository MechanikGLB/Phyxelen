import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Hashtable;
import java.io.*;
import java.util.List;

public class World {
    String name; /// World displayed name
    String path; /// World folder name

    Hashtable<String, Subworld> subworlds;
    String defaultSubworldId;
    // Array for mapping int to PixelDefinition
    PixelDefinition[] pixelIds;

    public World() {
        subworlds = new Hashtable<>();
//        for (int i = 0; i < subworlds.length; i++)
//            subworlds[i] = new Subworld(this);
    }


    public void tick(float dt) {
        for (Subworld subworld : subworlds.values()) {
            subworld.tick(dt);
        }
    }


    public static World createWorld(String name) {
        List<Character> forbiddenChars = Arrays.asList('/','\\',':','*','?','"','<','>','|');
        StringBuilder path = new StringBuilder();
        for (char ch : name.toCharArray()) {
            if (forbiddenChars.contains(ch))
                path.append('_');
            else
                path.append(ch);
        }

        World world = new World();
        /*TEMP*/ world.defaultSubworldId = "default";
        world.path = path.toString();
        world.name = name;

        File config = new File("worlds" + File.separator + world.path + File.separator + "world.conf");
        // TODO: write config
        try {
//            Files.createDirectory(Path.of("worlds", world.path));
            Files.createDirectories(Path.of("worlds", world.path, "subworlds"));
            config.createNewFile();
            PrintWriter writer = new PrintWriter(config);
            writer.println("default_subworld = default");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        world.startup();
        return world;
    }


    public static World loadWorldByPath(String path) {
        File config = new File("worlds" + File.separator + path + File.separator + "world.conf");
        if (!config.exists() || config.isDirectory())
            throw new RuntimeException("No world \"" + path + "\"");

        World world = new World();
        /*TEMP*/ world.defaultSubworldId = "default";
        world.startup();
        return world;
    }


    public Subworld loadOrCreateSubworld(String subworldId) {
        Subworld subworld = new Subworld(this, subworldId);
        subworlds.put(subworldId, subworld);
        return subworld;
    };


    private void startup() {
        loadContent();
    }


    private void loadContent() {
        //temp
        pixelIds = new PixelDefinition[2];
        pixelIds[0] = new PixelDefinition();
        pixelIds[0].colors = new ColorWithAplha[1];
        pixelIds[0].colors[0] = new ColorWithAplha(0.2f, 0.1f, 0.0f, 1f);
        pixelIds[1] = new PixelDefinition();
        pixelIds[1].colors = new ColorWithAplha[1];
        pixelIds[1].colors[0] = new ColorWithAplha(1.0f, 0.5f, 0.0f, 1f);
    }
}
