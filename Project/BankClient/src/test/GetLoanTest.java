package test;

import client.dlmsClient;

/**
 *
 * @author yucunli
 */
public class GetLoanTest {
    
    String[] lID = new String[4];
    
    public void runTest(String[] args){
        dlmsClient client = new dlmsClient(args);
        OpenAccountTest oat = new OpenAccountTest();

        lID[0] = client.sendRequest("A", "getLoan", new String[]{oat.ap[0], "pass", "100"});
        lID[1] = client.sendRequest("B", "getLoan", new String[]{oat.ap[1], "pass", "100"});
        lID[2] = client.sendRequest("C", "getLoan", new String[]{oat.ap[2], "pass", "100"});
        lID[3] = client.sendRequest("D", "getLoan", new String[]{oat.ap[3], "pass", "100"});
    }
}