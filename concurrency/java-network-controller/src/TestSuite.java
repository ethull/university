// COMP6610 - Theory and Practice of Concurrency
// School of Computing, University of Kent
// Dominic Orchard & Laura Bocchi 2018-2022

import java.lang.Thread.State;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.HashMap;

public class TestSuite {

  // **** Tests top-level ******************************************************

  public void tests() {
    describe("Test single threaded open/read/write/close");
    testSingleThread();

    describe("Test multithreading locking");
    testMultiThreadMulti();
    testMultiThreadBlock();
	describe("Test multithreading locking (additional)");
    testMultiThread2();
	testMultiThread3();
	}

  public void testSingleThread() {
    Network fs = newNetwork();

    // Test 1: All ports available
    try {
      // Base
      it("All ports available");
      assertEquals(fs.availablePorts().size(), 32);
    } catch (Exception e) {
      failure("Exception");
    } catch (Error e) {
      failure("Exception");
    }

    // Test 1a: Can open a port
    Port pA = null;
    try {
      it("Opening a port number greater than 33 should give a null port");
      pA = fs.open(33, Mode.RECEIVE);
      assertEquals(pA == null, true);
    } catch (Exception e) {
      failure("Exception: " + e.getLocalizedMessage());
    } catch (Error e) {
      failure("Exception");
    }
    
    // Test 2: Can open a port
    Port p = null;
    try {
      it("Receive mode test -- successful open");
      p = fs.open(0, Mode.RECEIVE);
      assertEquals(p != null, true);
    } catch (Exception e) {
      failure("Exception: " + e.getLocalizedMessage());
    } catch (Error e) {
      failure("Exception");
    }

    // Test 3: Fewer available ports
    try {
      // Base
      it("Port opened, reducing number of avilable ports");
      assertEquals(fs.availablePorts().size(), 31);
      assertEquals(fs.availablePorts().contains(0), false);
    } catch (Exception e) {
      failure("Exception");
    } catch (Error e) {
      failure("Exception");
    }

    // Test 5: Port is the correct one
    try {
      it("Receive mode test -- correct port");
      assertEquals(p.portNumber(), 0);
    } catch (Exception e) {
      failure("Exception");
    } catch (Error e) {
      failure("Exception");
    }

    // Test 6: Port is the correct mode
    try {
      it("Receive mode test -- correct mode");
      assertEquals(p.mode(), Mode.RECEIVE);
    } catch (Exception e) {
      failure("Exception");
    } catch (Error e) {
      failure("Exception");
    }

    // Test 7: Can receive from this port
    try {
      it("Receive test -- successfully read initialisation packet");
      assertEquals(p.receive(), "init");
    } catch (Exception e) {
      failure("Exception");
    } catch (Error e) {
      failure("Exception");
    }

    // Test 8: Close
    try {
      it("Receive mode test -- Closing (no longer open)");
      fs.close(p);
      assertEquals(p.isOpen(), false);
      it("Receive mode test -- Closing (32 ports availalbe) ");
      assertEquals(fs.availablePorts().size(), 32);
      it("Receive mode test -- Closing (0 is available)");
      assertEquals(fs.availablePorts().contains(0), true);
    } catch (Exception e) {
      failure("Exception");
    } catch (Error e) {
      failure("Exception");
    }

    // Test 9: Close was effective (no read after close)
    try {
      it("Receive mode test -- Receive after close");
      fs.close(p);
      assertEquals(p.receive() == null, true);
    } catch (Exception e) {
      failure("Exception");
      e.printStackTrace();
    } catch (Error e) {
      failure("Error");
      e.printStackTrace();
    }

    // Test 10: Sender test
    Port ps = null;
    try {
      it("Sender mode test -- successful open");
      ps = fs.open(1, Mode.SEND);
      assertEquals(ps == null, false);
    } catch (Exception e) {
      failure("Exception");
    } catch (Error e) {
      failure("Exception");
    }

    // Test 11: sending works
    try {
      it("Sender mode test -- sender works");
      assertEquals(ps.send("Hello"), true);
      ps.receive(); ps.receive();
      assertEquals(ps.receive(), "Hello");
    } catch (Exception e) {
      failure("Exception");
    } catch (Error e) {
      failure("Exception");
    }

    // Test 12: reclose
    try {
      it(
      "Reclosing a Port does not change the number of available ports status"
      );
      fs.close(ps);
      assertEquals(fs.availablePorts().size(), 32);
      assertEquals(fs.availablePorts().contains(1), true);
      fs.close(ps);
      assertEquals(fs.availablePorts().size(), 32);
      assertEquals(fs.availablePorts().contains(1), true);
    } catch (Exception e) {
      failure("");
      e.printStackTrace();
    } catch (Error e) {
      failure("Exception");
    }

    // Test 13: Two ports open
    Port p2 = null;
    try {
      p2 = fs.open(0, Mode.RECEIVE);
      p = fs.open(2, Mode.RECEIVE);
      assertEquals(fs.availablePorts().size(), 30);
    } catch (Exception e) {
      failure("Exception");
    } catch (Error e) {
      failure("Exception");
    }

    // Test 14: Not interfering
    try {
      it("Closed status");
      assertEquals(p.receive(), "init");
      assertEquals(p2.receive(), "init");
    } catch (Exception e) {
      failure("Exception");
    } catch (Error e) {
      failure("Exception");
    }

  }

