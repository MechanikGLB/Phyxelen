package game.NetMessage;

import game.*;

import java.nio.ByteBuffer;

public class ProjectileSpawn extends Message {
    static byte id = 8;
    int casterID;

    public ProjectileSpawn(int casterID) {
        this.casterID = casterID;
    }

    public ProjectileSpawn(ByteBuffer message) {
        this.casterID = message.getInt();
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
    public void process() {
//        if (!Main.isServer())
//            return;
        System.out.println("ProjectileSpawn by "+casterID);

        Player target = null;
        var players = Main.getGame().getActiveSubworld().getPlayers();
        for (Player p : players)
            if (p.getId() == casterID) {
                target = p;
                break;
            }
        if (target == null) {
            System.out.println("ProjectileSpawn target "+casterID+" not found");
            return;
        }

        if (target != ((Client) Main.getGame()).getPrimaryCharacter())
            ((Wand)target.getHeldItem()).cast();

//        if (Main.isServer())
//            Main.getServer().broadcastMessage(new ProjectileSpawn(casterID));
    }
}
