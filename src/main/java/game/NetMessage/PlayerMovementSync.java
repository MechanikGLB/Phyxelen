package game.NetMessage;

import game.*;

import java.nio.ByteBuffer;

public class PlayerMovementSync extends Message {
    static byte id = 10;
    Player player;
    int entityId;
    float x;
    float y;
    float mx;
    float my;

    public PlayerMovementSync(Player player) {
        this.player = player;
    }

    public PlayerMovementSync(ByteBuffer message) {
        this.entityId = message.getInt();
        this.x = message.getFloat();
        this.y = message.getFloat();
        this.mx = message.getFloat();
        this.my = message.getFloat();
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(2 + Integer.BYTES + Float.BYTES * 6);
        message.put(id);
        message.putInt(player.getId());
        message.putFloat(player.getX());
        message.putFloat(player.getY());
        message.putFloat(player.getMovingX());
        message.putFloat(player.getMovingY());
        return message.array();
    }

    @Override
    public void process() {


        Player target = null;
        Subworld subworld = Main.getGame().getActiveSubworld();
        if (subworld == null)
            return;
        var players = subworld.getPlayers();
        for (Player p : players)
            if (p.getId() == entityId) {
                target = p;
                break;
            }
        if (target == null) {
            System.out.println("Not found player");
            return;
        }
        if (target.getId() != ((Client) Main.getGame()).getPrimaryCharacter().getId()) {
            target.setX(x);
            target.setY(y);
            target.go(mx, my);
        }
    }
}
