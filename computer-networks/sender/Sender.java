/**
 * A Sender breaks up messages into the required number of pieces,
 * based on the maximum transmission unit (MTU), and outputs them
 * to standard output, suitably encoded.
 */
public class Sender {
    private final int mtu;
    // number of packets required to send the message
    private int numOfPackets;
    // size used for text in packets
    private int packetTextSize;

    /**
     * Create a Sender.
     * @param mtu The maximum transmission unit.
     */
    public Sender(int mtu) {
        this.mtu = mtu;
    }

    // figure out how many packets we need to send the data
    public void prep(int id, String message) {
        int packetSize = 0;
        int idLength = String.valueOf(id).length();
        packetSize += idLength; // size of id

        // add fixed size fields (add up to 7)
        packetSize += 1; // comma
        packetSize += 1; // last packet field
        packetSize += 1; // comma
        packetSize += 3; // packet number field
        packetSize += 1; // comma

        // calculate num of packets we need to send

        // the remaining size per packet for data/text (the actual message contents)
        double remainingSize = mtu-packetSize;
        double messageDataSize = message.length();
        // number of packets needed to send the data
        numOfPackets = (int)Math.ceil(messageDataSize / remainingSize);
        packetTextSize = (int)remainingSize;
        //System.out.println("numOfPackets: "+numOfPackets);

        // message must require no more than 999 packets to encode it
        if (numOfPackets > 999) {
            System.err.println("Message must require no more than 999 packets to encode it, your msg requires: "+numOfPackets);
            System.exit(1);
        }
    }

    // get encoding for packet number, with required spacing/padding
    public String getPacketNumText(int packetNumber){
        String text = "";
        String packetNumberStr = String.valueOf(packetNumber);
        if (packetNumberStr.length() == 1) text = packetNumberStr + "  ";
        else if (packetNumberStr.length() == 2) text = packetNumberStr + " ";
        else if (packetNumberStr.length() == 3) text = packetNumberStr;
        return text;
    }

    /**
     * Send a message in one or more pieces according to the mtu.
     * @param id The ID of the message.
     * @param message The message to send.
     */
    public void send(int id, String message) {
        int currentMsgTextChar = 0;
        // send all the packets except the last
        // this wont run if there is only one packet to send
        for (int packetNum=0; packetNum<numOfPackets-1; packetNum++){
            System.out.print(id);
            System.out.print(",");
            System.out.print("F");
            System.out.print(",");
            System.out.print(getPacketNumText(packetNum));
            System.out.print(",");

            for (int i=0; i<packetTextSize; i++){
                System.out.print(message.charAt(currentMsgTextChar));
                currentMsgTextChar += 1;
            }
            System.out.println();
        }
        // send the last packet
        int finalPacketNum = numOfPackets-1;
        System.out.print(id);
        System.out.print(",");
        System.out.print("T");
        System.out.print(",");
        System.out.print(getPacketNumText(finalPacketNum));
        System.out.print(",");
        int remainingMsgLength = message.length()-currentMsgTextChar;
        for (int i=0; i<(remainingMsgLength); i++){
            System.out.print(message.charAt(currentMsgTextChar));
            currentMsgTextChar += 1;
        }
        System.out.println();
    }
}
