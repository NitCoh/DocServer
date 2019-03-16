package Protocol;

import Server.Server;

public class AckApply implements Message {

    @Override
    public void process(Server server) {

    }

    @Override
    public byte[] encodeMe() {
        return new byte[0];
    }
}
