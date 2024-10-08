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
    void tick(float dt) {
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
}
