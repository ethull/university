// COMP6610 - Theory and Practice of Concurrency
// School of Computing, University of Kent
// Dominic Orchard & Laura Bocchi 2018-2022

import java.util.LinkedList;

public class Port {
  private Integer portNumber;
  private LinkedList<String> buffer;
  private Mode mode;
  private boolean closed;

  // constructor
  public Port(Integer portNumber, Mode mode, LinkedList<String> buffer) {
    this.portNumber = portNumber;
    this.mode = mode;
    this.closed = false;
    this.buffer = buffer;
  }

  // getter
  public Mode mode() {
    return this.mode;
  }

  public Integer portNumber() {
    return this.portNumber;
  }

  // Read from the port
  public String receive() {
    if (!this.closed) {
      return this.buffer.pop();
    } else {
      return null;
    }
  }

  // Write the file if it has the write mode
  // return true if succesful, otherwise false (non writeable file)
  public boolean send(String content) {
    if (this.mode == Mode.SEND && !this.closed) {
      this.buffer.add(content);
      return true;
    } else {
      return false;
    }
  }

  public void close() {
    this.closed = true;
  }

  public boolean isOpen() {
    return !this.closed;
  }
}
