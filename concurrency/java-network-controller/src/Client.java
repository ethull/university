// COMP6610 - Theory and Practice of Concurrency
// School of Computing, University of Kent
// Ethan Hullett 2022

import java.util.LinkedList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Client {
    private static MyNetworkInterface fs = new MyNetworkInterface();
    public static void main(String[] args){
		Thread t1 = new Thread(new Worker(fs));
		Thread t2 = new Thread(new Worker(fs));
		Thread t3 = new Thread(new Worker(fs));
		Thread t4 = new Thread(new Worker(fs));
		t1.start();
		t2.start();
		t3.start();
		t4.start();

    }
}

class Worker implements Runnable {
    private int randomPort;
    private Mode randomMode;
    private final LinkedList<Mode> modes;
    private MyNetworkInterface fs;
    public Worker(MyNetworkInterface fs){
        this.fs = fs;
        randomPort = ThreadLocalRandom.current().nextInt(0, 32);
        modes = new LinkedList<Mode>();
        modes.addAll(Arrays.asList(Mode.SEND, Mode.RECEIVE, Mode.MULTI));
        randomMode = modes.get(ThreadLocalRandom.current().nextInt(0, 3));
        //System.out.println(randomPort+" "+randomMode);
    }

	public void run() {
        System.out.println(String.format("Opening port: %d, in mode: %s.", randomPort, randomMode.toString()));
        Port ps = fs.open(randomPort, randomMode);
        if (randomMode == Mode.SEND){
            String msg = "chicken";
		    System.out.printf("Sending message: %s, to port: %d.\n", msg, randomPort);
            ps.send(msg);
        } else if (randomMode == Mode.RECEIVE){
            String msg = ps.receive();
		    System.out.printf("Received msg: %s, from port: %d.\n", msg, randomPort);
        } else if (randomMode == Mode.MULTI){
            String msg = "multiport";
		    System.out.printf("Sending message: %s, to port: %d.\n", msg, randomPort);
            ps.send(msg);
            String msg2 = ps.receive();
		    System.out.printf("Received msg: %s, from port: %d.\n", msg2, randomPort);
        }
        System.out.printf("Closing port: %d.\n", randomPort);
        fs.close(ps);
	}
}
