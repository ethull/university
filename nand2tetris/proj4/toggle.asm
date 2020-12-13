//@SCREEN
//D=A
//initialise pointer to addr -1
//@Pos
//M=D-1
@colour
M=0

@RESET
0;JMP

//loop until key >0, assume starts >0
(CHECK)
@KBD
D=M
@CHECK2
D;JGT
@CHECK
0;JMP

//loop until key=0
(CHECK2)
@KBD
D=M
@FLIP
D;JEQ
@CHECK2
0;JMP

(WRITE)
//if position is at max of the screen, reset
@24575
D=A
@Pos
D=D-M
@RESET
D;JEQ

@colour
D=M
//else start writing
@Pos
A=M
M=D

//go to next position
@Pos
D=M+1
@Pos
M=D

//next loop
@WRITE
0;JMP

(FLIP)
//change current colour
@colour
M=!M
//write screen change
@WRITE
0;JMP

(RESET)
//reset pointer position
@SCREEN
D=A
//initialise pointer to addr -1
@Pos
M=D-1
@CHECK
0;JMP
