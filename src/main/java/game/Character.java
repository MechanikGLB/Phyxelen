package game;

public class Character extends EntityWithCollision {
    private float lookDirection;
    public boolean canGo = true;
    protected int maxHealth = 100;
    protected int health = maxHealth;
    protected boolean clipHealth = false;
    protected HoldableItem holdedItem;

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

    public HoldableItem getHoldedItem() {
        return holdedItem;
    }

    void go(float dx, float dy) {
        if (canGo) {
            Pixel targetPixel = subworld.getPixel(Math.round(x+dx), Math.round(y+dy));
            if (targetPixel.chunk == null)
                return;
            if (targetPixel.chunk.materials[targetPixel.i] == subworld.world.pixelIds[0]) {
                move(dx, dy);
            }
        }
    }
}
