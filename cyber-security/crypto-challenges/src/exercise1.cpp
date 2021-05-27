// purpose: solve caesar cipher (cexercise1)
// author: ethan hullett

#include <bits/stdc++.h>

using namespace std;

int main() {
    freopen("cexercise1.txt", "r", stdin);
    //string s;
    //getline(cin, s);
    int offset = 6;
    char c[841];
    cin.getline(c,sizeof(c));
    for(int i=0; i<840; i++){
        if (c[i] >= 'A' && c[i] <= 'Z') {
            int toAdd = (offset + (c[i] - 'A')) % 26;
            c[i] = 'A' + toAdd;
        }
    }
    for(int i=0; i<30; i++){
        cout << c[i];
    }
}
