package game.request;

import game.Connection;

public abstract class Request {
    Connection receiver;

    abstract public void process();
}
