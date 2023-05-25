package main

import (
	"fmt"
	"math/rand"
	"time"
)


// write here your CCS specification for S3:
// a(x).( c(name). 𝜈 y. x'y.(P1|S3) + S3 )

func S3(a <-chan chan chan int, c <-chan string) {
    //fmt.Println("S3 before receive x from a")
    x := <-a // receive the next x channel from channel a
    //fmt.Println("S3 after receive x from a")
	//time.Sleep(time.Duration(rand.Intn(1)) * time.Millisecond)
    select {
        // if we get a name sent on c after we receive x, its a C1
        case name := <- c:
            //name := <-c // receive the next string (name) from channel c
            y := make(chan int) // create fresh session channel y
            //fmt.Printf("before x<-y for %s\n", name)
            x<-y // send that session channel y on channel x
            fmt.Printf("Creating a new service for %s\n", name)
            go P1(y, name) // run as gorutine as we want to run in parallel
        // else its a badc
        default:
            //fmt.Printf("Detected badc, not creating a new service")
    }
    //fmt.Println("S3 end")
    S3(a, c)
}

func P1(y chan<- int, name string) {
	n := 5 + rand.Intn(10)
	for x := 0; x < n; x++ {
		y <- x
		fmt.Printf("Sent %d to %s\n", x, name)
		time.Sleep(time.Duration(rand.Intn(5)) * time.Millisecond)
	}
	close(y)
}

func Q1(y <-chan int, name string) {
    // Q1 from part1:
    for k := range y { // k is the value,
	    fmt.Printf("%s has received %d\n", name, k)
        // the index is not needed!
    }
	fmt.Printf("%s is ending\n", name)
}

func C1(a chan<- chan chan int, c chan<- string, name string) {
    // C1 from part1:
    x := make(chan chan int) // create channel x (queue for session channels (channel y))
    a<-x // send channel x on channel a
    c<-name // send string name on channel c
    y := <-x // receive session channel y on channel x
    Q1(y, name) // call Q1 with session channel and name
}

func BadC(a chan<- chan chan int, c chan<- string, name string) {
//func BadC(a chan<- chan chan int) { : so we dont use c or name?
    // badC = a'x.0:
    // a'x.c'name. x(y).Q1 -> a'x.0
    x := make(chan chan int) // create channel x (queue for session channels (channel y))
    a<-x // send channel x on channel a
}

func main() {

	a := make(chan chan chan int, 5)
	c := make(chan string, 1)

	go S3(a, c)
	go C1(a, c, "alice")
	go BadC(a, c, "bob")
	go C1(a, c, "carol")
	go BadC(a, c, "david")

	time.Sleep(1000 * time.Second)
}
