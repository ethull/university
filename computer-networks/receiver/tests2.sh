#!/bin/bash
javac -cp . *.java

# works
echo $'\nshould succeed'
echo $'12,T,0  ,ABCDEF' | java -cp . ReceiverMain 12

# works
echo $'\nshould succeed'
echo $'13,F,0  ,tes\n13,T,2  ,g\n13,F,1  ,tin' | java -cp . ReceiverMain 13

# works
echo $'\nshould succeed'
echo $'14,F,0  ,t,e,s,\n14,T,2  ,g\n12,T,0  ,ABCDEF\n14,F,1  ,t,i,n,' | java -cp . ReceiverMain 14

# works
echo $'\nshould succeed (skips last packet)'
echo $'15,F,0  ,tes\n15,T,2  ,g\n15,T,1  ,tin' | java -cp . ReceiverMain 15

# works
echo $'\nshould fail (missing middle packet)'
echo $'15,F,0  ,tes\n15,T,2  ,g' | java -cp . ReceiverMain 15

# works
echo $'\nshould fail (no final packet)'
echo $'15,F,0  ,tes\n15,F,2  ,g\n15,F,1  ,tin' | java -cp . ReceiverMain 15

# works
echo $'\nshould fail (incorrect index padding)'
echo $'15,F,0  ,tes\n15,T,2  ,g\n15,F,01 ,tin' | java -cp . ReceiverMain 15

# works
echo $'\nshould fail (invalid index value)'
echo $'15,F,0  ,tes\n15,T,2  ,g\n15,F,1.0,tin' | java -cp . ReceiverMain 15

# works
echo $'\nshould succeed (ABCDEFGHI)'
{
    echo '12345,F,0  ,A'
    echo '12345,F,1  ,B'
    echo '12345,F,2  ,C'
    echo '12345XFX0  XA'
    echo '12345,F,1  ,B'
    echo '54321,F,2  ,C'
    echo '12345,F,6  ,G'
    echo '12345,F,7  ,H'
    echo '12345,T,8  ,I'
    echo '12345,F,3  ,D'
    echo '12345,F,4  ,E'
    echo '12345,F,5  ,F'
} | java -cp . ReceiverMain 12345