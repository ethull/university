# concurrency course assignments

## java concurrent network controller
- aim is to build a network controller
- it can be accessed concurrently by multiple local threads (clients) to open ports
- uses shared memory concurrency primitives such as semaphores

## go concurrent service factory
- implement a go service factory, then modify it to use the producer-consumer concurrency pattern
- uses synchronous and asynchronous message passing in go via go channels
- requires converting robert milner ccs to go code
