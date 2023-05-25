package main

import (
	"fmt"
	"math/rand"
	"time"
)

func S1(a <-chan chan chan int, c <-chan string) {
    // S1 = a(x).c(name). 𝜈 y. x'y.(P1|S1):
    x := <-a // receive the next x channel from channel a
    name := <-c // receive the next string (name) from channel c
    y := make(chan int) // create fresh session channel y
    x<-y // send that session channel y on channel x
	fmt.Printf("Creating a new service for %s\n", name)

    go P1(y, name) // run as gorutine as we want to run in parallel
    S1(a, c)
}

func P1(y chan<- int, name string) {
	n := 5 + rand.Intn(10)
	fmt.Printf("%s will receive %d messages\n", name, n)
	for x := 1; x < n+1; x++ {
		y <- x
		time.Sleep(time.Duration(rand.Intn(5)) * time.Millisecond)
	}
	close(y)
}

func Q1(y <-chan int, name string) {
    for k := range y { // k is the value,
	    fmt.Printf("%s has received %d\n", name, k)
        // the index is not needed!
    }
	fmt.Printf("%s is ending\n", name)

}

func C1(a chan<- chan chan int, c chan<- string, name string) {
    // C1 = a'x.c'name. x(y).Q1:
    x := make(chan chan int) // create channel x (queue for session channels (channel y))
    a<-x // send channel x on channel a
    c<-name // send string name on channel c
    y := <-x // receive session channel y on channel x
    Q1(y, name) // call Q1 with session channel and name
    // go Q1(y, name) // ?
}

func main() {
	a := make(chan chan chan int, 5)
	c := make(chan string, 1)

	go S1(a, c)
	go C1(a, c, "alice")
	go C1(a, c, "bob")
	go C1(a, c, "carol")

	time.Sleep(1000 * time.Second)
}
