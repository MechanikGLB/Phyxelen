package game;

import game.spells.Bullet;
import game.spells.Orb;
import game.spells.Sand;

import java.util.ArrayList;

import static java.lang.Math.PI;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL21.*;

public class Player extends Character {
    Client client = (Client)Main.getGame();
    ArrayList<HoldableItem> inventory = new ArrayList<>();
    boolean levitating = false;
    float levitationTime = 0f;
    float maxLevitationTime = 2f;
    byte animation = 0;
    static byte ANIMATION_IDLE = 0;
    static byte ANIMATION_WALK = 1;
    static byte ANIMATION_JUMP = 2;
    static byte ANIMATION_FALL = 3;
    private byte animationFrame = 0;
    private float animationTime = 0;
    boolean walking = false; /// For animation only

    public Player(float x, float y, Subworld subworld) {
        super(x, y, subworld);
        collisionBoxWidth = 4;
        collisionBoxHeight = 8;

        var wand = new Wand(this);
        wand.setTexture("wand_1.png");
        wand.spells.add(new Bullet());
        inventory.add(wand);
        wand = new Wand(this);
        wand.setTexture("wand_2.png");
        wand.spells.add(new Orb());
        inventory.add(wand);
        wand = new Wand(this);
        wand.setTexture("wand_3.png");
        wand.spells.add(new Sand());
        inventory.add(wand);
    }

    public ArrayList<HoldableItem> getInventory() { return inventory; }

    @Override
    void update(float dt) {
        super.update(dt);
        if (client.controlledCharacter == this) {
            // Pointing
            client.cameraPos.x = x;
            client.cameraPos.y = y;
            double[] x = new double[1];
            double[] y = new double[1];
            glfwGetCursorPos(client.window, x, y);
            setLookDirection(-(float) Math.atan2(
                    y[0] - client.renderer.screenHeight / 2d,
                    x[0] - client.renderer.screenWidth / 2d
            ));
            // Levitation
            if (levitating) {
                if (levitationTime < maxLevitationTime) {
                    vy += vy < 0 ? 20 : 10;
                    levitationTime += dt;
                }
            } else if (levitationTime > 0) {
                if (inAir)
                    levitationTime -= dt * 0.5f;
                else
                    levitationTime -= dt;
            }

            if (inAir) {
                if (levitating && levitationTime < maxLevitationTime)
                    animation = ANIMATION_JUMP;
                else if (vy < -40)
                    animation = ANIMATION_FALL;
            } else {
                if (walking)
                    animation = ANIMATION_WALK;
                else
                    animation = ANIMATION_IDLE;
            }
        }
        animationTime += dt;
        if (animationTime >= 0.6) {
            animationTime = 0;
            animationFrame = (byte)((animationFrame + 1) % 2);
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
            if (levitationTime > 0) {
                glColor3f(0.0f, 0.3f, 0.3f);
                glBegin(GL_QUADS);
                client.renderer.drawRectAtAbsCoordinates(
                        x - 4.4f, y - 6.4f, x + 4.4f, y - 4.6f);
                glColor3f(0.3f, 0.7f, 0.7f);
                client.renderer.drawRectAtAbsCoordinates(
                        x - 4, y - 6, x - 4 + (8f * (1 - levitationTime / maxLevitationTime)), y - 5);
                glEnd();
            }
        }
        glColor3f(1f, 1f, 1f);
        client.renderer.drawRectAtAbsCoordinates(
                0, 0, 10 * ((getLookDirection() > PI / 2 || getLookDirection() < -PI / 2) ? -1 : 1), 10,
                0, x, y,
                Content.getImage("player.png").getTextureBuffer(),
                0, 1, (animation * 2 + animationFrame) * 10/80f, (animation * 2 + 1 + animationFrame) * 10/80f);
        if (walking)
            walking = false;
        if (holdedItem != null)
            holdedItem.draw(fdt);
    }

    @Override
    void go(float dx, float dy) {
        walking = dx != 0;
        super.go(dx, dy);
    }
}
