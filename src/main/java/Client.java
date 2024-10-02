import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;
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
import static org.lwjgl.system.MemoryUtil.memAddress;


public class Client extends GameApp {

    private GLFWFramebufferSizeCallback fsCallback;
    protected long window;
    private int screenWidth = 600;
    private int screenHeight = 400;
    float maxFps = 30;
    float maxTps = 5;

    private VectorF cameraPos = new VectorF(0, 0);
    /// Length of world pixel side in real screen pixels
    private short viewScale = 8;
    private float relativePixelWidth = 0.01f;
    private float relativePixelHeight = 0.01f;
    // Render buffers
    float[] vertexArray;
    float[] colorArray;
    private int vertexBuffer;
    private int colorBuffer;

    /*Temp?*/private int paintingPixel = 1;


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
        window = glfwCreateWindow(screenWidth, screenHeight, "Phyxelen", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
        glfwSetWindowSizeLimits(window, 300, 300, GLFW_DONT_CARE, GLFW_DONT_CARE); //the window size limits
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (action != GLFW_PRESS) return;
        });

        glfwSetFramebufferSizeCallback(window, fsCallback = new GLFWFramebufferSizeCallback() {
            public void invoke(long window, int w, int h) {
                if (w > 0 && h > 0) {
                    screenWidth = w;
                    screenHeight = h;
                }
            }
        });


        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
     //       IntBuffer pWidth = stack.mallocInt(1); // int*
   //         IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
 //           glfwGetWindowSize(window, pWidth, pHeight);
            /*
            IntBuffer framebufferSize = stack.mallocInt(2);
            nglfwGetFramebufferSize(window, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
            screenWidth = framebufferSize.get(0);
            screenHeight = framebufferSize.get(1);
*/
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
        //glfwMakeContextCurrent(window);
        // Enable v-sync
//        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(window);
    }


    @Override
    protected void loop() {
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        int[] widthBuffer = new int[1];
        int[] heightBuffer = new int[1];
        glfwGetWindowSize(window, widthBuffer, heightBuffer);
        updateScreenSize(widthBuffer, heightBuffer);
        int[] widthBuffer2 = new int[1];
        int[] heightBuffer2 = new int[1];
//        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//        if (videoMode != null) {
//            throw new Exception("No video mode");
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

        long lastCycleStartTime = System.currentTimeMillis();
        long lastTickTime = 0;

        System.out.println("Start");
        while ( !glfwWindowShouldClose(window) ) {
            long cycleStartTime = System.currentTimeMillis();
            glViewport(0, 0, screenWidth, screenHeight);
            float dt = (cycleStartTime - lastCycleStartTime) / 1000.0f;
            lastCycleStartTime = cycleStartTime;
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

            /// Free camera movement speed in screen pixels per second
            short cameraSpeed = (short) (200 + 400 * glfwGetKey(window, GLFW_KEY_LEFT_SHIFT));

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
                screenSizeUpdated();
            }
            if (glfwGetKey(window, GLFW_KEY_EQUAL) != 0) {
                viewScale += 1;
                screenSizeUpdated();
            }
            for (int i = 0; i < 10; i++)
                if (glfwGetKey(window, GLFW_KEY_0 + i) != 0)
                    paintingPixel = i;


            if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) != 0) {
                setPixelAtCursorPosition(paintingPixel);
            } else if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_2) != 0)
                setPixelAtCursorPosition(0);

            if (glfwGetKey(window, GLFW_KEY_E) != 0) {
                jetPixelAtCursorPosition();
            }

            counter++;
            if (dt < (1000.0f/maxFps))
                try {
                    Thread.sleep((long)(1000.0f / maxFps - dt));
                } catch (InterruptedException e) {
                    continue;
                }
        }
    }


    private void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

//            glMatrixMode( GL_MODELVIEW );
//            glLoadIdentity();

        //Move to center of the screen
