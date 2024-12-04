package game.NetMessage;

public class Messages {
    static byte messageCounter = 0;
    static byte getNextMessageIndex() {
        return messageCounter++;
    }
}
