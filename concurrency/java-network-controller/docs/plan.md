# plan

## questions
- where are ports created? -
  - how do i close the ports, since i cant change the mode
- how do i change a ports mode when its a private field with no setter -
- how am i going to structure my locks/semaphores with ports (array/hashmap)? -1/2
- am i going to use locks/semaphores or both -1/2
  - when i going to create them
    - too ensure they dont get reset when a thread is still using them
    - too make sure stuff is concurrent
  - how am i going to vary their size relative to Port Mode
- deal with context switch between 1 and 1024 size semaphores
### locks or semaphores
- think semaphores make more sense, can adjust their size for multi access
### how do i open a port when i cant set it
- a port can only be opened when the obj is made
- at the start of the program
  - add a load of ports and close them
- when a new port is opened
  - replace the port obj with a new port obj with the current mode
### data structure for ports
- for each port we need to know:
  - is it open or closed
  - if open what mode is it in?
- hashmap
  - [Port Objs], [Port Semaphores] 
  - PortNumber: Port Obj: Semaphore
  - PortNumber: [Port Obj, Semaphore]
  - [Port Obj: Semaphore]
    - index is port number
### semaphore context switch
- have anouther array of semaphores with 1024, when it reaches 0 default to other semaphore
  - this could be his way of making us use locks (lock or semaphore(1)) and semaphores
  - semaphorePortAccess array
  - semaphoreMultiAccess array
  - but after the initial thread releases access to port, how will we check if its closed, will have to check semaphore MultiAccess whenever we acquire any port
      - in the close method could release access
      - the semapore is controlling access to open new closed ports and not for already open multi ports
        - the threads for closed ports and multi ports are not in the same queue
          - ! i need to think of this problem as two queues trying to access different tagged/marked resources
      - semapores are not directly linked to threads, just because one thread has done its job doesnt mean i 
- use release(1023) and reducePermits()
  - requires inheriting the class
  - when to reduce permits?
    - in close() when closing a multi mode port
  - when to increase permits?
    - in acquirePort when acquiring a multiPort
- replace port semaphore obj with new one relative to mode
  - likely wont work
  - this will require getting rid of acquire queue


## lock layout
- hashmap of Ports mapped to their status
## where is the concurrency
- when a thread is in Mode.MULTI mode
- when multiple clients request access to a port at once

## todo
- understand diff betwrrn semaphores and locks, and how to impl both
- examine test file to think through how it operates with my interface
- plan data structure for ports
- pseduocode plan
  - model entire problem?
- more questions?

## psedocode

## new plan
- program basic outline of problem using brief -
  - using comments for bits i dont understand and concurrent bits
- run basic tests and update to pass -
- update program using test code base as ref
- add concurrency and logic for blocking ports
  - use the internet to better understand sempahores and locks
  - decide data structure for sotring sempahores and locks
  - add concurrency for calling operations on ports
  - add concurrency for requesting multiple ports at once
- run the tests and update to pass

## implementing concurrency
### plan
- what needs to be concurrent
- what needs to be in our critical section
- other problems
- race conditions
### what needs to be concurrent
- request access to distinct ports at the same time
### what needs to be in our critical section
- the time when between closing/opening a port and releasing/acquiring a semaphore?
### problems
- distinction between multi-ports and normal ports
### race condition
- since multi has a semaphore size of 1024 (>1)
- race conditions can happen, we need mutual exclusion
  - synchronized method/block somewhere
  - where?
### how to think about this problem
- we have a list of items to share: portList
  - each item in our list has a different property: port.mode
- we have multiple clients that want to access each port in the list
- we need to have orderly access to shared items, but a diff number of clients can access each item at once depending each items property
- a client can change an items property
- an item that hasn't been
- layouts
  - diff queue for each property: doesnt work because items properties can change
  - a queue for all items (despite their property), and then a separate queue for properties where they have 
    - clients queue in the first
  - a queue for access to each port, then anouther queue
- plan:
  - items: [port1, port2, port3]
  - properties: [prop1 (1 access), prop2 (1 access), prop3 (1024 access)]
  - queue 1 -----> [client that wants item1, client that wants item2]
  - queue 2 -----> [client that wants item3]
#### in general
- think about the problem as multiple clients tring to access a shared item at once
- but only a certain number of ppl can access it at once (as the shared item is behind a locked door)
- so the clients queue up for access
- the properties of each item effect how many can access, and hence how many queues or the properties of the queues that we may need

## bugs
- thread should be blocked trying to re-open for writing.
      Failed: Expected WAITING got TERMINATED
  - sol: typo and logic in close() mtd
    - if (lockSemaphores.get(port.portNumber()).availablePermits() == 1) {
    - ->
    - if (multiSemaphores.get(port.portNumber()).availablePermits() == 1023) {
- am getting occasional test failures
  - there is a race condition in my code somewhere
  - to do with multiports
  - problems
    - somehow first sempahore is blocking the second or blocking port access
      - how could this be happening only when t1 doesnt finish first
  - sols
    - somehow make access to port obj one at a time
    - make it so two cant try to close at once //still fail
    - one cant talk to port when anouther is trying to close at the same time
  - test notes
    - if t1 goes first there is no error
    - the threads are waiting rather than terminating
  - the ports are not closed after running
    - Multiple receive is allowed with no blocking (thread 2).
          Failed: Expected TERMINATED got WAITING
  -
    - Multiple receive is allowed with no blocking (thread 1).
            Failed: Expected TERMINATED got WAITING
    - Multiple receive is allowed with no blocking (thread 2).
            Failed: Expected TERMINATED got WAITING
