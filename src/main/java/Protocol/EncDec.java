package Protocol;

import com.google.gson.Gson;

public class EncDec implements MessageEncoderDecoder<Message> {
    private byte[] sizeofString;
    private int numofBytestoIntegerCounter;
    private int toRead;
    private byte[] incomingString;
    private int index;
    Gson myMessage;

    public EncDec() {
        this.sizeofString = new byte[4];
        numofBytestoIntegerCounter=0;
    }

    public Message decodeNextByte(byte nextByte) {
        if (numofBytestoIntegerCounter < 4) {
            sizeofString[numofBytestoIntegerCounter] = nextByte;
            if (numofBytestoIntegerCounter == 3) {
                toRead = Integer.parseInt(new String(sizeofString));
                incomingString = new byte[toRead];
                index = 0;
            }
            numofBytestoIntegerCounter++;
        }
        else{
            incomingString[index]=nextByte;
            index++;
            toRead--;
        }
        if(toRead==0) {
            String jsonString=new String(incomingString);
            myMessage = new Gson();
            DiffMessage msg=myMessage.fromJson(jsonString,DiffMessage.class);
            return msg;
        }
        else
            return null;
    }

    public byte[] encode(Message message) {
        return message.encodeMe();
    }
}
