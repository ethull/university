# version 2
# auto chain hashes, no concurrency

import hashlib
import uuid
import csv

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


## hash operation methods
# write a new difficulty hash to disk and update current difficulty / num zeros
def writeHash(hashOut, nonce, numZeros):
    # before we write the hash, we need to make sure we have the most up to date prevHash and numZeros
    global currentZeros
    global prevHash
    writeCsv([hashOut, nonce, numZeros])
    currentZeros = numZeros
    prevHash = hashOut

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
    while True:
        randomNonce = uuid.uuid4().hex
        makeHash(randomNonce)


## util methods
# count the number of leading zeros
def countLeadingZeros(string):
    num0 = 0
    for i in string:
        if i == "0": num0 += 1
        else: break
    return num0

def printOutput():
    print(username)
    print("prevhash: " + prevHash)
    print(nonces)

def readCsv():
    global outputFileName
    data = []
    with open(outputFileName, "r", encoding="utf-8", errors="ignore") as file:
        reader = csv.reader(file, delimiter=',')
        for row in reader:
            if row:  # avoid blank lines
                # columns = [str(row_index), row[0], row[1], row[2]]
                #data.append(columns)
                data.append(row)
        file.close()
    return data

def writeCsv(data):
    global outputFileName
    with open(outputFileName, "a", encoding="utf-8", errors="ignore") as file:
        writer = csv.writer(file, delimiter=',',
                            quotechar='|', quoting=csv.QUOTE_MINIMAL)
        writer.writerow(data)
        file.close()


## setup/lifecycle methods methods
# initialise global variables
def init():
    global prevHash
    global currentZeros

    outputFileContents = readCsv()
    if len(outputFileContents) != 0:
        # get hash value and numZeros of last block
        prevHash = str(outputFileContents[-1][0])
        currentZeros = int(outputFileContents[-1][2])
    else:
        prevHash = "00000a2ed46cd277a0edc3f17ff3df541b034345f4696d75744279166e19d8eb"
        currentZeros = 0

def run():
    print("starting operation")
    generateInfiniteRandom()

init()
run()
