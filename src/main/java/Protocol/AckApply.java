package Protocol;
import Server.Server;
import Server.DocTPC;
import Server.Text;
import java.util.LinkedList;
import Server.ConnectionHandler;
import Server.BlockingConnectionHandler;

public class AckApply implements Message {


    @Override
    public void process(Server server,ConnectionHandler handler) {

    }

    @Override
    public byte[] encodeMe() {
        return new byte[0];
    }
}
