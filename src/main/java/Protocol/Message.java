package Protocol;

public interface Message {

    public void process();
    public byte[] encodeMe();
}
