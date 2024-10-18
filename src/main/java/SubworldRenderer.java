import static org.lwjgl.opengl.GL20.*;

public class SubworldRenderer implements WindowResizeListener {
    Client client;
    Subworld subworld;
    int vertexBuffer;
    float[] vertexArray;
    int colorBuffer;
    float[] colorArray;

//    int widthInWorldPixels;
//    int heightInWorldPixels;

    public SubworldRenderer(Subworld subworld) {
        this.subworld = subworld;
        client = (Client) Main.getGame();
        vertexBuffer = glGenBuffers();
        colorBuffer = glGenBuffers();
        updateVertexArraySize();
    }

    public void draw(float fdt) {
//        glEnableVertexAttribArray();
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

        float screenHalfWidthInWorldPixels = 0.5f / client.renderer.relativePixelWidth;

        int worldPixelCount = 0;
        for (var chunk : subworld.activeChunks.entrySet()) {
            int baseX = chunk.getKey().x * Chunk.size();
            int baseY = chunk.getKey().y * Chunk.size();
            if (Math.abs(client.cameraPos.x - baseX) > screenHalfWidthInWorldPixels) {
                continue;
            }
            for (int i = 0; i < Chunk.area(); i++) {

//                for (Pixel pixel : entry.getValue().pixels) {
//                    Material material = pixel.material;
//                    float drawX = (baseX + i % Chunk.size() - cameraPos.x) * relativePixelWidth;
//                    float drawY = (baseY + i / Chunk.size() - cameraPos.y) * relativePixelHeight;
                for (int vert = 0; vert < 4; vert++) {
//                    vertexArray[worldPixelCount] =

                }
                ++worldPixelCount;
            }
        }

        glDrawArrays(GL_QUADS, 0, vertexArray.length / 8);

    }

    void drawChunks() {

    }

    @Override
    public void onWindowResize(long window, int width, int height) {
        updateVertexArraySize();
    }

    void updateVertexArraySize() {
        int horizontalCount = (int)(2.0f / client.renderer.relativePixelWidth);
        int verticalCount = (int)(2.0f / client.renderer.relativePixelHeight);
        // (vertexes for world pixel) * (floats in coordinate)
        vertexArray = new float[horizontalCount * verticalCount * 4 * 2];
        // (vertexes for world pixel) * (floats in color)
        colorArray = new float[horizontalCount * verticalCount * 4 * 3];
        glBindBuffer(GL_VERTEX_ARRAY, vertexBuffer);
        glBufferData(GL_VERTEX_ARRAY, vertexArray, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_COLOR_ARRAY, colorBuffer);
        glBufferData(GL_COLOR_ARRAY, colorArray, GL_DYNAMIC_DRAW);
    }
}
