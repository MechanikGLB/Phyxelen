package game.NetMessage;

import game.Client;
import game.Main;
import game.Player;
import game.Subworld;

import java.nio.ByteBuffer;

public class PlayerHeldItemSync extends Message {
    static byte id = 11;
    Player player;
    int entityId;
    float x;
    float y;
    float mx;
    float my;
    float vy;
    float angle;
    byte item;

    public PlayerHeldItemSync(Player player) {
        this.player = player;
    }

    public PlayerHeldItemSync(ByteBuffer message) {
        this.entityId = message.getInt();
        this.angle = message.getFloat();
        this.item = message.get();
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(2 + Integer.BYTES + Float.BYTES * 6);
        message.put(id);
        message.putInt(player.getId());
        message.putFloat(player.getLookDirection());
        var inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            if (player.getHeldItem() == inventory.get(i)) {
                message.put((byte) i);
                break;
            }
        }
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
            target.setHeldItem(target.getInventory().get(item));
            target.setLookDirection(angle);
        }
    }
}
