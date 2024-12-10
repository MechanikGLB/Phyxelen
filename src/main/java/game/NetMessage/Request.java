package game.NetMessage;

import game.*;

import java.nio.ByteBuffer;

public class Request extends Message {
    static byte id = Messages.getNextMessageIndex();
    static {Messages.addMessages(new Request((byte)0,null));}

    byte requestedID; //ID of the requested message
    Connection target; // information with target(optional)

    public Request(byte requestedID) {
        this.requestedID = requestedID;
    }

    public static byte getId() {
        return id;
    }

    public Request(byte requestedID, Connection target) {
        this.requestedID = requestedID;
        this.target = target;
    }

    public byte[] buildMessage() {
            ByteBuffer message = ByteBuffer.allocate(2);
            message.put(id);
            message.put(requestedID);
            return message.array();
        }

    @Override
    public void processMessage(ByteBuffer message) {
        target = Main.getServer().getCurrentConnection();
        Message prototype = Messages.messages.get(message.get());
        GameApp.GameState state = Main.getGame().getGameState();
        if (prototype instanceof RequestableMessage) {
            if(state == GameApp.GameState.Server){
                target.addMessage(((RequestableMessage) prototype).makeMessageByRequest());
            }
            if(state == GameApp.GameState.Client){
                Main.getClient().addMessage(prototype);
            }
        }
    }
}
