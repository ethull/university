/*
    purpose: solve transposition cipher with 6 cols (cexercise6)
    author: ethan hullett
    
    columns are jumbled up (as with typical columnar transposition cipher)
    
    1 guess numCols 
    2 for each guested numCol
        3 workout colLengths
        4 divide into each cols
        5 find perms
        6 choose perm with most probable bigrams/digrams

    improvements: use trigrams, gen own freqs from tess26, impl own perms, impl hill climbing
*/

#include <bits/stdc++.h>
#include <cfloat>

using namespace std;

//freq tables for bigram in english
//map<array<char, 2>, double> //have to use string as you cant init array keys with char[]
map<string, double> cornellBigramFT {{"TH", 1.52}, {"HE", 1.28}, {"IN", 0.94}, {"ER", 0.94}, {"AN", 0.82}, {"RE", 0.68}, {"ND", 0.63}, {"AT", 0.59}, {"ON", 0.57}, {"NT", 0.56}, {"HA", 0.56}, {"ES", 0.56}, {"ST", 0.55}, {"EN", 0.55}, {"ED", 0.53}, {"TO", 0.52}, {"IT", 0.50}, {"OU", 0.50}, {"EA", 0.47}, {"HI", 0.46}, {"IS", 0.46}, {"OR", 0.43}, {"TI", 0.34}, {"AS", 0.33}, {"TE", 0.27}, {"ET", 0.19}, {"NG", 0.18}, {"OF", 0.16}, {"AL", 0.09}, {"DE", 0.09}, {"SE", 0.08}, {"LE", 0.08}, {"SA", 0.06}, {"SI", 0.05}, {"AR", 0.04}, {"VE", 0.04}, {"RA", 0.04}, {"LD", 0.02}, {"UR", 0.02}};
map<string, double> pcBigramFT {{"TH", 2.71}, {"HE", 2.33}, {"IN", 2.03}, {"ER", 1.78}, {"AN", 1.61}, {"RE", 1.41}, {"ND", 1.07}, {"AT", 1.12}, {"ON", 1.32}, {"NT", 1.17}, {"HA", 0.83}, {"ES", 1.32}, {"ST", 1.25}, {"EN", 1.13}, {"ED", 1.08}, {"TO", 1.07}, {"IT", 0.88}, {"OU", 0.72}, {"EA", 1.00}, {"HI", 0.0}, {"IS", 0.86}, {"OR", 1.06}, {"TI", 0.99}, {"AS", 0.87}, {"TE", 0.98}, {"ET", 0.76}, {"NG", 0.89}, {"OF", 0.71}, {"AL", 0.0}, {"DE", 0.00}, {"SE", 0.73}, {"LE", 0.00}, {"SA", 0.00}, {"SI", 0.00}, {"AR", 0.00}, {"VE", 0.00}, {"RA", 0.00}, {"LD", 0.00}, {"UR", 0.00}};
map<string, double> ndBigramFT {{"TH", 3.88}, {"HE", 3.68}, {"IN", 2.28}, {"ER", 2.18}, {"AN", 2.14}, {"RE", 1.75}, {"ND", 1.57}, {"AT", 1.33}, {"ON", 1.42}, {"NT", 0.0}, {"HA", 1.27}, {"ES", 1.09}, {"ST", 0.00}, {"EN", 1.38}, {"ED", 1.27}, {"TO", 1.17}, {"IT", 1.13}, {"OU", 1.28}, {"EA", 0.00}, {"HI", 1.09}, {"IS", 1.11}, {"OR", 1.15}, {"TI", 0.00}, {"AS", 0.00}, {"TE", 0.00}, {"ET", 0.00}, {"NG", 1.05}, {"OF", 0.00}, {"AL", 0.0}, {"DE", 0.00}, {"SE", 0.00}, {"LE", 0.00}, {"SA", 0.00}, {"SI", 0.00}, {"AR", 0.00}, {"VE", 0.00}, {"RA", 0.00}, {"LD", 0.00}, {"UR", 0.00}};

//calc log-frequency (log-bigram score), higher is better
int getBigramFitness(string str){
    //get the relative frequency of the bigram in english
    double relativeFreq = ndBigramFT[str];
    //log result (to avoid multipling probablities of small integers)
    if (relativeFreq != 0) {
        return log10(relativeFreq);
    } else {
        return log10(0.01);
    }
}

int main() {

    //open file
    freopen("cexercise6.txt", "r", stdin);
    char c[841];
    cin.getline(c,sizeof(c));
    int inpSize = strlen(c);
    int possibleNumCols[] = {6};
    for (int i = 0; i < 1; i++) {
        int nC = possibleNumCols[i];
        int colSize = inpSize/nC;

        //uses std vector, which makes it easier to later find permutations (perms)
        vector<vector<char>> groupedText(nC, vector<char>((colSize), 'a'));
        //char groupedText[nC][inpSize/nC];
        int cC = 0;
        for (int i = 0; i < nC; i++) {
            for (int j = 0; j < colSize; j++) {
                groupedText.at(i).at(j) = c[cC];
                cC++;
            }
        }

        //!nC possible perms
        int colPerms = 1;
        for(int i = 1; i <=nC; i++) {
            colPerms *= i;
        }

        //array for the best 3 fitness ciphertexts (col perms)
        array<vector<vector<char>>, 3> bestTexts;
        //array for the best 3 fitness vals, initialised at the smallest number possible
        double bestFits[] = {-DBL_MAX, -DBL_MAX, -DBL_MAX};
        //constexpr double lowest_double = std::numeric_limits<double>::lowest();

        for (int i = 0; i < colPerms; i++) {
            //cout << "perm: " << i << "\n";
            double fit = 0;
            for (int i = 0; i < colSize; i++) {
                for (int j = 0; j < nC-1; j++) {
                    //cout << groupedText[j][i];
                    string str = {groupedText[j][i], groupedText[j+1][i]};
                    //get fitness of current bigram
                    fit += getBigramFitness(str);
                    //cout << str << ":" << fit << " ";
                }
                //if (fit > -100)
                //    cout << i << ":" << fit << "\n";
                string str = {groupedText[nC-1][i], groupedText[0][i+1]};
                fit += getBigramFitness(str);
            }
            //check if the current column permutation has good fitness
            for (int i = 0; i < 3; i++) {
                //cout << bestFits[i] << " ";
                if (fit > bestFits[i]){
                    //cout << i << ":" << fit << " ";
                    bestFits[i] = fit;
                    //copy(groupedText.begin(), groupedText.end(), bestTexts[i]);
                    bestTexts[i] = groupedText;
                    break;
                }
            }

            //ask cpp stdlib to get the next permutation of cols
            next_permutation(groupedText.begin(), groupedText.end());
        }
        
        //output the decrypted text with the 3 best fitnesses
        for (int k = 0; k < 3; ++k) {
            cout << bestFits[k] << "\n";
            for (int i = 0; i < colSize; i++) {
                for (int j = 0; j < nC; j++) {
                    cout << bestTexts[k].at(j).at(i);
                }
            }
            cout << endl;
        }
    }
}
