package game;

import game.NetMessage.PlayerHeldItemSync;
import game.NetMessage.PlayerMovementSync;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.system.MemoryUtil.NULL;


public class Client extends GameApp {
    protected long window;
    protected Renderer renderer = new Renderer(this);
    protected game.gui.Main gui = new game.gui.Main();
    Input input = new Input();

    float maxFps = 30;
    /// Frame delta time;
    float fdt;
    float maxTps = 30;
    /// Tick delta time
    float dt;

    protected VectorF cameraPos = new VectorF(0, 0);
    /// Length of world pixel side in real screen pixels
    protected short viewScale = 7;
    /// Free camera movement speed in screen pixels per second
    protected short cameraSpeed = 150;
    Player controlledCharacter;
    private Player primaryCharacter;

    // Editor
    boolean editMode = false;
    private int paintingPixel = 1;
    private int paintingSize = 0;

    ArrayList<WindowResizeListener> windowResizeListeners = new ArrayList<>();


    public Character getControlledCharacter() { return controlledCharacter; }
    public Character getPrimaryCharacter() { return primaryCharacter; }
    public void setControlledCharacter(Player controlledCharacter) { this.controlledCharacter = controlledCharacter; }
    public void setPrimaryCharacter(Player character) {primaryCharacter = character;}

