import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class SubworldRenderer implements WindowResizeListener {
    Client client;
    Subworld subworld;
    int vertexBuffer;
    float[] vertexArray;
    int colorBuffer;
//    float[] colorArray;
    FloatBuffer colorArray;

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
        drawChunks();


    }

    void drawChunks() {
//        glLoadIdentity();
        var worldPixelSize = client.viewScale;
        int worldPixelCount = 0;
        try {
            client.logicSemaphore.acquire();
        } catch (InterruptedException e) {
            client.logicSemaphore.release();
            return;
        }
        for (var chunk : subworld.activeChunks.entrySet()) {
            int baseX = chunk.getKey().x * Chunk.size();
            int baseY = chunk.getKey().y * Chunk.size();
//            if (Math.abs(client.cameraPos.x - baseX) > screenHalfWidthInWorldPixels ||
//                    Math.abs(client.cameraPos.y - baseY) > screenHalfHeightInWorldPixels) {
//                continue;
//            }

            colorArray.rewind();
            for (int i = 0; i < Chunk.area(); i++) {
                float drawX = client.worldXToScreen(baseX + i % Chunk.size());
                float drawY = client.worldYToScreen(baseY + i / Chunk.size());

                Material material = chunk.getValue().materials[i];
                ColorWithAplha color = material.colors[chunk.getValue().colors[i]]; // chunk.getValue().colors[i]
                if (drawX < (-1 - (worldPixelSize)) || drawX > client.renderer.screenWidth ||
                        drawY < (-1 - (worldPixelSize)) || drawY > client.renderer.screenHeight
                ) {
                    continue;
                }
                if ((worldPixelCount+1)*8 > vertexArray.length) continue;
                vertexArray[worldPixelCount*8] = drawX;
                vertexArray[worldPixelCount*8 + 1] = drawY;
                vertexArray[worldPixelCount*8 + 2] = drawX + worldPixelSize;
                vertexArray[worldPixelCount*8 + 3] = drawY;
                vertexArray[worldPixelCount*8 + 4] = drawX + worldPixelSize;
                vertexArray[worldPixelCount*8 + 5] = drawY + worldPixelSize;
                vertexArray[worldPixelCount*8 + 6] = drawX;
                vertexArray[worldPixelCount*8 + 7] = drawY + worldPixelSize;
                for (int vert = 0; vert < 4; vert++) {
//                    colorArray[worldPixelCount * 12 + vert * 3] = color.r;
//                    colorArray[worldPixelCount * 12 + vert * 3 + 1] = color.g;
//                    colorArray[worldPixelCount * 12 + vert * 3 + 2] = color.b;
                    colorArray.put(worldPixelCount * 12 + vert * 3, color.r);
                    colorArray.put(worldPixelCount * 12 + vert * 3 + 1, color.g);
                    colorArray.put(worldPixelCount * 12 + vert * 3 + 2, color.b);
                }
                ++worldPixelCount;
            }
        }
        client.logicSemaphore.release();

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);
//        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexArray);
//        System.out.println(glGetError());
        glVertexPointer(2, GL_FLOAT, 0, 0);
//        System.out.println(glGetError());

        glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
//        colorArray.rewind();
//        colorArray.
        glBufferData(GL_ARRAY_BUFFER, colorArray.array(), GL_DYNAMIC_DRAW);
//        System.out.println(colorArray.position());
//        glBufferSubData(GL_ARRAY_BUFFER, 0, colorArray.array());

//        System.out.println(glGetError());
        glColorPointer(3, GL_FLOAT, 0, 0);
        glDrawArrays(GL_QUADS, 0, worldPixelCount * 4);
//        System.out.println(glGetError());
    }

    @Override
    public void onWindowResize(long window, int width, int height) {
        updateVertexArraySize();
    }

    void updateVertexArraySize() {
        int horizontalCount = (client.renderer.screenWidth / client.viewScale) + 3;
        int verticalCount = (client.renderer.screenHeight / client.viewScale) + 3;
        // (vertexes for world pixel) * (floats in coordinate)
        vertexArray = new float[horizontalCount * verticalCount * 4 * 2];
        // (vertexes for world pixel) * (floats in color)
//        colorArray = new float[horizontalCount * verticalCount * 4 * 3];
        colorArray = FloatBuffer.allocate(horizontalCount * verticalCount * 4 * 3);
        glBindBuffer(GL_VERTEX_ARRAY, vertexBuffer);
        glBufferData(GL_VERTEX_ARRAY, vertexArray, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_COLOR_ARRAY, colorBuffer);
        glBufferData(GL_COLOR_ARRAY, colorArray, GL_DYNAMIC_DRAW);
    }
}
