public class PixelEntity extends Entity {
    int pixel = 0;
    float velocityX = 0;
    float velocityY = 0;
    float accelerationX = 0;
    float accelerationY = 0;

    public PixelEntity(float x, float y, Subworld subworld, int pixel) {
        super(x, y, subworld);
        this.pixel = pixel;
    }
    public PixelEntity(
            float x, float y, Subworld subworld, int pixel,
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
        if (subworld.getPixel(Math.round(newX), Math.round(newY), 0) != 0) {
            subworld.setPixel(Math.round(x), Math.round(y), pixel);
            subworld.removeEntity(this);
            return;
        }
        x = newX;
        y = newY;
        velocityX += accelerationX;
        velocityY += accelerationY;
    }
}
