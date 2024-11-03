package game;

import java.util.Random;

public class Pixels {
    public final static int notLoadedPixel = 0x80000000;
    static Random random = new Random();

    static int getPixelWithRandomColor(int pixelId) {
        int colorCount = GameApp.activeWorld.pixelIds[pixelId].colors.length;
        return make(pixelId, Math.abs(random.nextInt()) % colorCount);
    }

    public static int getId(int pixel) {
        return pixel & 0x1FFFFFFF;
    }
    public static int getColor(int pixel) {
        return (pixel >> 29) & 0b111;
    }
    public static int make(int id, int colorId) {
        return id | (colorId << 29);
    }
}
