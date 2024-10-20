import java.util.function.BiFunction;

import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.stb.STBImage.*;

public class Projectile extends Entity {
    /// Function that receives collided object (can be `Pixel`) and returns is collision really happened
    BiFunction<Projectile, Object, Boolean> onCollide = null;
    float velocityX = 0;
    float velocityY = 0;
    float accelerationX = 0;
    float accelerationY = 0;
    short width = 1;
    short height = 1;
    boolean rotatable = false;
    ColorWithAplha color;
    float angle = 0;

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

    public Projectile(float x, float y, Subworld subworld, BiFunction<Projectile, Object, Boolean> onCollide,
                      float velocityX, float velocityY, float accelerationX, float accelerationY,
                      short width, short height, boolean rotatable, ColorWithAplha color
    ) {
        this(x, y, subworld, onCollide, velocityX, velocityY, accelerationX, accelerationY);
        this.width = width;
        this.height = height;
        this.rotatable = rotatable;
        if (rotatable)
            angle = (float) Math.atan2(velocityY, velocityX);
        this.color = color;
    }

    @Override
    void update(float dt) {
        float newX = x + velocityX * dt;
        float newY = y + velocityY * dt;
        Pixel pixel = subworld.getPixel(Math.round(newX), Math.round(newY));
        if (pixel.chunk == null)
            return;
        if (onCollide.apply(this, pixel))
            return;
        if (rotatable)
            angle = (float) Math.atan2(velocityY, velocityX);
        x = newX;
        y = newY;
        velocityX += accelerationX;
        velocityY += accelerationY;
    }

    @Override
    void draw(float fdt) {
        Client client = (Client) Main.getGame();
        glColor3f(color.r, color.g, color.b);
        if (!rotatable) {
            glBegin(GL_QUADS);
            client.renderer.drawRectAtAbsCoordinates(
                    x - width / 2f, y - height / 2f,
                    x + width / 2f, y + height / 2f
            );
            glEnd();
        } else
            client.renderer.drawRectAtAbsCoordinates(
                    0, 0, width, height, angle, x, y
            );
    }
}
