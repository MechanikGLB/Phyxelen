import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.*;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
//import static org.lwjgl.opengl.GL
import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Client extends GameApp {
    protected long window;
    private int screenWidth = 800;
    private int screenHeight = 600;
    float maxFps = 25;

    private VectorF cameraPos = new VectorF(0, 0);
    /// Length of world pixel side in real screen pixels
    private short viewScale = 8;
    /// Free camera movement speed in screen pixels per second
    private short cameraSpeed = 150;
    private float relativePixelWidth = 0.01f;
    private float relativePixelHeight = 0.01f;
    // Render buffers
    float[] vertexArray;
    float[] colorArray;
    private int vertexBuffer;
    private int colorBuffer;


    @Override
    public void run() {
        super.run();
        initGraphics();

        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }


    private void initGraphics() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(800, 600, "Phyxelen", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (action != GLFW_PRESS) return;

        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
//            glfwSetWindowPos(
//                    window,
//                    (vidmode.width() - pWidth.get(0)) / 2,
//                    (vidmode.height() - pHeight.get(0)) / 2
//            );
        } // the stack frame is popped automatically
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(window);
    }


    @Override
    protected void loop() {
        GL.createCapabilities();

        int[] widthBuffer = new int[1];
        int[] heightBuffer = new int[1];
        glfwGetWindowSize(window, widthBuffer, heightBuffer);
        updateScreenSize(widthBuffer, heightBuffer);
        int[] widthBuffer2 = new int[1];
        int[] heightBuffer2 = new int[1];
//        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//        if (videoMode != null) {
////            throw new Exception("No video mode");
//            screenWidth = videoMode.width();
//            screenHeight = videoMode.height();
//        } else {
//            System.err.println("No video mode");
//        };

        //Initialize Projection Matrix
        glMatrixMode( GL_PROJECTION );
        glLoadIdentity();

        //Initialize Modelview Matrix
        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();

         vertexBuffer = glGenBuffers();
        colorBuffer  = glGenBuffers();

        long time = System.currentTimeMillis();
        long lastFrameTime = 0;

        System.out.println("Start");
        while ( !glfwWindowShouldClose(window) ) {
            long newTime = System.currentTimeMillis();
            if ((newTime - time) < (1000.0f/maxFps))
                continue;
            float dt = (newTime - time) / 1000.0f;
            time = newTime;
            tick(dt);
//            lastFrameTime = newTime;

            glfwGetWindowSize(window, widthBuffer2, heightBuffer2);
            if (widthBuffer[0] != widthBuffer2[0] || heightBuffer[0] != heightBuffer2[0]) {
                widthBuffer[0] = widthBuffer2[0];
                heightBuffer[0] = heightBuffer2[0];
                updateScreenSize(widthBuffer, heightBuffer);
            }

            if (counter % 32 == 0) {
                updateChunks();
            }

            draw();

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
            if (glfwGetKey(window, GLFW_KEY_MINUS) != 0) {
                viewScale -= 1;
                screenSizeUpdated();
            }
            if (glfwGetKey(window, GLFW_KEY_EQUAL) != 0) {
                viewScale += 1;
                screenSizeUpdated();
            }

            if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) != 0) {
                setPixelAtCursorPosition(1);
            } else if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_2) != 0)
                setPixelAtCursorPosition(0);
            counter++;
        }
    }


    private void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

//            glMatrixMode( GL_MODELVIEW );
//            glLoadIdentity();

        //Move to center of the screen
