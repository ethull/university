/**
 * Main class for Receiver.
 * @author eh443
 * @version 2021.03.01
 */
public class ReceiverMain {
    public static final int MAX_ID = 65535;

    /**
     * Check the command-line arguments and create a Receiver.
     * @param args ID
     */
    public static void main(String[] args) {
        if(args.length != 1) {
            System.err.println("Usage: java ReceiverMain ID");
            System.exit(1);
        }
        int id = Integer.parseInt(args[0]);
        if(id < 0) {
            System.err.println("error");
            System.exit(1);
        }
        if(id > MAX_ID) {
            System.err.println("error");
            System.exit(1);
        }
        Receiver receiver = new Receiver(id);
        receiver.receive();
    }
}
