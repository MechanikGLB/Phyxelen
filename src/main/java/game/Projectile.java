package game;

import game.NetMessage.Message;
import game.NetMessage.PlayerSpawn;
import game.NetMessage.ProjectileSpawn;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static org.lwjgl.opengl.GL21.*;

public class Projectile extends Entity {
    // State
    float velocityX = 0;
    float velocityY = 0;
    float accelerationX = 0;
    float accelerationY = 0;
    short width = 1;
    short height = 1;
    boolean rotatable = false;
    float angle = 0;
    Character caster = null;

    Image image;
    ColorWithAplha color;

    /// Function which receive collided object (can be `game.Pixel`) and returns is collision really happened
    BiFunction<Projectile, Object, Boolean> onCollide = null;
    /// Functions which receive projectile and delta tick
    ArrayList<BiConsumer<Projectile, Float>> onUpdate = new ArrayList<>();
    /// Functions which receive collided object (can be `game.Pixel`) and returns is collision really happened
    ArrayList<BiConsumer<Projectile, Object>> onHit = new ArrayList<>();


    public Projectile(float x, float y, Subworld subworld, BiFunction<Projectile, Object, Boolean> onCollide) {
        super(x, y, subworld);
        this.onCollide = onCollide;
    }

    public Projectile(float x, float y, Subworld subworld, BiFunction<Projectile, Object, Boolean> onCollide,
                      float velocityX, float velocityY, float accelerationX, float accelerationY
    ) {
        this(x, y, subworld, onCollide);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
    }

    public Projectile(float x, float y, Subworld subworld, Character caster,
                      BiFunction<Projectile, Object, Boolean> onCollide,
                      float velocityX, float velocityY, float accelerationX, float accelerationY,
                      short width, short height, boolean rotatable, String image, ColorWithAplha color
    ) {
        this(x, y, subworld, onCollide, velocityX, velocityY, accelerationX, accelerationY);
        this.width = width;
        this.height = height;
        this.rotatable = rotatable;
        if (rotatable)
            angle = (float) Math.atan2(velocityY, velocityX);
        if (image != null)
            this.image = Content.getImage(image);
        this.color = color;
        this.caster = caster;
    }

    @Override
    void update(float dt) {
        Pixel castResult = subworld.rayCast(x, y, x + velocityX * dt, y + velocityY * dt, 1);
        float newX = castResult == null ? x + velocityX * dt : castResult.x();
        float newY = castResult == null ? y + velocityY * dt : castResult.y();

        for (EntityWithCollision entity : subworld.collidableEntities) {
            if (entity.intersectedByRay(x, y, newX, newY)) {
                if (onCollide.apply(this, entity)) {
                    subworld.removeEntity(this);
                    for (var func : onHit)
                        func.accept(this, entity);
                    return;
                }
            }
        }

        Pixel pixel = castResult == null ? subworld.getPixel(newX, newY) : castResult;
        if (pixel.chunk == null)
            return;
        if (onCollide.apply(this, pixel)) {
            subworld.removeEntity(this);
            for (var func : onHit)
                func.accept(this, pixel);
            return;
        }
        for (var func : onUpdate)
            func.accept(this, dt);

        if (rotatable)
            angle = (float) Math.atan2(velocityY, velocityX);
        x = newX;
        y = newY;
        velocityX += accelerationX;
        velocityY += accelerationY;
    }

    @Override
    public Message getSpawnMessage() {
        if (caster != null) return new ProjectileSpawn(caster.getId());
        else return new ProjectileSpawn();
    }

    @Override
    void draw(float fdt) {
        Client client = (Client) Main.getGame();
//        glColor3f(color.r, color.g, color.b);
//        glBindTexture(GL_TEXTURE_2D, );
        glColor4f(color.r, color.g, color.b, color.alpha);
        if (!rotatable) {
            glBegin(GL_QUADS);
            client.renderer.drawRectAtAbsCoordinates(
                    x - width / 2f, y - height / 2f,
                    x + width / 2f, y + height / 2f
            );
            glEnd();
        } else
            client.renderer.drawRectAtAbsCoordinates(
                    0, 0, width, height, angle, x, y,
                    image != null ? image.getTextureBuffer() : 0
            );
        glDisable(GL_TEXTURE_2D);
    }
}
