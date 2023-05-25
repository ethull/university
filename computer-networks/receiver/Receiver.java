import java.util.Scanner;
import java.util.TreeMap;

/**
 * Receiver for a message from a Sender.
 * @author eh443
 * @version 2021.03.01
 */
public class Receiver {
    // The ID of the message to be received.
    private final int id;
    //private ArrayList<String[]> packets;
    // packets by sequence number
    private TreeMap<Integer, String[]> packets = new TreeMap<>();
    // do we have the final packet, used to detect multiple Ts
    private boolean haveFinalPacket = false;

    /**
     * Create a Receiver for a message with the given ID.
     * @param id The ID of the message to be received.
     */
    public Receiver(int id) {
        this.id = id;
    }
    
    private void printPackets(){
        System.out.println("current packets: ");
        for (Integer name: packets.keySet()) {
            //String key = name.toString();
            String[] packetDetails = packets.get(name);
            System.out.print(name + " ");
            for (int i = 0; i < packetDetails.length; i++) {
                System.out.print(packetDetails[i]);
                if (i != packetDetails.length-1) System.out.print(",");
            }
            System.out.print("   ");
        }
        System.out.println();
    }

    /**
     * Receive a message from standard input.
     * Print the message to standard output.
     */
    public void receive() {
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            //System.out.println(line);
            processPacket(line);
        }
        scanner.close();
        // we have no packets so there is an err
        if (packets.size() == 0) {
            System.err.println("error");
            return;
        }
        //printPackets();
        buildMessage();
    }
    
    // check if the packet follows the senders protocol, and if its the packet we want
    public void processPacket(String packet) {
        // cant use packet.split doesnt take into account:
        //  situation where we have commas in the message
        // but when doing manually need to take into account multiple commas before message

        String[] packetSplit = {"","","",""};
        int currentField = 0;
        for (int i = 0; i < packet.length(); i++) {
            if (packet.charAt(i) == ','){
                if (currentField < 3) currentField++;
                // if we have too many commas then the commas are part of the message
                else packetSplit[currentField] += ',';
            } else packetSplit[currentField] += packet.charAt(i);
        }  
        // we had less than three commas
        if (currentField < 3) return;
        //System.out.println(packetSplit[0] + " " + packetSplit[1] + " " + packetSplit[2] + " " + packetSplit[3]);


        // does the ID part of the packet match the ID of correct msg
        try {
            if (Integer.parseInt(packetSplit[0]) != id){
                // discard packet as its malformed or not the ID we are looking for
                return;
            }
        }
        // could add more specific errors (but we dont care about code quality, and want to catch everything)
        catch (Exception err) {
            // we have got something weird (not an integer)
            //System.out.println(err);
            return;
        }

        try {
            // is the last packet a T or a F
            // this will also catch if field is too long/short
            if (!packetSplit[1].equals("T") && !packetSplit[1].equals("F")){
                return;
            }
            // if T packet (and hence last packet), do we already have a T packet
            //  then we are not following the protocol, there can only be one last packet
            if (packetSplit[1] == "T" && haveFinalPacket){
                return;
            // else its our first T, then remember that we do
            } else if (packetSplit[1] == "T") haveFinalPacket = true;
        }
        catch (Exception err) {
            // we have got something weird (not a string)
            //System.out.println(err);
            return;
        }
        
        try {
            // do we have 3 bits for the packet number
            if (packetSplit[2].length() != 3){
                return;
            }
            String trimmedPacketNum = packetSplit[2].trim();
            // is it an integer
            Integer.parseInt(trimmedPacketNum);
            // is it padded with zeros
            if (!trimmedPacketNum.equals("0") && trimmedPacketNum.length() > 1) {
                if (trimmedPacketNum.charAt(0) == '0') return;
            }
        }
        catch (Exception err) {
            // we have got something weird (not a string)
            //System.out.println(err);
            return;
        }
        
        // is the msg >1 char long
        try {
            if (packetSplit[3].length() < 1){
                return;
            }
        }
        catch (Exception err) {
            // we have got something weird (not a string)
            //System.out.println(err);
            return;
        }
        
        // packet has passed all checks, so add to arr for later processing
        //  addressed by sequence num for quicker addressing
        Integer sequenceNum = Integer.parseInt(packetSplit[2].trim());
       
        // is this a duplicate packet (we already have this sequence number)
        if (packets.containsKey(sequenceNum)) return;
        
        packets.put(sequenceNum, packetSplit);
    }
    
    // build message from all decoded and err checked packets
    public void buildMessage(){
        String message = "";
        
        for (int seq = 0; seq < packets.keySet().size(); seq++) {
            // we dont have a seq num for the current packet then there are missing packets
            //  (since we havent had a T yet and all packets are in order by seq num)
            if (!packets.containsKey(seq)) {
                System.err.println("error");
                break;
            }

            // add packets message contents
            message += packets.get(seq)[3];

            // if we are at the last packet break the loop
            if (packets.get(seq)[1].equals("T")){
                //System.out.println("last packet: ");
                System.out.println(message);
                break;
            }
            
            // if we are at the last packet and it wasnt a T
            if (seq == packets.keySet().size()-1) {
                System.err.println("error");
            }
        }



        // if we dont have every packet then err
    }
}
