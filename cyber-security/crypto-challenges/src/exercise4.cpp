/*
    purpose: solve vigenere cipher with key length within 4-6 given (cexercise4)
    author: ethan hullett

    1 guess possible keyword lengths
        2 break word into groups encrypted by the same letter of the keyword for frequency analysis
        3 run the index of coincidence (ioc) on every group and get an average
    2 choose kw length from the ioc average closest to english
    3 use getKeyword() mtd to calc kw with the given length (from exercise3.cpp)
    4 pass original inp text with key to decrypt() mtd (from exercise2.cpp)
*/

#include <bits/stdc++.h>

using namespace std;

// impl a known key to decrypt ciphertext
void decrypt(char c[], char key[]) {
    int offsets[strlen(key)];
    for (int i = 0; i < strlen(key); ++i) {
        //calc offset via ascii arithmetic
        offsets[i] = key[i] - 'A';
    }
    int cOI = 0;

    for(int i=0; i<31; i++){
        if (c[i] >= 'a' && c[i] <= 'z'){
            //apply index by ascii and modulus arithmetic, + 26 is only required for decryption
            int toAdd = ((c[i] - 'a') + 26 - offsets[cOI]) % 26;
            c[i] = 'a' + toAdd;
        } else if (c[i] >= 'A' && c[i] <= 'Z') {
            int toAdd = ((c[i] - 'A') + 26 - offsets[cOI]) % 26;
            c[i] = 'A' + toAdd;
        }
        cOI = (cOI + 1) % (strlen(key));
    }
    for(int i=0; i<30; i++){
        cout << c[i];
    }
    cout << "\n";

}

//calc a kw given a pointer to store it in, and text grouped by key letter
void getKeyword(char kw[], int groupSize, char *groupedText) {
    //cornwalls english letter frequency table
    double engFreqTable[] = {8.12, 1.49, 2.71, 4.32, 12.0, 2.30, 2.03, 
                            5.92, 7.31, 0.10, 0.69, 3.98, 2.61, 6.95, 
                            7.68, 1.82, 0.11, 6.02, 6.28, 9.10, 2.88, 
                            1.11, 2.09, 0.17, 2.11, 0.07};

    int kwLength = strlen(kw);
    for (int i = 0; i < kwLength; i++) {
        double minChiSquared = 100000000.0;
        int minShift = 0;
        //for each possible shift
        for (int k=0; k<26; k++){
            //freq table for current shift
            double freqTable[26] = {0};
            for (int j = 0; j < groupSize; j++) {
                //count occurances of each letter
                freqTable[((*((groupedText+i*groupSize)+j) - 'A' + 26 - k)%26)]++;
            }
            //calc chiSquared statistic for current freq table
            double currentChiSquared = 0.0;
            for (int n=0; n<26; n++){
                currentChiSquared+=(pow(freqTable[n]-engFreqTable[n],2)/engFreqTable[n]);
            }
            //check if current stat is the lowest/best
            if (currentChiSquared < minChiSquared) {
                minChiSquared = currentChiSquared;
                minShift = k;
            }
        }
        kw[i]='A'+minShift;
        //cout << "kwc: " << kw[i] << " "; 
    }
    cout << "kw:" << kw << "\n";
}

int main() {
    //open file
    freopen("cexercise4.txt", "r", stdin);
    char c[841];
    cin.getline(c,sizeof(c));
    int inpSize = strlen(c);

    //arr with text split into groups of 6, for manual analysis
    char splitText[140][6];
    int cC = 0;
    for (int i = 0; i < 140; i++) {
        for (int j = 0; j < 6; j++) {
            splitText[i][j] = c[cC];
            cout << splitText[i][j];
            cC++;
        }
        cout << " ";
    }
    cout << "\n";

    int kwLength = 0;
    //possible kw lengths we can test
    int possibleLengths[] = {4, 5, 6};
    int numOfPossibleLengths = 3;
    //average ioc for the ciphertext split into groups of each possible kwLength, we want the smallest 1 thats fits an english ioc
    double iocAverages[numOfPossibleLengths];
    //for each possible kwLength
    for (int i = 0; i < numOfPossibleLengths; i++) {
        int gKwLength = possibleLengths[i];
        //EG if kwSize is 4 and textSize is 840, there are 4 groups of 210
        int groupSize = inpSize/gKwLength;
        char groupedText[gKwLength][groupSize];
        cC = 0;
        for (int i = 0; i < groupSize; i++) {
            for (int j = 0; j < gKwLength; j++) {
                groupedText[j][i] = c[cC];
                cC++;
            }
        }

        //sum of iocs
        double sum = 0;
        //find ioc for each group and average
        for (int j = 0; j < gKwLength; j++){
            //build freq table
            double freqTable[26] = {0};
            for (int k = 0; k < groupSize; k++) {
                freqTable[(groupedText[j][k] - 'A')]++;
            }
            int num = 0;
            //keep track of count incase in last group not all key vals used
            int total = 0; 
            //calc individual ioc
            for(int k = 0; k < 26; k++){
                num += freqTable[k]*(freqTable[k]-1);
                total += freqTable[k];
            }
            sum += 26.0*num / (total*(total-1));
        }
        iocAverages[i] = sum / (double) gKwLength;
    }
    
    //choose the keyword length relative to ioc results
    //find average ioc that fits (is close to 1.7 (english ioc))
    for (int i = 0; i < numOfPossibleLengths; i++) {
        if (iocAverages[i] > 1.6 && iocAverages[i] < 1.7){
            kwLength = possibleLengths[i]; 
        }
    }

    //arr with each letter of the key in its own group (with correct kwLength), for getKeyword()
    char groupedText[kwLength][inpSize/kwLength];
    cC = 0;
    for (int i = 0; i < inpSize/kwLength; i++) {
        for (int j = 0; j < kwLength; j++) {
            groupedText[j][i] = c[cC];
            cC++;
        }
    }

    char kw[kwLength+1];
    kw[kwLength] = '\0';
    //get the keyword
    getKeyword(kw, inpSize/kwLength, (char *) groupedText);
    //decrypt ciphertext
    decrypt(c, kw);

}
