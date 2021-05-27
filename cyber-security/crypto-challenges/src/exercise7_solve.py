# purpose: apply manual/guested alphabet on the ciphertext (for exercise7)
# author: ethan hullett
# extra dependancies: cexercise7_uni_freq.json, tess27_uni_freq.json (from exercise7_counter.py)
# notes: written in python to quicken manual process

""" Summary of manual notes

Open ordered letter freqs -> create a mapping -> sub

This program is most utility methods for manual working out

Manual process
    Run exercise7_solve.py -> look at what letters in decrypted text should be changed -> swap them in the current map -> once enougth words create correct alphabet dict from current ciphertext
    EG ciphertext ts = as
        need to correct mappings
        so what ever mapped to t now needs to map to a
            L:A
        and we need to remap the vals that are now duplicated
            L:x and y:A -> y:x
            L:T and V:A -> V:T

map based on just the corresponding freq of letters
    {'F': '|', 'N': 'E', 'L': 'T', 'V': 'A', 'C': 'O', 'Q': 'H', 'X': 'N', 'W': 'I', 'Y': 'S', 'M': 'R', 'R': 'D', 'D': 'L', 'P': 'U', 'B': 'W', '|': 'M', 'I': 'C', 'K': 'F', 'S': 'G', 'O': 'Y', 'J': 'P', 'Z': 'B', 'U': 'V', 'A': 'K', 'T': 'X', 'E': 'J'}

    EG some manual changes to above map from deductions
        f : | ,has to be to appear common enougth for proper wordlength
        c : a/i/t/o ,needs to be a one letter word for the first letter
            its too often to be t or o
            it being i makes sense as this makes two letter words in the ciphertext match up

        t -> a, l:t v:a -> l:a v:t
        o -> d, w:o r:d -> w:d r:o
        y -> p, o:p j:y
        n -> h, x:h q:n
        n -> o, q:o r:n

Final mappings
    L:A J:B
    I:C D:D
    N:E Z:F
    K:G X:H
    C:I T:J
    U:K R:L
    B:M W:N
    Q:O O:P
    na:Q M:R
    Y:S V:T
    P:U A:V
    S:W E:X
    |:Y na:Z
    F:|

    F:| N:E Y:S M:R P:U I:C  //these fit the original frequencies
    L:T V:A C:O Q:H X:N W:I R:D D:L B:W |:M K:F S:G O:Y J:P Z:B U:V A:K T:X E:J
"""

import json

#  apply a map/dict to the ciphertext
def decrypt(aDict):
    output = ""
    ct = open("./cexercise7.txt", "r").read().rstrip()
    for i in range(len(ct)):
        output+= aDict[ct[i]];
    return output

# read unigram frequencies to generate the initial map
def convert(filename):
    # load and parse the json file
    ctf = open(filename, "r")
    ct_json = json.load(ctf)

    # reorder frequencies
    cj_set = [(v,k) for k,v in ct_json.items()]
    cj_set.sort(reverse=True) # natively sort tuples by first element

    # reformate as a dict for clear and consistent output
    outputDict = {}
    for (i,v) in cj_set:
        outputDict[v] = i;
    # print(outputDict)
    return outputDict

# get list of frequencies from ciphertext and tess27
ctfd = list(convert("./cexercise7_freq.json").keys())
tuf = list(convert("./tess27_uni_freq.json").keys())

#  sample dict used for figuring out other mappings
sampleDict = {}
for i in range(len(ctfd)):
    sampleDict[ctfd[i]] = tuf[i]
sampleDict["C"] = "I"
sampleDict["W"] = "O"
sampleDict["L"] = "A"
sampleDict["V"] = "T"
sampleDict["W"] = "D"
sampleDict["R"] = "O"
sampleDict["O"] = "P"
sampleDict["J"] = "Y"
sampleDict["X"] = "H"
sampleDict["Q"] = "N"
sampleDict["Q"] = "O"
sampleDict["R"] = "N"

# print(sampleDict)
#
# print (decrypt(sampleDict))

# final dict as a result of sampleDict and manual work
ansDict = {
    "L":"A", "J":"B", "I":"C", "D":"D",
    "N":"E", "Z":"F", "K":"G", "X":"H",
    "C":"I", "T":"J", "U":"K", "R":"L",
    "B":"M", "W":"N", "Q":"O", "O":"P",
    "M":"R", "Y":"S", "V":"T", "P":"U",
    "A":"V", "S":"W", "E":"X", "|":"Y",
    "F":"|"
}
print (decrypt(ansDict))
