./compile

# david test 1
echo "12,T,0  ,ABCDEF" | ./receiver 12
echo -e "\n"

# david test 2, wrong id
echo "12,T,0  ,ABCDEF" | ./receiver 99
echo -e "\n"

# david test 3, test multiple packets, test out of order packets
{ 
    echo "12345,F,0  ,A";
    echo "12345,F,1  ,B";
    echo "12345,F,2  ,C";
    echo "12345XFX0  XA";
    echo "12345,F,1  ,B";
    echo "54321,F,2  ,C";
    echo "12345,F,6  ,G";
    echo "12345,F,7  ,H";
    echo "12345,T,8  ,I";
    echo "12345,F,3  ,D";
    echo "12345,F,4  ,E";
    echo "12345,F,5  ,F";
} | ./receiver 12345
echo -e "\n"

# ethan test 1, commas in message
{ 
    echo "2,F,0  ,A,";
    echo "2,T,1  ,B,";
} | ./receiver 2
echo -e "\n"

# test messages in a row
#{ 
#    echo "12345,F,0  ,A";
#    echo "12345,T,2  ,B";
#} | ./receiver 1
#echo -e "\n"