// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: project01starter/Mux8Way8.tst

load Mux8Way8.hdl,
output-file Mux8Way8.out,
compare-to Mux8Way8.cmp,
output-list a%B1.8.1 b%B1.8.1 c%B1.8.1 d%B1.8.1 e%B1.8.1 f%B1.8.1 g%B1.8.1 h%B1.8.1 sel%B2.3.2 out%B1.8.1;

set a 0,
set b 0,
set c 0,
set d 0,
set e 0,
set f 0,
set g 0,
set h 0,
set sel 0,
eval,
output;

set sel 1,
eval,
output;

set sel 2,
eval,
output;

set sel 3,
eval,
output;

set sel 4,
eval,
output;

set sel 5,
eval,
output;

set sel 6,
eval,
output;

set sel 7,
eval,
output;

set a %B00010010,
set b %B00100011,
set c %B00110100,
set d %B01000101,
set e %B01010110,
set f %B01100111,
set g %B01111000,
set h %B10001001,
set sel 0,
eval,
output;

set sel 1,
eval,
output;

set sel 2,
eval,
output;

set sel 3,
eval,
output;

set sel 4,
eval,
output;

set sel 5,
eval,
output;

set sel 6,
eval,
output;

set sel 7,
eval,
output;
