import static org.lwjgl.opengl.GL20.*;

public class PixelEntity extends Entity {
    Material material;
    byte color;
    float velocityX = 0;
    float velocityY = 0;
    float accelerationX = 0;
    float accelerationY = 0;

    public PixelEntity(float x, float y, Subworld subworld, Material material, byte color) {
        super(x, y, subworld);
        this.material = material;
        this.color = color;
    }
    public PixelEntity(
            float x, float y, Subworld subworld, Material material, byte color,
            float velocityX, float velocityY,
            float accelerationX, float accelerationY) {
        this(x, y, subworld, material, color);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
    }

    @Override
    void update(float dt) {
        float newX = x + velocityX * dt;
        float newY = y + velocityY * dt;
        if (!(subworld.getPixelMaterial(Math.round(newX), Math.round(newY)) instanceof MaterialAir)) {
            subworld.setPixel(Math.round(x), Math.round(y), material, color);
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
        ColorWithAplha color = material.colors[this.color];
        glColor4f(color.r, color.g, color.g, color.alpha);
        // TEMP?
//        glBegin(GL_QUADS);
//
//        glEnd();
    }
}
