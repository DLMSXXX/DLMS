package test;

import client.dlmsClient;

/**
 *
 * @author yucunli
 */
public class TransferLoanTest {
    
    public void runTest(String[] args){
        dlmsClient client = new dlmsClient(args);
        GetLoanTest glt = new GetLoanTest();

        client.sendRequest("A", "transferLoan", new String[]{glt.lID[0], "B"});
        client.sendRequest("B", "transferLoan", new String[]{glt.lID[1], "C"});
        client.sendRequest("C", "transferLoan", new String[]{glt.lID[2], "D"});
    }
}