  public void testMultiThreadMulti() {
    System.out.println("testMultiThreadMulti--------------");
    Network fs = newNetwork();
    Semaphore signal = new Semaphore(0);

    Thread t1 = new Thread(
      new Runnable() {

        @Override
        public void run() {
          it("Multi threads can receive from a multi port (thread 1)");
          try {
            Port p1 = fs.open(1, Mode.MULTI);
            signal.release();
            System.out.println("avaliable multi ports t1:"+fs.availableMultiPorts());
            //System.out.println("p1 receive: "+p1.receive());
            assertEquals(p1.receive(), "init");
          } catch (Exception e) {
            failure("");
            e.printStackTrace();
          } catch (Error e) {
            failure("Exception");
          }
        }
      }
    );
    Thread t2 = new Thread(
      new Runnable() {

        @Override
        public void run() {
          it("Multi threads can receive from a multi port (thread 2)");
          try {
            Port p2 = fs.open(1, Mode.MULTI);
            signal.acquire();
            System.out.println("avaliable multi ports t2:"+fs.availableMultiPorts());
            //System.out.println("p2 receive: "+p2.receive());
            assertEquals(p2.receive(), "start");
            fs.close(p2);
          } catch (Exception e) {
            failure("");
            e.printStackTrace();
          } catch (Error e) {
            failure("Exception");
          }
        }
      }
    );
    Thread t3 = new Thread(
      new Runnable() {

        @Override
        public void run() {
          it("Multi threads can read (different Port state) (thread 3)");
          try {
            Port p3 = fs.open(2, Mode.MULTI);
            System.out.println("avaliable multi ports t3:"+fs.availableMultiPorts());
            assertEquals(p3.receive(), "init");
          } catch (Exception e) {
            failure("");
            e.printStackTrace();
          } catch (Error e) {
            failure("Exception");
          }
        }
      }
    );
    t1.start();
    t2.start();
    t3.start();
    try {
      t1.join(300);
      t2.join(300);
      t3.join(300);
    } catch (Exception e) {
      failure("Exception");
      e.printStackTrace();
    } catch (Error e) {
      failure("Exception");
    }
    System.out.println("avaliable ports endtest: "+fs.availablePorts());
    System.out.println("avaliable multi ports endtest: "+fs.availableMultiPorts());

    //fail
    it("Multiple receive is allowed with no blocking (thread 1)");
    assertEquals(t1.getState(), Thread.State.TERMINATED);

    //fail
    it("Multiple receive is allowed with no blocking (thread 2)");
    assertEquals(t2.getState(), Thread.State.TERMINATED);

    it(
      "Multiple receive (to another Port) is allowed with no blocking (thread 3)"
    );
    assertEquals(t3.getState(), Thread.State.TERMINATED);

  }

