package game;

abstract public class Character extends EntityWithCollision {
    private float lookDirection;
    public boolean canGo = true;
    protected int maxHealth = 100;
    protected int health = maxHealth;
    protected boolean clipHealth = false;
    protected HoldableItem heldItem;
    float movingX = 0;
    float movingY = 0;

    public Character(float x, float y, Subworld subworld) {
        super(x, y, subworld, true);
        collisionBoxWidth = 4;
        collisionBoxHeight = 6;
    }

    public void setLookDirection(float lookDirection) {
        this.lookDirection = lookDirection; // TODO: clamp
    }

    public float getLookDirection() {
        return lookDirection;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        if (clipHealth && health > maxHealth)
            health = maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        if (clipHealth && health > maxHealth)
            health = maxHealth;
    }

    public void setMovingX(float movingX) { this.movingX = movingX; }
    public float getMovingX() { return movingX; }
    public void setMovingY(float movingY) { this.movingY = movingY; }
    public float getMovingY() { return movingY; }


    @Override
    void update(float dt) {
        super.update(dt);
        if (canGo) {
            Pixel targetPixel = subworld.getPixel(Math.round(x+movingX), Math.round(y+movingY));
            if (targetPixel.chunk == null)
                return;
            if (targetPixel.material().density < 2) {
                move(movingX, movingY);
            }
        }
    }

    public void damage(int damage) {
        this.health -= damage;
        if (clipHealth && health > maxHealth)
            health = maxHealth;
        if (health < 0)
            health = 0;
    }

    public void setClipHealth(boolean clipHealth) {
        this.clipHealth = clipHealth;
        if (clipHealth && health > maxHealth)
            health = maxHealth;
    }

    public HoldableItem getHeldItem() { return heldItem; }
    public void setHeldItem(HoldableItem heldItem) { this.heldItem = heldItem; }

    void go(float dx, float dy) {
        movingX = dx;
        movingY = dy;
    }
}
