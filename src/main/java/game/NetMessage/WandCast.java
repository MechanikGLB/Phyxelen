package game.NetMessage;

import game.*;
import game.Character;

import java.nio.ByteBuffer;

public class WandCast extends Message {
    static byte id = 15;
    int casterID;
    float casterX;
    float casterY;
    float casterAngle;

    public WandCast(Character caster) {
        this.casterID = caster.getId();
        this.casterX = caster.getX();
        this.casterY = caster.getY();
        this.casterAngle = caster.getLookDirection();
    }

    public WandCast(ByteBuffer message) {
        this.casterID = message.getInt();
        this.casterX = message.getFloat();
        this.casterY = message.getFloat();
        this.casterAngle = message.getFloat();
    }


    public WandCast() {
        this.casterID = 0 ;
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(1 + Integer.BYTES + Float.BYTES * 3);
        message.put(id);
        message.putInt(casterID);
        message.putFloat(casterX);
        message.putFloat(casterY);
        message.putFloat(casterAngle);
        return message.array();
    }

    @Override
    public void process() {
//        if (!Main.isServer())
//            return;
//        System.out.println("WandCast by "+casterID);


        Player target = null;
        var players = Main.getGame().getActiveSubworld().getPlayers();
        for (Player p : players)
            if (p.getId() == casterID) {
                target = p;
                break;
            }
        if (target == null) {
            System.out.println("WandCast target "+casterID+" not found");
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
