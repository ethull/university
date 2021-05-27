/*
    purpose: solve vigenere cipher with the length given (cexercise3)
    author: ethan hullett

    1 break word into groups encrypted by the same letter of the keyword for frequency analysis
    2 for each group build frequency tables for every shift
    3 then compare which freq table is closest to the english language
    4 build letter of keyword corresponding to group using the wining shift
    5 pass original inp text with key to decrypt() mtd (from exercise2.cpp)
*/

#include <bits/stdc++.h>

using namespace std;

// impl a known key to decrypt ciphertext
void decrypt(char c[], char key[]) {
    //key[] = "AAAAAA";
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

int main() {
    //open file
    freopen("cexercise3.txt", "r", stdin);
    char c[841];
    cin.getline(c,sizeof(c));

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

    //arr with each letter of the key in its own group, for frequency analysis
    char groupedText[6][140];
    cC = 0;
    for (int i = 0; i < 140; i++) {
        for (int j = 0; j < 6; j++) {
            groupedText[j][i] = c[cC];
            cC++;
        }
    }

    //cornwalls english letter frequency table
    double engFreqTable[] = {8.12, 1.49, 2.71, 4.32, 12.0, 2.30, 2.03, 
                            5.92, 7.31, 0.10, 0.69, 3.98, 2.61, 6.95, 
                            7.68, 1.82, 0.11, 6.02, 6.28, 9.10, 2.88, 
                            1.11, 2.09, 0.17, 2.11, 0.07};

    char kw[7];
    for (int i = 0; i < 6; i++) {
        double minChiSquared = 100000000.0;
        int minShift = 0;
        //for each possible shift
        for (int k=0; k<26; k++){
            //freq table for current shift
            double freqTable[26] = {0};
            for (int j = 0; j < 140; j++) {
                //count occurances of each letter
                freqTable[((groupedText[i][j] - 'A' + 26 - k)%26)]++;
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
    //decrypt ciphertext
    decrypt(c, kw);

}
