// This file is BASED ON part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: project01starter/Or8.tst

load Or8.hdl,
output-file Or8.out,
compare-to Or8.cmp,
output-list a%B1.8.1 b%B1.8.1 out%B1.8.1;

set a %B00000000,
set b %B00000000,
eval,
output;

set a %B00000000,
set b %B11111111,
eval,
output;

set a %B11111111,
set b %B11111111,
eval,
output;

set a %B10101010,
set b %B01010101,
eval,
output;

set a %B00111100,
set b %B00001111,
eval,
output;

set a %B00010010,
set b %B10011000,
eval,
output;
