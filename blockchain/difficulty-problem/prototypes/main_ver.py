# main version 1
# auto chain hashes, concurrency, not complete
import hashlib
import uuid
import csv
from multiprocessing import Process, Lock

## hash submission parameters
# inp = input("enter a string to sha256: ")
username = "PlainChicken"
prevHash = None

## file parameters
# file pointers, global variables that are setup in init()
outputFile = None

# files for hash outputs, with nonce and difficulty
outputFileName = "output.csv"
# current difficulty num, already tried hashes with nonces
# outputs via difficulty
#progressFileName = "progress.txt"

## file contents paramters
# current difficulty of the chain
currentZeros = 0
currentDifficulty = 0

## arrays # currently just for testing
## nonces from all sessions
#all_nonces = []
## hashes from all sessions
#all_hashes = []

# nonces from current session
nonces = []
# hashes from current session
hashes = []

## variables for concurrent operations
outputFileLock = None
writeHashLock = None


## hash operation methods
# write a new difficulty hash to disk and update current difficulty / num zeros
def writeHash(hashOut, nonce, numZeros):
    # before we write the hash, we need to make sure we have the most up to date prevHash and numZeros
    global currentZeros
    writeCsv([hashOut, nonce, numZeros])
    currentZeros = numZeros

def evalHash(hashOut, nonce):
    # count num of zeros
    numZeros = countLeadingZeros(hashOut)
    # convert num 0s to difficulty
    if numZeros > currentZeros:
        print("new difficulty hash, current num zeros {} -> {}".format(currentZeros, numZeros))
        print(hashOut + " " + nonce + " " + str(numZeros))
        nonces.append(nonce)
        hashes.append(hashes)
        writeHash(hashOut, nonce, numZeros)

def makeHash(nonce):
    inp = prevHash + username + nonce
    hashOut = hashlib.sha256(inp.encode('utf-8')).hexdigest()
    evalHash(hashOut, nonce)


## nonce generator methods
# generate a range of random values
def generateRangeNumeric(num):
    for i in range(num):
        nonce = str(i)
        makeHash(nonce)

# generate hashes with random nonces
def generateRangeRandom(num):
    for i in range(num):
        randomNonce = uuid.uuid4().hex
        makeHash(randomNonce)

# for running on server, keep generating hashes with random nonces
# this func is run in parallel processes
def generateInfiniteRandom():
    counter = 0
    while True:
        randomNonce = uuid.uuid4().hex
        makeHash(randomNonce)
        counter+=1
        # every 5000 runs make sure we have up to date parameters
        if counter % 5000 == 0:
            updateParams()

# generate one hash value
def generateOne(nonce):
    inp = prevHash + username + str(nonce)
    x = hashlib.sha256(inp.encode('utf-8')).hexdigest()

    num0 = countLeadingZeros(x)
    print(x + " " + str(nonce) + " " + str(num0))


## util methods
# count the number of leading zeros
def countLeadingZeros(string):
    num0 = 0
    for i in string:
        if i == "0": num0 += 1
        else: break
    return num0

# need to update parameters
# to make sure we have the newest hash and the newest currentZeros (difficulty)
def updateParams():
    global prevHash
    global currentZeros
    global outputFileLock
    outputFileContents = readCsv()
    prevHash = int(outputFileContents[-1][0])
    currentZeros = int(outputFileContents[-1][2])

# check if params have updated
def checkParams():
    global prevHash
    global currentZeros
    global outputFileLock

def printOutput():
    print(username)
    print("prevhash: " + prevHash)
    print(nonces)

def readCsv():
    global outputFileLock
    global outputFileName
    outputFileLock.acquire()
    data = []
    with open(outputFileName, "r", encoding="utf-8", errors="ignore") as file:
        reader = csv.reader(file, delimiter=',')
        for row in reader:
            if row:  # avoid blank lines
                # columns = [str(row_index), row[0], row[1], row[2]]
                #data.append(columns)
                data.append(row)
        file.close()
    outputFileLock.release()
    return data

def writeCsv(data):
    global outputFileLock
    global outputFileName
    outputFileLock.acquire()
    with open(outputFileName, "a", encoding="utf-8", errors="ignore") as file:
        writer = csv.writer(file, delimiter=',',
                            quotechar='|', quoting=csv.QUOTE_MINIMAL)
        writer.writerow(data)
        file.close()
    outputFileLock.release()


## setup/lifecycle methods methods
# initialise global variables
def init():
    global prevHash
    global currentZeros
    global outputFileLock

    outputFileContents = readCsv()
    if len(outputFileContents) != 0:
        # get hash value and numZeros of last block
        prevHash = int(outputFileContents[-1][0])
        currentZeros = int(outputFileContents[-1][2])
    else:
        prevHash = "00000a2ed46cd277a0edc3f17ff3df541b034345f4696d75744279166e19d8eb"
        currentZeros = 0

    # initialise lock so we can control concurrent access to the output.csv file
    outputFileLock = Lock()

def run():
    print("starting operation")
    #generateRangeNumeric(prevHash, 2000)
    generateRangeRandom(prevHash, 200000)
    # run infinite generation loops with multiprocessing
    for i in range(5):
        Process(target=generateInfiniteRandom, args=()).start()

init()
run()

#generateOne("0c5c321c7bb942aa28a67ee0f58d6c1ecb0764fe5cd7ae4d07c47bf5790faf98", 3)
#generateOne("0256d274e7ee93ca58ee4ccd4f37ee98cc67ef35505b75df6cade340ada10a39", 40000000000)
#print(countLeadingZeros("000fe053d5c9ca567d003566ba760260dfdad0fb59c643648951d49160ca41be"))

# m = hashlib.sha256()
# print(x)
