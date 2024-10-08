import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.system.MemoryUtil.NULL;


public class Client extends GameApp {
    protected long window;
    private Renderer renderer = new Renderer(this);

    float maxFps = 30;
    float maxTps = 5;

    private VectorF cameraPos = new VectorF(0, 0);
    /// Length of world pixel side in real screen pixels
    private short viewScale = 8;
    /// Free camera movement speed in screen pixels per second
    private short cameraSpeed = 150;

    /*Temp?*/private int paintingPixel = 1;
    /*Temp?*/private int paintingSize = 0;


    @Override
    public void run() {
        super.run();
        initGlfw();
        GL.createCapabilities();
        renderer.init();
        System.out.println("Start");
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }


    private void initGlfw() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(800, 600, "Phyxelen", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (action != GLFW_PRESS) return;
        });

        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(window);


        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            renderer.screenWidth = width;
            renderer.screenHeight = height;
            renderer.screenSizeUpdated();
        });
    }


    @Override
    protected void loop() {
        long lastCycleStartTime = System.currentTimeMillis();
        long lastTickTime = 0;

        while (!glfwWindowShouldClose(window)) {
            long cycleStartTime = System.currentTimeMillis();

            float dt = (cycleStartTime - lastCycleStartTime) / 1000.0f;
            lastCycleStartTime = cycleStartTime;
            tick(dt);
//            lastFrameTime = newTime;

            if (counter % 32 == 0) {
                updateChunks();
            }

            renderer.draw();

            glfwPollEvents();

            cameraSpeed = (short) (200 + 400 * glfwGetKey(window, GLFW_KEY_LEFT_SHIFT));

            if (glfwGetKey(window, GLFW_KEY_UP) != 0 ||
                    glfwGetKey(window, GLFW_KEY_W) != 0)
                cameraPos.y += cameraSpeed / viewScale * dt;
            if (glfwGetKey(window, GLFW_KEY_DOWN) != 0 ||
                    glfwGetKey(window, GLFW_KEY_S) != 0)
                cameraPos.y -= cameraSpeed / viewScale * dt;
            if (glfwGetKey(window, GLFW_KEY_LEFT) != 0 ||
                    glfwGetKey(window, GLFW_KEY_A) != 0)
                cameraPos.x -= cameraSpeed / viewScale * dt;
            if (glfwGetKey(window, GLFW_KEY_RIGHT) != 0 ||
                    glfwGetKey(window, GLFW_KEY_D) != 0)
                cameraPos.x += cameraSpeed / viewScale * dt;

            // Scaling is not ready!
            if (glfwGetKey(window, GLFW_KEY_MINUS) != 0 && viewScale > 1) {
                viewScale -= 1;
                renderer.screenSizeUpdated();
            }
            if (glfwGetKey(window, GLFW_KEY_EQUAL) != 0) {
                viewScale += 1;
                renderer.screenSizeUpdated();
            }

            for (int i = 0; i < 10; i++)
                if (glfwGetKey(window, GLFW_KEY_0 + i) != 0)
                    paintingPixel = i;
            if (glfwGetKey(window, GLFW_KEY_LEFT_BRACKET) != 0 && paintingSize > 1)
                paintingSize -= 1;
            if (glfwGetKey(window, GLFW_KEY_RIGHT_BRACKET) != 0)
                paintingSize += 1;

            if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) != 0) {
                setPixelAtCursorPosition(paintingPixel);
            } else if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_2) != 0)
                setPixelAtCursorPosition(0);

            if (glfwGetKey(window, GLFW_KEY_E) != 0)
                jetPixelAtCursorPosition();
            if (glfwGetKey(window, GLFW_KEY_SPACE) != 0)
                jetPixelsAtCursorPosition();


            counter++;
            if (dt < (1000.0f / maxFps))
                try {
                    Thread.sleep((long) (1000.0f / maxFps - dt));
                } catch (InterruptedException e) {
                    continue;
                }
        }
    }


    int screenXToWorld(int x) {
        x -= renderer.screenWidth / 2;
        if (x < 0) x -= viewScale;
        return Math.round(cameraPos.x) + x / viewScale;
    }


    int screenYToWorld(int y) {
        y = renderer.screenHeight / 2 - y;
        if (y < 0) y -= viewScale;
        return Math.round(cameraPos.y) + y / viewScale;
    }


    private void updateChunks() {
        if (activeSubworld == null) return;
        int width = renderer.screenWidth / (viewScale * Chunk.size());
        int height = renderer.screenHeight / (viewScale * Chunk.size());
        activeSubworld.updateChunksForUser(
                (int) cameraPos.x / Chunk.size(), (int) cameraPos.y / Chunk.size(), width, height);
    }


    void setPixelAtCursorPosition(int pixel) {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window, x, y);
        for (int dx = -paintingSize/2; dx <= paintingSize/2; dx++) {
            for (int dy = -paintingSize/2; dy <= paintingSize/2; dy++) {
                activeSubworld.presetPixel(
                        new Pixel(Pixels.getPixelWithRandomColor(pixel), null,
                                screenXToWorld((int) x[0]) + dx, screenYToWorld((int) y[0]) + dy));
            }
        }
    }

    void jetPixelAtCursorPosition() {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window, x, y);
