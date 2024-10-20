import java.util.function.BiFunction;

public class Projectile extends Entity {
    /// Function that receives collided object (can be `Pixel`) and returns is collision really happened
    BiFunction<Projectile, Object, Boolean> onCollide = null;
    float velocityX = 0;
    float velocityY = 0;
    float accelerationX = 0;
    float accelerationY = 0;

    public Projectile(float x, float y, Subworld subworld, BiFunction<Projectile, Object, Boolean> onCollide) {
        super(x, y, subworld);
        this.onCollide = onCollide;
    }

    public Projectile(float x, float y, Subworld subworld, BiFunction<Projectile, Object, Boolean> onCollide, float velocityX, float velocityY, float accelerationX, float accelerationY) {
        super(x, y, subworld);
        this.onCollide = onCollide;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
    }

    @Override
    void update(float dt) {
        float newX = x + velocityX * dt;
        float newY = y + velocityY * dt;
        Pixel pixel = subworld.getPixel(Math.round(newX), Math.round(newY));
        if (onCollide.apply(this, pixel))
            return;

        x = newX;
        y = newY;
        velocityX += accelerationX;
        velocityY += accelerationY;
    }

    @Override
    void draw(float fdt) {

    }
}
