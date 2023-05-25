pragma solidity ^0.8.3;

contract TrustFund {
    // initialize the mutex (for re-entrancy attacks)
    bool private reEntrancyMutex = false;

    // set amount limit for withdraw to 1 ether
    uint public withdrawalLimit = 1 ether;
    // set time limit for withdrawal to 30 mins
    uint public withdrawalTimeLimit = 30 minutes;

    // map for any specific addresses last withdrawal
    mapping(address => uint) public lastWithdrawTime;
    // map for each addresses balance
    mapping(address => uint) public balances;

    // first retrievel address
    address private receiveAddr = 0x7160312B63389703D59DE296bd3c222609278B7E;
    // second retrievel address
    address private receiveAddr2 = 0x520E56daB648BEA371055d843D61F744d5AD203b;
    // stores owner of this contracts address
    address private owner;

    // when the contract is initiated owner is set to contracts deployers address
    constructor () payable {
        owner = msg.sender;
    }

    function depositFunds() external payable {
        // make sure sender is depositing >0 (not an assessment requirement but i think it is needed)
        require(msg.value > 0, "Deposit more than 0");
        balances[msg.sender] += msg.value;
    }

    function withdrawFunds(uint _amountWei) public {
        // make sure the mutex is not set (someone is not trying to withdraw again during an externel call (reentrant call))
        require(!reEntrancyMutex);
        // make sure we are retrieveing from one of our target addresses
        require(msg.sender == owner || msg.sender == receiveAddr || msg.sender == receiveAddr2, "Your address is not authorized to withdraw");
        // does the sender have enougth ether to withdraw?
        require(balances[msg.sender] >= _amountWei);
        // make sure withdrawal amount is <= 1.0
        require(_amountWei <= withdrawalLimit);
        // make sure they are not withdrawing within their wihdrawal time limit
        require((block.timestamp - lastWithdrawTime[msg.sender]) >= withdrawalTimeLimit, "You are trying to withdraw too soon");
        // update balance for the sender address relative to amount sent
        balances[msg.sender] -= _amountWei;
        // update withdrawal time to current time
        lastWithdrawTime[msg.sender] = block.timestamp;

        // set the reEntrancy mutex before the external call
        reEntrancyMutex = true;
        // actually send the ether (note all logic changing state is done before this)
        payable(msg.sender).transfer(_amountWei);
        // release the mutex after the external call
        reEntrancyMutex = false;
    }
 }
