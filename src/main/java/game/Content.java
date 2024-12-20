package game;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.*;

import de.matthiasmann.twl.utils.PNGDecoder;

import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;


public class Content {
    static protected HashMap<String, Material> materials = new HashMap<>();
    static protected Material airMaterial;
    static private HashMap<String, Image> images = new HashMap<>();
    static private final Load yaml = new Load(LoadSettings.builder().build());


    static public Material getMaterial(String id) {
        return materials.get(id);
    }
    static public Material air() { return airMaterial; }
    /// Returns loaded image if it exists; else tries load it from disk
    static public Image getImage(String name) {
        var image = images.get(name);
        if (image == null)
            image = loadImage(name);
        return image;
    }


    static HashMap<String, Object> getGameConfig(String gameName) {
        try {
            Object readed = yaml.loadFromReader(
                    new FileReader("games" + File.separator + gameName + File.separator + "config.yaml"));
            try {
                if (readed instanceof HashMap)
                    return (HashMap<String, Object>) readed;
                return null;
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    static void loadModules(String[] moduleNames) {
        for (String moduleName : moduleNames) {
            FileReader reader;
            if (Files.exists(Path.of("games", moduleName, "config.yaml"))) {
                try {
                    reader = new FileReader("games" + File.separator + moduleName + File.separator + "config.yaml");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else if (Files.exists(Path.of("mods", moduleName, "config.yaml"))) {
                try {
                    reader = new FileReader("mods" + File.separator + moduleName + File.separator + "config.yaml");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                continue;
            }
            Object readed = yaml.loadFromReader(reader);
            try {
                var table = (HashMap<String, Object>) readed;
                Object materials = table.get("materials");
                if (materials != null) {
                    var materialTable = (HashMap<String, HashMap<String, Object>>) materials;
                    for (var material : materialTable.entrySet()) {
                        Material materialDefinition = parseMaterialType(material);
                        if (materialDefinition == null)
                            continue;
                        materialDefinition.name = material.getKey();
                        parseMaterialColors(material, materialDefinition);
                        parseMaterialDensity(material, materialDefinition);
                        Content.materials.put(material.getKey(), materialDefinition);
                    }
                }
            } catch (ClassCastException e) {
                throw new RuntimeException("Module " + moduleName + " has wrong structure");
            }
        }
    }

    static private Material parseMaterialType(Map.Entry<String, HashMap<String, Object>> materialTable) {
        String type = (String) materialTable.getValue().get("type");
        switch (type.toLowerCase()) {
            case "solid" -> { return new MaterialSolid(); }
            case "powder" -> { return new MaterialPowder(); }
            case "liquid" -> { return new MaterialLiquid(); }
            case "gas" -> { return new MaterialGas(); }
            default -> { return null; }
        }
    }

    static private void parseMaterialColors(Map.Entry<String, HashMap<String, Object>> materialTable, Material definition) {
        Object colorObject = materialTable.getValue().get("color");
        if (colorObject != null) {
            ColorWithAplha[] colors = new ColorWithAplha[1];
            colors[0] = new ColorWithAplha(((String) colorObject));
            definition.colors = colors;
        } else if (materialTable.getValue().get("colors") != null) {
            var colorStrings = (List<String>) materialTable.getValue().get("colors");
            ColorWithAplha[] colors = new ColorWithAplha[colorStrings.size()];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = new ColorWithAplha(colorStrings.get(i));
            }
            definition.colors = colors;
        }
    }

    static private void parseMaterialDensity(Map.Entry<String, HashMap<String, Object>> materialTable, Material definition) {
        Object densityObject = materialTable.getValue().get("density");
        if (densityObject != null) {
            definition.density = (Double) densityObject;
        } else {
            definition.density = 0.02f;
        }
    }
//    void updateModuleList()

    /// Loads image from disk
    static public Image loadImage(String name) {
        try {
            PNGDecoder pngDecoder = new PNGDecoder(new FileInputStream(
                    "assets"+File.separator+"textures"+File.separator+name));
            int width = pngDecoder.getWidth();
            int height = pngDecoder.getHeight();
            ByteBuffer buffer = ByteBuffer.allocate(
                    width * height * 4);
            pngDecoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
            var image = new Image(buffer, width, height);
            images.put(name, image);
            return image;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void loadTextures() throws IOException {
        for (var entry : new File("assets" + File.separator + "textures").listFiles()) {
            if (entry.isFile() && entry.getName().endsWith(".png")) {
                PNGDecoder pngDecoder = new PNGDecoder(new FileInputStream(entry));
                int width = pngDecoder.getWidth();
                int height = pngDecoder.getHeight();
                ByteBuffer buffer = ByteBuffer.allocateDirect(
                        width * height * 4);
                pngDecoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
                images.put(entry.getName(), new Image(buffer, width, height));
            }
        }
    }
}
