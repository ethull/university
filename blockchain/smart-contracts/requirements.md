# ass reqirements
- accept SepETH from anyone -
- retrieve SepETH from 2 fixed addrs and contract owner (address from which it is deployed) -
- only retrieve SepETH at a rate of lt= 1.0 SepETH at a time -
- time difference of at least 30 minutes between any two retrievals -
- our contract should be re-entrancy attack resistant -
- high code quality (comments, no obvious security vulnerabilities) -

# questions
- do we need extra checks not asked for?
- like sending lt 0

- how does the contract receive money
  - send ether to contract address
  - it then calls the deposit method
