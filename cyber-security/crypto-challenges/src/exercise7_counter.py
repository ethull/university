# purpose: calculate unigram frequencies from tess27.txt (cexercise7)
# author: ethan hullett
# extra dependancies: tess27.txt

import json
from collections import Counter

# open target/new .json file and dump contents of the passed dict in json format
def dumpResult(filename, resultDict):
    with open(filename, "w") as fp:
        json.dump(resultDict, fp)
    fp.close();

# open target/new .txt file and extract unigram relative frequencies
def getUnigrams(filename):
    tessStr = open(filename, "r").read().rstrip()
    size = len(tessStr);

    # count the num of unigrams in the text
    uniGramsResult = dict(Counter(tessStr[x:x+1] for x in range(len(tessStr))))
    #biGramsResult = dict(Counter(string[x:x+2] for x in range(len(string) - 1)))

    # get relative frequencies
    for i in uniGramsResult:
        uniGramsResult[i] = uniGramsResult[i]/(size)
    return uniGramsResult

#get frequencies for tess27 and cexercise7, and then write them as json
dumpResult("./tess27_uni_freq.json", getUnigrams("./tess27.txt"))
dumpResult("./cexercise7_uni_freq.json", getUnigrams("./cexercise7.txt"))