//            glTranslatef( screenWidth / 2.f, screenHeight / 2.f, 0.f );

        int I = 0;
        for (Map.Entry<VectorI, Chunk> entry : activeSubworld.loadedChunks.entrySet()) {
            int baseX = entry.getKey().x * Chunk.size();
            int baseY = entry.getKey().y * Chunk.size();
            int i = 0;
            for (int pixel : entry.getValue().pixels) {
                PixelDefinition pixelDefinition = activeWorld.pixelIds[pixel];
                float drawX = (baseX + i % Chunk.size() - cameraPos.x) * relativePixelWidth * 2;
                float drawY = (baseY + i / Chunk.size() - cameraPos.y) * relativePixelHeight * 2;
                i++;
                if (drawX < (-1 - (relativePixelWidth * 2)) || drawX > 1 ||
                        drawY < (-1 - (relativePixelHeight * 2)) || drawY > 1
                ) {
                    continue;
                }
                drawPixel(drawX, drawY, I, pixelDefinition);
                I++;
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


        glDrawArrays(GL_QUADS, 0, I*4);

        glDisableClientState(GL_VERTEX_ARRAY);

        glfwSwapBuffers(window); // swap the color buffers
    }


    private void drawPixel(float drawX, float drawY, int i, PixelDefinition pixelDefinition) {
//        glColor3f(pixelDefinition.colors[0].r,
//                pixelDefinition.colors[0].g,
//                pixelDefinition.colors[0].b);
        int ci = i * 12;
        colorArray[ci] = pixelDefinition.colors[0].r;
        colorArray[ci+1] = pixelDefinition.colors[0].g;
        colorArray[ci+2] = pixelDefinition.colors[0].b;

        colorArray[ci+3] = pixelDefinition.colors[0].r;
        colorArray[ci+4] = pixelDefinition.colors[0].g;
        colorArray[ci+5] = pixelDefinition.colors[0].b;

        colorArray[ci+6] = pixelDefinition.colors[0].r;
        colorArray[ci+7] = pixelDefinition.colors[0].g;
        colorArray[ci+8] = pixelDefinition.colors[0].b;
//
        colorArray[ci+9] = pixelDefinition.colors[0].r;
        colorArray[ci+10] = pixelDefinition.colors[0].g;
        colorArray[ci+11] = pixelDefinition.colors[0].b;

        i *= 8;
        vertexArray[i] = drawX;
        vertexArray[i+1] = drawY;
        vertexArray[i+2] = drawX + relativePixelWidth * 2;
        vertexArray[i+3] = drawY;
        vertexArray[i+4] = drawX + relativePixelWidth * 2;
        vertexArray[i+5] = drawY + relativePixelHeight * 2;
        vertexArray[i+6] = drawX;
        vertexArray[i+7] = drawY + relativePixelHeight * 2;
    }


    void updateScreenSize(int[] width, int[] height) {
        screenWidth = width[0];
        screenHeight = height[0];
//        glfwSetWindowSize(window, screenWidth, screenHeight);
//        glfw
        screenSizeUpdated();
    }


    void screenSizeUpdated() {
        relativePixelWidth = viewScale / (float)screenWidth;
        relativePixelHeight = viewScale / (float)screenHeight;

//        int worldPixelCount = activeSubworld.loadedChunks.entrySet().size() * Chunk.area();
        int worldPixelCount = (int)(screenWidth / viewScale + 2) * (int)(screenHeight / viewScale + 2);
        vertexArray = new float[worldPixelCount * 8];
        colorArray = new float[worldPixelCount * 8 * 3];
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
        glBufferData(GL_ARRAY_BUFFER, colorArray, GL_STREAM_DRAW);
//        if (screenWidth > screenHeight) {
//            relativePixelSize = viewScale / (float)screenWidth;
//        } else {
//            relativePixelSize = viewScale / (float)screenHeight;
//        }
    }


    int screenXToWorld(int x) {
        x -= screenWidth / 2;
        if (x < 0) x -= viewScale;
        return (int)cameraPos.x + x / viewScale;
    }


    int screenYToWorld(int y) {
        y = screenHeight / 2 - y;
        if (y < 0) y -= viewScale;
        return (int)cameraPos.y + y / viewScale;
    }


    private void updateChunks() {
        if (activeSubworld == null) return;
        int width = screenWidth / (viewScale * Chunk.size());
        int height = screenHeight / (viewScale * Chunk.size());

        for (int x = -width; x <= width; x++) {
            for (int y = -height; y <= height; y++) {
                VectorI indexes = new VectorI(
                        x + ((int)cameraPos.x / Chunk.size()),
                        y + ((int)cameraPos.y / Chunk.size()));
                if (!activeSubworld.loadedChunks.containsKey(indexes)) {
                    activeSubworld.loadChunk(indexes);
                }
            }
        }
    }


    void setPixelAtCursorPosition(int pixel) {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window, x, y);
        activeSubworld.setPixel(
                screenXToWorld((int)x[0]),
                screenYToWorld((int)y[0]), pixel);
    }
}
