## mtu and the size limit
- each printed line is an encoded packet
- since each message has a max mtu
  - mtu is max num chars of packet minus the newline
- the mtu will dictate how many packets will be needed to send the data
  - will need to figure out how many packets are needed for current mtu and data

## pseudocode
- figure out num of packets needed to send the data

### prep() figure out num of packets needed to send data
packetSize = 0
lengthId = len(PACKETID)
packetSize+= lengthId
packetSize+= 1+1+1+3+1=7 //comma fields, last packet field, packet number field
remainingSize = MTU-packetSize
msgTextSize = size(MESSAGETEXT)
numOfPackets = (msgTextSize / remainingSize)+1
  // java rounds down integer division, so we add 1 to round Up

### prep hand run through
lengthId=5
packetSize=5
packetSize+=7 =12
MTU=30
remainingSize=30-12=18
msgTextSize=20
numOfPackets=(20/18)+1=2
  // we need 2 packets to send the data

## to rm
```java
// dont need these, since size is either mtu, or the remaining data
// size of data we are going to send with each packet
int packetMsgTextSize = mtu
// size of data we are going to send with the last packet
// if msgSize < mtu its all the data, else its the remaining data
int lastPacketMsgTextSize

// and size of the data we are going to send with the last packet (which could also be the only packet)
```

### send hand run through


## constraints
- msg length >1 -
- max 999 packets to encode it -
- msg id max 65535 min 0 -
- mtu max 65535 min 1 -
- mtu suf large ... -
