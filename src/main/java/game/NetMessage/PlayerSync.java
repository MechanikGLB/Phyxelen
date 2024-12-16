package game.NetMessage;

import game.Main;
import game.Player;
import game.Subworld;

import java.nio.ByteBuffer;

public class PlayerSync extends Message {
    static byte id = 10;
    Player player;

    public PlayerSync(Player player) {
        this.player = player;
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(2 + Integer.BYTES + Float.BYTES * 3);
        message.put(id);
        message.putInt(player.getId());
        message.putFloat(player.getX());
        message.putFloat(player.getY());
        message.putFloat(player.getLookDirection());
        var inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            if (player.getHoldedItem() == inventory.get(i)) {
                message.put((byte) i);
                break;
            }
        }
        return message.array();
    }

    @Override
    public void processReceivedBinMessage(ByteBuffer message) {
        int id = message.getInt();
        float x = message.getFloat();
        float y = message.getFloat();
        float angle = message.getFloat();
        byte item = message.get();

        Player target = null;
        Subworld subworld = Main.getGame().getActiveSubworld();
        if (subworld == null)
            return;
        var players = subworld.getPlayers();
        for (Player p : players)
            if (p.getId() == id) {
                target = p;
                break;
            }
        if (target == null) {
//            System.out.println();
            return;
        }
        target.setX(x);
        target.setY(y);
        target.setLookDirection(angle);
    }
}
