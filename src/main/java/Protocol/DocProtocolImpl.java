
package Protocol;
import Server.ConnectionHandler;
import Server.Connections;
import Server.DocTPC;

public class DocProtocolImpl implements DocProtocol<Message> {
    private DocTPC server;

    public DocProtocolImpl(DocTPC server){
        this.server=server;
    }


    @Override
    public void start(int connectionId, Connections<Message> connections) {

    }

    @Override
    public void process(Message message, ConnectionHandler handler) {
        message.process(server,handler);
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
