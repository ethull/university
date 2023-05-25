/*
COMP6610 - Theory and Practice of Concurrency
School of Computing, University of Kent
Ethan Hullett 2022

Concurrency explanation:
- how do i approach concurrency in this problem?
I approach concurrency in this problem by using the shared memory paradigm with semaphores.
When the port is in SEND or RECEIVE MODE I only want one thread to be able to access it at once, hence after an open() call for either of these modes any operations on the ports are within the critical section. I control access to the ports by having threads acquire a semaphore of size 1 for the target port as soon as its accessed. Providing exclusive access to the port (mutual exclusion from other threads) until the semaphore is released when the thread closes the port.
Each semaphores creates a FIFO queue of threads for access to a port, hence providing fairness allowing all threads to equally progress, and avoiding the starvation of threads.

I control access to the multi ports by using a semaphore of size 1024, This ensures fairness and keeps access udner the 1024 limit. Allowing multiple threads to access the port at once. However this allows for race condition because the semaphore size is >1, this problem is currently unsolved (i ran out of time). I believe the solution is to use an extra lock somewhere within the code.

Because i use semaphores of size 1 rather than locks, anyone can break the lock hence avoiding deadlocks.

*/

import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.Arrays;
import java.util.HashSet;

public class MyNetworkInterface implements Network {
  // list of all the ports
  private LinkedList<Port> ports = new LinkedList<Port>();
  // lock semaphores to control access to port (lock if open, allow if closed)
  private LinkedList<Semaphore> lockSemaphores = new LinkedList<Semaphore>();
  // large/multi semaphores to control access to open multi ports (lock if >connections, allow if <connections)
  private LinkedList<Semaphore> multiSemaphores = new LinkedList<Semaphore>();
  //semaphore to lock the critical section in close() for Multi Ports, avoiding data race
  private Semaphore multiCloseSemaphore = new Semaphore(1);

  public MyNetworkInterface() {
    System.out.println("initialise contructor");
    for (int i=0; i<32; i++){
      // create all the initial port objects
      // mode doesnt matter initially since we will close them all
      Port currentPort = initialisePort(i, Mode.SEND);
      // we want all the ports to be initially closed so threads can open them
      currentPort.close();
      ports.add(currentPort);
      // create semaphore objects
      lockSemaphores.add(new Semaphore(1, true));
      // multi semaphores need to have a larger limit
      multiSemaphores.add(new Semaphore(1024, true));
    }
  }

  public Port open(Integer portNumber, Mode mode){
    // there are only 32 ports avaliable
    if (portNumber > 31) return null;

    Port targetPort = ports.get(portNumber);
    // if port is closed, open it in desired mode
    if (!targetPort.isOpen()){
      // acquire the semaphore for the port and create the port obj
      requestAcquirePort(portNumber, mode);
    // if port is open, we need to check the mode
    } else {
      // if the port is open in SEND or RECEIVE, 
      if (targetPort.mode() == Mode.SEND || targetPort.mode() == Mode.RECEIVE){
        // block until port is closed, (request semaphore, which is released when its closed)
        requestAcquirePort(portNumber, mode);
      } else if (mode == Mode.MULTI){
        // since the port is already open, we dont call requestAcquirePort
        // instead we call requestAcquireMultiPort
        requestAcquireMultiPort(portNumber);
      }
    }
    return ports.get(portNumber);
  };

  // Close a port
  public void close(Port port){
    // if its a SEND or RECEIVE mode port just close it
    if (port.mode() == Mode.SEND || port.mode() == Mode.RECEIVE) {
        // close port
        port.close();
        // release the lock on the port (since its now closed)
        lockSemaphores.get(port.portNumber()).release();
    }
    // else if its a MULTI port
    else {
      // if is the last MULTI thread connected
      //System.out.println(multiSemaphores.get(port.portNumber()).availablePermits());
      if (multiSemaphores.get(port.portNumber()).availablePermits() == 1023) {
        // close the port
        port.close();
        // release this threads lock on access to the multi-port
        multiSemaphores.get(port.portNumber()).release();
        // release the lock on the port (since its now closed)
        lockSemaphores.get(port.portNumber()).release();
      // else there are still threads connected
      } else {
        // increase number of threads that can open in multi-mode
        multiSemaphores.get(port.portNumber()).release();
      }
      
    }
  };

  // What ports are available (unallocated)?
  public Set<Integer> availablePorts(){
    Set<Integer> avaliablePorts = new HashSet<Integer>();
    for (int i=0; i<32; i++){
      //if a port is closed add it to the set
      if (!ports.get(i).isOpen()){
        avaliablePorts.add(i);
      }
    }
    return avaliablePorts;
  };

  // What ports are available for multi receiver use?
  public Set<Integer> availableMultiPorts(){
    Set<Integer> avaliableMultiPorts = new HashSet<Integer>();
    for (int i=0; i<32; i++){
      if (ports.get(i).isOpen()){
        if (ports.get(i).mode() == Mode.MULTI) avaliableMultiPorts.add(i);
      }
    }
    return avaliableMultiPorts;
  };

  // initialise a port
  private Port initialisePort(int portNumber, Mode mode){
    LinkedList<String> buffer = new LinkedList<String>();
    // initialise buffer
    buffer.addAll(Arrays.asList("init", "start"));
    return new Port(portNumber, mode, buffer);
  };

  // acquire a port if its open, else block until its open
  // used by Mode.SEND, Mode.RECEIVE, and the initial Mode.MULTI
  private void requestAcquirePort(int portNumber, Mode mode){
    try {
      // join the queue for access to the port when its closed
      lockSemaphores.get(portNumber).acquire();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    // replace the port obj with a new one, in the correct mode with a fresh buffer
    ports.set(portNumber, initialisePort(portNumber, mode));
    // if its a Mode.MULTI port we need to start the queue for access to that port
    if (mode == Mode.MULTI) requestAcquireMultiPort(portNumber);
  }

  // acquire a multi port if their is enougth space, else block until there is
  // used only by Mode.MULTI when a MULTI port has already been opened (by requestAcquirePort)
  private void requestAcquireMultiPort(int portNumber){
    try {
      // join the queue for access to the open multi-port when there is enougth space
      multiSemaphores.get(portNumber).acquire();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void lockMultiCloseSemaphore(){
    try {
      multiCloseSemaphore.acquire();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
