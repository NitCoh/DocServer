package Protocol;

import Server.Connections;

public class DocProtocolImpl<Message> implements DocProtocol<Message> {
    @Override
    public void start(int connectionId, Connections<Message> connections) {

    }

    @Override
    public void process(Message message) {
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