    @Override
    public void run() {
        initGlfw();
        GL.createCapabilities();
        renderer.init();
        gui.init();
        super.run();
        bindKeys();

        if ( gameState != GameState.Client ) {
            primaryCharacter = new Player(0, 0, activeSubworld, null);
            activeSubworld.spawnPlayer(primaryCharacter);
        }

        System.out.println("Loading textures");
        try {
            Content.loadTextures();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

        while (!glfwWindowShouldClose(window)) {
            long cycleStartTime = System.currentTimeMillis();

            fdt = (cycleStartTime - lastCycleStartTime) / 1000.0f;
            if (fdt < (1.0f / maxFps)) {
                try {
                    Thread.sleep((long) ((1.0f / maxFps - fdt) * 1000.0f));
                } catch (InterruptedException e) {
                    continue;
                }
                continue;
            }
            lastCycleStartTime = cycleStartTime;

            if (counter % 8 == 0) {
                updateChunks();
            }
//            System.out.println(dt);
            tick(fdt);

            renderer.draw();

            glfwPollEvents();
            input.processInput(window);


            for (int i = 0; i < 10; i++)
                if (glfwGetKey(window, GLFW_KEY_0 + i) != 0)
                    if (controlledCharacter != null && ((Player)controlledCharacter).inventory.size() >= i && i>0)
                        controlledCharacter.heldItem = ((Player)controlledCharacter).inventory.get(i-1);
                    else
                        paintingPixel = i;
            if (glfwGetKey(window, GLFW_KEY_LEFT_BRACKET) != 0 && paintingSize > 1)
                paintingSize -= 1;
            if (glfwGetKey(window, GLFW_KEY_RIGHT_BRACKET) != 0)
                paintingSize += 1;

            if (glfwGetKey(window, GLFW_KEY_SPACE) != 0)
                jetPixelsAtCursorPosition();

            counter++;
        }
//        try {
//            logicThread.join();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }


    @Override
    protected void tick(float dt) {
        super.tick(dt);
        if (gameState != GameState.Local && counter % 4 == 0 && controlledCharacter != null) {
            if (Main.getClient() != null)
                Main.getClient().addMessage(new PlayerHeldItemSync(controlledCharacter));
            else
                Main.getServer().broadcastMessage(new PlayerHeldItemSync(controlledCharacter));
        }
    }

    @Override
    public void enterSubworld(Subworld subworld) {
        super.enterSubworld(subworld);
        activeSubworld.renderer = new SubworldRenderer(activeSubworld);
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

    float worldXToScreen(float x) {
        return (x - cameraPos.x) * viewScale + renderer.screenWidth / 2;
    }

    float worldYToScreen(float y) {
        return (y - cameraPos.y) * viewScale + renderer.screenHeight / 2;
    }


    private void updateChunks() {
        if (activeSubworld == null) return;
        activeSubworld.updateChunksForUsers();
    }


    void setPixelAtCursorPosition(int pixel) {
        double[] x = new double[1];
        double[] y = new double[1];
        Material material = activeWorld.pixelIds[pixel];
        glfwGetCursorPos(window, x, y);
        for (int dx = -paintingSize/2; dx <= paintingSize/2; dx++) {
            for (int dy = -paintingSize/2; dy <= paintingSize/2; dy++) {
                activeSubworld.presetPixel(
                        screenXToWorld((int) x[0]) + dx, screenYToWorld((int) y[0]) + dy,
                        material, (byte) activeSubworld.random.nextInt(material.colors.length));
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
//            activeSubworld.entities.add(new game.PixelEntity(
//                    screenXToWorld((int) x[0]), screenYToWorld((int) y[0]), activeSubworld, pixel,
//                    activeSubworld.random.nextFloat(-20f, 20f), 100, 0, -9.8f
//            ));
//        }
    }

    void jetPixelsAtCursorPosition() {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window, x, y);
        activeSubworld.jetPixels(screenXToWorld((int) x[0]), screenYToWorld((int) y[0]), paintingSize);
    }


    void syncPlayer() {
        if (Main.isClient())
            Main.getClient().addMessage(new PlayerMovementSync(controlledCharacter));
        else if (Main.isServer())
            Main.getServer().broadcastMessage(new PlayerMovementSync(controlledCharacter));
    }


    void bindKeys() {
        // Movement keys
        input.addInputAction("MoveUp",new InputAction(
                o -> {
                    if (controlledCharacter != null) {
                        controlledCharacter.movingY += 1;
                        syncPlayer();
                    }
                },
                o -> {
                    if (editMode)
                        cameraPos.y += cameraSpeed / viewScale * fdt;},
                o -> {
                    if (controlledCharacter != null) {
                        controlledCharacter.movingY -= 1;
                        syncPlayer();
                    }
                }));
        input.getKeyboardHandler().bindKey(GLFW_KEY_W, "MoveUp");
        input.getKeyboardHandler().bindKey(GLFW_KEY_UP, "MoveUp");

        input.addInputAction("MoveDown",new InputAction(
                o -> {
                    if (controlledCharacter != null) {
                        controlledCharacter.movingY -= 1;
                        syncPlayer();
                    }
                },
                o -> {
                if (editMode)
                    cameraPos.y -= cameraSpeed / viewScale * fdt;
                },
                o -> {
                    if (controlledCharacter != null) {
                        controlledCharacter.movingY += 1;
                        syncPlayer();
                    }
                }));
        input.getKeyboardHandler().bindKey(GLFW_KEY_S, "MoveDown");
        input.getKeyboardHandler().bindKey(GLFW_KEY_DOWN, "MoveDown");

        input.addInputAction("MoveRight",new InputAction(
                o -> {
                    if (controlledCharacter != null) {
                        controlledCharacter.movingX += 1;
                        syncPlayer();
                    }
                },
                o -> {
                    if (editMode)
                        cameraPos.x += cameraSpeed / viewScale * fdt;
                },
                o -> {
                    if (controlledCharacter != null) {
                        controlledCharacter.movingX -= 1;
                        syncPlayer();
                    }
                }));
        input.getKeyboardHandler().bindKey(GLFW_KEY_D, "MoveRight");
        input.getKeyboardHandler().bindKey(GLFW_KEY_RIGHT, "MoveRight");

        input.addInputAction("MoveLeft",new InputAction(
                o -> {
                    if (controlledCharacter != null) {
                        controlledCharacter.movingX -= 1;
                        syncPlayer();
                    }
                },
                o -> {
                    if (editMode)
                        cameraPos.x -= cameraSpeed / viewScale * fdt;
                },
                o -> {
                    if (controlledCharacter != null) {
                        controlledCharacter.movingX += 1;
                        syncPlayer();
                    }
                }));
        input.getKeyboardHandler().bindKey(GLFW_KEY_A, "MoveLeft");
        input.getKeyboardHandler().bindKey(GLFW_KEY_LEFT, "MoveLeft");
        // Temp? TODO: decide what to do with it
        input.addInputAction("Shift",new InputAction(
                o -> cameraSpeed = 600, null, o -> cameraSpeed = 200));
        input.getKeyboardHandler().bindKey(GLFW_KEY_LEFT_SHIFT, "Shift");
        input.getKeyboardHandler().bindKey(GLFW_KEY_RIGHT_SHIFT, "Shift");

        // Scale
        input.addInputAction("ScaleMinus",new InputAction(o -> {
            if (viewScale == 1) return;
            viewScale -= 1;
            renderer.screenSizeUpdated();
        }, null, null));
        input.getKeyboardHandler().bindKey(GLFW_KEY_MINUS, "ScaleMinus");

        input.addInputAction("ScalePlus",new InputAction(o -> {
            viewScale += 1;
            renderer.screenSizeUpdated();
        }, null, null));
        input.getKeyboardHandler().bindKey(GLFW_KEY_EQUAL, "ScalePlus");

        // Interacting
        input.addInputAction("PrimaryAction", new InputAction(
                o -> {
                    if (controlledCharacter != null &&
                            controlledCharacter.heldItem != null)
                        controlledCharacter.heldItem.activate();
                },
                o -> {
                    if (editMode)
                        setPixelAtCursorPosition(paintingPixel);
                },
                o -> {
                    if (controlledCharacter != null &&
                            controlledCharacter.heldItem != null)
                        controlledCharacter.heldItem.deactivate();
                }
        ));
        input.getMouseHandler().bindKey(GLFW_MOUSE_BUTTON_1, "PrimaryAction");

        // Development
        input.addInputAction("SwitchEditorMode", new InputAction(o -> {
            if (controlledCharacter == null) {
                controlledCharacter = primaryCharacter;
                editMode = false;
            } else {
                controlledCharacter = null;
                editMode = true;
            }
        }, null, null));
        input.getKeyboardHandler().bindKey(GLFW_KEY_E, "SwitchEditorMode");

        input.addInputAction("PhysicCounterDown", new InputAction(o -> {
            activeSubworld.pixelPhysicFreezingCountdown -= 1;
        }, null, null));
        input.getKeyboardHandler().bindKey(GLFW_KEY_PERIOD, "PhysicCounterDown");

        input.addInputAction("PhysicCounterUp", new InputAction(o -> {
            activeSubworld.pixelPhysicFreezingCountdown += 1;
        }, null, null));
        input.getKeyboardHandler().bindKey(GLFW_KEY_COMMA, "PhysicCounterUp");
    }


    class Renderer {
        Client client;
        protected int screenWidth = 800;
        protected int screenHeight = 600;
        protected float relativePixelWidth = 0.01f;
        protected float relativePixelHeight = 0.01f;


        Renderer(Client client) {
            this.client = client;
        }


        void init() {
            int[] width = new int[1];
            int[] height = new int[1];
            glfwGetWindowSize(client.window, width, height);
            screenWidth = width[0];
            screenHeight = height[0];
            screenSizeUpdated();
        }


        public void draw() {
            glViewport(0, 0, screenWidth, screenHeight);

            glMatrixMode( GL_PROJECTION );
            glLoadIdentity();
            glOrtho( 0.0, screenWidth, 0, screenHeight, 1.0, -1.0 );
            glMatrixMode( GL_MODELVIEW );
            glLoadIdentity();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_COLOR_ARRAY);
            glEnableClientState(GL_ALPHA);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable( GL_BLEND );

            activeSubworld.draw(fdt);

            // Frame rate bar
            float frameRate = 1f / fdt;
            glColor3f(1f, 1f, 0f);
            glBegin(GL_LINES);
            glVertex2i(4, screenHeight - 4);
            glVertex2f(4 + 5 * frameRate, screenHeight - 4);
            // Frame time bar
            glVertex2i(4, screenHeight - 6);
            glVertex2f(4 + 2000.0f * fdt, screenHeight - 6);
            // Profiler bars
            int e = 0;
            for (var entry : Profiler.entries.entrySet()) {
                glColor3b(entry.getValue().r, entry.getValue().g, entry.getValue().b);
                glVertex2i(4, screenHeight - 2 * e);
                glVertex2f(4 + entry.getValue().value * 100, screenHeight - 2 * e);
                e++;
            }
            glColor3f(0f, 1f, 0f);
            glEnd();
            // Marks
            glBegin(GL_POINTS);
            glVertex2i(5 * 30, screenHeight - 3);
            glVertex2i(5 * 60, screenHeight - 3);
            glVertex2i(5 * 120, screenHeight - 3);
            glEnd();
//            glBegin(GL_QUADS);
//            glColor4f(1,1,1,0.1f);
//            drawRectAtAbsCoordinates(0,0, Chunk.size(), Chunk.size());
//            glEnd();
            gui.draw(renderer.screenWidth, renderer.screenHeight);
            glfwSwapBuffers(window);
        }


        public void drawRectAtAbsCoordinates(float x1, float y1, float x2, float y2) {
            float startX = worldXToScreen(x1);
            float startY = worldYToScreen(y1);
            float width = (x2 - x1) * viewScale;
            float height = (y2 - y1) * viewScale;
            glVertex2f(startX, startY);
            glTexCoord2f(0f,0f);
            glVertex2f(startX + width, startY);
            glTexCoord2f(1f,0f);
            glVertex2f(startX + width, startY + height);
            glTexCoord2f(1f,1f);
            glVertex2f(startX, startY + height);
            glTexCoord2f(0f,1f);
        }

        public void drawRectAtAbsCoordinates(float centerX, float centerY, float w, float h,
                                             float angle, float rotationX, float rotationY, int texture,
                                             float uv0, float uv1, float uv2, float uv3
        ) {
            w *= viewScale; h *= viewScale;
            centerX *= viewScale; centerY *= viewScale;
            glLoadIdentity();
            float x = worldXToScreen(centerX - rotationX);
            float y = worldYToScreen(centerY - rotationY);

            glTranslatef(worldXToScreen(rotationX), worldYToScreen(rotationY), 0f);
            glRotatef(angle * 180f / (float)Math.PI,0, 0, 1);
            glTranslatef(0, 0, 0f);
            if (texture > 0) {
                glEnable(GL_TEXTURE_2D);
                glBindTexture(GL_TEXTURE_2D, texture);
            }
            glBegin(GL_QUADS);
            glTexCoord2f(uv2, uv0);
            glVertex2f(centerX - w/2, centerY + h/2);
            glTexCoord2f(uv3, uv0);
            glVertex2f(centerX + w/2, centerY + h/2);
            glTexCoord2f(uv3, uv1);
            glVertex2f(centerX + w/2, centerY - h/2);
            glTexCoord2f(uv2,  uv1);
            glVertex2f(centerX - w/2, centerY - h/2);
            glEnd();
            if (texture > 0)
                glDisable(GL_TEXTURE_2D);
            glLoadIdentity();
        }

        public void drawRectAtAbsCoordinates(float centerX, float centerY, float w, float h,
                                             float angle, float rotationX, float rotationY, int texture) {
            drawRectAtAbsCoordinates(centerX, centerY, w, h, angle, rotationX, rotationY, texture, 0, 1, 0, 1);
        }


        void screenSizeUpdated() {
            relativePixelWidth = viewScale / (float) screenWidth * 2;
            relativePixelHeight = viewScale / (float) screenHeight * 2;

//        int worldPixelCount = activeSubworld.loadedChunks.entrySet().size() * game.Chunk.area();
            int widthInWorldPixels = (int) (screenWidth / viewScale + 2);
            int heightInWorldPixels = (int) (screenHeight / viewScale + 2);

            int worldPixelCount = widthInWorldPixels * heightInWorldPixels;
//            vertexArray = new float[worldPixelCount * 2];
//
//            colorArray = new float[worldPixelCount * 8 * 3];
//            movingPixelVertexArray = new float[worldPixelCount * 4];
//            movingPixelColorArray = new float[worldPixelCount * 4 * 3];
////            glEnableClientState(GL_VERTEX_ARRAY);
//            glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
//            glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_STREAM_DRAW);
//
////            glEnableClientState(GL_COLOR_ARRAY);
//            glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
//            glBufferData(GL_ARRAY_BUFFER, colorArray, GL_STREAM_DRAW);
//
////            glBindAttribLocation();
//            elementArray = new int[vertexArray.length * 2];
//            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
//            glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementArray, GL_STREAM_DRAW);


            for (var listener : windowResizeListeners)
                listener.onWindowResize(window, screenWidth, screenHeight);
        }
    }
}