//            glTranslatef( screenWidth / 2.f, screenHeight / 2.f, 0.f );

        int I = 0;
        for (Map.Entry<VectorI, Chunk> entry : activeSubworld.activeChunks.entrySet()) {
            int baseX = entry.getKey().x * Chunk.size();
            int baseY = entry.getKey().y * Chunk.size();
            int i = 0;
            for (int pixel : entry.getValue().pixels) {
                Material material = activeWorld.pixelIds[Pixels.getId(pixel)];
                float drawX = (baseX + i % Chunk.size() - cameraPos.x) * relativePixelWidth;
                float drawY = (baseY + i / Chunk.size() - cameraPos.y) * relativePixelHeight;
                i++;
                if (drawX < (-1 - (relativePixelWidth)) || drawX > 1 ||
                        drawY < (-1 - (relativePixelHeight)) || drawY > 1
                ) {
                    continue;
                }
                int colorId = Pixels.getColor(pixel);
                drawPixel(drawX, drawY, I, material, colorId);
                I++;
            }
        }
        for (Entity entity : activeSubworld.entities) {
            if (entity instanceof PixelEntity) {
                Material material =
                        activeWorld.pixelIds[Pixels.getId(((PixelEntity) entity).pixel)];
                int colorId = Pixels.getColor(((PixelEntity) entity).pixel);
                drawPixel(
                        (entity.x - cameraPos.x) * relativePixelWidth,
                        (entity.y - cameraPos.y) * relativePixelHeight,
                        I, material, colorId
                );
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


    private void drawPixel(float drawX, float drawY, int i, Material material, int colorId) {
//        glColor3f(pixelDefinition.colors[0].r,
//                pixelDefinition.colors[0].g,
//                pixelDefinition.colors[0].b);

        int ci = i * 12;
        colorArray[ci] = material.colors[colorId].r;// + ci * 0.00001f;
        colorArray[ci+1] = material.colors[colorId].g;
        colorArray[ci+2] = material.colors[colorId].b;

        colorArray[ci+3] = material.colors[colorId].r;
        colorArray[ci+4] = material.colors[colorId].g;
        colorArray[ci+5] = material.colors[colorId].b;

        colorArray[ci+6] = material.colors[colorId].r;
        colorArray[ci+7] = material.colors[colorId].g;
        colorArray[ci+8] = material.colors[colorId].b;
//
        colorArray[ci+9] = material.colors[colorId].r;
        colorArray[ci+10] = material.colors[colorId].g;
        colorArray[ci+11] = material.colors[colorId].b;

        i *= 8;
        vertexArray[i] = drawX;
        vertexArray[i+1] = drawY;
        vertexArray[i+2] = drawX + relativePixelWidth;
        vertexArray[i+3] = drawY;
        vertexArray[i+4] = drawX + relativePixelWidth;
        vertexArray[i+5] = drawY + relativePixelHeight;
        vertexArray[i+6] = drawX;
        vertexArray[i+7] = drawY + relativePixelHeight;
    }


    void updateScreenSize(int[] width, int[] height) {

//        screenWidth = width[0];
//        screenHeight = height[0];
//        glfwSetWindowSize(window, screenWidth, screenHeight);
//        System.out.println("screenresized");
//        glfw
        screenSizeUpdated();
    }


    void screenSizeUpdated() {
        relativePixelWidth = viewScale / (float)screenWidth * 2;
        relativePixelHeight = viewScale / (float)screenHeight * 2;

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
        activeSubworld.updateChunksForUser(
                (int)cameraPos.x  / Chunk.size(), (int)cameraPos.y  / Chunk.size(), width, height);
    }


    void setPixelAtCursorPosition(int pixel) {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window, x, y);
        activeSubworld.setPixel(
                screenXToWorld((int)x[0]),
                screenYToWorld((int)y[0]), Pixels.getPixelWithRandomColor(pixel));
    }
    void jetPixelAtCursorPosition() {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window, x, y);
        int pixel = activeSubworld.getPixel(screenXToWorld((int)x[0]), screenYToWorld((int)y[0]));
        if (pixel != 0) {
            activeSubworld.setPixel(screenXToWorld((int)x[0]), screenYToWorld((int)y[0]), 0);
            activeSubworld.entities.add(new PixelEntity(
                    screenXToWorld((int)x[0]), screenYToWorld((int)y[0]), activeSubworld, pixel,
                    activeSubworld.random.nextFloat(-10f,10f),100,0,-9.8f
            ));
        }
    }
}