  public void testMultiThreadBlock() {
    System.out.println("testMultiThreadBlock--------------");
    Network fs = newNetwork();

    // -----------------------------------------------------------------------------
    // Scenario 1:
    //   Thread main: opens 10 for receiving
    //   Thread wt1: open 10 for sending
    //             - GETS BLOCKED
    //   Thread main: closes 10
    //   Thread wt1: -- GETS UNBLOCKED
    //                closes 10

    Thread main = new Thread(
      new Runnable() {

        @Override
        public void run() {
          Port fA = fs.open(10, Mode.RECEIVE);

          Signal signal = new Signal();
          signal.flag = false;

          // Start off another thread which gets blocked
          Thread wt1 = new Thread(
            new Runnable() {

              @Override
              public void run() {
                try {
                  Port faw1 = fs.open(10, Mode.SEND);
                  it(
                    "Process trying to open in send mode has been eventually unblocked (unblocked signal should be true)"
                  );
                  assertEquals(signal.flag, true);
                  fs.close(faw1);
                } catch (Exception e) {
                  // In case an exception happens
                  it(
                    "Process trying to open in send mode has been eventually unblocked (unblocked signal should be true)"
                  );
                  failure("");
                  e.printStackTrace();
                } catch (Error e) {
                  failure("Exception");
                }
              }
            }
          );
          wt1.start();
          try {
            wt1.join(500);
          } catch (InterruptedException e) {
            failure("Interrupt");
          }
          // Detect that the thread is blocked
          it(
            "Port open for receiving; another process trying to open it for sending is blocked"
          );
          assertEquals(wt1.getState(), Thread.State.WAITING);

          // Set observable checkpoint in other thread
          signal.flag = true;
          // Triggers unblock of wt1
          fs.close(fA);

          try {
            wt1.join(500);
          } catch (InterruptedException e) {
            failure("Interrupt");
          }

          it("Successfuly unblocked writing process");
          assertEquals(wt1.getState(), Thread.State.TERMINATED);
        }
      }
    );
    main.start();

    try {
      main.join(1000);
    } catch (InterruptedException e) {
      failure("Interrupt");
    }

    it("Successfuly closed Ports after blocking interaction");
    assertEquals(main.getState(), Thread.State.TERMINATED);
  }

