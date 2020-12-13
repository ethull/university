//divide 2 positive inputs
//inputs: R0, R1  (RAM locations)
//outputs: R3 quotient, R4 remainder

(LOOP)

    @R0
    D = M       // D = a
    @R1
    D = D - M   // D = a - b 

    @REMAINDER 
    D;JLT       // (a-b) < 0 go to REMAINDER

    @R3 //inc num divisions, done before equals check
    M=M+1

    @END
    D;JEQ       // If (a - b == 0)  go to END

    @R1
    D = M        // D = b
    @R0
    M = M - D    // a = a - b, current value of a used to break loop

    @LOOP
    0;JMP

(REMAINDER)
    @R0
    D=M
    @R4
    M=D

    //@R0
    //D = M        // D = b
    //@R1
    //D = M-D
    //@R3
    //M=D

(END)
    //dont need to do anything as R2 and R3 set elsewhere
    @END
    0;JMP 
