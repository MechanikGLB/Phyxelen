package game.NetMessage;

import game.*;

import java.nio.ByteBuffer;

public class ProjectileSpawn extends Message {
    static byte id = 8;
    int casterID;
    int projectileID;
    float casterX;
    float casterY;
    float casterAngle;

    public ProjectileSpawn(int casterID, int projectileID, float casterX, float casterY, float casterAngle) {
        this.casterID = casterID;
        this.projectileID = projectileID;
        this.casterX = casterX;
        this.casterY = casterY;
        this.casterAngle = casterAngle;
    }

    public ProjectileSpawn(ByteBuffer message) {
        this.casterID = message.getInt();
        this.projectileID = message.getInt();
        this.casterX = message.getFloat();
        this.casterY = message.getFloat();
        this.casterAngle = message.getFloat();
    }


    public ProjectileSpawn() {
        this.casterID = 0 ;
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(1 + Integer.BYTES * 2 + Float.BYTES * 3);
        message.put(id);
        message.putInt(casterID);
        message.putInt(projectileID);
        message.putFloat(casterX);
        message.putFloat(casterY);
        message.putFloat(casterAngle);
        return message.array();
    }

    @Override
    public void process() {
//        if (!Main.isServer())
//            return;
        System.out.println("ProjectileSpawn by "+casterID);

        for (Entity e : Main.getGame().getActiveSubworld().getEntities())
            if (e.getId() == projectileID)
                return;

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

        if (target != ((Client) Main.getGame()).getPrimaryCharacter()) {
            target.setX(casterX);
            target.setY(casterY);
            target.setLookDirection(casterAngle);
            ((Wand)target.getHeldItem()).cast();
        }

//        if (Main.isServer())
//            Main.getServer().broadcastMessage(new ProjectileSpawn(casterID));
    }
}
