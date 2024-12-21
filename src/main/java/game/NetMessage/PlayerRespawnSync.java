package game.NetMessage;

import game.Client;
import game.Main;
import game.Player;

import java.nio.ByteBuffer;

public class PlayerRespawnSync extends Message {

    static byte id = 14;
    int x;
    int y;
    int entityId;
    short seed;

    public PlayerRespawnSync(Player player) {
        this.entityId = player.getId();
        this.x = (int) player.getX();
        this.y = (int) player.getY();
        this.seed = player.getSeed();
    }

    public PlayerRespawnSync(ByteBuffer message) {
        this.entityId = message.getInt();
        this.x = message.getInt();
        this.y = message.getInt();
        this.seed = message.getShort();

    }

    @Override
    public byte[] toBytes() {
        System.out.println("Will send respawn player");
        ByteBuffer message = ByteBuffer.allocate(1 + Integer.BYTES * 3 + Short.BYTES);
        message.put(id);
        message.putInt(entityId);
        message.putInt(x);
        message.putInt(y);
        message.putShort(seed);
        System.out.println("Sends respawn player "+entityId+" with seed "+seed);
        return message.array();
    }

    @Override
    public void process() {
        var client = (Client) Main.getGame();

        var subworld = Main.getGame().getActiveSubworld();
        Player target = null;
        var players = Main.getGame().getActiveSubworld().getPlayers();
        for (Player p : players)
            if (p.getId() == entityId) {
                target = p;
                break;
            }
        if (target == null) {
            System.out.println("Respawn target "+entityId+" not found");
            return;
        }
        System.out.println("Respawns player "+entityId+" with seed "+seed);
//        playerToSpawn.setLocal(false);

        target.spawn(x,y,seed);
    }
}
