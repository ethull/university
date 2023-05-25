# notes

## considerations (given)
- packets with diff IDs, to discard -
    - done in processPacket()
- incorrectly formatted packets (according to senders protocol), to discard -?
    - done in processPacket()
    - more combinations to test, prob some issues missed
- correct targetID packets but arrive out of order
    - done by storing packets in hashmap in processPacket()
- correct targetID packets are missing
    - done in buildMessage by making sure packetIDs are consecutive
- packets with duplicate targetIDs, to discard
    - done in processPacket()

### considerations (by me)
- can only be one T packet -
    - done in processPacket with if statement and class var
- text is repeated between packets?
    - // since text must be consecutive between packets
    - todo in buildMessage?
- commas in the message
    - this assumes that everything after the last comma is in the message
- packets with diff widths of entire packet (know mtu based on first packet)
- package index is padded with zeros instead of spaces

## order of operations / high level pseudocode/plan
- break input up
- check if packet is correct format
- record packet if its correct
- go over all packets to check which are correct

## thoughts
- if packet in the correct format
- starts with ID with correct length
- other elements are the correct length

## stuff in the slides that could help?
- network layer issues: missing, corrupt, duplicate and mis-ordered packets
- w28 slides CO data-link
    - maybe have to use technologies from data-link slides
- dont think these are going to help much, due to the heavy level of abstraction in this ass
- err codes require checksum in sender, but sender and receiver are tested independantly
- arq
    - frames wont get lost
    - we just get rid of frames that are incorrect
    - no back and forth communication

## decisions
- use .split() or split up packet manually
    - one is more fingrained and allows better err checking

## questions
- are the receiver examples in pdf surposed to have one space instead of two //unlikely, prop just problem copying from pdf
- mtu thing?



## code to rm
```java
//Iterator<String> it = packetCharArr.iterator();

// do all formatting manually to make sure we get all errors (no .split(","))
char[] packetCharArr = packet.toCharArray();
int currentCharPtr = 0;

// arr of packets in order, free of duplicates, with no missing packets
ArrayList<String[]> readyPackets = new ArrayList<String[]>();
// get packets with lowest sequence number
        
// if this is the last packet then set i as the last sequence number
if (packetSplit[1] == "T") lastSeqNum = sequenceNum;

// old split code
String[] packetSplit = packet.split(",");
// should have at least 4 items separated by commas
if (packetSplit.length < 4){
    return;
}
System.out.println(packetSplit.length);
// concatonate items after the first three, as there could be strings in the message
if (packetSplit.length > 4) {
    System.out.println("short");
    String[] newArr = {packetSplit[0], packetSplit[1], packetSplit[2], packetSplit[3]};
    for (int i = 4; i < packetSplit.length-4; i++) {
        packetSplit[3] += ",";
        packetSplit[3] += packetSplit[i];
    }
    packetSplit = newArr;
}

// we dont have the correct syntax packet if we dont have 4 items separated by commas
//if (packetSplit.length != 4) return;
```

## packet arr
```
arr
    packets
```

## links
- https://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
- https://calvh.medium.com/how-to-pass-input-files-to-stdin-in-vscode-cb31cd7740b8
- https://unix.stackexchange.com/questions/700969/send-more-than-one-line-to-an-executable-using-bash
- https://stackoverflow.com/questions/3799130/how-to-iterate-through-a-string
- https://stackoverflow.com/questions/27086540/try-catch-not-catching-exception

## problems
- getting java debugger to use stdin and params (args)
- split problems with too many commas
    - how to iterate string and concat parts of lists
- using equals() for strings
