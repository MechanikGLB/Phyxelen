package game.request;

import game.*;

public class PlayerUpdateRequest extends Request {
    Player target;
    float x;
    float y;
    float mx;
    float my;
    float angle;
    byte item;

    public PlayerUpdateRequest(Connection receiver, Player target, float x, float y, float mx, float my, float angle, byte item) {
        super(receiver);
        this.target = target;
        this.x = x;
        this.y = y;
        this.mx = mx;
        this.my = my;
        this.angle = angle;
        this.item = item;
    }

    @Override
    public void process() {

        var subworld = Main.getGame().getActiveSubworld();
        if (target.getId() != ((Client) Main.getGame()).getPrimaryCharacter().getId()) {
            target.setX(x);
            target.setY(y);
            target.go(mx, my);
        }
        target.setLookDirection(angle);
//        target.setHoldedItem(target.getInventory().get(item));
    }
}
