import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL
import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Client extends GameApp {
    protected long window;
    private int screenWidth = 800;
    private int screenHeight = 600;
    private VectorF cameraPos = new VectorF(0, 0);
    /// Length of world pixel side in real screen pixels
    private short viewScale = 16;
    private float relativePixelWidth = 0.01f;
    private float relativePixelHeight = 0.01f;


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
        window = glfwCreateWindow(800, 600, "Hello World!", NULL, NULL);
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
        screenSizeUpdated();
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
        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (videoMode != null) {
//            throw new Exception("No video mode");
            screenWidth = videoMode.width();
            screenHeight = videoMode.height();
        } else {
            System.err.println("No video mode");
        };

        //Initialize Projection Matrix
        glMatrixMode( GL_PROJECTION );
        glLoadIdentity();

        //Initialize Modelview Matrix
        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();

        long time = System.currentTimeMillis();
        System.out.println("Start");
        while ( !glfwWindowShouldClose(window) ) {
            counter++;
            if (counter % 32 == 0)
                updateChunks();
            if (counter % 1024 == 0)
                draw();

            long newTime = System.currentTimeMillis();
            float dt = newTime - time;
            tick(dt);
            time = newTime;

            glfwPollEvents();


            if (glfwGetKey(window, GLFW_KEY_W) != 0) cameraPos.y += relativePixelHeight * dt;
            if (glfwGetKey(window, GLFW_KEY_S) != 0) cameraPos.y -= relativePixelHeight * dt;
            if (glfwGetKey(window, GLFW_KEY_A) != 0) cameraPos.x -= relativePixelWidth * dt;
            if (glfwGetKey(window, GLFW_KEY_D) != 0) cameraPos.x += relativePixelWidth * dt;
        }
    }


    private void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

//            glMatrixMode( GL_MODELVIEW );
//            glLoadIdentity();

        //Move to center of the screen
//            glTranslatef( screenWidth / 2.f, screenHeight / 2.f, 0.f );

//        glBegin( GL_TRIANGLES );
//        glDisableClientState(GL_COLOR_ARRAY);
//        glDisableClientState(GL_NORMAL_ARRAY);
//        glDisableClientState(GL_INDEX_ARRAY);
//        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
//        glDisableClientState(GL_EDGE_FLAG_ARRAY);
//        GL20.glEnableVertexAttribArray(0);
        int totalCount = activeSubworld.loadedChunks.entrySet().size() * Chunk.area();
//        FloatBuffer coordinates = FloatBuffer.allocate(8);
        float[] coordinateArray = {0f,0f, 0f,1f, 1f,1f};
        FloatBuffer coordinateBuffer = FloatBuffer.allocate(coordinateArray.length);
        coordinateBuffer.put(coordinateArray);
        glEnableClientState(GL_VERTEX_ARRAY);

        int vertexBuffer = glGenBuffers();
//        System.out.println(vertexBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, coordinateArray, GL_STATIC_DRAW);

        glVertexPointer(2, GL_FLOAT, 0, 0);
//        glVertexAttribPointer();

//        IntBuffer indexBuffer = IntBuffer.allocate(3);
//        int[] indexArray = {0,1,2};
//        indexBuffer.put(indexArray);
//        int elementBuffer = glGenBuffers();
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexArray, GL_STATIC_DRAW);
//        FloatBuffer coordinates = FloatBuffer.allocate(totalCount * 8);


//        for (Map.Entry<VectorI, Chunk> entry : activeSubworld.loadedChunks.entrySet()) {
//            int baseX = entry.getKey().x * Chunk.size();
//            int baseY = entry.getKey().y * Chunk.size();
//            int i = 0;
//            for (int pixel : entry.getValue().pixels) {
//                PixelDefinition pixelDefinition = activeWorld.pixelIds[pixel];
//                drawPixel(baseX + i % Chunk.size(),
//                        baseY + i / Chunk.size(), i,
//                        pixelDefinition, coordinates);
//                i++;
//            }
//        }
        PixelDefinition pixelDefinition = activeWorld.pixelIds[1];
        glColor3f(pixelDefinition.colors[0].r,
                pixelDefinition.colors[0].g,
                pixelDefinition.colors[0].b);

//        coordinates.put(0); coordinates.put(1);
//        System.out.printf("%d  %d\n", totalCount * 8, coordinates.array().length);

//        glDrawArrays(GL_TRIANGLES, 0, totalCount * 6);

        glDrawArrays(GL_TRIANGLES, 0, 6);
//        glDrawBuffer(vertexBuffer);
//        glDrawElements(GL_TRIANGLES, indexes);
//        } catch (Exception e) {
//            System.out.println(glGetError());
//        }

//        glEnd();
        glDeleteBuffers(vertexBuffer); // TODO: doesn't work
        glDisableClientState(GL_VERTEX_ARRAY);

//        glDeleteBuffers(elementBuffer);
        glfwSwapBuffers(window); // swap the color buffers
    }


    /// @param x Pixel global X coordinate
     /// @param y Pixel global X coordinate
    private void drawPixel(int x, int y, int i, PixelDefinition pixelDefinition, FloatBuffer buffer) {
        glColor3f(pixelDefinition.colors[0].r,
                pixelDefinition.colors[0].g,
                pixelDefinition.colors[0].b);
        float drawX = (x - cameraPos.x + screenWidth / 2.0f) * relativePixelWidth;
        float drawY = (y - cameraPos.y + screenHeight / 2.0f) * relativePixelHeight;
//        System.out.printf("(%d; %d)\n", x, y);
//        System.out.printf("(%f;%f)\n", drawX, drawY);

        if (drawX < -1.02 || drawX > 1 || drawY < -1.02 || drawY > 1 ) return;
        glVertex2f( drawX, drawY );
        glVertex2f( drawX + relativePixelWidth, drawY );
        glVertex2f( drawX + relativePixelWidth, drawY + relativePixelHeight);
        glVertex2f( drawX, drawY + relativePixelHeight);

//        buffer.put(0); buffer.put(0);
//        buffer.put(1); buffer.put(0);
//        buffer.put(0); buffer.put(1);
//        buffer.put(1); buffer.put(1);
//        buffer.put(drawX); buffer.put(drawY);
//        buffer.put(drawX + relativePixelWidth); buffer.put(drawY );
//        buffer.put(drawX + relativePixelWidth); buffer.put(drawY + relativePixelHeight);
//        buffer.put(drawX); buffer.put(drawY + relativePixelHeight);
    }


    void screenSizeUpdated() {
        relativePixelWidth = viewScale / (float)screenWidth;
        relativePixelHeight = viewScale / (float)screenHeight;

//        if (screenWidth > screenHeight) {
//            relativePixelSize = viewScale / (float)screenWidth;
//        } else {
//            relativePixelSize = viewScale / (float)screenHeight;
//        }
    }


    private void updateChunks() {
        if (activeSubworld == null) return;
        int width = screenWidth / viewScale;
        int height = screenHeight / viewScale;

        for (int x = -width/2; x < width/2; x++) {
            for (int y = -height/2; y < height/2; y++) {
                VectorI indexes = new VectorI(x, y);
                if (!activeSubworld.loadedChunks.containsKey(indexes)) {
                    activeSubworld.loadChunk(indexes);
                }
            }
        }
    }
}
