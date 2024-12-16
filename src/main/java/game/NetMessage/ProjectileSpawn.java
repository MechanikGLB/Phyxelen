package game.NetMessage;

import game.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ProjectileSpawn extends Message {
    static byte id = 8;
    int casterID;

    public ProjectileSpawn(int casterID) {
        this.casterID = casterID;
    }

    public ProjectileSpawn() {
        this.casterID = 0 ;
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(1 + Integer.BYTES );
        message.put(id);
        message.putInt(casterID);
        return message.array();
    }

    @Override
    public void processReceivedBinMessage(ByteBuffer message) {
        //TODO:continue after sync
        int caster = message.getInt();

        Player target = null;
        var players = Main.getGame().getActiveSubworld().getPlayers();
        for (Player p : players)
            if (p.getId() == id) {
                target = p;
                break;
            }
        if (target == null)
            return;

        ((Wand)target.getHoldedItem()).cast();

        if (Main.getGame().getGameState() == GameApp.GameState.Server)
            Main.getServer().broadcastMessage(new ProjectileSpawn(caster));
    }
}
