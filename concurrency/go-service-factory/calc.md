# calculations
## part1
### S1
``` text
S1: CCS to text to go code
S1 = a(x).c(name).𝜈 y. x'y.(P1 | S1)

receive channel x from channel a
receive value name from channel c
send channel y on channel x
run P1 in parallel with S1

a := make (chan chan chan int)
x:=<-a
name:=<-c
x<-y
go P1() //since P1 is first run it in goroutine
S1(a,c)

HOW DOES THE TRIPLE CHANNEL WORK?
channel a: channel that queues session requests, essentially a queue of channel x
channel x: channel that queues sessions, essentially a queue of channel y
channel y: channel repersenting the session, essentially a queue of integers that make up the session
```
### C1
```text
S1: CCS to text to go code

C1 = a'x.c'name. x(y). Q1

send channel x on channel a
send string name on channel c
receive channel y on channel x
call Q1

a<-x
c<-name

```
## part2
### S2
S2 = a(x).c(name).𝜈y. 𝜈notify x'y. x'notify (P2 | S2)

receive channel x from channel a
receive value name from channel c
send channel y on channel x
send channel notify on channel x
run P2 in parallel with S1


### C2
C2 = a'x.c'name.x(y).x(notify).Q2

### P2
- how do i implement the non-deternimisum
- select?, fan in?

#### chat with class supervisior
- send something on notify, use it to block
- block an send and receive
- is just send and receive

#### taking apart the brief
  
    - client never blocks on read on channel y, if its empty block on notify
        - Q2 case x:= <- y default <- notify
        - never blocks on a read, but not a send, so P2 y <- x always comes first
    - P2 after sending interger, it handshakes on notify only if the client is waiting
        - P2 y <- x always comes first
    - Q2 receives the next int on y if y is not empty, if it is block on notify before reading y
        - Q2 case  x:= <-y print(x), default <- notify  <-y
    - imagine wait on notify to be efficient as waiting for msg, blocking on y would be expensive
        - re-inforce never block on y
    - CCS doesnt specify priorities:
        - P2 Q2 need to use case/default to specify priorities

- process:
```
P2 sends y <- x
Q2 x:= <- y, print(x)
...
```
#### testing
```
- why are we getting deadlock
    - sleeping on notify for alice -> before y <- x alice
    - same happens for every client
    - then we get no more input, every client is on deadlock
    - once client is sleeping on notify, P2 cant get past y <- x
    - how can i get P2 to not block on y <- x, case?
```
#### p2 solutions
```go
// sol 1, follows ccs, causes deadlock at y <- x with notify
y <- x
case notify <- 0:
    fmt.Printf("notify <- 0 for %s\n", name) //this doesnt get called
default:
    fmt.Printf("not blocked on notify for %s\n", name)
    time.Sleep(time.Duration(rand.Intn(2)) * time.Millisecond)

// sol2, works but is not faithful to CCS
// y <- x and notify could succeed at once
// x is not being sent after notify (sometimes we skip an x)
case y <- x:
    fmt.Printf("case y <- x for %s\n", name) //this doesnt get called
case notify <- 0:
    fmt.Printf("case notify <- 0 for %s\n", name) //this doesnt get called
    fmt.Printf("not blocked on notify for %s\n", name)
    time.Sleep(time.Duration(rand.Intn(2)) * time.Millisecond)

// sol3, nested select, redundant, does it actually follow ccs?
case y <- x:
    fmt.Printf("case y <- x for %s\n", name) //this doesnt get called
default:
    case y <- x:
        fmt.Printf("case y <- x for %s\n", name) //this doesnt get called
    case notify <- 0:
        fmt.Printf("case notify <- 0 for %s\n", name) //this doesnt get called
        fmt.Printf("not blocked on notify for %s\n", name)
        time.Sleep(time.Duration(rand.Intn(2)) * time.Millisecond)

// sol4, sol2 but sends x
case y <- x:
    fmt.Printf("case y <- x for %s\n", name) //this doesnt get called
case notify <- 0:
    y <- x
    fmt.Printf("case notify <- 0 for %s\n", name) //this doesnt get called
    fmt.Printf("not blocked on notify for %s\n", name)
    time.Sleep(time.Duration(rand.Intn(2)) * time.Millisecond)
```
## part2
- soon as badc is run, the program hangs, why?
- diff executions are getting mixed up, <-a and <-c for badc getting mixed with C1
    - fix with critical section somehow?
