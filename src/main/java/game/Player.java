package game;

import game.NetMessage.Message;
import game.NetMessage.PlayerHealthSync;
import game.NetMessage.PlayerSpawn;
import game.spells.*;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.PI;
import static java.lang.Math.round;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL21.*;

public class Player extends Character {
    Client client = (Client)Main.getGame();
    ArrayList<HoldableItem> inventory = new ArrayList<>();
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

    private final float respawnTime = 3f;
    private float respawnTimer = respawnTime;

    private short seed;
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }
    public short getSeed() { return seed; }
    //    public void setSeed(short seed) {this.seed = seed;}
//    public short getSeed() {return seed;}

    public Player(float x, float y, Subworld subworld, Connection connection) {
        super(x, y, subworld);
        seed = (short)(subworld.random().nextInt());
        collisionBoxWidth = 4;
        collisionBoxHeight = 8;
        health = 0;
        respawnTimer = 0.3f;
        this.connection = connection;
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
        }

        // Levitation
        if (movingY > 0) {
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
            if (movingY > 0 && levitationTime < maxLevitationTime)
                animation = ANIMATION_JUMP;
            else if (vy < -40)
                animation = ANIMATION_FALL;
        } else {
            if (walking)
                animation = ANIMATION_WALK;
            else
                animation = ANIMATION_IDLE;
        }

        animationTime += dt;
        if (animationTime >= 0.6) {
            animationTime = 0;
            animationFrame = (byte)((animationFrame + 1) % 2);
        }
//        System.out.println(getLookDirection());
        if (heldItem != null)
            heldItem.update(dt);

        if (health <= 0) {
            respawnTimer -= dt;
            if (respawnTimer <= 0) {
                respawnTimer = respawnTime;
                if (!Main.isClient())
                    subworld.spawnPlayer(this);
            }
        }
    }

    @Override
    void draw(float fdt) {
        super.draw(fdt);
        if (health <= 0)
            return;
        if (client.controlledCharacter == this) {
            glColor3f(0.4f, 0.2f, 0.2f);
            glBegin(GL_QUADS);
            client.renderer.drawRectAtAbsCoordinates(
                    x - 4.4f, y + 7.4f, x + 4.4f, y + 5.6f);
            glColor3f(0.3f, 0.9f, 0.3f);
            client.renderer.drawRectAtAbsCoordinates(
                    x - 4, y + 7, x - 4 + (8f * health / maxHealth), y + 6);
            glEnd();
            if (levitationTime > 0) {
                glColor3f(0.0f, 0.3f, 0.3f);
                glBegin(GL_QUADS);
                client.renderer.drawRectAtAbsCoordinates(
                        x - 4.4f, y - 7.4f, x + 4.4f, y - 5.6f);
                glColor3f(0.3f, 0.7f, 0.7f);
                client.renderer.drawRectAtAbsCoordinates(
                        x - 4, y - 7, x - 4 + (8f * (1 - levitationTime / maxLevitationTime)), y - 6);
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
        if (heldItem != null)
            heldItem.draw(fdt);
    }

    @Override
    public void go(float dx, float dy) {
        walking = dx != 0;
        if (dy < 0)
            return;
        super.go(dx, dy);
    }

    @Override
    public void damage(int damage) {
        if (health <= 0)
            return;
        super.damage(damage);
        if (health <= 0)
            die();

        if (Main.getGame().getGameState() == GameApp.GameState.Client){
            Main.getClient().addMessage(new PlayerHealthSync(id,health));
        } else if (Main.getGame().getGameState() == GameApp.GameState.Server) {
            Main.getServer().broadcastMessage(new PlayerHealthSync(id,health));
        }

    }

    public void spawn(int x, int y, short seed) {
        this.seed = seed;
        health = maxHealth;
        if (client.getPrimaryCharacter() == this)
            client.controlledCharacter = this;

        Random random = new Random(seed);
        Wand wand;
        int wandCount = random.nextInt(3,6);
        for (int i = 0; i < wandCount; i++) {
            wand = new Wand(this);
            int spellCount = random.nextInt(1, 3);
            for (int j = 0; j < spellCount; j++) {
                Spell spell;
                switch (random.nextInt(5)) {
                    case 1: spell = new Orb(); break;
                    case 2: spell = new WaterOrb(); break;
                    case 3: spell = new Sand(); break;
                    case 4: spell = new ExplosiveOrb(); break;
                    default: spell = new Bullet(); break;
                }
                wand.spells.add(spell);
            }
            wand.setTexture("wand_"+random.nextInt(1,4)+".png");
            inventory.add(wand);
        }
        heldItem = inventory.getFirst();

        if (client.getPrimaryCharacter() == this)
            client.setControlledCharacter(this);
    }

    @Override
    public Message getSpawnMessage() {
        return new PlayerSpawn((int)x,(int)y,id,seed);
    }

    public void die() {
        if (client.controlledCharacter == this)
            client.controlledCharacter = null;
        health = 0;
        subworld.fillPixels(round(x) - 2, round(y) - 5, 4, 9, Content.getMaterial("sand"), (byte) -1, 2);
        subworld.fillPixels(round(x) - 1, round(y) + 4, 2, 1, Content.getMaterial("sand"), (byte) -1, 2);
        movingX = 0;
        movingY = 0;
        heldItem.deactivate();
        inventory.clear();
    }
}
