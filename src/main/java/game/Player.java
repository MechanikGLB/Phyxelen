package game;

import game.spells.Bullet;
import game.spells.Orb;
import game.spells.Sand;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL21.*;

public class Player extends Character {
    Client client = (Client)Main.getGame();
    ArrayList<HoldableItem> inventory = new ArrayList<>();

    public Player(float x, float y, Subworld subworld) {
        super(x, y, subworld);
        /*TEMP*/
        var wand = new Wand(this);
        wand.spells.add(new Bullet());
        inventory.add(wand);
        wand = new Wand(this);
        wand.spells.add(new Orb());
        inventory.add(wand);
        wand = new Wand(this);
        wand.spells.add(new Sand());
        inventory.add(wand);
    }

    public ArrayList<HoldableItem> getInventory() { return inventory; }

    @Override
    void update(float dt) {
        super.update(dt);
        if (client.controlledCharacter == this) {
            client.cameraPos.x = x;
            client.cameraPos.y = y;
//            DoubleBuffer cursorPosition = DoubleBuffer.allocate(2);
            double[] x = new double[1];
            double[] y = new double[1];
//            glfwGetCursorPos(client.window, cursorPosition, cursorPosition);
            glfwGetCursorPos(client.window, x, y);
            x[0] /= client.renderer.screenWidth;
            y[0] /= client.renderer.screenHeight;
            setLookDirection(-(float) Math.atan2(
                    y[0] * 2 - 1,
                    x[0] * 2 - 1
            ));
        }
//        System.out.println(getLookDirection());
        if (holdedItem != null)
            holdedItem.update(dt);
    }

    @Override
    void draw(float fdt) {
        super.draw(fdt);
        if (client.controlledCharacter == this) {
            glColor3f(0.4f, 0.2f, 0.2f);
            glBegin(GL_QUADS);
            client.renderer.drawRectAtAbsCoordinates(
                    x - 4.4f, y + 6.4f, x + 4.4f, y + 4.6f);
            glColor3f(0.3f, 0.9f, 0.3f);
            client.renderer.drawRectAtAbsCoordinates(
                    x - 4, y + 6, x - 4 + (8f * health / maxHealth), y + 5);
            glEnd();
        }
        if (holdedItem != null)
            holdedItem.draw(fdt);
    }
}
