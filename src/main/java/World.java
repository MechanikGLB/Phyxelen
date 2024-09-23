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

        return world;
    }

    public static World loadWorldByPath(String path) {
        File config = new File("worlds" + File.separator + path + File.separator + "world.conf");
        if (!config.exists() || config.isDirectory())
            throw new RuntimeException("No world \"" + path + "\"");

        World world = new World();
        /*TEMP*/ world.defaultSubworldId = "default";
        return world;
    }


    public Subworld loadOrCreateSubworld(String subworldId) {
        Subworld subworld = new Subworld(this, subworldId);
        subworlds.put(subworldId, subworld);
        return subworld;
    };
}