  public void testMultiThread2() {
    System.out.println("testMultiThread2--------------");
    Network fs = newNetwork();

    /* Situation
		   Main: Open b for MULTI
		   sub1:  Open a for SEND
		        -- check success
		         (WAIT on SEMAPHORE)
		   sub2: Open a for SEND
		     -- check it gets block
		 * kill this thread
		   Main: Open b for MULTI (not blocked)
		         Read b . Close b
		   sub3: Open a for SEND
		           -- check it gets blocked
		   Main: signal sub1 to procedd
		   sub1:  send "claudio" to a
		          close a
		   sub3 should get unblocked
		          -- check unblocked
		          -- get the Port and read it 'claudio'
		          -- close a
		   -- check sub3 is not blocked and termintes
		 */
    // Open b
    Port fb = fs.open(11, Mode.MULTI);

    Semaphore signaller = new Semaphore(1);
    try {
      signaller.acquire();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    Signal doneOpen = new Signal();
    Signal signal = new Signal();

    Thread sub1 = new Thread(
      new Runnable() {

        @Override
        public void run() {
          try {
            Port fa1 = fs.open(10, Mode.SEND);
            doneOpen.flag = true;
            it(
              "Process with send mode of Port succeeds in receiving while blocking others"
            );
            assertEquals(fa1.receive(), "init");
            assertEquals(fa1.receive(), "start");

            // Wait
            signaller.acquire();
            fa1.send("claudio");
            // Set observable checkpoint in other thread
            signal.flag = true;
            // Triggers unblock
            fs.close(fa1);
          } catch (Exception e) {
            failure("");
            e.printStackTrace();
          } catch (Error e) {
            failure("Exception");
          }
        }
      }
    );
    sub1.start();

    // Wait and check that it succesfully opened
    try {
      sub1.join(500);
    } catch (InterruptedException e) {
      failure("Interrupt");
    }
    it(
      "Another process trying to receive whilst another send gets blocked."
    );
    assertEquals(doneOpen.flag, true);

    // Start off another thread which gets blocked
    Thread sub2 = new Thread(
      new Runnable() {

        @Override
        public void run() {
          Port faa = fs.open(10, Mode.RECEIVE);
          // Eventually close
          fs.close(faa);
        }
      }
    );
    sub2.start();
    try {
      sub2.join(500);
    } catch (InterruptedException e) {
      failure("Interrupt");
    }

    // Detect that the thread is blocked
    assertEquals(sub2.getState(), Thread.State.WAITING);
    // Give up on the thread
    it(
      "Another process trying to open a Port on MULTI (while another writes a different Port) is not blocked"
    );
    Port fb2 = fs.open(11, Mode.MULTI);
    
    try {
      assertEquals(fb2.receive(), "init");
      fs.close(fb2);
    } catch (Exception e) {
      failure("Exception");
    } catch (Error e) {
      failure("Exception");
    }

    signal.flag = false;

    Thread sub3 = new Thread(
      new Runnable() {

        @Override
        public void run() {
          try {
            Port fa2 = fs.open(10, Mode.RECEIVE);
            // I'm unblocked!
            it("Second open attempt gets unblocked");
            assertEquals(signal.flag, true);

            try {
              // see new init message
              assertEquals(fa2.receive(), "init");
              fs.close(fa2);
            } catch (Exception e) {
              failure("");
              e.printStackTrace();
            } catch (Error e) {
              failure("Exception");
            }
          } catch (Exception e) {
            failure("");
            e.printStackTrace();
          } catch (Error e) {
            failure("Exception");
          }
        }
      }
    );
    sub3.start();
    // Let a bit of time elapse to allow the thread to get blocked
    try {
      sub3.join(500);
    } catch (InterruptedException e) {
      failure("Interrupt");
    }
    // Detect block
    it("Another process trying to send whilst another send gets blocked");
    assertEquals(sub3.getState(), Thread.State.WAITING);

    // Make the sub1 close the Port, unclocking sub3
    signaller.release();

    // Let a bit of time elapse to allow the thread to get unblocked
    try {
      sub3.join(500);
    } catch (InterruptedException e) {
      failure("Interrupt");
    }
    // Detect block
    it("Second send attempt was indeed unblocked");
    assertEquals(sub3.getState(), Thread.State.TERMINATED);

    try {
      sub2.join(500);
    } catch (InterruptedException e) {
      failure("Interrupt");
    }

    try {
      // Detect that the thread 2 was unblocked eventually
      it("Blocked receiver thread was eventually unblocked");
      assertEquals(sub2.getState(), Thread.State.TERMINATED);
    } catch (Exception e) {
      failure("Exception");
    } catch (Error e) {
      failure("Exception");
    }

    try {
      it("Second sender attempt gets unblocked, and now the Port is closed");
      assertEquals(fs.availablePorts().contains(10), true);
    } catch (Exception e) {
      failure("Exception");
    } catch (Error e) {
      failure("Exception");
    }

    try {
      it(
        "Two processed open 'b' for receive multi, but only one closed it, so it should still be marked as available"
      );
      assertEquals(fs.availableMultiPorts().contains(11), true);
    } catch (Exception e) {
      failure("Exception");
    } catch (Error e) {
      failure("Exception");
    }

	}
	
	public void testMultiThread3() {
        System.out.println("testMultiThread3--------------");
		Network fs = newNetwork();

		// Oppen a Port for reading and close
		Port fR = fs.open(10, Mode.MULTI);
		fs.close(fR);

        //System.out.println(fs.availablePorts());
        //System.out.println(fs.availableMultiPorts());

		// Open "a" for writing
		Port fW1 = fs.open(10, Mode.SEND);


		// Re-close "a" for reading (bug here)
		fs.close(fR);

		// Now do an illegal open "a" for writing
		Thread w = new Thread(
			new Runnable() {

				@Override
				public void run() {
					Port fW2 = fs.open(10, Mode.MULTI);
				}
			}
		);
		w.start();
		try {
			w.join(500);
		} catch (Exception e) {}
    catch (Error e) {
      failure("Exception");
    }
		it("Thread should be blocked trying to re-open for writing");
		assertEquals(w.getState(), Thread.State.WAITING);
	}

  

  // ************ TEST HARNESS *************************************************

  public String className;

  public static final String ANSI_RED = "\u001B[31m\033[1m";
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_GREEN = "\u001B[32m\033[1m";
  public static final String ANSI_BLUE = "\u001B[34m\033[1m";

  private Integer testCount = 0;
  private Integer passedTests = 0;
  private boolean prevTestFailed = false;

  private HashMap<Thread,String> currentTestName;

  public static void main(String[] args) {
    System.out.println("COMP6610 - Assessment 1 - Test Suite v1.1");
    // First string provides that name of your Network class
    if (args.length < 1) {
      System.out.println(
        "Please pass the name of your Network class as an argument"
      );
    } else {
      // Ok
      String className = args[0];
      System.out.println(className);
      TestSuite ts = new TestSuite(className);
      ts.go();
    }
  }

  public TestSuite(String className) {
    this.className = className;
    this.currentTestName = new HashMap<Thread, String>();
  }

  public Network newNetwork() {
    // Create a new JavaClassLoader
    ClassLoader classLoader = this.getClass().getClassLoader();
    // Load the target class using its binary name
    Network fs = null;

    try {
      Class loadedMyClass = classLoader.loadClass(className);
      //System.out.println("Loaded class name: " + loadedMyClass.getName());

      // Create a new instance from the loaded class
      Constructor constructor = loadedMyClass.getConstructor();
      Object myClassObject = constructor.newInstance();
      fs = (Network)myClassObject;
    } catch (ClassNotFoundException e) {
      System.out.println("Error: Could not find class " + className);
      System.exit(1);
    } catch (NoSuchMethodException e) {
      System.out.println(
        "Error: " + className + " is missing its constructor."
      );
      System.exit(1);
    } catch (Exception e) {
      System.out.println(e);
      System.out.println("Error: " + className + " could not be instantiated.");
      System.exit(1);
    }

    return fs;
  }

  public void go() {
    tests();
    System.out.println("\n" + ANSI_BLUE + "Tests: " + testCount + ANSI_RESET);
    System.out.println(ANSI_GREEN + "Passed: " + passedTests + ANSI_RESET);
    if (passedTests == testCount) {
      System.out.println("\nOk.");
    } else {
      System.out.println(
        ANSI_RED + "Failed: " + (testCount - passedTests) + ANSI_RESET
      );
    }

    describe("Done");
  }

  public void describe(String msg) {
    System.out.println("\n" + msg);
  }

  public void it(String msg) {
    this.currentTestName.put(Thread.currentThread(), msg);
  }

  // Messages
  public synchronized void success() {
    this.passedTests++;
    this.prevTestFailed = false;
    System.out.print(".");
  }

  public synchronized void failure(String msg) {
    if (!this.prevTestFailed) {
      System.out.print("\n");
    }
    System.out.println(
      ANSI_RED + this.currentTestName.get(Thread.currentThread()) + ".\n\tFailed: " + msg + "\n" + ANSI_RESET
    );
    this.prevTestFailed = true;
  }

  // Assertion boilerplate
  public synchronized void assertEquals(String s1, String s2) {
    if ((s1 == null || s2 == null) && !(s1 == null && s2 == null)) {
      failure("Expected " + s2 + " got " + s1);
    } else {
      if (s1.equals(s2)) {
        success();
      } else {
        failure("Expected " + s2 + " got " + s1);
      }
    }
    this.testCount++;
  }

  // Assertion boilerplate
  public synchronized void assertEquals(String[] s1, String[] s2) {
    boolean eq = (s1.length == s2.length);
    for (int i = 0; i < s1.length; i++) {
      eq = eq & (s1[i].equals(s2[i]));
    }
    if (eq) {
      success();
    } else {
      failure("Expected " + s2 + " got " + s1);
    }
    this.testCount++;
  }

  public synchronized void assertEquals(int s1, int s2) {
    if (s1 == s2) {
      success();
    } else {
      failure("Expected " + s2 + " got " + s1);
    }
    this.testCount++;
  }

  public synchronized void assertEquals(boolean s1, boolean s2) {
    if (s1 == s2) {
      success();
    } else {
      failure("Expected " + s2 + " got " + s1);
    }
    this.testCount++;
  }

  public synchronized void assertEquals(Mode s1, Mode s2) {
    if (s1 == s2) {
      success();
    } else {
      failure("Expected " + s2 + " got " + s1);
    }
    this.testCount++;
  }

  public synchronized void assertEquals(Thread.State s1, Thread.State s2) {
    if (s1 == s2) {
      success();
    } else {
      failure("Expected " + s2 + " got " + s1);
    }
    this.testCount++;
  }
}

class Signal {
  public boolean flag;
}
