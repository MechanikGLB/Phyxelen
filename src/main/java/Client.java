import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Client extends GameApp {
    protected long window;
    private int screenWidth = 800;
    private int screenHeight = 600;
    private VectorF cameraPos = new VectorF(0, 0);
    private short viewScale = 16;


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
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
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
        counter++;
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

        System.out.println("Start");
        while ( !glfwWindowShouldClose(window) ) {
            if (counter % 32 == 0)
                updateChunks();
            draw();
            glfwPollEvents();
        }
    }


    private void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

//            glMatrixMode( GL_MODELVIEW );
//            glLoadIdentity();

        //Move to center of the screen
//            glTranslatef( screenWidth / 2.f, screenHeight / 2.f, 0.f );

        // TODO: pixel drawing
        for (Map.Entry<VectorI, Chunk> entry : activeSubworld.loadedChunks.entrySet())
            for (int pixel : entry.getValue().pixels) {
                PixelDefinition pixelDefinition = activeWorld.pixelIds[pixel];

            }

        glBegin( GL_QUADS );
        glVertex2f( -0.5f, -0.5f );
        glVertex2f(  0.5f, -0.5f );
        glVertex2f(  0.5f,  0.5f );
        glVertex2f( -0.5f,  0.5f );
        glEnd();

//            glBegin(GL_TRIANGLES);
//            glColor3f(1.0f, 0.0f, 0.0f);
//            glVertex2d(-0.5, -0.5);
//            glColor3f(0.0f, 1.0f, 0.0f);
//            glVertex2d(0.0, 0.5);
//            glColor3f(0.0f, 0.0f, 1.0f);
//            glVertex2d(0.5, -0.5);
//            glEnd();

        glfwSwapBuffers(window); // swap the color buffers
    }


    private void updateChunks() {
        if (activeSubworld == null) return;


    }
}
