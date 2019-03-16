package Protocol;
import Server.Server;
import Server.DocTPC;
import Server.Text;
import java.util.LinkedList;
import Server.ConnectionHandler;
import Server.BlockingConnectionHandler;

public class DiffMessage implements Message {
    String update;

    public DiffMessage(String toUpdate) {
        update =toUpdate;
    }

    @Override
    /**
     * Locking main text, applying change using diff_match_patch Google Algorithm.
     * The current thread applying the change also distribute all changes to other handlers and send Acknowledge message
     * to it's assigned client.
     */
    public void process(Server server, ConnectionHandler handler) {
        Text myDoc = ((DocTPC) server).getMyDoc();
        synchronized (myDoc) {
            String serverText=myDoc.getText();
            diff_match_patch dmp = new diff_match_patch();
            LinkedList<diff_match_patch.Diff> diff = dmp.diff_main(serverText, update);
            dmp.diff_cleanupSemantic(diff);
            LinkedList<diff_match_patch.Patch> patches=dmp.patch_make(diff);
            dmp.patch_apply(patches,serverText);
            myDoc.setText(serverText);
//            System.out.println(diff);
            ((DocTPC)server).distributeUpdates(this,new AckApply(),((BlockingConnectionHandler)handler).getMyID());
        }
    }

    public byte[] encodeMe() {

        return new byte[0];
    }

}
