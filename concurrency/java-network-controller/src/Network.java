// COMP6610 - Theory and Practice of Concurrency
// School of Computing, University of Kent
// Dominic Orchard & Laura Bocchi 2018-2022

import java.util.Set;

// Network interface controller interface
public interface Network {
  // Attempt to open a connection at a port
  // -- may block if the port is not available at that mode
  public Port open(Integer portNumber, Mode mode);

  // Close a port
  public void close(Port port);

  // What ports are available (unallocated)?
  public Set<Integer> availablePorts();

  // What ports are available for multi receiver use?
  public Set<Integer> availableMultiPorts();
}
