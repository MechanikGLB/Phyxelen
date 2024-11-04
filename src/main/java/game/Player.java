package game;

import game.spells.Bullet;
import game.spells.Orb;

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
        wand.spells.add(new Orb());
        inventory.add(wand);
//        inventory.add(new HoldableItem(null, item -> {
//            if (item.active && item.counter > 0.15) {
//                item.counter = 0;
//                subworld.addEntity(new Projectile(this.x, this.y, subworld,
//                        (projectile, o) -> {
//                            if (o instanceof Pixel && !(((Pixel) o).material() instanceof MaterialAir)) {
//                                ((Pixel) o).chunk.setPixel(((Pixel) o).i, subworld.world.pixelIds[0], (byte) 0);
//                                return true;
//                            }
//                            return false;
//                        },
//                        (float) Math.cos(getLookDirection()) * 200,
//                        (float) Math.sin(getLookDirection()) * 200,
//                        0f, -9.8f,
//                        (short) 3, (short) 1, true, new ColorWithAplha(1f, 1f, 0f, 1f)));
//            }}, null, this));
//        inventory.add(new HoldableItem(null, item -> {
//            if (item.active && item.counter > 0.3) {
//                item.counter = 0;
//                subworld.addEntity(new Projectile(this.x, this.y, subworld,
//                        (projectile, o) -> {
//                            if (o instanceof Pixel && !(((Pixel) o).material() instanceof MaterialAir)) {
//                                subworld.removeEntity(projectile);
//                                subworld.fillPixels(
//                                        ((Pixel)o).x() - 2, ((Pixel)o).y() - 2, 4, 4,
//                                        subworld.world.pixelIds[0], (byte) 0);
//                                return true;
//                            }
//                            return false;
//                        },
//                        (float) Math.cos(getLookDirection()) * 100,
//                        (float) Math.sin(getLookDirection()) * 100,
//                        0f, -9.8f,
//                        (short) 3, (short) 1, true, new ColorWithAplha(1f, 0.5f, 0f, 1f)));
//            }}, null, this
//        ));

//        holdedItem = inventory.getFirst();
    }

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
