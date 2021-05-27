/*
    purpose: solve transposition cipher with unjumbled 4-6 cols (cexercise5)
    author: ethan hullett

    1 for each possible numCol
        2 workout colLengths
        3 divide into each cols
        4 read rowwise and output
    5 choose what ever of the 3 outputs looks most like english

    colLength = cipherTextLength / numOfCols
        4*210
        5*168
        6*140
*/

#include <bits/stdc++.h>

using namespace std;

int main() {
    
    //open file
    freopen("cexercise5.txt", "r", stdin);
    char c[841];
    cin.getline(c,sizeof(c));
    int inpSize = strlen(c);
    int possibleNumCols[] = {4,5,6};
    //iterate possible cols
    for (int i = 0; i < 3; i++) {
        int nC = possibleNumCols[i];
        int colSize = inpSize/nC;
        char kw[] = "";

        //uses std vector, which makes it easier to later find permutations (perms)
        vector<vector<char>> groupedText(nC, vector<char>((colSize), 'a'));
        //group cols
        int cC = 0;
        for (int i = 0; i < nC; i++) {
            for (int j = 0; j < colSize; j++) {
                groupedText.at(i).at(j) = c[cC];
                cC++;
            }
            cout << " ";
        }
        cout << "\n";

        //output rowwise
        for (int i = 0; i < 1; i++) {
            cout << "perm: " << i << "\n";
            for (int i = 0; i < colSize; i++) {
                for (int j = 0; j < nC; j++) {
                    cout << groupedText[j][i];
                }
            }
            cout << "\n";
        }
    }

}

