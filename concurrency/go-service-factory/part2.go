package main

import (
	"fmt"
    "math/rand"
	"time"
)

// server
func S2(a <-chan chan chan int, c <-chan string) {
    // S2 = a(x).c(name).𝜈y. 𝜈notify x'y. x'notify (P2 | S2):
    x := <-a
    name := <-c
    y := make(chan int)
    x<-y
    notify := make(chan int) // create synchronous channel notify
    x<-notify //send notify channel on channel x
	fmt.Printf("Creating a new service for %s\n", name)

    go P2(y, notify, name) // include new notify channel when calling P2
    S2(a, c)
}

// service
func P2(y chan<- int, notify chan<- int, name string) {
    // P2 = y' . (notify' .P2 + P2):
    // follow ccs or have a working solution?
	//fmt.Printf("P2 run for %s\n", name)
	for x := 1; ; x++ {
	    //fmt.Printf("before y <- x %s\n", name)
		//y <- x // send int x on channel y
	    //fmt.Printf("after y <- x %s\n", name)
        select {
            //case notify <- 0:
	        //    fmt.Printf("notify <- 0 for %s\n", name) //this doesnt get called
            //default:
	        //    fmt.Printf("not blocked on notify for %s\n", name)
		    //    time.Sleep(time.Duration(rand.Intn(2)) * time.Millisecond)

            case y <- x:
	            //fmt.Printf("case y <- x for %s\n", name)
            case notify <- 0:
                y <- x
	            //fmt.Printf("case notify <- 0 for %s\n", name)
		        time.Sleep(time.Duration(rand.Intn(5)) * time.Millisecond)
        }
	}
}

// client
func Q2(y <-chan int, notify <-chan int, name string) {
    // Q2 = y . Q2 + notify . y. Q2:
	//fmt.Printf("Q2 run for %s\n", name)
    select {
        case x := <- y:
	        fmt.Printf("%s has received %d\n", name, x)
            Q2(y, notify, name)
        // if buffer is empty sleep on notify
        default:
	        //fmt.Printf("sleeping on notify for %s\n", name)
            <- notify
	        fmt.Printf("%s has received %d\n", name, <-y)
            Q2(y, notify, name)
        //case <- notify:
	    //    fmt.Printf("%s has received %d\n", name, <-y)
        //    Q2(y, notify, name)
    }
}

// client initialisation
func C2(a chan<- chan chan int, c chan<- string, name string) {
    // C2 = a'x.c'name.x(y).x(notify).Q2:
    x := make(chan chan int)
    a<-x
    c<-name
    y := <-x
    notify := <-x //receive notify channel on channel x
    Q2(y, notify, name)
}

func main() {

	a := make(chan chan chan int, 5)
	c := make(chan string, 1)

	go S2(a, c)
	go C2(a, c, "alice")
	go C2(a, c, "bob")
	go C2(a, c, "carol")

	time.Sleep(1000 * time.Millisecond)
	//time.Sleep(10000 * time.Microsecond)
}
