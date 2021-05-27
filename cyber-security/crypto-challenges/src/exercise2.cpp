/*
    purpose: solve the vigenere cipher with the keyword given (cexercise2)
    author: ethan hullett

    1 create arr of offsets for each char of the keyword
    2 iterate ciphertext 
        3 check if each letter is within the correct ascii range (A-Z)
        4 change letter to a number from 0-25 -> add offset -> apply modulo -> convert back to letter
        5 update offset pointed at for the next letter, may need to loop back round to 1st offset
    6 output plaintext 
*/

#include <bits/stdc++.h>

using namespace std;
int main() {
    //get offsets
    //char alphabet[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    char key[] = "TESSOFTHEDURBERVILLES";
    int offsets[strlen(key)];
    for (int i = 0; i < strlen(key); ++i) {
        //calc offset via ascii arithmetic, faster than getting index from the alphabet
        offsets[i] = key[i] - 'A';
        //offsets[i] = find(alphabet, alphabet+sizeof(alphabet), key[i])-alphabet;
    }
    int cOI = 0;

    //read ciphertext file
    freopen("cexercise2.txt", "r", stdin);
    char c[31];
    cin.getline(c,sizeof(c));

    for(int i=0; i<31; i++){
        if (c[i] >= 'a' && c[i] <= 'z'){
            //apply index by ascii and modulus arithmetic, + 26 is only required for decryption
            int toAdd = ((c[i] - 'a') + 26 - offsets[cOI]) % 26;
            c[i] = 'a' + toAdd;
        //check if letter within correct ascii range
        } else if (c[i] >= 'A' && c[i] <= 'Z') {
            //apply offset to letter
            int toAdd = ((c[i] - 'A') + 26 - offsets[cOI]) % 26;
            c[i] = 'A' + toAdd;
        }
        //increment offset currently being pointed to
        cOI = (cOI + 1) % (strlen(key));
    }
    //output plaintext
    for(int i=0; i<30; i++){
        cout << c[i];
    }
    cout << "\n";
}
