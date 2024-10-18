import static org.lwjgl.opengl.GL20.*;

public class PixelEntity extends Entity {
    Pixel pixel;
    float velocityX = 0;
    float velocityY = 0;
    float accelerationX = 0;
    float accelerationY = 0;

    public PixelEntity(float x, float y, Subworld subworld, Pixel pixel) {
        super(x, y, subworld);
        this.pixel = pixel;
    }
    public PixelEntity(
            float x, float y, Subworld subworld, Pixel pixel,
            float velocityX, float velocityY,
            float accelerationX, float accelerationY) {
        super(x, y, subworld);
        this.pixel = pixel;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
    }

    @Override
    void update(float dt) {
        float newX = x + velocityX * dt;
        float newY = y + velocityY * dt;
        if (!(subworld.getPixel(Math.round(newX), Math.round(newY)).material instanceof MaterialAir)) {
            pixel.x = Math.round(x);
            pixel.y = Math.round(y);
//            pixel.material =
            subworld.setPixel(pixel);
            subworld.removeEntity(this);
            return;
        }
        x = newX;
        y = newY;
        velocityX += accelerationX;
        velocityY += accelerationY;
    }

    @Override
    void draw(float fdt) {
        // TODO: waits for new world pixel implementation merge
        ColorWithAplha color = pixel.material.colors[pixel.color];
        glColor4f(color.r, color.g, color.g, color.alpha);
        // TEMP?
//        glBegin(GL_QUADS);
//
//        glEnd();
    }
}
