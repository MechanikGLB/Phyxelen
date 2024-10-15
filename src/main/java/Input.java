import java.util.*;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;


class InputAction {
    Consumer<Object> onStart;
    Consumer<Object> onTick;
    Consumer<Object> onFinish;

    boolean active = false;

    public InputAction(Consumer<Object> onStart, Consumer<Object> onTick, Consumer<Object> onFinish) {
        this.onStart = onStart;
        this.onTick = onTick;
        this.onFinish = onFinish;
    }

    void activate() {
        if (active) return;
        active = true;
        if (onStart != null)
            onStart.accept(null);
    }

    void tick() {
        if (onTick != null)
            onTick.accept(null);
    }

    void deactivate() {
        if (!active) return;
        active = false;
        if (onFinish != null)
            onFinish.accept(null);
    }
}


abstract class InputHandler {
    ArrayList<Integer> boundKeys = new ArrayList<>();
    ArrayList<Integer> pressedKeys = new ArrayList<>();
    ArrayList<ArrayList<InputAction>> keyBindings = new ArrayList<>();

    public void processInput(long window) {
        for (int i = 0; i < boundKeys.size(); i++) {
            int key = boundKeys.get(i);
            if (getKeyPressed(window, key)) {
                if (!pressedKeys.contains(key)) {
                    pressedKeys.add(key);
                    for (var action : keyBindings.get(i))
                        action.activate();
                }
            } else if (pressedKeys.contains(key)) {
                pressedKeys.remove(Integer.valueOf(key));
                for (var action : keyBindings.get(i))
                    action.deactivate();
            }
        }
    }

    /// Binds `key` to `action`
     /// @param key GLFW constant of key to bind
     /// @param inputAction Action to bind to
    public void bindKey(int key, InputAction inputAction) {
        int keyIndex = boundKeys.indexOf(key); // TODO: does it work?
        if (keyIndex < 0) {
            boundKeys.add(key);
            keyBindings.add(new ArrayList(Collections.singletonList(inputAction)));
        } else {
            keyBindings.get(keyIndex).add(inputAction);
        }
    }

    abstract boolean getKeyPressed(long window, int keyId);
}


class KeyboardHandler extends InputHandler {
    @Override
    boolean getKeyPressed(long window, int keyId) {
        return glfwGetKey(window, keyId) != 0;
    }
}


class MouseHandler extends InputHandler {
    @Override
    boolean getKeyPressed(long window, int keyId) {
        return glfwGetMouseButton(window, keyId) != 0;
    }
}


public class Input {
    private static HashMap<String, InputAction> inputActions = new HashMap<>();
    final private static KeyboardHandler keyboardHandler = new KeyboardHandler();
    final private static MouseHandler mouseHandler = new MouseHandler();

    public void addInputAction(String name, InputAction inputAction) {
        inputActions.put(name, inputAction);
    }
    public InputAction getInputAction(String name) {
        return inputActions.get(name);
    }
    public Set<Map.Entry<String, InputAction>> getAllInputActions() {
        return inputActions.entrySet();
    }

    public static KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }
    public static MouseHandler getMouseHandler() {
        return mouseHandler;
    }

    protected void processInput(long window) {
        keyboardHandler.processInput(window);
        mouseHandler.processInput(window);

        for (var action : inputActions.values())
            if (action.active)
                action.tick();
    }
}