//        int pixel = activeSubworld.getPixel(screenXToWorld((int) x[0]), screenYToWorld((int) y[0]));
//        if (pixel != 0) {
//            activeSubworld.setPixel(screenXToWorld((int) x[0]), screenYToWorld((int) y[0]), 0);
//            activeSubworld.entities.add(new PixelEntity(
//                    screenXToWorld((int) x[0]), screenYToWorld((int) y[0]), activeSubworld, pixel,
//                    activeSubworld.random.nextFloat(-20f, 20f), 100, 0, -9.8f
//            ));
//        }
    }

    void jetPixelsAtCursorPosition() {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window, x, y);
        for (int dx = -paintingSize/2; dx <= paintingSize/2; dx++) {
            for (int dy = paintingSize/2; dy > -paintingSize/2; dy--) {
                Pixel pixel = activeSubworld.getPixel(
                        screenXToWorld((int) x[0]) + dx, screenYToWorld((int) y[0]) + dy);
                if (!(pixel.material instanceof MaterialAir) ) {
                    activeSubworld.setPixel(
                            new Pixel(0, null,
                                    screenXToWorld((int) x[0]) + dx, screenYToWorld((int) y[0]) + dy));
                    double angle = activeSubworld.random.nextDouble(-Math.PI / 3, Math.PI / 3);
                    activeSubworld.entities.add(new PixelEntity(
                            screenXToWorld((int) x[0]) + dx, screenYToWorld((int) y[0]) + dy, activeSubworld, pixel,
                            (float)Math.sin(angle) * 100.f, (float)Math.cos(angle) * 100.f,
                            0, -9.8f
                    ));
                }
            }
        }
    }


    class Renderer {
        Client client;
        private int screenWidth = 800;
        private int screenHeight = 600;
        private float relativePixelWidth = 0.01f;
        private float relativePixelHeight = 0.01f;
        // Render buffers
        float[] vertexArray;
        float[] colorArray;
        private int vertexBuffer;
        private int colorBuffer;
        float[] movingPixelVertexArray;
        float[] movingPixelColorArray;
        private int movingPixelVertexBuffer;
        private int movingPixelColorBuffer;


        Renderer(Client client) {
            this.client = client;
        }


        void init() {
            vertexBuffer = glGenBuffers();
            colorBuffer = glGenBuffers();
            movingPixelVertexBuffer = glGenBuffers();
            movingPixelColorBuffer = glGenBuffers();

            int[] width = new int[1];
            int[] height = new int[1];
            glfwGetWindowSize(client.window, width, height);
            screenWidth = width[0];
            screenHeight = height[0];
            screenSizeUpdated();
        }


        public void draw() {
            glViewport(0, 0, screenWidth, screenHeight);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            int worldPixelCount = 0;
            int movingPixelCount = 0;
            for (Map.Entry<VectorI, Chunk> entry : activeSubworld.activeChunks.entrySet()) {
                int baseX = entry.getKey().x * Chunk.size();
                int baseY = entry.getKey().y * Chunk.size();
                int i = 0;
                for (Pixel pixel : entry.getValue().pixels) {
                    Material material = pixel.material;
                    float drawX = (baseX + i % Chunk.size() - cameraPos.x) * relativePixelWidth;
                    float drawY = (baseY + i / Chunk.size() - cameraPos.y) * relativePixelHeight;
                    i++;
                    if (drawX < (-1 - (relativePixelWidth)) || drawX > 1 ||
                            drawY < (-1 - (relativePixelHeight)) || drawY > 1
                    ) {
                        continue;
                    }
                    byte colorId = pixel.color;
                    drawPixel(drawX, drawY, worldPixelCount, material, colorId, vertexArray, colorArray);
                    worldPixelCount++;
                }
            }
            for (Entity entity : activeSubworld.entities) {
                if (entity instanceof PixelEntity) {
                    Material material =
                            ((PixelEntity) entity).pixel.material;
                    int colorId = Pixels.getColor(((PixelEntity) entity).pixel.color);
                    drawPixel(
                            (entity.x - cameraPos.x) * relativePixelWidth,
                            (entity.y - cameraPos.y) * relativePixelHeight,
                            movingPixelCount, material, colorId,
                            movingPixelVertexArray, movingPixelColorArray
                    );
                    movingPixelCount++;
                }
            }

            glEnableClientState(GL_VERTEX_ARRAY);
            glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
            glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_STREAM_DRAW);
            glVertexPointer(2, GL_FLOAT, 0, 0);

            glEnableClientState(GL_COLOR_ARRAY);
            glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
            glBufferData(GL_ARRAY_BUFFER, colorArray, GL_STREAM_DRAW);
            glColorPointer(3, GL_FLOAT, 0, 0);

            glDrawArrays(GL_QUADS, 0, worldPixelCount * 4);

//            glDisableClientState(GL_VERTEX_ARRAY);
//            glDisableClientState(GL_COLOR_ARRAY);

            glEnableClientState(GL_VERTEX_ARRAY);
            glBindBuffer(GL_ARRAY_BUFFER, movingPixelVertexBuffer);
            glBufferData(GL_ARRAY_BUFFER, movingPixelVertexArray, GL_STREAM_DRAW);
            glVertexPointer(2, GL_FLOAT, 0, 0);

            glEnableClientState(GL_COLOR_ARRAY);
            glBindBuffer(GL_ARRAY_BUFFER, movingPixelColorBuffer);
            glBufferData(GL_ARRAY_BUFFER, movingPixelColorArray, GL_STREAM_DRAW);
            glColorPointer(3, GL_FLOAT, 0, 0);

            glDrawArrays(GL_QUADS, 0, movingPixelCount * 4);

            glDisableClientState(GL_VERTEX_ARRAY);

            glfwSwapBuffers(window);
        }


        private void drawPixel(float drawX, float drawY, int i, Material material, int colorId,
                float[] vertexArray, float[] colorArray
        ) {
            int ci = i * 12;
            colorArray[ci] = material.colors[colorId].r;
            colorArray[ci + 1] = material.colors[colorId].g;
            colorArray[ci + 2] = material.colors[colorId].b;

            colorArray[ci + 3] = material.colors[colorId].r;
            colorArray[ci + 4] = material.colors[colorId].g;
            colorArray[ci + 5] = material.colors[colorId].b;

            colorArray[ci + 6] = material.colors[colorId].r;
            colorArray[ci + 7] = material.colors[colorId].g;
            colorArray[ci + 8] = material.colors[colorId].b;
//
            colorArray[ci + 9] = material.colors[colorId].r;
            colorArray[ci + 10] = material.colors[colorId].g;
            colorArray[ci + 11] = material.colors[colorId].b;

            i *= 8;
            vertexArray[i] = drawX;
            vertexArray[i + 1] = drawY;
            vertexArray[i + 2] = drawX + relativePixelWidth;
            vertexArray[i + 3] = drawY;
            vertexArray[i + 4] = drawX + relativePixelWidth;
            vertexArray[i + 5] = drawY + relativePixelHeight;
            vertexArray[i + 6] = drawX;
            vertexArray[i + 7] = drawY + relativePixelHeight;
        }


        void screenSizeUpdated() {
            relativePixelWidth = viewScale / (float) screenWidth * 2;
            relativePixelHeight = viewScale / (float) screenHeight * 2;

//        int worldPixelCount = activeSubworld.loadedChunks.entrySet().size() * Chunk.area();
            int worldPixelCount = (int) (screenWidth / viewScale + 2) * (int) (screenHeight / viewScale + 2);
            vertexArray = new float[worldPixelCount * 8];
            colorArray = new float[worldPixelCount * 8 * 3];
            movingPixelVertexArray = new float[worldPixelCount * 4];
            movingPixelColorArray = new float[worldPixelCount * 4 * 3];
            glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
            glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_STREAM_DRAW);
            glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
            glBufferData(GL_ARRAY_BUFFER, colorArray, GL_STREAM_DRAW);
        }
    }
}