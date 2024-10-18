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
        client.windowResizeListeners.add(this);
    }

    public void draw(float fdt) {
//        glEnableVertexAttribArray();
//        glEnableClientState(GL_VERTEX_ARRAY);
//        glEnableClientState(GL_COLOR_ARRAY);

        var relativePixelWidth = client.renderer.relativePixelWidth;
        var relativePixelHeight = client.renderer.relativePixelHeight;
        float screenHalfWidthInWorldPixels = 1.0f / relativePixelWidth;

        int worldPixelCount = 0;
        for (var chunk : subworld.activeChunks.entrySet()) {
            int baseX = chunk.getKey().x * Chunk.size();
            int baseY = chunk.getKey().y * Chunk.size();
//            if (Math.abs(client.cameraPos.x - baseX) > screenHalfWidthInWorldPixels) {
//                continue;
//            }
            for (int i = 0; i < Chunk.area(); i++) {
                float drawX = (baseX + i % Chunk.size() - client.cameraPos.x) * relativePixelWidth;
                float drawY = (baseY + i / Chunk.size() - client.cameraPos.y) * relativePixelHeight;

                Material material = chunk.getValue().materials[i];
                ColorWithAplha color = material.colors[chunk.getValue().colors[i]];
                if (drawX < (-1 - (relativePixelWidth)) || drawX > 1 ||
                        drawY < (-1 - (relativePixelHeight)) || drawY > 1
                ) {
                    continue;
                }
                if ((worldPixelCount+1)*8 > vertexArray.length) continue;
                vertexArray[worldPixelCount*8] = drawX;
                vertexArray[worldPixelCount*8 + 1] = drawY;
                vertexArray[worldPixelCount*8 + 2] = drawX + relativePixelWidth;
                vertexArray[worldPixelCount*8 + 3] = drawY;
                vertexArray[worldPixelCount*8 + 4] = drawX + relativePixelWidth;
                vertexArray[worldPixelCount*8 + 5] = drawY + relativePixelHeight;
                vertexArray[worldPixelCount*8 + 6] = drawX;
                vertexArray[worldPixelCount*8 + 7] = drawY + relativePixelHeight;
                for (int vert = 0; vert < 4; vert++) {
                    colorArray[worldPixelCount * 12 + vert * 3] = color.r;
                    colorArray[worldPixelCount * 12 + vert * 3 + 1] = color.g;
                    colorArray[worldPixelCount * 12 + vert * 3 + 2] = color.b;
                }
                ++worldPixelCount;
            }
        }

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
//        System.out.println(glGetError());
        glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);
//        System.out.println(glGetError());
//        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexArray);
        glVertexPointer(2, GL_FLOAT, 0, 0);
//        System.out.println(glGetError());

        glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
        glBufferData(GL_ARRAY_BUFFER, colorArray, GL_DYNAMIC_DRAW);
//        glBufferSubData(GL_ARRAY_BUFFER, 0, colorArray);
        glColorPointer(3, GL_FLOAT, 0, 0);
        glDrawArrays(GL_QUADS, 0, worldPixelCount * 4);
//        System.out.println(glGetError());

        glColor3f(0.5f, 0.5f, 0.5f);
//        glBegin(GL_POINTS);
//        for (int i = 0; i < vertexArray.length / 2; i++) {
//            glColor3f(colorArray[i*3], colorArray[i*3+1], colorArray[i*3+2]);
//            glVertex2f(vertexArray[i*2], vertexArray[i*2+1]);
//        }
//        glEnd();
    }

    void drawChunks() {

    }

    @Override
    public void onWindowResize(long window, int width, int height) {
        updateVertexArraySize();
    }

    void updateVertexArraySize() {
        int horizontalCount = (int)(2.0f / client.renderer.relativePixelWidth) + 2;
        int verticalCount = (int)(2.0f / client.renderer.relativePixelHeight) + 2;
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
