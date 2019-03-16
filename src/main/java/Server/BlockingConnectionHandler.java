package Server;

import Protocol.DocProtocol;
import Protocol.MessageEncoderDecoder;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private static final int BUFFER_ALLOCATION_SIZE = 1 << 13; //8k
    private static final ConcurrentLinkedQueue<ByteBuffer> BUFFER_POOL = new ConcurrentLinkedQueue<>();

    private final DocProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final SocketChannel sock;
    private LinkedBlockingQueue<Runnable> tasks;
    private DocTPC<T> myServer;
    private volatile boolean connected = true;
    private int myID;
    private static int totalIDs=0;
    Connections<T> connections;


    public BlockingConnectionHandler(SocketChannel sock, MessageEncoderDecoder<T> reader, DocProtocol<T> protocol,DocTPC<T> serv, Connections connections) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.connections=connections;
        myServer=serv;
        tasks=new LinkedBlockingQueue<>();
        myID=totalIDs;
        totalIDs++;
    }

    @Override
    public void run() {
        protocol.start(myID,connections);
            while (!protocol.shouldTerminate() && connected) {
                try {
                    Runnable r = tasks.take();
                    if(r!=null)
                        r.run();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        close();
            }


    @Override
    public void close(){
        connected = false;
        try{
            sock.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public void submit(Runnable task){
        tasks.add(task);
    }

    public Runnable continueRead(){
        ByteBuffer buf = leaseBuffer();
        boolean success = false;
        try {
            success = sock.read(buf) != -1;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (success) {
            buf.flip();
            return () -> {
                try {
                    while (buf.hasRemaining()) {
                        T nextMessage = encdec.decodeNextByte(buf.get());
                        if (nextMessage != null) {
                            protocol.process(nextMessage,this);
                        }
                    }
                } finally {
                    releaseBuffer(buf);
                }
            };
        } else {
            releaseBuffer(buf);
            close();
            return null;
        }
    }

    @Override
    public void send(T msg) {
        byte[] encodedMsg = encdec.encode(msg);
        ByteBuffer toSend=ByteBuffer.wrap(encodedMsg);
        while(toSend.hasRemaining()) {
            try{
            sock.write(toSend);}
            catch(IOException ex){
                ex.printStackTrace();
                close();
            }
        }
    }

    private static ByteBuffer leaseBuffer() {
        ByteBuffer buff = BUFFER_POOL.poll();
        if (buff == null) {
            return ByteBuffer.allocateDirect(BUFFER_ALLOCATION_SIZE);
        }

        buff.clear();
        return buff;
    }

    private static void releaseBuffer(ByteBuffer buff) {
        BUFFER_POOL.add(buff);
    }
    public int getMyID(){return myID;}
}
