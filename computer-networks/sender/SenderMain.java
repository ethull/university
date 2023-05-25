/**
 * This class is the main class for the Sender.
 * It checks the command-line arguments and creates a Sender object.
 *
 * @author eh443
 * @version 2023.03.01
 */
public class SenderMain {
    public static final int MAX_MTU = 65535;
    public static final int MAX_ID = 65535;

    /**
     * Check the command-line arguments and create a Sender.
     * @param args mtu ID message
     */
    public static void main(String[] args) {
        if(args.length != 3) {
            System.err.println("Usage: java SenderMain MTU ID message");
            System.exit(1);
        }
        int mtu = Integer.parseInt(args[0]);
        int id = Integer.parseInt(args[1]);
        String message = args[2];

        // message length must be >=1
        if(message.length() < 1) {
            System.err.println("Message length must be more than equal to 1");
            System.exit(1);
        }

        // message id should be between 0 and 65535
        if(id < 0) {
            System.err.println("ID must be at least 0");
            System.exit(1);
        }
        if(id > MAX_ID) {
            System.err.println("ID must be at most " + MAX_ID);
            System.exit(1);
        }

        // mtu should be between 1 and 65535
        if(mtu < 1) {
            System.err.println("MTU must be at least 1");
            System.exit(1);
        }
        if(mtu > MAX_MTU) {
            System.err.println("MTU must be at most " + MAX_MTU);
            System.exit(1);
        }

        // mtu must be sufficiently large to accommodate at least one message character per packet
        if(mtu <= (7 + String.valueOf(id).length())) {
            System.err.println("MTU cannot accomodate one message character per packet");
            System.err.println("For this message it should be at least: " + (1 + 7 + String.valueOf(id).length()));
            System.exit(1);
        }

        Sender sender = new Sender(mtu);
        // figure out how many packets we need to send
        sender.prep(id, message);
        // send all packets
        sender.send(id, message);
    }
}
